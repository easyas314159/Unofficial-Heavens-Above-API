package com.uhaapi.server.cache;

import java.util.HashMap;
import java.util.Map;

public class SimpleCache<Key, Value> extends Cache<Key, Value> {
	private Map<Key, Entry<Value>> cache;

	public SimpleCache() {
		cache = new HashMap<Key, Entry<Value>>();
	}

	@Override
	public Value get(Key key) {
		Entry<Value> entry = cache.get(key);
		if(entry == null || entry.isExpired()) {
			return null;
		}
		return entry.getValue();
	}
	@Override
	public void put(Key key, Value value, int exp) {
		cache.put(
				key,
				new Entry<Value>(
						value,
						1000L * exp + (exp > 2592000 ? 0 : System.currentTimeMillis())
					)
			);
	}

	@Override
	public void clear() {
		cache.clear();
	}
	@Override
	public void expire(Key key) {
		cache.remove(key);
	}

	private class Entry<V> {
		private final V value;
		private final long expires;

		public Entry(V value, long expires) {
			this.value = value;
			this.expires = expires;
		}

		public V getValue() {
			return value;
		}
		public long getExpires() {
			return expires;
		}
		public boolean isExpired() {
			return getExpires() < System.currentTimeMillis();
		}
	}
}
