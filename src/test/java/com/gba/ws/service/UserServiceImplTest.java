/**
 * 
 */
package com.gba.ws.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.gba.ws.bean.SettingsBean;
import com.gba.ws.dao.StudyDao;
import com.gba.ws.dao.UserDao;
import com.gba.ws.model.AuthInfoDto;
import com.gba.ws.model.FitbitUserInfoDto;
import com.gba.ws.model.StudiesDto;
import com.gba.ws.model.UserDto;
import com.gba.ws.model.UserStudiesDto;
import com.gba.ws.util.AppConstants;
import com.gba.ws.util.AppUtil;
import com.gba.ws.util.ErrorCode;

/**
 * @author Mohan
 * @createdOn Nov 17, 2017 6:31:18 PM
 */
@RunWith(value = SpringRunner.class)
@SpringBootTest(classes = { UserServiceImpl.class })
public class UserServiceImplTest {

	@Autowired
	private UserService userService;

	@MockBean
	private UserDao userDao;

	@MockBean
	private StudyDao studyDao;

	@MockBean
	private RestTemplate restTemplate;

	@MockBean
	private MockHttpServletResponse response;

	@Before
	public void setUp() throws Exception {
		
	}

	static final String email = "mohant@boston-technology.com";
	static final String pswd = "Password@123";
	static final String encrypted_pswd = "$2a$10$rOGCnLjgRjuVbSEfTMXnjuo/WfU1BBvawORJGxyT8T5uGS8TuBlE2";

	// *************************** authenticateUser ***************************
	@Test
	public void test_authenticateUser_when_user_doesnot_exists() throws Exception {

		/*userDao.deleteNonSignedUpUsersToStudy();
		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(null);
		assertThat(userService.authenticateUser("12345", "1", response).getError().getCode())
				.isEqualTo(ErrorCode.EC_102.code());*/
	}

	@Test
	public void test_authenticateUser_when_authinfo_doesnot_exists() throws Exception {

		// if user is active
		UserDto user = new UserDto().setStatus(true);
		userDao.deleteNonSignedUpUsersToStudy();

		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(user);
		when(userDao.fetchAuthInfoDetails(anyString(), anyString())).thenReturn(null);
		assertThat(userService.authenticateUser("12345", "1", response).getError().getCode())
				.isEqualTo(ErrorCode.EC_61.code());

		// if user is in active
		user.setStatus(false);
		userDao.deleteNonSignedUpUsersToStudy();

		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(user);
		when(userDao.fetchAuthInfoDetails(anyString(), anyString())).thenReturn(null);
		assertThat(userService.authenticateUser("12345", "1", response).getError().getCode())
				.isEqualTo(ErrorCode.EC_31.code());
	}

	@Test
	public void test_authenticateUser_when_authinfo_exists() throws Exception {

		UserDto user = new UserDto().setStatus(true);
		AuthInfoDto authInfo = new AuthInfoDto();

		// check authkey empty
		userDao.deleteNonSignedUpUsersToStudy();

		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(user);
		when(userDao.fetchAuthInfoDetails(anyString(), anyString())).thenReturn(authInfo);
		assertThat(userService.authenticateUser("12345", "1", response).getError().getCode())
				.isEqualTo(ErrorCode.EC_101.code());

		// check authkey same or not
		authInfo.setAuthKey("123");

		userDao.deleteNonSignedUpUsersToStudy();

		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(user);
		when(userDao.fetchAuthInfoDetails(anyString(), anyString())).thenReturn(authInfo);
		assertThat(userService.authenticateUser("12345", "1", response).getError().getCode())
				.isEqualTo(ErrorCode.EC_101.code());

		// check authkey same
		authInfo.setAuthKey("12345").setSessionExpiredDate("2017-11-13 17:34:56");

		userDao.deleteNonSignedUpUsersToStudy();

		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(user);
		when(userDao.fetchAuthInfoDetails(anyString(), anyString())).thenReturn(authInfo);
		assertThat(userService.authenticateUser("12345", "1", response).getError().getCode())
				.isEqualTo(ErrorCode.EC_101.code());

		// check authkey same
		authInfo.setAuthKey("12345").setSessionExpiredDate(AppUtil.addMinutes(AppUtil.getCurrentDateTime(), 10));

		userDao.deleteNonSignedUpUsersToStudy();

		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(user);
		when(userDao.fetchAuthInfoDetails(anyString(), anyString())).thenReturn(authInfo);
		when(userDao.saveOrUpdateAuthInfoDetails(anyObject(), anyString())).thenReturn(authInfo);
		assertThat(userService.authenticateUser("12345", "1", response).getError().getCode())
				.isEqualTo(ErrorCode.EC_200.code());
	}

