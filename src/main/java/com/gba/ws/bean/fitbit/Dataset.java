/**
 * 
 */
package com.gba.ws.bean.fitbit;

/**
 * @author Mohan
 * @createdOn Jan 12, 2018 11:31:58 AM
 */
public class Dataset {

	private String time = "";
	private String value = "";

	public String getTime() {
		return this.time;
	}

	public Dataset setTime(String time) {
		this.time = time;
		return this;
	}

	public String getValue() {
		return this.value;
	}

	public Dataset setValue(String value) {
		this.value = value;
		return this;
	}

}
