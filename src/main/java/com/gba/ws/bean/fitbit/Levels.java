/**
 * 
 */
package com.gba.ws.bean.fitbit;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mohan
 * @createdOn Jan 12, 2018 11:48:45 AM
 */
public class Levels {

	private LevelsSummary summary = new LevelsSummary();
	private List<Data> data = new ArrayList<>();

	public LevelsSummary getSummary() {
		return this.summary;
	}

	public Levels setSummary(LevelsSummary summary) {
		this.summary = summary;
		return this;
	}

	public List<Data> getData() {
		return this.data;
	}

	public Levels setData(List<Data> data) {
		this.data = data;
		return this;
	}

}
