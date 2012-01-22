package com.uhaapi.server;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class LoadFilter implements Runnable, Filter {
	private final Logger log = Logger.getLogger(getClass());

	private final AtomicLong requestTime;
	private final AtomicLong requestCounter;

	private ScheduledFuture<?> future = null;
	private ScheduledExecutorService executor = null;

	@Inject
	public LoadFilter() {
		requestTime = new AtomicLong();
		requestCounter = new AtomicLong();

		executor = Executors.newSingleThreadScheduledExecutor();
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		future = executor.scheduleWithFixedDelay(this, 1, 1, TimeUnit.MINUTES);
		
		
	}
	@Override
	public void destroy() {
		future.cancel(false);
		try {
			executor.shutdown();
			if(!executor.awaitTermination(5, TimeUnit.SECONDS)) {
				executor.shutdownNow();
			}
		} catch(InterruptedException ex) {
			log.error("Threadpool shutdown failed", ex);
		}
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse rsp,
			FilterChain chain) throws IOException, ServletException {

		long elapsed = System.nanoTime();
		chain.doFilter(req, rsp);
		elapsed = System.nanoTime() - elapsed;

		requestTime.addAndGet(elapsed);
		requestCounter.incrementAndGet();
	}

	@Override
	public void run() {
		Long requests = 0L;
		do {
			requests = requestCounter.get();
		} while(!requestCounter.compareAndSet(requests, 0));

		Long average = 0L;
		do {
			average = requestTime.get();
		} while(!requestTime.compareAndSet(average, 0));

		if(requests > 0L) {
			average = average / requests;
		}
	}
}
