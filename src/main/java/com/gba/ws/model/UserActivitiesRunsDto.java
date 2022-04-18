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
 * Provides user activity runs details.
 * 
 * @author Mohan
 * @createdOn Jan 8, 2018 6:00:00 PM
 */
@Entity
@Table(name = "user_activities_runs")
@NamedQueries(value = {

		@NamedQuery(name = "UserActivitiesRunsDto.fetchByUserActivityRunId", query = "FROM UserActivitiesRunsDto UARDTO"
				+ " WHERE UARDTO.userActivityRunId =:userActivityRunId AND UARDTO.userStudiesId =:userStudiesId"),

		@NamedQuery(name = "UserActivitiesRunsDto.fetchByUserIdNConditionIds", query = "FROM UserActivitiesRunsDto UARDTO"
				+ " WHERE UARDTO.userId =:userId AND UARDTO.conditionId IN (:conditionIdList) AND UARDTO.userStudiesId =:userStudiesId"),

		@NamedQuery(name = "UserActivitiesRunsDto.fetchByUserIdNConditionIdNActivityRunId", query = "FROM UserActivitiesRunsDto UARDTO"
				+ " WHERE UARDTO.userId =:userId AND UARDTO.conditionId =:conditionId"
				+ " AND UARDTO.activityRunId =:activityRunId AND UARDTO.userStudiesId =:userStudiesId"),

		@NamedQuery(name = "UserActivitiesRunsDto.fetchByUserIdNConditionId", query = "FROM UserActivitiesRunsDto UARDTO"
				+ " WHERE UARDTO.userId =:userId AND UARDTO.conditionId =:conditionId AND UARDTO.userStudiesId =:userStudiesId"),

		@NamedQuery(name = "UserActivitiesRunsDto.fetchCountByUserIdNConditionId", query = "SELECT COUNT(UARDTO.userActivityRunId)"
				+ " FROM UserActivitiesRunsDto UARDTO"
				+ " WHERE UARDTO.userId =:userId AND UARDTO.conditionId =:conditionId AND runState =:runState AND UARDTO.userStudiesId =:userStudiesId"),

		@NamedQuery(name = "UserActivitiesRunsDto.fetchByUserIdNConditionIdNActivityRunIdNThreshold", query = "FROM UserActivitiesRunsDto UARDTO"
				+ " WHERE UARDTO.userId =:userId AND UARDTO.conditionId =:conditionId"
				+ " AND UARDTO.activityRunId <=:activityRunId AND UARDTO.userStudiesId =:userStudiesId"),

		@NamedQuery(name = "UserActivitiesRunsDto.fetchAllByUserIdNStudiesIdNConditionId", query = "FROM UserActivitiesRunsDto UARDTO"
				+ " WHERE UARDTO.userId =:userId AND UARDTO.conditionId =:conditionId AND UARDTO.userStudiesId =:userStudiesId"
				+ " ORDER BY UARDTO.userActivityRunId DESC"), })
public class UserActivitiesRunsDto implements Serializable {

	private static final long serialVersionUID = -7230604398597081273L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "user_activity_run_id")
	private int userActivityRunId;

	@Column(name = "user_id")
	private int userId;

	@Column(name = "user_studies_id")
	private int userStudiesId;

	@Column(name = "condition_id")
	private int conditionId;

	@Column(name = "activity_run_id")
	private int activityRunId;

	@Column(name = "run_starts_on")
	private String runStartsOn;

	@Column(name = "run_ends_on")
	private String runEndsOn;

	@Column(name = "run_state")
	private String runState;

	@Column(name = "created_on")
	private String createdOn;

	@Column(name = "modified_on")
	private String modifiedOn;
	
	@Column(name="expire_notification_sent", columnDefinition="TINYINT(1)")
	private Boolean expireNotificationSent = false;

	public int getUserActivityRunId() {
		return userActivityRunId;
	}

	public UserActivitiesRunsDto setUserActivityRunId(int userActivityRunId) {
		this.userActivityRunId = userActivityRunId;
		return this;
	}

	public int getUserId() {
		return userId;
	}

	public UserActivitiesRunsDto setUserId(int userId) {
		this.userId = userId;
		return this;
	}

	public int getUserStudiesId() {
		return this.userStudiesId;
	}

	public UserActivitiesRunsDto setUserStudiesId(int userStudiesId) {
		this.userStudiesId = userStudiesId;
		return this;
	}

	public int getConditionId() {
		return conditionId;
	}

	public UserActivitiesRunsDto setConditionId(int conditionId) {
		this.conditionId = conditionId;
		return this;
	}

	public int getActivityRunId() {
		return activityRunId;
	}

	public UserActivitiesRunsDto setActivityRunId(int activityRunId) {
		this.activityRunId = activityRunId;
		return this;
	}

	public String getRunStartsOn() {
		return runStartsOn;
	}

	public UserActivitiesRunsDto setRunStartsOn(String runStartsOn) {
		this.runStartsOn = runStartsOn;
		return this;
	}

	public String getRunEndsOn() {
		return runEndsOn;
	}

	public UserActivitiesRunsDto setRunEndsOn(String runEndsOn) {
		this.runEndsOn = runEndsOn;
		return this;
	}

	public String getRunState() {
		return runState;
	}

	public UserActivitiesRunsDto setRunState(String runState) {
		this.runState = runState;
		return this;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public UserActivitiesRunsDto setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
		return this;
	}

	public String getModifiedOn() {
		return modifiedOn;
	}

	public UserActivitiesRunsDto setModifiedOn(String modifiedOn) {
		this.modifiedOn = modifiedOn;
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
		return "UserActivitiesRunsDto [userActivityRunId=" + userActivityRunId + ", userId=" + userId
				+ ", userStudiesId=" + userStudiesId + ", conditionId=" + conditionId + ", activityRunId="
				+ activityRunId + ", runStartsOn=" + runStartsOn + ", runEndsOn=" + runEndsOn + ", runState=" + runState
				+ ", createdOn=" + createdOn + ", modifiedOn=" + modifiedOn + ", expireNotificationSent="
				+ expireNotificationSent + "]";
	}

}
