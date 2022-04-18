/**
 * 
 */
package com.gba.ws.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides Fitbit error information in response.
 * 
 * @author Mohan
 * @createdOn Dec 11, 2017 1:27:10 PM
 */
public class FitbitErrorResponse {

	private List<FitbitErrorBean> errors = new ArrayList<>();
	private boolean success = false;

	public List<FitbitErrorBean> getErrors() {
		return this.errors;
	}

	public FitbitErrorResponse setErrors(List<FitbitErrorBean> errors) {
		this.errors = errors;
		return this;
	}

	public boolean getSuccess() {
		return this.success;
	}

	public FitbitErrorResponse setSuccess(boolean success) {
		this.success = success;
		return this;
	}

	@Override
	public String toString() {
		return "FitbitErrorResponse [errors=" + errors + ", success=" + success + "]";
	}

}
