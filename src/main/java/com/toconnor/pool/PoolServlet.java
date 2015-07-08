package com.toconnor.pool;

import javax.servlet.ServletException;

import com.toconnor.pool.data.Bootstrap;
import com.vaadin.server.GAEVaadinServlet;

/**
 * PoolServlet
 */
public class PoolServlet extends GAEVaadinServlet   //TODO CHANGE FOR LOCAL TESTING vs. GAE!  VaadinServlet <--> GAEVaadinServlet
{
	private final PoolUIProvider provider = new PoolUIProvider();

	static
	{
		//perform any one time system initialization
		Bootstrap.init();
	}

	@Override
	protected void servletInitialized() throws ServletException
	{
		getService().addSessionInitListener(provider);
	}
}
