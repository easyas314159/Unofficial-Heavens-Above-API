package com.uhaapi.server.modules;

import javax.annotation.Nullable;

import org.apache.http.client.HttpClient;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.uhaapi.server.geo.ElevationService;
import com.uhaapi.server.geo.MapsCredentials;

public class ElevationServiceProvider implements Provider<ElevationService> {
	private final HttpClient httpClient;
	private final MapsCredentials credentials;

	@Inject
	public ElevationServiceProvider(
			@Nullable HttpClient httpClient,
			@Nullable MapsCredentials credentials
		) {
		this.httpClient = httpClient;
		this.credentials = credentials;
	}

	@Override
	public ElevationService get() {
		return new ElevationService(httpClient, credentials);
	}
}
