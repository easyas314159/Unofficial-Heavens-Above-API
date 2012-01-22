package com.uhaapi.server.api;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.spy.memcached.MemcachedClientIF;
import net.spy.memcached.transcoders.Transcoder;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpStatus;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.heavens_above.HeavensAbove;
import com.uhaapi.server.ServletInitOptions;
import com.uhaapi.server.api.entity.Satellite;
import com.uhaapi.server.api.entity.SatellitePass;
import com.uhaapi.server.api.entity.SatellitePasses;
import com.uhaapi.server.cache.GsonTranscoder;
import com.uhaapi.server.geo.ElevationResponse;
import com.uhaapi.server.geo.ElevationService;
import com.uhaapi.server.geo.LatLng;

@Path("satellites")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class SatelliteResource {
	private static final GsonTranscoder<List<SatellitePass>> transcoderPasses;

	static {
		transcoderPasses = new GsonTranscoder<List<SatellitePass>>(new ParameterizedType() {
			@Override
			public Type getRawType() {
				return List.class;
			}
			@Override
			public Type getOwnerType() {
				return Collection.class;
			}
			@Override
			public Type[] getActualTypeArguments() {
				return new Type[]{SatellitePass.class};
			}
		}); 
	}

	private final String NS_SATELLITES = DigestUtils.md5Hex("satellites");
	private final String NS_PASSES = DigestUtils.md5Hex("satellites/passes");

	private final MemcachedClientIF memcached;
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
		this.memcached = memcached;
		this.elevationService = elevationService;
		this.heavensScraper = heavensScraper;

		this.precisionDenominator = precisionDenominator;
	}

	@GET
	public Response getStatus() {
		return Response.status(HttpStatus.SC_NO_CONTENT).build();
	}

	@GET
	@Path("{id}")
	public Response getSatellite(
			@PathParam("id") Integer id
		) {

		Transcoder<Satellite> tSat = null;
		
		String key = null;
		Satellite response = null;
		if(memcached != null) {
			tSat = new GsonTranscoder<Satellite>(Satellite.class);
			key = NS_SATELLITES + DigestUtils.md5Hex(String.format("%d", id));
			memcached.get(key, tSat);
		}

		try {
			if(response == null) {
				response = heavensScraper.getSatellite(id);

				if(memcached != null && response != null) {
					memcached.set(key, 24*60*60, response, tSat);
				}
			}
		}
		catch(Exception ex) {
			return Response.serverError().build();
		}

		return Response.ok(response).build();
	}

	@GET
	@Path("{id}/passes")
	public Response getSatellitePasses(
			@PathParam("id") Integer id,
			@QueryParam("lat") @DefaultValue("0") Double lat,
			@QueryParam("lng") @DefaultValue("0") Double lng,
			@QueryParam("lm") Double lm
		) {
		String key = null;

		long latid = Math.round(precisionDenominator * lat);
    	long lngid = Math.round(precisionDenominator * lng);

    	Future<List<SatellitePass>> futureAllPasses = null;
    	if(memcached != null) {
	    	key = NS_PASSES + DigestUtils.md5Hex(String.format("%s:%d:%d", id, latid, lngid));
	    	futureAllPasses = memcached.asyncGet(key, transcoderPasses);
    	}

    	lat = ((double)latid) / precisionDenominator;
    	lng = ((double)lngid) / precisionDenominator;

    	// The elevation service caches results under normal
    	// operation so making a call every request is ok
    	Future<ElevationResponse> futureElevation = elevationService.elevationAsync(lat, lng);

    	SatellitePasses response = new SatellitePasses();
    	response.setId(id);
    	response.setLocation(new LatLng(lat, lng));

    	List<SatellitePass> allPasses = null;
    	if(futureAllPasses != null) {
	    	try {
	    		allPasses = futureAllPasses.get();
	    	}
	    	catch(Exception ex) {}
    	}

    	try {
	    	if(allPasses == null) {
	    		double alt = 0.0;

	    		ElevationResponse elevation = futureElevation.get();
	    		if(elevation != null && elevation.getFirstResult() != null) {
	    			alt = elevation.getFirstResult().getElevation();
	    		}

	    		// TODO: Load/Parse passes
	    		SatellitePasses passResponse = heavensScraper.getVisiblePasses(id, lat, lng, alt);
	    		if(passResponse == null) {
	    			allPasses = Collections.EMPTY_LIST;
	    		}
	    		else {
	    			allPasses = passResponse.getResults();
	    		}

	    		if(memcached != null) {
	    			int expires = 0;
	    			if(passResponse != null && passResponse.inOrbit()) {
	    				Calendar cal = Calendar.getInstance();
	    	        	cal.setTime(passResponse.getTo());
	    	        	cal.add(Calendar.DAY_OF_MONTH, -1);

	    				expires = (int)(cal.getTimeInMillis() / 1000);
	    			}
    				memcached.set(key, expires, allPasses, transcoderPasses);
	    		}
	    	}
    	}
    	catch(Exception ex) {
    		allPasses = Collections.EMPTY_LIST;
    	}

    	Calendar cal = Calendar.getInstance();
    	response.setFrom(cal.getTime());
    	cal.add(Calendar.DAY_OF_MONTH, 1);
    	response.setTo(cal.getTime());

    	List<SatellitePass> releventPasses = new ArrayList<SatellitePass>();
    	for(SatellitePass pass : allPasses){
    		if(pass.getStart().getTime().before(response.getTo())
    				&& pass.getEnd().getTime().after(response.getFrom())
    				&& (lm == null || lm <= pass.getMagnitude())
    			) {
    			releventPasses.add(pass);
    		}
    	}
    	response.setResults(releventPasses);

    	try {
    		ElevationResponse elevation = futureElevation.get();
    		if(elevation != null && elevation.getFirstResult() != null) {
    			response.setAltitude(elevation.getFirstResult().getElevation());
    		}
    	}
    	catch(Exception ex) {
    		response.setAltitude(0.0);
    	}

		return Response.ok(response).build();
	}

	@GET
	@Path("{id}/tle")
	@Produces({MediaType.TEXT_PLAIN})
	public Response getSatelliteTLE(@PathParam("id") String id) {
		return Response.status(HttpStatus.SC_NOT_FOUND).build();
	}
}
