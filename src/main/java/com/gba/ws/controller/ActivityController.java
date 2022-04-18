package com.gba.ws.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.gba.ws.bean.ActivityDetailsResponse;
import com.gba.ws.bean.ActivityListResponse;
import com.gba.ws.bean.ActivityRunsResponse;
import com.gba.ws.bean.AuthResponse;
import com.gba.ws.bean.ErrorBean;
import com.gba.ws.bean.ErrorResponse;
import com.gba.ws.bean.KeywordsResponse;
import com.gba.ws.bean.Lass4USensorDataResponse;
import com.gba.ws.bean.RewardsResponse;
import com.gba.ws.exception.CustomException;
import com.gba.ws.model.ActivityConditionDto;
import com.gba.ws.model.UserDto;
import com.gba.ws.service.ActivityService;
import com.gba.ws.service.StudyService;
import com.gba.ws.service.UserService;
import com.gba.ws.util.AppConstants;
import com.gba.ws.util.AppEnums;
import com.gba.ws.util.AppUtil;
import com.gba.ws.util.ErrorCode;
//helloworld
/**
 * 
 * @author Mohan
 * @createdOn Jan 9, 2018 12:37:23 PM
 */
@Controller
public class ActivityController {

	private static final Logger LOGGER = Logger.getLogger(ActivityController.class);

	private static final String ACTIVITIES_URI = "/study/{studyId}/activity";
	private static final String UPDATE_ACTIVITY_STATE_URI = "/study/{studyId}/activity/{activityId}/activityState";
	private static final String ACTIVITY_RUN_URI = "/study/{studyId}/activity/run";
	private static final String ACTIVITY_DETAILS_URI = "/study/{studyId}/activity/{activityId}";
	private static final String UPDATE_THRESHOLD_ACTIVITY_STATE_URI = "/study/{studyId}/activity/{activityId}/thresholdActivityState";
	private static final String REWARDS_URI = "/user/{userId}/rewards";
	private static final String DASHBOARD_URI = "/user/sensorData";
	private static final String SENSOR_DATA = "/sensorData/addData";
	 private static final String KEYWORD_CHOICE = "/autTaskList";
	private static final String RESPONSE_SAVE_ACTIVITY_URI = "/jsonResponseFile";
	
	@Autowired
	private UserService userService;

	@Autowired
	private StudyService studyService;

	@Autowired
	private ActivityService activityService;

	/**
	 * @param userService
	 *            the {@link UserService}
	 * @param studyService
	 *            the {@link StudyService}
	 * @param activityService
	 *            the {@link ActivityService}
	 */
	public ActivityController(UserService userService, StudyService studyService, ActivityService activityService) {
		super();
		this.userService = userService;
		this.studyService = studyService;
		this.activityService = activityService;
	}

