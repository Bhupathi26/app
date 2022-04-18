/**
 * 
 */
package com.gba.ws.controller;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.gba.ws.bean.ErrorBean;
import com.gba.ws.bean.ErrorResponse;
import com.gba.ws.bean.SignedConsentResponse;
import com.gba.ws.service.StudyService;
import com.gba.ws.service.UserService;
import com.gba.ws.util.ErrorCode;

/**
 * @author Mohan
 * @createdOn Jan 9, 2018 6:56:50 PM
 */
@RunWith(value = SpringRunner.class)
@SpringBootTest(classes = { StudyController.class })
@AutoConfigureMockMvc(secure = true)
@FixMethodOrder
public class StudyControllerTest {

	private MockMvc mockMvc;

	@Autowired
	private StudyController studyController;

	@MockBean
	private UserService userService;

	@MockBean
	private StudyService studyService;

	@Before
	public void setUp() throws Exception {

		this.mockMvc = MockMvcBuilders.standaloneSetup(this.studyController).build();
		this.studyController = mock(StudyController.class);
	}

	final static String AUTHORIZATION_VALUE = "Basic Y29tLmJ0Yy5HQkE6ZWU5MWE0ZjYtZDljNC00ZWU5LWEwZTItNTY4MmM1YjFjOTE2";
	final static String ACCESS_TOKEN = "3057765333";

