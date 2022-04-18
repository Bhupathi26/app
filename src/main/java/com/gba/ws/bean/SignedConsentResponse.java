package com.gba.ws.bean;

/**
 * Provides signed consent information in response.
 * 
 * @author Mohan
 * @createdOn Jan 8, 2018 3:46:12 PM
 */
public class SignedConsentResponse {

	private ErrorBean error = new ErrorBean();
	private String url = "";

	public ErrorBean getError() {
		return error;
	}

	public SignedConsentResponse setError(ErrorBean error) {
		this.error = error;
		return this;
	}

	public String getUrl() {
		return url;
	}

	public SignedConsentResponse setUrl(String url) {
		this.url = url;
		return this;
	}

	@Override
	public String toString() {
		return "SignedConsentResponse [error=" + error + ", url=" + url + "]";
	}

}
