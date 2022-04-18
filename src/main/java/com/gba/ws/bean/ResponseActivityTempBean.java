package com.gba.ws.bean;

public class ResponseActivityTempBean {

	private Integer userId = 0;
	private String enrollmentID = "";
	private String jsonFile = "";
	private String createdDate = "";

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getEnrollmentID() {
		return enrollmentID;
	}

	public void setEnrollmentID(String enrollmentID) {
		this.enrollmentID = enrollmentID;
	}

	public String getJsonFile() {
		return jsonFile;
	}

	public void setJsonFile(String jsonFile) {
		this.jsonFile = jsonFile;
	}

	@Override
	public String toString() {
		return "ResponseActivityTempBean [userId=" + userId + ", enrollmentID=" + enrollmentID + ", jsonFile="
				+ jsonFile + ", createdDate=" + createdDate + "]";
	}

}
