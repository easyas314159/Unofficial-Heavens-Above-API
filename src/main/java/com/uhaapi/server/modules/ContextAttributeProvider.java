package com.uhaapi.server.modules;

import javax.servlet.ServletContext;

import com.google.inject.Provider;

public class ContextAttributeProvider<T> implements Provider<T> {
	private final ServletContext context;
	private final String key;
	private final T def;

	public ContextAttributeProvider(ServletContext ctx, String key) {
		this(ctx, key, null);
	}
	public ContextAttributeProvider(ServletContext ctx, String key, T def) {
		this.context = ctx;
		this.key = key;
		this.def = def;
	}

	@Override
	public T get() {
		T instance = (T)context.getAttribute(key);
		return instance == null ? def : instance;
	}
}
