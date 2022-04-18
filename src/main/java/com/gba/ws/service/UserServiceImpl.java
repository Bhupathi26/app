package com.gba.ws.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gba.ws.bean.AuthResponse;
import com.gba.ws.bean.ErrorBean;
import com.gba.ws.bean.ErrorResponse;
import com.gba.ws.bean.FitbitErrorResponse;
import com.gba.ws.bean.LoginResponse;
import com.gba.ws.bean.ProfileBean;
import com.gba.ws.bean.SettingsBean;
import com.gba.ws.bean.UserProfileResponse;
import com.gba.ws.dao.StudyDao;
import com.gba.ws.dao.UserDao;
import com.gba.ws.exception.CustomException;
import com.gba.ws.model.AuthInfoDto;
import com.gba.ws.model.FitbitUserInfoDto;
import com.gba.ws.model.StudiesDto;
import com.gba.ws.model.UserDto;
import com.gba.ws.model.UserStudiesDto;
import com.gba.ws.util.AppConstants;
import com.gba.ws.util.AppUtil;
import com.gba.ws.util.ErrorCode;
import com.gba.ws.util.Mail;
import com.gba.ws.util.MailContent;

/**
 * Implements {@link UserService} interface.
 * 
 * @author Mohan
 * @createdOn Jan 9, 2018 12:25:25 PM
 */
@Service
public class UserServiceImpl implements UserService {

	private static final Logger LOGGER = Logger.getLogger(UserServiceImpl.class);

	@Autowired
	private UserDao userDao;

	@Autowired
	private StudyDao studyDao;

	@Autowired
	private RestTemplate restTemplate;

	/**
	 * @param userDao
	 *            the {@link UserDao}
	 * @param studyDao
	 *            the {@link StudyDao}
	 * @param restTemplate
	 *            the {@link RestTemplate} details
	 */
	public UserServiceImpl(UserDao userDao, StudyDao studyDao, RestTemplate restTemplate) {
		super();
		this.userDao = userDao;
		this.studyDao = studyDao;
		this.restTemplate = restTemplate;
	}

