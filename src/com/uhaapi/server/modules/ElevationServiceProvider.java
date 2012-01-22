package com.uhaapi.server.modules;

import javax.annotation.Nullable;

import net.spy.memcached.MemcachedClientIF;

import org.apache.http.client.HttpClient;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.uhaapi.server.geo.ElevationService;
import com.uhaapi.server.geo.MapsCredentials;

public class ElevationServiceProvider implements Provider<ElevationService> {
	private final HttpClient httpClient;
	private final MemcachedClientIF memcached;
	private final MapsCredentials credentials;

	@Inject
	public ElevationServiceProvider(
			@Nullable HttpClient httpClient,
			@Nullable MemcachedClientIF memcached,
			MapsCredentials credentials
		) {
		this.httpClient = httpClient;
		this.memcached = memcached;
		this.credentials = credentials;
	}

	@Override
	public ElevationService get() {
		return new ElevationService(httpClient, memcached, credentials);
	}
}
