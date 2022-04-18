package com.gba.ws.bean;

import com.gba.ws.util.AppConstants;

/**
 * Provides activity metadata information.
 * 
 * @author Mohan
 * @createdOn Jan 8, 2018 3:41:50 PM
 */
public class MetaDataBean {

	private String studyId = "";
	private String activityId = "";
	private int numberOfAttempts = 0;
	private String version = AppConstants.APP_DEFAULT_VERSION;
	private String lastModifiedDate = "";
	private String startDate = "";
	private String endDate = "";

	public String getStudyId() {
		return studyId;
	}

	public MetaDataBean setStudyId(String studyId) {
		this.studyId = studyId;
		return this;
	}

	public String getActivityId() {
		return activityId;
	}

	public MetaDataBean setActivityId(String activityId) {
		this.activityId = activityId;
		return this;
	}

	public int getNumberOfAttempts() {
		return numberOfAttempts;
	}

	public MetaDataBean setNumberOfAttempts(int numberOfAttempts) {
		this.numberOfAttempts = numberOfAttempts;
		return this;
	}

	public String getVersion() {
		return version;
	}

	public MetaDataBean setVersion(String version) {
		this.version = version;
		return this;
	}

	public String getLastModifiedDate() {
		return lastModifiedDate;
	}

	public MetaDataBean setLastModifiedDate(String lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
		return this;
	}

	public String getStartDate() {
		return startDate;
	}

	public MetaDataBean setStartDate(String startDate) {
		this.startDate = startDate;
		return this;
	}

	public String getEndDate() {
		return endDate;
	}

	public MetaDataBean setEndDate(String endDate) {
		this.endDate = endDate;
		return this;
	}

	@Override
	public String toString() {
		return "MetaDataBean [studyId=" + studyId + ", activityId=" + activityId + ", numberOfAttempts="
				+ numberOfAttempts + ", version=" + version + ", lastModifiedDate=" + lastModifiedDate + ", startDate="
				+ startDate + ", endDate=" + endDate + "]";
	}

}