	@Override
	public ErrorResponse authenticateUser(String authKey, String userId, HttpServletResponse response)
			throws CustomException {
		LOGGER.info("INFO: UserServiceImpl - authenticateUser() :: starts");
		ErrorResponse errorResponse = new ErrorResponse();
		UserDto user = null;
		AuthInfoDto authInfo = null;
		try {

			// Delete non signed-up users for study in 30 days
			//userDao.deleteNonSignedUpUsersToStudy();

			user = userDao.fetchUserDetails(userId, AppConstants.FIND_BY_TYPE_USERID);
			if (user == null) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				return errorResponse.setError(
						new ErrorBean().setCode(ErrorCode.EC_102.code()).setMessage(ErrorCode.EC_102.errorMessage()));
			}

			authInfo = userDao.fetchAuthInfoDetails(userId, AppConstants.FIND_BY_TYPE_USERID);
			if (authInfo == null) {
				if (!user.getStatus()) {
					response.setStatus(HttpServletResponse.SC_FORBIDDEN);
					return errorResponse.setError(
							new ErrorBean().setCode(ErrorCode.EC_31.code()).setMessage(ErrorCode.EC_31.errorMessage()));
				}

				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				return errorResponse.setError(
						new ErrorBean().setCode(ErrorCode.EC_61.code()).setMessage(ErrorCode.EC_61.errorMessage()));
			} else {
				errorResponse.setError(new ErrorBean().setCode(ErrorCode.EC_200.code())
						.setMessage(AppUtil.getAppProperties().get(AppConstants.USER_LOGIN_SUCCESS)));
				if (StringUtils.isEmpty(authInfo.getAuthKey())) {
					errorResponse.setError(new ErrorBean().setCode(ErrorCode.EC_101.code())
							.setMessage(ErrorCode.EC_101.errorMessage()));
					response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				} else if (!authKey.equals(authInfo.getAuthKey())) {
					errorResponse.setError(new ErrorBean().setCode(ErrorCode.EC_101.code())
							.setMessage(ErrorCode.EC_101.errorMessage()));
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				} else {

					// Check user session is expired or not
					if (AppConstants.SDF_DATE_TIME.parse(AppUtil.getCurrentDateTime())
							.before(AppConstants.SDF_DATE_TIME.parse(authInfo.getSessionExpiredDate()))) {
						authInfo.setSessionExpiredDate(AppUtil.addMinutes(AppUtil.getCurrentDateTime(),
								Integer.parseInt(AppUtil.getAppProperties().get(AppConstants.USER_SESSION_TIMEOUT))))
								.setModifiedOn(AppUtil.getCurrentDateTime());
						userDao.saveOrUpdateAuthInfoDetails(authInfo, AppConstants.DB_UPDATE);
					} else {
						errorResponse.setError(new ErrorBean().setCode(ErrorCode.EC_101.code())
								.setMessage(ErrorCode.EC_101.errorMessage()));
						response.setStatus(HttpServletResponse.SC_FORBIDDEN);
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: UserServiceImpl - authenticateUser()", e);
		}
		LOGGER.info("INFO: UserServiceImpl - authenticateUser() :: ends");
		return errorResponse;
	}

	@Override
	public AuthResponse validateSessionAuthKey(String sessionAuthKey, HttpServletResponse response)
			throws CustomException {
		LOGGER.info("INFO: UserServiceImpl - validateSessionAuthKey() :: starts");
		AuthResponse authResponse = new AuthResponse();
		AuthInfoDto authInfo = null;
		UserDto user = null;
		try {
			authInfo = userDao.fetchAuthInfoDetails(sessionAuthKey, AppConstants.FIND_BY_TYPE_SESSION_AUTH_KEY);
			if (authInfo == null) {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				return new AuthResponse().setError(
						new ErrorBean().setCode(ErrorCode.EC_102.code()).setMessage(ErrorCode.EC_102.errorMessage()));
			}

			user = userDao.fetchUserDetails(String.valueOf(authInfo.getUserId()), AppConstants.FIND_BY_TYPE_USERID);
			if (user == null) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				return new AuthResponse().setError(
						new ErrorBean().setCode(ErrorCode.EC_61.code()).setMessage(ErrorCode.EC_61.errorMessage()));
			}

			user.setModifiedOn(AppUtil.getCurrentDateTime());
			userDao.saveOrUpdateUserDetails(user, AppConstants.DB_UPDATE);

			// Update the new auth_key generated(temporary key)
			authInfo.setAuthKey(RandomStringUtils.randomNumeric(AppConstants.AUTH_KEY_LENGTH))
					.setModifiedOn(AppUtil.getCurrentDateTime())
					.setSessionExpiredDate(AppUtil.addMinutes(AppUtil.getCurrentDateTime(),
							Integer.parseInt(AppUtil.getAppProperties().get(AppConstants.USER_SESSION_TIMEOUT))));
			authInfo = userDao.saveOrUpdateAuthInfoDetails(authInfo, AppConstants.DB_UPDATE);

			authResponse.setAccessToken(authInfo.getAuthKey()).setRefreshToken(authInfo.getSessionAuthKey())
					.setUserId(String.valueOf(authInfo.getUserId()))
					.setError(new ErrorBean().setCode(ErrorCode.EC_200.code())
							.setMessage(AppUtil.getAppProperties().get(AppConstants.USER_AUTHKEY_UPD)));
		} catch (Exception e) {
			LOGGER.error("ERROR: UserServiceImpl - validateSessionAuthKey()", e);
		}
		LOGGER.info("INFO: UserServiceImpl - validateSessionAuthKey() :: ends");
		return authResponse;
	}

	@Override
	public boolean validateEmail(String email) throws CustomException {
		LOGGER.info("INFO: UserServiceImpl - validateEmail() :: starts");
		boolean isInvalidValid = false;
		try {
			isInvalidValid = userDao.validateEmail(email);
		} catch (Exception e) {
			LOGGER.error("ERROR: UserServiceImpl - validateEmail()", e);
		}
		LOGGER.info("INFO: UserServiceImpl - validateEmail() :: ends");
		return isInvalidValid;
	}

	@Override
	public AuthResponse userRegistration(UserDto user, String authorization) throws CustomException {
		LOGGER.info("INFO: UserServiceImpl - userRegistration() :: starts");
		AuthResponse authResponse = new AuthResponse();
		AuthInfoDto authInfo = new AuthInfoDto();
		UserDto saveUser = new UserDto();
		StudiesDto studyDto = new StudiesDto();
		try {
			if (user != null && StringUtils.isNotEmpty(authorization)) {

				user.setVerificationKey(RandomStringUtils.randomNumeric(AppConstants.VERIFICATION_KEY_LENGTH))
						.setTemperature(AppConstants.TEMPERATURE_CELCIUS).setStatus(true).setTempPassword(false)
						.setCreatedOn(AppUtil.getCurrentDateTime())
						.setPasswordUpdatedDate(AppUtil.getCurrentDateTime());
				saveUser = userDao.saveOrUpdateUserDetails(user, AppConstants.DB_SAVE);

				authInfo.setUserId(saveUser.getUserId())
						.setAuthKey(RandomStringUtils.randomNumeric(AppConstants.AUTH_KEY_LENGTH))
						.setCreatedOn(AppUtil.getCurrentDateTime()).setDeviceType(AppUtil.platformType(authorization))
						.setSessionAuthKey(AppUtil.uniqueUUID())
						.setSessionExpiredDate(AppUtil.addMinutes(AppUtil.getCurrentDateTime(),
								Integer.parseInt(AppUtil.getAppProperties().get(AppConstants.USER_SESSION_TIMEOUT))));
				authInfo = userDao.saveOrUpdateAuthInfoDetails(authInfo, AppConstants.DB_SAVE);

				authResponse.setUserId(String.valueOf(authInfo.getUserId())).setAccessToken(authInfo.getAuthKey())
						.setRefreshToken(authInfo.getSessionAuthKey())
						.setError(new ErrorBean().setCode(ErrorCode.EC_200.code())
								.setMessage(AppUtil.getAppProperties().get(AppConstants.USER_REGISTRATION_SUCCESS)));

				studyDto = studyDao.fetchStudyDetailsByStudyId(AppConstants.DEFAULT_STUDY_ID);

				// Participant enters an email during sign up and submits it for verification
				Map<String, String> mailContentMap = MailContent.emailVerificationContent(saveUser.getFirstName(),
						saveUser.getVerificationKey(), studyDto.getStudyName(), saveUser.getLanguage());
				Mail.sendEmail(saveUser.getEmail(), mailContentMap.get(AppConstants.MAIL_SUBJECT),
						mailContentMap.get(AppConstants.MAIL_BODY));
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: UserServiceImpl - userRegistration()", e);
		}
		LOGGER.info("INFO: UserServiceImpl - userRegistration() :: ends");
		return authResponse;
	}

	@Override
	public LoginResponse login(String email, String password) throws CustomException {
		LOGGER.info("INFO: UserServiceImpl - login() :: starts");
		LoginResponse loginResponse = new LoginResponse();
		UserDto user = null;
		try {
			user = userDao.fetchUserDetails(email, AppConstants.FIND_BY_TYPE_EMAIL);
			if (user == null) {
				return new LoginResponse().setError(
						new ErrorBean().setCode(ErrorCode.EC_71.code()).setMessage(ErrorCode.EC_71.errorMessage()));
			}

			if (!user.getStatus()) {
				loginResponse.setError(
						new ErrorBean().setCode(ErrorCode.EC_31.code()).setMessage(ErrorCode.EC_31.errorMessage()));
			} else if (AppUtil.compareEncryptedPassword(user.getUserPassword(), password)
					|| AppUtil.compareEncryptedPassword(user.getResetPassword(), password)) {
				
				System.out.println("Password");
				return this.getLoginResponse(user, password);
			} else {
				loginResponse.setError(
						new ErrorBean().setCode(ErrorCode.EC_33.code()).setMessage(ErrorCode.EC_33.errorMessage()));
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: UserServiceImpl - login()", e);
		}
		LOGGER.info("INFO: UserServiceImpl - login() :: ends");
		return loginResponse;
	}

	@Override
	public ErrorResponse emailVerification(String userId, String authKey, String token) throws CustomException {
		LOGGER.info("INFO: UserServiceImpl - emailVerification() :: starts");
		ErrorResponse errorResponse = new ErrorResponse();
		UserDto user = null;
		try {
			user = userDao.fetchUserDetails(userId, AppConstants.FIND_BY_TYPE_USERID);
			if (user == null) {
				return new ErrorResponse().setError(
						new ErrorBean().setCode(ErrorCode.EC_61.code()).setMessage(ErrorCode.EC_61.errorMessage()));
			}

			if (StringUtils.isEmpty(user.getVerificationKey()) || !user.getVerificationKey().equals(token)) {
				errorResponse.setError(
						new ErrorBean().setCode(ErrorCode.EC_52.code()).setMessage(ErrorCode.EC_52.errorMessage()));
			} else {
				user.setVerificationKey(null).setModifiedOn(AppUtil.getCurrentDateTime());
				userDao.saveOrUpdateUserDetails(user, AppConstants.DB_UPDATE);

				errorResponse.setError(new ErrorBean().setCode(ErrorCode.EC_200.code())
						.setMessage(AppUtil.getAppProperties().get(AppConstants.USER_EMAIL_VERIFIED)));
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: UserServiceImpl - emailVerification()", e);
		}
		LOGGER.info("INFO: UserServiceImpl - emailVerification() :: ends");
		return errorResponse;
	}

	@Override
	public ErrorResponse resendVerificationToken(String userId) throws CustomException {
		LOGGER.info("INFO: UserServiceImpl - resendVerificationToken() :: starts");
		ErrorResponse errorResponse = new ErrorResponse();
		UserDto user = null;
		StudiesDto studyDto = null;
		try {
			user = userDao.fetchUserDetails(userId, AppConstants.FIND_BY_TYPE_USERID);
			if (user == null) {
				return new ErrorResponse().setError(
						new ErrorBean().setCode(ErrorCode.EC_61.code()).setMessage(ErrorCode.EC_61.errorMessage()));
			}

			user.setVerificationKey(RandomStringUtils.randomNumeric(AppConstants.VERIFICATION_KEY_LENGTH))
					.setModifiedOn(AppUtil.getCurrentDateTime());
			user = userDao.saveOrUpdateUserDetails(user, AppConstants.DB_UPDATE);

			errorResponse.setError(new ErrorBean().setCode(ErrorCode.EC_200.code())
					.setMessage(AppUtil.getAppProperties().get(AppConstants.USER_RESEND_TOKEN)));

			studyDto = studyDao.fetchStudyDetailsByStudyId(AppConstants.DEFAULT_STUDY_ID);

			// Participant enters an email during sign up and submits it for verification
			Map<String, String> mailContentMap = MailContent.emailVerificationContent(user.getFirstName(),
					user.getVerificationKey(), studyDto.getStudyName(), user.getLanguage());
			Mail.sendEmail(user.getEmail(), mailContentMap.get(AppConstants.MAIL_SUBJECT),
					mailContentMap.get(AppConstants.MAIL_BODY));
		} catch (Exception e) {
			LOGGER.error("ERROR: UserServiceImpl - resendVerificationToken()", e);
		}
		LOGGER.info("INFO: UserServiceImpl - resendVerificationToken() :: ends");
		return errorResponse;
	}

	@Override
	public ErrorResponse forgotPassword(String email) throws CustomException {
		LOGGER.info("INFO: UserServiceImpl - forgotPassword() :: starts");
		ErrorResponse errorResponse = new ErrorResponse();
		UserDto user = null;
		String tempPassword = null;
		try {
			user = userDao.fetchUserDetails(email, AppConstants.FIND_BY_TYPE_EMAIL);
			if (user == null) {
				return new ErrorResponse().setError(
						new ErrorBean().setCode(ErrorCode.EC_71.code()).setMessage(ErrorCode.EC_71.errorMessage()));
			}

			tempPassword = AppUtil.randomAlphanumeric();
			if(StringUtils.isNotBlank(tempPassword)) {
				user.setResetPassword(AppUtil.getEncryptedPassword(tempPassword.trim())).setTempPassword(true)
				.setModifiedOn(AppUtil.getCurrentDateTime()).setPasswordUpdatedDate(AppUtil.getCurrentDateTime())
				.setTempPasswordExipiryDate(
						AppUtil.addDays(AppUtil.getCurrentDateTime(),AppConstants.SDF_DATE_TIME_FORMAT ,3, AppConstants.SDF_DATE_FORMAT));
		user = userDao.saveOrUpdateUserDetails(user, AppConstants.DB_UPDATE);

		errorResponse.setError(new ErrorBean().setCode(ErrorCode.EC_200.code())
				.setMessage(AppUtil.getAppProperties().get(AppConstants.USER_FORGOT_PSWD)));

		// Participant clicks on forgot password and enters valid email id
		Map<String, String> mailContentMap = MailContent.forgotPasswordContent(user.getFirstName(), tempPassword,
				user.getLanguage());
		Mail.sendEmail(user.getEmail(), mailContentMap.get(AppConstants.MAIL_SUBJECT),
				mailContentMap.get(AppConstants.MAIL_BODY));
			}
		
		} catch (Exception e) {
			LOGGER.error("ERROR: UserServiceImpl - forgotPassword()", e);
		}
		LOGGER.info("INFO: UserServiceImpl - forgotPassword() :: ends");
		return errorResponse;
	}

/*	@Override
	public UserProfileResponse userProfileDetails(String userId) throws CustomException {
		LOGGER.info("INFO: UserServiceImpl - userProfileDetails() :: starts");
		UserProfileResponse userProfileResponse = new UserProfileResponse();
		UserDto user = null;
		AuthInfoDto authInfo = null;
		FitbitUserInfoDto fitbitUserInfoDto = null;
		try {
			user = userDao.fetchUserDetails(userId, AppConstants.FIND_BY_TYPE_USERID);
			if (user == null) {
				return new UserProfileResponse().setError(
						new ErrorBean().setCode(ErrorCode.EC_61.code()).setMessage(ErrorCode.EC_61.errorMessage()));
			}

			authInfo = userDao.fetchAuthInfoDetails(String.valueOf(user.getUserId()), AppConstants.FIND_BY_TYPE_USERID);
			if (authInfo == null) {
				return new UserProfileResponse().setError(
						new ErrorBean().setCode(ErrorCode.EC_61.code()).setMessage(ErrorCode.EC_61.errorMessage()));
			}

			userProfileResponse.setProfile(new ProfileBean().setGroupId(user.getGroupId())
					.setEmail(user.getEmail()).setFirstName(user.getFirstName()).setLastName(user.getLastName()));
			userProfileResponse.setSettings(
					new SettingsBean().setFitbit(StringUtils.isEmpty(user.getFitbitId()) ? "" : user.getFitbitId())
							.setLass4u(StringUtils.isEmpty(user.getLass4uId()) ? "" : user.getLass4uId())
							.setLanguage(user.getLanguage()).setReminders(user.getReceiveActivityReminders())
							.setTemperature(user.getTemperature()).setFitbitAuthRedirectionUrl(
									AppUtil.getAppProperties().get(AppConstants.FITBIT_REDIRECTION_URL)
											+ authInfo.getSessionAuthKey()));

			// Get the fitbit user info for the provided user identifier
			fitbitUserInfoDto = userDao.fetchFitbitUserInfo(userId, AppConstants.FIND_BY_TYPE_USERID);
			if (fitbitUserInfoDto != null) {
				userProfileResponse.getSettings()
						.setFitbitAccessToken(StringUtils.isEmpty(fitbitUserInfoDto.getFitbitAccessToken()) ? ""
								: fitbitUserInfoDto.getFitbitAccessToken())
						.setFitbitRefreshtoken(StringUtils.isEmpty(fitbitUserInfoDto.getFitbitRefreshToken()) ? ""
								: fitbitUserInfoDto.getFitbitRefreshToken());
			}

			userProfileResponse.setError(new ErrorBean().setCode(ErrorCode.EC_200.code())
					.setMessage(AppUtil.getAppProperties().get(AppConstants.USER_FETCH_UPD)));
		} catch (Exception e) {
			LOGGER.error("ERROR: UserServiceImpl - userProfileDetails()", e);
		}
		LOGGER.info("INFO: UserServiceImpl - userProfileDetails() :: ends");
		return userProfileResponse;
	}
*/
	@Override
	public UserProfileResponse userProfileDetails(String userId) throws CustomException {
		LOGGER.info("INFO: UserServiceImpl - userProfileDetails() :: starts");
		UserProfileResponse userProfileResponse = new UserProfileResponse();
		UserDto user = null;
		AuthInfoDto authInfo = null;
		FitbitUserInfoDto fitbitUserInfoDto = null;
		try {
			user = userDao.fetchUserDetails(userId, AppConstants.FIND_BY_TYPE_USERID);
			if (user == null) {
				return new UserProfileResponse().setError(
						new ErrorBean().setCode(ErrorCode.EC_61.code()).setMessage(ErrorCode.EC_61.errorMessage()));
			}

			authInfo = userDao.fetchAuthInfoDetails(String.valueOf(user.getUserId()), AppConstants.FIND_BY_TYPE_USERID);
			if (authInfo == null) {
				return new UserProfileResponse().setError(
						new ErrorBean().setCode(ErrorCode.EC_61.code()).setMessage(ErrorCode.EC_61.errorMessage()));
			}

			userProfileResponse.setProfile(new ProfileBean()
					.setEmail(user.getEmail()).setFirstName(user.getFirstName()).setLastName(user.getLastName()).setTimeZone(user.getTimeZone()));
			userProfileResponse.setSettings(
					new SettingsBean().setFitbit(StringUtils.isEmpty(user.getFitbitId()) ? "" : user.getFitbitId())
							.setLass4u(StringUtils.isEmpty(user.getLass4uId()) ? "" : user.getLass4uId())
							.setLanguage(user.getLanguage()).setReminders(user.getReceiveActivityReminders())
							.setTemperature(user.getTemperature()).setFitbitAuthRedirectionUrl(
									AppUtil.getAppProperties().get(AppConstants.FITBIT_REDIRECTION_URL)
											+ authInfo.getSessionAuthKey()));

			// Get the fitbit user info for the provided user identifier
			fitbitUserInfoDto = userDao.fetchFitbitUserInfo(userId, AppConstants.FIND_BY_TYPE_USERID);
			if (fitbitUserInfoDto != null) {
				userProfileResponse.getSettings()
						.setFitbitAccessToken(StringUtils.isEmpty(fitbitUserInfoDto.getFitbitAccessToken()) ? ""
								: fitbitUserInfoDto.getFitbitAccessToken())
						.setFitbitRefreshtoken(StringUtils.isEmpty(fitbitUserInfoDto.getFitbitRefreshToken()) ? ""
								: fitbitUserInfoDto.getFitbitRefreshToken());
			}

			userProfileResponse.setError(new ErrorBean().setCode(ErrorCode.EC_200.code())
					.setMessage(AppUtil.getAppProperties().get(AppConstants.USER_FETCH_UPD)));
		} catch (Exception e) {
			LOGGER.error("ERROR: UserServiceImpl - userProfileDetails()", e);
		}
		LOGGER.info("INFO: UserServiceImpl - userProfileDetails() :: ends");
		return userProfileResponse;
	}

	@Override
	public ErrorResponse updateUserProfile(SettingsBean settings, String userId, Boolean reminders)
			throws CustomException {
		LOGGER.info("INFO: UserServiceImpl - updateUserProfile() :: starts");
		ErrorResponse errorResponse = new ErrorResponse();
		UserDto user = null;
		try {
			user = userDao.fetchUserDetails(userId, AppConstants.FIND_BY_TYPE_USERID);
			if (user == null) {
				return new ErrorResponse().setError(
						new ErrorBean().setCode(ErrorCode.EC_61.code()).setMessage(ErrorCode.EC_61.errorMessage()));
			}

//			user.setLass4uId(StringUtils.isEmpty(settings.getLass4u()) ? user.getLass4uId() : settings.getLass4u())
//					.setFitbitId(StringUtils.isEmpty(settings.getFitbit()) ? user.getFitbitId() : settings.getFitbit())
//					.setLanguage(
//							StringUtils.isEmpty(settings.getLanguage()) ? user.getLanguage() : settings.getLanguage())
//					.setTemperature(StringUtils.isEmpty(settings.getTemperature()) ? user.getTemperature()
//							: settings.getTemperature())
//					.setReceiveActivityReminders(reminders == null ? user.getReceiveActivityReminders() : reminders)
//					.setModifiedOn(AppUtil.getCurrentDateTime());
			
			userDao.saveOrUpdateUserDetails(user, AppConstants.DB_UPDATE);

			errorResponse.setError(new ErrorBean().setCode(ErrorCode.EC_200.code())
					.setMessage(AppUtil.getAppProperties().get(AppConstants.USER_UPDATE_UPD)));
		} catch (Exception e) {
			LOGGER.error("ERROR: UserServiceImpl - updateUserProfile()", e);
		}
		LOGGER.info("INFO: UserServiceImpl - updateUserProfile() :: ends");
		return errorResponse;
	}

	@Override
	public ErrorResponse changePassword(String userId, String oldPassword, String newPassword) throws CustomException {
		LOGGER.info("INFO: UserServiceImpl - changePassword() :: starts");
		ErrorResponse errorResponse = new ErrorResponse();
		UserDto user = null;
		try {
			user = userDao.fetchUserDetails(userId, AppConstants.FIND_BY_TYPE_USERID);
			if (user == null) {
				return new ErrorResponse().setError(
						new ErrorBean().setCode(ErrorCode.EC_61.code()).setMessage(ErrorCode.EC_61.errorMessage()));
			}

			if (!AppUtil.compareEncryptedPassword(user.getUserPassword(), oldPassword)
					&& !AppUtil.compareEncryptedPassword(user.getResetPassword(), oldPassword)) {
				errorResponse.setError(
						new ErrorBean().setCode(ErrorCode.EC_73.code()).setMessage(ErrorCode.EC_73.errorMessage()));
			} else if (AppUtil.compareEncryptedPassword(user.getUserPassword(), newPassword)
					|| AppUtil.compareEncryptedPassword(user.getResetPassword(), newPassword)) {
				errorResponse.setError(
						new ErrorBean().setCode(ErrorCode.EC_72.code()).setMessage(ErrorCode.EC_72.errorMessage()));
			} else {
				user.setUserPassword(AppUtil.getEncryptedPassword(newPassword))
						.setModifiedOn(AppUtil.getCurrentDateTime())
						.setPasswordUpdatedDate(AppUtil.getCurrentDateTime()).setTempPassword(false)
						.setResetPassword(null).setTempPasswordExipiryDate(null);
				userDao.saveOrUpdateUserDetails(user, AppConstants.DB_UPDATE);

				errorResponse.setError(new ErrorBean().setCode(ErrorCode.EC_200.code())
						.setMessage(AppUtil.getAppProperties().get(AppConstants.USER_UPDATE_PSWD)));
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: UserServiceImpl - changePassword()", e);
		}
		LOGGER.info("INFO: UserServiceImpl - changePassword() :: ends");
		return errorResponse;
	}

	@Override
	public ErrorResponse logout(String userId) throws CustomException {
		LOGGER.info("INFO: UserServiceImpl - logout() :: starts");
		ErrorResponse errorResponse = new ErrorResponse();
		UserDto user = null;
		AuthInfoDto authInfo = null;
		try {
			user = userDao.fetchUserDetails(userId, AppConstants.FIND_BY_TYPE_USERID);
			if (user == null) {
				return new ErrorResponse().setError(
						new ErrorBean().setCode(ErrorCode.EC_61.code()).setMessage(ErrorCode.EC_61.errorMessage()));
			}

			authInfo = userDao.fetchAuthInfoDetails(String.valueOf(user.getUserId()), AppConstants.FIND_BY_TYPE_USERID);
			if (authInfo == null) {
				return new ErrorResponse().setError(
						new ErrorBean().setCode(ErrorCode.EC_61.code()).setMessage(ErrorCode.EC_61.errorMessage()));
			}

			// Update auth info details i.e. when user logout remove temporary and long term
			// auth_key from the auth_info (user logged out by own)
			authInfo.setAuthKey(null).setSessionExpiredDate(AppUtil.getCurrentDateTime())
					.setModifiedOn(AppUtil.getCurrentDateTime()).setSessionAuthKey(null);
			userDao.saveOrUpdateAuthInfoDetails(authInfo, AppConstants.DB_UPDATE);

			// Update user info details
			user.setModifiedOn(AppUtil.getCurrentDateTime());
			userDao.saveOrUpdateUserDetails(user, AppConstants.DB_UPDATE);

			errorResponse.setError(new ErrorBean().setCode(ErrorCode.EC_200.code())
					.setMessage(AppUtil.getAppProperties().get(AppConstants.USER_LOGOUT_SUCCESS)));
		} catch (Exception e) {
			LOGGER.error("ERROR: UserServiceImpl - logout()", e);
		}
		LOGGER.info("INFO: UserServiceImpl - logout() :: ends");
		return errorResponse;
	}

	/**
	 * Get the user metadata details for the provided user identifier
	 * 
	 * @author Mohan
	 * @param user
	 *            the {@link UserDto} details
	 * @param password
	 *            the login passowrd
	 * @return the {@link LoginResponse} details
	 * @throws CustomException
	 */
	public LoginResponse getLoginResponse(UserDto user, String password) throws CustomException {
		LOGGER.info("INFO: UserServiceImpl - getLoginResponse() :: ends");
		LoginResponse loginResponse = new LoginResponse();
		AuthInfoDto authInfo = null;
		boolean isValidLogin = false;
		UserStudiesDto userStudies = null;
		FitbitUserInfoDto fitbitUserInfo = null;
		try {
			if (AppUtil.compareEncryptedPassword(user.getResetPassword(), password)) {

				// Validate is't temp-password is expired or not
		/*		if (AppConstants.SDF_DATE_TIME.parse(AppUtil.getCurrentDateTime())
						.before(AppConstants.SDF_DATE_TIME.parse(user.getTempPasswordExipiryDate()))) {*/
				if (LocalDate
						.parse(user.getTempPasswordExipiryDate(),
								DateTimeFormatter.ofPattern(AppConstants.SDF_DATE_FORMAT)).isAfter(LocalDate.parse(AppUtil.getCurrentDate(), DateTimeFormatter.ofPattern(AppConstants.SDF_DATE_FORMAT)))) {
					isValidLogin = true;
				}
			} else {
				isValidLogin = true;
			}

			if (!isValidLogin) {
				return new LoginResponse().setError(
						new ErrorBean().setCode(ErrorCode.EC_33.code()).setMessage(ErrorCode.EC_33.errorMessage()));
			}

			
			if(isValidLogin) {
				authInfo = userDao.fetchAuthInfoDetails(String.valueOf(user.getUserId()), AppConstants.FIND_BY_TYPE_USERID);
				authInfo.setAuthKey(RandomStringUtils.randomNumeric(AppConstants.AUTH_KEY_LENGTH))
						.setModifiedOn(AppUtil.getCurrentDateTime()).setSessionAuthKey(AppUtil.uniqueUUID())
						.setSessionExpiredDate(AppUtil.addMinutes(AppUtil.getCurrentDateTime(),
								Integer.parseInt(AppUtil.getAppProperties().get(AppConstants.USER_SESSION_TIMEOUT))));
				authInfo = userDao.saveOrUpdateAuthInfoDetails(authInfo, AppConstants.DB_UPDATE);

				loginResponse.setError(new ErrorBean().setCode(ErrorCode.EC_200.code())
						.setMessage(AppUtil.getAppProperties().get(AppConstants.USER_LOGIN_SUCCESS)));
				loginResponse.setAccessToken(authInfo.getAuthKey()).setUserId(String.valueOf(user.getUserId()))
						.setRefreshToken(authInfo.getSessionAuthKey())
						.setLass4uSetup(StringUtils.isEmpty(user.getLass4uId()) ? false : true)
						.setVerified(StringUtils.isEmpty(user.getVerificationKey()) ? true : false)
						.setLanguage(user.getLanguage());

				// Check fitbit set-up is done
				fitbitUserInfo = userDao.fetchFitbitUserInfo(String.valueOf(user.getUserId()),
						AppConstants.FIND_BY_TYPE_USERID);
				if (fitbitUserInfo != null) {
					loginResponse.setFitbitSetup(true);
				}

				// Check whether the user is enrolled to study
				userStudies = studyDao.fetchUserStudiesDetails(String.valueOf(user.getUserId()),
						AppConstants.DEFAULT_STUDY_ID, AppConstants.FIND_BY_TYPE_USERID_STUDYID);
				if (userStudies != null) {
					loginResponse.setEnrolled(true).setEnrollmentId(
							StringUtils.isEmpty(userStudies.getEnrollmentId()) ? "" : userStudies.getEnrollmentId());
				}

				// Check is't user logged in with old password/temporary password
				if (!AppUtil.compareEncryptedPassword(user.getUserPassword(), password)) {
					loginResponse.setTempPassword(true);
				}
			}
	
		} catch (Exception e) {
			LOGGER.error("ERROR: UserServiceImpl - getLoginResponse()", e);
		}
		LOGGER.info("INFO: UserServiceImpl - getLoginResponse() :: ends");
		return loginResponse;
	}

	@SuppressWarnings("unchecked")
	@Override
	public AuthResponse refreshFitbitAccessToken(String userId) throws CustomException {
		LOGGER.info("INFO: UserServiceImpl - refreshFitbitAccessToken() :: Starts");
		AuthResponse authResponse = new AuthResponse();
		FitbitUserInfoDto fitbitUserInfo = null;
		try {
			LOGGER.info("UserServiceImpl - refreshFitbitAccessToken() :: userId ==> " + userId);
			/*authResponse.setError(
					new ErrorBean().setCode(ErrorCode.EC_107.code()).setMessage(ErrorCode.EC_107.errorMessage()));*/
			authResponse.setError(new ErrorBean().setCode(ErrorCode.EC_106.code())
					.setMessage(ErrorCode.EC_106.errorMessage()));
			
			fitbitUserInfo = userDao.fetchFitbitUserInfo(userId, AppConstants.FIND_BY_TYPE_USERID);
			if (fitbitUserInfo == null) {
				LOGGER.info("UserServiceImpl - refreshFitbitAccessToken() :: fitbitUserInfo ==> is NULL");
				return authResponse;
			}
			LOGGER.info("UserServiceImpl - refreshFitbitAccessToken() :: fitbitUserInfo ==> is not NULL");
			ResponseEntity<Object> responseEntity = null;
			HttpHeaders headers = new HttpHeaders();
			headers.add(AppConstants.HEADER_KEY_AUTHORIZATION,
					AppUtil.getAppProperties().get(AppConstants.FITBIT_RT_HEADER_AUTHORIZATION));
			headers.add(AppConstants.HEADER_KEY_CONTENT_TYPE,
					AppUtil.getAppProperties().get(AppConstants.FITBIT_RT_HEADER_CONTENT_TYPE));
			// Authorization = Basic MjJDSlFEOjZmMjJiMTczOWQyZGYxOTc4ZTNkMjcxYzAxM2U5Mzc0
			// Content-Type = application/x-www-form-urlencoded
			// grant_type = refresh_token
			// refresh_token = is user specific and get it from DB
			
			
			// Create the request body as a MultiValueMap
			MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
			body.add(AppConstants.HEADER_KEY_GRANT_TYPE,
					AppUtil.getAppProperties().get(AppConstants.FITBIT_RT_HEADER_GRANT_TYPE));
			body.add(AppConstants.HEADER_KEY_REFRESH_TOKEN, fitbitUserInfo.getFitbitRefreshToken());
			LOGGER.info("UserServiceImpl - refreshFitbitAccessToken() :: USER FITBIT REFRESH TOKEN FROM DB ==> " + fitbitUserInfo.getFitbitRefreshToken());

			HttpEntity<Object> entity = new HttpEntity<>(body, headers);
			try {
				LOGGER.info("UserServiceImpl - refreshFitbitAccessToken() :: FITBIT_RT_URL ==> https://api.fitbit.com/oauth2/token");
				/*responseEntity = restTemplate.exchange(AppUtil.getAppProperties().get(AppConstants.FITBIT_RT_URL),
						HttpMethod.POST, entity, Object.class);*/
				RestTemplate newRestTemplate = new RestTemplate();
				UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(AppUtil.getAppProperties().get(AppConstants.FITBIT_RT_URL));
				responseEntity = newRestTemplate.exchange(builder.build().encode().toUri(),
						HttpMethod.POST, entity, Object.class);
				
				LOGGER.info("UserServiceImpl - refreshFitbitAccessToken() :: After calling FITBIT oauth2 API");
				if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
					LOGGER.info("UserServiceImpl - refreshFitbitAccessToken() :: Http Status is OK");
					LinkedHashMap<String, Object> respBodyMap = (LinkedHashMap<String, Object>) responseEntity
							.getBody();

					// Update the fitbit user info details
					fitbitUserInfo.setModifiedOn(AppUtil.getCurrentDateTime());
					LOGGER.info("UserServiceImpl - refreshFitbitAccessToken() :: access_token ==> " + respBodyMap.get("access_token").toString());
					LOGGER.info("UserServiceImpl - refreshFitbitAccessToken() :: refresh_token ==> " + respBodyMap.get("refresh_token").toString());
					fitbitUserInfo.setFitbitAccessToken(respBodyMap.get("access_token").toString());
					fitbitUserInfo.setFitbitRefreshToken(respBodyMap.get("refresh_token").toString());
					
					LOGGER.info("UserServiceImpl - refreshFitbitAccessToken() :: Before userDao.saveOrUpdateFitbitUserInfoDetails()");
					fitbitUserInfo = userDao.saveOrUpdateFitbitUserInfoDetails(fitbitUserInfo, AppConstants.DB_UPDATE);
					LOGGER.info("UserServiceImpl - refreshFitbitAccessToken() :: After userDao.saveOrUpdateFitbitUserInfoDetails()");
					
					authResponse.setAccessToken(fitbitUserInfo.getFitbitAccessToken())
							.setRefreshToken(fitbitUserInfo.getFitbitRefreshToken())
							.setUserId(String.valueOf(fitbitUserInfo.getUserId())).setError(new ErrorBean()
									.setCode(ErrorCode.EC_200.code()).setMessage(ErrorCode.EC_200.errorMessage()));
				} else {
					if(responseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
						ObjectMapper mapper = new ObjectMapper();
						FitbitErrorResponse fitbitErrorResponse = mapper.readValue(responseEntity.getBody().toString(),
								FitbitErrorResponse.class);
						if (fitbitErrorResponse != null && !fitbitErrorResponse.getErrors().isEmpty()
								&& fitbitErrorResponse.getErrors().get(0).getErrorType().equals("invalid_grant")) {
						LOGGER.info("UserServiceImpl - refreshFitbitAccessToken() :: Http Status is NOT OK");
						LOGGER.error("ERROR: UserServiceImpl - refreshFitbitAccessToken() ==> invalid_grant ");
						LOGGER.info("UserServiceImpl - refreshFitbitAccessToken() :: Before userDao.deleteFitBitUserInfo()");
						userDao.deleteFitBitUserInfo(userId, AppConstants.FIND_BY_TYPE_USERID);
						LOGGER.info("UserServiceImpl - refreshFitbitAccessToken() :: After userDao.deleteFitBitUserInfo()");
						authResponse.setError(new ErrorBean().setCode(ErrorCode.EC_106.code())
								.setMessage(ErrorCode.EC_106.errorMessage()));
						}else {
							authResponse.setError(new ErrorBean().setCode(ErrorCode.EC_107.code())
									.setMessage(ErrorCode.EC_107.errorMessage()));
						}
					}else {
						authResponse.setError(new ErrorBean().setCode(ErrorCode.EC_107.code())
								.setMessage(ErrorCode.EC_107.errorMessage()));
					}
				}
			} catch (HttpStatusCodeException httpsce) {
				LOGGER.error("ERROR: UserServiceImpl - refreshFitbitAccessToken()", httpsce);
				ObjectMapper mapper = new ObjectMapper();
				FitbitErrorResponse fitbitErrorResponse = mapper.readValue(httpsce.getResponseBodyAsString(),
						FitbitErrorResponse.class);
				if (fitbitErrorResponse != null && !fitbitErrorResponse.getErrors().isEmpty()
						&& fitbitErrorResponse.getErrors().get(0).getErrorType().equals("invalid_grant")) {
					LOGGER.error("ERROR: UserServiceImpl - refreshFitbitAccessToken() ==> invalid_grant ");
					// Delete fitbit info
					LOGGER.info("UserServiceImpl - refreshFitbitAccessToken() :: Before userDao.deleteFitBitUserInfo()");
					userDao.deleteFitBitUserInfo(userId, AppConstants.FIND_BY_TYPE_USERID);
					LOGGER.info("UserServiceImpl - refreshFitbitAccessToken() :: After userDao.deleteFitBitUserInfo()");
					authResponse.setError(new ErrorBean().setCode(ErrorCode.EC_106.code())
							.setMessage(ErrorCode.EC_106.errorMessage()));
				}else {
					authResponse.setError(new ErrorBean().setCode(ErrorCode.EC_107.code())
							.setMessage(ErrorCode.EC_107.errorMessage()));
				}
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: UserServiceImpl - refreshFitbitAccessToken()", e);
		}
		LOGGER.info("INFO: UserServiceImpl - refreshFitbitAccessToken() :: ends");
		return authResponse;
	}

	@Override
	public ErrorResponse updateDeviceToken(String userId, String deviceToken, String osType) {
		LOGGER.info("INFO: UserServiceImpl - updateDeviceToken() :: starts");
		ErrorResponse errorResponse = new ErrorResponse();
		UserDto user = null;
		AuthInfoDto authInfo = null;
		try {
			user = userDao.fetchUserDetails(userId, AppConstants.FIND_BY_TYPE_USERID);
			if (user == null) {
				return new ErrorResponse().setError(
						new ErrorBean().setCode(ErrorCode.EC_61.code()).setMessage(ErrorCode.EC_61.errorMessage()));
			}

			authInfo = userDao.fetchAuthInfoDetails(String.valueOf(user.getUserId()), AppConstants.FIND_BY_TYPE_USERID);
			if (authInfo == null) {
				return new ErrorResponse().setError(
						new ErrorBean().setCode(ErrorCode.EC_61.code()).setMessage(ErrorCode.EC_61.errorMessage()));
			}

			if (!(AppConstants.PLATFORM_TYPE_IOS.equals(osType) || AppConstants.PLATFORM_TYPE_ANDROID.equals(osType))) {
				return new ErrorResponse().setError(
						new ErrorBean().setCode(ErrorCode.EC_109.code()).setMessage(ErrorCode.EC_109.errorMessage()));
			}

			authInfo.setDeviceToken(deviceToken).setDeviceType(osType);
			userDao.saveOrUpdateAuthInfoDetails(authInfo, AppConstants.DB_UPDATE);

			errorResponse.setError(new ErrorBean().setCode(ErrorCode.EC_200.code())
					.setMessage(AppUtil.getAppProperties().get(AppConstants.UPDATE_DEVICE_TOKEN_SUCCESS)));
		} catch (Exception e) {
			LOGGER.error("ERROR: UserServiceImpl - updateDeviceToken()", e);
		}
		LOGGER.info("INFO: UserServiceImpl - updateDeviceToken() :: ends");
		return errorResponse;

	}

}
