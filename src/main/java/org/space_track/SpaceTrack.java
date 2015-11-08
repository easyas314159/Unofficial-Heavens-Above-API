package org.space_track;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class SpaceTrack {
	private final Logger log = Logger.getLogger(getClass());

	private final HttpClient httpClient;

	private final String username;
	private final String password;

	public SpaceTrack(String username, String password) {
		this(new DefaultHttpClient(), username, password);
	}
	public SpaceTrack(HttpClient httpClient, String username, String password) {
		this.httpClient = httpClient;

		this.username = username;
		this.password = password;
	}

	public boolean login() {
		if(username == null || username.isEmpty() || password == null || password.isEmpty()) {
			return false;
		}

		try {
			HttpPost post = new HttpPost("https://www.space-track.org/perl/login.pl");
			post.setEntity(new UrlEncodedFormEntity(new ArrayList<NameValuePair>(){{
				add(new BasicNameValuePair("username", username));
				add(new BasicNameValuePair("password", password));
				add(new BasicNameValuePair("_submitted", "1"));
			}}));

			HttpResponse response = httpClient.execute(post);
			EntityUtils.consume(response.getEntity());

			if(httpClient instanceof AbstractHttpClient) {
				List<Cookie> cookies = ((AbstractHttpClient)httpClient).getCookieStore().getCookies();
				for(Cookie cookie : cookies) {
					if(cookie.getName().equalsIgnoreCase("spacetrack_session")
							&& cookie.getDomain().equalsIgnoreCase("www.space-track.org")
							&& !(cookie.getValue() == null || cookie.getValue().isEmpty())) {
						return true;
					}
				}
			}
		}
		catch(Exception ex) {
			log.warn("", ex);
		}

		return false;
	}

	public Collection<TwoLineElement> getFullCatalog() {
		try {
			HttpGet fullCatalog = new HttpGet("https://www.space-track.org/perl/dl.pl?ID=2");
			HttpResponse response = httpClient.execute(fullCatalog);

			HttpEntity entity = response.getEntity();
			if(entity == null) {
				return null;
			}

			Header contentType = entity.getContentType();
			if(contentType == null) {
				return null;
			}

			for(HeaderElement type : contentType.getElements()) {
				if(type.getName().equalsIgnoreCase("application/x-gzip")) {
					return readElements3(new GZIPInputStream(entity.getContent()));
					
				}
			}
		}
		catch(Exception ex) {
			log.warn("", ex);
		}
		return null;
	}

	public void logout() {
		try {
			HttpGet logout = new HttpGet("https://www.space-track.org/perl/logout.pl");
			httpClient.execute(logout);
		}
		catch(Exception ex) {
			log.warn("", ex);
		}
	}
	
	private Collection<TwoLineElement> readElements2(InputStream input) {
		throw new NotImplementedException();
	}
	private Collection<TwoLineElement> readElements3(InputStream input) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		Collection<TwoLineElement> tles = new ArrayList<TwoLineElement>();

		while(true) {
			String title, line1, line2;
			try {
				title = reader.readLine();
				line1 = reader.readLine();
				line2 = reader.readLine();
			}
			catch(Throwable t) {
				break;
			}

			if(title == null || line1 == null || line2 == null) {
				break;
			}

			TwoLineElement tle = new TwoLineElement(title, line1, line2);
			tles.add(tle);
		}

		IOUtils.closeQuietly(reader);
		return tles;
	}
}
