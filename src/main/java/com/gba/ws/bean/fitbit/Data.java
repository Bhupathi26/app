/**
 * 
 */
package com.gba.ws.bean.fitbit;

/**
 * @author Mohan
 * @createdOn Jan 12, 2018 11:43:49 AM
 */
public class Data {

	private String level = "";
	private String seconds = "";
	private String dateTime = "";

	public String getLevel() {
		return this.level;
	}

	public Data setLevel(String level) {
		this.level = level;
		return this;
	}

	public String getSeconds() {
		return this.seconds;
	}

	public Data setSeconds(String seconds) {
		this.seconds = seconds;
		return this;
	}

	public String getDateTime() {
		return this.dateTime;
	}

	public Data setDateTime(String dateTime) {
		this.dateTime = dateTime;
		return this;
	}

}
