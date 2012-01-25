package com.uhaapi.server.http;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.htmlparser.Parser;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.lexer.Page;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import com.uhaapi.server.util.WhitespaceFilter;

public final class HttpScraper {
	public static NodeList scrape(HttpClient httpClient, String host, int port, String path, List<NameValuePair> params) throws URISyntaxException, ClientProtocolException, IOException, ParserException {
		if(params == null) {
			params = Collections.EMPTY_LIST;
		}
		return scrape(httpClient, URIUtils.createURI("http", host, port, path, URLEncodedUtils.format(params, "UTF-8"), null));
	}

	public static NodeList scrape(HttpClient httpClient, URI uri) throws ClientProtocolException, IOException, ParserException {
		HttpGet method = new HttpGet(uri);
		HttpResponse rsp = httpClient.execute(method);

		int statusCode = rsp.getStatusLine().getStatusCode();
		if(!(200 <= statusCode && statusCode < 300)) {
			return null;
		}

		HttpEntity entity = rsp.getEntity();

		String charset = "UTF-8";
		Header contentType = entity.getContentType();
		if(contentType != null) {
			for(HeaderElement el : contentType.getElements()) {
				NameValuePair key = el.getParameterByName("charset");
				if(key != null) {
					charset = key.getValue();
					break;
				}
			}
		}

		Parser parser = new Parser();
		Lexer lexer = new Lexer(new Page(entity.getContent(), charset));
		parser.setLexer(lexer);

		NodeList nodes = parser.parse(null);
		nodes.keepAllNodesThatMatch(new WhitespaceFilter(), true);
		return nodes;
	}
}
