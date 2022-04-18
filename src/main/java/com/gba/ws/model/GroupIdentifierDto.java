/**
 * 
 */
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
 * @author Mohan
 * @createdOn Jan 16, 2018 4:10:49 PM
 */
@Entity
@Table(name = "groups_mstr")
@NamedQueries(value = {

		@NamedQuery(name = "GroupIdentifierDto.findByGroupId", query = "FROM GroupIdentifierDto BIDTO"
				+ " WHERE BIDTO.groupIdsName =:groupIdsName"),
		@NamedQuery(name = "GroupIdentifierDto.findByGroupIdWithoutName", query = "FROM GroupIdentifierDto BIDTO"
				+ " WHERE BIDTO.groupId =:groupId") })
public class GroupIdentifierDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6958453393463456291L;


	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "group_id")
	private Integer groupId;

	@Column(name = "groups_name")
	private String groupIdsName;
	
	@Column(name="country")
	private String country;
	
	@Column(name="latitude")
	private String latitude;
	
	@Column(name="longitude")
	private String longitude;
	
	@Column(name="group_label")
	private String groupLabel;
	
	@Transient
	private Boolean checked = false;
	
	@Transient
	private Integer groupUsers = 0;
	
	@Column(name = "group_type")
	private String groupType;

	public String getGroupType() {
		return groupType;
	}

	public GroupIdentifierDto setGroupType(String groupType) {
		this.groupType = groupType;
		return this;
	}

	public int getGroupId() {
		return this.groupId;
	}

	public GroupIdentifierDto setGroupId(int groupId) {
		this.groupId = groupId;
		return this;
	}

	public String getGroupIdsName() {
		return this.groupIdsName;
	}

	public GroupIdentifierDto setGroupIdsName(String groupIdsName) {
		this.groupIdsName = groupIdsName;
		return this;
	}

	public String getCountry() {
		return this.country;
	}

	public GroupIdentifierDto setCountry(String country) {
		this.country = country;
		return this;
	}

	public String getLatitude() {
		return this.latitude;
	}

	public GroupIdentifierDto setLatitude(String latitude) {
		this.latitude = latitude;
		return this;
	}

	public String getLongitude() {
		return this.longitude;
	}

	public GroupIdentifierDto setLongitude(String longitude) {
		this.longitude = longitude;
		return this;
	}

	public String getGroupLabel() {
		return this.groupLabel;
	}

	public GroupIdentifierDto setGroupLabel(String groupLabel) {
		this.groupLabel = groupLabel;
		return this;
	}

	public Boolean getChecked() {
		return checked;
	}

	public GroupIdentifierDto setChecked(Boolean checked) {
		this.checked = checked;
		return this;
	}

	public Integer getGroupUsers() {
		return groupUsers;
	}

	public GroupIdentifierDto setGroupUsers(Integer groupUsers) {
		this.groupUsers = groupUsers;
		return this;
	}

	@Override
	public String toString() {
		return "GroupIdentifierDto [groupId=" + groupId + ", groupIdsName=" + groupIdsName + ", country=" + country
				+ ", latitude=" + latitude + ", longitude=" + longitude + ", groupLabel=" + groupLabel + ", checked="
				+ checked + ", groupUsers=" + groupUsers + ", groupType=" + groupType + "]";
	}
	
	

	

}
