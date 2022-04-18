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
 * Provides Question choice details for the Study.
 * 
 * @author Mohan
 * @createdOn Jan 8, 2018 5:52:28 PM
 */
@Entity
@Table(name = "choices")
@NamedQueries(value = {

		@NamedQuery(name = "QuestionChoiceDto.findByChoicesId", query = "FROM QuestionChoiceDto QCDTO"
				+ " WHERE QCDTO.choicesId =:choicesId"),

		@NamedQuery(name = "QuestionChoiceDto.findByQuestionId", query = "FROM QuestionChoiceDto QCDTO"
				+ " WHERE QCDTO.questionId =:questionId"), })
public class QuestionChoiceDto implements Serializable {

	private static final long serialVersionUID = -6281490333116187819L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "choices_id")
	private int choicesId;

	@Column(name = "question_id")
	private int questionId;

	@Column(name = "description")
	private String description;

	public int getChoicesId() {
		return choicesId;
	}

	public QuestionChoiceDto setChoicesId(int choicesId) {
		this.choicesId = choicesId;
		return this;
	}

	public int getQuestionId() {
		return questionId;
	}

	public QuestionChoiceDto setQuestionId(int questionId) {
		this.questionId = questionId;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public QuestionChoiceDto setDescription(String description) {
		this.description = description;
		return this;
	}

	@Override
	public String toString() {
		return "QuestionChoiceDto [choicesId=" + choicesId + ", questionId=" + questionId + ", description="
				+ description + "]";
	}
	
	

}
