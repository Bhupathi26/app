/**
 * 
 */
package com.gba.ws.bean;

/**
 * Provides Fitbit error details.
 * 
 * @author Mohan
 * @createdOn Dec 11, 2017 1:29:33 PM
 */
public class FitbitErrorBean {

	private String errorType;
	private String message;

	public String getErrorType() {
		return this.errorType;
	}

	public FitbitErrorBean setErrorType(String errorType) {
		this.errorType = errorType;
		return this;
	}

	public String getMessage() {
		return this.message;
	}

	public FitbitErrorBean setMessage(String message) {
		this.message = message;
		return this;
	}

	@Override
	public String toString() {
		return "FitbitErrorBean [errorType=" + errorType + ", message=" + message + "]";
	}

}
