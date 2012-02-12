package com.uhaapi.server;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.spy.memcached.KeyedMemcachedClient;
import net.spy.memcached.MemcachedClientIF;

import org.space_track.SpaceTrack;
import org.space_track.SpaceTrackUpdater;

import com.google.inject.Injector;

public class SpaceTrackListener implements ServletContextListener {
	private static final String SPACETRACK_UPDATE = SpaceTrackUpdater.class.getName();

	private ScheduledExecutorService executor;
	private MemcachedClientIF memcached;
	private SpaceTrack spaceTrack;

	@Override
	public void contextInitialized(ServletContextEvent evt) {
		ServletContext ctx = evt.getServletContext();
		Injector injector = (Injector)ctx.getAttribute(Injector.class.getName());

		spaceTrack = injector.getInstance(SpaceTrack.class);
		if(spaceTrack == null) {
			return;
		}

		executor = injector.getInstance(ScheduledExecutorService.class);

		memcached = injector.getInstance(MemcachedClientIF.class);
		memcached = new KeyedMemcachedClient(memcached, MemcachedKeys.SATELLITE_TLE);

		ScheduledFuture<?> future = executor.scheduleAtFixedRate(
				new SpaceTrackUpdater(spaceTrack, memcached), 0, 1, TimeUnit.DAYS
			);
		ctx.setAttribute(SPACETRACK_UPDATE, future);
	}
	@Override
	public void contextDestroyed(ServletContextEvent evt) {
		ServletContext ctx = evt.getServletContext();

		ScheduledFuture<?> future = (ScheduledFuture<?>)ctx.getAttribute(SPACETRACK_UPDATE);
		if(future != null) {
			future.cancel(false);
		}
	}
}
