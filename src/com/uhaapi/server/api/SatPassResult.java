package com.uhaapi.server.api;

public class SatPassResult {
	public Double magnitude;

	public SatPassWaypoint start;
	public SatPassWaypoint max;
	public SatPassWaypoint end;
	
	public Double getMagnitude() {
		return magnitude;
	}
	public void setMagnitude(Double magnitude) {
		this.magnitude = magnitude;
	}

	public SatPassWaypoint getStart() {
		return start;
	}
	public void setStart(SatPassWaypoint start) {
		this.start = start;
	}
	public SatPassWaypoint getMax() {
		return max;
	}
	public void setMax(SatPassWaypoint max) {
		this.max = max;
	}
	public SatPassWaypoint getEnd() {
		return end;
	}
	public void setEnd(SatPassWaypoint end) {
		this.end = end;
	}
}
