package com.uhaapi.server.modules;

import javax.servlet.ServletContext;

import net.spy.memcached.MemcachedClientIF;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.uhaapi.server.MemcachedListener;

public class MemcachedClientIFProvider implements Provider<MemcachedClientIF> {
	private final MemcachedClientIF memcached;

	@Inject
	public MemcachedClientIFProvider(ServletContext context) {
		this.memcached = (MemcachedClientIF)context.getAttribute(MemcachedListener.MEMCACHED);
	}

	@Override
	public MemcachedClientIF get() {
		return memcached;
	}

}