	// *************************** verifiyEligibility ***************************
	@Test
	public void test_verifiyEligibility_when_params_are_invalid() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.get("/study/{studyId}/verifiyEligibility", 1)
				.header("Authorization", AUTHORIZATION_VALUE).header("accessToken", ACCESS_TOKEN).header("userId", "")
				.header("token", "").accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)).andDo(print()).andExpect(status().isBadRequest());

		mockMvc.perform(MockMvcRequestBuilders.get("/study/{studyId}/verifiyEligibility", 1)
				.header("Authorization", AUTHORIZATION_VALUE).header("accessToken", ACCESS_TOKEN).header("userId", "1")
				.header("token", "").accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)).andDo(print()).andExpect(status().isBadRequest());

		mockMvc.perform(MockMvcRequestBuilders.get("/study/{studyId}/verifiyEligibility", 1)
				.header("Authorization", AUTHORIZATION_VALUE).header("accessToken", ACCESS_TOKEN).header("userId", "")
				.header("token", "123").accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)).andDo(print()).andExpect(status().isBadRequest());
	}

	@Test
	public void test_verifiyEligibility_when_study_id_is_not_valid() throws Exception {

		when(studyService.validateStudyId(anyString())).thenReturn(false);

		mockMvc.perform(MockMvcRequestBuilders.get("/study/{studyId}/verifiyEligibility", 1)
				.header("Authorization", AUTHORIZATION_VALUE).header("accessToken", ACCESS_TOKEN).header("userId", "1")
				.header("token", "1234").accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)).andDo(print()).andExpect(status().isNotFound());
	}

	@Test
	public void test_verifiyEligibility_when_failed_to_verify_eligibility() throws Exception {

		ErrorResponse errorResponse = new ErrorResponse();

		when(studyService.validateStudyId(anyString())).thenReturn(true);
		when(studyService.verifiyEligibility(anyString(), anyString(), anyString())).thenReturn(errorResponse);

		mockMvc.perform(MockMvcRequestBuilders.get("/study/{studyId}/verifiyEligibility", 1)
				.header("Authorization", AUTHORIZATION_VALUE).header("accessToken", ACCESS_TOKEN).header("userId", "1")
				.header("token", "1234").accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)).andDo(print()).andExpect(status().isForbidden());
	}

	@Test
	public void test_verifiyEligibility_when_verify_eligibility_is_valid() throws Exception {

		ErrorResponse errorResponse = new ErrorResponse()
				.setError(new ErrorBean().setCode(ErrorCode.EC_200.code()).setMessage(ErrorCode.EC_200.errorMessage()));

		when(studyService.validateStudyId(anyString())).thenReturn(true);
		when(studyService.verifiyEligibility(anyString(), anyString(), anyString())).thenReturn(errorResponse);

		mockMvc.perform(MockMvcRequestBuilders.get("/study/{studyId}/verifiyEligibility", 1)
				.header("Authorization", AUTHORIZATION_VALUE).header("accessToken", ACCESS_TOKEN).header("userId", "1")
				.header("token", "1234").accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)).andDo(print()).andExpect(status().isOk());
	}

	@Test
	public void test_verifiyEligibility_when_exception_is_occured() throws Exception {

		when(studyService.validateStudyId(anyString())).thenReturn(true);
		when(studyService.verifiyEligibility(anyString(), anyString(), anyString())).thenReturn(null);

		mockMvc.perform(MockMvcRequestBuilders.get("/study/{studyId}/verifiyEligibility", 1)
				.header("Authorization", AUTHORIZATION_VALUE).header("accessToken", ACCESS_TOKEN).header("userId", "1")
				.header("token", "1234").accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)).andDo(print())
				.andExpect(status().isInternalServerError());
	}

	// *************************** enrollInStudy ***************************
	@Test
	public void test_enrollInStudy_when_params_are_not_present() throws Exception {
		String json = "{\"userId1\":\"\",\"token1\":\"\"}";

		mockMvc.perform(
				MockMvcRequestBuilders.post("/study/{studyId}/enroll", 1).header("Authorization", AUTHORIZATION_VALUE)
						.header("accessToken", ACCESS_TOKEN).accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
						.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(json))
				.andDo(print()).andExpect(status().isNotAcceptable());
	}

	@Test
	public void test_enrollInStudy_when_params_are_not_valid() throws Exception {
		String json = "{\"userId\":\"\",\"token\":\"\"}";

		mockMvc.perform(
				MockMvcRequestBuilders.post("/study/{studyId}/enroll", 1).header("Authorization", AUTHORIZATION_VALUE)
						.header("accessToken", ACCESS_TOKEN).accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
						.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(json))
				.andDo(print()).andExpect(status().isBadRequest());
	}

	@Test
	public void test_enrollInStudy_when_params_are_valid_and_studyId_is_invalid() throws Exception {

		String json = "{\"userId\":\"1\",\"token\":\"1\"}";

		ErrorResponse errorResponse = new ErrorResponse();

		when(userService.authenticateUser(anyString(), anyString(), anyObject())).thenReturn(errorResponse);
		when(studyService.validateStudyId(anyString())).thenReturn(false);

		mockMvc.perform(
				MockMvcRequestBuilders.post("/study/{studyId}/enroll", 1).header("Authorization", AUTHORIZATION_VALUE)
						.header("accessToken", ACCESS_TOKEN).accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
						.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(json))
				.andDo(print()).andExpect(status().isNotFound());
	}

	@Test
	public void test_enrollInStudy_when_failed_to_enroll_study() throws Exception {

		String json = "{\"userId\":\"1\",\"token\":\"1\"}";

		ErrorResponse errorResponse = new ErrorResponse();

		when(userService.authenticateUser(anyString(), anyString(), anyObject())).thenReturn(errorResponse);
		when(studyService.validateStudyId(anyString())).thenReturn(true);
		when(studyService.enrollInStudy(anyString(), anyString(), anyString())).thenReturn(errorResponse);

		mockMvc.perform(
				MockMvcRequestBuilders.post("/study/{studyId}/enroll", 1).header("Authorization", AUTHORIZATION_VALUE)
						.header("accessToken", ACCESS_TOKEN).accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
						.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(json))
				.andDo(print()).andExpect(status().isForbidden());
	}

	@Test
	public void test_enrollInStudy_when_enroll_to_study() throws Exception {

		String json = "{\"userId\":\"1\",\"token\":\"1\"}";

		ErrorResponse errorResponse = new ErrorResponse()
				.setError(new ErrorBean().setCode(ErrorCode.EC_200.code()).setMessage(ErrorCode.EC_200.errorMessage()));

		when(userService.authenticateUser(anyString(), anyString(), anyObject())).thenReturn(errorResponse);
		when(studyService.validateStudyId(anyString())).thenReturn(true);
		when(studyService.enrollInStudy(anyString(), anyString(), anyString())).thenReturn(errorResponse);

		mockMvc.perform(
				MockMvcRequestBuilders.post("/study/{studyId}/enroll", 1).header("Authorization", AUTHORIZATION_VALUE)
						.header("accessToken", ACCESS_TOKEN).accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
						.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(json))
				.andDo(print()).andExpect(status().isOk());
	}

	@Test
	public void test_enrollInStudy_when_exception_is_occured() throws Exception {

		String json = "{\"userId\":\"1\",\"token\":\"1\"}";

		when(userService.authenticateUser(anyString(), anyString(), anyObject())).thenReturn(null);
		when(studyService.validateStudyId(anyString())).thenReturn(true);
		when(studyService.enrollInStudy(anyString(), anyString(), anyString())).thenReturn(null);

		mockMvc.perform(
				MockMvcRequestBuilders.post("/study/{studyId}/enroll", 1).header("Authorization", AUTHORIZATION_VALUE)
						.header("accessToken", ACCESS_TOKEN).accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
						.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(json))
				.andDo(print()).andExpect(status().isInternalServerError());
	}

	// *************************** getSignedConsent ***************************
	@Test
	public void test_getSignedConsent_when_params_are_invalid() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.get("/study/{studyId}/signedConsent", 1)
				.header("Authorization", AUTHORIZATION_VALUE).header("accessToken", ACCESS_TOKEN).header("userId", "")
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andDo(print()).andExpect(status().isBadRequest());
	}

	@Test
	public void test_getSignedConsent_when_study_is_not_present() throws Exception {

		when(studyService.validateStudyId(anyString())).thenReturn(false);

		mockMvc.perform(MockMvcRequestBuilders.get("/study/{studyId}/signedConsent", 1)
				.header("Authorization", AUTHORIZATION_VALUE).header("accessToken", ACCESS_TOKEN).header("userId", "1")
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andDo(print()).andExpect(status().isNotFound());
	}

	@Test
	public void test_getSignedConsent_when_failed_to_get_consent_document() throws Exception {

		SignedConsentResponse signedConsentResponse = new SignedConsentResponse()
				.setError(new ErrorBean().setCode(ErrorCode.EC_108.code()).setMessage(ErrorCode.EC_108.errorMessage()));

		when(studyService.validateStudyId(anyString())).thenReturn(true);
		when(studyService.getSignedConsent(anyString(), anyString())).thenReturn(signedConsentResponse);

		mockMvc.perform(MockMvcRequestBuilders.get("/study/{studyId}/signedConsent", 1)
				.header("Authorization", AUTHORIZATION_VALUE).header("accessToken", ACCESS_TOKEN).header("userId", "1")
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andDo(print()).andExpect(status().isNotFound());

		signedConsentResponse = new SignedConsentResponse();

		when(studyService.validateStudyId(anyString())).thenReturn(true);
		when(studyService.getSignedConsent(anyString(), anyString())).thenReturn(signedConsentResponse);

		mockMvc.perform(MockMvcRequestBuilders.get("/study/{studyId}/signedConsent", 1)
				.header("Authorization", AUTHORIZATION_VALUE).header("accessToken", ACCESS_TOKEN).header("userId", "1")
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andDo(print()).andExpect(status().isForbidden());
	}

	@Test
	public void test_getSignedConsent_when_valid() throws Exception {

		SignedConsentResponse signedConsentResponse = new SignedConsentResponse()
				.setError(new ErrorBean().setCode(ErrorCode.EC_200.code()).setMessage(ErrorCode.EC_200.errorMessage()));

		when(studyService.validateStudyId(anyString())).thenReturn(true);
		when(studyService.getSignedConsent(anyString(), anyString())).thenReturn(signedConsentResponse);

		mockMvc.perform(MockMvcRequestBuilders.get("/study/{studyId}/signedConsent", 1)
				.header("Authorization", AUTHORIZATION_VALUE).header("accessToken", ACCESS_TOKEN).header("userId", "1")
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andDo(print()).andExpect(status().isOk());
	}

	@Test
	public void test_getSignedConsent_when_exception_is_occured() throws Exception {

		when(studyService.validateStudyId(anyString())).thenReturn(true);
		when(studyService.getSignedConsent(anyString(), anyString())).thenReturn(null);

		mockMvc.perform(MockMvcRequestBuilders.get("/study/{studyId}/signedConsent", 1)
				.header("Authorization", AUTHORIZATION_VALUE).header("accessToken", ACCESS_TOKEN).header("userId", "1")
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andDo(print()).andExpect(status().isInternalServerError());
	}

	// *************************** storeSignedConsent ***************************
	@Test
	public void test_storeSignedConsent_when_params_are_not_present() throws Exception {

		String json = "{\"userId1\":\"213\",\"studyId1\":\"1\",\"consent1\":\"wMDAwMDY3OTI0IDAwMDAwIG4gCnRyYWl\"}";

		mockMvc.perform(MockMvcRequestBuilders.post("/study/{studyId}/signedConsent", 1)
				.header("Authorization", AUTHORIZATION_VALUE).header("accessToken", ACCESS_TOKEN)
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.content(json)).andDo(print()).andExpect(status().isNotAcceptable());
	}

	@Test
	public void test_storeSignedConsent_when_params_are_not_invalid() throws Exception {

		String json = "{\"userId\":\"\",\"studyId\":\"\",\"consent\":\"\"}";

		mockMvc.perform(MockMvcRequestBuilders.post("/study/{studyId}/signedConsent", 1)
				.header("Authorization", AUTHORIZATION_VALUE).header("accessToken", ACCESS_TOKEN)
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.content(json)).andDo(print()).andExpect(status().isBadRequest());
	}

	@Test
	public void test_storeSignedConsent_when_studyId_is_not_present() throws Exception {

		String json = "{\"userId\":\"1\",\"studyId\":\"1\",\"consent\":\"wMDAwMDY3OTI0IDAwMDAwIG4gCnRyYWl\"}";

		ErrorResponse errorResponse = new ErrorResponse();

		when(userService.authenticateUser(anyString(), anyString(), anyObject())).thenReturn(errorResponse);
		when(studyService.validateStudyId(anyString())).thenReturn(false);

		mockMvc.perform(MockMvcRequestBuilders.post("/study/{studyId}/signedConsent", 1)
				.header("Authorization", AUTHORIZATION_VALUE).header("accessToken", ACCESS_TOKEN)
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.content(json)).andDo(print()).andExpect(status().isNotFound());
	}

	@Test
	public void test_storeSignedConsent_when_failed_to_store_consent_document() throws Exception {

		String json = "{\"userId\":\"1\",\"studyId\":\"1\",\"consent\":\"wMDAwMDY3OTI0IDAwMDAwIG4gCnRyYWl\"}";

		ErrorResponse errorResponse = new ErrorResponse();

		when(userService.authenticateUser(anyString(), anyString(), anyObject())).thenReturn(errorResponse);
		when(studyService.validateStudyId(anyString())).thenReturn(true);
		when(studyService.storeSignedConsent(anyString(), anyString(), anyString())).thenReturn(errorResponse);

		mockMvc.perform(MockMvcRequestBuilders.post("/study/{studyId}/signedConsent", 1)
				.header("Authorization", AUTHORIZATION_VALUE).header("accessToken", ACCESS_TOKEN)
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.content(json)).andDo(print()).andExpect(status().isForbidden());
	}

	@Test
	public void test_storeSignedConsent_when_store_consent_document_is_success() throws Exception {

		String json = "{\"userId\":\"1\",\"studyId\":\"1\",\"consent\":\"wMDAwMDY3OTI0IDAwMDAwIG4gCnRyYWl\"}";

		ErrorResponse errorResponse = new ErrorResponse()
				.setError(new ErrorBean().setCode(ErrorCode.EC_200.code()).setMessage(ErrorCode.EC_200.errorMessage()));

		when(userService.authenticateUser(anyString(), anyString(), anyObject())).thenReturn(errorResponse);
		when(studyService.validateStudyId(anyString())).thenReturn(true);
		when(studyService.storeSignedConsent(anyString(), anyString(), anyString())).thenReturn(errorResponse);

		mockMvc.perform(MockMvcRequestBuilders.post("/study/{studyId}/signedConsent", 1)
				.header("Authorization", AUTHORIZATION_VALUE).header("accessToken", ACCESS_TOKEN)
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.content(json)).andDo(print()).andExpect(status().isOk());
	}

	@Test
	public void test_storeSignedConsent_when_exception_is_occured() throws Exception {

		String json = "{\"userId\":\"1\",\"studyId\":\"1\",\"consent\":\"wMDAwMDY3OTI0IDAwMDAwIG4gCnRyYWl\"}";

		when(userService.authenticateUser(anyString(), anyString(), anyObject())).thenReturn(null);
		when(studyService.validateStudyId(anyString())).thenReturn(true);
		when(studyService.storeSignedConsent(anyString(), anyString(), anyString())).thenReturn(null);

		mockMvc.perform(MockMvcRequestBuilders.post("/study/{studyId}/signedConsent", 1)
				.header("Authorization", AUTHORIZATION_VALUE).header("accessToken", ACCESS_TOKEN)
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.content(json)).andDo(print()).andExpect(status().isInternalServerError());
	}

	// *************************** leaveStudy ***************************
	@Test
	public void test_leaveStudy_when_params_are_invalid() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.get("/study/{studyId}/leaveStudy", 1)
				.header("Authorization", AUTHORIZATION_VALUE).header("accessToken", ACCESS_TOKEN).header("userId", "")
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andDo(print()).andExpect(status().isBadRequest());
	}

	@Test
	public void test_leaveStudy_when_study_is_not_present() throws Exception {

		when(studyService.validateStudyId(anyString())).thenReturn(false);

		mockMvc.perform(MockMvcRequestBuilders.get("/study/{studyId}/leaveStudy", 1)
				.header("Authorization", AUTHORIZATION_VALUE).header("accessToken", ACCESS_TOKEN).header("userId", "1")
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andDo(print()).andExpect(status().isNotFound());
	}

	@Test
	public void test_leaveStudy_when_failed_to_leave_study() throws Exception {

		ErrorResponse errorResponse = new ErrorResponse();

		when(studyService.validateStudyId(anyString())).thenReturn(true);
		when(studyService.leaveStudy(anyString(), anyString())).thenReturn(errorResponse);

		mockMvc.perform(MockMvcRequestBuilders.get("/study/{studyId}/leaveStudy", 1)
				.header("Authorization", AUTHORIZATION_VALUE).header("accessToken", ACCESS_TOKEN).header("userId", "1")
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andDo(print()).andExpect(status().isNotFound());
	}

	@Test
	public void test_leaveStudy_when_leave_study_is_success() throws Exception {

		ErrorResponse errorResponse = new ErrorResponse()
				.setError(new ErrorBean().setCode(ErrorCode.EC_200.code()).setMessage(ErrorCode.EC_200.errorMessage()));

		when(studyService.validateStudyId(anyString())).thenReturn(true);
		when(studyService.leaveStudy(anyString(), anyString())).thenReturn(errorResponse);

		mockMvc.perform(MockMvcRequestBuilders.get("/study/{studyId}/leaveStudy", 1)
				.header("Authorization", AUTHORIZATION_VALUE).header("accessToken", ACCESS_TOKEN).header("userId", "1")
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andDo(print()).andExpect(status().isOk());
	}

	@Test
	public void test_leaveStudy_when_exceptions_is_occured() throws Exception {

		when(studyService.validateStudyId(anyString())).thenReturn(true);
		when(studyService.leaveStudy(anyString(), anyString())).thenReturn(null);

		mockMvc.perform(MockMvcRequestBuilders.get("/study/{studyId}/leaveStudy", 1)
				.header("Authorization", AUTHORIZATION_VALUE).header("accessToken", ACCESS_TOKEN).header("userId", "1")
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andDo(print()).andExpect(status().isInternalServerError());
	}
}
