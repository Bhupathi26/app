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

/**
 * @author Kavyashree
 * @createdOn July 16, 2019 4:10:49 PM
 */
@Entity
@Table(name = "respones_tasks_activities")
@NamedQueries(value = {

		@NamedQuery(name = "ResponsesTasksActivitiesDto.findByActivityId", query = "FROM ResponsesTasksActivitiesDto RTADTO"
				+ " WHERE RTADTO.activityId =:activityId") })
public class ResponsesTasksActivitiesDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6958453393463456291L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "response_task_id")
	private Integer responseTaskId;

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

	@Column(name = "word")
	private String word;

	@Column(name = "question_id")
	private String questionId;
	
	@Column(name = "response_val",length=10000)
	private String responseVal;

	@Column(name = "created_on")
	private String createdOn;

	@Column(name = "enrollment_id")
	private String enrollmentId;

	public Integer getResponseTaskId() {
		return responseTaskId;
	}

	public ResponsesTasksActivitiesDto setResponseTaskId(Integer responseTaskId) {
		this.responseTaskId = responseTaskId;
		return this;
	}

	public Integer getActivityId() {
		return activityId;
	}

	public ResponsesTasksActivitiesDto setActivityId(Integer activityId) {
		this.activityId = activityId;
		return this;
	}

	public Integer getConditionId() {
		return conditionId;
	}

	public ResponsesTasksActivitiesDto setConditionId(Integer conditionId) {
		this.conditionId = conditionId;
		return this;
	}

	public String getResultType() {
		return resultType;
	}

	public ResponsesTasksActivitiesDto setResultType(String resultType) {
		this.resultType = resultType;
		return this;
	}

	public String getEndTime() {
		return endTime;
	}

	public ResponsesTasksActivitiesDto setEndTime(String endTime) {
		this.endTime = endTime;
		return this;
	}

	public String getStartTime() {
		return startTime;
	}

	public ResponsesTasksActivitiesDto setStartTime(String startTime) {
		this.startTime = startTime;
		return this;
	}

	public String getTotalTime() {
		return totalTime;
	}

	public ResponsesTasksActivitiesDto setTotalTime(String totalTime) {
		this.totalTime = totalTime;
		return this;
	}

	public String getWord() {
		return word;
	}

	public ResponsesTasksActivitiesDto setWord(String word) {
		this.word = word;
		return this;
	}

	public String getQuestionId() {
		return questionId;
	}

	public ResponsesTasksActivitiesDto setQuestionId(String questionId) {
		this.questionId = questionId;
		return this;
	}

	public String getResponseVal() {
		return responseVal;
	}

	public ResponsesTasksActivitiesDto setResponseVal(String responseVal) {
		this.responseVal = responseVal;
		return this;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public ResponsesTasksActivitiesDto setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
		return this;
	}

	public String getEnrollmentId() {
		return enrollmentId;
	}

	public ResponsesTasksActivitiesDto setEnrollmentId(String enrollmentId) {
		this.enrollmentId = enrollmentId;
		return this;
	}

	@Override
	public String toString() {
		return "ResponsesTasksActivitiesDto [responseTaskId=" + responseTaskId + ", activityId=" + activityId
				+ ", conditionId=" + conditionId + ", resultType=" + resultType + ", endTime=" + endTime
				+ ", startTime=" + startTime + ", totalTime=" + totalTime + ", word=" + word + ", questionId="
				+ questionId + ", responseVal=" + responseVal + ", createdOn=" + createdOn + ", enrollmentId="
				+ enrollmentId + "]";
	}

}
