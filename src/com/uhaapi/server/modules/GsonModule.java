package com.uhaapi.server.modules;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;

public class GsonModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(GsonBuilder.class)
			.toProvider(GsonBuilderProvider.class)
			.asEagerSingleton();
		bind(Gson.class)
			.toProvider(GsonProvider.class)
			.asEagerSingleton();
	}
}
