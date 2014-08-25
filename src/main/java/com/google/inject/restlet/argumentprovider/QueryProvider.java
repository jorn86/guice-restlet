package com.google.inject.restlet.argumentprovider;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.QueryParam;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.restlet.TypeConverter;

public class QueryProvider extends UrlDecodingProvider {
	private final String name;
	
	@Inject
	public QueryProvider(@Assisted Class<?> parameterType, @Assisted Annotation annotation,
			Set<TypeConverter<?>> converters, Provider<HttpServletRequest> requestProvider) {
		super(parameterType, converters, requestProvider);
		this.name = ((QueryParam) annotation).value();
	}
	
	@Override
	protected String getEncodedValue() {
		return getRequest().getParameter(name);
	}
}
