package com.gba.ws.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gba.ws.bean.FitbitErrorResponse;
import com.gba.ws.bean.FitbitLogBean;
import com.gba.ws.bean.fitbit.ActivitiesSteps;
import com.gba.ws.bean.fitbit.IntraHeartRateBean;
import com.gba.ws.bean.fitbit.RestingDataset;
import com.gba.ws.bean.fitbit.RestingHeartRateBean;
import com.gba.ws.bean.fitbit.Sleep;
import com.gba.ws.bean.fitbit.SleepBean;
import com.gba.ws.bean.fitbit.StepsBean;
import com.gba.ws.dao.ActivityDao;
import com.gba.ws.dao.StudyDao;
import com.gba.ws.dao.UserDao;
import com.gba.ws.model.AuthInfoDto;
import com.gba.ws.model.FitbitLogDto;
import com.gba.ws.model.FitbitUserInfoDto;
import com.gba.ws.model.HeartRateDto;
import com.gba.ws.model.SleepDto;
import com.gba.ws.model.StepsDto;
import com.gba.ws.model.UserDto;
import com.gba.ws.model.UserStudiesDto;
import com.gba.ws.util.AppConstants;
import com.gba.ws.util.AppEnums;
import com.gba.ws.util.AppUtil;
import com.gba.ws.util.MailContent;
import com.gba.ws.util.Responsemodel;
import com.google.gson.Gson;

@Service
public class ApplicationServiceImpl {
  private static final Logger LOGGER = Logger.getLogger(ApplicationServiceImpl.class);

  @Autowired private UserDao userDao;

  @Autowired private StudyDao studyDao;

  @Autowired private ActivityDao activityDao;

  @Autowired private RestTemplate restTemplate;

  public ApplicationServiceImpl(
      UserDao userDao, StudyDao studyDao, ActivityDao activityDao, RestTemplate restTemplate) {
    super();
    this.userDao = userDao;
    this.studyDao = studyDao;
    this.activityDao = activityDao;
    this.restTemplate = restTemplate;
  }

