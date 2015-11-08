package com.uhaapi.server.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CompletedFuture<V> implements Future<V> {
	private final V re;
	private boolean cancelled = false;

	public CompletedFuture(V re) {
		this.re = re;
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		cancelled = true;
		return true;
	}

	@Override
	public V get() throws InterruptedException, ExecutionException {
		return re;
	}

	@Override
	public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		return re;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public boolean isDone() {
		return true;
	}

}
