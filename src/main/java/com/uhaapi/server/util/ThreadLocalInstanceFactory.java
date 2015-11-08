package com.uhaapi.server.util;

public abstract class ThreadLocalInstanceFactory<T> {
	private ThreadLocal<T> threadLocal = new ThreadLocal<T>();

	public T getInstance() {
		T instance = threadLocal.get();
		if(instance == null) {
			threadLocal.set(instance = instantiate());
		}
		return configure(instance);
	}
	protected abstract T instantiate();
	protected T configure(T o) {
		return o;
	}
}
