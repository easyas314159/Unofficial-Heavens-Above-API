package com.uhaapi.server.geo;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Vector;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
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

import com.uhaapi.server.cache.Cache;

public abstract class MapsService {
	private static final String SIGNING_ALGORITHM = "HmacSHA1";

	private HttpClient httpClient;
	private Logger log = Logger.getLogger(getClass());

	private String apiKey = null;

	private String clientId = null;
	private SecretKeySpec clientKey = null;

	private boolean sensor = false;

	private Cache<String, String> cache = null;

	private ThreadLocal<Mac> threadMac = new ThreadLocal<Mac>();

	private MapsService(Cache<String, String> cache) {
		this.cache = cache;
		this.httpClient = new DefaultHttpClient();
	}

	protected MapsService(Cache<String, String> cache, String apiKey) {
		this(cache);

		this.apiKey = apiKey;
	}
	protected MapsService(Cache<String, String> cache, String clientId, String clientKey) {
		this(cache);

		this.clientId = clientId;
		this.clientKey = new SecretKeySpec(Base64.decodeBase64(clientKey), SIGNING_ALGORITHM);
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
		if(clientId == null) {
			params.add(new BasicNameValuePair("key", apiKey));
		}
		else {
			params.add(new BasicNameValuePair("client", clientId));
			params.add(
					new BasicNameValuePair(
							"signature",
							generateSignature(path, params)
						)
				);
		}

		String result = null;
		try {
			String key = null;
			URI uri = generateURI(path, params);
			if(cache != null) {
				key = DigestUtils.md5Hex(uri.getPath() + "?" + uri.getQuery());
			}
			if(result == null) {
				HttpGet get = new HttpGet(uri);
				HttpResponse rsp = httpClient.execute(get);
				HttpEntity entity = rsp.getEntity();

				result = IOUtils.toString(entity.getContent());

				if(cache != null) {
					cache.put(key, result, 2592000);
				}
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
			mac.init(clientKey);
		}
		return mac;
	}
}