  //@Scheduled(cron = "0 0 23 1/5 * ?")
  //@Scheduled(cron = "0 0 6 1/5 * ?")//Every friday 6AM server time
  @Scheduled(cron = "0 0 6 1/5 * ?", zone= AppConstants.SERVER_TIMEZONE)
  public void giveFitbit() {
    LOGGER.info("INFO: AppServiceImpl - sendPushNotifications() :: starts");
    List<UserDto> activeUsersList = new ArrayList<>();
    List<String> timeZoneList = new ArrayList<>();
    List<UserDto> userTimeZoneList = null;
    try {
      activeUsersList = userDao.getALLUserInfo();
      timeZoneList = userDao.getAllTimeZoneList();
      if (!activeUsersList.isEmpty() && !timeZoneList.isEmpty()) {

        for (String timeZone : timeZoneList) {

          userTimeZoneList = new ArrayList<>();
          List<Object> userIdsList = new ArrayList<>();
          List<FitbitUserInfoDto> fitbitUserInfoList = new ArrayList<>();
          List<UserStudiesDto> userStudyList = new ArrayList<>();
          Map<Integer, List<UserStudiesDto>> userStudyMap = new HashMap<>();
          List<AuthInfoDto> authInfoList = new ArrayList<>();
          Map<Integer, List<AuthInfoDto>> userAuthInfoMap = new HashMap<>();

          userTimeZoneList =
              activeUsersList
                  .stream()
                  .filter(x -> timeZone.equals(x.getTimeZone()))
                  .collect(Collectors.toList());
          userIdsList =
              userTimeZoneList.stream().map(UserDto::getUserId).collect(Collectors.toList());
          LOGGER.info("userIdsList size : "+userIdsList.size()+", userIdsList : "+userIdsList);
          fitbitUserInfoList = userDao.findAllFitBitUserInfoDetailsUserIdsList(userIdsList);
          Map<Integer, List<FitbitUserInfoDto>> fitbitInfoMap =
              fitbitUserInfoList
                  .stream()
                  .collect(Collectors.groupingBy(FitbitUserInfoDto::getUserId));

          userStudyList =
              studyDao.fetchUserStudiesDetailsList(
                  userIdsList, AppConstants.DEFAULT_STUDY_ID, null);
          userStudyMap =
              userStudyList.stream().collect(Collectors.groupingBy(UserStudiesDto::getUserId));

          authInfoList = userDao.findAllAuthinfoDetailsByUserIdList(userIdsList);
          userAuthInfoMap =
              authInfoList.stream().collect(Collectors.groupingBy(AuthInfoDto::getUserId));

          for (UserDto user : userTimeZoneList) {
            FitbitLogBean conditionsMap = new FitbitLogBean();

            LOGGER.info("USER ID ::::::::::::::::::::::::::::::::: " + user.getUserId());

            // Check Fitbit details availble for or not and save in database
            conditionsMap = this.fetchAndSaveFitbitDetailsByUser(user, fitbitInfoMap, conditionsMap,userStudyMap);

            // Update the fitbit and lass4u details for user
           /* if ((userStudyMap.get(user.getUserId()) != null)
                && StringUtils.isNotEmpty(
                    userStudyMap.get(user.getUserId()).get(0).getEnrollmentId())) {
              FitbitLogDto fitbitlog = new FitbitLogDto();
              fitbitlog = this.saveFitbitLog(userStudyMap, user, conditionsMap, fitbitlog);
              userDao.saveOrUpdateFitbitLogDetails(fitbitlog, AppConstants.DB_SAVE);
            }*/
          }
        }
      }
    } catch (Exception e) {
      LOGGER.error("ERROR: AppServiceImpl - sendPushNotifications()", e);
    }
    LOGGER.info("INFO: AppServiceImpl - sendPushNotifications() :: ends");
  }

