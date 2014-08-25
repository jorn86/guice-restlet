package com.google.inject.restlet.argumentprovider;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Set;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;

import lombok.Setter;

import com.google.inject.restlet.TypeConverter;

public abstract class UrlDecodingProvider extends TypeConvertingProvider {
	@Setter private boolean encoded = false;
	
	public UrlDecodingProvider(Class<?> parameterType, Set<TypeConverter<?>> converters, Provider<HttpServletRequest> requestProvider) {
		super(parameterType, converters, requestProvider);
	}
	
	@Override
	protected final String getRawValue() {
		String encodedValue = getEncodedValue();
		if (encoded) {
			return encodedValue;
		}
		try {
			return URLDecoder.decode(encodedValue, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			throw new WebApplicationException(e);
		}
	}
	
	protected abstract String getEncodedValue();
}
