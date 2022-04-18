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

import org.hibernate.annotations.Type;

/**
 * Provides User information.
 * 
 * @author Mohan
 * @createdOn Jan 8, 2018 6:00:26 PM
 */
@Entity
@Table(name = "user_details")
@NamedQueries(value = {

		@NamedQuery(name = "UserDto.findByUserId", query = "FROM UserDto UDTO WHERE UDTO.userId =:userId"),
		@NamedQuery(name = "UserDto.findByGroupId", query = "FROM UserDto UDTO WHERE UDTO.groupId =:groupId"),

		@NamedQuery(name = "UserDto.findByEmail", query = "FROM UserDto UDTO WHERE UDTO.email =:email"),

		@NamedQuery(name = "UserDto.findByEmailNPassword", query = "FROM UserDto UDTO"
				+ " WHERE UDTO.email =:email AND UDTO.userPassword =:userPassword"),

		@NamedQuery(name = "UserDto.findByVerificationKey", query = "FROM UserDto UDTO"
				+ " WHERE UDTO.verificationKey =:verificationKey"),

		@NamedQuery(name = "UserDto.fetchAllNonSignedUpUsers", query = "FROM UserDto UDTO"
				+ " WHERE UDTO.createdOn <=DATE(:createdOn) AND UDTO.userId NOT IN (" + "SELECT USDTO.userId "
				+ "FROM UserStudiesDto USDTO)"),

		@NamedQuery(name = "UserDto.deleteByUserId", query = "DELETE FROM UserDto UDTO"
				+ " WHERE UDTO.userId =:userId"),
		
		/*
		 * @NamedQuery(name = "UserDto.getAllUsersInfo", query =
		 * "FROM UserDto UDTO , UserStudiesDto USDTO " +
		 * " WHERE UDTO.userId = USDTO.userId"),
		 */
		
		@NamedQuery(name = "UserDto.getAllUsersInfo", query = "FROM UserDto UDTO"),

		@NamedQuery(name = "UserDto.findAllActiveLoggedInUsers", query = "FROM UserDto UDTO"
				+ " WHERE UDTO.userId IN (SELECT AIDTO.userId FROM AuthInfoDto AIDTO"
				/*+ " WHERE AIDTO.sessionAuthKey IS NOT NULL AND AIDTO.sessionExpiredDate >= TIMESTAMP(:sessionExpiredDate))"*/
				+ " WHERE AIDTO.sessionAuthKey IS NOT NULL )"
				+ " AND UDTO.userId IN (SELECT USDTO.userId FROM UserStudiesDto USDTO"
				+ " WHERE USDTO.active=true)"), })
public class UserDto implements Serializable {

