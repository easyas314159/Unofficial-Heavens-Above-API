package net.spy.memcached;

import java.net.SocketAddress;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import net.spy.memcached.CASResponse;
import net.spy.memcached.CASValue;
import net.spy.memcached.ConnectionObserver;
import net.spy.memcached.MemcachedClientIF;
import net.spy.memcached.NodeLocator;
import net.spy.memcached.OperationTimeoutException;
import net.spy.memcached.internal.BulkFuture;
import net.spy.memcached.transcoders.Transcoder;

import org.apache.commons.lang.NotImplementedException;

public class KeyedMemcachedClient implements MemcachedClientIF {
	private final String key;
	private final MemcachedClientIF mc;

	public KeyedMemcachedClient(MemcachedClientIF mc, String key) {
		this.mc = mc;
		this.key = key;
	}

	@Override
	public Future<Boolean> add(String key, int exp, Object o) {
		return mc.add(this.key + key, exp, o);
	}

	@Override
	public <T> Future<Boolean> add(String key, int exp, T o, Transcoder<T> t) {
		return mc.add(this.key + key, exp, o, t);
	}

	@Override
	public boolean addObserver(ConnectionObserver o) {
		return mc.addObserver(o);
	}

	@Override
	public Future<Boolean> append(long cas, String key, Object o) {
		return mc.append(cas, this.key + key, o);
	}

	@Override
	public <T> Future<Boolean> append(long cas, String key, T o, Transcoder<T> t) {
		return mc.append(cas, this.key + key, o, t);
	}

	@Override
	public Future<CASResponse> asyncCAS(String key, long casId, Object o) {
		return mc.asyncCAS(this.key + key, casId, o);
	}

	@Override
	public <T> Future<CASResponse> asyncCAS(String key, long casId, T o, Transcoder<T> t) {
		return mc.asyncCAS(this.key + key, casId, o, t);
	}

	@Override
	public Future<Long> asyncDecr(String key, int by) {
		return mc.asyncDecr(this.key + key, by);
	}

	@Override
	public Future<Object> asyncGet(String key) {
		return mc.asyncGet(this.key + key);
	}

	@Override
	public <T> Future<T> asyncGet(String key, Transcoder<T> t) {
		return mc.asyncGet(this.key + key, t);
	}

	@Override
	public BulkFuture<Map<String, Object>> asyncGetBulk(Collection<String> keys) {
		throw new NotImplementedException();
	}

	@Override
	public BulkFuture<Map<String, Object>> asyncGetBulk(String... keys) {
		throw new NotImplementedException();
	}

	@Override
	public <T> BulkFuture<Map<String, T>> asyncGetBulk(Collection<String> keys, Iterator<Transcoder<T>> tcs) {
		throw new NotImplementedException();
	}

	@Override
	public <T> BulkFuture<Map<String, T>> asyncGetBulk(Collection<String> keys, Transcoder<T> tc) {
		throw new NotImplementedException();
	}

	@Override
	public <T> BulkFuture<Map<String, T>> asyncGetBulk(Transcoder<T> tc, String... keys) {
		throw new NotImplementedException();
	}

	@Override
	public Future<CASValue<Object>> asyncGets(String key) {
		return mc.asyncGets(this.key + key);
	}

	@Override
	public <T> Future<CASValue<T>> asyncGets(String key, Transcoder<T> tc) {
		return mc.asyncGets(this.key + key, tc);
	}

	@Override
	public Future<Long> asyncIncr(String key, int by) {
		return mc.asyncIncr(this.key + key, by);
	}

	@Override
	public CASResponse cas(String key, long casId, Object o) throws OperationTimeoutException {
		return mc.cas(this.key + key, casId, o);
	}

	@Override
	public <T> CASResponse cas(String key, long casId, T o, Transcoder<T> tc) throws OperationTimeoutException {
		return mc.cas(this.key + key, casId, o, tc);
	}

	@Override
	public long decr(String key, int by) throws OperationTimeoutException {
		return mc.decr(this.key + key, by);
	}

	@Override
	public long decr(String key, int by, long def) throws OperationTimeoutException {
		return mc.decr(this.key + key, by, def);
	}

	@Override
	public long decr(String key, int by, long def, int exp) throws OperationTimeoutException {
		return mc.decr(this.key + key, by, def, exp);
	}

