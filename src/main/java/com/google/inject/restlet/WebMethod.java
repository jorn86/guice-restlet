package com.google.inject.restlet;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.EnumSet;

import javax.ws.rs.HttpMethod;

import com.google.common.collect.Iterables;

public enum WebMethod {
	GET, POST, PUT, DELETE, HEAD, OPTIONS;
	
	public static WebMethod fromAnnotation(Method method) {
		EnumSet<WebMethod> methods = findHttpMethods(method);
		if (methods.isEmpty()) {
			return fromAnnotation(method.getDeclaringClass());
		}
		if (methods.size() > 1) {
			throw new IllegalStateException("Restlet method declares multiple HTTP methods: " + methods);
		}
		return Iterables.getOnlyElement(methods);
	}
	
	public static WebMethod fromAnnotation(Class<?> restlet) {
		EnumSet<WebMethod> methods = findHttpMethods(restlet);
		if (methods.isEmpty()) {
			throw new IllegalStateException("Restlet method does not declare HTTP method");
		}
		if (methods.size() > 1) {
			throw new IllegalStateException("Restlet method declares multiple HTTP methods: " + methods);
		}
		return Iterables.getOnlyElement(methods);
	}
	
	private static EnumSet<WebMethod> findHttpMethods(AnnotatedElement element) {
		EnumSet<WebMethod> methods = EnumSet.noneOf(WebMethod.class);
		for (Annotation annotation : element.getAnnotations()) {
			HttpMethod httpMethodAnnotation = annotation.annotationType().getAnnotation(HttpMethod.class);
			if (httpMethodAnnotation != null) {
				methods.add(valueOf(httpMethodAnnotation.value()));
			}
		}
		return methods;
	}
}
