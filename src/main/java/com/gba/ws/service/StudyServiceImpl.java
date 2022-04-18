package com.gba.ws.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gba.ws.bean.GroupLocationResponse;
import com.gba.ws.bean.ErrorBean;
import com.gba.ws.bean.ErrorResponse;
import com.gba.ws.bean.SignedConsentResponse;
import com.gba.ws.dao.ActivityDao;
import com.gba.ws.dao.StudyDao;
import com.gba.ws.dao.UserDao;
import com.gba.ws.exception.CustomException;
import com.gba.ws.model.AdminUsersDto;
import com.gba.ws.model.GroupIdentifierDto;
import com.gba.ws.model.GroupUsersInfoDto;
import com.gba.ws.model.QuestionChoiceDto;
import com.gba.ws.model.EnrollmentTokensDto;
import com.gba.ws.model.ResponseActivityTempDto;
import com.gba.ws.model.ResponsesSurveysActivitiesDto;
import com.gba.ws.model.ResponsesTasksActivitiesDto;
import com.gba.ws.model.StudiesDto;
import com.gba.ws.model.StudyConsentDto;
import com.gba.ws.model.UserDto;
import com.gba.ws.model.UserStudiesDto;
import com.gba.ws.util.AppConstants;
import com.gba.ws.util.AppUtil;
import com.gba.ws.util.ErrorCode;
import com.gba.ws.util.Mail;
import com.gba.ws.util.MailContent;
import com.gba.ws.util.ResponseSurveysResultList;
import com.gba.ws.util.ResponseTaskListValues;
import com.gba.ws.util.ResponseTaskResultList;
import com.gba.ws.util.ResponsesSurveyResults;
import com.gba.ws.util.ResponsesTaskResults;

import java.io.FileInputStream;
import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.hibernate.annotations.Type;

/**
 * Implements {@link StudyService} interface.
 * 
 * @author Mohan
 * @createdOn Jan 9, 2018 12:22:25 PM
 */
@Service
public class StudyServiceImpl implements StudyService {

	private static final Logger LOGGER = Logger.getLogger(StudyServiceImpl.class);

	@Autowired
	private UserDao userDao;

	@Autowired
	private StudyDao studyDao;
	
	@Autowired
	private ActivityDao activityDao;

	/**
	 * @param userDao
	 *            the {@link UserDao}
	 * @param studyDao
	 *            the {@link StudyDao}
	 */
	public StudyServiceImpl(UserDao userDao, StudyDao studyDao,ActivityDao activityDao) {
		super();
		this.userDao = userDao;
		this.studyDao = studyDao;
		this.activityDao = activityDao;
	}

	@Override
	public boolean validateStudyId(String studyId) throws CustomException {
		LOGGER.info("INFO: StudyServiceImpl - validateStudyId() :: starts");
		boolean isValidStudyId = false;
		try {
			isValidStudyId = studyDao.validateStudyId(studyId);
		} catch (Exception e) {
			LOGGER.error("ERROR: StudyServiceImpl - validateStudyId()", e);
		}
		LOGGER.info("INFO: StudyServiceImpl - validateStudyId() :: ends");
		return isValidStudyId;
	}

	@Override
	public ErrorResponse verifiyEligibility(String userId, String studyId, String token) throws CustomException {
		LOGGER.info("INFO: StudyServiceImpl - verifiyEligibility() :: starts");
		ErrorResponse errorResponse = new ErrorResponse();
		EnrollmentTokensDto enrollmentTokenDto = null;
		try {
			enrollmentTokenDto = studyDao.fetchEnrollmentTokenDetails(token, AppConstants.FIND_BY_TYPE_ENROLLMENTID);
			if (enrollmentTokenDto == null) {
				return new ErrorResponse().setError(
						new ErrorBean().setCode(ErrorCode.EC_92.code()).setMessage(ErrorCode.EC_92.errorMessage()));
			}

			if (!enrollmentTokenDto.getIsActive()) {
				return new ErrorResponse().setError(
						new ErrorBean().setCode(ErrorCode.EC_91.code()).setMessage(ErrorCode.EC_91.errorMessage()));
			}

			errorResponse.setError(new ErrorBean().setCode(ErrorCode.EC_200.code())
					.setMessage(AppUtil.getAppProperties().get(AppConstants.STUDY_ELGBTY_VERIFIED)));
		} catch (Exception e) {
			LOGGER.error("ERROR: StudyServiceImpl - verifiyEligibility()", e);
		}
		LOGGER.info("INFO: StudyServiceImpl - verifiyEligibility() :: ends");
		return errorResponse;
	}

