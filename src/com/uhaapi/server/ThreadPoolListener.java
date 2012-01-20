package com.uhaapi.server;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

public class ThreadPoolListener implements ServletContextListener, RejectedExecutionHandler {
	public static final String THREAD_POOL = ExecutorService.class.getName();

	public final Logger log = Logger.getLogger(getClass());

	@Override
	public void contextInitialized(ServletContextEvent evt) {
		ServletContext ctx = evt.getServletContext();

		ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1, this);

		// TODO: Make this configurable

		ctx.setAttribute(THREAD_POOL, executor);
	}
	@Override
	public void contextDestroyed(ServletContextEvent evt) {
		ServletContext ctx = evt.getServletContext();

		ExecutorService executor = (ExecutorService)ctx.getAttribute(THREAD_POOL);
		ctx.removeAttribute(THREAD_POOL);

		if(executor != null) {
			try {
				executor.shutdown();
				if(!executor.awaitTermination(10, TimeUnit.SECONDS)) {
					Collection<Runnable> unfinished = executor.shutdownNow();
					log.error(String.format(
							"Forced thread pool shutdown (%d uncompleted tasks)",
							unfinished.size()
						));
				}
				else {
					log.info("Threadpool shutdown successful");
				}
			}
			catch(InterruptedException ex) {
				log.error("Thread pool shutdown interrupted", ex);
			}
		}
	}

	@Override
	public void rejectedExecution(Runnable arg0, ThreadPoolExecutor arg1) {
	}
}
