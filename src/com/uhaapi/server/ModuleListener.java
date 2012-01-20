package com.uhaapi.server;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.google.inject.Module;
import com.google.inject.Stage;
import com.uhaapi.server.modules.CoreModule;
import com.uhaapi.server.modules.GsonModule;
import com.uhaapi.server.modules.MapsServicesModule;
import com.uhaapi.server.util.ParamUtils;

public class ModuleListener implements ServletContextListener {
	public static final String MODULES = "GuiceModulesList";
	public static final String STAGE = "GuiceStage";

	@Override
	public void contextInitialized(ServletContextEvent evt) {
		final ServletContext ctx = evt.getServletContext();

		Stage stage = ParamUtils.asEnum(
				Stage.class,
				ctx.getInitParameter(ServletInitOptions.APP_STAGE),
				Stage.DEVELOPMENT
			);

		Collection<Module> modules = new ArrayList<Module>() {{
			add(new CoreModule(ctx));
			add(new GsonModule());
			add(new MapsServicesModule());
		}};

		ctx.setAttribute(STAGE, stage);
		ctx.setAttribute(MODULES, modules);
	}
	@Override
	public void contextDestroyed(ServletContextEvent evt) {
		final ServletContext ctx = evt.getServletContext();

		ctx.removeAttribute(STAGE);
		ctx.removeAttribute(MODULES);
	}
}
