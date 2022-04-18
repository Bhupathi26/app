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

import com.gba.ws.bean.AuthResponse;
import com.gba.ws.bean.ErrorBean;
import com.gba.ws.bean.ErrorResponse;
import com.gba.ws.bean.LoginResponse;
import com.gba.ws.bean.SettingsBean;
import com.gba.ws.bean.UserProfileResponse;
import com.gba.ws.exception.CustomException;
import com.gba.ws.model.UserDto;
import com.gba.ws.service.UserService;
import com.gba.ws.util.AppConstants;
import com.gba.ws.util.AppEnums;
import com.gba.ws.util.AppUtil;
import com.gba.ws.util.ErrorCode;

/**
 * 
 * @author Mohan
 * @createdOn Jan 8, 2018 7:29:16 PM
 */
@Controller
public class UserController {

	private static final Logger LOGGER = Logger.getLogger(UserController.class);

	private static final String LOGIN_URI = "/user/authentication";
	private static final String REFRESH_TOKEN_URI = "/user/refreshToken";
	private static final String REGISTRATION_URI = "/user";
	private static final String EMAIL_VERIFICATION_URI = "/user/{userId}/verification";
	private static final String RESEND_VERIFICATION_URI = "/user/{userId}/verification/token";
	private static final String FORGOT_PSWRD_URI = "/user/password/forgot";
	private static final String PROFILE_DETAILS_URI = "/user/{userId}";
	private static final String CHANGE_PSWRD_URI = "/user/{userId}/password";
	private static final String LOGOUT_URI = "/user/{userId}/authentication";
	private static final String FITBIT_REFRESH_ACCESS_TOKEN_URI = "/fitbit/refreshAccessToken";
	private static final String UPDATE_DEVICE_TOKEN_URI = "/user/{userId}/deviceToken";
	
	@Autowired
	private UserService userService;

	/**
	 * @param userService
	 *            the {@link UserService}
	 */
	public UserController(UserService userService) {
		super();
		this.userService = userService;
	}
	
