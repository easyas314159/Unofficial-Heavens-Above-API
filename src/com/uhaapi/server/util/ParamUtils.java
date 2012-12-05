package com.uhaapi.server.util;

import org.apache.commons.lang3.StringUtils;

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
	
	public static Boolean asBoolean(String value) {
		return asBoolean(value, null);
	}
	public static Boolean asBoolean(String value, Boolean def) {
		value = StringUtils.trimToNull(value);
		if(value == null) {
			return Boolean.valueOf(def);
		}
		return Boolean.valueOf(value);
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

	public static Float asFloat(String value, Float def) {
		value = StringUtils.trimToNull(value);
		if(value == null) {
			return def;
		}

		Float result = def;
		try {
			result = Float.valueOf(value);
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

	public static <T extends Enum<T>> T asEnum(Class<T> clazz, String value) { 
		return asEnum(clazz, value, null);
	}
	public static <T extends Enum<T>> T asEnum(Class<T> clazz, String value, T def) {
		value = StringUtils.trimToNull(value);
		if(value == null) {
			return def;
		}

		T result = def;
		try {
			result = Enum.valueOf(clazz, value);
		}
		catch(Throwable t) {}
		return result;
	}
	
	public static <T> T coalesce(T ... ts) {
		for(T t : ts) {
			if(t == null) {
				continue;
			}
			return t;
		}
		return null;
	}
}
