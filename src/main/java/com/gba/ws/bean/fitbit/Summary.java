/**
 * 
 */
package com.gba.ws.bean.fitbit;

/**
 * @author Mohan
 * @createdOn Jan 12, 2018 11:40:31 AM
 */
public class Summary {

	private String totalTimeInBed = "";
	private String totalMinutesAsleep = "";
	private String totalSleepRecords = "";

	public String getTotalTimeInBed() {
		return this.totalTimeInBed;
	}

	public Summary setTotalTimeInBed(String totalTimeInBed) {
		this.totalTimeInBed = totalTimeInBed;
		return this;
	}

	public String getTotalMinutesAsleep() {
		return this.totalMinutesAsleep;
	}

	public Summary setTotalMinutesAsleep(String totalMinutesAsleep) {
		this.totalMinutesAsleep = totalMinutesAsleep;
		return this;
	}

	public String getTotalSleepRecords() {
		return this.totalSleepRecords;
	}

	public Summary setTotalSleepRecords(String totalSleepRecords) {
		this.totalSleepRecords = totalSleepRecords;
		return this;
	}

}
