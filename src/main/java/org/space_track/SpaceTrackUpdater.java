package org.space_track;

import java.util.Collection;

import net.spy.memcached.MemcachedClientIF;
import net.spy.memcached.transcoders.Transcoder;

import org.apache.commons.codec.digest.DigestUtils;

public class SpaceTrackUpdater implements Runnable {
	private final SpaceTrack spaceTrack;
	private final MemcachedClientIF memcached;

	private final Transcoder<TwoLineElement> transcoder;

	public SpaceTrackUpdater(SpaceTrack spaceTrack, MemcachedClientIF memcached, Transcoder<TwoLineElement> tc) {
		this.spaceTrack = spaceTrack;

		this.memcached = memcached;
		this.transcoder = tc;
	}

	@Override
	public void run() {
		if(!spaceTrack.login()) {
			return;
		}

		Collection<TwoLineElement> tles = spaceTrack.getFullCatalog();
		for(TwoLineElement tle : tles) {
			String key = DigestUtils.md5Hex(tle.getId().replaceAll("^0+", ""));
			memcached.set(key, 2*86400, tle, transcoder);
		}

		spaceTrack.logout();
	}
}
