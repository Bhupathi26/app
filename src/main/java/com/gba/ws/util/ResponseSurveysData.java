package com.gba.ws.util;

import java.util.ArrayList;
import java.util.List;

public class ResponseSurveysData {

	
	private String endTime;
	 private String totalTime;
	 private String startTime;
	 
	 List<ResponseSurveysResultList> results  = new ArrayList<>();
	 
	public List<ResponseSurveysResultList> getResults() {
		return results;
	}
	public void setResults(List<ResponseSurveysResultList> results) {
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
