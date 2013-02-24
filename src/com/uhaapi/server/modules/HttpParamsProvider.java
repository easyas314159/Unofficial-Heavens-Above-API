package com.uhaapi.server.modules;

import javax.annotation.Nullable;

import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.SyncBasicHttpParams;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.uhaapi.server.ServletInitOptions;

public class HttpParamsProvider implements Provider<HttpParams> {
	private final String userAgent;
	private final Integer connectionTimeout;

	@Inject
	public HttpParamsProvider(
			@Named(ServletInitOptions.HTTP_USER_AGENT) @Nullable String userAgent,
			@Named(ServletInitOptions.HTTP_CONNECTION_TIMEOUT) @Nullable Integer connectionTimeout
		){
		this.userAgent = userAgent;
		this.connectionTimeout = connectionTimeout;
	}

	@Override
	public HttpParams get() {
		HttpParams params = new SyncBasicHttpParams();

		if(userAgent != null) {
			params.setParameter(CoreProtocolPNames.USER_AGENT, userAgent);
		}
		if(connectionTimeout != null) {
			HttpConnectionParams.setConnectionTimeout(params, connectionTimeout);
		}

		return params;
	}
}
