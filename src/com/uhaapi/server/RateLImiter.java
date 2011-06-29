package com.uhaapi.server;

import java.io.IOException;
import java.net.InetAddress;
import java.security.SecureRandom;
import java.util.Random;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import net.spy.memcached.MemcachedClient;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.gson.Gson;
import com.uhaapi.server.api.StatusCodes;
import com.uhaapi.server.api.UhaapiResponse;
import com.uhappi.server.util.GsonUtils;

public class RateLImiter implements Filter {
	private long rateLimit = 1000;
	private String namespace = null;
	private MemcachedClient memcached = null;

	public void init(FilterConfig config) throws ServletException {
		ServletContext context = config.getServletContext();

		byte [] seed = new byte[32];
		Random r = new SecureRandom();
		r.nextBytes(seed);
		namespace = DigestUtils.md5Hex(seed);

		memcached = (MemcachedClient)context.getAttribute(Configurator.MEMCAHED);
	}
	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		boolean allow = true;
		if(memcached != null) {
			InetAddress ip = InetAddress.getByName(request.getRemoteAddr());
			String key = namespace + DigestUtils.md5Hex(ip.getAddress());

			long rate = memcached.incr(key, 1);
			if(rate < 0) {
				rate = memcached.incr(key, 1, 0, 3600);
			}
			else if(rate > rateLimit) {
				allow = false;
			}
		}

		if(allow) {
			chain.doFilter(request, response);
		}
		else {
			Gson gson = GsonUtils.newGson();
			gson.toJson(new UhaapiResponse(StatusCodes.OVER_QUERY_LIMIT), response.getWriter());
		}
	}
}
