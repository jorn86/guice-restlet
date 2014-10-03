package com.google.inject.servlet;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class GuiceServletWorkaround extends AbstractModule {
	@Override
	protected void configure() {
		bind(InternalServletModule.BackwardsCompatibleServletContextProvider.class).in(Singleton.class);
	}
}
