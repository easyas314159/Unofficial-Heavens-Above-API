package com.uhaapi.server.api.entity;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import com.google.gson.annotations.Expose;

@XmlAccessorType(XmlAccessType.NONE)
public class IridiumFlare extends Pass {
	@Expose
	@XmlElement
	private Double magnitude;

	@Expose
	@XmlElement
	private Date time;

	@Expose
	@XmlElement
	private Double alt = null;

	@Expose
	@XmlElement
	private Double az = null;

	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}

	@Override
	public Date getStartTime() {
		return time;
	}
	@Override
	public Date getEndTime() {
		return time;
	}
	@Override
	public Double getMagnitude() {
		return magnitude;
	}
	public void setMagnitude(Double magnitude) {
		this.magnitude = magnitude;
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
