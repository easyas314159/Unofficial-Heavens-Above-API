package com.uhaapi.server.api.entity;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class SatellitePass extends Pass {
	@XmlElement
	public Double magnitude;

	@XmlElement
	public SatellitePassWaypoint start;
	@XmlElement
	public SatellitePassWaypoint max;
	@XmlElement
	public SatellitePassWaypoint end;

	@Override
	public Double getMagnitude() {
		return magnitude;
	}
	public void setMagnitude(Double magnitude) {
		this.magnitude = magnitude;
	}

	public SatellitePassWaypoint getStart() {
		return start;
	}
	public void setStart(SatellitePassWaypoint start) {
		this.start = start;
	}
	public SatellitePassWaypoint getMax() {
		return max;
	}
	public void setMax(SatellitePassWaypoint max) {
		this.max = max;
	}
	public SatellitePassWaypoint getEnd() {
		return end;
	}
	public void setEnd(SatellitePassWaypoint end) {
		this.end = end;
	}

	@Override
	public Date getStartTime() {
		return getStart().getTime();
	}
	@Override
	public Date getEndTime() {
		return getEnd().getTime();
	}
}
