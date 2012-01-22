package com.uhaapi.server.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import javax.ws.rs.core.HttpHeaders;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.protocol.HttpContext;

public class GZIPInterceptor implements HttpRequestInterceptor, HttpResponseInterceptor {
	@Override
	public void process(HttpRequest req, HttpContext context) throws HttpException, IOException {
		// Are we already requesting gzip compression
		for(Header header : req.getHeaders(HttpHeaders.ACCEPT_ENCODING)) {
			for(HeaderElement codec : header.getElements()) {
				if(codec.getName().equalsIgnoreCase("gzip")) {
					return;
				}
			}
		}
		req.addHeader(HttpHeaders.ACCEPT_ENCODING, "gzip");
	}
	@Override
	public void process(HttpResponse rsp, HttpContext context) throws HttpException, IOException {
		HttpEntity entity = rsp.getEntity();
		if(entity == null) {
			return;
		}

		Header contentEncoding = entity.getContentEncoding();
		if(contentEncoding == null) {
			return;
		}
		for(HeaderElement codec : contentEncoding.getElements()) {
			if(codec.getName().equalsIgnoreCase("gzip")) {
				rsp.setEntity(new GZIPEntity(entity));
				return;
			}
		}
		
	}

	private class GZIPEntity extends HttpEntityWrapper {
		public GZIPEntity(HttpEntity wrapped) {
			super(wrapped);
		}

		@Override
		public InputStream getContent() throws IOException {
			return new GZIPInputStream(super.getContent());
		}
		@Override
		public long getContentLength() {
			return -1;
		}
	}
}