	/**
	 * Get the activities for the provided user and study identifier
	 * 
	 * @author Mohan
	 * @param userId
	 *            the user identifier
	 * @param studyId
	 *            the study identifier
	 * @param request
	 *            the {@link HttpServletRequest}
	 * @param response
	 *            the {@link HttpServletResponse}
	 * @return the {@link ActivityListResponse} details
	 * @throws CustomException
	 */
	@RequestMapping(value = ACTIVITIES_URI, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> getActivities(@RequestHeader(name = "userId", required = true) String userId,
			@PathVariable(name = "studyId", required = true) String studyId, HttpServletRequest request,
			HttpServletResponse response) throws CustomException {
		LOGGER.info("INFO: ActivityController - getActivities() :: starts");
		ActivityListResponse activityListResponse = new ActivityListResponse();
		ErrorResponse errorResponse = new ErrorResponse();
		boolean isValidStudyId = false;
		boolean isUserEnrolledToStudy = false;
		try {
			if (StringUtils.isNotEmpty(studyId) && StringUtils.isNotEmpty(userId)) {
				isValidStudyId = studyService.validateStudyId(studyId);
				if (!isValidStudyId) {
					return AppUtil.httpResponseForNotFound(ErrorCode.EC_95.code());
				}

				isUserEnrolledToStudy = activityService.validateUserEnrolledToStudy(userId, studyId);
				if (!isUserEnrolledToStudy) {
					return AppUtil.httpResponseForUnAuthorized(ErrorCode.EC_94.code());
				}

				activityListResponse = activityService.getActivities(userId, studyId);
				if (activityListResponse.getError().getCode() != ErrorCode.EC_200.code()) {
					errorResponse.setError(activityListResponse.getError());
					return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
				}
			} else {
				return AppUtil.httpResponseForBadRequest(ErrorCode.EC_43.code());
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityController - getActivities()", e);
			return AppUtil.httpResponseForInternalServerError();
		}
		LOGGER.info("INFO: ActivityController - getActivities() :: ends");
		return new ResponseEntity<>(activityListResponse, HttpStatus.OK);
	}

	/**
	 * Update user activity run state
	 * 
	 * @author Mohan
	 * @param accessToken
	 *            the access token
	 * @param params
	 *            the request body parameters
	 * @param studyId
	 *            the study identifier
	 * @param activityId
	 *            the activity identifier
	 * @param request
	 *            the {@link HttpServletRequest}
	 * @param response
	 *            the {@link HttpServletResponse}
	 * @return the {@link ErrorResponse} details
	 * @throws CustomException
	 */
	@RequestMapping(value = UPDATE_ACTIVITY_STATE_URI, method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> updateActivityState(
			@RequestHeader(name = "accessToken", required = true) String accessToken,
			@RequestBody(required = true) String params,
			@PathVariable(name = "studyId", required = true) String studyId,
			@PathVariable(name = "activityId", required = true) String activityId, HttpServletRequest request,
			HttpServletResponse response) throws CustomException {
		LOGGER.info("INFO: ActivityController - updateActivityState() :: starts");
		ErrorResponse errorResponse = new ErrorResponse();
		boolean isValidStudyId = false;
		boolean isValidActivityId = false;
		boolean isUserEnrolledToStudy = false;
		try {

			JSONObject json = new JSONObject(params);
			String userId = json.getString(AppEnums.RP_USER_ID.value());
			String lastCompletedDate = json.getString(AppEnums.RP_LAST_COMPLETED_DATE.value());
			String runId = String.valueOf(json.getInt(AppEnums.RP_RUN_ID.value()));
			String runState = json.getString(AppEnums.RP_RUN_STATE.value());
			Long duration = params.contains(AppEnums.RP_DURATION.value()) ? json.getLong(AppEnums.RP_DURATION.value())
					: 0L;

			if (StringUtils.isEmpty(studyId) || StringUtils.isEmpty(activityId) || StringUtils.isEmpty(userId)
					|| StringUtils.isEmpty(lastCompletedDate) || StringUtils.isEmpty(runId)
					|| StringUtils.isEmpty(runState)) {
				return AppUtil.httpResponseForBadRequest(ErrorCode.EC_43.code());
			}

			// authenticate loggedin user
			errorResponse = userService.authenticateUser(accessToken, userId, response);
			if (response.getStatus() == HttpServletResponse.SC_UNAUTHORIZED) {
				return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
			} else if (response.getStatus() == HttpServletResponse.SC_FORBIDDEN) {
				return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
			}

			isValidStudyId = studyService.validateStudyId(studyId);
			if (!isValidStudyId) {
				return AppUtil.httpResponseForNotFound(ErrorCode.EC_95.code());
			}

			isValidActivityId = activityService.validateActivityId(activityId);
			if (!isValidActivityId) {
				return AppUtil.httpResponseForNotFound(ErrorCode.EC_45.code());
			}

			isUserEnrolledToStudy = activityService.validateUserEnrolledToStudy(userId, studyId);
			if (!isUserEnrolledToStudy) {
				return AppUtil.httpResponseForUnAuthorized(ErrorCode.EC_94.code());
			}

			errorResponse = activityService.updateActivityState(userId, studyId, activityId, lastCompletedDate, runId,
					runState, duration);
			if (errorResponse.getError().getCode() != ErrorCode.EC_200.code()) {
				return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
			}
		} catch (JSONException e) {
			LOGGER.error("ERROR: ActivityController - updateActivityState()", e);
			return AppUtil.httpResponseForNotAcceptable(ErrorCode.EC_44.code());
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityController - updateActivityState()", e);
			return AppUtil.httpResponseForInternalServerError();
		}
		LOGGER.info("INFO: ActivityController - updateActivityState() :: ends");
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}

	/**
	 * Get the activities runs by study and user identifier
	 * 
	 * @author Mohan
	 * @param userId
	 *            the user identifier
	 * @param studyId
	 *            the study identifier
	 * @param request
	 *            the {@link HttpServletRequest}
	 * @param response
	 *            the {@link HttpServletResponse}
	 * @return the {@link ActivityRunsResponse} details
	 * @throws CustomException
	 */
	@RequestMapping(value = ACTIVITY_RUN_URI, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> getActivityRun(@RequestHeader(name = "userId", required = true) String userId,
			@PathVariable(name = "studyId", required = true) String studyId, HttpServletRequest request,
			HttpServletResponse response) throws CustomException {
		LOGGER.info("INFO: ActivityController - getActivityRun() :: starts");
		ActivityRunsResponse activityRunsResponse = new ActivityRunsResponse();
		ErrorResponse errorResponse = new ErrorResponse();
		boolean isValidStudyId = false;
		boolean isUserEnrolledToStudy = false;
		try {
			if (StringUtils.isNotEmpty(studyId) && StringUtils.isNotEmpty(userId)) {
				isValidStudyId = studyService.validateStudyId(studyId);
				if (!isValidStudyId) {
					return AppUtil.httpResponseForNotFound(ErrorCode.EC_95.code());
				}

				isUserEnrolledToStudy = activityService.validateUserEnrolledToStudy(userId, studyId);
				if (!isUserEnrolledToStudy) {
					return AppUtil.httpResponseForUnAuthorized(ErrorCode.EC_94.code());
				}

				activityRunsResponse = activityService.getActivityRun(userId, studyId);
				if (activityRunsResponse.getError().getCode() != ErrorCode.EC_200.code()) {
					errorResponse.setError(activityRunsResponse.getError());
					return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
				}
			} else {
				return AppUtil.httpResponseForBadRequest(ErrorCode.EC_43.code());
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityController - getActivityRun()", e);
			return AppUtil.httpResponseForInternalServerError();
		}
		LOGGER.info("INFO: ActivityController - getActivityRun() :: ends");
		return new ResponseEntity<>(activityRunsResponse, HttpStatus.OK);
	}

	/**
	 * Get the activity details for the provided activity and study identifier
	 * 
	 * @author Mohan
	 * @param userId
	 *            the user identifier
	 * @param studyId
	 *            the study identifier
	 * @param activityId
	 *            the activity identifier
	 * @param request
	 *            the {@link HttpServletRequest}
	 * @param response
	 *            the {@link HttpServletResponse}
	 * @return the {@link ActivityDetailsResponse} details
	 * @throws CustomException
	 */
	@RequestMapping(value = ACTIVITY_DETAILS_URI, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> getActivityDetails(@RequestHeader(name = "userId", required = true) String userId,
			@PathVariable(name = "studyId", required = true) String studyId,
			@PathVariable(name = "activityId", required = true) String activityId, HttpServletRequest request,
			HttpServletResponse response) throws CustomException {
		LOGGER.info("INFO: ActivityController - getActivityDetails() :: starts");
		ActivityDetailsResponse activityDetailsResponse = new ActivityDetailsResponse();
		ErrorResponse errorResponse = new ErrorResponse();
		boolean isValidStudyId = false;
		boolean isValidActivityId = false;
		boolean isUserEnrolledToStudy = false;
		try {
			if (StringUtils.isNotEmpty(studyId) && StringUtils.isNotEmpty(userId)
					&& StringUtils.isNotEmpty(activityId)) {
				isValidStudyId = studyService.validateStudyId(studyId);
				if (!isValidStudyId) {
					return AppUtil.httpResponseForNotFound(ErrorCode.EC_95.code());
				}

				isValidActivityId = activityService.validateActivityId(activityId);
				if (!isValidActivityId) {
					return AppUtil.httpResponseForNotFound(ErrorCode.EC_45.code());
				}

				isUserEnrolledToStudy = activityService.validateUserEnrolledToStudy(userId, studyId);
				if (!isUserEnrolledToStudy) {
					return AppUtil.httpResponseForUnAuthorized(ErrorCode.EC_94.code());
				}

				activityDetailsResponse = activityService.getActivityDetails(userId, studyId, activityId);
				if (activityDetailsResponse.getError().getCode() != ErrorCode.EC_200.code()) {
					errorResponse.setError(activityDetailsResponse.getError());
					return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
				}
			} else {
				return AppUtil.httpResponseForBadRequest(ErrorCode.EC_43.code());
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityController - getActivityDetails()", e);
			return AppUtil.httpResponseForInternalServerError();
		}
		LOGGER.info("INFO: ActivityController - getActivityDetails() :: ends");
		return new ResponseEntity<>(activityDetailsResponse, HttpStatus.OK);
	}

	/**
	 * Update the threshold activity state
	 * 
	 * @author Mohan
	 * @param accessToken
	 *            the access token
	 * @param params
	 *            the request parameters
	 * @param studyId
	 *            the study identifier
	 * @param activityId
	 *            the activity identifier
	 * @param request
	 *            the {@link HttpServletRequest}
	 * @param response
	 *            the {@link HttpServletResponse}
	 * @return the {@link ErrorResponse} details
	 * @throws CustomException
	 */
	@RequestMapping(value = UPDATE_THRESHOLD_ACTIVITY_STATE_URI, method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> updateThresholdActivityState(
			@RequestHeader(name = "accessToken", required = true) String accessToken,
			@RequestBody(required = true) String params,
			@PathVariable(name = "studyId", required = true) String studyId,
			@PathVariable(name = "activityId", required = true) String activityId, HttpServletRequest request,
			HttpServletResponse response) throws CustomException {
		LOGGER.info("INFO: ActivityController - updateThresholdActivityState() :: starts");
		ErrorResponse errorResponse = new ErrorResponse();
		boolean isValidStudyId = false;
		boolean isValidActivityId = false;
		boolean isUserEnrolledToStudy = false;
		try {

			JSONObject json = new JSONObject(params);
			String userId = json.getString(AppEnums.RP_USER_ID.value());
			String currentDate = json.getString(AppEnums.RP_CURRENT_DATE.value());
			String runId = String.valueOf(json.getInt(AppEnums.RP_RUN_ID.value()));
			String runState = json.getString(AppEnums.RP_RUN_STATE.value());
			Long duration = params.contains(AppEnums.RP_DURATION.value()) ? json.getLong(AppEnums.RP_DURATION.value())
					: 0L;

			if (StringUtils.isEmpty(studyId) || StringUtils.isEmpty(activityId) || StringUtils.isEmpty(userId)
					|| StringUtils.isEmpty(currentDate) || StringUtils.isEmpty(runId)
					|| StringUtils.isEmpty(runState)) {
				return AppUtil.httpResponseForBadRequest(ErrorCode.EC_43.code());
			}

			// authenticate loggedin user
			errorResponse = userService.authenticateUser(accessToken, userId, response);
			if (response.getStatus() == HttpServletResponse.SC_UNAUTHORIZED) {
				return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
			} else if (response.getStatus() == HttpServletResponse.SC_FORBIDDEN) {
				return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
			}

			isValidStudyId = studyService.validateStudyId(studyId);
			if (!isValidStudyId) {
				return AppUtil.httpResponseForNotFound(ErrorCode.EC_95.code());
			}

			isValidActivityId = activityService.validateActivityId(activityId);
			if (!isValidActivityId) {
				return AppUtil.httpResponseForNotFound(ErrorCode.EC_45.code());
			}

			isUserEnrolledToStudy = activityService.validateUserEnrolledToStudy(userId, studyId);
			if (!isUserEnrolledToStudy) {
				return AppUtil.httpResponseForUnAuthorized(ErrorCode.EC_94.code());
			}

			errorResponse = activityService.updateThresholdActivityState(userId, studyId, activityId, currentDate,
					runId, runState, duration);
			if (errorResponse.getError().getCode() != ErrorCode.EC_200.code()) {
				return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
			}
		} catch (JSONException e) {
			LOGGER.error("ERROR: ActivityController - updateThresholdActivityState()", e);
			return AppUtil.httpResponseForNotAcceptable(ErrorCode.EC_44.code());
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityController - updateThresholdActivityState()", e);
			return AppUtil.httpResponseForInternalServerError();
		}
		LOGGER.info("INFO: ActivityController - updateThresholdActivityState() :: ends");
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}

	/**
	 * Rewards details for the provided user identifier
	 * 
	 * @author Mohan
	 * @param userId
	 *            the user identifier
	 * @param request
	 *            the {@link HttpServletRequest}
	 * @param response
	 *            the {@link HttpServletResponse}
	 * @return the {@link RewardsResponse} details
	 */
	@RequestMapping(value = REWARDS_URI, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> rewards(@PathVariable(name = "userId", required = true) String userId,
			HttpServletRequest request, HttpServletResponse response) {
		LOGGER.info("INFO: ActivityController - rewards() :: starts");
		RewardsResponse rewardsResponse = null;
		try {
			rewardsResponse = activityService.rewards(userId);
			if (rewardsResponse.getError().getCode() != ErrorCode.EC_200.code()) {
				return AppUtil.httpResponseForUnAuthorized(ErrorCode.EC_61.code());
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityController - rewards()", e);
			return AppUtil.httpResponseForInternalServerError();
		}
		LOGGER.info("INFO: ActivityController - rewards() :: ends");
		return new ResponseEntity<>(rewardsResponse, HttpStatus.OK);
	}
	
	/*
	@RequestMapping(value = DASHBOARD_URI, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> getSensorData(@RequestHeader(name = "deviceId", required = true) String deviceId,
			@RequestHeader(name = "connect", required = true) String connect,
			HttpServletRequest request,
			HttpServletResponse response) throws CustomException {
		LOGGER.info("INFO: ActivityController - getSensorData() :: starts");
		Lass4USensorDataResponse lass4USensorDataResponse = null;
		String message = AppConstants.FAILURE;
		boolean getData = false;
		try {
			if (StringUtils.isNotEmpty(deviceId)) {
				message = activityService.isDeviceIdExist(deviceId);
				if(AppConstants.SUCCESS.equalsIgnoreCase(message)){
					if("false".equalsIgnoreCase(connect)) {
						lass4USensorDataResponse = activityService.getSensorData(deviceId);
						if(null == lass4USensorDataResponse) {
							return AppUtil.httpResponseNotFound(ErrorCode.EC_111.code());
						}
					}else {
						lass4USensorDataResponse = new Lass4USensorDataResponse();
						getData = activityService.isLass4USensorDataExist(deviceId);
						if(!getData){
							return AppUtil.httpResponseNotFound(ErrorCode.EC_111.code());
						}
					}
				}else {
					return AppUtil.httpResponseNotFound(ErrorCode.EC_111.code());
				}
			} else {
				return AppUtil.httpResponseNotFound(ErrorCode.EC_43.code());
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityController - getSensorData()", e);
			return AppUtil.httpResponseForInternalServerError();
		}
		LOGGER.info("INFO: ActivityController - getSensorData() :: ends");
		return new ResponseEntity<>(lass4USensorDataResponse.getLass4UBean(), HttpStatus.OK);
	}*/
	
	@RequestMapping(value = DASHBOARD_URI, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> getSensorData(@RequestHeader(name = "deviceId", required = true) String deviceId,
			@RequestHeader(name = "timeZone", required = false) String timeZone,
			@RequestHeader(name = "accessToken", required = true) String accessToken,
			@RequestHeader(name = "connect", required = true) String connect,
			HttpServletRequest request,
			HttpServletResponse response) throws CustomException {
		LOGGER.info("INFO: ActivityController - getSensorData() :: starts");
		Lass4USensorDataResponse lass4USensorDataResponse = null;
		String message = AppConstants.FAILURE;
		boolean getData = false;
		String userTimeZone = "";
		
		try {
			userTimeZone = StringUtils.isNotEmpty(timeZone) ? timeZone : activityService.getUserTimeZoneByAccessToken(accessToken);
			if (StringUtils.isNotEmpty(deviceId) && StringUtils.isNotEmpty(userTimeZone) && StringUtils.isNotEmpty(accessToken) ) {
				message = activityService.isDeviceIdExist(deviceId);
				if(AppConstants.SUCCESS.equalsIgnoreCase(message)){
					if("false".equalsIgnoreCase(connect) && StringUtils.isNoneBlank(userTimeZone)) {
						lass4USensorDataResponse = activityService.getSensorData(deviceId,userTimeZone);
						if(null == lass4USensorDataResponse) {
							return AppUtil.httpResponseNotFound(ErrorCode.EC_111.code());
						}
					}else {
						lass4USensorDataResponse = new Lass4USensorDataResponse();
						getData = activityService.isLass4USensorDataExist(deviceId);
						if(!getData){
							return AppUtil.httpResponseNotFound(ErrorCode.EC_111.code());
						}
					}
				}else {
					return AppUtil.httpResponseNotFound(ErrorCode.EC_111.code());
				}
			} else {
				return AppUtil.httpResponseNotFound(ErrorCode.EC_43.code());
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityController - getSensorData()", e);
			return AppUtil.httpResponseForInternalServerError();
		}
		LOGGER.info("INFO: ActivityController - getSensorData() :: ends");
		return new ResponseEntity<>(lass4USensorDataResponse.getLass4UBean(), HttpStatus.OK);
	}
	
	@RequestMapping(value = SENSOR_DATA, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> addData(@RequestBody(required = true) String params,
			@RequestHeader(name = "Authorization", required = true) String authorization, HttpServletRequest request,
			HttpServletResponse response) throws CustomException {
		LOGGER.info("INFO: UserController - userRegistration() :: starts");
		AuthResponse authResponse = null;
		try {

			JSONObject json = new JSONObject(params);
			String timestamp = json.getString("timestamp");
			String app = json.getString("app");
			String device_id = json.getString("device_id");
			String s_g8 = json.getString("s_g8");
			String s_t0 = json.getString("s_t0");
			String s_d0 = json.getString("s_d0");
			String s_d1 = json.getString("s_d1");
			String s_h0 = json.getString("s_h0");
			String s_d2 = json.getString("s_d2");
			String s_n0 = json.getString("s_n0");
			String s_l0 = json.getString("s_l0");

			if (StringUtils.isNotEmpty(timestamp) && StringUtils.isNotEmpty(app) && StringUtils.isNotEmpty(device_id)
					&& StringUtils.isNotEmpty(s_g8) && StringUtils.isNotEmpty(s_t0)
					&& StringUtils.isNotEmpty(s_d0) && StringUtils.isNotEmpty(s_d1) && StringUtils.isNotEmpty(s_h0) && StringUtils.isNotEmpty(s_d2)
					&& StringUtils.isNotEmpty(s_n0) && StringUtils.isNotEmpty(s_l0)) {
				boolean addData = activityService.insertSensorData(timestamp, app, device_id, s_g8, s_t0, s_d0, s_d1, s_h0, s_d2, s_n0, s_l0);
				if (!addData) {
					return AppUtil.httpResponseForNotFound(ErrorCode.EC_112.code());
				}
			} else {
				return AppUtil.httpResponseForBadRequest(ErrorCode.EC_43.code());
			}
		} catch (JSONException e) {
			LOGGER.error("ERROR: UserController - userRegistration()", e);
			return AppUtil.httpResponseForNotAcceptable(ErrorCode.EC_44.code());
		} catch (Exception e) {
			LOGGER.error("ERROR: UserController - userRegistration()", e);
			return AppUtil.httpResponseForInternalServerError();
		}
		LOGGER.info("INFO: UserController - userRegistration() :: ends");
		return new ResponseEntity<>(authResponse, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = KEYWORD_CHOICE, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> getKeywordChoicesList(@RequestHeader(name = "language", required = true) String language,
			HttpServletRequest request,
			HttpServletResponse response) throws CustomException {
		LOGGER.info("INFO: ActivityController - getKeywordChoicesList() :: starts");
		KeywordsResponse keywordChoiceResponse = null;
		try {
			if (StringUtils.isNotEmpty(language)) {
				keywordChoiceResponse =	activityService.getKeywordsList(language);
				
			} else {
				return AppUtil.httpResponseNotFound(ErrorCode.EC_43.code());
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityController - getKeywordChoicesList()", e);
			return AppUtil.httpResponseForInternalServerError();
		}
		LOGGER.info("INFO: ActivityController - getKeywordChoicesList() :: ends");
		return new ResponseEntity<>(keywordChoiceResponse.getKeywords(), HttpStatus.OK);
	}
	/**
	 * To store the Responses of all the activities
	 * 
	 * @author Kavyashreea
	 * @param accessToken
	 *            the access token
	 * @param params
	 *            the request body parameters
	 * @param request
	 *            the {@link HttpServletRequest}
	 * @param response
	 *            the {@link HttpServletResponse}
	 * @return the {@link ErrorResponse} details
	 * @throws CustomException
	 */
	@RequestMapping(value = RESPONSE_SAVE_ACTIVITY_URI, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> storeJsonResponseFile(
			@RequestHeader(name = "accessToken", required = true) String accessToken,
			@RequestBody(required = true) String params,
			 HttpServletRequest request,
			HttpServletResponse response) throws CustomException {
		LOGGER.info("INFO: ActivityController - storeJsonResponseFile() :: starts");
		ErrorResponse errorResponse = new ErrorResponse();
		boolean isValidEnrollmentId = false;
		ActivityConditionDto activityConditionDto = null;
		try {

			JSONObject json = new JSONObject(params);
			String enrollmentId = json.getString(AppEnums.QK_ENROLLMENT_IDENTIFIER.value());
			String jsonResponse = json.getString(AppEnums.RESP_ST_TYPE.value());
			String userId = json.getString(AppEnums.QK_USER_IDENTIFIER.value());
			String activityConditionId = json.getString(AppEnums.PN_ACTIVITY_ID.value());

			if (StringUtils.isNotEmpty(enrollmentId) && StringUtils.isNotEmpty(jsonResponse) && StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(activityConditionId)) {
				// authenticate logged In user
				errorResponse = userService.authenticateUser(accessToken, userId, response);
				if (response.getStatus() == HttpServletResponse.SC_UNAUTHORIZED) {
					return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
				} else if (response.getStatus() == HttpServletResponse.SC_FORBIDDEN) {
					return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
				}

				isValidEnrollmentId = studyService.validateEnrollmentId(enrollmentId);
				if (!isValidEnrollmentId) {
					return AppUtil.httpResponseForNotFound(ErrorCode.EC_95.code());
				}
				
				activityConditionDto = activityService. fetchActivityConditionDetailsByIdandType(activityConditionId, 
						    		AppConstants.FIND_BY_TYPE_ACTIVITY_CONDITIONID);

				errorResponse = studyService.storeResponseActivitiesTemp(userId, enrollmentId,
						                                 jsonResponse, activityConditionDto.getActivityType(), activityConditionDto.getConditionId(),  activityConditionDto.getActivityId());
				if (errorResponse.getError().getCode() != ErrorCode.EC_200.code()) {
					return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
				}
			} else {
				return AppUtil.httpResponseForBadRequest(ErrorCode.EC_43.code());
			}
		} catch (JSONException e) {
			LOGGER.error("ERROR: ActivityController - storeJsonResponseFile()", e);
			return AppUtil.httpResponseForNotAcceptable(ErrorCode.EC_44.code());
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityController - storeJsonResponseFile()", e);
			return AppUtil.httpResponseForInternalServerError();
		}
		LOGGER.info("INFO: ActivityController - storeJsonResponseFile() :: ends");
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}
}
