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

@Entity
@Table(name = "heart_rate")
@NamedQueries(value = {
		@NamedQuery(name = "HeartRateDto.deleteHeartRate", query = "FROM HeartRateDto hr"
				+ " WHERE hr.userId =:userId and hr.date in (:date)")
})
public class HeartRateDto implements Serializable{
	
	private static final long serialVersionUID = 6017178346753211679L;
	
	 @Id
	  @GeneratedValue(strategy = GenerationType.AUTO)
	  @Column(name = "heart_rate_id")
	  private int heartRateId;

	  @Column(name = "user_id")
	  private int userId;

	  @Column(name = "enrollment_id")
	  private String enrollmentId;
	  
	  @Column(name = "created_on")
	  private String createdOn;
	  
	  @Column(name = "date")
	  private String date;
	  
	  @Column(name = "resting_heart_rate")
	  private String restingHeartRate;
	  
	  @Column(name = "time")
	  private String time;
	  
	  @Column(columnDefinition="LONGTEXT" ,name = "heart_rate")
	  private String heartRate = "";
	  
	  public HeartRateDto setHeartRateId(int heartRateId) {
		  this.heartRateId = heartRateId;
		  return this;
	}
	  
	  public int getUserId() {
		    return userId;
		  }

	  public HeartRateDto setUserId(int userId) {
		  this.userId = userId;
		  return this;
	}
	  
	  
	  public String getEnrollmentId() {
		    return enrollmentId;
		  }

	  public HeartRateDto setEnrollmentId(String enrollmentId) {
		  this.enrollmentId = enrollmentId;
		  return this;
	}
	  
	  
	  public String getCreatedOn() {
		    return createdOn;
		  }

	  public HeartRateDto setCreatedOn(String createdOn) {
		  this.createdOn = createdOn;
		  return this;
	}
	  
	  public String getDate() {
		    return date;
		  }

	  public HeartRateDto setDate(String date) {
		  this.date = date;
		  return this;
	}
	  
	  public String getRestingHeartRate() {
		    return restingHeartRate;
		  }

	  public HeartRateDto setRestingHeartRate(String restingHeartRate) {
		  this.restingHeartRate = restingHeartRate;
		  return this;
	}
	  
	  public String getTime() {
		    return time;
		  }

	  public HeartRateDto setTime(String time) {
		  this.time = time;
		  return this;
	}
	  
	  public String getHeartRate() {
		    return heartRate;
		  }

	  public HeartRateDto setHeartRate(String heartRate) {
		  this.heartRate = heartRate;
		  return this;
	}

	@Override
	public String toString() {
		return "HeartRateDto [heartRateId=" + heartRateId + ", userId=" + userId + ", enrollmentId=" + enrollmentId
				+ ", createdOn=" + createdOn + ", date=" + date + ", restingHeartRate=" + restingHeartRate + ", time="
				+ time + ", heartRate=" + heartRate + "]";
	}
	  
	  

}
