package com.uhaapi.server.geo;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.crypto.Mac;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

public abstract class MapsService {
	protected static final ExecutorService threadPool = Executors.newCachedThreadPool();

	public static final String SIGNING_ALGORITHM = "HmacSHA1";

	private HttpClient httpClient;
	private Logger log = Logger.getLogger(getClass());

	private final MapsCredentials credentials;

	private boolean sensor = false;

	private ThreadLocal<Mac> threadMac = new ThreadLocal<Mac>();

	protected MapsService(MapsCredentials credentials) {
		this(null, credentials);
	}
	protected MapsService(HttpClient httpClient, MapsCredentials credentials) {
		this.credentials = credentials;

		this.httpClient = httpClient == null ? new DefaultHttpClient() : httpClient;
	}

	public void setSensor(boolean sensor) {
		this.sensor = sensor;
	}
	public boolean hasSensor() {
		return sensor;
	}

	protected String makeRequest(String path, List<NameValuePair> queryString) {
		List<NameValuePair> params = new Vector<NameValuePair>(queryString);

		params.add(new BasicNameValuePair("sensor", Boolean.toString(sensor)));
		if(credentials != null && credentials.getClientKey() != null) {
			params.add(new BasicNameValuePair("client", credentials.getClientId()));
			params.add(
					new BasicNameValuePair(
							"signature",
							generateSignature(path, params)
						)
				);
		}

		String result = null;
		try {
			URI uri = generateURI(path, params);
			if(result == null) {
				HttpGet get = new HttpGet(uri);
				HttpResponse rsp = httpClient.execute(get);
				HttpEntity entity = rsp.getEntity();

				result = IOUtils.toString(entity.getContent());
			}
		}
		catch(Exception ex) {
			log.warn("Maps API Service request failed", ex);
		}
		return result;
	}

	protected URI generateURI(String path, List<NameValuePair> params) throws URISyntaxException {
		return URIUtils.createURI(
				"http",
				"maps.googleapis.com",
				-1,
				path,
				URLEncodedUtils.format(params, "UTF-8"),
				null
			);
	}

	private String generateSignature(String path, List<NameValuePair> params) {
		String signature = null;
		try {
			signature = generateSignature(generateURI(path, params));
		}
		catch(Exception ex) {
			log.warn("Failed to generate signature", ex);
		}
		return signature;
	}

	private String generateSignature(URI uri) throws NoSuchAlgorithmException, InvalidKeyException {
		return Base64.encodeBase64URLSafeString(getSigningMac().doFinal((uri.getPath() + "?" + uri.getQuery()).getBytes()));
	}

	private Mac getSigningMac() throws NoSuchAlgorithmException, InvalidKeyException {
		Mac mac = threadMac.get();
		if(mac == null) {
			threadMac.set(mac = Mac.getInstance(SIGNING_ALGORITHM));
			mac.init(credentials.getClientKey());
		}
		return mac;
	}
}
