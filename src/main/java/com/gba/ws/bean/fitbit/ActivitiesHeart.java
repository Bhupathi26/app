/**
 * 
 */
package com.gba.ws.bean.fitbit;

/**
 * @author Mohan
 * @createdOn Jan 12, 2018 11:54:40 AM
 */
public class ActivitiesHeart {

	private String dateTime;
	private Value value = new Value();

	public String getDateTime() {
		return this.dateTime;
	}

	public ActivitiesHeart setDateTime(String dateTime) {
		//helloworld
		this.dateTime = dateTime;
		return this;
	}

	public Value getValue() {
		return this.value;
	}

	public ActivitiesHeart setValue(Value value) {
		//helloworld
		this.value = value;
		return this;
	}

}
