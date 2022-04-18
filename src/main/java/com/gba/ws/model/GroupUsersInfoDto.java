package com.gba.ws.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "group_users_mapping")
@NamedQueries(value = {

		@NamedQuery(name = "GroupUsersInfoDto.findByUserId", query = "FROM GroupUsersInfoDto GUIDTO"
				+ " WHERE GUIDTO.userId =:userId") ,
		@NamedQuery(name = "GroupUsersInfoDto.findByGroupId", query = "FROM GroupUsersInfoDto GUIDTO"
				+ " WHERE GUIDTO.groupId =:groupId"),
		@NamedQuery(name = "GroupUsersInfoDto.findALL", query = "FROM GroupUsersInfoDto GUIDTO"
				+ " where GUIDTO.enabled = 'Y' ") 
})
public class GroupUsersInfoDto implements Serializable{

	private static final long serialVersionUID = 193851443027423864L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "group_users_id")
	private Integer groupUsersId;

	@Column(name = "group_id")
	private Integer groupId;
	
	@Column(name="user_id")
	private Integer userId;
	
	
	@Column(name = "status")
	@Type(type="yes_no")
	private boolean enabled;
	
	@Column(name = "created_on")
	private String createdOn;

	public Integer getGroupUsersId() {
		return groupUsersId;
	}

	public GroupUsersInfoDto setGroupUsersId(Integer groupUsersId) {
		this.groupUsersId = groupUsersId;
		return this;
		
	}

	public Integer getGroupId() {
		return groupId;
	}

	public GroupUsersInfoDto setGroupId(Integer groupId) {
		this.groupId = groupId;
		return this;
	}

	public Integer getUserId() {
		return userId;
	}

	public GroupUsersInfoDto setUserId(Integer userId) {
		this.userId = userId;
		return this;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public GroupUsersInfoDto setEnabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public GroupUsersInfoDto setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
		return this;
	}

	@Override
	public String toString() {
		return "GroupUsersInfoDto [groupUsersId=" + groupUsersId + ", groupId=" + groupId + ", userId=" + userId
				+ ", enabled=" + enabled + ", createdOn=" + createdOn + "]";
	}
	
	



	
}
