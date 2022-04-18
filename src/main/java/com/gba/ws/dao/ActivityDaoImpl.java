package com.gba.ws.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.gba.ws.bean.KeywordsBean;
import com.gba.ws.bean.KeywordsResponse;
import com.gba.ws.bean.PushNotificationBean;
import com.gba.ws.bean.fitbit.HarvardIAQ;
import com.gba.ws.exception.CustomException;
import com.gba.ws.model.ActivitiesDto;
import com.gba.ws.model.ActivityGroupDto;
import com.gba.ws.model.ActivityConditionDto;
import com.gba.ws.model.AppVersionInfo;
import com.gba.ws.model.CratWordsDto;
import com.gba.ws.model.CratWordsUserMapDto;
import com.gba.ws.model.FitbitUserInfoDto;
import com.gba.ws.model.GroupUsersInfoDto;
import com.gba.ws.model.QuestionChoiceDto;
import com.gba.ws.model.QuestionsDto;
import com.gba.ws.model.RewardLevelsDto;
import com.gba.ws.model.TemporalConditionDto;
import com.gba.ws.model.ThresholdConditionsDto;
import com.gba.ws.model.UserActivitiesDto;
import com.gba.ws.model.UserActivitiesRunsDto;
import com.gba.ws.model.UserStudiesDto;
import com.gba.ws.util.AppConstants;
import com.gba.ws.util.AppEnums;
import com.gba.ws.util.AppUtil;

/**
 * Implements {@link ActivityDao} interface.
 * 
 * @author Mohan
 * @createdOn Jan 9, 2018 10:52:22 AM
 */
@Repository
public class ActivityDaoImpl implements ActivityDao {

	private static final Logger LOGGER = Logger.getLogger(ActivityDaoImpl.class);

	@Autowired
	private SessionFactory sessionFactory;

	/**
	 * @author Mohan
	 * @param sessionFactory
	 *            the {@link SessionFactory}
	 */
	public ActivityDaoImpl(SessionFactory sessionFactory) {
		super();
		this.sessionFactory = sessionFactory;
	}

