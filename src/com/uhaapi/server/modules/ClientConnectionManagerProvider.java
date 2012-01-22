package com.uhaapi.server.modules;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;

import com.google.inject.Provider;

public class ClientConnectionManagerProvider implements Provider<ClientConnectionManager> {
	@Override
	public ClientConnectionManager get() {
		return new ThreadSafeClientConnManager();
	}
}
