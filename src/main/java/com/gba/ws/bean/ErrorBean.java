package com.gba.ws.bean;

/**
 * Provides Error information.
 * 
 * @author Mohan
 * @createdOn Jan 8, 2018 3:38:42 PM
 */
public class ErrorBean {

	private int code = 0;
	private String message = "";

	public int getCode() {
		return code;
	}

	public ErrorBean setCode(int code) {
		this.code = code;
		return this;
	}

	public String getMessage() {
		return message;
	}

	public ErrorBean setMessage(String message) {
		this.message = message;
		return this;
	}

	@Override
	public String toString() {
		return "ErrorBean [code=" + code + ", message=" + message + "]";
	}

}
