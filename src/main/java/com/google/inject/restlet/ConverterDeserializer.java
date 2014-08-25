package com.google.inject.restlet;

import java.lang.reflect.Type;

import lombok.AllArgsConstructor;
import lombok.ToString;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

@AllArgsConstructor @ToString
public class ConverterDeserializer<T> implements JsonDeserializer<T> {
	private final TypeConverter<T> converter;
	
	@Override
	public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		if (converter.applies((Class<?>) typeOfT)) {
			return converter.apply(json.getAsString());
		}
		throw new JsonParseException(this + " cannot parse " + typeOfT);
	}
}
