package com.uhaapi.server.cache;

import java.util.Date;

public abstract class Cache<Key, Value> {
	public abstract Value get(Key key);
	public abstract void put(Key key, Value value, int exp);
	public void put(Key key, Value value, Date exp) {
		put(key, value, (int)(exp.getTime() / 1000));
	}

	public abstract void clear();
	public abstract void expire(Key key);
}
