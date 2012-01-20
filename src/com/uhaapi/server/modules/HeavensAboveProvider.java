package com.uhaapi.server.modules;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.uhaapi.server.ServletInitOptions;
import com.uhaapi.server.util.HeavensAbove;

public class HeavensAboveProvider implements Provider<HeavensAbove> {
	private final String userAgent;

	@Inject
	public HeavensAboveProvider(@Named(ServletInitOptions.APP_USER_AGENT) String userAgent) {
		this.userAgent = userAgent;
	}

	@Override
	public HeavensAbove get() {
		return new HeavensAbove(userAgent);
	}
}
