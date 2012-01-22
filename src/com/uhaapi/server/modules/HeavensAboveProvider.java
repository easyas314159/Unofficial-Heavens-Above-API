package com.uhaapi.server.modules;

import org.apache.http.client.HttpClient;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.heavens_above.HeavensAbove;

public class HeavensAboveProvider implements Provider<HeavensAbove> {
	private final HttpClient httpClient;

	@Inject
	public HeavensAboveProvider(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

	@Override
	public HeavensAbove get() {
		return new HeavensAbove(httpClient);
	}
}
