package com.gba.ws.util;

import org.springframework.stereotype.Component;

@Component
public class ListValues {
private String word;
private String questionID;
private String response;

public String getWord() {
	return word;
}
public void setWord(String word) {
	this.word = word;
}
public String getQuestionID() {
	return questionID;
}
public void setQuestionID(String questionID) {
	this.questionID = questionID;
}
public String getResponse() {
	return response;
}
public void setResponse(String response) {
	this.response = response;
}



}
