package com.gba.ws.config;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.log4j.Logger;

/**
 * Implements {@link Filter} interface, applies filter to the incoming requests.
 * 
 * @author Mohan
 * @createdOn Jan 8, 2018 3:49:16 PM
 */
public class AppFilter implements Filter {

	private static final Logger LOGGER = Logger.getLogger(AppFilter.class);
	
	@Override
	public void destroy() {
		LOGGER.info("INFO: AppFilter - destroy()");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		filterChain.doFilter(request, response);
		LOGGER.info("INFO: AppFilter - doFilter()");
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		LOGGER.info("INFO: AppFilter - init()");
	}

}
