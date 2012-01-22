package com.uhaapi.server.api.entity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.gson.annotations.Expose;

@XmlRootElement(name="satellite")
@XmlAccessorType(XmlAccessType.NONE)
public class Satellite {
	@Expose
	private int id;

	@Expose
	private String idc;

	@Expose
	private String name;
	
	@Expose
	private String origin;

	@Expose
	private String mass;

	@Expose
	@XmlElement
	private Orbit orbit;

	@Expose
	private Launch launch;

	@Expose
	private Category category;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public String getIdc() {
		return idc;
	}
	public void setIdc(String idc) {
		this.idc = idc;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getOrigin() {
		return origin;
	}
	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getMass() {
		return mass;
	}
	public void setMass(String mass) {
		this.mass = mass;
	}

	public Orbit getOrbit() {
		return orbit;
	}
	public void setOrbit(Orbit orbit) {
		this.orbit = orbit;
	}

	public Launch getLaunch() {
		return launch;
	}
	public void setLaunch(Launch launch) {
		this.launch = launch;
	}

	public Category getCategory() {
		return category;
	}
	public void setCategory(Category category) {
		this.category = category;
	}

	public static interface Orbit {}

	public static class DecayedOrbit implements Orbit {
	}

	public static class NormalOrbit implements Orbit {
	}

	public static class Launch {
		
	}

	public enum Category {
		ROCKET_BODY,
		SPACE_STATION
		;
	}
}
