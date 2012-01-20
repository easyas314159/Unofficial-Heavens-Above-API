package com.uhaapi.server.api.entity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="satellite")
@XmlAccessorType(XmlAccessType.NONE)
public class Satellite {
	private int id;
	private String idc;
	
	private String origin;
	private String mass;

	private Orbit orbit;

	private Category category; 

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
