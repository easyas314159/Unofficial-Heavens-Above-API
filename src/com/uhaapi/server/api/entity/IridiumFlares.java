package com.uhaapi.server.api.entity;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.gson.annotations.Expose;
import com.uhaapi.server.geo.LatLng;

@XmlRootElement(name="iridium_flares")
@XmlAccessorType(XmlAccessType.NONE)
public class IridiumFlares {
	@Expose
	@XmlElement
	private LatLng location = null;

	@Expose
	@XmlElement
	private Double altitude = null;

	@Expose
	@XmlElement
	private Date from = null;
	@Expose
	@XmlElement
	private Date to = null;

	@Expose
	@XmlElement(name="flare")
	private List<IridiumFlare> results = null;
	
	public LatLng getLocation() {
		return location;
	}

	public void setLocation(LatLng location) {
		this.location = location;
	}

	public Double getAltitude() {
		return altitude;
	}

	public void setAltitude(Double altitude) {
		this.altitude = altitude;
	}

	public Date getFrom() {
		return from;
	}
	public void setFrom(Date from) {
		this.from = from;
	}

	public Date getTo() {
		return to;
	}
	public void setTo(Date to) {
		this.to = to;
	}

	public List<IridiumFlare> getResults() {
		return results;
	}
	public void setResults(List<IridiumFlare> results) {
		this.results = results;
	}
}
