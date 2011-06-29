package com.uhaapi.server;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.log4j.Logger;

/**
 * Servlet Filter implementation class ErrorTrap
 */
public class ErrorTrap implements Filter {
	private Logger log = Logger.getLogger(getClass());

	@Override
	public void init(FilterConfig config) throws ServletException {
	}
	@Override
	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		try {
			chain.doFilter(request, response);
		}
		catch(Throwable t) {
			log.error("Error handling request", t);
			if(t instanceof IOException) {
				throw (IOException)t;
			}
			if(t instanceof ServletException) {
				throw (ServletException)t;
			}
			throw new ServletException(t);
		}
	}
}
