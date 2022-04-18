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

import org.hibernate.annotations.Type;

/**
 * Provides activity condition details created from the activities created in
 * Qualtrics.
 * 
 * @author Mohan
 * @createdOn Jan 8, 2018 5:49:23 PM
 */
@Entity
@Table(name = "activity_condition")
@NamedQueries(value = {

		@NamedQuery(name = "ActivityConditionDto.findByConditionId", query = "FROM ActivityConditionDto ACDTO"
				+ " WHERE ACDTO.status=true AND ACDTO.conditionId =:conditionId"),

		@NamedQuery(name = "ActivityConditionDto.findByActivityId", query = "FROM ActivityConditionDto ACDTO"
				+ " WHERE ACDTO.status=true AND ACDTO.activityId =:activityId"),

		@NamedQuery(name = "ActivityConditionDto.findByCreatedBy", query = "FROM ActivityConditionDto ACDTO"
				+ " WHERE ACDTO.status=true AND ACDTO.createdBy =:createdBy"),

		@NamedQuery(name = "ActivityConditionDto.findAllByActivityIds", query = "FROM ActivityConditionDto ACDTO"
				+ " WHERE ACDTO.status=true AND ACDTO.activityId IN (:activityIdList) ORDER BY ACDTO.conditionId"),

		@NamedQuery(name = "ActivityConditionDto.findByActivityConditionId", query = "FROM ActivityConditionDto ACDTO"
				+ " WHERE ACDTO.status=true AND ACDTO.activityConditionId =:activityConditionId"),

		@NamedQuery(name = "ActivityConditionDto.findAllByConditionIds", query = "FROM ActivityConditionDto ACDTO"
				+ " WHERE ACDTO.status=true AND ACDTO.conditionId IN (:conditionIdList)"),

		@NamedQuery(name = "ActivityConditionDto.findAllByActivitySubTypeNEndDate", query = "FROM ActivityConditionDto ACDTO"
				+ " WHERE ACDTO.status=true AND ACDTO.activitySubType =:activitySubType AND ACDTO.conditionId IN ("
				+ "SELECT TCDTO.conditionId FROM TemporalConditionDto TCDTO"
				+ " WHERE TCDTO.endDate >= DATE(:endDate))"), })
public class ActivityConditionDto implements Serializable {

	private static final long serialVersionUID = 5695717035619212463L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "condition_id")
	private int conditionId;

	@Column(name = "activity_condition_id")
	private String activityConditionId;

	@Column(name = "activity_condition_name")
	private String activityConditionName;

	@Column(name = "activity_id")
	private int activityId;

	@Column(name = "total_participation_target")
	private int totalParticipationTarget;

	@Column(name = "points_per_completion")
	private int pointsPerCompletion;

	@Column(name = "activity_type")
	private String activityType;

	@Column(name = "activity_sub_type")
	private String activitySubType;

	@Column(name = "status", length = 1)
	@Type(type = "yes_no")
	private boolean status = true;

	@Column(name = "created_on")
	private String createdOn;

	@Column(name = "created_by")
	private int createdBy;

	public int getConditionId() {
		return conditionId;
	}

	public ActivityConditionDto setConditionId(int conditionId) {
		this.conditionId = conditionId;
		return this;
	}

	public String getActivityConditionId() {
		return activityConditionId;
	}

	public ActivityConditionDto setActivityConditionId(String activityConditionId) {
		this.activityConditionId = activityConditionId;
		return this;
	}

	public String getActivityConditionName() {
		return activityConditionName;
	}

	public ActivityConditionDto setActivityConditionName(String activityConditionName) {
		this.activityConditionName = activityConditionName;
		return this;
	}

	public int getActivityId() {
		return activityId;
	}

	public ActivityConditionDto setActivityId(int activityId) {
		this.activityId = activityId;
		return this;
	}

	public int getTotalParticipationTarget() {
		return totalParticipationTarget;
	}

	public ActivityConditionDto setTotalParticipationTarget(int totalParticipationTarget) {
		this.totalParticipationTarget = totalParticipationTarget;
		return this;
	}

	public int getPointsPerCompletion() {
		return pointsPerCompletion;
	}

	public ActivityConditionDto setPointsPerCompletion(int pointsPerCompletion) {
		this.pointsPerCompletion = pointsPerCompletion;
		return this;
	}

	public String getActivityType() {
		return activityType;
	}

	public ActivityConditionDto setActivityType(String activityType) {
		this.activityType = activityType;
		return this;
	}

	public boolean getStatus() {
		return status;
	}

	public ActivityConditionDto setStatus(boolean status) {
		this.status = status;
		return this;
	}

	public String getActivitySubType() {
		return activitySubType;
	}

	public ActivityConditionDto setActivitySubType(String activitySubType) {
		this.activitySubType = activitySubType;
		return this;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public ActivityConditionDto setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
		return this;
	}

	public int getCreatedBy() {
		return createdBy;
	}

	public ActivityConditionDto setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
		return this;
	}

	@Override
	public String toString() {
		return "ActivityConditionDto [conditionId=" + conditionId + ", activityConditionId=" + activityConditionId
				+ ", activityConditionName=" + activityConditionName + ", activityId=" + activityId
				+ ", totalParticipationTarget=" + totalParticipationTarget + ", pointsPerCompletion="
				+ pointsPerCompletion + ", activityType=" + activityType + ", activitySubType=" + activitySubType
				+ ", status=" + status + ", createdOn=" + createdOn + ", createdBy=" + createdBy + "]";
	}
	
	

}