	// *************************** validateSessionAuthKey
	// ***************************
	@Test
	public void test_validateSessionAuthKey_when_authinfo_doesnot_exists() throws Exception {

		when(userDao.fetchAuthInfoDetails(anyString(), anyString())).thenReturn(null);
		assertThat(userService.validateSessionAuthKey("12345", response).getError().getCode())
				.isEqualTo(ErrorCode.EC_102.code());
	}

	@Test
	public void test_validateSessionAuthKey_when_userinfo_doesnot_exists() throws Exception {

		AuthInfoDto authInfo = new AuthInfoDto().setUserId(1).setAuthKey("12345").setSessionAuthKey("123");

		when(userDao.fetchAuthInfoDetails(anyString(), anyString())).thenReturn(authInfo);
		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(null);
		assertThat(userService.validateSessionAuthKey("12345", response).getError().getCode())
				.isEqualTo(ErrorCode.EC_61.code());
	}

	@Test
	public void test_validateSessionAuthKey_when_authinfo_userinfo_exists() throws Exception {

		UserDto user = new UserDto().setStatus(true).setModifiedOn("2018-01-01 06:15:40");

		AuthInfoDto authInfo = new AuthInfoDto().setUserId(1).setAuthKey("12345").setSessionAuthKey("123");

		when(userDao.fetchAuthInfoDetails(anyString(), anyString())).thenReturn(authInfo);
		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(user);
		when(userDao.saveOrUpdateUserDetails(anyObject(), anyString())).thenReturn(user);
		when(userDao.saveOrUpdateAuthInfoDetails(anyObject(), anyString())).thenReturn(authInfo);
		assertThat(userService.validateSessionAuthKey("12345", response).getError().getCode())
				.isEqualTo(ErrorCode.EC_200.code());
	}

	// *************************** validateEmail ***************************
	@Test
	public void test_validateEmail() throws Exception {

		when(userDao.validateEmail(anyString())).thenReturn(true);
		assertThat(userService.validateEmail(email)).isTrue();
	}

	// *************************** userRegistration ***************************
	@Test
	public void test_userRegistration_when_userinfo_authorization_invalid() throws Exception {
		UserDto user = new UserDto();
		assertThat(userService.userRegistration(null, null)).isNotEqualTo(ErrorCode.EC_200.code());
		assertThat(userService.userRegistration(user, null)).isNotEqualTo(ErrorCode.EC_200.code());
		assertThat(userService.userRegistration(null,
				"Basic Y29tLmJ0Yy5HQkE6ZWU5MWE0ZjYtZDljNC00ZWU5LWEwZTItNTY4MmM1YjFjOTE2"))
						.isNotEqualTo(ErrorCode.EC_200.code());
	}

