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
import javax.persistence.Transient;

/**
 * Provides user fitbit information.
 * 
 * @author Mohan
 * @createdOn Nov 8, 2017 4:21:45 PM
 */
@Entity
@Table(name = "fitbit_user_info")
@NamedQueries(value = {

		@NamedQuery(name = "FitbitUserInfoDto.findByUserId", query = "FROM FitbitUserInfoDto FUDTO"
				+ " WHERE FUDTO.userId=:userId"),

		@NamedQuery(name = "FitbitUserInfoDto.findAllByUserId", query = "FROM FitbitUserInfoDto FUDTO"
				+ " WHERE FUDTO.userId IN (:userIdsList)") })
public class FitbitUserInfoDto implements Serializable {

	private static final long serialVersionUID = 5766826201310444693L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "fui_id")
	private int fuiId;

	@Column(name = "user_id")
	private int userId;

	@Column(name = "fitbit_access_token")
	private String fitbitAccessToken;

	@Column(name = "fitbit_refresh_token")
	private String fitbitRefreshToken;

	@Column(name = "created_on")
	private String createdOn;

	@Column(name = "modified_on")
	private String modifiedOn;
	
	@Transient
	private boolean disconnected;

	public int getFuiId() {
		return fuiId;
	}

	public FitbitUserInfoDto setFuiId(int fuiId) {
		this.fuiId = fuiId;
		return this;
	}

	public int getUserId() {
		return userId;
	}

	public FitbitUserInfoDto setUserId(int userId) {
		this.userId = userId;
		return this;
	}

	public String getFitbitAccessToken() {
		return fitbitAccessToken;
	}

	public FitbitUserInfoDto setFitbitAccessToken(String fitbitAccessToken) {
		this.fitbitAccessToken = fitbitAccessToken;
		return this;
	}

	public String getFitbitRefreshToken() {
		return fitbitRefreshToken;
	}

	public FitbitUserInfoDto setFitbitRefreshToken(String fitbitRefreshToken) {
		this.fitbitRefreshToken = fitbitRefreshToken;
		return this;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public FitbitUserInfoDto setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
		return this;
	}

	public String getModifiedOn() {
		return modifiedOn;
	}

	public FitbitUserInfoDto setModifiedOn(String modifiedOn) {
		this.modifiedOn = modifiedOn;
		return this;
	}

	public boolean isDisconnected() {
		return disconnected;
	}

	public void setDisconnected(boolean disconnected) {
		this.disconnected = disconnected;
	}

	@Override
	public String toString() {
		return "FitbitUserInfoDto [fuiId=" + fuiId + ", userId=" + userId + ", fitbitAccessToken=" + fitbitAccessToken
				+ ", fitbitRefreshToken=" + fitbitRefreshToken + ", createdOn=" + createdOn + ", modifiedOn="
				+ modifiedOn + ", disconnected=" + disconnected + "]";
	}
	
	

}
