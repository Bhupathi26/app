package com.gba.ws.bean;

/**
 * Provides keywords information.
 * 
 * @author Kavyashree
 * @createdOn May 15, 2019 1:29:00 am
 */
public class KeywordsResponse {

	private String language = "";
	private KeywordsBean keywords = new KeywordsBean();

	public KeywordsBean getKeywords() {
		return keywords;
	}

	public KeywordsResponse setKeywords(KeywordsBean keywords) {
		this.keywords = keywords;
		return this;
	}

	public String getLanguage() {
		return language;
	}

	public KeywordsResponse setLanguage(String language) {
		this.language = language;
		return this;
	}

	@Override
	public String toString() {
		return "KeywordsResponse [language=" + language + ", keywords=" + keywords + "]";
	}

}