	/**
	 * Update the access token when it is expired, to keep the user session alive
	 * until the user logout of the application or login from different device.
	 * 
	 * @author Mohan
	 * @param params
	 *            the request body parameters
	 * @param request
	 *            the {@link HttpServletRequest}
	 * @param response
	 *            the {@link HttpServletResponse}
	 * @return the {@link AuthResponse} details
	 * @throws CustomException
	 */
	@RequestMapping(value = REFRESH_TOKEN_URI, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> validateSessionAuthKey(@RequestBody(required = true) String params,
			HttpServletRequest request, HttpServletResponse response) throws CustomException {
		LOGGER.info("INFO: UserController - validateSessionAuthKey() :: starts");
		AuthResponse authResponse = null;
		ErrorResponse errorResponse = new ErrorResponse();
		try {

			JSONObject json = new JSONObject(params);
			String refreshToken = json.getString(AppEnums.RP_REFRESH_TOKEN.value());

			if (StringUtils.isNotEmpty(refreshToken)) {
				authResponse = userService.validateSessionAuthKey(refreshToken, response);
				if (authResponse.getError().getCode() != ErrorCode.EC_200.code()) {
					errorResponse.setError(authResponse.getError());
					if (response.getStatus() == HttpServletResponse.SC_UNAUTHORIZED) {
						return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
					} else {
						return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
					}
				}
			} else {
				return AppUtil.httpResponseForBadRequest(ErrorCode.EC_400.code());
			}
		} catch (JSONException e) {
			LOGGER.error("ERROR: UserController - validateSessionAuthKey()", e);
			return AppUtil.httpResponseForNotAcceptable(ErrorCode.EC_44.code());
		} catch (Exception e) {
			LOGGER.error("ERROR: UserController - validateSessionAuthKey()", e);
			return AppUtil.httpResponseForInternalServerError();
		}
		LOGGER.info("INFO: UserController - validateSessionAuthKey() :: ends");
		return new ResponseEntity<>(authResponse, HttpStatus.OK);
	}

	/**
	 * Authenticate user during login
	 * 
	 * @author Mohan
	 * @param params
	 *            the request body parameters
	 * @param request
	 *            the {@link HttpServletRequest}
	 * @param response
	 *            the {@link HttpServletResponse}
	 * @return the {@link LoginResponse} details
	 * @throws CustomException
	 */
	@RequestMapping(value = LOGIN_URI, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> login(@RequestBody(required = true) String params, HttpServletRequest request,
			HttpServletResponse response) throws CustomException {
		LOGGER.info("INFO: UserController - login() :: starts");
		LoginResponse loginResponse = null;
		ErrorResponse errorResponse = new ErrorResponse();
		try {

			JSONObject json = new JSONObject(params);
			String email = json.getString(AppEnums.RP_EMAIL.value());
			String password = json.getString(AppEnums.RP_PASSWORD.value());

			if (StringUtils.isNotEmpty(email) && StringUtils.isNotEmpty(password)) {
				loginResponse = userService.login(email, password);
				if (loginResponse.getError().getCode() != ErrorCode.EC_200.code()) {
					errorResponse.setError(loginResponse.getError());
					return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
				}
			} else {
				return AppUtil.httpResponseForBadRequest(ErrorCode.EC_400.code());
			}
		} catch (JSONException e) {
			LOGGER.error("ERROR: UserController - login()", e);
			return AppUtil.httpResponseForNotAcceptable(ErrorCode.EC_44.code());
		} catch (Exception e) {
			LOGGER.error("ERROR: UserController - login()", e);
			return AppUtil.httpResponseForInternalServerError();
		}
		LOGGER.info("INFO: UserController - login() :: ends");
		return new ResponseEntity<>(loginResponse, HttpStatus.OK);
	}

	/**
	 * Create new user
	 * 
	 * @author Mohan
	 * @param params
	 *            the request body parameters
	 * @param authorization
	 *            the Authorization key
	 * @param request
	 *            the {@link HttpServletRequest}
	 * @param response
	 *            the {@link HttpServletResponse}
	 * @return the {@link AuthResponse} details
	 * @throws CustomException
	 */
	@RequestMapping(value = REGISTRATION_URI, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> userRegistration(@RequestBody(required = true) String params,
			@RequestHeader(name = "Authorization", required = true) String authorization, HttpServletRequest request,
			HttpServletResponse response) throws CustomException {
		LOGGER.info("INFO: UserController - userRegistration() :: starts");
		AuthResponse authResponse = null;
		ErrorResponse errorResponse = new ErrorResponse();
		try {

			JSONObject json = new JSONObject(params);
			String email = json.getString(AppEnums.RP_EMAIL.value());
			String password = json.getString(AppEnums.RP_PASSWORD.value());
			String firstName = json.getString(AppEnums.RP_FIRST_NAME.value());
			String lastName = json.getString(AppEnums.RP_LAST_NAME.value());
			String language = json.getString(AppEnums.RP_LANGUAGE.value());
			boolean agreedTNC = json.getBoolean(AppEnums.RP_AGREED_TNC.value());
			String timeZone = params.contains(AppEnums.RP_TIMEZONE.value())
					? json.getString(AppEnums.RP_TIMEZONE.value())

					: "AppConstants.SERVER_TIMEZONE ";


			if (StringUtils.isNotEmpty(email) && StringUtils.isNotEmpty(password) && StringUtils.isNotEmpty(firstName)
					&& StringUtils.isNotEmpty(lastName) && StringUtils.isNotEmpty(language)) {
				boolean isInvalidValid = userService.validateEmail(email);
				if (!isInvalidValid) {
					UserDto user = new UserDto();
					user.setEmail(email).setFirstName(firstName).setLastName(lastName)
							.setUserPassword(AppUtil.getEncryptedPassword(password))
							.setLanguage(language).setTimeZone(timeZone).setAggreedTnc(agreedTNC);
					
					authResponse = userService.userRegistration(user, authorization);
					if (authResponse.getError().getCode() != ErrorCode.EC_200.code()) {
						errorResponse.setError(new ErrorBean().setCode(ErrorCode.EC_403.code())
								.setMessage(ErrorCode.EC_403.errorMessage()));
						return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
					}
				} else {
					errorResponse.setError(
							new ErrorBean().setCode(ErrorCode.EC_42.code()).setMessage(ErrorCode.EC_42.errorMessage()));
					return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
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

	/**
	 * Verify the user email
	 * 
	 * @author Mohan
	 * @param params
	 *            the request body parameters
	 * @param userId
	 *            the user identifier
	 * @param accessToken
	 *            the access token
	 * @param request
	 *            the {@link HttpServletRequest}
	 * @param response
	 *            the {@link HttpServletResponse}
	 * @return the {@link ErrorResponse} details
	 * @throws CustomException
	 */
	@RequestMapping(value = EMAIL_VERIFICATION_URI, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> emailVerification(@RequestBody(required = true) String params,
			@PathVariable(name = "userId", required = true) String userId,
			@RequestHeader(name = "accessToken", required = true) String accessToken, HttpServletRequest request,
			HttpServletResponse response) throws CustomException {
		LOGGER.info("INFO: UserController - emailVerification() :: starts");
		ErrorResponse errorResponse = new ErrorResponse();
		try {

			JSONObject json = new JSONObject(params);
			String token = json.getString(AppEnums.RP_TOKEN.value());

			if (StringUtils.isNotEmpty(token) && StringUtils.isNotEmpty(userId)) {
				errorResponse = userService.emailVerification(userId, accessToken, token);
				if (errorResponse.getError().getCode() != ErrorCode.EC_200.code()) {
					return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
				}
			} else {
				return AppUtil.httpResponseForBadRequest(ErrorCode.EC_43.code());
			}
		} catch (JSONException e) {
			LOGGER.error("ERROR: UserController - emailVerification()", e);
			return AppUtil.httpResponseForNotAcceptable(ErrorCode.EC_44.code());
		} catch (Exception e) {
			LOGGER.error("ERROR: UserController - emailVerification()", e);
			return AppUtil.httpResponseForInternalServerError();
		}
		LOGGER.info("INFO: UserController - emailVerification() :: ends");
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}

	/**
	 * Resend the verification token to the requesting user
	 * 
	 * @author Mohan
	 * @param userId
	 *            the user identifier
	 * @param request
	 *            the {@link HttpServletRequest}
	 * @param response
	 *            the {@link HttpServletResponse}
	 * @return the {@link ErrorResponse} details
	 * @throws CustomException
	 */
	@RequestMapping(value = RESEND_VERIFICATION_URI, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> resendVerificationToken(@PathVariable(name = "userId", required = true) String userId,
			HttpServletRequest request, HttpServletResponse response) throws CustomException {
		LOGGER.info("INFO: UserController - resendVerificationToken() :: starts");
		ErrorResponse errorResponse = new ErrorResponse();
		try {
			if (StringUtils.isNotEmpty(userId)) {
				errorResponse = userService.resendVerificationToken(userId);
				if (errorResponse.getError().getCode() != ErrorCode.EC_200.code()) {
					return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
				}
			} else {
				return AppUtil.httpResponseForBadRequest(ErrorCode.EC_43.code());
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: UserController - resendVerificationToken()", e);
			return AppUtil.httpResponseForInternalServerError();
		}
		LOGGER.info("INFO: UserController - resendVerificationToken() :: ends");
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}

	/**
	 * Provide new password for the user
	 * 
	 * @author Mohan
	 * @param params
	 *            the request body paramters
	 * @param request
	 *            the {@link HttpServletRequest}
	 * @param response
	 *            the {@link HttpServletResponse}
	 * @return the {@link ErrorResponse} details
	 * @throws CustomException
	 */
	@RequestMapping(value = FORGOT_PSWRD_URI, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> forgotPassword(@RequestBody(required = true) String params,
			HttpServletRequest request, HttpServletResponse response) throws CustomException {
		LOGGER.info("INFO: UserController - forgotPassword() :: starts");
		ErrorResponse errorResponse = new ErrorResponse();
		try {

			JSONObject json = new JSONObject(params);
			String email = json.getString(AppEnums.RP_EMAIL.value());

			if (StringUtils.isNotEmpty(email)) {
				boolean isValid = userService.validateEmail(email);
				if (isValid) {
					errorResponse = userService.forgotPassword(email);
					if (errorResponse.getError().getCode() != ErrorCode.EC_200.code()) {
						return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
					}
				} else {
					return AppUtil.httpResponseForNotFound(ErrorCode.EC_71.code());
				}
			} else {
				return AppUtil.httpResponseForBadRequest(ErrorCode.EC_43.code());
			}
		} catch (JSONException e) {
			LOGGER.error("ERROR: UserController - forgotPassword()", e);
			return AppUtil.httpResponseForNotAcceptable(ErrorCode.EC_44.code());
		} catch (Exception e) {
			LOGGER.error("ERROR: UserController - forgotPassword()", e);
			return AppUtil.httpResponseForInternalServerError();
		}
		LOGGER.info("INFO: UserController - forgotPassword() :: ends");
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}

	/**
	 * Get the user profile details
	 * 
	 * @author Mohan
	 * @param userId
	 *            the user identifier
	 * @param request
	 *            the {@link HttpServletRequest}
	 * @param response
	 *            the {@link HttpServletResponse}
	 * @return the {@link UserProfileResponse} details
	 * @throws CustomException
	 */
/*	@RequestMapping(value = PROFILE_DETAILS_URI, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> userProfileDetails(@PathVariable(name = "userId", required = true) String userId,
			HttpServletRequest request, HttpServletResponse response) throws CustomException {
		LOGGER.info("INFO: UserController - userProfileDetails() :: starts");
		UserProfileResponse userProfileResponse = new UserProfileResponse();
		ErrorResponse errorResponse = new ErrorResponse();
		try {
			if (StringUtils.isNotEmpty(userId)) {
				userProfileResponse = userService.userProfileDetails(userId);
				if (userProfileResponse.getError().getCode() != ErrorCode.EC_200.code()) {
					errorResponse.setError(userProfileResponse.getError());
					return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
				}
			} else {
				return AppUtil.httpResponseForBadRequest(ErrorCode.EC_43.code());
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: UserController - userProfileDetails()", e);
			return AppUtil.httpResponseForInternalServerError();
		}
		LOGGER.info("INFO: UserController - userProfileDetails() :: ends");
		return new ResponseEntity<>(userProfileResponse, HttpStatus.OK);
	}*/
	
	@RequestMapping(value = PROFILE_DETAILS_URI, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> userProfileDetails(@PathVariable(name = "userId", required = true) String userId,
			HttpServletRequest request, HttpServletResponse response) throws CustomException {
		LOGGER.info("INFO: UserController - userProfileDetails() :: starts");
		UserProfileResponse userProfileResponse = new UserProfileResponse();
		ErrorResponse errorResponse = new ErrorResponse();
		try {
			if (StringUtils.isNotEmpty(userId)) {
				userProfileResponse = userService.userProfileDetails(userId);
				if (userProfileResponse.getError().getCode() != ErrorCode.EC_200.code()) {
					errorResponse.setError(userProfileResponse.getError());
					return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
				}
			} else {
				return AppUtil.httpResponseForBadRequest(ErrorCode.EC_43.code());
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: UserController - userProfileDetails()", e);
			return AppUtil.httpResponseForInternalServerError();
		}
		LOGGER.info("INFO: UserController - userProfileDetails() :: ends");
		return new ResponseEntity<>(userProfileResponse, HttpStatus.OK);
	}

	/**
	 * Update the user profile details
	 * 
	 * @author Mohan
	 * @param params
	 *            the request body parameters
	 * @param userId
	 *            the user identifier
	 * @param request
	 *            the {@link HttpServletRequest}
	 * @param response
	 *            the {@link HttpServletResponse}
	 * @return the {@link ErrorResponse} details
	 * @throws CustomException
	 */
	@RequestMapping(value = PROFILE_DETAILS_URI, method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> updateUserProfile(@RequestBody(required = true) String params,
			@PathVariable(name = "userId", required = true) String userId, HttpServletRequest request,
			HttpServletResponse response) throws CustomException {
		LOGGER.info("INFO: UserController - updateUserProfile() :: starts");
		ErrorResponse errorResponse = new ErrorResponse();
		try {

			JSONObject json = new JSONObject(params);
			Boolean reminders = params.contains(AppEnums.RP_REMAINDERS.value())
					? json.getBoolean(AppEnums.RP_REMAINDERS.value())
					: null;
			String lass4u = params.contains(AppEnums.RP_LASS4U.value()) ? json.getString(AppEnums.RP_LASS4U.value())
					: "";
			String fitbit = params.contains(AppEnums.RP_FITBIT.value()) ? json.getString(AppEnums.RP_FITBIT.value())
					: "";
			String language = params.contains(AppEnums.RP_LANGUAGE.value())
					? json.getString(AppEnums.RP_LANGUAGE.value())
					: "";
			String temperature = params.contains(AppEnums.RP_TEMPERATURE.value())
					? json.getString(AppEnums.RP_TEMPERATURE.value())
					: "";

			if (StringUtils.isNotEmpty(userId)) {
				SettingsBean settings = new SettingsBean().setFitbit(fitbit).setLass4u(lass4u).setLanguage(language)
						.setTemperature(temperature).setReminders(reminders);
				errorResponse = userService.updateUserProfile(settings, userId, reminders);
				if (errorResponse.getError().getCode() != ErrorCode.EC_200.code()) {
					return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
				}
			} else {
				return AppUtil.httpResponseForBadRequest(ErrorCode.EC_43.code());
			}
		} catch (JSONException e) {
			LOGGER.error("ERROR: UserController - updateUserProfile()", e);
			return AppUtil.httpResponseForNotAcceptable(ErrorCode.EC_44.code());
		} catch (Exception e) {
			LOGGER.error("ERROR: UserController - updateUserProfile()", e);
			return AppUtil.httpResponseForInternalServerError();
		}
		LOGGER.info("INFO: UserController - updateUserProfile() :: ends");
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}

	/**
	 * Used to update new user password
	 * 
	 * @author Mohan
	 * @param params
	 *            the request body parameters
	 * @param userId
	 *            the user identifier
	 * @param request
	 *            the {@link HttpServletRequest}
	 * @param response
	 *            the {@link HttpServletResponse}
	 * @return the {@link ErrorResponse} details
	 * @throws CustomException
	 */
	@RequestMapping(value = CHANGE_PSWRD_URI, method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> changePassword(@RequestBody(required = true) String params,
			@PathVariable(name = "userId", required = true) String userId, HttpServletRequest request,
			HttpServletResponse response) throws CustomException {
		LOGGER.info("INFO: UserController - changePassword() :: starts");
		ErrorResponse errorResponse = new ErrorResponse();
		try {
			JSONObject json = new JSONObject(params);
			String oldPassword = json.getString(AppEnums.RP_OLD_PASSWORD.value());
			String newPassword = json.getString(AppEnums.RP_NEW_PASSWORD.value());

			if (StringUtils.isNotEmpty(oldPassword) && StringUtils.isNotEmpty(newPassword)
					&& StringUtils.isNotEmpty(userId)) {
				errorResponse = userService.changePassword(userId, oldPassword, newPassword);
				if (errorResponse.getError().getCode() != ErrorCode.EC_200.code()) {
					return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
				}
			} else {
				return AppUtil.httpResponseForBadRequest(ErrorCode.EC_43.code());
			}
		} catch (JSONException e) {
			LOGGER.error("ERROR: UserController - changePassword()", e);
			return AppUtil.httpResponseForNotAcceptable(ErrorCode.EC_43.code());
		} catch (Exception e) {
			LOGGER.error("ERROR: UserController - changePassword()", e);
			return AppUtil.httpResponseForInternalServerError();
		}
		LOGGER.info("INFO: UserController - changePassword() :: ends");
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}

	/**
	 * Logout the user
	 * 
	 * @author Mohan
	 * @param userId
	 *            the user identifier
	 * @param request
	 *            the {@link HttpServletRequest}
	 * @param response
	 *            the {@link HttpServletResponse}
	 * @return the {@link ErrorResponse} details
	 * @throws CustomException
	 */
	@RequestMapping(value = LOGOUT_URI, method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> logout(@PathVariable(name = "userId", required = true) String userId,
			HttpServletRequest request, HttpServletResponse response) throws CustomException {
		LOGGER.info("INFO: UserController - logout() :: starts");
		ErrorResponse errorResponse = new ErrorResponse();
		try {
			if (StringUtils.isNotEmpty(userId)) {
				errorResponse = userService.logout(userId);
				if (errorResponse.getError().getCode() != ErrorCode.EC_200.code()) {
					return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
				}
			} else {
				return AppUtil.httpResponseForBadRequest(ErrorCode.EC_43.code());
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: UserController - logout()", e);
			return AppUtil.httpResponseForInternalServerError();
		}
		LOGGER.info("INFO: UserController - logout() :: ends");
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}

	/**
	 * Fecth new accessToken for fitbit
	 * 
	 * @author Mohan
	 * @param userId
	 *            the user identifier
	 * @param request
	 *            the {@link HttpServletRequest}
	 * @param response
	 *            the {@link HttpServletResponse}
	 * @return the {@link AuthResponse} details
	 */
	@RequestMapping(value = FITBIT_REFRESH_ACCESS_TOKEN_URI, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> refreshFitbitAccessToken(
			@RequestHeader(name = "userId", required = true) String userId, HttpServletRequest request,
			HttpServletResponse response) {
		LOGGER.info("INFO: UserController - refreshFitbitAccessToken() :: starts");
		AuthResponse authResponse = null;
		ErrorResponse errorResponse = new ErrorResponse();
		try {
			if (StringUtils.isNotEmpty(userId)) {
				authResponse = userService.refreshFitbitAccessToken(userId);
				if (authResponse.getError().getCode() != ErrorCode.EC_200.code()) {
					if (authResponse.getError().getCode() == ErrorCode.EC_106.code() || authResponse.getError().getCode() == ErrorCode.EC_107.code()) {
						errorResponse = new ErrorResponse().setError(authResponse.getError());
						return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
					}else {
						errorResponse = new ErrorResponse().setError(authResponse.getError());
						return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
					}
				}
			} else {
				return AppUtil.httpResponseForBadRequest(ErrorCode.EC_43.code());
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: UserController - refreshFitbitAccessToken()", e);
			return AppUtil.httpResponseForInternalServerError();
		}
		LOGGER.info("INFO: UserController - refreshFitbitAccessToken() :: ends");
		return new ResponseEntity<>(authResponse, HttpStatus.OK);
	}

	/**
	 * Update the device token for the provided user identifier
	 * 
	 * @author Mohan
	 * @param userId
	 *            the user identifier
	 * @param params
	 *            the request body parameters
	 * @param request
	 *            the {@link HttpServletRequest} details
	 * @param response
	 *            the {@link HttpServletResponse} details
	 * @return
	 */
	@RequestMapping(value = UPDATE_DEVICE_TOKEN_URI, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> updateDeviceToken(@PathVariable(name = "userId", required = true) String userId,
			@RequestBody(required = true) String params, HttpServletRequest request, HttpServletResponse response) {
		LOGGER.info("INFO: UserController - updateDeviceToken() :: starts");
		ErrorResponse errorResponse = new ErrorResponse();
		try {

			JSONObject json = new JSONObject(params);
			String deviceToken = json.getString(AppEnums.RP_DEVICE_TOKEN.value());
			String osType = json.getString(AppEnums.RP_OS_TYPE.value());

			if (StringUtils.isNotEmpty(userId) && StringUtils.isNotEmpty(deviceToken)
					&& StringUtils.isNotEmpty(osType)) {
				errorResponse = userService.updateDeviceToken(userId, deviceToken, osType);
				if (errorResponse.getError().getCode() != ErrorCode.EC_200.code()) {
					return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
				}
			} else {
				return AppUtil.httpResponseForBadRequest(ErrorCode.EC_43.code());
			}
		} catch (JSONException e) {
			LOGGER.error("ERROR: UserController - updateDeviceToken()", e);
			return AppUtil.httpResponseForNotAcceptable(ErrorCode.EC_44.code());
		} catch (Exception e) {
			LOGGER.error("ERROR: UserController - updateDeviceToken()", e);
			return AppUtil.httpResponseForInternalServerError();
		}
		LOGGER.info("INFO: UserController - updateDeviceToken() :: ends");
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}

}
