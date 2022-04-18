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

import org.apache.commons.lang.StringUtils;

import com.gba.ws.util.AppConstants;

/**
 * Provides Study Temporal Condition details.
 * 
 * @author Mohan
 * @createdOn Jan 8, 2018 5:55:19 PM
 */
@Entity
@Table(name = "temporal_condition")
@NamedQueries(value = {

		@NamedQuery(name = "TemporalConditionDto.findByTemporalConditionId", query = "FROM TemporalConditionDto TCDTO"
				+ " WHERE TCDTO.temporalConditionId =:temporalConditionId"),

		@NamedQuery(name = "TemporalConditionDto.findByConditionId", query = "FROM TemporalConditionDto TCDTO"
				+ " WHERE TCDTO.conditionId =:conditionId"),

		@NamedQuery(name = "TemporalConditionDto.findByTemporalConditionIdNConditionId", query = "FROM TemporalConditionDto TCDTO"
				+ " WHERE TCDTO.temporalConditionId =:temporalConditionId AND TCDTO.conditionId =:conditionId"),

		@NamedQuery(name = "TemporalConditionDto.findAllByConditionIds", query = "FROM TemporalConditionDto TCDTO"
				+ " WHERE TCDTO.conditionId IN (:conditionIdList)"),

		@NamedQuery(name = "TemporalConditionDto.findAllByDate", query = "FROM TemporalConditionDto TCDTO"
				+ " WHERE TCDTO.startDate<=:currentDate and TCDTO.endDate>=:currentDate"), })

public class TemporalConditionDto implements Serializable {

	private static final long serialVersionUID = -7194376037871396827L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "temporal_condition_id")
	private int temporalConditionId;

	@Column(name = "condition_id")
	private int conditionId;

	@Column(name = "start_date")
	private String startDate;

	@Column(name = "end_date")
	private String endDate;

	@Column(name = "anchor_days")
	private int anchorDays;

	@Column(name = "repetition_frequency")
	private String repetitionFrequency;

	@Column(name = "repetition_frequency_days")
	private String repetitionFrequencyDays;

	@Column(name = "start_time")
	private String startTime;

	@Column(name = "end_time")
	private String endTime;

	public Boolean getGeoFence() {
		return geoFence;
	}

	public void setGeoFence(Boolean geoFence) {
		this.geoFence = geoFence;
	}

	@Column(name="geo_fence")
	private Boolean geoFence;

	public int getTemporalConditionId() {
		return temporalConditionId;
	}

	public TemporalConditionDto setTemporalConditionId(int temporalConditionId) {
		this.temporalConditionId = temporalConditionId;
		return this;
	}

	public int getConditionId() {
		return conditionId;
	}

	public TemporalConditionDto setConditionId(int conditionId) {
		this.conditionId = conditionId;
		return this;
	}

	public String getStartDate() {
		return startDate;
	}

	public TemporalConditionDto setStartDate(String startDate) {
		this.startDate = startDate;
		return this;
	}

	public String getEndDate() {
		return endDate;
	}

	public TemporalConditionDto setEndDate(String endDate) {
		this.endDate = endDate;
		return this;
	}

	public int getAnchorDays() {
		return anchorDays;
	}

	public TemporalConditionDto setAnchorDays(int anchorDays) {
		this.anchorDays = anchorDays;
		return this;
	}

	public String getRepetitionFrequency() {
		return repetitionFrequency;
	}

	public TemporalConditionDto setRepetitionFrequency(String repetitionFrequency) {
		this.repetitionFrequency = repetitionFrequency;
		return this;
	}

	public String getRepetitionFrequencyDays() {
		return repetitionFrequencyDays;
	}

	public TemporalConditionDto setRepetitionFrequencyDays(String repetitionFrequencyDays) {
		this.repetitionFrequencyDays = repetitionFrequencyDays;
		return this;
	}

	public String getStartTime() {
		return StringUtils.isEmpty(this.startTime) ? AppConstants.START_TIME : this.startTime;
	}

	public TemporalConditionDto setStartTime(String startTime) {
		this.startTime = startTime;
		return this;
	}

	public String getEndTime() {
		return StringUtils.isEmpty(this.endTime) ? AppConstants.END_TIME : this.endTime;
	}

	public TemporalConditionDto setEndTime(String endTime) {
		this.endTime = endTime;
		return this;
	}

	@Override
	public String toString() {
		return "TemporalConditionDto [temporalConditionId=" + temporalConditionId + ", conditionId=" + conditionId
				+ ", startDate=" + startDate + ", endDate=" + endDate + ", anchorDays=" + anchorDays
				+ ", repetitionFrequency=" + repetitionFrequency + ", repetitionFrequencyDays="
				+ repetitionFrequencyDays + ", startTime=" + startTime + ", endTime=" + endTime + ", geoFence="
				+ geoFence + "]";
	}

}
