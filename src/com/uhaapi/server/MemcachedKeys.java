package com.uhaapi.server;

import org.apache.commons.codec.digest.DigestUtils;

public final class MemcachedKeys {
	public static final String HTTP_CACHE = DigestUtils.md5Hex("http");

	public static final String SATELLITES = DigestUtils.md5Hex("satellites");
	public static final String SATELLITE_PASSES = DigestUtils.md5Hex("satellites/passes");
	public static final String SATELLITE_TLE = DigestUtils.md5Hex("satellites/tle");

	private MemcachedKeys() {}
}
