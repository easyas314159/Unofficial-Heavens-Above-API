package com.uhaapi.server.api;

import java.util.Date;
import java.util.List;
import java.util.Vector;

import com.uhaapi.server.geo.LatLng;

public class SatPassResponse extends UhaapiResponse {
	private Integer id = null;
	private String name = null;

	private LatLng location = null;
	private Double altitude = null;
	
	private Date from = null;
	private Date to = null;

	private List<SatPassResult> results = null;

	public SatPassResponse() {
		this(StatusCodes.OK);
	}
	public SatPassResponse(StatusCodes status) {
		super(status);
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public LatLng getLocation() {
		return location;
	}
	public void setLocation(LatLng location) {
		this.location = location;
	}

	public Double getAltitude() {
		return altitude;
	}
	public void setAltitude(Double altitude) {
		this.altitude = altitude;
	}

	public Date getFrom() {
		return from;
	}
	public void setFrom(Date from) {
		this.from = from;
	}

	public Date getTo() {
		return to;
	}
	public void setTo(Date to) {
		this.to = to;
	}

	boolean inOrbit() {
		return !(getFrom() == null || getTo() == null) && getFrom().before(getTo());
	}

	public List<SatPassResult> getResults() {
		return results;
	}
	public void setResults(List<SatPassResult> results) {
		this.results = results;
	}

	public SatPassResponse subset(Date from, Date to) {
		SatPassResponse passes = new SatPassResponse();

		passes.setId(getId());
		passes.setName(getName());

		passes.setStatus(getStatus());

		passes.setFrom(from);
		passes.setTo(to);
		
		passes.setAltitude(getAltitude());
		passes.setLocation(getLocation());

		if(getResults() != null) {
			List<SatPassResult> results = new Vector<SatPassResult>();
			for(SatPassResult result : getResults()) {
				if(from.before(result.getStart().getTime()) && to.after(result.getStart().getTime())) {
					results.add(result);
				}
			}
			passes.setResults(results);
		}

		return passes;
	}
}
