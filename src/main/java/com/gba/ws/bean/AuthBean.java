package com.gba.ws.bean;

import com.gba.ws.util.AppConstants;

/**
 * Provides authorization details.
 * 
 * @author Mohan
 * @createdOn Jan 8, 2018 3:35:35 PM
 */
public class AuthBean {

	private String status = AppConstants.FAILURE;
	private String authKey = "";
	private int userId;
	private String message = "";

	public String getStatus() {
		return status;
	}

	public AuthBean setStatus(String status) {
		this.status = status;
		return this;
	}

	public String getAuthKey() {
		return authKey;
	}

	public AuthBean setAuthKey(String authKey) {
		this.authKey = authKey;
		return this;
	}

	public int getUserId() {
		return userId;
	}

	public AuthBean setUserId(int userId) {
		this.userId = userId;
		return this;
	}

	public String getMessage() {
		return message;
	}

	public AuthBean setMessage(String message) {
		this.message = message;
		return this;
	}

	@Override
	public String toString() {
		return "AuthBean [status=" + status + ", authKey=" + authKey + ", userId=" + userId + ", message=" + message
				+ "]";
	}

}
