package com.uhaapi.server.geo;

import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class SignedCredentials implements MapsCredentials {
	private final String clientId;
	private final SecretKeySpec clientKey;

	public SignedCredentials(String clientId, String clientKey) {
		this(
				clientId,
				new SecretKeySpec(
						Base64.decodeBase64(clientKey),
						MapsService.SIGNING_ALGORITHM
					)
			);
	}
	public SignedCredentials(String clientId, SecretKeySpec clientKey) {
		this.clientId = clientId;
		this.clientKey = clientKey;
	}

	@Override
	public String getClientId() {
		return clientId;
	}
	@Override
	public SecretKeySpec getClientKey() {
		return clientKey;
	}
}
