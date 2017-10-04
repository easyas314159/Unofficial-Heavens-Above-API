package com.uhaapi.server;

import java.util.TimeZone;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

public class Configurator implements ServletContextListener {
	private final Logger log = Logger.getLogger(getClass());

	public static final String MEMCAHED = "memcached";
	public static final String ELEVATION_SERVICE = "elevation_service";

	@Override
	public void contextInitialized(ServletContextEvent event) {
		try {
			TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		}
		catch(Throwable t) {
			log.error("Uh Oh!", t);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent eventt) {
	}
}
