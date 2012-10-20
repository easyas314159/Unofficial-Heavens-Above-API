package com.uhaapi.server.modules;

import javax.annotation.Nullable;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.uhaapi.server.ServletInitOptions;
import com.uhaapi.server.geo.MapsCredentials;

public class MapsCredentialsProvider implements Provider<MapsCredentials> {
	private final String clientId;
	private final String clientKey;

	@Inject
	public MapsCredentialsProvider(
			@Named(ServletInitOptions.MAPS_CLIENT_ID) @Nullable String clientId,
			@Named(ServletInitOptions.MAPS_CLIENT_KEY) @Nullable String clientKey
		) {
		this.clientId = clientId;
		this.clientKey = clientKey;
	}

	@Override
	public MapsCredentials get() {
		if(clientId == null || clientKey == null) {
			return null;
		}

		return new MapsCredentials(clientId, clientKey);
	}
}
