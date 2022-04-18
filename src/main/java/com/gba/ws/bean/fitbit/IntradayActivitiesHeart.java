/**
 * 
 */
package com.gba.ws.bean.fitbit;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mohan
 * @createdOn Jan 12, 2018 12:05:27 PM
 */
public class IntradayActivitiesHeart {

	private String dateTime = "";
	private List<IntradayHeartRateZones> heartRateZones = new ArrayList<>();
	private List<String> customHeartRateZones = new ArrayList<>();
	private String value = "";

	public String getDateTime() {
		return this.dateTime;
	}

	public IntradayActivitiesHeart setDateTime(String dateTime) {
		this.dateTime = dateTime;
		return this;
	}

	public List<IntradayHeartRateZones> getHeartRateZones() {
		return this.heartRateZones;
	}

	public IntradayActivitiesHeart setHeartRateZones(List<IntradayHeartRateZones> heartRateZones) {
		this.heartRateZones = heartRateZones;
		return this;
	}

	public List<String> getCustomHeartRateZones() {
		return this.customHeartRateZones;
	}

	public IntradayActivitiesHeart setCustomHeartRateZones(List<String> customHeartRateZones) {
		this.customHeartRateZones = customHeartRateZones;
		return this;
	}

	public String getValue() {
		return this.value;
	}

	public IntradayActivitiesHeart setValue(String value) {
		this.value = value;
		return this;
	}

}
