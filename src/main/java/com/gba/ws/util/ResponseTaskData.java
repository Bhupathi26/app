package com.gba.ws.util;

import java.util.ArrayList;
import java.util.List;

public class ResponseTaskData {

	
	private String endTime;
	 private String totalTime;
	 private String startTime;
	 
	 List<ResponseTaskResultList> results  = new ArrayList<>();;
	 
	public List<ResponseTaskResultList> getResults() {
		return results;
	}
	public void setResults(List<ResponseTaskResultList> results) {
		this.results = results;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getTotalTime() {
		return totalTime;
	}
	public void setTotalTime(String totalTime) {
		this.totalTime = totalTime;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	 
	 
}
