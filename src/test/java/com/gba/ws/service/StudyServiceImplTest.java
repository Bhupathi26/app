/**
 * 
 */
package com.gba.ws.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.gba.ws.dao.StudyDao;
import com.gba.ws.dao.UserDao;
import com.gba.ws.model.AdminUsersDto;
import com.gba.ws.model.EnrollmentTokensDto;
import com.gba.ws.model.StudiesDto;
import com.gba.ws.model.StudyConsentDto;
import com.gba.ws.model.UserDto;
import com.gba.ws.model.UserStudiesDto;
import com.gba.ws.util.AppConstants;
import com.gba.ws.util.ErrorCode;

/**
 * @author Mohan
 * @createdOn Jan 10, 2018 10:38:27 AM
 */
@RunWith(value = SpringRunner.class)
@SpringBootTest(classes = { StudyServiceImpl.class })
public class StudyServiceImplTest {

	@Autowired
	private StudyService studyService;

	@MockBean
	private UserDao userDao;

	@MockBean
	private StudyDao studyDao;

	@Before
	public void setUp() throws Exception {

	}

	// *************************** validateStudyId ***************************
	@Test
	public void test_validateStudyId_when_study_is_not_present() throws Exception {

		when(studyDao.validateStudyId(anyString())).thenReturn(false);
		assertThat(studyService.validateStudyId("1")).isFalse();

	}

	// *************************** verifiyEligibility ***************************
	@Test
	public void test_verifiyEligibility_when_enrollment_id_is_invalid() throws Exception {

		when(studyDao.fetchEnrollmentTokenDetails(anyString(), anyString())).thenReturn(null);
		assertThat(studyService.verifiyEligibility("1", "1", "1").getError().getCode())
				.isEqualTo(ErrorCode.EC_92.code());
	}

	@Test
	public void test_verifiyEligibility_when_enrollment_id_is_not_active() throws Exception {

		EnrollmentTokensDto enrollmentTokenDto = new EnrollmentTokensDto().setIsActive(false);

		when(studyDao.fetchEnrollmentTokenDetails(anyString(), anyString())).thenReturn(enrollmentTokenDto);
		assertThat(studyService.verifiyEligibility("1", "1", "1").getError().getCode())
				.isEqualTo(ErrorCode.EC_91.code());
	}

	@Test
	public void test_verifiyEligibility_when_enrollment_id_is_active() throws Exception {

		EnrollmentTokensDto enrollmentTokenDto = new EnrollmentTokensDto().setIsActive(true);

		when(studyDao.fetchEnrollmentTokenDetails(anyString(), anyString())).thenReturn(enrollmentTokenDto);
		assertThat(studyService.verifiyEligibility("1", "1", "1").getError().getCode())
				.isEqualTo(ErrorCode.EC_200.code());
	}

	// *************************** enrollInStudy ***************************
	@Test
	public void test_enrollInStudy_when_enrollment_id_is_invalid() throws Exception {

		when(studyDao.fetchEnrollmentTokenDetails(anyString(), anyString())).thenReturn(null);
		assertThat(studyService.enrollInStudy("1", "1", "1").getError().getCode()).isEqualTo(ErrorCode.EC_92.code());
	}

	@Test
	public void test_enrollInStudy_when_enrollment_id_is_not_active() throws Exception {

		EnrollmentTokensDto enrollmentTokenDto = new EnrollmentTokensDto().setIsActive(false);

		when(studyDao.fetchEnrollmentTokenDetails(anyString(), anyString())).thenReturn(enrollmentTokenDto);
		assertThat(studyService.enrollInStudy("1", "1", "1").getError().getCode()).isEqualTo(ErrorCode.EC_91.code());
	}

	@Test
	public void test_enrollInStudy_when_study_consent_details_are_not_present() throws Exception {

		EnrollmentTokensDto enrollmentTokenDto = new EnrollmentTokensDto().setIsActive(true);

		UserDto user = new UserDto();
		StudyConsentDto studyConsent = new StudyConsentDto().setConsentPdf("");

		when(studyDao.fetchEnrollmentTokenDetails(anyString(), anyString())).thenReturn(enrollmentTokenDto);
		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(user);
		when(studyDao.fetchStudyConsentDetails(anyString(), anyString(), anyString())).thenReturn(null);

		assertThat(studyService.enrollInStudy("1", "1", "1").getError().getCode()).isEqualTo(ErrorCode.EC_94.code());

		when(studyDao.fetchEnrollmentTokenDetails(anyString(), anyString())).thenReturn(enrollmentTokenDto);
		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(user);
		when(studyDao.fetchStudyConsentDetails(anyString(), anyString(), anyString())).thenReturn(studyConsent);

		assertThat(studyService.enrollInStudy("1", "1", "1").getError().getCode()).isEqualTo(ErrorCode.EC_94.code());
	}

