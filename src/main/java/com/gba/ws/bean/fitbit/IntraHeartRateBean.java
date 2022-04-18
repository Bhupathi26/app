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
 * @createdOn Jan 12, 2018 12:08:20 PM
 */
public class IntraHeartRateBean {

	@SerializedName("activities-heart")
	@Expose
	private List<IntradayActivitiesHeart> activitiesHeart = new ArrayList<>();

	@SerializedName("activities-heart-intraday")
	@Expose
	private IntradayActivitiesHeartIntraday activitiesHeartIntraday = new IntradayActivitiesHeartIntraday();

	public List<IntradayActivitiesHeart> getActivitiesHeart() {
		return this.activitiesHeart;
	}

	public IntraHeartRateBean setActivitiesHeart(List<IntradayActivitiesHeart> activitiesHeart) {
		this.activitiesHeart = activitiesHeart;
		return this;
	}

	public IntradayActivitiesHeartIntraday getActivitiesHeartIntraday() {
		return this.activitiesHeartIntraday;
	}

	public IntraHeartRateBean setActivitiesHeartIntraday(IntradayActivitiesHeartIntraday activitiesHeartIntraday) {
		this.activitiesHeartIntraday = activitiesHeartIntraday;
		return this;
	}

}
