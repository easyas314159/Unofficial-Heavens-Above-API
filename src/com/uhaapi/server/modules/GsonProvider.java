package com.uhaapi.server.modules;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class GsonProvider implements Provider<Gson> {
	@Inject private GsonBuilder gsonBuilder;

	@Override
	public Gson get() {
		return gsonBuilder.create();
	}
}
