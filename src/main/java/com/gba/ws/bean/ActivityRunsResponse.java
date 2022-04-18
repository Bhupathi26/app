package com.gba.ws.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides activities runs information in response.
 * 
 * @author Mohan
 * @createdOn Jan 8, 2018 3:35:04 PM
 */
public class ActivityRunsResponse {

	private ErrorBean error = new ErrorBean();
	private List<ActivityRunBean> activities = new ArrayList<>();

	public ErrorBean getError() {
		return error;
	}

	public ActivityRunsResponse setError(ErrorBean error) {
		this.error = error;
		return this;
	}

	public List<ActivityRunBean> getActivities() {
		return activities;
	}

	public ActivityRunsResponse setActivities(List<ActivityRunBean> activities) {
		this.activities = activities;
		return this;
	}

	@Override
	public String toString() {
		return "ActivityRunsResponse [error=" + error + ", activities=" + activities + "]";
	}

}
