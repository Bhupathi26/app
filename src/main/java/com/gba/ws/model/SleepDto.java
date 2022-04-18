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
@Table(name = "sleep")
@NamedQueries(value = {
		@NamedQuery(name = "SleepDto.deleteSleep", query = "FROM SleepDto sl"
				+ " WHERE sl.userId =:userId and sl.date in (:date)")
})
public class SleepDto implements Serializable{
	
	private static final long serialVersionUID = 6017178346753211679L;
	
	 @Id
	  @GeneratedValue(strategy = GenerationType.AUTO)
	  @Column(name = "sleep_id")
	  private int sleepId;

	  @Column(name = "user_id")
	  private int userId;

	  @Column(name = "enrollment_id")
	  private String enrollmentId;
	  
	  @Column(name = "created_on")
	  private String createdOn;
	  
	  @Column(name = "start_time")
	  private String startTime;
	  
	  @Column(name = "end_time")
	  private String endTime;
	  
	  @Column(name = "date")
	  private String date;
	  
	  @Column(name = "minutes_asleep")
	  private int minutesAsleep;
	  
	  @Column(name = "minutes_awake")
	  private String minutesAwake;
	  
	  @Column(name = "time_in_bed")
	  private String timeInBed;
	  
	  public int getSleepId() {
		    return sleepId;
		  }

	  public SleepDto setSleepId(int sleepId) {
		  this.sleepId = sleepId;
		  return this;
	}
	  
	  public int getUserId() {
		    return userId;
		  }

	  public SleepDto setUserId(int userId) {
		  this.userId = userId;
		  return this;
	}
	  
	  
	  public String getEnrollmentId() {
		    return enrollmentId;
		  }

	  public SleepDto setEnrollmentId(String enrollmentId) {
		  this.enrollmentId = enrollmentId;
		  return this;
	}
	  
	  
	  public String getCreatedOn() {
		    return createdOn;
		  }

	  public SleepDto setCreatedOn(String createdOn) {
		  this.createdOn = createdOn;
		  return this;
	}
	  
	  
	  public String getStartTime() {
		    return startTime;
		  }

	  public SleepDto setStartTime(String startTime) {
		  this.startTime = startTime;
		  return this;
	}
	  
	  
	  public String getEndTime() {
		    return endTime;
		  }

	  public SleepDto setEndTime(String endTime) {
		  this.endTime = endTime;
		  return this;
	}
	  
	  
	  public String getDate() {
		    return date;
		  }

	  public SleepDto setDate(String date) {
		  this.date = date;
		  return this;
	}
	  
	  
	  public int getMinutesAsleep() {
		    return minutesAsleep;
		  }

	  public SleepDto setMinutesAsleep(int minutesAsleep) {
		  this.minutesAsleep = minutesAsleep;
		  return this;
	}
	  
	  public String getMinutesAwake() {
		    return minutesAwake;
		  }

	  public SleepDto setMinutesAwake(String minutesAwake) {
		  this.minutesAwake = minutesAwake;
		  return this;
	}
	  
	  public String getTimeInBed() {
		    return timeInBed;
		  }

	  public SleepDto setTimeInBed(String timeInBed) {
		  this.timeInBed = timeInBed;
		  return this;
	}

	@Override
	public String toString() {
		return "SleepDto [sleepId=" + sleepId + ", userId=" + userId + ", enrollmentId=" + enrollmentId + ", createdOn="
				+ createdOn + ", startTime=" + startTime + ", endTime=" + endTime + ", date=" + date
				+ ", minutesAsleep=" + minutesAsleep + ", minutesAwake=" + minutesAwake + ", timeInBed=" + timeInBed
				+ "]";
	}
	  
	  

		 

}
