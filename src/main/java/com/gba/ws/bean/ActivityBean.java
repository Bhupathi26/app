
package com.gba.ws.bean;

import java.util.ArrayList;
import java.util.List;

import com.gba.ws.model.CratWordsDto;

/**
 * Provides activity information.
 * 
 * @author Mohan
 * @createdOn Jan 8, 2018 3:29:28 PM
 */
public class ActivityBean {

	private String type = "";
	private MetaDataBean metadata = new MetaDataBean();
	private List<Object> steps = new ArrayList<>();
	private List<CratWordsDto> cratWordsList = new ArrayList<>();

	public String getType() {
		return type;
	}

	public ActivityBean setType(String type) {
		this.type = type;
		return this;
	}

	public MetaDataBean getMetadata() {
		return metadata;
	}

	public ActivityBean setMetadata(MetaDataBean metadata) {
		this.metadata = metadata;
		return this;
	}

	public List<Object> getSteps() {
		return steps;
	}

	public ActivityBean setSteps(List<Object> steps) {
		this.steps = steps;
		return this;
	}

	public List<CratWordsDto> getCratWordsList() {
		return cratWordsList;
	}

	public ActivityBean setCratWordsList(List<CratWordsDto> cratWordsList) {
		this.cratWordsList = cratWordsList;
		return this;
	}

	@Override
	public String toString() {
		return "ActivityBean [type=" + type + ", metadata=" + metadata + ", steps=" + steps + ", cratWordsList="
				+ cratWordsList + "]";
	}

}
