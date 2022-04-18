package com.gba.ws.controller;

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
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.gba.ws.bean.ActivityDetailsResponse;
import com.gba.ws.bean.ActivityListResponse;
import com.gba.ws.bean.ErrorBean;
import com.gba.ws.bean.RewardsResponse;
import com.gba.ws.service.ActivityService;
import com.gba.ws.service.StudyService;
import com.gba.ws.service.UserService;
import com.gba.ws.util.ErrorCode;

@RunWith(value = SpringRunner.class)
@SpringBootTest(classes = { ActivityController.class })
@AutoConfigureMockMvc(secure = true)
@FixMethodOrder
public class ActivityControllerTest {

	private MockMvc mockMvc;

	/*
	 * @Autowired private WebApplicationContext wac;
	 */

	@Autowired
	private ActivityController activityController;

	@MockBean
	private ActivityService activityService;

	@MockBean
	private UserService userService;

	@MockBean
	private StudyService studyService;

	@Autowired
	MockHttpServletRequest request;

	@Autowired
	MockHttpServletResponse response;

	final static String AUTHORIZATION_VALUE = "Basic Y29tLmJ0Yy5HQkE6ZWU5MWE0ZjYtZDljNC00ZWU5LWEwZTItNTY4MmM1YjFjOTE2";
	final static String ACCESS_TOKEN = "3057765333";

	@Before
	public void setUp() throws Exception {
		this.mockMvc = /* MockMvcBuilders.webAppContextSetup(wac).build(); */MockMvcBuilders
				.standaloneSetup(this.activityController).build();
		this.activityController = mock(ActivityController.class);
	}
	
