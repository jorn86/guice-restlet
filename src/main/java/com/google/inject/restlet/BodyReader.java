package com.google.inject.restlet;

import java.io.IOException;
import java.io.Reader;

import javax.ws.rs.core.MediaType;

public interface BodyReader {
	<T> T read(Reader bodyContent, Class<T> parameterType) throws IOException;
	boolean consumes(MediaType contentType);
	boolean produces(Class<?> parameterType);
}
