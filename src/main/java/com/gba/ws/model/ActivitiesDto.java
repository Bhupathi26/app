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
//helloworld
/**
 * Provides activities details created in Qualtrics.
 * 
 * @author Mohan
 * @createdOn Jan 8, 2018 5:45:38 PM
 */
@Entity
@Table(name = "activities")
@NamedQueries(value = {

		@NamedQuery(name = "ActivitiesDto.findByActivityId", query = "FROM ActivitiesDto ADTO"
				+ " WHERE ADTO.status=true AND ADTO.activityId =:activityId"),

		@NamedQuery(name = "ActivitiesDto.findByQualtricsId", query = "FROM ActivitiesDto ADTO"
				+ " WHERE ADTO.status=true AND ADTO.qualtricsId =:qualtricsId"),

		@NamedQuery(name = "ActivitiesDto.findByStudyId", query = "FROM ActivitiesDto ADTO"
				+ " WHERE ADTO.status=true AND ADTO.studyId =:studyId"),

		@NamedQuery(name = "ActivitiesDto.findByActivityIdNStudyId", query = "FROM ActivitiesDto ADTO"
				+ " WHERE ADTO.status=true AND ADTO.activityId =:activityId AND ADTO.studyId =:studyId"),

		@NamedQuery(name = "ActivitiesDto.findByQualtricsIdNStudyId", query = "FROM ActivitiesDto ADTO"
				+ " WHERE ADTO.status=true AND ADTO.qualtricsId =:qualtricsId AND ADTO.studyId =:studyId"),

		@NamedQuery(name = "ActivitiesDto.findAll", query = "FROM ActivitiesDto ADTO" + " WHERE ADTO.status=true"), })
public class ActivitiesDto implements Serializable {

	private static final long serialVersionUID = -3582253962906723468L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "activity_id")
	private int activityId;

	@Column(name = "study_id")
	private int studyId;

	@Column(name = "qualtrics_id")
	private String qualtricsId;

	@Column(name = "owner_id")
	private String ownerId;

	@Column(name = "status", length = 1)
	@Type(type = "yes_no")
	private boolean status = true;

	@Column(name = "created_date")
	private String createdDate;

	@Column(name = "activity_name")
	private String activityName;

	@Column(name = "language")
	private String language;

	public int getActivityId() {
		return activityId;
	}

	public ActivitiesDto setActivityId(int activityId) {
		this.activityId = activityId;
		return this;
	}

	public int getStudyId() {
		return studyId;
	}

	public ActivitiesDto setStudyId(int studyId) {
		this.studyId = studyId;
		return this;
	}

	public String getQualtricsId() {
		return qualtricsId;
	}

	public ActivitiesDto setQualtricsId(String qualtricsId) {
		this.qualtricsId = qualtricsId;
		return this;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public ActivitiesDto setOwnerId(String ownerId) {
		this.ownerId = ownerId;
		return this;
	}

	public boolean getStatus() {
		return status;
	}

	public ActivitiesDto setStatus(boolean status) {
		this.status = status;
		return this;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public ActivitiesDto setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
		return this;
	}

	public String getActivityName() {
		return activityName;
	}

	public ActivitiesDto setActivityName(String activityName) {
		this.activityName = activityName;
		return this;
	}

	public String getLanguage() {
		return language;
	}

	public ActivitiesDto setLanguage(String language) {
		this.language = language;
		return this;
	}

	@Override
	public String toString() {
		return "ActivitiesDto [activityId=" + activityId + ", studyId=" + studyId + ", qualtricsId=" + qualtricsId
				+ ", ownerId=" + ownerId + ", status=" + status + ", createdDate=" + createdDate + ", activityName="
				+ activityName + ", language=" + language + "]";
	}
	
	

}
