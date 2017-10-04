package com.uhaapi.server.api.entity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.gson.annotations.Expose;
import com.uhaapi.server.api.ApiStatus;

@XmlRootElement(name="error")
@XmlAccessorType(XmlAccessType.NONE)
public class ApiError {
	@Expose
	@XmlElement
	private final ApiStatus code;

	@Expose
	@XmlElement
	private final String message;

	@SuppressWarnings("unused")
	private ApiError() {
		this(null, null);
	}
	
	public ApiError(ApiStatus code, String message) {
		this.code = code;
		this.message = message;
	}

	public ApiStatus getCode() {
		return code;
	}
	public String getMessage() {
		return message;
	}
}
