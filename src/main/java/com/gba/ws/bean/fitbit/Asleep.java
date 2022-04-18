/**
 * 
 */
package com.gba.ws.bean.fitbit;

/**
 * @author Mohan
 * @createdOn Jan 12, 2018 11:41:35 AM
 */
public class Asleep {

	private String minutes = "";
	private String count = "";

	public String getMinutes() {
		return this.minutes;
	}

	public Asleep setMinutes(String minutes) {
		this.minutes = minutes;
		return this;
	}

	public String getCount() {
		return this.count;
	}

	public Asleep setCount(String count) {
		this.count = count;
		return this;
	}

}
