/**
 * 
 */
package com.gba.ws.bean.fitbit;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mohan
 * @createdOn Jan 12, 2018 12:07:01 PM
 */
public class IntradayActivitiesHeartIntraday {

	private String datasetType = "";
	private List<IntradayDataset> dataset = new ArrayList<>();
	private String datasetInterval = "";

	public String getDatasetType() {
		return this.datasetType;
	}

	public IntradayActivitiesHeartIntraday setDatasetType(String datasetType) {
		this.datasetType = datasetType;
		return this;
	}

	public List<IntradayDataset> getDataset() {
		return this.dataset;
	}

	public IntradayActivitiesHeartIntraday setDataset(List<IntradayDataset> dataset) {
		this.dataset = dataset;
		return this;
	}

	public String getDatasetInterval() {
		return this.datasetInterval;
	}

	public IntradayActivitiesHeartIntraday setDatasetInterval(String datasetInterval) {
		this.datasetInterval = datasetInterval;
		return this;
	}

}
