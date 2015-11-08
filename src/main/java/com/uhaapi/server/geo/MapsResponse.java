package com.uhaapi.server.geo;

abstract class MapsResponse {
	private MapsStatus status = null;

	public MapsStatus getStatus() {
		return status;
	}
	public void setStatus(MapsStatus status) {
		this.status = status;
	}
}
