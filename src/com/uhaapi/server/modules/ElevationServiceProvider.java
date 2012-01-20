package com.uhaapi.server.modules;

import javax.annotation.Nullable;

import net.spy.memcached.MemcachedClientIF;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.uhaapi.server.geo.ElevationService;
import com.uhaapi.server.geo.MapsCredentials;

public class ElevationServiceProvider implements Provider<ElevationService> {
	private final MemcachedClientIF memcached;
	private final MapsCredentials credentials;

	@Inject
	public ElevationServiceProvider(@Nullable MemcachedClientIF memcached, MapsCredentials credentials) {
		this.memcached = memcached;
		this.credentials = credentials;
	}

	@Override
	public ElevationService get() {
		return new ElevationService(memcached, credentials);
	}
}
