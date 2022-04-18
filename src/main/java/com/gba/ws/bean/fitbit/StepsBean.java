package com.gba.ws.bean.fitbit;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Mohan
 * @createdOn Jan 12, 2018 11:36:43 AM
 */
public class StepsBean {

	@SerializedName("activities-steps")
	@Expose
	private List<ActivitiesSteps> activitiesSteps = new ArrayList<>();

	@SerializedName("activities-steps-intraday")
	@Expose
	private ActivitiesStepsIntraday activitiesStepsIntraday = new ActivitiesStepsIntraday();

	public List<ActivitiesSteps> getActivitiesSteps() {
		return this.activitiesSteps;
	}

	public StepsBean setActivitiesSteps(List<ActivitiesSteps> activitiesSteps) {
		this.activitiesSteps = activitiesSteps;
		return this;
	}

	public ActivitiesStepsIntraday getActivitiesStepsIntraday() {
		return this.activitiesStepsIntraday;
	}

	public StepsBean setActivitiesStepsIntraday(ActivitiesStepsIntraday activitiesStepsIntraday) {
		this.activitiesStepsIntraday = activitiesStepsIntraday;
		return this;
	}

}
