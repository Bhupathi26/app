package com.gba.ws.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides activity metadata information
 * 
 * @author Mohan
 * @createdOn Jan 8, 2018 3:32:40 PM
 */
public class ActivityListBean {

	private String activityId = "";
	private String qualtricsId = "";
	private int completedRun = 0;
	private int missedRun = 0;
	private int totalRun = 0;
	private String title = "";
	private String type = "";
	private String subType = "";
	private String lastCompletedDate = "";
	private int lastCompletedRunId = 0;
	private int lastRunId = 0;
	private String startTime = "";
	private String endTime = "";
	private int participationTarget = 0;
	private String frequency = "";
	private boolean geoFence =false;
	
	private List<ConditionsBean> conditions = new ArrayList<>();

	public String getActivityId() {
		return activityId;
	}

	public ActivityListBean setActivityId(String activityId) {
		this.activityId = activityId;
		return this;
	}

	public int getCompletedRun() {
		return completedRun;
	}

	public ActivityListBean setCompletedRun(int completedRun) {
		this.completedRun = completedRun;
		return this;
	}

	public int getMissedRun() {
		return missedRun;
	}

	public ActivityListBean setMissedRun(int missedRun) {
		this.missedRun = missedRun;
		return this;
	}

	public int getTotalRun() {
		return totalRun;
	}

	public ActivityListBean setTotalRun(int totalRun) {
		this.totalRun = totalRun;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public ActivityListBean setTitle(String title) {
		this.title = title;
		return this;
	}

	public String getType() {
		return type;
	}

	public ActivityListBean setType(String type) {
		this.type = type;
		return this;
	}

	public String getSubType() {
		return subType;
	}

	public ActivityListBean setSubType(String subType) {
		this.subType = subType;
		return this;
	}

	public String getLastCompletedDate() {
		return lastCompletedDate;
	}

	public ActivityListBean setLastCompletedDate(String lastCompletedDate) {
		this.lastCompletedDate = lastCompletedDate;
		return this;
	}

	public int getLastCompletedRunId() {
		return lastCompletedRunId;
	}

	public ActivityListBean setLastCompletedRunId(int lastCompletedRunId) {
		this.lastCompletedRunId = lastCompletedRunId;
		return this;
	}

	public int getLastRunId() {
		return this.lastRunId;
	}

	public ActivityListBean setLastRunId(int lastRunId) {
		this.lastRunId = lastRunId;
		return this;
	}

	public String getStartTime() {
		return startTime;
	}

	public ActivityListBean setStartTime(String startTime) {
		this.startTime = startTime;
		return this;
	}

	public String getEndTime() {
		return endTime;
	}

	public ActivityListBean setEndTime(String endTime) {
		this.endTime = endTime;
		return this;
	}

	public String getFrequency() {
		return frequency;
	}

	public ActivityListBean setFrequency(String frequency) {
		this.frequency = frequency;
		return this;
	}

	public List<ConditionsBean> getConditions() {
		return conditions;
	}

	public ActivityListBean setConditions(List<ConditionsBean> conditions) {
		this.conditions = conditions;
		return this;
	}

	public String getQualtricsId() {
		return qualtricsId;
	}

	public ActivityListBean setQualtricsId(String qualtricsId) {
		this.qualtricsId = qualtricsId;
		return this;
	}

	public int getParticipationTarget() {
		return participationTarget;
	}

	public ActivityListBean setParticipationTarget(int participationTarget) {
		this.participationTarget = participationTarget;
		return this;
	}

	public boolean isGeoFence() {
		return geoFence;
	}

	public void setGeoFence(boolean geoFence) {
		this.geoFence = geoFence;
	}

	@Override
	public String toString() {
		return "ActivityListBean [activityId=" + activityId + ", qualtricsId=" + qualtricsId + ", completedRun="
				+ completedRun + ", missedRun=" + missedRun + ", totalRun=" + totalRun + ", title=" + title + ", type="
				+ type + ", subType=" + subType + ", lastCompletedDate=" + lastCompletedDate + ", lastCompletedRunId="
				+ lastCompletedRunId + ", lastRunId=" + lastRunId + ", startTime=" + startTime + ", endTime=" + endTime
				+ ", participationTarget=" + participationTarget + ", frequency=" + frequency + ", geoFence=" + geoFence
				+ ", conditions=" + conditions + "]";
	}

	
	
}
