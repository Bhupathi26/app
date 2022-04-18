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
import javax.persistence.Transient;
//helloworld
/**
 * Provides activity groups details.
 * 
 * @author Mohan
 * @createdOn Jan 8, 2018 5:46:58 PM
 */
@Entity
@Table(name = "activity_group")
@NamedQueries(value = {

		@NamedQuery(name = "ActivityGroupDto.findByConditionId", query = "FROM ActivityGroupDto ABDTO"
				+ " WHERE ABDTO.conditionId =:conditionId ORDER BY ABDTO.groupId"),

		@NamedQuery(name = "ActivityGroupDto.deleteByConditionId", query = "DELETE"
				+ " FROM ActivityGroupDto ABDTO" + " WHERE ABDTO.conditionId=:conditionId"),

		@NamedQuery(name = "ActivityGroupDto.findByGroupId", query = "FROM ActivityGroupDto ABDTO"
				+ " WHERE ABDTO.groupId =:groupId"),
		@NamedQuery(name = "ActivityGroupDto.findByGroupIdList", query = "FROM ActivityGroupDto ABDTO"
				+ " WHERE ABDTO.groupId IN (:groupIdList)"),

		@NamedQuery(name = "ActivityGroupDto.findByUserId", query = " FROM ActivityGroupDto  ABDTO WHERE ABDTO.groupId IN " + 
				"(SELECT GP.groupId FROM GroupIdentifierDto GP WHERE GP.groupId IN " + 
				"(SELECT GPU.groupId FROM GroupUsersInfoDto GPU WHERE  GPU.userId =:userId))"),

		@NamedQuery(name = "ActivityGroupDto.findAllByActivitySubType", query = "FROM ActivityGroupDto ABDTO WHERE ABDTO.conditionId IN ("
				+ "SELECT ACDTO.conditionId FROM ActivityConditionDto ACDTO"
				+ " WHERE ACDTO.status=true AND ACDTO.activitySubType =:activitySubType)"),

		@NamedQuery(name = "ActivityGroupDto.findAllByConditionIds", query = "FROM ActivityGroupDto ABDTO"
				+ " WHERE ABDTO.conditionId IN (:conditionIdList)") })
public class ActivityGroupDto implements Serializable {

	private static final long serialVersionUID = 5961193324273433065L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "activity_group_id")
	private String activityGroupId;

	@Column(name = "condition_id")
	private int conditionId;

	@Column(name = "group_id")
	private int groupId;
	
	@Transient
	private String groupName ;
	
	@Transient
	private String groupIdVal;

	public String getGroupName() {
		return groupName;
	}

	public ActivityGroupDto setGroupName(String groupName) {
		this.groupName = groupName;
		return this;
	}

	public String getGroupIdVal() {
		return groupIdVal;
	}

	public ActivityGroupDto setGroupIdVal(String groupIdVal) {
		this.groupIdVal = groupIdVal;
		return this;
	}

	public String getActivityGroupId() {
		return activityGroupId;
	}

	public ActivityGroupDto setActivityGroupId(String activityGroupId) {
		this.activityGroupId = activityGroupId;
		return this;
	}

	public int getConditionId() {
		return conditionId;
	}

	public ActivityGroupDto setConditionId(int conditionId) {
		this.conditionId = conditionId;
		return this;
	}

	public int getGroupId() {
		return groupId;
	}

	public ActivityGroupDto setGroupId(int groupId) {
		this.groupId = groupId;
		return this;
	}

	@Override
	public String toString() {
		return "ActivityGroupDto [activityGroupId=" + activityGroupId + ", conditionId=" + conditionId + ", groupId="
				+ groupId + ", groupName=" + groupName + ", groupIdVal=" + groupIdVal + "]";
	}
	
	

}
