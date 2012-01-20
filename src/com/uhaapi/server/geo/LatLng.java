package com.uhaapi.server.geo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class LatLng {
	@XmlElement
	private double lat;
	@XmlElement
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
