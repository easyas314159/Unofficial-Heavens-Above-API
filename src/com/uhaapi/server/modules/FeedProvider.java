package com.uhaapi.server.modules;

import org.apache.log4j.Logger;

import Pachube.Feed;
import Pachube.Pachube;
import Pachube.PachubeException;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.uhaapi.server.ServletInitOptions;

public class FeedProvider implements Provider<Feed> {
	private final Logger log = Logger.getLogger(getClass());

	private final Pachube pachube;
	private final Integer feedId;

	@Inject
	public FeedProvider(Pachube pachube, @Named(ServletInitOptions.PACHUBE_FEED_ID) Integer feedId) {
		this.pachube = pachube;
		this.feedId = feedId;
	}

	@Override
	public Feed get() {
		if(feedId == null) {
			return null;
		}
		try {
			return pachube.getFeed(feedId);
		} catch(PachubeException ex) {
			log.warn(String.format("Failed to obtain Pachube feed with id \'%d\'", feedId), ex);
		}
		return null;
	}
}
