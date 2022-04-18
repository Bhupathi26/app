package com.gba.ws.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Provides Study Threshold condition details.
 * 
 * @author Mohan
 * @createdOn Jan 8, 2018 5:55:51 PM
 */
@Entity
@Table(name = "threshold_conditions")
@NamedQueries(value = {

		@NamedQuery(name = "ThresholdConditionsDto.findByThresholdConditionsId", query = "FROM ThresholdConditionsDto TCDTO"
				+ " WHERE TCDTO.thresholdConditionsId =:thresholdConditionsId" + " ORDER BY TCDTO.thresholdId"),

		@NamedQuery(name = "ThresholdConditionsDto.findByConditionId", query = "FROM ThresholdConditionsDto TCDTO"
				+ " WHERE TCDTO.conditionId =:conditionId" + " ORDER BY TCDTO.thresholdId"),

		@NamedQuery(name = "ThresholdConditionsDto.findAllByConditionIds", query = "FROM ThresholdConditionsDto TCDTO"
				+ " WHERE TCDTO.applicable=true AND TCDTO.conditionId IN (:conditionIdList)"
				+ " ORDER BY TCDTO.thresholdId"), })
public class ThresholdConditionsDto implements Serializable {

	private static final long serialVersionUID = -98676838778728624L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "threshold_conditions_id")
	private int thresholdConditionsId;

	@Column(name = "condition_id")
	private int conditionId;

	@Column(name = "threshold_id")
	private int thresholdId;

	@Column(name = "applicable", length = 1)
	private boolean applicable = false;

	@Column(name = "threshold_range")
	private String thresholdRange;

	@Column(name = "value")
	private String value;

	@Column(name = "max_value")
	private String maxValue;

	public int getThresholdConditionsId() {
		return thresholdConditionsId;
	}

	public ThresholdConditionsDto setThresholdConditionsId(int thresholdConditionsId) {
		this.thresholdConditionsId = thresholdConditionsId;
		return this;
	}

	public int getConditionId() {
		return conditionId;
	}

	public ThresholdConditionsDto setConditionId(int conditionId) {
		this.conditionId = conditionId;
		return this;
	}

	public boolean getApplicable() {
		return applicable;
	}

	public ThresholdConditionsDto setApplicable(boolean applicable) {
		this.applicable = applicable;
		return this;
	}

	public String getValue() {
		return value;
	}

	public ThresholdConditionsDto setValue(String value) {
		this.value = value;
		return this;
	}

	public String getMaxValue() {
		return maxValue;
	}

	public ThresholdConditionsDto setMaxValue(String maxValue) {
		this.maxValue = maxValue;
		return this;
	}

	public int getThresholdId() {
		return thresholdId;
	}

	public ThresholdConditionsDto setThresholdId(int thresholdId) {
		this.thresholdId = thresholdId;
		return this;
	}

	public String getThresholdRange() {
		return thresholdRange;
	}

	public ThresholdConditionsDto setThresholdRange(String thresholdRange) {
		this.thresholdRange = thresholdRange;
		return this;
	}

	@Override
	public String toString() {
		return "ThresholdConditionsDto [thresholdConditionsId=" + thresholdConditionsId + ", conditionId=" + conditionId
				+ ", thresholdId=" + thresholdId + ", applicable=" + applicable + ", thresholdRange=" + thresholdRange
				+ ", value=" + value + ", maxValue=" + maxValue + "]";
	}

}
