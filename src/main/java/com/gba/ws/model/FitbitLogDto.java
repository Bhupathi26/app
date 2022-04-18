package com.gba.ws.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "fitbit_log")
public class FitbitLogDto {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "fld_id")
  private int fldId;

  @Column(name = "user_id")
  private int userId;

  @Column(name = "enrollment_id")
  private String enrollmentId;

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
    return fldId;
  }

  public FitbitLogDto setFldId(int fldId) {
    this.fldId = fldId;
    return this;
  }

  public String getEnrollmentId() {
    return enrollmentId;
  }

  public FitbitLogDto setEnrollmentId(String enrollmentId) {
    this.enrollmentId = enrollmentId;
    return this;
  }

  public String getCurrentHeartRate() {
    return currentHeartRate;
  }

  public FitbitLogDto setCurrentHeartRate(String currentHeartRate) {
    this.currentHeartRate = currentHeartRate;
    return this;
  }

  public String getRestingHeartRate() {
    return restingHeartRate;
  }

  public FitbitLogDto setRestingHeartRate(String restingHeartRate) {
    this.restingHeartRate = restingHeartRate;
    return this;
  }

  public String getHeartRate() {
    return heartRate;
  }

  public FitbitLogDto setHeartRate(String heartRate) {
    this.heartRate = heartRate;
    return this;
  }

  public String getSleep() {
    return sleep;
  }

  public FitbitLogDto setSleep(String sleep) {
    this.sleep = sleep;
    return this;
  }

  public String getStepsCount() {
    return stepsCount;
  }

  public FitbitLogDto setStepsCount(String stepsCount) {
    this.stepsCount = stepsCount;
    return this;
  }

  public String getCreatedOn() {
    return createdOn;
  }

  public FitbitLogDto setCreatedOn(String createdOn) {
    this.createdOn = createdOn;
    return this;
  }

  public int getUserId() {
    return userId;
  }

  public FitbitLogDto setUserId(int userId) {
    this.userId = userId;
    return this;
  }

@Override
public String toString() {
	return "FitbitLogDto [fldId=" + fldId + ", userId=" + userId + ", enrollmentId=" + enrollmentId
			+ ", currentHeartRate=" + currentHeartRate + ", restingHeartRate=" + restingHeartRate + ", heartRate="
			+ heartRate + ", sleep=" + sleep + ", stepsCount=" + stepsCount + ", createdOn=" + createdOn + "]";
}
  
  
}
