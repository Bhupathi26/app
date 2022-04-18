package com.gba.ws.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.gba.ws.bean.ActivityListBean;
import com.gba.ws.bean.ActivityListResponse;
import com.gba.ws.dao.ActivityDao;
import com.gba.ws.dao.StudyDao;
import com.gba.ws.dao.UserDao;
import com.gba.ws.model.ActivitiesDto;
import com.gba.ws.model.ActivityGroupDto;
import com.gba.ws.model.ActivityConditionDto;
import com.gba.ws.model.EnrollmentTokensDto;
import com.gba.ws.model.RewardLevelsDto;
import com.gba.ws.model.StudyConsentDto;
import com.gba.ws.model.TemporalConditionDto;
import com.gba.ws.model.ThresholdConditionsDto;
import com.gba.ws.model.UserActivitiesDto;
import com.gba.ws.model.UserDto;
import com.gba.ws.model.UserStudiesDto;
import com.gba.ws.util.AppConstants;
import com.gba.ws.util.AppUtil;
import com.gba.ws.util.ErrorCode;

@RunWith(value = SpringRunner.class)
@SpringBootTest(classes = {ActivityServiceImpl.class})
/*@FixMethodOrder(MethodSorters.NAME_ASCENDING)*/
public class ActivityServiceImplTest {

	@Autowired
	private ActivityService activityService;

	@Autowired
	private ActivityServiceImpl activityServiceImpl;

	@MockBean
	private ActivityDao activityDao;

	@MockBean
	private StudyDao studyDao;

	@MockBean
	private UserDao userDao;

	/*@MockBean
	private AppUtil appUtil;*/


	@Autowired
	private ActivityServiceImpl activityServiceImplSpy;

	@Before
	public void setUp() throws Exception {
		this.activityServiceImplSpy = spy(activityServiceImpl);
	}

	@Test
	public void testValidateActivityId() throws Exception {

		boolean isValid = false;

		when(activityDao.validateActivityId(anyString())).thenReturn(false);
		isValid = activityService.validateActivityId("AC_1");
		assertThat(isValid).isEqualTo(false);

		when(activityDao.validateActivityId(anyString())).thenReturn(true);
		isValid = activityService.validateActivityId("AC_1");
		assertThat(isValid).isEqualTo(true);
	}

	@Test
	public void testValidateUserEnrolledToStudyWhenUserNotSavedConsent() throws Exception {

		StudyConsentDto studyConsentDto = null;

		when(studyDao.fetchStudyConsentDetails(anyString(), anyString(), anyString())).thenReturn(studyConsentDto);
		when(activityDao.validateActivityId(anyString())).thenReturn(false);

		assertThat(studyDao.fetchStudyConsentDetails("1", "1", AppConstants.FIND_BY_TYPE_USERID_STUDYID)).isEqualTo(studyConsentDto);

		assertThat(activityService.validateUserEnrolledToStudy("1", "1")).isEqualTo(false);
	}

	@Test
	public void testValidateUserEnrolledToStudyWhenUserNotEnrolledYet() throws Exception {

		StudyConsentDto studyConsentDto = new StudyConsentDto();
		UserStudiesDto userStudies = null;

		when(studyDao.fetchStudyConsentDetails(anyString(), anyString(), anyString())).thenReturn(studyConsentDto);
		when(studyDao.fetchUserStudiesDetails(anyString(), anyString(), anyString())).thenReturn(userStudies);
		when(activityDao.validateActivityId(anyString())).thenReturn(false);

		assertThat(studyDao.fetchStudyConsentDetails("1", "1", AppConstants.FIND_BY_TYPE_USERID_STUDYID)).isEqualTo(studyConsentDto);
		assertThat(studyDao.fetchUserStudiesDetails("1", "1", AppConstants.FIND_BY_TYPE_USERID_STUDYID)).isEqualTo(userStudies);

		assertThat(activityService.validateUserEnrolledToStudy("1", "1")).isEqualTo(false);
	}

	@Test
	public void testValidateUserEnrolledToStudy() throws Exception {

		StudyConsentDto studyConsentDto = new StudyConsentDto();
		UserStudiesDto userStudies = new UserStudiesDto();

		when(studyDao.fetchStudyConsentDetails(anyString(), anyString(), anyString())).thenReturn(studyConsentDto);
		when(studyDao.fetchUserStudiesDetails(anyString(), anyString(), anyString())).thenReturn(userStudies);
		when(activityDao.validateActivityId(anyString())).thenReturn(true);

		assertThat(studyDao.fetchStudyConsentDetails("1", "1", AppConstants.FIND_BY_TYPE_USERID_STUDYID)).isEqualTo(studyConsentDto);
		assertThat(studyDao.fetchUserStudiesDetails("1", "1", AppConstants.FIND_BY_TYPE_USERID_STUDYID)).isEqualTo(userStudies);

		assertThat(activityService.validateUserEnrolledToStudy("1", "1")).isEqualTo(true);
	}

