package com.uhaapi.server.error;

import com.uhaapi.server.api.ApiStatus;

public class NotFoundException extends ApiException {
	public NotFoundException() {
		this(null);
	}
	public NotFoundException(String message) {
		super(ApiStatus.NOT_FOUND, message);
	}
}
