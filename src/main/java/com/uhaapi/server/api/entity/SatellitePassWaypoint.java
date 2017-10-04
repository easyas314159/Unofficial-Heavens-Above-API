package com.uhaapi.server.api.entity;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import com.google.gson.annotations.Expose;

@XmlAccessorType(XmlAccessType.NONE)
public class SatellitePassWaypoint {
	@Expose
	@XmlElement
	private Date time = null;

	@Expose
	@XmlElement
	private Double alt = null;

	@Expose
	@XmlElement
	private Double az = null;

	public SatellitePassWaypoint() {
	}
	public SatellitePassWaypoint(Date time, Double alt, Double az) {
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