	private static final long serialVersionUID = 6017178346753211679L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "user_id")
	private Integer userId;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "email")
	private String email;

	@Column(name = "password")
	private String userPassword;

	public void setStatus(Boolean status) {
		this.status = status;
	}

	@Column(name = "status", length = 1)
	@Type(type = "yes_no")
	private Boolean status = true;

	@Column(name = "country")
	private String country;

	@Column(name = "time_zone")
	private String timeZone;

	@Column(name = "verification_key")
	private String verificationKey;

	@Column(name = "temp_password", length = 1)
	@Type(type = "yes_no")
	private Boolean tempPassword = false;

	public Boolean getReceiveNotification() {
		return receiveNotification;
	}

	public void setReceiveNotification(Boolean receiveNotification) {
		this.receiveNotification = receiveNotification;
	}

	public void setTempPassword(Boolean tempPassword) {
		this.tempPassword = tempPassword;
	}

	@Column(name = "reset_password")
	private String resetPassword;

	@Column(name = "password_updated_date")
	private String passwordUpdatedDate;

	@Column(name = "receive_notification", length = 1)
	@Type(type = "yes_no")
	private Boolean receiveNotification = true;

	@Column(name = "receive_activity_reminders", length = 1)
	@Type(type = "yes_no")
	private Boolean receiveActivityReminders = true;

	@Column(name = "lass4u_id")
	private String lass4uId;

	@Column(name = "created_on")
	private String createdOn;

	@Column(name = "modified_on")
	private String modifiedOn;

	@Column(name = "group_id")
	private Integer groupId;

	@Column(name = "language")
	private String language;

	@Column(name = "aggreed_tnc", length = 1)
	@Type(type = "yes_no")
	private Boolean aggreedTnc = true;

	public void setAggreedTnc(Boolean aggreedTnc) {
		this.aggreedTnc = aggreedTnc;
	}

	@Column(name = "temperature")
	private String temperature;

	@Column(name = "fitbit_id")
	private String fitbitId;

	@Column(name = "temp_password_exipiry_date")
	private String tempPasswordExipiryDate;

	@Column(name = "reward_level")
	private Integer rewardLevel;

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public void setRewardLevel(Integer rewardLevel) {
		this.rewardLevel = rewardLevel;
	}
	@Column(name = "points_earned")
	private Long pointsEarned=0L;
	
	
	
	public Long getPointsEarned() {
		return pointsEarned;
	}

	public void setPointsEarned(Long pointsEarned) {
		this.pointsEarned = pointsEarned;
	}

	@Column(name="latitude")
	private String latitude;
	
	@Column(name="longitude")
	private String longitude;
	
	public String getLatitude() {
		return latitude;
	}

	public UserDto setLatitude(String latitude) {
		this.latitude = latitude;
		return this;
	}

	public String getLongitude() {
		return longitude;
	}

	public UserDto setLongitude(String longitude) {
		this.longitude = longitude;
		return this;
	}

	public int getUserId() {
		return userId;
	}

	public UserDto setUserId(int userId) {
		this.userId = userId;
		return this;
	}

	public String getFirstName() {
		return firstName;
	}

	public UserDto setFirstName(String firstName) {
		this.firstName = firstName;
		return this;
	}

	public String getLastName() {
		return lastName;
	}

	public UserDto setLastName(String lastName) {
		this.lastName = lastName;
		return this;
	}

	public String getEmail() {
		return email;
	}

	public UserDto setEmail(String email) {
		this.email = email;
		return this;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public UserDto setUserPassword(String userPassword) {
		this.userPassword = userPassword;
		return this;
	}

	public Boolean getStatus() {
		return status;
	}

	public UserDto setStatus(boolean status) {
		this.status = status;
		return this;
	}

	public String getCountry() {
		return country;
	}

	public UserDto setCountry(String country) {
		this.country = country;
		return this;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public UserDto setTimeZone(String timeZone) {
		this.timeZone = timeZone;
		return this;
	}

	public String getVerificationKey() {
		return verificationKey;
	}

	public UserDto setVerificationKey(String verificationKey) {
		this.verificationKey = verificationKey;
		return this;
	}

	public Boolean getTempPassword() {
		return tempPassword;
	}

	public UserDto setTempPassword(boolean tempPassword) {
		this.tempPassword = tempPassword;
		return this;
	}

	public String getResetPassword() {
		return resetPassword;
	}

	public UserDto setResetPassword(String resetPassword) {
		this.resetPassword = resetPassword;
		return this;
	}

	public String getPasswordUpdatedDate() {
		return passwordUpdatedDate;
	}

	public UserDto setPasswordUpdatedDate(String passwordUpdatedDate) {
		this.passwordUpdatedDate = passwordUpdatedDate;
		return this;
	}

	
	

	public boolean isReceiveNotification() {
		return receiveNotification;
	}

	public void setReceiveNotification(boolean receiveNotification) {
		this.receiveNotification = receiveNotification;
	}

	public Boolean getReceiveActivityReminders() {
		return receiveActivityReminders;
	}

	public void setReceiveActivityReminders(Boolean receiveActivityReminders) {
		this.receiveActivityReminders = receiveActivityReminders;
	}

	public Boolean getAggreedTnc() {
		return aggreedTnc;
	}

	public String getLass4uId() {
		return lass4uId;
	}

	public UserDto setLass4uId(String lass4uId) {
		this.lass4uId = lass4uId;
		return this;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public UserDto setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
		return this;
	}

	public String getModifiedOn() {
		return modifiedOn;
	}

	public UserDto setModifiedOn(String modifiedOn) {
		this.modifiedOn = modifiedOn;
		return this;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public UserDto setGroupId(Integer groupId) {
		this.groupId = groupId;
		return this;
	}

	public String getLanguage() {
		return language;
	}

	public UserDto setLanguage(String language) {
		this.language = language;
		return this;
	}

	
	
	

	public String getTemperature() {
		return temperature;
	}

	public UserDto setTemperature(String temperature) {
		this.temperature = temperature;
		return this;
	}

	public String getFitbitId() {
		return fitbitId;
	}

	public UserDto setFitbitId(String fitbitId) {
		this.fitbitId = fitbitId;
		return this;
	}

	public String getTempPasswordExipiryDate() {
		return tempPasswordExipiryDate;
	}

	public UserDto setTempPasswordExipiryDate(String tempPasswordExipiryDate) {
		this.tempPasswordExipiryDate = tempPasswordExipiryDate;
		return this;
	}

	public int getRewardLevel() {
		return this.rewardLevel;
	}

	public UserDto setRewardLevel(int rewardLevel) {
		this.rewardLevel = rewardLevel;
		return this;
	}

	@Override
	public String toString() {
		return "UserDto [userId=" + userId + ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email
				+ ", userPassword=" + userPassword + ", status=" + status + ", country=" + country + ", timeZone="
				+ timeZone + ", verificationKey=" + verificationKey + ", tempPassword=" + tempPassword
				+ ", resetPassword=" + resetPassword + ", passwordUpdatedDate=" + passwordUpdatedDate
				+ ", receiveNotification=" + receiveNotification + ", receiveActivityReminders="
				+ receiveActivityReminders + ", lass4uId=" + lass4uId + ", createdOn=" + createdOn + ", modifiedOn="
				+ modifiedOn + ", groupId=" + groupId + ", language=" + language + ", aggreedTnc=" + aggreedTnc
				+ ", temperature=" + temperature + ", fitbitId=" + fitbitId + ", tempPasswordExipiryDate="
				+ tempPasswordExipiryDate + ", rewardLevel=" + rewardLevel + ", pointsEarned=" + pointsEarned
				+ ", latitude=" + latitude + ", longitude=" + longitude + "]";
	}
	
	

	
}
