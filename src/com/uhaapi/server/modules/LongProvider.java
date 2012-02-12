package com.uhaapi.server.modules;

import javax.servlet.ServletContext;

import com.uhaapi.server.util.ParamUtils;

public class LongProvider extends InitParameterProvider<Long> {
	public LongProvider(ServletContext context, String name, Long def) {
		super(context, name, def);
	}
	@Override
	public Long get() {
		return ParamUtils.asLong(getValue(), getDefault());
	}
}
