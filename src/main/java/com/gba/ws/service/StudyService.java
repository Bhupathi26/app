package com.gba.ws.service;

import com.gba.ws.bean.GroupLocationResponse;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

import com.gba.ws.bean.ErrorResponse;
import com.gba.ws.bean.SignedConsentResponse;
import com.gba.ws.exception.CustomException;
import com.gba.ws.model.StudiesDto;

/**
 * 
 * @author Mohan
 * @createdOn Jan 8, 2018 6:59:48 PM
 */
public interface StudyService {

	/**
	 * Check the {@link StudiesDto} is available for the provided study identifier
	 * 
	 * @author Mohan
	 * @param studyId
	 *            the study identifier
	 * @return true or false
	 * @throws CustomException
	 */
	public boolean validateStudyId(String studyId) throws CustomException;

	/**
	 * Verify elibility for the provided user, study identifier and token
	 * 
	 * @author Mohan
	 * @param userId
	 *            the user identifier
	 * @param studyId
	 *            the study identifier
	 * @param token
	 *            the enrollment identifier
	 * @return the {@link ErrorResponse} details
	 * @throws CustomException
	 */
	public ErrorResponse verifiyEligibility(String userId, String studyId, String token) throws CustomException;

	/**
	 * Enroll the user to Study details for the provided user, study identifier and
	 * token
	 * 
	 * @author Mohan
	 * @param userId
	 *            the user identifier
	 * @param studyId
	 *            the study identifier
	 * @param token
	 *            the enrollment identifier
	 * @return the {@link ErrorResponse} details
	 * @throws CustomException
	 */
	public ErrorResponse enrollInStudy(String userId, String studyId, String token) throws CustomException;

	/**
	 * Get the signed consent document details for the provided user and study
	 * identifier
	 * 
	 * @author Mohan
	 * @param userId
	 *            the user identifier
	 * @param studyId
	 *            the study identifier
	 * @return the {@link SignedConsentResponse} details
	 * @throws CustomException
	 */
	public SignedConsentResponse getSignedConsent(String userId, String studyId) throws CustomException;

	/**
	 * Save consent document for the provided user, study identifier and the consent
	 * document
	 * 
	 * @author Mohan
	 * @param userId
	 *            the user identifier
	 * @param studyId
	 *            the study identifier
	 * @param consent
	 *            the consent document base64 value
	 * @return the {@link ErrorResponse} details
	 * @throws CustomException
	 */
	public ErrorResponse storeSignedConsent(String userId, String studyId, String consent) throws CustomException;

	/**
	 * Leave study for the provided user and study identifier
	 * 
	 * @author Mohan
	 * @param userId
	 *            the user identifier
	 * @param studyId
	 *            the study identifier
	 * @return the {@link ErrorResponse} details
	 * @throws CustomException
	 */
	public ErrorResponse leaveStudy(String userId, String studyId) throws CustomException;

	/**
	 * Get the group location details for the provided user identifier
	 * 
	 * @author Mohan
	 * @param userId
	 *            the user identifier
	 * @return the {@link GroupLocationResponse} details
	 */
	public GroupLocationResponse getGroupLocation(String userId);

	
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
	 * Save responses document for the provided user, enrollmentId identifier and the responses
	 * document
	 * 
	 * @author Kavya
	 * @param userId
	 *            the user identifier
	 * @param enrollmentId
	 *            the study identifier
	 * @param responseFile
	 *            the responses document base64 value
	 * @return the {@link ErrorResponse} details
	 * @throws CustomException
	 */
	public ErrorResponse storeResponseActivitiesTemp(String userId, String enrollmentId, String jsonFile, String activityType,
			Integer conditionId, Integer activityId) throws CustomException;
	
	public ErrorResponse storeResponsesBasedOnType(Integer conditionId, Integer activityId , String fileName, String type, String enrollmentId) 
			throws CustomException, JsonGenerationException , JsonMappingException, IOException;
	
	/**
	 * Get the building location details for the provided user identifier
	 * 
	 * @author Mohan
	 * @param userId
	 *            the user identifier
	 * @return the {@link GroupLocationResponse} details
	 */
	public GroupLocationResponse getBuildingLocation(String userId);

}
