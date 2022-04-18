package com.gba.ws.bean;

/**
 * Provides Login information in response.
 * 
 * @author Mohan
 * @createdOn Jan 8, 2018 3:41:21 PM
 */
public class LoginResponse {

	private ErrorBean error = new ErrorBean();
	private String userId = "";
	private String accessToken = "";
	private String refreshToken = "";
	private String language = "";
	private boolean fitbitSetup = false;
	private boolean lass4uSetup = false;
	private boolean enrolled = false;
	private boolean verified = false;
	private boolean tempPassword = false;
	private String enrollmentId = "";

	public ErrorBean getError() {
		return error;
	}

	public LoginResponse setError(ErrorBean error) {
		this.error = error;
		return this;
	}

	public String getUserId() {
		return userId;
	}

	public LoginResponse setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public LoginResponse setAccessToken(String accessToken) {
		this.accessToken = accessToken;
		return this;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public LoginResponse setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
		return this;
	}

	public String getLanguage() {
		return language;
	}

	public LoginResponse setLanguage(String language) {
		this.language = language;
		return this;
	}

	public boolean isFitbitSetup() {
		return fitbitSetup;
	}

	public LoginResponse setFitbitSetup(boolean fitbitSetup) {
		this.fitbitSetup = fitbitSetup;
		return this;
	}

	public boolean isLass4uSetup() {
		return lass4uSetup;
	}

	public LoginResponse setLass4uSetup(boolean lass4uSetup) {
		this.lass4uSetup = lass4uSetup;
		return this;
	}

	public boolean isEnrolled() {
		return enrolled;
	}

	public LoginResponse setEnrolled(boolean enrolled) {
		this.enrolled = enrolled;
		return this;
	}

	public boolean isVerified() {
		return verified;
	}

	public LoginResponse setVerified(boolean verified) {
		this.verified = verified;
		return this;
	}

	public boolean isTempPassword() {
		return tempPassword;
	}

	public LoginResponse setTempPassword(boolean tempPassword) {
		this.tempPassword = tempPassword;
		return this;
	}

	public String getEnrollmentId() {
		return enrollmentId;
	}

	public LoginResponse setEnrollmentId(String enrollmentId) {
		this.enrollmentId = enrollmentId;
		return this;
	}

	@Override
	public String toString() {
		return "LoginResponse [error=" + error + ", userId=" + userId + ", accessToken=" + accessToken
				+ ", refreshToken=" + refreshToken + ", language=" + language + ", fitbitSetup=" + fitbitSetup
				+ ", lass4uSetup=" + lass4uSetup + ", enrolled=" + enrolled + ", verified=" + verified
				+ ", tempPassword=" + tempPassword + ", enrollmentId=" + enrollmentId + "]";
	}

}