	@Test
	public void test_enrollInStudy_when_user_study_details_are_not_present() throws Exception {

		EnrollmentTokensDto enrollmentTokenDto = new EnrollmentTokensDto().setIsActive(true);

		UserDto user = new UserDto();
		StudyConsentDto studyConsent = new StudyConsentDto().setConsentPdf("consent.pdf");
		UserStudiesDto userStudy = new UserStudiesDto();
		List<AdminUsersDto> adminUsers = new ArrayList<>();

		when(studyDao.fetchEnrollmentTokenDetails(anyString(), anyString())).thenReturn(enrollmentTokenDto);
		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(user);
		when(studyDao.fetchStudyConsentDetails(anyString(), anyString(), anyString())).thenReturn(studyConsent);
		when(studyDao.fetchUserStudiesDetails(anyString(), anyString(), anyString())).thenReturn(null);
		when(studyDao.saveOrUpdateUserStudies(anyObject(), anyString())).thenReturn(userStudy);
		when(studyDao.saveOrUpdateEnrollmentTokens(anyObject(), anyString())).thenReturn(enrollmentTokenDto);
		when(studyDao.fetchStudyDetailsByStudyId(anyString())).thenReturn(null);
		when(studyDao.fetchAllAdminUsers()).thenReturn(adminUsers);

		assertThat(studyService.enrollInStudy("1", "1", "1").getError().getCode()).isEqualTo(ErrorCode.EC_200.code());
	}

	@Test
	public void test_enrollInStudy_when_user_study_details_are_present() throws Exception {

		EnrollmentTokensDto enrollmentTokenDto = new EnrollmentTokensDto().setIsActive(true);

		UserDto user = new UserDto();
		StudyConsentDto studyConsent = new StudyConsentDto().setConsentPdf("consent.pdf");
		UserStudiesDto userStudy = new UserStudiesDto();
		List<AdminUsersDto> adminUsers = new ArrayList<>();
		adminUsers.add(new AdminUsersDto().setEmail("apps@boston-technology.com"));

		when(studyDao.fetchEnrollmentTokenDetails(anyString(), anyString())).thenReturn(enrollmentTokenDto);
		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(user);
		when(studyDao.fetchStudyConsentDetails(anyString(), anyString(), anyString())).thenReturn(studyConsent);
		when(studyDao.fetchUserStudiesDetails(anyString(), anyString(), anyString())).thenReturn(userStudy);
		when(studyDao.saveOrUpdateUserStudies(anyObject(), anyString())).thenReturn(userStudy);
		when(studyDao.saveOrUpdateEnrollmentTokens(anyObject(), anyString())).thenReturn(enrollmentTokenDto);
		when(studyDao.fetchStudyDetailsByStudyId(anyString())).thenReturn(null);
		when(studyDao.fetchAllAdminUsers()).thenReturn(adminUsers);

		assertThat(studyService.enrollInStudy("1", "1", "1").getError().getCode()).isEqualTo(ErrorCode.EC_200.code());
	}

