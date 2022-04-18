/**
 * 
 */
package com.gba.ws.bean.fitbit;

/**
 * @author Mohan
 * @createdOn Jan 12, 2018 11:26:35 AM
 */
public class ActivitiesSteps {

	private String dateTime = "";
	private String value = "";

	public String getDateTime() {
		return this.dateTime;
	}

	public ActivitiesSteps setDateTime(String dateTime) {
		//helloworld
		this.dateTime = dateTime;
		return this;
	}

	public String getValue() {
		return this.value;
	}

	public ActivitiesSteps setValue(String value) {
		//helloworld
		this.value = value;
		return this;
	}

}
