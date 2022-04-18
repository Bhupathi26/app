package com.gba.ws.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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
import com.gba.ws.util.AppConstants;
import com.gba.ws.util.AppEnums;
import com.gba.ws.util.ResponsesTaskResults;

/**
 * Implements {@link StudyDao} details.
 * 
 * @author Mohan
 * @createdOn Jan 9, 2018 10:52:30 AM
 */
@Repository
public class StudyDaoImpl implements StudyDao {

	private static final Logger LOGGER = Logger.getLogger(StudyDaoImpl.class);

	@Autowired
	private SessionFactory sessionFactory;

	/**
	 * @author Mohan
	 * @param sessionFactory
	 *            the {@link SessionFactory}
	 */
	public StudyDaoImpl(SessionFactory sessionFactory) {
		super();
		this.sessionFactory = sessionFactory;
	}

	@Override
	public EnrollmentTokensDto fetchEnrollmentTokenDetails(String findBy, String findByType) throws CustomException {
		LOGGER.info("INFO: StudyDaoImpl - fetchEnrollmentTokenDetails() :: starts");
		EnrollmentTokensDto enrollmentTokenDto = null;
		try (Session session = sessionFactory.openSession()) {
			if (findByType.equals(AppConstants.FIND_BY_TYPE_ENROLLMENTID)) {
				enrollmentTokenDto = (EnrollmentTokensDto) session
						.getNamedQuery("EnrollmentTokensDto.findByEnrollmentId")
						.setString(AppEnums.QK_ENROLLMENT_IDENTIFIER.value(), findBy).uniqueResult();
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: StudyDaoImpl - fetchEnrollmentTokenDetails()", e);
		}
		LOGGER.info("INFO: StudyDaoImpl - fetchEnrollmentTokenDetails() :: ends");
		return enrollmentTokenDto;
	}

	@Override
	public boolean validateStudyId(String studyId) throws CustomException {
		LOGGER.info("INFO: StudyDaoImpl - validateStudyId() :: starts");
		boolean isValidStudyId = false;
		StudiesDto study = null;
		try (Session session = sessionFactory.openSession()) {
			study = (StudiesDto) session.getNamedQuery("StudiesDto.fetchByStudyId")
					.setInteger(AppEnums.QK_STUDY_IDENTIFIER.value(), Integer.parseInt(studyId)).uniqueResult();
			if (study != null) {
				isValidStudyId = true;
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: StudyDaoImpl - validateStudyId()", e);
		}
		LOGGER.info("INFO: StudyDaoImpl - validateStudyId() :: ends");
		return isValidStudyId;
	}

	@Override
	public UserStudiesDto saveOrUpdateUserStudies(UserStudiesDto userStudies, String type) throws CustomException {
		LOGGER.info("INFO: StudyDaoImpl - saveOrUpdateUserStudies() :: starts");
		Transaction transaction = null;
		UserStudiesDto updateUserStudies = null;
		try (Session session = sessionFactory.openSession()) {
			if (userStudies != null) {
				transaction = session.beginTransaction();
				switch (type) {
					case AppConstants.DB_SAVE:
						session.save(userStudies);
						break;
					case AppConstants.DB_UPDATE:
						session.update(userStudies);
						break;
					case AppConstants.DB_SAVE_OR_UPDATE:
						session.saveOrUpdate(userStudies);
						break;
					default:
						break;
				}
				transaction.commit();
				updateUserStudies = userStudies;
			}
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			LOGGER.error("ERROR: StudyDaoImpl - saveOrUpdateUserStudies()", e);
		}
		LOGGER.info("INFO: StudyDaoImpl - saveOrUpdateUserStudies() :: ends");
		return updateUserStudies;
	}

	@Override
	public EnrollmentTokensDto saveOrUpdateEnrollmentTokens(EnrollmentTokensDto enrollmentToken, String type)
			throws CustomException {
		LOGGER.info("INFO: StudyDaoImpl - saveOrUpdateEnrollmentTokens() :: starts");
		Transaction transaction = null;
		EnrollmentTokensDto updateEnrollmentToken = null;
		try (Session session = sessionFactory.openSession()) {
			if (enrollmentToken != null) {
				transaction = session.beginTransaction();
				switch (type) {
					case AppConstants.DB_SAVE:
						session.save(enrollmentToken);
						break;
					case AppConstants.DB_UPDATE:
						session.update(enrollmentToken);
						break;
					default:
						break;
				}
				transaction.commit();
				updateEnrollmentToken = enrollmentToken;
			}
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			LOGGER.error("ERROR: StudyDaoImpl - saveOrUpdateEnrollmentTokens()", e);
		}
		LOGGER.info("INFO: StudyDaoImpl - saveOrUpdateEnrollmentTokens() :: ends");
		return updateEnrollmentToken;
	}

	@Override
	public UserStudiesDto fetchUserStudiesDetails(String findBy1, String findBy2, String findByType)
			throws CustomException {
		LOGGER.info("INFO: StudyDaoImpl - fetchUserStudiesDetails() :: starts");
		UserStudiesDto updateUserStudies = null;
		try (Session session = sessionFactory.openSession()) {
			switch (findByType) {
				case AppConstants.FIND_BY_TYPE_USERID:
					updateUserStudies = (UserStudiesDto) session.getNamedQuery("UserStudiesDto.fetchByUserId")
							.setInteger(AppEnums.QK_USER_IDENTIFIER.value(), Integer.parseInt(findBy1)).uniqueResult();
					break;
				case AppConstants.FIND_BY_TYPE_USERID_STUDYID:
					updateUserStudies = (UserStudiesDto) session.getNamedQuery("UserStudiesDto.fetchByUserIdNStudyId")
							.setInteger(AppEnums.QK_USER_IDENTIFIER.value(), Integer.parseInt(findBy1))
							.setInteger(AppEnums.QK_STUDY_IDENTIFIER.value(), Integer.parseInt(findBy2)).uniqueResult();
					break;
				default:
					break;
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: StudyDaoImpl - fetchUserStudiesDetails()", e);
		}
		LOGGER.info("INFO: StudyDaoImpl - fetchUserStudiesDetails() :: ends");
		return updateUserStudies;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void activateNonEnrolledTokens() throws CustomException {
		LOGGER.info("INFO: StudyDaoImpl - fetchUserStudiesDetails() :: starts");
		Transaction transaction = null;
		List<EnrollmentTokensDto> activateNonEnrolledToken = null;
		try (Session session = sessionFactory.openSession()) {
			activateNonEnrolledToken = session.getNamedQuery("EnrollmentTokensDto.updateEnrollmentTokens").list();
			if (activateNonEnrolledToken != null && !activateNonEnrolledToken.isEmpty()) {
				transaction = session.beginTransaction();
				for (EnrollmentTokensDto enrollToken : activateNonEnrolledToken) {
					enrollToken.setIsActive(true);
					session.update(enrollToken);
				}
				transaction.commit();
			}
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			LOGGER.error("ERROR: StudyDaoImpl - fetchUserStudiesDetails()", e);
		}
		LOGGER.info("INFO: StudyDaoImpl - fetchUserStudiesDetails() :: ends");
	}

	@Override
	public StudyConsentDto saveOrUpdateStudyConsent(StudyConsentDto studyConsent, String type) throws CustomException {
		LOGGER.info("INFO: StudyDaoImpl - saveOrUpdateStudyConsent() :: starts");
		Transaction transaction = null;
		StudyConsentDto updateStudyConsent = null;
		try (Session session = sessionFactory.openSession()) {
			if (studyConsent != null) {
				transaction = session.beginTransaction();
				switch (type) {
					case AppConstants.DB_SAVE:
						session.save(studyConsent);
						break;
					case AppConstants.DB_UPDATE:
						session.update(studyConsent);
						break;
					default:
						break;
				}
				transaction.commit();
				updateStudyConsent = studyConsent;
			}
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			LOGGER.error("ERROR: StudyDaoImpl - saveOrUpdateStudyConsent()", e);
		}
		LOGGER.info("INFO: StudyDaoImpl - saveOrUpdateStudyConsent() :: ends");
		return updateStudyConsent;
	}

	@Override
	public StudyConsentDto fetchStudyConsentDetails(String findBy1, String findBy2, String findByType)
			throws CustomException {
		LOGGER.info("INFO: StudyDaoImpl - fetchStudyConsentDetails() :: starts");
		StudyConsentDto studyConsent = null;
		try (Session session = sessionFactory.openSession()) {
			if (findByType.equals(AppConstants.FIND_BY_TYPE_USERID_STUDYID)) {
				studyConsent = (StudyConsentDto) session.getNamedQuery("StudyConsentDto.findByUserIdStudyId")
						.setInteger(AppEnums.QK_USER_IDENTIFIER.value(), Integer.parseInt(findBy1))
						.setInteger(AppEnums.QK_STUDY_IDENTIFIER.value(), Integer.parseInt(findBy2)).uniqueResult();
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: StudyDaoImpl - fetchStudyConsentDetails()", e);
		}
		LOGGER.info("INFO: StudyDaoImpl - fetchStudyConsentDetails() :: ends");
		return studyConsent;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AdminUsersDto> fetchAllAdminUsers() throws CustomException {
		LOGGER.info("INFO: StudyDaoImpl - fetchAllAdminUsers() :: starts");
		List<AdminUsersDto> adminUsers = new ArrayList<>();
		try (Session session = sessionFactory.openSession()) {
			adminUsers = session.getNamedQuery("AdminUsersDto.fetchAll").list();
		} catch (Exception e) {
			LOGGER.error("ERROR: StudyDaoImpl - fetchAllAdminUsers()", e);
		}
		LOGGER.info("INFO: StudyDaoImpl - fetchAllAdminUsers() :: ends");
		return adminUsers;
	}

	@Override
	public StudiesDto fetchStudyDetailsByStudyId(String studyId) throws CustomException {
		LOGGER.info("INFO: StudyDaoImpl - fetchStudyDetailsByStudyId() :: starts");
		StudiesDto study = null;
		try (Session session = sessionFactory.openSession()) {
			study = (StudiesDto) session.getNamedQuery("StudiesDto.fetchByStudyId")
					.setInteger(AppEnums.QK_STUDY_IDENTIFIER.value(), Integer.parseInt(studyId)).uniqueResult();
		} catch (Exception e) {
			LOGGER.error("ERROR: StudyDaoImpl - fetchStudyDetailsByStudyId()", e);
		}
		LOGGER.info("INFO: StudyDaoImpl - fetchStudyDetailsByStudyId() :: ends");
		return study;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ActivityGroupDto> fetchActivityGroupsList(String findBy, String findByType)
			throws CustomException {
		LOGGER.info("INFO: StudyDaoImpl - fetchActivityGroupsList() :: starts");
		List<ActivityGroupDto> activityGroupList = new ArrayList<>();
		try (Session session = sessionFactory.openSession()) {
			activityGroupList = session.getNamedQuery("ActivityGroupDto.findByGroupId")
					.setInteger(AppEnums.QK_GROUP_IDENTIFIER.value(), Integer.parseInt(findBy)).list();
		} catch (Exception e) {
			LOGGER.error("ERROR: StudyDaoImpl - fetchActivityGroupsList()", e);
		}
		LOGGER.info("INFO: StudyDaoImpl - fetchActivityGroupsList() :: ends");
		return activityGroupList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ActivityGroupDto> fetchActivityGroupsListByConditionId(int conditionId)
			throws CustomException {
		LOGGER.info("INFO: StudyDaoImpl - fetchActivityGroupsListByConditionId() :: starts");
		List<ActivityGroupDto> activityGroupList = new ArrayList<>();
		try (Session session = sessionFactory.openSession()) {
			activityGroupList = session.getNamedQuery("ActivityGroupDto.findByConditionId")
					.setInteger("conditionId", conditionId).list();
		} catch (Exception e) {
			LOGGER.error("ERROR: StudyDaoImpl - fetchActivityGroupsListByConditionId()", e);
		}
		LOGGER.info("INFO: StudyDaoImpl - fetchActivityGroupsListByConditionId() :: ends");
		return activityGroupList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UserStudiesDto> fetchUserStudiesDetailsList(List<Object> findBy1, String findBy2, String findByType) {
		LOGGER.info("INFO: StudyDaoImpl - fetchUserStudiesDetailsList() :: starts");
		List<UserStudiesDto> userStudyList = new ArrayList<>();
		try (Session session = sessionFactory.openSession()) {
			userStudyList = session.getNamedQuery("UserStudiesDto.fetchAllByUserIdNStudyId")
					.setParameterList(AppEnums.QK_USER_IDENTIFIER_LIST.value(), findBy1)
					.setInteger(AppEnums.QK_STUDY_IDENTIFIER.value(), Integer.parseInt(findBy2)).list();
		} catch (Exception e) {
			LOGGER.error("ERROR: StudyDaoImpl - fetchUserStudiesDetailsList()", e);
		}
		LOGGER.info("INFO: StudyDaoImpl - fetchUserStudiesDetailsList() :: ends");
		return userStudyList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ActivityGroupDto> fetchAllActivityGroupList(List<Object> findBy, String findByType) {
		LOGGER.info("INFO: StudyDaoImpl - fetchAllActivityGroupList() :: starts");
		List<ActivityGroupDto> activityGroupList = new ArrayList<>();
		try (Session session = sessionFactory.openSession()) {
			activityGroupList = session.getNamedQuery("ActivityGroupDto.findAllByConditionIds")
					.setParameterList(AppEnums.QK_CONDITION_IDENTIFIER_LIST.value(), findBy).list();
		} catch (Exception e) {
			LOGGER.error("ERROR: StudyDaoImpl - fetchAllActivityGroupList()", e);
		}
		LOGGER.info("INFO: StudyDaoImpl - fetchAllActivityGroupList() :: ends");
		return activityGroupList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EnrollmentTokensDto> fetchAllEnrollmentDetailsList() {
		LOGGER.info("INFO: StudyDaoImpl - fetchAllEnrollmentDetailsList() :: starts");
		List<EnrollmentTokensDto> enrollmentTokensList = new ArrayList<>();
		try (Session session = sessionFactory.openSession()) {
			enrollmentTokensList = session.getNamedQuery("EnrollmentTokensDto.findAll").list();
		} catch (Exception e) {
			LOGGER.error("ERROR: StudyDaoImpl - fetchAllEnrollmentDetailsList()", e);
		}
		LOGGER.info("INFO: StudyDaoImpl - fetchAllEnrollmentDetailsList() :: ends");
		return enrollmentTokensList;
	}

	@Override
	public GroupIdentifierDto fetchGroupIdentifierDetails(String findBy, String findByType) {
		LOGGER.info("INFO: StudyDaoImpl - fetchGroupIdentifierDetails() :: starts");
		GroupIdentifierDto groupIdentifier = null;
		try (Session session = sessionFactory.openSession()) {
			groupIdentifier = (GroupIdentifierDto) session.getNamedQuery("GroupIdentifierDto.findByGroupId")
					.setInteger(AppConstants.FIND_BY_TYPE_GROUP_ID_NAME, Integer.parseInt(findBy)).uniqueResult();
		} catch (Exception e) {
			LOGGER.error("ERROR: StudyDaoImpl - fetchGroupIdentifierDetails()", e);
		}
		LOGGER.info("INFO: StudyDaoImpl - fetchGroupIdentifierDetails() :: ends");
		return groupIdentifier;
	}

	public GroupIdentifierDto fetchGroupIdentifierDetailsById(int id) {
		LOGGER.info("INFO: StudyDaoImpl - fetchGroupIdentifierDetailsById() :: starts");
		GroupIdentifierDto groupIdentifier = null;
		try (Session session = sessionFactory.openSession()) {
			groupIdentifier = (GroupIdentifierDto) session.getNamedQuery("GroupIdentifierDto.findByGroupIdWithoutName")
					.setInteger("groupId", id).uniqueResult();
		} catch (Exception e) {
			LOGGER.error("ERROR: StudyDaoImpl - fetchGroupIdentifierDetailsById()", e);
		}
		LOGGER.info("INFO: StudyDaoImpl - fetchGroupIdentifierDetailsById() :: ends");
		return groupIdentifier;
	}
	
	@Override
	public boolean validateEnrollmentId(String enrollmentId) throws CustomException {
		LOGGER.info("INFO: StudyDaoImpl - validateEnrollmentId() :: starts");
		boolean isValidEnrollmentId = false;
		EnrollmentTokensDto enrollment = null;
		try (Session session = sessionFactory.openSession()) {
			enrollment = (EnrollmentTokensDto) session.getNamedQuery("EnrollmentTokensDto.findByEnrollmentId")
					.setString(AppEnums.QK_ENROLLMENT_IDENTIFIER.value(), enrollmentId).uniqueResult();
			if (enrollment != null) {
				isValidEnrollmentId = true;
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: StudyDaoImpl - validateEnrollmentId()", e);
		}
		LOGGER.info("INFO: StudyDaoImpl - validateEnrollmentId() :: ends");
		return isValidEnrollmentId;
	}
	
	
	@Override
	public ResponseActivityTempDto fetchResponseActivityDetails(String findBy1, String findBy2, String findByType)
			throws CustomException {
		LOGGER.info("INFO: StudyDaoImpl - fetchResponseActivityDetails() :: starts");
		ResponseActivityTempDto activityTempDto = null;
		try (Session session = sessionFactory.openSession()) {
			if (findByType.equals(AppConstants.FIND_BY_TYPE_USERID_ENROLLMENTID)) {
				activityTempDto = (ResponseActivityTempDto) session.getNamedQuery("ResponseActivityTempDto.findByUserIdEnrollmentId")
						.setInteger(AppEnums.QK_USER_IDENTIFIER.value(), Integer.parseInt(findBy1))
						.setString(AppEnums.QK_ENROLLMENT_IDENTIFIER.value(), findBy2).uniqueResult();
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: StudyDaoImpl - fetchResponseActivityDetails()", e);
		}
		LOGGER.info("INFO: StudyDaoImpl - fetchResponseActivityDetails() :: ends");
		return activityTempDto;
	}

	
	@Override
	public ResponseActivityTempDto saveOrUpdateResponseActivityTemp(ResponseActivityTempDto activityTempDto, String type) throws CustomException {
		LOGGER.info("INFO: StudyDaoImpl - saveOrUpdateResponseActivityTemp() :: starts");
		Transaction transaction = null;
		ResponseActivityTempDto updateResponsesActivityTemp = null;
		try (Session session = sessionFactory.openSession()) {
			if (activityTempDto != null) {
				transaction = session.beginTransaction();
				switch (type) {
					case AppConstants.DB_SAVE:
						session.save(activityTempDto);
						break;
					case AppConstants.DB_UPDATE:
						session.update(activityTempDto);
						break;
					default:
						break;
				}
				transaction.commit();
				updateResponsesActivityTemp = activityTempDto;
			}
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			LOGGER.error("ERROR: StudyDaoImpl - saveOrUpdateResponseActivityTemp()", e);
		}
		LOGGER.info("INFO: StudyDaoImpl - saveOrUpdateResponseActivityTemp() :: ends");
		return updateResponsesActivityTemp;
	}
	
	

	@Override
	public GroupUsersInfoDto saveOrUpdateGroupUsers(GroupUsersInfoDto groupUsersInfoDto, String type) throws CustomException {
		LOGGER.info("INFO: StudyDaoImpl - saveOrUpdateGroupUsers() :: starts");
		Transaction transaction = null;
		GroupUsersInfoDto updateUsersInfoDto = null;
		try (Session session = sessionFactory.openSession()) {
			if (groupUsersInfoDto != null) {
				transaction = session.beginTransaction();
				switch (type) {
					case AppConstants.DB_SAVE:
						session.save(groupUsersInfoDto);
						break;
					case AppConstants.DB_UPDATE:
						session.update(groupUsersInfoDto);
						break;
					case AppConstants.DB_SAVE_OR_UPDATE:
						session.saveOrUpdate(groupUsersInfoDto);
						break;
					default:
						break;
				}
				transaction.commit();
				updateUsersInfoDto = groupUsersInfoDto;
			}
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			LOGGER.error("ERROR: StudyDaoImpl - saveOrUpdateGroupUsers()", e);
		}
		LOGGER.info("INFO: StudyDaoImpl - saveOrUpdateGroupUsers() :: ends");
		return updateUsersInfoDto;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<GroupUsersInfoDto> fetchGroupUsersInfoByType(Integer findBy, String findByType) {
		LOGGER.info("INFO: StudyDaoImpl - fetchGroupUsersInfoByUserId() :: starts");
		List<GroupUsersInfoDto> groupUsersInfoList = new ArrayList<>();
		try (Session session = sessionFactory.openSession()) {
			groupUsersInfoList = session.getNamedQuery("GroupUsersInfoDto.findByUserId")
					.setInteger(findByType, findBy).list();
		} catch (Exception e) {
			LOGGER.error("ERROR: StudyDaoImpl - fetchGroupUsersInfoByUserId()", e);
		}
		LOGGER.info("INFO: StudyDaoImpl - fetchGroupUsersInfoByUserId() :: ends");
		return groupUsersInfoList;
	}
	

	@Override
	public boolean saveOrUpdateGroupUsersList(List<GroupUsersInfoDto> groupUsersInfoDto, String type) throws CustomException {
		LOGGER.info("INFO: StudyDaoImpl - saveOrUpdateGroupUsersList() :: starts");
		Transaction transaction = null;
		boolean updateGroupuser = false;
		try (Session session = sessionFactory.openSession()) {
			if (groupUsersInfoDto != null) {
				transaction = session.beginTransaction();
				switch (type) {
					case AppConstants.DB_SAVE:
						groupUsersInfoDto.forEach(groupUser->{
							session.save(groupUser);
						});
						break;
					case AppConstants.DB_UPDATE:
						groupUsersInfoDto.forEach(groupUser->{
							session.update(groupUser);
						});
						break;
					case AppConstants.DB_SAVE_OR_UPDATE:
						groupUsersInfoDto.forEach(groupUser->{
							session.saveOrUpdate(groupUser);
						});
						break;
					default:
						break;
				}
				transaction.commit();
				updateGroupuser = true;
			}
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			LOGGER.error("ERROR: StudyDaoImpl - saveOrUpdateGroupUsersList()", e);
		}
		LOGGER.info("INFO: StudyDaoImpl - saveOrUpdateGroupUsersList() :: ends");
		return updateGroupuser;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ActivityGroupDto> fetchActivityGroupsListByUserId(Integer findBy,Integer groupId, String findByType)
			throws CustomException {
		LOGGER.info("INFO: StudyDaoImpl - fetchActivityGroupsListByUserId() :: starts");
		List<ActivityGroupDto> activityGroupList = new ArrayList<>();
		List<GroupUsersInfoDto> infoDtos = new ArrayList<>();
		List<Integer> groupList = new ArrayList<>();
		try (Session session = sessionFactory.openSession()) {
			
			infoDtos = this.fetchGroupUsersInfoByType(findBy, AppConstants.FIND_BY_TYPE_USERID);
			if(null != infoDtos && !infoDtos.isEmpty()) {
				groupList = infoDtos.stream().map(GroupUsersInfoDto :: getGroupId).collect(Collectors.toList());
			}
			if(groupId != 0) {
				groupList.add(groupId);
			}
				if(null != groupList && !groupList.isEmpty()) {
					activityGroupList = session.getNamedQuery("ActivityGroupDto.findByGroupIdList")
							.setParameterList(AppEnums.QK_GROUP_IDENTIFIER_LIST.value(), groupList).list();
				}
	
			
			
		} catch (Exception e) {
			LOGGER.error("ERROR: StudyDaoImpl - fetchActivityGroupsListByUserId()", e);
		}
		LOGGER.info("INFO: StudyDaoImpl - fetchActivityGroupsListByUserId() :: ends");
		return activityGroupList;
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<GroupUsersInfoDto> fetchAllGroupUsersInfoListbyId(Integer findBy, String findByType) {
		LOGGER.info("INFO: StudyDaoImpl - fetchAllGroupUsersInfoListbyId() :: starts");
		List<GroupUsersInfoDto> groupUsersInfoList = new ArrayList<>();
		String namedQuery = "";
		String parametreQuery = "";
		try (Session session = sessionFactory.openSession()) {
			if(findByType.equals(AppConstants.FIND_BY_TYPE_USERID)) {
				namedQuery = "GroupUsersInfoDto.findByUserId";
				parametreQuery = AppEnums.QK_USER_IDENTIFIER.value();
			}else if(findByType.equals(AppConstants.FIND_BY_TYPE_GROUPID)) {
				namedQuery = "GroupUsersInfoDto.findByGroupId";
				parametreQuery = AppEnums.QK_GROUP_IDENTIFIER.value();
			}
			groupUsersInfoList = session.getNamedQuery(namedQuery)
					.setParameter(parametreQuery, findBy).list();
		} catch (Exception e) {
			LOGGER.error("ERROR: StudyDaoImpl - fetchAllGroupUsersInfoListbyId()", e);
		}
		LOGGER.info("INFO: StudyDaoImpl - fetchAllGroupUsersInfoListbyId() :: ends");
		return groupUsersInfoList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<Integer, List<Integer>> getGroupUsersIdListFromMappingDetails(List<Integer> groupIds)
			throws CustomException {
		LOGGER.info("INFO: StudyDaoImpl - getGroupUsersIdListFromMappingDetails() :: Starts");
		List<Object[]> objList = null;
		Map<Integer, List<Integer>> userGroupIds = new HashMap<>();
		try (Session session = sessionFactory.openSession()) {
			if(null != groupIds && !groupIds.isEmpty()) {
				objList = (List<Object[]>) session.createSQLQuery("SELECT GMU.group_id , GROUP_CONCAT(CAST(GMU.user_id as CHAR)) FROM group_users_mapping GMU " + 
					" WHERE  GMU.group_id IN (:groupIds) AND GMU.status = 'Y' GROUP BY GMU.group_id").setParameterList("groupIds", groupIds);
				if(null != objList && !objList.isEmpty()) {
					for(Object[] obj : objList) {
						
						List<Integer> userIds = new ArrayList<>();
						if(null != obj[1]) {
							userIds = Arrays.asList(String.valueOf(obj[1]).split(",")).stream().
									map(s -> Integer.parseInt(s.trim()))
									.collect(Collectors.toList());
						}
						userGroupIds.put(null != obj[0] ? (Integer)obj[0] : 0, userIds);
					}
					
				}
				
			}
			
		} catch (Exception e) {
			LOGGER.error("ERROR: StudyDaoImpl - getGroupUsersIdListFromMappingDetails()", e);
		}
		LOGGER.info("INFO: StudyDaoImpl - getGroupUsersIdListFromMappingDetails() :: Ends");
		return userGroupIds;
	}

	@Override
	public GroupIdentifierDto saveOrUpdateGroups(GroupIdentifierDto groupInfoDto, String type)
			throws CustomException {
		LOGGER.info("INFO: StudyDaoImpl - saveOrUpdateGroups() :: starts");
		Transaction transaction = null;
		GroupIdentifierDto updateInfoDto = null;
		try (Session session = sessionFactory.openSession()) {
			if (groupInfoDto != null) {
				transaction = session.beginTransaction();
				switch (type) {
					case AppConstants.DB_SAVE:
						session.save(groupInfoDto);
						break;
					case AppConstants.DB_UPDATE:
						session.update(groupInfoDto);
						break;
					case AppConstants.DB_SAVE_OR_UPDATE:
						session.saveOrUpdate(groupInfoDto);
						break;
					default:
						break;
				}
				transaction.commit();
				updateInfoDto = groupInfoDto;
			}
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			LOGGER.error("ERROR: StudyDaoImpl - saveOrUpdateGroups()", e);
		}
		LOGGER.info("INFO: StudyDaoImpl - saveOrUpdateGroups() :: ends");
		return updateInfoDto;
	}

	@Override
	public boolean saveOrUpdateResponsesOfTaskActivitivies(List<ResponsesTasksActivitiesDto> responsestaskResults,
			String type) throws CustomException {
		LOGGER.info("INFO: StudyDaoImpl - saveOrUpdateResponsesOfTaskActivitivies() :: starts");
		Transaction transaction = null;
		boolean updateResponseTasks = false;
		try (Session session = sessionFactory.openSession()) {
			if (responsestaskResults != null && !responsestaskResults.isEmpty()) {
				transaction = session.beginTransaction();
				switch (type) {
					case AppConstants.DB_SAVE:
						responsestaskResults.forEach(responsesTasks->{
							session.save(responsesTasks);
						});
						break;
					case AppConstants.DB_UPDATE:
						responsestaskResults.forEach(responsesTasks->{
							session.update(responsesTasks);
						});
						break;
					case AppConstants.DB_SAVE_OR_UPDATE:
						responsestaskResults.forEach(responsesTasks->{
							session.saveOrUpdate(responsesTasks);
						});
						break;
					default:
						break;
				}
				transaction.commit();
				updateResponseTasks = true;
			}
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			LOGGER.error("ERROR: StudyDaoImpl - saveOrUpdateResponsesOfTaskActivitivies()", e);
		}
		LOGGER.info("INFO: StudyDaoImpl - saveOrUpdateResponsesOfTaskActivitivies() :: ends");
		return updateResponseTasks;
	}

	@Override
	public boolean saveOrUpdateResponsesOfSurveysActivitivies(
			List<ResponsesSurveysActivitiesDto> responsesSurveyResults, String type) throws CustomException {
		LOGGER.info("INFO: StudyDaoImpl - saveOrUpdateResponsesOfTaskActivitivies() :: starts");
		Transaction transaction = null;
		boolean updateResponseSurvey = false;
		try (Session session = sessionFactory.openSession()) {
			if (responsesSurveyResults != null && !responsesSurveyResults.isEmpty()) {
				transaction = session.beginTransaction();
				switch (type) {
					case AppConstants.DB_SAVE:
						responsesSurveyResults.forEach(responsesSurveys->{
							session.save(responsesSurveys);
						});
						break;
					case AppConstants.DB_UPDATE:
						responsesSurveyResults.forEach(responsesSurveys->{
							session.update(responsesSurveys);
						});
						break;
					case AppConstants.DB_SAVE_OR_UPDATE:
						responsesSurveyResults.forEach(responsesSurveys->{
							session.saveOrUpdate(responsesSurveys);
						});
						break;
					default:
						break;
				}
				transaction.commit();
				updateResponseSurvey = true;
			}
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			LOGGER.error("ERROR: StudyDaoImpl - saveOrUpdateResponsesOfTaskActivitivies()", e);
		}
		LOGGER.info("INFO: StudyDaoImpl - saveOrUpdateResponsesOfTaskActivitivies() :: ends");
		return updateResponseSurvey;
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public List<GroupUsersInfoDto> fetchAllGroupUsersInfoDetails() {
		LOGGER.info("INFO: StudyDaoImpl - fetchGroupUsersInfoByUserId() :: starts");
		List<GroupUsersInfoDto> groupUsersInfoList = new ArrayList<>();
		try (Session session = sessionFactory.openSession()) {
			groupUsersInfoList = session.getNamedQuery("GroupUsersInfoDto.findALL").list();
		} catch (Exception e) {
			LOGGER.error("ERROR: StudyDaoImpl - fetchGroupUsersInfoByUserId()", e);
		}
		LOGGER.info("INFO: StudyDaoImpl - fetchGroupUsersInfoByUserId() :: ends");
		return groupUsersInfoList;
	}
	
	
}
