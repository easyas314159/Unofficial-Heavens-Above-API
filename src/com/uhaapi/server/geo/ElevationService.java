package com.uhaapi.server.geo;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import net.spy.memcached.MemcachedClientIF;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.uhaapi.server.util.GsonUtils;

public class ElevationService extends MapsService {
	public ElevationService(MemcachedClientIF memcached, MapsCredentials credentials) {
		super(memcached, credentials);
	}

	public Future<ElevationResponse> elevationAsync(final double lat, final double lng) {
		return threadPool.submit(new Callable<ElevationResponse>() {
			@Override
			public ElevationResponse call() throws Exception {
				return elevation(lat, lng);
			}
		});
	}

	public ElevationResponse elevation(double lat, double lng) {
		List<NameValuePair> params = new Vector<NameValuePair>();
		params.add(new BasicNameValuePair("locations", String.format("%f,%f", lat, lng)));

		return GsonUtils.newGson().fromJson(makeRequest("/maps/api/elevation/json", params), ElevationResponse.class);
	}
}
