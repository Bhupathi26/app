/**
 * 
 */
package com.gba.ws.util;

import java.io.IOException;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.HttpRequestWrapper;

/**
 * Implements {@link ClientHttpRequestInterceptor} interface, exchange the
 * header request details.
 * 
 * @author Mohan
 * @createdOn Jan 11, 2018 11:40:25 AM
 */
public class HeaderRequestInterceptor implements ClientHttpRequestInterceptor {

	private final String headerName;
	private final String headerValue;

	/**
	 * Exchange the header request details.
	 * 
	 * @author Mohan
	 * @param headerName
	 *            the header name details
	 * @param headerValue
	 *            the header value details
	 */
	public HeaderRequestInterceptor(String headerName, String headerValue) {
		this.headerName = headerName;
		this.headerValue = headerValue;
	}

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {
		HttpRequest wrapper = new HttpRequestWrapper(request);
		wrapper.getHeaders().set(headerName, headerValue);
		return execution.execute(wrapper, body);
	}
}
