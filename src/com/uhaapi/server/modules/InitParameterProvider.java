package com.uhaapi.server.modules;

import javax.servlet.ServletContext;

import com.google.inject.Provider;

public abstract class InitParameterProvider<T> implements Provider<T> {
	private final ServletContext context;
	private final String name;
	private final T def;

	protected InitParameterProvider(ServletContext context, String name) {
		this(context, name, null);
	}
	protected InitParameterProvider(ServletContext context, String name, T def) {
		this.context = context;
		this.name = name;
		this.def = def;
	}

	protected String getValue() {
		return context.getInitParameter(name);
	}
	protected T getDefault() {
		return def;
	}
}
