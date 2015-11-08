package com.uhaapi.server.modules;

import javax.servlet.ServletContext;

import com.uhaapi.server.util.ParamUtils;

public class BooleanProvider extends InitParameterProvider<Boolean> {
	protected BooleanProvider(ServletContext context, String name) {
		super(context, name);
	}
	protected BooleanProvider(ServletContext context, String name, Boolean def) {
		super(context, name, def);
	}

	@Override
	public Boolean get() {
		return ParamUtils.asBoolean(getValue(), getDefault());
	}
}
