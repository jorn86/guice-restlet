package com.google.inject.restlet.internal;

import static com.google.common.base.Preconditions.*;

import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;

import lombok.Getter;
import lombok.experimental.Builder;

import com.google.common.collect.ImmutableSet;

@Getter
public class ResponseImpl extends Response {
	public static ResponseBuilder builder() {
		return new ResponseBuilderImpl();
	}
	
	private final StatusType statusInfo;
	private final Object entity;
	private final MediaType mediaType;
	private final Locale language;
	private final Set<String> allowedMethods;
	private final Map<String, NewCookie> cookies;
	private final EntityTag entityTag;
	private final Date date;
	private final Date lastModified;
	private final URI location;
	private final Set<Link> links;
	private final MultivaluedMap<String, Object> metadata;
	private final MultivaluedMap<String, String> stringHeaders;
	
	public ResponseImpl(StatusType statusInfo, Object entity, MediaType mediaType, Locale language, Set<String> allowedMethods,
			Map<String, NewCookie> cookies, EntityTag entityTag, Date date, Date lastModified, URI location, Set<Link> links,
			MultivaluedMap<String, Object> metadata, MultivaluedMap<String, String> stringHeaders) {
		this.statusInfo = checkNotNull(statusInfo);
		this.entity = entity;
		this.mediaType = mediaType;
		this.language = language;
		this.allowedMethods = allowedMethods;
		this.cookies = cookies;
		this.entityTag = entityTag;
		this.date = date;
		this.lastModified = lastModified;
		this.location = location;
		this.links = links;
		this.metadata = metadata;
		this.stringHeaders = stringHeaders;
	}
	
	@Builder(builderClassName="LombokBuilder")
	private ResponseImpl(StatusType status, Object entity, Set<String> allow, CacheControl cacheControl, String encoding,
			MultivaluedMap<String, Object> replaceAll, Locale language, MediaType type, Variant variant, URI contentLocation,
			Date expires, Date lastModified, URI location, EntityTag tag, List<Variant> variants) {
		this(status, entity, type, language, allow, null, tag, new Date(), lastModified, location, null, replaceAll, null);
	}
	
	@Override
	public int getStatus() {
		return statusInfo.getStatusCode();
	}
	
	@Override
	public <T> T readEntity(Class<T> entityType) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public <T> T readEntity(GenericType<T> entityType) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public <T> T readEntity(Class<T> entityType, Annotation[] annotations) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public <T> T readEntity(GenericType<T> entityType, Annotation[] annotations) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean hasEntity() {
		return entity != null;
	}
	
	@Override
	public boolean bufferEntity() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void close() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public int getLength() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean hasLink(String relation) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Link getLink(String relation) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Link.Builder getLinkBuilder(String relation) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public String getHeaderString(String name) {
		return stringHeaders.getFirst(name);
	}
	
	private static abstract class LombokBuilder extends ResponseBuilder {
	}
	
	private static class ResponseBuilderImpl extends LombokBuilder {
		@Override
		public ResponseBuilder clone() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public ResponseBuilder status(int status) {
			return status(Status.fromStatusCode(status));
		}
		
		@Override
		public ResponseBuilder entity(Object entity, Annotation[] annotations) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public ResponseBuilder allow(String... methods) {
			return allow(ImmutableSet.copyOf(methods));
		}
		
		@Override
		public ResponseBuilder header(String name, Object value) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public ResponseBuilder language(String language) {
			return language(Locale.forLanguageTag(language));
		}
		
		@Override
		public ResponseBuilder type(String type) {
			return type(MediaType.valueOf(type));
		}
		
		@Override
		public ResponseBuilder cookie(NewCookie... cookies) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public ResponseBuilder tag(String tag) {
			return tag(EntityTag.valueOf(tag));
		}
		
		@Override
		public ResponseBuilder variants(Variant... variants) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public ResponseBuilder links(Link... links) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public ResponseBuilder link(URI uri, String rel) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public ResponseBuilder link(String uri, String rel) {
			throw new UnsupportedOperationException();
		}
	}
}
