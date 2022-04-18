/**
 * 
 */
package com.gba.ws.service;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.gba.ws.model.GroupIdentifierDto;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gba.ws.bean.FitbitErrorResponse;
import com.gba.ws.bean.FitbitLass4uApiResponseBean;
import com.gba.ws.bean.PushNotificationBean;
import com.gba.ws.bean.ThresholdConditionDetailsBean;
import com.gba.ws.bean.fitbit.AppVersionInfoBean;
import com.gba.ws.bean.fitbit.DeviceVersion;
import com.gba.ws.bean.fitbit.HarvardIAQ;
import com.gba.ws.bean.fitbit.IntraHeartRateBean;
import com.gba.ws.bean.fitbit.Lass4UBean;
import com.gba.ws.bean.fitbit.RestingHeartRateBean;
import com.gba.ws.bean.fitbit.Sleep;
import com.gba.ws.bean.fitbit.SleepBean;
import com.gba.ws.bean.fitbit.StepsBean;
import com.gba.ws.dao.ActivityDao;
import com.gba.ws.dao.StudyDao;
import com.gba.ws.dao.UserDao;
import com.gba.ws.exception.CustomException;
import com.gba.ws.model.ActivitiesDto;
import com.gba.ws.model.ActivityConditionDto;
import com.gba.ws.model.ActivityGroupDto;
import com.gba.ws.model.AppVersionInfo;
import com.gba.ws.model.AuthInfoDto;
import com.gba.ws.model.EnrollmentTokensDto;
import com.gba.ws.model.FitbitLass4UDataDto;
import com.gba.ws.model.FitbitUserInfoDto;
import com.gba.ws.model.GroupUsersInfoDto;
import com.gba.ws.model.TemporalConditionDto;
import com.gba.ws.model.ThresholdConditionsDto;
import com.gba.ws.model.UserActivitiesDto;
import com.gba.ws.model.UserActivitiesRunsDto;
import com.gba.ws.model.UserDto;
import com.gba.ws.model.UserStudiesDto;
import com.gba.ws.util.AppConstants;
import com.gba.ws.util.AppEnums;
import com.gba.ws.util.AppUtil;
import com.gba.ws.util.HeaderRequestInterceptor;
import com.gba.ws.util.MailContent;
import com.gba.ws.util.Responsemodel;
import com.gba.ws.util.UnsafeOkHttpClient;
import com.google.gson.Gson;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

import static com.gba.ws.util.AppConstants.BUILDING_GROUP;
import static com.gba.ws.util.AppConstants.FIND_BY_TYPE_USERID;
import static com.gba.ws.util.AppConstants.INDIVIDUAL_GROUP;
import static com.gba.ws.util.AppConstants.SDF_DATE_TIME;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

/**
 * Implements {@link AppService} interface, provides the push notification
 * details for active triggered surveys.
 * 
 * @author Mohan <i>createdOn</i> Jan 11, 2018 12:00:59 PM
 */
@Service
public class AppServiceImpl {

	private static final Logger LOGGER = Logger.getLogger(AppServiceImpl.class);

	@Autowired
	private UserDao userDao;

	@Autowired
	private StudyDao studyDao;

	@Autowired
	private ActivityDao activityDao;

	@Autowired
	private RestTemplate restTemplate;

	public static final String BASE_URL = "https://pm25.lass-net.org/";
	private static Retrofit retrofit = null;

	/**
	 * Parameterized {@link AppServiceImpl} Constructor
	 *
	 * @param userDao      the {@link UserDao} details
	 * @param studyDao     the {@link StudyDao} details
	 * @param activityDao  the {@link ActivityDao} details
	 * @param restTemplate the {@link RestTemplate} details
	 * @author Mohan
	 */
	public AppServiceImpl(UserDao userDao, StudyDao studyDao, ActivityDao activityDao, RestTemplate restTemplate) {
		super();
		this.userDao = userDao;
		this.studyDao = studyDao;
		this.activityDao = activityDao;
		this.restTemplate = restTemplate;
	}

	//@Override
	@Async
	@Scheduled(cron = "0 0/15 * * * 1,2,3,4,5")
	public void sendPushNotifications() {
		LOGGER.info("INFO: AppServiceImpl - sendPushNotifications() :: starts");
		ExecutorService executorService = Executors.newFixedThreadPool(5);
		List<FitbitLass4uApiResponseBean> fitbitLass4uApiResponseList = new ArrayList<>();
		List<String> timeZoneList = new ArrayList<>();
		List<UserDto> userTimeZoneList = null;
		List<UserDto> activeUsersList = new ArrayList<>();
		try {

			//fetch active user (present in auth_info and user_studies=true)
			activeUsersList = userDao.findAllActiveLoggedInUsers();
			//
			timeZoneList = userDao.getAllTimeZoneList();
			//timeZoneList.add("Asia/Kolkata");
			if (!activeUsersList.isEmpty() && !timeZoneList.isEmpty()) {
				List<EnrollmentTokensDto> enrollmentTokensList = studyDao.fetchAllEnrollmentDetailsList();
				Map<String, List<EnrollmentTokensDto>> userEnrollmentMap = enrollmentTokensList.parallelStream()
						.collect(Collectors.groupingBy(EnrollmentTokensDto::getEnrollmentId));
				List<GroupUsersInfoDto> groupUsersInfoDto =  studyDao.fetchAllGroupUsersInfoDetails();
				Map<Integer, List<GroupUsersInfoDto>> groupUsersIdMap = groupUsersInfoDto.parallelStream()
						.collect(Collectors.groupingBy(GroupUsersInfoDto::getUserId));
				List<ActivitiesDto> activitiesList = activityDao.fetchAllActiveActivities();
				for (String timeZone : timeZoneList) {
					String currentDateTimeBasedOnZone = AppUtil.getCurrentDateTime(timeZone);
					//get active temporal_condition
					String currentDate = currentDateTimeBasedOnZone.split(" ")[0];

					List<TemporalConditionDto> tmpSchedulesList = activityDao.fetchTemporalActivityScheduleList(currentDateTimeBasedOnZone.split(" ")[0]);
					List<ActivityConditionDto> triggerActivityConditionList = activityDao.fetchActivityConditionDetailsList(
							AppConstants.ACTIVITY_SUB_TYPE_SCHEDULED, AppConstants.FIND_BY_TYPE_ACTIVITY_SUB_TYPE, timeZone);
					List<ActivityConditionDto> triggerActivityConditionListRuns = activityDao.fetchActivityConditionDetailsList(
							AppConstants.ACTIVITY_SUB_TYPE_TRIGGERED, AppConstants.FIND_BY_TYPE_ACTIVITY_SUB_TYPE, timeZone);
					List<Object> activityConditionIds = triggerActivityConditionListRuns.stream().map(ActivityConditionDto::getConditionId)
							.collect(Collectors.toList());
					List<ActivityGroupDto> activityGroupList = studyDao.fetchAllActivityGroupList(activityConditionIds,
							AppConstants.FIND_BY_TYPE_CONDITIONID);
					List<TemporalConditionDto> temporalConditionsList = activityDao.fetchTemporalConditionDetailsList(activityConditionIds,
							AppConstants.FIND_BY_TYPE_CONDITIONID);
					List<ThresholdConditionsDto> thresholdConditionsList = activityDao.fetchThresholdConditionDetailsList(activityConditionIds,
							AppConstants.FIND_BY_TYPE_CONDITIONID);

					System.out.println(timeZone);
					userTimeZoneList = new ArrayList<>();
					List<Object> userIdsList = new ArrayList<>();

					//matches timezone with active users and creates list of user for given timezone
					userTimeZoneList = activeUsersList.stream().filter(x -> timeZone.equals(x.getTimeZone()))
							.collect(Collectors.toList());
					//creates list of user_ids for given timezone
					userIdsList = userTimeZoneList.stream().map(UserDto::getUserId).collect(Collectors.toList());
					if(userIdsList.isEmpty())
						continue;
					executorService.submit(new AppServiceImplThread(timeZone, userIdsList,userDao, studyDao, activityDao,
							restTemplate,userTimeZoneList,tmpSchedulesList,triggerActivityConditionList,triggerActivityConditionListRuns,
							userEnrollmentMap,activityGroupList, groupUsersIdMap, temporalConditionsList,thresholdConditionsList,activitiesList,null));
				}
				executorService.shutdown();
				try {
					executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
				} catch (InterruptedException e) {
					System.out.println("catch : " + e.getMessage());
				}
			}

		} catch (Exception e) {
			LOGGER.error("ERROR: AppServiceImpl - sendPushNotifications()", e);
		}
		LOGGER.info("INFO: AppServiceImpl - sendPushNotifications() :: ends");
	}

	@Async
	@Scheduled(cron = "0 0/5 * * * 1,2,3,4,5")
	public void sendSurveyPushNotifications() {
		LOGGER.info("INFO: AppServiceImpl - sendSurveyPushNotifications() :: starts");
		ExecutorService executorService = Executors.newFixedThreadPool(1);
		List<String> timeZoneList = new ArrayList<>();
		List<UserDto> userTimeZoneList = null;
		List<UserDto> activeUsersList = new ArrayList<>();
		try {

			//fetch active user (present in auth_info and user_studies=true)
			activeUsersList = userDao.findAllActiveLoggedInUsers();
			//
			timeZoneList = userDao.getAllTimeZoneList();
			//timeZoneList.add("Asia/Kolkata");
			if (!activeUsersList.isEmpty() && !timeZoneList.isEmpty()) {
				List<EnrollmentTokensDto> enrollmentTokensList = studyDao.fetchAllEnrollmentDetailsList();
				Map<String, List<EnrollmentTokensDto>> userEnrollmentMap = enrollmentTokensList.parallelStream()
						.collect(Collectors.groupingBy(EnrollmentTokensDto::getEnrollmentId));
				List<GroupUsersInfoDto> groupUsersInfoDto =  studyDao.fetchAllGroupUsersInfoDetails();
				Map<Integer, List<GroupUsersInfoDto>> groupUsersIdMap = groupUsersInfoDto.parallelStream()
						.collect(Collectors.groupingBy(GroupUsersInfoDto::getUserId));
				List<ActivitiesDto> activitiesList = activityDao.fetchAllActiveActivities();
				for (String timeZone : timeZoneList) {
					String currentDateTimeBasedOnZone = AppUtil.getCurrentDateTime(timeZone);
					//get active temporal_condition
					String currentDate = currentDateTimeBasedOnZone.split(" ")[0];

					List<TemporalConditionDto> tmpSchedulesList = activityDao.fetchTemporalActivityScheduleList(currentDateTimeBasedOnZone.split(" ")[0]);
					List<ActivityConditionDto> triggerActivityConditionList = activityDao.fetchActivityConditionDetailsList(
							AppConstants.ACTIVITY_SUB_TYPE_SCHEDULED, AppConstants.FIND_BY_TYPE_ACTIVITY_SUB_TYPE, timeZone);
					List<ActivityConditionDto> triggerActivityConditionListRuns = activityDao.fetchActivityConditionDetailsList(
							AppConstants.ACTIVITY_SUB_TYPE_TRIGGERED, AppConstants.FIND_BY_TYPE_ACTIVITY_SUB_TYPE, timeZone);
					List<Object> activityConditionIds = triggerActivityConditionListRuns.stream().map(ActivityConditionDto::getConditionId)
							.collect(Collectors.toList());
					List<ActivityGroupDto> activityGroupList = studyDao.fetchAllActivityGroupList(activityConditionIds,
							AppConstants.FIND_BY_TYPE_CONDITIONID);
					List<TemporalConditionDto> temporalConditionsList = activityDao.fetchTemporalConditionDetailsList(activityConditionIds,
							AppConstants.FIND_BY_TYPE_CONDITIONID);
					List<ThresholdConditionsDto> thresholdConditionsList = activityDao.fetchThresholdConditionDetailsList(activityConditionIds,
							AppConstants.FIND_BY_TYPE_CONDITIONID);

					System.out.println(timeZone);
					List<Object> userIdsList = new ArrayList<>();

					//matches timezone with active users and creates list of user for given timezone
					userTimeZoneList = activeUsersList.stream().filter(x -> timeZone.equals(x.getTimeZone()))
							.collect(Collectors.toList());
					//creates list of user_ids for given timezone
					userIdsList = userTimeZoneList.stream().map(UserDto::getUserId).collect(Collectors.toList());
					if(userIdsList.isEmpty())
						continue;
					executorService.submit(new AppServiceImplThread(timeZone, userIdsList,userDao, studyDao, activityDao,
							restTemplate,userTimeZoneList,tmpSchedulesList,triggerActivityConditionList,triggerActivityConditionListRuns,
							userEnrollmentMap,activityGroupList, groupUsersIdMap, temporalConditionsList,thresholdConditionsList,activitiesList,"survey"));
				}
				executorService.shutdown();
				try {
					executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
				} catch (InterruptedException e) {
					System.out.println("catch : " + e.getMessage());
				}
			}

		} catch (Exception e) {
			LOGGER.error("ERROR: AppServiceImpl - sendSurveyPushNotifications()", e);
		}
		LOGGER.info("INFO: AppServiceImpl - sendSurveyPushNotifications() :: ends");
	}

