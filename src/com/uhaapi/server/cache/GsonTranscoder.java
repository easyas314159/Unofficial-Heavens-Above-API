package com.uhaapi.server.cache;

import net.spy.memcached.CachedData;
import net.spy.memcached.transcoders.Transcoder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonTranscoder<T> implements Transcoder<T> {
	private Gson gson = null;
	private Class<T> baseType = null;

	public GsonTranscoder(Class<T> baseType) {
		this(new GsonBuilder(), baseType);
	}
	public GsonTranscoder(GsonBuilder gsonBuilder, Class<T> baseType) {
		this.gson = gsonBuilder.create();
		this.baseType = baseType;
	}

	@Override
	public boolean asyncDecode(CachedData data) {
		return false;
	}
	@Override
	public T decode(CachedData data) {
		return gson.fromJson(new String(data.getData()), baseType);
	}
	@Override
	public CachedData encode(T o) {
		return new CachedData(0, gson.toJson(o, baseType).getBytes(), 1<<20);
	}

	@Override
	public int getMaxSize() {
		return 1<<20;
	}
}
