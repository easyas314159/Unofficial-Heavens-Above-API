package com.uhaapi.server.error;

import com.uhaapi.server.api.ApiStatus;

public class HeavensAboveException extends ApiException {
	public HeavensAboveException(String message) {
		super(ApiStatus.SERVICE_UNAVAILABLE, message);
	}
}
