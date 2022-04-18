/**
 * 
 */
package com.gba.ws.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.gba.ws.bean.AuthResponse;
import com.gba.ws.bean.ErrorBean;
import com.gba.ws.bean.ErrorResponse;
import com.gba.ws.bean.LoginResponse;
import com.gba.ws.bean.UserProfileResponse;
import com.gba.ws.service.UserService;
import com.gba.ws.util.AppConstants;
import com.gba.ws.util.AppUtil;
import com.gba.ws.util.ErrorCode;

/**
 * @author Mohan
 * @createdOn Nov 14, 2017 12:47:45 PM
 */
@RunWith(value = SpringRunner.class)
@SpringBootTest(classes = { UserController.class })
@AutoConfigureMockMvc(secure = true)
@FixMethodOrder
public class UserControllerTest {

	private MockMvc mockMvc;

	@Autowired
	private UserController userController;

	@MockBean
	private UserService userService;

	@Autowired
	private MockHttpServletResponse response;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		this.mockMvc = MockMvcBuilders.standaloneSetup(this.userController).build();
		this.userController = mock(UserController.class);
		this.response = new MockHttpServletResponse();
	}

	final static String AUTHORIZATION_VALUE = "Basic Y29tLmJ0Yy5HQkE6ZWU5MWE0ZjYtZDljNC00ZWU5LWEwZTItNTY4MmM1YjFjOTE2";
	final static String ACCESS_TOKEN = "3057765333";

	// *************************** validateSessionAuthKey
	// ***************************
	@Test
	public void test_validateSessionAuthKey_when_refreshtoken_is_not_present() throws Exception {
		String json = "{\"refreshToken1\":\"286e6532-3e7a-442f-9cf1-c9f430ec90b9\"}";

		mockMvc.perform(MockMvcRequestBuilders.post("/user/refreshToken").header("Authorization", AUTHORIZATION_VALUE)
				.header("accessToken", ACCESS_TOKEN).accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(json)).andDo(print())
				.andExpect(status().isNotAcceptable());
	}

	@Test
	public void test_validateSessionAuthKey_when_refreshtoken_is_empty() throws Exception {
		String json = "{\"refreshToken\":\"\"}";

		mockMvc.perform(MockMvcRequestBuilders.post("/user/refreshToken").header("Authorization", AUTHORIZATION_VALUE)
				.header("accessToken", ACCESS_TOKEN).accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(json)).andDo(print())
				.andExpect(status().isBadRequest());
	}

	@Test
	public void test_validateSessionAuthKey_when_refreshtoken_is_not_empty_and_ok() throws Exception {
		String json = "{\"refreshToken\":\"286e6532-3e7a-442f-9cf1-c9f430ec90b9\"}";
		AuthResponse authResponse = new AuthResponse().setAccessToken("123456")
				.setRefreshToken("286e6532-3e7a-442f-9cf1-c9f430ec90b9").setUserId("1")
				.setError(new ErrorBean().setCode(ErrorCode.EC_200.code()).setMessage(ErrorCode.EC_200.errorMessage()));

		when(userService.validateSessionAuthKey(anyObject(), anyObject())).thenReturn(authResponse);
		mockMvc.perform(MockMvcRequestBuilders.post("/user/refreshToken").header("Authorization", AUTHORIZATION_VALUE)
				.header("accessToken", ACCESS_TOKEN).accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(json)).andDo(print())
				.andExpect(status().isOk());

		assertThat(userService.validateSessionAuthKey("286e6532-3e7a-442f-9cf1-c9f430ec90b9", response))
				.isEqualTo(authResponse);
	}

	@Test
	public void test_validateSessionAuthKey_when_refreshtoken_is_not_empty_and_unauthorized() throws Exception {
		String json = "{\"refreshToken\":\"286e6532-3e7a-442f-9cf1-c9f430ec90b9\"}";
		AuthResponse authResponse = new AuthResponse()
				.setError(new ErrorBean().setCode(ErrorCode.EC_61.code()).setMessage(ErrorCode.EC_61.errorMessage()));

		response = new MockHttpServletResponse();
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

		when(userService.validateSessionAuthKey(anyObject(), anyObject())).thenReturn(authResponse);

		mockMvc.perform(MockMvcRequestBuilders.post("/user/refreshToken").header("Authorization", AUTHORIZATION_VALUE)
				.header("accessToken", ACCESS_TOKEN).accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(json)).andDo(print())
				.andExpect(status().isForbidden()).andReturn();

		userService.validateSessionAuthKey("286e6532-3e7a-442f-9cf1-c9f430ec90b9", response);
		assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
	}

	@Test
	public void test_validateSessionAuthKey_when_refreshtoken_is_not_empty_and_forbidden() throws Exception {
		String json = "{\"refreshToken\":\"286e6532-3e7a-442f-9cf1-c9f430ec90b9\"}";
		AuthResponse authResponse = new AuthResponse()
				.setError(new ErrorBean().setCode(ErrorCode.EC_102.code()).setMessage(ErrorCode.EC_102.errorMessage()));

		response = new MockHttpServletResponse();
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);

		when(userService.validateSessionAuthKey(anyObject(), anyObject())).thenReturn(authResponse);

		MvcResult result = mockMvc
				.perform(MockMvcRequestBuilders.post("/user/refreshToken").header("Authorization", AUTHORIZATION_VALUE)
						.header("accessToken", ACCESS_TOKEN).accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
						.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(json))
				.andDo(print()).andExpect(status().isForbidden()).andReturn();

		userService.validateSessionAuthKey("286e6532-3e7a-442f-9cf1-c9f430ec90b9", response);
		assertEquals(HttpServletResponse.SC_FORBIDDEN, result.getResponse().getStatus());
	}

	@Test
	public void test_validateSessionAuthKey_when_exception_is_occured() throws Exception {
		String json = "{\"refreshToken\":\"286e6532-3e7a-442f-9cf1-c9f430ec90b9\"}";

		when(userService.validateSessionAuthKey(anyObject(), anyObject())).thenReturn(null);

		mockMvc.perform(MockMvcRequestBuilders.post("/user/refreshToken").header("Authorization", AUTHORIZATION_VALUE)
				.header("accessToken", ACCESS_TOKEN).accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(json)).andDo(print())
				.andExpect(status().isInternalServerError());
	}

	// *************************** login ***************************
	@Test
	public void test_login_when_params_are_not_present() throws Exception {
		String json = "{\"email1\":\"mohant@boston-technology.com\",\"password1\":\"Password@123\"}";
		mockMvc.perform(MockMvcRequestBuilders.post("/user/authentication").header("Authorization", AUTHORIZATION_VALUE)
				.header("accessToken", ACCESS_TOKEN).accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(json)).andDo(print())
				.andExpect(status().isNotAcceptable());
	}

	@Test
	public void test_login_when_params_are_empty() throws Exception {
		String json = "{\"email\":\"\",\"password\":\"Password@123\"}";
		mockMvc.perform(MockMvcRequestBuilders.post("/user/authentication").header("Authorization", AUTHORIZATION_VALUE)
				.header("accessToken", ACCESS_TOKEN).accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(json)).andDo(print())
				.andExpect(status().isBadRequest());

		json = "{\"email\":\"mohant@boston-technology.com\",\"password\":\"\"}";
		mockMvc.perform(MockMvcRequestBuilders.post("/user/authentication").header("Authorization", AUTHORIZATION_VALUE)
				.header("accessToken", ACCESS_TOKEN).accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(json)).andDo(print())
				.andExpect(status().isBadRequest());
	}

	@Test
	public void test_login_when_credencials_are_invalid() throws Exception {
		String json = "{\"email\":\"mohant@boston-technology.com\",\"password\":\"Password@123\"}";
		LoginResponse loginResponse = new LoginResponse()
				.setError(new ErrorBean().setCode(ErrorCode.EC_33.code()).setMessage(ErrorCode.EC_33.errorMessage()));

		when(userService.login(anyObject(), anyObject())).thenReturn(loginResponse);

		mockMvc.perform(MockMvcRequestBuilders.post("/user/authentication").header("Authorization", AUTHORIZATION_VALUE)
				.header("accessToken", ACCESS_TOKEN).accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(json)).andDo(print())
				.andExpect(status().isForbidden());
	}

	@Test
	public void test_login_when_credencials_are_valid() throws Exception {
		String json = "{\"email\":\"mohant@boston-technology.com\",\"password\":\"Password@123\"}";
		LoginResponse loginResponse = new LoginResponse().setAccessToken("123456").setEnrolled(true)
				.setEnrollmentId("1").setFitbitSetup(true).setLanguage("en").setLass4uSetup(false)
				.setRefreshToken("asdsa-adssa-ewew-addas").setTempPassword(false).setUserId("1").setVerified(true)
				.setError(new ErrorBean().setCode(ErrorCode.EC_200.code())
						.setMessage(AppUtil.getAppProperties().get(AppConstants.USER_LOGIN_SUCCESS)));

		when(userService.login(anyObject(), anyObject())).thenReturn(loginResponse);

		mockMvc.perform(MockMvcRequestBuilders.post("/user/authentication").header("Authorization", AUTHORIZATION_VALUE)
				.header("accessToken", ACCESS_TOKEN).accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(json)).andDo(print())
				.andExpect(status().isOk());
	}

	@Test
	public void test_login_when_exception_is_occured() throws Exception {
		String json = "{\"email\":\"mohant@boston-technology.com\",\"password\":\"Password@123\"}";

		when(userService.login(anyObject(), anyObject())).thenReturn(null);

		mockMvc.perform(MockMvcRequestBuilders.post("/user/authentication").header("Authorization", AUTHORIZATION_VALUE)
				.header("accessToken", ACCESS_TOKEN).accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(json)).andDo(print())
				.andExpect(status().isInternalServerError());
	}

	// *************************** userRegistration ***************************
	@Test
	public void test_userRegistration_when_params_are_not_present() throws Exception {
		String json = "{\"email1\":\"t1@grr.la\",\"password1\":\"Password@123\",\"firstName1\":\"磨憨\",\"lastName1\":\"Ť\",\"language1\":\"zh-CN\",\"agreedTNC1\": true,\"timeZone1\":\"Asia/Calcutta\"}";
		mockMvc.perform(MockMvcRequestBuilders.post("/user").header("Authorization", AUTHORIZATION_VALUE)
				.header("accessToken", ACCESS_TOKEN).accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(json)).andDo(print())
				.andExpect(status().isNotAcceptable());
	}

	@Test
	public void test_userRegistration_when_params_are_empty() throws Exception {
		String json = "{\"email\":\"\",\"password\":\"\",\"firstName\":\"\",\"lastName\":\"\",\"language\":\"\",\"agreedTNC\": true}";
		mockMvc.perform(MockMvcRequestBuilders.post("/user").header("Authorization", AUTHORIZATION_VALUE)
				.header("accessToken", ACCESS_TOKEN).accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(json)).andDo(print())
				.andExpect(status().isBadRequest());

		json = "{\"email\":\"\",\"password\":\"\",\"firstName\":\"\",\"lastName\":\"\",\"language\":\"\",\"agreedTNC\": true,\"timeZone\":\"Asia/Calcutta\"}";
		mockMvc.perform(MockMvcRequestBuilders.post("/user").header("Authorization", AUTHORIZATION_VALUE)
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.content(json)).andDo(print()).andExpect(status().isBadRequest());

		json = "{\"email\":\"\",\"password\":\"\",\"firstName\":\"\",\"lastName\":\"\",\"language\":\"\",\"agreedTNC\": true}";
		mockMvc.perform(MockMvcRequestBuilders.post("/user").header("Authorization", AUTHORIZATION_VALUE)
				.header("accessToken", ACCESS_TOKEN).accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(json)).andDo(print())
				.andExpect(status().isBadRequest());
	}

	@Test
	public void test_userRegistration_when_email_is_invalid() throws Exception {
		String json = "{\"email\":\"t1@grr.la\",\"password\":\"Password@123\",\"firstName\":\"磨憨\",\"lastName\":\"Ť\",\"language\":\"zh-CN\",\"agreedTNC\": true,\"timeZone\":\"Asia/Calcutta\"}";

		when(userService.validateEmail(anyObject())).thenReturn(true);
		mockMvc.perform(MockMvcRequestBuilders.post("/user").header("Authorization", AUTHORIZATION_VALUE)
				.header("accessToken", ACCESS_TOKEN).accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(json)).andDo(print())
				.andExpect(status().isForbidden());
	}

	@Test
	public void test_userRegistration_when_email_is_valid_and_failed_to_save() throws Exception {
		String json = "{\"email\":\"t1@grr.la\",\"password\":\"Password@123\",\"firstName\":\"磨憨\",\"lastName\":\"Ť\",\"language\":\"zh-CN\",\"agreedTNC\": true,\"timeZone\":\"Asia/Calcutta\"}";

		AuthResponse authResponse = new AuthResponse().setError(new ErrorBean().setCode(ErrorCode.EC_403.code()));

		when(userService.validateEmail(anyObject())).thenReturn(false);
		when(userService.userRegistration(anyObject(), anyString())).thenReturn(authResponse);

		mockMvc.perform(MockMvcRequestBuilders.post("/user").header("Authorization", AUTHORIZATION_VALUE)
				.header("accessToken", ACCESS_TOKEN).accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(json)).andDo(print())
				.andExpect(status().isForbidden());

		json = "{\"email\":\"t1@grr.la\",\"password\":\"Password@123\",\"firstName\":\"磨憨\",\"lastName\":\"Ť\",\"language\":\"zh-CN\",\"agreedTNC\": true,\"timeZone\":\"\"}";

		when(userService.validateEmail(anyObject())).thenReturn(false);
		when(userService.userRegistration(anyObject(), anyString())).thenReturn(authResponse);

		mockMvc.perform(MockMvcRequestBuilders.post("/user").header("Authorization", AUTHORIZATION_VALUE)
				.header("accessToken", ACCESS_TOKEN).accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(json)).andDo(print())
				.andExpect(status().isForbidden());
	}

	@Test
	public void test_userRegistration_when_email_is_valid_and_saved() throws Exception {
		String json = "{\"email\":\"t1@grr.la\",\"password\":\"Password@123\",\"firstName\":\"磨憨\",\"lastName\":\"Ť\",\"language\":\"zh-CN\",\"agreedTNC\": true,\"timeZone\":\"Asia/Calcutta\"}";

		AuthResponse authResponse = new AuthResponse().setError(new ErrorBean().setCode(ErrorCode.EC_200.code())
				.setMessage(AppUtil.getAppProperties().get(AppConstants.USER_REGISTRATION_SUCCESS)));

		when(userService.validateEmail(anyObject())).thenReturn(false);
		when(userService.userRegistration(anyObject(), anyString())).thenReturn(authResponse);

		mockMvc.perform(MockMvcRequestBuilders.post("/user").header("Authorization", AUTHORIZATION_VALUE)
				.header("accessToken", ACCESS_TOKEN).accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(json)).andDo(print())
				.andExpect(status().isOk());
	}

	@Test
	public void test_userRegistration_when_exception_is_occured() throws Exception {
		String json = "{\"email\":\"t1@grr.la\",\"password\":\"Password@123\",\"firstName\":\"磨憨\",\"lastName\":\"Ť\",\"language\":\"zh-CN\",\"agreedTNC\": true,\"timeZone\":\"Asia/Calcutta\"}";

		when(userService.validateEmail(anyObject())).thenReturn(false);
		when(userService.userRegistration(anyObject(), anyString())).thenReturn(null);

		mockMvc.perform(MockMvcRequestBuilders.post("/user").header("Authorization", AUTHORIZATION_VALUE)
				.header("accessToken", ACCESS_TOKEN).accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(json)).andDo(print())
				.andExpect(status().isInternalServerError());
	}

	// *************************** emailVerification ***************************
	@Test
	public void test_emailVerification_when_params_are_not_present() throws Exception {
		String json = "{\"token1\":\"2323\"}";
		mockMvc.perform(MockMvcRequestBuilders.post("/user/{userId}/verification", 1)
				.header("Authorization", AUTHORIZATION_VALUE).header("accessToken", ACCESS_TOKEN)
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.content(json)).andDo(print()).andExpect(status().isNotAcceptable());
	}

	@Test
	public void test_emailVerification_when_params_are_empty() throws Exception {
		String json = "{\"token\":\"\"}";
		mockMvc.perform(MockMvcRequestBuilders.post("/user/{userId}/verification", 1)
				.header("Authorization", AUTHORIZATION_VALUE).header("accessToken", ACCESS_TOKEN)
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.content(json)).andDo(print()).andExpect(status().isBadRequest());

		json = "{\"token\":\"2323\"}";
		mockMvc.perform(MockMvcRequestBuilders.post("/user/{userId}/verification", "")
				.header("Authorization", AUTHORIZATION_VALUE).accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(json)).andDo(print())
				.andExpect(status().isMethodNotAllowed());

		json = "{\"token\":\"\"}";
		mockMvc.perform(MockMvcRequestBuilders.post("/user/{userId}/verification", "")
				.header("Authorization", AUTHORIZATION_VALUE).header("accessToken", ACCESS_TOKEN)
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.content(json)).andDo(print()).andExpect(status().isMethodNotAllowed());
	}

	@Test
	public void test_emailVerification_when_verification_is_failed() throws Exception {
		String json = "{\"token\":\"2323\"}";
		ErrorResponse errorResponse = new ErrorResponse()
				.setError(new ErrorBean().setCode(ErrorCode.EC_403.code()).setMessage(ErrorCode.EC_403.errorMessage()));

		when(userService.emailVerification(anyString(), anyString(), anyString())).thenReturn(errorResponse);

		mockMvc.perform(MockMvcRequestBuilders.post("/user/{userId}/verification", 1)
				.header("Authorization", AUTHORIZATION_VALUE).header("accessToken", ACCESS_TOKEN)
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.content(json)).andDo(print()).andExpect(status().isForbidden());
	}

	@Test
	public void test_emailVerification_when_verification_is_valid() throws Exception {
		String json = "{\"token\":\"2323\"}";
		ErrorResponse errorResponse = new ErrorResponse()
				.setError(new ErrorBean().setCode(ErrorCode.EC_200.code()).setMessage(ErrorCode.EC_200.errorMessage()));

		when(userService.emailVerification(anyString(), anyString(), anyString())).thenReturn(errorResponse);

		mockMvc.perform(MockMvcRequestBuilders.post("/user/{userId}/verification", 1)
				.header("Authorization", AUTHORIZATION_VALUE).header("accessToken", ACCESS_TOKEN)
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.content(json)).andDo(print()).andExpect(status().isOk());
	}

	@Test
	public void test_emailVerification_when_exception_is_occured() throws Exception {
		String json = "{\"token\":\"2323\"}";

		when(userService.emailVerification(anyString(), anyString(), anyString())).thenReturn(null);

		mockMvc.perform(MockMvcRequestBuilders.post("/user/{userId}/verification", 1)
				.header("Authorization", AUTHORIZATION_VALUE).header("accessToken", ACCESS_TOKEN)
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.content(json)).andDo(print()).andExpect(status().isInternalServerError());
	}

	// *************************** resendVerificationToken
	// ***************************
	@Test
	public void test_resendVerificationToken_when_userid_is_invalid() throws Exception {
		ErrorResponse errorResponse = new ErrorResponse()
				.setError(new ErrorBean().setCode(ErrorCode.EC_403.code()).setMessage(ErrorCode.EC_403.errorMessage()));

		when(userService.resendVerificationToken(anyString())).thenReturn(errorResponse);

		mockMvc.perform(MockMvcRequestBuilders.get("/user/{userId}/verification/token", 1)
				.header("Authorization", AUTHORIZATION_VALUE).header("accessToken", ACCESS_TOKEN)
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andDo(print()).andExpect(status().isForbidden());
	}

	@Test
	public void test_resendVerificationToken_when_userid_is_valid() throws Exception {
		ErrorResponse errorResponse = new ErrorResponse()
				.setError(new ErrorBean().setCode(ErrorCode.EC_200.code()).setMessage(ErrorCode.EC_200.errorMessage()));

		when(userService.resendVerificationToken(anyString())).thenReturn(errorResponse);

		mockMvc.perform(MockMvcRequestBuilders.get("/user/{userId}/verification/token", 1)
				.header("Authorization", AUTHORIZATION_VALUE).header("accessToken", ACCESS_TOKEN)
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andDo(print()).andExpect(status().isOk());
	}

	@Test
	public void test_resendVerificationToken_when_exception_is_occured() throws Exception {
		when(userService.resendVerificationToken(anyString())).thenReturn(null);

		mockMvc.perform(MockMvcRequestBuilders.get("/user/{userId}/verification/token", 1)
				.header("Authorization", AUTHORIZATION_VALUE).header("accessToken", ACCESS_TOKEN)
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andDo(print()).andExpect(status().isInternalServerError());
	}

	// *************************** forgotPassword ***************************
	@Test
	public void test_forgotPassword_when_params_are_not_present() throws Exception {
		String json = "{\"email1\":\"master1@grr.la\"}";
		mockMvc.perform(
				MockMvcRequestBuilders.post("/user/password/forgot").header("Authorization", AUTHORIZATION_VALUE)
						.header("accessToken", ACCESS_TOKEN).accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
						.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(json))
				.andDo(print()).andExpect(status().isNotAcceptable());
	}

	@Test
	public void test_forgotPassword_when_params_are_invalid() throws Exception {
		String json = "{\"email\":\"\"}";
		mockMvc.perform(
				MockMvcRequestBuilders.post("/user/password/forgot").header("Authorization", AUTHORIZATION_VALUE)
						.header("accessToken", ACCESS_TOKEN).accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
						.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(json))
				.andDo(print()).andExpect(status().isBadRequest());
	}

	@Test
	public void test_forgotPassword_when_params_are_present_but_email_is_invalid() throws Exception {
		String json = "{\"email\":\"master1@grr.la\"}";

		when(userService.validateEmail(anyString())).thenReturn(false);

		mockMvc.perform(
				MockMvcRequestBuilders.post("/user/password/forgot").header("Authorization", AUTHORIZATION_VALUE)
						.header("accessToken", ACCESS_TOKEN).accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
						.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(json))
				.andDo(print()).andExpect(status().isNotFound());
	}

	@Test
	public void test_forgotPassword_when_params_are_present_email_is_not_exists() throws Exception {
		String json = "{\"email\":\"master1@grr.la\"}";
		ErrorResponse errorResponse = new ErrorResponse()
				.setError(new ErrorBean().setCode(ErrorCode.EC_403.code()).setMessage(ErrorCode.EC_403.errorMessage()));

		when(userService.validateEmail(anyString())).thenReturn(true);
		when(userService.forgotPassword(anyString())).thenReturn(errorResponse);

		mockMvc.perform(
				MockMvcRequestBuilders.post("/user/password/forgot").header("Authorization", AUTHORIZATION_VALUE)
						.header("accessToken", ACCESS_TOKEN).accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
						.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(json))
				.andDo(print()).andExpect(status().isForbidden());
	}

	@Test
	public void test_forgotPassword_when_params_are_present_email_is_exists() throws Exception {
		String json = "{\"email\":\"master1@grr.la\"}";
		ErrorResponse errorResponse = new ErrorResponse()
				.setError(new ErrorBean().setCode(ErrorCode.EC_200.code()).setMessage(ErrorCode.EC_200.errorMessage()));

		when(userService.validateEmail(anyString())).thenReturn(true);
		when(userService.forgotPassword(anyString())).thenReturn(errorResponse);

		mockMvc.perform(
				MockMvcRequestBuilders.post("/user/password/forgot").header("Authorization", AUTHORIZATION_VALUE)
						.header("accessToken", ACCESS_TOKEN).accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
						.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(json))
				.andDo(print()).andExpect(status().isOk());
	}

	@Test
	public void test_forgotPassword_when_exception_is_occured() throws Exception {
		String json = "{\"email\":\"master1@grr.la\"}";

		when(userService.validateEmail(anyString())).thenReturn(true);
		when(userService.forgotPassword(anyString())).thenReturn(null);

		mockMvc.perform(
				MockMvcRequestBuilders.post("/user/password/forgot").header("Authorization", AUTHORIZATION_VALUE)
						.header("accessToken", ACCESS_TOKEN).accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
						.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(json))
				.andDo(print()).andExpect(status().isInternalServerError());
	}

	// *************************** userProfileDetails ***************************
	@Test
	public void test_userProfileDetails_when_userid_is_invalid() throws Exception {
		UserProfileResponse userProfileResponse = new UserProfileResponse()
				.setError(new ErrorBean().setCode(ErrorCode.EC_403.code()).setMessage(ErrorCode.EC_403.errorMessage()));

		when(userService.userProfileDetails(anyString())).thenReturn(userProfileResponse);

		mockMvc.perform(MockMvcRequestBuilders.get("/user/{userId}", 1).header("Authorization", AUTHORIZATION_VALUE)
				.header("accessToken", ACCESS_TOKEN).accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)).andDo(print()).andExpect(status().isForbidden());
	}

	@Test
	public void test_userProfileDetails_when_userid_is_valid() throws Exception {
		UserProfileResponse userProfileResponse = new UserProfileResponse()
				.setError(new ErrorBean().setCode(ErrorCode.EC_200.code()).setMessage(ErrorCode.EC_200.errorMessage()));

		when(userService.userProfileDetails(anyString())).thenReturn(userProfileResponse);

		mockMvc.perform(MockMvcRequestBuilders.get("/user/{userId}", 1).header("Authorization", AUTHORIZATION_VALUE)
				.header("accessToken", ACCESS_TOKEN).accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)).andDo(print()).andExpect(status().isOk());
	}

	@Test
	public void test_userProfileDetails_when_exception_is_occured() throws Exception {
		when(userService.userProfileDetails(anyString())).thenReturn(null);

		mockMvc.perform(MockMvcRequestBuilders.get("/user/{userId}", 1).header("Authorization", AUTHORIZATION_VALUE)
				.header("accessToken", ACCESS_TOKEN).accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)).andDo(print())
				.andExpect(status().isInternalServerError());
	}

	// *************************** updateUserProfile ***************************
	@Test
	public void test_updateUserProfile_when_params_are_invalid() throws Exception {
		String json = "{\"reminders\": \"\",\"lass4u\": \"девушка\",\"fitbit\": \"поместиться\",\"language\": \"es\",\"temperature\": \"farenheit\"}";
		mockMvc.perform(MockMvcRequestBuilders.put("/user/{userId}", 1).header("Authorization", AUTHORIZATION_VALUE)
				.header("accessToken", ACCESS_TOKEN).accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(json)).andDo(print())
				.andExpect(status().isNotAcceptable());
	}

	@Test
	public void test_updateUserProfile_when_params_are_valid_but_failed_to_update_profile() throws Exception {
		String json = "{}";

		ErrorResponse errorResponse = new ErrorResponse()
				.setError(new ErrorBean().setCode(ErrorCode.EC_403.code()).setMessage(ErrorCode.EC_403.errorMessage()));

		when(userService.updateUserProfile(anyObject(), anyString(), anyBoolean())).thenReturn(errorResponse);

		mockMvc.perform(MockMvcRequestBuilders.put("/user/{userId}", 1).header("Authorization", AUTHORIZATION_VALUE)
				.header("accessToken", ACCESS_TOKEN).accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(json)).andDo(print())
				.andExpect(status().isForbidden());
	}

	@Test
	public void test_updateUserProfile_when_params_are_valid_and_update_profile_success() throws Exception {
		String json = "{\"reminders\": true,\"lass4u\": \"девушка\",\"fitbit\": \"поместиться\",\"language\": \"es\",\"temperature\": \"farenheit\"}";

		ErrorResponse errorResponse = new ErrorResponse()
				.setError(new ErrorBean().setCode(ErrorCode.EC_200.code()).setMessage(ErrorCode.EC_200.errorMessage()));

		when(userService.updateUserProfile(anyObject(), anyString(), anyBoolean())).thenReturn(errorResponse);

		mockMvc.perform(MockMvcRequestBuilders.put("/user/{userId}", 1).header("Authorization", AUTHORIZATION_VALUE)
				.header("accessToken", ACCESS_TOKEN).accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(json)).andDo(print())
				.andExpect(status().isOk());
	}

	@Test
	public void test_updateUserProfile_when_exception_is_occured() throws Exception {
		String json = "{\"reminders\": true,\"lass4u\": \"девушка\",\"fitbit\": \"поместиться\",\"language\": \"es\",\"temperature\": \"farenheit\"}";

		when(userService.updateUserProfile(anyObject(), anyString(), anyBoolean())).thenReturn(null);

		mockMvc.perform(MockMvcRequestBuilders.put("/user/{userId}", 1).header("Authorization", AUTHORIZATION_VALUE)
				.header("accessToken", ACCESS_TOKEN).accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(json)).andDo(print())
				.andExpect(status().isInternalServerError());
	}

	// *************************** changePassword ***************************
	@Test
	public void test_changePassword_when_params_are_not_present() throws Exception {
		String json = "{\"oldPassword1\": \"Password@123\",\"newPassword1\": \"Password@123\"}";

		mockMvc.perform(
				MockMvcRequestBuilders.put("/user/{userId}/password", 1).header("Authorization", AUTHORIZATION_VALUE)
						.header("accessToken", ACCESS_TOKEN).accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
						.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(json))
				.andDo(print()).andExpect(status().isNotAcceptable());
	}

	@Test
	public void test_changePassword_when_params_are_invalid() throws Exception {
		String json = "{\"oldPassword\": \"\",\"newPassword\": \"\"}";

		mockMvc.perform(
				MockMvcRequestBuilders.put("/user/{userId}/password", 1).header("Authorization", AUTHORIZATION_VALUE)
						.header("accessToken", ACCESS_TOKEN).accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
						.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(json))
				.andDo(print()).andExpect(status().isBadRequest());
	}

	@Test
	public void test_changePassword_when_params_are_valid_but_failed() throws Exception {
		String json = "{\"oldPassword\": \"Pasword@123\",\"newPassword\": \"Pasword@123\"}";

		ErrorResponse errorResponse = new ErrorResponse()
				.setError(new ErrorBean().setCode(ErrorCode.EC_403.code()).setMessage(ErrorCode.EC_403.errorMessage()));

		when(userService.changePassword(anyString(), anyString(), anyString())).thenReturn(errorResponse);

		mockMvc.perform(
				MockMvcRequestBuilders.put("/user/{userId}/password", 1).header("Authorization", AUTHORIZATION_VALUE)
						.header("accessToken", ACCESS_TOKEN).accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
						.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(json))
				.andDo(print()).andExpect(status().isForbidden());
	}

	@Test
	public void test_changePassword_when_params_are_valid_and_success() throws Exception {
		String json = "{\"oldPassword\": \"Pasword@123\",\"newPassword\": \"Pasword@123\"}";

		ErrorResponse errorResponse = new ErrorResponse()
				.setError(new ErrorBean().setCode(ErrorCode.EC_200.code()).setMessage(ErrorCode.EC_200.errorMessage()));

		when(userService.changePassword(anyString(), anyString(), anyString())).thenReturn(errorResponse);

		mockMvc.perform(
				MockMvcRequestBuilders.put("/user/{userId}/password", 1).header("Authorization", AUTHORIZATION_VALUE)
						.header("accessToken", ACCESS_TOKEN).accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
						.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(json))
				.andDo(print()).andExpect(status().isOk());
	}

	@Test
	public void test_changePassword_when_exception_is_occured() throws Exception {
		String json = "{\"oldPassword\": \"Pasword@123\",\"newPassword\": \"Pasword@123\"}";

		when(userService.changePassword(anyString(), anyString(), anyString())).thenReturn(null);

		mockMvc.perform(
				MockMvcRequestBuilders.put("/user/{userId}/password", 1).header("Authorization", AUTHORIZATION_VALUE)
						.header("accessToken", ACCESS_TOKEN).accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
						.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(json))
				.andDo(print()).andExpect(status().isInternalServerError());
	}

	// *************************** logout ***************************
	@Test
	public void test_logout_when_failed_to_logout() throws Exception {

		ErrorResponse errorResponse = new ErrorResponse()
				.setError(new ErrorBean().setCode(ErrorCode.EC_403.code()).setMessage(ErrorCode.EC_403.errorMessage()));

		when(userService.logout(anyString())).thenReturn(errorResponse);

		mockMvc.perform(MockMvcRequestBuilders.delete("/user/{userId}/authentication", 1)
				.header("Authorization", AUTHORIZATION_VALUE).header("accessToken", ACCESS_TOKEN)
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andDo(print()).andExpect(status().isForbidden());
	}

	@Test
	public void test_logout_when_params_are_valid_and_logout_success() throws Exception {

		ErrorResponse errorResponse = new ErrorResponse()
				.setError(new ErrorBean().setCode(ErrorCode.EC_200.code()).setMessage(ErrorCode.EC_200.errorMessage()));

		when(userService.logout(anyString())).thenReturn(errorResponse);

		mockMvc.perform(MockMvcRequestBuilders.delete("/user/{userId}/authentication", 1)
				.header("Authorization", AUTHORIZATION_VALUE).header("accessToken", ACCESS_TOKEN)
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andDo(print()).andExpect(status().isOk());
	}

	@Test
	public void test_logout_when_exception_is_occured() throws Exception {

		when(userService.logout(anyString())).thenReturn(null);

		mockMvc.perform(MockMvcRequestBuilders.delete("/user/{userId}/authentication", 1)
				.header("Authorization", AUTHORIZATION_VALUE).header("accessToken", ACCESS_TOKEN)
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andDo(print()).andExpect(status().isInternalServerError());
	}

	// *************************** refreshFitbitAccessToken
	// ***************************
	@Test
	public void test_refreshFitbitAccessToken_when_param_is_invalid() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.get("/fitbit/refreshAccessToken")
				.header("Authorization", AUTHORIZATION_VALUE).header("accessToken", ACCESS_TOKEN).header("userId", "")
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andDo(print()).andExpect(status().isBadRequest());
	}

	@Test
	public void test_refreshFitbitAccessToken_when_failed() throws Exception {

		AuthResponse authResponse = new AuthResponse()
				.setError(new ErrorBean().setCode(ErrorCode.EC_403.code()).setMessage(ErrorCode.EC_403.errorMessage()));

		when(userService.refreshFitbitAccessToken(anyString())).thenReturn(authResponse);

		mockMvc.perform(MockMvcRequestBuilders.get("/fitbit/refreshAccessToken")
				.header("Authorization", AUTHORIZATION_VALUE).header("accessToken", ACCESS_TOKEN).header("userId", "1")
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andDo(print()).andExpect(status().isBadRequest());
	}

	@Test
	public void test_refreshFitbitAccessToken_when_success() throws Exception {

		AuthResponse authResponse = new AuthResponse()
				.setError(new ErrorBean().setCode(ErrorCode.EC_200.code()).setMessage(ErrorCode.EC_200.errorMessage()));

		when(userService.refreshFitbitAccessToken(anyString())).thenReturn(authResponse);

		mockMvc.perform(MockMvcRequestBuilders.get("/fitbit/refreshAccessToken")
				.header("Authorization", AUTHORIZATION_VALUE).header("accessToken", ACCESS_TOKEN).header("userId", "1")
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andDo(print()).andExpect(status().isOk());
	}

	@Test
	public void test_refreshFitbitAccessToken_when_exception_is_occured() throws Exception {

		when(userService.refreshFitbitAccessToken(anyString())).thenReturn(null);

		mockMvc.perform(MockMvcRequestBuilders.get("/fitbit/refreshAccessToken")
				.header("Authorization", AUTHORIZATION_VALUE).header("accessToken", ACCESS_TOKEN).header("userId", "1")
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andDo(print()).andExpect(status().isInternalServerError());
	}
}
