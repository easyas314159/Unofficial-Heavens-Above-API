package com.uhaapi.server.modules;

import javax.annotation.Nullable;

import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.SyncBasicHttpParams;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.uhaapi.server.ServletInitOptions;

public class HttpParamsProvider implements Provider<HttpParams> {
	private final String userAgent;

	@Inject
	public HttpParamsProvider(
			@Named(ServletInitOptions.HTTP_USER_AGENT) @Nullable String userAgent
		){
		this.userAgent = userAgent;
	}

	@Override
	public HttpParams get() {
		HttpParams params = new SyncBasicHttpParams();

		if(userAgent != null) {
			params.setParameter(CoreProtocolPNames.USER_AGENT, userAgent);
		}

		return params;
	}
}
