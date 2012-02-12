package com.uhaapi.server.api;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import net.spy.memcached.GsonTranscoder;
import net.spy.memcached.KeyedMemcachedClient;
import net.spy.memcached.MemcachedClientIF;
import net.spy.memcached.transcoders.Transcoder;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicHeaderElement;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.heavens_above.HeavensAbove;
import com.uhaapi.server.MemcachedKeys;
import com.uhaapi.server.ServletInitOptions;
import com.uhaapi.server.api.entity.Satellite;
import com.uhaapi.server.api.entity.SatellitePass;
import com.uhaapi.server.api.entity.SatellitePasses;
import com.uhaapi.server.geo.ElevationResponse;
import com.uhaapi.server.geo.ElevationService;
import com.uhaapi.server.geo.LatLng;

@Path("satellites")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class SatelliteResource {
	private static final GsonTranscoder<SatellitePasses> transcoderPasses
		= new GsonTranscoder<SatellitePasses>(SatellitePasses.class);

	private final Logger log = Logger.getLogger(getClass());
	
	private final MemcachedClientIF memcachedSat;
	private final MemcachedClientIF memcachedPasses;
	private final MemcachedClientIF memcachedTLE;

	private final ElevationService elevationService;
	private final HeavensAbove heavensScraper;

	private final Integer precisionDenominator;

	@Inject
	public SatelliteResource(
			MemcachedClientIF memcached,
			ElevationService elevationService,
			HeavensAbove heavensScraper,
			@Named(ServletInitOptions.APP_DEGREE_PRECISION_DENOMINATOR) Integer precisionDenominator
		) {
		this.memcachedSat = new KeyedMemcachedClient(memcached, MemcachedKeys.SATELLITES);
		this.memcachedPasses = new KeyedMemcachedClient(memcached, MemcachedKeys.SATELLITE_PASSES);
		this.memcachedTLE = new KeyedMemcachedClient(memcached, MemcachedKeys.SATELLITE_TLE);

		this.elevationService = elevationService;
		this.heavensScraper = heavensScraper;

		this.precisionDenominator = precisionDenominator;
	}

	@GET
	public Response getStatus() {
		return Response.ok().build();
	}

	@GET
	@Path("{id}")
	public Response getSatellite(
			@PathParam("id") Integer id,
			@Context UriInfo ui
		) {

		Transcoder<Satellite> tSat = null;

		String key = null;
		Satellite response = null;
		if(memcachedSat != null) {
			tSat = new GsonTranscoder<Satellite>(Satellite.class);
			key = DigestUtils.md5Hex(String.format("%d", id));
			response = memcachedSat.get(key, tSat);
		}

		try {
			if(response == null) {
				response = heavensScraper.getSatellite(id);

				if(memcachedSat != null && response != null) {
					memcachedSat.set(key, 24*60*60, response, tSat);
				}
			}
		}
		catch(Exception ex) {
			return Response.serverError().build();
		}

		if(response == null) {
			return Response.status(HttpStatus.SC_NOT_FOUND).build();
		}

		BasicNameValuePair rel, title;

		rel = new BasicNameValuePair("rel", "passes");
		title = new BasicNameValuePair("title", "Visible pass in the next 24-hours");
		BasicHeaderElement linkPasses = new BasicHeaderElement("<passes>", null, new NameValuePair[]{rel, title});

		rel = new BasicNameValuePair("rel", "tle");
		title = new BasicNameValuePair("title", "Most recent two line element");
		BasicHeaderElement linkTLE = new BasicHeaderElement("<tle>", null, new NameValuePair[]{rel});
		
		return Response
				.ok(response)
				.header("Link", StringUtils.join(new Object[]{linkPasses, linkTLE}, ","))
				.build();
	}

	@GET
	@Path("{id}/passes")
	public Response getSatellitePasses(
			@PathParam("id") Integer id,
			@QueryParam("lat") @DefaultValue("0") Double lat,
			@QueryParam("lng") @DefaultValue("0") Double lng,
			@QueryParam("lm") Double lm
		) {
		Date now = new Date();
		Calendar cal = Calendar.getInstance();

		long latid = Math.round(precisionDenominator * lat);
    	long lngid = Math.round(precisionDenominator * lng);

    	String key = DigestUtils.md5Hex(String.format("%s:%d:%d", id, latid, lngid));

    	Future<SatellitePasses> asyncCachedPasses = null;
    	if(memcachedPasses != null) {
	    	asyncCachedPasses = memcachedPasses.asyncGet(key, transcoderPasses);
    	}

    	lat = ((double)latid) / precisionDenominator;
    	lng = ((double)lngid) / precisionDenominator;

    	// The elevation service caches results under normal
    	// operation so making a call every request is ok
    	Future<ElevationResponse> futureElevation = elevationService.elevationAsync(lat, lng);

    	SatellitePasses response = new SatellitePasses();
    	response.setId(id);
    	response.setLocation(new LatLng(lat, lng));

    	SatellitePasses cachedPasses = null;
    	if(asyncCachedPasses != null) {
	    	try {
	    		cachedPasses = asyncCachedPasses.get();
	    	}
	    	catch(Exception ex) {}
    	}

    	try {
	    	if(cachedPasses == null) {
	    		double alt = 0.0;

	    		ElevationResponse elevation = futureElevation.get();
	    		if(elevation != null && elevation.getFirstResult() != null) {
	    			alt = elevation.getFirstResult().getElevation();
	    		}

	    		cachedPasses = heavensScraper.getVisiblePasses(id, lat, lng, alt);

	    		if(memcachedPasses != null) {
	    			int expires = 0;
	    			if(cachedPasses != null) {
	    	        	cal.setTime(cachedPasses.getTo());
	    	        	cal.add(Calendar.DAY_OF_MONTH, -1);

	    				expires = (int)(cal.getTimeInMillis() / 1000);
	    			}
    				memcachedPasses.set(key, expires, cachedPasses, transcoderPasses);
	    		}
	    	}
    	}
    	catch(Exception ex) {
    		log.warn("", ex);
    		return Response.serverError().build();
    	}

    	if(cachedPasses == null) {
    		return Response.status(HttpStatus.SC_NOT_FOUND).build();
    	}

    	cal.setTime(now);
    	cal.add(Calendar.DAY_OF_MONTH, 1);
    	response.setTo(cal.getTime());

    	Date nextPass = cachedPasses.getTo();
    	List<SatellitePass> releventPasses = new ArrayList<SatellitePass>();
    	for(SatellitePass pass : cachedPasses.getResults()){
    		if(pass.getEnd().getTime().after(now)) {
    			if(pass.getStart().getTime().before(response.getTo())
    					&& (lm == null || pass.getMagnitude() <= lm)
    				) {
    				releventPasses.add(pass);
    			}
    			else if(pass.getStart().getTime().before(nextPass)) {
    				nextPass = pass.getStart().getTime();
    			}
    		}
    	}
    	response.setResults(releventPasses);

    	cal.setTime(nextPass);
    	cal.add(Calendar.DAY_OF_MONTH, -1);
    	Date expires = cal.getTime();

    	CacheControl cache = new CacheControl();

    	cache.setMaxAge((int)(expires.getTime() - now.getTime()) / 1000);

    	EntityTag tag = new EntityTag(key);

    	response.setFrom(now);
    	response.setTo(nextPass);

    	try {
    		ElevationResponse elevation = futureElevation.get();
    		if(elevation != null && elevation.getFirstResult() != null) {
    			response.setAltitude(elevation.getFirstResult().getElevation());
    		}
    	}
    	catch(Exception ex) {
    		response.setAltitude(0.0);
    	}

		return Response.ok(response)
			.header(HttpHeaders.EXPIRES, expires)
			.cacheControl(cache)
			.tag(tag)
			.build();
	}

	@GET
	@Path("{id}/tle")
	@Produces({MediaType.TEXT_PLAIN})
	public Response getSatelliteTLE(@PathParam("id") String id) {
		String key = DigestUtils.md5Hex(id);

		//memcachedTLE.get(key, new GsonTranscoder<T>(gsonBuilder, baseType))

		return Response
				.status(HttpStatus.SC_NOT_IMPLEMENTED)
				.type(MediaType.TEXT_PLAIN)
				.build();
	}
}
