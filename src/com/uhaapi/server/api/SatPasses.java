package com.uhaapi.server.api;

import java.io.IOException;
import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.memcached.MemcachedClient;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.Text;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.CssSelectorNodeFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.HasParentFilter;
import org.htmlparser.filters.NotFilter;
import org.htmlparser.filters.RegexFilter;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.lexer.Page;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.NodeVisitor;

import com.google.gson.Gson;
import com.uhaapi.server.Configurator;
import com.uhaapi.server.ServletInitOptions;
import com.uhaapi.server.cache.Cache;
import com.uhaapi.server.cache.DistributedCache;
import com.uhaapi.server.cache.GsonTranscoder;
import com.uhaapi.server.cache.SimpleCache;
import com.uhaapi.server.geo.ElevationResponse;
import com.uhaapi.server.geo.ElevationResult;
import com.uhaapi.server.geo.ElevationService;
import com.uhaapi.server.geo.LatLng;
import com.uhappi.server.util.GsonUtils;
import com.uhappi.server.util.ParamUtils;
import com.uhappi.server.util.ThreadLocalInstanceFactory;

public class SatPasses extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private final Logger log = Logger.getLogger(getClass());

	private HttpClient httpClient;
	private Integer precisionDenominator;
	private String userAgent = null;

	private Cache<String, SatPassResponse> cache = null;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		httpClient = new DefaultHttpClient();

		ServletContext context = config.getServletContext();
		MemcachedClient memcached = (MemcachedClient)context.getAttribute(Configurator.MEMCAHED);
		if(memcached == null) {
			cache = new SimpleCache<String, SatPassResponse>();
		}
		else {
			cache = new DistributedCache<SatPassResponse>(
					memcached,
					new GsonTranscoder<SatPassResponse>(SatPassResponse.class),
					DigestUtils.md5Hex("skyscraper")
				);
		}

		precisionDenominator = ParamUtils.asInteger(
				context.getInitParameter(ServletInitOptions.APP_DEGREE_PRECISION_DENOMINATOR),
				20
			);
		userAgent = ParamUtils.asString(context.getInitParameter(ServletInitOptions.APP_USER_AGENT));
	}

    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	ServletContext context = getServletContext();

    	Integer satid = ParamUtils.asInteger(request.getParameter("satid"));
    	if(satid == null) {
    		// TODO: Lookup satellite alias
    		response.sendError(HttpServletResponse.SC_NOT_FOUND);
    		return;
    	}

    	long latid = Math.round(precisionDenominator * ParamUtils.asDouble(request.getParameter("lat"), 0.0));
    	long lngid = Math.round(precisionDenominator * ParamUtils.asDouble(request.getParameter("lng"), 0.0));

    	String key = DigestUtils.md5Hex(String.format("%s %d %d", satid, latid, lngid));
    	SatPassResponse allPasses = cache.get(key);
    	if(allPasses == null) {
    		double alt = 0.0;
    		double lat = ((double)latid) / precisionDenominator;
    		double lng = ((double)lngid) / precisionDenominator;

    		ElevationService elevationService = (ElevationService)context.getAttribute(Configurator.ELEVATION_SERVICE);
    		ElevationResponse elevation = elevationService.elevation(lat, lng);

    		Iterator<ElevationResult> result = elevation.resultsIterator();
    		if(result.hasNext()) {
    			alt = result.next().getElevation();
    		}

    		allPasses = scrape(satid, lat, lng, alt);
    		if(allPasses == null) {
    			allPasses = new SatPassResponse(StatusCodes.NO_SATELLITE);
    		}

    		// We can cache indefinitely if it's no longer in orbit or doesn't exist
    		if(allPasses.inOrbit()) {
	        	Calendar cal = Calendar.getInstance();
	        	cal.setTime(allPasses.getTo());
	        	cal.add(Calendar.DAY_OF_MONTH, -1);
	
	        	cache.put(key, allPasses, cal.getTime());
    		}
    		else {
    			cache.put(key, allPasses, 0);
    		}
    	}

    	Calendar cal = Calendar.getInstance();
    	Date from = cal.getTime();
    	cal.add(Calendar.DAY_OF_MONTH, 1);
    	Date to = cal.getTime();

    	Gson gson = GsonUtils.newGson();
    	gson.toJson(allPasses.subset(from, to), response.getWriter());
    }

    private SatPassResponse scrape(int satid, double lat, double lng, double alt) throws IOException {
    	SatPassResponse response = null;
    	List<NameValuePair> params = new Vector<NameValuePair>();
    	params.add(new BasicNameValuePair("satid", Integer.toString(satid)));
		params.add(new BasicNameValuePair("lat", Double.toString(lat)));
		params.add(new BasicNameValuePair("lng", Double.toString(lng)));
		params.add(new BasicNameValuePair("alt", Double.toString(alt)));
		params.add(new BasicNameValuePair("tz", "UCT"));

		try {
			URI uri = URIUtils.createURI(
					"http",
					"heavens-above.com",
					-1,
					"PassSummary.aspx",
					URLEncodedUtils.format(params, "UTF-8"),
					null
				);
			log.debug(uri);
			NodeList page = getHTML(uri);
			if(page != null) {
				response = new SatPassResponse();
				response.setId(satid);
				response.setAltitude(alt);
				response.setLocation(new LatLng(lat, lng));
	
				extractDetails(page, response);
				extractAllPasses(page, response);
			}
		}
		catch(IOException ex) {
			throw ex;
		}
		catch(Exception ex) {
			log.warn("Failed to scrape contents", ex);
		}

		return response;
    }

    private ThreadLocalInstanceFactory<Parser> threadLocalParser = new ThreadLocalInstanceFactory<Parser>() {
		@Override
		protected Parser instantiate() {
			return new Parser();
		}
		@Override
		protected Parser configure(Parser o) {
			o.reset();
			return o;
		};
	};

    private NodeList getHTML(URI uri) throws ClientProtocolException, IOException, ParserException {
    	HttpGet get = new HttpGet(uri);
    	get.setHeader("User-Agent", userAgent);
		HttpResponse rsp = httpClient.execute(get);
		
		if(rsp.getStatusLine().getStatusCode() != 200) {
			return null;
		}

		HttpEntity entity = rsp.getEntity();

		Parser parser = threadLocalParser.getInstance();
		Lexer lexer = new Lexer(new Page(entity.getContent(), "UTF-8"));
		parser.setLexer(lexer);

		NodeList result = parser.parse(null);
		result.keepAllNodesThatMatch(new NotFilter(new RegexFilter("^\\s*$", RegexFilter.MATCH)), true);
		return result;
    }

    private SatPassResponse extractDetails(NodeList page, SatPassResponse response) {
    	DateFormat searchRangeFormat = new SimpleDateFormat("HH:mm EEEE, d MMMM, yyyy");
    	NodeList working = null;

    	// The Satellite Name
    	working = page.extractAllNodesThatMatch(
    			new HasParentFilter(new HasAttributeFilter("id", "ctl00_lblTitle"), true),
    			true
    		);
    	response.setName(StringUtils.chomp(working.asString(), " - Visible Passes"));

    	// The Search Period
    	Date searchStart = null;
    	working = page.extractAllNodesThatMatch(
    			new HasParentFilter(new HasAttributeFilter("id", "ctl00_ContentPlaceHolder1_lblSearchStart")),
    			true
    		);
		try {
			searchStart = searchRangeFormat.parse(working.asString());
			response.setFrom(searchStart);
		} catch(ParseException ex) {
			log.warn("Failed to parse start time", ex);
		}

		Date searchEnd = null;
    	working = page.extractAllNodesThatMatch(
    			new HasParentFilter(new HasAttributeFilter("id", "ctl00_ContentPlaceHolder1_lblSearchEnd")),
    			true
    		);
    	try {
    		searchEnd = searchRangeFormat.parse(working.asString());
    		response.setTo(searchEnd);
    	} catch(ParseException ex) {
    		log.warn("Failed to parse end time", ex);
    	}

    	return response;
    }

    private void extractAllPasses(NodeList page, SatPassResponse response) {
    	NodeFilter filter = new AndFilter(
				new HasParentFilter(new HasAttributeFilter("id", "ctl00_ContentPlaceHolder1_tblPasses"), false),
				new NotFilter(new CssSelectorNodeFilter(".tablehead"))
			);

    	Calendar prevPassTime = Calendar.getInstance();
    	prevPassTime.setTime(response.getFrom());

    	List<SatPassResult> results = new Vector<SatPassResult>();
		NodeList allPasses = page.extractAllNodesThatMatch(filter, true);
		for(Node pass : allPasses.toNodeArray()) {
			results.add(extractNextPass(pass.getChildren(), prevPassTime));
		}
		response.setResults(results);
    }

    private SatPassResult extractNextPass(NodeList pass, Calendar prevPassTime) {
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
		SatPassResult result = null;
		try {
			pass.visitAllNodesWith(visitor);

			result = new SatPassResult();
			result.setMagnitude(ParamUtils.asDouble(details.get(1)));

			Date start = format.parse(String.format("%s %04d %s", details.get(0), prevPassTime.get(Calendar.YEAR), details.get(2)));
			if(nextPassTime.getTimeInMillis() < prevPassTime.getTimeInMillis() && prevPassTime.get(Calendar.MONTH) != prevPassTime.get(Calendar.MONTH)) {
				nextPassTime.add(Calendar.YEAR, 1);
				start = nextPassTime.getTime();
			}
			prevPassTime.setTimeInMillis(nextPassTime.getTimeInMillis());
			result.setStart(new SatPassWaypoint(
					start,
					ParamUtils.asDouble(details.get(3)),
					convertCompassPoint(details.get(4))
				));

			Date max = format.parse(String.format("%s %04d %s", details.get(0), prevPassTime.get(Calendar.YEAR), details.get(5)));
			if(max.before(start)) {
				nextPassTime.add(Calendar.DAY_OF_MONTH, 1);
				max = nextPassTime.getTime();
			}
			prevPassTime.setTimeInMillis(nextPassTime.getTimeInMillis());
			result.setMax(new SatPassWaypoint(
					max,
					ParamUtils.asDouble(details.get(6)),
					convertCompassPoint(details.get(7))
				));

			Date end = format.parse(String.format("%s %04d %s", details.get(0), prevPassTime.get(Calendar.YEAR), details.get(8)));
			if(end.before(start)) {
				nextPassTime.add(Calendar.DAY_OF_MONTH, 1);
				end = nextPassTime.getTime();
			}
			prevPassTime.setTimeInMillis(nextPassTime.getTimeInMillis());
			result.setEnd(new SatPassWaypoint(
					end,
					ParamUtils.asDouble(details.get(9)),
					convertCompassPoint(details.get(10))
				));
		}
		catch(Exception ex) {
			log.warn("Failed to parse satellite pass", ex);
		}

    	return result;
    }

    private Double convertCompassPoint(String name) {
    	return Compass.valueOf(name).getAzimuth();
    }
}
