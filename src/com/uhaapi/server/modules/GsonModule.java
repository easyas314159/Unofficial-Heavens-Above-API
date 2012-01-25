package com.uhaapi.server.modules;

import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.servlet.RequestScoped;
import com.uhaapi.server.util.DateAdapter;

public class GsonModule extends AbstractModule {
	@Override
	protected void configure() {
		GsonBuilder gsonBuilder = new GsonBuilder()
			.registerTypeAdapter(Date.class, new DateAdapter())
			.setPrettyPrinting()
			;

		// TODO: Create a provider for GSON builders so we can inject things like pretty printing
		
		bind(GsonBuilder.class)
			.toInstance(gsonBuilder);
		bind(Gson.class)
			.toProvider(GsonProvider.class)
			.in(RequestScoped.class);
	}
}
