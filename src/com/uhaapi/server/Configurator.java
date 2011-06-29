package com.uhaapi.server;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.ConnectionFactory;
import net.spy.memcached.DefaultConnectionFactory;
import net.spy.memcached.MemcachedClient;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.uhaapi.server.cache.Cache;
import com.uhaapi.server.cache.DistributedCache;
import com.uhaapi.server.cache.SimpleCache;
import com.uhaapi.server.geo.ElevationService;
import com.uhappi.server.util.DateAdapter;
import com.uhappi.server.util.GsonUtils;

public class Configurator implements ServletContextListener {
	private final Logger log = Logger.getLogger(getClass());

	public static final String MEMCAHED = "memcached";
	public static final String ELEVATION_SERVICE = "elevation_service";

	public void contextInitialized(ServletContextEvent event) {
		try {
			ServletContext context = event.getServletContext();

			TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
			
			GsonUtils.getGsonBuilder()
				.registerTypeAdapter(Date.class, new DateAdapter())
				;

			initMemcached(context);
			initElevationService(context);
		}
		catch(Throwable t) {
			log.error("Uh Oh!", t);
		}
	}

	public void contextDestroyed(ServletContextEvent eventt) {
	}

	private MemcachedClient initMemcached(ServletContext context) {
		ClassLoader loader = ConnectionFactory.class.getClassLoader();
		String connectionFactoryClass = StringUtils
				.trimToNull(context
						.getInitParameter(ServletInitOptions.MEMCACHED_CONNECTION_FACTORY));

		ConnectionFactory connectionFactory = null;
		if(connectionFactoryClass != null) {
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

		MemcachedClient memcached = null;
		String cluster = StringUtils.trimToNull(context
				.getInitParameter(ServletInitOptions.MEMCACHED_CLUSTER));
		if(cluster == null) {
			log.warn("No memcached cluster specified; memcached will be disabled");
		} else {
			try {
				List<InetSocketAddress> addresses = AddrUtil
						.getAddresses(cluster);
				memcached = new MemcachedClient(connectionFactory, addresses);

				// memcached.addObserver(new MemcachedMonitor());
				context.setAttribute(Configurator.MEMCAHED, memcached);
			} catch(Throwable t) {
				log.error("Unable to create memcached client", t);
			}
		}
		return memcached;
	}

	private ElevationService initElevationService(ServletContext context) {
		Cache<String, String> cache = null;
		MemcachedClient memcached = (MemcachedClient)context.getAttribute(MEMCAHED);
		if(memcached == null) {
			cache = new SimpleCache<String, String>();
		}
		else {
			cache = new DistributedCache<String>(
					memcached,
					DigestUtils.md5Hex("elevation")
				);
		}

		ElevationService service = null;
		String apiKey = context.getInitParameter(ServletInitOptions.MAPS_API_KEY);
		String clientId = context.getInitParameter(ServletInitOptions.MAPS_CLIENT_ID);
		String clientKey = context.getInitParameter(ServletInitOptions.MAPS_CLIENT_KEY);

		if(clientId == null || clientKey == null) {
			service = new ElevationService(cache, apiKey);
		}
		else {
			service = new ElevationService(cache, clientId, clientKey);
		}

		context.setAttribute(ELEVATION_SERVICE, service);
		return service;
	}
}
