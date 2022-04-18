package com.gba.ws.dao;

import java.util.List;
import java.util.Map;

import com.gba.ws.bean.KeywordsResponse;
import com.gba.ws.bean.PushNotificationBean;
import com.gba.ws.bean.fitbit.HarvardIAQ;
import com.gba.ws.exception.CustomException;
import com.gba.ws.model.ActivitiesDto;
import com.gba.ws.model.ActivityConditionDto;
import com.gba.ws.model.AppVersionInfo;
import com.gba.ws.model.CratWordsDto;
import com.gba.ws.model.CratWordsUserMapDto;
import com.gba.ws.model.GroupUsersInfoDto;
import com.gba.ws.model.QuestionChoiceDto;
import com.gba.ws.model.QuestionsDto;
import com.gba.ws.model.RewardLevelsDto;
import com.gba.ws.model.TemporalConditionDto;
import com.gba.ws.model.ThresholdConditionsDto;
import com.gba.ws.model.UserActivitiesDto;
import com.gba.ws.model.UserActivitiesRunsDto;
//helloworld
/**
 * 
 * @author Mohan
 * @createdOn Jan 8, 2018 6:01:44 PM
 */
public interface ActivityDao {

	/**
	 * Check the activity is available for the provided activity identifier
	 * 
	 * @author Mohan
	 * @param activityId
	 *            the activity identifier
	 * @return true or false
	 * @throws CustomException
	 */
	public boolean validateActivityId(String activityId) throws CustomException;

	/**
	 * Get the available active {@link ActivitiesDto} details for the provided
	 * criteria
	 * 
	 * @author Mohan
	 * @param findBy
	 *            the condition value
	 * @param findByType
	 *            the condition type
	 * @return the {@link ActivitiesDto} details
	 * @throws CustomException
	 */
	public List<ActivitiesDto> fetchActivitiesDetailsList(String findBy, String findByType) throws CustomException;

	/**
	 * Get the {@link ActivityConditionDto} details for the provided criteria
	 * 
	 * @author Mohan
	 * @param findBy
	 *            the condition value
	 * @param findByType
	 *            the condition type
	 * @return the {@link ActivityConditionDto} details list
	 * @throws CustomException
	 */
	public List<ActivityConditionDto> fetchActivityConditionDetailsList(List<Object> findBy, String findByType)
			throws CustomException;

	/**
	 * Get the {@link UserActivitiesDto} details list for the provided criteria
	 * 
	 * @author Mohan
	 * @param findBy1
	 *            the condition1 value
	 * @param findBy2
	 *            the condition2 value
	 * @param findByType
	 *            the condition type
	 * @return the {@link UserActivitiesDto} details list
	 * @throws CustomException
	 */
	public List<UserActivitiesDto> fetchUserActivityDetailsList(String findBy1, String findBy2, String findByType, String timeZone)
			throws CustomException;

	/**
	 * Save or Update the {@link UserActivitiesDto} details for the provided type
	 * 
	 * @author Mohan
	 * @param userActivitiesDto
	 *            the {@link UserActivitiesDto} details
	 * @param type
	 *            the type of action
	 * @return the {@link UserActivitiesDto} details
	 * @throws CustomException
	 */
	public UserActivitiesDto saveOrUpdateUserActivities(UserActivitiesDto userActivitiesDto, String type)
			throws CustomException;

	/**
	 * Get the {@link TemporalConditionDto} details list for the provided criteria
	 * 
	 * @author Mohan
	 * @param findBy
	 *            the condition value
	 * @param findByType
	 *            the condition type
	 * @return the {@link TemporalConditionDto} details list
	 * @throws CustomException
	 */
	public List<TemporalConditionDto> fetchTemporalConditionDetailsList(List<Object> findBy, String findByType)
			throws CustomException;

	/**
	 * Get the {@link TemporalConditionDto} details for the provided criteria
	 * 
	 * @author Mohan
	 * @param findBy
	 *            the condition value
	 * @param findByType
	 *            the condition type
	 * @return the {@link TemporalConditionDto} details
	 * @throws CustomException
	 */
	public TemporalConditionDto fetchTemporalConditionDetails(String findBy, String findByType) throws CustomException;

	/**
	 * Get the {@link ThresholdConditionsDto} details for the provided criteria
	 * 
	 * @author Mohan
	 * @param findBy
	 *            the condition value
	 * @param findByType
	 *            the consition type
	 * @return the {@link ThresholdConditionsDto} details list
	 * @throws CustomException
	 */
	public List<ThresholdConditionsDto> fetchThresholdConditionDetailsList(List<Object> findBy, String findByType)
			throws CustomException;