	@Test
	public void test_userRegistration_when_userinfo_authorization_is_valid() throws Exception {

		UserDto user = new UserDto().setLanguage(AppConstants.USER_LANGUAGE_SPANISH).setFirstName("Mohan")
				.setVerificationKey("1");
		AuthInfoDto authInfo = new AuthInfoDto().setUserId(1).setAuthKey("12345").setSessionAuthKey("123");
		StudiesDto studyDto = new StudiesDto().setStudyName("GBA COGfx");

		when(userDao.saveOrUpdateUserDetails(anyObject(), anyString())).thenReturn(user);
		when(userDao.saveOrUpdateAuthInfoDetails(anyObject(), anyString())).thenReturn(authInfo);
		when(studyDao.fetchStudyDetailsByStudyId(anyString())).thenReturn(studyDto);
		assertThat(userService
				.userRegistration(user, "Basic Y29tLmJ0Yy5HQkE6ZWU5MWE0ZjYtZDljNC00ZWU5LWEwZTItNTY4MmM1YjFjOTE2")
				.getError().getCode()).isEqualTo(ErrorCode.EC_200.code());

		user.setLanguage(AppConstants.USER_LANGUAGE_ENGLISH);
		when(userDao.saveOrUpdateUserDetails(anyObject(), anyString())).thenReturn(user);
		when(userDao.saveOrUpdateAuthInfoDetails(anyObject(), anyString())).thenReturn(authInfo);
		when(studyDao.fetchStudyDetailsByStudyId(anyString())).thenReturn(studyDto);
		assertThat(userService
				.userRegistration(user, "Basic Y29tLmJ0Yy5HQkE6ZWU5MWE0ZjYtZDljNC00ZWU5LWEwZTItNTY4MmM1YjFjOTE2")
				.getError().getCode()).isEqualTo(ErrorCode.EC_200.code());

		user.setLanguage(AppConstants.USER_LANGUAGE_CHINESE);
		when(userDao.saveOrUpdateUserDetails(anyObject(), anyString())).thenReturn(user);
		when(userDao.saveOrUpdateAuthInfoDetails(anyObject(), anyString())).thenReturn(authInfo);
		when(studyDao.fetchStudyDetailsByStudyId(anyString())).thenReturn(studyDto);
		assertThat(userService
				.userRegistration(user, "Basic Y29tLmJ0Yy5HQkE6ZWU5MWE0ZjYtZDljNC00ZWU5LWEwZTItNTY4MmM1YjFjOTE2")
				.getError().getCode()).isEqualTo(ErrorCode.EC_200.code());
	}

	// *************************** login ***************************
	@Test
	public void test_login_when_userinfo_doesnot_exists() throws Exception {

		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(null);
		assertThat(userService.login(email, pswd).getError().getCode()).isEqualTo(ErrorCode.EC_71.code());
	}

	@Test
	public void test_login_when_userinfo_exists_and_user_is_not_active() throws Exception {

		UserDto user = new UserDto().setStatus(false).setUserId(1);

		AuthInfoDto authInfo = new AuthInfoDto();
		FitbitUserInfoDto fitbitUserInfo = new FitbitUserInfoDto();
		UserStudiesDto userStudy = new UserStudiesDto().setEnrollmentId("1");

		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(user);
		assertThat(userService.login(email, pswd).getError().getCode()).isEqualTo(ErrorCode.EC_31.code());

		user.setStatus(true);
		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(user);
		assertThat(userService.login(email, pswd).getError().getCode()).isEqualTo(ErrorCode.EC_33.code());

		user.setStatus(true).setUserPassword("").setResetPassword(encrypted_pswd)
				.setTempPasswordExipiryDate(AppUtil.addMinutes(AppUtil.getCurrentDateTime(), -30));

		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(user);
		assertThat(userService.login(email, pswd).getError().getCode()).isEqualTo(ErrorCode.EC_33.code());

		user.setStatus(true).setUserPassword(encrypted_pswd).setResetPassword("")
				.setTempPasswordExipiryDate(AppUtil.addMinutes(AppUtil.getCurrentDateTime(), 30));

		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(user);
		when(userDao.fetchAuthInfoDetails(anyString(), anyString())).thenReturn(authInfo);
		when(userDao.saveOrUpdateAuthInfoDetails(anyObject(), anyString())).thenReturn(authInfo);
		when(userDao.fetchFitbitUserInfo(anyString(), anyString())).thenReturn(fitbitUserInfo);
		when(studyDao.fetchUserStudiesDetails(anyString(), anyString(), anyString())).thenReturn(userStudy);
		assertThat(userService.login(email, pswd).getError().getCode()).isEqualTo(ErrorCode.EC_200.code());

		user.setStatus(true).setUserPassword("").setResetPassword(encrypted_pswd);
		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(user);
		when(userDao.fetchAuthInfoDetails(anyString(), anyString())).thenReturn(authInfo);
		when(userDao.saveOrUpdateAuthInfoDetails(anyObject(), anyString())).thenReturn(authInfo);
		when(userDao.fetchFitbitUserInfo(anyString(), anyString())).thenReturn(fitbitUserInfo);
		when(studyDao.fetchUserStudiesDetails(anyString(), anyString(), anyString())).thenReturn(userStudy);
		assertThat(userService.login(email, pswd).getError().getCode()).isEqualTo(ErrorCode.EC_200.code());

		user.setStatus(true).setUserPassword("").setResetPassword(encrypted_pswd).setLass4uId("1234")
				.setVerificationKey("123");

		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(user);
		when(userDao.fetchAuthInfoDetails(anyString(), anyString())).thenReturn(authInfo);
		when(userDao.saveOrUpdateAuthInfoDetails(anyObject(), anyString())).thenReturn(authInfo);
		when(userDao.fetchFitbitUserInfo(anyString(), anyString())).thenReturn(null);
		when(studyDao.fetchUserStudiesDetails(anyString(), anyString(), anyString())).thenReturn(null);
		assertThat(userService.login(email, pswd).getError().getCode()).isEqualTo(ErrorCode.EC_200.code());

		userStudy.setEnrollmentId("");
		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(user);
		when(userDao.fetchAuthInfoDetails(anyString(), anyString())).thenReturn(authInfo);
		when(userDao.saveOrUpdateAuthInfoDetails(anyObject(), anyString())).thenReturn(authInfo);
		when(userDao.fetchFitbitUserInfo(anyString(), anyString())).thenReturn(null);
		when(studyDao.fetchUserStudiesDetails(anyString(), anyString(), anyString())).thenReturn(userStudy);
		assertThat(userService.login(email, pswd).getError().getCode()).isEqualTo(ErrorCode.EC_200.code());
	}

