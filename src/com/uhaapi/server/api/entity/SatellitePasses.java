package com.uhaapi.server.api.entity;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.uhaapi.server.geo.LatLng;

@XmlRootElement(name="satellite_passes")
@XmlAccessorType(XmlAccessType.NONE)
public class SatellitePasses {
	@XmlAttribute
	private Integer id = null;

	@XmlElement
	private LatLng location = null;
	@XmlElement
	private Double altitude = null;

	@XmlElement
	private Date from = null;
	@XmlElement
	private Date to = null;

	@XmlElement(name="pass")
	private List<SatellitePass> results = null;

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

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

	public boolean inOrbit() {
		return !(getFrom() == null || getTo() == null) && getFrom().before(getTo());
	}

	public List<SatellitePass> getResults() {
		return results;
	}
	public void setResults(List<SatellitePass> results) {
		this.results = results;
	}
}