	/**
	 * Get the {@link ActivityConditionDto} details for the provided criteria
	 * 
	 * @author Mohan
	 * @param findBy
	 *            the condition value
	 * @param findByType
	 *            the condition type
	 * @return the {@link ActivityConditionDto} details
	 * @throws CustomException
	 */
	public ActivityConditionDto fetchActivityConditionDetails(String findBy, String findByType) throws CustomException;
	public UserActivitiesDto findByUserIdConditionId(int conditionId, int userId, String startActivityTime) throws CustomException;
	/**
	 * Get the {@link UserActivitiesDto} details for the provided criteria
	 * 
	 * @author Mohan
	 * @param findBy1
	 *            the condition1 value
	 * @param findBy2
	 *            the condition2 value
	 * @param findBy3
	 *            the condition3 value
	 * @param findByType
	 *            the condition type
	 * @return the {@link UserActivitiesDto} details
	 * @throws CustomException
	 */
	public UserActivitiesDto fetchUserActivityDetails(String findBy1, String findBy2, String findBy3, String findByType)
			throws CustomException;

	/**
	 * Get the {@link UserActivitiesRunsDto} details for the provided criteria
	 * 
	 * @author Mohan
	 * @param findBy1
	 *            the condition1 value
	 * @param findBy2
	 *            the condition2 value
	 * @param findBy3
	 *            the condition3 value
	 * @param findByType
	 *            the condition type
	 * @return the {@link UserActivitiesRunsDto} details list
	 * @throws CustomException
	 */
	public List<UserActivitiesRunsDto> fetchUserActivityRunDetailsList(String findBy1, List<Object> findBy2,
			String findBy3, String findByType) throws CustomException;

	/**
	 * Get the {@link UserActivitiesRunsDto} details for the provided criteria
	 * 
	 * @author Mohan
	 * @param findBy1
	 *            the condition1 value
	 * @param criteriaMap
	 *            the condition2 value
	 * @param findByType
	 *            the conidtion type
	 * @return the {@link UserActivitiesRunsDto} details list
	 * @throws CustomException
	 */
	public List<UserActivitiesRunsDto> fetchUserActivityRunDetailsListCriteria(String findBy1,
			Map<String, Object> criteriaMap, String findByType) throws CustomException;

	/**
	 * Save or Update {@link UserActivitiesRunsDto} details for the provided type
	 * 
	 * @author Mohan
	 * @param userActivitiesRunsDto
	 *            the {@link UserActivitiesRunsDto} details
	 * @param type
	 *            the type of action
	 * @return the {@link UserActivitiesRunsDto} details
	 * @throws CustomException
	 */
	public UserActivitiesRunsDto saveOrUpdateUserActivityRunsDetails(UserActivitiesRunsDto userActivitiesRunsDto,
			String type) throws CustomException;

	/**
	 * Get the user activities count for the provided criteria
	 * 
	 * @author Mohan
	 * @param findBy1
	 *            the condition1 value
	 * @param findBy2
	 *            the condition2 value
	 * @param findBy3
	 *            the condition3 value
	 * @param findByType
	 *            the condition type
	 * @return the activities count
	 * @throws CustomException
	 */
	public int fetchUserActivityRunsCount(String findBy1, String findBy2, String findBy3, String findByType)
			throws CustomException;

	/**
	 * Get the {@link ActivitiesDto} details for the provided criteria
	 * 
	 * @author Mohan
	 * @param findBy
	 *            the condition value
	 * @param findByType
	 *            the condition type
	 * @return the {@link ActivitiesDto} details
	 * @throws CustomException
	 */
	public ActivitiesDto fetchActivitiesDetails(String findBy, String findByType) throws CustomException;

	/**
	 * Get the {@link QuestionsDto} details for the provided criteria
	 * 
	 * @author Mohan
	 * @param findBy
	 *            the condition value
	 * @param findByType
	 *            the condition type
	 * @return the {@link QuestionsDto} details list
	 * @throws CustomException
	 */
	public List<QuestionsDto> fetchQuestionsDetailsList(String findBy, String findByType) throws CustomException;

	/**
	 * Get the {@link QuestionsDto} details for the provided criteria
	 * 
	 * @author Mohan
	 * @param findBy
	 *            the condition value
	 * @param findByType
	 *            the condition type
	 * @return the {@link QuestionsDto} details
	 * @throws CustomException
	 */
	public QuestionsDto fetchQuestionDetails(String findBy, String findByType) throws CustomException;

	/**
	 * Get the {@link QuestionChoiceDto} details for the provided criteria
	 * 
	 * @author Mohan
	 * @param findBy
	 *            the condition value
	 * @param findByType
	 *            the condition type
	 * @return the {@link QuestionChoiceDto} details list
	 * @throws CustomException
	 */
	public List<QuestionChoiceDto> fetchQuestionsChoiceDetailsList(String findBy, String findByType)
			throws CustomException;