	// *************************** getActivities ***************************
	@Test
	public void test_getActivities_when_request_params_are_not_present() throws Exception {
		
		mockMvc.perform(MockMvcRequestBuilders.get("/study/{studyId}/activity", 1)
				.header("Authorization", AUTHORIZATION_VALUE).header("accessToken", ACCESS_TOKEN)
				.header("userId", "")
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andDo(print()).andExpect(status().isBadRequest());
	}
	
	@Test
	public void test_getActivities_when_study_is_not_present() throws Exception {
		
		when(studyService.validateStudyId(anyString())).thenReturn(false);
		
		mockMvc.perform(MockMvcRequestBuilders.get("/study/{studyId}/activity", 1)
				.header("Authorization", AUTHORIZATION_VALUE).header("accessToken", ACCESS_TOKEN)
				.header("userId", "1")
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andDo(print()).andExpect(status().isNotFound());
	}
	
	@Test
	public void test_getActivities_when_userStudiesDetails_is_not_present() throws Exception {
		
		when(studyService.validateStudyId(anyString())).thenReturn(true);
		when(activityService.validateUserEnrolledToStudy(anyString(), anyString())).thenReturn(false);
		
		mockMvc.perform(MockMvcRequestBuilders.get("/study/{studyId}/activity", 1)
				.header("Authorization", AUTHORIZATION_VALUE).header("accessToken", ACCESS_TOKEN)
				.header("userId", "1")
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andDo(print()).andExpect(status().isUnauthorized());
	}
	
	@Test
	public void test_getActivities_when_failed_to_getActivities() throws Exception {
		
		ActivityListResponse activityListResponse = new ActivityListResponse();
		
		when(studyService.validateStudyId(anyString())).thenReturn(true);
		when(activityService.validateUserEnrolledToStudy(anyString(), anyString())).thenReturn(true);
		when(activityService.getActivities(anyString(), anyString())).thenReturn(activityListResponse);
		
		mockMvc.perform(MockMvcRequestBuilders.get("/study/{studyId}/activity", 1)
				.header("Authorization", AUTHORIZATION_VALUE).header("accessToken", ACCESS_TOKEN)
				.header("userId", "1")
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andDo(print()).andExpect(status().isNotFound());
	}
	
	@Test
	public void test_getActivities_for_success() throws Exception {
		
		ActivityListResponse activityListResponse = new ActivityListResponse()
				.setError(new ErrorBean().setCode(ErrorCode.EC_200.code()).setMessage(ErrorCode.EC_200.errorMessage()));
		
		when(studyService.validateStudyId(anyString())).thenReturn(true);
		when(activityService.validateUserEnrolledToStudy(anyString(), anyString())).thenReturn(true);
		when(activityService.getActivities(anyString(), anyString())).thenReturn(activityListResponse);
		
		mockMvc.perform(MockMvcRequestBuilders.get("/study/{studyId}/activity", 1)
				.header("Authorization", AUTHORIZATION_VALUE).header("accessToken", ACCESS_TOKEN)
				.header("userId", "1")
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andDo(print()).andExpect(status().isOk());
	}
	
	@Test
	public void test_getActivities_when_exception_is_occured() throws Exception {
		
		when(studyService.validateStudyId(anyString())).thenReturn(true);
		when(activityService.validateUserEnrolledToStudy(anyString(), anyString())).thenReturn(true);
		when(activityService.getActivities(anyString(), anyString())).thenReturn(null);
		
		mockMvc.perform(MockMvcRequestBuilders.get("/study/{studyId}/activity", 1)
				.header("Authorization", AUTHORIZATION_VALUE).header("accessToken", ACCESS_TOKEN)
				.header("userId", "1")
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andDo(print()).andExpect(status().isInternalServerError());
	}
	
	// *************************** getActivityDetails ***************************
	@Test
	public void test_getActivityDetails_when_request_params_are_not_present() throws Exception {
		
		mockMvc.perform(MockMvcRequestBuilders.get("/study/{studyId}/activity/{activityId}", 1, "ACT1")
				.header("Authorization", AUTHORIZATION_VALUE).header("accessToken", ACCESS_TOKEN)
				.header("userId", "")
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andDo(print()).andExpect(status().isBadRequest());
	}
	
	@Test
	public void test_getActivityDetails_when_study_is_not_present() throws Exception {
		
		when(studyService.validateStudyId(anyString())).thenReturn(false);
		
		mockMvc.perform(MockMvcRequestBuilders.get("/study/{studyId}/activity/{activityId}", 1, "ACT1")
				.header("Authorization", AUTHORIZATION_VALUE).header("accessToken", ACCESS_TOKEN)
				.header("userId", "1")
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andDo(print()).andExpect(status().isNotFound());
	}
	
	@Test
	public void test_getActivityDetails_when_activity_is_not_present() throws Exception {
		
		when(studyService.validateStudyId(anyString())).thenReturn(true);
		when(activityService.validateActivityId(anyString())).thenReturn(false);
		
		mockMvc.perform(MockMvcRequestBuilders.get("/study/{studyId}/activity/{activityId}", 1, "ACT1")
				.header("Authorization", AUTHORIZATION_VALUE).header("accessToken", ACCESS_TOKEN)
				.header("userId", "1")
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andDo(print()).andExpect(status().isNotFound());
	}
	
	@Test
	public void test_getActivityDetails_when_userStudiesDetails_is_not_present() throws Exception {
		
		when(studyService.validateStudyId(anyString())).thenReturn(true);
		when(activityService.validateActivityId(anyString())).thenReturn(true);
		when(activityService.validateUserEnrolledToStudy(anyString(), anyString())).thenReturn(false);
		
		mockMvc.perform(MockMvcRequestBuilders.get("/study/{studyId}/activity/{activityId}", 1, "ACT1")
				.header("Authorization", AUTHORIZATION_VALUE).header("accessToken", ACCESS_TOKEN)
				.header("userId", "1")
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andDo(print()).andExpect(status().isUnauthorized());
	}
	
	@Test
	public void test_getActivityDetails_when_failed_to_getActivityDetails() throws Exception {
		
		ActivityDetailsResponse activityDetailsResponse = new ActivityDetailsResponse();
		
		when(studyService.validateStudyId(anyString())).thenReturn(true);
		when(activityService.validateActivityId(anyString())).thenReturn(true);
		when(activityService.validateUserEnrolledToStudy(anyString(), anyString())).thenReturn(true);
		when(activityService.getActivityDetails(anyString(), anyString(), anyString())).thenReturn(activityDetailsResponse);
		
		mockMvc.perform(MockMvcRequestBuilders.get("/study/{studyId}/activity/{activityId}", 1, "ACT1")
				.header("Authorization", AUTHORIZATION_VALUE).header("accessToken", ACCESS_TOKEN)
				.header("userId", "1")
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andDo(print()).andExpect(status().isForbidden());
	}
	
	@Test
	public void test_getActivityDetails_for_success() throws Exception {
		
		ActivityDetailsResponse activityDetailsResponse = new ActivityDetailsResponse()
				.setError(new ErrorBean().setCode(ErrorCode.EC_200.code()).setMessage(ErrorCode.EC_200.errorMessage()));
		
		when(studyService.validateStudyId(anyString())).thenReturn(true);
		when(activityService.validateActivityId(anyString())).thenReturn(true);
		when(activityService.validateUserEnrolledToStudy(anyString(), anyString())).thenReturn(true);
		when(activityService.getActivityDetails(anyString(), anyString(), anyString())).thenReturn(activityDetailsResponse);
		
		mockMvc.perform(MockMvcRequestBuilders.get("/study/{studyId}/activity/{activityId}", 1, "ACT1")
				.header("Authorization", AUTHORIZATION_VALUE).header("accessToken", ACCESS_TOKEN)
				.header("userId", "1")
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andDo(print()).andExpect(status().isOk());
	}
	
	@Test
	public void test_getActivityDetails_when_exception_is_occured() throws Exception {
		
		when(studyService.validateStudyId(anyString())).thenReturn(true);
		when(activityService.validateActivityId(anyString())).thenReturn(true);
		when(activityService.validateUserEnrolledToStudy(anyString(), anyString())).thenReturn(true);
		when(activityService.getActivityDetails(anyString(), anyString(), anyString())).thenReturn(null);
		
		mockMvc.perform(MockMvcRequestBuilders.get("/study/{studyId}/activity/{activityId}", 1, "ACT1")
				.header("Authorization", AUTHORIZATION_VALUE).header("accessToken", ACCESS_TOKEN)
				.header("userId", "1")
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andDo(print()).andExpect(status().isInternalServerError());
	}
	
	// *************************** rewards ***************************
	@Test
	public void test_rewards_when_failed_to_get_rewards() throws Exception {

		RewardsResponse rewards = new RewardsResponse();

		when(activityService.rewards(anyString())).thenReturn(rewards);

		mockMvc.perform(MockMvcRequestBuilders.get("/user/{userId}/rewards", 1)
				.header("Authorization", AUTHORIZATION_VALUE).header("accessToken", ACCESS_TOKEN)
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andDo(print()).andExpect(status().isUnauthorized());
	}

	@Test
	public void test_rewards_when_fetchingRewards_is_success() throws Exception {

		RewardsResponse rewards = new RewardsResponse()
				.setError(new ErrorBean().setCode(ErrorCode.EC_200.code()).setMessage(ErrorCode.EC_200.errorMessage()));

		when(activityService.rewards(anyString())).thenReturn(rewards);

		mockMvc.perform(MockMvcRequestBuilders.get("/user/{userId}/rewards", 1)
				.header("Authorization", AUTHORIZATION_VALUE).header("accessToken", ACCESS_TOKEN)
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andDo(print()).andExpect(status().isOk());
	}

	@Test
	public void test_rewards_when_exception_is_occured() throws Exception {

		when(activityService.rewards(anyString())).thenReturn(null);

		mockMvc.perform(MockMvcRequestBuilders.get("/user/{userId}/rewards", 1)
				.header("Authorization", AUTHORIZATION_VALUE).header("accessToken", ACCESS_TOKEN)
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andDo(print()).andExpect(status().isInternalServerError());
	}

}
