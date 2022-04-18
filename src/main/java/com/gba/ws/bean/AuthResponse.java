package com.gba.ws.bean;

/**
 * Provides authorization information in response.
 * 
 * @author Mohan
 * @createdOn Jan 8, 2018 3:36:06 PM
 */
public class AuthResponse {

	private ErrorBean error = new ErrorBean();
	private String userId = "";
	private String accessToken = "";
	private String refreshToken = "";

	public ErrorBean getError() {
		return error;
	}

	public AuthResponse setError(ErrorBean error) {
		this.error = error;
		return this;
	}

	public String getUserId() {
		return userId;
	}

	public AuthResponse setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public AuthResponse setAccessToken(String accessToken) {
		this.accessToken = accessToken;
		return this;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public AuthResponse setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
		return this;
	}

	@Override
	public String toString() {
		return "AuthResponse [error=" + error + ", userId=" + userId + ", accessToken=" + accessToken
				+ ", refreshToken=" + refreshToken + "]";
	}

}
