package com.uhaapi.server.geo;

import java.util.Iterator;
import java.util.List;

public class ElevationResponse extends MapsResponse {
	private List<ElevationResult> results = null;

	public Iterator<ElevationResult> resultsIterator() {
		return results.iterator();
	}
}
