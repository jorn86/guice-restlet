package com.google.inject.restlet;

import javax.ws.rs.ext.RuntimeDelegate;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.restlet.argumentprovider.ProviderFactory;
import com.google.inject.restlet.internal.RuntimeDelegateImpl;

public class InternalRestletModule extends AbstractModule {
	private Multibinder<TypeConverter<?>> converterBinder;
	
	@Override
	protected void configure() {
		RuntimeDelegate.setInstance(new RuntimeDelegateImpl());
		
		install(new FactoryModuleBuilder().build(ProviderFactory.class));
		
		Multibinder.newSetBinder(binder(), BodyReader.class).addBinding().to(GsonBodyReader.class);
		
		converterBinder = Multibinder.newSetBinder(binder(), new TypeLiteral<TypeConverter<?>>(){});
		bindConverter(new PrimitiveTypeConverter<>(byte.class, Byte.class));
		bindConverter(new PrimitiveTypeConverter<>(double.class, Double.class));
		bindConverter(new PrimitiveTypeConverter<>(float.class, Float.class));
		bindConverter(new PrimitiveTypeConverter<>(int.class, Integer.class));
		bindConverter(new PrimitiveTypeConverter<>(long.class, Long.class));
		bindConverter(new PrimitiveTypeConverter<>(short.class, Short.class));
		
		bindConverter(new TypeConverter<String>() {
			@Override
			public String apply(String input) {
				return input;
			}
		});
		
		// spec: "Types that have a static method named valueOf or fromString with a single String argument
		// that return an instance of the type. If both methods are present then valueOf MUST be used,
		// unless the type is an enum in which case fromString MUST be used."
		bindConverter(new FromStringMethodConverter("fromString") {
			@Override
			public boolean applies(Class<?> targetType) {
				return Enum.class.isAssignableFrom(targetType) && super.applies(targetType);
			}
		});
		bindConverter(new FromStringMethodConverter("valueOf"));
		bindConverter(new FromStringMethodConverter("fromString"));
	}
	
	protected final void bindConverter(TypeConverter<?> converter) {
		converterBinder.addBinding().toInstance(converter);
	}
}
