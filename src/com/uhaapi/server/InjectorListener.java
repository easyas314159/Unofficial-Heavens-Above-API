package com.uhaapi.server;

import java.util.Collection;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;

public class InjectorListener implements ServletContextListener {
	public static final String INJECTOR = Injector.class.getName();

	private final Logger log = Logger.getLogger(getClass());

	@Override
	public void contextInitialized(ServletContextEvent evt) {
		ServletContext ctx = evt.getServletContext();
		try {
			Injector injector = getInjector(ctx);
			ctx.setAttribute(INJECTOR, injector);
		}
		catch(Throwable t) {
			log.fatal("Dependency Injection Failed!", t);
		}
	}
	@Override
	public void contextDestroyed(ServletContextEvent evt) {
		ServletContext ctx = evt.getServletContext();

		//Injector injector = (Injector)ctx.getAttribute(INJECTOR);
		ctx.removeAttribute(INJECTOR);
	}

	private Injector getInjector(ServletContext context) {
		Stage stage = (Stage)context.getAttribute(ModuleListener.STAGE);
		Collection<Module> modules = (Collection<Module>)context.getAttribute(ModuleListener.MODULES);

		return Guice.createInjector(stage, modules);
	}
}