	@Test
	public void test_enrollInStudy_when_study_and_admins_details_are_present() throws Exception {

		EnrollmentTokensDto enrollmentTokenDto = new EnrollmentTokensDto().setIsActive(true);
		UserDto user = new UserDto().setFirstName("Mohan").setLanguage(AppConstants.USER_LANGUAGE_SPANISH);
		StudyConsentDto studyConsent = new StudyConsentDto().setConsentPdf("consent.pdf");
		UserStudiesDto userStudy = new UserStudiesDto();
		StudiesDto study = new StudiesDto().setStudyName("COGfx");
		List<AdminUsersDto> adminUsers = new ArrayList<>();
		adminUsers.add(new AdminUsersDto().setEmail("apps@boston-technology.com"));

		when(studyDao.fetchEnrollmentTokenDetails(anyString(), anyString())).thenReturn(enrollmentTokenDto);
		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(user);
		when(studyDao.fetchStudyConsentDetails(anyString(), anyString(), anyString())).thenReturn(studyConsent);
		when(studyDao.fetchUserStudiesDetails(anyString(), anyString(), anyString())).thenReturn(userStudy);
		when(studyDao.saveOrUpdateUserStudies(anyObject(), anyString())).thenReturn(userStudy);
		when(studyDao.saveOrUpdateEnrollmentTokens(anyObject(), anyString())).thenReturn(enrollmentTokenDto);
		when(studyDao.fetchStudyDetailsByStudyId(anyString())).thenReturn(study);
		when(studyDao.fetchAllAdminUsers()).thenReturn(adminUsers);

		assertThat(studyService.enrollInStudy("1", "1", "1").getError().getCode()).isEqualTo(ErrorCode.EC_200.code());

		user.setLanguage(AppConstants.USER_LANGUAGE_CHINESE);
		enrollmentTokenDto.setIsActive(true);

		when(studyDao.fetchEnrollmentTokenDetails(anyString(), anyString())).thenReturn(enrollmentTokenDto);
		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(user);
		when(studyDao.fetchStudyConsentDetails(anyString(), anyString(), anyString())).thenReturn(studyConsent);
		when(studyDao.fetchUserStudiesDetails(anyString(), anyString(), anyString())).thenReturn(userStudy);
		when(studyDao.saveOrUpdateUserStudies(anyObject(), anyString())).thenReturn(userStudy);
		when(studyDao.saveOrUpdateEnrollmentTokens(anyObject(), anyString())).thenReturn(enrollmentTokenDto);
		when(studyDao.fetchStudyDetailsByStudyId(anyString())).thenReturn(study);
		when(studyDao.fetchAllAdminUsers()).thenReturn(adminUsers);

		assertThat(studyService.enrollInStudy("1", "1", "1").getError().getCode()).isEqualTo(ErrorCode.EC_200.code());

		user.setLanguage(AppConstants.USER_LANGUAGE_ENGLISH);
		enrollmentTokenDto.setIsActive(true);

		when(studyDao.fetchEnrollmentTokenDetails(anyString(), anyString())).thenReturn(enrollmentTokenDto);
		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(user);
		when(studyDao.fetchStudyConsentDetails(anyString(), anyString(), anyString())).thenReturn(studyConsent);
		when(studyDao.fetchUserStudiesDetails(anyString(), anyString(), anyString())).thenReturn(userStudy);
		when(studyDao.saveOrUpdateUserStudies(anyObject(), anyString())).thenReturn(userStudy);
		when(studyDao.saveOrUpdateEnrollmentTokens(anyObject(), anyString())).thenReturn(enrollmentTokenDto);
		when(studyDao.fetchStudyDetailsByStudyId(anyString())).thenReturn(study);
		when(studyDao.fetchAllAdminUsers()).thenReturn(adminUsers);

		assertThat(studyService.enrollInStudy("1", "1", "1").getError().getCode()).isEqualTo(ErrorCode.EC_200.code());
	}

	// *************************** getSignedConsent ***************************
	@Test
	public void test_fetchStudyConsentDetails_when_study_consent_are_not_present() throws Exception {

		when(studyDao.fetchStudyConsentDetails(anyString(), anyString(), anyString())).thenReturn(null);
		assertThat(studyService.getSignedConsent("1", "1").getError().getCode()).isEqualTo(ErrorCode.EC_94.code());

	}

	@Test
	public void test_fetchStudyConsentDetails_when_study_consent_document_is_empty() throws Exception {

		StudyConsentDto studyConsent = new StudyConsentDto();

		when(studyDao.fetchStudyConsentDetails(anyString(), anyString(), anyString())).thenReturn(studyConsent);
		assertThat(studyService.getSignedConsent("1", "1").getError().getCode()).isEqualTo(ErrorCode.EC_108.code());

	}

	@Test
	public void test_fetchStudyConsentDetails_when_study_consent_document_is_present() throws Exception {

		StudyConsentDto studyConsent = new StudyConsentDto().setConsentPdf("consnet.pdf");

		when(studyDao.fetchStudyConsentDetails(anyString(), anyString(), anyString())).thenReturn(studyConsent);
		assertThat(studyService.getSignedConsent("1", "1").getError().getCode()).isEqualTo(ErrorCode.EC_200.code());

	}

