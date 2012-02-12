package com.uhaapi.server.modules;

import org.apache.http.client.HttpClient;
import org.apache.http.client.cache.HttpCacheEntrySerializer;
import org.apache.http.client.cache.HttpCacheStorage;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.params.HttpParams;

import com.google.inject.AbstractModule;
import com.google.inject.util.Providers;

public class HttpModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(HttpParams.class)
			.toProvider(HttpParamsProvider.class)
			.asEagerSingleton();
		bind(ClientConnectionManager.class)
			.toProvider(ClientConnectionManagerProvider.class)
			.asEagerSingleton();
		bind(HttpClient.class)
			.toProvider(HttpClientProvider.class)
			.asEagerSingleton();
		bind(HttpCacheStorage.class)
			.toProvider(HttpCacheStorageProvider.class)
			.asEagerSingleton();

		bind(CacheConfig.class)
			.toProvider(CacheConfigProvider.class)
			.asEagerSingleton();
		bind(HttpCacheEntrySerializer.class)
			.toProvider(Providers.<HttpCacheEntrySerializer>of(null))
			.asEagerSingleton();
	}
}
