package com.uhaapi.server.modules;

import javax.servlet.ServletContext;

import com.uhaapi.server.util.ParamUtils;

public class IntegerProvider extends InitParameterProvider<Integer> {
	public IntegerProvider(ServletContext context, String name, Integer def) {
		super(context, name, def);
	}
	@Override
	public Integer get() {
		return ParamUtils.asInteger(getValue(), getDefault());
	}
}
