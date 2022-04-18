/**
 * 
 */
package com.gba.ws.bean.fitbit;

/**
 * @author Mohan
 * @createdOn Jan 12, 2018 11:56:30 AM
 */
public class RestingDataset {

	private String time = "";
	private String value = "";

	public String getTime() {
		return this.time;
	}

	public RestingDataset setTime(String time) {
		this.time = time;
		return this;
	}

	public String getValue() {
		return this.value;
	}

	public RestingDataset setValue(String value) {
		this.value = value;
		return this;
	}

}
