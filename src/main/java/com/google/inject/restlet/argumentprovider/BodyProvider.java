package com.google.inject.restlet.argumentprovider;

import java.io.IOException;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import lombok.extern.slf4j.Slf4j;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.restlet.BodyReader;

@Slf4j
public class BodyProvider implements ArgumentProvider {
	private final Provider<HttpServletRequest> requestProvider;
	private final Set<BodyReader> bodyReaders;
	private final Class<?> parameterType;
	
	@Inject
	public BodyProvider(@Assisted Class<?> parameterType, Provider<HttpServletRequest> requestProvider, Set<BodyReader> bodyReaders) {
		this.parameterType = parameterType;
		this.requestProvider = requestProvider;
		this.bodyReaders = bodyReaders;
	}
	
	@Override
	public Object provide() {
		try {
			HttpServletRequest request = requestProvider.get();
			final MediaType contentType = parseContentType(request.getHeader("Content-Type"));
			BodyReader reader = Iterables.find(bodyReaders, new Predicate<BodyReader>() {
				@Override
				public boolean apply(BodyReader input) {
					return input.consumes(contentType) && input.produces(parameterType);
				}
			}, null);
			if (reader == null) {
				throw new WebApplicationException("No body reader found for converting " + contentType + " to " + parameterType);
			}
			return reader.read(request.getReader(), parameterType);
		}
		catch (IOException e) {
			throw new WebApplicationException(e);
		}
	}
	
	private MediaType parseContentType(String header) {
		try {
			return MediaType.valueOf(header);
		}
		catch (IllegalArgumentException e) {
			log.warn("Unrecognized content type {}, assuming {}", header, MediaType.TEXT_PLAIN, e);
			return MediaType.TEXT_PLAIN_TYPE;
		}
	}
}
