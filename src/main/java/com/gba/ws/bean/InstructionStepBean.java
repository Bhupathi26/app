package com.gba.ws.bean;

import com.gba.ws.util.AppConstants;

/**
 * Provides Instruction step details.
 * 
 * @author Mohan
 * @createdOn Jan 8, 2018 3:40:51 PM
 */
public class InstructionStepBean {

	private String questionId = "";
	private String type = AppConstants.STEP_INTSRUCTION;
	private String resultType = "";
	private String key = "";
	private String title = "";
	private String text = "";
	private boolean skippable = false;

	public String getQuestionId() {
		return questionId;
	}

	public InstructionStepBean setQuestionId(String questionId) {
		this.questionId = questionId;
		return this;
	}

	public String getType() {
		return type;
	}

	public InstructionStepBean setType(String type) {
		this.type = type;
		return this;
	}

	public String getResultType() {
		return resultType;
	}

	public InstructionStepBean setResultType(String resultType) {
		this.resultType = resultType;
		return this;
	}

	public String getKey() {
		return key;
	}

	public InstructionStepBean setKey(String key) {
		this.key = key;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public InstructionStepBean setTitle(String title) {
		this.title = title;
		return this;
	}

	public String getText() {
		return text;
	}

	public InstructionStepBean setText(String text) {
		this.text = text;
		return this;
	}

	public boolean getSkippable() {
		return skippable;
	}

	public InstructionStepBean setSkippable(boolean skippable) {
		this.skippable = skippable;
		return this;
	}

	@Override
	public String toString() {
		return "InstructionStepBean [questionId=" + questionId + ", type=" + type + ", resultType=" + resultType
				+ ", key=" + key + ", title=" + title + ", text=" + text + ", skippable=" + skippable + "]";
	}

}
