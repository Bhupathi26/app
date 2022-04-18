/**
 * 
 */
package com.gba.ws.bean.fitbit;

/**
 * @author Mohan
 * @createdOn Jan 12, 2018 11:42:31 AM
 */
public class Awake {

	private String minutes = "";
	private String count = "";

	public String getMinutes() {
		return this.minutes;
	}

	public Awake setMinutes(String minutes) {
		this.minutes = minutes;
		return this;
	}

	public String getCount() {
		return this.count;
	}

	public Awake setCount(String count) {
		this.count = count;
		return this;
	}

}
