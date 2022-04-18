package com.gba.ws.dao;

import java.util.List;

import com.gba.ws.exception.CustomException;
import com.gba.ws.model.AuthInfoDto;
import com.gba.ws.model.FitbitLass4UDataDto;
import com.gba.ws.model.FitbitLogDto;
import com.gba.ws.model.FitbitUserInfoDto;
import com.gba.ws.model.HeartRateDto;
import com.gba.ws.model.SleepDto;
import com.gba.ws.model.StepsDto;
import com.gba.ws.model.UserDto;
//helloworld
/**
 * 
 * @author Mohan
 * @createdOn Jan 8, 2018 6:36:51 PM
 */
public interface UserDao {

	/**
	 * Get the {@link UserDto} details for the provided criteria
	 * 
	 * @author Mohan
	 * @param findBy
	 *            the condition value
	 * @param findByType
	 *            the condition type
	 * @return the {@link UserDto} details
	 * @throws CustomException
	 */
	public UserDto fetchUserDetails(String findBy, String findByType) throws CustomException;
	public UserDto fetchByGroupId(int groupId) throws CustomException;
	/**
	 * Get the {@link AuthInfoDto} details for the provided criteria
	 * 
	 * @author Mohan
	 * @param findBy
	 *            the condition value
	 * @param findByType
	 *            the condition type
	 * @return the {@link AuthInfoDto} details
	 * @throws CustomException
	 */
	public AuthInfoDto fetchAuthInfoDetails(String findBy, String findByType) throws CustomException;

	/**
	 * Save or Update {@link UserDto} details for the provided type
	 * 
	 * @author Mohan
	 * @param user
	 *            the {@link UserDto} details
	 * @param type
	 *            the type of action
	 * @return the {@link UserDto} details
	 * @throws CustomException
	 */
	public UserDto saveOrUpdateUserDetails(UserDto user, String type) throws CustomException;

	/**
	 * Save or Update {@link AuthInfoDto} details for the provided type
	 * 
	 * @author Mohan
	 * @param authInfo
	 *            the {@link AuthInfoDto} details
	 * @param type
	 *            the type of action
	 * @return the {@link AuthInfoDto} details
	 * @throws CustomException
	 */
	public AuthInfoDto saveOrUpdateAuthInfoDetails(AuthInfoDto authInfo, String type) throws CustomException;

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
	 * Delete all non-signed up users to study after 30 days from registration
	 * 
	 * @author Mohan
	 * @throws CustomException
	 */
	public void deleteNonSignedUpUsersToStudy() throws CustomException;

	/**
	 * Generate the Enrollment Id's for the Study
	 * 
	 * @author Mohan
	 * @throws CustomException
	 */
	public void generateEnrollmentTokens() throws CustomException;

	/**
	 * Get the {@link FitbitUserInfoDto} details for the provided criteria
	 * 
	 * @author Mohan
	 * @param findBy
	 *            the condition value
	 * @param findByType
	 *            the condition type
	 * @return the {@link FitbitUserInfoDto} details
	 * @throws CustomException
	 */
	public FitbitUserInfoDto fetchFitbitUserInfo(String findBy, String findByType) throws CustomException;

	/**
	 * Save or Update {@link FitbitUserInfoDto} details for the type provided
	 * 
	 * @author Mohan
	 * @param fitbitUserInfo
	 *            the {@link FitbitUserInfoDto} details
	 * @param type
	 *            the type of action
	 * @return the {@link FitbitUserInfoDto} details
	 * @throws CustomException
	 */
	public FitbitUserInfoDto saveOrUpdateFitbitUserInfoDetails(FitbitUserInfoDto fitbitUserInfo, String type)
			throws CustomException;

	/**
	 * Delete Fitbit User Information for the provided criteria
	 * 
	 * @author Mohan
	 * @param findBy
	 *            the condition value
	 * @param findByType
	 *            the condition type
	 * @throws CustomException
	 */
	public void deleteFitBitUserInfo(String findBy, String findByType) throws CustomException;

	/**
	 * Get all active logged in users
	 * 
	 * @author Mohan
	 * @return the {@link UserDto} details list
	 */
	public List<UserDto> findAllActiveLoggedInUsers();

	/**
	 * Get the {@link FitbitUserInfoDto} details for the provided user identifier
	 * 
	 * @author Mohan
	 * @param userIdsList
	 *            the user identifier list
	 * @return the {@link FitbitUserInfoDto} details list
	 */
	public List<FitbitUserInfoDto> findAllFitBitUserInfoDetailsUserIdsList(List<Object> userIdsList);

	/**
	 * Get the {@link AuthInfoDto} details list for the provided user identifier
	 * list
	 * 
	 * @author Mohan
	 * @param userIdsList
	 *            the user identifier list
	 * @return the {@link AuthInfoDto} details list
	 */
	public List<AuthInfoDto> findAllAuthinfoDetailsByUserIdList(List<Object> userIdsList);

	/**
	 * Get the {@link FitbitLass4UDataDto} details for the provided condition
	 * 
	 * @author Mohan
	 * @param findBy
	 *            the condition value
	 * @param findByType
	 *            the condition type
	 * @return the {@link FitbitLass4UDataDto} details
	 * @throws CustomException
	 */
	public FitbitLass4UDataDto fetchFitbitLass4UDataDetails(String findBy, String findByType) throws CustomException;

	/**
	 * Save or Update {@link FitbitLass4UDataDto} details for the type provided
	 * 
	 * @author Mohan
	 * @param fitbitLass4UDataDto
	 *            the {@link FitbitLass4UDataDto} details
	 * @param type
	 *            the type of action
	 * @return the {@link FitbitLass4UDataDto} details
	 * @throws CustomException
	 */
	public FitbitLass4UDataDto saveOrUpdateFitbitLass4UDataDetails(FitbitLass4UDataDto fitbitLass4UDataDto, String type)
			throws CustomException;
	
	
	public List<String> getAllTimeZoneList() throws CustomException;
	
	public FitbitLogDto saveOrUpdateFitbitLogDetails(FitbitLogDto fitbitLass4UDataDto, String dbSave)
		      throws CustomException;
	
	public List<UserDto> getALLUserInfo() throws CustomException;
	
	public SleepDto saveOrUpdateFitbitSleepDetails(SleepDto sleepDto, String type) throws CustomException;
	
	public StepsDto saveOrUpdateFitbitStepsDetails(StepsDto stepsDto, String type) throws CustomException;
	
	public HeartRateDto saveOrUpdateFitbitHeartRateDetails(HeartRateDto heartRateDto, String type) throws CustomException;
	
	public void deleteFitbitSleepData(List<String> days, int userId) throws CustomException;
	
	public void deleteFitbitStepsData(List<String> days, int userId) throws CustomException;
	
	public void deleteFitbitHeartRateData(List<String> days, int userId) throws CustomException;
}
