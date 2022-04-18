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

import org.hibernate.annotations.Type;

/**
 * Provides Activity Question details.
 * 
 * @author Mohan
 * @createdOn Jan 8, 2018 5:53:17 PM
 */
@Entity
@Table(name = "questions")
@NamedQueries(value = {

		@NamedQuery(name = "QuestionsDto.findByQuestionId", query = "FROM QuestionsDto QDTO"
				+ " WHERE QDTO.questionId =:questionId"),

		@NamedQuery(name = "QuestionsDto.findByActivityId", query = "FROM QuestionsDto QDTO"
				+ " WHERE QDTO.activityId =:activityId"),

		@NamedQuery(name = "QuestionsDto.findByQuestionIdNActivityId", query = "FROM QuestionsDto QDTO"
				+ " WHERE QDTO.questionId =:questionId AND QDTO.activityId =:activityId"), })
public class QuestionsDto implements Serializable {

	private static final long serialVersionUID = -1844651899903890039L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "questions_id")
	private int questionId;

	@Column(name = "activity_id")
	private int activityId;

	@Column(name = "question_name")
	private String questionName;

	@Column(name = "question_text", length=500)
	private String questionText;

	@Column(name = "v_does_force_response", length = 1)
	@Type(type = "yes_no")
	private boolean vDoesForceResponse = false;

	@Column(name = "qt_type")
	private String qtType;

	@Column(name = "response_type")
	private String responseType;

	@Column(name = "response_sub_type")
	private String responseSubType;

	@Column(name = "language")
	private String language;

	@Column(name = "qualtrics_questions_id")
	private String qualtricsQuestionsId;

	public int getQuestionId() {
		return questionId;
	}

	public QuestionsDto setQuestionId(int questionId) {
		this.questionId = questionId;
		return this;
	}

	public int getActivityId() {
		return activityId;
	}

	public QuestionsDto setActivityId(int activityId) {
		this.activityId = activityId;
		return this;
	}

	public String getQuestionName() {
		return questionName;
	}

	public QuestionsDto setQuestionName(String questionName) {
		this.questionName = questionName;
		return this;
	}

	public String getQuestionText() {
		return questionText;
	}

	public QuestionsDto setQuestionText(String questionText) {
		this.questionText = questionText;
		return this;
	}

	public boolean getvDoesForceResponse() {
		return vDoesForceResponse;
	}

	public QuestionsDto setvDoesForceResponse(boolean vDoesForceResponse) {
		this.vDoesForceResponse = vDoesForceResponse;
		return this;
	}

	public String getQtType() {
		return qtType;
	}

	public QuestionsDto setQtType(String qtType) {
		this.qtType = qtType;
		return this;
	}

	public String getResponseType() {
		return responseType;
	}

	public QuestionsDto setResponseType(String responseType) {
		this.responseType = responseType;
		return this;
	}

	public String getResponseSubType() {
		return responseSubType;
	}

	public QuestionsDto setResponseSubType(String responseSubType) {
		this.responseSubType = responseSubType;
		return this;
	}

	public String getLanguage() {
		return language;
	}

	public QuestionsDto setLanguage(String language) {
		this.language = language;
		return this;
	}

	public String getQualtricsQuestionsId() {
		return qualtricsQuestionsId;
	}

	public QuestionsDto setQualtricsQuestionsId(String qualtricsQuestionsId) {
		this.qualtricsQuestionsId = qualtricsQuestionsId;
		return this;
	}

	@Override
	public String toString() {
		return "QuestionsDto [questionId=" + questionId + ", activityId=" + activityId + ", questionName="
				+ questionName + ", questionText=" + questionText + ", vDoesForceResponse=" + vDoesForceResponse
				+ ", qtType=" + qtType + ", responseType=" + responseType + ", responseSubType=" + responseSubType
				+ ", language=" + language + ", qualtricsQuestionsId=" + qualtricsQuestionsId + "]";
	}
	
	

}