	// *************************** emailVerification ***************************
	@Test
	public void test_emailVerification_when_userinfo_doesnot_exists() throws Exception {

		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(null);
		assertThat(userService.emailVerification("1", "12345", "1").getError().getCode())
				.isEqualTo(ErrorCode.EC_61.code());
	}

	@Test
	public void test_emailVerification_when_userinfo_exists() throws Exception {

		UserDto user = new UserDto();

		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(user);
		assertThat(userService.emailVerification("1", "12345", "1").getError().getCode())
				.isEqualTo(ErrorCode.EC_52.code());

		user.setVerificationKey("456");
		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(user);
		assertThat(userService.emailVerification("1", "12345", "1").getError().getCode())
				.isEqualTo(ErrorCode.EC_52.code());

		user.setVerificationKey("1");
		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(user);
		when(userDao.saveOrUpdateUserDetails(anyObject(), anyString())).thenReturn(user);
		assertThat(userService.emailVerification("1", "12345", "1").getError().getCode())
				.isEqualTo(ErrorCode.EC_200.code());
	}

	// *************************** resendVerificationToken
	// ***************************
	@Test
	public void test_resendVerificationToken_when_userinfo_doesnot_exists() throws Exception {

		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(null);
		assertThat(userService.resendVerificationToken("1").getError().getCode()).isEqualTo(ErrorCode.EC_61.code());
	}

	@Test
	public void test_resendVerificationToken_when_userinfo_exists() throws Exception {

		UserDto user = new UserDto().setLanguage(AppConstants.USER_LANGUAGE_SPANISH).setFirstName("Mohan")
				.setVerificationKey("1");

		StudiesDto studyDto = new StudiesDto().setStudyName("GBA COGfx");

		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(user);
		when(userDao.saveOrUpdateUserDetails(anyObject(), anyString())).thenReturn(user);
		when(studyDao.fetchStudyDetailsByStudyId(anyString())).thenReturn(studyDto);
		assertThat(userService.resendVerificationToken("1").getError().getCode()).isEqualTo(ErrorCode.EC_200.code());
	}

	// *************************** forgotPassword ***************************
	@Test
	public void test_forgotPassword_when_userinfo_doesnot_exists() throws Exception {

		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(null);
		assertThat(userService.forgotPassword(email).getError().getCode()).isEqualTo(ErrorCode.EC_71.code());
	}

