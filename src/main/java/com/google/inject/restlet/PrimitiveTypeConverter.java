package com.google.inject.restlet;

import javax.ws.rs.WebApplicationException;

final class PrimitiveTypeConverter<T> extends TypeConverter<T> {
	private final Class<T> primitive;
	private final Class<T> wrapper;
	
	PrimitiveTypeConverter(Class<T> primitive, Class<T> wrapper) {
		this.primitive = primitive;
		this.wrapper = wrapper;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T apply(String input) {
		try {
			return (T) wrapper.getMethod("valueOf", String.class).invoke(null, input);
		}
		catch (ReflectiveOperationException e) {
			throw new WebApplicationException(e);
		}
		catch (NumberFormatException e) {
			throw new WebApplicationException(e);
		}
	}
	
	@Override
	public boolean applies(Class<?> targetType) {
		return targetType == primitive || targetType == wrapper;
	}
}