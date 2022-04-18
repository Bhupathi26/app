/**
 * 
 */
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
 * Provides User Fitbit and LASS4U Sensor details
 * 
 * @author Mohan
 * @createdOn Apr 17, 2018 5:17:58 PM
 */
@Entity
@Table(name = "fitbit_lassfouru_data")
@NamedQueries(value = {

		@NamedQuery(name = "FitbitLass4UDataDto.findByEnrollmentId", query = "FROM FitbitLass4UDataDto FLDDTO WHERE FLDDTO.enrollmentId =:enrollmentId") })
public class FitbitLass4UDataDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3220599638405522144L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "fld_id")
	private int fldId;

	@Column(name = "enrollment_id")
	private String enrollmentId;

	@Column(name = "co")
	private String co2;

	@Column(name = "light")
	private String light;

	@Column(name = "pm")
	private String pm25;

	@Column(name = "noise")
	private String noise;

	@Column(name = "temperature")
	private String temperature;

	@Column(name = "relative_humidity")
	private String relativeHumidity;

	@Column(name = "current_heart_rate")
	private String currentHeartRate;

	@Column(name = "resting_heart_rate")
	private String restingHeartRate;

	@Column(name = "heart_rate")
	private String heartRate;

	@Column(name = "sleep")
	private String sleep;

	@Column(name = "steps_count")
	private String stepsCount;

	@Column(name = "created_on")
	private String createdOn;

	public int getFldId() {
		return this.fldId;
	}

	public FitbitLass4UDataDto setFldId(int fldId) {
		this.fldId = fldId;
		return this;
	}

	public String getEnrollmentId() {
		return this.enrollmentId;
	}

	public FitbitLass4UDataDto setEnrollmentId(String enrollmentId) {
		this.enrollmentId = enrollmentId;
		return this;
	}

	public String getCo2() {
		return this.co2;
	}

	public FitbitLass4UDataDto setCo2(String co2) {
		this.co2 = co2;
		return this;
	}

	public String getLight() {
		return this.light;
	}

	public FitbitLass4UDataDto setLight(String light) {
		this.light = light;
		return this;
	}

	public String getPm25() {
		return this.pm25;
	}

	public FitbitLass4UDataDto setPm25(String pm25) {
		this.pm25 = pm25;
		return this;
	}

	public String getNoise() {
		return this.noise;
	}

	public FitbitLass4UDataDto setNoise(String noise) {
		this.noise = noise;
		return this;
	}

	public String getTemperature() {
		return this.temperature;
	}

	public FitbitLass4UDataDto setTemperature(String temperature) {
		this.temperature = temperature;
		return this;
	}

	public String getRelativeHumidity() {
		return this.relativeHumidity;
	}

	public FitbitLass4UDataDto setRelativeHumidity(String relativeHumidity) {
		this.relativeHumidity = relativeHumidity;
		return this;
	}

	public String getCurrentHeartRate() {
		return this.currentHeartRate;
	}

	public FitbitLass4UDataDto setCurrentHeartRate(String currentHeartRate) {
		this.currentHeartRate = currentHeartRate;
		return this;
	}

	public String getRestingHeartRate() {
		return this.restingHeartRate;
	}

	public FitbitLass4UDataDto setRestingHeartRate(String restingHeartRate) {
		this.restingHeartRate = restingHeartRate;
		return this;
	}

	public String getHeartRate() {
		return this.heartRate;
	}

	public FitbitLass4UDataDto setHeartRate(String heartRate) {
		this.heartRate = heartRate;
		return this;
	}

	public String getSleep() {
		return this.sleep;
	}

	public FitbitLass4UDataDto setSleep(String sleep) {
		this.sleep = sleep;
		return this;
	}

	public String getStepsCount() {
		return this.stepsCount;
	}

	public FitbitLass4UDataDto setStepsCount(String stepsCount) {
		this.stepsCount = stepsCount;
		return this;
	}

	public String getCreatedOn() {
		return this.createdOn;
	}

	public FitbitLass4UDataDto setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
		return this;
	}

	@Override
	public String toString() {
		return "FitbitLass4UDataDto [fldId=" + fldId + ", enrollmentId=" + enrollmentId + ", co2=" + co2 + ", light="
				+ light + ", pm25=" + pm25 + ", noise=" + noise + ", temperature=" + temperature + ", relativeHumidity="
				+ relativeHumidity + ", currentHeartRate=" + currentHeartRate + ", restingHeartRate=" + restingHeartRate
				+ ", heartRate=" + heartRate + ", sleep=" + sleep + ", stepsCount=" + stepsCount + ", createdOn="
				+ createdOn + "]";
	}
	
	

}