	@Async
	@Scheduled(cron = "0 0/5 * * * 1,2,3,4,5")
	public void sendExpiryPushNotifications() {
		LOGGER.info("INFO: AppServiceImpl - sendExpiryPushNotifications() :: starts");
		List<UserDto> activeUsersList = new ArrayList<>();
		List<String> timeZoneList = new ArrayList<>();
		try {
			activeUsersList = userDao.findAllActiveLoggedInUsers();
			timeZoneList = userDao.getAllTimeZoneList();
			//timeZoneList.add("Asia/Kolkata");
			if (null != activeUsersList && !activeUsersList.isEmpty() && null != timeZoneList && !timeZoneList.isEmpty()) {
				for(String timeZone : timeZoneList) {
					LOGGER.info("timeZone: "+timeZone);
					StringBuilder users = null;
					List<PushNotificationBean> pushNotificationBeanList = null;
					List<PushNotificationBean> pushNotificationBeanListAll = null;
					//List<TemporalConditionDto> temporalConditionsList = new ArrayList<>();
					//Map<Integer, List<TemporalConditionDto>> temporalConditionMap = new HashMap<>();
					List<UserDto> userTimeZoneList = new ArrayList<>();
					//List<Object> activityConditionIds = new ArrayList<>();
					//List<ActivityConditionDto> activityConditionDtoList = new ArrayList<>();
					//Map<Integer, List<ActivityConditionDto>> activityConditionMap = new HashMap<>();
					AuthInfoDto authInfoDto = null;
					TemporalConditionDto temporalCondition = null;
					ActivityConditionDto activityConditionDto = null;
					userTimeZoneList = activeUsersList.stream().filter(x -> timeZone.equals(x.getTimeZone()))
							.collect(Collectors.toList());
					for (UserDto user : userTimeZoneList) {
						if(null == users) {
							users = new StringBuilder().append("'"+user.getUserId()+"'");
						}else {
							users = users.append(",").append("'"+user.getUserId()+"'");
						}
					}
					String currentDateTime = AppUtil.getCurrentDateTime(timeZone);
					String currentDate = AppUtil.getCurrentUserDate(timeZone);
					if(null != users) {
						pushNotificationBeanListAll = activityDao.getExpiryUserActivities(users.toString());
						if(null != pushNotificationBeanListAll && !pushNotificationBeanListAll.isEmpty()) {
						/*activityConditionIds = pushNotificationBeanListAll.stream().map(PushNotificationBean::getConditionId)
								.collect(Collectors.toList());*/
						/*temporalConditionsList = activityDao.fetchTemporalConditionDetailsList(activityConditionIds,
								AppConstants.FIND_BY_TYPE_CONDITIONID);
						if(null != temporalConditionsList && !temporalConditionsList.isEmpty()) {
							temporalConditionMap = temporalConditionsList.stream()
									.collect(Collectors.groupingBy(TemporalConditionDto::getConditionId));
						}*/
						/*activityConditionDtoList = activityDao
								.fetchActivityConditionDetailsList(activityConditionIds, AppConstants.FIND_BY_TYPE_CONDITIONIDS);
						if(null != activityConditionDtoList && !activityConditionDtoList.isEmpty()) {

							activityConditionMap = activityConditionDtoList.stream()
									.collect(Collectors.groupingBy(ActivityConditionDto::getConditionId));
						}*/
							pushNotificationBeanList = new ArrayList<>();
							for (PushNotificationBean pushNotificationBean : pushNotificationBeanListAll) {
								authInfoDto = userDao.fetchAuthInfoDetails(pushNotificationBean.getUserId(), FIND_BY_TYPE_USERID);
								temporalCondition = activityDao.
										fetchTemporalConditionDetails(pushNotificationBean.getConditionId(),AppConstants.FIND_BY_TYPE_CONDITIONID);
								activityConditionDto = activityDao
										.fetchActivityConditionDetails(pushNotificationBean.getConditionId(),AppConstants.FIND_BY_TYPE_CONDITIONID);
								if(null != temporalCondition && null != activityConditionDto && null != authInfoDto) {
									//System.out.println(String.valueOf((AppConstants.SDF_DATE_TIME.parse(currentDate + " " +temporalCondition.getEndTime())).getMinutes()-(AppConstants.SDF_DATE_TIME.parse(currentDateTime)).getMinutes()));

									//TemporalConditionDto temporalCondition = temporalConditionMap.get(pushNotificationBean.getConditionId()).get(0);
									boolean run_check = (SDF_DATE_TIME.parse(currentDate + " " +temporalCondition.getEndTime())).after(SDF_DATE_TIME.parse(AppUtil.addMinutes(currentDateTime,15)))? false : true;
									long timeDiffInMin = ((SDF_DATE_TIME.parse(currentDate + " " +temporalCondition.getEndTime())).getTime() - (SDF_DATE_TIME.parse(currentDateTime)).getTime())/(1000*60);
                                    boolean checkIntervalTime = timeDiffInMin < 15 && timeDiffInMin>0;// && (SDF_DATE_TIME.parse(currentDate + " " +temporalCondition.getEndTime())).getMinutes()-(SDF_DATE_TIME.parse(currentDateTime)).getMinutes()-1 >= 10;
									boolean start = SDF_DATE_TIME.parse(temporalCondition.getStartDate() + " " +temporalCondition.getStartTime()).before(SDF_DATE_TIME.parse(currentDateTime)) ? true : false;
									boolean end = SDF_DATE_TIME.parse(temporalCondition.getEndDate() + " " + temporalCondition.getEndTime()).after(SDF_DATE_TIME.parse(currentDateTime)) ? true : false;
									boolean inStartEqual = SDF_DATE_TIME.parse(currentDate + " " +temporalCondition.getStartTime()).equals(SDF_DATE_TIME.parse(currentDateTime)) ? true : false;
									UserActivitiesDto userActivity = activityDao.findByUserIdConditionId(activityConditionDto.getConditionId(),authInfoDto.getUserId(), temporalCondition.getStartDate() + " " +temporalCondition.getStartTime());
                                    LOGGER.info("temporalCondition startdatetime = "+temporalCondition.getStartDate()+":"+temporalCondition.getStartTime()+", currentDateTime="+currentDateTime+", conditionId="+temporalCondition.getConditionId()+", temporalConditionEndDatetime="+temporalCondition.getEndDate()+":"+temporalCondition.getEndTime()+",TemporalConditionId="+temporalCondition.getTemporalConditionId()+",authInfoDtoUserId="+authInfoDto.getUserId()+",pushNotificationBean.getUserId()="+pushNotificationBean.getUserId()+",timeDiffInMin="+timeDiffInMin);
									if((start || inStartEqual) && end && run_check && checkIntervalTime && userActivity!=null && !userActivity.getActivityStatus().equals(AppConstants.RUN_STATE_COMPLETED) && !pushNotificationBean.getActivityStatus().equals(AppConstants.RUN_STATE_COMPLETED)) {
										pushNotificationBean.setActivityId(String.valueOf(activityConditionDto.getActivityId()));
										pushNotificationBean.setDeviceToken(authInfoDto.getDeviceToken());
										pushNotificationBean.setDeviceType(authInfoDto.getDeviceType());
										pushNotificationBean.setActivityId(String.valueOf(activityConditionDto.getActivityConditionName()));
										//pushNotificationBean.setExpiryMinutes(String.valueOf((AppConstants.SDF_DATE_TIME.parse(currentDate + " " +temporalCondition.getEndTime())).getMinutes()-(AppConstants.SDF_DATE_TIME.parse(currentDateTime)).getMinutes()-1));
										pushNotificationBean.setExpiryMinutes(String.valueOf(timeDiffInMin));
                                        /*if(SDF_DATE_TIME.parse(currentDateTime).getMinutes() >
												SDF_DATE_TIME.parse(currentDate + " " +temporalCondition.getEndTime()).getMinutes()) {
											pushNotificationBean.setExpiryMinutes
													(String.valueOf((SDF_DATE_TIME.parse(currentDate + " " +temporalCondition.getEndTime())).getMinutes()-(SDF_DATE_TIME.parse(currentDateTime)).getMinutes()));

										}else {
											pushNotificationBean.setExpiryMinutes(String.valueOf((SDF_DATE_TIME.parse(currentDate + " " +temporalCondition.getEndTime())).getMinutes()-(SDF_DATE_TIME.parse(currentDateTime)).getMinutes()));

										}*/
										pushNotificationBeanList.add(pushNotificationBean);
										userActivity.setActivityStatus(AppConstants.RUN_STATE_COMPLETED);
										userActivity = activityDao.saveOrUpdateUserActivities(userActivity, AppConstants.DB_UPDATE);
										LOGGER.info("Final call :: UserActivityId = "+userActivity.getUserActivityId()+"temporalCondition startdatetime = "+temporalCondition.getStartDate()+":"+temporalCondition.getStartTime()+", currentDateTime="+currentDateTime+", conditionId="+temporalCondition.getConditionId()+", temporalConditionEndDatetime="+temporalCondition.getEndDate()+":"+temporalCondition.getEndTime()+",TemporalConditionId="+temporalCondition.getTemporalConditionId()+",authInfoDtoUserId="+authInfoDto.getUserId()+",pushNotificationBean.getUserId()="+pushNotificationBean.getUserId()+",timeDiffInMin="+timeDiffInMin);

									}
								}
							}
						}
						if(null != pushNotificationBeanList && !pushNotificationBeanList.isEmpty()) {
							for (PushNotificationBean pushNotificationBean : pushNotificationBeanList.stream().collect(Collectors.toMap(x -> x.getConditionId(),x->x, (x1, x2) -> x1)).values()) {
								JSONObject body = new JSONObject();
								JSONObject notification = new JSONObject();
								JSONObject data = new JSONObject();

								body.put(AppEnums.PN_PRIORITY.value(), AppEnums.PN_HIGH.value());
								body.put(AppEnums.PN_TO.value(), pushNotificationBean.getDeviceToken());

								data.put(AppEnums.PN_ACTIVITY_ID.value(), pushNotificationBean.getActivityId());
								data.put(AppEnums.PN_RUN_ID.value(), pushNotificationBean.getCurrentRunId());
								data.put(AppEnums.RP_STUDY_ID.value(), pushNotificationBean.getUserId());
								data.put(AppEnums.PN_N_TYPE.value(), AppEnums.PN_TRIGGER.value());

								if (AppConstants.PLATFORM_TYPE_ANDROID.equals(pushNotificationBean.getDeviceType())) {
									data = this.getExpiryNotificationBodyDetails(pushNotificationBean, data);
									body.put(AppEnums.PN_DATA.value(), data);
								} else {
									notification = this.getExpiryNotificationBodyDetails(pushNotificationBean, notification);
									notification.put(AppEnums.PN_CONTENT_AVAILABLE.value(), 1);
									body.put(AppEnums.PN_NOTIFICATION.value(), notification);
									body.put(AppEnums.PN_DATA.value(), data);
								}

								LOGGER.info("PUSH NOTIFICATION BEAN : " + pushNotificationBean.toString() + "\n BODY : "
										+ body.toString());

								this.callFCMPushNotificationAPI(body);
								//activityDao.saveOrUpdateUserActivityRunsDetail(pushNotificationBean.getUserActivityRunId());
								activityDao.saveOrUpdateUserActivityDetail(pushNotificationBean.getUserActivityRunId());
							}
						}
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: AppServiceImpl - sendExpiryPushNotifications()", e);
		}
		LOGGER.info("INFO: AppServiceImpl - sendExpiryPushNotifications() :: ends");
	}
	public void callFCMPushNotificationAPI(JSONObject body) {
		LOGGER.info("INFO: AppServiceImpl - callFCMPushNotificationAPI() :: starts");
		String firebaseResponse;
		try {

			HttpEntity<Object> entity = new HttpEntity<>(body.toString());
			ArrayList<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
			interceptors.add(new HeaderRequestInterceptor(AppConstants.HEADER_KEY_AUTHORIZATION,
					"key=" + AppUtil.getAppProperties().get(AppConstants.FIREBASE_SERVER_KEY)));
			interceptors.add(new HeaderRequestInterceptor(AppConstants.HEADER_KEY_CONTENT_TYPE,
					MediaType.APPLICATION_JSON_UTF8_VALUE));
			restTemplate.setInterceptors(interceptors);

			firebaseResponse = restTemplate.postForObject(AppUtil.getAppProperties().get(AppConstants.FIREBASE_API_URL),
					entity, String.class);
			CompletableFuture<String> pushNotification = CompletableFuture.completedFuture(firebaseResponse);
			CompletableFuture.allOf(pushNotification).join();
		} catch (Exception e) {
			LOGGER.error("ERROR: AppServiceImpl - callFCMPushNotificationAPI() - Failed to send FCM push notification",
					e);
		}
		LOGGER.info("INFO: AppServiceImpl - callFCMPushNotificationAPI() :: ends");
	}

	public JSONObject getExpiryNotificationBodyDetails(PushNotificationBean pushNotificationBean, JSONObject jsonObj) {
		LOGGER.info("INFO: AppServiceImpl - getExpiryNotificationBodyDetails() :: starts");
		Map<String, String> keyValuesMap = new HashMap<>();
		String titleText = "For Health";
		try {

			keyValuesMap.put(AppEnums.MKV_ACTIVITY_NAME.value(), pushNotificationBean.getActivityName());
			keyValuesMap.put(AppEnums.MKV_RUN_END_TIME.value(), pushNotificationBean.getExpiryMinutes());

			// push notification message based on the user language selection
			switch (pushNotificationBean.getLanguage()) {
				case AppConstants.USER_LANGUAGE_SPANISH:
					jsonObj.put(AppEnums.PN_TITLE.value(), titleText);
					jsonObj.put(
							AppConstants.PLATFORM_TYPE_ANDROID.equals(pushNotificationBean.getDeviceType())
									? AppEnums.PN_MESSAGE.value()
									: AppEnums.PN_BODY.value(),
							MailContent.generateMailContent(
									AppUtil.getAppProperties().get(AppConstants.PUSH_EXPIRY_NOTIFICATION_MESSAGE_SPANISH),
									keyValuesMap));
					break;
				case AppConstants.USER_LANGUAGE_CHINESE:
					jsonObj.put(AppEnums.PN_TITLE.value(), titleText);
					jsonObj.put(
							AppConstants.PLATFORM_TYPE_ANDROID.equals(pushNotificationBean.getDeviceType())
									? AppEnums.PN_MESSAGE.value()
									: AppEnums.PN_BODY.value(),
							MailContent.generateMailContent(
									AppUtil.getAppProperties().get(AppConstants.PUSH_EXPIRY_NOTIFICATION_MESSAGE_CHINESE),
									keyValuesMap));
					break;
				default:
					jsonObj.put(AppEnums.PN_TITLE.value(), titleText);
					jsonObj.put(
							AppConstants.PLATFORM_TYPE_ANDROID.equals(pushNotificationBean.getDeviceType())
									? AppEnums.PN_MESSAGE.value()
									: AppEnums.PN_BODY.value(),
							MailContent.generateMailContent(
									AppUtil.getAppProperties().get(AppConstants.PUSH_EXPIRY_NOTIFICATION_MESSAGE_ENGLISH),
									keyValuesMap));
					break;
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: AppServiceImpl - getExpiryNotificationBodyDetails()", e);
		}
		LOGGER.info("INFO: AppServiceImpl - getExpiryNotificationBodyDetails() :: ends");
		return jsonObj;
	}
}

@Service
class AppServiceImplThread  implements Runnable, AppService {
	private static final Logger LOGGER = Logger.getLogger(AppServiceImplThread.class);



	public static final String BASE_URL = "https://pm25.lass-net.org/";
	private static Retrofit retrofit = null;
	String timeZone;
	List<Object> userIdsList;

	UserDao userDao;
	StudyDao studyDao;
	ActivityDao activityDao;
	RestTemplate restTemplate;
	List<UserDto> userTimeZoneList = null;
	List<TemporalConditionDto> tmpSchedulesList = null;
	List<ActivityConditionDto> triggerActivityConditionList = null;
	List<ActivityConditionDto> triggerActivityConditionListRuns = null;
	Map<String, List<EnrollmentTokensDto>> userEnrollmentMap = null;
	List<ActivityGroupDto> activityGroupList = null;
	Map<Integer, List<GroupUsersInfoDto>> groupUsersIdMap = null;
	List<TemporalConditionDto> temporalConditionsList = null;
	List<ThresholdConditionsDto> thresholdConditionsList = null;
	List<ActivitiesDto> activitiesList = null;
	String methodType = null;
	AppServiceImplThread(){}
	AppServiceImplThread(UserDao userDao, StudyDao studyDao, ActivityDao activityDao, RestTemplate restTemplate) {
		super();
		this.userDao = userDao;
		this.studyDao = studyDao;
		this.activityDao = activityDao;
		this.restTemplate = restTemplate;
	}
	AppServiceImplThread(String timeZone,List<Object> userIdsList,UserDao userDao, StudyDao studyDao,
						 ActivityDao activityDao, RestTemplate restTemplate, List<UserDto> userTimeZoneList,
						 List<TemporalConditionDto> tmpSchedulesList, List<ActivityConditionDto> triggerActivityConditionList,
						 List<ActivityConditionDto> triggerActivityConditionListRuns,Map<String, List<EnrollmentTokensDto>> userEnrollmentMap,
						 List<ActivityGroupDto> activityGroupList, Map<Integer, List<GroupUsersInfoDto>> groupUsersIdMap, List<TemporalConditionDto> temporalConditionsList,
						 List<ThresholdConditionsDto> thresholdConditionsList,List<ActivitiesDto> activitiesList,String methodType ){
		this.timeZone = timeZone;
		this.userIdsList = userIdsList;
		this.userDao = userDao;
		this.studyDao = studyDao;
		this.activityDao = activityDao;
		this.restTemplate = restTemplate;
		this.userTimeZoneList = userTimeZoneList;
		this.tmpSchedulesList = tmpSchedulesList;
		this.triggerActivityConditionList = triggerActivityConditionList;
		this.triggerActivityConditionListRuns = triggerActivityConditionListRuns;
		this.userEnrollmentMap = userEnrollmentMap;
		this.activityGroupList = activityGroupList;
		this.groupUsersIdMap = groupUsersIdMap;
		this.temporalConditionsList = temporalConditionsList;
		this.thresholdConditionsList= thresholdConditionsList;
		this.activitiesList= activitiesList;
		this.methodType=methodType;
	}
	public void sendPushNotifications() {
		LOGGER.info("INFO: AppServiceImpl - thread sendPushNotifications() :: starts");
		List<UserDto> activeUsersList = new ArrayList<>();


		List<FitbitLass4uApiResponseBean> fitbitLass4uApiResponseList = new ArrayList<>();
		List<String> timeZoneList = new ArrayList<>();

		try {
			Map<Integer, ThresholdConditionDetailsBean> activeUserConditionsMap = new HashMap<>();
			Map<Integer, List<PushNotificationBean>> pushNotificationMap = new TreeMap<>();
			Map<Integer, List<PushNotificationBean>> scheduledPushNotificationMap = new TreeMap<>();
			Map<Integer, List<PushNotificationBean>> surveyPushNotificationMap = new TreeMap<>();
			List<FitbitUserInfoDto> fitbitUserInfoList = new ArrayList<>();
			List<UserStudiesDto> userStudyList = new ArrayList<>();
			Map<Integer, List<UserStudiesDto>> userStudyMap = new HashMap<>();
			List<AuthInfoDto> authInfoList = new ArrayList<>();
			Map<Integer, List<AuthInfoDto>> userAuthInfoMap = new HashMap<>();


			/*activeUsersList = userDao.findAllActiveLoggedInUsers();
			timeZoneList = userDao.getAllTimeZoneList();
			//timeZoneList.add("Asia/Kolkata");
			if (!activeUsersList.isEmpty() && !timeZoneList.isEmpty()) {
				for (String timeZone : timeZoneList) {*/

			//fetch fitbit_user_info based on userIds passed
			fitbitUserInfoList = userDao.findAllFitBitUserInfoDetailsUserIdsList(userIdsList);

			//map of userId with list of fitbitusers
			Map<Integer, List<FitbitUserInfoDto>> fitbitInfoMap = fitbitUserInfoList.stream()
					.collect(Collectors.groupingBy(FitbitUserInfoDto::getUserId));

			//fetch  user_studies based on userIds
			userStudyList = studyDao.fetchUserStudiesDetailsList(userIdsList, AppConstants.DEFAULT_STUDY_ID,
					null);

			//map of userId with list of user_studies
			userStudyMap = userStudyList.stream().collect(Collectors.groupingBy(UserStudiesDto::getUserId));

			//fetch auth_info based on userIds
			authInfoList = userDao.findAllAuthinfoDetailsByUserIdList(userIdsList);
			//map of userId with list of auth_info
			userAuthInfoMap = authInfoList.stream().collect(Collectors.groupingBy(AuthInfoDto::getUserId));

			//matches timezone with active users and creates list of user for given timezone
			if (methodType ==null) {
				for (UserDto user : userTimeZoneList) {
					ThresholdConditionDetailsBean conditionsMap = new ThresholdConditionDetailsBean();

					LOGGER.info("USER ID ::::::::::::::::::::::::::::::::: " + user.getUserId());

					// Check lass4u id is available for user or not
					//no records as moved to archive tables
					conditionsMap = this.callLass4USensorAPI(user, conditionsMap);

					// Check Fitbit details available for or not
					conditionsMap = this.fetchFitbitDetailsByUser(user, fitbitInfoMap, conditionsMap);

					// Update the fitbit and lass4u details for user
					if ((userStudyMap.get(user.getUserId()) != null) && StringUtils
							.isNotEmpty(userStudyMap.get(user.getUserId()).get(0).getEnrollmentId())) {
						FitbitLass4UDataDto fitbitLass4UDataDto = new FitbitLass4UDataDto();
						fitbitLass4UDataDto = this.saveFitbitLass4uData(userStudyMap, user, conditionsMap,
								fitbitLass4UDataDto);
						userDao.saveOrUpdateFitbitLass4UDataDetails(fitbitLass4UDataDto, AppConstants.DB_SAVE);
					}
					// Check the FITBIT and LASS4U sensor api status

					fitbitLass4uApiResponseList =
							this.getFailedAPIResponseDetailsListByUser(userAuthInfoMap,
									fitbitLass4uApiResponseList, user, conditionsMap);

					activeUserConditionsMap.put(user.getUserId(), conditionsMap);

				}
				// Get the triggered activities available
				pushNotificationMap = this.getTriggeredActivityRunsByUserAndActivityConditionDetails(
						activeUserConditionsMap, userStudyMap, userTimeZoneList, userAuthInfoMap,timeZone, triggerActivityConditionListRuns,
						userEnrollmentMap,activityGroupList, groupUsersIdMap, temporalConditionsList, thresholdConditionsList,activitiesList);

				//Get the scheduled activities available
				scheduledPushNotificationMap = this.getScheduledActivityRunsByUserAndActivityConditionDetails(
						userStudyMap, userTimeZoneList, userAuthInfoMap,timeZone, triggerActivityConditionList,userEnrollmentMap,
						activityGroupList, groupUsersIdMap, temporalConditionsList);
				// Send triggered push notifications to users
				this.sendTriggerredActivityRunsPushNotification(pushNotificationMap);

				// Send scheduled push notifications to users
				this.sendTriggerredActivityRunsPushNotification(scheduledPushNotificationMap);
			} else if(methodType.equals("survey")) {
				surveyPushNotificationMap = this.getActivityByUserAndActivityConditionDetails(
						null, userStudyMap, userTimeZoneList, userAuthInfoMap,timeZone, tmpSchedulesList, triggerActivityConditionList);
				LOGGER.info(">>>>>>>>>>>>>>surveyPushNotificationMap size : "+surveyPushNotificationMap.size());
				LOGGER.info(">>>>>>>>>>>>>>surveyPushNotificationMap map : "+surveyPushNotificationMap);
				LOGGER.info(">>>>>>>>>>>>>>surveyPushNotificationMap values : "+surveyPushNotificationMap.values());

				// Send scheduled push notifications to users
				this.sendTriggerredActivityRunsPushNotification(surveyPushNotificationMap);

			}

			// Send failure notification to users
			// this.sendFitbitAndLass4uSensorFailedNotification(fitbitLass4uApiResponseList);
			//---}
		}catch (Exception e) {
			LOGGER.error("ERROR: AppServiceImpl - thread sendPushNotifications()", e);
		}
		LOGGER.info("INFO: AppServiceImpl - thread  sendPushNotifications() :: ends");
	}
	public void run() {
		sendPushNotifications();
	}

	/**
	 * Get the LASS4U details for the provided user identifier
	 * 
	 * @author Mohan
	 * @param user
	 *            the {@link UserDto} details
	 * @param conditionsMap
	 *            the user threshold condition details
	 * @return the user condition details
	 */
	public ThresholdConditionDetailsBean callLass4USensorAPI(UserDto user,
			ThresholdConditionDetailsBean conditionsMap) {
		LOGGER.info("INFO: AppServiceImpl - callLass4USensorAPI() :: starts");
		/* HttpEntity<Object> entity = null; */
		HarvardIAQ lass4USensorData = null;
		try {

			// Check the LASS4U Sensor identifier is available or not
			if (StringUtils.isNotEmpty(user.getLass4uId())) {
				/*
				 * String certificatesTrustStorePath = "jdk1.8.0_51/jre/lib/security/cacerts";
				 * System.setProperty("javax.net.ssl.trustStore", certificatesTrustStorePath);
				 */
				/*
				 * String responsee; HttpURLConnection urlConnection; int responseCode = 0;
				 */
				/* MultiValueMap<String, String> headers = new LinkedMultiValueMap<>(); */
				/*
				 * headers.add(AppConstants.HEADER_KEY_AUTHORIZATION, new
				 * AppAuthentication().createLass4UAuthorizationValue(
				 * AppUtil.getAppProperties().get(AppConstants.LASS4U_API_USERNAME),
				 * AppUtil.getAppProperties().get(AppConstants.LASS4U_API_PSSWORD))); entity =
				 * new HttpEntity<>(null, headers);
				 */

				/* API call with valid SSL certification */
				/*
				 * Lass4uApiInterface apiService = getClient().create(Lass4uApiInterface.class);
				 * 
				 * Call<Lass4UBean> call = apiService.getLass4uData(new
				 * AppAuthentication().createLass4UAuthorizationValue(
				 * AppUtil.getAppProperties().get(AppConstants.LASS4U_API_USERNAME),
				 * AppUtil.getAppProperties().get(AppConstants.LASS4U_API_PSSWORD)),user.
				 * getLass4uId()); Response<Lass4UBean> bean=call.execute();
				 * System.out.println("*************"+bean.message()+
				 * "*******************************");
				 * 
				 * if (bean.message().equals(HttpStatus.OK.name())) {
				 * 
				 * LOGGER.info("LASS4U ::::::::::::::::::::::::::::::::: " + bean.body());
				 * 
				 * if (bean.body() != null && !bean.body().getFeeds().isEmpty()) {
				 * LOGGER.info("LASS4U RESPONSE :::::::::::::::::::::::::::::::::: " +
				 * bean.body().getFeeds().get(0).getHarvardIAQ().toString());
				 * conditionsMap.setCo(String.valueOf(bean.body().getFeeds().get(0).
				 * getHarvardIAQ().getCo2())) .setTemperature(
				 * String.valueOf(bean.body().getFeeds().get(0).getHarvardIAQ().getTemperature()
				 * ))
				 * .setPm(String.valueOf(bean.body().getFeeds().get(0).getHarvardIAQ().getPm25()
				 * )) .setRelativeHumidity(String
				 * .valueOf(bean.body().getFeeds().get(0).getHarvardIAQ().getRelativeHumidity())
				 * ) .setLight(String.valueOf(bean.body().getFeeds().get(0).getHarvardIAQ().
				 * getLight())); } else { conditionsMap.setLass4UCount(1); } }
				 */

				lass4USensorData = activityDao.fetchLass4USesorData(user.getLass4uId(), user.getTimeZone());
				if (null != lass4USensorData) {
					conditionsMap.setCo(String.valueOf(lass4USensorData.getsG8e()))
					.setTemperature(String.valueOf(lass4USensorData.getTemperature()))
					.setPm(String.valueOf(lass4USensorData.getPm25()))
					.setRelativeHumidity(String.valueOf(lass4USensorData.getRelativeHumidity()))
					.setLight(String.valueOf(lass4USensorData.getLight()));
				} else {
					conditionsMap.setLass4UCount(1);
				}
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: AppServiceImpl - callLass4USensorAPI()", e);
			conditionsMap.setLass4UCount(1);
		}
		LOGGER.info("INFO: AppServiceImpl - callLass4USensorAPI() :: ends");
		return conditionsMap;
	}

	/**
	 * Get the Fitbit details for the provided user identifier and fitbit identifier
	 * 
	 * @author Mohan
	 * @param user
	 *            the {@link UserDto} details
	 * @param fitbitInfoMap
	 *            the user fitbit details map
	 * @param conditionsMap
	 *            the conditions details map
	 * @return the user fitbit condition details
	 */
	public ThresholdConditionDetailsBean fetchFitbitDetailsByUser(UserDto user,
			Map<Integer, List<FitbitUserInfoDto>> fitbitInfoMap, ThresholdConditionDetailsBean conditionsMap) {
		LOGGER.info("INFO: AppServiceImpl - fetchFitbitDetailsByUser() :: starts");
		String heartRate = null;
		FitbitUserInfoDto fitbitUserInfo = null;
		ThresholdConditionDetailsBean updatedConditionMap = conditionsMap;
		try {

			if (fitbitInfoMap.get(user.getUserId()) != null && !fitbitInfoMap.get(user.getUserId()).isEmpty()) {
				fitbitUserInfo = fitbitInfoMap.get(user.getUserId()).get(0);

				// Check the access token is valid or not, if expired get the new access token
				// from fitbit refresh token and update the details
				updatedConditionMap = this.callFitbitSleepAPI(fitbitUserInfo, conditionsMap);

				if (!updatedConditionMap.isDisconnected()) {
					fitbitUserInfo = updatedConditionMap.getFitbitUserInfo();
					/*
					 * if (StringUtils.isEmpty(updatedConditionMap.getSleep())) {
					 * updatedConditionMap.setFitbitCount(updatedConditionMap.getFitbitCount() + 1);
					 * return updatedConditionMap; }else {
					 */
					conditionsMap.setSleep(updatedConditionMap.getSleep());
					/* } */

					updatedConditionMap = this.callFitbitActivityStepsAPI(fitbitUserInfo, conditionsMap);
					fitbitUserInfo = updatedConditionMap.getFitbitUserInfo();
					/*
					 * if (StringUtils.isEmpty(updatedConditionMap.getSteps())) {
					 * updatedConditionMap.setFitbitCount(updatedConditionMap.getFitbitCount() + 1);
					 * return updatedConditionMap; }else {
					 */
					conditionsMap.setSteps(updatedConditionMap.getSteps());
					/* } */
				}
				if (!updatedConditionMap.isDisconnected()) {
					updatedConditionMap = this.callFitbitRestingHeartRateAPI(fitbitUserInfo, conditionsMap);
					fitbitUserInfo = updatedConditionMap.getFitbitUserInfo();
					/*
					 * if (StringUtils.isEmpty(updatedConditionMap.getRestingHeartRate())) {
					 * updatedConditionMap.setFitbitCount(updatedConditionMap.getFitbitCount() + 1);
					 * return updatedConditionMap; }else {
					 */
					conditionsMap.setRestingHeartRate(updatedConditionMap.getRestingHeartRate());
					/* } */
				}
				if (!updatedConditionMap.isDisconnected()) {
					updatedConditionMap = this.callFitbitIntradayHeartRateAPI(fitbitUserInfo, user, conditionsMap);

					if (!updatedConditionMap.isDisconnected()) {
						heartRate = this.getCalculatedHeartRate(updatedConditionMap.getRestingHeartRate(),
								updatedConditionMap.getIntraDayHeartRate());
						updatedConditionMap.setHeartRate(heartRate);
					}
				}
			} else {
				updatedConditionMap.setFitbitCount(4);
			}
		} catch (Exception e) {
			updatedConditionMap.setFitbitCount(4);
			LOGGER.error("ERROR: AppServiceImpl - fetchFitbitDetailsByUser()", e);
		}
		LOGGER.info("INFO: AppServiceImpl - fetchFitbitDetailsByUser() :: ends");
		return updatedConditionMap;
	}

	/**
	 * Provides new fitbit access token for the provided refresh token and user
	 * identifier
	 * 
	 * @author Mohan
	 * @param fitbitRefreshToken
	 *            the fitbit refresh token
	 * @return the {@link FitbitUserInfoDto} details
	 */
	public FitbitUserInfoDto callFitbitRefreshTokenAPI(FitbitUserInfoDto fitbitUserInfo) {
		LOGGER.info("INFO: AppServiceImpl - callFitbitRefreshTokenAPI() :: starts");
		FitbitUserInfoDto updatedFitbitUserInfo = null;
		try {
			try {
				ResponseEntity<Object> responseEntity = null;
				HttpHeaders headers = new HttpHeaders();
				headers.add(AppConstants.HEADER_KEY_AUTHORIZATION,
						AppUtil.getAppProperties().get(AppConstants.FITBIT_RT_HEADER_AUTHORIZATION));
				headers.add(AppConstants.HEADER_KEY_CONTENT_TYPE,
						AppUtil.getAppProperties().get(AppConstants.FITBIT_RT_HEADER_CONTENT_TYPE));

				// Create the request body as a MultiValueMap
				MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
				body.add(AppConstants.HEADER_KEY_GRANT_TYPE,
						AppUtil.getAppProperties().get(AppConstants.FITBIT_RT_HEADER_GRANT_TYPE));
				body.add(AppConstants.HEADER_KEY_REFRESH_TOKEN, fitbitUserInfo.getFitbitRefreshToken());

				HttpEntity<Object> entity = new HttpEntity<>(body, headers);
	/*			try {*/
					responseEntity = restTemplate.exchange(AppUtil.getAppProperties().get("fitbit.rt.url"), HttpMethod.POST,
							entity, Object.class);
				/*	
				} catch (Exception e) {
					LOGGER.error("ERROR: AppServiceImpl - ResponseEntity()", e);

				}*/
				
				if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {

					@SuppressWarnings("unchecked")
					LinkedHashMap<String, Object> respBodyMap = (LinkedHashMap<String, Object>) responseEntity
					.getBody();

					// Update the fitbit user info details
					updatedFitbitUserInfo = new FitbitUserInfoDto().setModifiedOn(AppUtil.getCurrentDateTime())
							.setFitbitAccessToken(respBodyMap.get("access_token").toString())
							.setFitbitRefreshToken(respBodyMap.get("refresh_token").toString())
							.setUserId(fitbitUserInfo.getUserId()).setFuiId(fitbitUserInfo.getFuiId());
					return userDao.saveOrUpdateFitbitUserInfoDetails(updatedFitbitUserInfo, AppConstants.DB_UPDATE);
				} else {
					ObjectMapper mapper = new ObjectMapper();
					FitbitErrorResponse fitbitErrorResponse = mapper.readValue(responseEntity.getBody().toString(),
							FitbitErrorResponse.class);
					if (fitbitErrorResponse != null && !fitbitErrorResponse.getErrors().isEmpty()
							&& fitbitErrorResponse.getErrors().get(0).getErrorType().equals("invalid_grant")) {
						userDao.deleteFitBitUserInfo(String.valueOf(fitbitUserInfo.getUserId()),
								FIND_BY_TYPE_USERID);
						updatedFitbitUserInfo = new FitbitUserInfoDto();
						updatedFitbitUserInfo.setDisconnected(true);
					}
				}
			} catch (HttpStatusCodeException httpsce) {
				LOGGER.error("ERROR: AppServiceImpl - callFitbitRefreshTokenAPI()", httpsce);
				ObjectMapper mapper = new ObjectMapper();
				FitbitErrorResponse fitbitErrorResponse = mapper.readValue(httpsce.getResponseBodyAsString(),
						FitbitErrorResponse.class);
				if (fitbitErrorResponse != null && !fitbitErrorResponse.getErrors().isEmpty()
						&& fitbitErrorResponse.getErrors().get(0).getErrorType().equals("invalid_grant")) {
					userDao.deleteFitBitUserInfo(String.valueOf(fitbitUserInfo.getUserId()),
							FIND_BY_TYPE_USERID);
					updatedFitbitUserInfo = new FitbitUserInfoDto();
					updatedFitbitUserInfo.setDisconnected(true);
				}
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: AppServiceImpl - callFitbitRefreshTokenAPI()", e);

		}
		LOGGER.info("INFO: AppServiceImpl - callFitbitRefreshTokenAPI() :: ends");
		return updatedFitbitUserInfo;
	}

	/**
	 * Get the user fibit sleep details for the provided user identifier
	 * 
	 * @author Mohan
	 * @param fitbitUserInfo
	 *            the {@link FitbitUserInfoDto} details
	 * @param conditionsMap
	 *            the conditions details map
	 * @return the user condition details
	 */
	public ThresholdConditionDetailsBean callFitbitSleepAPI(FitbitUserInfoDto fitbitUserInfo,
			ThresholdConditionDetailsBean conditionsMap) {
		LOGGER.info("INFO: AppServiceImpl - callFitbitSleepAPI() :: starts");
		String sleepDuration = null;
		int count = 0;
		try {
			conditionsMap.setFitbitUserInfo(fitbitUserInfo);

			HashMap<String, String> headers = new HashMap<>();
			headers.put(AppConstants.HEADER_KEY_AUTHORIZATION,
					AppConstants.HEADER_VALUE_AUTH_TYPE + fitbitUserInfo.getFitbitAccessToken());

			Responsemodel responseEntity = AppUtil.exchangeData(
					AppUtil.getAppProperties().get(AppConstants.FITBIT_API_SLEEP), HttpMethod.GET.toString(), headers,
					null);
			if (responseEntity.getStatusCode() == HttpStatus.OK.value()) {

				SleepBean sleepBean = new Gson().fromJson(responseEntity.getBody(), SleepBean.class);
				if (sleepBean != null && !sleepBean.getSleep().isEmpty()) {
					List<Sleep> sleepList = sleepBean.getSleep().stream().filter(Sleep::isMainSleep)
							.collect(Collectors.toList());
					sleepDuration = String.valueOf(sleepList.stream().mapToInt(Sleep::getMinutesAsleep).sum());

					conditionsMap.setSleep(sleepDuration);
				}
			} else if (responseEntity.getStatusCode() == HttpStatus.UNAUTHORIZED.value() && count == 0) {
				FitbitUserInfoDto fitbitInfo = this.callFitbitRefreshTokenAPI(fitbitUserInfo);
				if (fitbitInfo == null) {
					return conditionsMap;
				} else if (fitbitInfo.isDisconnected()) {
					conditionsMap.setDisconnected(true);
					return conditionsMap;
				}

				// Call fitbit sleep API using the new fitbit access token
				count++;
				this.callFitbitSleepAPI(fitbitInfo, conditionsMap);
			} else {

			}
		} catch (Exception e) {
			LOGGER.error("ERROR: AppServiceImpl - callFitbitSleepAPI()", e);
		}
		LOGGER.info("INFO: AppServiceImpl - callFitbitSleepAPI() :: ends");
		return conditionsMap;
	}

	/**
	 * Get the user fibit activity steps details for the provided user identifier
	 * 
	 * @author Mohan
	 * @param fitbitUserInfo
	 *            the {@link FitbitUserInfoDto} details
	 * @param conditionsMap
	 *            the conditions details map
	 * @return the user condition details
	 */
	public ThresholdConditionDetailsBean callFitbitActivityStepsAPI(FitbitUserInfoDto fitbitUserInfo,
			ThresholdConditionDetailsBean conditionsMap) {
		LOGGER.info("INFO: AppServiceImpl - callFitbitActivityStepsAPI() :: starts");
		String steps = null;
		int count = 0;
		try {
			conditionsMap.setFitbitUserInfo(fitbitUserInfo);

			HashMap<String, String> headers = new HashMap<>();
			headers.put(AppConstants.HEADER_KEY_AUTHORIZATION,
					AppConstants.HEADER_VALUE_AUTH_TYPE + fitbitUserInfo.getFitbitAccessToken());

			Responsemodel responseEntity = AppUtil.exchangeData(
					AppUtil.getAppProperties().get(AppConstants.FITBIT_API_ACTIVITY_STEPS), HttpMethod.GET.toString(),
					headers, null);
			if (responseEntity.getStatusCode() == HttpStatus.OK.value()) {

				StepsBean stepsBean = new Gson().fromJson(responseEntity.getBody(), StepsBean.class);
				if (stepsBean != null && !stepsBean.getActivitiesSteps().isEmpty()) {
					steps = stepsBean.getActivitiesSteps().get(0).getValue();

					conditionsMap.setSteps(steps);
				}
			} else if (responseEntity.getStatusCode() == HttpStatus.UNAUTHORIZED.value() && count == 0) {
				FitbitUserInfoDto fitbitInfo = this.callFitbitRefreshTokenAPI(fitbitUserInfo);
				if (fitbitInfo == null) {
					return conditionsMap;
				} else if (fitbitInfo.isDisconnected()) {
					conditionsMap.setDisconnected(true);
					return conditionsMap;
				}
				count++;
				// Call fitbit activity steps API using the new fitbit access token
				this.callFitbitActivityStepsAPI(fitbitInfo, conditionsMap);
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: AppServiceImpl - callFitbitActivityStepsAPI()", e);
		}
		LOGGER.info("INFO: AppServiceImpl - callFitbitActivityStepsAPI() :: ends");
		return conditionsMap;
	}

	/**
	 * Get the user fibit resting heart rate details for the provided user
	 * identifier
	 * 
	 * @author Mohan
	 * @param fitbitUserInfo
	 *            the {@link FitbitUserInfoDto} details
	 * @param conditionsMap
	 *            the conditions details map
	 * @return the user condition details
	 */
	public ThresholdConditionDetailsBean callFitbitRestingHeartRateAPI(FitbitUserInfoDto fitbitUserInfo,
			ThresholdConditionDetailsBean conditionsMap) {
		LOGGER.info("INFO: AppServiceImpl - callFitbitRestingHeartRateAPI() :: starts");
		String restingHeartRate = null;
		int count = 0;
		try {
			conditionsMap.setFitbitUserInfo(fitbitUserInfo);

			HashMap<String, String> headers = new HashMap<>();
			headers.put(AppConstants.HEADER_KEY_AUTHORIZATION,
					AppConstants.HEADER_VALUE_AUTH_TYPE + fitbitUserInfo.getFitbitAccessToken());

			Responsemodel responseEntity = AppUtil.exchangeData(
					AppUtil.getAppProperties().get(AppConstants.FITBIT_API_HEARTRATE_RESTING),
					HttpMethod.GET.toString(), headers, null);
			if (responseEntity.getStatusCode() == HttpStatus.OK.value()) {

				RestingHeartRateBean restingHeartRateBean = new Gson().fromJson(responseEntity.getBody(),
						RestingHeartRateBean.class);
				if (restingHeartRateBean != null && !restingHeartRateBean.getActivitiesHeart().isEmpty()) {
					restingHeartRate = String
							.valueOf(restingHeartRateBean.getActivitiesHeart().get(0).getValue().getRestingHeartRate());

					conditionsMap.setRestingHeartRate(restingHeartRate);
				}
			} else if (responseEntity.getStatusCode() == HttpStatus.UNAUTHORIZED.value() && count == 0) {
				FitbitUserInfoDto fitbitInfo = this.callFitbitRefreshTokenAPI(fitbitUserInfo);
				if (fitbitInfo == null) {
					return conditionsMap;
				} else if (fitbitInfo.isDisconnected()) {
					conditionsMap.setDisconnected(true);
					return conditionsMap;
				}
				count++;
				// Call fitbit resting heart rate API using the new fitbit access token
				this.callFitbitRestingHeartRateAPI(fitbitInfo, conditionsMap);
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: AppServiceImpl - callFitbitRestingHeartRateAPI()", e);
		}
		LOGGER.info("INFO: AppServiceImpl - callFitbitRestingHeartRateAPI() :: ends");
		return conditionsMap;
	}

	/**
	 * Get the user fibit intraday heart rate details for the provided user
	 * identifier
	 * 
	 * @author Mohan
	 * @param user
	 *            the {@link UserDto} details
	 * @param fitbitUserInfo
	 *            the {@link FitbitUserInfoDto} details
	 * @param conditionsMap
	 *            the conditions details map
	 * @return the user condition details
	 */
	public ThresholdConditionDetailsBean callFitbitIntradayHeartRateAPI(FitbitUserInfoDto fitbitUserInfo, UserDto user,
			ThresholdConditionDetailsBean conditionsMap) {
		LOGGER.info("INFO: AppServiceImpl - callFitbitIntradayHeartRateAPI() :: starts");
		String intraDayHeartRate = null;
		String fromDateTime = "";
		String toDateTime = "";
		Map<String, String> keyValuesMap = new HashMap<>();
		int count = 0;
		try {
			conditionsMap.setFitbitUserInfo(fitbitUserInfo);

			HashMap<String, String> headers = new HashMap<>();
			headers.put(AppConstants.HEADER_KEY_AUTHORIZATION,
					AppConstants.HEADER_VALUE_AUTH_TYPE + fitbitUserInfo.getFitbitAccessToken());

			// Get the user current date/time for the user timezone
			toDateTime = AppUtil.convertDateTimeByTimeZone(AppUtil.getCurrentDateTime(),
					AppConstants.SDF_DATE_TIME_FORMAT, AppConstants.SDF_DATE_TIME_FORMAT, user.getTimeZone());
			fromDateTime = AppUtil.addMinutes(toDateTime, -15);
			keyValuesMap.put(AppEnums.FT_TIME_FROM.value(), fromDateTime.substring(11, fromDateTime.length() - 3));
			keyValuesMap.put(AppEnums.FT_TIME_TO.value(), toDateTime.substring(11, toDateTime.length() - 3));

			Responsemodel responseEntity = AppUtil.exchangeData(
					MailContent.generateMailContent(
							AppUtil.getAppProperties().get(AppConstants.FITBIT_API_HEARTRATE_INTRADAY), keyValuesMap),
					HttpMethod.GET.toString(), headers, null);
			if (responseEntity.getStatusCode() == HttpStatus.OK.value()) {

				IntraHeartRateBean intraHeartRateBean = new Gson().fromJson(responseEntity.getBody(),
						IntraHeartRateBean.class);
				if (intraHeartRateBean != null && !intraHeartRateBean.getActivitiesHeart().isEmpty()) {
					intraDayHeartRate = intraHeartRateBean.getActivitiesHeart().get(0).getValue();

					conditionsMap.setIntraDayHeartRate(intraDayHeartRate);
				}
			} else if (responseEntity.getStatusCode() == HttpStatus.UNAUTHORIZED.value() && count == 0) {
				FitbitUserInfoDto fitbitInfo = this.callFitbitRefreshTokenAPI(fitbitUserInfo);
				if (fitbitInfo == null) {
					return conditionsMap;
				} else if (fitbitInfo.isDisconnected()) {
					conditionsMap.setDisconnected(true);
					return conditionsMap;
				}
				count++;
				// Call fitbit intraday heart rate API using the new fitbit access token
				this.callFitbitIntradayHeartRateAPI(fitbitInfo, user, conditionsMap);
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: AppServiceImpl - callFitbitIntradayHeartRateAPI()", e);
		}
		LOGGER.info("INFO: AppServiceImpl - callFitbitIntradayHeartRateAPI() :: ends");
		return conditionsMap;
	}

	/**
	 * Get the failed fitbit and lass4u api response for the provided user
	 * 
	 * @author Mohan
	 * @param userAuthInfoMap
	 *            the user auth info details
	 * @param fitbitLass4uApiResponseList
	 *            the {@link FitbitLass4uApiResponseBean} details list
	 * @param user
	 *            the {@link UserDto} details
	 * @param conditionsMap
	 *            the user threshold condition details
	 * @return the {@link FitbitLass4uApiResponseBean} details list
	 */
	public List<FitbitLass4uApiResponseBean> getFailedAPIResponseDetailsListByUser(
			Map<Integer, List<AuthInfoDto>> userAuthInfoMap,
			List<FitbitLass4uApiResponseBean> fitbitLass4uApiResponseList, UserDto user,
			ThresholdConditionDetailsBean conditionsMap) {
		LOGGER.info("INFO: AppServiceImpl - getFailedAPIResponseDetailsListByUser() :: starts");
		try {

			// Check LASS4U sensor api failed status
			if (conditionsMap.getLass4UCount() > 0) {
				FitbitLass4uApiResponseBean fitbitLass4uApiResponseBean = new FitbitLass4uApiResponseBean()
						.setLass4uSensorStatus((conditionsMap.getLass4UCount() > 0) ? true : false)
						.setFitbitSensorStatus(false).setUserId(String.valueOf(user.getUserId()))
						.setLanguage(user.getLanguage()).setTimeZone(user.getTimeZone())
						.setDeviceToken(userAuthInfoMap.get(user.getUserId()).get(0).getDeviceToken())
						.setDeviceType(userAuthInfoMap.get(user.getUserId()).get(0).getDeviceType());
				fitbitLass4uApiResponseList.add(fitbitLass4uApiResponseBean);
			}

			// Check Fitbit sensor api failed status
			if (conditionsMap.getFitbitCount() > 0) {
				FitbitLass4uApiResponseBean fitbitLass4uApiResponseBean = new FitbitLass4uApiResponseBean()
						.setLass4uSensorStatus(false)
						.setFitbitSensorStatus((conditionsMap.getFitbitCount() > 0) ? true : false)
						.setUserId(String.valueOf(user.getUserId())).setLanguage(user.getLanguage())
						.setTimeZone(user.getTimeZone())
						.setDeviceToken(userAuthInfoMap.get(user.getUserId()).get(0).getDeviceToken())
						.setDeviceType(userAuthInfoMap.get(user.getUserId()).get(0).getDeviceType());
				fitbitLass4uApiResponseList.add(fitbitLass4uApiResponseBean);
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: AppServiceImpl - getFailedAPIResponseDetailsListByUser()", e);
		}
		LOGGER.info("INFO: AppServiceImpl - getFailedAPIResponseDetailsListByUser() :: ends");
		return fitbitLass4uApiResponseList;
	}

	/**
	 * Get the actual heart rate value for the provided resting and intra heart rate
	 * 
	 * @author Mohan
	 * @param restingHeartRate
	 *            the resting heart rate value
	 * @param intraDayHeartRate
	 *            the intraday heart rate value
	 * @return the actual heart rate value
	 */
	public String getCalculatedHeartRate(String restingHeartRate, String intraDayHeartRate) {
		LOGGER.info("INFO: AppServiceImpl - getCalculatedHeartRate() :: starts");
		String heartRate = null;
		try {

			if (StringUtils.isNotEmpty(restingHeartRate) && StringUtils.isNotEmpty(intraDayHeartRate)) {
				heartRate = String.valueOf(
						Math.round(((Double.parseDouble(intraDayHeartRate) - Double.parseDouble(restingHeartRate))
								/ Double.parseDouble(restingHeartRate)) * 100D))
						.replace("-", "");
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: AppServiceImpl - getCalculatedHeartRate()", e);
		}
		LOGGER.info("INFO: AppServiceImpl - getCalculatedHeartRate() :: ends");
		return heartRate;
	}

	/**
	 * Provides the user activity runs for the activity condition details and user
	 * identifier
	 * 
	 * @author Mohan
	 * @param activeUserConditionsMap
	 *            the active user threshold condition details for Fitbit and Lass4u
	 *            Sensors
	 * @param userStudyMap
	 *            the user study details for the provided user identifier
	 * @param usersList
	 *            the active users details list
	 * @param userAuthInfoMap
	 *            the user authinfo details map
	 * @return
	 */
	public Map<Integer, List<PushNotificationBean>> getTriggeredActivityRunsByUserAndActivityConditionDetails(
			Map<Integer, ThresholdConditionDetailsBean> activeUserConditionsMap,
			Map<Integer, List<UserStudiesDto>> userStudyMap, List<UserDto> usersList,
			Map<Integer, List<AuthInfoDto>> userAuthInfoMap, String timeZone, List<ActivityConditionDto> triggerActivityConditionList ,
			Map<String, List<EnrollmentTokensDto>> userEnrollmentMap, List<ActivityGroupDto> activityGroupList,
			Map<Integer, List<GroupUsersInfoDto>> groupUsersIdMap, List<TemporalConditionDto> temporalConditionsList,
			List<ThresholdConditionsDto> thresholdConditionsList,List<ActivitiesDto> activitiesList) {
		LOGGER.info("INFO: AppServiceImpl - getTriggeredActivityRunsByUserAndActivityConditionDetails() :: starts");
		//List<ActivityConditionDto> triggerActivityConditionList = new ArrayList<>();
		List<Object> activityConditionIds = new ArrayList<>();
	//	List<TemporalConditionDto> temporalConditionsList = new ArrayList<>();
		Map<Integer, List<TemporalConditionDto>> temporalConditionMap = new HashMap<>();
		Map<String, UserActivitiesDto> userActivityMap = new HashMap<>();
		Map<String, UserActivitiesRunsDto> userActivityRunMap = new HashMap<>();
		List<EnrollmentTokensDto> enrollmentTokensList = new ArrayList<>();
	//	Map<String, List<EnrollmentTokensDto>> userEnrollmentMap = new HashMap<>();
	//	List<ActivityGroupDto> activityGroupList = new ArrayList<>();
		Map<Integer, List<ActivityGroupDto>> activityGroupMap = new HashMap<>();
		Map<Integer, List<PushNotificationBean>> pushNotificationMap = new TreeMap<>();
		Map<Integer, List<ActivityConditionDto>> activityConditionMap = new HashMap<>();
    // Map<Integer, List<GroupUsersInfoDto>> groupUsersIdMap = new HashMap<>();
        List<Integer> groupIdsList = new ArrayList<>();
        List<GroupUsersInfoDto> groupUsersInfoDto = new ArrayList<>();
		try {

			/*triggerActivityConditionList = activityDao.fetchActivityConditionDetailsList(
					AppConstants.ACTIVITY_SUB_TYPE_TRIGGERED, AppConstants.FIND_BY_TYPE_ACTIVITY_SUB_TYPE, timeZone);*/
			if (!triggerActivityConditionList.isEmpty()) {
				activityConditionIds = triggerActivityConditionList.stream().map(ActivityConditionDto::getConditionId)
						.collect(Collectors.toList());
				activityConditionMap = triggerActivityConditionList.stream()
						.collect(Collectors.groupingBy(ActivityConditionDto::getConditionId));

				/*enrollmentTokensList = studyDao.fetchAllEnrollmentDetailsList();
				userEnrollmentMap = enrollmentTokensList.parallelStream()
						.collect(Collectors.groupingBy(EnrollmentTokensDto::getEnrollmentId));*/
			
				/*activityGroupList = studyDao.fetchAllActivityGroupList(activityConditionIds,
						AppConstants.FIND_BY_TYPE_CONDITIONID);*/
				activityGroupMap = activityGroupList.parallelStream()
						.collect(Collectors.groupingBy(ActivityGroupDto::getGroupId));
	//			groupUsersInfoDto =  studyDao.fetchAllGroupUsersInfoDetails();
	//			groupUsersIdMap = groupUsersInfoDto.parallelStream()
	//			.collect(Collectors.groupingBy(GroupUsersInfoDto::getUserId));
				
				groupIdsList = activityGroupList.stream().map(ActivityGroupDto::getGroupId).collect(Collectors.toList());
				
	/*			temporalConditionsList = activityDao.fetchTemporalConditionDetailsList(activityConditionIds,
						AppConstants.FIND_BY_TYPE_CONDITIONID);*/
				temporalConditionMap = temporalConditionsList.stream()
						.collect(Collectors.groupingBy(TemporalConditionDto::getConditionId));
				// Map if any new triggered activities are added from Qualtrics

					this.mapUserAndActivities(userStudyMap, usersList, triggerActivityConditionList, temporalConditionMap,
							userEnrollmentMap, activityGroupMap, groupIdsList, groupUsersIdMap,activitiesList);
				

				for (UserDto user : usersList) {
					String userId = String.valueOf(user.getUserId());
					String userStudiesId = String.valueOf(userStudyMap.get(user.getUserId()).get(0).getUserStudiesId());

					// Get the user activities details
					List<UserActivitiesDto> userActivityList = activityDao.fetchUserActivityDetailsList(userId,
							userStudiesId, AppConstants.FIND_BY_TYPE_USERID_STUDYID_ACTIVITY_SUB_TYPE, user.getTimeZone());
					for (UserActivitiesDto userActivitiesDto : userActivityList) {
						boolean mapped = false;
						// Check the user has meet the participation target count or not for the
						// activity condition
						mapped = activityDao.checkUserRelatedToCorrectGroup(user.getUserId(),
								userActivitiesDto.getConditionId(), user.getGroupId(), groupUsersIdMap);
						if (mapped) {
							if (userActivitiesDto.getCompletedCount() < activityConditionMap
									.get(userActivitiesDto.getConditionId()).get(0).getTotalParticipationTarget()) {
								UserActivitiesRunsDto userActivityRun = activityDao.fetchUserActivityRunsDetails(userId,
										userStudiesId, String.valueOf(userActivitiesDto.getConditionId()), null);
								userActivityMap.put(
										userId + "@" + userStudiesId + "@" + userActivitiesDto.getConditionId(),
										userActivitiesDto);
								userActivityRunMap.put(
										userId + "@" + userStudiesId + "@" + userActivitiesDto.getConditionId(),
										userActivityRun);
							}
						}
					}
				}

				pushNotificationMap = this.checkUserActivityConditionPrerequisists(thresholdConditionsList , userStudyMap,
						usersList, triggerActivityConditionList, temporalConditionMap, userActivityMap,
						userActivityRunMap, activeUserConditionsMap, userAuthInfoMap, timeZone);
			}

		} catch (Exception e) {
			LOGGER.error("ERROR: AppServiceImpl - getTriggeredActivityRunsByUserAndActivityConditionDetails()", e);
		}
		LOGGER.info("INFO: AppServiceImpl - getTriggeredActivityRunsByUserAndActivityConditionDetails() :: ends");
		return pushNotificationMap;
	}

	public Map<Integer, List<PushNotificationBean>> getActivityByUserAndActivityConditionDetails(
			Map<Integer, ThresholdConditionDetailsBean> activeUserConditionsMap,
			Map<Integer, List<UserStudiesDto>> userStudyMap, List<UserDto> usersList,
			Map<Integer, List<AuthInfoDto>> userAuthInfoMap, String timeZone, List<TemporalConditionDto> tmpSchedulesList, List<ActivityConditionDto> triggerActivityConditionList) {
		LOGGER.info("INFO: AppServiceImpl - getActivityByUserAndActivityConditionDetails() :: starts");
		//List<ActivityConditionDto> triggerActivityConditionList = new ArrayList<>();
		List<ActivityGroupDto> activityGroupList = new ArrayList<>();
		Map<Integer, List<PushNotificationBean>> pushNotificationMap = new TreeMap<>();
		Map<Integer, UserDto> userIdUserMap = usersList.stream()
				.collect(Collectors.toMap(UserDto::getUserId, Function.identity()));
		List<PushNotificationBean> userPushNotificationBeanList = new ArrayList<>();
		try {

			String currentDateTimeBasedOnZone = AppUtil.getCurrentDateTime(timeZone);
			//get active temporal_condition
			String currentDate = currentDateTimeBasedOnZone.split(" ")[0];

			//List<TemporalConditionDto> tmpSchedulesList = activityDao.fetchTemporalActivityScheduleList(currentDateTimeBasedOnZone.split(" ")[0]);

			/*triggerActivityConditionList = activityDao.fetchActivityConditionDetailsList(
					AppConstants.ACTIVITY_SUB_TYPE_SCHEDULED, AppConstants.FIND_BY_TYPE_ACTIVITY_SUB_TYPE, timeZone);*/

			Map<Integer,ActivityConditionDto> conditionIdActivityCondition = triggerActivityConditionList.stream()
					.collect(Collectors.toMap(ActivityConditionDto::getConditionId, Function.identity()));

			for (TemporalConditionDto tmpSchedule: tmpSchedulesList) {
				String tmpScheduleDateStartTime = tmpSchedule.getStartDate() + " " + tmpSchedule.getStartTime();
				String tmpScheduleDateEndTime = tmpSchedule.getEndDate() + " " + tmpSchedule.getEndTime();
				LOGGER.info("tmpSchedule.getRepetitionFrequencyDays() >>"+tmpSchedule.getRepetitionFrequencyDays()+",getConditionId >>"+tmpSchedule.getConditionId()+",getTemporalConditionId >>"+tmpSchedule.getTemporalConditionId()+",tmpScheduleDateStartTime : "+tmpScheduleDateStartTime+", tmpScheduleDateEndTime : "+tmpScheduleDateEndTime+", currentDateTimeBasedOnZone : "+currentDateTimeBasedOnZone+", timeZone : "+timeZone);
				if(tmpSchedule.getRepetitionFrequencyDays() == null){
					continue;
				}
				List<String> repetitionDayList = Arrays.asList(tmpSchedule.getRepetitionFrequencyDays().split(","));
				if (SDF_DATE_TIME.parse(tmpScheduleDateStartTime).before(SDF_DATE_TIME.parse(currentDateTimeBasedOnZone)) && SDF_DATE_TIME.parse(tmpScheduleDateEndTime).after(SDF_DATE_TIME.parse(currentDateTimeBasedOnZone)) && repetitionDayList.contains(AppUtil.getDayByDate(currentDate)) ){
					activityGroupList = studyDao.fetchActivityGroupsListByConditionId(tmpSchedule.getConditionId());
					GroupIdentifierDto groupIdentifier= studyDao.fetchGroupIdentifierDetailsById(activityGroupList.get(0).getGroupId());
					if (groupIdentifier.getGroupType().equals(INDIVIDUAL_GROUP)){
						UserDto user = userDao.fetchByGroupId(groupIdentifier.getGroupId());

						if (userIdUserMap.get(user.getUserId())!=null) {
							UserActivitiesDto userActivity = activityDao.findByUserIdConditionId(activityGroupList.get(0).getConditionId(),user.getUserId(),tmpScheduleDateStartTime);
							if (userActivity != null) {
								continue;
							}
							ActivityConditionDto activityCondition = conditionIdActivityCondition.get(activityGroupList.get(0).getConditionId());
							UserActivitiesDto saveUserActivity = new UserActivitiesDto();
							saveUserActivity.setConditionId(activityCondition.getConditionId())
									.setUserId(user.getUserId())
									.setUserStudiesId(userStudyMap.get(user.getUserId()).get(0)
											.getUserStudiesId())
									.setActivityStatus(AppConstants.RUN_STATE_START).setActivityRunId(0)
									.setCompletedCount(0).setMissedCount(0).setTotalCount(1)
									.setLastCompletedRunId(0).setCreatedOn(AppUtil.getCurrentDateTime(user.getTimeZone()))
									.setCurrentRunDate(null).setExpireNotificationSent(false);
							saveUserActivity = activityDao.saveOrUpdateUserActivities(saveUserActivity,
									AppConstants.DB_SAVE);
							LOGGER.info("UserActivityId >>"+saveUserActivity.getUserActivityId()+" groupIdentifier.getGroupType() >>>"+groupIdentifier.getGroupType()+" tmpSchedule.getRepetitionFrequencyDays() >>"+tmpSchedule.getRepetitionFrequencyDays()+",getConditionId >>"+tmpSchedule.getConditionId()+",getTemporalConditionId >>"+tmpSchedule.getTemporalConditionId()+",tmpScheduleDateStartTime : "+tmpScheduleDateStartTime+", tmpScheduleDateEndTime : "+tmpScheduleDateEndTime+", currentDateTimeBasedOnZone : "+currentDateTimeBasedOnZone+", timeZone : "+timeZone);

							PushNotificationBean pushNotificationBean = new PushNotificationBean()
									.setActivityId(activityCondition.getActivityConditionId())
									.setConditionId(String.valueOf(activityCondition.getConditionId()))
									//.setCurrentRunId(String.valueOf(userActivityMap.get(user.getUserId()).getTotalCount() + 1))
									.setLanguage(user.getLanguage())
									.setUserId(String.valueOf(user.getUserId()))
									.setUserTimeZone(user.getTimeZone())
									.setDeviceToken(
											userAuthInfoMap.get(user.getUserId()).get(0).getDeviceToken())
									.setDeviceType(
											userAuthInfoMap.get(user.getUserId()).get(0).getDeviceType())
									.setActivityName(activityCondition.getActivityConditionName());
							if(pushNotificationMap.get(user.getUserId())!=null) {
								userPushNotificationBeanList = pushNotificationMap.get(user.getUserId());
							}
							userPushNotificationBeanList.add(pushNotificationBean);
						}
						if(userPushNotificationBeanList!=null && !userPushNotificationBeanList.isEmpty()) {
							pushNotificationMap.put(user.getUserId(), userPushNotificationBeanList);
						}
					}else if (groupIdentifier.getGroupType().equals(BUILDING_GROUP)){
						// group user mapping all userids associated with group
						List<GroupUsersInfoDto> groupUserList = studyDao.fetchAllGroupUsersInfoListbyId(groupIdentifier.getGroupId(),AppConstants.FIND_BY_TYPE_GROUPID);
						for (GroupUsersInfoDto groupUsersInfo: groupUserList) {
							userPushNotificationBeanList = new ArrayList<>();
							UserDto user = userDao.fetchUserDetails(groupUsersInfo.getUserId().toString(), FIND_BY_TYPE_USERID);
                            if (user == null) {
                                LOGGER.info("user not found for groupUsersInfo ID : "+groupUsersInfo.getGroupId() +", userId : "+groupUsersInfo.getUserId());
                            }
                            if (user!=null && userIdUserMap.get(user.getUserId())!=null) {
								UserActivitiesDto userActivity = activityDao.findByUserIdConditionId(activityGroupList.get(0).getConditionId(),user.getUserId(),tmpScheduleDateStartTime);
								if (userActivity != null) {
									continue;
								}
								ActivityConditionDto activityCondition = conditionIdActivityCondition.get(activityGroupList.get(0).getConditionId());
								UserActivitiesDto saveUserActivity = new UserActivitiesDto();
								saveUserActivity.setConditionId(activityCondition.getConditionId())
										.setUserId(user.getUserId())
										.setUserStudiesId(userStudyMap.get(user.getUserId()).get(0)
												.getUserStudiesId())
										.setActivityStatus(AppConstants.RUN_STATE_START).setActivityRunId(0)
										.setCompletedCount(0).setMissedCount(0).setTotalCount(1)
										.setLastCompletedRunId(0).setCreatedOn(AppUtil.getCurrentDateTime(user.getTimeZone()))
										.setCurrentRunDate(null).setExpireNotificationSent(false);
								saveUserActivity = activityDao.saveOrUpdateUserActivities(saveUserActivity,
										AppConstants.DB_SAVE);

								LOGGER.info("UserActivityId >>"+saveUserActivity.getUserActivityId()+" groupIdentifier.getGroupType() >>>"+groupIdentifier.getGroupType()+" tmpSchedule.getRepetitionFrequencyDays() >>"+tmpSchedule.getRepetitionFrequencyDays()+",getConditionId >>"+tmpSchedule.getConditionId()+",getTemporalConditionId >>"+tmpSchedule.getTemporalConditionId()+",tmpScheduleDateStartTime : "+tmpScheduleDateStartTime+", tmpScheduleDateEndTime : "+tmpScheduleDateEndTime+", currentDateTimeBasedOnZone : "+currentDateTimeBasedOnZone+", timeZone : "+timeZone);
								PushNotificationBean pushNotificationBean = new PushNotificationBean()
										.setActivityId(activityCondition.getActivityConditionId())
										.setConditionId(String.valueOf(activityCondition.getConditionId()))
										//.setCurrentRunId(String.valueOf(userActivityMap.get(user.getUserId()).getTotalCount() + 1))
										.setLanguage(user.getLanguage())
										.setUserId(String.valueOf(user.getUserId()))
										.setUserTimeZone(user.getTimeZone())
										.setDeviceToken(
												userAuthInfoMap.get(user.getUserId()).get(0).getDeviceToken())
										.setDeviceType(
												userAuthInfoMap.get(user.getUserId()).get(0).getDeviceType())
										.setActivityName(activityCondition.getActivityConditionName());
								if(pushNotificationMap.get(user.getUserId())!=null) {
									userPushNotificationBeanList = pushNotificationMap.get(user.getUserId());
									boolean pushNotificationExist = false;
									for(PushNotificationBean pnb :userPushNotificationBeanList) {
										if(pnb.getConditionId() == pushNotificationBean.getConditionId() && pnb.getUserId() == pushNotificationBean.getUserId() && pnb.getActivityId() == pushNotificationBean.getActivityId()){
											pushNotificationExist = true;
											break;
										}
									}
									if(pushNotificationExist){
										continue;
									}
								}
								userPushNotificationBeanList.add(pushNotificationBean);
							}

							if(userPushNotificationBeanList!=null && !userPushNotificationBeanList.isEmpty()) {
								pushNotificationMap.put(user.getUserId(),userPushNotificationBeanList);
							}
						}
					}
				}
			}

		} catch (Exception e) {
			LOGGER.error("ERROR: AppServiceImpl - getActivityByUserAndActivityConditionDetails()", e);
		}
		LOGGER.info("INFO: AppServiceImpl - getActivityByUserAndActivityConditionDetails() :: ends");
		return pushNotificationMap;
	}

	/**
	 * Map users to the new activity
	 * 
	 * @author Mohan
	 * @param userStudyMap
	 *            the user studies details map
	 * @param usersList
	 *            the users details list
	 * @param triggerActivityConditionList
	 *            the activity details list
	 * @param temporalConditionMap
	 *            the temporal condition details map
	 * @param userEnrollmentMap
	 *            the user enrollment details map
	 * @param activityGroupMap
	 *            the activity group details map
	 * @throws CustomException
	 * @throws ParseException
	 */
	public void mapUserAndActivities(Map<Integer, List<UserStudiesDto>> userStudyMap, List<UserDto> usersList,
			List<ActivityConditionDto> triggerActivityConditionList,
			Map<Integer, List<TemporalConditionDto>> temporalConditionMap,
			Map<String, List<EnrollmentTokensDto>> userEnrollmentMap,
			Map<Integer, List<ActivityGroupDto>> activityGroupMap, List<Integer> groupIdList , Map<Integer, List<GroupUsersInfoDto>> groupUsersListIdMap,List<ActivitiesDto> activitiesList ) throws CustomException, ParseException {
		LOGGER.info("INFO: AppServiceImpl - mapUserAndActivities() :: ends");
		
	//	List<ActivitiesDto> activitiesList = new ArrayList<>();
		try {

	//		activitiesList = activityDao.fetchAllActiveActivities();
			if (activitiesList != null && !activitiesList.isEmpty()) {
				Map<Integer, List<ActivitiesDto>> activitiesMap = activitiesList.stream()
						.collect(Collectors.groupingBy(ActivitiesDto::getActivityId));
				for (ActivityConditionDto activityConditionDto : triggerActivityConditionList) {
					for (UserDto user : usersList) {
						boolean mapped = false;
						String activityLanguage = AppConstants.USER_LANGUAGE_CHINESE_QUALTRICS.equalsIgnoreCase(
								activitiesMap.get(activityConditionDto.getActivityId()).get(0).getLanguage())
								? AppConstants.USER_LANGUAGE_CHINESE
										: activitiesMap.get(activityConditionDto.getActivityId()).get(0).getLanguage();
						if (user.getLanguage().equalsIgnoreCase(activityLanguage)) {
							String userId = String.valueOf(user.getUserId());
							String userStudiesId = String
									.valueOf(userStudyMap.get(user.getUserId()).get(0).getUserStudiesId());

							/*EnrollmentTokensDto enrollmentToken = userEnrollmentMap
									.get(userStudyMap.get(user.getUserId()).get(0).getEnrollmentId()).get(0);*/
							
							for(Integer groupId : groupIdList) {
								List<ActivityGroupDto> activityGroupList = activityGroupMap
										.get(groupId);
								List<Integer> groupConditionIdList = new ArrayList<>();
								if (activityGroupList != null && !activityGroupList.isEmpty()) {
									groupConditionIdList = activityGroupList.stream()
											.map(ActivityGroupDto::getConditionId).collect(Collectors.toList());
								}

								if (!groupConditionIdList.isEmpty()
										&& groupConditionIdList.contains(activityConditionDto.getConditionId())) {

									UserActivitiesDto userActDto = activityDao.fetchUserActivityDetails(userId,
											String.valueOf(activityConditionDto.getConditionId()), userStudiesId,
											AppConstants.FIND_BY_TYPE_USERID_CONDITIONID);
									TemporalConditionDto temporalConditionDto = temporalConditionMap
											.get(activityConditionDto.getConditionId()).get(0);
									if (userActDto == null) {
										UserActivitiesDto saveUserActivity = new UserActivitiesDto();
										Integer anchorDays = temporalConditionDto.getAnchorDays();
										if (!AppConstants.SDF_DATE.parse(temporalConditionDto.getEndDate())
												.before(AppConstants.SDF_DATE.parse(AppUtil.addDays(
														userStudyMap.get(user.getUserId()).get(0).getCreatedOn(),AppConstants.SDF_DATE_TIME_FORMAT
														,anchorDays, AppConstants.SDF_DATE_FORMAT)))) {

											mapped = activityDao.checkUserRelatedToCorrectGroup(user.getUserId(),
													activityConditionDto.getConditionId(), user.getGroupId(), groupUsersListIdMap);
											if (mapped) {
												saveUserActivity.setConditionId(activityConditionDto.getConditionId())
												.setUserId(user.getUserId())
												.setUserStudiesId(userStudyMap.get(user.getUserId()).get(0)
														.getUserStudiesId())
												.setActivityStatus(AppConstants.RUN_STATE_START).setActivityRunId(0)
												.setCompletedCount(0).setMissedCount(0).setTotalCount(0)
												.setLastCompletedRunId(0).setCreatedOn(AppUtil.getCurrentDateTime(user.getTimeZone()))
												.setCurrentRunDate(null).setExpireNotificationSent(false);;
												activityDao.saveOrUpdateUserActivities(saveUserActivity,
														AppConstants.DB_SAVE);
											}
										}
									}
								}	
								
							}
							
			
						}
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: AppServiceImpl - mapUserAndActivities()", e);
		}
		LOGGER.info("INFO: AppServiceImpl - mapUserAndActivities() :: ends");
	}

	/**
	 * Get the list of users to be notified about the new run available
	 * 
	 * @author Mohan
	 * @param activityConditionIds
	 *            the available activity identifier
	 * @param userStudyMap
	 *            the user study details map
	 * @param usersList
	 *            the active user
	 * @param triggerActivityConditionList
	 *            the activity condition details list
	 * @param temporalConditionMap
	 *            the temporal condition details
	 * @param userActivityMap
	 *            the user activity details map
	 * @param userAuthInfoMap
	 *            the user authinfo details map
	 * @return the list of user to be notified
	 * @throws ParseException
	 *             when exception is occured while parsing data
	 */
	private Map<Integer, List<PushNotificationBean>> checkUserActivityConditionPrerequisists(
			List<ThresholdConditionsDto> thresholdConditionsList , Map<Integer, List<UserStudiesDto>> userStudyMap, List<UserDto> usersList,
			List<ActivityConditionDto> triggerActivityConditionList,
			Map<Integer, List<TemporalConditionDto>> temporalConditionMap,
			Map<String, UserActivitiesDto> userActivityMap, Map<String, UserActivitiesRunsDto> userActivityRunMap,
			Map<Integer, ThresholdConditionDetailsBean> activeUserConditionsMap,
			Map<Integer, List<AuthInfoDto>> userAuthInfoMap, String timeZone) throws ParseException {
		LOGGER.info("INFO: AppServiceImpl - checkUserActivityConditionPrerequisists() :: starts");
		//List<ThresholdConditionsDto> thresholdConditionsList = new ArrayList<>();
		Map<Integer, List<PushNotificationBean>> pushNotificationMap = new TreeMap<>();
		Map<Integer, List<ThresholdConditionsDto>> thresholdConditionMap = new HashMap<>();
		try {

			/*thresholdConditionsList = activityDao.fetchThresholdConditionDetailsList(activityConditionIds,
					AppConstants.FIND_BY_TYPE_CONDITIONID);*/
			if (!thresholdConditionsList.isEmpty()) {
				String currentDate = AppUtil.getCurrentDateTime(timeZone);
				thresholdConditionMap = thresholdConditionsList.stream()
						.collect(Collectors.groupingBy(ThresholdConditionsDto::getConditionId));

				// check the user activity condition logic
				for (ActivityConditionDto activityCondition : triggerActivityConditionList) {
					TemporalConditionDto temporalCondition = temporalConditionMap
							.get(activityCondition.getConditionId()).get(0);
					List<PushNotificationBean> userPushNotificationBeanList = new ArrayList<>();

					for (UserDto user : usersList) {
						UserStudiesDto userStudies = userStudyMap.get(user.getUserId()).get(0);
						UserActivitiesDto userActivity = userActivityMap.get(user.getUserId() + "@"
								+ userStudies.getUserStudiesId() + "@" + activityCondition.getConditionId());

						if (userActivity != null && temporalCondition != null
								&& userActivity.getConditionId() == activityCondition.getConditionId()) {
							int anchorDays = temporalCondition.getAnchorDays();
							String startDate = AppUtil.addDays(userStudies.getCreatedOn(),AppConstants.SDF_DATE_TIME_FORMAT,anchorDays,
									AppConstants.SDF_DATE_TIME_FORMAT);
							String activityStartDate = temporalCondition.getStartDate() + " "
									+ temporalCondition.getStartTime();
							String endDate = temporalCondition.getEndDate() + " " + temporalCondition.getEndTime();
							String resultStartDate;

							// Check the activity is available or not for the user
							if (!SDF_DATE_TIME.parse(startDate)
									.before(SDF_DATE_TIME.parse(activityStartDate))) {
								resultStartDate = startDate;
							} else {
								resultStartDate = activityStartDate;
							}

							if ((!SDF_DATE_TIME.parse(currentDate)
									.before(SDF_DATE_TIME.parse(resultStartDate))
									&& !SDF_DATE_TIME.parse(endDate)
									.before(SDF_DATE_TIME.parse(resultStartDate))
									&& !SDF_DATE_TIME.parse(endDate)
									.before(SDF_DATE_TIME.parse(currentDate)))
									&& (userActivity.getCompletedCount() < activityCondition
											.getTotalParticipationTarget())) {
								ThresholdConditionDetailsBean conditions = activeUserConditionsMap
										.get(user.getUserId());

								boolean conditionLogicStatus = this.getConditionLogicStatus(
										thresholdConditionMap.get(activityCondition.getConditionId()), conditions);

								// Check the threshold condition logic is meet or not for the user
								if (conditionLogicStatus) {
									LOGGER.info("CONDITION LOGIC STATUS ::::::::::::: " + conditionLogicStatus);
									UserActivitiesRunsDto userActivityRun = userActivityRunMap
											.get(user.getUserId() + "@" + userStudies.getUserStudiesId() + "@"
													+ userActivity.getConditionId());
									boolean notifyUserFlag = false;
									notifyUserFlag = this.saveOrUpdateUserRunsDetailsByConditionLogic(activityCondition,
											user, userStudies, userActivity, userActivityRun, temporalCondition);
									if (notifyUserFlag) {
										PushNotificationBean pushNotificationBean = new PushNotificationBean()
												.setActivityId(activityCondition.getActivityConditionId())
												.setConditionId(String.valueOf(activityCondition.getConditionId()))
												.setCurrentRunId(String.valueOf(userActivity.getTotalCount() + 1))
												.setLanguage(user.getLanguage())
												.setStudyId(String.valueOf(userStudies.getStudyId()))
												.setUserId(String.valueOf(user.getUserId()))
												.setUserTimeZone(user.getTimeZone())
												.setDeviceToken(
														userAuthInfoMap.get(user.getUserId()).get(0).getDeviceToken())
												.setDeviceType(
														userAuthInfoMap.get(user.getUserId()).get(0).getDeviceType())
												.setActivityName(activityCondition.getActivityConditionName());
										userPushNotificationBeanList.add(pushNotificationBean);
									}
								}
							}
						}
					}

					// Get the users to be notified about the new run available
					if (!userPushNotificationBeanList.isEmpty()) {
						pushNotificationMap.put(activityCondition.getConditionId(), userPushNotificationBeanList);
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: AppServiceImpl - checkUserActivityConditionPrerequisists()", e);
		}
		LOGGER.info("INFO: AppServiceImpl - checkUserActivityConditionPrerequisists() :: ends");
		return pushNotificationMap;
	}

	/**
	 * Get the user activity run status flag
	 * 
	 * @author Mohan
	 * @param activityCondition
	 *            the {@link ActivityConditionDto} details
	 * @param user
	 *            the {@link UserDto} details
	 * @param userStudies
	 *            the {@link UserStudiesDto} details
	 * @param userActivity
	 *            the {@link UserActivitiesDto} details
	 * @param userActivityRun
	 *            the {@link UserActivitiesRunsDto} details
	 * @param temporalCondition
	 *            the {@link TemporalConditionDto} details
	 * @return the notify user status
	 * @throws CustomException
	 * @throws ParseException
	 */
	public boolean saveOrUpdateUserRunsDetailsByConditionLogic(ActivityConditionDto activityCondition, UserDto user,
			UserStudiesDto userStudies, UserActivitiesDto userActivity, UserActivitiesRunsDto userActivityRun,
			TemporalConditionDto temporalCondition) throws CustomException, ParseException {
		LOGGER.info("INFO: AppServiceImpl - saveOrUpdateUserRunsDetailsByConditionLogic() :: starts");
		Boolean notifyRunFlag = false;
		String currentDateTime = "";
		String currentDate = "";
		/*String userCurrentDateTime = "";*/
		try {

			// Check the user timezone start and end time for an triggered activity
			currentDateTime = AppUtil.getCurrentDateTime(user.getTimeZone());
			// currentDate = AppUtil.getCurrentDate();
			currentDate = AppUtil.getCurrentUserDate(user.getTimeZone());
			/*userCurrentDateTime = AppUtil.getCurrentDateTimeForUser(AppConstants.SDF_DATE_TIME_FORMAT,
					user.getTimeZone());*/
			
			boolean start = SDF_DATE_TIME.parse(currentDate + " " +temporalCondition.getStartTime()).before(SDF_DATE_TIME.parse(currentDateTime)) ? true : false;
            boolean end = SDF_DATE_TIME.parse(currentDate + " " + temporalCondition.getEndTime()).after(SDF_DATE_TIME.parse(currentDateTime)) ? true : false;
			boolean inStartEqual = SDF_DATE_TIME.parse(currentDate + " " +temporalCondition.getStartTime()).equals(SDF_DATE_TIME.parse(currentDateTime)) ? true : false;
				if ((start || inStartEqual) && end) {
				if (userActivityRun == null) {
					UserActivitiesRunsDto saveUserActivityRun = new UserActivitiesRunsDto();

					// Save user activity runs details
					saveUserActivityRun.setUserId(user.getUserId()).setUserStudiesId(userStudies.getUserStudiesId())
					.setConditionId(activityCondition.getConditionId()).setActivityRunId(1)
					.setRunStartsOn(currentDateTime)
					.setRunEndsOn(this.getActualRunEndsOn(temporalCondition, currentDate, currentDateTime,
							user.getTimeZone()))
					.setCreatedOn(currentDateTime).setRunState(AppConstants.RUN_STATE_START)
					.setExpireNotificationSent(false);
					activityDao.saveOrUpdateUserActivityRunsDetails(saveUserActivityRun, AppConstants.DB_SAVE);

					// Update user activities details
					userActivity.setActivityStatus(AppConstants.RUN_STATE_START).setTotalCount(1).setActivityRunId(1)
					.setMissedCount(0).setCompletedCount(0).setLastCompletedRunId(0).setLastCompletedDate(null)
					.setModifiedOn(currentDateTime);
					activityDao.saveOrUpdateUserActivities(userActivity, AppConstants.DB_UPDATE);

					notifyRunFlag = true;
				} else {

					if (userActivityRun.getUserId() != 0 && AppUtil.noOfDaysBetweenTwoDates(AppUtil.getFormattedDate(userActivityRun.getRunEndsOn(),
							AppConstants.SDF_DATE_TIME_FORMAT, AppConstants.SDF_DATE_FORMAT), currentDate) == 1) {
						if (!AppConstants.RUN_STATE_COMPLETED.equals(userActivityRun.getRunState())
								&& SDF_DATE_TIME.parse(currentDateTime)
								.after(SDF_DATE_TIME.parse(userActivityRun.getRunEndsOn()))) {

							// Update user activity runs details
							userActivityRun.setRunState(AppConstants.RUN_STATE_START).setModifiedOn(currentDateTime)
							.setRunStartsOn(currentDateTime)
							.setRunEndsOn(this.getActualRunEndsOn(temporalCondition, currentDate,
									currentDateTime, user.getTimeZone()))
							.setExpireNotificationSent(false);
							activityDao.saveOrUpdateUserActivityRunsDetails(userActivityRun, AppConstants.DB_UPDATE);

							// Update user activities details
							userActivity.setActivityStatus(AppConstants.RUN_STATE_START).setModifiedOn(currentDateTime);
							activityDao.saveOrUpdateUserActivities(userActivity, AppConstants.DB_UPDATE);

							notifyRunFlag = true;
						}
					} else {

						// Update user activity runs details
						userActivityRun.setUserId(user.getUserId()).setUserStudiesId(userStudies.getUserStudiesId())
						.setConditionId(activityCondition.getConditionId())
						.setActivityRunId(userActivity.getActivityRunId() + 1).setRunStartsOn(currentDateTime)
						.setRunEndsOn(this.getActualRunEndsOn(temporalCondition, currentDate, currentDateTime,
								user.getTimeZone()))
						.setCreatedOn(currentDateTime).setRunState(AppConstants.RUN_STATE_START);
						activityDao.saveOrUpdateUserActivityRunsDetails(userActivityRun, AppConstants.DB_UPDATE);

						// Update user activities details
						userActivity.setActivityStatus(AppConstants.RUN_STATE_START)
						.setTotalCount(userActivity.getTotalCount() + 1)
						.setActivityRunId(userActivity.getActivityRunId() + 1).setModifiedOn(currentDateTime);
						activityDao.saveOrUpdateUserActivities(userActivity, AppConstants.DB_UPDATE);

						notifyRunFlag = true;
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: AppServiceImpl - saveOrUpdateUserRunsDetailsByConditionLogic()", e);
		}
		LOGGER.info("INFO: AppServiceImpl - saveOrUpdateUserRunsDetailsByConditionLogic() :: ends");
		return notifyRunFlag;
	}

	/**
	 * Get the actual user activity run end date time
	 * 
	 * @author Mohan
	 * @param temporalCondition
	 *            the {@link TemporalConditionDto} details
	 * @param currentDate
	 *            the currentDate
	 * @param currentDateTime
	 *            the currentDateTime
	 * @param timeZone
	 *            the user timezone
	 * @return the actual ends on date time of activity run
	 * @throws ParseException
	 *             when failed to parse date time
	 */
	public String getActualRunEndsOn(TemporalConditionDto temporalCondition, String currentDate, String currentDateTime,
			String timeZone) throws ParseException {
		LOGGER.info("INFO: AppServiceImpl - getActualRunEndsOn() :: starts");
		String endsOn = "";
		String calEndsOn = "";
		String actualEndsOn = "";
		try {

			calEndsOn = AppUtil.addMinutes(currentDateTime, 60);
			actualEndsOn = currentDate + " " + temporalCondition.getEndTime();
			if (SDF_DATE_TIME
					.parse(AppUtil.convertDateTimeByTimeZone(calEndsOn, AppConstants.SDF_DATE_TIME_FORMAT,
							AppConstants.SDF_DATE_TIME_FORMAT, timeZone))
					.before(SDF_DATE_TIME.parse(actualEndsOn))) {
				endsOn = calEndsOn;
			} else {
				endsOn = AppUtil.convertDateTimeFromOneTimeZoneToAnotherTimeZone(actualEndsOn,
						AppConstants.SDF_DATE_TIME_FORMAT, AppConstants.SDF_DATE_TIME_FORMAT, timeZone,
						timeZone);
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: AppServiceImpl - getActualRunEndsOn()", e);
		}
		LOGGER.info("INFO: AppServiceImpl - getActualRunEndsOn() :: ends");
		return endsOn;
	}

	/**
	 * Get the condition logic status for the provided criteria
	 * 
	 * @author Mohan
	 * @param thresholdConditionList
	 *            the {@link ThresholdConditionsDto} details list
	 * @param conditions
	 *            the user conditions details
	 * @return the condition status
	 */
	public boolean getConditionLogicStatus(List<ThresholdConditionsDto> thresholdConditionList,
			ThresholdConditionDetailsBean conditions) {
		LOGGER.info("INFO: AppServiceImpl - getConditionLogicStatus() :: starts");
		boolean conditionLogicStatus = false;
		try {

			if (!thresholdConditionList.isEmpty()) {
				for (ThresholdConditionsDto thresholdCondition : thresholdConditionList) {
					switch (thresholdCondition.getThresholdId()) {
					case 1:
						conditionLogicStatus = this.getConditionStatus(thresholdCondition.getThresholdRange(),
								conditions.getCo(), thresholdCondition.getValue(), thresholdCondition.getMaxValue());
						break;
					case 2:
						conditionLogicStatus = this.getConditionStatus(thresholdCondition.getThresholdRange(),
								conditions.getTemperature(), thresholdCondition.getValue(),
								thresholdCondition.getMaxValue());
						break;
					case 3:
						conditionLogicStatus = this.getConditionStatus(thresholdCondition.getThresholdRange(),
								conditions.getRelativeHumidity(), thresholdCondition.getValue(),
								thresholdCondition.getMaxValue());
						break;
					case 4:
						conditionLogicStatus = this.getConditionStatus(thresholdCondition.getThresholdRange(),
								conditions.getNoise(), thresholdCondition.getValue(), thresholdCondition.getMaxValue());
						break;
					case 5:
						conditionLogicStatus = this.getConditionStatus(thresholdCondition.getThresholdRange(),
								conditions.getLight(), thresholdCondition.getValue(), thresholdCondition.getMaxValue());
						break;
					case 6:
						conditionLogicStatus = this.getConditionStatus(thresholdCondition.getThresholdRange(),
								conditions.getPm(), thresholdCondition.getValue(), thresholdCondition.getMaxValue());
						break;
					case 7:
						conditionLogicStatus = this.getConditionStatus(thresholdCondition.getThresholdRange(),
								conditions.getSteps(), thresholdCondition.getValue(), thresholdCondition.getMaxValue());
						break;
					case 8:
						conditionLogicStatus = this.getConditionStatus(thresholdCondition.getThresholdRange(),
								conditions.getHeartRate(), thresholdCondition.getValue(),
								thresholdCondition.getMaxValue());
						break;
					case 9:
						conditionLogicStatus = this.getConditionStatus(thresholdCondition.getThresholdRange(),
								conditions.getSleep(), thresholdCondition.getValue(), thresholdCondition.getMaxValue());
						break;
					default:
						break;
					}

					// Skip the loop if any one condition does not meet the threshold condition
					// criteria
					if (!conditionLogicStatus) {
						break;
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: AppServiceImpl - getConditionLogicStatus()", e);
		}
		LOGGER.info("INFO: AppServiceImpl - getConditionLogicStatus() :: starts");
		return conditionLogicStatus;
	}

	/**
	 * Get the condition status for the provided threshold condition details
	 * 
	 * @author Mohan
	 * @param rangeType
	 *            the threshold range type
	 * @param value
	 *            the condition value
	 * @param value
	 *            the condition min value
	 * @param maxValue
	 *            the condition maximum value
	 * @return the condition status
	 */
	public boolean getConditionStatus(String rangeType, String value, String minValue, String maxValue) {
		LOGGER.info("INFO: AppServiceImpl - getConditionStatus() :: starts");
		boolean conditionFlag = false;
		try {

			if (StringUtils.isNotEmpty(value)) {
				switch (rangeType) {
				case AppConstants.THRESHOLD_RANGE_LT:
					if (Double.parseDouble(value) < Double.parseDouble(minValue)) {
						conditionFlag = true;
					}
					break;
				case AppConstants.THRESHOLD_RANGE_GT:
					if (Double.parseDouble(value) > Double.parseDouble(minValue)) {
						conditionFlag = true;
					}
					break;
				case AppConstants.THRESHOLD_RANGE_BTW:
					if ((Double.parseDouble(value) >= Double.parseDouble(minValue))
							&& (Double.parseDouble(value) <= Double.parseDouble(maxValue))) {
						conditionFlag = true;
					}
					break;
				default:
					break;
				}
			}
		} catch (NumberFormatException e) {
			LOGGER.error("ERROR: AppServiceImpl - getConditionStatus()", e);
		}
		LOGGER.info("INFO: AppServiceImpl - getConditionStatus() :: ends");
		return conditionFlag;
	}

	@Override
	@Async
	public void sendTriggerredActivityRunsPushNotification(
			Map<Integer, List<PushNotificationBean>> pushNotificationMap) {
		LOGGER.info("INFO: AppServiceImpl - sendTriggerredActivityRunsPushNotification() :: starts");
		try {

			/**
			 * https://fcm.googleapis.com/fcm/send Content-Type:application/json
			 * Authorization:key=FIREBASE_SERVER_KEY
			 */
			/**
			 * { "notification": { "title": "JSA Notification", "body": "Happy Message!" },
			 * "data": { "Key-1": "JSA Data 1", "Key-2": "JSA Data 2" }, "to":
			 * "/topics/JavaSampleApproach", "priority": "high" }
			 */
			// Iterate the user push notification information details
			for (Entry<Integer, List<PushNotificationBean>> map : pushNotificationMap.entrySet()) {
				List<PushNotificationBean> userActivityNotificationList = map.getValue();
				if(userActivityNotificationList.isEmpty()){
					continue;
				}
				LOGGER.info("PUSH NOTIFICATION BEAN MAP " + pushNotificationMap);
				for (PushNotificationBean pushNotificationBean : userActivityNotificationList) {
					JSONObject body = new JSONObject();
					JSONObject notification = new JSONObject();
					JSONObject data = new JSONObject();

					// Create payload for push notification
					body.put(AppEnums.PN_PRIORITY.value(), AppEnums.PN_HIGH.value());
					body.put(AppEnums.PN_TO.value(), pushNotificationBean.getDeviceToken());

					data.put(AppEnums.PN_ACTIVITY_ID.value(), pushNotificationBean.getActivityId());
					data.put(AppEnums.PN_RUN_ID.value(), pushNotificationBean.getCurrentRunId());
					data.put(AppEnums.RP_STUDY_ID.value(), pushNotificationBean.getUserId());
					data.put(AppEnums.PN_N_TYPE.value(), AppEnums.PN_TRIGGER.value());

					if (AppConstants.PLATFORM_TYPE_ANDROID.equals(pushNotificationBean.getDeviceType())) {
						data = this.getNewRunNotificationBodyDetails(pushNotificationBean, data);
						body.put(AppEnums.PN_DATA.value(), data);
					} else {
						notification = this.getNewRunNotificationBodyDetails(pushNotificationBean, notification);
						notification.put(AppEnums.PN_CONTENT_AVAILABLE.value(), 1);
						body.put(AppEnums.PN_NOTIFICATION.value(), notification);
						body.put(AppEnums.PN_DATA.value(), data);
					}

					LOGGER.info("PUSH NOTIFICATION BEAN : " + pushNotificationBean.toString() + "\n BODY : "
							+ body.toString());

					// Send FCM push notification for the provided payload
					this.callFCMPushNotificationAPI(body);
				}
			}
		} catch (RestClientException | JSONException e) {
			LOGGER.error("ERROR: AppServiceImpl - sendTriggerredActivityRunsPushNotification()", e);
		}
		LOGGER.info("INFO: AppServiceImpl - sendTriggerredActivityRunsPushNotification() :: ends");
	}

	@Override
	public void sendFitbitAndLass4uSensorFailedNotification(
			List<FitbitLass4uApiResponseBean> fitbitLass4uApiResponseList) {
		LOGGER.info("INFO: AppServiceImpl - sendFitbitAndLass4uSensorFailedNotification() :: starts");
		try {

			if (!fitbitLass4uApiResponseList.isEmpty()) {
				// Iterate the user push notification information details
				for (FitbitLass4uApiResponseBean apiResponseBean : fitbitLass4uApiResponseList) {

					JSONObject body = new JSONObject();
					JSONObject notification = new JSONObject();
					JSONObject data = new JSONObject();

					// Create payload for push notification
					body.put(AppEnums.PN_PRIORITY.value(), AppEnums.PN_HIGH.value());
					body.put(AppEnums.PN_TO.value(), apiResponseBean.getDeviceToken());

					data.put(AppEnums.PN_N_TYPE.value(),
							apiResponseBean.getLass4uSensorStatus() ? "fetchLass4UFailure" : "fetchFitbitFailure");

					if (AppConstants.PLATFORM_TYPE_ANDROID.equals(apiResponseBean.getDeviceType())) {
						data = this.getFailedNotificationBodyDetails(apiResponseBean, data);
						body.put(AppEnums.PN_DATA.value(), data);
					} else {
						notification = this.getFailedNotificationBodyDetails(apiResponseBean, notification);
						notification.put(AppEnums.PN_CONTENT_AVAILABLE.value(), 1);
						body.put(AppEnums.PN_NOTIFICATION.value(), notification);
						body.put(AppEnums.PN_DATA.value(), data);
					}

					LOGGER.info(
							"PUSH NOTIFICATION BEAN : " + apiResponseBean.toString() + "\n BODY : " + body.toString());

					// Send FCM push notification for the provided payload
					this.callFCMPushNotificationAPI(body);
				}
			}
		} catch (RestClientException | JSONException e) {
			LOGGER.error("ERROR: AppServiceImpl - sendFitbitAndLass4uSensorFailedNotification()", e);
		}
		LOGGER.info("INFO: AppServiceImpl - sendFitbitAndLass4uSensorFailedNotification() :: ends");
	}

	/**
	 * Get new run notification request body details
	 * 
	 * @author Mohan
	 * @param pushNotificationBean
	 *            the {@link PushNotificationBean} details
	 * @param jsonObj
	 *            the jsonObj object
	 * @return the notification body details
	 */
	public JSONObject getNewRunNotificationBodyDetails(PushNotificationBean pushNotificationBean, JSONObject jsonObj) {
		LOGGER.info("INFO: AppServiceImpl - getNewRunNotificationBodyDetails() :: starts");
		Map<String, String> keyValuesMap = new HashMap<>();
		String titleText = "For Health";
		try {

			keyValuesMap.put(AppEnums.MKV_ACTIVITY_NAME.value(), pushNotificationBean.getActivityName());

			// push notification message based on the user language selection
			switch (pushNotificationBean.getLanguage()) {
			case AppConstants.USER_LANGUAGE_SPANISH:
				jsonObj.put(AppEnums.PN_TITLE.value(), titleText);
				jsonObj.put(
						AppConstants.PLATFORM_TYPE_ANDROID.equals(pushNotificationBean.getDeviceType())
						? AppEnums.PN_MESSAGE.value()
								: AppEnums.PN_BODY.value(),
								MailContent.generateMailContent(
										AppUtil.getAppProperties().get(AppConstants.PUSH_NOTIFICATION_MESSAGE_SPANISH),
										keyValuesMap));
				break;
			case AppConstants.USER_LANGUAGE_CHINESE:
				jsonObj.put(AppEnums.PN_TITLE.value(), titleText);
				jsonObj.put(
						AppConstants.PLATFORM_TYPE_ANDROID.equals(pushNotificationBean.getDeviceType())
						? AppEnums.PN_MESSAGE.value()
								: AppEnums.PN_BODY.value(),
								MailContent.generateMailContent(
										AppUtil.getAppProperties().get(AppConstants.PUSH_NOTIFICATION_MESSAGE_CHINESE),
										keyValuesMap));
				break;
			default:
				jsonObj.put(AppEnums.PN_TITLE.value(), titleText);
				jsonObj.put(
						AppConstants.PLATFORM_TYPE_ANDROID.equals(pushNotificationBean.getDeviceType())
						? AppEnums.PN_MESSAGE.value()
								: AppEnums.PN_BODY.value(),
								MailContent.generateMailContent(
										AppUtil.getAppProperties().get(AppConstants.PUSH_NOTIFICATION_MESSAGE_ENGLISH),
										keyValuesMap));
				break;
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: AppServiceImpl - getNewRunNotificationBodyDetails()", e);
		}
		LOGGER.info("INFO: AppServiceImpl - getNewRunNotificationBodyDetails() :: ends");
		return jsonObj;
	}

	/**
	 * Get failed notification request body details
	 * 
	 * @author Mohan
	 * @param apiResponseBean
	 *            the {@link FitbitLass4uApiResponseBean} details
	 * @param jsonObj
	 *            the jsonObj object
	 * @return the notification body details
	 */
	public JSONObject getFailedNotificationBodyDetails(FitbitLass4uApiResponseBean apiResponseBean,
			JSONObject jsonObj) {
		LOGGER.info("INFO: AppServiceImpl - getFailedNotificationBodyDetails() :: starts");
		try {
			// push notification message based on the user language selection
			switch (apiResponseBean.getLanguage()) {
			case AppConstants.USER_LANGUAGE_SPANISH:
				jsonObj.put(AppEnums.PN_TITLE.value(), (apiResponseBean.getLass4uSensorStatus()
						? AppUtil.getAppProperties().get(AppConstants.PUSH_NOTIFICATION_LASS4U_DATA_FAILED_SPANISH)
								: AppUtil.getAppProperties().get(AppConstants.PUSH_NOTIFICATION_FITBIT_DATA_FAILED_SPANISH)));
				jsonObj.put(
						AppConstants.PLATFORM_TYPE_ANDROID.equals(apiResponseBean.getDeviceType())
						? AppEnums.PN_MESSAGE.value()
								: AppEnums.PN_BODY.value(),
								(apiResponseBean.getLass4uSensorStatus()
										? AppUtil.getAppProperties()
												.get(AppConstants.PUSH_NOTIFICATION_LASS4U_DATA_FAILED_SPANISH)
												: AppUtil.getAppProperties()
												.get(AppConstants.PUSH_NOTIFICATION_FITBIT_DATA_FAILED_SPANISH)));
				break;
			case AppConstants.USER_LANGUAGE_CHINESE:
				jsonObj.put(AppEnums.PN_TITLE.value(), (apiResponseBean.getLass4uSensorStatus()
						? AppUtil.getAppProperties().get(AppConstants.PUSH_NOTIFICATION_LASS4U_DATA_FAILED_CHINESE)
								: AppUtil.getAppProperties().get(AppConstants.PUSH_NOTIFICATION_FITBIT_DATA_FAILED_CHINESE)));
				jsonObj.put(
						AppConstants.PLATFORM_TYPE_ANDROID.equals(apiResponseBean.getDeviceType())
						? AppEnums.PN_MESSAGE.value()
								: AppEnums.PN_BODY.value(),
								(apiResponseBean.getLass4uSensorStatus()
										? AppUtil.getAppProperties()
												.get(AppConstants.PUSH_NOTIFICATION_LASS4U_DATA_FAILED_CHINESE)
												: AppUtil.getAppProperties()
												.get(AppConstants.PUSH_NOTIFICATION_FITBIT_DATA_FAILED_CHINESE)));
				break;
			default:
				jsonObj.put(AppEnums.PN_TITLE.value(), (apiResponseBean.getLass4uSensorStatus()
						? AppUtil.getAppProperties().get(AppConstants.PUSH_NOTIFICATION_LASS4U_DATA_FAILED_ENGLISH)
								: AppUtil.getAppProperties().get(AppConstants.PUSH_NOTIFICATION_FITBIT_DATA_FAILED_ENGLISH)));
				jsonObj.put(
						AppConstants.PLATFORM_TYPE_ANDROID.equals(apiResponseBean.getDeviceType())
						? AppEnums.PN_MESSAGE.value()
								: AppEnums.PN_BODY.value(),
								(apiResponseBean.getLass4uSensorStatus()
										? AppUtil.getAppProperties()
												.get(AppConstants.PUSH_NOTIFICATION_LASS4U_DATA_FAILED_ENGLISH)
												: AppUtil.getAppProperties()
												.get(AppConstants.PUSH_NOTIFICATION_FITBIT_DATA_FAILED_ENGLISH)));
				break;
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: AppServiceImpl - getFailedNotificationBodyDetails()", e);
		}
		LOGGER.info("INFO: AppServiceImpl - getFailedNotificationBodyDetails() :: ends");
		return jsonObj;
	}

	/**
	 * Call FCM API to send notification to the users
	 * 
	 * @author Mohan
	 * @param body
	 *            the FCM payload details
	 */
	public void callFCMPushNotificationAPI(JSONObject body) {
		LOGGER.info("INFO: AppServiceImpl - callFCMPushNotificationAPI() :: starts");
		String firebaseResponse;
		try {

			HttpEntity<Object> entity = new HttpEntity<>(body.toString());
			ArrayList<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
			interceptors.add(new HeaderRequestInterceptor(AppConstants.HEADER_KEY_AUTHORIZATION,
					"key=" + AppUtil.getAppProperties().get(AppConstants.FIREBASE_SERVER_KEY)));
			interceptors.add(new HeaderRequestInterceptor(AppConstants.HEADER_KEY_CONTENT_TYPE,
					MediaType.APPLICATION_JSON_UTF8_VALUE));
			restTemplate.setInterceptors(interceptors);

			firebaseResponse = restTemplate.postForObject(AppUtil.getAppProperties().get(AppConstants.FIREBASE_API_URL),
					entity, String.class);
			LOGGER.info("firebaseResponse>>>>>"+firebaseResponse);
			CompletableFuture<String> pushNotification = CompletableFuture.completedFuture(firebaseResponse);
			CompletableFuture.allOf(pushNotification).join();
		} catch (Exception e) {
			LOGGER.error("ERROR: AppServiceImpl - callFCMPushNotificationAPI() - Failed to send FCM push notification",
					e);
		}
		LOGGER.info("INFO: AppServiceImpl - callFCMPushNotificationAPI() :: ends");
	}

	/**
	 * Get the Fitbit and LASS4U sensor details for the provided criteria
	 * 
	 * @author Mohan
	 * @param userStudyMap
	 *            the user study map details
	 * @param user
	 *            the {@link UserDto} details
	 * @param conditionsMap
	 *            the conditions map details
	 * @param fitbitLass4UDataDto
	 *            the {@link FitbitLass4UDataDto} details
	 * @return the updated {@link FitbitLass4UDataDto} details
	 */
	public FitbitLass4UDataDto saveFitbitLass4uData(Map<Integer, List<UserStudiesDto>> userStudyMap, UserDto user,
			ThresholdConditionDetailsBean conditionsMap, FitbitLass4UDataDto fitbitLass4UDataDto) {
		LOGGER.info("INFO: AppServiceImpl - saveFitbitLass4uData() :: starts");
		try {
			if (fitbitLass4UDataDto != null) {
				fitbitLass4UDataDto.setCo2(conditionsMap.getCo())
				.setCurrentHeartRate(conditionsMap.getIntraDayHeartRate())
				.setEnrollmentId(userStudyMap.get(user.getUserId()).get(0).getEnrollmentId())
				.setHeartRate(conditionsMap.getHeartRate()).setLight(conditionsMap.getLight())
				.setNoise(conditionsMap.getNoise()).setPm25(conditionsMap.getPm())
				.setRelativeHumidity(conditionsMap.getRelativeHumidity())
				.setRestingHeartRate(conditionsMap.getRestingHeartRate()).setSleep(conditionsMap.getSleep())
				.setStepsCount(conditionsMap.getSteps()).setTemperature(conditionsMap.getTemperature())
				.setCreatedOn(AppUtil.getCurrentDateTime(user.getTimeZone()));
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: AppServiceImpl - saveFitbitLass4uData()", e);
		}
		LOGGER.info("INFO: AppServiceImpl - saveFitbitLass4uData() :: ends");
		return fitbitLass4UDataDto;
	}

	public static Retrofit getClient() {
		if (retrofit == null) {
			OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();
			retrofit = new Retrofit.Builder().baseUrl(BASE_URL).client(okHttpClient)
					.addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create()).build();
		}
		return retrofit;
	}

	interface Lass4uApiInterface {
		@GET("MAPS/API/last.php?")
		Call<Lass4UBean> getLass4uData(@Header("Authorization") String token, @Query("device_id") String id);
	}

	private static final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
		@Override
		public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
				throws CertificateException {
		}

		@Override
		public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
				throws CertificateException {
		}

		@Override
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return new java.security.cert.X509Certificate[] {};
		}
	} };
	private static final SSLContext trustAllSslContext;
	static {
		try {
			trustAllSslContext = SSLContext.getInstance("SSL");
			trustAllSslContext.init(null, trustAllCerts, new java.security.SecureRandom());
		} catch (NoSuchAlgorithmException | KeyManagementException e) {
			throw new RuntimeException(e);
		}
	}
	private static final SSLSocketFactory trustAllSslSocketFactory = trustAllSslContext.getSocketFactory();

	public static OkHttpClient trustAllSslClient(OkHttpClient client) {
		okhttp3.OkHttpClient.Builder builder = client.newBuilder();
		builder.sslSocketFactory(trustAllSslSocketFactory, (X509TrustManager) trustAllCerts[0]);
		builder.hostnameVerifier(new HostnameVerifier() {
			@Override
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		});
		return builder.build();
	}

	@SuppressWarnings("unlikely-arg-type")
	@Override
	//@Scheduled(cron = "* * * ? * *")
	public void sendExpiryPushNotifications() {
		LOGGER.info("INFO: AppServiceImpl - sendExpiryPushNotifications() :: starts");
		List<UserDto> activeUsersList = new ArrayList<>();
		List<String> timeZoneList = new ArrayList<>();
		try {
			activeUsersList = userDao.findAllActiveLoggedInUsers();
			timeZoneList = userDao.getAllTimeZoneList();
			//timeZoneList.add("Asia/Kolkata");
			if (null != activeUsersList && !activeUsersList.isEmpty() && null != timeZoneList && !timeZoneList.isEmpty()) {
			for(String timeZone : timeZoneList) {
				LOGGER.info("timeZone: "+timeZone);
				StringBuilder users = null;
				List<PushNotificationBean> pushNotificationBeanList = null;
				List<PushNotificationBean> pushNotificationBeanListAll = null;
				//List<TemporalConditionDto> temporalConditionsList = new ArrayList<>();
				//Map<Integer, List<TemporalConditionDto>> temporalConditionMap = new HashMap<>();
				List<UserDto> userTimeZoneList = new ArrayList<>();
				//List<Object> activityConditionIds = new ArrayList<>();
				//List<ActivityConditionDto> activityConditionDtoList = new ArrayList<>();
				//Map<Integer, List<ActivityConditionDto>> activityConditionMap = new HashMap<>();
				AuthInfoDto authInfoDto = null;
				TemporalConditionDto temporalCondition = null;
				ActivityConditionDto activityConditionDto = null;
				userTimeZoneList = activeUsersList.stream().filter(x -> timeZone.equals(x.getTimeZone()))
						.collect(Collectors.toList());
				for (UserDto user : userTimeZoneList) {
					if(null == users) {
						users = new StringBuilder().append("'"+user.getUserId()+"'");
					}else {
						users = users.append(",").append("'"+user.getUserId()+"'");
					}
				}
				String currentDateTime = AppUtil.getCurrentDateTime(timeZone);
				String currentDate = AppUtil.getCurrentUserDate(timeZone);
				if(null != users) {
					pushNotificationBeanListAll = activityDao.getExpiryUserActivities(users.toString());
					if(null != pushNotificationBeanListAll && !pushNotificationBeanListAll.isEmpty()) {
						/*activityConditionIds = pushNotificationBeanListAll.stream().map(PushNotificationBean::getConditionId)
								.collect(Collectors.toList());*/
						/*temporalConditionsList = activityDao.fetchTemporalConditionDetailsList(activityConditionIds,
								AppConstants.FIND_BY_TYPE_CONDITIONID);
						if(null != temporalConditionsList && !temporalConditionsList.isEmpty()) {
							temporalConditionMap = temporalConditionsList.stream()
									.collect(Collectors.groupingBy(TemporalConditionDto::getConditionId));
						}*/
						/*activityConditionDtoList = activityDao
								.fetchActivityConditionDetailsList(activityConditionIds, AppConstants.FIND_BY_TYPE_CONDITIONIDS);
						if(null != activityConditionDtoList && !activityConditionDtoList.isEmpty()) {

							activityConditionMap = activityConditionDtoList.stream()
									.collect(Collectors.groupingBy(ActivityConditionDto::getConditionId));
						}*/
						pushNotificationBeanList = new ArrayList<>();
						for (PushNotificationBean pushNotificationBean : pushNotificationBeanListAll) {
							authInfoDto = userDao.fetchAuthInfoDetails(pushNotificationBean.getUserId(), FIND_BY_TYPE_USERID);
							temporalCondition = activityDao.
									fetchTemporalConditionDetails(pushNotificationBean.getConditionId(),AppConstants.FIND_BY_TYPE_CONDITIONID);
							activityConditionDto = activityDao
									.fetchActivityConditionDetails(pushNotificationBean.getConditionId(),AppConstants.FIND_BY_TYPE_CONDITIONID);
							if(null != temporalCondition && null != activityConditionDto && null != authInfoDto) {
								//System.out.println(String.valueOf((AppConstants.SDF_DATE_TIME.parse(currentDate + " " +temporalCondition.getEndTime())).getMinutes()-(AppConstants.SDF_DATE_TIME.parse(currentDateTime)).getMinutes()));

								//TemporalConditionDto temporalCondition = temporalConditionMap.get(pushNotificationBean.getConditionId()).get(0);
								boolean run_check = (SDF_DATE_TIME.parse(currentDate + " " +temporalCondition.getEndTime())).after(SDF_DATE_TIME.parse(AppUtil.addMinutes(currentDateTime,15)))? false : true;
								boolean checkIntervalTime = (SDF_DATE_TIME.parse(currentDate + " " +temporalCondition.getEndTime())).getMinutes()-(SDF_DATE_TIME.parse(currentDateTime)).getMinutes()-1 < 15 && (SDF_DATE_TIME.parse(currentDate + " " +temporalCondition.getEndTime())).getMinutes()-(SDF_DATE_TIME.parse(currentDateTime)).getMinutes()-1 >= 10;
								;
								boolean start = SDF_DATE_TIME.parse(temporalCondition.getStartDate() + " " +temporalCondition.getStartTime()).before(SDF_DATE_TIME.parse(currentDateTime)) ? true : false;
					            boolean end = SDF_DATE_TIME.parse(temporalCondition.getEndDate() + " " + temporalCondition.getEndTime()).after(SDF_DATE_TIME.parse(currentDateTime)) ? true : false;
								boolean inStartEqual = SDF_DATE_TIME.parse(currentDate + " " +temporalCondition.getStartTime()).equals(SDF_DATE_TIME.parse(currentDateTime)) ? true : false;
								UserActivitiesDto userActivity = activityDao.findByUserIdConditionId(activityConditionDto.getConditionId(),authInfoDto.getUserId(), temporalCondition.getStartDate() + " " +temporalCondition.getStartTime());

								if((start || inStartEqual) && end && run_check && checkIntervalTime && userActivity!=null && !userActivity.getActivityStatus().equals(AppConstants.RUN_STATE_COMPLETED) && !pushNotificationBean.getActivityStatus().equals(AppConstants.RUN_STATE_COMPLETED)) {
									pushNotificationBean.setActivityId(String.valueOf(activityConditionDto.getActivityId()));
									pushNotificationBean.setDeviceToken(authInfoDto.getDeviceToken());
									pushNotificationBean.setDeviceType(authInfoDto.getDeviceType());
									pushNotificationBean.setActivityId(String.valueOf(activityConditionDto.getActivityConditionName()));
									//pushNotificationBean.setExpiryMinutes(String.valueOf((AppConstants.SDF_DATE_TIME.parse(currentDate + " " +temporalCondition.getEndTime())).getMinutes()-(AppConstants.SDF_DATE_TIME.parse(currentDateTime)).getMinutes()-1));
									if(SDF_DATE_TIME.parse(currentDateTime).getMinutes() >
										SDF_DATE_TIME.parse(currentDate + " " +temporalCondition.getEndTime()).getMinutes()) {
											pushNotificationBean.setExpiryMinutes
											(String.valueOf((SDF_DATE_TIME.parse(currentDate + " " +temporalCondition.getEndTime())).getMinutes()-(SDF_DATE_TIME.parse(currentDateTime)).getMinutes()));

									}else {
										pushNotificationBean.setExpiryMinutes(String.valueOf((SDF_DATE_TIME.parse(currentDate + " " +temporalCondition.getEndTime())).getMinutes()-(SDF_DATE_TIME.parse(currentDateTime)).getMinutes()));

									}
									pushNotificationBeanList.add(pushNotificationBean);
									userActivity.setActivityStatus(AppConstants.RUN_STATE_COMPLETED);
									activityDao.saveOrUpdateUserActivities(userActivity, AppConstants.DB_UPDATE);
								}
							}
						}
					}
					if(null != pushNotificationBeanList && !pushNotificationBeanList.isEmpty()) {
						for (PushNotificationBean pushNotificationBean : pushNotificationBeanList.stream().collect(Collectors.toMap(x -> x.getConditionId(),x->x, (x1, x2) -> x1)).values()) {
							JSONObject body = new JSONObject();
							JSONObject notification = new JSONObject();
							JSONObject data = new JSONObject();

							body.put(AppEnums.PN_PRIORITY.value(), AppEnums.PN_HIGH.value());
							body.put(AppEnums.PN_TO.value(), pushNotificationBean.getDeviceToken());

							data.put(AppEnums.PN_ACTIVITY_ID.value(), pushNotificationBean.getActivityId());
							data.put(AppEnums.PN_RUN_ID.value(), pushNotificationBean.getCurrentRunId());
							data.put(AppEnums.RP_STUDY_ID.value(), pushNotificationBean.getUserId());
							data.put(AppEnums.PN_N_TYPE.value(), AppEnums.PN_TRIGGER.value());

							if (AppConstants.PLATFORM_TYPE_ANDROID.equals(pushNotificationBean.getDeviceType())) {
								data = this.getExpiryNotificationBodyDetails(pushNotificationBean, data);
								body.put(AppEnums.PN_DATA.value(), data);
							} else {
								notification = this.getExpiryNotificationBodyDetails(pushNotificationBean, notification);
								notification.put(AppEnums.PN_CONTENT_AVAILABLE.value(), 1);
								body.put(AppEnums.PN_NOTIFICATION.value(), notification);
								body.put(AppEnums.PN_DATA.value(), data);
							}

							LOGGER.info("PUSH NOTIFICATION BEAN : " + pushNotificationBean.toString() + "\n BODY : "
									+ body.toString());

							this.callFCMPushNotificationAPI(body);
							//activityDao.saveOrUpdateUserActivityRunsDetail(pushNotificationBean.getUserActivityRunId());
							activityDao.saveOrUpdateUserActivityDetail(pushNotificationBean.getUserActivityRunId());
						}
					}
				}
			}
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: AppServiceImpl - sendExpiryPushNotifications()", e);
		}
		LOGGER.info("INFO: AppServiceImpl - sendExpiryPushNotifications() :: ends");
	}

	public JSONObject getExpiryNotificationBodyDetails(PushNotificationBean pushNotificationBean, JSONObject jsonObj) {
		LOGGER.info("INFO: AppServiceImpl - getExpiryNotificationBodyDetails() :: starts");
		Map<String, String> keyValuesMap = new HashMap<>();
		String titleText = "For Health";
		try {

			keyValuesMap.put(AppEnums.MKV_ACTIVITY_NAME.value(), pushNotificationBean.getActivityName());
			keyValuesMap.put(AppEnums.MKV_RUN_END_TIME.value(), pushNotificationBean.getExpiryMinutes());

			// push notification message based on the user language selection
			switch (pushNotificationBean.getLanguage()) {
			case AppConstants.USER_LANGUAGE_SPANISH:
				jsonObj.put(AppEnums.PN_TITLE.value(), titleText);
				jsonObj.put(
						AppConstants.PLATFORM_TYPE_ANDROID.equals(pushNotificationBean.getDeviceType())
						? AppEnums.PN_MESSAGE.value()
								: AppEnums.PN_BODY.value(),
								MailContent.generateMailContent(
										AppUtil.getAppProperties().get(AppConstants.PUSH_EXPIRY_NOTIFICATION_MESSAGE_SPANISH),
										keyValuesMap));
				break;
			case AppConstants.USER_LANGUAGE_CHINESE:
				jsonObj.put(AppEnums.PN_TITLE.value(), titleText);
				jsonObj.put(
						AppConstants.PLATFORM_TYPE_ANDROID.equals(pushNotificationBean.getDeviceType())
						? AppEnums.PN_MESSAGE.value()
								: AppEnums.PN_BODY.value(),
								MailContent.generateMailContent(
										AppUtil.getAppProperties().get(AppConstants.PUSH_EXPIRY_NOTIFICATION_MESSAGE_CHINESE),
										keyValuesMap));
				break;
			default:
				jsonObj.put(AppEnums.PN_TITLE.value(), titleText);
				jsonObj.put(
						AppConstants.PLATFORM_TYPE_ANDROID.equals(pushNotificationBean.getDeviceType())
						? AppEnums.PN_MESSAGE.value()
								: AppEnums.PN_BODY.value(),
								MailContent.generateMailContent(
										AppUtil.getAppProperties().get(AppConstants.PUSH_EXPIRY_NOTIFICATION_MESSAGE_ENGLISH),
										keyValuesMap));
				break;
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: AppServiceImpl - getExpiryNotificationBodyDetails()", e);
		}
		LOGGER.info("INFO: AppServiceImpl - getExpiryNotificationBodyDetails() :: ends");
		return jsonObj;
	}

	@Override
	public AppVersionInfoBean getAppVersionInfo() {
		LOGGER.info("INFO: AppServiceImpl - getExpiryNotificationBodyDetails() :: starts");

		AppVersionInfoBean aAppVersionInfoBean = new AppVersionInfoBean();
		AppVersionInfo appVersionInfo = null;
		DeviceVersion android = new DeviceVersion();
		DeviceVersion ios = new DeviceVersion();

		appVersionInfo = activityDao.getAppVersionInfo();

		android.setLatestVersion(appVersionInfo.getAndroidVersion());
		android.setForceUpdate("true");

		ios.setForceUpdate("true");
		ios.setLatestVersion(appVersionInfo.getIosVersion());

		aAppVersionInfoBean.setAndroid(android);
		aAppVersionInfoBean.setIos(ios);

		LOGGER.info("INFO: AppServiceImpl - getExpiryNotificationBodyDetails() :: ends");
		return aAppVersionInfoBean;
	}
	
	/**
	 * Get the user activity run status flag
	 * 
	 * @author Fathima
	 * @param activityCondition
	 *            the {@link ActivityConditionDto} details
	 * @param user
	 *            the {@link UserDto} details
	 * @param userStudies
	 *            the {@link UserStudiesDto} details
	 * @param userActivity
	 *            the {@link UserActivitiesDto} details
	 * @param userActivityRun
	 *            the {@link UserActivitiesRunsDto} details
	 * @param temporalCondition
	 *            the {@link TemporalConditionDto} details
	 * @return the notify user status
	 * @throws CustomException
	 * @throws ParseException
	 */
	public boolean saveOrUpdateUserRunsDetailsByConditionLogicScheduledActivities(ActivityConditionDto activityCondition, UserDto user,
			UserStudiesDto userStudies, UserActivitiesDto userActivity, UserActivitiesRunsDto userActivityRun,
			TemporalConditionDto temporalCondition) throws CustomException, ParseException {
		LOGGER.info("INFO: AppServiceImpl - saveOrUpdateUserRunsDetailsByConditionLogicScheduledActivities() :: starts");
		Boolean notifyRunFlag = false;
		String currentDateTime = "";
		String currentDate = "";
		/*String userCurrentDateTime = "";*/
		try {

			// Check the user timezone start and end time for an triggered activity
			currentDateTime = AppUtil.getCurrentDateTime(user.getTimeZone());
			// currentDate = AppUtil.getCurrentDate();
			currentDate = AppUtil.getCurrentUserDate(user.getTimeZone());
			/*userCurrentDateTime = AppUtil.getCurrentDateTimeForUser(AppConstants.SDF_DATE_TIME_FORMAT,
					user.getTimeZone());*/
			
			boolean start = SDF_DATE_TIME.parse(currentDate + " " +temporalCondition.getStartTime()).before(SDF_DATE_TIME.parse(currentDateTime)) ? true : false;
            boolean end = SDF_DATE_TIME.parse(currentDate + " " + temporalCondition.getEndTime()).after(SDF_DATE_TIME.parse(currentDateTime)) ? true : false;
			boolean inStartEqual = SDF_DATE_TIME.parse(currentDate + " " +temporalCondition.getStartTime()).equals(SDF_DATE_TIME.parse(currentDateTime)) ? true : false;
			boolean notifyDateTimeCheck = (SDF_DATE_TIME.parse(currentDateTime)).before(SDF_DATE_TIME.parse(AppUtil.addMinutes((currentDate + " " +temporalCondition.getStartTime()),15)))? true : false;
				if ((start || inStartEqual) && end && notifyDateTimeCheck && !userActivity.getActivityStatus().equals(AppConstants.RUN_STATE_COMPLETED)) {
				
					notifyRunFlag = true;
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: AppServiceImpl - saveOrUpdateUserRunsDetailsByConditionLogicScheduledActivities()", e);
		}
		LOGGER.info("INFO: AppServiceImpl - saveOrUpdateUserRunsDetailsByConditionLogicScheduledActivities() :: ends");
		return notifyRunFlag;
	}
	
	/**
	 * Provides the scheduled user activity runs for the activity condition details and user
	 * identifier
	 * 
	 * @author Fathima
	 * @param userStudyMap
	 *            the user study details for the provided user identifier
	 * @param usersList
	 *            the active users details list
	 * @param userAuthInfoMap
	 *            the user authinfo details map
	 * @return
	 */
	public Map<Integer, List<PushNotificationBean>> getScheduledActivityRunsByUserAndActivityConditionDetails(
			Map<Integer, List<UserStudiesDto>> userStudyMap, List<UserDto> usersList,
			Map<Integer, List<AuthInfoDto>> userAuthInfoMap, String timeZone, List<ActivityConditionDto> scheduledActivityConditionList,
			Map<String, List<EnrollmentTokensDto>> userEnrollmentMap, List<ActivityGroupDto> activityGroupList,
			Map<Integer, List<GroupUsersInfoDto>> groupUsersIdMap, List<TemporalConditionDto> temporalConditionsList) {
		LOGGER.info("INFO: AppServiceImpl - getScheduledActivityRunsByUserAndActivityConditionDetails() :: starts");
		//List<ActivityConditionDto> scheduledActivityConditionList = new ArrayList<>();
		List<Object> activityConditionIds = new ArrayList<>();
		//List<TemporalConditionDto> temporalConditionsList = new ArrayList<>();
		Map<Integer, List<TemporalConditionDto>> temporalConditionMap = new HashMap<>();
		Map<String, UserActivitiesDto> userActivityMap = new HashMap<>();
		Map<String, UserActivitiesRunsDto> userActivityRunMap = new HashMap<>();
		List<EnrollmentTokensDto> enrollmentTokensList = new ArrayList<>();
		//Map<String, List<EnrollmentTokensDto>> userEnrollmentMap = new HashMap<>();
		// List<ActivityGroupDto> activityGroupList = new ArrayList<>();
		Map<Integer, List<ActivityGroupDto>> activityGroupMap = new HashMap<>();
		Map<Integer, List<PushNotificationBean>> pushNotificationMap = new TreeMap<>();
		Map<Integer, List<ActivityConditionDto>> activityConditionMap = new HashMap<>();
    //  Map<Integer, List<GroupUsersInfoDto>> groupUsersIdMap = new HashMap<>();
        List<Integer> groupIdsList = new ArrayList<>();
        List<GroupUsersInfoDto> groupUsersInfoDto = new ArrayList<>();
		try {

			/*scheduledActivityConditionList = activityDao.fetchActivityConditionDetailsList(
					AppConstants.ACTIVITY_SUB_TYPE_SCHEDULED, AppConstants.FIND_BY_TYPE_ACTIVITY_SUB_TYPE, timeZone);*/
			if (!scheduledActivityConditionList.isEmpty()) {
				activityConditionIds = scheduledActivityConditionList.stream().map(ActivityConditionDto::getConditionId)
						.collect(Collectors.toList());
				activityConditionMap = scheduledActivityConditionList.stream()
						.collect(Collectors.groupingBy(ActivityConditionDto::getConditionId));
				/*enrollmentTokensList = studyDao.fetchAllEnrollmentDetailsList();
				userEnrollmentMap = enrollmentTokensList.parallelStream()
						.collect(Collectors.groupingBy(EnrollmentTokensDto::getEnrollmentId));*/

				/*activityGroupList = studyDao.fetchAllActivityGroupList(activityConditionIds,
						AppConstants.FIND_BY_TYPE_CONDITIONID);*/
				activityGroupMap = activityGroupList.parallelStream()
						.collect(Collectors.groupingBy(ActivityGroupDto::getGroupId));
		/*		groupUsersInfoDto =  studyDao.fetchAllGroupUsersInfoDetails();
				groupUsersIdMap = groupUsersInfoDto.parallelStream()
				.collect(Collectors.groupingBy(GroupUsersInfoDto::getUserId));*/
				
				groupIdsList = activityGroupList.stream().map(ActivityGroupDto::getGroupId).collect(Collectors.toList());
				
				/*temporalConditionsList = activityDao.fetchTemporalConditionDetailsList(activityConditionIds,
						AppConstants.FIND_BY_TYPE_CONDITIONID);*/
				temporalConditionMap = temporalConditionsList.stream()
						.collect(Collectors.groupingBy(TemporalConditionDto::getConditionId));
				// Map if any new triggered activities are added from Qualtrics

					this.mapUserAndActivities(userStudyMap, usersList, scheduledActivityConditionList, temporalConditionMap,
							userEnrollmentMap, activityGroupMap, groupIdsList, groupUsersIdMap,activitiesList);
				

				for (UserDto user : usersList) {
					String userId = String.valueOf(user.getUserId());
					String userStudiesId = String.valueOf(userStudyMap.get(user.getUserId()).get(0).getUserStudiesId());

					// Get the user activities details
					List<UserActivitiesDto> userActivityList = activityDao.fetchUserScheduledActivityDetailsList(userId,
							userStudiesId, AppConstants.FIND_BY_TYPE_USERID_STUDYID_ACTIVITY_SUB_TYPE, user.getTimeZone());
					for (UserActivitiesDto userActivitiesDto : userActivityList) {
						boolean mapped = false;
						// Check the user has meet the participation target count or not for the
						// activity condition
						mapped = activityDao.checkUserRelatedToCorrectGroup(user.getUserId(),
								userActivitiesDto.getConditionId(), user.getGroupId(), groupUsersIdMap);
						if (mapped) {
							if (userActivitiesDto.getCompletedCount() < activityConditionMap
									.get(userActivitiesDto.getConditionId()).get(0).getTotalParticipationTarget()) {
								UserActivitiesRunsDto userActivityRun = activityDao.fetchUserActivityRunsDetails(userId,
										userStudiesId, String.valueOf(userActivitiesDto.getConditionId()), null);
								userActivityMap.put(
										userId + "@" + userStudiesId + "@" + userActivitiesDto.getConditionId(),
										userActivitiesDto);
								userActivityRunMap.put(
										userId + "@" + userStudiesId + "@" + userActivitiesDto.getConditionId(),
										userActivityRun);
							}
						}
					}
				}

				pushNotificationMap = this.checkUserScheduledActivityConditionPrerequisists(temporalConditionsList, userStudyMap,
						usersList,scheduledActivityConditionList, temporalConditionMap, userActivityMap,
						userActivityRunMap, userAuthInfoMap, timeZone);
			}

		} catch (Exception e) {
			LOGGER.error("ERROR: AppServiceImpl - getScheduledActivityRunsByUserAndActivityConditionDetails()", e);
		}
		LOGGER.info("INFO: AppServiceImpl - getScheduledActivityRunsByUserAndActivityConditionDetails() :: ends");
		return pushNotificationMap;
	}
	
	
	/**
	 * Get the list of users to be notified about the new run available for scheduled activities
	 * 
	 * @author Fathima
	 * @param userStudyMap
	 *            the user study details map
	 * @param usersList
	 *            the active user
	 * @param scheduledActivityConditionList
	 *            the activity condition details list
	 * @param temporalConditionMap
	 *            the temporal condition details
	 * @param userActivityMap
	 *            the user activity details map
	 * @param pushNotificationMap
	 *            the user activity runs details map
	 * @param userAuthInfoMap
	 *            the user authinfo details map
	 * @return the list of user to be notified
	 * @throws ParseException
	 *             when exception is occured while parsing data
	 */
	private Map<Integer, List<PushNotificationBean>> checkUserScheduledActivityConditionPrerequisists(
			List<TemporalConditionDto> temporalConditionsList, Map<Integer, List<UserStudiesDto>> userStudyMap, List<UserDto> usersList,
			List<ActivityConditionDto> scheduledActivityConditionList,
			Map<Integer, List<TemporalConditionDto>> temporalConditionMap,
			Map<String, UserActivitiesDto> userActivityMap, Map<String, UserActivitiesRunsDto> userActivityRunMap,
			Map<Integer, List<AuthInfoDto>> userAuthInfoMap, String timeZone) throws ParseException {
		LOGGER.info("INFO: AppServiceImpl - checkUserScheduledActivityConditionPrerequisists() :: starts");
		//List<TemporalConditionDto> temporalConditionsList = new ArrayList<>();
		Map<Integer, List<PushNotificationBean>> pushNotificationMap = new TreeMap<>();
		String[] days = null;
		String day = null;
		boolean dayCheck = false;
		try {

			/*temporalConditionsList = activityDao.fetchTemporalConditionDetailsList(activityConditionIds,
					AppConstants.FIND_BY_TYPE_CONDITIONID);*/
			if (!temporalConditionsList.isEmpty()) {
				String currentDate = AppUtil.getCurrentDateTime(timeZone);

				// check the user activity condition logic
				for (ActivityConditionDto activityCondition : scheduledActivityConditionList) {
					TemporalConditionDto temporalCondition = temporalConditionMap
							.get(activityCondition.getConditionId()).get(0);
					List<PushNotificationBean> userPushNotificationBeanList = new ArrayList<>();

					for (UserDto user : usersList) {
						UserStudiesDto userStudies = userStudyMap.get(user.getUserId()).get(0);
						UserActivitiesDto userActivity = userActivityMap.get(user.getUserId() + "@"
								+ userStudies.getUserStudiesId() + "@" + activityCondition.getConditionId());

						if (userActivity != null && temporalCondition != null
								&& userActivity.getConditionId() == activityCondition.getConditionId()) {
							int anchorDays = temporalCondition.getAnchorDays();
							String startDate = AppUtil.addDays(userStudies.getCreatedOn(),AppConstants.SDF_DATE_TIME_FORMAT,anchorDays,
									AppConstants.SDF_DATE_TIME_FORMAT);
							String activityStartDate = temporalCondition.getStartDate() + " "
									+ temporalCondition.getStartTime();
							String endDate = temporalCondition.getEndDate() + " " + temporalCondition.getEndTime();
							String resultStartDate;

							// Check the activity is available or not for the user
							if (!SDF_DATE_TIME.parse(startDate)
									.before(SDF_DATE_TIME.parse(activityStartDate))) {
								resultStartDate = startDate;
							} else {
								resultStartDate = activityStartDate;
							}

							if ((!SDF_DATE_TIME.parse(currentDate)
									.before(SDF_DATE_TIME.parse(resultStartDate))
									&& !SDF_DATE_TIME.parse(endDate)
									.before(SDF_DATE_TIME.parse(resultStartDate))
									&& !SDF_DATE_TIME.parse(endDate)
									.before(SDF_DATE_TIME.parse(currentDate)))
									&& (userActivity.getCompletedCount() < activityCondition
											.getTotalParticipationTarget())) {
								if(temporalCondition.getRepetitionFrequencyDays() != null) {
									days = temporalCondition.getRepetitionFrequencyDays().split(",");
								}
								if(days != null) {
									Date now = new Date();
							        SimpleDateFormat simpleDateformat = new SimpleDateFormat(AppConstants.SDF_DAY);
							        day = simpleDateformat.format(now);
							        for(int i=0;i<days.length;i++) {
							        	if(days[i].equalsIgnoreCase(day)) {
							        		dayCheck = true;
							        	}
							        }
								}
								
							if(dayCheck == true) {
									UserActivitiesRunsDto userActivityRun = userActivityRunMap
											.get(user.getUserId() + "@" + userStudies.getUserStudiesId() + "@"
													+ userActivity.getConditionId());
									boolean notifyUserFlag = false;
									notifyUserFlag = this.saveOrUpdateUserRunsDetailsByConditionLogicScheduledActivities(activityCondition,
											user, userStudies, userActivity, userActivityRun, temporalCondition);
									if (notifyUserFlag) {
										PushNotificationBean pushNotificationBean = new PushNotificationBean()
												.setActivityId(activityCondition.getActivityConditionId())
												.setConditionId(String.valueOf(activityCondition.getConditionId()))
												.setCurrentRunId(String.valueOf(userActivity.getTotalCount() + 1))
												.setLanguage(user.getLanguage())
												.setStudyId(String.valueOf(userStudies.getStudyId()))
												.setUserId(String.valueOf(user.getUserId()))
												.setUserTimeZone(user.getTimeZone())
												.setDeviceToken(
														userAuthInfoMap.get(user.getUserId()).get(0).getDeviceToken())
												.setDeviceType(
														userAuthInfoMap.get(user.getUserId()).get(0).getDeviceType())
												.setActivityName(activityCondition.getActivityConditionName());
										userPushNotificationBeanList.add(pushNotificationBean);
									}
									
							}
								
							}
						}
					}

					// Get the users to be notified about the new run available
					if(userPushNotificationBeanList!=null && !userPushNotificationBeanList.isEmpty()) {
						pushNotificationMap.put(activityCondition.getConditionId(), userPushNotificationBeanList);
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: AppServiceImpl - checkUserScheduledActivityConditionPrerequisists()", e);
		}
		LOGGER.info("INFO: AppServiceImpl - checkUserScheduledActivityConditionPrerequisists() :: ends");
		return pushNotificationMap;
	}

	

}
