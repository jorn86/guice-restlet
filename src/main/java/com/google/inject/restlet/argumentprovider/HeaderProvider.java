package com.google.inject.restlet.argumentprovider;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.HeaderParam;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.restlet.TypeConverter;

public class HeaderProvider extends TypeConvertingProvider {
	private final String name;
	
	@Inject
	public HeaderProvider(@Assisted Class<?> parameterType, @Assisted Annotation annotation,
			Set<TypeConverter<?>> converters, Provider<HttpServletRequest> requestProvider) {
		super(parameterType, converters, requestProvider);
		this.name = ((HeaderParam) annotation).value();
	}
	
	@Override
	protected String getRawValue() {
		return getRequest().getHeader(name);
	}
}
