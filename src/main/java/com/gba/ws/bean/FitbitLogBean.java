package com.gba.ws.bean;

import javax.persistence.Transient;
import com.gba.ws.model.FitbitUserInfoDto;

public class FitbitLogBean {
	private String steps = null;
	private String heartRate = null;
	private String sleep = null;

	private int fitbitCount = 0;
	private FitbitUserInfoDto fitbitUserInfo = null;
	private String restingHeartRate = null;
	private String intraDayHeartRate = null;
	@Transient
	private boolean disconnected;

	public boolean isDisconnected() {
		return disconnected;
	}

	public void setDisconnected(boolean disconnected) {
		this.disconnected = disconnected;
	}

	public String getSteps() {
		return this.steps;
	}

	public FitbitLogBean setSteps(String steps) {
		this.steps = steps;
		return this;
	}

	public String getHeartRate() {
		return this.heartRate;
	}

	public FitbitLogBean setHeartRate(String heartRate) {
		this.heartRate = heartRate;
		return this;
	}

	public String getSleep() {
		return this.sleep;
	}

	public FitbitLogBean setSleep(String sleep) {
		this.sleep = sleep;
		return this;
	}

	public int getFitbitCount() {
		return this.fitbitCount;
	}

	public FitbitLogBean setFitbitCount(int fitbitCount) {
		this.fitbitCount = fitbitCount;
		return this;
	}

	public FitbitUserInfoDto getFitbitUserInfo() {
		return this.fitbitUserInfo;
	}

	public FitbitLogBean setFitbitUserInfo(FitbitUserInfoDto fitbitUserInfo) {
		this.fitbitUserInfo = fitbitUserInfo;
		return this;
	}

	public String getRestingHeartRate() {
		return this.restingHeartRate;
	}

	public FitbitLogBean setRestingHeartRate(String restingHeartRate) {
		this.restingHeartRate = restingHeartRate;
		return this;
	}

	public String getIntraDayHeartRate() {
		return this.intraDayHeartRate;
	}

	public FitbitLogBean setIntraDayHeartRate(String intraDayHeartRate) {
		this.intraDayHeartRate = intraDayHeartRate;
		return this;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[steps=");
		builder.append(this.steps);
		builder.append(", heartRate=");
		builder.append(this.heartRate);
		builder.append(", sleep=");
		builder.append(this.sleep);
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
