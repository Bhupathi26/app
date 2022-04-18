package com.gba.ws.bean;

import com.gba.ws.model.ThresholdConditionsDto;

/**
 * Provides Threshold conditions {@link ThresholdConditionsDto} details.
 * 
 * @author Mohan
 * @createdOn Jan 8, 2018 3:36:38 PM
 */
public class ConditionsBean {

	private String type = "";
	private String value = "";
	private String logicalOperation = "";

	public String getType() {
		return type;
	}

	public ConditionsBean setType(String type) {
		this.type = type;
		return this;
	}

	public String getValue() {
		return value;
	}

	public ConditionsBean setValue(String value) {
		this.value = value;
		return this;
	}

	public String getLogicalOperation() {
		return logicalOperation;
	}

	public ConditionsBean setLogicalOperation(String logicalOperation) {
		this.logicalOperation = logicalOperation;
		return this;
	}

	@Override
	public String toString() {
		return "ConditionsBean [type=" + type + ", value=" + value + ", logicalOperation=" + logicalOperation + "]";
	}

}
