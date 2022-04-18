package com.gba.ws.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Provides user authorization and session details.
 * 
 * @author Mohan
 * @createdOn Jan 8, 2018 5:50:06 PM
 */
@Entity
@Table(name = "auth_info")
@NamedQueries(value = {

		@NamedQuery(name = "AuthInfoDto.findByUserId", query = "FROM AuthInfoDto AIDTO"
				+ " WHERE AIDTO.userId =:userId"),

		@NamedQuery(name = "AuthInfoDto.findBySessionAuthKey", query = "FROM AuthInfoDto AIDTO"
				+ " WHERE AIDTO.sessionAuthKey =:sessionAuthKey"),

		@NamedQuery(name = "AuthInfoDto.deleteByAuthInfoId", query = "DELETE" + " FROM AuthInfoDto AIDTO"
				+ " WHERE AIDTO.authInfoId =:authInfoId"),

		@NamedQuery(name = "AuthInfoDto.findAllByUserIdsList", query = "FROM AuthInfoDto AIDTO"
				+ " WHERE AIDTO.userId IN(:userIdsList)"), })
public class AuthInfoDto implements Serializable {

	private static final long serialVersionUID = 3692828379435007186L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "auth_info_id")
	private int authInfoId;

	@Column(name = "user_id")
	private int userId;

	@Column(name = "device_token")
	private String deviceToken = "";

	@Column(name = "device_type")
	private String deviceType;

	@Column(name = "auth_key")
	private String authKey;

	@Column(name = "ios_app_version")
	private String iosAppVersion;

	@Column(name = "android_app_version")
	private String androidAppVersion;

	@Column(name = "session_expired_date")
	private String sessionExpiredDate;

	@Column(name = "created_on")
	private String createdOn;

	@Column(name = "modified_on")
	private String modifiedOn;

	@Column(name = "session_auth_key")
	private String sessionAuthKey;

	public int getAuthInfoId() {
		return authInfoId;
	}

	public AuthInfoDto setAuthInfoId(int authInfoId) {
		this.authInfoId = authInfoId;
		return this;
	}

	public int getUserId() {
		return userId;
	}

	public AuthInfoDto setUserId(int userId) {
		this.userId = userId;
		return this;
	}

	public String getDeviceToken() {
		return deviceToken;
	}

	public AuthInfoDto setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
		return this;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public AuthInfoDto setDeviceType(String deviceType) {
		this.deviceType = deviceType;
		return this;
	}

	public String getAuthKey() {
		return authKey;
	}

	public AuthInfoDto setAuthKey(String authKey) {
		this.authKey = authKey;
		return this;
	}

	public String getIosAppVersion() {
		return iosAppVersion;
	}

	public AuthInfoDto setIosAppVersion(String iosAppVersion) {
		this.iosAppVersion = iosAppVersion;
		return this;
	}

	public String getAndroidAppVersion() {
		return androidAppVersion;
	}

	public AuthInfoDto setAndroidAppVersion(String androidAppVersion) {
		this.androidAppVersion = androidAppVersion;
		return this;
	}

	public String getSessionExpiredDate() {
		return sessionExpiredDate;
	}

	public AuthInfoDto setSessionExpiredDate(String sessionExpiredDate) {
		this.sessionExpiredDate = sessionExpiredDate;
		return this;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public AuthInfoDto setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
		return this;
	}

	public String getModifiedOn() {
		return modifiedOn;
	}

	public AuthInfoDto setModifiedOn(String modifiedOn) {
		this.modifiedOn = modifiedOn;
		return this;
	}

	public String getSessionAuthKey() {
		return sessionAuthKey;
	}

	public AuthInfoDto setSessionAuthKey(String sessionAuthKey) {
		this.sessionAuthKey = sessionAuthKey;
		return this;
	}

	@Override
	public String toString() {
		return "AuthInfoDto [authInfoId=" + authInfoId + ", userId=" + userId + ", deviceToken=" + deviceToken
				+ ", deviceType=" + deviceType + ", authKey=" + authKey + ", iosAppVersion=" + iosAppVersion
				+ ", androidAppVersion=" + androidAppVersion + ", sessionExpiredDate=" + sessionExpiredDate
				+ ", createdOn=" + createdOn + ", modifiedOn=" + modifiedOn + ", sessionAuthKey=" + sessionAuthKey
				+ "]";
	}
	
	

}
