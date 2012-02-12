package com.uhaapi.server.modules;

import javax.annotation.Nullable;

import org.apache.http.impl.client.cache.CacheConfig;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.uhaapi.server.ServletInitOptions;

public class CacheConfigProvider implements Provider<CacheConfig> {
	private final Boolean heuristicCache;
	private final Float heuristicCoefficient;
	private final Long heuristicLifetime;

	@Inject
	public CacheConfigProvider(
			@Named(ServletInitOptions.HTTP_HEURISTIC_CACHE) Boolean heuristicCache,
			@Named(ServletInitOptions.HTTP_HEURISTIC_COEFFICIENT) @Nullable Float heuristicCoefficient,
			@Named(ServletInitOptions.HTTP_HEURISTIC_LIFETIME) @Nullable Long heuristicLifetime
		) {
		this.heuristicCache = heuristicCache;
		this.heuristicCoefficient = heuristicCoefficient;
		this.heuristicLifetime = heuristicLifetime;
	}

	@Override
	public CacheConfig get() {
		CacheConfig cfg = new CacheConfig();

		cfg.setHeuristicCachingEnabled(heuristicCache);
		if(cfg.isHeuristicCachingEnabled()) {
			if(heuristicCoefficient != null) {
				cfg.setHeuristicCoefficient(heuristicCoefficient);
			}
			if(heuristicLifetime != null) {
				cfg.setHeuristicDefaultLifetime(heuristicLifetime);
			}
		}

		return cfg;
	}
}
