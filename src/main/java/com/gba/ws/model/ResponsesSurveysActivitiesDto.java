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
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

/**
 * @author Kavyashree
 * @createdOn July 16, 2019 4:10:49 PM
 */
@Entity
@Table(name = "respones_surveys_activities")
@NamedQueries(value = {

		@NamedQuery(name = "ResponsesSurveysActivitiesDto.findByActivityId", query = "FROM ResponsesSurveysActivitiesDto RTADTO"
				+ " WHERE RTADTO.activityId =:activityId") })
public class ResponsesSurveysActivitiesDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6958453393463456291L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "response_survey_id")
	private Integer responseSurveyId;

	@Column(name = "activity_id")
	private Integer activityId;
	
	@Column(name="condition_id")
	private Integer conditionId;
	
	@Column(name="result_type")
	private String resultType;
	
	@Column(name="end_time")
	private String endTime;
	
	@Column(name="start_time")
	private String startTime;
	
	@Column(name="total_time")
	private String totalTime;
	
	
	@Column(name = "question_skip", length = 1)
	@Type(type = "yes_no")
	private boolean questionSkip = false;

	@Column(name = "question_id")
	private String questionId;
	
	@Column(name = "response_value")
	private String responseValue;

	
	@Column(name = "response_question_type")
	private String responseQuestionType;
	
	@Column(name = "response_start_time")
	private String responseStartTime;
	

	@Column(name = "response_end_time")
	private String responseEndTime;

	@Column(name = "created_on")
	private String createdOn;
	
	@Column(name = "enrollment_id")
	private String enrollmentId;


	public Integer getResponseSurveyId() {
		return responseSurveyId;
	}


	public ResponsesSurveysActivitiesDto setResponseSurveyId(Integer responseSurveyId) {
		this.responseSurveyId = responseSurveyId;
		return this;
	}


	public Integer getActivityId() {
		return activityId;
	}


	public ResponsesSurveysActivitiesDto setActivityId(Integer activityId) {
		this.activityId = activityId;
		return this;
	}


	public Integer getConditionId() {
		return conditionId;
	}


	public ResponsesSurveysActivitiesDto setConditionId(Integer conditionId) {
		this.conditionId = conditionId;
		return this;
	}


	public String getResultType() {
		return resultType;
	}


	public ResponsesSurveysActivitiesDto setResultType(String resultType) {
		this.resultType = resultType;
		return this;
		
	}


	public String getEndTime() {
		return endTime;
	}


	public ResponsesSurveysActivitiesDto setEndTime(String endTime) {
		this.endTime = endTime;
		return this;
	}


	public String getStartTime() {
		return startTime;
	}


	public ResponsesSurveysActivitiesDto setStartTime(String startTime) {
		this.startTime = startTime;
		return this;
	}


	public String getTotalTime() {
		return totalTime;
	}


	public ResponsesSurveysActivitiesDto setTotalTime(String totalTime) {
		this.totalTime = totalTime;
		return this;
	}


	public boolean isQuestionSkip() {
		return questionSkip;
	}


	public ResponsesSurveysActivitiesDto setQuestionSkip(boolean questionSkip) {
		this.questionSkip = questionSkip;
		return this;
	}


	public String getQuestionId() {
		return questionId;
	}


	public ResponsesSurveysActivitiesDto setQuestionId(String questionId) {
		this.questionId = questionId;
		return this;
	}


	public String getResponseValue() {
		return responseValue;
	}


	public ResponsesSurveysActivitiesDto setResponseValue(String responseValue) {
		this.responseValue = responseValue;
		return this;
	}


	public String getResponseQuestionType() {
		return responseQuestionType;
	}


	public ResponsesSurveysActivitiesDto setResponseQuestionType(String responseQuestionType) {
		this.responseQuestionType = responseQuestionType;
		return this;
	}


	public String getResponseStartTime() {
		return responseStartTime;
	}


	public ResponsesSurveysActivitiesDto setResponseStartTime(String responseStartTime) {
		this.responseStartTime = responseStartTime;
		return this;
	}


	public String getResponseEndTime() {
		return responseEndTime;
	}


	public ResponsesSurveysActivitiesDto setResponseEndTime(String responseEndTime) {
		this.responseEndTime = responseEndTime;
		return this;
	}


	public String getCreatedOn() {
		return createdOn;
	}


	public ResponsesSurveysActivitiesDto setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
		return this;
	}


	public String getEnrollmentId() {
		return enrollmentId;
	}


	public ResponsesSurveysActivitiesDto setEnrollmentId(String enrollmentId) {
		this.enrollmentId = enrollmentId;
		return this;
	}


	@Override
	public String toString() {
		return "ResponsesSurveysActivitiesDto [responseSurveyId=" + responseSurveyId + ", activityId=" + activityId
				+ ", conditionId=" + conditionId + ", resultType=" + resultType + ", endTime=" + endTime
				+ ", startTime=" + startTime + ", totalTime=" + totalTime + ", questionSkip=" + questionSkip
				+ ", questionId=" + questionId + ", responseValue=" + responseValue + ", responseQuestionType="
				+ responseQuestionType + ", responseStartTime=" + responseStartTime + ", responseEndTime="
				+ responseEndTime + ", createdOn=" + createdOn + ", enrollmentId=" + enrollmentId + "]";
	}
	
	
	
}
