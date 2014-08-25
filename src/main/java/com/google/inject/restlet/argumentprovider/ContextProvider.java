package com.google.inject.restlet.argumentprovider;

import javax.inject.Inject;

import lombok.Getter;

import com.google.inject.ConfigurationException;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;

public class ContextProvider implements ArgumentProvider {
	@Getter private final Key<?> key;
	private final Provider<?> provider;
	
	@Inject
	public ContextProvider(@Assisted Class<?> parameterType, Injector injector) {
		this.key = Key.get(parameterType);
		this.provider = getProvider(injector);
	}
	
	private Provider<?> getProvider(Injector injector) {
		try {
			return injector.getProvider(key);
		}
		catch (ConfigurationException e) {
			throw new IllegalStateException("@Context does not support arguments of type " + key, e);
		}
	}
	
	@Override
	public Object provide() {
		return provider.get();
	}
}
