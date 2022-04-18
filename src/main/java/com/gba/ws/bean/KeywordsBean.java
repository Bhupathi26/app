package com.gba.ws.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides keywords information.
 * 
 * @author Kavyashree
 * @createdOn May 15, 2019 1:29:00 am
 */
public class KeywordsBean {

	private List<String> keyValue = new ArrayList<>();

	public List<String> getKeyValue() {
		return keyValue;
	}

	public KeywordsBean setKeyValue(List<String> keyValue) {
		this.keyValue = keyValue;
		return this;
	}

	@Override
	public String toString() {
		return "KeywordsBean [keyValue=" + keyValue + "]";
	}

}
