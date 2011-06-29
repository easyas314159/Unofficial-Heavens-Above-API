package com.uhappi.server.util;

import org.apache.commons.lang.StringUtils;

public final class ParamUtils {
	private ParamUtils() {}

	public static String asString(String value) {
		return asString(value, null);
	}
	public static String asString(String value, String def) {
		if(value == null) {
			return def;
		}
		return value;
	}
	
	public static Integer asInteger(String value) {
		return asInteger(value, null);
	}
	public static Integer asInteger(String value, Integer def) {
		value = StringUtils.trimToNull(value);
		if(value == null) {
			return def;
		}

		Integer result = def;
		try {
			result = Integer.valueOf(value);
		}
		catch(Throwable t) {}
		return result;
	}

	public static Long asLong(String value) {
		return asLong(value, null);
	}
	public static Long asLong(String value, Long def) {
		value = StringUtils.trimToNull(value);
		if(value == null) {
			return def;
		}

		Long result = null;
		try {
			result = Long.valueOf(value);
		}
		catch(Throwable t) {}
		return result;
	}

	public static Double asDouble(String value) {
		return asDouble(value, null);
	}
	public static Double asDouble(String value, Double def) {
		value = StringUtils.trimToNull(value);
		if(value == null) {
			return def;
		}

		Double result = def;
		try {
			result = Double.valueOf(value);
		}
		catch(Throwable t) {}
		return result;
	}
}
