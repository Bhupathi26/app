package com.gba.ws.bean;

import com.gba.ws.util.AppConstants;

public class BaseResponse {

	private ErrorBean error = new ErrorBean();

	private String status = AppConstants.FAILURE;

	public String getStatus() {
		return status;
	}

	public BaseResponse setStatus(String status) {
		this.status = status;
		return this;
	}

	public ErrorBean getError() {
		return error;
	}

	public BaseResponse setError(ErrorBean error) {
		this.error = error;
		return this;
	}

	@Override
	public String toString() {
		return "BaseResponse [error=" + error + ", status=" + status + "]";
	}

}
