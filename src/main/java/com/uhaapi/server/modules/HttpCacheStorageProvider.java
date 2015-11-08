package com.uhaapi.server.modules;

import javax.annotation.Nullable;

import net.spy.memcached.KeyedMemcachedClient;
import net.spy.memcached.MemcachedClientIF;

import org.apache.http.client.cache.HttpCacheEntrySerializer;
import org.apache.http.client.cache.HttpCacheStorage;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.memcached.MemcachedHttpCacheStorage;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.uhaapi.server.MemcachedKeys;

public class HttpCacheStorageProvider implements Provider<HttpCacheStorage> {
	private final MemcachedClientIF memcached;
	private final CacheConfig config;
	private final HttpCacheEntrySerializer serializer;

	@Inject
	public HttpCacheStorageProvider(
			MemcachedClientIF memcached,
			@Nullable CacheConfig config,
			@Nullable HttpCacheEntrySerializer serializer
		) {
		this.memcached = new KeyedMemcachedClient(memcached, MemcachedKeys.HTTP_CACHE);

		this.config = config;
		this.serializer = serializer;
	}

	@Override
	public HttpCacheStorage get() {
		return new MemcachedHttpCacheStorage(memcached, config, serializer);
	}
}
