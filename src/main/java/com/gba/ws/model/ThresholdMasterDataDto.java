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
 * Provides Threshold Condition master details.
 * <p>
 * Below are the master details:
 * <ol>
 * <li>CO2
 * <li>Temperature
 * <li>Relative Humidity
 * <li>Noise
 * <li>Light
 * <li>PM2.5
 * <li>Steps
 * <li>HeartRate
 * <li>Sleep
 * 
 * @author Mohan
 * @createdOn Jan 8, 2018 5:56:16 PM
 */
@Entity
@Table(name = "threshold_master_data")
@NamedQueries(value = {

		@NamedQuery(name = "ThresholdMasterDataDto.fetchAllThresholdMasterData", query = "FROM ThresholdMasterDataDto TMDTO"),

		@NamedQuery(name = "ThresholdMasterDataDto.fetchByThresholdId", query = "FROM ThresholdMasterDataDto TMDTO"
				+ " WHERE TMDTO.thresholdId =:thresholdId"), })
public class ThresholdMasterDataDto implements Serializable {

	private static final long serialVersionUID = 7382837381144424275L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "threshold_id")
	private int thresholdId;

	@Column(name = "threshold_name")
	private String thresholdName;

	public int getThresholdId() {
		return thresholdId;
	}

	public ThresholdMasterDataDto setThresholdId(int thresholdId) {
		this.thresholdId = thresholdId;
		return this;
	}

	public String getThresholdName() {
		return thresholdName;
	}

	public ThresholdMasterDataDto setThresholdName(String thresholdName) {
		this.thresholdName = thresholdName;
		return this;
	}

	@Override
	public String toString() {
		return "ThresholdMasterDataDto [thresholdId=" + thresholdId + ", thresholdName=" + thresholdName + "]";
	}

}
