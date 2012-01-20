package com.uhaapi.server.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.uhaapi.server.geo.ElevationService;

public class MapsServicesModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(ElevationService.class)
			.toProvider(ElevationServiceProvider.class)
			.in(Singleton.class);
	}
}
