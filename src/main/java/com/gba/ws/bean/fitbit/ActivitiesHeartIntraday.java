/**
 * 
 */
package com.gba.ws.bean.fitbit;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mohan
 * @createdOn Jan 12, 2018 11:57:26 AM
 */
public class ActivitiesHeartIntraday {

	private String datasetType = "";
	private List<RestingDataset> dataset = new ArrayList<>();
	private String datasetInterval = "";

	public String getDatasetType() {
		//helloworld
		return this.datasetType;
	}

	public ActivitiesHeartIntraday setDatasetType(String datasetType) {
		this.datasetType = datasetType;
		return this;
	}

	public List<RestingDataset> getDataset() {
		//helloworld
		return this.dataset;
	}

	public ActivitiesHeartIntraday setDataset(List<RestingDataset> dataset) {
		this.dataset = dataset;
		return this;
	}

	public String getDatasetInterval() {
		return this.datasetInterval;
	}

	public ActivitiesHeartIntraday setDatasetInterval(String datasetInterval) {
		this.datasetInterval = datasetInterval;
		return this;
	}

}
