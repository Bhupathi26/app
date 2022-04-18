package com.gba.ws.util;

import java.util.ArrayList;
import java.util.List;

public class ResponseTaskResultList {
	 private String endTime;
	 private String resultType;
	 private String startTime;
	 List<ResponseTaskListValues> values = new ArrayList<>();
	 
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getResultType() {
		return resultType;
	}
	public void setResultType(String resultType) {
		this.resultType = resultType;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public List<ResponseTaskListValues> getValues() {
		return values;
	}
	public void setValues(List<ResponseTaskListValues> values) {
		this.values = values;
	}
	 

}
