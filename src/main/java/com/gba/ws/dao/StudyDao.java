package com.gba.ws.dao;

import java.util.List;
import java.util.Map;

import com.gba.ws.exception.CustomException;
import com.gba.ws.model.ActivityGroupDto;
import com.gba.ws.model.AdminUsersDto;
import com.gba.ws.model.GroupIdentifierDto;
import com.gba.ws.model.GroupUsersInfoDto;
import com.gba.ws.model.EnrollmentTokensDto;
import com.gba.ws.model.ResponseActivityTempDto;
import com.gba.ws.model.ResponsesSurveysActivitiesDto;
import com.gba.ws.model.ResponsesTasksActivitiesDto;
import com.gba.ws.model.StudiesDto;
import com.gba.ws.model.StudyConsentDto;
import com.gba.ws.model.UserStudiesDto;
import com.gba.ws.util.ResponsesTaskResults;

/**
 * 
 * @author Mohan
 * @createdOn Jan 8, 2018 6:36:37 PM
 */
public interface StudyDao {

	/**
	 * Get the {@link EnrollmentTokensDto} for the provided criteria
	 * 
	 * @author Mohan
	 * @param findBy
	 *            the condition value
	 * @param findByType
	 *            the condition type
	 * @return the {@link EnrollmentTokensDto} details
	 * @throws CustomException
	 */
	public EnrollmentTokensDto fetchEnrollmentTokenDetails(String findBy, String findByType) throws CustomException;

	/**
	 * Check the study is available for the study identifier
	 * 
	 * @author Mohan
	 * @param studyId
	 *            the study identifier
	 * @return true or false
	 * @throws CustomException
	 */
	public boolean validateStudyId(String studyId) throws CustomException;

	/**
	 * Save or Update {@link UserStudiesDto} details for the type provided
	 * 
	 * @author Mohan
	 * @param userStudies
	 *            the {@link UserStudiesDto} details
	 * @param type
	 *            the type of action
	 * @return the {@link UserStudiesDto} details
	 * @throws CustomException
	 */
	public UserStudiesDto saveOrUpdateUserStudies(UserStudiesDto userStudies, String type) throws CustomException;

	/**
	 * Save or Update {@link EnrollmentTokensDto} details for the provided type
	 * 
	 * @author Mohan
	 * @param enrollmentToken
	 *            the {@link EnrollmentTokensDto} details
	 * @param type
	 *            the type of action
	 * @return the {@link EnrollmentTokensDto} details
	 * @throws CustomException
	 */
	public EnrollmentTokensDto saveOrUpdateEnrollmentTokens(EnrollmentTokensDto enrollmentToken, String type)
			throws CustomException;

	/**
	 * Get the {@link UserStudiesDto} details for the provided criteria
	 * 
	 * @author Mohan
	 * @param findBy1
	 *            the condition1 value
	 * @param findBy2
	 *            the conidtion2 value
	 * @param findByType
	 *            the condition type
	 * @return the {@link UserStudiesDto} details
	 * @throws CustomException
	 */
	public UserStudiesDto fetchUserStudiesDetails(String findBy1, String findBy2, String findByType)
			throws CustomException;

	/**
	 * Activate all non enrolled tokens
	 * 
	 * @author Mohan
	 * @throws CustomException
	 */
	public void activateNonEnrolledTokens() throws CustomException;

	/**
	 * Save or Update {@link StudyConsentDto} details for the type provided
	 * 
	 * @author Mohan
	 * @param studyConsent
	 *            the {@link StudyConsentDto} details
	 * @param type
	 *            the type of the action
	 * @return the {@link StudyConsentDto} details
	 * @throws CustomException
	 */
	public StudyConsentDto saveOrUpdateStudyConsent(StudyConsentDto studyConsent, String type) throws CustomException;

	/**
	 * Get the {@link StudyConsentDto} details for the provided criteria
	 * 
	 * @author Mohan
	 * @param findBy1
	 *            the condition1 value
	 * @param findBy2
	 *            the condition2 value
	 * @param findByType
	 *            the conidition type
	 * @return the {@link StudyConsentDto} details
	 * @throws CustomException
	 */
	public StudyConsentDto fetchStudyConsentDetails(String findBy1, String findBy2, String findByType)
			throws CustomException;

	/**
	 * Get the {@link AdminUsersDto} details
	 * 
	 * @author Mohan
	 * @return the {@link AdminUsersDto} details list
	 * @throws CustomException
	 */
	public List<AdminUsersDto> fetchAllAdminUsers() throws CustomException;

	/**
	 * Get the {@link StudiesDto} details for the provided study identifier
	 * 
	 * @author Mohan
	 * @param studyId
	 *            the study identifier
	 * @return the {@link StudiesDto} details
	 * @throws CustomException
	 */
	public StudiesDto fetchStudyDetailsByStudyId(String studyId) throws CustomException;

