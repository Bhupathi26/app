package com.gba.ws.bean;

import com.gba.ws.util.AppConstants;

/**
 * Provides question step information.
 * 
 * @author Mohan
 * @createdOn Jan 8, 2018 3:42:37 PM
 */
public class QuestionStepBean {

	private String questionId = "";
	private String type = AppConstants.STEP_QUESTION;
	private String resultType = "";
	private String key = "";
	private String title = "";
	private String text = "";
	private boolean skippable = false;
	private Object format = new Object();

	public String getQuestionId() {
		return questionId;
	}

	public QuestionStepBean setQuestionId(String questionId) {
		this.questionId = questionId;
		return this;
	}

	public String getType() {
		return type;
	}

	public QuestionStepBean setType(String type) {
		this.type = type;
		return this;
	}

	public String getResultType() {
		return resultType;
	}

	public QuestionStepBean setResultType(String resultType) {
		this.resultType = resultType;
		return this;
	}

	public String getKey() {
		return key;
	}

	public QuestionStepBean setKey(String key) {
		this.key = key;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public QuestionStepBean setTitle(String title) {
		this.title = title;
		return this;
	}

	public String getText() {
		return text;
	}

	public QuestionStepBean setText(String text) {
		this.text = text;
		return this;
	}

	public boolean getSkippable() {
		return skippable;
	}

	public QuestionStepBean setSkippable(boolean skippable) {
		this.skippable = skippable;
		return this;
	}

	public Object getFormat() {
		return format;
	}

	public QuestionStepBean setFormat(Object format) {
		this.format = format;
		return this;
	}

	@Override
	public String toString() {
		return "QuestionStepBean [questionId=" + questionId + ", type=" + type + ", resultType=" + resultType + ", key="
				+ key + ", title=" + title + ", text=" + text + ", skippable=" + skippable + ", format=" + format + "]";
	}

}
