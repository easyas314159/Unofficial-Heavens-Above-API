package com.uhaapi.server.modules;

import javax.annotation.Nullable;

import Pachube.Pachube;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.uhaapi.server.ServletInitOptions;

public class PachubeProvider implements Provider<Pachube> {
	private final String apiKey;

	@Inject
	public PachubeProvider(@Named(ServletInitOptions.PACHUBE_API_KEY) @Nullable String apiKey) {
		this.apiKey = apiKey;
	}

	@Override
	public Pachube get() {
		if(apiKey == null) {
			return null;
		}
		return new Pachube(apiKey);
	}
}
