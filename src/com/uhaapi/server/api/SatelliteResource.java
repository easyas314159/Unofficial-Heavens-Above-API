package com.uhaapi.server.api;

import java.io.IOException;
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
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicHeaderElement;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.space_track.TwoLineElement;

import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.heavens_above.HeavensAbove;
import com.uhaapi.server.MemcachedKeys;
import com.uhaapi.server.ServletInitOptions;
import com.uhaapi.server.api.entity.IridiumFlare;
import com.uhaapi.server.api.entity.IridiumFlares;
import com.uhaapi.server.api.entity.Pass;
import com.uhaapi.server.api.entity.Satellite;
import com.uhaapi.server.api.entity.SatellitePass;
import com.uhaapi.server.api.entity.SatellitePasses;
import com.uhaapi.server.error.InternalServerException;
import com.uhaapi.server.error.NotFoundException;
import com.uhaapi.server.geo.ElevationResponse;
import com.uhaapi.server.geo.ElevationService;
import com.uhaapi.server.geo.LatLng;

@Path("satellites")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class SatelliteResource {
	private final GsonTranscoder<SatellitePasses> transcoderPasses;
	private final GsonTranscoder<TwoLineElement> transcoderTLE;

	private final GsonTranscoder<IridiumFlares> transcoderFlares;

	private final Logger log = Logger.getLogger(getClass());
	
	private final MemcachedClientIF memcachedSat;
	private final MemcachedClientIF memcachedPasses;
	private final MemcachedClientIF memcachedTLE;
	private final MemcachedClientIF memcachedFlares;

	private final ElevationService elevationService;
	private final HeavensAbove heavensScraper;

	private final Integer precisionDenominator;

	@Inject
	public SatelliteResource(
			MemcachedClientIF memcached,
			ElevationService elevationService,
			HeavensAbove heavensScraper,
			GsonBuilder gsonBuilder,
			@Named(ServletInitOptions.APP_DEGREE_PRECISION_DENOMINATOR) Integer precisionDenominator
		) {
		this.memcachedSat = new KeyedMemcachedClient(memcached, MemcachedKeys.SATELLITES);
		this.memcachedPasses = new KeyedMemcachedClient(memcached, MemcachedKeys.SATELLITE_PASSES);
		this.memcachedTLE = new KeyedMemcachedClient(memcached, MemcachedKeys.SATELLITE_TLE);
		this.memcachedFlares = new KeyedMemcachedClient(memcached, MemcachedKeys.FLARES);

		this.elevationService = elevationService;
		this.heavensScraper = heavensScraper;

		this.precisionDenominator = precisionDenominator;

		this.transcoderPasses = new GsonTranscoder<SatellitePasses>(gsonBuilder, SatellitePasses.class);
		this.transcoderTLE = new GsonTranscoder<TwoLineElement>(gsonBuilder, TwoLineElement.class);
		this.transcoderFlares = new GsonTranscoder<IridiumFlares>(gsonBuilder, IridiumFlares.class);
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
		catch(IOException ex) {
    		throw new InternalServerException("Internal IO error please try again later");
		}

		if(response == null) {
			throw new NotFoundException("No satellite information available");
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
	    		double alt = safeGetElevationResponse(futureElevation);
	    		cachedPasses = heavensScraper.getVisiblePasses(id, lat, lng, alt);

	    		if(memcachedPasses != null) {
	    			int expires = 0;
	    			if(cachedPasses != null) {
	    	        	cal.setTime(cachedPasses.getFrom());
	    	        	cal.add(Calendar.DAY_OF_MONTH, 1);

	    				expires = (int)(cal.getTimeInMillis() / 1000);
	    			}
    				memcachedPasses.set(key, expires, cachedPasses, transcoderPasses);
	    		}
	    	}
    	}
    	catch(IOException ex) {
    		log.warn("", ex);
    		throw new InternalServerException("Internal IO error please try again later");
    	}

    	if(cachedPasses == null) {
    		throw new NotFoundException("No pass information available");
    	}

    	cal.setTime(now);
    	cal.add(Calendar.DAY_OF_MONTH, 5);

    	response.setFrom(now);
    	response.setTo(cal.getTime());
    	
    	response.setAltitude(cachedPasses.getAltitude());

    	List<SatellitePass> releventPasses = new ArrayList<SatellitePass>();
    	selectPasses(now, response.getTo(), lm, cachedPasses.getResults(), releventPasses);
    	response.setResults(releventPasses);

    	CacheControl cache = new CacheControl();
    	cache.setMaxAge(86400);

    	EntityTag tag = new EntityTag(key);

		return Response.ok(response)
			.cacheControl(cache)
			.tag(tag)
			.build();
	}

	@GET
	@Path("{id}/tle")
	@Produces({MediaType.TEXT_PLAIN})
	public Response getSatelliteTLE(@PathParam("id") String id) throws NotFoundException {
		String key = DigestUtils.md5Hex(id);

		TwoLineElement tle = memcachedTLE.get(key, transcoderTLE);
		if(tle == null) {
			throw new NotFoundException("No two line element available");
		}

		return Response
				.status(HttpStatus.SC_OK)
				.type(MediaType.TEXT_PLAIN)
				.entity(tle)
				.build();
	}

	@GET
	@Path("iridium/flares")
	public Response getSatellitePasses(
			@QueryParam("lat") @DefaultValue("0") Double lat,
			@QueryParam("lng") @DefaultValue("0") Double lng,
			@QueryParam("lm") Double lm
		) {
		Date now = new Date();
		Calendar cal = Calendar.getInstance();

		long latid = Math.round(precisionDenominator * lat);
    	long lngid = Math.round(precisionDenominator * lng);

    	String key = DigestUtils.md5Hex(String.format("%d:%d", latid, lngid));

    	Future<IridiumFlares> asyncCachedFlares = null;
    	if(memcachedPasses != null) {
    		asyncCachedFlares = memcachedFlares.asyncGet(key, transcoderFlares);
    	}

    	lat = ((double)latid) / precisionDenominator;
    	lng = ((double)lngid) / precisionDenominator;

    	// The elevation service caches results under normal
    	// operation so making a call every request is ok
    	Future<ElevationResponse> futureElevation = elevationService.elevationAsync(lat, lng);

    	IridiumFlares response = new IridiumFlares();
    	response.setLocation(new LatLng(lat, lng));

    	IridiumFlares cachedFlares = null;
    	if(asyncCachedFlares != null) {
    		try {
    			cachedFlares = asyncCachedFlares.get();
    		}
    		catch(Exception ex) {}
    	}

		if(cachedFlares == null) {
    		double alt = safeGetElevationResponse(futureElevation);
    		cachedFlares = heavensScraper.getIridiumFlares(lat, lng, alt);

    		if(memcachedFlares != null) {
    			int expires = 0;
    			if(cachedFlares != null) {
    	        	cal.setTime(new Date());
    	        	cal.add(Calendar.DAY_OF_MONTH, 1);

    				expires = (int)(cal.getTimeInMillis() / 1000);
    			}
    			memcachedFlares.set(key, expires, cachedFlares, transcoderFlares);
    		}
    	}

    	cal.setTime(now);
    	cal.add(Calendar.DAY_OF_MONTH, 5);

    	response.setFrom(now);
    	response.setTo(cal.getTime());
    	response.setAltitude(cachedFlares.getAltitude());

    	List<IridiumFlare> releventPasses = new ArrayList<IridiumFlare>();
    	selectPasses(now, response.getTo(), lm, cachedFlares.getResults(), releventPasses);
    	response.setResults(releventPasses);

    	CacheControl cache = new CacheControl();
    	cache.setMaxAge(86400);

    	EntityTag tag = new EntityTag(key);

		return Response.ok(response)
				.cacheControl(cache)
				.tag(tag)
				.build();
	}

	private <X extends Pass> void selectPasses(Date start, Date end, Double lm, List<X> src, List<X> dst) {
		for(X pass : src){
    		if(pass.getStartTime().after(start) && pass.getStartTime().before(end)
					&& (lm == null || pass.getMagnitude() <= lm)
				) {
				dst.add(pass);
			}
    	}
	}

	private Double safeGetElevationResponse(Future<ElevationResponse> futureElevation) {
		try {
			ElevationResponse elevation = futureElevation.get();
			if(elevation != null && elevation.getFirstResult() != null) {
    			return elevation.getFirstResult().getElevation();
    		}
		}
		catch(Exception ex) {}
		
		return 0.0;
	}
}
