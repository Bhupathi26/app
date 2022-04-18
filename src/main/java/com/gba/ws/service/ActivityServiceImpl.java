package com.gba.ws.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.gba.ws.bean.ActivityBean;
import com.gba.ws.bean.ActivityDetailsResponse;
import com.gba.ws.bean.ActivityListBean;
import com.gba.ws.bean.ActivityListResponse;
import com.gba.ws.bean.ActivityRunBean;
import com.gba.ws.bean.ActivityRunsResponse;
import com.gba.ws.bean.ConditionsBean;
import com.gba.ws.bean.ErrorBean;
import com.gba.ws.bean.ErrorResponse;
import com.gba.ws.bean.InstructionStepBean;
import com.gba.ws.bean.KeywordsResponse;
import com.gba.ws.bean.Lass4USensorDataResponse;
import com.gba.ws.bean.MetaDataBean;
import com.gba.ws.bean.QuestionStepBean;
import com.gba.ws.bean.RewardLevelBean;
import com.gba.ws.bean.RewardsResponse;
import com.gba.ws.bean.RunBean;
import com.gba.ws.bean.TaskStepBean;
import com.gba.ws.bean.fitbit.FeedSensorData;
import com.gba.ws.bean.fitbit.HarvardIAQ;
import com.gba.ws.bean.fitbit.HarvardIAQSensorData;
import com.gba.ws.bean.fitbit.Lass4UBeanSensorData;
import com.gba.ws.dao.ActivityDao;
import com.gba.ws.dao.StudyDao;
import com.gba.ws.dao.UserDao;
import com.gba.ws.exception.CustomException;
import com.gba.ws.model.ActivitiesDto;
import com.gba.ws.model.ActivityGroupDto;
import com.gba.ws.model.ActivityConditionDto;
import com.gba.ws.model.AdminUsersDto;
import com.gba.ws.model.CratWordsDto;
import com.gba.ws.model.CratWordsUserMapDto;
import com.gba.ws.model.EnrollmentTokensDto;
import com.gba.ws.model.QuestionChoiceDto;
import com.gba.ws.model.QuestionsDto;
import com.gba.ws.model.RewardLevelsDto;
import com.gba.ws.model.StudiesDto;
import com.gba.ws.model.StudyConsentDto;
import com.gba.ws.model.TemporalConditionDto;
import com.gba.ws.model.ThresholdConditionsDto;
import com.gba.ws.model.UserActivitiesDto;
import com.gba.ws.model.UserActivitiesRunsDto;
import com.gba.ws.model.UserDto;
import com.gba.ws.model.UserStudiesDto;
import com.gba.ws.util.AppConstants;
import com.gba.ws.util.AppEnums;
import com.gba.ws.util.AppUtil;
import com.gba.ws.util.ErrorCode;
import com.gba.ws.util.Mail;
import com.gba.ws.util.MailContent;

/**
 * Implements {@link ActivityService} interface.
 * 
 * @author Mohan
 * @createdOn Jan 9, 2018 10:52:44 AM
 */
@Service
public class ActivityServiceImpl implements ActivityService {

	private static final Logger LOGGER = Logger.getLogger(ActivityServiceImpl.class);

	@Autowired
	private ActivityDao activityDao;

	@Autowired
	private StudyDao studyDao;

	@Autowired
	private UserDao userDao;

	/**
	 * @author Mohan
	 * @param activityDao
	 *            the {@link ActivityDao}
	 * @param studyDao
	 *            the {@link StudyDao}
	 * @param userDao
	 *            the {@link UserDao}
	 */
	public ActivityServiceImpl(ActivityDao activityDao, StudyDao studyDao, UserDao userDao) {
		super();
		this.activityDao = activityDao;
		this.studyDao = studyDao;
		this.userDao = userDao;
	}

	@Override
	public boolean validateActivityId(String activityId) throws CustomException {
		LOGGER.info("INFO: ActivityServiceImpl - validateActivityId() :: starts");
		boolean isValidActivityId = false;
		try {
			isValidActivityId = activityDao.validateActivityId(activityId);
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityServiceImpl - validateActivityId()", e);
		}
		LOGGER.info("INFO: ActivityServiceImpl - validateActivityId() :: ends");
		return isValidActivityId;
	}

	@Override
	public boolean validateUserEnrolledToStudy(String userId, String studyId) throws CustomException {
		LOGGER.info("INFO: ActivityServiceImpl - validateUserEnrolledToStudy() :: starts");
		boolean isUserEnrolledToStudy = false;
		UserStudiesDto userStudies = null;
		StudyConsentDto studyConsent = null;
		try {
			// Validate the user has enrolled to the study and study consent details is
			// present or not
			studyConsent = studyDao.fetchStudyConsentDetails(userId, String.valueOf(studyId),
					AppConstants.FIND_BY_TYPE_USERID_STUDYID);
			if (studyConsent == null) {
				return isUserEnrolledToStudy;
			}

			userStudies = studyDao.fetchUserStudiesDetails(userId, studyId, AppConstants.FIND_BY_TYPE_USERID_STUDYID);
			if (userStudies == null) {
				return isUserEnrolledToStudy;
			}

			isUserEnrolledToStudy = true;
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityServiceImpl - validateUserEnrolledToStudy()", e);
		}
		LOGGER.info("INFO: ActivityServiceImpl - validateUserEnrolledToStudy() :: ends");
		return isUserEnrolledToStudy;
	}

