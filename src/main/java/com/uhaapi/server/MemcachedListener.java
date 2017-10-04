package com.uhaapi.server;

import java.net.InetSocketAddress;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.ConnectionFactory;
import net.spy.memcached.DefaultConnectionFactory;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.MemcachedClientIF;
import net.spy.memcached.NullMemcachedClient;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class MemcachedListener implements ServletContextListener {
	public static final String MEMCACHED = MemcachedClientIF.class.getName();
	
	private final Logger log = Logger.getLogger(getClass());

	@Override
	public void contextInitialized(ServletContextEvent evt) {
ServletContext ctx = evt.getServletContext();
		
		String connectionFactoryClass = StringUtils.trimToNull(ctx.getInitParameter(ServletInitOptions.MEMCACHED_CONNECTION_FACTORY));

		ConnectionFactory connectionFactory = null;
		if(connectionFactoryClass != null) {
			ClassLoader loader = ConnectionFactory.class.getClassLoader();
			try {
				connectionFactory = loader.loadClass(connectionFactoryClass)
						.asSubclass(ConnectionFactory.class).newInstance();
			} catch(Throwable t) {
				log.warn("Failed to instantiate memcached connection factory", t);
			}
		}
		if(connectionFactory == null) {
			log.info("Using default memcached connection factory");
			connectionFactory = new DefaultConnectionFactory();
		}

		MemcachedClientIF memcached = null;
		String cluster = StringUtils.trimToNull(ctx
				.getInitParameter(ServletInitOptions.MEMCACHED_CLUSTER));
		if(cluster == null) {
			log.warn("No memcached cluster specified; memcached will be disabled");
			memcached = new NullMemcachedClient();
		} else {
			try {
				List<InetSocketAddress> addresses = AddrUtil.getAddresses(cluster);
				memcached = new MemcachedClient(connectionFactory, addresses);
			} catch(Throwable t) {
				log.error("Unable to create memcached client", t);
				memcached = new NullMemcachedClient();
			}
		}
		ctx.setAttribute(MEMCACHED, memcached);
	}
	@Override
	public void contextDestroyed(ServletContextEvent evt) {
		ServletContext ctx = evt.getServletContext();
		
		MemcachedClientIF memcached = (MemcachedClientIF)ctx.getAttribute(MEMCACHED);
		ctx.removeAttribute(MEMCACHED);

		memcached.shutdown();
	}
}
