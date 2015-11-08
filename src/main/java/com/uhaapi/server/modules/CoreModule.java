package com.uhaapi.server.modules;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import javax.servlet.ServletContext;

import net.spy.memcached.MemcachedClientIF;

import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.log4j.Logger;
import org.space_track.SpaceTrack;

import com.google.inject.name.Names;
import com.heavens_above.HeavensAbove;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import com.uhaapi.server.ErrorFilter;
import com.uhaapi.server.LoadFilter;
import com.uhaapi.server.MemcachedListener;
import com.uhaapi.server.ServletInitOptions;
import com.uhaapi.server.ThreadPoolListener;
import com.uhaapi.server.geo.MapsCredentials;
import com.uhaapi.server.injection.PrettyPrinting;

public class CoreModule extends JerseyServletModule {
	private final Logger log = Logger.getLogger(getClass());

	private final ServletContext context;

	public CoreModule(ServletContext ctx) {
		this.context = ctx;
	}

	@Override
	protected void configureServlets() {
		try {
			configureInitParameters(context);

			bind(MemcachedClientIF.class)
				.toProvider(new ContextAttributeProvider<MemcachedClientIF>(context, MemcachedListener.MEMCACHED));
			bind(ScheduledExecutorService.class)
				.toProvider(new ContextAttributeProvider<ScheduledExecutorService>(context, ThreadPoolListener.THREAD_POOL));
			bind(ExecutorService.class)
				.to(ScheduledExecutorService.class);

			bind(MapsCredentials.class)
				.toProvider(MapsCredentialsProvider.class)
				.asEagerSingleton();
			bind(HeavensAbove.class)
				.toProvider(HeavensAboveProvider.class)
				.asEagerSingleton();
			bind(SpaceTrack.class)
				.toProvider(SpaceTrackProvider.class)
				.asEagerSingleton();

			Map<String, String> params = new HashMap<String, String>();
            params.put(
            		PackagesResourceConfig.PROPERTY_PACKAGES,
            		"com.uhaapi.server;"
            	);

            filter("/*").through(ErrorFilter.class);
            filter("/*").through(LoadFilter.class);

            serve("/*")
            	.with(GuiceContainer.class, params);
		}
		catch(Throwable t) {
			log.error("Configuration Failed!", t);
		}
	}

	private void configureInitParameters(ServletContext ctx) {
		bind(Boolean.class)
			.annotatedWith(PrettyPrinting.class)
			.toProvider(new BooleanProvider(ctx, ServletInitOptions.APP_PRETTY_PRINTING, Boolean.FALSE))
			.asEagerSingleton();
		bind(Integer.class)
			.annotatedWith(Names.named(ServletInitOptions.APP_DEGREE_PRECISION_DENOMINATOR))
			.toProvider(new IntegerProvider(ctx, ServletInitOptions.APP_DEGREE_PRECISION_DENOMINATOR, 20))
			.asEagerSingleton();

		// HTTP Client
		bind(String.class)
			.annotatedWith(Names.named(ServletInitOptions.HTTP_USER_AGENT))
			.toProvider(new StringProvider(ctx, ServletInitOptions.HTTP_USER_AGENT, null))
			.asEagerSingleton();
		bind(Integer.class)
			.annotatedWith(Names.named(ServletInitOptions.HTTP_CONNECTION_TIMEOUT))
			.toProvider(new IntegerProvider(ctx, ServletInitOptions.HTTP_CONNECTION_TIMEOUT, 30 * 1000))
			.asEagerSingleton();

		bind(Boolean.class)
			.annotatedWith(Names.named(ServletInitOptions.HTTP_HEURISTIC_CACHE))
			.toProvider(new BooleanProvider(ctx, ServletInitOptions.HTTP_HEURISTIC_CACHE, CacheConfig.DEFAULT_HEURISTIC_CACHING_ENABLED))
			.asEagerSingleton();
		bind(Float.class)
			.annotatedWith(Names.named(ServletInitOptions.HTTP_HEURISTIC_COEFFICIENT))
			.toProvider(new FloatProvider(ctx, ServletInitOptions.HTTP_HEURISTIC_COEFFICIENT, CacheConfig.DEFAULT_HEURISTIC_COEFFICIENT))
			.asEagerSingleton();
		bind(Long.class)
			.annotatedWith(Names.named(ServletInitOptions.HTTP_HEURISTIC_LIFETIME))
			.toProvider(new LongProvider(ctx, ServletInitOptions.HTTP_HEURISTIC_LIFETIME, CacheConfig.DEFAULT_HEURISTIC_LIFETIME))
			.asEagerSingleton();

		// Google Maps API
		bind(String.class)
			.annotatedWith(Names.named(ServletInitOptions.MAPS_CLIENT_ID))
			.toProvider(new StringProvider(ctx, ServletInitOptions.MAPS_CLIENT_ID, null))
			.asEagerSingleton();
		bind(String.class)
			.annotatedWith(Names.named(ServletInitOptions.MAPS_CLIENT_KEY))
			.toProvider(new StringProvider(ctx, ServletInitOptions.MAPS_CLIENT_KEY, null))
			.asEagerSingleton();

		// Space-Track
		bind(String.class)
			.annotatedWith(Names.named(ServletInitOptions.SPACETRACK_USERNAME))
			.toProvider(new StringProvider(ctx, ServletInitOptions.SPACETRACK_USERNAME, null))
			.asEagerSingleton();
		bind(String.class)
			.annotatedWith(Names.named(ServletInitOptions.SPACETRACK_PASSWORD))
			.toProvider(new StringProvider(ctx, ServletInitOptions.SPACETRACK_PASSWORD, null))
			.asEagerSingleton();
	}
}
