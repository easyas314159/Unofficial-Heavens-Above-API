package net.spy.memcached;

import java.net.SocketAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import net.spy.memcached.internal.BulkFuture;
import net.spy.memcached.transcoders.Transcoder;

import org.apache.commons.lang.NotImplementedException;

import com.uhaapi.server.util.CompletedFuture;

public class NullMemcachedClient implements MemcachedClientIF {
	private static final Future<Boolean> futureTrue = new CompletedFuture<Boolean>(Boolean.TRUE);
	private static final Future<CASResponse> futureCASResponse = new CompletedFuture<CASResponse>(CASResponse.NOT_FOUND);

	@Override
	public Future<Boolean> add(String arg0, int arg1, Object arg2) {
		return futureTrue;
	}

	@Override
	public <T> Future<Boolean> add(String arg0, int arg1, T arg2, Transcoder<T> arg3) {
		return futureTrue;
	}

	@Override
	public boolean addObserver(ConnectionObserver arg0) {
		return true;
	}

	@Override
	public Future<Boolean> append(long arg0, String arg1, Object arg2) {
		return futureTrue;
	}

	@Override
	public <T> Future<Boolean> append(long arg0, String arg1, T arg2, Transcoder<T> arg3) {
		return futureTrue;
	}

	@Override
	public Future<CASResponse> asyncCAS(String arg0, long arg1, Object arg2) {
		return futureCASResponse;
	}

	@Override
	public <T> Future<CASResponse> asyncCAS(String arg0, long arg1, T arg2,	Transcoder<T> arg3) {
		return futureCASResponse;
	}

	@Override
	public Future<Long> asyncDecr(String arg0, int arg1) {
		return new CompletedFuture<Long>(-1L);
	}

	@Override
	public Future<Object> asyncGet(String arg0) {
		return new CompletedFuture<Object>(null);
	}

	@Override
	public <T> Future<T> asyncGet(String arg0, Transcoder<T> arg1) {
		return new CompletedFuture<T>(null);
	}

	@Override
	public BulkFuture<Map<String, Object>> asyncGetBulk(Collection<String> arg0) {
		throw new NotImplementedException();
	}

	@Override
	public BulkFuture<Map<String, Object>> asyncGetBulk(String... arg0) {
		throw new NotImplementedException();
	}

	@Override
	public <T> BulkFuture<Map<String, T>> asyncGetBulk(Collection<String> arg0, Iterator<Transcoder<T>> arg1) {
		throw new NotImplementedException();
	}

	@Override
	public <T> BulkFuture<Map<String, T>> asyncGetBulk(Collection<String> arg0, Transcoder<T> arg1) {
		throw new NotImplementedException();
	}

	@Override
	public <T> BulkFuture<Map<String, T>> asyncGetBulk(Transcoder<T> arg0, String... arg1) {
		throw new NotImplementedException();
	}

	@Override
	public Future<CASValue<Object>> asyncGets(String arg0) {
		throw new NotImplementedException();
	}

	@Override
	public <T> Future<CASValue<T>> asyncGets(String arg0, Transcoder<T> arg1) {
		throw new NotImplementedException();
	}

	@Override
	public Future<Long> asyncIncr(String arg0, int arg1) {
		return new CompletedFuture<Long>(-1L);
	}

	@Override
	public CASResponse cas(String arg0, long arg1, Object arg2) throws OperationTimeoutException {
		return CASResponse.NOT_FOUND;
	}

	@Override
	public <T> CASResponse cas(String arg0, long arg1, T arg2,
			Transcoder<T> arg3) throws OperationTimeoutException {
		return CASResponse.NOT_FOUND;
	}

	@Override
	public long decr(String arg0, int arg1) throws OperationTimeoutException {
		return -1;
	}

	@Override
	public long decr(String arg0, int arg1, long arg2) throws OperationTimeoutException {
		return -1;
	}

	@Override
	public long decr(String arg0, int arg1, long arg2, int arg3) throws OperationTimeoutException {
		return -1;
	}

	@Override
	public Future<Boolean> delete(String arg0) {
		return futureTrue;
	}

	@Override
	public Future<Boolean> flush() {
		return futureTrue;
	}

	@Override
	public Future<Boolean> flush(int arg0) {
		return futureTrue;
	}

	@Override
	public Object get(String arg0) throws OperationTimeoutException {
		return null;
	}

	@Override
	public <T> T get(String arg0, Transcoder<T> arg1) throws OperationTimeoutException {
		return null;
	}

	@Override
	public Collection<SocketAddress> getAvailableServers() {
		return Collections.EMPTY_SET;
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
		return null;
	}

	@Override
	public Map<SocketAddress, Map<String, String>> getStats() {
		return Collections.EMPTY_MAP;
	}

	@Override
	public Map<SocketAddress, Map<String, String>> getStats(String arg0) {
		return Collections.EMPTY_MAP;
	}

	@Override
	public Transcoder<Object> getTranscoder() {
		return null;
	}

	@Override
	public Collection<SocketAddress> getUnavailableServers() {
		return Collections.EMPTY_SET;
	}

	@Override
	public Map<SocketAddress, String> getVersions() {
		return Collections.EMPTY_MAP;
	}

	@Override
	public CASValue<Object> gets(String arg0) throws OperationTimeoutException {
		throw new NotImplementedException();
	}

	@Override
	public <T> CASValue<T> gets(String arg0, Transcoder<T> arg1) throws OperationTimeoutException {
		throw new NotImplementedException();
	}

	@Override
	public long incr(String arg0, int arg1) throws OperationTimeoutException {
		return -1;
	}

	@Override
	public long incr(String arg0, int arg1, long arg2) throws OperationTimeoutException {
		return -1;
	}

	@Override
	public long incr(String arg0, int arg1, long arg2, int arg3) throws OperationTimeoutException {
		return -1;
	}

	@Override
	public Set<String> listSaslMechanisms() {
		return Collections.EMPTY_SET;
	}

	@Override
	public Future<Boolean> prepend(long arg0, String arg1, Object arg2) {
		return futureTrue;
	}

	@Override
	public <T> Future<Boolean> prepend(long arg0, String arg1, T arg2, Transcoder<T> arg3) {
		return futureTrue;
	}

	@Override
	public boolean removeObserver(ConnectionObserver arg0) {
		return true;
	}

	@Override
	public Future<Boolean> replace(String arg0, int arg1, Object arg2) {
		return futureTrue;
	}

	@Override
	public <T> Future<Boolean> replace(String arg0, int arg1, T arg2, Transcoder<T> arg3) {
		return futureTrue;
	}

	@Override
	public Future<Boolean> set(String arg0, int arg1, Object arg2) {
		return futureTrue;
	}

	@Override
	public <T> Future<Boolean> set(String arg0, int arg1, T arg2, Transcoder<T> arg3) {
		return futureTrue;
	}

	@Override
	public void shutdown() {
	}

	@Override
	public boolean shutdown(long arg0, TimeUnit arg1) {
		return true;
	}

	@Override
	public boolean waitForQueues(long arg0, TimeUnit arg1) {
		return true;
	}
}
