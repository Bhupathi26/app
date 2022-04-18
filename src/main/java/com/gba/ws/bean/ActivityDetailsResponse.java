package com.gba.ws.bean;

/**
 * Provides activity details in response.
 * 
 * @author Mohan
 * @createdOn Jan 8, 2018 3:31:52 PM
 */
public class ActivityDetailsResponse {

	private ErrorBean error = new ErrorBean();
	private ActivityBean activity = new ActivityBean();

	public ErrorBean getError() {
		return error;
	}

	public ActivityDetailsResponse setError(ErrorBean error) {
		this.error = error;
		return this;
	}

	public ActivityBean getActivity() {
		return activity;
	}

	public ActivityDetailsResponse setActivity(ActivityBean activity) {
		this.activity = activity;
		return this;
	}

	@Override
	public String toString() {
		return "ActivityDetailsResponse [error=" + error + ", activity=" + activity + "]";
	}

}
