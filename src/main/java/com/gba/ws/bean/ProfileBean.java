package com.gba.ws.bean;

/**
 * Provides profile information.
 * 
 * @author Mohan
 * @createdOn Jan 8, 2018 3:42:18 PM
 */
public class ProfileBean {

	private String firstName = "";
	private String lastName = "";
	private String email = "";
	private String groupId = "";

	/* Added By Kavya */
	private String timeZone = "";

	public String getFirstName() {
		return firstName;
	}

	public ProfileBean setFirstName(String firstName) {
		this.firstName = firstName;
		return this;
	}

	public String getLastName() {
		return lastName;
	}

	public ProfileBean setLastName(String lastName) {
		this.lastName = lastName;
		return this;
	}

	public String getEmail() {
		return email;
	}

	public ProfileBean setEmail(String email) {
		this.email = email;
		return this;
	}

	public String getGroupId() {
		return groupId;
	}

	public ProfileBean setGroupId(String groupId) {
		this.groupId = groupId;
		return this;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public ProfileBean setTimeZone(String timeZone) {
		this.timeZone = timeZone;
		return this;
	}

	@Override
	public String toString() {
		return "ProfileBean [firstName=" + firstName + ", lastName=" + lastName + ", email=" + email + ", groupId="
				+ groupId + ", timeZone=" + timeZone + "]";
	}

}
