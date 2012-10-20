package com.uhaapi.server.geo;

import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class MapsCredentials {
	private final String clientId;
	private final SecretKeySpec clientKey;

	public MapsCredentials(String clientId, String clientKey) {
		this(
				clientId,
				new SecretKeySpec(
						Base64.decodeBase64(clientKey),
						MapsService.SIGNING_ALGORITHM
					)
			);
	}
	public MapsCredentials(String clientId, SecretKeySpec clientKey) {
		this.clientId = clientId;
		this.clientKey = clientKey;
	}

	public String getClientId() {
		return clientId;
	}
	public SecretKeySpec getClientKey() {
		return clientKey;
	}
}
