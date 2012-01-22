package com.uhaapi.server.modules;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.params.HttpParams;

import com.google.inject.AbstractModule;

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
	}
}
