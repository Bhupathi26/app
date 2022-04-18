package com.gba.ws.util;

import java.util.Arrays;
import java.util.Base64;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.gba.ws.bean.ErrorBean;
import com.gba.ws.bean.ErrorResponse;
import com.gba.ws.service.UserServiceImpl;

/**
 * Provides Authentication check.
 * 
 * @author Mohan
 * @createdOn Jan 8, 2018 3:58:00 PM
 */
public class AppAuthentication {

	private static final Logger LOGGER = Logger.getLogger(AppAuthentication.class);

	@Autowired
	private UserServiceImpl userServiceImpl;

	public void setUserServiceImpl(UserServiceImpl userServiceImpl) {
		this.userServiceImpl = userServiceImpl;
	}

	/**
	 * Check whether request url is an interceptor url or not
	 * 
	 * @author Mohan
	 * @param uri
	 *            the uri of the request url
	 * @return true or false
	 */
	public boolean isAnInterceptorUrl(String uri) {
		LOGGER.info("INFO: AppAuthentication - isAnInterceptorUrl() :: starts");
		boolean isInterceptorUrl = true;
		try {
			String excludeActions = AppUtil.getAppProperties().get("app.interceptor.urls");
			String[] list = excludeActions.split(",");

			for (int i = 0; i < list.length; i++) {
				if (uri.endsWith(list[i].trim())) {
					isInterceptorUrl = false;
				}
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: AppAuthentication - isAnInterceptorUrl()", e);
		}
		LOGGER.info("INFO: AppAuthentication - isAnInterceptorUrl() :: ends");
		return isInterceptorUrl;
	}

	/**
	 * Check is authentication is required or not for the url
	 * 
	 * @author Mohan
	 * @param uri
	 *            the uri of the request url
	 * @param isAuthCheckRequired
	 *            is authentication check is required
	 * @return true or false
	 */
	public boolean isAuthenticationRequired(String uri, boolean isAuthCheckRequired) {
		LOGGER.info("INFO: AppAuthentication - isAuthenticationRequired() :: starts");
		boolean authRequired = isAuthCheckRequired;
		try {
			String excludeAuthActions = AppUtil.getAppProperties().get("app.authentication.skip.urls");
			String[] skipAuthlist = excludeAuthActions.split(",");

			for (int i = 0; i < skipAuthlist.length; i++) {
				if (uri.endsWith(skipAuthlist[i].trim())) {
					authRequired = false;
				}
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: AppAuthentication - isAuthenticationRequired()", e);
		}
		LOGGER.info("INFO: AppAuthentication - isAuthenticationRequired() :: ends");
		return authRequired;
	}

	/**
	 * Check is authorized or not.
	 * 
	 * @param authCredentials
	 *            the Authorization keys details
	 * @param authenticationFlag
	 *            the authentication status
	 * @param errorResponse
	 *            the {@link ErrorResponse} details
	 * @param response
	 *            the response
	 * @return true or false
	 */
	public boolean isAuthorized(String authCredentials, boolean authenticationFlag, ErrorResponse errorResponse,
			HttpServletResponse response) {
		LOGGER.info("INFO: AppAuthentication - isAuthorized() :: starts");
		boolean auth = authenticationFlag;
		try {
			if (StringUtils.isNotEmpty(authCredentials)) {
				boolean isAuthorized = authenticate(authCredentials);
				if (isAuthorized) {
					auth = true;
				} else {
					errorResponse.setError(new ErrorBean().setCode(HttpServletResponse.SC_UNAUTHORIZED)
							.setMessage(ErrorCode.EC_401.errorMessage()));
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					AppUtil.setErrorResponse(errorResponse, response);
				}
			} else {
				errorResponse.setError(new ErrorBean().setCode(HttpServletResponse.SC_FORBIDDEN)
						.setMessage(ErrorCode.EC_403.errorMessage()));
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				AppUtil.setErrorResponse(errorResponse, response);
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: AppAuthentication - isAuthorized()", e);
		}
		LOGGER.info("INFO: AppAuthentication - isAuthorized() :: ends");
		return auth;
	}

	/**
	 * To validate the authCredentials with the saved app auth keys i.e.(bundleId
	 * and appToken)
	 * 
	 * @author Mohan
	 * @param authCredentials
	 *            the Authorization keys details
	 * @return true or false
	 */
	public boolean authenticate(String authCredentials) {
		LOGGER.info("INFO: AppAuthentication - authenticate() :: starts");
		boolean authenticationStatus = false;
		String bundleIdAndAppToken = null;
		try {
			if (StringUtils.isNotEmpty(authCredentials) && authCredentials.contains("Basic")) {
				final String encodedUserPassword = authCredentials.replaceFirst("Basic" + " ", "");
				byte[] decodedBytes = Base64.getDecoder().decode(encodedUserPassword);
				bundleIdAndAppToken = new String(decodedBytes, AppConstants.CHARSET_ENCODING_UTF_8);
				if (bundleIdAndAppToken.contains(":")) {
					final StringTokenizer tokenizer = new StringTokenizer(bundleIdAndAppToken, ":");
					final String bundleId = tokenizer.nextToken();
					final String appToken = tokenizer.nextToken();
					if ((Arrays
							.asList(AppUtil.getAuthorizationProperties().get(AppConstants.ANROID_BUNDLEID_KEY).trim()
									.split(","))
							.contains(bundleId)
							|| Arrays.asList(AppUtil.getAuthorizationProperties().get(AppConstants.IOS_BUNDLEID_KEY)
									.trim().split(",")).contains(bundleId))
							&& AppUtil.getAuthorizationProperties().containsValue(appToken)) {
						authenticationStatus = true;
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: AppAuthentication - authenticate()", e);
		}
		LOGGER.info("INFO: AppAuthentication - authenticate() :: ends");
		return authenticationStatus;
	}

	/**
	 * Check temporary authorization url
	 * 
	 * @author Mohan
	 * @param uri
	 *            the uri of the request url
	 * @return true or false
	 */
	public boolean isTempAuthUrl(String uri) {
		LOGGER.info("INFO: AppAuthentication - isTempAuthUrl() :: starts");
		boolean isTempAuthUrl = false;
		try {
			String excludeActions = AppUtil.getAppProperties().get("app.temp.authentication.urls");
			String[] list = excludeActions.split(",");

			for (int i = 0; i < list.length; i++) {
				if (uri.endsWith(list[i].trim())) {
					isTempAuthUrl = true;
				}
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: AppAuthentication - isTempAuthUrl()", e);
		}
		LOGGER.info("INFO: AppAuthentication - isTempAuthUrl() :: ends");
		return isTempAuthUrl;
	}

	/**
	 * To validate the request URL is authorized or not for the access token and
	 * user identifier or is't an interceptor url
	 * 
	 * @author Mohan
	 * @param isInterceptorUrl
	 *            is interceptor url or not
	 * @param userId
	 *            the user identifier
	 * @param accessToken
	 *            the access token
	 * @param response
	 *            the response
	 * @return true or false
	 */
	public boolean isURLAuthorized(boolean isInterceptorUrl, String userId, String accessToken,
			HttpServletResponse response) {
		LOGGER.info("INFO: AppAuthentication - isURLAuthorized() :: starts");
		boolean isValidUrl = false;
		ErrorResponse errorResponse = new ErrorResponse();
		try {
			if (!isInterceptorUrl || StringUtils.isEmpty(userId)) {
				isValidUrl = true;
				return isValidUrl;
			}

			// Check the accesstoken and userId empty or not, if empty then user is
			// UNAUTHORIZED and request is BAD_REQUEST
			if (StringUtils.isEmpty(accessToken) || StringUtils.isEmpty(userId)) {
				errorResponse.setError(
						new ErrorBean().setCode(ErrorCode.EC_43.code()).setMessage(ErrorCode.EC_43.errorMessage()));
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				AppUtil.setErrorResponse(errorResponse, response);
				return isValidUrl;
			}

			// Validate access token before processing the request
			errorResponse = userServiceImpl.authenticateUser(accessToken, userId, response);
			if (errorResponse.getError().getCode() == ErrorCode.EC_200.code()) {
				isValidUrl = true;
				return isValidUrl;
			}

			AppUtil.setErrorResponse(errorResponse, response);
			return isValidUrl;
		} catch (Exception e) {
			LOGGER.error("ERROR: AppAuthentication - isURLAuthorized()", e);
		}
		LOGGER.info("INFO: AppAuthentication - isURLAuthorized() :: ends");
		return isValidUrl;
	}

	/**
	 * Get the Authorization header value for LASS4U Sensor
	 * 
	 * @author Mohan
	 * @param userName
	 *            the lass4U username
	 * @param pssword
	 *            the lass4U pssword
	 * @return the basic authorization value
	 */
	public String createLass4UAuthorizationValue(String userName, String pssword) {
		LOGGER.info("INFO: AppAuthentication - createLass4UAuthorizationValue() :: starts");
		String lass4UAuthorization = "";
		try {

			if (StringUtils.isNotEmpty(userName) && StringUtils.isNotEmpty(pssword)) {
				String auth = new StringBuilder().append(userName.trim()).append(":").append(pssword.trim()).toString();
				lass4UAuthorization = new StringBuilder().append("Basic ")
						.append(Base64.getEncoder().encodeToString(auth.getBytes())).toString();
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: AppAuthentication - createLass4UAuthorizationValue()", e);
		}
		LOGGER.info("INFO: AppAuthentication - createLass4UAuthorizationValue() :: ends");
		return lass4UAuthorization;
	}
}
