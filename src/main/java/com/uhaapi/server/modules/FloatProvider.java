package com.uhaapi.server.modules;

import javax.servlet.ServletContext;

import com.uhaapi.server.util.ParamUtils;

public class FloatProvider extends InitParameterProvider<Float> {
	public FloatProvider(ServletContext context, String name, Float def) {
		super(context, name, def);
	}
	@Override
	public Float get() {
		return ParamUtils.asFloat(getValue(), getDefault());
	}
}
