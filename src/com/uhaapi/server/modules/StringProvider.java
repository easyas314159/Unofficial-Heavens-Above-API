package com.uhaapi.server.modules;

import javax.servlet.ServletContext;

import com.uhaapi.server.util.ParamUtils;

public class StringProvider extends InitParameterProvider<String> {
	public StringProvider(ServletContext context, String name, String def) {
		super(context, name, def);
	}
	@Override
	public String get() {
		return ParamUtils.asString(getValue(), getDefault());
	}
}
