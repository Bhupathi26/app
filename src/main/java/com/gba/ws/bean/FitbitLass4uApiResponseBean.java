/**
 * 
 */
package com.gba.ws.bean;

/**
 * Provides Lass4u and Fitbit sensor api's status
 * 
 * @author Mohan
 * @createdOn Jan 16, 2018 8:01:15 PM
 */
public class FitbitLass4uApiResponseBean {

	private boolean lass4uSensorStatus = false;
	private boolean fitbitSensorStatus = false;
	private String userId = "";
	private String deviceToken = "";
	private String language = "";
	private String timeZone = "";
	private String deviceType = "";

	public boolean getLass4uSensorStatus() {
		return this.lass4uSensorStatus;
	}

	public FitbitLass4uApiResponseBean setLass4uSensorStatus(boolean lass4uSensorStatus) {
		this.lass4uSensorStatus = lass4uSensorStatus;
		return this;
	}

	public boolean getFitbitSensorStatus() {
		return this.fitbitSensorStatus;
	}

	public FitbitLass4uApiResponseBean setFitbitSensorStatus(boolean fitbitSensorStatus) {
		this.fitbitSensorStatus = fitbitSensorStatus;
		return this;
	}

	public String getUserId() {
		return this.userId;
	}

	public FitbitLass4uApiResponseBean setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	public String getDeviceToken() {
		return this.deviceToken;
	}

	public FitbitLass4uApiResponseBean setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
		return this;
	}

	public String getLanguage() {
		return this.language;
	}

	public FitbitLass4uApiResponseBean setLanguage(String language) {
		this.language = language;
		return this;
	}

	public String getTimeZone() {
		return this.timeZone;
	}

	public FitbitLass4uApiResponseBean setTimeZone(String timeZone) {
		this.timeZone = timeZone;
		return this;
	}

	public String getDeviceType() {
		return this.deviceType;
	}

	public FitbitLass4uApiResponseBean setDeviceType(String deviceType) {
		this.deviceType = deviceType;
		return this;
	}

	@Override
	public String toString() {
		return "FitbitLass4uApiResponseBean [lass4uSensorStatus=" + lass4uSensorStatus + ", fitbitSensorStatus="
				+ fitbitSensorStatus + ", userId=" + userId + ", deviceToken=" + deviceToken + ", language=" + language
				+ ", timeZone=" + timeZone + ", deviceType=" + deviceType + "]";
	}

}
