package com.gba.ws.bean;

/**
 * Provides settings details.
 * 
 * @author Mohan
 * @createdOn Jan 8, 2018 3:45:50 PM
 */
public class SettingsBean {

	private Boolean reminders = false;
	private String lass4u = "";
	private String fitbit = "";
	private String language = "";
	private String temperature = "";
	private String fitbitAccessToken = "";
	private String fitbitRefreshtoken = "";
	private String fitbitAuthRedirectionUrl = "";

	public Boolean isReminders() {
		return reminders;
	}

	public SettingsBean setReminders(Boolean reminders) {
		this.reminders = reminders;
		return this;
	}

	public String getLass4u() {
		return lass4u;
	}

	public SettingsBean setLass4u(String lass4u) {
		this.lass4u = lass4u;
		return this;
	}

	public String getFitbit() {
		return fitbit;
	}

	public SettingsBean setFitbit(String fitbit) {
		this.fitbit = fitbit;
		return this;
	}

	public String getLanguage() {
		return language;
	}

	public SettingsBean setLanguage(String language) {
		this.language = language;
		return this;
	}

	public String getTemperature() {
		return temperature;
	}

	public SettingsBean setTemperature(String temperature) {
		this.temperature = temperature;
		return this;
	}

	public String getFitbitAccessToken() {
		return fitbitAccessToken;
	}

	public SettingsBean setFitbitAccessToken(String fitbitAccessToken) {
		this.fitbitAccessToken = fitbitAccessToken;
		return this;
	}

	public String getFitbitRefreshtoken() {
		return fitbitRefreshtoken;
	}

	public SettingsBean setFitbitRefreshtoken(String fitbitRefreshtoken) {
		this.fitbitRefreshtoken = fitbitRefreshtoken;
		return this;
	}

	public String getFitbitAuthRedirectionUrl() {
		return fitbitAuthRedirectionUrl;
	}

	public SettingsBean setFitbitAuthRedirectionUrl(String fitbitAuthRedirectionUrl) {
		this.fitbitAuthRedirectionUrl = fitbitAuthRedirectionUrl;
		return this;
	}

	@Override
	public String toString() {
		return "SettingsBean [reminders=" + reminders + ", lass4u=" + lass4u + ", fitbit=" + fitbit + ", language="
				+ language + ", temperature=" + temperature + ", fitbitAccessToken=" + fitbitAccessToken
				+ ", fitbitRefreshtoken=" + fitbitRefreshtoken + ", fitbitAuthRedirectionUrl="
				+ fitbitAuthRedirectionUrl + "]";
	}

}
