package com.uhaapi.server.error;

import com.uhaapi.server.api.ApiStatus;

public class InternalServerException extends ApiException {
	public InternalServerException(String message) {
		super(ApiStatus.INTERNAL_SERVER_ERROR, message);
	}
}
