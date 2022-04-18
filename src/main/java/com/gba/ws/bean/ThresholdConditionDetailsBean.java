/**
 * 
 */
package com.gba.ws.bean;

import javax.persistence.Transient;

import com.gba.ws.model.FitbitUserInfoDto;

/**
 * Provides the user threshold condition details for the provided user
 * identifier.
 * 
 * @author Mohan
 * @createdOn Jan 31, 2018 12:39:55 PM
 */
public class ThresholdConditionDetailsBean {

	private String co = null;
	private String temperature = null;
	private String relativeHumidity = null;
	private String noise = null;
	private String light = null;
	private String pm = null;
	private String steps = null;
	private String heartRate = null;
	private String sleep = null;
	private int lass4UCount = 0;
	private int fitbitCount = 0;
	private FitbitUserInfoDto fitbitUserInfo = null;
	private String restingHeartRate = null;
	private String intraDayHeartRate = null;
	@Transient
	private boolean disconnected;

	public String getCo() {
		return this.co;
	}

	public boolean isDisconnected() {
		return disconnected;
	}

	public void setDisconnected(boolean disconnected) {
		this.disconnected = disconnected;
	}

	public ThresholdConditionDetailsBean setCo(String co) {
		this.co = co;
		return this;
	}

	public String getTemperature() {
		return this.temperature;
	}

	public ThresholdConditionDetailsBean setTemperature(String temperature) {
		this.temperature = temperature;
		return this;
	}

	public String getRelativeHumidity() {
		return this.relativeHumidity;
	}

	public ThresholdConditionDetailsBean setRelativeHumidity(String relativeHumidity) {
		this.relativeHumidity = relativeHumidity;
		return this;
	}

	public String getNoise() {
		return this.noise;
	}

	public ThresholdConditionDetailsBean setNoise(String noise) {
		this.noise = noise;
		return this;
	}

	public String getLight() {
		return this.light;
	}

	public ThresholdConditionDetailsBean setLight(String light) {
		this.light = light;
		return this;
	}

	public String getPm() {
		return this.pm;
	}

	public ThresholdConditionDetailsBean setPm(String pm) {
		this.pm = pm;
		return this;
	}

	public String getSteps() {
		return this.steps;
	}

	public ThresholdConditionDetailsBean setSteps(String steps) {
		this.steps = steps;
		return this;
	}

	public String getHeartRate() {
		return this.heartRate;
	}

	public ThresholdConditionDetailsBean setHeartRate(String heartRate) {
		this.heartRate = heartRate;
		return this;
	}

	public String getSleep() {
		return this.sleep;
	}

	public ThresholdConditionDetailsBean setSleep(String sleep) {
		this.sleep = sleep;
		return this;
	}

	public int getLass4UCount() {
		return this.lass4UCount;
	}

	public ThresholdConditionDetailsBean setLass4UCount(int lass4uCount) {
		this.lass4UCount = lass4uCount;
		return this;
	}

	public int getFitbitCount() {
		return this.fitbitCount;
	}

	public ThresholdConditionDetailsBean setFitbitCount(int fitbitCount) {
		this.fitbitCount = fitbitCount;
		return this;
	}

	public FitbitUserInfoDto getFitbitUserInfo() {
		return this.fitbitUserInfo;
	}

	public ThresholdConditionDetailsBean setFitbitUserInfo(FitbitUserInfoDto fitbitUserInfo) {
		this.fitbitUserInfo = fitbitUserInfo;
		return this;
	}

	public String getRestingHeartRate() {
		return this.restingHeartRate;
	}

	public ThresholdConditionDetailsBean setRestingHeartRate(String restingHeartRate) {
		this.restingHeartRate = restingHeartRate;
		return this;
	}

	public String getIntraDayHeartRate() {
		return this.intraDayHeartRate;
	}

	public ThresholdConditionDetailsBean setIntraDayHeartRate(String intraDayHeartRate) {
		this.intraDayHeartRate = intraDayHeartRate;
		return this;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ThresholdConditionDetailsBean [co=");
		builder.append(this.co);
		builder.append(", temperature=");
		builder.append(this.temperature);
		builder.append(", relativeHumidity=");
		builder.append(this.relativeHumidity);
		builder.append(", noise=");
		builder.append(this.noise);
		builder.append(", light=");
		builder.append(this.light);
		builder.append(", pm=");
		builder.append(this.pm);
		builder.append(", steps=");
		builder.append(this.steps);
		builder.append(", heartRate=");
		builder.append(this.heartRate);
		builder.append(", sleep=");
		builder.append(this.sleep);
		builder.append(", lass4UCount=");
		builder.append(this.lass4UCount);
		builder.append(", fitbitCount=");
		builder.append(this.fitbitCount);
		builder.append(", fitbitUserInfo=");
		builder.append(this.fitbitUserInfo);
		builder.append(", restingHeartRate=");
		builder.append(this.restingHeartRate);
		builder.append(", intraDayHeartRate=");
		builder.append(this.intraDayHeartRate);
		builder.append("]");
		return builder.toString();
	}

}