	@Override
	public Future<Boolean> delete(String key) {
		return mc.delete(this.key + key);
	}

	@Override
	public Future<Boolean> flush() {
		return mc.flush();
	}

	@Override
	public Future<Boolean> flush(int delay) {
		return mc.flush(delay);
	}

	@Override
	public Object get(String key) throws OperationTimeoutException {
		return mc.get(this.key + key);
	}

	@Override
	public <T> T get(String key, Transcoder<T> tc) throws OperationTimeoutException {
		return mc.get(this.key + key, tc);
	}

	@Override
	public Collection<SocketAddress> getAvailableServers() {
		return mc.getAvailableServers();
	}

	@Override
	public Map<String, Object> getBulk(Collection<String> arg0) throws OperationTimeoutException {
		throw new NotImplementedException();
	}

	@Override
	public Map<String, Object> getBulk(String... arg0) throws OperationTimeoutException {
		throw new NotImplementedException();
	}

	@Override
	public <T> Map<String, T> getBulk(Collection<String> arg0, Transcoder<T> arg1) throws OperationTimeoutException {
		throw new NotImplementedException();
	}

	@Override
	public <T> Map<String, T> getBulk(Transcoder<T> arg0, String... arg1) throws OperationTimeoutException {
		throw new NotImplementedException();
	}

	@Override
	public NodeLocator getNodeLocator() {
		return mc.getNodeLocator();
	}

	@Override
	public Map<SocketAddress, Map<String, String>> getStats() {
		return mc.getStats();
	}

	@Override
	public Map<SocketAddress, Map<String, String>> getStats(String prefix) {
		return mc.getStats(prefix);
	}

	@Override
	public Transcoder<Object> getTranscoder() {
		return mc.getTranscoder();
	}

	@Override
	public Collection<SocketAddress> getUnavailableServers() {
		return mc.getUnavailableServers();
	}

	@Override
	public Map<SocketAddress, String> getVersions() {
		return mc.getVersions();
	}

	@Override
	public CASValue<Object> gets(String key) throws OperationTimeoutException {
		return mc.gets(this.key + key);
	}

	@Override
	public <T> CASValue<T> gets(String key, Transcoder<T> tc) throws OperationTimeoutException {
		return mc.gets(this.key + key, tc);
	}

	@Override
	public long incr(String key, int by) throws OperationTimeoutException {
		return mc.incr(this.key + key, by);
	}

	@Override
	public long incr(String key, int by, long def) throws OperationTimeoutException {
		return mc.incr(this.key + key, by, def);
	}

	@Override
	public long incr(String key, int by, long def, int exp) throws OperationTimeoutException {
		return mc.incr(this.key + key, by, def, exp);
	}

	@Override
	public Set<String> listSaslMechanisms() {
		return mc.listSaslMechanisms();
	}

	@Override
	public Future<Boolean> prepend(long cas, String key, Object o) {
		return mc.prepend(cas, this.key + key, o);
	}

	@Override
	public <T> Future<Boolean> prepend(long cas, String key, T o, Transcoder<T> tc) {
		return mc.prepend(cas, this.key + key, o, tc);
	}

	@Override
	public boolean removeObserver(ConnectionObserver co) {
		return mc.removeObserver(co);
	}

	@Override
	public Future<Boolean> replace(String key, int exp, Object o) {
		return mc.replace(this.key + key, exp, o);
	}

	@Override
	public <T> Future<Boolean> replace(String key, int exp, T o, Transcoder<T> tc) {
		return mc.replace(this.key + key, exp, o, tc);
	}

	@Override
	public Future<Boolean> set(String key, int exp, Object o) {
		return mc.set(this.key + key, exp, o);
	}

	@Override
	public <T> Future<Boolean> set(String key, int exp, T o, Transcoder<T> tc) {
		return mc.set(this.key + key, exp, o, tc);
	}

	@Override
	public void shutdown() {
		mc.shutdown();
	}

	@Override
	public boolean shutdown(long timeout, TimeUnit unit) {
		return mc.shutdown(timeout, unit);
	}

	@Override
	public boolean waitForQueues(long timeout, TimeUnit unit) {
		return mc.waitForQueues(timeout, unit);
	}

}
