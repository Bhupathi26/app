package com.gba.ws.bean;

/**
 * Provides run details.
 * 
 * @author Mohan
 * @createdOn Jan 8, 2018 3:45:19 PM
 */
public class RunBean {

	private int runId = 0;
	private String runStartDateTime = "";
	private String runEndDateTime = "";
	private String runState = "";

	public int getRunId() {
		return runId;
	}

	public RunBean setRunId(int runId) {
		this.runId = runId;
		return this;
	}

	public String getRunStartDateTime() {
		return runStartDateTime;
	}

	public RunBean setRunStartDateTime(String runStartDateTime) {
		this.runStartDateTime = runStartDateTime;
		return this;
	}

	public String getRunEndDateTime() {
		return runEndDateTime;
	}

	public RunBean setRunEndDateTime(String runEndDateTime) {
		this.runEndDateTime = runEndDateTime;
		return this;
	}

	public String getRunState() {
		return runState;
	}

	public RunBean setRunState(String runState) {
		this.runState = runState;
		return this;
	}

	@Override
	public String toString() {
		return "RunBean [runId=" + runId + ", runStartDateTime=" + runStartDateTime + ", runEndDateTime="
				+ runEndDateTime + ", runState=" + runState + "]";
	}

}
