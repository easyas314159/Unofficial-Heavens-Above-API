package com.uhaapi.server.modules;

import javax.annotation.Nullable;

import org.apache.http.client.HttpClient;
import org.space_track.SpaceTrack;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.uhaapi.server.ServletInitOptions;

public class SpaceTrackProvider implements Provider<SpaceTrack> {
	private final HttpClient httpClient;
	
	private final String username;
	private final String password;

	@Inject
	public SpaceTrackProvider(
			HttpClient httpClient,
			@Named(ServletInitOptions.SPACETRACK_USERNAME) @Nullable String username,
			@Named(ServletInitOptions.SPACETRACK_PASSWORD) @Nullable String password
		) {
		this.httpClient = httpClient;

		this.username = username;
		this.password = password;
	}

	@Override
	public SpaceTrack get() {
		if(username == null || password == null) {
			return null;
		}
		return new SpaceTrack(httpClient, username, password);
	}
}
