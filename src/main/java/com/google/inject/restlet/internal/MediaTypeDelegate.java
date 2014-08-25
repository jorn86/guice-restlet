package com.google.inject.restlet.internal;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.RuntimeDelegate.HeaderDelegate;

public class MediaTypeDelegate implements HeaderDelegate<MediaType> {
	@Override
	public MediaType fromString(String value) {
		String[] values = value.split(";");
		MediaType type = getType(values[0]);
		for (String parameter : values) {
			String[] parameterValues = parameter.split("=");
			if (parameterValues[0].equals(MediaType.CHARSET_PARAMETER)) {
				return type.withCharset(parameterValues[1]);
			}
		}
		return type;
	}
	
	private MediaType getType(String type) {
		switch (type) {
			case MediaType.APPLICATION_JSON: return MediaType.APPLICATION_JSON_TYPE;
			case MediaType.APPLICATION_XML: return MediaType.APPLICATION_XML_TYPE;
			case MediaType.MULTIPART_FORM_DATA: return MediaType.MULTIPART_FORM_DATA_TYPE;
			case MediaType.TEXT_HTML: return MediaType.TEXT_HTML_TYPE;
			case MediaType.TEXT_PLAIN: return MediaType.TEXT_PLAIN_TYPE;
			case MediaType.TEXT_XML: return MediaType.TEXT_XML_TYPE;
			case MediaType.WILDCARD: return MediaType.WILDCARD_TYPE;
			default: throw new UnsupportedOperationException(type);
		}
	}
	
	@Override
	public String toString(MediaType value) {
		return value.getType() + "/" + value.getSubtype();
	}
}
