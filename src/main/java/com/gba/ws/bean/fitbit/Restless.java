/**
 * 
 */
package com.gba.ws.bean.fitbit;

/**
 * @author Mohan
 * @createdOn Jan 12, 2018 11:45:40 AM
 */
public class Restless {

	private String minutes = "";
	private String count = "";

	public String getMinutes() {
		return this.minutes;
	}

	public Restless setMinutes(String minutes) {
		this.minutes = minutes;
		return this;
	}

	public String getCount() {
		return this.count;
	}

	public Restless setCount(String count) {
		this.count = count;
		return this;
	}

}