	@Test
	public void test_forgotPassword_when_userinfo_exists() throws Exception {

		UserDto user = new UserDto().setLanguage(AppConstants.USER_LANGUAGE_SPANISH).setFirstName("Mohan");

		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(user);
		when(userDao.saveOrUpdateUserDetails(anyObject(), anyString())).thenReturn(user);
		assertThat(userService.forgotPassword(email).getError().getCode()).isEqualTo(ErrorCode.EC_200.code());

		user.setLanguage(AppConstants.USER_LANGUAGE_CHINESE);
		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(user);
		when(userDao.saveOrUpdateUserDetails(anyObject(), anyString())).thenReturn(user);
		assertThat(userService.forgotPassword(email).getError().getCode()).isEqualTo(ErrorCode.EC_200.code());

		user.setLanguage(AppConstants.USER_LANGUAGE_ENGLISH);
		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(user);
		when(userDao.saveOrUpdateUserDetails(anyObject(), anyString())).thenReturn(user);
		assertThat(userService.forgotPassword(email).getError().getCode()).isEqualTo(ErrorCode.EC_200.code());
	}

	// *************************** userProfileDetails ***************************
	@Test
	public void test_userProfileDetails_when_userinfo_doesnot_exists() throws Exception {

		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(null);
		assertThat(userService.userProfileDetails("1").getError().getCode()).isEqualTo(ErrorCode.EC_61.code());
	}

	@Test
	public void test_userProfileDetails_when_authInfo_doesnot_exists() throws Exception {

		UserDto user = new UserDto();

		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(user);
		when(userDao.fetchAuthInfoDetails(anyString(), anyString())).thenReturn(null);
		assertThat(userService.userProfileDetails("1").getError().getCode()).isEqualTo(ErrorCode.EC_61.code());
	}

	@Test
	public void test_userProfileDetails_when_userInfo_and_authInfo_exists() throws Exception {

		UserDto user = new UserDto();
		AuthInfoDto authInfo = new AuthInfoDto();
		FitbitUserInfoDto fitbitUserInfoDto = new FitbitUserInfoDto();

		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(user);
		when(userDao.fetchAuthInfoDetails(anyString(), anyString())).thenReturn(authInfo);
		when(userDao.fetchFitbitUserInfo(anyString(), anyString())).thenReturn(null);
		assertThat(userService.userProfileDetails("1").getError().getCode()).isEqualTo(ErrorCode.EC_200.code());

		user.setLass4uId("1234").setFitbitId("123");
		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(user);
		when(userDao.fetchAuthInfoDetails(anyString(), anyString())).thenReturn(authInfo);
		when(userDao.fetchFitbitUserInfo(anyString(), anyString())).thenReturn(fitbitUserInfoDto);
		assertThat(userService.userProfileDetails("1").getError().getCode()).isEqualTo(ErrorCode.EC_200.code());

		fitbitUserInfoDto.setFitbitAccessToken("1234").setFitbitRefreshToken("1234");
		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(user);
		when(userDao.fetchAuthInfoDetails(anyString(), anyString())).thenReturn(authInfo);
		when(userDao.fetchFitbitUserInfo(anyString(), anyString())).thenReturn(fitbitUserInfoDto);
		assertThat(userService.userProfileDetails("1").getError().getCode()).isEqualTo(ErrorCode.EC_200.code());
	}

	// *************************** updateUserProfile ***************************
	@Test
	public void test_updateUserProfile_when_userinfo_doesnot_exists() throws Exception {

		SettingsBean settings = new SettingsBean();

		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(null);
		assertThat(userService.updateUserProfile(settings, "1", false).getError().getCode())
				.isEqualTo(ErrorCode.EC_61.code());
	}

