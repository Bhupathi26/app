package com.gba.ws.bean.fitbit;

import java.util.ArrayList;
import java.util.List;

public class Lass4UBeanSensorData {

	private String source = "";

	private List<FeedSensorData> feeds = new ArrayList<>();

	private String version = "";

	private int numOfRecords = 0;

	private String deviceId = "";

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public List<FeedSensorData> getFeeds() {
		return feeds;
	}

	public void setFeeds(List<FeedSensorData> feeds) {
		this.feeds = feeds;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public int getNumOfRecords() {
		return numOfRecords;
	}

	public void setNumOfRecords(int numOfRecords) {
		this.numOfRecords = numOfRecords;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
}