	/**
	 * Get the {@link QuestionChoiceDto} details for the provided criteria
	 * 
	 * @author Mohan
	 * @param findBy
	 *            the condition value
	 * @param findByType
	 *            the condition type
	 * @return the {@link QuestionChoiceDto} details
	 * @throws CustomException
	 */
	public QuestionChoiceDto fetchQuestionChoiceDetails(String findBy, String findByType) throws CustomException;

	/**
	 * Get the {@link UserActivitiesRunsDto} details for the provided criteria
	 * 
	 * @author Mohan
	 * @param findBy1
	 *            the condition1 value
	 * @param criteriaMap
	 *            the condition2 value
	 * @param findByType
	 *            the condition type
	 * @return the {@link UserActivitiesRunsDto} details
	 * @throws CustomException
	 */
	public UserActivitiesRunsDto fetchUserActivityRunDetailsCriteria(String findBy1, Map<String, Object> criteriaMap,
			String findByType) throws CustomException;

	/**
	 * Get the {@link RewardLevelsDto} details for the Study
	 * 
	 * @author Mohan
	 * @return the {@link RewardLevelsDto} details list
	 * @throws CustomException
	 */
	public List<RewardLevelsDto> fetchRewardLevelsList() throws CustomException;

	/**
	 * Get the {@link ActivityConditionDto} details for the provided criteria
	 * 
	 * @author Mohan
	 * @param findBy
	 *            the condition value
	 * @param findByType
	 *            the condition type
	 * @return the {@link ActivityConditionDto} details list
	 */
	public List<ActivityConditionDto> fetchActivityConditionDetailsList(String findBy, String findByType, String timeZone);

	/**
	 * Get the {@link UserActivitiesRunsDto} details for the provided criteria
	 * 
	 * @author Mohan
	 * @param findBy1
	 *            the condition1 value
	 * @param findBy2
	 *            the condition2 value
	 * @param findBy3
	 *            the condition2 value
	 * @param findByType
	 *            the condition type
	 * @return the {@link UserActivitiesRunsDto} details
	 */
	public UserActivitiesRunsDto fetchUserActivityRunsDetails(String findBy1, String findBy2, String findBy3,
			String findByType);

	/**
	 * Get the active {@link ActivitiesDto} details list
	 * 
	 * @author Mohan
	 * @return the {@link ActivitiesDto} details list
	 */
	public List<ActivitiesDto> fetchAllActiveActivities();
	
	public List<PushNotificationBean> getUserActivityRuns(String userIds, String currentDateTime);
	
	public void saveOrUpdateUserActivityRunsDetail(String userActivityRunId) throws CustomException;
	
	/**
	 * Get Lass4U data from hb_sensor_data table
	 * 
	 * @author Pradyumn
	 * @return the {@link HarvardIAQ} details
	 */
	public HarvardIAQ fetchLass4USesorData(String lass4UId,String timeZone);
	
	public String isDeviceIdExist(String deviceId);
	
	public boolean isLass4USensorDataExist(String lass4UId);
	
	public boolean insertSensorData(String timestamp,String app,String device_id,String s_g8,String s_t0,
			String s_d0,String s_d1,String s_h0,String s_d2,String s_n0,String s_l0);
	

	public AppVersionInfo getAppVersionInfo();
	
	public KeywordsResponse getKeywordList(String language);
	
	public String getUserTimeZone(String accessToken);


	public boolean checkUserRelatedToCorrectGroup(int userId, int conditionId, int groupId,
			Map<Integer, List<GroupUsersInfoDto>> groupUsers) throws CustomException;
	
	/**
	 * Get the {@link UserActivitiesDto} details list for the provided criteria
	 * 
	 * @author Fathima
	 * @param findBy1
	 *            the condition1 value
	 * @param findBy2
	 *            the condition2 value
	 * @param findByType
	 *            the condition type
	 * @return the {@link UserActivitiesDto} details list
	 * @throws CustomException
	 */
	public List<UserActivitiesDto> fetchUserScheduledActivityDetailsList(String findBy1, String findBy2, String findByType,
			String timeZone) throws CustomException;
	
	public List<PushNotificationBean> getExpiryUserActivities(String userIds);

	void saveOrUpdateUserActivityDetail(String userActivityId) throws CustomException;
	
	public List<CratWordsUserMapDto> fetchCratWordsId(String userId) throws CustomException;
	
	public List<CratWordsDto> fetchCratWords() throws CustomException;
	
	public CratWordsUserMapDto saveCratWordsUserMap(CratWordsUserMapDto cratWordsUserMapDto) throws CustomException;
	
	public void deleteCratWordsUserMap(String userId) throws CustomException;
	public List<TemporalConditionDto> fetchTemporalActivityScheduleList(String currentDate) throws CustomException;
}
