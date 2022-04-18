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

import com.gba.ws.bean.GroupLocationResponse;
import com.gba.ws.bean.ErrorResponse;
import com.gba.ws.bean.SignedConsentResponse;
import com.gba.ws.exception.CustomException;
import com.gba.ws.service.StudyService;
import com.gba.ws.service.UserService;
import com.gba.ws.util.AppEnums;
import com.gba.ws.util.AppUtil;
import com.gba.ws.util.ErrorCode;

/**
 * 
 * @author Mohan
 * @createdOn Jan 9, 2018 12:37:41 PM
 */
@Controller
public class StudyController {

	private static final Logger LOGGER = Logger.getLogger(StudyController.class);

	private static final String VERIFY_ELIGIBILITY_URI = "/study/{studyId}/verifiyEligibility";
	private static final String ENROLL_STUDY_URI = "/study/{studyId}/enroll";
	private static final String SIGNED_CONSENT_DOCUMENT_URI = "/study/{studyId}/signedConsent";
	private static final String LEAVE_STUDY_URI = "/study/{studyId}/leaveStudy";
	private static final String GROUP_LOCATION_URI = "/user/{userId}/groupLocaion";
	private static final String BUILDING_LOCATION_URI = "/user/{userId}/buildingLocation";
	
	@Autowired
	private UserService userService;

	@Autowired
	private StudyService studyService;

	/**
	 * @param userService
	 *            the {@link UserService}
	 * @param studyService
	 *            the {@link StudyService}
	 */
	public StudyController(UserService userService, StudyService studyService) {
		super();
		this.userService = userService;
		this.studyService = studyService;
	}
	
