package com.gba.ws.bean;

/**
 * Provides user profile information in response.
 * 
 * @author Mohan
 * @createdOn Jan 8, 2018 3:47:04 PM
 */
public class UserProfileResponse {

	private ErrorBean error = new ErrorBean();
	private ProfileBean profile = new ProfileBean();
	private SettingsBean settings = new SettingsBean();

	public ErrorBean getError() {
		return error;
	}

	public UserProfileResponse setError(ErrorBean error) {
		this.error = error;
		return this;
	}

	public ProfileBean getProfile() {
		return profile;
	}

	public UserProfileResponse setProfile(ProfileBean profile) {
		this.profile = profile;
		return this;
	}

	public SettingsBean getSettings() {
		return settings;
	}

	public UserProfileResponse setSettings(SettingsBean settings) {
		this.settings = settings;
		return this;
	}

	@Override
	public String toString() {
		return "UserProfileResponse [error=" + error + ", profile=" + profile + ", settings=" + settings + "]";
	}

}
