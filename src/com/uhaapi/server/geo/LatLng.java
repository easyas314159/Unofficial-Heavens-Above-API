package com.uhaapi.server.geo;

public class LatLng {
	private double lat;
	private double lng;

	public LatLng() {
		this(0.0, 0.0);
	}
	public LatLng(double lat, double lng) {
		this.lat = lat;
		this.lng = lng;
	}
	
	public double getLat() {
		return lat;
	}
	public double getLng() {
		return lng;
	}
}