	// *************************** storeSignedConsent ***************************
	@Test
	public void test_storeSignedConsent_when_study_consent_are_not_present() throws Exception {

		StudyConsentDto studyConsent = new StudyConsentDto();

		when(studyDao.fetchStudyConsentDetails(anyString(), anyString(), anyString())).thenReturn(null);
		when(studyDao.saveOrUpdateStudyConsent(anyObject(), anyString())).thenReturn(studyConsent);

		assertThat(studyService.storeSignedConsent("1", "1", "consent.pdf").getError().getCode())
				.isEqualTo(ErrorCode.EC_200.code());
	}

	@Test
	public void test_storeSignedConsent_when_study_consent_are_present() throws Exception {

		StudyConsentDto studyConsent = new StudyConsentDto();

		when(studyDao.fetchStudyConsentDetails(anyString(), anyString(), anyString())).thenReturn(studyConsent);
		when(studyDao.saveOrUpdateStudyConsent(anyObject(), anyString())).thenReturn(studyConsent);

		assertThat(studyService.storeSignedConsent("1", "1", "consent.pdf").getError().getCode())
				.isEqualTo(ErrorCode.EC_200.code());

		studyConsent.setConsentPdf("consent.pdf");

		when(studyDao.fetchStudyConsentDetails(anyString(), anyString(), anyString())).thenReturn(studyConsent);
		when(studyDao.saveOrUpdateStudyConsent(anyObject(), anyString())).thenReturn(studyConsent);

		assertThat(studyService.storeSignedConsent("1", "1", "consent.pdf").getError().getCode())
				.isEqualTo(ErrorCode.EC_200.code());

	}

	// *************************** leaveStudy ***************************
	@Test
	public void test_leaveStudy_when_userDetails_are_not_present() throws Exception {

		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(null);
		assertThat(studyService.leaveStudy("1", "1").getError().getCode()).isEqualTo(ErrorCode.EC_404.code());
	}

	@Test
	public void test_leaveStudy_when_studyConsnetDetails_are_not_present() throws Exception {

		UserDto user = new UserDto();

		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(user);
		when(studyDao.fetchStudyConsentDetails(anyString(), anyString(), anyString())).thenReturn(null);
		assertThat(studyService.leaveStudy("1", "1").getError().getCode()).isEqualTo(ErrorCode.EC_404.code());
	}

	@Test
	public void test_leaveStudy_when_userStudyDetails_are_not_present() throws Exception {

		UserDto user = new UserDto();
		StudyConsentDto studyConsent = new StudyConsentDto();

		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(user);
		when(studyDao.fetchStudyConsentDetails(anyString(), anyString(), anyString())).thenReturn(studyConsent);
		when(studyDao.fetchUserStudiesDetails(anyString(), anyString(), anyString())).thenReturn(null);
		assertThat(studyService.leaveStudy("1", "1").getError().getCode()).isEqualTo(ErrorCode.EC_404.code());
	}

	@Test
	public void test_leaveStudy_when_studyDetails_are_not_present() throws Exception {

		UserDto user = new UserDto();
		StudyConsentDto studyConsent = new StudyConsentDto();
		UserStudiesDto userStudy = new UserStudiesDto();

		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(user);
		when(studyDao.fetchStudyConsentDetails(anyString(), anyString(), anyString())).thenReturn(studyConsent);
		when(studyDao.fetchUserStudiesDetails(anyString(), anyString(), anyString())).thenReturn(userStudy);
		when(studyDao.fetchStudyDetailsByStudyId(anyString())).thenReturn(null);
		assertThat(studyService.leaveStudy("1", "1").getError().getCode()).isEqualTo(ErrorCode.EC_404.code());
	}

