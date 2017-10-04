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

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Text;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.HasParentFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.visitors.NodeVisitor;

import com.uhaapi.server.api.entity.Compass;
import com.uhaapi.server.api.entity.IridiumFlare;
import com.uhaapi.server.api.entity.IridiumFlares;
import com.uhaapi.server.api.entity.Satellite;
import com.uhaapi.server.api.entity.SatellitePass;
import com.uhaapi.server.api.entity.SatellitePassWaypoint;
import com.uhaapi.server.api.entity.SatellitePasses;
import com.uhaapi.server.error.HeavensAboveException;
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
		if (page == null) {
			return null;
		}

		Satellite satellite = new Satellite();
		satellite.setId(id);

		extractSatelliteDetails(page, satellite);

		return satellite;
	}

	private void extractSatelliteDetails(NodeList page, Satellite satellite) {
		NodeList working = null;

		working = page.extractAllNodesThatMatch(new HasAttributeFilter("id", "ctl00_lblTitle"), true);
		String name = working.asString();
		int idx = name.lastIndexOf('-');
		if (idx > 0) {
			satellite.setName(StringUtils.trimToNull(name.substring(0, idx)));
		}

		working = page.extractAllNodesThatMatch(new HasAttributeFilter("id", "ctl00_cph1_lblIntDesig"), true);
		satellite.setIdc(StringUtils.trimToNull(working.asString()));

		Date launched = null;
		DateFormat f = new SimpleDateFormat("HH:mm, MMMM dd, yyyy");

		working = page.extractAllNodesThatMatch(new HasAttributeFilter("id", "ctl00_cph1_lblLaunchDate"), true);
		try {
			String data = StringUtils.trimToNull(working.asString());
			launched = f.parse(data);
		} catch (ParseException e) {
		}
		satellite.setLaunched(launched);
	}

	public SatellitePasses getVisiblePasses(int id, double lat, double lng, double alt) throws HeavensAboveException {
		List<NameValuePair> params = new Vector<NameValuePair>();
		params.add(new BasicNameValuePair("satid", Integer.toString(id)));
		params.add(new BasicNameValuePair("lat", Double.toString(lat)));
		params.add(new BasicNameValuePair("lng", Double.toString(lng)));
		params.add(new BasicNameValuePair("alt", Double.toString(alt)));
		params.add(new BasicNameValuePair("tz", "UCT"));

		NodeList page = getPage("PassSummary.aspx", params);
		if (page == null) {
			return null;
		}

		SatellitePasses response = new SatellitePasses();

		response.setId(id);
		response.setAltitude(alt);
		response.setLocation(new LatLng(lat, lng));

		extractTimeDetails(page, response);
		if (response.getFrom() == null) {
			return null;
		}

		extractAllPasses(page, response);

		return response;
	}

	private void extractTimeDetails(NodeList page, SatellitePasses passes) {
		DateFormat searchRangeFormat = new SimpleDateFormat("d MMMM yyyy HH:mm");
		NodeList working = null;

		// The Search Period
		working = page.extractAllNodesThatMatch(
				new HasParentFilter(new HasAttributeFilter("id", "ctl00_cph1_lblSearchStart")), true);
		passes.setFrom(parseDate(searchRangeFormat, working.asString()));

		working = page.extractAllNodesThatMatch(
				new HasParentFilter(new HasAttributeFilter("id", "ctl00_cph1_lblSearchEnd")), true);
		passes.setTo(parseDate(searchRangeFormat, working.asString()));
	}

	private Date parseDate(DateFormat fmt, String val) {
		try {
			return fmt.parse(val);
		} catch (ParseException ex) {
			log.error("Failed to parse time", ex);
		}
		return null;
	}

	private void extractAllPasses(NodeList page, SatellitePasses response) {
		NodeFilter filter = new AndFilter(new TagNameFilter("tr"), new HasAttributeFilter("onclick"));

		Calendar prevPassTime = Calendar.getInstance();
		prevPassTime.setTime(response.getFrom());

		List<SatellitePass> results = new Vector<SatellitePass>();
		NodeList allPasses = page.extractAllNodesThatMatch(filter, true);
		for (Node pass : allPasses.toNodeArray()) {
			SatellitePass sp = extractNextPass(pass.getChildren(), prevPassTime);

			if (response.getTo() == null || response.getTo().before(sp.getEnd().getTime())) {
				response.setTo(sp.getEnd().getTime());
			}

			results.add(sp);
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

			Date start = format.parse(
					String.format("%s %04d %s", details.get(0), prevPassTime.get(Calendar.YEAR), details.get(2)));
			if (nextPassTime.getTimeInMillis() < prevPassTime.getTimeInMillis()
					&& prevPassTime.get(Calendar.MONTH) != prevPassTime.get(Calendar.MONTH)) {
				nextPassTime.add(Calendar.YEAR, 1);
				start = nextPassTime.getTime();
			}
			prevPassTime.setTimeInMillis(nextPassTime.getTimeInMillis());
			result.setStart(
					new SatellitePassWaypoint(start, ParamUtils.asDouble(details.get(3).replaceAll("[^0-9\\.]", "")),
							convertCompassPoint(details.get(4))));

			Date max = format.parse(
					String.format("%s %04d %s", details.get(0), prevPassTime.get(Calendar.YEAR), details.get(5)));
			if (max.before(start)) {
				nextPassTime.add(Calendar.DAY_OF_MONTH, 1);
				max = nextPassTime.getTime();
			}
			prevPassTime.setTimeInMillis(nextPassTime.getTimeInMillis());
			result.setMax(
					new SatellitePassWaypoint(max, ParamUtils.asDouble(details.get(6).replaceAll("[^0-9\\.]", "")),
							convertCompassPoint(details.get(7))));

			Date end = format.parse(
					String.format("%s %04d %s", details.get(0), prevPassTime.get(Calendar.YEAR), details.get(8)));
			if (end.before(start)) {
				nextPassTime.add(Calendar.DAY_OF_MONTH, 1);
				end = nextPassTime.getTime();
			}
			prevPassTime.setTimeInMillis(nextPassTime.getTimeInMillis());
			result.setEnd(
					new SatellitePassWaypoint(end, ParamUtils.asDouble(details.get(9).replaceAll("[^0-9\\.]", "")),
							convertCompassPoint(details.get(10))));
		} catch (Exception ex) {
			log.warn("Failed to parse satellite pass", ex);
		}

		return result;
	}

	private Double convertCompassPoint(String name) {
		return Compass.valueOf(name).getAzimuth();
	}

	public IridiumFlares getIridiumFlares(double lat, double lng, double alt) throws HeavensAboveException {
		List<NameValuePair> params = new Vector<NameValuePair>();
		params.add(new BasicNameValuePair("lat", Double.toString(lat)));
		params.add(new BasicNameValuePair("lng", Double.toString(lng)));
		params.add(new BasicNameValuePair("alt", Double.toString(alt)));
		params.add(new BasicNameValuePair("tz", "UCT"));

		NodeList page = getPage("IridiumFlares.aspx", params);
		if (page == null) {
			return null;
		}

		IridiumFlares response = new IridiumFlares();
		response.setAltitude(alt);
		response.setLocation(new LatLng(lat, lng));

		DateFormat parser = new SimpleDateFormat("HH:mm EEEE, d MMMM, yyyy");

		Node parentNode1 = page
				.extractAllNodesThatMatch(new HasAttributeFilter("id", "ctl00_cph1_lblSearchStart"), true).elementAt(0);
		Node fromNode = parentNode1.getFirstChild();
		Node parentNode2 = page.extractAllNodesThatMatch(new HasAttributeFilter("id", "ctl00_cph1_lblSearchEnd"), true)
				.elementAt(0);
		Node toNode = parentNode2.getFirstChild();

		try {
			response.setFrom(parser.parse(fromNode.getText()));
		} catch (ParseException ex) {
		}
		try {
			response.setTo(parser.parse(toNode.getText()));
		} catch (ParseException ex) {
		}

		extractAllFlares(page, response);

		return response;
	}

	private void extractAllFlares(NodeList page, IridiumFlares flares) {
		NodeFilter filter = new AndFilter(new TagNameFilter("tr"), new HasAttributeFilter("onclick"));
		NodeList working = page.extractAllNodesThatMatch(filter, true);

		List<IridiumFlare> results = new Vector<IridiumFlare>();
		for (Node pass : working.toNodeArray()) {
			IridiumFlare flare = extractNextFlare(pass.getChildren());
			if (flare == null) {
				continue;
			}
			results.add(flare);
		}

		flares.setResults(results);
	}

	private IridiumFlare extractNextFlare(NodeList pass) {
		IridiumFlare flare = new IridiumFlare();
		DateFormat format = new SimpleDateFormat("MMM dd',' HH:mm:ss yyyy");

		Node[] nodes = pass.toNodeArray();
		for (int idx = 0; idx < nodes.length; ++idx) {
			nodes[idx] = nodes[idx].getFirstChild();
		}

		try {
			nodes[0] = nodes[0].getFirstChild();
			flare.setTime(format.parse(nodes[0].getText() + " " + Calendar.getInstance().get(Calendar.YEAR)));
		} catch (ParseException ex) {
			return null;
		}

		flare.setMagnitude(Double.parseDouble(nodes[1].getText()));

		flare.setAltitude(Double.parseDouble(nodes[2].getText().split("[^0-9\\.]", 2)[0]));
		flare.setAzimuth(Double.parseDouble(nodes[3].getText().split("[^0-9\\.]", 2)[0]));

		return flare;
	}

	private NodeList getPage(String path, List<NameValuePair> params) throws HeavensAboveException {
		try {
			return HttpScraper.scrape(httpClient, "heavens-above.com", -1, path, params);
		} catch (ConnectTimeoutException ex) {
			throw new HeavensAboveException(
					"Heavens Above seems to be taking too long to respond please try again later");
		} catch (Exception ex) {
			return null;
		}
	}
}
