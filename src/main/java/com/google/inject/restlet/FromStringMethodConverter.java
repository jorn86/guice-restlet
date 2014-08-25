package com.google.inject.restlet;

import java.lang.reflect.Modifier;

import javax.ws.rs.WebApplicationException;

import lombok.AllArgsConstructor;

@AllArgsConstructor class FromStringMethodConverter extends TypeConverter<Object> {
	private final String name;
	
	@Override
	public Object apply(String input, Class<? extends Object> targetType) {
		try {
			return targetType.getMethod(name, String.class).invoke(null, input);
		}
		catch (ReflectiveOperationException e) {
			throw new WebApplicationException(e);
		}
		catch (RuntimeException e) {
			throw new WebApplicationException(e);
		}
	}
	
	@Override
	public boolean applies(Class<?> targetType) {
		try {
			int modifiers = targetType.getMethod(name, String.class).getModifiers();
			return (modifiers & (Modifier.STATIC | Modifier.PUBLIC)) > 0;
		}
		catch (NoSuchMethodException e) {
			return false;
		}
	}
	
	@Override public Object apply(String input) { throw new UnsupportedOperationException(); }
}