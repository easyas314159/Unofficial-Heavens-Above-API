package com.uhaapi.server.modules;

import org.apache.http.impl.client.cache.CacheConfig;

import com.google.inject.Provider;

public class CacheConfigProvider implements Provider<CacheConfig> {
	@Override
	public CacheConfig get() {
		return new CacheConfig();
	}
}