	@Test
	public void test_leaveStudy_when_adminUsers_are_not_present() throws Exception {

		UserDto user = new UserDto().setFirstName("Mohan").setLanguage(AppConstants.USER_LANGUAGE_SPANISH);

		StudyConsentDto studyConsent = new StudyConsentDto();
		UserStudiesDto userStudy = new UserStudiesDto();
		StudiesDto study = new StudiesDto().setStudyName("COGfx");
		List<AdminUsersDto> adminUsers = new ArrayList<>();

		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(user);
		when(studyDao.fetchStudyConsentDetails(anyString(), anyString(), anyString())).thenReturn(studyConsent);
		when(studyDao.fetchUserStudiesDetails(anyString(), anyString(), anyString())).thenReturn(userStudy);
		when(studyDao.fetchStudyDetailsByStudyId(anyString())).thenReturn(study);
		when(studyDao.fetchAllAdminUsers()).thenReturn(adminUsers);
		when(userDao.saveOrUpdateUserDetails(anyObject(), anyString())).thenReturn(user);
		when(studyDao.saveOrUpdateStudyConsent(anyObject(), anyString())).thenReturn(studyConsent);
		when(studyDao.saveOrUpdateUserStudies(anyObject(), anyString())).thenReturn(userStudy);

		assertThat(studyService.leaveStudy("1", "1").getError().getCode()).isEqualTo(ErrorCode.EC_200.code());

		user.setLanguage(AppConstants.USER_LANGUAGE_CHINESE);

		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(user);
		when(studyDao.fetchStudyConsentDetails(anyString(), anyString(), anyString())).thenReturn(studyConsent);
		when(studyDao.fetchUserStudiesDetails(anyString(), anyString(), anyString())).thenReturn(userStudy);
		when(studyDao.fetchStudyDetailsByStudyId(anyString())).thenReturn(study);
		when(studyDao.fetchAllAdminUsers()).thenReturn(adminUsers);
		when(userDao.saveOrUpdateUserDetails(anyObject(), anyString())).thenReturn(user);
		when(studyDao.saveOrUpdateStudyConsent(anyObject(), anyString())).thenReturn(studyConsent);
		when(studyDao.saveOrUpdateUserStudies(anyObject(), anyString())).thenReturn(userStudy);

		assertThat(studyService.leaveStudy("1", "1").getError().getCode()).isEqualTo(ErrorCode.EC_200.code());

		user.setLanguage(AppConstants.USER_LANGUAGE_ENGLISH);

		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(user);
		when(studyDao.fetchStudyConsentDetails(anyString(), anyString(), anyString())).thenReturn(studyConsent);
		when(studyDao.fetchUserStudiesDetails(anyString(), anyString(), anyString())).thenReturn(userStudy);
		when(studyDao.fetchStudyDetailsByStudyId(anyString())).thenReturn(study);
		when(studyDao.fetchAllAdminUsers()).thenReturn(adminUsers);
		when(userDao.saveOrUpdateUserDetails(anyObject(), anyString())).thenReturn(user);
		when(studyDao.saveOrUpdateStudyConsent(anyObject(), anyString())).thenReturn(studyConsent);
		when(studyDao.saveOrUpdateUserStudies(anyObject(), anyString())).thenReturn(userStudy);

		assertThat(studyService.leaveStudy("1", "1").getError().getCode()).isEqualTo(ErrorCode.EC_200.code());
	}

	@Test
	public void test_leaveStudy_when_adminUsers_are_present() throws Exception {

		UserDto user = new UserDto().setFirstName("Mohan").setLanguage(AppConstants.USER_LANGUAGE_SPANISH);

		StudyConsentDto studyConsent = new StudyConsentDto();
		UserStudiesDto userStudy = new UserStudiesDto();
		StudiesDto study = new StudiesDto().setStudyName("COGfx");
		List<AdminUsersDto> adminUsers = new ArrayList<>();
		adminUsers.add(new AdminUsersDto().setEmail("apps@boston-tehnology.com"));

		when(userDao.fetchUserDetails(anyString(), anyString())).thenReturn(user);
		when(studyDao.fetchStudyConsentDetails(anyString(), anyString(), anyString())).thenReturn(studyConsent);
		when(studyDao.fetchUserStudiesDetails(anyString(), anyString(), anyString())).thenReturn(userStudy);
		when(studyDao.fetchStudyDetailsByStudyId(anyString())).thenReturn(study);
		when(studyDao.fetchAllAdminUsers()).thenReturn(adminUsers);
		when(userDao.saveOrUpdateUserDetails(anyObject(), anyString())).thenReturn(user);
		when(studyDao.saveOrUpdateStudyConsent(anyObject(), anyString())).thenReturn(studyConsent);
		when(studyDao.saveOrUpdateUserStudies(anyObject(), anyString())).thenReturn(userStudy);

		assertThat(studyService.leaveStudy("1", "1").getError().getCode()).isEqualTo(ErrorCode.EC_200.code());
	}
}