  public FitbitLogBean fetchAndSaveFitbitDetailsByUser(
      UserDto user,
      Map<Integer, List<FitbitUserInfoDto>> fitbitInfoMap,
      FitbitLogBean conditionsMap, Map<Integer, List<UserStudiesDto>> userStudyMap) {
    LOGGER.info("INFO: AppServiceImpl - fetchAndSaveFitbitDetailsByUser() :: starts");
    FitbitUserInfoDto fitbitUserInfo = null;
    FitbitLogBean updatedConditionMap = conditionsMap;
    try {

      if (fitbitInfoMap.get(user.getUserId()) != null
          && !fitbitInfoMap.get(user.getUserId()).isEmpty()) {
        fitbitUserInfo = fitbitInfoMap.get(user.getUserId()).get(0);

        
        //Delete previous 15 days records of sleep step and heart rate
        LocalDate now = LocalDate.now(ZoneId.of(AppConstants.SERVER_TIMEZONE));
        String day = null;
        List<String> days = new ArrayList<String>();        
        for(int i = 15 ; i >=0 ; i--) {
      	  day = now.minusDays(i).format(DateTimeFormatter.ofPattern(AppConstants.SDF_DATE_FORMAT));
      	  days.add(day);
        }
        userDao.deleteFitbitSleepData(days, user.getUserId());
        userDao.deleteFitbitStepsData(days, user.getUserId());
        userDao.deleteFitbitHeartRateData(days, user.getUserId());
        
        // Check the access token is valid or not, if expired get the new access token
        // from fitbit refresh token and update the details
        updatedConditionMap = this.callFitbitSleepAPI(fitbitUserInfo, conditionsMap,user,userStudyMap);

        if (!updatedConditionMap.isDisconnected()) {
          fitbitUserInfo = updatedConditionMap.getFitbitUserInfo();
          //conditionsMap.setSleep(updatedConditionMap.getSleep());
          updatedConditionMap = this.callFitbitActivityStepsAPI(fitbitUserInfo, conditionsMap,user,userStudyMap);
          fitbitUserInfo = updatedConditionMap.getFitbitUserInfo();
         // conditionsMap.setSteps(updatedConditionMap.getSteps());
        }
        if (!updatedConditionMap.isDisconnected()) {
          updatedConditionMap = this.callFitbitRestingHeartRateAPI(fitbitUserInfo, conditionsMap,user,userStudyMap);
          fitbitUserInfo = updatedConditionMap.getFitbitUserInfo();
         //conditionsMap.setRestingHeartRate(updatedConditionMap.getRestingHeartRate());
        }
       /* if (!updatedConditionMap.isDisconnected()) {
          updatedConditionMap =
              this.callFitbitIntradayHeartRateAPI(fitbitUserInfo, user, conditionsMap);

          if (!updatedConditionMap.isDisconnected()) {
            heartRate =
                this.getCalculatedHeartRate(
                    updatedConditionMap.getRestingHeartRate(),
                    updatedConditionMap.getIntraDayHeartRate());
            updatedConditionMap.setHeartRate(heartRate);
          }
        }*/
      } else {
        updatedConditionMap.setFitbitCount(4);
      }
    } catch (Exception e) {
      updatedConditionMap.setFitbitCount(4);
      LOGGER.error("ERROR: AppServiceImpl - fetchAndSaveFitbitDetailsByUser()", e);
    }
    LOGGER.info("INFO: AppServiceImpl - fetchAndSaveFitbitDetailsByUser() :: ends");
    System.out.println("The" + updatedConditionMap);
    return updatedConditionMap;
  }
  /**
   * Provides new fitbit access token for the provided refresh token and user identifier
   *
   * @author Mohan
   * @param fitbitRefreshToken the fitbit refresh token
   * @return the {@link FitbitUserInfoDto} details
   */
  public FitbitUserInfoDto callFitbitRefreshTokenAPI(FitbitUserInfoDto fitbitUserInfo) {
    LOGGER.info("INFO: AppServiceImpl - callFitbitRefreshTokenAPI() :: starts");
    FitbitUserInfoDto updatedFitbitUserInfo = null;
    try {
      try {
        ResponseEntity<Object> responseEntity = null;
        HttpHeaders headers = new HttpHeaders();
        headers.add(
            AppConstants.HEADER_KEY_AUTHORIZATION,
            AppUtil.getAppProperties().get(AppConstants.FITBIT_RT_HEADER_AUTHORIZATION));
        headers.add(
            AppConstants.HEADER_KEY_CONTENT_TYPE,
            AppUtil.getAppProperties().get(AppConstants.FITBIT_RT_HEADER_CONTENT_TYPE));

        // Create the request body as a MultiValueMap
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add(
            AppConstants.HEADER_KEY_GRANT_TYPE,
            AppUtil.getAppProperties().get(AppConstants.FITBIT_RT_HEADER_GRANT_TYPE));
        body.add(AppConstants.HEADER_KEY_REFRESH_TOKEN, fitbitUserInfo.getFitbitRefreshToken());

        HttpEntity<Object> entity = new HttpEntity<>(body, headers);
        /*          try {*/
        responseEntity =
            restTemplate.exchange(
                AppUtil.getAppProperties().get("fitbit.rt.url"),
                HttpMethod.POST,
                entity,
                Object.class);
        /*
        } catch (Exception e) {
            LOGGER.error("ERROR: AppServiceImpl - ResponseEntity()", e);

        }*/

        if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {

          @SuppressWarnings("unchecked")
          LinkedHashMap<String, Object> respBodyMap =
              (LinkedHashMap<String, Object>) responseEntity.getBody();

          // Update the fitbit user info details
          updatedFitbitUserInfo =
              new FitbitUserInfoDto()
                  .setModifiedOn(AppUtil.getCurrentDateTime())
                  .setFitbitAccessToken(respBodyMap.get("access_token").toString())
                  .setFitbitRefreshToken(respBodyMap.get("refresh_token").toString())
                  .setUserId(fitbitUserInfo.getUserId())
                  .setFuiId(fitbitUserInfo.getFuiId());
          return userDao.saveOrUpdateFitbitUserInfoDetails(
              updatedFitbitUserInfo, AppConstants.DB_UPDATE);
        } else {
          ObjectMapper mapper = new ObjectMapper();
          FitbitErrorResponse fitbitErrorResponse =
              mapper.readValue(responseEntity.getBody().toString(), FitbitErrorResponse.class);
          if (fitbitErrorResponse != null
              && !fitbitErrorResponse.getErrors().isEmpty()
              && fitbitErrorResponse.getErrors().get(0).getErrorType().equals("invalid_grant")) {
            userDao.deleteFitBitUserInfo(
                String.valueOf(fitbitUserInfo.getUserId()), AppConstants.FIND_BY_TYPE_USERID);
            updatedFitbitUserInfo = new FitbitUserInfoDto();
            updatedFitbitUserInfo.setDisconnected(true);
          }
        }
      } catch (HttpStatusCodeException httpsce) {
        LOGGER.error("ERROR: AppServiceImpl - callFitbitRefreshTokenAPI()", httpsce);
        ObjectMapper mapper = new ObjectMapper();
        FitbitErrorResponse fitbitErrorResponse =
            mapper.readValue(httpsce.getResponseBodyAsString(), FitbitErrorResponse.class);
        if (fitbitErrorResponse != null
            && !fitbitErrorResponse.getErrors().isEmpty()
            && fitbitErrorResponse.getErrors().get(0).getErrorType().equals("invalid_grant")) {
          userDao.deleteFitBitUserInfo(
              String.valueOf(fitbitUserInfo.getUserId()), AppConstants.FIND_BY_TYPE_USERID);
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
   * @param fitbitUserInfo the {@link FitbitUserInfoDto} details
   * @param conditionsMap the conditions details map
   * @return the user condition details
   */
  public FitbitLogBean callFitbitSleepAPI(
      FitbitUserInfoDto fitbitUserInfo, FitbitLogBean conditionsMap, UserDto user, Map<Integer, List<UserStudiesDto>> userStudyMap) {
    LOGGER.info("INFO: AppServiceImpl - callFitbitSleepAPI() :: starts");
    String sleepDuration = null;
    int count = 0;
    Map<String, String> keyValuesMap = new HashMap<String,String>();
    String toDateTime = null;
    String fromDateTime = null;
    try {
      conditionsMap.setFitbitUserInfo(fitbitUserInfo);

      HashMap<String, String> headers = new HashMap<>();
      headers.put(
          AppConstants.HEADER_KEY_AUTHORIZATION,
          AppConstants.HEADER_VALUE_AUTH_TYPE + fitbitUserInfo.getFitbitAccessToken());
      
      LocalDate now = LocalDate.now(ZoneId.of(AppConstants.SERVER_TIMEZONE));
      toDateTime = now.format(DateTimeFormatter.ofPattern(AppConstants.SDF_DATE_FORMAT));
      fromDateTime = now.minusDays(15).format(DateTimeFormatter.ofPattern(AppConstants.SDF_DATE_FORMAT));
      
      keyValuesMap.put(AppEnums.FT_TIME_FROM.value(), fromDateTime);
      keyValuesMap.put(AppEnums.FT_TIME_TO.value(), toDateTime);

      
      Responsemodel responseEntity =
              AppUtil.exchangeData(
                  MailContent.generateMailContent(
                      AppUtil.getAppProperties().get(AppConstants.FITBIT_API_SLEEP),
                      keyValuesMap),
                  HttpMethod.GET.toString(),
                  headers,
                  null);
      if (responseEntity.getStatusCode() == HttpStatus.OK.value()) {

        SleepBean sleepBean = new Gson().fromJson(responseEntity.getBody(), SleepBean.class);
        if (sleepBean != null && !sleepBean.getSleep().isEmpty()) {
          List<Sleep> sleepList =
              //sleepBean.getSleep().stream().filter(Sleep::isMainSleep).collect(Collectors.toList());
          sleepBean.getSleep().stream().collect(Collectors.toList());
			/*
			 * sleepDuration =
			 * String.valueOf(sleepList.stream().mapToInt(Sleep::getMinutesAsleep).sum());
			 * 
			 * conditionsMap.setSleep(sleepDuration);
			 */
          if ((userStudyMap.get(user.getUserId()) != null)
                  && StringUtils.isNotEmpty(
                      userStudyMap.get(user.getUserId()).get(0).getEnrollmentId())) {
	          for(Sleep sleep : sleepList) {
	        	  SleepDto sleepDto = new SleepDto();
	        	  sleepDto.setCreatedOn(AppUtil.getCurrentDateTime(user.getTimeZone()));
	        	  sleepDto.setDate(sleep.getDateOfSleep());
	        	  sleepDto.setStartTime(sleep.getStartTime().replaceAll("T", " "));
	        	  sleepDto.setEndTime(sleep.getEndTime().replaceAll("T", " "));
	        	  sleepDto.setEnrollmentId(userStudyMap.get(user.getUserId()).get(0).getEnrollmentId());
	        	  sleepDto.setUserId(fitbitUserInfo.getUserId());
	        	  sleepDto.setMinutesAsleep(sleep.getMinutesAsleep());
	        	  sleepDto.setMinutesAwake(sleep.getMinutesAwake());
	        	  sleepDto.setTimeInBed(sleep.getTimeInBed());
	        	  userDao.saveOrUpdateFitbitSleepDetails(sleepDto, AppConstants.DB_SAVE);
	          }
          }
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
        this.callFitbitSleepAPI(fitbitInfo, conditionsMap,user,userStudyMap);
      } else {

      }
    } catch (Exception e) {
      LOGGER.error("ERROR: AppServiceImpl - callFitbitSleepAPI()", e);
    }
    LOGGER.info("INFO: AppServiceImpl - callFitbitSleepAPI() :: ends");
    System.out.println("----" + conditionsMap);
    return conditionsMap;
  }

  /**
   * Get the user fibit activity steps details for the provided user identifier
   *
   * @author Mohan
   * @param fitbitUserInfo the {@link FitbitUserInfoDto} details
   * @param conditionsMap the conditions details map
   * @return the user condition details
   */
  public FitbitLogBean callFitbitActivityStepsAPI(
      FitbitUserInfoDto fitbitUserInfo, FitbitLogBean conditionsMap, UserDto user, Map<Integer, List<UserStudiesDto>> userStudyMap) {
    LOGGER.info("INFO: AppServiceImpl - callFitbitActivityStepsAPI() :: starts");
    int count = 0;
    Map<String, String> keyValuesMap = new HashMap<String,String>();
    String toDateTime = null;
    String fromDateTime = null;
    try {
      conditionsMap.setFitbitUserInfo(fitbitUserInfo);

      HashMap<String, String> headers = new HashMap<>();
      headers.put(
          AppConstants.HEADER_KEY_AUTHORIZATION,
          AppConstants.HEADER_VALUE_AUTH_TYPE + fitbitUserInfo.getFitbitAccessToken());

      LocalDate now = LocalDate.now(ZoneId.of(AppConstants.SERVER_TIMEZONE));
      toDateTime = now.format(DateTimeFormatter.ofPattern(AppConstants.SDF_DATE_FORMAT));
      fromDateTime = now.minusDays(15).format(DateTimeFormatter.ofPattern(AppConstants.SDF_DATE_FORMAT));
      
      keyValuesMap.put(AppEnums.FT_TIME_FROM.value(), fromDateTime);
      keyValuesMap.put(AppEnums.FT_TIME_TO.value(), toDateTime);
      
		
      Responsemodel responseEntity =
              AppUtil.exchangeData(
                  MailContent.generateMailContent(
                      AppUtil.getAppProperties().get(AppConstants.FITBIT_API_ACTIVITY_STEPS),
                      keyValuesMap),
                  HttpMethod.GET.toString(),
                  headers,
                  null);
      if (responseEntity.getStatusCode() == HttpStatus.OK.value()) {

        StepsBean stepsBean = new Gson().fromJson(responseEntity.getBody(), StepsBean.class);
        if (stepsBean != null && !stepsBean.getActivitiesSteps().isEmpty()) {
			/*
			 * steps = stepsBean.getActivitiesSteps().get(0).getValue();
			 * 
			 * conditionsMap.setSteps(steps);
			 */
        	 if ((userStudyMap.get(user.getUserId()) != null)
                     && StringUtils.isNotEmpty(
                         userStudyMap.get(user.getUserId()).get(0).getEnrollmentId())) {
		          for(ActivitiesSteps steps : stepsBean.getActivitiesSteps()) {
		        	  StepsDto stepsDto = new StepsDto();
		        	  stepsDto.setCreatedOn(AppUtil.getCurrentDateTime(user.getTimeZone()));
		        	  stepsDto.setDate(steps.getDateTime());
		        	  stepsDto.setEnrollmentId(userStudyMap.get(user.getUserId()).get(0).getEnrollmentId());
		        	  stepsDto.setSteps(steps.getValue());
		        	  stepsDto.setUserId(fitbitUserInfo.getUserId());
		        	  userDao.saveOrUpdateFitbitStepsDetails(stepsDto, AppConstants.DB_SAVE);
		          }
        	 }
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
        this.callFitbitActivityStepsAPI(fitbitInfo, conditionsMap,user,userStudyMap);
      }
    } catch (Exception e) {
      LOGGER.error("ERROR: AppServiceImpl - callFitbitActivityStepsAPI()", e);
    }
    LOGGER.info("INFO: AppServiceImpl - callFitbitActivityStepsAPI() :: ends");
    return conditionsMap;
  }

  /**
   * Get the user fibit resting heart rate details for the provided user identifier
   *
   * @author Mohan
   * @param fitbitUserInfo the {@link FitbitUserInfoDto} details
   * @param conditionsMap the conditions details map
   * @return the user condition details
   */
  public FitbitLogBean callFitbitRestingHeartRateAPI(
      FitbitUserInfoDto fitbitUserInfo, FitbitLogBean conditionsMap, UserDto user, Map<Integer, List<UserStudiesDto>> userStudyMap) {
    LOGGER.info("INFO: AppServiceImpl - callFitbitRestingHeartRateAPI() :: starts");
    String restingHeartRate = null;
    int count = 0;
    String toDateTime = null;
    String fromDateTime = null;
    try {
      conditionsMap.setFitbitUserInfo(fitbitUserInfo);

      HashMap<String, String> headers = new HashMap<>();
      headers.put(
          AppConstants.HEADER_KEY_AUTHORIZATION,
          AppConstants.HEADER_VALUE_AUTH_TYPE + fitbitUserInfo.getFitbitAccessToken());
      
      Map<String, String> keyValuesMap = new HashMap<String,String>();
      LocalDate now = LocalDate.now(ZoneId.of(AppConstants.SERVER_TIMEZONE));
      //toDateTime = now.format(DateTimeFormatter.ofPattern(AppConstants.SDF_DATE_FORMAT));
      
      for(int i = 15 ; i >=0 ; i--) {
    	  fromDateTime = now.minusDays(i).format(DateTimeFormatter.ofPattern(AppConstants.SDF_DATE_FORMAT));
    	  keyValuesMap.put(AppEnums.FT_DAY.value(), fromDateTime);

  		/*
  		 * Responsemodel responseEntity = AppUtil.exchangeData(
  		 * AppUtil.getAppProperties().get(AppConstants.FITBIT_API_HEARTRATE_RESTING),
  		 * HttpMethod.GET.toString(), headers, null);
  		 */
        Responsemodel responseEntity =
                AppUtil.exchangeData(
                    MailContent.generateMailContent(
                        AppUtil.getAppProperties().get(AppConstants.FITBIT_API_HEARTRATE_RESTING),
                        keyValuesMap),
                    HttpMethod.GET.toString(),
                    headers,
                    null);
        if (responseEntity.getStatusCode() == HttpStatus.OK.value()) {

          RestingHeartRateBean restingHeartRateBean =
              new Gson().fromJson(responseEntity.getBody(), RestingHeartRateBean.class);
          if (restingHeartRateBean != null && restingHeartRateBean.getActivitiesHeartIntraday() != null && !restingHeartRateBean.getActivitiesHeartIntraday().getDataset().isEmpty()) { 
        	  if ((userStudyMap.get(user.getUserId()) != null)
                      && StringUtils.isNotEmpty(
                          userStudyMap.get(user.getUserId()).get(0).getEnrollmentId())) {
	            for(RestingDataset data : restingHeartRateBean.getActivitiesHeartIntraday().getDataset()) {
	              HeartRateDto heartRateDto = new HeartRateDto();
	              heartRateDto.setCreatedOn(AppUtil.getCurrentDateTime(user.getTimeZone()));
	              heartRateDto.setDate(fromDateTime);
	          	  heartRateDto.setEnrollmentId(userStudyMap.get(user.getUserId()).get(0).getEnrollmentId());
	          	  heartRateDto.setUserId(fitbitUserInfo.getUserId());
	          	  heartRateDto.setRestingHeartRate(data.getValue());
	          	  heartRateDto.setTime(data.getTime());
	          	  userDao.saveOrUpdateFitbitHeartRateDetails(heartRateDto, AppConstants.DB_SAVE);
	            }
        	  }
          }
          if(restingHeartRateBean != null) {
        	  String json = new Gson().toJson(restingHeartRateBean);
        	  if ((userStudyMap.get(user.getUserId()) != null)
                      && StringUtils.isNotEmpty(
                          userStudyMap.get(user.getUserId()).get(0).getEnrollmentId()) && null != json && !json.isEmpty()) {
            	  HeartRateDto heartRateDto = new HeartRateDto();
                  heartRateDto.setCreatedOn(AppUtil.getCurrentDateTime(user.getTimeZone()));
                  heartRateDto.setDate(fromDateTime);
              	  heartRateDto.setEnrollmentId(userStudyMap.get(user.getUserId()).get(0).getEnrollmentId());
              	  heartRateDto.setUserId(fitbitUserInfo.getUserId());
              	  heartRateDto.setHeartRate(json);
              	  userDao.saveOrUpdateFitbitHeartRateDetails(heartRateDto, AppConstants.DB_SAVE);
              }
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
          this.callFitbitRestingHeartRateAPI(fitbitInfo, conditionsMap,user,userStudyMap);
        }
      }
    } catch (Exception e) {
      LOGGER.error("ERROR: AppServiceImpl - callFitbitRestingHeartRateAPI()", e);
    }
    LOGGER.info("INFO: AppServiceImpl - callFitbitRestingHeartRateAPI() :: ends");
    return conditionsMap;
  }

  /**
   * Get the user fibit intraday heart rate details for the provided user identifier
   *
   * @author Mohan
   * @param user the {@link UserDto} details
   * @param fitbitUserInfo the {@link FitbitUserInfoDto} details
   * @param conditionsMap the conditions details map
   * @return the user condition details
   */
  public FitbitLogBean callFitbitIntradayHeartRateAPI(
      FitbitUserInfoDto fitbitUserInfo, UserDto user, FitbitLogBean conditionsMap) {
    LOGGER.info("INFO: AppServiceImpl - callFitbitIntradayHeartRateAPI() :: starts");
    String intraDayHeartRate = null;
    String fromDateTime = "";
    String toDateTime = "";
    Map<String, String> keyValuesMap = new HashMap<>();
    int count = 0;
    try {
      conditionsMap.setFitbitUserInfo(fitbitUserInfo);

      HashMap<String, String> headers = new HashMap<>();
      headers.put(
          AppConstants.HEADER_KEY_AUTHORIZATION,
          AppConstants.HEADER_VALUE_AUTH_TYPE + fitbitUserInfo.getFitbitAccessToken());

      // Get the user current date/time for the user timezone
      toDateTime =
          AppUtil.convertDateTimeByTimeZone(
              AppUtil.getCurrentDateTime(),
              AppConstants.SDF_DATE_TIME_FORMAT,
              AppConstants.SDF_DATE_TIME_FORMAT,
              user.getTimeZone());
      fromDateTime = AppUtil.addMinutes(toDateTime, -15);
      keyValuesMap.put(
          AppEnums.FT_TIME_FROM.value(), fromDateTime.substring(11, fromDateTime.length() - 3));
      keyValuesMap.put(
          AppEnums.FT_TIME_TO.value(), toDateTime.substring(11, toDateTime.length() - 3));

      Responsemodel responseEntity =
          AppUtil.exchangeData(
              MailContent.generateMailContent(
                  AppUtil.getAppProperties().get(AppConstants.FITBIT_API_HEARTRATE_INTRADAY),
                  keyValuesMap),
              HttpMethod.GET.toString(),
              headers,
              null);
      if (responseEntity.getStatusCode() == HttpStatus.OK.value()) {

        IntraHeartRateBean intraHeartRateBean =
            new Gson().fromJson(responseEntity.getBody(), IntraHeartRateBean.class);
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

  public String getCalculatedHeartRate(String restingHeartRate, String intraDayHeartRate) {
    LOGGER.info("INFO: AppServiceImpl - getCalculatedHeartRate() :: starts");
    String heartRate = null;
    try {

      if (StringUtils.isNotEmpty(restingHeartRate) && StringUtils.isNotEmpty(intraDayHeartRate)) {
        heartRate =
            String.valueOf(
                    Math.round(
                        ((Double.parseDouble(intraDayHeartRate)
                                    - Double.parseDouble(restingHeartRate))
                                / Double.parseDouble(restingHeartRate))
                            * 100D))
                .replace("-", "");
      }
    } catch (Exception e) {
      LOGGER.error("ERROR: AppServiceImpl - getCalculatedHeartRate()", e);
    }
    LOGGER.info("INFO: AppServiceImpl - getCalculatedHeartRate() :: ends");
    return heartRate;
  }

  public FitbitLogDto saveFitbitLog(
      Map<Integer, List<UserStudiesDto>> userStudyMap,
      UserDto user,
      FitbitLogBean conditionsMap,
      FitbitLogDto fitbitLog) {
    LOGGER.info("INFO: AppServiceImpl - saveFitbitLog() :: starts");
    try {
      if (fitbitLog != null) {
        fitbitLog
            .setUserId(user.getUserId())
            .setCurrentHeartRate(conditionsMap.getIntraDayHeartRate())
            .setEnrollmentId(userStudyMap.get(user.getUserId()).get(0).getEnrollmentId())
            .setHeartRate(conditionsMap.getHeartRate())
            .setRestingHeartRate(conditionsMap.getRestingHeartRate())
            .setSleep(conditionsMap.getSleep())
            .setStepsCount(conditionsMap.getSteps())
            .setCreatedOn(AppUtil.getCurrentDateTime(user.getTimeZone()));
      }
    } catch (Exception e) {
      LOGGER.error("ERROR: AppServiceImpl - saveFitbitLog()", e);
    }
    LOGGER.info("INFO: AppServiceImpl - saveFitbitLog() :: ends");
    return fitbitLog;
  }
}
