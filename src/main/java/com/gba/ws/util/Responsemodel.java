/**
 * 
 */
package com.gba.ws.util;

/**
 * Provides information about the request made
 * 
 * @author Mohan
 * @createdOn Jan 30, 2018 2:14:48 PM
 */
public class Responsemodel {

	private int statusCode;
	private String body;

	public int getStatusCode() {
		return this.statusCode;
	}

	public Responsemodel setStatusCode(int statusCode) {
		this.statusCode = statusCode;
		return this;
	}

	public String getBody() {
		return this.body;
	}

	public Responsemodel setBody(String body) {
		this.body = body;
		return this;
	}

	@Override
	public String toString() {
		return "Responsemodel [statusCode=" + this.statusCode + ", body=" + this.body + "]";
	}

}
