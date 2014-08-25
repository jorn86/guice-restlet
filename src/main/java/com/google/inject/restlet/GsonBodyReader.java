package com.google.inject.restlet;

import java.io.IOException;
import java.io.Reader;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Singleton
public class GsonBodyReader implements BodyReader {
	private final Gson gson;
	
	@Inject
	public GsonBodyReader(Set<TypeConverter<?>> converters) {
		GsonBuilder builder = new GsonBuilder();
		for (TypeConverter<?> converter : converters) {
//			builder.registerTypeAdapter(null, new ConverterDeserializer<>(converter));
		}
		this.gson = builder.create();
	}
	
	@Override
	public <T> T read(Reader bodyContent, Class<T> parameterType) throws IOException {
		return gson.fromJson(bodyContent, parameterType);
	}
	
	@Override
	public boolean produces(Class<?> parameterType) {
		try {
			gson.getAdapter(parameterType);
			return true;
		}
		catch (IllegalArgumentException e) {
			return false;
		}
	}
	
	@Override
	public boolean consumes(MediaType contentType) {
		return contentType.equals(MediaType.APPLICATION_JSON_TYPE);
	}
}
