/**
 * 
 */
package com.gba.ws.bean.fitbit;

/**
 * @author Mohan
 * @createdOn Jan 12, 2018 12:03:01 PM
 */
public class IntradayDataset {

	private String time = "";
	private String value = "";

	public String getTime() {
		return this.time;
	}

	public IntradayDataset setTime(String time) {
		this.time = time;
		return this;
	}

	public String getValue() {
		return this.value;
	}

	public IntradayDataset setValue(String value) {
		this.value = value;
		return this;
	}

}
