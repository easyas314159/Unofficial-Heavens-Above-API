package com.uhaapi.server.api;

public class UhaapiResponse {
	private StatusCodes status = null;

	public UhaapiResponse() {
		this(StatusCodes.OK);
	}
	public UhaapiResponse(StatusCodes status) {
		this.status = status;
	}
	
	public StatusCodes getStatus() {
		return status;
	}
	public void setStatus(StatusCodes status) {
		this.status = status;
	}
}
