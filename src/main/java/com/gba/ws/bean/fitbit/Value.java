/**
 * 
 */
package com.gba.ws.bean.fitbit;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mohan
 * @createdOn Jan 12, 2018 11:59:15 AM
 */
public class Value {

	private int restingHeartRate = 0;
	private List<HeartRateZones> heartRateZones = new ArrayList<>();
	private List<String> customHeartRateZones = new ArrayList<>();

	public int getRestingHeartRate() {
		return this.restingHeartRate;
	}

	public Value setRestingHeartRate(int restingHeartRate) {
		this.restingHeartRate = restingHeartRate;
		return this;
	}

	public List<HeartRateZones> getHeartRateZones() {
		return this.heartRateZones;
	}

	public Value setHeartRateZones(List<HeartRateZones> heartRateZones) {
		this.heartRateZones = heartRateZones;
		return this;
	}

	public List<String> getCustomHeartRateZones() {
		return this.customHeartRateZones;
	}

	public Value setCustomHeartRateZones(List<String> customHeartRateZones) {
		this.customHeartRateZones = customHeartRateZones;
		return this;
	}

}
