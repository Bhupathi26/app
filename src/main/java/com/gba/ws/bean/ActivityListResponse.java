package com.gba.ws.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides activities information in response.
 * 
 * @author Mohan
 * @createdOn Jan 8, 2018 3:33:49 PM
 */
public class ActivityListResponse {

	private ErrorBean error = new ErrorBean();
	private List<ActivityListBean> activities = new ArrayList<>();

	public ErrorBean getError() {
		return error;
	}

	public ActivityListResponse setError(ErrorBean error) {
		this.error = error;
		return this;
	}

	public List<ActivityListBean> getActivities() {
		return activities;
	}

	public ActivityListResponse setActivities(List<ActivityListBean> activities) {
		this.activities = activities;
		return this;
	}

	@Override
	public String toString() {
		return "ActivityListResponse [error=" + error + ", activities=" + activities + "]";
	}

}
