package com.heavens_above;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Text;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.CssSelectorNodeFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.HasParentFilter;
import org.htmlparser.filters.NotFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.visitors.NodeVisitor;

import com.uhaapi.server.api.entity.Compass;
import com.uhaapi.server.api.entity.Satellite;
import com.uhaapi.server.api.entity.SatellitePass;
import com.uhaapi.server.api.entity.SatellitePassWaypoint;
import com.uhaapi.server.api.entity.SatellitePasses;
import com.uhaapi.server.geo.LatLng;
import com.uhaapi.server.http.HttpScraper;
import com.uhaapi.server.util.ParamUtils;

public class HeavensAbove {
	private final Logger log = Logger.getLogger(getClass());

	private final HttpClient httpClient;

	public HeavensAbove() {
		this(new DefaultHttpClient());
	}

	public HeavensAbove(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

	public Satellite getSatellite(int id) throws IOException {

		List<NameValuePair> params = new Vector<NameValuePair>();
		params.add(new BasicNameValuePair("SatID", Integer.toString(id)));

		NodeList page = getPage("satinfo.aspx", params);
		if(page == null) {
			return null;
		}

		Satellite satellite = new Satellite();
		satellite.setId(id);

		extractSatelliteDetails(page, satellite);

		return satellite;
	}

	private void extractSatelliteDetails(NodeList page, Satellite satellite) {
		NodeList working = null;

		working = page.extractAllNodesThatMatch(new HasAttributeFilter("id", "ctl00_lblTitle"));
		String name = working.asString();
		int idx = name.lastIndexOf('-');
		if(idx > 0) {
			satellite.setName(StringUtils.trimToNull(name.substring(0, idx)));
		}
		
		working = page.extractAllNodesThatMatch(new HasAttributeFilter("id", "ctl00_ContentPlaceHolder1_lblIntDesig"));
		satellite.setIdc(StringUtils.trimToNull(working.asString()));

		working = page.extractAllNodesThatMatch(new HasAttributeFilter("id", "ctl00_ContentPlaceHolder1_lblLaunchDate"));
	}

	public SatellitePasses getVisiblePasses(int id, double lat, double lng,
			double alt) throws IOException {
		List<NameValuePair> params = new Vector<NameValuePair>();
		params.add(new BasicNameValuePair("satid", Integer.toString(id)));
		params.add(new BasicNameValuePair("lat", Double.toString(lat)));
		params.add(new BasicNameValuePair("lng", Double.toString(lng)));
		params.add(new BasicNameValuePair("alt", Double.toString(alt)));
		params.add(new BasicNameValuePair("tz", "UCT"));

		NodeList page = getPage("PassSummary.aspx", params);
		if(page == null) {
			return null;
		}

		SatellitePasses response = new SatellitePasses();

		response.setId(id);
		response.setAltitude(alt);
		response.setLocation(new LatLng(lat, lng));

		extractTimeDetails(page, response);
		extractAllPasses(page, response);

		return response;
	}

	private void extractTimeDetails(NodeList page,
			SatellitePasses passes) {
		DateFormat searchRangeFormat = new SimpleDateFormat(
				"HH:mm EEEE, d MMMM, yyyy");
		NodeList working = null;

		// The Search Period
		Date searchStart = null;
		working = page.extractAllNodesThatMatch(new HasParentFilter(
				new HasAttributeFilter("id",
						"ctl00_ContentPlaceHolder1_lblSearchStart")), true);
		try {
			searchStart = searchRangeFormat.parse(working.asString());
			passes.setFrom(searchStart);
		} catch(ParseException ex) {
			log.warn("Failed to parse start time", ex);
		}

		Date searchEnd = null;
		working = page.extractAllNodesThatMatch(new HasParentFilter(
				new HasAttributeFilter("id",
						"ctl00_ContentPlaceHolder1_lblSearchEnd")), true);
		try {
			searchEnd = searchRangeFormat.parse(working.asString());
			passes.setTo(searchEnd);
		} catch(ParseException ex) {
			log.warn("Failed to parse end time", ex);
		}
	}

	private void extractAllPasses(NodeList page, SatellitePasses response) {
		NodeFilter filter = new AndFilter(new HasParentFilter(
				new HasAttributeFilter("id",
						"ctl00_ContentPlaceHolder1_tblPasses"), false),
				new NotFilter(new CssSelectorNodeFilter(".tablehead")));

		Calendar prevPassTime = Calendar.getInstance();
		prevPassTime.setTime(response.getFrom());

		List<SatellitePass> results = new Vector<SatellitePass>();
		NodeList allPasses = page.extractAllNodesThatMatch(filter, true);
		for(Node pass : allPasses.toNodeArray()) {
			results.add(extractNextPass(pass.getChildren(), prevPassTime));
		}
		response.setResults(results);
	}

	private SatellitePass extractNextPass(NodeList pass, Calendar prevPassTime) {
		Calendar nextPassTime = Calendar.getInstance();
		DateFormat format = new SimpleDateFormat("d MMM yyyy HH:mm:ss");
		format.setCalendar(nextPassTime);

		final List<String> details = new ArrayList<String>();
		NodeVisitor visitor = new NodeVisitor(true, true) {
			@Override
			public void visitStringNode(Text string) {
				details.add(StringUtils.trimToEmpty(string.getText()));
			}
		};
		SatellitePass result = null;
		try {
			pass.visitAllNodesWith(visitor);

			result = new SatellitePass();
			result.setMagnitude(ParamUtils.asDouble(details.get(1)));

			Date start = format.parse(String.format("%s %04d %s",
					details.get(0), prevPassTime.get(Calendar.YEAR),
					details.get(2)));
			if(nextPassTime.getTimeInMillis() < prevPassTime.getTimeInMillis()
					&& prevPassTime.get(Calendar.MONTH) != prevPassTime
							.get(Calendar.MONTH)) {
				nextPassTime.add(Calendar.YEAR, 1);
				start = nextPassTime.getTime();
			}
			prevPassTime.setTimeInMillis(nextPassTime.getTimeInMillis());
			result.setStart(new SatellitePassWaypoint(start, ParamUtils
					.asDouble(details.get(3)), convertCompassPoint(details
					.get(4))));

			Date max = format.parse(String.format("%s %04d %s", details.get(0),
					prevPassTime.get(Calendar.YEAR), details.get(5)));
			if(max.before(start)) {
				nextPassTime.add(Calendar.DAY_OF_MONTH, 1);
				max = nextPassTime.getTime();
			}
			prevPassTime.setTimeInMillis(nextPassTime.getTimeInMillis());
			result.setMax(new SatellitePassWaypoint(max, ParamUtils.asDouble(details
					.get(6)), convertCompassPoint(details.get(7))));

			Date end = format.parse(String.format("%s %04d %s", details.get(0),
					prevPassTime.get(Calendar.YEAR), details.get(8)));
			if(end.before(start)) {
				nextPassTime.add(Calendar.DAY_OF_MONTH, 1);
				end = nextPassTime.getTime();
			}
			prevPassTime.setTimeInMillis(nextPassTime.getTimeInMillis());
			result.setEnd(new SatellitePassWaypoint(end, ParamUtils.asDouble(details
					.get(9)), convertCompassPoint(details.get(10))));
		} catch(Exception ex) {
			log.warn("Failed to parse satellite pass", ex);
		}

		return result;
	}

	private Double convertCompassPoint(String name) {
		return Compass.valueOf(name).getAzimuth();
	}

	private NodeList getPage(String path, List<NameValuePair> params) {
		try {
			return HttpScraper.scrape(httpClient, "heavens-above.com", -1, path, params);
		} catch(Exception ex) {
			return null;
		}
	}
}