	/**
	 * Get the {@link ActivityGroupDto} details for the provided criteria
	 * 
	 * @author Mohan
	 * @param findBy
	 *            the condition value
	 * @param findByType
	 *            the condition type
	 * @return the {@link ActivityGroupDto} details list
	 * @throws CustomException
	 */
	public List<ActivityGroupDto> fetchActivityGroupsList(String findBy, String findByType)
			throws CustomException;
	public List<ActivityGroupDto> fetchActivityGroupsListByConditionId(int conditionId)
			throws CustomException;

	/**
	 * Get the {@link UserStudiesDto} details for the provided criteria
	 * 
	 * @author Mohan
	 * @param findBy1
	 *            the condition1 value
	 * @param findBy2
	 *            the condition2 value
	 * @param findByType
	 *            the condition type
	 * @return the {@link UserStudiesDto} details list
	 */
	public List<UserStudiesDto> fetchUserStudiesDetailsList(List<Object> findBy1, String findBy2, String findByType);

	/**
	 * Get the {@link ActivityGroupDto} details list
	 * 
	 * @author Mohan
	 * @param findBy
	 *            the condition values list
	 * @param findByType
	 *            the condition type
	 * @return the {@link ActivityGroupDto} details list
	 */
	public List<ActivityGroupDto> fetchAllActivityGroupList(List<Object> findBy, String findByType);

	/**
	 * Get all the active {@link EnrollmentTokensDto} details list
	 * 
	 * @author Mohan
	 * @return the {@link EnrollmentTokensDto} details list
	 */
	public List<EnrollmentTokensDto> fetchAllEnrollmentDetailsList();

	/**
	 * Get the group details for the provided group identifier
	 * 
	 * @author Mohan
	 * @param findBy
	 *            the condition value
	 * @param findByType
	 *            the condition type
	 * @return the {@link GroupIdentifierDto} details
	 */
	public GroupIdentifierDto fetchGroupIdentifierDetails(String findBy, String findByType);
	public GroupIdentifierDto fetchGroupIdentifierDetailsById(int id);

	
	/**
	 * Check the enrollment is available for the enrollment identifier
	 * 
	 * @author kavya
	 * @param enrollmentId
	 *            the enrollment identifier
	 * @return true or false
	 * @throws CustomException
	 */

	public boolean validateEnrollmentId(String enrollmentId) throws CustomException;

	/**
	 * Get the {@link ResponseActivityTempDto} details for the provided responses identifier
	 * 
	 * @author Kavya
	 * @param userId
	 * @param enrollmentId
	 *            the responses identifier
	 * @return the {@link ResponseActivityTempDto} details
	 * @throws CustomException
	 */
	public ResponseActivityTempDto fetchResponseActivityDetails(String findBy1, String findBy2, String findByType)
			throws CustomException;

	/**
	 * Save or Update {@link ResponseActivityTempDto} details for the type provided
	 * 
	 * @author Kavya
	 * @param responseActivityTemp
	 *            the {@link ResponseActivityTempDto} details
	 * @param type
	 *            the type of the action
	 * @return the {@link ResponseActivityTempDto} details
	 * @throws CustomException
	 */
	public ResponseActivityTempDto saveOrUpdateResponseActivityTemp(ResponseActivityTempDto activityTempDto, String type)
			throws CustomException;

	public GroupUsersInfoDto saveOrUpdateGroupUsers(GroupUsersInfoDto groupUsersInfoDto, String type) throws CustomException;

	public List<GroupUsersInfoDto> fetchGroupUsersInfoByType(Integer findBy, String findByType);

	public boolean saveOrUpdateGroupUsersList(List<GroupUsersInfoDto> groupUsersInfoDto, String type) throws CustomException;
	
	

	public List<GroupUsersInfoDto> fetchAllGroupUsersInfoListbyId(Integer findBy, String findByType);
	
	
	public Map<Integer, List<Integer>> getGroupUsersIdListFromMappingDetails(List<Integer> groupIds) throws CustomException;
	
	public GroupIdentifierDto saveOrUpdateGroups(GroupIdentifierDto groupUsersInfoDto, String type) throws CustomException;
	
	public boolean saveOrUpdateResponsesOfTaskActivitivies(List<ResponsesTasksActivitiesDto> responsestaskResults , String type) throws CustomException;
	
	
	public boolean saveOrUpdateResponsesOfSurveysActivitivies(List<ResponsesSurveysActivitiesDto> responsesSurveyResults , String type) throws CustomException;

	public List<GroupUsersInfoDto> fetchAllGroupUsersInfoDetails();

	public List<ActivityGroupDto> fetchActivityGroupsListByUserId(Integer findBy, Integer groupId, String findByType)
			throws CustomException;
	

}
