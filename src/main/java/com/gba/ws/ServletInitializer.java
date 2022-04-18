
package com.gba.ws;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

/**
 * 
 * Extends {@link SpringBootServletInitializer} class initialize the servlets.
 * 
 * @author Mohan
 * @createdOn Jan 8, 2018 3:26:41 PM
 */
public class ServletInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(AppLabApplication.class);
	}

}
