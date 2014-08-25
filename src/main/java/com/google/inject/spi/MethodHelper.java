package com.google.inject.spi;

import static com.google.common.base.Preconditions.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import javax.inject.Inject;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.InjectionPoint.InjectableMethod;

/**
 * Helper class to allow access to {@link InjectableMethod}
 */
public class MethodHelper {
	/**
	 * @return the parameters of the given method, as {@link Dependency} instances
	 */
	public static List<Dependency<?>> getDependencies(Class<?> restlet, Method method) {
		checkArgument(restlet.isAssignableFrom(method.getDeclaringClass()), "Method {} is not declared in {} or a subclass", method, restlet);
		return new InjectableMethod(TypeLiteral.get(restlet), method, InjectImpl.INSTANCE).toInjectionPoint().getDependencies();
	}
	
	@SuppressWarnings("all")
	private static enum InjectImpl implements Inject {
		INSTANCE;
		
		@Override
		public Class<? extends Annotation> annotationType() {
			return Inject.class;
		}
	}
}
