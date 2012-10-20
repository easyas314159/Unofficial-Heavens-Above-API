package org.space_track;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class TwoLineElementAdapter implements JsonSerializer<TwoLineElement>,
		JsonDeserializer<TwoLineElement> {

	@Override
	public TwoLineElement deserialize(JsonElement el, Type type, JsonDeserializationContext ctx) throws JsonParseException {
		String tle = el.getAsString();
		if(tle == null) {
			return null;
		}

		String line[] = tle.split("\n");
		if(line.length == 3) {
			return new TwoLineElement(line[0], line[1], line[2]);
		}
		throw new JsonParseException("Malformated TLE");
	}

	@Override
	public JsonElement serialize(TwoLineElement o, Type type, JsonSerializationContext ctx) {
		return new JsonPrimitive(o.toString());
	}

}