	@Override
	public ActivityListResponse getActivities(String userId, String studyId) throws CustomException {
		LOGGER.info("INFO: ActivityServiceImpl - getActivities() :: starts");
		ActivityListResponse activityListResponse = new ActivityListResponse();
		List<ActivitiesDto> activitiesList = new ArrayList<>();
		UserStudiesDto userStudies = null;
		List<Object> activityConditionIdList = new ArrayList<>();
		UserDto user = null;
		List<ActivityListBean> activities = new ArrayList<>();
		EnrollmentTokensDto enrollmentTokenDto = null;
		List<ActivityGroupDto> activityGroupList = new ArrayList<>();
		try {

			activityListResponse.setError(new ErrorBean().setCode(ErrorCode.EC_200.code())
					.setMessage(AppUtil.getAppProperties().get(AppConstants.ACTIVITY_LIST_SUCCESS)));
			activityListResponse.setActivities(activities);

			user = userDao.fetchUserDetails(userId, AppConstants.FIND_BY_TYPE_USERID);

			userStudies = studyDao.fetchUserStudiesDetails(userId, studyId, AppConstants.FIND_BY_TYPE_USERID_STUDYID);

			enrollmentTokenDto = studyDao.fetchEnrollmentTokenDetails(userStudies.getEnrollmentId(),
					AppConstants.FIND_BY_TYPE_ENROLLMENTID);

			activitiesList = activityDao.fetchActivitiesDetailsList(studyId, AppConstants.FIND_BY_TYPE_STUDYID);
			if (null != activitiesList && !activitiesList.isEmpty()) {
				List<Object> activityIds = activitiesList.stream().map(ActivitiesDto::getActivityId)
						.collect(Collectors.toList());
				Map<Integer, List<ActivitiesDto>> activitiesMap = activitiesList.stream()
						.collect(Collectors.groupingBy(ActivitiesDto::getActivityId));
				List<ActivityConditionDto> activityConditionDtoList = activityDao
						.fetchActivityConditionDetailsList(activityIds, AppConstants.FIND_BY_TYPE_ACTIVITY_IDS);
				if (!activityConditionDtoList.isEmpty()) {
					
					/*activityGroupList = studyDao.fetchActivityGroupsList(enrollmentTokenDto.getGroupId(),
							AppConstants.FIND_BY_TYPE_GROUPID);*/
					
					activityGroupList = studyDao.fetchActivityGroupsListByUserId(user.getUserId(), user.getGroupId(),
							AppConstants.FIND_BY_TYPE_GROUPID_LIST);
					Map<Integer, List<ActivityGroupDto>> activityGroupMap = activityGroupList.parallelStream()
							.collect(Collectors.groupingBy(ActivityGroupDto::getConditionId));
					for (ActivityConditionDto activityConditionDto : activityConditionDtoList) {
						String activityLanguage = AppConstants.USER_LANGUAGE_CHINESE_QUALTRICS.equalsIgnoreCase(
								activitiesMap.get(activityConditionDto.getActivityId()).get(0).getLanguage())
										? AppConstants.USER_LANGUAGE_CHINESE
										: activitiesMap.get(activityConditionDto.getActivityId()).get(0).getLanguage();
						LOGGER.info("::::::::::::" + activityGroupMap.containsKey(activityConditionDto.getConditionId()) + "::::::::::::"
								+  user.getLanguage().equalsIgnoreCase(activityLanguage));
						// Check the activity is associated with the group id
						if (activityGroupMap.containsKey(activityConditionDto.getConditionId())
								&& user.getLanguage().equalsIgnoreCase(activityLanguage)) {
							LOGGER.info("::::::::::::" + activityLanguage + "::::::::::::"
									+ activityConditionDto.getActivityConditionName());
							Map<String, Object> activityListMap = new HashMap<>();
							activityListMap.put("user", user);
							activityListMap.put("userStudies", userStudies);

							activityListMap.put("activityDetails",
									activitiesMap.get(activityConditionDto.getActivityId()).get(0));

							activityListMap.put("activityConditionDetails", activityConditionDto);
							activityConditionIdList.add(activityConditionDto.getConditionId());

							List<ThresholdConditionsDto> thresholdConditionsList = null;
							if (activityConditionDto.getActivitySubType()
									.equals(AppConstants.ACTIVITY_SUB_TYPE_TRIGGERED)) {
								thresholdConditionsList = activityDao.fetchThresholdConditionDetailsList(
										activityConditionIdList, AppConstants.FIND_BY_TYPE_CONDITIONID);
							}
							activityListMap.put("thresholdConditionDetails", thresholdConditionsList);

							TemporalConditionDto temporalConditionDto = activityDao.fetchTemporalConditionDetails(
									String.valueOf(activityConditionDto.getConditionId()),
									AppConstants.FIND_BY_TYPE_CONDITIONID);
							activityListMap.put("temporalConditionDetails", temporalConditionDto);

							UserActivitiesDto userActivitiesDto = this.getUserActivitiesDetails(user, userStudies,
									activityConditionDto, temporalConditionDto);
							activityListMap.put("userActivityDetails", userActivitiesDto);

							ActivityListBean activityListBean = this.getActivityListMetaData(activityListMap);
							activities.add(activityListBean);
						}
					}
				}
				activityListResponse.setError(new ErrorBean().setCode(ErrorCode.EC_200.code())
						.setMessage(AppUtil.getAppProperties().get(AppConstants.ACTIVITY_LIST_SUCCESS)));
				activityListResponse.setActivities(activities);
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityServiceImpl - getActivities()", e);
		}
		LOGGER.info("INFO: ActivityServiceImpl - getActivities() :: ends");
		return activityListResponse;
	}

	@Override
	public ErrorResponse updateActivityState(String userId, String studyId, String activityId, String lastCompletedDate,
			String runId, String runState, long duration) throws CustomException {
		LOGGER.info("INFO: ActivityServiceImpl - updateActivityState() :: starts"+"userId:"+userId+" studyId:"+studyId
				+" activityId:"+activityId+" lastCompletedDate:"+lastCompletedDate+" runId"+runId+" runState"+runState+
				"duration:"+duration);
		ErrorResponse errorResponse = new ErrorResponse();
		ActivityConditionDto activityCondition = null;
		UserActivitiesDto userActivity = null;
		boolean updateUserActivityFlag = false;
		UserDto user = null;
		UserStudiesDto userStudies = null;
		Map<String, Object> map = null;
		try {
			errorResponse.getError().setCode(ErrorCode.EC_404.code()).setMessage(ErrorCode.EC_404.errorMessage());

			user = userDao.fetchUserDetails(userId, AppConstants.FIND_BY_TYPE_USERID);
			if (user == null) {
				errorResponse.setError(
						new ErrorBean().setCode(ErrorCode.EC_61.code()).setMessage(ErrorCode.EC_61.errorMessage()));
				return errorResponse;
			}

			userStudies = studyDao.fetchUserStudiesDetails(userId, studyId, AppConstants.FIND_BY_TYPE_USERID_STUDYID);
			if (userStudies == null) {
				errorResponse.setError(
						new ErrorBean().setCode(ErrorCode.EC_93.code()).setMessage(ErrorCode.EC_93.errorMessage()));
				return errorResponse;
			}

			activityCondition = activityDao.fetchActivityConditionDetails(activityId,
					AppConstants.FIND_BY_TYPE_ACTIVITY_CONDITIONID);
			if (activityCondition == null) {
				errorResponse.getError().setCode(ErrorCode.EC_45.code()).setMessage(ErrorCode.EC_45.errorMessage());
				return errorResponse;
			}

			userActivity = activityDao.fetchUserActivityDetails(userId,
					String.valueOf(activityCondition.getConditionId()), String.valueOf(userStudies.getUserStudiesId()),
					AppConstants.FIND_BY_TYPE_USERID_CONDITIONID);
			if (userActivity == null) {
				errorResponse.getError().setCode(ErrorCode.EC_45.code()).setMessage(ErrorCode.EC_45.errorMessage());
				return errorResponse;
			}

			switch (runState) {
				case AppConstants.RUN_STATE_COMPLETED:
					map = this.getCompletedActivityStatePrerequisists(lastCompletedDate, runId, runState, activityCondition,
							userActivity, user, errorResponse);
					updateUserActivityFlag = (boolean) map.get("updateUserActivityFlag");
					errorResponse = (ErrorResponse) map.get("errorResponse");
					userActivity = (UserActivitiesDto) map.get("userActivity");
					break;
				case AppConstants.RUN_STATE_RESUME:
					updateUserActivityFlag = true;
					break;
				default:
					errorResponse.getError().setCode(ErrorCode.EC_105.code()).setMessage(ErrorCode.EC_105.errorMessage());
					break;
			}

			if (updateUserActivityFlag) {
				if (runState.equals(AppConstants.RUN_STATE_COMPLETED)) {
					userActivity
							.setLastCompletedDate(AppUtil.getFormattedDateByTimeZone(lastCompletedDate,
									AppConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_FORMAT,
									AppConstants.SDF_DATE_TIME_FORMAT, user.getTimeZone()))
							.setLastCompletedRunId(Integer.parseInt(runId)).setActivityRunId(Integer.parseInt(runId));
				}

				userActivity.setActivityStatus(runState).setModifiedOn(AppUtil.getCurrentDateTime());
				activityDao.saveOrUpdateUserActivities(userActivity, AppConstants.DB_UPDATE);

				if (runState.equals(AppConstants.RUN_STATE_COMPLETED)) {
					this.notifyParticipantsAboutNewLevelAndRewardPoints(userId, studyId, duration, activityCondition);
				}

				errorResponse.getError().setCode(ErrorCode.EC_200.code())
						.setMessage(AppUtil.getAppProperties().get(AppConstants.UPDATE_ACTIVITY_STATE));
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityServiceImpl - updateActivityState()", e);
		}
		LOGGER.info("INFO: ActivityServiceImpl - updateActivityState() :: ends");
		return errorResponse;
	}

	@Override
	public ActivityRunsResponse getActivityRun(String userId, String studyId) throws CustomException {
		LOGGER.info("INFO: ActivityServiceImpl - getActivityRun() :: starts");
		ActivityRunsResponse activityRunsResponse = new ActivityRunsResponse();
		UserStudiesDto userStudies = null;
		UserDto user = null;
		List<UserActivitiesDto> userActivitiesList = new ArrayList<>();
		List<ActivityRunBean> activities = new ArrayList<>();
		EnrollmentTokensDto enrollmentTokenDto = null;
		List<ActivityGroupDto> activityGroupList = new ArrayList<>();
		try {
			activityRunsResponse.setActivities(activities).setError(new ErrorBean().setCode(ErrorCode.EC_200.code())
					.setMessage(AppUtil.getAppProperties().get(AppConstants.ACTIVITY_RUNS)));

			user = userDao.fetchUserDetails(userId, AppConstants.FIND_BY_TYPE_USERID);
			userStudies = studyDao.fetchUserStudiesDetails(userId, studyId, AppConstants.FIND_BY_TYPE_USERID_STUDYID);
			enrollmentTokenDto = studyDao.fetchEnrollmentTokenDetails(userStudies.getEnrollmentId(),
					AppConstants.FIND_BY_TYPE_ENROLLMENTID);
			userActivitiesList = activityDao.fetchUserActivityDetailsList(userId,
					String.valueOf(userStudies.getUserStudiesId()), AppConstants.FIND_BY_TYPE_USERID_STUDYID, user.getTimeZone());
			if (!userActivitiesList.isEmpty() && StringUtils.isNotEmpty(String.valueOf(user.getGroupId()))) {
				//activityGroupList = studyDao.fetchActivityGroupsList(enrollmentTokenDto.getGroupId(),AppConstants.FIND_BY_TYPE_GROUPID);
				activityGroupList = studyDao.fetchActivityGroupsListByUserId(userStudies.getUserId(), user.getGroupId(),AppConstants.FIND_BY_TYPE_GROUPID_LIST);
				Map<Integer, List<ActivityGroupDto>> activityGroupMap = activityGroupList.stream()
						.collect(Collectors.groupingBy(ActivityGroupDto::getConditionId));
				for (UserActivitiesDto userActivitiesDto : userActivitiesList) {
					ActivityConditionDto activityCondition = activityDao.fetchActivityConditionDetails(
							String.valueOf(userActivitiesDto.getConditionId()), AppConstants.FIND_BY_TYPE_CONDITIONID);
					if (activityCondition != null
							&& activityGroupMap.containsKey(activityCondition.getConditionId())) {
						TemporalConditionDto temporalConditionDto = activityDao.fetchTemporalConditionDetails(
								String.valueOf(activityCondition.getConditionId()),
								AppConstants.FIND_BY_TYPE_CONDITIONID);
						if (temporalConditionDto != null) {
							userActivitiesDto = this.updateActivityRunDetails(user, userStudies, activityCondition,
									userActivitiesDto, temporalConditionDto);

							ActivityRunBean activityRunBean = this.getActivityRuns(temporalConditionDto,
									activityCondition, userActivitiesDto, userStudies, user);
							activities.add(activityRunBean);
						}
					}
				}
				activityRunsResponse.setActivities(activities).setError(new ErrorBean().setCode(ErrorCode.EC_200.code())
						.setMessage(AppUtil.getAppProperties().get(AppConstants.ACTIVITY_RUNS)));
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityServiceImpl - getActivityRun()", e);
		}
		LOGGER.info("INFO: ActivityServiceImpl - getActivityRun() :: ends");
		return activityRunsResponse;
	}

	@Override
	public ActivityDetailsResponse getActivityDetails(String userId, String studyId, String activityId)
			throws CustomException {
		LOGGER.info("INFO: ActivityServiceImpl - getActivityDetails() :: starts");
		ActivityDetailsResponse activityDetailsResponse = new ActivityDetailsResponse();
		ActivityBean activity = new ActivityBean();
		MetaDataBean metaData = new MetaDataBean();
		List<Object> steps = new ArrayList<>();
		ActivityConditionDto activityConditionDto = null;
		List<QuestionsDto> questionsList = null;
		TemporalConditionDto temopralCondition = null;
		UserDto user = null;
		List<String> activityTypeList = new ArrayList<>();
		List<CratWordsDto> cratWordsListResponse = null;
		try {
			activityDetailsResponse.setError(
					new ErrorBean().setCode(ErrorCode.EC_404.code()).setMessage(ErrorCode.EC_404.errorMessage()));

			activityConditionDto = activityDao.fetchActivityConditionDetails(activityId,
					AppConstants.FIND_BY_TYPE_ACTIVITY_CONDITIONID);
			if (activityConditionDto != null) {
				user = userDao.fetchUserDetails(userId, AppConstants.FIND_BY_TYPE_USERID);
				temopralCondition = activityDao.fetchTemporalConditionDetails(
						String.valueOf(activityConditionDto.getConditionId()), AppConstants.FIND_BY_TYPE_CONDITIONID);
				metaData.setActivityId(activityConditionDto.getActivityConditionId()).setStudyId(studyId)
						.setVersion(AppConstants.APP_DEFAULT_VERSION)
						.setStartDate(AppUtil.getFormattedDateByTimeZone(
								temopralCondition.getStartDate() + " " + temopralCondition.getStartTime(),
								AppConstants.SDF_DATE_TIME_FORMAT,
								AppConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_FORMAT, user.getTimeZone()))
						.setEndDate(AppUtil.getFormattedDateByTimeZone(
								temopralCondition.getEndDate() + " " + temopralCondition.getEndTime(),
								AppConstants.SDF_DATE_TIME_FORMAT,
								AppConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_FORMAT, user.getTimeZone()))
						.setLastModifiedDate("");

				activityTypeList = this.getAttemptsCountByActivityType(activityConditionDto, metaData,
						activityTypeList);

				questionsList = activityDao.fetchQuestionsDetailsList(
						String.valueOf(activityConditionDto.getActivityId()), AppConstants.FIND_BY_TYPE_ACTIVITY_ID);
				if (!questionsList.isEmpty()) {
					for (QuestionsDto question : questionsList) {
						if (activityTypeList.contains(question.getQtType())
								&& StringUtils.isNotEmpty(question.getQualtricsQuestionsId())) {
							question.setQtType(question.getQtType().trim())
									.setResponseType(question.getResponseType().trim())
									.setResponseSubType(question.getResponseSubType().trim());

							List<QuestionChoiceDto> questionChoiceDtoList = activityDao.fetchQuestionsChoiceDetailsList(
									String.valueOf(question.getQuestionId()), AppConstants.FIND_BY_TYPE_QUESTION_ID);
							/*if(question.getResponseType().equals(AppConstants.RESPONSE_TYPE_CRAT_TEST)) {
								Object step = this.getCratFormat(question, userId);
								steps.add(step);
							}else {*/
								Object step = this.getQuestionFormatByResponseType(question, questionChoiceDtoList);
								steps.add(step);
							//}
						}
					}
				}
				if(activityConditionDto.getActivityType().equals(AppConstants.ACTIVITY_TYPE_CRAT_WEB)) {
					cratWordsListResponse = this.fetchCratWords(userId);
					activity.setCratWordsList(cratWordsListResponse);
				}
				activity.setType(this.getActivityType(activityConditionDto.getActivityType())).setMetadata(metaData)
						.setSteps(steps);

				activityDetailsResponse.setActivity(activity).setError(new ErrorBean().setCode(ErrorCode.EC_200.code())
						.setMessage(AppUtil.getAppProperties().get(AppConstants.ACTIVITY_META_DATA)));
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityServiceImpl - getActivityDetails()", e);
		}
		LOGGER.info("INFO: ActivityServiceImpl - getActivityDetails() :: ends");
		return activityDetailsResponse;
	}

	@Override
	public ErrorResponse updateThresholdActivityState(String userId, String studyId, String activityId,
			String currentDate, String runId, String runState, long duration) throws CustomException {
		LOGGER.info("INFO: ActivityServiceImpl - updateThresholdActivityState() :: starts"+"userId:"+userId+" studyId:"+studyId
				+" activityId:"+activityId+" currentDate:"+currentDate+" runId"+runId+" runState"+runState+
				"duration:"+duration);
		ErrorResponse errorResponse = new ErrorResponse();
		ActivityConditionDto activityCondition = null;
		UserActivitiesDto userActivity = null;
		UserDto user = null;
		UserStudiesDto userStudies = null;
		try {
			errorResponse.getError().setCode(ErrorCode.EC_404.code()).setMessage(ErrorCode.EC_404.errorMessage());

			user = userDao.fetchUserDetails(userId, AppConstants.FIND_BY_TYPE_USERID);
			if (user == null) {
				errorResponse.setError(
						new ErrorBean().setCode(ErrorCode.EC_61.code()).setMessage(ErrorCode.EC_61.errorMessage()));
				return errorResponse;
			}

			userStudies = studyDao.fetchUserStudiesDetails(userId, studyId, AppConstants.FIND_BY_TYPE_USERID_STUDYID);
			if (userStudies == null) {
				errorResponse.setError(
						new ErrorBean().setCode(ErrorCode.EC_93.code()).setMessage(ErrorCode.EC_93.errorMessage()));
				return errorResponse;
			}

			activityCondition = activityDao.fetchActivityConditionDetails(activityId,
					AppConstants.FIND_BY_TYPE_ACTIVITY_CONDITIONID);
			if (activityCondition == null
					|| !activityCondition.getActivitySubType().equals(AppConstants.ACTIVITY_SUB_TYPE_TRIGGERED)) {
				errorResponse.getError().setCode(ErrorCode.EC_45.code()).setMessage(ErrorCode.EC_45.errorMessage());
				return errorResponse;
			}

			userActivity = activityDao.fetchUserActivityDetails(userId,
					String.valueOf(activityCondition.getConditionId()), String.valueOf(userStudies.getUserStudiesId()),
					AppConstants.FIND_BY_TYPE_USERID_CONDITIONID);
			if (userActivity == null) {
				errorResponse.getError().setCode(ErrorCode.EC_45.code()).setMessage(ErrorCode.EC_45.errorMessage());
				return errorResponse;
			}

			// Check the run id previous run or not,
			// if previous one don't do any action otherwise update the user activity
			// details
			// and user activity runs details
			if (Integer.parseInt(runId) < userActivity.getActivityRunId()) {
				errorResponse.setError(
						new ErrorBean().setCode(ErrorCode.EC_103.code()).setMessage(ErrorCode.EC_103.errorMessage()));
				return errorResponse;
			}

			if (Arrays.asList(AppConstants.RUN_STATES.split(",")).contains(runState)) {
				errorResponse = this.updateActivityStateForTriggredSurveysByPrerequisists(runId, runState, currentDate,
						activityCondition, userActivity, user, errorResponse);

				if (runState.equals(AppConstants.RUN_STATE_COMPLETED)
						&& errorResponse.getError().getCode() == ErrorCode.EC_200.code()) {
					this.notifyParticipantsAboutNewLevelAndRewardPoints(userId, studyId, duration, activityCondition);
				}
			} else {
				errorResponse.getError().setCode(ErrorCode.EC_105.code()).setMessage(ErrorCode.EC_105.errorMessage());
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityServiceImpl - updateThresholdActivityState()", e);
		}
		LOGGER.info("INFO: ActivityServiceImpl - updateThresholdActivityState() :: ends");
		return errorResponse;
	}

	@Override
	public RewardsResponse rewards(String userId) throws CustomException {
		LOGGER.info("INFO: ActivityServiceImpl - rewards() :: starts");
		RewardsResponse rewardsResponse = new RewardsResponse();
		UserDto user = null;
		UserStudiesDto userStudies = null;
		String studyId = AppConstants.DEFAULT_STUDY_ID;
		try {
			user = userDao.fetchUserDetails(userId, AppConstants.FIND_BY_TYPE_USERID);
			userStudies = studyDao.fetchUserStudiesDetails(userId, studyId, AppConstants.FIND_BY_TYPE_USERID_STUDYID);
			if (user == null || userStudies == null) {
				return new RewardsResponse().setError(
						new ErrorBean().setCode(ErrorCode.EC_61.code()).setMessage(ErrorCode.EC_61.errorMessage()));
			}

			List<RewardLevelsDto> rewardLevelsList = activityDao.fetchRewardLevelsList();
			rewardsResponse = this.getRewardsResponseDetails(rewardLevelsList, user.getPointsEarned());
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityServiceImpl - rewards()", e);
		}
		LOGGER.info("INFO: ActivityServiceImpl - rewards() :: ends");
		return rewardsResponse;
	}

	/**
	 * Get the rewards details for the total points earned after completing the runs
	 * 
	 * @author Mohan
	 * @param rewardLevelsList
	 *            the {@link RewardLevelsDto} details
	 * @param pointsEarned
	 *            the total points earned
	 * @return the {@link RewardsResponse} details
	 */
	public RewardsResponse getRewardsResponseDetails(List<RewardLevelsDto> rewardLevelsList, Long pointsEarned) {
		LOGGER.info("INFO: ActivityServiceImpl - getRewardsResponseDetails() :: starts");
		RewardsResponse rewardsResponse = new RewardsResponse();
		try {
			if (!rewardLevelsList.isEmpty()) {
				List<RewardLevelBean> rewardLevels = new ArrayList<>();
				int currentLevel = 0;
				for (RewardLevelsDto rewardLevelsDto : rewardLevelsList) {
					RewardLevelBean rewardLevelBean = new RewardLevelBean();
					rewardLevelBean.setLevel(rewardLevelsDto.getLevel()).setPoints(rewardLevelsDto.getPoints());
					rewardLevels.add(rewardLevelBean);

					if (pointsEarned >= rewardLevelsDto.getPoints()) {
						currentLevel = rewardLevelsDto.getLevel();
					}
				}

				rewardsResponse.setCurrentLevel(currentLevel).setPoints(pointsEarned).setRewardLevels(rewardLevels)
						.setError(new ErrorBean().setCode(ErrorCode.EC_200.code())
								.setMessage(ErrorCode.EC_200.errorMessage()));
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityServiceImpl - getRewardsResponseDetails()", e);
		}
		LOGGER.info("INFO: ActivityServiceImpl - getRewardsResponseDetails() :: ends");
		return rewardsResponse;
	}

	/**
	 * Get the {@link UserActivitiesDto} for the provided activity condition
	 * identifier
	 * 
	 * @author Mohan
	 * @param user
	 *            the {@link UserDto} details
	 * @param userStudies
	 *            the {@link UserStudiesDto} details
	 * @param activityConditionDto
	 *            the {@link ActivityConditionDto} details
	 * @param temporalConditionDto
	 *            the {@link TemporalConditionDto} details
	 * @return the {@link UserActivitiesDto} details
	 * @throws CustomException
	 */
	public UserActivitiesDto getUserActivitiesDetails(UserDto user, UserStudiesDto userStudies,
			ActivityConditionDto activityConditionDto, TemporalConditionDto temporalConditionDto)
			throws CustomException {
		LOGGER.info("INFO: ActivityServiceImpl - getUserActivitiesDetails() :: starts");
		UserActivitiesDto userActDto = null;
		UserActivitiesDto saveUserActivity = new UserActivitiesDto();
		int anchorDays = 0;
		boolean isActivityAvailable = true;
		try {

			userActDto = activityDao.fetchUserActivityDetails(String.valueOf(user.getUserId()),
					String.valueOf(activityConditionDto.getConditionId()),
					String.valueOf(userStudies.getUserStudiesId()), AppConstants.FIND_BY_TYPE_USERID_CONDITIONID);
			if (userActDto == null) {

				// Map the activity to the user only if the activity is active for the user
				// (i.e. Activity not past the endDate)
				anchorDays = temporalConditionDto.getAnchorDays();
				if (!AppConstants.SDF_DATE.parse(temporalConditionDto.getEndDate()).before(AppConstants.SDF_DATE.parse(
						AppUtil.addDays(userStudies.getCreatedOn(), AppConstants.SDF_DATE_TIME_FORMAT, anchorDays, AppConstants.SDF_DATE_FORMAT)))) {
					saveUserActivity.setConditionId(activityConditionDto.getConditionId()).setUserId(user.getUserId())
							.setUserStudiesId(userStudies.getUserStudiesId())
							.setActivityStatus(AppConstants.RUN_STATE_START).setActivityRunId(0).setCompletedCount(0)
							.setMissedCount(0).setTotalCount(0).setLastCompletedRunId(0)
							.setCreatedOn(AppUtil.getCurrentDateTime()).setCurrentRunDate(null).setExpireNotificationSent(false);;

					saveUserActivity.setTotalCount(
							this.calculateTotalRunCount(temporalConditionDto, userStudies, activityConditionDto));
					userActDto = activityDao.saveOrUpdateUserActivities(saveUserActivity, AppConstants.DB_SAVE);
				} else {
					isActivityAvailable = false;
				}
			}

			// Update the activityRunDetails for the current run only if the activity sub
			// type is 'scheduled'
			if (isActivityAvailable && StringUtils.isNotEmpty(activityConditionDto.getActivitySubType())
					&& activityConditionDto.getActivitySubType().equals(AppConstants.ACTIVITY_SUB_TYPE_SCHEDULED)) {
				userActDto = this.updateActivityRunDetails(user, userStudies, activityConditionDto, userActDto,
						temporalConditionDto);
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityServiceImpl - getUserActivitiesDetails()", e);
		}
		LOGGER.info("INFO: ActivityServiceImpl - getUserActivitiesDetails() :: ends");
		return userActDto;
	}

	/**
	 * Get the activity metadata details list from the provided activity
	 * pre-requisiste
	 * 
	 * @author Mohan
	 * @param activityListMap
	 *            the activity details map
	 * @return the {@link ActivityListBean} details
	 */
	@SuppressWarnings("unchecked")
	public ActivityListBean getActivityListMetaData(Map<String, Object> activityListMap) {
		LOGGER.info("INFO: ActivityServiceImpl - getActivityListMetaData() :: starts");
		ActivityListBean activityListBean = new ActivityListBean();
		ActivitiesDto activityDto = null;
		ActivityConditionDto actConditionDto = null;
		TemporalConditionDto temporalConditionDto = null;
		List<ThresholdConditionsDto> thresholdConditionsList = null;
		UserActivitiesDto userActivitiesDto = null;
		UserDto user = null;
		UserStudiesDto userStudies = null;
		try {
			activityDto = (ActivitiesDto) activityListMap.get("activityDetails");
			actConditionDto = (ActivityConditionDto) activityListMap.get("activityConditionDetails");
			temporalConditionDto = (TemporalConditionDto) activityListMap.get("temporalConditionDetails");
			thresholdConditionsList = (List<ThresholdConditionsDto>) activityListMap.get("thresholdConditionDetails");
			userActivitiesDto = (UserActivitiesDto) activityListMap.get("userActivityDetails");
			user = (UserDto) activityListMap.get("user");
			userStudies = (UserStudiesDto) activityListMap.get("userStudies");

			if (activityDto != null) {
				activityListBean.setQualtricsId(activityDto.getQualtricsId());
			}

			if (actConditionDto != null) {
				activityListBean.setTitle(actConditionDto.getActivityConditionName())
						.setActivityId(actConditionDto.getActivityConditionId())
						.setParticipationTarget(actConditionDto.getTotalParticipationTarget())
						.setType(this.getActivityType(actConditionDto.getActivityType()))
						.setSubType(actConditionDto.getActivitySubType());
			}

			if (temporalConditionDto != null && userStudies != null) {
				activityListBean
						.setStartTime(AppUtil.getFormattedDateByTimeZone(
								temporalConditionDto.getStartDate() + " " + temporalConditionDto.getStartTime(),
								AppConstants.SDF_DATE_TIME_FORMAT,
								AppConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_FORMAT, user.getTimeZone()))
						.setEndTime(AppUtil.getFormattedDateByTimeZone(
								temporalConditionDto.getEndDate() + " " + temporalConditionDto.getEndTime(),
								AppConstants.SDF_DATE_TIME_FORMAT,
								AppConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_FORMAT, user.getTimeZone()))
						.setFrequency(StringUtils.isEmpty(temporalConditionDto.getRepetitionFrequency()) ? ""
								: temporalConditionDto.getRepetitionFrequency())
						.setGeoFence(temporalConditionDto.getGeoFence()!=null?temporalConditionDto.getGeoFence():Boolean.FALSE);
			}

			if (thresholdConditionsList != null && !thresholdConditionsList.isEmpty()) {
				activityListBean
						.setConditions(this.getThresholdConditionsByRange(thresholdConditionsList, actConditionDto));
			}

			if (userActivitiesDto != null) {
				activityListBean.setCompletedRun(userActivitiesDto.getCompletedCount())
						.setTotalRun(userActivitiesDto.getTotalCount())
						.setLastCompletedRunId(userActivitiesDto.getLastCompletedRunId())
						.setLastCompletedDate(StringUtils.isEmpty(userActivitiesDto.getLastCompletedDate()) ? ""
								: AppUtil.getFormattedDateByTimeZone(userActivitiesDto.getLastCompletedDate(),
										AppConstants.SDF_DATE_TIME_FORMAT,
										AppConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_FORMAT, user.getTimeZone()))
						.setMissedRun(userActivitiesDto.getMissedCount())
						.setLastRunId(this.getLastRunIdByActivity(userActivitiesDto));
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityServiceImpl - getActivityListMetaData()", e);
		}
		LOGGER.info("INFO: ActivityServiceImpl - getActivityListMetaData() :: ends");
		return activityListBean;
	}

	/**
	 * Get the user anchor date from the date when the user has joined to study
	 * 
	 * @author Mohan
	 * @param temporalConditionDto
	 *            the {@link TemporalConditionDto} details
	 * @param userStudiesDto
	 *            the {@link UserStudiesDto} details
	 * @return the anchor date
	 */
	public String getAnchorDateTimeForActivity(TemporalConditionDto temporalConditionDto,
			UserStudiesDto userStudiesDto) {
		LOGGER.info("INFO: ActivityServiceImpl - getAnchorDateTimeForActivity() :: starts");
		String anchorDate = "";
		int anchorDays = 0;
		try {
			anchorDays = temporalConditionDto.getAnchorDays();
			anchorDate = AppUtil.addDays(userStudiesDto.getCreatedOn(),AppConstants.SDF_DATE_TIME_FORMAT , anchorDays, AppConstants.SDF_DATE_FORMAT);

			// NOTE: if the anchor date is before the start date of the activity consider
			// the start date otherwise set anchor date as the start date of the activity
			// for the user
			if (AppConstants.SDF_DATE.parse(anchorDate)
					.before(AppConstants.SDF_DATE.parse(temporalConditionDto.getStartDate()))) {
				anchorDate = temporalConditionDto.getStartDate();
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityServiceImpl - getAnchorDateTimeForActivity()", e);
		}
		LOGGER.info("INFO: ActivityServiceImpl - getAnchorDateTimeForActivity() :: ends");
		return anchorDate;
	}

	/**
	 * Get the logical operator by type
	 * <p>
	 * Threshold range types are mentioned below:
	 * <ol>
	 * <li>Greater than (GT)
	 * <li>Lesser than (LT)
	 * <li>Between (BTW)
	 * 
	 * @author Mohan
	 * @param type
	 *            the threshold range type
	 * @return the logical operator
	 */
	public String getLogicalOperatorByType(String type) {
		LOGGER.info("INFO: ActivityServiceImpl - getLogicalOperatorByType() :: starts");
		String logicalOperator = "";
		try {
			switch (type) {
				case AppConstants.THRESHOLD_RANGE_GT:
					logicalOperator = ">";
					break;
				case AppConstants.THRESHOLD_RANGE_LT:
					logicalOperator = "<";
					break;
				default:
					break;
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityServiceImpl - getLogicalOperatorByType()", e);
		}
		LOGGER.info("INFO: ActivityServiceImpl - getLogicalOperatorByType() :: ends");
		return logicalOperator;
	}

	/**
	 * Get the threshold condition type name for the provided threshold identifier
	 * <p>
	 * Threshold Conditions are mentioned below:
	 * <ol>
	 * <li>CO2
	 * <li>Temperature
	 * <li>Relative Humidity
	 * <li>Noise
	 * <li>Light
	 * <li>PM2.5
	 * <li>Steps
	 * <li>Heart Rate
	 * <li>Sleep
	 * 
	 * @author Mohan
	 * @param type
	 *            the threshold identifier
	 * @return the threshold type name
	 */
	public String getThresholdConditionType(int type) {
		LOGGER.info("INFO: ActivityServiceImpl - getThresholdConditionType() :: starts");
		String conditionType = "";
		try {
			switch (type) {
				case 1:
					conditionType = AppEnums.TC_CO2.value();
					break;
				case 2:
					conditionType = AppEnums.TC_TEMPERATURE.value();
					break;
				case 3:
					conditionType = AppEnums.TC_HUMIDITY.value();
					break;
				case 4:
					conditionType = AppEnums.TC_NOISE.value();
					break;
				case 5:
					conditionType = AppEnums.TC_LIGHT.value();
					break;
				case 6:
					conditionType = AppEnums.TC_PM2_5.value();
					break;
				case 7:
					conditionType = AppEnums.TC_STEPS.value();
					break;
				case 8:
					conditionType = AppEnums.TC_HEART_RATE.value();
					break;
				case 9:
					conditionType = AppEnums.TC_SLEEP.value();
					break;
				default:
					break;
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityServiceImpl - getThresholdConditionType()", e);
		}
		LOGGER.info("INFO: ActivityServiceImpl - getThresholdConditionType() :: ends");
		return conditionType;
	}

/*	public String getActivityType(String type) {
		LOGGER.info("INFO: ActivityServiceImpl - getActivityType() :: starts");
		String activityType = "";
		try {
			switch (type) {
				case AppConstants.ACTIVITY_TYPE_ARITHMETIC_WEB:
					activityType = AppConstants.ACTIVITY_TYPE_ARITHMETIC_TEST_TASK;
					break;
				case AppConstants.ACTIVITY_TYPE_STROOP_WEB:
					activityType = AppConstants.ACTIVITY_TYPE_STROOP_TEST_TASK;
					break;
				case AppConstants.ACTIVITY_TYPE_SURVEY_WEB:
					activityType = AppConstants.ACTIVITY_TYPE_SURVEY;
					break;
				default:
					break;
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityServiceImpl - getActivityType()", e);
		}
		LOGGER.info("INFO: ActivityServiceImpl - getActivityType() :: ends");
		return activityType;
	}
*/

	/**
	 * Get the activity type name for the provided activity type
	 * <p>
	 * Activity types are mentioned below:
	 * <ol>
	 * <li>Arithmetic
	 * <li>Stroop
	 * <li>Survey
	 * <li> AUT
	 * 
	 * @author Mohan
	 * @param type
	 *            the activity type
	 * @return the activity type name
	 */
	public String getActivityType(String type) {
		LOGGER.info("INFO: ActivityServiceImpl - getActivityType() :: starts");
		String activityType = "";
		try {
			switch (type) {
				case AppConstants.ACTIVITY_TYPE_ARITHMETIC_WEB:
					activityType = AppConstants.ACTIVITY_TYPE_ARITHMETIC_TEST_TASK;
					break;
				case AppConstants.ACTIVITY_TYPE_STROOP_WEB:
					activityType = AppConstants.ACTIVITY_TYPE_STROOP_TEST_TASK;
					break;
				case AppConstants.ACTIVITY_TYPE_SURVEY_WEB:
					activityType = AppConstants.ACTIVITY_TYPE_SURVEY;
					break;
				case AppConstants.ACTIVITY_TYPE_AUT_WEB:
					activityType = AppConstants.ACTIVITY_TYPE_AUT_TEST_TASK;
					break;
				case AppConstants.ACTIVITY_TYPE_CRAT_WEB:
					activityType = AppConstants.ACTIVITY_TYPE_CRAT_TEST_TASK;
					break;
				default:
					break;
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityServiceImpl - getActivityType()", e);
		}
		LOGGER.info("INFO: ActivityServiceImpl - getActivityType() :: ends");
		return activityType;
	}
	/**
	 * To calculate the total run count for the provided activity condition,
	 * temporal condition
	 * 
	 * @author Mohan
	 * @param temporalConditions
	 *            the {@link TemporalConditionDto} details
	 * @param userStudy
	 *            the {@link UserStudiesDto} details
	 * @param activityCondition
	 *            the {@link ActivityConditionDto} details
	 * @return the total run count
	 */
	public int calculateTotalRunCount(TemporalConditionDto temporalConditions, UserStudiesDto userStudy,
			ActivityConditionDto activityCondition) {
		LOGGER.info("INFO: ActivityServiceImpl - calculateTotalRunCount() :: starts");
		int totalCount = 0;
		int anchorDays = 0;
		String startDate = "";
		String endDate = "";
		List<String> daysList = null;
		try {
			anchorDays = temporalConditions.getAnchorDays();
			startDate = AppUtil.addDays(userStudy.getCreatedOn(),AppConstants.SDF_DATE_TIME_FORMAT ,anchorDays, AppConstants.SDF_DATE_FORMAT);
			endDate = temporalConditions.getEndDate();

			// get the runs for activity sub type is 'scheduled' and not for 'triggered'
			if (activityCondition.getActivitySubType().equals(AppConstants.ACTIVITY_SUB_TYPE_SCHEDULED)
					&& !AppConstants.SDF_DATE.parse(endDate).before(AppConstants.SDF_DATE.parse(startDate))) {
				int tempTotalCount = AppUtil.noOfDaysBetweenTwoDates(startDate, endDate);
				daysList = Arrays.asList(temporalConditions.getRepetitionFrequencyDays().split(","));

				while (tempTotalCount > 0) {
					if (daysList.contains(AppUtil.getDayByDate(startDate)) && (!AppConstants.SDF_DATE.parse(startDate)
							.before(AppConstants.SDF_DATE.parse(temporalConditions.getStartDate())))) {
						totalCount++;
					}

					startDate = AppUtil.addDays(startDate,AppConstants.SDF_DATE_TIME_FORMAT ,1, AppConstants.SDF_DATE_FORMAT);
					tempTotalCount--;
				}
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityServiceImpl - calculateTotalRunCount()", e);
		}
		LOGGER.info("INFO: ActivityServiceImpl - calculateTotalRunCount() :: ends");
		return totalCount;
	}

	/**
	 * To calculate the current run identifier and the start date time of the run
	 * for the provided {@link TemporalConditionDto} and {@link UserStudiesDto}
	 * details
	 * 
	 * @author Mohan
	 * @param temporalConditions
	 *            the {@link TemporalConditionDto} details
	 * @param userStudy
	 *            the {@link UserStudiesDto} details
	 * @return the current run details
	 */
	public Map<String, Object> calculateCurrentRunId(TemporalConditionDto temporalConditions,
			UserStudiesDto userStudy) {
		LOGGER.info("INFO: ActivityServiceImpl - calculateCurrentRunId() :: starts");
		Map<String, Object> runMap = new HashMap<>();
		int totalCount = 0;
		int anchorDays = 0;
		int runId = 0;
		String startDate = "";
		String endDate = "";
		String currentDate = "";
		List<String> daysList = null;
		String runDate = null;
		try {
			anchorDays = temporalConditions.getAnchorDays();
			startDate = AppUtil.addDays(userStudy.getCreatedOn() ,AppConstants.SDF_DATE_TIME_FORMAT ,  anchorDays, AppConstants.SDF_DATE_FORMAT);
			endDate = temporalConditions.getEndDate();
			currentDate = AppUtil.getCurrentDate();
			if (!AppConstants.SDF_DATE.parse(endDate).before(AppConstants.SDF_DATE.parse(startDate))) {
				totalCount = AppUtil.noOfDaysBetweenTwoDates(startDate, endDate);
				daysList = StringUtils.isEmpty(temporalConditions.getRepetitionFrequencyDays())
						? Arrays.asList(AppConstants.REPEAT_FREQUENCY_DAYS.split(","))
						: Arrays.asList(temporalConditions.getRepetitionFrequencyDays().split(","));

				while (totalCount > 0) {
					totalCount--;
					if (daysList.contains(AppUtil.getDayByDate(startDate))
							&& !AppConstants.SDF_DATE.parse(currentDate).before(AppConstants.SDF_DATE.parse(startDate))
							&& !AppConstants.SDF_DATE.parse(startDate)
									.before(AppConstants.SDF_DATE.parse(temporalConditions.getStartDate()))) {
						runId++;
						runDate = startDate;
					}

					startDate = AppUtil.addDaysDate(startDate,AppConstants.SDF_DATE_FORMAT , 1, AppConstants.SDF_DATE_FORMAT);
				}
			}

			runMap.put("runId", runId);
			runMap.put("runDate", runDate);
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityServiceImpl - calculateCurrentRunId()", e);
		}
		LOGGER.info("INFO: ActivityServiceImpl - calculateCurrentRunId() :: ends");
		return runMap;
	}

	/**
	 * Get the activity runs details
	 * 
	 * @author Mohan
	 * @param temporalCondition
	 *            the {@link TemporalConditionDto} details
	 * @param activityCondition
	 *            the {@link ActivityConditionDto} details
	 * @param userActivity
	 *            the {@link UserActivitiesDto} details
	 * @param userStudies
	 *            the {@link UserStudiesDto} details
	 * @param user
	 *            the {@link UserDto} details
	 * @return the {@link ActivityRunBean} details
	 */
	public ActivityRunBean getActivityRuns(TemporalConditionDto temporalCondition,
			ActivityConditionDto activityCondition, UserActivitiesDto userActivity, UserStudiesDto userStudies,
			UserDto user) {
		LOGGER.info("INFO: ActivityServiceImpl - getActivityRuns() :: starts");
		ActivityRunBean activityRunBean = new ActivityRunBean();
		List<RunBean> runs = new ArrayList<>();
		int anchorDays = 0;
		String startDate = "";
		String endDate = "";
		try {
			anchorDays = temporalCondition.getAnchorDays();
			startDate = AppUtil.addDays(userStudies.getCreatedOn(),AppConstants.SDF_DATE_TIME_FORMAT ,anchorDays, AppConstants.SDF_DATE_FORMAT);
			endDate = temporalCondition.getEndDate();

			// get the runs for activity sub type is 'scheduled' and not for 'triggered'
			if ((!AppConstants.SDF_DATE.parse(endDate).before(AppConstants.SDF_DATE.parse(startDate)))
					&& (userActivity.getCompletedCount() < activityCondition.getTotalParticipationTarget())) {
				runs = this.getActivityRunBeanDetailsByActivityType(startDate, endDate, activityCondition,
						temporalCondition, userActivity, user);
			}
			activityRunBean.setActivityId(activityCondition.getActivityConditionId()).setRuns(runs);
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityServiceImpl - getActivityRuns()", e);
		}
		LOGGER.info("INFO: ActivityServiceImpl - getActivityRuns() :: ends");
		return activityRunBean;
	}

	/**
	 * Get the activity runs metadata information for the provided activity type
	 * <p>
	 * activity types mentioned below:
	 * <ol>
	 * <li>Scheduled Survey
	 * <li>Triggred Survey
	 * 
	 * @author Mohan
	 * @param startDate
	 *            the activity start date time
	 * @param endDate
	 *            the activity end date time
	 * @param activityCondition
	 *            the {@link ActivityConditionDto} details
	 * @param temporalCondition
	 *            the {@link TemporalConditionDto} details
	 * @param userActivity
	 *            the {@link UserActivitiesDto} details
	 * @param user
	 *            the {@link UserDto} details
	 * @return the {@link RunBean} details list
	 */
	public List<RunBean> getActivityRunBeanDetailsByActivityType(String startDate, String endDate,
			ActivityConditionDto activityCondition, TemporalConditionDto temporalCondition,
			UserActivitiesDto userActivity, UserDto user) {
		LOGGER.info("INFO: ActivityServiceImpl - getActivityRunBeanDetailsByActivityType() :: starts");
		List<RunBean> runs = new ArrayList<>();
		List<String> daysList = null;
		int totalCount = 0;
		String userTimeZoneFormat = "";
		String runStartDate = startDate;
		String runEndDate = endDate;
		try {

			userTimeZoneFormat = AppUtil.getFormattedDateByTimeZone(AppUtil.getCurrentDateTime(),
					AppConstants.SDF_DATE_TIME_FORMAT, AppConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_FORMAT,
					user.getTimeZone());

			// Check the user met the participation target count or not, if not met then
			// calculate the runs
			int tempTotalCount = AppUtil.noOfDaysBetweenTwoDates(runStartDate, runEndDate);

			if (activityCondition.getActivitySubType().equals(AppConstants.ACTIVITY_SUB_TYPE_SCHEDULED)) {
				daysList = Arrays.asList(temporalCondition.getRepetitionFrequencyDays().split(","));

				// For scheduled surveys
				while ((tempTotalCount > 0) && (runs.size() < AppConstants.MAX_RUN_COUNT)) {
					tempTotalCount--;
					if (daysList.contains(AppUtil.getDayByDate(runStartDate))
							&& !AppConstants.SDF_DATE.parse(runStartDate)
									.before(AppConstants.SDF_DATE.parse(temporalCondition.getStartDate()))) {
						totalCount++;

						// set the runs based on runId
						if (totalCount >= userActivity.getActivityRunId()) {
							RunBean runBean = new RunBean();
							runBean.setRunId(totalCount)
									.setRunStartDateTime(AppUtil.getUserTimeZoneRunFormat(runStartDate,
											temporalCondition.getStartTime(), userTimeZoneFormat))
									.setRunEndDateTime(AppUtil.getUserTimeZoneRunFormat(runStartDate,
											temporalCondition.getEndTime(), userTimeZoneFormat));

							runBean.setRunState(
									(totalCount == userActivity.getActivityRunId()) ? userActivity.getActivityStatus()
											: AppConstants.RUN_STATE_START);
							runs.add(runBean);
						}
					}

					runStartDate = AppUtil.addDaysDate(runStartDate, AppConstants.SDF_DATE_FORMAT,1, AppConstants.SDF_DATE_FORMAT);
				}
			} else {

				// For triggered surveys
				daysList = Arrays.asList(AppConstants.REPEAT_FREQUENCY_DAYS.split(","));

				// Check already activity run id details is updated or not
				Map<String, Object> criteriaMap = new HashMap<>();
				criteriaMap.put(AppEnums.QK_CONDITION_IDENTIFIER.value(), activityCondition.getConditionId());
				criteriaMap.put(AppEnums.QK_ACTIVITY_RUN_IDENTIFIER.value(), userActivity.getActivityRunId());
				criteriaMap.put(AppEnums.QK_USER_STUDIES_IDENTIFIER.value(), userActivity.getUserStudiesId());

				UserActivitiesRunsDto userActivityRuns = activityDao.fetchUserActivityRunDetailsCriteria(
						String.valueOf(user.getUserId()), criteriaMap,
						AppConstants.FIND_BY_TYPE_USERID_ACTIVITYID_RUNID);
				if (userActivityRuns != null && userActivity.getCurrentRunDate().equals(AppUtil.getCurrentDate())
						&& daysList.contains(AppUtil.getDayByDate(userActivity.getCurrentRunDate()))) {
					RunBean runBean = new RunBean();
					runBean.setRunId(userActivity.getActivityRunId())
							.setRunStartDateTime(AppUtil.getFormattedDateByTimeZone(userActivityRuns.getRunStartsOn(),
									AppConstants.SDF_DATE_TIME_FORMAT,
									AppConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_FORMAT, user.getTimeZone()))
							.setRunEndDateTime(AppUtil.getFormattedDateByTimeZone(userActivityRuns.getRunEndsOn(),
									AppConstants.SDF_DATE_TIME_FORMAT,
									AppConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_FORMAT, user.getTimeZone()));

					runBean.setRunState(userActivity.getActivityStatus());
					runs.add(runBean);
				}
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityServiceImpl - getActivityRunBeanDetailsByActivityType()", e);
		}
		LOGGER.info("INFO: ActivityServiceImpl - getActivityRunBeanDetailsByActivityType() :: ends");
		return runs;
	}

	/**
	 * Update the {@link UserActivitiesRunsDto} details
	 * 
	 * @author Mohan
	 * @param user
	 *            the {@link UserDto} details
	 * @param userStudies
	 *            the {@link UserStudiesDto} details
	 * @param activityConditionDto
	 *            the {@link ActivityConditionDto} details
	 * @param userActivitiesDto
	 *            the {@link UserActivitiesDto} details
	 * @param temporalConditionDto
	 *            the {@link TemporalConditionDto} details
	 * @return the updated {@link UserActivitiesDto} details
	 * @throws CustomException
	 */
	public UserActivitiesDto updateActivityRunDetails(UserDto user, UserStudiesDto userStudies,
			ActivityConditionDto activityConditionDto, UserActivitiesDto userActivitiesDto,
			TemporalConditionDto temporalConditionDto) throws CustomException {
		LOGGER.info("INFO: ActivityServiceImpl - updateActivityRunDetails() :: starts");
		UserActivitiesDto updatedUserActivitiesDto = null;
		int activityRunId = 0;
		int completedCount = 0;
		Map<String, Object> runMap = new HashMap<>();
		UserActivitiesDto updatedMissedCountUADto = null;
		try {

			if (StringUtils.isEmpty(userActivitiesDto.getCurrentRunDate())
					|| !userActivitiesDto.getCurrentRunDate().equals(AppUtil.getCurrentDate())) {
				runMap = this.calculateCurrentRunId(temporalConditionDto, userStudies);
				activityRunId = (int) runMap.get("runId");
				userActivitiesDto.setCurrentRunDate((String) runMap.get("runDate"));
			} else {
				activityRunId = userActivitiesDto.getActivityRunId();
			}

			completedCount = activityDao.fetchUserActivityRunsCount(String.valueOf(user.getUserId()),
					String.valueOf(activityConditionDto.getConditionId()),
					String.valueOf(userActivitiesDto.getUserStudiesId()), AppConstants.FIND_BY_TYPE_USERID_CONDITIONID);

			// Update missed runs only if the user has not met the participant target count
			// Update the missed counts if the user missed previous runs
			updatedMissedCountUADto = this.getMissedCountDetailsForScheduledSurveys(userActivitiesDto,
					activityConditionDto, temporalConditionDto, activityRunId, completedCount,user);
			/*if(updatedMissedCountUADto.getActivityRunId() == activityRunId) {
				updatedMissedCountUADto.setActivityStatus(updatedMissedCountUADto.getActivityStatus());
			}else if(updatedMissedCountUADto.getActivityStatus().equals(AppConstants.RUN_STATE_COMPLETED)){
				if(StringUtils.isEmpty(userActivitiesDto.getCurrentRunDate()) ||
					userActivitiesDto.getCurrentRunDate() == null) {
					updatedMissedCountUADto.setActivityStatus(updatedMissedCountUADto.getActivityStatus());
				}
				
			}else {
				updatedMissedCountUADto.setActivityStatus(AppConstants.RUN_STATE_START);
			}*/
			updatedMissedCountUADto
					.setActivityStatus(updatedMissedCountUADto.getActivityRunId() == activityRunId
							? updatedMissedCountUADto.getActivityStatus()
							: AppConstants.RUN_STATE_START)
					.setActivityRunId(activityRunId).setModifiedOn(AppUtil.getCurrentDateTime())
					.setCompletedCount(completedCount);
			updatedUserActivitiesDto = activityDao.saveOrUpdateUserActivities(updatedMissedCountUADto,
					AppConstants.DB_UPDATE);
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityServiceImpl - updateActivityRunDetails()", e);
		}
		LOGGER.info("INFO: ActivityServiceImpl - updateActivityRunDetails() :: ends");
		return updatedUserActivitiesDto;
	}

	/**
	 * Calculate the missed run count for the provided activity run identifier,
	 * completed run count
	 * 
	 * @author Mohan
	 * @param userActivitiesDto
	 *            the {@link UserActivitiesDto} details
	 * @param activityConditionDto
	 *            the {@link ActivityConditionDto} details
	 * @param temporalConditionDto
	 *            the {@link TemporalConditionDto} details
	 * @param activityRunId
	 *            the activity run identifier
	 * @param completedCount
	 *            the completed count
	 * @return the updated {@link UserActivitiesDto} details
	 */
	public UserActivitiesDto getMissedCountDetailsForScheduledSurveys(UserActivitiesDto userActivitiesDto,
			ActivityConditionDto activityConditionDto, TemporalConditionDto temporalConditionDto, int activityRunId,
			int completedCount,UserDto user) {
		LOGGER.info("INFO: ActivityServiceImpl - getMissedCountDetailsForScheduledSurveys() :: ends");
		UserActivitiesDto updatedUserActiviiesDto = null;
		int missedCount = 0;
		try {
			if (userActivitiesDto.getCompletedCount() < activityConditionDto.getTotalParticipationTarget()) {
				if (activityRunId > 0) {

					// Check the end date exceeding the current date or not
					if (AppConstants.SDF_DATE.parse(AppUtil.getCurrentDate())
							.after(AppConstants.SDF_DATE.parse(temporalConditionDto.getEndDate()))) {
						missedCount = activityRunId - completedCount;
					} else if (userActivitiesDto.getActivityStatus().equals(AppConstants.RUN_STATE_COMPLETED)) {
						missedCount = activityRunId - completedCount;
					} else if (AppConstants.SDF_DATE.parse(AppUtil.getCurrentDate())
							.after(AppConstants.SDF_DATE.parse(userActivitiesDto.getCurrentRunDate()))) {
						missedCount = activityRunId - completedCount;
					} else if (AppConstants.SDF_DATE_TIME.parse(AppUtil.getCurrentDateTime(user.getTimeZone()))
							.after(AppConstants.SDF_DATE_TIME.parse(userActivitiesDto.getCurrentRunDate()+" " + temporalConditionDto.getEndTime()))) {
						missedCount = activityRunId - completedCount;
					}else {
						// Ignore missed count for current run
						missedCount = (activityRunId - 1) - completedCount;
					}

					// Check the user completed future runs, if yes send missed count as 0
					userActivitiesDto.setMissedCount(missedCount < 0 ? 0 : missedCount);
				} else if (AppConstants.SDF_DATE.parse(AppUtil.getCurrentDate())
						.after(AppConstants.SDF_DATE.parse(temporalConditionDto.getEndDate()))) {
					userActivitiesDto.setMissedCount(userActivitiesDto.getTotalCount());
				}
			}

			updatedUserActiviiesDto = userActivitiesDto;
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityServiceImpl - getMissedCountDetailsForScheduledSurveys()", e);
		}
		LOGGER.info("INFO: ActivityServiceImpl - getMissedCountDetailsForScheduledSurveys() :: ends");
		return updatedUserActiviiesDto;
	}

	/**
	 * Get the completed activity state prerequisists for the provided last
	 * completed date time, run identifier, run state
	 * 
	 * @author Mohan
	 * @param lastCompletedDate
	 *            the run completed date time
	 * @param runId
	 *            the run identifier
	 * @param runState
	 *            the run state
	 * @param activityCondition
	 *            the {@link ActivityConditionDto} details
	 * @param userActivity
	 *            the {@link UserActivitiesDto} details
	 * @param user
	 *            the {@link UserDto} details
	 * @param errorResponse
	 *            the {@link ErrorResponse} details
	 * @return the activity pre-requisists
	 * @throws CustomException
	 */
	public Map<String, Object> getCompletedActivityStatePrerequisists(String lastCompletedDate, String runId,
			String runState, ActivityConditionDto activityCondition, UserActivitiesDto userActivity, UserDto user,
			ErrorResponse errorResponse) throws CustomException {
		LOGGER.info("INFO: ActivityServiceImpl - getCompletedActivityStatePrerequisists() :: starts");
		Map<String, Object> map = new HashMap<>();
		List<UserActivitiesRunsDto> tempUserActivityRunsList = null;
		int completedCount = 0;
		boolean updateUserActivityFlag = false;
		TemporalConditionDto temporalCondition = new TemporalConditionDto();
		try {

			// Check already activity run id details is updated or not
			Map<String, Object> criteriaMap = new HashMap<>();
			criteriaMap.put(AppEnums.QK_CONDITION_IDENTIFIER.value(), activityCondition.getConditionId());
			criteriaMap.put(AppEnums.QK_ACTIVITY_RUN_IDENTIFIER.value(), Integer.parseInt(runId));
			criteriaMap.put(AppEnums.QK_USER_STUDIES_IDENTIFIER.value(), userActivity.getUserStudiesId());

			temporalCondition = activityDao.fetchTemporalConditionDetails(
					String.valueOf(activityCondition.getConditionId()), AppConstants.FIND_BY_TYPE_CONDITIONID);

			tempUserActivityRunsList = activityDao.fetchUserActivityRunDetailsListCriteria(
					String.valueOf(user.getUserId()), criteriaMap, AppConstants.FIND_BY_TYPE_USERID_ACTIVITYID_RUNID);
			if (tempUserActivityRunsList.isEmpty()) {
				completedCount = activityDao.fetchUserActivityRunsCount(String.valueOf(user.getUserId()),
						String.valueOf(userActivity.getConditionId()), String.valueOf(userActivity.getUserStudiesId()),
						AppConstants.FIND_BY_TYPE_USERID_CONDITIONID);

				// Check completed count is lessThan participation target count
				if (completedCount < activityCondition.getTotalParticipationTarget()) {
					UserActivitiesRunsDto userActivityRuns = new UserActivitiesRunsDto();
					userActivityRuns.setConditionId(userActivity.getConditionId())
							.setActivityRunId(Integer.parseInt(runId)).setUserId(user.getUserId())
							.setRunStartsOn(AppUtil.getFormattedDateByTimeZone(lastCompletedDate,
									AppConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_FORMAT,
									AppConstants.SDF_DATE_FORMAT, user.getTimeZone()) + " "
									+ temporalCondition.getStartTime())
							.setRunEndsOn(AppUtil.getFormattedDateByTimeZone(lastCompletedDate,
									AppConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_FORMAT,
									AppConstants.SDF_DATE_FORMAT, user.getTimeZone()) + " "
									+ temporalCondition.getEndTime())
							.setRunState(runState).setCreatedOn(AppUtil.getCurrentDateTime())
							.setUserStudiesId(userActivity.getUserStudiesId());

					activityDao.saveOrUpdateUserActivityRunsDetails(userActivityRuns, AppConstants.DB_SAVE);

					// increment the completed count
					userActivity.setCompletedCount(completedCount + 1);
					if (userActivity.getActivityRunId() > 1) {
						userActivity.setMissedCount(
								(userActivity.getActivityRunId() - userActivity.getCompletedCount()) < 0 ? 0
										: (userActivity.getActivityRunId() - userActivity.getCompletedCount()));
					}
					updateUserActivityFlag = true;
				}
			} else if (userActivity.getActivityStatus().equals(AppConstants.RUN_STATE_COMPLETED)) {
				errorResponse.getError().setCode(ErrorCode.EC_103.code()).setMessage(ErrorCode.EC_103.errorMessage());
			} else if ((Integer.parseInt(runId) <= userActivity.getTotalCount())
					&& (userActivity.getCompletedCount() < activityCondition.getTotalParticipationTarget())) {
				updateUserActivityFlag = true;
			} else {
				errorResponse.getError().setCode(ErrorCode.EC_104.code()).setMessage(ErrorCode.EC_104.errorMessage());
			}

			map.put("updateUserActivityFlag", updateUserActivityFlag);
			map.put("errorResponse", errorResponse);
			map.put("userActivity", userActivity);
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityServiceImpl - getCompletedActivityStatePrerequisists()", e);
		}
		LOGGER.info("INFO: ActivityServiceImpl - getCompletedActivityStatePrerequisists() :: ends");
		return map;
	}

	/**
	 * Get the maximum number of attempts for the provided activity sub type
	 * <p>
	 * Activity types are mentioned below:
	 * <ol>
	 * <li>Arithmetic
	 * <li>Stroop
	 * <li>Survey
	 * 
	 * @author Mohan
	 * @param activityConditionDto
	 *            the {@link ActivityConditionDto} details
	 * @param metaData
	 *            the metadata details
	 * @param activityTypeList
	 *            the qualtrics question types
	 * @return the updated activity details list
	 */
	public List<String> getAttemptsCountByActivityType(ActivityConditionDto activityConditionDto, MetaDataBean metaData,
			List<String> activityTypeList) {
		LOGGER.info("INFO: ActivityServiceImpl - getAttemptsCountByActivityType() :: starts");
		List<String> activityTypeListNew = activityTypeList;
		try {
			switch (activityConditionDto.getActivityType()) {
				case AppConstants.ACTIVITY_TYPE_ARITHMETIC_WEB:
					metaData.setNumberOfAttempts(10);
					activityTypeListNew = Arrays.asList(AppConstants.QUALTRICS_QT_TYPE_TASK.split(","));
					break;
				case AppConstants.ACTIVITY_TYPE_STROOP_WEB:
					metaData.setNumberOfAttempts(20);
					activityTypeListNew = Arrays.asList(AppConstants.QUALTRICS_QT_TYPE_TASK.split(","));
					break;
				case AppConstants.ACTIVITY_TYPE_AUT_WEB:
					metaData.setNumberOfAttempts(1);
					activityTypeListNew = Arrays.asList(AppConstants.QUALTRICS_QT_TYPE_TASK.split(","));
					break;
				case AppConstants.ACTIVITY_TYPE_CRAT_WEB:
					metaData.setNumberOfAttempts(8);
					activityTypeListNew = Arrays.asList(AppConstants.QUALTRICS_QT_TYPE_TASK.split(","));
					break;
				default:
					metaData.setNumberOfAttempts(0);
					activityTypeListNew = Arrays.asList(AppConstants.QUALTRICS_QT_TYPE_QUESTIONNAIRE.split(","));
					break;
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityServiceImpl - getAttemptsCountByActivityType()", e);
		}
		LOGGER.info("INFO: ActivityServiceImpl - getAttemptsCountByActivityType() :: ends");
		return activityTypeListNew;
	}

	/**
	 * Update the {@link UserActivitiesRunsDto} details for the provided run
	 * identifier, run state, run completed date time
	 * 
	 * @author Mohan
	 * @param runId
	 *            the run identifier
	 * @param runState
	 *            the run state
	 * @param currentDate
	 *            the run completed date time
	 * @param activityCondition
	 *            the {@link ActivityConditionDto} details
	 * @param userActivity
	 *            the {@link UserActivitiesDto} details
	 * @param user
	 *            the {@link UserDto} details
	 * @param errorResponse
	 *            the {@link ErrorResponse} details
	 * @return the {@link ErrorResponse} details
	 * @throws CustomException
	 */
	public ErrorResponse updateActivityStateForTriggredSurveysByPrerequisists(String runId, String runState,
			String currentDate, ActivityConditionDto activityCondition, UserActivitiesDto userActivity, UserDto user,
			ErrorResponse errorResponse) throws CustomException {
		LOGGER.info("INFO: ActivityServiceImpl - updateActivityStateForTriggredSurveysByPrerequisists() :: starts");
		UserActivitiesRunsDto userActivityRuns = null;
		List<UserActivitiesRunsDto> tempUserActivityRunsList = new ArrayList<>();
		try {

			// Check already activity run id details is updated or not
			Map<String, Object> criteriaMap = new HashMap<>();
			criteriaMap.put(AppEnums.QK_CONDITION_IDENTIFIER.value(), activityCondition.getConditionId());
			criteriaMap.put(AppEnums.QK_ACTIVITY_RUN_IDENTIFIER.value(), Integer.parseInt(runId));
			criteriaMap.put(AppEnums.QK_USER_STUDIES_IDENTIFIER.value(), userActivity.getUserStudiesId());

			userActivityRuns = activityDao.fetchUserActivityRunDetailsCriteria(String.valueOf(user.getUserId()),
					criteriaMap, AppConstants.FIND_BY_TYPE_USERID_ACTIVITYID_RUNID);
			if (userActivityRuns != null) {
				userActivityRuns.setModifiedOn(AppUtil.getCurrentDateTime()).setRunState(runState);

				userActivityRuns = this.updateTriggeredSurveyRunsDate(userActivityRuns, user, runState, currentDate);
				activityDao.saveOrUpdateUserActivityRunsDetails(userActivityRuns, AppConstants.DB_UPDATE);
			} else {
				userActivityRuns = new UserActivitiesRunsDto();
				userActivityRuns.setActivityRunId(Integer.parseInt(runId))
						.setConditionId(activityCondition.getConditionId()).setCreatedOn(AppUtil.getCurrentDateTime())
						.setRunState(runState).setUserId(user.getUserId())
						.setUserStudiesId(userActivity.getUserStudiesId());

				userActivityRuns = this.updateTriggeredSurveyRunsDate(userActivityRuns, user, runState, currentDate);
				activityDao.saveOrUpdateUserActivityRunsDetails(userActivityRuns, AppConstants.DB_SAVE);
			}

			// Update the threshold changes to the user activity
			userActivity.setActivityRunId(Integer.parseInt(runId)).setActivityStatus(runState)
					.setCurrentRunDate(AppUtil.getFormattedDateByTimeZone(currentDate,
							AppConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_FORMAT, AppConstants.SDF_DATE_FORMAT,
							user.getTimeZone()))
					.setModifiedOn(AppUtil.getCurrentDateTime());

			// Get the user and activity runs details based on the activity run id and the
			// userId (runs which are lesser than currentRunId) to handle the scenario that
			// if the user changes the datetime from device and update the activitystate
			tempUserActivityRunsList = activityDao.fetchUserActivityRunDetailsListCriteria(
					String.valueOf(user.getUserId()), criteriaMap,
					AppConstants.FIND_BY_TYPE_USERID_ACTIVITYID_RUNID_THRESHOLD);
			if (!tempUserActivityRunsList.isEmpty()) {
				int missedCount = 0;
				int completedCount = 0;
				for (UserActivitiesRunsDto userActivityRunsDto : tempUserActivityRunsList) {
					if (userActivityRunsDto.getRunState().equals(AppConstants.RUN_STATE_COMPLETED)) {
						if (userActivityRunsDto.getActivityRunId() == Integer.parseInt(runId)) {
							userActivity.setLastCompletedRunId(Integer.parseInt(runId))
									.setLastCompletedDate(AppUtil.getFormattedDateByTimeZone(currentDate,
											AppConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_FORMAT,
											AppConstants.SDF_DATE_TIME_FORMAT, user.getTimeZone()));
						}
						completedCount++;
					} else if (userActivityRunsDto.getActivityRunId() != Integer.parseInt(runId)
							|| AppConstants.SDF_DATE.parse(AppUtil.getCurrentDate())
									.after(AppConstants.SDF_DATE.parse(userActivity.getCurrentRunDate()))) {
						missedCount++;
					}
				}

				userActivity.setTotalCount(tempUserActivityRunsList.size()).setCompletedCount(completedCount)
						.setMissedCount(missedCount);

				activityDao.saveOrUpdateUserActivities(userActivity, AppConstants.DB_UPDATE);
				errorResponse.getError().setCode(ErrorCode.EC_200.code())
						.setMessage(AppUtil.getAppProperties().get(AppConstants.UPDATE_THRESHOLD_ACTIVITY_STATE));
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityServiceImpl - updateActivityStateForTriggredSurveysByPrerequisists()", e);
		}
		LOGGER.info("INFO: ActivityServiceImpl - updateActivityStateForTriggredSurveysByPrerequisists() :: ends");
		return errorResponse;
	}

	/**
	 * Get the {@link ThresholdConditionsDto} details for the provided threshold
	 * condition range type
	 * 
	 * @author Mohan
	 * @param thresholdConditionsList
	 *            the {@link ThresholdConditionsDto} details list
	 * @param actConditionDto
	 *            the {@link ActivityConditionDto} details
	 * @return the {@link ConditionsBean} details list
	 */
	public List<ConditionsBean> getThresholdConditionsByRange(List<ThresholdConditionsDto> thresholdConditionsList,
			ActivityConditionDto actConditionDto) {
		LOGGER.info("INFO: ActivityServiceImpl - getThresholdConditionsByRange() :: starts");
		List<ConditionsBean> conditions = new ArrayList<>();
		try {
			for (ThresholdConditionsDto thresholdConditionsDto : thresholdConditionsList) {
				if (actConditionDto.getConditionId() == thresholdConditionsDto.getConditionId()
						&& thresholdConditionsDto.getApplicable()) {
					ConditionsBean conditionsBean = new ConditionsBean();
					if (thresholdConditionsDto.getThresholdRange().equals(AppConstants.THRESHOLD_RANGE_BTW)) {
						conditionsBean.setType(this.getThresholdConditionType(thresholdConditionsDto.getThresholdId()))
								.setValue(thresholdConditionsDto.getValue()).setLogicalOperation(">");
						conditions.add(conditionsBean);

						conditionsBean = new ConditionsBean();
						conditionsBean.setType(this.getThresholdConditionType(thresholdConditionsDto.getThresholdId()))
								.setValue(thresholdConditionsDto.getMaxValue()).setLogicalOperation("<");
						conditions.add(conditionsBean);
					} else {
						conditionsBean.setType(this.getThresholdConditionType(thresholdConditionsDto.getThresholdId()))
								.setValue(thresholdConditionsDto.getValue()).setLogicalOperation(
										this.getLogicalOperatorByType(thresholdConditionsDto.getThresholdRange()));
						conditions.add(conditionsBean);
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityServiceImpl - getThresholdConditionsByRange()", e);
		}
		LOGGER.info("INFO: ActivityServiceImpl - getThresholdConditionsByRange() :: ends");
		return conditions;
	}

	/**
	 * Get the question step details for the provided response type
	 * <p>
	 * Response types are mentioned below:
	 * <ol>
	 * <li>Instruction
	 * <li>Boolean
	 * <li>Date
	 * <li>Email
	 * <li>Numeric
	 * <li>Text
	 * <li>Text Choice
	 * <li>Time Interval
	 * <li>Time of day
	 * <li>Value Picker
	 * 
	 * @author Mohan
	 * @param question
	 *            the {@link QuestionsDto} details
	 * @param questionChoiceList
	 *            the {@link QuestionChoiceDto} details list
	 * @return the question step details
	 */
	public Object getQuestionFormatByResponseType(QuestionsDto question, List<QuestionChoiceDto> questionChoiceList) {
		LOGGER.info("INFO: ActivityServiceImpl - getQuestionFormatByResponseType() :: starts");
		Object stepBean = new Object();
		try {
			switch (question.getResponseType()) {
				case AppConstants.RESPONSE_TYPE_INTSRUCTION:
					stepBean = this.getInstructionFormat(question);
					break;
				case AppConstants.RESPONSE_TYPE_BOOLEAN:
					stepBean = this.getBooleanFormat(question);
					break;
				case AppConstants.RESPONSE_TYPE_DATE:
					stepBean = this.getDateFormat(question);
					break;
				case AppConstants.RESPONSE_TYPE_EMAIL:
					stepBean = this.getEmailFormat(question);
					break;
				case AppConstants.RESPONSE_TYPE_NUMERIC:
					stepBean = this.getNumericFormat(question);
					break;
				case AppConstants.RESPONSE_TYPE_TEXT:
					stepBean = this.getTextFormat(question);
					break;
				case AppConstants.RESPONSE_TYPE_TEXT_CHOICE:
					stepBean = this.getTextChoiceFormat(question, questionChoiceList);
					break;
				case AppConstants.RESPONSE_TYPE_TIME_INTERVAL:
					stepBean = this.getTimeIntervalFormat(question);
					break;
				case AppConstants.RESPONSE_TYPE_TIME_OF_DAY:
					stepBean = this.getTimeOfDayFormat(question);
					break;
				case AppConstants.RESPONSE_TYPE_VALUE_PICKER:
					stepBean = this.getValuePickerFormat(question, questionChoiceList);
					break;
				default:
					stepBean = this.getActiveTaskStroopNArithmeticFormat(question);
					break;
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityServiceImpl - getQuestionFormatByResponseType()", e);
		}
		LOGGER.info("INFO: ActivityServiceImpl - getQuestionFormatByResponseType() :: ends");
		return stepBean;
	}

	/**
	 * Get the step metadata format for the provided question type from Qualtrics
	 * 
	 * @author Mohan
	 * @param question
	 *            the {@link QuestionsDto} details
	 * @return the step metadata
	 */
	public Object getStepStructureByType(QuestionsDto question) {
		LOGGER.info("INFO: ActivityServiceImpl - getStepStructureByType() :: starts");
		QuestionStepBean questionStep = new QuestionStepBean();
		InstructionStepBean instructionStep = new InstructionStepBean();
		TaskStepBean taskStepBean = new TaskStepBean();
		Object stepStructure = new Object();
		try {
			switch (question.getQtType()) {
				case AppConstants.STEP_INTSRUCTION:
					instructionStep.setType(AppConstants.STEP_INTSRUCTION).setResultType("")
							.setText(StringUtils.isEmpty(question.getQuestionText()) ? "" : question.getQuestionText())
							.setTitle("")
							.setQuestionId(StringUtils.isEmpty(question.getQualtricsQuestionsId()) ? ""
									: question.getQualtricsQuestionsId())
							.setKey(String.valueOf(question.getQuestionId())).setSkippable(question.getvDoesForceResponse())
							.setResultType(question.getResponseType());
					stepStructure = instructionStep;
					break;
				case AppConstants.STEP_QUESTION:
					questionStep
							.setQuestionId(StringUtils.isEmpty(question.getQualtricsQuestionsId()) ? ""
									: question.getQualtricsQuestionsId())
							.setKey(String.valueOf(question.getQuestionId())).setType(AppConstants.STEP_QUESTION)
							.setText(StringUtils.isEmpty(question.getQuestionText()) ? "" : question.getQuestionText())
							.setSkippable(question.getvDoesForceResponse()).setResultType(question.getResponseType());
					stepStructure = questionStep;
					break;
				case AppConstants.STEP_TASK:
					taskStepBean
							.setQuestionId(StringUtils.isEmpty(question.getQualtricsQuestionsId()) ? ""
									: question.getQualtricsQuestionsId())
							.setKey(String.valueOf(question.getQuestionId())).setType(AppConstants.STEP_TASK)
							.setText(StringUtils.isEmpty(question.getQuestionText()) ? "" : question.getQuestionText())
							.setSkippable(question.getvDoesForceResponse()).setResultType(question.getResponseType());
					stepStructure = taskStepBean;
					break;
				default:
					break;
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityServiceImpl - getStepStructureByType()", e);
		}
		LOGGER.info("INFO: ActivityServiceImpl - getStepStructureByType() :: ends");
		return stepStructure;
	}

	/**
	 * Get the Instruction question type format
	 * 
	 * @author Mohan
	 * @param question
	 *            the {@link QuestionsDto} details
	 * @return the instruction step format details
	 */
	public Object getInstructionFormat(QuestionsDto question) {
		LOGGER.info("INFO: ActivityServiceImpl - getInstructionFormat() :: starts");
		Object instructionStepBean = new Object();
		try {
			instructionStepBean = this.getStepStructureByType(question);
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityServiceImpl - getInstructionFormat()", e);
		}
		LOGGER.info("INFO: ActivityServiceImpl - getInstructionFormat() :: ends");
		return instructionStepBean;
	}

	/**
	 * Get the boolean question format
	 * 
	 * @author Mohan
	 * @param question
	 *            the {@link QuestionsDto} details
	 * @return the boolean question format
	 */
	public Object getBooleanFormat(QuestionsDto question) {
		LOGGER.info("INFO: ActivityServiceImpl - getBooleanFormat() :: starts");
		Map<String, Object> map = new HashMap<>();
		QuestionStepBean questionStep = new QuestionStepBean();
		try {
			questionStep = (QuestionStepBean) this.getStepStructureByType(question);

			questionStep.setFormat(map);
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityServiceImpl - getBooleanFormat()", e);
		}
		LOGGER.info("INFO: ActivityServiceImpl - getBooleanFormat() :: ends");
		return questionStep;
	}

	/**
	 * Get the date question format
	 * 
	 * @author Mohan
	 * @param question
	 *            the {@link QuestionsDto} details
	 * @return the date question format
	 */
	public Object getDateFormat(QuestionsDto question) {
		LOGGER.info("INFO: ActivityServiceImpl - getDateFormat() :: starts");
		Map<String, Object> map = new LinkedHashMap<>();
		QuestionStepBean questionStep = new QuestionStepBean();
		try {
			questionStep = (QuestionStepBean) this.getStepStructureByType(question);
			map.put(AppEnums.QF_STYLE.value(),
					StringUtils.isEmpty(question.getResponseSubType()) ? AppConstants.DATE_FORMAT_TYPE_DATE
							: question.getResponseSubType());

			questionStep.setFormat(map);
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityServiceImpl - getDateFormat()", e);
		}
		LOGGER.info("INFO: ActivityServiceImpl - getDateFormat() :: ends");
		return questionStep;
	}

	/**
	 * Get the email question format
	 * 
	 * @author Mohan
	 * @param question
	 *            the {@link QuestionsDto} details
	 * @return the email question format
	 */
	public Object getEmailFormat(QuestionsDto question) {
		LOGGER.info("INFO: ActivityServiceImpl - getEmailFormat() :: starts");
		Map<String, Object> map = new LinkedHashMap<>();
		QuestionStepBean questionStep = new QuestionStepBean();
		try {
			questionStep = (QuestionStepBean) this.getStepStructureByType(question);

			map.put(AppEnums.QF_PLACEHOLDER.value(), "");

			questionStep.setFormat(map);
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityServiceImpl - getEmailFormat()", e);
		}
		LOGGER.info("INFO: ActivityServiceImpl - getEmailFormat() :: ends");
		return questionStep;
	}

	/**
	 * Get the numeric question format
	 * 
	 * @author Mohan
	 * @param question
	 *            the {@link QuestionsDto} details
	 * @return the numeric question format
	 */
	public Object getNumericFormat(QuestionsDto question) {
		LOGGER.info("INFO: ActivityServiceImpl - getNumericFormat() :: starts");
		Map<String, Object> map = new LinkedHashMap<>();
		QuestionStepBean questionStep = new QuestionStepBean();
		try {
			questionStep = (QuestionStepBean) this.getStepStructureByType(question);

			map.put(AppEnums.QF_STYLE.value(),
					StringUtils.isEmpty(question.getResponseSubType()) ? AppConstants.NUMERIC_FORMAT_INTEGER
							: question.getResponseSubType());
			map.put(AppEnums.QF_UNIT.value(), "");
			map.put(AppEnums.QF_MIN_VALUE.value(), 0);
			map.put(AppEnums.QF_MAX_VALUE.value(), 0);
			map.put(AppEnums.QF_PLACEHOLDER.value(), "");

			questionStep.setFormat(map);
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityServiceImpl - getNumericFormat()", e);
		}
		LOGGER.info("INFO: ActivityServiceImpl - getNumericFormat() :: ends");
		return questionStep;
	}

	/**
	 * Get the text question format
	 * 
	 * @author Mohan
	 * @param question
	 *            the {@link QuestionsDto} details
	 * @return the text question format
	 */
	public Object getTextFormat(QuestionsDto question) {
		LOGGER.info("INFO: ActivityServiceImpl - getTextFormat() :: starts");
		Map<String, Object> map = new LinkedHashMap<>();
		QuestionStepBean questionStep = new QuestionStepBean();
		try {
			questionStep = (QuestionStepBean) this.getStepStructureByType(question);

			map.put(AppEnums.QF_MAX_LENGTH.value(), 0);
			map.put(AppEnums.QF_VALIDATION_REGEX.value(), "");
			map.put(AppEnums.QF_INVALID_MESSAGE.value(), "");
			map.put(AppEnums.QF_MULTIPLE_LINES.value(), false);
			map.put(AppEnums.QF_PLACEHOLDER.value(), "");

			questionStep.setFormat(map);
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityServiceImpl - getTextFormat()", e);
		}
		LOGGER.info("INFO: ActivityServiceImpl - getTextFormat() :: ends");
		return questionStep;
	}

	/**
	 * Get the text choice question format
	 * 
	 * @author Mohan
	 * @param question
	 *            the {@link QuestionsDto} details
	 * @return the text choice question format
	 */
	public Object getTextChoiceFormat(QuestionsDto question, List<QuestionChoiceDto> questionChoiceList) {
		LOGGER.info("INFO: ActivityServiceImpl - getTextChoiceFormat() :: starts");
		Map<String, Object> map = new LinkedHashMap<>();
		QuestionStepBean questionStep = new QuestionStepBean();
		List<String> textChoices = new ArrayList<>();
		try {
			questionStep = (QuestionStepBean) this.getStepStructureByType(question);
			if (questionChoiceList != null && !questionChoiceList.isEmpty()) {
				for (QuestionChoiceDto choices : questionChoiceList) {
					textChoices.add(StringUtils.isEmpty(choices.getDescription()) ? "" : choices.getDescription());
				}
			}
			map.put(AppEnums.QF_TEXT_CHOICES.value(), textChoices);
			map.put(AppEnums.QF_SELECTION_STYLE.value(),
					StringUtils.isEmpty(question.getResponseSubType()) ? AppConstants.TEXT_CHOICE_FORMAT_SINGLE
							: question.getResponseSubType());

			questionStep.setFormat(map);
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityServiceImpl - getTextChoiceFormat()", e);
		}
		LOGGER.info("INFO: ActivityServiceImpl - getTextChoiceFormat() :: ends");
		return questionStep;
	}

	/**
	 * Get the time interval question format
	 * 
	 * @author Mohan
	 * @param question
	 *            the {@link QuestionsDto} details
	 * @return the time interval question format
	 */
	public Object getTimeIntervalFormat(QuestionsDto question) {
		LOGGER.info("INFO: ActivityServiceImpl - getTimeIntervalFormat() :: starts");
		Map<String, Object> map = new LinkedHashMap<>();
		QuestionStepBean questionStep = new QuestionStepBean();
		try {
			questionStep = (QuestionStepBean) this.getStepStructureByType(question);

			map.put(AppEnums.QF_DEFAULT.value(), 0);
			map.put(AppEnums.QF_STEP.value(), 1);

			questionStep.setFormat(map);
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityServiceImpl - getTimeIntervalFormat()", e);
		}
		LOGGER.info("INFO: ActivityServiceImpl - getTimeIntervalFormat() :: ends");
		return questionStep;
	}

	/**
	 * Get the time of day question format
	 * 
	 * @author Mohan
	 * @param question
	 *            the {@link QuestionsDto} details
	 * @return the time of day question format
	 */
	public Object getTimeOfDayFormat(QuestionsDto question) {
		LOGGER.info("INFO: ActivityServiceImpl - getTimeOfDayFormat() :: starts");
		Map<String, Object> map = new LinkedHashMap<>();
		QuestionStepBean questionStep = new QuestionStepBean();
		try {
			questionStep = (QuestionStepBean) this.getStepStructureByType(question);

			map.put(AppEnums.QF_DEFAULT.value(), 0);

			questionStep.setFormat(map);
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityServiceImpl - getTimeOfDayFormat()", e);
		}
		LOGGER.info("INFO: ActivityServiceImpl - getTimeOfDayFormat() :: ends");
		return questionStep;
	}

	/**
	 * Get the value picker question format
	 * 
	 * @author Mohan
	 * @param question
	 *            the {@link QuestionsDto} details
	 * @param questionChoiceList
	 *            the {@link QuestionChoiceDto} details list
	 * @return the value picker question format
	 */
	public Object getValuePickerFormat(QuestionsDto question, List<QuestionChoiceDto> questionChoiceList) {
		LOGGER.info("INFO: ActivityServiceImpl - getValuePickerFormat() :: starts");
		Map<String, Object> map = new LinkedHashMap<>();
		QuestionStepBean questionStep = new QuestionStepBean();
		List<String> valuePickerChoices = new ArrayList<>();
		try {
			questionStep = (QuestionStepBean) this.getStepStructureByType(question);
			if (questionChoiceList != null && !questionChoiceList.isEmpty()) {
				for (QuestionChoiceDto choices : questionChoiceList) {
					valuePickerChoices
							.add(StringUtils.isEmpty(choices.getDescription()) ? "" : choices.getDescription());
				}
			}
			map.put(AppEnums.QF_VALUE_PICKER_CHOICES.value(), valuePickerChoices); // ArrayList of string

			questionStep.setFormat(map);
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityServiceImpl - getValuePickerFormat()", e);
		}
		LOGGER.info("INFO: ActivityServiceImpl - getValuePickerFormat() :: ends");
		return questionStep;
	}

	/**
	 * Get the task details for the provided type.
	 * <p>
	 * Tasks are mentioned below:
	 * <ol>
	 * <li>Stroop Test
	 * <li>Arithmetic Test
	 * 
	 * @author Mohan
	 * @param question
	 *            the {@link QuestionsDto} details
	 * @return the task format
	 */
	public Object getActiveTaskStroopNArithmeticFormat(QuestionsDto question) {
		LOGGER.info("INFO: ActivityServiceImpl - getActiveTaskStroopNArithmeticFormat() :: starts");
		Map<String, Object> map = new LinkedHashMap<>();
		Object format = new Object();
		QuestionStepBean questionStep = new QuestionStepBean();
		TaskStepBean taskStepBean = new TaskStepBean();
		try {
			if (question.getQtType().equals(AppConstants.STEP_TASK)) {
				taskStepBean = (TaskStepBean) this.getStepStructureByType(question);
				format = taskStepBean;
			} else {
				questionStep = (QuestionStepBean) this.getStepStructureByType(question);
				questionStep.setFormat(map);
				format = questionStep;
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityServiceImpl - getActiveTaskStroopNArithmeticFormat()", e);
		}
		LOGGER.info("INFO: ActivityServiceImpl - getActiveTaskStroopNArithmeticFormat() :: ends");
		return format;
	}

	/**
	 * Get the last run identifier for the provided {@link UserActivitiesDto}
	 * details
	 * 
	 * @author Mohan
	 * @param userActivities
	 *            the {@link UserActivitiesDto} details
	 * @return the last run identifier
	 */
	public int getLastRunIdByActivity(UserActivitiesDto userActivities) {
		LOGGER.info("INFO: ActivityServiceImpl - getLastRunIdByActivity() :: starts");
		int lastRunId = userActivities.getActivityRunId();
		try {
			// Check the current run is is today or not, if not get the lastRunId from the
			// currentRunId
			if (StringUtils.isNotEmpty(userActivities.getCurrentRunDate())
					&& userActivities.getCurrentRunDate().equals(AppUtil.getCurrentDate())) {
				lastRunId = userActivities.getActivityRunId() >= 2 ? userActivities.getActivityRunId() - 1 : 0;
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityServiceImpl - getLastRunIdByActivity()", e);
		}
		LOGGER.info("INFO: ActivityServiceImpl - getLastRunIdByActivity() :: ends");
		return lastRunId;
	}

	/**
	 * Update the Triggred survey runs details for the provided run state, current
	 * date time
	 * 
	 * @author Mohan
	 * @param userActivityRuns
	 *            the {@link UserActivitiesRunsDto} details
	 * @param user
	 *            the {@link UserDto} details
	 * @param runState
	 *            the run state
	 * @param currentDate
	 *            the current date time
	 * @return the updated {@link UserActivitiesRunsDto} details
	 */
	public UserActivitiesRunsDto updateTriggeredSurveyRunsDate(UserActivitiesRunsDto userActivityRuns, UserDto user,
			String runState, String currentDate) {
		LOGGER.info("INFO: ActivityServiceImpl - updateTriggeredSurveyRunsDate() :: starts");
		UserActivitiesRunsDto updateActivityRunsDto = userActivityRuns;
		TemporalConditionDto temporalConditionDto = new TemporalConditionDto();
		try {

			// Calculate the survey expired datetime for Triggred surveys (i.e. valid for 1
			// hour from the start date time) only if the run State is 'start'
			if (runState.equals(AppConstants.RUN_STATE_START)) {
				userActivityRuns.setRunStartsOn(AppUtil.getFormattedDateByTimeZone(currentDate,
						AppConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_FORMAT, AppConstants.SDF_DATE_TIME_FORMAT,
						user.getTimeZone()));

				temporalConditionDto = activityDao.fetchTemporalConditionDetails(
						String.valueOf(updateActivityRunsDto.getConditionId()), AppConstants.FIND_BY_TYPE_CONDITIONID);

				String endDateTime = AppUtil.addMinutes(userActivityRuns.getRunStartsOn(), 60);
				String actualRunEndDateTime = AppUtil.getFormattedDate(userActivityRuns.getRunStartsOn(),
						AppConstants.SDF_DATE_TIME_FORMAT, AppConstants.SDF_DATE_FORMAT) + " "
						+ temporalConditionDto.getEndTime();
				if (AppConstants.SDF_DATE_TIME.parse(endDateTime)
						.after(AppConstants.SDF_DATE_TIME.parse(actualRunEndDateTime))) {
					userActivityRuns.setRunEndsOn(actualRunEndDateTime);
				} else {
					userActivityRuns.setRunEndsOn(endDateTime);
				}
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityServiceImpl - updateTriggeredSurveyRunsDate()", e);
		}
		LOGGER.info("INFO: ActivityServiceImpl - updateTriggeredSurveyRunsDate() :: ends");
		return updateActivityRunsDto;
	}

	/**
	 * Notify participient and admins about reaching a new reward level and eligible
	 * for compensation
	 * 
	 * @author Mohan
	 * @param userId
	 *            the user identifier
	 * @param studyId
	 *            the study identifier
	 * @param duration
	 *            the time taken to complete the run
	 * @param activityCondition
	 *            the {@link ActivityConditionDto} details
	 * @throws CustomException
	 */
	public void notifyParticipantsAboutNewLevelAndRewardPoints(String userId, String studyId, Long duration,
			ActivityConditionDto activityCondition) throws CustomException {
		LOGGER.info("INFO: ActivityServiceImpl - notifyParticipantsAboutNewLevelAndRewardPoints() :: starts");
		UserDto user = null;
		UserStudiesDto userStudies = null;
		StudiesDto studyDto = null;
		List<AdminUsersDto> adminUsers = null;
		List<String> toAdminList = new ArrayList<>();
		int currentLevel = 0;
		Boolean isEligibleForRewards = false;
		String newLevel = "";
		String oldLevel = "";
		Long previousPoints = 0L;
		try {
			user = userDao.fetchUserDetails(userId, AppConstants.FIND_BY_TYPE_USERID);
			userStudies = studyDao.fetchUserStudiesDetails(userId, studyId, AppConstants.FIND_BY_TYPE_USERID_STUDYID);
			studyDto = studyDao.fetchStudyDetailsByStudyId(studyId);
			if (user != null && userStudies != null && studyDto != null) {

				// Get the reward point pre-requisist based on the activtyType
				switch (activityCondition.getActivityType()) {
					case AppConstants.ACTIVITY_TYPE_ARITHMETIC_WEB:
						if (duration <= AppConstants.ARITHMETIC_TEST_COMPLETION_MIN_TIME)
							isEligibleForRewards = true;
						break;
					case AppConstants.ACTIVITY_TYPE_STROOP_WEB:
						if (duration <= AppConstants.STROOP_TEST_COMPLETION_MIN_TIME)
							isEligibleForRewards = true;
						break;
					case AppConstants.ACTIVITY_TYPE_AUT_WEB:
						if(duration <= AppConstants.AUT_TEST_COMPLETION_MIN_TIME)
							isEligibleForRewards = true;
						break;
					case AppConstants.ACTIVITY_TYPE_CRAT_WEB:
						if(duration <= AppConstants.CRAT_TEST_COMPLETION_MIN_TIME)
							isEligibleForRewards = true;
						break;
					default:
						isEligibleForRewards = true;
						break;
				}

				if (isEligibleForRewards) {

					previousPoints = user.getPointsEarned();

					// Add points after completion based on the activityCondition
					Long totalPointsEarned = user.getPointsEarned() + activityCondition.getPointsPerCompletion();

					// Update points earned by user on completion of the run
					user.setPointsEarned(totalPointsEarned);
					userDao.saveOrUpdateUserDetails(user, AppConstants.DB_UPDATE);

					List<RewardLevelsDto> rewardLevelsList = activityDao.fetchRewardLevelsList();
					if (!rewardLevelsList.isEmpty()) {
						for (RewardLevelsDto rewardLevelsDto : rewardLevelsList) {
							if (totalPointsEarned >= rewardLevelsDto.getPoints()) {
								currentLevel = rewardLevelsDto.getLevel() + 1;
							}
						}

						// Trigger mail to admin and participants about the new level
						if (currentLevel != user.getRewardLevel() && currentLevel > 0) {

							// Check is the current level is final level or not
							boolean isFinalLevel = false;
							boolean isBonusLevel = false;
							if (currentLevel == (rewardLevelsList.get(rewardLevelsList.size() - 1).getLevel() + 1)) {
								currentLevel--;
								isFinalLevel = true;

								if (totalPointsEarned >= AppConstants.MAX_BONUS_POINTS
										&& previousPoints < AppConstants.MAX_BONUS_POINTS) {
									isBonusLevel = true;
								}
							}

							newLevel = String.valueOf(currentLevel);
							oldLevel = String.valueOf(currentLevel - 1);

							if (previousPoints < rewardLevelsList.get(rewardLevelsList.size() - 1).getPoints()
									|| isBonusLevel) {/*
								// Participant reaches a new level and receives a reward
								Map<String, String> mailContentMap = MailContent
										.participantReachesANewLevelAndRecievesRewards(user.getFirstName(), newLevel,
												oldLevel, studyDto.getStudyName(), isFinalLevel, isBonusLevel,
												user.getLanguage());
								Mail.sendEmail(user.getEmail(), mailContentMap.get(AppConstants.MAIL_SUBJECT),
										mailContentMap.get(AppConstants.MAIL_BODY));

								// Participant reaches a new Level [Mail Admin]
								adminUsers = studyDao.fetchAllAdminUsers();
								if (!adminUsers.isEmpty()) {
									toAdminList = adminUsers.stream().map(AdminUsersDto::getEmail)
											.collect(Collectors.toList());

									Map<String, String> adminMailContentMap = MailContent.participantReachesNewLevel(
											userStudies.getEnrollmentId(),
											(isFinalLevel
													? (isBonusLevel ? AppConstants.BONUS_LEVEL
															: String.valueOf(currentLevel))
													: String.valueOf(currentLevel - 1)));
									Mail.sendEmailToMany(adminMailContentMap.get(AppConstants.MAIL_SUBJECT),
											adminMailContentMap.get(AppConstants.MAIL_BODY), toAdminList, null, null);
								}
							*/}

							// Update reward level for user
							user.setRewardLevel(currentLevel).setModifiedOn(AppUtil.getCurrentDateTime())
									.setPointsEarned(totalPointsEarned);
							userDao.saveOrUpdateUserDetails(user, AppConstants.DB_UPDATE);
						}
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityServiceImpl - notifyParticipantsAboutNewLevelAndRewardPoints()", e);
		}
		LOGGER.info("INFO: ActivityServiceImpl - notifyParticipantsAboutNewLevelAndRewardPoints() :: ends");
	}
	/*
	@Override
	public Lass4USensorDataResponse getSensorData(String deviceId) throws CustomException {
		LOGGER.info("INFO: ActivityServiceImpl - getSensorData() :: starts");
		Lass4USensorDataResponse lass4USensorDataResponse = null;
		Lass4UBeanSensorData lass4UBean = null ;
		HarvardIAQ harvardIAQData = null;
		HarvardIAQSensorData harvardIAQ = null;
		try {
			harvardIAQData = activityDao.fetchLass4USesorData(deviceId);
			if(null != harvardIAQData) {
				lass4UBean = new Lass4UBeanSensorData();
				lass4USensorDataResponse = new Lass4USensorDataResponse();
				lass4UBean.setSource("");
				List<FeedSensorData> feedList = new ArrayList<>();
				FeedSensorData feed = new FeedSensorData();
				harvardIAQ = new HarvardIAQSensorData();
				harvardIAQ.setApp(harvardIAQData.getApp());
				harvardIAQ.setS_g8(String.valueOf(String.format("%.2f", harvardIAQData.getsG8e())).replace("F", ""));
				harvardIAQ.setS_t0(String.valueOf(String.format("%.2f", harvardIAQData.getTemperature())).replace("F", ""));
				harvardIAQ.setS_d0(String.valueOf(String.format("%.2f", harvardIAQData.getPm25())).replace("F", ""));
				harvardIAQ.setTimestamp(harvardIAQData.getTimestamp());
				harvardIAQ.setS_d1(String.valueOf(String.format("%.2f", harvardIAQData.getsD1())).replace("F", ""));
				harvardIAQ.setS_h0(String.valueOf(String.format("%.2f", harvardIAQData.getRelativeHumidity())).replace("F", ""));
				harvardIAQ.setS_d2(String.valueOf(String.format("%.2f", harvardIAQData.getsD2())).replace("F", ""));
				harvardIAQ.setDate(harvardIAQData.getDate());
				harvardIAQ.setTime(harvardIAQData.getTime());
				harvardIAQ.setS_n0(harvardIAQData.getS_n0());
				harvardIAQ.setS_l0(String.valueOf(String.format("%.2f", harvardIAQData.getLight())).replace("F", ""));
				feed.setHarvardIAQ(harvardIAQ);
				feedList.add(feed);
				lass4UBean.setFeeds(feedList);
				lass4UBean.setVersion("");
				lass4UBean.setNumOfRecords(1);
				lass4UBean.setDeviceId(deviceId);
				lass4USensorDataResponse.setLass4UBean(lass4UBean);
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityServiceImpl - getSensorData()", e);
		}
		LOGGER.info("INFO: ActivityServiceImpl - getSensorData() :: ends");
		return lass4USensorDataResponse;
	}*/
	
	@Override
	public Lass4USensorDataResponse getSensorData(String deviceId,String timeZone) throws CustomException {
		LOGGER.info("INFO: ActivityServiceImpl - getSensorData() :: starts");
		Lass4USensorDataResponse lass4USensorDataResponse = null;
		Lass4UBeanSensorData lass4UBean = null ;
		HarvardIAQ harvardIAQData = null;
		HarvardIAQSensorData harvardIAQ = null;
		try {
			harvardIAQData = activityDao.fetchLass4USesorData(deviceId,timeZone);
			if(null != harvardIAQData) {
				lass4UBean = new Lass4UBeanSensorData();
				lass4USensorDataResponse = new Lass4USensorDataResponse();
				lass4UBean.setSource("");
				List<FeedSensorData> feedList = new ArrayList<>();
				FeedSensorData feed = new FeedSensorData();
				harvardIAQ = new HarvardIAQSensorData();
				harvardIAQ.setApp(harvardIAQData.getApp());
				harvardIAQ.setS_g8(String.valueOf(String.format("%.2f", harvardIAQData.getsG8e())).replace("F", ""));
				harvardIAQ.setS_t0(String.valueOf(String.format("%.2f", harvardIAQData.getTemperature())).replace("F", ""));
				harvardIAQ.setS_d0(String.valueOf(String.format("%.2f", harvardIAQData.getPm25())).replace("F", ""));
				harvardIAQ.setTimestamp(harvardIAQData.getTimestamp());
				harvardIAQ.setS_d1(String.valueOf(String.format("%.2f", harvardIAQData.getsD1())).replace("F", ""));
				harvardIAQ.setS_h0(String.valueOf(String.format("%.2f", harvardIAQData.getRelativeHumidity())).replace("F", ""));
				harvardIAQ.setS_d2(String.valueOf(String.format("%.2f", harvardIAQData.getsD2())).replace("F", ""));
				harvardIAQ.setDate(harvardIAQData.getDate());
				harvardIAQ.setTime(harvardIAQData.getTime());
				harvardIAQ.setS_n0(harvardIAQData.getS_n0());
				harvardIAQ.setS_l0(String.valueOf(String.format("%.2f", harvardIAQData.getLight())).replace("F", ""));
				feed.setHarvardIAQ(harvardIAQ);
				feedList.add(feed);
				lass4UBean.setFeeds(feedList);
				lass4UBean.setVersion("");
				lass4UBean.setNumOfRecords(1);
				lass4UBean.setDeviceId(deviceId);
				lass4USensorDataResponse.setLass4UBean(lass4UBean);
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityServiceImpl - getSensorData()", e);
		}
		LOGGER.info("INFO: ActivityServiceImpl - getSensorData() :: ends");
		return lass4USensorDataResponse;
	}
	
	@Override
	public String isDeviceIdExist(String deviceId) {
		LOGGER.info("INFO: ActivityServiceImpl - isDeviceIdExist() :: starts");
		String message = AppConstants.FAILURE;
		try {
			message = activityDao.isDeviceIdExist(deviceId);
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityServiceImpl - isDeviceIdExist()", e);
		}
		LOGGER.info("INFO: ActivityServiceImpl - isDeviceIdExist() :: ends");
		return message;
	}
	
	@Override
	public boolean isLass4USensorDataExist(String lass4UId) {
		LOGGER.info("INFO: ActivityServiceImpl - isLass4USensorDataExist() :: starts");
		boolean getData = false;
		try{
			getData = activityDao.isLass4USensorDataExist(lass4UId);
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityServiceImpl - isLass4USensorDataExist()", e);
		}
		LOGGER.info("INFO: ActivityServiceImpl - isLass4USensorDataExist() :: ends");
		return getData;
	}
	
	@Override
	public boolean insertSensorData(String timestamp,String app,String device_id,String s_g8,String s_t0,
			String s_d0,String s_d1,String s_h0,String s_d2,String s_n0,String s_l0) {
		LOGGER.info("INFO: ActivityServiceImpl - insertSensorData() :: starts");
		boolean addData = false;
		try{
			addData = activityDao.insertSensorData(timestamp, app, device_id, s_g8, s_t0, s_d0, s_d1, s_h0, s_d2, s_n0, s_l0);
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityServiceImpl - insertSensorData()", e);
		}
		LOGGER.info("INFO: ActivityServiceImpl - insertSensorData() :: ends");
		return addData;
	}

	@Override
	public KeywordsResponse getKeywordsList(String language) throws CustomException {
		LOGGER.info("INFO: ActivityServiceImpl - getKeywordsList() :: starts");
		KeywordsResponse choiceBean = null;
		try {
			
			choiceBean = activityDao.getKeywordList(language);
			
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityServiceImpl - getKeywordsList()", e);
		}
		LOGGER.info("INFO: ActivityServiceImpl - getKeywordsList() :: ends");
		return choiceBean;
	}

	@Override
	public String getUserTimeZoneByAccessToken(String accessToken) {
		LOGGER.info("INFO: ActivityServiceImpl - getUserTimeZoneByAccessToken() :: starts");
		String token = "";
		try {
			token =   activityDao.getUserTimeZone(accessToken);
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityServiceImpl - getUserTimeZoneByAccessToken()", e);
		}
		LOGGER.info("INFO: ActivityServiceImpl - getUserTimeZoneByAccessToken() :: ends");
		return token;
	}

	@Override
	public ActivityConditionDto fetchActivityConditionDetailsByIdandType(String activityConditionId, String type)
			throws CustomException {
		LOGGER.info("INFO: ActivityServiceImpl - fetchActivityConditionDetailsByIdandType() :: Starts");
		ActivityConditionDto conditionDto = null;
		try {
			conditionDto = 	activityDao.fetchActivityConditionDetails(activityConditionId, type);
		} catch (Exception e) {
			LOGGER.error("INFO: ActivityServiceImpl - fetchActivityConditionDetailsByIdandType() :: ERROR",e);
		}
		LOGGER.info("INFO: ActivityServiceImpl - fetchActivityConditionDetailsByIdandType() :: Ends");
		return conditionDto;
	}
	
	//Added by Fathima for crat activities 
	
	@SuppressWarnings("unlikely-arg-type")
	public List<CratWordsDto> fetchCratWords(String userId)
			throws CustomException {
		LOGGER.info("INFO: ActivityServiceImpl - fetchCratWords() :: Starts");
		List<CratWordsDto> cratWordsList = new ArrayList<CratWordsDto>();
		List<CratWordsUserMapDto> cratWordsUserMapList = null;
		List<Integer> wordIdList = new ArrayList<Integer>();;
		List<CratWordsDto> cratWordsListUnique = new ArrayList<CratWordsDto>();
		List<CratWordsDto> cratWordsListResponse = new ArrayList<CratWordsDto>();
		try {
			//Fetching cratWordId for the given user
			cratWordsUserMapList = 	activityDao.fetchCratWordsId(userId);
			if(null != cratWordsUserMapList && !cratWordsUserMapList.isEmpty()) {
				for(CratWordsUserMapDto map : cratWordsUserMapList) {
					wordIdList.add(map.getCratWordsId());
				}
			}
			
			//Fetching all combination of crat words
			cratWordsList = activityDao.fetchCratWords();
			if(null != cratWordsList && !cratWordsList.isEmpty()) {
				for(CratWordsDto cratWords : cratWordsList) {
					//fetching the unique crat words for the user
					if(!wordIdList.contains(cratWords.getCratWordsId())) {
						cratWordsListUnique.add(cratWords);
					}
				}
			}
			
			if(null != cratWordsListUnique) {
				if(cratWordsListUnique.size() == 0) {
					activityDao.deleteCratWordsUserMap(userId);
					int count = 0;
					while(count < 8) {
						cratWordsListResponse.add(cratWordsList.get(count));
						count++;
					}
					for(CratWordsDto saveWords : cratWordsListResponse) {
						CratWordsUserMapDto mapDto = new CratWordsUserMapDto();
						mapDto.setUserId(Integer.valueOf(userId)).setCratWordsId(saveWords.getCratWordsId());
						activityDao.saveCratWordsUserMap(mapDto);
					}
				}else if(cratWordsListUnique.size() < 8) {
					cratWordsListResponse.addAll(cratWordsListUnique);
					for(CratWordsDto saveWords : cratWordsListResponse) {
						CratWordsUserMapDto mapDto = new CratWordsUserMapDto();
						mapDto.setUserId(Integer.valueOf(userId)).setCratWordsId(saveWords.getCratWordsId());
						activityDao.saveCratWordsUserMap(mapDto);
					}
					int count = cratWordsListResponse.size();
					
					while(count < 8) {
						if(!cratWordsListUnique.contains(cratWordsList.get(count).getCratWordsId())) {
							cratWordsListResponse.add(cratWordsList.get(count));
							count++;
						}
					}
				}else if(cratWordsListUnique.size() >= 8){
					int count = 0;
					while(count < 8) {
						cratWordsListResponse.add(cratWordsListUnique.get(count));
						count++;
					}
					for(CratWordsDto saveWords : cratWordsListResponse) {
						CratWordsUserMapDto mapDto = new CratWordsUserMapDto();
						mapDto.setUserId(Integer.valueOf(userId)).setCratWordsId(saveWords.getCratWordsId());
						activityDao.saveCratWordsUserMap(mapDto);
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("INFO: ActivityServiceImpl - fetchCratWords() :: ERROR",e);
		}
		LOGGER.info("INFO: ActivityServiceImpl - fetchCratWords() :: Ends");
		return cratWordsListResponse;
	}
	
	/*
	 * public Object getCratFormat(QuestionsDto question,String userId) {
	 * LOGGER.info("INFO: ActivityServiceImpl - getCratFormat() :: starts"); Object
	 * format = new Object(); TaskStepBean taskStepBean = new TaskStepBean(); try {
	 * if (question.getQtType().equals(AppConstants.STEP_TASK)) { taskStepBean
	 * .setQuestionId(StringUtils.isEmpty(question.getQualtricsQuestionsId()) ? "" :
	 * question.getQualtricsQuestionsId())
	 * .setKey(String.valueOf(question.getQuestionId())).setType(AppConstants.
	 * STEP_TASK) .setText(StringUtils.isEmpty(question.getQuestionText()) ? "" :
	 * question.getQuestionText())
	 * .setSkippable(question.getvDoesForceResponse()).setResultType(question.
	 * getResponseType());
	 * 
	 * format = taskStepBean; } } catch (Exception e) {
	 * LOGGER.error("ERROR: ActivityServiceImpl - getCratFormat()", e); }
	 * LOGGER.info("INFO: ActivityServiceImpl - getCratFormat() :: ends"); return
	 * format; }
	 */
	
	/*
	 * @Scheduled(cron = "0 0/1 * * * 1,2,3,4,5") public void test() { try {
	 * this.fetchCratWords("1"); }catch(Exception e) { e.printStackTrace(); }
	 * 
	 * }
	 */

}
