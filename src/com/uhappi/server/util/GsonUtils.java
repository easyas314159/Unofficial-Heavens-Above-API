package com.uhappi.server.util;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public final class GsonUtils {
	private GsonUtils() {}

	private static final GsonBuilder gsonBuilder = new GsonBuilder();

	public static synchronized GsonBuilder getGsonBuilder() {
		return gsonBuilder;
	}
	public static synchronized Gson newGson() {
		return getGsonBuilder().create();
	}
	public static synchronized <T> Type getType() {
		return new TypeToken<T>() {}.getType();
	}
}
