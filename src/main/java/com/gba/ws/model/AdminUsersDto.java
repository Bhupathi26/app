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
 * Provides admins users details.
 * 
 * @author Mohan
 * @createdOn Jan 8, 2018 5:49:44 PM
 */
@Entity
@Table(name = "admin_users")
@NamedQueries(value = {

		@NamedQuery(name = "AdminUsersDto.fetchAll", query = "FROM AdminUsersDto AUDTO" + " WHERE AUDTO.status=true") })
public class AdminUsersDto implements Serializable {

	private static final long serialVersionUID = 6124076139373756112L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "admin_users_id")
	private Integer adminUsersId;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "email")
	private String email;

	@Column(name = "password")
	private String password;

	@Column(name = "last_logged_on")
	private String lastLoggedOn;

	@Column(name = "status", length = 1)
	@Type(type = "yes_no")
	private Boolean status = true;

	@Column(name = "access_code")
	private String accessCode;

	@Column(name = "account_non_expired", length = 1)
	@Type(type = "yes_no")
	private Boolean accountNonExpired = true;

	@Column(name = "account_non_locked", length = 1)
	@Type(type = "yes_no")
	private Boolean accountNonLocked = true;

	@Column(name = "credentials_non_expired", length = 1)
	@Type(type = "yes_no")
	private Boolean credentialsNonExpired = true;

	@Column(name = "force_logout", length = 1)
	@Type(type = "yes_no")
	private Boolean forceLogout = true;

	@Column(name = "created_by")
	private Integer createdBy;

	@Column(name = "created_on")
	private String createdOn;

	@Column(name = "modified_by")
	private Integer modifiedBy;

	@Column(name = "modified_on")
	private String modifiedOn;

	@Column(name = "password_expairded_datetime")
	private String passwordExpairdedDatetime;

	@Column(name = "security_token")
	private String securityToken;

	@Column(name = "token_expiry_date")
	private String tokenExpiryDate;

	@Column(name = "token_used", length = 1)
	@Type(type = "yes_no")
	private Boolean tokenUsed = true;

	public Integer getAdminUsersId() {
		return adminUsersId;
	}

	public AdminUsersDto setAdminUsersId(Integer adminUsersId) {
		this.adminUsersId = adminUsersId;
		return this;
	}

	public String getFirstName() {
		return firstName;
	}

	public AdminUsersDto setFirstName(String firstName) {
		this.firstName = firstName;
		return this;
	}

	public String getLastName() {
		return lastName;
	}

	public AdminUsersDto setLastName(String lastName) {
		this.lastName = lastName;
		return this;
	}

	public String getEmail() {
		return email;
	}

	public AdminUsersDto setEmail(String email) {
		this.email = email;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public AdminUsersDto setPassword(String password) {
		this.password = password;
		return this;
	}

	public String getLastLoggedOn() {
		return lastLoggedOn;
	}

	public AdminUsersDto setLastLoggedOn(String lastLoggedOn) {
		this.lastLoggedOn = lastLoggedOn;
		return this;
	}

	public Boolean getStatus() {
		return status;
	}

	public AdminUsersDto setStatus(Boolean status) {
		this.status = status;
		return this;
	}

	public String getAccessCode() {
		return accessCode;
	}

	public AdminUsersDto setAccessCode(String accessCode) {
		this.accessCode = accessCode;
		return this;
	}

	public Boolean getAccountNonExpired() {
		return accountNonExpired;
	}

	public AdminUsersDto setAccountNonExpired(Boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
		return this;
	}

	public Boolean getAccountNonLocked() {
		return accountNonLocked;
	}

	public AdminUsersDto setAccountNonLocked(Boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
		return this;
	}

	public Boolean getCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	public AdminUsersDto setCredentialsNonExpired(Boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
		return this;
	}

	public Boolean getForceLogout() {
		return forceLogout;
	}

	public AdminUsersDto setForceLogout(Boolean forceLogout) {
		this.forceLogout = forceLogout;
		return this;
	}

	public Integer getCreatedBy() {
		return createdBy;
	}

	public AdminUsersDto setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
		return this;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public AdminUsersDto setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
		return this;
	}

	public Integer getModifiedBy() {
		return modifiedBy;
	}

	public AdminUsersDto setModifiedBy(Integer modifiedBy) {
		this.modifiedBy = modifiedBy;
		return this;
	}

	public String getModifiedOn() {
		return modifiedOn;
	}

	public AdminUsersDto setModifiedOn(String modifiedOn) {
		this.modifiedOn = modifiedOn;
		return this;
	}

	public String getPasswordExpairdedDatetime() {
		return passwordExpairdedDatetime;
	}

	public AdminUsersDto setPasswordExpairdedDatetime(String passwordExpairdedDatetime) {
		this.passwordExpairdedDatetime = passwordExpairdedDatetime;
		return this;
	}

	public String getSecurityToken() {
		return securityToken;
	}

	public AdminUsersDto setSecurityToken(String securityToken) {
		this.securityToken = securityToken;
		return this;
	}

	public String getTokenExpiryDate() {
		return tokenExpiryDate;
	}

	public AdminUsersDto setTokenExpiryDate(String tokenExpiryDate) {
		this.tokenExpiryDate = tokenExpiryDate;
		return this;
	}

	public Boolean getTokenUsed() {
		return tokenUsed;
	}

	public AdminUsersDto setTokenUsed(Boolean tokenUsed) {
		this.tokenUsed = tokenUsed;
		return this;
	}

	@Override
	public String toString() {
		return "AdminUsersDto [adminUsersId=" + adminUsersId + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", email=" + email + ", password=" + password + ", lastLoggedOn=" + lastLoggedOn + ", status="
				+ status + ", accessCode=" + accessCode + ", accountNonExpired=" + accountNonExpired
				+ ", accountNonLocked=" + accountNonLocked + ", credentialsNonExpired=" + credentialsNonExpired
				+ ", forceLogout=" + forceLogout + ", createdBy=" + createdBy + ", createdOn=" + createdOn
				+ ", modifiedBy=" + modifiedBy + ", modifiedOn=" + modifiedOn + ", passwordExpairdedDatetime="
				+ passwordExpairdedDatetime + ", securityToken=" + securityToken + ", tokenExpiryDate="
				+ tokenExpiryDate + ", tokenUsed=" + tokenUsed + "]";
	}
	
	

}
