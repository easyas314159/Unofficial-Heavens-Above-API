package com.uhaapi.server.geo;

import java.util.List;
import java.util.Vector;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.uhaapi.server.cache.Cache;
import com.uhappi.server.util.GsonUtils;

public class ElevationService extends MapsService {
	public ElevationService(String apiKey) {
		super(null, apiKey);
	}
	public ElevationService(String clientId, String clientKey) {
		super(null, clientId, clientKey);
	}

	public ElevationService(Cache<String, String> cache, String apiKey) {
		super(cache, apiKey);
	}
	public ElevationService(Cache<String, String> cache, String clientId, String clientKey) {
		super(cache, clientId, clientKey);
	}

	public ElevationResponse elevation(double lat, double lng) {
		List<NameValuePair> params = new Vector<NameValuePair>();
		params.add(new BasicNameValuePair("locations", String.format("%f,%f", lat, lng)));

		return GsonUtils.newGson().fromJson(makeRequest("/maps/api/elevation/json", params), ElevationResponse.class);
	}
}
