package com.uhaapi.server.modules;

import javax.annotation.Nullable;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.cache.CachingHttpClient;
import org.apache.http.params.HttpParams;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.uhaapi.server.http.GZIPInterceptor;

public class HttpClientProvider implements Provider<HttpClient> {
	private final ClientConnectionManager connectionManager;
	private final HttpParams httpParams;

	@Inject
	public HttpClientProvider(
			@Nullable ClientConnectionManager connectionManager,
			@Nullable HttpParams httpParams
		) {
		this.connectionManager = connectionManager;
		this.httpParams = httpParams;
	}

	@Override
	public HttpClient get() {
		DefaultHttpClient httpClient = new DefaultHttpClient(connectionManager, httpParams);

		GZIPInterceptor gzipInterceptor = new GZIPInterceptor();
		httpClient.addRequestInterceptor(gzipInterceptor);
		httpClient.addResponseInterceptor(gzipInterceptor);

		return new CachingHttpClient(httpClient);
	}
}
