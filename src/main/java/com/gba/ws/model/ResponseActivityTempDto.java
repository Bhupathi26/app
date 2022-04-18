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
 * Provides responses activities details.
 * 
 * @author Kavya
 * @createdOn June 14, 2015 12:50:38 PM
 */
@Entity
@Table(name = "response_temp_activities")
@NamedQueries(value = {

		@NamedQuery(name = "ResponseActivityTempDto.findByUserIdEnrollmentId", query = "FROM ResponseActivityTempDto RATDTO"
				+ " WHERE RATDTO.userId =:userId AND RATDTO.enrollmentID =:enrollmentId") })

public class ResponseActivityTempDto implements Serializable {

	private static final long serialVersionUID = -3582253962906723468L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "response_temp_id")
	private Integer responseTempId;
	
	@Column(name ="user_id")
	private Integer userId;
	
	@Column(name = "enrollment_id")
	private String enrollmentID;
	
	@Column(name = "json_file")
	private String jsonFile;
	
	@Column(name = "created_date")
	private String createdDate;
	
	@Column(name = "modified_date")
	private String modifiedDate;
	
	public Integer getUserId() {
		return userId;
	}
	public ResponseActivityTempDto setUserId(Integer userId) {
		this.userId = userId;
		return this;
	}
	public String getEnrollmentID() {
		return enrollmentID;
	}
	public ResponseActivityTempDto setEnrollmentID(String enrollmentID) {
		this.enrollmentID = enrollmentID;
		return this;
	}
	public String getJsonFile() {
		return jsonFile;
	}
	public ResponseActivityTempDto setJsonFile(String jsonFile) {
		this.jsonFile = jsonFile;
		return this;
	}
	public Integer getResponseTempId() {
		return responseTempId;
	}
	public ResponseActivityTempDto setResponseTempId(Integer responseTempId) {
		this.responseTempId = responseTempId;
		return this;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public ResponseActivityTempDto setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
		return this;
	}
	public String getModifiedDate() {
		return modifiedDate;
	}
	public ResponseActivityTempDto setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
		return this;
	}
	@Override
	public String toString() {
		return "ResponseActivityTempDto [responseTempId=" + responseTempId + ", userId=" + userId + ", enrollmentID="
				+ enrollmentID + ", jsonFile=" + jsonFile + ", createdDate=" + createdDate + ", modifiedDate="
				+ modifiedDate + "]";
	}
	

}
