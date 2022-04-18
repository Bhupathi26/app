/**
 * 
 */
package com.gba.ws.bean.fitbit;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mohan
 * @createdOn Jan 12, 2018 11:52:22 AM
 */
public class SleepBean {

	private Summary summary;
	private List<Sleep> sleep = new ArrayList<>();

	public Summary getSummary() {
		return this.summary;
	}

	public SleepBean setSummary(Summary summary) {
		this.summary = summary;
		return this;
	}

	public List<Sleep> getSleep() {
		return this.sleep;
	}

	public SleepBean setSleep(List<Sleep> sleep) {
		this.sleep = sleep;
		return this;
	}

}
