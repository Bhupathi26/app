package com.gba.ws.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Provides user activities details.
 * 
 * @author Mohan
 * @createdOn Jan 8, 2018 5:58:48 PM
 */
@Entity
@Table(name = "user_activities")
@NamedQueries(value = {

		@NamedQuery(name = "UserActivitiesDto.findByUserActivityId", query = "FROM UserActivitiesDto UADTO"
				+ " WHERE UADTO.userActivityId =:userActivityId"),

		@NamedQuery(name = "UserActivitiesDto.findByUserIdConditionId", query = "FROM UserActivitiesDto UADTO"
				+ " WHERE UADTO.conditionId =:conditionId and UADTO.userId=:userId and created_on>:startActivityTime order by created_on desc"),

		@NamedQuery(name = "UserActivitiesDto.findByUserIdNUserStudiesId", query = "FROM UserActivitiesDto UADTO"
				+ " WHERE UADTO.userId =:userId AND UADTO.userStudiesId =:userStudiesId"
				+ " ORDER BY UADTO.conditionId"),

		@NamedQuery(name = "UserActivitiesDto.findByConditionId", query = "FROM UserActivitiesDto UADTO"
				+ " WHERE UADTO.conditionId =:conditionId AND UADTO.userStudiesId =:userStudiesId"),

		@NamedQuery(name = "UserActivitiesDto.findByUserIdNConditionId", query = "FROM UserActivitiesDto UADTO"
				+ " WHERE UADTO.userId =:userId AND UADTO.conditionId =:conditionId AND UADTO.userStudiesId =:userStudiesId order by created_on desc"),

		@NamedQuery(name = "UserActivitiesDto.findByUserId", query = "FROM UserActivitiesDto UADTO"
				+ " WHERE UADTO.userId =:userId AND UADTO.userStudiesId =:userStudiesId"),

		@NamedQuery(name = "UserActivitiesDto.findByUserIdNUserStudiesIdNActivitySubType", query = "FROM UserActivitiesDto UADTO"
				+ " WHERE UADTO.userId =:userId AND UADTO.userStudiesId =:userStudiesId AND UADTO.conditionId IN ("
				+ "SELECT ACDTO.conditionId FROM ActivityConditionDto ACDTO"
				+ " WHERE ACDTO.status=true AND ACDTO.activitySubType =:activitySubType) AND UADTO.conditionId IN ("
				+ "SELECT TCDTO.conditionId FROM TemporalConditionDto TCDTO"
				+ " WHERE TCDTO.endDate >= DATE(:endDate))"), })
public class UserActivitiesDto implements Serializable {

	private static final long serialVersionUID = 8052044149247220197L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "user_activity_id")
	private int userActivityId;

	@Column(name = "user_id")
	private int userId;

	@Column(name = "user_studies_id")
	private int userStudiesId;

	@Column(name = "activity_status")
	private String activityStatus;

	@Column(name = "condition_id")
	private int conditionId;

	@Column(name = "activity_run_id")
	private int activityRunId;

	@Column(name = "total_count")
	private int totalCount = 0;

	@Column(name = "completed_count")
	private int completedCount = 0;

	@Column(name = "missed_count")
	private int missedCount = 0;

	@Column(name = "last_completed_run_id")
	private int lastCompletedRunId = 0;

	@Column(name = "last_completed_date")
	private String lastCompletedDate;

	@Column(name = "current_run_date")
	private String currentRunDate;

	@Column(name = "created_on")
	private String createdOn;

	@Column(name = "modified_on")
	private String modifiedOn;
	
	@Column(name="expire_notification_sent", columnDefinition="TINYINT(1)")
	private Boolean expireNotificationSent = false;

	public int getUserActivityId() {
		return userActivityId;
	}

	public UserActivitiesDto setUserActivityId(int userActivityId) {
		this.userActivityId = userActivityId;
		return this;
	}

	public int getUserId() {
		return userId;
	}

	public UserActivitiesDto setUserId(int userId) {
		this.userId = userId;
		return this;
	}

	public int getUserStudiesId() {
		return userStudiesId;
	}

	public UserActivitiesDto setUserStudiesId(int userStudiesId) {
		this.userStudiesId = userStudiesId;
		return this;
	}

	public String getActivityStatus() {
		return activityStatus;
	}

	public UserActivitiesDto setActivityStatus(String activityStatus) {
		this.activityStatus = activityStatus;
		return this;
	}

	public int getConditionId() {
		return conditionId;
	}

	public UserActivitiesDto setConditionId(int conditionId) {
		this.conditionId = conditionId;
		return this;
	}

	public int getActivityRunId() {
		return activityRunId;
	}

	public UserActivitiesDto setActivityRunId(int activityRunId) {
		this.activityRunId = activityRunId;
		return this;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public UserActivitiesDto setTotalCount(int totalCount) {
		this.totalCount = totalCount;
		return this;
	}

	public int getCompletedCount() {
		return completedCount;
	}

	public UserActivitiesDto setCompletedCount(int completedCount) {
		this.completedCount = completedCount;
		return this;
	}

	public int getMissedCount() {
		return missedCount;
	}

	public UserActivitiesDto setMissedCount(int missedCount) {
		this.missedCount = missedCount;
		return this;
	}

	public int getLastCompletedRunId() {
		return lastCompletedRunId;
	}

	public UserActivitiesDto setLastCompletedRunId(int lastCompletedRunId) {
		this.lastCompletedRunId = lastCompletedRunId;
		return this;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public UserActivitiesDto setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
		return this;
	}

	public String getModifiedOn() {
		return modifiedOn;
	}

	public UserActivitiesDto setModifiedOn(String modifiedOn) {
		this.modifiedOn = modifiedOn;
		return this;
	}

	public String getLastCompletedDate() {
		return lastCompletedDate;
	}

	public UserActivitiesDto setLastCompletedDate(String lastCompletedDate) {
		this.lastCompletedDate = lastCompletedDate;
		return this;
	}

	public String getCurrentRunDate() {
		return currentRunDate;
	}

	public UserActivitiesDto setCurrentRunDate(String currentRunDate) {
		this.currentRunDate = currentRunDate;
		return this;
	}

	public Boolean getExpireNotificationSent() {
		return expireNotificationSent;
	}

	public void setExpireNotificationSent(Boolean expireNotificationSent) {
		this.expireNotificationSent = expireNotificationSent;
	}

	@Override
	public String toString() {
		return "UserActivitiesDto [userActivityId=" + userActivityId + ", userId=" + userId + ", userStudiesId="
				+ userStudiesId + ", activityStatus=" + activityStatus + ", conditionId=" + conditionId
				+ ", activityRunId=" + activityRunId + ", totalCount=" + totalCount + ", completedCount="
				+ completedCount + ", missedCount=" + missedCount + ", lastCompletedRunId=" + lastCompletedRunId
				+ ", lastCompletedDate=" + lastCompletedDate + ", currentRunDate=" + currentRunDate + ", createdOn="
				+ createdOn + ", modifiedOn=" + modifiedOn + ", expireNotificationSent=" + expireNotificationSent + "]";
	}

}
