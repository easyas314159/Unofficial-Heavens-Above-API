package com.uhaapi.server.geo;

import javax.crypto.spec.SecretKeySpec;

public class BasicCredentials implements MapsCredentials {
	private final String apiKey;

	public BasicCredentials(String apiKey) {
		this.apiKey = apiKey;
	}

	@Override
	public String getClientId() {
		return apiKey;
	}
	@Override
	public SecretKeySpec getClientKey() {
		return null;
	}

}
