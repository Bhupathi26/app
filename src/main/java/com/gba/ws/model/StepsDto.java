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
@Table(name = "steps")
@NamedQueries(value = {
		@NamedQuery(name = "StepsDto.deleteSteps", query = "FROM StepsDto st"
				+ " WHERE st.userId =:userId and st.date in (:date)")
})
public class StepsDto implements Serializable{
	
	private static final long serialVersionUID = 6017178346753211679L;
	
	 @Id
	  @GeneratedValue(strategy = GenerationType.AUTO)
	  @Column(name = "steps_id")
	  private int stepsId;

	  @Column(name = "user_id")
	  private int userId;

	  @Column(name = "enrollment_id")
	  private String enrollmentId;
	  
	  @Column(name = "created_on")
	  private String createdOn;
	  
	  @Column(name = "date")
	  private String date;
	  
	  @Column(name = "steps")
	  private String steps;
	  
	  public int getStepsId() {
		    return stepsId;
		  }

	  public StepsDto setStepsId(int stepsId) {
		  this.stepsId = stepsId;
		  return this;
	}
	  
	  public int getUserId() {
		    return userId;
		  }

	  public StepsDto setUserId(int userId) {
		  this.userId = userId;
		  return this;
	}
	  
	  
	  public String getEnrollmentId() {
		    return enrollmentId;
		  }

	  public StepsDto setEnrollmentId(String enrollmentId) {
		  this.enrollmentId = enrollmentId;
		  return this;
	}
	  
	  
	  public String getCreatedOn() {
		    return createdOn;
		  }

	  public StepsDto setCreatedOn(String createdOn) {
		  this.createdOn = createdOn;
		  return this;
	}
	  
	  public String getDate() {
		    return date;
		  }

	  public StepsDto setDate(String date) {
		  this.date = date;
		  return this;
	}
	  
	  public String getSteps() {
		    return steps;
		  }

	  public StepsDto setSteps(String steps) {
		  this.steps = steps;
		  return this;
	}

	@Override
	public String toString() {
		return "StepsDto [stepsId=" + stepsId + ", userId=" + userId + ", enrollmentId=" + enrollmentId + ", createdOn="
				+ createdOn + ", date=" + date + ", steps=" + steps + "]";
	}
	  
	  

}
