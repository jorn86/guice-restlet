package com.google.inject.restlet.argumentprovider;

import java.lang.annotation.Annotation;
import java.util.regex.Pattern;

public interface ProviderFactory {
	BodyProvider body(Class<?> parameterType);
	ContextProvider context(Class<?> parameterType);
	HeaderProvider header(Class<?> parameterType, Annotation annotation);
	PathProvider path(Class<?> parameterType, Annotation annotation, Pattern pathRegex);
	QueryProvider query(Class<?> parameterType, Annotation annotation);
}