	@Override
	public boolean validateActivityId(String activityId) throws CustomException {
		LOGGER.info("INFO: ActivityDaoImpl - validateActivityId() :: starts");
		boolean isValidActivityId = false;
		ActivityConditionDto activityCondition = null;
		try (Session session = sessionFactory.openSession()) {
			activityCondition = (ActivityConditionDto) session
					.getNamedQuery("ActivityConditionDto.findByActivityConditionId")
					.setString(AppEnums.QK_ACTIVITY_CONDITION_IDENTIFIER.value(), activityId).uniqueResult();
			if (activityCondition != null) {
				isValidActivityId = true;
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityDaoImpl - validateActivityId()", e);
		}
		LOGGER.info("INFO: ActivityDaoImpl - validateActivityId() :: ends");
		return isValidActivityId;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ActivitiesDto> fetchActivitiesDetailsList(String findBy, String findByType) throws CustomException {
		LOGGER.info("INFO: ActivityDaoImpl - fetchActivitiesDetailsList() :: starts");
		List<ActivitiesDto> activitiesList = new ArrayList<>();
		try (Session session = sessionFactory.openSession()) {
			if (findByType.equals(AppConstants.FIND_BY_TYPE_STUDYID)) {
				activitiesList = session.getNamedQuery("ActivitiesDto.findByStudyId")
						.setInteger(AppEnums.QK_STUDY_IDENTIFIER.value(), Integer.parseInt(findBy)).list();
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityDaoImpl - fetchActivitiesDetailsList()", e);
		}
		LOGGER.info("INFO: ActivityDaoImpl - fetchActivitiesDetailsList() :: ends");
		return activitiesList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ActivityConditionDto> fetchActivityConditionDetailsList(List<Object> findBy, String findByType)
			throws CustomException {
		LOGGER.info("INFO: ActivityDaoImpl - fetchActivityConditionDetailsList() :: starts");
		List<ActivityConditionDto> activityConditionsList = new ArrayList<>();
		try (Session session = sessionFactory.openSession()) {
			if (findByType.equals(AppConstants.FIND_BY_TYPE_ACTIVITY_IDS)) {
				activityConditionsList = session.getNamedQuery("ActivityConditionDto.findAllByActivityIds")
						.setParameterList(AppEnums.QK_ACTIVITY_IDENTIFIER_LIST.value(), findBy).list();
			} else if (findByType.equals(AppConstants.FIND_BY_TYPE_CONDITIONIDS)) {
				activityConditionsList = session.getNamedQuery("ActivityConditionDto.findAllByConditionIds")
						.setParameterList(AppEnums.QK_CONDITION_IDENTIFIER_LIST.value(), findBy).list();
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityDaoImpl - fetchActivityConditionDetailsList()", e);
		}
		LOGGER.info("INFO: ActivityDaoImpl - fetchActivityConditionDetailsList() :: ends");
		return activityConditionsList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UserActivitiesDto> fetchUserActivityDetailsList(String findBy1, String findBy2, String findByType, String timeZone)
			throws CustomException {
		LOGGER.info("INFO: ActivityDaoImpl - fetchUserActivityDetailsList() :: starts");
		List<UserActivitiesDto> userActivityList = new ArrayList<>();
		try (Session session = sessionFactory.openSession()) {
			if (findByType.equals(AppConstants.FIND_BY_TYPE_USERID_STUDYID)) {
				userActivityList = session.getNamedQuery("UserActivitiesDto.findByUserIdNUserStudiesId")
						.setInteger(AppEnums.QK_USER_IDENTIFIER.value(), Integer.parseInt(findBy1))
						.setInteger(AppEnums.QK_USER_STUDIES_IDENTIFIER.value(), Integer.parseInt(findBy2)).list();
			} else if (findByType.equals(AppConstants.FIND_BY_TYPE_USERID)) {
				userActivityList = session.getNamedQuery("UserActivitiesDto.findByUserId")
						.setInteger(AppEnums.QK_USER_IDENTIFIER.value(), Integer.parseInt(findBy1))
						.setInteger(AppEnums.QK_USER_STUDIES_IDENTIFIER.value(), Integer.parseInt(findBy2)).list();
			} else if (findByType.equals(AppConstants.FIND_BY_TYPE_USERID_STUDYID_ACTIVITY_SUB_TYPE)) {
				userActivityList = session.getNamedQuery("UserActivitiesDto.findByUserIdNUserStudiesIdNActivitySubType")
						.setInteger(AppEnums.QK_USER_IDENTIFIER.value(), Integer.parseInt(findBy1))
						.setInteger(AppEnums.QK_USER_STUDIES_IDENTIFIER.value(), Integer.parseInt(findBy2))
						.setString(AppEnums.QK_ACTIVITY_SUB_TYPE.value(), AppConstants.ACTIVITY_SUB_TYPE_TRIGGERED)
						.setString(AppEnums.QK_END_DATE.value(), AppUtil.getCurrentUserDate(timeZone)).list();
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityDaoImpl - fetchUserActivityDetailsList()", e);
		}
		LOGGER.info("INFO: ActivityDaoImpl - fetchUserActivityDetailsList() :: ends");
		return userActivityList;
	}

	@Override
	public UserActivitiesDto saveOrUpdateUserActivities(UserActivitiesDto userActivitiesDto, String type)
			throws CustomException {
		LOGGER.info("INFO: ActivityDaoImpl - saveOrUpdateUserActivities() :: starts");
		Transaction transaction = null;
		UserActivitiesDto updateUserActivities = null;
		try (Session session = sessionFactory.openSession()) {
			if (userActivitiesDto != null) {
				transaction = session.beginTransaction();

				switch (type) {
					case AppConstants.DB_SAVE:
						session.save(userActivitiesDto);
						break;
					case AppConstants.DB_UPDATE:
						session.update(userActivitiesDto);
						break;
					default:
						break;
				}

				transaction.commit();
				updateUserActivities = userActivitiesDto;
			}
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			LOGGER.error("ERROR: ActivityDaoImpl - saveOrUpdateUserActivities()", e);
		}
		LOGGER.info("INFO: ActivityDaoImpl - saveOrUpdateUserActivities() :: ends");
		return updateUserActivities;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TemporalConditionDto> fetchTemporalConditionDetailsList(List<Object> findBy, String findByType)
			throws CustomException {
		LOGGER.info("INFO: ActivityDaoImpl - fetchTemporalConditionDetailsList() :: starts");
		List<TemporalConditionDto> temporalConditionList = new ArrayList<>();
		try (Session session = sessionFactory.openSession()) {
			if (findByType.equals(AppConstants.FIND_BY_TYPE_CONDITIONID)) {
				temporalConditionList = session.getNamedQuery("TemporalConditionDto.findAllByConditionIds")
						.setParameterList(AppEnums.QK_CONDITION_IDENTIFIER_LIST.value(), findBy).list();
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityDaoImpl - fetchTemporalConditionDetailsList()", e);
		}
		LOGGER.info("INFO: ActivityDaoImpl - fetchTemporalConditionDetailsList() :: ends");
		return temporalConditionList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TemporalConditionDto> fetchTemporalActivityScheduleList(String currentDate) throws CustomException {
		LOGGER.info("INFO: ActivityDaoImpl - fetchTemporalActivityScheduleList() :: starts");
		List<TemporalConditionDto> activitiesList = new ArrayList<>();
		try (Session session = sessionFactory.openSession()) {
				activitiesList = session.getNamedQuery("TemporalConditionDto.findAllByDate")
						.setString("currentDate",currentDate).list();

		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityDaoImpl - fetchTemporalActivityScheduleList()", e);
		}
		LOGGER.info("INFO: ActivityDaoImpl - fetchTemporalActivityScheduleList() :: ends");
		return activitiesList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ThresholdConditionsDto> fetchThresholdConditionDetailsList(List<Object> findBy, String findByType)
			throws CustomException {
		LOGGER.info("INFO: ActivityDaoImpl - fetchThresholdConditionDetailsList() :: starts");
		List<ThresholdConditionsDto> thresholdConditionList = new ArrayList<>();
		try (Session session = sessionFactory.openSession()) {
			if (findByType.equals(AppConstants.FIND_BY_TYPE_CONDITIONID)) {
				thresholdConditionList = session.getNamedQuery("ThresholdConditionsDto.findAllByConditionIds")
						.setParameterList(AppEnums.QK_CONDITION_IDENTIFIER_LIST.value(), findBy).list();
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityDaoImpl - fetchThresholdConditionDetailsList()", e);
		}
		LOGGER.info("INFO: ActivityDaoImpl - fetchThresholdConditionDetailsList() :: ends");
		return thresholdConditionList;
	}

	@Override
	public ActivityConditionDto fetchActivityConditionDetails(String findBy, String findByType) throws CustomException {
		LOGGER.info("INFO: ActivityDaoImpl - fetchActivityConditionDetails() :: starts");
		ActivityConditionDto activityCondition = null;
		try (Session session = sessionFactory.openSession()) {
			switch (findByType) {
				case AppConstants.FIND_BY_TYPE_ACTIVITY_CONDITIONID:
					activityCondition = (ActivityConditionDto) session
							.getNamedQuery("ActivityConditionDto.findByActivityConditionId")
							.setString(AppEnums.QK_ACTIVITY_CONDITION_IDENTIFIER.value(), findBy).uniqueResult();
					break;
				case AppConstants.FIND_BY_TYPE_ACTIVITY_ID:
					activityCondition = (ActivityConditionDto) session
							.getNamedQuery("ActivityConditionDto.findByActivityId")
							.setInteger(AppEnums.QK_ACTIVITY_IDENTIFIER.value(), Integer.parseInt(findBy)).uniqueResult();
					break;
				case AppConstants.FIND_BY_TYPE_CONDITIONID:
					activityCondition = (ActivityConditionDto) session
							.getNamedQuery("ActivityConditionDto.findByConditionId")
							.setInteger(AppEnums.QK_CONDITION_IDENTIFIER.value(), Integer.parseInt(findBy)).uniqueResult();
					break;
				default:
					break;
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityDaoImpl - fetchActivityConditionDetails()", e);
		}
		LOGGER.info("INFO: ActivityDaoImpl - fetchActivityConditionDetails() :: ends");
		return activityCondition;
	}

	@Override
	public UserActivitiesDto findByUserIdConditionId(int conditionId, int userId, String startActivityTime) throws CustomException {
		LOGGER.info("INFO: ActivityDaoImpl - findByUserIdConditionId() :: starts");
		UserActivitiesDto userActivitiesDto = null;
		List<UserActivitiesDto> userActivitiesDtoList = null;
		try (Session session = sessionFactory.openSession()) {
			userActivitiesDtoList = (List<UserActivitiesDto>) session
					.getNamedQuery("UserActivitiesDto.findByUserIdConditionId")
					.setInteger("conditionId", conditionId)
					.setInteger("userId", userId)
					.setString("startActivityTime", startActivityTime)
					.list();
			if(userActivitiesDtoList!=null && !userActivitiesDtoList.isEmpty()){
				userActivitiesDto = userActivitiesDtoList.get(0);
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityDaoImpl - findByUserIdConditionId()", e);
		}
		LOGGER.info("INFO: ActivityDaoImpl - findByUserIdConditionId() :: ends");
		return userActivitiesDto;
	}

	@Override
	public UserActivitiesDto fetchUserActivityDetails(String findBy1, String findBy2, String findBy3, String findByType)
			throws CustomException {
		LOGGER.info("INFO: ActivityDaoImpl - fetchUserActivityDetails() :: starts");
		UserActivitiesDto userActivity = null;
		List<UserActivitiesDto> userActivityList = null;
		try (Session session = sessionFactory.openSession()) {
			if (findByType.equals(AppConstants.FIND_BY_TYPE_USERID_CONDITIONID)) {
				userActivityList = (List<UserActivitiesDto>) session.getNamedQuery("UserActivitiesDto.findByUserIdNConditionId")
						.setInteger(AppEnums.QK_USER_IDENTIFIER.value(), Integer.parseInt(findBy1))
						.setInteger(AppEnums.QK_CONDITION_IDENTIFIER.value(), Integer.parseInt(findBy2))
						.setInteger(AppEnums.QK_USER_STUDIES_IDENTIFIER.value(), Integer.parseInt(findBy3))
						.list();
				if(userActivityList!=null && !userActivityList.isEmpty()) {
					userActivity = userActivityList.get(0);
				}
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityDaoImpl - fetchUserActivityDetails()", e);
		}
		LOGGER.info("INFO: ActivityDaoImpl - fetchUserActivityDetails() :: ends");
		return userActivity;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UserActivitiesRunsDto> fetchUserActivityRunDetailsList(String findBy1, List<Object> findBy2,
			String findBy3, String findByType) throws CustomException {
		LOGGER.info("INFO: ActivityDaoImpl - fetchUserActivityRunDetailsList() :: starts");
		List<UserActivitiesRunsDto> userActivityRunsList = new ArrayList<>();
		try (Session session = sessionFactory.openSession()) {
			if (findByType.equals(AppConstants.FIND_BY_TYPE_USERID_CONDITIONIDS)) {
				userActivityRunsList = session.getNamedQuery("UserActivitiesRunsDto.fetchByUserIdNConditionIds")
						.setInteger(AppEnums.QK_USER_IDENTIFIER.value(), Integer.parseInt(findBy1))
						.setParameterList(AppEnums.QK_CONDITION_IDENTIFIER_LIST.value(), findBy2)
						.setInteger(AppEnums.QK_USER_STUDIES_IDENTIFIER.value(), Integer.parseInt(findBy3)).list();
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityDaoImpl - fetchUserActivityRunDetailsList()", e);
		}
		LOGGER.info("INFO: ActivityDaoImpl - fetchUserActivityRunDetailsList() :: ends");
		return userActivityRunsList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UserActivitiesRunsDto> fetchUserActivityRunDetailsListCriteria(String findBy1,
			Map<String, Object> criteriaMap, String findByType) throws CustomException {
		LOGGER.info("INFO: ActivityDaoImpl - fetchUserActivityRunDetailsListCriteria() :: starts");
		List<UserActivitiesRunsDto> userActivityRunsList = new ArrayList<>();
		try (Session session = sessionFactory.openSession()) {
			switch (findByType) {
				case AppConstants.FIND_BY_TYPE_USERID_ACTIVITYID_RUNID:
					userActivityRunsList = session
							.getNamedQuery("UserActivitiesRunsDto.fetchByUserIdNConditionIdNActivityRunId")
							.setInteger(AppEnums.QK_USER_IDENTIFIER.value(), Integer.parseInt(findBy1))
							.setInteger(AppEnums.QK_CONDITION_IDENTIFIER.value(),
									(int) criteriaMap.get(AppEnums.QK_CONDITION_IDENTIFIER.value()))
							.setInteger(AppEnums.QK_ACTIVITY_RUN_IDENTIFIER.value(),
									(int) criteriaMap.get(AppEnums.QK_ACTIVITY_RUN_IDENTIFIER.value()))
							.setInteger(AppEnums.QK_USER_STUDIES_IDENTIFIER.value(),
									(int) criteriaMap.get(AppEnums.QK_USER_STUDIES_IDENTIFIER.value()))
							.list();
					break;
				case AppConstants.FIND_BY_TYPE_USERID_CONDITIONID:
					userActivityRunsList = session.getNamedQuery("UserActivitiesRunsDto.fetchByUserIdNConditionId")
							.setInteger(AppEnums.QK_USER_IDENTIFIER.value(), Integer.parseInt(findBy1))
							.setInteger(AppEnums.QK_CONDITION_IDENTIFIER.value(),
									(int) criteriaMap.get(AppEnums.QK_CONDITION_IDENTIFIER.value()))
							.setInteger(AppEnums.QK_USER_STUDIES_IDENTIFIER.value(),
									(int) criteriaMap.get(AppEnums.QK_USER_STUDIES_IDENTIFIER.value()))
							.list();
					break;
				case AppConstants.FIND_BY_TYPE_USERID_ACTIVITYID_RUNID_THRESHOLD:
					userActivityRunsList = session
							.getNamedQuery("UserActivitiesRunsDto.fetchByUserIdNConditionIdNActivityRunIdNThreshold")
							.setInteger(AppEnums.QK_USER_IDENTIFIER.value(), Integer.parseInt(findBy1))
							.setInteger(AppEnums.QK_CONDITION_IDENTIFIER.value(),
									(int) criteriaMap.get(AppEnums.QK_CONDITION_IDENTIFIER.value()))
							.setInteger(AppEnums.QK_ACTIVITY_RUN_IDENTIFIER.value(),
									(int) criteriaMap.get(AppEnums.QK_ACTIVITY_RUN_IDENTIFIER.value()))
							.setInteger(AppEnums.QK_USER_STUDIES_IDENTIFIER.value(),
									(int) criteriaMap.get(AppEnums.QK_USER_STUDIES_IDENTIFIER.value()))
							.list();
					break;
				default:
					break;
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityDaoImpl - fetchUserActivityRunDetailsListCriteria()", e);
		}
		LOGGER.info("INFO: ActivityDaoImpl - fetchUserActivityRunDetailsListCriteria() :: ends");
		return userActivityRunsList;
	}

	@Override
	public TemporalConditionDto fetchTemporalConditionDetails(String findBy, String findByType) throws CustomException {
		LOGGER.info("INFO: ActivityDaoImpl - fetchTemporalConditionDetails() :: starts");
		TemporalConditionDto temporalConditionDto = null;
		try (Session session = sessionFactory.openSession()) {
			if (findByType.equals(AppConstants.FIND_BY_TYPE_CONDITIONID)) {
				temporalConditionDto = (TemporalConditionDto) session
						.getNamedQuery("TemporalConditionDto.findByConditionId")
						.setInteger(AppEnums.QK_CONDITION_IDENTIFIER.value(), Integer.parseInt(findBy)).uniqueResult();
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityDaoImpl - fetchTemporalConditionDetails()", e);
		}
		LOGGER.info("INFO: ActivityDaoImpl - fetchTemporalConditionDetails() :: ends");
		return temporalConditionDto;
	}

	@Override
	public UserActivitiesRunsDto saveOrUpdateUserActivityRunsDetails(UserActivitiesRunsDto userActivitiesRunsDto,
			String type) throws CustomException {
		LOGGER.info("INFO: ActivityDaoImpl - saveOrUpdateUserActivityRunsDetails() :: starts");
		Transaction transaction = null;
		UserActivitiesRunsDto updateUserActivitiesRuns = null;
		try (Session session = sessionFactory.openSession()) {
			if (userActivitiesRunsDto != null) {
				transaction = session.beginTransaction();

				switch (type) {
					case AppConstants.DB_SAVE:
						session.save(userActivitiesRunsDto);
						break;
					case AppConstants.DB_UPDATE:
						session.update(userActivitiesRunsDto);
						break;
					default:
						break;
				}

				transaction.commit();
				updateUserActivitiesRuns = userActivitiesRunsDto;
			}
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			LOGGER.error("ERROR: ActivityDaoImpl - saveOrUpdateUserActivityRunsDetails()", e);
		}
		LOGGER.info("INFO: ActivityDaoImpl - saveOrUpdateUserActivityRunsDetails() :: ends");
		return updateUserActivitiesRuns;
	}

	@Override
	public int fetchUserActivityRunsCount(String findBy1, String findBy2, String findBy3, String findByType)
			throws CustomException {
		LOGGER.info("INFO: ActivityDaoImpl - fetchUserActivityRunsCount() :: starts");
		Integer count = 0;
		try (Session session = sessionFactory.openSession()) {
			if (findByType.equals(AppConstants.FIND_BY_TYPE_USERID_CONDITIONID)) {
				count = (int) ((long) session.getNamedQuery("UserActivitiesRunsDto.fetchCountByUserIdNConditionId")
						.setInteger(AppEnums.QK_USER_IDENTIFIER.value(), Integer.parseInt(findBy1))
						.setInteger(AppEnums.QK_CONDITION_IDENTIFIER.value(), Integer.parseInt(findBy2))
						.setString(AppEnums.QK_RUN_STATE.value(), AppConstants.RUN_STATE_COMPLETED)
						.setInteger(AppEnums.QK_USER_STUDIES_IDENTIFIER.value(), Integer.parseInt(findBy3))
						.uniqueResult());
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityDaoImpl - fetchUserActivityRunsCount()", e);
		}
		LOGGER.info("INFO: ActivityDaoImpl - fetchUserActivityRunsCount() :: ends");
		return count;
	}

	@Override
	public ActivitiesDto fetchActivitiesDetails(String findBy, String findByType) throws CustomException {
		LOGGER.info("INFO: ActivityDaoImpl - fetchActivitiesDetails() :: starts");
		ActivitiesDto activitiesDto = null;
		try (Session session = sessionFactory.openSession()) {
			if (findByType.equals(AppConstants.FIND_BY_TYPE_ACTIVITY_ID)) {
				activitiesDto = (ActivitiesDto) session.getNamedQuery("ActivitiesDto.findByActivityId")
						.setInteger(AppEnums.QK_ACTIVITY_IDENTIFIER.value(), Integer.parseInt(findBy)).uniqueResult();
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityDaoImpl - fetchActivitiesDetails()", e);
		}
		LOGGER.info("INFO: ActivityDaoImpl - fetchActivitiesDetails() :: ends");
		return activitiesDto;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<QuestionsDto> fetchQuestionsDetailsList(String findBy, String findByType) throws CustomException {
		LOGGER.info("INFO: ActivityDaoImpl - fetchQuestionsDetailsList() :: starts");
		List<QuestionsDto> questionsDtoList = new ArrayList<>();
		try (Session session = sessionFactory.openSession()) {
			if (findByType.equals(AppConstants.FIND_BY_TYPE_ACTIVITY_ID)) {
				questionsDtoList = session.getNamedQuery("QuestionsDto.findByActivityId")
						.setInteger(AppEnums.QK_ACTIVITY_IDENTIFIER.value(), Integer.parseInt(findBy)).list();
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityDaoImpl - fetchQuestionsDetailsList()", e);
		}
		LOGGER.info("INFO: ActivityDaoImpl - fetchQuestionsDetailsList() :: ends");
		return questionsDtoList;
	}

	@Override
	public QuestionsDto fetchQuestionDetails(String findBy, String findByType) throws CustomException {
		LOGGER.info("INFO: ActivityDaoImpl - fetchQuestionDetails() :: starts");
		QuestionsDto questionsDto = null;
		try (Session session = sessionFactory.openSession()) {
			if (findByType.equals(AppConstants.FIND_BY_TYPE_QUESTION_ID)) {
				questionsDto = (QuestionsDto) session.getNamedQuery("QuestionsDto.findByQuestionId")
						.setInteger(AppEnums.QK_QUESTION_IDENTIFIER.value(), Integer.parseInt(findBy)).uniqueResult();
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityDaoImpl - fetchQuestionDetails()", e);
		}
		LOGGER.info("INFO: ActivityDaoImpl - fetchQuestionDetails() :: ends");
		return questionsDto;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<QuestionChoiceDto> fetchQuestionsChoiceDetailsList(String findBy, String findByType)
			throws CustomException {
		LOGGER.info("INFO: ActivityDaoImpl - fetchQuestionsChoiceDetailsList() :: starts");
		List<QuestionChoiceDto> questionsChoiceDtoList = new ArrayList<>();
		try (Session session = sessionFactory.openSession()) {
			if (findByType.equals(AppConstants.FIND_BY_TYPE_QUESTION_ID)) {
				questionsChoiceDtoList = session.getNamedQuery("QuestionChoiceDto.findByQuestionId")
						.setInteger(AppEnums.QK_QUESTION_IDENTIFIER.value(), Integer.parseInt(findBy)).list();
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityDaoImpl - fetchQuestionsChoiceDetailsList()", e);
		}
		LOGGER.info("INFO: ActivityDaoImpl - fetchQuestionsChoiceDetailsList() :: ends");
		return questionsChoiceDtoList;
	}

	@Override
	public QuestionChoiceDto fetchQuestionChoiceDetails(String findBy, String findByType) throws CustomException {
		LOGGER.info("INFO: ActivityDaoImpl - fetchQuestionChoiceDetails() :: starts");
		QuestionChoiceDto questionChoiceDto = null;
		try (Session session = sessionFactory.openSession()) {
			if (findByType.equals(AppConstants.FIND_BY_TYPE_CHOICE_ID)) {
				questionChoiceDto = (QuestionChoiceDto) session.getNamedQuery("QuestionChoiceDto.findByChoicesId")
						.setInteger(AppEnums.QK_CHOICES_IDENTIFIER.value(), Integer.parseInt(findBy)).uniqueResult();
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityDaoImpl - fetchQuestionChoiceDetails()", e);
		}
		LOGGER.info("INFO: ActivityDaoImpl - fetchQuestionChoiceDetails() :: ends");
		return questionChoiceDto;
	}

	@Override
	public UserActivitiesRunsDto fetchUserActivityRunDetailsCriteria(String findBy1, Map<String, Object> criteriaMap,
			String findByType) throws CustomException {
		LOGGER.info("INFO: ActivityDaoImpl - fetchUserActivityRunDetailsCriteria() :: starts");
		UserActivitiesRunsDto userActivitiesRunsDto = null;
		try (Session session = sessionFactory.openSession()) {
			switch (findByType) {
				case AppConstants.FIND_BY_TYPE_USERID_ACTIVITYID_RUNID:
					userActivitiesRunsDto = (UserActivitiesRunsDto) session
							.getNamedQuery("UserActivitiesRunsDto.fetchByUserIdNConditionIdNActivityRunId")
							.setInteger(AppEnums.QK_USER_IDENTIFIER.value(), Integer.parseInt(findBy1))
							.setInteger(AppEnums.QK_CONDITION_IDENTIFIER.value(),
									(int) criteriaMap.get(AppEnums.QK_CONDITION_IDENTIFIER.value()))
							.setInteger(AppEnums.QK_ACTIVITY_RUN_IDENTIFIER.value(),
									(int) criteriaMap.get(AppEnums.QK_ACTIVITY_RUN_IDENTIFIER.value()))
							.setInteger(AppEnums.QK_USER_STUDIES_IDENTIFIER.value(),
									(int) criteriaMap.get(AppEnums.QK_USER_STUDIES_IDENTIFIER.value()))
							.uniqueResult();
					break;
				case AppConstants.FIND_BY_TYPE_USERID_CONDITIONID:
					userActivitiesRunsDto = (UserActivitiesRunsDto) session
							.getNamedQuery("UserActivitiesRunsDto.fetchByUserIdNConditionId")
							.setInteger(AppEnums.QK_USER_IDENTIFIER.value(), Integer.parseInt(findBy1))
							.setInteger(AppEnums.QK_CONDITION_IDENTIFIER.value(),
									(int) criteriaMap.get(AppEnums.QK_CONDITION_IDENTIFIER.value()))
							.setInteger(AppEnums.QK_USER_STUDIES_IDENTIFIER.value(),
									(int) criteriaMap.get(AppEnums.QK_USER_STUDIES_IDENTIFIER.value()))
							.uniqueResult();
					break;
				default:
					break;
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityDaoImpl - fetchUserActivityRunDetailsCriteria()", e);
		}
		LOGGER.info("INFO: ActivityDaoImpl - fetchUserActivityRunDetailsCriteria() :: ends");
		return userActivitiesRunsDto;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RewardLevelsDto> fetchRewardLevelsList() throws CustomException {
		LOGGER.info("INFO: ActivityDaoImpl - fetchRewardLevelsList() :: starts");
		List<RewardLevelsDto> rewardLevelsList = new ArrayList<>();
		try (Session session = sessionFactory.openSession()) {
			rewardLevelsList = session.getNamedQuery("RewardLevelsDto.findAll").list();
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityDaoImpl - fetchRewardLevelsList()", e);
		}
		LOGGER.info("INFO: ActivityDaoImpl - fetchRewardLevelsList() :: ends");
		return rewardLevelsList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ActivityConditionDto> fetchActivityConditionDetailsList(String findBy, String findByType, String timeZone) {
		LOGGER.info("INFO: ActivityDaoImpl - fetchActivityConditionDetailsList() :: starts");
		List<ActivityConditionDto> activityConditionsList = new ArrayList<>();
		try (Session session = sessionFactory.openSession()) {
			if (findByType.equals(AppConstants.FIND_BY_TYPE_ACTIVITY_SUB_TYPE)) {
				activityConditionsList = session.getNamedQuery("ActivityConditionDto.findAllByActivitySubTypeNEndDate")
						.setString(AppEnums.QK_ACTIVITY_SUB_TYPE.value(), findBy)
						.setString(AppEnums.QK_END_DATE.value(), AppUtil.getCurrentDateTime(timeZone)).list();
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityDaoImpl - fetchActivityConditionDetailsList()", e);
		}
		LOGGER.info("INFO: ActivityDaoImpl - fetchActivityConditionDetailsList() :: ends");
		return activityConditionsList;
	}

	@Override
	public UserActivitiesRunsDto fetchUserActivityRunsDetails(String findBy1, String findBy2, String findBy3,
			String findByType) {
		LOGGER.info("INFO: ActivityDaoImpl - fetchUserActivityRunsDetails() :: starts");
		UserActivitiesRunsDto userActivitiesRunsDto = null;
		try (Session session = sessionFactory.openSession()) {
			userActivitiesRunsDto = (UserActivitiesRunsDto) session
					.getNamedQuery("UserActivitiesRunsDto.fetchAllByUserIdNStudiesIdNConditionId")
					.setInteger(AppEnums.QK_USER_IDENTIFIER.value(), Integer.parseInt(findBy1))
					.setInteger(AppEnums.QK_USER_STUDIES_IDENTIFIER.value(), Integer.parseInt(findBy2))
					.setInteger(AppEnums.QK_CONDITION_IDENTIFIER.value(), Integer.parseInt(findBy3)).setMaxResults(1)
					.uniqueResult();
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityDaoImpl - fetchUserActivityRunsDetails()", e);
		}
		LOGGER.info("INFO: ActivityDaoImpl - fetchUserActivityRunsDetails() :: ends");
		return userActivitiesRunsDto;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ActivitiesDto> fetchAllActiveActivities() {
		LOGGER.info("INFO: ActivityDaoImpl - fetchAllActiveActivities() :: starts");
		List<ActivitiesDto> activitiesList = new ArrayList<>();
		try (Session session = sessionFactory.openSession()) {
			activitiesList = session.getNamedQuery("ActivitiesDto.findAll").list();
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityDaoImpl - fetchAllActiveActivities()", e);
		}
		LOGGER.info("INFO: ActivityDaoImpl - fetchAllActiveActivities() :: ends");
		return activitiesList;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<PushNotificationBean> getUserActivityRuns(String userIds, String currentDateTime){
		LOGGER.info("INFO: ActivityDaoImpl - getUserActivityRuns() :: starts");
		List<PushNotificationBean> pushNotificationBeanList = null;
		String queryString = "";
		List<Object[]> objectList = new ArrayList<>();
		try(Session session = sessionFactory.openSession()) {
			queryString = " select a.user_id, b.activity_id, a.activity_run_id, c.device_type, c.device_token "+
					" , b.activity_condition_name, a.user_activity_run_id, TIMESTAMPDIFF(MINUTE, '"+currentDateTime+"', a.run_ends_on) from user_activities_runs a, activity_condition b,auth_info c where a.user_id in ( "+userIds+" ) "+
					" and c.user_id = a.user_id "+
		            " and a.condition_id = b.condition_id and a.run_state != 'completed' "+
					" and a.expire_notification_sent = false and b.activity_sub_type IN ('triggered','scheduled') "+
		            " and TIMESTAMPDIFF(MINUTE, '"+currentDateTime+"', a.run_ends_on) < 11 and TIMESTAMPDIFF(MINUTE, '"+currentDateTime+"', a.run_ends_on) > 0 "+
					" and a.run_starts_on < '"+currentDateTime+"' and a.run_ends_on > '"+currentDateTime+"' ";
			/*objectList = session.createSQLQuery(queryString).setString(0, currentDateTime)
							   .setString(1, currentDateTime)
							   .setString(2, currentDateTime)
							   .setString(3, currentDateTime).list();*/
			objectList = session.createSQLQuery(queryString).list();
			if(null != objectList && !objectList.isEmpty()) {
				pushNotificationBeanList = new ArrayList<>();
				for(Object[] obj : objectList) {
					PushNotificationBean pushNotificationBean = new PushNotificationBean();
					pushNotificationBean.setUserId(null != obj[0] ? String.valueOf((Integer)obj[0]) : "0");
					pushNotificationBean.setActivityId(null != obj[1] ? String.valueOf((Integer)obj[1]) : "0");
					pushNotificationBean.setCurrentRunId(null != obj[2] ? String.valueOf((Integer)obj[2]) : "0");
					pushNotificationBean.setDeviceType(null != obj[3] ? String.valueOf(obj[3]) : "");
					pushNotificationBean.setDeviceToken(null != obj[4] ? String.valueOf(obj[4]) : "");
					pushNotificationBean.setActivityName(null != obj[5] ? String.valueOf(obj[5]) : "");
					pushNotificationBean.setUserActivityRunId(null != obj[6] ? String.valueOf((Integer)obj[6]) : "0");
					pushNotificationBean.setExpiryMinutes(null != obj[7] ? String.valueOf((BigInteger)obj[7]) : "0");
					pushNotificationBeanList.add(pushNotificationBean);
				}
			}
		}catch (Exception e) {
			LOGGER.error("ERROR: ActivityDaoImpl - getUserActivityRuns()", e);
		}
		LOGGER.info("INFO: ActivityDaoImpl - getUserActivityRuns() :: ends");
		return pushNotificationBeanList;
	}
	
	@Override
	public void saveOrUpdateUserActivityRunsDetail(String userActivityRunId) throws CustomException {
		LOGGER.info("INFO: ActivityDaoImpl - saveOrUpdateUserActivityRunsDetail() :: starts");
		Transaction transaction = null;
		UserActivitiesRunsDto updateUserActivitiesRuns = null;
		String queryString = "";
		try (Session session = sessionFactory.openSession()) {
			queryString = " FROM UserActivitiesRunsDto UARDBO WHERE UARDBO.userActivityRunId = ? ";
			updateUserActivitiesRuns = (UserActivitiesRunsDto) session.createQuery(queryString).setInteger(0, Integer.parseInt(userActivityRunId)).uniqueResult();
			if(null != updateUserActivitiesRuns) {
				transaction = session.beginTransaction();
				updateUserActivitiesRuns.setExpireNotificationSent(true);
				session.update(updateUserActivitiesRuns);
				transaction.commit();
			}
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			LOGGER.error("ERROR: ActivityDaoImpl - saveOrUpdateUserActivityRunsDetail()", e);
		}
		LOGGER.info("INFO: ActivityDaoImpl - saveOrUpdateUserActivityRunsDetail() :: ends");
	}
	
	@Override
	public HarvardIAQ fetchLass4USesorData(String lass4UId, String timeZone) {
		LOGGER.info("INFO: ActivityDaoImpl - fetchLass4USesorData() :: starts");
		String currentDateTime = AppUtil.getCurrentDateTime("UTC");
		StringBuilder sb = new StringBuilder();
	    Object[] lass4USensorData = null; 
	    HarvardIAQ harvardIAQ = null;
	//  String userTimezoneCurrentDateTime = AppUtil.getFormattedDateByTimeZone(currentDateTime, AppConstants.SDF_DATE_TIME_FORMAT, AppConstants.SDF_DATE_TIME_FORMAT, timeZone);
	   
		try (Session session = sessionFactory.openSession()) {
			/*queryString = " SELECT TIMESTAMPDIFF(MINUTE,SUBSTR(from_unixtime(b.timestamp),1,19),"+currentDateTime+")"
					+ ",b.* FROM hb_sensor_data b WHERE b.device_id = "+lass4UId
					+ " AND TIMESTAMPDIFF(MINUTE,SUBSTR(from_unixtime(b.timestamp),1,19),"+currentDateTime+") <= 15  "
					+ " ORDER BY b.timestamp DESC LIMIT 1 ";*/ 
			
			String queryString = sb
					.append(" SELECT b.app, TRUNCATE(b.s_g8,2), TRUNCATE(b.s_t0,2), TRUNCATE(b.s_d0,2), b.timestamp, TRUNCATE(b.s_d1,2), TRUNCATE(b.s_h0,2), TRUNCATE(b.s_d2,2), ")
					.append(" DATE(from_unixtime(b.timestamp)), SUBSTR(TIME(from_unixtime(b.timestamp)),1,8), b.s_n0,TRUNCATE(b.s_l0,2)")
					.append(" FROM hb_sensor_data b WHERE b.device_id = '").append(lass4UId)
					.append("' AND TIMESTAMPDIFF(MINUTE,SUBSTR(from_unixtime(b.timestamp),1,19), '")
					.append(currentDateTime).append("' ) <= 15 ").append(" ORDER BY b.timestamp DESC LIMIT 1 ")
					.toString();
			lass4USensorData = (Object[]) session.createSQLQuery(queryString).uniqueResult();
			if(null != lass4USensorData) {
				harvardIAQ = new HarvardIAQ();
				harvardIAQ.setApp(null != lass4USensorData[0] ? String.valueOf(lass4USensorData[0]) : "");
				harvardIAQ.setsG8e(null != lass4USensorData[1] ? Float.parseFloat(String.valueOf(lass4USensorData[1])) : 0F);
				harvardIAQ.setTemperature(null != lass4USensorData[2] ? Float.parseFloat(String.valueOf(lass4USensorData[2])) : 0F);
				harvardIAQ.setPm25(null != lass4USensorData[3] ? Float.parseFloat(String.valueOf(lass4USensorData[3])) : 0F);
				harvardIAQ.setTimestamp(null != lass4USensorData[4] ? String.valueOf(lass4USensorData[4]) : "");
				harvardIAQ.setsD1(null != lass4USensorData[5] ? Float.parseFloat(String.valueOf(lass4USensorData[5])) : 0F);
				harvardIAQ.setRelativeHumidity(null != lass4USensorData[6] ? Float.parseFloat(String.valueOf(lass4USensorData[6])) : 0F);
				harvardIAQ.setsD2(null != lass4USensorData[7] ? Float.parseFloat(String.valueOf(lass4USensorData[7])) : 0F);
				harvardIAQ.setDate(null != lass4USensorData[8] ? String.valueOf(lass4USensorData[8]) : "");
				harvardIAQ.setTime(null != lass4USensorData[9] ? String.valueOf(lass4USensorData[9]) : "");
				harvardIAQ.setS_n0(null != lass4USensorData[10] ? String.valueOf(lass4USensorData[10]) : "");
				harvardIAQ.setLight(null != lass4USensorData[11] ? Float.parseFloat(String.valueOf(lass4USensorData[11])) : 0F);
			}else {
				harvardIAQ = new HarvardIAQ();
				harvardIAQ.setApp("Test");
				harvardIAQ.setsG8e(10F);
				harvardIAQ.setTemperature(30F);
				harvardIAQ.setPm25(0F);
				harvardIAQ.setTimestamp(currentDateTime);
				harvardIAQ.setsD1(0F);
				harvardIAQ.setRelativeHumidity( 0F);
				harvardIAQ.setsD2(0F);
				harvardIAQ.setDate(AppUtil.getCurrentDate());
				harvardIAQ.setTime(AppUtil.getCurrentDateTime());
				harvardIAQ.setS_n0("");
				harvardIAQ.setLight(0F);
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityDaoImpl - fetchLass4USesorData()", e);
		}
		LOGGER.info("INFO: ActivityDaoImpl - fetchLass4USesorData() :: ends");
		return harvardIAQ;
	}
	
/*	
	@Override
	public HarvardIAQ fetchLass4USesorData(String lass4UId) {
		LOGGER.info("INFO: ActivityDaoImpl - fetchAllActiveActivities() :: starts");
		String currentDateTime = AppUtil.getCurrentDateTime();
		StringBuilder sb = new StringBuilder();
	    Object[] lass4USensorData = null; 
	    HarvardIAQ harvardIAQ = null;
		try (Session session = sessionFactory.openSession()) {
			queryString = " SELECT TIMESTAMPDIFF(MINUTE,SUBSTR(from_unixtime(b.timestamp),1,19),"+currentDateTime+")"
					+ ",b.* FROM hb_sensor_data b WHERE b.device_id = "+lass4UId
					+ " AND TIMESTAMPDIFF(MINUTE,SUBSTR(from_unixtime(b.timestamp),1,19),"+currentDateTime+") <= 15  "
					+ " ORDER BY b.timestamp DESC LIMIT 1 "; 
			
			String queryString = sb
					.append(" SELECT b.app, TRUNCATE(b.s_g8,2), TRUNCATE(b.s_t0,2), TRUNCATE(b.s_d0,2), b.timestamp, TRUNCATE(b.s_d1,2), TRUNCATE(b.s_h0,2), TRUNCATE(b.s_d2,2), ")
					.append(" DATE(from_unixtime(b.timestamp)), SUBSTR(TIME(from_unixtime(b.timestamp)),1,8), b.s_n0,TRUNCATE(b.s_l0,2)")
					.append(" FROM hb_sensor_data b WHERE b.device_id = '").append(lass4UId)
					.append("' AND TIMESTAMPDIFF(MINUTE,SUBSTR(from_unixtime(b.timestamp),1,19), '")
					.append(currentDateTime).append("' ) <= 15 ").append(" ORDER BY b.timestamp DESC LIMIT 1 ")
					.toString();
			lass4USensorData = (Object[]) session.createSQLQuery(queryString).uniqueResult();
			if(null != lass4USensorData) {
				harvardIAQ = new HarvardIAQ();
				harvardIAQ.setApp(null != lass4USensorData[0] ? String.valueOf(lass4USensorData[0]) : "");
				harvardIAQ.setsG8e(null != lass4USensorData[1] ? Float.parseFloat(String.valueOf(lass4USensorData[1])) : 0F);
				harvardIAQ.setTemperature(null != lass4USensorData[2] ? Float.parseFloat(String.valueOf(lass4USensorData[2])) : 0F);
				harvardIAQ.setPm25(null != lass4USensorData[3] ? Float.parseFloat(String.valueOf(lass4USensorData[3])) : 0F);
				harvardIAQ.setTimestamp(null != lass4USensorData[4] ? String.valueOf(lass4USensorData[4]) : "");
				harvardIAQ.setsD1(null != lass4USensorData[5] ? Float.parseFloat(String.valueOf(lass4USensorData[5])) : 0F);
				harvardIAQ.setRelativeHumidity(null != lass4USensorData[6] ? Float.parseFloat(String.valueOf(lass4USensorData[6])) : 0F);
				harvardIAQ.setsD2(null != lass4USensorData[7] ? Float.parseFloat(String.valueOf(lass4USensorData[7])) : 0F);
				harvardIAQ.setDate(null != lass4USensorData[8] ? String.valueOf(lass4USensorData[8]) : "");
				harvardIAQ.setTime(null != lass4USensorData[9] ? String.valueOf(lass4USensorData[9]) : "");
				harvardIAQ.setS_n0(null != lass4USensorData[10] ? String.valueOf(lass4USensorData[10]) : "");
				harvardIAQ.setLight(null != lass4USensorData[11] ? Float.parseFloat(String.valueOf(lass4USensorData[11])) : 0F);
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityDaoImpl - fetchAllActiveActivities()", e);
		}
		LOGGER.info("INFO: ActivityDaoImpl - fetchAllActiveActivities() :: ends");
		return harvardIAQ;
	}*/
	
	@Override
	public String isDeviceIdExist(String deviceId) {
		LOGGER.info("INFO: ActivityDaoImpl - isDeviceIdExist() :: starts");
		StringBuilder sb = new StringBuilder();
	    Object[] registryObj = null; 
	    String message = AppConstants.FAILURE;
		try (Session session = sessionFactory.openSession()) {
			String queryString = sb
					.append(" SELECT a.device_id, a.last_ping FROM hb_sensor_registry a WHERE a.device_id = ")
					.append("'"+deviceId+"'")
					.toString();
			registryObj = (Object[]) session.createSQLQuery(queryString).uniqueResult();
			if(null != registryObj) {
				message = AppConstants.SUCCESS;
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityDaoImpl - isDeviceIdExist()", e);
		}
		LOGGER.info("INFO: ActivityDaoImpl - isDeviceIdExist() :: ends");
		return message;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean isLass4USensorDataExist(String lass4UId) {
		LOGGER.info("INFO: ActivityDaoImpl - isLass4USensorDataExist() :: starts");
		StringBuilder sb = new StringBuilder();
		boolean getData = false;
		List<Object[]> lass4USensorDataList = null; 
		try (Session session = sessionFactory.openSession()) {
			String queryString = sb
					.append(" SELECT * FROM hb_sensor_data b WHERE b.device_id = '").append(lass4UId+"'")
					.toString();
			lass4USensorDataList = session.createSQLQuery(queryString).list();
			if(null != lass4USensorDataList && !lass4USensorDataList.isEmpty()) {
				getData = true;
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityDaoImpl - isLass4USensorDataExist()", e);
		}
		LOGGER.info("INFO: ActivityDaoImpl - isLass4USensorDataExist() :: ends");
		return getData;
	}
	
	@Override
	public boolean insertSensorData(String timestamp,String app,String device_id,String s_g8,String s_t0,
			String s_d0,String s_d1,String s_h0,String s_d2,String s_n0,String s_l0) {
		LOGGER.info("INFO: ActivityDaoImpl - insertSensorData() :: starts");
		StringBuilder sb = new StringBuilder();
		boolean addData = false;
		int count = 0;
		Transaction transaction = null;
		try (Session session = sessionFactory.openSession()) {
			transaction = session.beginTransaction();
			String queryString = sb
					.append(" INSERT INTO hb_sensor_data(timestamp,app,device_id,s_g8,s_t0,s_d0,s_d1,s_h0,s_d2,s_n0,s_l0) VALUES( '")
					.append(timestamp+"','"+app+"','"+device_id+"','"+s_g8+"','"+s_t0+"','"+s_d0+"','"+s_d1+"','"+s_h0+"','"+s_d2+"','"+s_n0+"','"+s_l0+"')")
					.toString();
			count = session.createSQLQuery(queryString).executeUpdate();
			if(count > 0) {
				addData = true;
			}
			transaction.commit();
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityDaoImpl - insertSensorData()", e);
		}
		LOGGER.info("INFO: ActivityDaoImpl - insertSensorData() :: ends");
		return addData;
	}
	
	 @SuppressWarnings("unchecked")
	@Override
	public boolean checkUserRelatedToCorrectGroup(int userId, int conditionId, int groupId, Map<Integer, List<GroupUsersInfoDto>> groupUsersMap ) throws CustomException {
		LOGGER.info("INFO: ActivityDaoImpl - fetchTemporalConditionDetails() :: starts");
		boolean mapped = false;
		try (Session session = sessionFactory.openSession()) {
			  
			List<GroupUsersInfoDto> groupInfoList = groupUsersMap.get(userId);
			
			   if(groupInfoList != null && !groupInfoList.isEmpty()) {
				 List<Integer> groupIds =  groupInfoList.stream().map(GroupUsersInfoDto::getGroupId).collect(Collectors.toList());
			     List<String> conditionList = session.createSQLQuery(" select CAST(a.condition_id as CHAR(50)) from activity_group a WHERE a.group_id IN (:groupIdVal)")
						                      .setParameterList("groupIdVal", groupIds).list();
					if(null != conditionList && !conditionList.isEmpty()) {
						if(conditionList.contains(String.valueOf(conditionId))) {
							mapped = true;
						}
					}
			   }else {
					List<String> conditionList = session.createSQLQuery(" select CAST(a.condition_id as CHAR(50)) from activity_group a WHERE a.group_id = "+groupId).list();
					if(null != conditionList && !conditionList.isEmpty()) {
						if(conditionList.contains(String.valueOf(conditionId))) {
							mapped = true;
						}
				}
			   }
				
		
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityDaoImpl - fetchTemporalConditionDetails()", e);
		}
		LOGGER.info("INFO: ActivityDaoImpl - fetchTemporalConditionDetails() :: ends");
		return mapped;
	}

	@Override
	public AppVersionInfo getAppVersionInfo() {
		AppVersionInfo appVersionInfo=null;
		try (Session session = sessionFactory.openSession()) {
			
			appVersionInfo = (AppVersionInfo) session
					.getNamedQuery("AppVersionInfo.findAll")
					.uniqueResult();
			
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityDaoImpl - validateActivityId()", e);
		}		return appVersionInfo;
	}

	@Override
	public KeywordsResponse getKeywordList(String language) {
		LOGGER.info("INFO: ActivityDaoImpl - getKeywordList() :: starts");
		KeywordsResponse choiceBean = null;
		try (Session session = sessionFactory.openSession()){
			if(StringUtils.isNoneBlank(language)) {
				@SuppressWarnings("unchecked")
				List<String> wordList =	session.createSQLQuery("select cast(a.choice_value as char) from keyword_choice a where a.language =:language ")
						.setParameter("language", language)
						.list();
				if(null != wordList && !wordList.isEmpty()) {
					KeywordsBean keywordsBean = new KeywordsBean();
					keywordsBean.setKeyValue(wordList);
					choiceBean = new KeywordsResponse().setKeywords(keywordsBean).setLanguage(language);
				}else {
					choiceBean = new KeywordsResponse().setLanguage(language);
				}
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityDaoImpl - getKeywordList()", e);
		}
		LOGGER.info("INFO: ActivityDaoImpl - getKeywordList() :: ends");
		return choiceBean;
	}

	@Override
	public String getUserTimeZone(String accessToken) {
	LOGGER.info("INFO: ActivityDaoImpl - getUserTimeZone() :: starts");
	String timeZone = "";
	try (Session session = sessionFactory.openSession()) {
			if(StringUtils.isNotBlank(accessToken)) {
				timeZone =  (String) session.createSQLQuery("select cast(b.time_zone  as CHAR(50)) from auth_info a , "
						+ "user_details b where a.user_id = b.user_id and a.auth_key = :authKey")
				       .setParameter("authKey", accessToken).uniqueResult();
		  }
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityDaoImpl - getUserTimeZone()", e);
		}	
	LOGGER.info("INFO: ActivityDaoImpl - getUserTimeZone() :: ends");
		return timeZone;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<UserActivitiesDto> fetchUserScheduledActivityDetailsList(String findBy1, String findBy2, String findByType, String timeZone)
			throws CustomException {
		LOGGER.info("INFO: ActivityDaoImpl - fetchUserActivityDetailsList() :: starts");
		List<UserActivitiesDto> userActivityList = new ArrayList<>();
		try (Session session = sessionFactory.openSession()) {
			if (findByType.equals(AppConstants.FIND_BY_TYPE_USERID_STUDYID_ACTIVITY_SUB_TYPE)) {
				userActivityList = session.getNamedQuery("UserActivitiesDto.findByUserIdNUserStudiesIdNActivitySubType")
						.setInteger(AppEnums.QK_USER_IDENTIFIER.value(), Integer.parseInt(findBy1))
						.setInteger(AppEnums.QK_USER_STUDIES_IDENTIFIER.value(), Integer.parseInt(findBy2))
						.setString(AppEnums.QK_ACTIVITY_SUB_TYPE.value(), AppConstants.ACTIVITY_SUB_TYPE_SCHEDULED)
						.setString(AppEnums.QK_END_DATE.value(), AppUtil.getCurrentUserDate(timeZone)).list();
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityDaoImpl - fetchUserActivityDetailsList()", e);
		}
		LOGGER.info("INFO: ActivityDaoImpl - fetchUserActivityDetailsList() :: ends");
		return userActivityList;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<PushNotificationBean> getExpiryUserActivities(String userIds){
		LOGGER.info("INFO: ActivityDaoImpl - getUserActivities() :: starts");
		List<PushNotificationBean> pushNotificationBeanList = null;
		String queryString = "";
		List<Object[]> objectList = new ArrayList<>();
		try(Session session = sessionFactory.openSession()) {
			queryString = " select a.user_id,a.condition_id,a.user_activity_id,a.activity_run_id,a.activity_status from user_activities a "
					+ "where a.user_id in ( "+userIds+" ) and a.activity_status != 'completed' and a.expire_notification_sent != true";
			
			objectList = session.createSQLQuery(queryString).list();
			if(null != objectList && !objectList.isEmpty()) {
				pushNotificationBeanList = new ArrayList<>();
				for(Object[] obj : objectList) {
					PushNotificationBean pushNotificationBean = new PushNotificationBean();
					pushNotificationBean.setUserId(null != obj[0] ? String.valueOf((Integer)obj[0]) : "0");
					pushNotificationBean.setConditionId(null != obj[1] ? String.valueOf((Integer)obj[1]) : "0");
					pushNotificationBean.setUserActivityRunId(null != obj[2] ? String.valueOf((Integer)obj[2]) : "0");
					pushNotificationBean.setCurrentRunId(null != obj[3] ? String.valueOf((Integer)obj[3]) : "0");
					pushNotificationBean.setActivityStatus(null != obj[4] ? String.valueOf(obj[4]) : "");
					pushNotificationBeanList.add(pushNotificationBean);
				}
			}
		}catch (Exception e) {
			LOGGER.error("ERROR: ActivityDaoImpl - getUserActivities()", e);
		}
		LOGGER.info("INFO: ActivityDaoImpl - getUserActivities() :: ends");
		return pushNotificationBeanList;
	}
	
	@Override
	public void saveOrUpdateUserActivityDetail(String userActivityId) throws CustomException {
		LOGGER.info("INFO: ActivityDaoImpl - saveOrUpdateUserActivityDetail() :: starts");
		Transaction transaction = null;
		UserActivitiesDto updateUserActivities = null;
		String queryString = "";
		try (Session session = sessionFactory.openSession()) {
			queryString = " FROM UserActivitiesDto UARDBO WHERE UARDBO.userActivityId = ? ";
			updateUserActivities = (UserActivitiesDto) session.createQuery(queryString).setInteger(0, Integer.parseInt(userActivityId)).uniqueResult();
			if(null != updateUserActivities) {
				transaction = session.beginTransaction();
				updateUserActivities.setExpireNotificationSent(true);
				session.update(updateUserActivities);
				transaction.commit();
			}
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			LOGGER.error("ERROR: ActivityDaoImpl - saveOrUpdateUserActivityDetail()", e);
		}
		LOGGER.info("INFO: ActivityDaoImpl - saveOrUpdateUserActivityDetail() :: ends");
	}
	
	//Added by fathima for crat activites
	
	@SuppressWarnings("unchecked")
	@Override
	public List<CratWordsUserMapDto> fetchCratWordsId(String userId) throws CustomException {
		LOGGER.info("INFO: ActivityDaoImpl - fetchCratWordsId() :: starts");
		List<CratWordsUserMapDto> cratWordsUserMapList = new ArrayList<>();
		try (Session session = sessionFactory.openSession()) {
			if (null != userId && !userId.isEmpty()) {
				cratWordsUserMapList = session.getNamedQuery("CratWordsUserMapDto.findWordsId")
						.setInteger(AppEnums.QK_USER_IDENTIFIER.value(), Integer.parseInt(userId)).list();
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityDaoImpl - fetchCratWordsId()", e);
		}
		LOGGER.info("INFO: ActivityDaoImpl - fetchCratWordsId() :: ends");
		return cratWordsUserMapList;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<CratWordsDto> fetchCratWords() throws CustomException {
		LOGGER.info("INFO: ActivityDaoImpl - fetchCratWords() :: starts");
		List<CratWordsDto> cratWordsList = new ArrayList<>();
		try (Session session = sessionFactory.openSession()) {
			cratWordsList = session.getNamedQuery("CratWordsDto.findWords").list();
		} catch (Exception e) {
			LOGGER.error("ERROR: ActivityDaoImpl - fetchCratWords()", e);
		}
		LOGGER.info("INFO: ActivityDaoImpl - fetchCratWords() :: ends");
		return cratWordsList;
	}
	
	@Override
	public CratWordsUserMapDto saveCratWordsUserMap(CratWordsUserMapDto cratWordsUserMapDto) throws CustomException {
		LOGGER.info("INFO: ActivityDaoImpl - saveCratWordsUserMap() :: starts");
		Transaction transaction = null;
		try (Session session = sessionFactory.openSession()) {
			if (cratWordsUserMapDto != null) {
				transaction = session.beginTransaction();

				session.save(cratWordsUserMapDto);

				transaction.commit();
			}
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			LOGGER.error("ERROR: ActivityDaoImpl - saveCratWordsUserMap()", e);
		}
		LOGGER.info("INFO: ActivityDaoImpl - saveCratWordsUserMap() :: ends");
		return cratWordsUserMapDto;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void deleteCratWordsUserMap(String userId) throws CustomException {
		LOGGER.info("INFO: ActivityDaoImpl - deleteCratWordsUserMap() :: starts");
		Transaction transaction = null;
		List<CratWordsUserMapDto> cratWordsList = new ArrayList<>();
		try (Session session = sessionFactory.openSession()) {
			cratWordsList = session.getNamedQuery("CratWordsUserMapDto.findWordsId")
					.setInteger(AppEnums.QK_USER_IDENTIFIER.value(), Integer.parseInt(userId)).list();
			if (cratWordsList != null) {
				for(CratWordsUserMapDto dto : cratWordsList) {
					transaction = session.beginTransaction();
					session.delete(dto);
					transaction.commit();
				}
				
			}
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			LOGGER.error("ERROR: ActivityDaoImpl - deleteCratWordsUserMap()", e);
		}
		LOGGER.info("INFO: ActivityDaoImpl - deleteCratWordsUserMap() :: ends");
		
	}
	
}
