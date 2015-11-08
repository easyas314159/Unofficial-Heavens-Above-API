package com.uhaapi.server;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

import org.apache.log4j.Logger;

import com.google.inject.servlet.GuiceFilter;

public class IntermediateGuiceFilter extends GuiceFilter {
	private final Logger log = Logger.getLogger(getClass());

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		try {
			super.init(filterConfig);
		}
		catch(ServletException ex) {
			log.error(ex.getMessage(), ex);
			throw ex;
		}
		catch(Throwable t) {
			log.fatal(t.getMessage(), t);
			throw new ServletException(t);
		}
	}

	
}
