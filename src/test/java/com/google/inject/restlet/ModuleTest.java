package com.google.inject.restlet;

import java.util.Set;
import java.util.regex.Pattern;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.restlet.argumentprovider.ProviderFactory;

public class ModuleTest {
	private static Injector injector;

	@BeforeClass
	public static void createInjector() {
		injector = Guice.createInjector(new RestletModule());
	}
	
	@Test
	public void testConverters() {
		injector.getInstance(Key.get(new TypeLiteral<Set<TypeConverter<?>>>(){}));
	}
	
	@Test
	public void testArgumentProviders() {
		ProviderFactory factory = injector.getInstance(ProviderFactory.class);
		factory.context(Object.class);
		factory.header(Object.class, Mockito.mock(HeaderParam.class));
		factory.path(Object.class, Mockito.mock(PathParam.class), Pattern.compile(""));
		factory.query(Object.class, Mockito.mock(QueryParam.class));
	}
	
	@Test
	public void testBodyReaders() {
		injector.getInstance(Key.get(new TypeLiteral<Set<BodyReader>>() {}));
	}
}
