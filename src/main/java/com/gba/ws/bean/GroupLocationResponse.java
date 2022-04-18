/**
 * 
 */
package com.gba.ws.bean;

/**
 * @author Mohan
 * @createdOn Jan 16, 2018 3:41:25 PM
 */
public class GroupLocationResponse {

	private ErrorBean error = new ErrorBean();
	private String latitude = "";
	private String longitude = "";

	public ErrorBean getError() {
		return this.error;
	}

	public GroupLocationResponse setError(ErrorBean error) {
		this.error = error;
		return this;
	}

	public String getLatitude() {
		return this.latitude;
	}

	public GroupLocationResponse setLatitude(String latitude) {
		this.latitude = latitude;
		return this;
	}

	public String getLongitude() {
		return this.longitude;
	}

	public GroupLocationResponse setLongitude(String longitude) {
		this.longitude = longitude;
		return this;
	}

	@Override
	public String toString() {
		return "GroupLocationResponse [error=" + error + ", latitude=" + latitude + ", longitude=" + longitude + "]";
	}

}
