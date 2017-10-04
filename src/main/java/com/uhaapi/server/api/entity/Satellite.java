package com.uhaapi.server.api.entity;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.gson.annotations.Expose;

@XmlRootElement(name="satellite")
@XmlAccessorType(XmlAccessType.NONE)
public class Satellite {
	@Expose
	@XmlAttribute
	private int id;

	@Expose
	@XmlElement
	private String idc;

	@Expose
	@XmlElement
	private String name;
	
	@Expose
	@XmlElement
	private Date launched;

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

	public Date getLaunched() {
		return launched;
	}
	public void setLaunched(Date launched) {
		this.launched = launched;
	}
}
