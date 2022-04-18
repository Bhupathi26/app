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
 * Provides Enrollment ID details for the Study.
 * 
 * @author Mohan
 * @createdOn Jan 8, 2018 5:51:13 PM
 */
@Entity
@Table(name = "enrollment_tokens")
@NamedQueries(value = {

		@NamedQuery(name = "EnrollmentTokensDto.findByEnrollmentId", query = "FROM EnrollmentTokensDto ETDTO"
				+ " WHERE ETDTO.enrollmentId =:enrollmentId"),

		@NamedQuery(name = "EnrollmentTokensDto.updateEnrollmentTokens", query = "FROM EnrollmentTokensDto ETDTO"
				+ " WHERE ETDTO.isActive =false AND ETDTO.enrollmentId NOT IN (" + "SELECT USDTO.enrollmentId"
				+ " FROM UserStudiesDto USDTO)"),

		@NamedQuery(name = "EnrollmentTokensDto.findAll", query = "FROM EnrollmentTokensDto ETDTO"), })
public class EnrollmentTokensDto implements Serializable {

	private static final long serialVersionUID = -3437511209961394519L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "enrollment_token_id")
	private int enrollmentTokenId;

	@Column(name = "enrollment_id")
	private String enrollmentId;

	@Column(name = "is_active", length = 1)
	@Type(type = "yes_no")
	private boolean isActive = false;

	@Column(name = "group_id")
	private String groupId;

	@Column(name = "country")
	private String country;

	public int getEnrollmentTokenId() {
		return enrollmentTokenId;
	}

	public EnrollmentTokensDto setEnrollmentTokenId(int enrollmentTokenId) {
		this.enrollmentTokenId = enrollmentTokenId;
		return this;
	}

	public String getEnrollmentId() {
		return enrollmentId;
	}

	public EnrollmentTokensDto setEnrollmentId(String enrollmentId) {
		this.enrollmentId = enrollmentId;
		return this;
	}

	public boolean getIsActive() {
		return isActive;
	}

	public EnrollmentTokensDto setIsActive(boolean isActive) {
		this.isActive = isActive;
		return this;
	}

	public String getGroupId() {
		return groupId;
	}

	public EnrollmentTokensDto setGroupId(String groupId) {
		this.groupId = groupId;
		return this;
	}

	public String getCountry() {
		return country;
	}

	public EnrollmentTokensDto setCountry(String country) {
		this.country = country;
		return this;
	}

	@Override
	public String toString() {
		return "EnrollmentTokensDto [enrollmentTokenId=" + enrollmentTokenId + ", enrollmentId=" + enrollmentId
				+ ", isActive=" + isActive + ", groupId=" + groupId + ", country=" + country + "]";
	}
	
	

}
