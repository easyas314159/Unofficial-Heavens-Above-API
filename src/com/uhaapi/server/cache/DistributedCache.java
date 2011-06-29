package com.uhaapi.server.cache;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import net.spy.memcached.MemcachedClient;
import net.spy.memcached.transcoders.Transcoder;

public class DistributedCache<Value> extends Cache<String, Value> {
	private MemcachedClient memcached = null;

	private String namespace = null;
	private Transcoder<Value> transcoder = null;

	public DistributedCache(MemcachedClient memcached) {
		this(memcached, null, null);
	}
	public DistributedCache(MemcachedClient memcached, String namespace) {
		this(memcached, null, namespace);
	}
	public DistributedCache(MemcachedClient memcached, Transcoder<Value> transcoder) {
		this(memcached, transcoder, null);
	}
	public DistributedCache(MemcachedClient memcached, Transcoder<Value> transcoder, String namespace) {
		this.memcached = memcached;
		this.transcoder = transcoder;
		if(namespace == null) {
			namespace = "";
		}
		this.namespace = namespace;
	}

	@Override
	public Value get(String key) {
		Value result = null;
		Future<Value> f = memcached.asyncGet(namespaceifyKey(key), transcoder);
		try {
			result = f.get(1, TimeUnit.SECONDS);
		}
		catch(Exception ex) {
			f.cancel(false);
		}
		return result;
	}
	@Override
	public void put(String key, Value value, int exp) {
		key = namespaceifyKey(key);
		if(transcoder == null) {
			memcached.set(key, exp, value);
		}
		else {
			memcached.set(key, exp, value, transcoder);
		}
	}

	@Override
	public void clear() {
	}
	@Override
	public void expire(String key) {
		memcached.delete(namespaceifyKey(key));
	}

	public String namespaceifyKey(String key) {
		return namespace + key;
	}
}