	@Test
	public void test_updateUserProfile_when_userinfo_exists() throws Exception {

		UserDto user = new UserDto();
		SettingsBean settings = new SettingsBean();

		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(user);
		when(userDao.saveOrUpdateUserDetails(anyObject(), anyString())).thenReturn(user);
		assertThat(userService.updateUserProfile(settings, "1", false).getError().getCode())
				.isEqualTo(ErrorCode.EC_200.code());

		settings.setLass4u("1234").setFitbit("123").setLanguage(AppConstants.USER_LANGUAGE_ENGLISH)
				.setTemperature(AppConstants.TEMPERATURE_CELCIUS);
		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(user);
		when(userDao.saveOrUpdateUserDetails(anyObject(), anyString())).thenReturn(user);
		assertThat(userService.updateUserProfile(settings, "1", false).getError().getCode())
				.isEqualTo(ErrorCode.EC_200.code());
	}

	// *************************** changePassword ***************************
	@Test
	public void test_changePassword_when_userinfo_doesnot_exists() throws Exception {

		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(null);
		assertThat(userService.changePassword("1", pswd, pswd).getError().getCode()).isEqualTo(ErrorCode.EC_61.code());
	}

	@Test
	public void test_changePassword_when_userinfo_exists() throws Exception {

		UserDto user = new UserDto().setUserPassword("").setResetPassword("");

		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(user);
		assertThat(userService.changePassword("1", pswd, pswd).getError().getCode()).isEqualTo(ErrorCode.EC_73.code());

		user.setUserPassword(encrypted_pswd).setResetPassword("");
		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(user);
		assertThat(userService.changePassword("1", pswd, pswd).getError().getCode()).isEqualTo(ErrorCode.EC_72.code());

		user.setUserPassword("").setResetPassword(encrypted_pswd);
		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(user);
		assertThat(userService.changePassword("1", pswd, pswd).getError().getCode()).isEqualTo(ErrorCode.EC_72.code());

		user.setUserPassword("").setResetPassword(encrypted_pswd);
		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(user);
		when(userDao.saveOrUpdateUserDetails(anyObject(), anyString())).thenReturn(user);
		assertThat(userService.changePassword("1", pswd, "Password@1234").getError().getCode())
				.isEqualTo(ErrorCode.EC_200.code());

	}

	// *************************** logout ***************************
	@Test
	public void test_logout_when_userinfo_doesnot_exists() throws Exception {

		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(null);
		assertThat(userService.logout("1").getError().getCode()).isEqualTo(ErrorCode.EC_61.code());
	}

	@Test
	public void test_logout_when_authinfo_doesnot_exists() throws Exception {

		UserDto user = new UserDto();

		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(user);
		when(userDao.fetchAuthInfoDetails(anyString(), anyString())).thenReturn(null);
		assertThat(userService.logout("1").getError().getCode()).isEqualTo(ErrorCode.EC_61.code());
	}

	@Test
	public void test_logout_when_userInfo_authInfo_exists() throws Exception {

		UserDto user = new UserDto();
		AuthInfoDto authInfo = new AuthInfoDto();

		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(user);
		when(userDao.fetchAuthInfoDetails(anyString(), anyString())).thenReturn(authInfo);
		when(userDao.saveOrUpdateUserDetails(anyObject(), anyString())).thenReturn(user);
		when(userDao.saveOrUpdateAuthInfoDetails(anyObject(), anyString())).thenReturn(authInfo);
		assertThat(userService.logout("1").getError().getCode()).isEqualTo(ErrorCode.EC_200.code());
	}

	// *************************** refreshFitbitAccessToken
	// ***************************
	@Test
	public void test_refreshFitbitAccessToken_when_fitbitUserInfo_doesnot_exists() throws Exception {

		/*when(userDao.fetchFitbitUserInfo(anyString(), anyString())).thenReturn(null);
		assertThat(userService.refreshFitbitAccessToken("1").getError().getCode()).isEqualTo(ErrorCode.EC_107.code());*/
	}

	/*@Test
	public void test_refreshFitbitAccessToken_when_fitbitUserInfo_exists_invalid() throws Exception {

		FitbitUserInfoDto fitbitUserInfo = new FitbitUserInfoDto().setFitbitAccessToken("1234")
				.setFitbitRefreshToken("12123").setUserId(1);

		when(userDao.fetchFitbitUserInfo(anyString(), anyString())).thenReturn(fitbitUserInfo);
		assertThat(userService.refreshFitbitAccessToken("1").getError().getCode()).isEqualTo(ErrorCode.EC_107.code());
	}*/
}
