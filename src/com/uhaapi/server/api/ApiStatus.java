package com.uhaapi.server.api;

import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.Response.StatusType;

public enum ApiStatus implements StatusType {
	// Success Codes
	OK(Family.SUCCESSFUL, 200),
	CREATE(Family.SUCCESSFUL, 201),
	ACCEPTED(Family.SUCCESSFUL, 202),
	NO_CONTENT(Family.SUCCESSFUL, 204),
	RESET_CONTENT(Family.SUCCESSFUL, 205),

	// Redirection
	MULTIPLE_CHOICES(Family.REDIRECTION, 300),
	MOVED_PERMANENTLY(Family.REDIRECTION, 301),
	FOUND(Family.REDIRECTION, 302),
	SEE_OTHER(Family.REDIRECTION, 303),
	NOT_MODIFIED(Family.REDIRECTION, 304),
	TEMPORARY_REDIRECT(Family.REDIRECTION, 307),

	// Client Errors
	BAD_REQUEST(Family.CLIENT_ERROR, 400),
	UNAUTHORIZED(Family.CLIENT_ERROR, 401),
	FORBIDDEN(Family.CLIENT_ERROR, 403),
	NOT_FOUND(Family.CLIENT_ERROR, 404),
	METHOD_NOT_ALLOWED(Family.CLIENT_ERROR, 405),
	REQUEST_ENTITY_TO_LARGE(Family.CLIENT_ERROR, 413),
	IM_A_TEAPOT(Family.CLIENT_ERROR, 418),
	UNPROCESSABLE_ENTITY(Family.CLIENT_ERROR, 422),

	// Server Errors
	INTERNAL_SERVER_ERROR(Family.SERVER_ERROR, 500),
	NOT_IMPLEMENTED(Family.SERVER_ERROR, 501),
	SERVICE_UNAVAILABLE(Family.SERVER_ERROR, 503),
	;

	private final Family family;
	private final int statusCode;
	private final String reasonPhrase;

	private ApiStatus(Family family, int statusCode) {
		this(family, statusCode, null);
	}
	private ApiStatus(Family family, int statusCode, String reasonPhrase) {
		this.family = family;
		this.statusCode = statusCode;
		this.reasonPhrase = reasonPhrase;
	}

	@Override
	public Family getFamily() {
		return family;
	}
	@Override
	public int getStatusCode() {
		return statusCode;
	}
	@Override
	public String getReasonPhrase() {
		return reasonPhrase;
	}
}
