package com.gba.ws.config;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;

import com.gba.ws.bean.ErrorBean;
import com.gba.ws.bean.ErrorResponse;
import com.gba.ws.util.AppAuthentication;
import com.gba.ws.util.AppConstants;
import com.gba.ws.util.AppUtil;
import com.gba.ws.util.ErrorCode;

/**
 * Implements {@link HandlerInterceptor} interface, intercepts all the incoming
 * requests.
 * 
 * @author Mohan
 * @createdOn Jan 8, 2018 3:50:03 PM
 */
public class AppInterceptor implements HandlerInterceptor {

	private static final Logger LOGGER = Logger.getLogger(AppInterceptor.class);

	private static final String ACCESS_TOKEN_HEADER = "accessToken";
	private static final String USER_ID_HEADER = "userId";
	private static final String AUTHORIZATION_HEADER = "Authorization";

	@Autowired
	private AppAuthentication appAuthentication;

	/**
	 * @author Mohan
	 * @param appAuthentication
	 */
	public void setAppAuthentication(AppAuthentication appAuthentication) {
		this.appAuthentication = appAuthentication;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		LOGGER.info("INFO: AppInterceptor - preHandle() :: starts");
		String accessToken = request.getHeader(ACCESS_TOKEN_HEADER);
		String userId = request.getHeader(USER_ID_HEADER);
		String authCredentials = request.getHeader(AUTHORIZATION_HEADER);
		String uri = request.getRequestURI();
		boolean isValidUrl = false;
		boolean isInterceptorUrl = true;
		boolean isAuthCheckRequired = true;
		boolean authenticationFlag = false;
		boolean isTempAuthUrl = false;
		ErrorResponse errorResponse = new ErrorResponse();
		Map<String, String> pathVariables = null;
		try {

			// Check the incoming url is an interceptor url or not
			isInterceptorUrl = appAuthentication.isAnInterceptorUrl(uri); // check app interceptor urls

			// Check is't Authentication is required for the incoming url i.e. (url's
			// otherthan ping and error)
			isAuthCheckRequired = appAuthentication.isAuthenticationRequired(uri, isAuthCheckRequired);
			if (!isAuthCheckRequired) {
				isValidUrl = true;
				return isValidUrl;
			}

			// Authenticate the url having valid credencials or not
			authenticationFlag = appAuthentication.isAuthorized(authCredentials, authenticationFlag, errorResponse,
					response);
			if (authenticationFlag) {

				// Check url is refresh token url or not
				if (uri.endsWith(AppConstants.REFRESH_SESSION_AUTHKEY_PATH)) {
					isValidUrl = true;
					return isValidUrl;
				}

				// Non-interceptor url's need authectication check
				pathVariables = (Map<String, String>) request
						.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE); // get path variable from the
																						// request
				userId = pathVariables.get(USER_ID_HEADER) == null ? userId : pathVariables.get(USER_ID_HEADER);
				if (StringUtils.isEmpty(userId)) {
					isTempAuthUrl = appAuthentication.isTempAuthUrl(uri);
					if (isTempAuthUrl) {
						isValidUrl = true;
						return isValidUrl;
					}
				}

				// Check the url is authorized to proceed further or not
				return appAuthentication.isURLAuthorized(isInterceptorUrl, userId, accessToken, response);
			}
		} catch (Exception e) {
			errorResponse.setError(new ErrorBean().setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
					.setMessage(ErrorCode.EC_500.errorMessage()));
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			AppUtil.setErrorResponse(errorResponse, response);
			LOGGER.error("ERROR: AppInterceptor - preHandle()", e);
		}
		LOGGER.info("INFO: AppInterceptor - preHandle() :: ends");
		return isValidUrl;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception execption) throws Exception {
		LOGGER.info("INFO: AppInterceptor - afterCompletion()");
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView mav)
			throws Exception {
		LOGGER.info("INFO: AppInterceptor - postHandle()");
	}

}
