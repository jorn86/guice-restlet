package com.google.inject.restlet.argumentprovider;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.PathParam;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.restlet.TypeConverter;

public class PathProvider extends UrlDecodingProvider {
	private final String name;
	private final Pattern pathRegex;
	
	@Inject
	public PathProvider(@Assisted Class<?> parameterType, @Assisted Annotation annotation, @Assisted Pattern pathRegex,
			Set<TypeConverter<?>> converters, Provider<HttpServletRequest> requestProvider) {
		super(parameterType, converters, requestProvider);
		this.name = ((PathParam) annotation).value();
		this.pathRegex = pathRegex;
	}
	
	@Override
	protected String getEncodedValue() {
		Matcher matcher = pathRegex.matcher(getRequest().getRequestURI());
		if (matcher.matches()) {
			return matcher.group(name);
		}
		return null;
	}
}
