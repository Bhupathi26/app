/**
 * 
 */
package com.gba.ws.bean.fitbit;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mohan
 * @createdOn Jan 12, 2018 11:31:27 AM
 */
public class ActivitiesStepsIntraday {

	private String datasetType = "";
	private List<Dataset> dataset = new ArrayList<>();
	private String datasetInterval = "";

	public String getDatasetType() {
		return this.datasetType;
	}

	public ActivitiesStepsIntraday setDatasetType(String datasetType) {
		this.datasetType = datasetType;
		return this;
	}

	public List<Dataset> getDataset() {
		return this.dataset;
	}

	public ActivitiesStepsIntraday setDataset(List<Dataset> dataset) {
		this.dataset = dataset;
		return this;
	}

	public String getDatasetInterval() {
		return this.datasetInterval;
	}

	public ActivitiesStepsIntraday setDatasetInterval(String datasetInterval) {
		this.datasetInterval = datasetInterval;
		return this;
	}

}
