package com.google.inject.restlet;

import java.lang.reflect.Method;

import javax.inject.Inject;
import javax.inject.Singleton;

import lombok.RequiredArgsConstructor;

import com.google.inject.Provider;
import com.google.inject.restlet.argumentprovider.ProviderFactory;

@Singleton
@RequiredArgsConstructor
public class RestletMethodProvider implements Provider<RestletMethod> {
	private final Class<?> restlet;
	private final Method method;
	private final String servletPrefix;
	@Inject private ProviderFactory providerFactory;
	
	@Override
	public RestletMethod get() {
		return new RestletMethod(restlet, method, servletPrefix, providerFactory);
	}
}
