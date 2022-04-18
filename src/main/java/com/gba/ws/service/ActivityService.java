package com.gba.ws.service;

import com.gba.ws.bean.ActivityDetailsResponse;
import com.gba.ws.bean.ActivityListResponse;
import com.gba.ws.bean.ActivityRunsResponse;
import com.gba.ws.bean.ErrorResponse;
import com.gba.ws.bean.KeywordsResponse;
import com.gba.ws.bean.Lass4USensorDataResponse;
import com.gba.ws.bean.RewardsResponse;
import com.gba.ws.bean.fitbit.HarvardIAQ;
import com.gba.ws.bean.fitbit.Lass4UBean;
import com.gba.ws.exception.CustomException;
import com.gba.ws.model.ActivityConditionDto;
import com.gba.ws.model.UserActivitiesDto;

/**
 * 
 * @author Mohan
 * @createdOn Jan 8, 2018 6:45:43 PM
 */
public interface ActivityService {

	/**
	 * Check the activity exists or not for the provided activity identifier
	 * 
	 * @author Mohan
	 * @param activityId
	 *            the activity identifier
	 * @return true or false
	 * @throws CustomException
	 */
	public boolean validateActivityId(String activityId) throws CustomException;

	/**
	 * Check the user is enrolled to study or not for the provided user and study
	 * identifier
	 * 
	 * @author Mohan
	 * @param userId
	 *            the user identifier
	 * @param studyId
	 *            the study identifier
	 * @return true or false
	 * @throws CustomException
	 */
	public boolean validateUserEnrolledToStudy(String userId, String studyId) throws CustomException;

	/**
	 * Get all the available {@link ActivityListResponse} details for the provided
	 * user and study identifier
	 * 
	 * @author Mohan
	 * @param userId
	 *            the user identifier
	 * @param studyId
	 *            the study identifier
	 * @return the {@link ActivityListResponse} details
	 * @throws CustomException
	 */
	public ActivityListResponse getActivities(String userId, String studyId) throws CustomException;

	/**
	 * Update {@link UserActivitiesDto} details for the provided user identifier,
	 * study identifier, activity identifier, last completed date time, run
	 * identifier, run state and duration.
	 * <p>
	 * Activity run states are mentioned below:
	 * <ol>
	 * <li>Start
	 * <li>Resume
	 * <li>Completed
	 * <li>Incomplete
	 * 
	 * @author Mohan
	 * @param userId
	 *            the user identifier
	 * @param studyId
	 *            the study identifier
	 * @param activityId
	 *            the activity identifier
	 * @param lastCompletedDate
	 *            the run completed date time
	 * @param runId
	 *            the run identifier
	 * @param runState
	 *            the run state
	 * @param duration
	 *            the time taken in milliseconds to complete the run
	 * @return the {@link ErrorResponse} details
	 * @throws CustomException
	 */
	public ErrorResponse updateActivityState(String userId, String studyId, String activityId, String lastCompletedDate,
			String runId, String runState, long duration) throws CustomException;

	/**
	 * 
	 * Get the {@link ActivityRunsResponse} details for the provided user and study
	 * identifier
	 * 
	 * @author Mohan
	 * @param userId
	 *            the user identifier
	 * @param studyId
	 *            the study identifier
	 * @return the {@link ActivityRunsResponse} details
	 * @throws CustomException
	 */
	public ActivityRunsResponse getActivityRun(String userId, String studyId) throws CustomException;

	/**
	 * Get the {@link ActivityDetailsResponse} details for the provided user, study
	 * and activity identifier
	 * 
	 * @author Mohan
	 * @param userId
	 *            the user identifier
	 * @param studyId
	 *            the study identifier
	 * @param activityId
	 *            the activity identifier
	 * @return the {@link ActivityDetailsResponse} details
	 * @throws CustomException
	 */
	public ActivityDetailsResponse getActivityDetails(String userId, String studyId, String activityId)
			throws CustomException;

	/**
	 * Update the {@link UserActivitiesDto} details for the provided user
	 * identifier, study identifier, activity identifier, current date, run
	 * identifier, run state and duration
	 * 
	 * @author Mohan
	 * @param userId
	 *            the user identifier
	 * @param studyId
	 *            the study identifier
	 * @param activityId
	 *            the activity identifier
	 * @param currentDate
	 *            the run completed date time
	 * @param runId
	 *            the run identifier
	 * @param runState
	 *            the run state
	 * @param duration
	 *            the time taken in milliseconds to complete the run
	 * @return the {@link ErrorResponse} details
	 * @throws CustomException
	 */
	public ErrorResponse updateThresholdActivityState(String userId, String studyId, String activityId,
			String currentDate, String runId, String runState, long duration) throws CustomException;

	/**
	 * Get the {@link RewardsResponse} details for the provided user identifer
	 * 
	 * @author Mohan
	 * @param userId
	 *            the user identifier
	 * @return the {@link RewardsResponse} details
	 * @throws CustomException
	 */
	public RewardsResponse rewards(String userId) throws CustomException;
	
	public Lass4USensorDataResponse getSensorData(String deviceId, String timeZone) throws CustomException;
	
	public String isDeviceIdExist(String deviceId);

	public boolean isLass4USensorDataExist(String lass4UId);
	
	public boolean insertSensorData(String timestamp,String app,String device_id,String s_g8,String s_t0,
			String s_d0,String s_d1,String s_h0,String s_d2,String s_n0,String s_l0);
	
	
	public KeywordsResponse getKeywordsList(String language) throws CustomException;
	
	public String getUserTimeZoneByAccessToken(String accessToken);

	
	public ActivityConditionDto fetchActivityConditionDetailsByIdandType(String activityConditionId , String type)throws CustomException;

}
