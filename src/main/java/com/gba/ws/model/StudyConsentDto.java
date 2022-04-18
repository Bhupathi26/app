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
 * Provides Consnet document details for the Study.
 * 
 * @author Mohan
 * @createdOn Jan 8, 2018 5:54:45 PM
 */
@Entity
@Table(name = "study_consent")
@NamedQueries(value = {

		@NamedQuery(name = "StudyConsentDto.findByUserIdStudyId", query = "FROM StudyConsentDto SCDTO"
				+ " WHERE SCDTO.userId =:userId AND SCDTO.studyId =:studyId AND SCDTO.active=true") })
public class StudyConsentDto implements Serializable {

	private static final long serialVersionUID = -1449301718057140136L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "study_consent_id")
	private int studyConsentId;

	@Column(name = "user_id")
	private int userId;

	@Column(name = "study_id")
	private int studyId;

	@Column(name = "consent_status", length = 1)
	@Type(type = "yes_no")
	private boolean consentStatus = true;

	@Column(name = "consent_pdf")
	private String consentPdf;

	@Column(name = "created_on")
	private String createdOn;

	@Column(name = "modified_on")
	private String modifiedOn;

	@Column(name = "active", length = 1)
	@Type(type = "yes_no")
	private boolean active = true;

	public int getStudyConsentId() {
		return studyConsentId;
	}

	public StudyConsentDto setStudyConsentId(int studyConsentId) {
		this.studyConsentId = studyConsentId;
		return this;
	}

	public int getUserId() {
		return userId;
	}

	public StudyConsentDto setUserId(int userId) {
		this.userId = userId;
		return this;
	}

	public int getStudyId() {
		return studyId;
	}

	public StudyConsentDto setStudyId(int studyId) {
		this.studyId = studyId;
		return this;
	}

	public boolean getConsentStatus() {
		return consentStatus;
	}

	public StudyConsentDto setConsentStatus(boolean consentStatus) {
		this.consentStatus = consentStatus;
		return this;
	}

	public String getConsentPdf() {
		return consentPdf;
	}

	public StudyConsentDto setConsentPdf(String consentPdf) {
		this.consentPdf = consentPdf;
		return this;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public StudyConsentDto setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
		return this;
	}

	public String getModifiedOn() {
		return modifiedOn;
	}

	public StudyConsentDto setModifiedOn(String modifiedOn) {
		this.modifiedOn = modifiedOn;
		return this;
	}

	public boolean getActive() {
		return this.active;
	}

	public StudyConsentDto setActive(boolean active) {
		this.active = active;
		return this;
	}

	@Override
	public String toString() {
		return "StudyConsentDto [studyConsentId=" + studyConsentId + ", userId=" + userId + ", studyId=" + studyId
				+ ", consentStatus=" + consentStatus + ", consentPdf=" + consentPdf + ", createdOn=" + createdOn
				+ ", modifiedOn=" + modifiedOn + ", active=" + active + "]";
	}

}
