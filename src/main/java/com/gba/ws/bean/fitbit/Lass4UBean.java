
package com.gba.ws.bean.fitbit;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Mohan
 * @createdOn Jan 12, 2018 12:22:43 PM
 */
public class Lass4UBean {

	private String source = "";

	private List<Feed> feeds = new ArrayList<>();

	private String version = "";

	@SerializedName("num_of_records")
	@Expose
	private int numOfRecords = 0;

	@SerializedName("device_id")
	@Expose
	private String deviceId = "";

	public String getSource() {
		return this.source;
	}

	public Lass4UBean setSource(String source) {
		this.source = source;
		return this;
	}

	public List<Feed> getFeeds() {
		return this.feeds;
	}

	public Lass4UBean setFeeds(List<Feed> feeds) {
		this.feeds = feeds;
		return this;
	}

	public String getVersion() {
		return this.version;
	}

	public Lass4UBean setVersion(String version) {
		this.version = version;
		return this;
	}

	public int getNumOfRecords() {
		return this.numOfRecords;
	}

	public Lass4UBean setNumOfRecords(int numOfRecords) {
		this.numOfRecords = numOfRecords;
		return this;
	}

	public String getDeviceId() {
		return this.deviceId;
	}

	public Lass4UBean setDeviceId(String deviceId) {
		this.deviceId = deviceId;
		return this;
	}

}
