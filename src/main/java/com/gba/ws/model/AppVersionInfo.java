package com.gba.ws.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "version_info")
@NamedQueries(value = {

		@NamedQuery(name = "AppVersionInfo.findAll", query = "FROM AppVersionInfo")
, })

public class AppVersionInfo {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "version_info_id")
	private int versionInfoId;

	@Column(name = "android")
	private String androidVersion;

	@Column(name = "ios")
	private String iosVersion = "";

	public int getVersionInfoId() {
		return versionInfoId;
	}

	public void setVersionInfoId(int versionInfoId) {
		this.versionInfoId = versionInfoId;
	}

	public String getAndroidVersion() {
		return androidVersion;
	}

	public void setAndroidVersion(String androidVersion) {
		this.androidVersion = androidVersion;
	}

	public String getIosVersion() {
		return iosVersion;
	}

	public void setIosVersion(String iosVersion) {
		this.iosVersion = iosVersion;
	}

	@Override
	public String toString() {
		return "AppVersionInfo [versionInfoId=" + versionInfoId + ", androidVersion=" + androidVersion + ", iosVersion="
				+ iosVersion + "]";
	}

}
