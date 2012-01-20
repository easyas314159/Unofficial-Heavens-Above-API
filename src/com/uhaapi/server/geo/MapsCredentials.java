package com.uhaapi.server.geo;

import javax.crypto.spec.SecretKeySpec;

public interface MapsCredentials {
	public String getClientId();
	public SecretKeySpec getClientKey();
}
