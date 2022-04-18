/**
 * 
 */
package com.gba.ws.bean.fitbit;

/**
 * @author Mohan
 * @createdOn Jan 12, 2018 11:55:08 AM
 */
public class HeartRateZones {

	private String min = "";
	private String minutes = "";
	private String max = "";
	private String name = "";
	private String caloriesOut = "";

	public String getMin() {
		return this.min;
	}

	public HeartRateZones setMin(String min) {
		this.min = min;
		return this;
	}

	public String getMinutes() {
		return this.minutes;
	}

	public HeartRateZones setMinutes(String minutes) {
		this.minutes = minutes;
		return this;
	}

	public String getMax() {
		return this.max;
	}

	public HeartRateZones setMax(String max) {
		this.max = max;
		return this;
	}

	public String getName() {
		return this.name;
	}

	public HeartRateZones setName(String name) {
		this.name = name;
		return this;
	}

	public String getCaloriesOut() {
		return this.caloriesOut;
	}

	public HeartRateZones setCaloriesOut(String caloriesOut) {
		this.caloriesOut = caloriesOut;
		return this;
	}

}
