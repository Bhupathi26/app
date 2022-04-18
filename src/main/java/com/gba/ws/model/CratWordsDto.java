package com.gba.ws.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

@Entity
@Table(name = "crat_words")
@NamedQueries(value = {

		@NamedQuery(name = "CratWordsDto.findWords", query = "FROM CratWordsDto CWD")
})
public class CratWordsDto implements Serializable{
	
	private static final long serialVersionUID = 193851443027423864L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "crat_words_id")
	private Integer cratWordsId;
	
	@Column(name = "word_1")
	private String word1;
	
	@Column(name = "word_2")
	private String word2;
	
	@Column(name = "word_3")
	private String word3;
	
	@Column(name = "expected_answer")
	private String expectedAnswer;

	public Integer getCratWordsId() {
		return cratWordsId;
	}

	public CratWordsDto setCratWordsId(Integer cratWordsId) {
		this.cratWordsId = cratWordsId;
		return this;
		
	}

	public String getWord1() {
		return word1;
	}

	public CratWordsDto setWord1(String word1) {
		this.word1 = word1;
		return this;
	}
	
	public String getWord2() {
		return word2;
	}

	public CratWordsDto setWord2(String word2) {
		this.word2 = word2;
		return this;
	}
	
	public String getWord3() {
		return word3;
	}

	public CratWordsDto setWord3(String word3) {
		this.word3 = word3;
		return this;
	}
	
	public String getExpectedAnswer() {
		return expectedAnswer;
	}

	public CratWordsDto setExpectedAnswer(String expectedAnswer) {
		this.expectedAnswer = expectedAnswer;
		return this;
	}

	@Override
	public String toString() {
		return "CratWordsDto [cratWordsId=" + cratWordsId + ", word1=" + word1 + ", word2=" + word2 + ", word3=" + word3
				+ ", expectedAnswer=" + expectedAnswer + "]";
	}
	
	

}