	@Override
	public ErrorResponse enrollInStudy(String userId, String studyId, String token) throws CustomException {
		LOGGER.info("INFO: StudyServiceImpl - enrollInStudy() :: starts");
		ErrorResponse errorResponse = new ErrorResponse();
		UserStudiesDto userStudies = null;
		EnrollmentTokensDto enrollmentTokenDto = null;
		List<AdminUsersDto> adminUsers = new ArrayList<>();
		List<String> toAdminList = new ArrayList<>();
		StudiesDto studyDto = null;
		StudyConsentDto studyConsent = null;
		UserDto user = null;
		UserStudiesDto saveUserStudies = null;
		GroupIdentifierDto saveGroupIdentifier  = null;
		try {
			enrollmentTokenDto = studyDao.fetchEnrollmentTokenDetails(token, AppConstants.FIND_BY_TYPE_ENROLLMENTID);
			if (enrollmentTokenDto == null) {
				return new ErrorResponse().setError(
						new ErrorBean().setCode(ErrorCode.EC_92.code()).setMessage(ErrorCode.EC_92.errorMessage()));
			}/*else {
				groupIdentifier = studyDao.fetchGroupIdentifierDetails(enrollmentTokenDto.getGroupId(),
						AppConstants.FIND_BY_TYPE_GROUP_ID_NAME);
			}*/

			if (!enrollmentTokenDto.getIsActive()) {
				return new ErrorResponse().setError(
						new ErrorBean().setCode(ErrorCode.EC_91.code()).setMessage(ErrorCode.EC_91.errorMessage()));
			}

			user = userDao.fetchUserDetails(userId, AppConstants.FIND_BY_TYPE_USERID);
			studyConsent = studyDao.fetchStudyConsentDetails(userId, studyId, AppConstants.FIND_BY_TYPE_USERID_STUDYID);
			if (studyConsent == null || StringUtils.isEmpty(studyConsent.getConsentPdf())) {
				return new ErrorResponse().setError(
						new ErrorBean().setCode(ErrorCode.EC_94.code()).setMessage(ErrorCode.EC_94.errorMessage()));
			}

			userStudies = studyDao.fetchUserStudiesDetails(userId, studyId, AppConstants.FIND_BY_TYPE_USERID_STUDYID);
			
			
			if (userStudies == null) {
				saveUserStudies = new UserStudiesDto();
				saveUserStudies.setCreatedOn(AppUtil.getCurrentDateTime()).setEligibility(true)
						.setEnrollmentId(enrollmentTokenDto.getEnrollmentId()).setUserId(Integer.parseInt(userId))
						.setStudyId(Integer.parseInt(studyId)).setActive(true)
						.setConsentId(studyConsent.getStudyConsentId());
				saveGroupIdentifier = new GroupIdentifierDto()
						.setGroupLabel(enrollmentTokenDto.getEnrollmentId())
						.setGroupIdsName(enrollmentTokenDto.getEnrollmentId())
					   .setGroupType(AppConstants.INDIVIDUAL_GROUP) ;
				
			} else {
				saveUserStudies = userStudies;
				saveUserStudies.setModifiedOn(AppUtil.getCurrentDateTime());
			}

			userStudies = studyDao.saveOrUpdateUserStudies(saveUserStudies, AppConstants.DB_SAVE_OR_UPDATE);
			
	       /*		Saving the GroupIdentifierDto for Each User , 
	        *      where Enrollemnt ID by itself will be there Group Label  and
	       *       there First three Digit will be  GroupId Name 
	       *       Updating User Details with the Individual Group Id*/
			if(null != saveGroupIdentifier) {
				saveGroupIdentifier = studyDao.saveOrUpdateGroups(saveGroupIdentifier, AppConstants.DB_SAVE);
				if(0 != saveGroupIdentifier.getGroupId()) {
					user.setGroupId(saveGroupIdentifier.getGroupId());
					user.setModifiedOn(AppUtil.getCurrentDateTime());
					userDao.saveOrUpdateUserDetails(user, AppConstants.DB_UPDATE);
				}
			}
			
			// Update the enrollment token status is inActive, since already a user used the
			// token
			enrollmentTokenDto.setIsActive(false);
			studyDao.saveOrUpdateEnrollmentTokens(enrollmentTokenDto, AppConstants.DB_UPDATE);

			// Update the tokens to active if the user not enrolled
			studyDao.activateNonEnrolledTokens();
			
			studyDto = studyDao.fetchStudyDetailsByStudyId(studyId);
			if (studyDto != null) {
				// Participant Joins a Study [Mail Participant]
				Map<String, String> mailContentMap = MailContent.consentOverviewCompletionContent(user.getFirstName(),
						studyDto.getStudyName(), user.getLanguage());
				Mail.sendEmailWithAnAttachment(user.getEmail(), mailContentMap.get(AppConstants.MAIL_SUBJECT),
						mailContentMap.get(AppConstants.MAIL_BODY), studyConsent.getConsentPdf());
			}

			// Send onboarding of new participant mail to study admins
			adminUsers = studyDao.fetchAllAdminUsers();
			if (!adminUsers.isEmpty() && studyDto != null) {
				toAdminList = adminUsers.stream().map(AdminUsersDto::getEmail).collect(Collectors.toList());

				// Participant Joins a Study [Mail Admin]
				Map<String, String> adminMailContentMap = MailContent
						.newParticipantOnBoarding(userStudies.getEnrollmentId(), studyDto.getStudyName());
				Mail.sendEmailToManyWithAnAttachment(adminMailContentMap.get(AppConstants.MAIL_SUBJECT),
						adminMailContentMap.get(AppConstants.MAIL_BODY), toAdminList, null, null,
						studyConsent.getConsentPdf());
			}

			errorResponse.setError(new ErrorBean().setCode(ErrorCode.EC_200.code())
					.setMessage(AppUtil.getAppProperties().get(AppConstants.STUDY_ENROLL)));
		} catch (Exception e) {
			LOGGER.error("ERROR: StudyServiceImpl - enrollInStudy()", e);
		}
		LOGGER.info("INFO: StudyServiceImpl - enrollInStudy() :: ends");
		return errorResponse;
	}

