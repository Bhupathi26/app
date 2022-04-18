package com.gba.ws.service;

import javax.servlet.http.HttpServletResponse;

import com.gba.ws.bean.AuthResponse;
import com.gba.ws.bean.ErrorResponse;
import com.gba.ws.bean.LoginResponse;
import com.gba.ws.bean.SettingsBean;
import com.gba.ws.bean.UserProfileResponse;
import com.gba.ws.exception.CustomException;
import com.gba.ws.model.UserDto;

/**
 * 
 * @author Mohan
 * @createdOn Jan 8, 2018 7:12:03 PM
 */
public interface UserService {

	/**
	 * Authenticate the user details for the provided user identifier, access token
	 * 
	 * @author Mohan
	 * @param authKey
	 *            the access token
	 * @param userId
	 *            the user identifier
	 * @param response
	 *            the {@link HttpServletResponse}
	 * @return the {@link ErrorResponse} details
	 * @throws CustomException
	 */
	public ErrorResponse authenticateUser(String authKey, String userId, HttpServletResponse response)
			throws CustomException;

	/**
	 * Check the session access token is exists or not for the provided session
	 * access token
	 * 
	 * @author Mohan
	 * @param sessionAuthKey
	 *            the session access token
	 * @param response
	 *            the {@link HttpServletResponse}
	 * @return the {@link AuthResponse} details
	 * @throws CustomException
	 */
	public AuthResponse validateSessionAuthKey(String sessionAuthKey, HttpServletResponse response)
			throws CustomException;

	/**
	 * Check the email is exists or not
	 * 
	 * @author Mohan
	 * @param email
	 *            the user email
	 * @return true or false
	 * @throws CustomException
	 */
	public boolean validateEmail(String email) throws CustomException;

	/**
	 * Register signed up users for the provided {@link UserDto} and Authorization
	 * details
	 * 
	 * @author Mohan
	 * @param user
	 *            the {@link UserDto} details
	 * @param authorization
	 *            the Authorization key details
	 * @return the {@link AuthResponse} details
	 * @throws CustomException
	 */
	public AuthResponse userRegistration(UserDto user, String authorization) throws CustomException;

	/**
	 * Check the provided email and password are exists or not
	 * 
	 * @author Mohan
	 * @param email
	 *            the user email
	 * @param password
	 *            the password
	 * @return the {@link LoginResponse} details
	 * @throws CustomException
	 */
	public LoginResponse login(String email, String password) throws CustomException;

	/**
	 * Verify the user email for the provided user identifier, access token and
	 * access code
	 * 
	 * @author Mohan
	 * @param userId
	 *            the user identifier
	 * @param authKey
	 *            the access token
	 * @param token
	 *            the access code
	 * @return the {@link ErrorResponse} details
	 * @throws CustomException
	 */
	public ErrorResponse emailVerification(String userId, String authKey, String token) throws CustomException;

	/**
	 * Resend the email verification token for the provided user identifier
	 * 
	 * @author Mohan
	 * @param userId
	 *            the user identifier
	 * @return the {@link ErrorResponse} details
	 * @throws CustomException
	 */
	public ErrorResponse resendVerificationToken(String userId) throws CustomException;

	/**
	 * Send the new password for the provided email
	 * 
	 * @author Mohan
	 * @param email
	 *            the user email
	 * @return the {@link ErrorResponse} details
	 * @throws CustomException
	 */
	public ErrorResponse forgotPassword(String email) throws CustomException;

	/**
	 * Get the {@link UserProfileResponse} details for the provided user identifier
	 * 
	 * @author Mohan
	 * @param userId
	 *            the user identifier
	 * @return the {@link UserProfileResponse} details
	 * @throws CustomException
	 */
	public UserProfileResponse userProfileDetails(String userId) throws CustomException;

	/**
	 * Update the user profile details for the provided user identifier
	 * 
	 * @author Mohan
	 * @param settings
	 *            the user profile settings details
	 * @param userId
	 *            the user identifier
	 * @param reminders
	 *            the remainders flag
	 * @return the {@link ErrorResponse} details
	 * @throws CustomException
	 */
	public ErrorResponse updateUserProfile(SettingsBean settings, String userId, Boolean reminders)
			throws CustomException;

	/**
	 * Update the new password for the provided user identifier.
	 * 
	 * @author Mohan
	 * @param userId
	 *            the user identifier
	 * @param oldPassword
	 *            the old password
	 * @param newPassword
	 *            the new password
	 * @return the {@link ErrorResponse} details
	 * @throws CustomException
	 */
	public ErrorResponse changePassword(String userId, String oldPassword, String newPassword) throws CustomException;

	/**
	 * Logout the user from the application
	 * 
	 * @author Mohan
	 * @param userId
	 *            the user identifier
	 * @return {@link ErrorResponse}
	 * @throws CustomException
	 */
	public ErrorResponse logout(String userId) throws CustomException;

	/**
	 * Get the new refresh token for the provided user identifier
	 * 
	 * @author Mohan
	 * @param userId
	 *            the user identifier
	 * @return the {@link AuthResponse} details
	 * @throws CustomException
	 */
	public AuthResponse refreshFitbitAccessToken(String userId) throws CustomException;

	/**
	 * Update the user device token and os type for the user
	 * 
	 * @author Mohan
	 * @param userId
	 *            the user identifier
	 * @param deviceToken
	 *            the user device token identifier
	 * @param osType
	 *            the user platform type
	 * @return the {@link ErrorResponse} details
	 */
	public ErrorResponse updateDeviceToken(String userId, String deviceToken, String osType);
	

}
