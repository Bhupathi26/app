/**
 * 
 */
package com.gba.ws.bean.fitbit;

/**
 * @author Mohan
 * @createdOn Jan 12, 2018 12:03:56 PM
 */
public class IntradayHeartRateZones {

	private String min = "";
	private String minutes = "";
	private String max = "";
	private String name = "";
	private String caloriesOut = "";

	public String getMin() {
		return this.min;
	}

	public IntradayHeartRateZones setMin(String min) {
		this.min = min;
		return this;
	}

	public String getMinutes() {
		return this.minutes;
	}

	public IntradayHeartRateZones setMinutes(String minutes) {
		this.minutes = minutes;
		return this;
	}

	public String getMax() {
		return this.max;
	}

	public IntradayHeartRateZones setMax(String max) {
		this.max = max;
		return this;
	}

	public String getName() {
		return this.name;
	}

	public IntradayHeartRateZones setName(String name) {
		this.name = name;
		return this;
	}

	public String getCaloriesOut() {
		return this.caloriesOut;
	}

	public IntradayHeartRateZones setCaloriesOut(String caloriesOut) {
		this.caloriesOut = caloriesOut;
		return this;
	}

}
