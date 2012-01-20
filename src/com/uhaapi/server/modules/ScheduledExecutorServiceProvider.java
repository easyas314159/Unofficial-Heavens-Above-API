package com.uhaapi.server.modules;

import java.util.concurrent.ScheduledExecutorService;

import javax.servlet.ServletContext;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.uhaapi.server.ThreadPoolListener;

@Singleton
public class ScheduledExecutorServiceProvider implements Provider<ScheduledExecutorService> {
	private final ScheduledExecutorService executor;

	@Inject
	public ScheduledExecutorServiceProvider(ServletContext context) {
		this.executor = (ScheduledExecutorService)context.getAttribute(ThreadPoolListener.THREAD_POOL);
	}

	@Override
	public ScheduledExecutorService get() {
		return executor;
	}
}
