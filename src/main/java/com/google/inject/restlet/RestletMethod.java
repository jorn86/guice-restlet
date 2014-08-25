package com.google.inject.restlet;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import javax.ws.rs.BeanParam;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Encoded;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.restlet.argumentprovider.ArgumentProvider;
import com.google.inject.restlet.argumentprovider.BodyProvider;
import com.google.inject.restlet.argumentprovider.ContextProvider;
import com.google.inject.restlet.argumentprovider.HeaderProvider;
import com.google.inject.restlet.argumentprovider.ProviderFactory;
import com.google.inject.restlet.argumentprovider.UrlDecodingProvider;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.MethodHelper;

@Slf4j
public class RestletMethod {
	private final Class<?> restlet;
	private final Method method;
	@Getter private final WebMethod webMethod;
	private final String path;
	private final Pattern pathRegex;
	private final ImmutableList<ArgumentProvider> argumentProviders;
	
	public RestletMethod(Class<?> restlet, Method method, String pathPrefix, ProviderFactory providerFactory) {
		this.restlet = restlet;
		this.method = method;
		this.webMethod = WebMethod.fromAnnotation(method);
		this.path = path(pathPrefix, restlet.getAnnotation(Path.class), method.getAnnotation(Path.class));
		this.pathRegex = pathExpression(path);
		this.argumentProviders = createArgumentProviders(restlet, method, providerFactory);
		log.info("Registered {}", this);
	}
	
	@VisibleForTesting static String path(String pathPrefix, Path restletPath, Path methodPath) {
		StringBuilder sb = new StringBuilder("/").append(checkNotNull(pathPrefix));
		if (restletPath != null) {
			sb.append(restletPath.value());
		}
		sb.append("/");
		if (methodPath != null) {
			sb.append(methodPath.value());
		}
		String path = sb.append("/").toString().replaceAll("/+", "/").substring(1);
		return path.isEmpty() ? "/" : path;
	}
	
	private static Pattern pathExpression(String path) {
		String[] parts = path.split("/");
		StringBuilder regex = new StringBuilder();
		for (String part : parts) {
			regex.append("/?");
			if (part.startsWith("{") && part.endsWith("}")) {
				String name = part.substring(1, part.length() - 1);
				regex.append("(?<").append(name).append(">(\\w|\\d|\\s|\\%)+)");
			}
			else {
				regex.append(part);
			}
		}
		regex.append("/?");
		return Pattern.compile(regex.toString());
	}
	
	private ImmutableList<ArgumentProvider> createArgumentProviders(Class<?> restlet, Method method, ProviderFactory providerFactory) {
		List<Dependency<?>> dependencies = MethodHelper.getDependencies(restlet, method);
		ImmutableList.Builder<ArgumentProvider> builder = ImmutableList.builder();
		int i = 0;
		for (Dependency<?> dependency : dependencies) {
			builder.add(getProvider(dependency, method.getParameterAnnotations()[i++], providerFactory));
		}
		ImmutableList<ArgumentProvider> providers = builder.build();
		checkState(Iterables.size(Iterables.filter(providers, BodyProvider.class)) < 2, "Multiple arguments for request body in method %s$%s", restlet.getSimpleName(), method.getName());
		return providers;
	}
	
	private ArgumentProvider getProvider(Dependency<?> dependency, Annotation[] annotations, ProviderFactory providerFactory) {
		Key<?> key = dependency.getKey();
		Class<?> parameterType = key.getTypeLiteral().getRawType();
		DefaultValue defaultValue = getAnnotation(annotations, DefaultValue.class);
		Encoded encoded = getAnnotation(annotations, Encoded.class);
		for (Annotation annotation : annotations) {
			if (annotation instanceof Context) {
				if (key.getAnnotation() != null) {
					throw new UnsupportedOperationException("@Context parameters currently do not support scope annotations (" + key.getAnnotationType() + ")");
				}
				return providerFactory.context(parameterType);
			}
			else if (annotation instanceof HeaderParam) {
				HeaderProvider provider = providerFactory.header(parameterType, annotation);
				if (defaultValue != null) {
					provider.setDefaultValue(defaultValue.value());
				}
				return provider;
			}
			else if (annotation instanceof PathParam) {
				return set(providerFactory.path(parameterType, annotation, pathRegex), defaultValue, encoded);
			}
			else if (annotation instanceof QueryParam) {
				return set(providerFactory.query(parameterType, annotation), defaultValue, encoded);
			}
			else if (annotation instanceof BeanParam ||
					annotation instanceof CookieParam ||
					annotation instanceof FormParam ||
					annotation instanceof MatrixParam) {
				throw new UnsupportedOperationException(annotation + " is not supported yet"); // TODO
			}
		}
		return providerFactory.body(parameterType);
	}
	
	private ArgumentProvider set(UrlDecodingProvider provider, DefaultValue defaultValue, Encoded encoded) {
		if (defaultValue != null) {
			provider.setDefaultValue(defaultValue.value());
		}
		if (encoded != null) {
			provider.setEncoded(true);
		}
		return provider;
	}
	
	private <T extends Annotation> T getAnnotation(Annotation[] annotations, Class<T> type) {
		return Iterators.getOnlyElement(Iterators.filter(Iterators.forArray(annotations), type), null);
	}
	
	public Iterable<Key<?>> getGuiceDependencies() {
		return Iterables.transform(Iterables.filter(argumentProviders, ContextProvider.class), new Function<ContextProvider, Key<?>>() {
			@Override
			public Key<?> apply(ContextProvider input) {
				return input.getKey();
			}
		});
	}
	
	public Object invoke(Injector injector) throws WebApplicationException {
		try {
			Object[] args = new Object[argumentProviders.size()];
			for (int i = 0; i < args.length; i++) {
				args[i] = argumentProviders.get(i).provide();
			}
			log.debug("Invoking {} on {} with {}", method.getName(), restlet.getSimpleName(), Arrays.toString(args));
			return method.invoke(injector.getInstance(restlet), args);
		}
		catch (InvocationTargetException e) {
			Throwables.propagateIfPossible(e.getCause(), WebApplicationException.class);
			throw new WebApplicationException(e.getCause());
		}
		catch (ReflectiveOperationException e) {
			throw new WebApplicationException(e);
		}
	}
	
	@Override
	public String toString() {
		return String.format("%s %s (%s#%s)", getWebMethod(), path, method.getDeclaringClass().getSimpleName(), method.getName());
	}
	
	public boolean handles(String requestURI) {
		return pathRegex.matcher(requestURI).matches();
	}
}