	@Override
	public SignedConsentResponse getSignedConsent(String userId, String studyId) throws CustomException {
		LOGGER.info("INFO: StudyServiceImpl - enrollInStudy() :: starts");
		SignedConsentResponse signedConsentResponse = new SignedConsentResponse();
		StudyConsentDto studyConsent = null;
		try {
			studyConsent = studyDao.fetchStudyConsentDetails(userId, studyId, AppConstants.FIND_BY_TYPE_USERID_STUDYID);
			if (studyConsent != null) {
				if (StringUtils.isEmpty(studyConsent.getConsentPdf())) {
					signedConsentResponse.setUrl("").setError(new ErrorBean().setCode(ErrorCode.EC_108.code())
							.setMessage(ErrorCode.EC_108.errorMessage()));
				} else {
					signedConsentResponse
							.setUrl(AppUtil.getAppProperties().get("gba.docs.download.path")
									+ studyConsent.getConsentPdf())
							.setError(new ErrorBean().setCode(ErrorCode.EC_200.code())
									.setMessage(AppUtil.getAppProperties().get(AppConstants.STUDY_SIGNED_CONSENT)));
				}
			} else {
				signedConsentResponse.setError(
						new ErrorBean().setCode(ErrorCode.EC_94.code()).setMessage(ErrorCode.EC_94.errorMessage()));
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: StudyServiceImpl - enrollInStudy()", e);
		}
		LOGGER.info("INFO: StudyServiceImpl - enrollInStudy() :: ends");
		return signedConsentResponse;
	}

	@Override
	public ErrorResponse storeSignedConsent(String userId, String studyId, String consent) throws CustomException {
		LOGGER.info("INFO: StudyServiceImpl - storeSignedConsent() :: starts");
		ErrorResponse errorResponse = new ErrorResponse();
		StudyConsentDto studyConsent = null;
		try {
			studyConsent = studyDao.fetchStudyConsentDetails(userId, studyId, AppConstants.FIND_BY_TYPE_USERID_STUDYID);
			if (studyConsent == null) {
				studyConsent = new StudyConsentDto();
				studyConsent.setUserId(Integer.parseInt(userId)).setStudyId(Integer.parseInt(studyId))
						.setConsentStatus(true).setActive(true).setCreatedOn(AppUtil.getCurrentDateTime())
						.setConsentPdf(AppUtil.saveSignedConsentDocument(consent, userId, studyId));

				studyDao.saveOrUpdateStudyConsent(studyConsent, AppConstants.DB_SAVE);
			} else {

				// Delete old consent document with new
				if (StringUtils.isNotEmpty(studyConsent.getConsentPdf())) {
					AppUtil.deleteOldConsentPDF(studyConsent.getConsentPdf());
				}

				studyConsent.setModifiedOn(AppUtil.getCurrentDateTime())
						.setConsentPdf(AppUtil.saveSignedConsentDocument(consent, userId, studyId));
				studyDao.saveOrUpdateStudyConsent(studyConsent, AppConstants.DB_UPDATE);
			}
			errorResponse.setError(new ErrorBean().setCode(ErrorCode.EC_200.code())
					.setMessage(AppUtil.getAppProperties().get(AppConstants.STUDY_STORE_CONSENT)));
		} catch (Exception e) {
			LOGGER.error("ERROR: StudyServiceImpl - storeSignedConsent()", e);
		}
		LOGGER.info("INFO: StudyServiceImpl - storeSignedConsent() :: ends");
		return errorResponse;
	}

	@Override
	public ErrorResponse leaveStudy(String userId, String studyId) throws CustomException {
		LOGGER.info("INFO: StudyServiceImpl - leaveStudy() :: starts");
		ErrorResponse errorResponse = new ErrorResponse();
		UserDto user = null;
		UserStudiesDto userStudies = null;
		StudyConsentDto studyConsent = null;
		StudiesDto studyDto = null;
		List<AdminUsersDto> adminUsers = new ArrayList<>();
		List<String> toAdminList = new ArrayList<>();
		List<GroupUsersInfoDto> groupInfoList = new ArrayList<>();
		try {
			user = userDao.fetchUserDetails(userId, AppConstants.FIND_BY_TYPE_USERID);
			studyConsent = studyDao.fetchStudyConsentDetails(userId, studyId, AppConstants.FIND_BY_TYPE_USERID_STUDYID);
			userStudies = studyDao.fetchUserStudiesDetails(userId, studyId, AppConstants.FIND_BY_TYPE_USERID_STUDYID);
			studyDto = studyDao.fetchStudyDetailsByStudyId(studyId);
			     
			if (user != null && studyConsent != null && userStudies != null && studyDto != null) {
				groupInfoList = studyDao.fetchGroupUsersInfoByType(Integer.parseInt(userId) , AppConstants.FIND_BY_TYPE_USERID);
				if(null != groupInfoList && !groupInfoList.isEmpty() ) {
					groupInfoList.forEach(groupInfo->{
						groupInfo.setEnabled(false);
					});
				studyDao.saveOrUpdateGroupUsersList(groupInfoList, AppConstants.DB_UPDATE);
					
				}
				// Participant leave study
				Map<String, String> mailContentMap = MailContent.participantLeavesStudy(user.getFirstName(),
						studyDto.getStudyName(), user.getLanguage());
				Mail.sendEmail(user.getEmail(), mailContentMap.get(AppConstants.MAIL_SUBJECT),
						mailContentMap.get(AppConstants.MAIL_BODY));

				// Participant leaves study [Mail Admin]
				adminUsers = studyDao.fetchAllAdminUsers();
				if (!adminUsers.isEmpty()) {
					toAdminList = adminUsers.stream().map(AdminUsersDto::getEmail).collect(Collectors.toList());

					Map<String, String> adminMailContentMap = MailContent
							.notifyLeaveStudyToAdmin(userStudies.getEnrollmentId(), studyDto.getStudyName());
					Mail.sendEmailToMany(adminMailContentMap.get(AppConstants.MAIL_SUBJECT),
							adminMailContentMap.get(AppConstants.MAIL_BODY), toAdminList, null, null);
				}

				// Reset user reward level on leave study
				user.setRewardLevel(0).setPointsEarned(0L);
				userDao.saveOrUpdateUserDetails(user, AppConstants.DB_UPDATE);

				// Deactivate enrollmentId on leave study
				userStudies.setActive(false);
				studyDao.saveOrUpdateUserStudies(userStudies, AppConstants.DB_UPDATE);

				// Deactivate consentDocument on leave study
				studyConsent.setActive(false);
				studyDao.saveOrUpdateStudyConsent(studyConsent, AppConstants.DB_UPDATE);

				errorResponse.setError(
						new ErrorBean().setCode(ErrorCode.EC_200.code()).setMessage(ErrorCode.EC_200.errorMessage()));
			} else {
				errorResponse.setError(
						new ErrorBean().setCode(ErrorCode.EC_404.code()).setMessage(ErrorCode.EC_404.errorMessage()));
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: StudyServiceImpl - leaveStudy()", e);
		}
		LOGGER.info("INFO: StudyServiceImpl - leaveStudy() :: ends");
		return errorResponse;
	}

	@Override
	public GroupLocationResponse getGroupLocation(String userId) {
		LOGGER.info("INFO: StudyServiceImpl - getGroupLocation() :: starts");
		GroupLocationResponse groupLocationResponse = new GroupLocationResponse();
		UserDto user = null;
		StudyConsentDto studyConsent = null;
		UserStudiesDto userStudies = null;
		EnrollmentTokensDto enrollmentToken = null;
		GroupIdentifierDto groupIdentifier = null;
		try {
			user = userDao.fetchUserDetails(userId, AppConstants.FIND_BY_TYPE_USERID);
			if (user == null) {
				return new GroupLocationResponse().setError(
						new ErrorBean().setCode(ErrorCode.EC_61.code()).setMessage(ErrorCode.EC_61.errorMessage()));
			}

			studyConsent = studyDao.fetchStudyConsentDetails(userId, AppConstants.DEFAULT_STUDY_ID,
					AppConstants.FIND_BY_TYPE_USERID_STUDYID);
			if (studyConsent == null) {
				return new GroupLocationResponse().setError(
						new ErrorBean().setCode(ErrorCode.EC_93.code()).setMessage(ErrorCode.EC_93.errorMessage()));
			}

			userStudies = studyDao.fetchUserStudiesDetails(userId, AppConstants.DEFAULT_STUDY_ID,
					AppConstants.FIND_BY_TYPE_USERID_STUDYID);
			if (userStudies == null) {
				return new GroupLocationResponse().setError(
						new ErrorBean().setCode(ErrorCode.EC_93.code()).setMessage(ErrorCode.EC_93.errorMessage()));
			}

			enrollmentToken = studyDao.fetchEnrollmentTokenDetails(userStudies.getEnrollmentId(),
					AppConstants.FIND_BY_TYPE_ENROLLMENTID);
			if (enrollmentToken == null) {
				return new GroupLocationResponse().setError(
						new ErrorBean().setCode(ErrorCode.EC_93.code()).setMessage(ErrorCode.EC_93.errorMessage()));
			}

			groupIdentifier = studyDao.fetchGroupIdentifierDetails(enrollmentToken.getGroupId(),
					AppConstants.FIND_BY_TYPE_GROUP_ID_NAME);
			if (groupIdentifier == null) {
				return new GroupLocationResponse().setError(
						new ErrorBean().setCode(ErrorCode.EC_110.code()).setMessage(ErrorCode.EC_110.errorMessage()));
			}

			groupLocationResponse
					.setError(new ErrorBean().setCode(ErrorCode.EC_200.code())
							.setMessage(AppUtil.getAppProperties().get(AppConstants.FETCH_GROUP_LOCATION_SUCCESS)))
					.setLatitude(StringUtils.isEmpty(groupIdentifier.getLatitude()) ? "0.0"
							: groupIdentifier.getLatitude())
					.setLongitude(StringUtils.isEmpty(groupIdentifier.getLongitude()) ? "0.0"
							: groupIdentifier.getLongitude());
		} catch (Exception e) {
			LOGGER.error("ERROR: StudyServiceImpl - getGroupLocation()", e);
		}
		LOGGER.info("INFO: StudyServiceImpl - getGroupLocation() :: ends");
		return groupLocationResponse;
	}
	
	
	@Override
	public boolean validateEnrollmentId(String enrollmentId) throws CustomException {
		LOGGER.info("INFO: StudyServiceImpl - validateEnrollmentId() :: starts");
		boolean isValidEnrollmentId = false;
		try {
			isValidEnrollmentId = studyDao.validateEnrollmentId(enrollmentId);
		} catch (Exception e) {
			LOGGER.error("ERROR: StudyServiceImpl - validateEnrollmentId()", e);
		}
		LOGGER.info("INFO: StudyServiceImpl - validateEnrollmentId() :: ends");
		return isValidEnrollmentId;
	}
	
	@Override
	public ErrorResponse storeResponseActivitiesTemp(String userId, String enrollmentId, String jsonFile, String activityType, Integer conditionId, Integer activityId) throws CustomException {
		LOGGER.info("INFO: StudyServiceImpl - storeResponseActivitiesTemp() :: starts");
		ErrorResponse errorResponse = new ErrorResponse();
		ResponseActivityTempDto responseActivityTempDto = null;
		try {
             if(StringUtils.isNotEmpty(userId) && StringUtils.isNotEmpty(enrollmentId) 
    		 &&  StringUtils.isNotEmpty(jsonFile)  && StringUtils.isNotEmpty(activityType) && conditionId != 0  &&  activityId != 0) {
	      responseActivityTempDto = new ResponseActivityTempDto();
	       String jsonResponseDocName = AppUtil.saveResponsesActivityDocument(jsonFile, userId, enrollmentId);
	          if(StringUtils.isNotEmpty(jsonResponseDocName) && 0 != conditionId && activityId != 0 && StringUtils.isNotEmpty(enrollmentId)) {
		             responseActivityTempDto.setUserId(Integer.parseInt(userId)).setEnrollmentID(enrollmentId)
			             .setCreatedDate(AppUtil.getCurrentDateTime()).setJsonFile(jsonResponseDocName);
		             studyDao.saveOrUpdateResponseActivityTemp(responseActivityTempDto, AppConstants.DB_SAVE);
		             errorResponse =   this.storeResponsesBasedOnType(conditionId, activityId ,
		            		                AppUtil.getFilePathOFResponseDoucument(jsonResponseDocName), activityType, enrollmentId);
	          }else {
	        	  errorResponse.setError(new ErrorBean().setCode(ErrorCode.EC_112.code())
	        			  .setMessage(ErrorCode.EC_112.errorMessage()));
	          }
         }
		
		} catch (Exception e) {
			LOGGER.error("ERROR: StudyServiceImpl - storeResponseActivitiesTemp()", e);
		}
		LOGGER.info("INFO: StudyServiceImpl - storeResponseActivitiesTemp() :: ends");
		return errorResponse;
	}

	@Override
	public ErrorResponse storeResponsesBasedOnType(Integer conditionId, Integer activityId, String fileName, String type, String enrollmentId)
			throws CustomException, JsonGenerationException, JsonMappingException, IOException {
		ErrorResponse errorResponse = new ErrorResponse();
		ObjectMapper mapper = new ObjectMapper(); 
		List<ResponsesTasksActivitiesDto> responsesTasksActivitiesDtos = new ArrayList<>();
		List<ResponsesSurveysActivitiesDto> responsesSurveysActivitiesDtos = new ArrayList<>();
		LOGGER.info("INFO: StudyServiceImpl - storeResponsesBasedOnType() :: starts");
		try {
			if(type.equalsIgnoreCase(AppConstants.ACTIVITY_TYPE_AUT_WEB) 
					|| type.equalsIgnoreCase(AppConstants.ACTIVITY_TYPE_STROOP_WEB) 
					|| type.equalsIgnoreCase(AppConstants.ACTIVITY_TYPE_ARITHMETIC_WEB)
					|| type.equalsIgnoreCase(AppConstants.ACTIVITY_TYPE_CRAT_WEB)) {
				
				ResponsesTaskResults tasksResults = mapper.readValue
						                  (new FileInputStream(fileName), ResponsesTaskResults.class);
				
				if(null != tasksResults &&  null != tasksResults.getData()
						&& null != tasksResults.getData().getResults() && !tasksResults.getData().getResults().isEmpty()
						&& StringUtils.isNotEmpty(tasksResults.getData().getTotalTime())){
					String totalTime = (null != tasksResults.getData().getTotalTime() ) ? tasksResults.getData().getTotalTime():"";
					for(ResponseTaskResultList taskResults :  tasksResults.getData().getResults()) {
						for(ResponseTaskListValues  resultValues : taskResults.getValues()) {
							ResponsesTasksActivitiesDto responsesTasksActivitiesDto = new ResponsesTasksActivitiesDto().
                                    setActivityId(activityId).setConditionId(conditionId)
                                    .setStartTime(StringUtils.isNotEmpty(taskResults.getStartTime()) ? taskResults.getStartTime():"")
                                    .setEndTime(StringUtils.isNotEmpty(taskResults.getEndTime()) ? taskResults.getEndTime():"")
                                    .setResultType(StringUtils.isNotEmpty(taskResults.getResultType()) ? taskResults.getResultType() : "")
                                    .setTotalTime(totalTime)
							       .setWord(StringUtils.isNotEmpty(resultValues.getWord()) ? resultValues.getWord(): "")
						           .setQuestionId(StringUtils.isNotEmpty(resultValues.getQuestionID()) ?  resultValues.getQuestionID() : "")
						           .setCreatedOn(AppUtil.getCurrentDateTime())
						           .setResponseVal(StringUtils.isNotEmpty(resultValues.getResponse() )  ? resultValues.getResponse() : "");
							
							responsesTasksActivitiesDtos.add(responsesTasksActivitiesDto);
						}
					}
					if(null != responsesTasksActivitiesDtos && !responsesTasksActivitiesDtos.isEmpty()) {
					  for(ResponsesTasksActivitiesDto  responsesDto : responsesTasksActivitiesDtos) {
							responsesDto.setStartTime(AppUtil.getFormattedDateTimeESTFormat(
									responsesDto.getStartTime(), AppConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_FORMAT, AppConstants.SDF_DATE_TIME_FORMAT));
							responsesDto.setEndTime(AppUtil.getFormattedDateTimeESTFormat(
									responsesDto.getEndTime(), AppConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_FORMAT, AppConstants.SDF_DATE_TIME_FORMAT));
							responsesDto.setEnrollmentId(StringUtils.isNotBlank(enrollmentId) ? enrollmentId : "");
					  }
						
					 studyDao.saveOrUpdateResponsesOfTaskActivitivies(responsesTasksActivitiesDtos, AppConstants.DB_SAVE);

					      errorResponse.setError(new ErrorBean().setCode(ErrorCode.EC_200.code())
									.setMessage(AppUtil.getAppProperties().get(AppConstants.RESPONSE_STORE_ACTIVITY)));
					}else {
						 errorResponse.setError(new ErrorBean().setCode(ErrorCode.EC_406.code())
								 .setMessage(ErrorCode.EC_406.errorMessage()));
					}
					
				}else {
					 errorResponse.setError(new ErrorBean().setCode(ErrorCode.EC_406.code())
							 .setMessage(ErrorCode.EC_406.errorMessage()));
					
				}
			}else if(type.equalsIgnoreCase(AppConstants.ACTIVITY_TYPE_SURVEY_WEB)){
				ResponsesSurveyResults surveyResults = mapper.readValue
		                  (new FileInputStream(fileName), ResponsesSurveyResults.class);
				String date = AppUtil.getCurrentDateTime();
				
				if(null != surveyResults && null != surveyResults.getData() && 
						null != surveyResults.getData().getResults() && !surveyResults.getData().getResults().isEmpty() ) {
					for(ResponseSurveysResultList surveyResultList : surveyResults.getData().getResults()) {
						ResponsesSurveysActivitiesDto responsesSurveysActivitiesDto = new ResponsesSurveysActivitiesDto()
								                                          .setActivityId(activityId).setConditionId(conditionId).setCreatedOn(date)
								                                          .setResultType(StringUtils.isNotBlank(surveyResults.getResultType())? surveyResults.getResultType():"" )
								                                          .setStartTime(StringUtils.isNotBlank(surveyResults.getData().getStartTime())? surveyResults.getData().getStartTime(): "")
								                                          .setEndTime(StringUtils.isNotBlank(surveyResults.getData().getEndTime())? surveyResults.getData().getEndTime(): "")
								                                          .setTotalTime(StringUtils.isNotBlank(surveyResults.getData().getTotalTime())? surveyResults.getData().getTotalTime(): "")
								                                          .setQuestionId(StringUtils.isNotBlank(surveyResultList.getKey())? surveyResultList.getKey() : "")
								                                          .setQuestionSkip(surveyResultList.getSkipped())
								                                          .setResponseStartTime(StringUtils.isNotBlank(surveyResultList.getStartTime()) ? surveyResultList.getStartTime() : "")
								                                          .setResponseEndTime(StringUtils.isNotBlank(surveyResultList.getEndTime()) ? surveyResultList.getEndTime() : "")
								                                          .setResponseQuestionType(StringUtils.isNotBlank(surveyResultList.getResultType())? surveyResultList.getResultType(): "")
								                                       .setResponseValue(this.getResponseSurveyValueByList(surveyResultList.getValue() ,surveyResultList.getResultType()))
								                                          ;
						
						responsesSurveysActivitiesDtos.add(responsesSurveysActivitiesDto);
					}
					
					
					if(null != responsesSurveysActivitiesDtos && !responsesSurveysActivitiesDtos.isEmpty()) {
						for(ResponsesSurveysActivitiesDto  responsesDto : responsesSurveysActivitiesDtos) {
							if(responsesDto.getResponseQuestionType().equals(AppConstants.RESPONSE_TYPE_VALUE_PICKER)) {
								List<QuestionChoiceDto> questionChoiceDtoList = activityDao.fetchQuestionsChoiceDetailsList(
										String.valueOf(responsesDto.getQuestionId()), AppConstants.FIND_BY_TYPE_QUESTION_ID);
								
								if(null != questionChoiceDtoList && !questionChoiceDtoList.isEmpty()) {
									System.out.println(questionChoiceDtoList.size());
									int valueCount =0;
									for(QuestionChoiceDto question : questionChoiceDtoList) {
										if(responsesDto.getResponseValue().equals(String.valueOf(valueCount)) && 
												StringUtils.isNotBlank(question.getDescription())) {
											responsesDto.setResponseValue(question.getDescription());
										}
										valueCount++;
									}
									
								}
							}else if(responsesDto.getResponseQuestionType().equals(AppConstants.RESPONSE_TYPE_TIME_INTERVAL)) {
								if(StringUtils.isNotBlank(responsesDto.getResponseValue())) {
									String firstValue = responsesDto.getResponseValue().replace(".", "-");
									String[] test = firstValue.split("-");
									if(StringUtils.isNotBlank(test[0]) && StringUtils.isNotBlank(test[1])) {
										if(test[1].trim().equals("0") || test[1].trim().equals("00")) {
											responsesDto.setResponseValue(test[0]+":"+"00");
										}else {
											String addCount ="";
											if(test[1].length() >= 2) {
												addCount = "0."+test[1].substring(0, 2);
											}else {
												 addCount = "0."+test[1]+"0";
											}
											if(StringUtils.isNotBlank(addCount)) {
												double fullCheck =   Double.parseDouble(addCount.trim()) * 60;
												String firstValueFull  = String.valueOf(fullCheck).replace(".", "-").replace("L", "");
												String[] responeTimeInt = firstValueFull.split("-");
												responsesDto.setResponseValue(test[0]+":"+responeTimeInt[0]);
											}else {
												responsesDto.setResponseValue(test[0]+":"+"00");
											}
											
										}
									
									}
									
								}
								
							}else if(responsesDto.getResponseQuestionType().equals(AppConstants.RESPONSE_TYPE_BOOLEAN)) {
								if(StringUtils.isNotBlank(responsesDto.getResponseValue())) {
									if(responsesDto.getResponseValue().equalsIgnoreCase("false")) {
										responsesDto.setResponseValue("no");
									}else {
										responsesDto.setResponseValue("yes");
									}
								}
							}
								responsesDto.setStartTime(AppUtil.getFormattedDateTimeESTFormat(
										responsesDto.getStartTime(), AppConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_FORMAT, AppConstants.SDF_DATE_TIME_FORMAT));
								responsesDto.setResponseStartTime(AppUtil.getFormattedDateTimeESTFormat(
										responsesDto.getResponseStartTime(), AppConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_FORMAT, AppConstants.SDF_DATE_TIME_FORMAT));
								responsesDto.setEndTime(AppUtil.getFormattedDateTimeESTFormat(
								    responsesDto.getEndTime(), AppConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_FORMAT, AppConstants.SDF_DATE_TIME_FORMAT));
								responsesDto.setResponseEndTime(AppUtil.getFormattedDateTimeESTFormat(
										responsesDto.getResponseEndTime(), AppConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_FORMAT, AppConstants.SDF_DATE_TIME_FORMAT));
								responsesDto.setEnrollmentId(StringUtils.isNotEmpty(enrollmentId)? enrollmentId : "");
						}
						 studyDao.saveOrUpdateResponsesOfSurveysActivitivies(responsesSurveysActivitiesDtos, AppConstants.DB_SAVE);

						      errorResponse.setError(new ErrorBean().setCode(ErrorCode.EC_200.code())
										.setMessage(AppUtil.getAppProperties().get(AppConstants.RESPONSE_STORE_ACTIVITY)));
						}else {
							 errorResponse.setError(new ErrorBean().setCode(ErrorCode.EC_406.code())
									 .setMessage(ErrorCode.EC_406.errorMessage()));
						}
					 
					
				}else {
					errorResponse.setError(new ErrorBean().setCode(ErrorCode.EC_406.code())
							 .setMessage(ErrorCode.EC_406.errorMessage()));
					
				}
			
				  
				
			}
      
			
		} catch (Exception e) {
			LOGGER.error("ERROR: StudyServiceImpl - storeResponsesBasedOnType()", e);
		}
		LOGGER.info("INFO: StudyServiceImpl - storeResponsesBasedOnType() :: ends");
		return errorResponse;
	}
	
	
	public String getResponseSurveyValueByList(Object surveyValue, String type) {
		String responseValue = "";
		LOGGER.info("INFO: StudyServiceImpl - getResponseSurveyValueByList ::: starts");
		if(null != surveyValue && StringUtils.isNotEmpty(type)) {
			switch (type) {
			case AppConstants.RESPONSE_TYPE_INTSRUCTION:
				responseValue = surveyValue.toString();
				break;
			case AppConstants.RESPONSE_TYPE_BOOLEAN:
				responseValue = surveyValue.toString();
				break;
			case AppConstants.RESPONSE_TYPE_DATE:
				responseValue = surveyValue.toString();
				break;
			case AppConstants.RESPONSE_TYPE_EMAIL:
				responseValue =surveyValue.toString();
				break;
			case AppConstants.RESPONSE_TYPE_NUMERIC:
				responseValue = surveyValue.toString();
				break;
			case AppConstants.RESPONSE_TYPE_TEXT:
				responseValue = surveyValue.toString();
				break;
			case AppConstants.RESPONSE_TYPE_TEXT_CHOICE:
				@SuppressWarnings("unchecked") List<String> list = (List<String>) surveyValue;
				responseValue = list.stream().collect(Collectors.joining(","));
				
				break;
			case AppConstants.RESPONSE_TYPE_TIME_INTERVAL:
				responseValue = surveyValue.toString();
				break;
			case AppConstants.RESPONSE_TYPE_TIME_OF_DAY:
				responseValue = surveyValue.toString();
				break;
			case AppConstants.RESPONSE_TYPE_VALUE_PICKER:
				responseValue = surveyValue.toString();
				break;
			case AppConstants.RESPONSE_TYPE_SENSOR_ID:
				responseValue = surveyValue.toString();
				break;
			case AppConstants.RESPONSE_TYPE_ENROLLMENT_ID:
				responseValue = surveyValue.toString();
				break;
			case AppConstants.RESPONSE_TYPE_ACTIVITY_ID:
				responseValue = surveyValue.toString();
				break;
			default:
				responseValue = surveyValue.toString();
				break;
		
			}
		

		}
		LOGGER.info("INFO: StudyServiceImpl - getResponseSurveyValueByList ::: ends");
		return responseValue;
		
	}
	
	@Override
	public GroupLocationResponse getBuildingLocation(String userId) {
		LOGGER.info("INFO: StudyServiceImpl - getGroupLocation() :: starts");
		GroupLocationResponse groupLocationResponse = new GroupLocationResponse();
		UserDto user = null;
		StudyConsentDto studyConsent = null;
		UserStudiesDto userStudies = null;
		EnrollmentTokensDto enrollmentToken = null;
		try {
			user = userDao.fetchUserDetails(userId, AppConstants.FIND_BY_TYPE_USERID);
			if (user == null) {
				return new GroupLocationResponse().setError(
						new ErrorBean().setCode(ErrorCode.EC_61.code()).setMessage(ErrorCode.EC_61.errorMessage()));
			}

			studyConsent = studyDao.fetchStudyConsentDetails(userId, AppConstants.DEFAULT_STUDY_ID,
					AppConstants.FIND_BY_TYPE_USERID_STUDYID);
			if (studyConsent == null) {
				return new GroupLocationResponse().setError(
						new ErrorBean().setCode(ErrorCode.EC_93.code()).setMessage(ErrorCode.EC_93.errorMessage()));
			}

			userStudies = studyDao.fetchUserStudiesDetails(userId, AppConstants.DEFAULT_STUDY_ID,
					AppConstants.FIND_BY_TYPE_USERID_STUDYID);
			if (userStudies == null) {
				return new GroupLocationResponse().setError(
						new ErrorBean().setCode(ErrorCode.EC_93.code()).setMessage(ErrorCode.EC_93.errorMessage()));
			}

			enrollmentToken = studyDao.fetchEnrollmentTokenDetails(userStudies.getEnrollmentId(),
					AppConstants.FIND_BY_TYPE_ENROLLMENTID);
			if (enrollmentToken == null) {
				return new GroupLocationResponse().setError(
						new ErrorBean().setCode(ErrorCode.EC_93.code()).setMessage(ErrorCode.EC_93.errorMessage()));
			}

			if(null != user) {
				groupLocationResponse
				.setError(new ErrorBean().setCode(ErrorCode.EC_200.code())
						.setMessage(AppUtil.getAppProperties().get(AppConstants.FETCH_BUILDING_LOCATION_SUCCESS)))
				.setLatitude(StringUtils.isEmpty(user.getLatitude()) ? "0.0"
						: user.getLatitude())
				.setLongitude(StringUtils.isEmpty(user.getLongitude()) ? "0.0"
						: user.getLongitude());
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: StudyServiceImpl - getGroupLocation()", e);
		}
		LOGGER.info("INFO: StudyServiceImpl - getGroupLocation() :: ends");
		return groupLocationResponse;
	}
}
