package com.uhaapi.server.error;

import javax.ws.rs.WebApplicationException;

import com.uhaapi.server.api.ApiStatus;
import com.uhaapi.server.api.entity.ApiError;

public class ApiException extends WebApplicationException {
	private final ApiStatus code;
	private final String message;

	public ApiException(ApiStatus code, String message) {
		this.code = code;
		this.message = message;
	}

	public ApiStatus getCode() {
		return code;
	}
	@Override
	public String getMessage() {
		return message;
	}
	
	public ApiError getError() {
		return new ApiError(getCode(), getMessage());
	}
}
