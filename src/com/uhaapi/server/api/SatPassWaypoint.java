package com.uhaapi.server.api;

import java.util.Date;

public class SatPassWaypoint {
	private Date time = null;
	private Double alt = null;
	private Double az = null;

	public SatPassWaypoint() {
	}
	public SatPassWaypoint(Date time, Double alt, Double az) {
		setTime(time);
		setAltitude(alt);
		setAzimuth(az);
	}

	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public Double getAltitude() {
		return alt;
	}
	public void setAltitude(Double altitude) {
		this.alt = altitude;
	}
	public Double getAzimuth() {
		return az;
	}
	public void setAzimuth(Double azimuth) {
		this.az = azimuth;
	}
}
