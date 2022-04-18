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
 * Provides User and Studies details.
 * 
 * @author Mohan
 * @createdOn Jan 8, 2018 6:00:59 PM
 */
@Entity
@Table(name = "user_studies")
@NamedQueries(value = {

		@NamedQuery(name = "UserStudiesDto.fetchByUserId", query = "FROM UserStudiesDto USDTO"
				+ " WHERE USDTO.userId =:userId AND USDTO.active=true"),

		@NamedQuery(name = "UserStudiesDto.fetchByUserIdNStudyId", query = "FROM UserStudiesDto USDTO"
				+ " WHERE USDTO.userId =:userId AND USDTO.studyId =:studyId AND USDTO.active=true"),

		@NamedQuery(name = "UserStudiesDto.fetchAllByUserIdNStudyId", query = "FROM UserStudiesDto USDTO"
				+ " WHERE USDTO.userId IN (:userIdsList) AND USDTO.studyId =:studyId AND USDTO.active=true") })
public class UserStudiesDto implements Serializable {

	private static final long serialVersionUID = -1801005753070100004L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "user_studies_id")
	private int userStudiesId;

	@Column(name = "user_id")
	private int userId;

	@Column(name = "study_id")
	private int studyId;

	@Column(name = "consent_id")
	private int consentId;

	@Column(name = "eligibility", length = 1)
	@Type(type = "yes_no")
	private boolean eligibility = false;

	@Column(name = "enrollment_id")
	private String enrollmentId;

	@Column(name = "created_on")
	private String createdOn;

	@Column(name = "modified_on")
	private String modifiedOn;

	@Column(name = "active", length = 1)
	@Type(type = "yes_no")
	private boolean active = true;

	public int getUserStudiesId() {
		return userStudiesId;
	}

	public UserStudiesDto setUserStudiesId(int userStudiesId) {
		this.userStudiesId = userStudiesId;
		return this;
	}

	public int getUserId() {
		return userId;
	}

	public UserStudiesDto setUserId(int userId) {
		this.userId = userId;
		return this;
	}

	public int getStudyId() {
		return studyId;
	}

	public UserStudiesDto setStudyId(int studyId) {
		this.studyId = studyId;
		return this;
	}

	public int getConsentId() {
		return this.consentId;
	}

	public UserStudiesDto setConsentId(int consentId) {
		this.consentId = consentId;
		return this;
	}

	public boolean getEligibility() {
		return eligibility;
	}

	public UserStudiesDto setEligibility(boolean eligibility) {
		this.eligibility = eligibility;
		return this;
	}

	public String getEnrollmentId() {
		return enrollmentId;
	}

	public UserStudiesDto setEnrollmentId(String enrollmentId) {
		this.enrollmentId = enrollmentId;
		return this;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public UserStudiesDto setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
		return this;
	}

	public String getModifiedOn() {
		return modifiedOn;
	}

	public UserStudiesDto setModifiedOn(String modifiedOn) {
		this.modifiedOn = modifiedOn;
		return this;
	}

	public boolean getActive() {
		return this.active;
	}

	public UserStudiesDto setActive(boolean active) {
		this.active = active;
		return this;
	}

	@Override
	public String toString() {
		return "UserStudiesDto [userStudiesId=" + userStudiesId + ", userId=" + userId + ", studyId=" + studyId
				+ ", consentId=" + consentId + ", eligibility=" + eligibility + ", enrollmentId=" + enrollmentId
				+ ", createdOn=" + createdOn + ", modifiedOn=" + modifiedOn + ", active=" + active + "]";
	}

}
