package com.google.inject.restlet.argumentprovider;

import java.util.Set;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;

import lombok.Setter;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.inject.restlet.TypeConverter;

public abstract class TypeConvertingProvider implements ArgumentProvider {
	private final TypeConverter<?> converter;
	private final Provider<HttpServletRequest> requestProvider;
	@Setter private String defaultValue = null;
	
	public TypeConvertingProvider(final Class<?> parameterType, Set<TypeConverter<?>> converters, Provider<HttpServletRequest> requestProvider) {
		this.requestProvider = requestProvider;
		this.converter = Iterables.find(converters, new Predicate<TypeConverter<?>>() {
			@Override
			public boolean apply(TypeConverter<?> input) {
				return input.applies(parameterType);
			}
		});
	}
	
	protected abstract String getRawValue();
	
	protected final HttpServletRequest getRequest() {
		return requestProvider.get();
	}
	
	@Override
	public Object provide() {
		String value = getRawValue();
		if (value == null) {
			value = defaultValue;
		}
		return converter.apply(value);
	}
}
