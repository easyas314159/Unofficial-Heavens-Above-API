package org.space_track;

import java.util.Collection;

import net.spy.memcached.MemcachedClientIF;

public class SpaceTrackUpdater implements Runnable {
	private final SpaceTrack spaceTrack;
	private final MemcachedClientIF memcached;

	public SpaceTrackUpdater(SpaceTrack spaceTrack, MemcachedClientIF memcached) {
		this.spaceTrack = spaceTrack;

		this.memcached = memcached;
	}

	@Override
	public void run() {
		if(!spaceTrack.login()) {
			return;
		}

		Collection<TwoLineElement> tles = spaceTrack.getFullCatalog();
		for(TwoLineElement tle : tles) {
			
		}

		spaceTrack.logout();
	}
}
