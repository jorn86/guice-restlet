package com.google.inject.restlet;

import static com.google.common.base.Preconditions.*;

import java.lang.reflect.Method;

import javax.inject.Singleton;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;

import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.servlet.RequestScoped;
import com.google.inject.servlet.ServletModule;

public class RestletModule extends ServletModule {
	private final String path;
	private final String prefix;
	private Multibinder<RestletMethod> restletBinder;
	private Multibinder<BodyReader> bodyReaderBinder;
	private Multibinder<TypeConverter<?>> converterBinder;
	
	public RestletModule() {
		this("/*");
	}
	
	public RestletModule(String path) {
		checkArgument(path.endsWith("/*"), "Invalid path");
		this.path = path;
		this.prefix = path.substring(0, path.length() - 1);
	}
	
	@Override
	protected final void configureServlets() {
		serve(path).with(RestServlet.class);
		
		install(new InternalRestletModule()); // skipped if already installed
		
		restletBinder = Multibinder.newSetBinder(binder(), RestletMethod.class);
		bodyReaderBinder = Multibinder.newSetBinder(binder(), new TypeLiteral<BodyReader>(){});
		converterBinder = Multibinder.newSetBinder(binder(), new TypeLiteral<TypeConverter<?>>(){});
		configureRestlets();
	}
	
	protected void configureRestlets() {
	}
	
	protected final void bindRestlet(Class<?> restletClass) {
		bind(restletClass).in(RequestScoped.class);
		for (Method method : restletClass.getMethods()) {
			if (method.isAnnotationPresent(GET.class) ||
					method.isAnnotationPresent(POST.class) ||
					method.isAnnotationPresent(PUT.class) ||
					method.isAnnotationPresent(DELETE.class)) {
				
				restletBinder.addBinding().toProvider(new RestletMethodProvider(restletClass, method, prefix)).in(Singleton.class);
			}
		}
	}
	
	protected final void bindConverter(TypeConverter<?> converter) {
		converterBinder.addBinding().toInstance(converter);
	}
	
	protected final void bindConverter(Class<? extends TypeConverter<?>> converterType) {
		converterBinder.addBinding().to(converterType);
	}
	
	protected final void bindBodyReader(BodyReader bodyReader) {
		bodyReaderBinder.addBinding().toInstance(bodyReader);
	}
	
	protected final void bindBodyReader(Class<? extends BodyReader> readerType) {
		bodyReaderBinder.addBinding().to(readerType);
	}
}
