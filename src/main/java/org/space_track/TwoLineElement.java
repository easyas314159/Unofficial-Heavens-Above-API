package org.space_track;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

@XmlRootElement(name="tle")
@XmlAccessorType(XmlAccessType.NONE)
public class TwoLineElement {
	private final String title;
	private final String line1;
	private final String line2;

	public TwoLineElement(String line1, String line2) {
		this(null, line1, line2);
	}
	public TwoLineElement(String title, String line1, String line2) {
		this.title = title;
		this.line1 = line1;
		this.line2 = line2;
	}

	public String getTitle() {
		return title;
	}
	public String getLine1() {
		return line1;
	}
	public String getLine2() {
		return line2;
	}

	@XmlAttribute(name="id")
	public String getId() {
		return line1.substring(2, 7);
	}

	public String getClassification() {
		return line1.substring(7, 8);
	}

	public String getInternationalDesignatorYear() {
		return line1.substring(9, 11);
	}
	public String getInternationalDesignatorLaunch() {
		return line1.substring(11, 14);
	}
	public String getInternationalDesignatorPiece() {
		return line1.substring(14, 17);
	}

	public String getEpochYear() {
		return line1.substring(18, 20);
	}
	public String getEpochDay() {
		return line1.substring(20, 32);
	}

	public Date getEpoch() {
		throw new NotImplementedException();
	}

	@Override
	public String toString() {
		return String.format(
				"%s\n%s\n%s",
				getTitle(), getLine1(), getLine2()
			);
	}
}
