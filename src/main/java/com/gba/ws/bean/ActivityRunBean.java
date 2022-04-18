package com.gba.ws.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides activity run details.
 * 
 * @author Mohan
 * @createdOn Jan 8, 2018 3:34:30 PM
 */
public class ActivityRunBean {

	private String activityId = "";
	private List<RunBean> runs = new ArrayList<>();

	public String getActivityId() {
		return activityId;
	}

	public ActivityRunBean setActivityId(String activityId) {
		this.activityId = activityId;
		return this;
	}

	public List<RunBean> getRuns() {
		return runs;
	}

	public ActivityRunBean setRuns(List<RunBean> runs) {
		this.runs = runs;
		return this;
	}

	@Override
	public String toString() {
		return "ActivityRunBean [activityId=" + activityId + ", runs=" + runs + "]";
	}

}
