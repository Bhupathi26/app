package com.gba.ws.bean;

import com.gba.ws.util.AppConstants;

/**
 * Provides task step details.
 * 
 * @author Mohan
 * @createdOn Jan 8, 2018 3:46:37 PM
 */
public class TaskStepBean {

	private String questionId = "";
	private String type = AppConstants.STEP_TASK;
	private String resultType = "";
	private String key = "";
	private String title = "";
	private String text = "";
	private boolean skippable = false;

	public String getQuestionId() {
		return questionId;
	}

	public TaskStepBean setQuestionId(String questionId) {
		this.questionId = questionId;
		return this;
	}

	public String getType() {
		return type;
	}

	public TaskStepBean setType(String type) {
		this.type = type;
		return this;
	}

	public String getResultType() {
		return resultType;
	}

	public TaskStepBean setResultType(String resultType) {
		this.resultType = resultType;
		return this;
	}

	public String getKey() {
		return key;
	}

	public TaskStepBean setKey(String key) {
		this.key = key;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public TaskStepBean setTitle(String title) {
		this.title = title;
		return this;
	}

	public String getText() {
		return text;
	}

	public TaskStepBean setText(String text) {
		this.text = text;
		return this;
	}

	public boolean getSkippable() {
		return skippable;
	}

	public TaskStepBean setSkippable(boolean skippable) {
		this.skippable = skippable;
		return this;
	}

	@Override
	public String toString() {
		return "TaskStepBean [questionId=" + questionId + ", type=" + type + ", resultType=" + resultType + ", key="
				+ key + ", title=" + title + ", text=" + text + ", skippable=" + skippable + "]";
	}

}
