/**
 * 
 */
package com.gba.ws.bean.fitbit;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Mohan
 * @createdOn Jan 12, 2018 12:01:29 PM
 */
public class RestingHeartRateBean {

	@SerializedName("activities-heart-intraday")
	@Expose
	private ActivitiesHeartIntraday activitiesHeartIntraday = new ActivitiesHeartIntraday();

	@SerializedName("activities-heart")
	@Expose
	private List<ActivitiesHeart> activitiesHeart = new ArrayList<>();

	public ActivitiesHeartIntraday getActivitiesHeartIntraday() {
		return this.activitiesHeartIntraday;
	}

	public RestingHeartRateBean setActivitiesHeartIntraday(ActivitiesHeartIntraday activitiesHeartIntraday) {
		this.activitiesHeartIntraday = activitiesHeartIntraday;
		return this;
	}

	public List<ActivitiesHeart> getActivitiesHeart() {
		return this.activitiesHeart;
	}

	public RestingHeartRateBean setActivitiesHeart(List<ActivitiesHeart> activitiesHeart) {
		this.activitiesHeart = activitiesHeart;
		return this;
	}

}
