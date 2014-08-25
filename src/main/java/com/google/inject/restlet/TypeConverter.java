package com.google.inject.restlet;

import com.google.common.base.Function;
import com.google.common.reflect.TypeToken;

public abstract class TypeConverter<T> implements Function<String, T> {
	private final TypeToken<T> typeOfT = new TypeToken<T>(getClass()){private static final long serialVersionUID = 1L;};
	
	/**
	 * Convert the given {@link String} argument to the requested type
	 */
	@Override public abstract T apply(String input);
	
	/**
	 * Override this method if you need the target type information to convert the given {@link String} to the requested type
	 */
	public T apply(String input, Class<? extends T> targetType) {
		return apply(input);
	}
	
	/**
	 * By default, returns true if the target type is assignable from the return type of this converter.
	 * @return true iff this converter can convert a {@link String} value to the given type
	 */
	public boolean applies(Class<?> targetType){
		return targetType.isAssignableFrom(typeOfT.getRawType());
	}
}
