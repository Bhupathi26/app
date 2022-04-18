/**
 * 
 */
package com.gba.ws.bean;

/**
 * Provides user activity push notification information
 * 
 * @author Mohan
 * @createdOn Jan 16, 2018 12:32:49 PM
 */
public class PushNotificationBean {

	private String conditionId = "";
	private String activityId = "";
	private String studyId = "";
	private String currentRunId = "";
	private String language = "";
	private String userTimeZone = "";
	private String userId = "";
	private String deviceToken = "";
	private String deviceType = "";
	private String activityName = "";
	private String userActivityRunId = "";
	private String expiryMinutes = "";
	private String activityStatus = "";

	public String getConditionId() {
		return this.conditionId;
	}

	public PushNotificationBean setConditionId(String conditionId) {
		this.conditionId = conditionId;
		return this;
	}

	public String getActivityId() {
		return this.activityId;
	}

	public PushNotificationBean setActivityId(String activityId) {
		this.activityId = activityId;
		return this;
	}

	public String getStudyId() {
		return this.studyId;
	}

	public PushNotificationBean setStudyId(String studyId) {
		this.studyId = studyId;
		return this;
	}

	public String getCurrentRunId() {
		return this.currentRunId;
	}

	public PushNotificationBean setCurrentRunId(String currentRunId) {
		this.currentRunId = currentRunId;
		return this;
	}

	public String getLanguage() {
		return this.language;
	}

	public PushNotificationBean setLanguage(String language) {
		this.language = language;
		return this;
	}

	public String getUserTimeZone() {
		return this.userTimeZone;
	}

	public PushNotificationBean setUserTimeZone(String userTimeZone) {
		this.userTimeZone = userTimeZone;
		return this;
	}

	public String getUserId() {
		return this.userId;
	}

	public PushNotificationBean setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	public String getDeviceToken() {
		return this.deviceToken;
	}

	public PushNotificationBean setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
		return this;
	}

	public String getDeviceType() {
		return this.deviceType;
	}

	public PushNotificationBean setDeviceType(String deviceType) {
		this.deviceType = deviceType;
		return this;
	}

	public String getActivityName() {
		return this.activityName;
	}

	public PushNotificationBean setActivityName(String activityName) {
		this.activityName = activityName;
		return this;
	}

	public String getUserActivityRunId() {
		return this.userActivityRunId;
	}

	public PushNotificationBean setUserActivityRunId(String userActivityRunId) {
		this.userActivityRunId = userActivityRunId;
		return this;
	}

	public String getExpiryMinutes() {
		return this.expiryMinutes;
	}

	public PushNotificationBean setExpiryMinutes(String expiryMinutes) {
		this.expiryMinutes = expiryMinutes;
		return this;
	}

	public String getActivityStatus() {
		return this.activityStatus;
	}

	public PushNotificationBean setActivityStatus(String activityStatus) {
		this.activityStatus = activityStatus;
		return this;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PushNotificationBean [conditionId=");
		builder.append(this.conditionId);
		builder.append(", activityId=");
		builder.append(this.activityId);
		builder.append(", studyId=");
		builder.append(this.studyId);
		builder.append(", currentRunId=");
		builder.append(this.currentRunId);
		builder.append(", language=");
		builder.append(this.language);
		builder.append(", userTimeZone=");
		builder.append(this.userTimeZone);
		builder.append(", userId=");
		builder.append(this.userId);
		builder.append(", deviceToken=");
		builder.append(this.deviceToken);
		builder.append(", deviceType=");
		builder.append(this.deviceType);
		builder.append(", activityName=");
		builder.append(this.activityName);
		builder.append(", userActivityRunId=");
		builder.append(this.userActivityRunId);
		builder.append(", expiryMinutes=");
		builder.append(this.expiryMinutes);
		builder.append(", activityStatus=");
		builder.append(this.activityStatus);
		builder.append("]");
		return builder.toString();
	}

}