	/**
	 * To verify user study eligibility
	 * 
	 * @author Mohan
	 * @param userId
	 *            the user identifier
	 * @param token
	 *            the enrollment identifier
	 * @param studyId
	 *            the study identifier
	 * @param request
	 *            the {@link HttpServletRequest}
	 * @param response
	 *            the {@link HttpServletResponse}
	 * @return the {@link ErrorResponse} details
	 * @throws CustomException
	 */
	@RequestMapping(value = VERIFY_ELIGIBILITY_URI, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> verifiyEligibility(@RequestHeader(name = "userId", required = true) String userId,
			@RequestHeader(name = "token", required = true) String token,
			@PathVariable(name = "studyId", required = true) String studyId, HttpServletRequest request,
			HttpServletResponse response) throws CustomException {
		LOGGER.info("INFO: StudyController - verifiyEligibility() :: starts");
		ErrorResponse errorResponse = new ErrorResponse();
		boolean isValidStudyId = false;
		try {

			if (!(StringUtils.isNotEmpty(userId) && StringUtils.isNotEmpty(token) && StringUtils.isNotEmpty(studyId))) {
				return AppUtil.httpResponseForBadRequest(ErrorCode.EC_43.code());
			}

			isValidStudyId = studyService.validateStudyId(studyId);
			if (!isValidStudyId) {
				return AppUtil.httpResponseForNotFound(ErrorCode.EC_95.code());
			}

			errorResponse = studyService.verifiyEligibility(userId, studyId, token);
			if (errorResponse.getError().getCode() != ErrorCode.EC_200.code()) {
				return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: StudyController - verifiyEligibility()", e);
			return AppUtil.httpResponseForInternalServerError();
		}
		LOGGER.info("INFO: StudyController - verifiyEligibility() :: ends");
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}

	/**
	 * To enroll the user to the study
	 * 
	 * @author Mohan
	 * @param accessToken
	 *            the access token
	 * @param params
	 *            the request body parameters
	 * @param studyId
	 *            the study identifier
	 * @param request
	 *            the {@link HttpServletRequest}
	 * @param response
	 *            the {@link HttpServletResponse}
	 * @return the {@link ErrorResponse} details
	 * @throws CustomException
	 */
	@RequestMapping(value = ENROLL_STUDY_URI, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> enrollInStudy(
			@RequestHeader(name = "accessToken", required = true) String accessToken,
			@RequestBody(required = true) String params,
			@PathVariable(name = "studyId", required = true) String studyId, HttpServletRequest request,
			HttpServletResponse response) throws CustomException {
		LOGGER.info("INFO: StudyController - enrollInStudy() :: starts");
		ErrorResponse errorResponse = new ErrorResponse();
		boolean isValidStudyId = false;
		try {

			JSONObject json = new JSONObject(params);
			String userId = json.getString(AppEnums.RP_USER_ID.value());
			String token = json.getString(AppEnums.RP_TOKEN.value());

			if (StringUtils.isNotEmpty(userId) && StringUtils.isNotEmpty(token) && StringUtils.isNotEmpty(studyId)
					&& StringUtils.isNotEmpty(accessToken)) {

				// Authenticate loggedin user
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

				errorResponse = studyService.enrollInStudy(userId, studyId, token);
				if (errorResponse.getError().getCode() != ErrorCode.EC_200.code()) {
					return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
				}
			} else {
				return AppUtil.httpResponseForBadRequest(ErrorCode.EC_43.code());
			}
		} catch (JSONException e) {
			LOGGER.error("ERROR: StudyController - enrollInStudy()", e);
			return AppUtil.httpResponseForNotAcceptable(ErrorCode.EC_44.code());
		} catch (Exception e) {
			LOGGER.error("ERROR: StudyController - enrollInStudy()", e);
			return AppUtil.httpResponseForInternalServerError();
		}
		LOGGER.info("INFO: StudyController - enrollInStudy() :: ends");
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}

	/**
	 * Get the signed consent document
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
	 * @return the {@link SignedConsentResponse} details
	 * @throws CustomException
	 */
	@RequestMapping(value = SIGNED_CONSENT_DOCUMENT_URI, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> getSignedConsent(@RequestHeader(name = "userId", required = true) String userId,
			@PathVariable(name = "studyId", required = true) String studyId, HttpServletRequest request,
			HttpServletResponse response) throws CustomException {
		LOGGER.info("INFO: StudyController - getSignedConsent() :: starts");
		SignedConsentResponse signedConsentResponse = new SignedConsentResponse();
		ErrorResponse errorResponse = new ErrorResponse();
		boolean isValidStudyId = false;
		try {
			if (StringUtils.isNotEmpty(userId) && StringUtils.isNotEmpty(studyId)) {
				isValidStudyId = studyService.validateStudyId(studyId);
				if (isValidStudyId) {
					signedConsentResponse = studyService.getSignedConsent(userId, studyId);
					if (signedConsentResponse.getError().getCode() != ErrorCode.EC_200.code()) {
						errorResponse.setError(signedConsentResponse.getError());
						if (errorResponse.getError().getCode() == ErrorCode.EC_108.code()) {
							return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
						} else {
							return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
						}
					}
				} else {
					return AppUtil.httpResponseForNotFound(ErrorCode.EC_95.code());
				}
			} else {
				return AppUtil.httpResponseForBadRequest(ErrorCode.EC_43.code());
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: StudyController - getSignedConsent()", e);
			return AppUtil.httpResponseForInternalServerError();
		}
		LOGGER.info("INFO: StudyController - getSignedConsent() :: ends");
		return new ResponseEntity<>(signedConsentResponse, HttpStatus.OK);
	}

	/**
	 * To store the signed consent
	 * 
	 * @author Mohan
	 * @param accessToken
	 *            the access token
	 * @param params
	 *            the request body parameters
	 * @param studyId
	 *            the study identifier
	 * @param request
	 *            the {@link HttpServletRequest}
	 * @param response
	 *            the {@link HttpServletResponse}
	 * @return the {@link ErrorResponse} details
	 * @throws CustomException
	 */
	@RequestMapping(value = SIGNED_CONSENT_DOCUMENT_URI, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> storeSignedConsent(
			@RequestHeader(name = "accessToken", required = true) String accessToken,
			@RequestBody(required = true) String params,
			@PathVariable(name = "studyId", required = true) String studyId, HttpServletRequest request,
			HttpServletResponse response) throws CustomException {
		LOGGER.info("INFO: StudyController - storeSignedConsent() :: starts");
		ErrorResponse errorResponse = new ErrorResponse();
		boolean isValidStudyId = false;
		try {

			JSONObject json = new JSONObject(params);
			String userId = json.getString(AppEnums.RP_USER_ID.value());
			String consent = json.getString(AppEnums.RP_CONSENT.value());

			if (StringUtils.isNotEmpty(userId) && StringUtils.isNotEmpty(consent) && StringUtils.isNotEmpty(studyId)) {
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

				errorResponse = studyService.storeSignedConsent(userId, studyId, consent);
				if (errorResponse.getError().getCode() != ErrorCode.EC_200.code()) {
					return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
				}
			} else {
				return AppUtil.httpResponseForBadRequest(ErrorCode.EC_43.code());
			}
		} catch (JSONException e) {
			LOGGER.error("ERROR: StudyController - storeSignedConsent()", e);
			return AppUtil.httpResponseForNotAcceptable(ErrorCode.EC_44.code());
		} catch (Exception e) {
			LOGGER.error("ERROR: StudyController - storeSignedConsent()", e);
			return AppUtil.httpResponseForInternalServerError();
		}
		LOGGER.info("INFO: StudyController - storeSignedConsent() :: ends");
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}

	/**
	 * To leave study
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
	 * @return the {@link ErrorResponse} details
	 * @throws CustomException
	 */
	@RequestMapping(value = LEAVE_STUDY_URI, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> leaveStudy(@RequestHeader(name = "userId", required = true) String userId,
			@PathVariable(name = "studyId", required = true) String studyId, HttpServletRequest request,
			HttpServletResponse response) throws CustomException {
		LOGGER.info("INFO: StudyController - leaveStudy() :: starts");
		ErrorResponse errorResponse = new ErrorResponse();
		boolean isValidStudyId = false;
		try {
			if (StringUtils.isNotEmpty(userId) && StringUtils.isNotEmpty(studyId)) {
				isValidStudyId = studyService.validateStudyId(studyId);
				if (isValidStudyId) {
					errorResponse = studyService.leaveStudy(userId, studyId);
					if (errorResponse.getError().getCode() != ErrorCode.EC_200.code()) {
						return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
					}
				} else {
					return AppUtil.httpResponseForNotFound(ErrorCode.EC_95.code());
				}
			} else {
				return AppUtil.httpResponseForBadRequest(ErrorCode.EC_43.code());
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: StudyController - leaveStudy()", e);
			return AppUtil.httpResponseForInternalServerError();
		}
		LOGGER.info("INFO: StudyController - leaveStudy() :: ends");
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}

	/**
	 * Get the user group location details for the provided user identifier
	 * 
	 * @author Mohan
	 * @param userId
	 *            the user identifier
	 * @param request
	 *            the {@link HttpServletRequest} details
	 * @param response
	 *            the {@link HttpServletResponse} details
	 * @return the group location details for the user
	 */
	@RequestMapping(value = GROUP_LOCATION_URI, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> getGroupLocation(@PathVariable(name = "userId", required = true) String userId,
			HttpServletRequest request, HttpServletResponse response) {
		LOGGER.info("INFO: StudyController - getGroupLocation() :: starts");
		GroupLocationResponse groupLocationResponse = new GroupLocationResponse();
		ErrorResponse errorResponse = new ErrorResponse();
		try {

			if (StringUtils.isNotEmpty(userId)) {
				groupLocationResponse = studyService.getGroupLocation(userId);
				if (groupLocationResponse.getError().getCode() != ErrorCode.EC_200.code()) {
					errorResponse.setError(groupLocationResponse.getError());
					return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
				}
			} else {
				return AppUtil.httpResponseForBadRequest(ErrorCode.EC_43.code());
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: StudyController - getGroupLocation()", e);
			return AppUtil.httpResponseForInternalServerError();
		}
		LOGGER.info("INFO: StudyController - getGroupLocation() :: ends");
		return new ResponseEntity<>(groupLocationResponse, HttpStatus.OK);
	}
	
	/**
	 * Get the user building location details for the provided user identifier
	 * 
	 * @author Fathima
	 * @param userId
	 *            the user identifier
	 * @param request
	 *            the {@link HttpServletRequest} details
	 * @param response
	 *            the {@link HttpServletResponse} details
	 * @return the group location details for the user
	 */
	@RequestMapping(value = BUILDING_LOCATION_URI, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> getBuildingLocation(@PathVariable(name = "userId", required = true) String userId,
			HttpServletRequest request, HttpServletResponse response) {
		LOGGER.info("INFO: StudyController - getBuildingLocation() :: starts");
		GroupLocationResponse groupLocationResponse = new GroupLocationResponse();
		ErrorResponse errorResponse = new ErrorResponse();
		try {

			if (StringUtils.isNotEmpty(userId)) {
				groupLocationResponse = studyService.getBuildingLocation(userId);
				if (groupLocationResponse.getError().getCode() != ErrorCode.EC_200.code()) {
					errorResponse.setError(groupLocationResponse.getError());
					return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
				}
			} else {
				return AppUtil.httpResponseForBadRequest(ErrorCode.EC_43.code());
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: StudyController - getBuildingLocation()", e);
			return AppUtil.httpResponseForInternalServerError();
		}
		LOGGER.info("INFO: StudyController - getBuildingLocation() :: ends");
		return new ResponseEntity<>(groupLocationResponse, HttpStatus.OK);
	}
}
