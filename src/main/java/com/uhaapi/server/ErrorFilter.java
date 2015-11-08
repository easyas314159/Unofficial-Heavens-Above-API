package com.uhaapi.server;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.log4j.Logger;

import com.google.inject.Singleton;

@Singleton
public class ErrorFilter implements Filter {
	private final Logger log = Logger.getLogger(getClass());

	@Override
	public void init(FilterConfig config) throws ServletException {
	}
	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse rsp,
			FilterChain chain) throws IOException, ServletException {
		
		try {
			chain.doFilter(req, rsp);
		}
		catch(IOException ex) {
			throw ex;
		}
		catch(ServletException ex) {
			throw ex;
		}
		catch(Throwable t) {
			log.error("Unhandled Exception", t);
			throw new ServletException(t);
		}
	}


}