	@Test
	public void testGetActivitiesWhenActivitiesAreNotFound() throws Exception {

		UserDto user = new UserDto();
		UserStudiesDto userStudies = new UserStudiesDto();
		List<ActivitiesDto> activitiesList = null;

		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(user);
		when(studyDao.fetchUserStudiesDetails(anyString(), anyString(), anyString())).thenReturn(userStudies);
		when(activityDao.fetchActivitiesDetailsList(anyString(), anyString())).thenReturn(activitiesList);

		assertThat(userDao.fetchUserDetails("1", AppConstants.FIND_BY_TYPE_USERID)).isEqualTo(user);
		assertThat(studyDao.fetchUserStudiesDetails("1", "1", AppConstants.FIND_BY_TYPE_USERID_STUDYID)).isEqualTo(userStudies);
		assertThat(activityDao.fetchActivitiesDetailsList("1", AppConstants.FIND_BY_TYPE_STUDYID)).isNullOrEmpty();

		activityService.getActivities("1", "1");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetActivitiesWhenActivitiesAreFound() throws Exception {
		UserDto user = new UserDto()
				.setUserId(1)
				.setTimeZone(AppConstants.SERVER_TIMEZONE);
		UserStudiesDto userStudies = new UserStudiesDto()
				.setCreatedOn("2017-10-20 08:03:42");

		EnrollmentTokensDto enrollmentTokenDto = new EnrollmentTokensDto()
				.setGroupId("101")
				.setEnrollmentId("1")
				.setIsActive(true);
		
		List<ActivitiesDto> activitiesList = new ArrayList<>();
		activitiesList.add(new ActivitiesDto()
				.setActivityId(1)
				.setQualtricsId("SV_6rFC3Iue5lrash7"));
		
		List<ActivityGroupDto> activityGroupList = new ArrayList<>();
		activityGroupList.add(new ActivityGroupDto()
				.setGroupId(101)
				.setConditionId(1));
		activityGroupList.add(new ActivityGroupDto()
				.setGroupId(101)
				.setConditionId(2));
		activityGroupList.add(new ActivityGroupDto()
				.setGroupId(101)
				.setConditionId(3));
		activityGroupList.add(new ActivityGroupDto()
				.setGroupId(101)
				.setConditionId(4));
		
		Map<Integer, List<ActivitiesDto>> activitiesMap = new HashMap<>();
		activitiesMap.put(1, activitiesList);
		
		List<ActivityConditionDto> activityConditionDtoList = new ArrayList<>();
		activityConditionDtoList.add(new ActivityConditionDto()
				.setActivityId(1)
				.setStatus(false)
				.setConditionId(1)
				.setActivityType(AppConstants.ACTIVITY_TYPE_SURVEY_WEB)
				.setActivitySubType(AppConstants.ACTIVITY_SUB_TYPE_TRIGGERED));
		activityConditionDtoList.add(new ActivityConditionDto()
				.setActivityId(1)
				.setStatus(true)
				.setConditionId(2)
				.setActivityType(AppConstants.ACTIVITY_TYPE_ARITHMETIC_WEB)
				.setActivitySubType(AppConstants.ACTIVITY_SUB_TYPE_TRIGGERED));
		activityConditionDtoList.add(new ActivityConditionDto()
				.setActivityId(1)
				.setStatus(true)
				.setConditionId(3)
				.setActivityType(AppConstants.ACTIVITY_TYPE_STROOP_WEB)
				.setActivitySubType(AppConstants.ACTIVITY_SUB_TYPE_TRIGGERED));
		activityConditionDtoList.add(new ActivityConditionDto()
				.setActivityId(1)
				.setStatus(true)
				.setConditionId(4)
				.setActivityType(AppConstants.ACTIVITY_TYPE_STROOP_WEB)
				.setActivitySubType(AppConstants.ACTIVITY_SUB_TYPE_SCHEDULED));
		List<Object> activityIds = Arrays.asList((Object) new String("1").split(","));
		List<Object> activityConditionIds = Arrays.asList((Object) new String("1,2,3,4").split(","));

		List<ThresholdConditionsDto> thresholdConditionsList = new ArrayList<>();
		thresholdConditionsList.add(new ThresholdConditionsDto()
				.setThresholdRange(AppConstants.THRESHOLD_RANGE_BTW)
				.setApplicable(true)
				.setConditionId(1)
				.setThresholdId(1));
		thresholdConditionsList.add(new ThresholdConditionsDto()
				.setThresholdRange(AppConstants.THRESHOLD_RANGE_GT)
				.setApplicable(true)
				.setConditionId(2)
				.setThresholdId(2));
		thresholdConditionsList.add(new ThresholdConditionsDto()
				.setThresholdRange(AppConstants.THRESHOLD_RANGE_LT)
				.setApplicable(true)
				.setConditionId(3)
				.setThresholdId(3));

		TemporalConditionDto temporalCondition = new TemporalConditionDto()
				.setAnchorDays(0)
				.setStartDate("2017-10-20")
				.setEndDate("2017-10-27")
				.setRepetitionFrequency(AppConstants.ACTIVITY_FREQUENCY_WEEKLY)
				.setRepetitionFrequencyDays("monday,wednesday,thursday,friday");

		UserActivitiesDto userActivitiesDto = new UserActivitiesDto()
				.setCurrentRunDate(AppUtil.getCurrentDate())
				.setActivityStatus(AppConstants.RUN_STATE_START)
				.setActivityRunId(0);
		temporalCondition.setEndDate(AppUtil.addDays(AppUtil.getCurrentDate(), -1, AppConstants.SDF_DATE_FORMAT));
		ActivityListBean activityListBean = new ActivityListBean();

		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(user);
		when(studyDao.fetchUserStudiesDetails(anyString(), anyString(), anyString())).thenReturn(userStudies);
		when(activityDao.fetchActivitiesDetailsList(anyString(), anyString())).thenReturn(activitiesList);
		when(activityDao.fetchActivityConditionDetailsList(anyList(), anyString())).thenReturn(activityConditionDtoList);
		when(studyDao.fetchEnrollmentTokenDetails(anyString(), anyString())).thenReturn(enrollmentTokenDto);
		
		when(studyDao.fetchActivityGroupsList(anyString(), anyString())).thenReturn(activityGroupList);
		activityGroupList = studyDao.fetchActivityGroupsList(enrollmentTokenDto.getGroupId(), AppConstants.FIND_BY_TYPE_GROUPID);
		
		when(activityDao.fetchThresholdConditionDetailsList(anyObject(), anyString())).thenReturn(thresholdConditionsList);
		when(activityDao.fetchTemporalConditionDetails(anyString(), anyString())).thenReturn(temporalCondition);

		doReturn(userActivitiesDto).when(activityServiceImplSpy).getUserActivitiesDetails(anyObject(), anyObject(), anyObject(), anyObject());
		doReturn(activityListBean).when(activityServiceImplSpy).getActivityListMetaData(anyObject());
		doReturn(5).when(activityServiceImplSpy).calculateTotalRunCount(anyObject(), anyObject(), anyObject());
		doReturn(new HashMap<>()).when(activityServiceImplSpy).calculateCurrentRunId(anyObject(), anyObject());

		assertThat(userDao.fetchUserDetails("1", AppConstants.FIND_BY_TYPE_USERID)).isEqualTo(user);
		assertThat(studyDao.fetchUserStudiesDetails("1", "1", AppConstants.FIND_BY_TYPE_USERID_STUDYID)).isEqualTo(userStudies);
		assertThat(activityDao.fetchActivitiesDetailsList("1", AppConstants.FIND_BY_TYPE_STUDYID)).isNotNull().isNotEmpty().isEqualTo(activitiesList);
		assertThat(activityDao.fetchActivityConditionDetailsList(activityIds, AppConstants.FIND_BY_TYPE_ACTIVITY_IDS)).isEqualTo(activityConditionDtoList);
		assertThat(studyDao.fetchEnrollmentTokenDetails("1", AppConstants.FIND_BY_TYPE_GROUPID)).isEqualTo(enrollmentTokenDto);
		assertThat(activityDao.fetchThresholdConditionDetailsList(activityConditionIds, AppConstants.FIND_BY_TYPE_CONDITIONID)).isEqualTo(thresholdConditionsList);
		assertThat(activityDao.fetchTemporalConditionDetails("1", AppConstants.FIND_BY_TYPE_CONDITIONID)).isNotNull().isEqualTo(temporalCondition);

		assertThat(activityService.getActivities("1", "1")).isInstanceOfAny(ActivityListResponse.class);
	}

	@Test
	public void testWhenUserActivitiesDetails() throws Exception{

		UserActivitiesDto userActivities = new UserActivitiesDto()
				.setCurrentRunDate(AppUtil.getCurrentDate())
				.setActivityRunId(1)
				.setCompletedCount(0)
				.setActivityStatus(AppConstants.RUN_STATE_START);
		UserDto user = new UserDto()
				.setUserId(1)
				.setTimeZone(AppConstants.SERVER_TIMEZONE);
		ActivityConditionDto activityCondition = new ActivityConditionDto()
				.setConditionId(1)
				.setTotalParticipationTarget(20);
		UserStudiesDto userStudies = new UserStudiesDto()
				.setCreatedOn("2017-10-20 08:03:42");
		TemporalConditionDto temporalCondition = new TemporalConditionDto()
				.setStartDate("2017-10-23")
				.setEndDate("2017-10-27")
				.setAnchorDays(0)
				.setRepetitionFrequency(AppConstants.ACTIVITY_FREQUENCY_DAILY)
				.setRepetitionFrequencyDays(AppConstants.REPEAT_FREQUENCY_DAYS);

		//when user activity details found
		when(activityDao.fetchUserActivityDetails(anyString(), anyString(), anyString(), anyString())).thenReturn(userActivities);
		doReturn(userActivities).when(activityServiceImplSpy).updateActivityRunDetails(anyObject(), anyObject(), anyObject(), anyObject(), anyObject());

		activityServiceImpl.getUserActivitiesDetails(user, userStudies, activityCondition, temporalCondition);

		
		temporalCondition.setAnchorDays(0);
		activityCondition.setActivitySubType(AppConstants.ACTIVITY_SUB_TYPE_SCHEDULED);
		
		//when user activity details not found
		when(activityDao.fetchUserActivityDetails(anyString(), anyString(), anyString(), anyString())).thenReturn(null);
		doReturn(5).when(activityServiceImplSpy).calculateTotalRunCount(anyObject(), anyObject(), anyObject());
		doReturn(userActivities).when(activityServiceImplSpy).updateActivityRunDetails(anyObject(), anyObject(), anyObject(), anyObject(), anyObject());

		activityServiceImpl.getUserActivitiesDetails(user, userStudies, activityCondition, temporalCondition);
	}

	@Test
	public void testToCalculateTotalRunCount() throws Exception{

		int totalCount = 0;
		UserStudiesDto userStudies = new UserStudiesDto().setCreatedOn("2017-10-20 08:03:42");

		ActivityConditionDto activityCondition = new ActivityConditionDto()
				.setConditionId(1)
				.setTotalParticipationTarget(20)
				.setActivitySubType(AppConstants.ACTIVITY_SUB_TYPE_SCHEDULED);
		
		TemporalConditionDto temporalCondition = new TemporalConditionDto()
				.setAnchorDays(0)
				.setStartDate("2017-10-20")
				.setEndDate("2017-10-27")
				.setRepetitionFrequency(AppConstants.ACTIVITY_FREQUENCY_WEEKLY)
				.setRepetitionFrequencyDays("monday,wednesday,thursday,friday");

		totalCount = activityServiceImpl.calculateTotalRunCount(temporalCondition, userStudies, activityCondition);
		assertThat(totalCount).isEqualTo(5);

		temporalCondition.setRepetitionFrequency("")
		.setRepetitionFrequencyDays("monday,tuesday,wednesday,thursday,friday");

		totalCount = activityServiceImpl.calculateTotalRunCount(temporalCondition, userStudies, activityCondition);
		assertThat(totalCount).isEqualTo(6);
	}

	@Test
	public void testUpdateActivityRunDetails() throws Exception{

		String currentDate = AppUtil.getCurrentDate();
		UserDto user = new UserDto()
				.setUserId(1)
				.setTimeZone(AppConstants.SERVER_TIMEZONE);
		UserStudiesDto userStudies = new UserStudiesDto()
				.setCreatedOn("2017-10-23 10:00:00");
		ActivityConditionDto activityCondition = new ActivityConditionDto()
				.setConditionId(1)
				.setTotalParticipationTarget(20);
		TemporalConditionDto temporalCondition = new TemporalConditionDto()
				.setStartDate("2017-10-23")
				.setEndDate("2017-10-27")
				.setAnchorDays(0)
				.setRepetitionFrequency(AppConstants.ACTIVITY_FREQUENCY_DAILY)
				.setRepetitionFrequencyDays(AppConstants.REPEAT_FREQUENCY_DAYS);

		UserActivitiesDto userActivities = new UserActivitiesDto()
				.setCurrentRunDate(null)
				.setActivityRunId(1)
				.setCompletedCount(0)
				.setActivityStatus(AppConstants.RUN_STATE_START);


		//when completed count is less than total target participation count
		userActivities.setCompletedCount(10)
		.setCurrentRunDate(currentDate);
		activityCondition.setTotalParticipationTarget(5);
		activityServiceImpl.updateActivityRunDetails(user, userStudies, activityCondition, userActivities, temporalCondition);

		//when activity run id is not greater than 0
		userActivities.setCompletedCount(0)
		.setCurrentRunDate(currentDate)
		.setActivityRunId(0);
		activityCondition.setTotalParticipationTarget(20);
		activityServiceImpl.updateActivityRunDetails(user, userStudies, activityCondition, userActivities, temporalCondition);

		//When Current Run Date Is Null Or Equal To CurrentDate
		userActivities.setCompletedCount(0)
		.setCurrentRunDate(null)
		.setActivityRunId(1);
		doReturn(new HashMap<>()).when(activityServiceImplSpy).calculateCurrentRunId(anyObject(), anyObject());
		when(activityDao.fetchUserActivityRunsCount(anyString(), anyString(), anyString(), anyString())).thenReturn(1);

		activityServiceImpl.updateActivityRunDetails(user, userStudies, activityCondition, userActivities, temporalCondition);

		//When Current Run Date Is Not Null
		userActivities.setCurrentRunDate(currentDate);
		temporalCondition.setEndDate(AppUtil.addDays(currentDate, -1, AppConstants.SDF_DATE_FORMAT));

		when(activityDao.fetchUserActivityRunsCount(anyString(), anyString(), anyString(), anyString())).thenReturn(0);
		when(activityDao.saveOrUpdateUserActivities(anyObject(), anyString())).thenReturn(userActivities);

		activityServiceImpl.updateActivityRunDetails(user, userStudies, activityCondition, userActivities, temporalCondition);

		userActivities.setActivityStatus(AppConstants.RUN_STATE_COMPLETED);
		temporalCondition.setEndDate(AppUtil.addDays(currentDate, 3, AppConstants.SDF_DATE_FORMAT));

		when(activityDao.fetchUserActivityRunsCount(anyString(), anyString(), anyString(), anyString())).thenReturn(0);
		when(activityDao.saveOrUpdateUserActivities(anyObject(), anyString())).thenReturn(userActivities);

		activityServiceImpl.updateActivityRunDetails(user, userStudies, activityCondition, userActivities, temporalCondition);

		userActivities
		.setCurrentRunDate(currentDate)
		.setActivityStatus(AppConstants.RUN_STATE_START)
		.setActivityRunId(0);
		temporalCondition.setEndDate(AppUtil.addDays(currentDate, -1, AppConstants.SDF_DATE_FORMAT));

		when(activityDao.fetchUserActivityRunsCount(anyString(), anyString(), anyString(), anyString())).thenReturn(0);
		when(activityDao.saveOrUpdateUserActivities(anyObject(), anyString())).thenReturn(userActivities);

		activityServiceImpl.updateActivityRunDetails(user, userStudies, activityCondition, userActivities, temporalCondition);

		userActivities
		.setCurrentRunDate("2017-10-20")
		.setActivityStatus(AppConstants.RUN_STATE_START)
		.setActivityRunId(1);

		temporalCondition.setStartDate("2017-10-19")
		.setEndDate("2017-10-27")
		.setAnchorDays(0)
		.setRepetitionFrequency(AppConstants.ACTIVITY_FREQUENCY_WEEKLY)
		.setRepetitionFrequencyDays("thursday,friday");


		doReturn(new HashMap<>()).when(activityServiceImplSpy).calculateCurrentRunId(anyObject(), anyObject());
		when(activityDao.fetchUserActivityRunsCount(anyString(), anyString(), anyString(), anyString())).thenReturn(0);

		activityServiceImpl.updateActivityRunDetails(user, userStudies, activityCondition, userActivities, temporalCondition);
	}

	@Test
	public void testToCalculateCurrentRunId() throws Exception{

		UserStudiesDto userStudies = new UserStudiesDto().setCreatedOn("2017-10-20 08:03:42");

		TemporalConditionDto temporalCondition = new TemporalConditionDto()
				.setAnchorDays(0)
				.setStartDate("2017-10-20")
				.setEndDate("2017-10-27")
				.setRepetitionFrequency(AppConstants.ACTIVITY_FREQUENCY_WEEKLY)
				.setRepetitionFrequencyDays("monday,wednesday,thursday,friday");

		Map<String, Object> runMap = new HashMap<>();
		runMap.put("runId", 1);
		runMap.put("runDate", "2017-10-23");

		runMap = activityServiceImpl.calculateCurrentRunId(temporalCondition, userStudies);

		temporalCondition.setRepetitionFrequency("")
		.setRepetitionFrequencyDays("monday,tuesday,wednesday,thursday,friday");

		runMap = activityServiceImpl.calculateCurrentRunId(temporalCondition, userStudies);
	}

	@Test
	public void testGetActivityListMetaDataForAllActivityTypes() throws Exception{

		ActivityListBean activityListBean = new ActivityListBean();

		ActivitiesDto activityDto = new ActivitiesDto();
		ActivityConditionDto actConditionDto = new ActivityConditionDto()
				.setConditionId(1)
				.setActivityConditionName("Sample Survey")
				.setActivityConditionId("AC4")
				.setActivitySubType(AppConstants.ACTIVITY_SUB_TYPE_SCHEDULED);
		TemporalConditionDto temporalConditionDto = new TemporalConditionDto()
				.setAnchorDays(0)
				.setStartDate("2017-10-20")
				.setEndDate("2017-10-27")
				.setRepetitionFrequency(AppConstants.ACTIVITY_FREQUENCY_WEEKLY)
				.setRepetitionFrequencyDays("monday,wednesday,thursday,friday");
		UserActivitiesDto userActivitiesDto = new UserActivitiesDto();
		UserDto user = new UserDto()
				.setUserId(1)
				.setTimeZone(AppConstants.SERVER_TIMEZONE);

		UserStudiesDto userStudies = new UserStudiesDto();
		
		List<ThresholdConditionsDto> thresholdConditionsList = new ArrayList<>();
		thresholdConditionsList.add(new ThresholdConditionsDto()
				.setThresholdRange(AppConstants.THRESHOLD_RANGE_BTW)
				.setApplicable(true)
				.setConditionId(1)
				.setThresholdId(1)
				.setValue(null)
				.setMaxValue(null));
		thresholdConditionsList.add(new ThresholdConditionsDto()
				.setThresholdRange(AppConstants.THRESHOLD_RANGE_BTW)
				.setApplicable(true)
				.setConditionId(1)
				.setThresholdId(1)
				.setValue("10")
				.setMaxValue("10"));
		thresholdConditionsList.add(new ThresholdConditionsDto()
				.setThresholdRange(AppConstants.THRESHOLD_RANGE_GT)
				.setApplicable(false)
				.setConditionId(1)
				.setThresholdId(2)
				.setValue(null));
		thresholdConditionsList.add(new ThresholdConditionsDto()
				.setThresholdRange(AppConstants.THRESHOLD_RANGE_LT)
				.setApplicable(true)
				.setConditionId(1)
				.setThresholdId(3));
		thresholdConditionsList.add(new ThresholdConditionsDto()
				.setThresholdRange(AppConstants.THRESHOLD_RANGE_LT)
				.setApplicable(true)
				.setConditionId(1)
				.setThresholdId(4)
				.setValue("10"));
		thresholdConditionsList.add(new ThresholdConditionsDto()
				.setThresholdRange(AppConstants.THRESHOLD_RANGE_LT)
				.setApplicable(false)
				.setConditionId(6)
				.setThresholdId(5));
		thresholdConditionsList.add(new ThresholdConditionsDto()
				.setThresholdRange(AppConstants.THRESHOLD_RANGE_LT)
				.setApplicable(true)
				.setConditionId(1)
				.setThresholdId(6));
		thresholdConditionsList.add(new ThresholdConditionsDto()
				.setThresholdRange(AppConstants.THRESHOLD_RANGE_LT)
				.setApplicable(true)
				.setConditionId(1)
				.setThresholdId(7));
		thresholdConditionsList.add(new ThresholdConditionsDto()
				.setThresholdRange(AppConstants.THRESHOLD_RANGE_LT)
				.setApplicable(true)
				.setConditionId(1)
				.setThresholdId(8));
		thresholdConditionsList.add(new ThresholdConditionsDto()
				.setThresholdRange(AppConstants.THRESHOLD_RANGE_LT)
				.setApplicable(true)
				.setConditionId(1)
				.setThresholdId(9));

		Map<String, Object> activityListMap = new HashMap<>();
		activityListMap.put("activityDetails", activityDto);
		activityListMap.put("activityConditionDetails", actConditionDto);
		activityListMap.put("temporalConditionDetails", temporalConditionDto);
		activityListMap.put("thresholdConditionDetails", thresholdConditionsList);
		activityListMap.put("userActivityDetails", userActivitiesDto);
		activityListMap.put("user", user);
		activityListMap.put("userStudies", userStudies);
		
		
		//for arithmetic
		actConditionDto = new ActivityConditionDto()
				.setConditionId(1)
				.setActivityType(AppConstants.ACTIVITY_TYPE_ARITHMETIC_WEB);
		activityListBean = activityServiceImpl.getActivityListMetaData(activityListMap);
		assertThat(activityListBean).isNotNull();

		//for stroop
		actConditionDto = new ActivityConditionDto()
				.setConditionId(1)
				.setActivityType(AppConstants.ACTIVITY_TYPE_STROOP_WEB);
		activityListMap.remove("activityConditionDetails");
		activityListMap.put("activityConditionDetails", actConditionDto);

		activityListBean = activityServiceImpl.getActivityListMetaData(activityListMap);
		assertThat(activityListBean).isNotNull();

		//for survey
		actConditionDto = new ActivityConditionDto()
				.setConditionId(1)
				.setActivityType(AppConstants.ACTIVITY_TYPE_SURVEY_WEB);
		activityListMap.remove("activityConditionDetails");
		activityListMap.put("activityConditionDetails", actConditionDto);

		activityListBean = activityServiceImpl.getActivityListMetaData(activityListMap);
		assertThat(activityListBean).isNotNull();
		
		
		
		
		userActivitiesDto.setLastCompletedDate("2017-11-12T06:00:00.000+0530");
		actConditionDto.setActivityConditionName("")
		.setActivityConditionId("")
		.setActivitySubType("");
		temporalConditionDto = new TemporalConditionDto()
				.setAnchorDays(0)
				.setStartDate("")
				.setEndDate("")
				.setRepetitionFrequency("");
		
		
		activityListMap.remove("userActivityDetails");
		activityListMap.remove("activityConditionDetails");
		activityListMap.remove("temporalConditionDetails");
		
		activityListMap.put("userActivityDetails", userActivitiesDto);
		activityListMap.put("activityConditionDetails", actConditionDto);
		activityListMap.put("temporalConditionDetails", temporalConditionDto);
		actConditionDto = new ActivityConditionDto()
				.setConditionId(1)
				.setActivityType(AppConstants.ACTIVITY_TYPE_SURVEY_WEB);
		activityListMap.remove("activityConditionDetails");
		activityListMap.put("activityConditionDetails", actConditionDto);

		activityListBean = activityServiceImpl.getActivityListMetaData(activityListMap);
		assertThat(activityListBean).isNotNull();
		
		activityListMap = new HashMap<>();
		activityListMap.put("activityDetails", null);
		activityListMap.put("activityConditionDetails", null);
		activityListMap.put("temporalConditionDetails", null);
		activityListMap.put("thresholdConditionDetails", new ArrayList<>());
		activityListMap.put("userActivityDetails", null);
		activityListMap.put("user", user);
		activityListMap.put("userStudies", null);
		activityListBean = activityServiceImpl.getActivityListMetaData(activityListMap);
		
		activityListMap = new HashMap<>();
		activityListMap.put("activityDetails", null);
		activityListMap.put("activityConditionDetails", null);
		activityListMap.put("temporalConditionDetails", null);
		activityListMap.put("thresholdConditionDetails", null);
		activityListMap.put("userActivityDetails", null);
		activityListMap.put("user", user);
		activityListMap.put("userStudies", null);
		activityListBean = activityServiceImpl.getActivityListMetaData(activityListMap);
		
		activityListMap = new HashMap<>();
		activityListMap.put("activityDetails", null);
		activityListMap.put("activityConditionDetails", null);
		activityListMap.put("temporalConditionDetails", null);
		activityListMap.put("thresholdConditionDetails", null);
		activityListMap.put("userActivityDetails", null);
		activityListMap.put("user", user);
		activityListMap.put("userStudies", null);
		activityListBean = activityServiceImpl.getActivityListMetaData(activityListMap);

	}
	
	@Test
	public void testGetAnchorDateTimeForActivity() throws Exception{
		
		TemporalConditionDto temporalConditionDto = new TemporalConditionDto()
				.setAnchorDays(5)
				.setStartDate("2017-11-09")
				.setEndDate("2017-12-20");
		UserStudiesDto userStudiesDto = new UserStudiesDto()
				.setCreatedOn("2017-10-24 08:21:20");
		activityServiceImpl.getAnchorDateTimeForActivity(temporalConditionDto, userStudiesDto);
		
		temporalConditionDto.setAnchorDays(0);
		activityServiceImpl.getAnchorDateTimeForActivity(temporalConditionDto, userStudiesDto);
	}
	
	//*************************** updateActivityState ***************************
	@Test
	public void test_updateActivityState_when_user_details_not_present() throws Exception {
		
		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(null);
		assertThat(activityService.updateActivityState("1", "1", "ACT1", "2018-01-11 02:40:00", "1", AppConstants.RUN_STATE_START, 100L).getError().getCode()).isEqualTo(ErrorCode.EC_61.code());
	}
	
	@Test
	public void test_updateActivityState_when_user_study_details_not_present() throws Exception {
		
		UserDto user = new UserDto();
		
		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(user);
		when(studyDao.fetchUserStudiesDetails(anyString(), anyString(), anyString())).thenReturn(null);
		
		assertThat(activityService.updateActivityState("1", "1", "ACT1", "2018-01-11 02:40:00", "1", AppConstants.RUN_STATE_START, 100L).getError().getCode()).isEqualTo(ErrorCode.EC_93.code());
	}
	
	@Test
	public void test_updateActivityState_when_activity_condition_details_not_present() throws Exception {
		
		UserDto user = new UserDto();
		UserStudiesDto userStudy = new UserStudiesDto();
		
		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(user);
		when(studyDao.fetchUserStudiesDetails(anyString(), anyString(), anyString())).thenReturn(userStudy);
		when(activityDao.fetchActivityConditionDetails(anyString(), anyString())).thenReturn(null);
		
		assertThat(activityService.updateActivityState("1", "1", "ACT1", "2018-01-11 02:40:00", "1", AppConstants.RUN_STATE_START, 100L).getError().getCode()).isEqualTo(ErrorCode.EC_45.code());
	}
	
	@Test
	public void test_updateActivityState_when_user_activity_details_not_present() throws Exception {
		
		UserDto user = new UserDto();
		UserStudiesDto userStudy = new UserStudiesDto()
				.setUserStudiesId(1);
		ActivityConditionDto activityCondition = new ActivityConditionDto()
				.setConditionId(1);
		
		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(user);
		when(studyDao.fetchUserStudiesDetails(anyString(), anyString(), anyString())).thenReturn(userStudy);
		when(activityDao.fetchActivityConditionDetails(anyString(), anyString())).thenReturn(activityCondition);
		when(activityDao.fetchUserActivityDetails(anyString(), anyString(), anyString(), anyString())).thenReturn(null);
		
		assertThat(activityService.updateActivityState("1", "1", "ACT1", "2018-01-11 02:40:00", "1", AppConstants.RUN_STATE_START, 100L).getError().getCode()).isEqualTo(ErrorCode.EC_45.code());
	}
	
	@Test
	public void test_updateActivityState_when_run_state_is_not_valid() throws Exception {
		
		UserDto user = new UserDto();
		UserStudiesDto userStudy = new UserStudiesDto()
				.setUserStudiesId(1);
		ActivityConditionDto activityCondition = new ActivityConditionDto()
				.setConditionId(1);
		UserActivitiesDto userActivity = new UserActivitiesDto();
		
		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(user);
		when(studyDao.fetchUserStudiesDetails(anyString(), anyString(), anyString())).thenReturn(userStudy);
		when(activityDao.fetchActivityConditionDetails(anyString(), anyString())).thenReturn(activityCondition);
		when(activityDao.fetchUserActivityDetails(anyString(), anyString(), anyString(), anyString())).thenReturn(userActivity);
		
		assertThat(activityService.updateActivityState("1", "1", "ACT1", "2018-01-11 02:40:00", "1", AppConstants.RUN_STATE_START, 100L).getError().getCode()).isEqualTo(ErrorCode.EC_105.code());
	}
	
	@Test
	public void test_updateActivityState_when_run_state_is_resume() throws Exception {
		
		UserDto user = new UserDto();
		UserStudiesDto userStudy = new UserStudiesDto()
				.setUserStudiesId(1);
		ActivityConditionDto activityCondition = new ActivityConditionDto()
				.setConditionId(1);
		UserActivitiesDto userActivity = new UserActivitiesDto();
		
		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(user);
		when(studyDao.fetchUserStudiesDetails(anyString(), anyString(), anyString())).thenReturn(userStudy);
		when(activityDao.fetchActivityConditionDetails(anyString(), anyString())).thenReturn(activityCondition);
		when(activityDao.fetchUserActivityDetails(anyString(), anyString(), anyString(), anyString())).thenReturn(userActivity);
		when(activityDao.saveOrUpdateUserActivities(anyObject(), anyString())).thenReturn(userActivity);
		
		assertThat(activityService.updateActivityState("1", "1", "ACT1", "2018-01-11 02:40:00", "1", AppConstants.RUN_STATE_RESUME, 100L).getError().getCode()).isEqualTo(ErrorCode.EC_200.code());
	}
	
	@Test
	public void test_updateActivityState_when_run_state_is_completed() throws Exception {
		
		UserDto user = new UserDto()
				.setTimeZone(AppConstants.SERVER_TIMEZONE)
				.setUserId(1);
		UserStudiesDto userStudy = new UserStudiesDto()
				.setUserStudiesId(1);
		ActivityConditionDto activityCondition = new ActivityConditionDto()
				.setConditionId(1)
				.setTotalParticipationTarget(20);
		UserActivitiesDto userActivity = new UserActivitiesDto()
				.setUserStudiesId(1)
				.setCompletedCount(0)
				.setActivityRunId(1)
				.setMissedCount(0)
				.setActivityStatus(AppConstants.RUN_STATE_COMPLETED)
				.setTotalCount(1);
		
		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(user);
		when(studyDao.fetchUserStudiesDetails(anyString(), anyString(), anyString())).thenReturn(userStudy);
		when(activityDao.fetchActivityConditionDetails(anyString(), anyString())).thenReturn(activityCondition);
		when(activityDao.fetchUserActivityDetails(anyString(), anyString(), anyString(), anyString())).thenReturn(userActivity);
		when(activityDao.saveOrUpdateUserActivities(anyObject(), anyString())).thenReturn(userActivity);
		
		assertThat(activityService.updateActivityState("1", "1", "ACT1", "2018-01-11 02:40:00", "1", AppConstants.RUN_STATE_COMPLETED, 100L).getError().getCode()).isEqualTo(ErrorCode.EC_404.code());
	}
	
	//*************************** rewards ***************************
	@Test
	public void test_rewards_when_user_or_userStudiesDetails_are_not_present() throws Exception {

		UserDto user = new UserDto();
		
		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(null);
		assertThat(activityService.rewards("1").getError().getCode()).isEqualTo(ErrorCode.EC_61.code());
		
		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(user);
		when(studyDao.fetchUserStudiesDetails(anyString(), anyString(), anyString())).thenReturn(null);
		assertThat(activityService.rewards("1").getError().getCode()).isEqualTo(ErrorCode.EC_61.code());
		
		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(null);
		when(studyDao.fetchUserStudiesDetails(anyString(), anyString(), anyString())).thenReturn(null);
		assertThat(activityService.rewards("1").getError().getCode()).isEqualTo(ErrorCode.EC_61.code());
	}
	
	@Test
	public void test_rewards_when_user_or_userStudiesDetails_are_present() throws Exception {

		UserDto user = new UserDto()
				.setPointsEarned(150L);
		UserStudiesDto userStudy = new UserStudiesDto();
		List<RewardLevelsDto> rewardsList = new ArrayList<>();
		
		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(user);
		when(studyDao.fetchUserStudiesDetails(anyString(), anyString(), anyString())).thenReturn(userStudy);
		when(activityDao.fetchRewardLevelsList()).thenReturn(rewardsList);
		assertThat(activityService.rewards("1").getError().getCode()).isNotEqualTo(ErrorCode.EC_200.code());
		
		
		rewardsList.add(new RewardLevelsDto().setLevel(1).setPoints(100).setRewardLevelId(1));
		rewardsList.add(new RewardLevelsDto().setLevel(2).setPoints(200).setRewardLevelId(2));
		rewardsList.add(new RewardLevelsDto().setLevel(3).setPoints(300).setRewardLevelId(3));
		rewardsList.add(new RewardLevelsDto().setLevel(4).setPoints(400).setRewardLevelId(4));
		
		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(user);
		when(studyDao.fetchUserStudiesDetails(anyString(), anyString(), anyString())).thenReturn(userStudy);
		when(activityDao.fetchRewardLevelsList()).thenReturn(rewardsList);
		assertThat(activityService.rewards("1").getError().getCode()).isEqualTo(ErrorCode.EC_200.code());
	}
}
