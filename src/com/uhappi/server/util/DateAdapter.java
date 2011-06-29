package com.uhappi.server.util;

import java.lang.reflect.Type;
import java.util.Date;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class DateAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {
	@Override
	public JsonElement serialize(Date o, Type type, JsonSerializationContext ctx) {
		return new JsonPrimitive(o.getTime() / 1000);
	}

	@Override
	public Date deserialize(JsonElement el, Type type, JsonDeserializationContext ctx) throws JsonParseException {
		return null;
	}

}
