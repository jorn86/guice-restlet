package com.google.inject.restlet.internal;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Link.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Variant.VariantListBuilder;
import javax.ws.rs.ext.RuntimeDelegate;

public class RuntimeDelegateImpl extends RuntimeDelegate {
	@Override
	public UriBuilder createUriBuilder() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public ResponseBuilder createResponseBuilder() {
		return ResponseImpl.builder();
	}
	
	@Override
	public VariantListBuilder createVariantListBuilder() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public <T> T createEndpoint(Application application, Class<T> endpointType) throws IllegalArgumentException, UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> HeaderDelegate<T> createHeaderDelegate(Class<T> type) throws IllegalArgumentException {
		if (type == MediaType.class) {
			return (HeaderDelegate<T>) new MediaTypeDelegate();
		}
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Builder createLinkBuilder() {
		throw new UnsupportedOperationException();
	}
}
