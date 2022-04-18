package com.gba.ws.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.gba.ws.exception.CustomException;
import com.gba.ws.model.AuthInfoDto;
import com.gba.ws.model.EnrollmentTokensDto;
import com.gba.ws.model.FitbitLass4UDataDto;
import com.gba.ws.model.FitbitLogDto;
import com.gba.ws.model.FitbitUserInfoDto;
import com.gba.ws.model.HeartRateDto;
import com.gba.ws.model.SleepDto;
import com.gba.ws.model.StepsDto;
import com.gba.ws.model.UserDto;
import com.gba.ws.util.AppConstants;
import com.gba.ws.util.AppEnums;
import com.gba.ws.util.AppUtil;
//helloworld
/**
 * Implements {@link UserDao} interface.
 * 
 * @author Mohan
 * @createdOn Jan 9, 2018 10:52:36 AM
 */
@Repository
public class UserDaoImpl implements UserDao {

	private static final Logger LOGGER = Logger.getLogger(UserDaoImpl.class);

	@Autowired
	private SessionFactory sessionFactory;

	/**
	 * @author Mohan
	 * @param sessionFactory
	 *            the {@link SessionFactory}
	 */
	public UserDaoImpl(SessionFactory sessionFactory) {
		super();
		this.sessionFactory = sessionFactory;
	}

	@Override
	public void generateEnrollmentTokens() throws CustomException {
		LOGGER.info("INFO: UserDaoImpl - generateEnrollmentTokens() :: starts");
		List<EnrollmentTokensDto> enrollmentTokensList = null;
		Session session = null;
		Transaction transaction = null;
		try {
			session = sessionFactory.openSession();
			enrollmentTokensList = AppUtil.readXLSXFile(
					"D:/MyProjects/Current Projects/HSPH-GBA (Global Groups App)/GBA_Enrollment_Tokens.xlsx");
			if (enrollmentTokensList != null && !enrollmentTokensList.isEmpty()) {
				transaction = session.beginTransaction();
				for (int i = 1; i <= enrollmentTokensList.size(); i++) {
					if ((i % 30) == 0) {
						transaction.commit();
						session.close();

						session = sessionFactory.openSession();
						transaction = session.beginTransaction();
					}
					session.save(enrollmentTokensList.get(i - 1));
				}
				transaction.commit();
			}
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			LOGGER.error("ERROR: UserDaoImpl - generateEnrollmentTokens()", e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
		LOGGER.info("INFO: UserDaoImpl - generateEnrollmentTokens() :: ends");
	}

	@Override
	public UserDto fetchUserDetails(String findBy, String findByType) throws CustomException {
		LOGGER.info("INFO: UserDaoImpl - fetchUserDetails() :: starts");
		UserDto user = null;
		try (Session session = sessionFactory.openSession()) {
			switch (findByType) {
				case AppConstants.FIND_BY_TYPE_USERID:
					user = (UserDto) session.getNamedQuery("UserDto.findByUserId")
							.setInteger(AppEnums.QK_USER_IDENTIFIER.value(), Integer.parseInt(findBy)).uniqueResult();
					break;
				case AppConstants.FIND_BY_TYPE_EMAIL:
					user = (UserDto) session.getNamedQuery("UserDto.findByEmail")
							.setString(AppEnums.QK_EMAIL.value(), findBy).uniqueResult();
					break;
				default:
					break;
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: UserDaoImpl - fetchUserDetails()", e);
		}
		LOGGER.info("INFO: UserDaoImpl - fetchUserDetails() :: ends");
		return user;
	}

	@Override
	public UserDto fetchByGroupId(int groupId) throws CustomException {
		LOGGER.info("INFO: UserDaoImpl - fetchAuthInfoDetails() :: starts");
		UserDto user = null;
		try (Session session = sessionFactory.openSession()) {
			user = (UserDto) session.getNamedQuery("UserDto.findByGroupId")
					.setInteger("groupId", groupId).uniqueResult();
		} catch (Exception e) {
			LOGGER.error("ERROR: UserDaoImpl - fetchAuthInfoDetails()", e);
		}
		LOGGER.info("INFO: UserDaoImpl - fetchAuthInfoDetails() :: ends");
		return user;
	}

	@Override
	public AuthInfoDto fetchAuthInfoDetails(String findBy, String findByType) throws CustomException {
		LOGGER.info("INFO: UserDaoImpl - fetchAuthInfoDetails() :: starts");
		AuthInfoDto authInfo = null;
		try (Session session = sessionFactory.openSession()) {
			switch (findByType) {
				case AppConstants.FIND_BY_TYPE_USERID:
					authInfo = (AuthInfoDto) session.getNamedQuery("AuthInfoDto.findByUserId")
							.setInteger(AppEnums.QK_USER_IDENTIFIER.value(), Integer.parseInt(findBy)).uniqueResult();
					break;
				case AppConstants.FIND_BY_TYPE_SESSION_AUTH_KEY:
					authInfo = (AuthInfoDto) session.getNamedQuery("AuthInfoDto.findBySessionAuthKey")
							.setString(AppEnums.QK_SESSION_AUTHORIZATION_KEY.value(), findBy).uniqueResult();
					break;
				default:
					break;
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: UserDaoImpl - fetchAuthInfoDetails()", e);
		}
		LOGGER.info("INFO: UserDaoImpl - fetchAuthInfoDetails() :: ends");
		return authInfo;
	}

	@Override
	public UserDto saveOrUpdateUserDetails(UserDto user, String type) throws CustomException {
		LOGGER.info("INFO: UserDaoImpl - saveOrUpdateUserDetails() :: starts");
		Transaction transaction = null;
		UserDto updateUser = null;
		try (Session session = sessionFactory.openSession()) {
			if (user != null) {
				transaction = session.beginTransaction();
				switch (type) {
					case AppConstants.DB_SAVE:
						session.save(user);
						break;
					case AppConstants.DB_UPDATE:
						session.update(user);
						break;
					default:
						break;
				}
				transaction.commit();
				updateUser = user;
			}
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			LOGGER.error("ERROR: UserDaoImpl - saveOrUpdateUserDetails()", e);
		}
		LOGGER.info("INFO: UserDaoImpl - saveOrUpdateUserDetails() :: ends");
		return updateUser;
	}

	@Override
	public AuthInfoDto saveOrUpdateAuthInfoDetails(AuthInfoDto authInfo, String type) throws CustomException {
		LOGGER.info("INFO: UserDaoImpl - saveOrUpdateAuthInfoDetails() :: starts");
		Transaction transaction = null;
		AuthInfoDto updateAuthInfo = null;
		try (Session session = sessionFactory.openSession()) {
			if (authInfo != null) {
				transaction = session.beginTransaction();
				switch (type) {
					case AppConstants.DB_SAVE:
						session.save(authInfo);
						break;
					case AppConstants.DB_UPDATE:
						session.update(authInfo);
						break;
					default:
						break;
				}
				transaction.commit();
				updateAuthInfo = authInfo;
			}
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			LOGGER.error("ERROR: UserDaoImpl - saveOrUpdateAuthInfoDetails()", e);
		}
		LOGGER.info("INFO: UserDaoImpl - saveOrUpdateAuthInfoDetails() :: ends");
		return updateAuthInfo;
	}

	@Override
	public boolean validateEmail(String email) throws CustomException {
		LOGGER.info("INFO: UserDaoImpl - validateEmail() :: starts");
		boolean isValid = false;
		UserDto user = null;
		try (Session session = sessionFactory.openSession()) {
			user = (UserDto) session.getNamedQuery("UserDto.findByEmail").setString(AppEnums.QK_EMAIL.value(), email)
					.uniqueResult();
			if (user != null) {
				isValid = true;
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: UserDaoImpl - validateEmail()", e);
		}
		LOGGER.info("INFO: UserDaoImpl - validateEmail() :: ends");
		return isValid;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void deleteNonSignedUpUsersToStudy() throws CustomException {
		LOGGER.info("INFO: UserDaoImpl - deleteNonSignedUpUsersToStudy() :: starts");
		Transaction transaction = null;
		List<UserDto> usersList = new ArrayList<>();
		try (Session session = sessionFactory.openSession()) {
			transaction = session.beginTransaction();
			usersList = session.getNamedQuery("UserDto.fetchAllNonSignedUpUsers")
					.setString(AppEnums.QK_CREATED_ON.value(),
							AppUtil.addDays(AppUtil.getCurrentDateTime(), AppConstants.SDF_DATE_TIME_FORMAT,-30, AppConstants.SDF_DATE_TIME_FORMAT))
					.setMaxResults(5).list();
			if (usersList != null && !usersList.isEmpty()) {
				for (UserDto user : usersList) {
					AuthInfoDto authInfo = (AuthInfoDto) session.getNamedQuery("AuthInfoDto.findByUserId")
							.setInteger(AppEnums.QK_USER_IDENTIFIER.value(), user.getUserId()).uniqueResult();
					if (authInfo != null) {
						session.delete(authInfo);
						session.delete(user);
					}
				}
				transaction.commit();
			}
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			LOGGER.error("ERROR: UserDaoImpl - deleteNonSignedUpUsersToStudy()", e);
		}
		LOGGER.info("INFO: UserDaoImpl - deleteNonSignedUpUsersToStudy() :: ends");
	}

	@Override
	public FitbitUserInfoDto fetchFitbitUserInfo(String findBy, String findByType) throws CustomException {
		LOGGER.info("INFO: UserDaoImpl - fetchFitbitUserInfo() :: starts");
		FitbitUserInfoDto fitbitUserInfoDto = null;
		try (Session session = sessionFactory.openSession()) {
			if (findByType.equals(AppConstants.FIND_BY_TYPE_USERID)) {
				fitbitUserInfoDto = (FitbitUserInfoDto) session.getNamedQuery("FitbitUserInfoDto.findByUserId")
						.setInteger(AppEnums.QK_USER_IDENTIFIER.value(), Integer.parseInt(findBy)).uniqueResult();
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: UserDaoImpl - fetchFitbitUserInfo()", e);
		}
		LOGGER.info("INFO: UserDaoImpl - fetchFitbitUserInfo() :: ends");
		return fitbitUserInfoDto;
	}

	@Override
	public FitbitUserInfoDto saveOrUpdateFitbitUserInfoDetails(FitbitUserInfoDto fitbitUserInfo, String type)
			throws CustomException {
		LOGGER.info("INFO: UserDaoImpl - saveOrUpdateFitbitUserInfoDetails() :: starts");
		Transaction transaction = null;
		FitbitUserInfoDto updateFitbitUserInfo = null;
		try (Session session = sessionFactory.openSession()) {
			if (fitbitUserInfo != null) {
				transaction = session.beginTransaction();
				switch (type) {
					case AppConstants.DB_SAVE:
						session.save(fitbitUserInfo);
						break;
					case AppConstants.DB_UPDATE:
						session.update(fitbitUserInfo);
						break;
					default:
						break;
				}
				transaction.commit();
				updateFitbitUserInfo = fitbitUserInfo;
			}
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			LOGGER.error("ERROR: UserDaoImpl - saveOrUpdateFitbitUserInfoDetails()", e);
		}
		LOGGER.info("INFO: UserDaoImpl - saveOrUpdateFitbitUserInfoDetails() :: ends");
		return updateFitbitUserInfo;
	}

	@Override
	public void deleteFitBitUserInfo(String findBy, String findByType) throws CustomException {
		LOGGER.info("INFO: UserDaoImpl - deleteFitBitUserInfo() :: starts");
		Transaction transaction = null;
		FitbitUserInfoDto fitbitUserInfo = null;
		try (Session session = sessionFactory.openSession()) {
			fitbitUserInfo = (FitbitUserInfoDto) session.getNamedQuery("FitbitUserInfoDto.findByUserId")
					.setInteger(AppEnums.QK_USER_IDENTIFIER.value(), Integer.parseInt(findBy)).uniqueResult();
			if (fitbitUserInfo != null) {
				transaction = session.beginTransaction();
				session.delete(fitbitUserInfo);
				transaction.commit();
			}
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			LOGGER.error("ERROR: UserDaoImpl - deleteFitBitUserInfo()", e);
		}
		LOGGER.info("INFO: UserDaoImpl - deleteFitBitUserInfo() :: ends");
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UserDto> findAllActiveLoggedInUsers() {
		LOGGER.info("INFO: UserDaoImpl - findAllActiveLoggedInUsers() :: starts");
		List<UserDto> activeUsersList = new ArrayList<>();
		try (Session session = sessionFactory.openSession()) {
			activeUsersList = session.getNamedQuery("UserDto.findAllActiveLoggedInUsers").list();
					/*.setString(AppEnums.QK_SESSION_EXPIRED_DATE.value(), AppUtil.getCurrentDateTime()).list();*/
		} catch (Exception e) {
			LOGGER.error("ERROR: UserDaoImpl - findAllActiveLoggedInUsers()", e);
		}
		LOGGER.info("INFO: UserDaoImpl - findAllActiveLoggedInUsers() :: ends");
		return activeUsersList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FitbitUserInfoDto> findAllFitBitUserInfoDetailsUserIdsList(List<Object> userIdsList) {
		LOGGER.info("INFO: UserDaoImpl - findAllFitBitUserInfoDetailsUserIdsList() :: starts");
		List<FitbitUserInfoDto> fitbitUserInfoList = new ArrayList<>();
		try (Session session = sessionFactory.openSession()) {
			fitbitUserInfoList = session.getNamedQuery("FitbitUserInfoDto.findAllByUserId")
					.setParameterList(AppEnums.QK_USER_IDENTIFIER_LIST.value(), userIdsList).list();
		} catch (Exception e) {
			LOGGER.error("ERROR: UserDaoImpl - findAllFitBitUserInfoDetailsUserIdsList()", e);
		}
		LOGGER.info("INFO: UserDaoImpl - findAllFitBitUserInfoDetailsUserIdsList() :: ends");
		return fitbitUserInfoList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AuthInfoDto> findAllAuthinfoDetailsByUserIdList(List<Object> userIdsList) {
		LOGGER.info("INFO: UserDaoImpl - findAllAuthinfoDetailsByUserIdList() :: starts");
		List<AuthInfoDto> authInfoList = new ArrayList<>();
		try (Session session = sessionFactory.openSession()) {
			authInfoList = session.getNamedQuery("AuthInfoDto.findAllByUserIdsList")
					.setParameterList(AppEnums.QK_USER_IDENTIFIER_LIST.value(), userIdsList).list();
		} catch (Exception e) {
			LOGGER.error("ERROR: UserDaoImpl - findAllAuthinfoDetailsByUserIdList()", e);
		}
		LOGGER.info("INFO: UserDaoImpl - findAllAuthinfoDetailsByUserIdList() :: ends");
		return authInfoList;
	}

	@Override
	public FitbitLass4UDataDto fetchFitbitLass4UDataDetails(String findBy, String findByType) throws CustomException {
		LOGGER.info("INFO: UserDaoImpl - fetchFitbitLass4UDataDetails() :: starts");
		FitbitLass4UDataDto fitbitLass4UDataDto = null;
		try (Session session = sessionFactory.openSession()) {
			if (AppConstants.FIND_BY_TYPE_ENROLLMENTID.equals(findByType)) {
				fitbitLass4UDataDto = (FitbitLass4UDataDto) session
						.getNamedQuery("FitbitLass4UDataDto.findByEnrollmentId")
						.setString(AppEnums.QK_ENROLLMENT_IDENTIFIER.value(), findBy).uniqueResult();
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: UserDaoImpl - fetchFitbitLass4UDataDetails()", e);
		}
		LOGGER.info("INFO: UserDaoImpl - fetchFitbitLass4UDataDetails() :: ends");
		return fitbitLass4UDataDto;
	}

	@Override
	public FitbitLass4UDataDto saveOrUpdateFitbitLass4UDataDetails(FitbitLass4UDataDto fitbitLass4UDataDto, String type)
			throws CustomException {
		LOGGER.info("INFO: UserDaoImpl - saveOrUpdateFitbitLass4UDataDetails() :: starts");
		Transaction transaction = null;
		FitbitLass4UDataDto updateFitbitLass4UData = null;
		try (Session session = sessionFactory.openSession()) {
			if (fitbitLass4UDataDto != null) {
				transaction = session.beginTransaction();
				switch (type) {
					case AppConstants.DB_SAVE:
						session.save(fitbitLass4UDataDto);
						break;
					case AppConstants.DB_UPDATE:
						session.update(fitbitLass4UDataDto);
						break;
					default:
						break;
				}
				transaction.commit();
				updateFitbitLass4UData = fitbitLass4UDataDto;
			}
		} catch (Exception e) {
			if (transaction != null)
				transaction.rollback();
			LOGGER.error("ERROR: UserDaoImpl - saveOrUpdateFitbitLass4UDataDetails()", e);
		}
		LOGGER.info("INFO: UserDaoImpl - saveOrUpdateFitbitLass4UDataDetails() :: ends");
		return updateFitbitLass4UData;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getAllTimeZoneList() throws CustomException {
		LOGGER.info("INFO: UserDaoImpl - fetchFitbitLass4UDataDetails() :: starts");
		List<String> timeZoneList = new ArrayList<>();
		try (Session session = sessionFactory.openSession()) {
			
			timeZoneList = 	session.createSQLQuery("select distinct(a.time_zone) from user_details a  where user_id in (SELECT user_id FROM user_details where user_id in (SELECT user_id from auth_info where session_auth_key is not null) and user_id in (select user_id from user_studies where active='Y')) order by a.time_zone").list();
		} catch (Exception e) {
			LOGGER.error("ERROR: UserDaoImpl - fetchFitbitLass4UDataDetails()", e);
		}
		LOGGER.info("INFO: UserDaoImpl - fetchFitbitLass4UDataDetails() :: ends");
		return timeZoneList;
	}
	

	  @Override
	  public FitbitLogDto saveOrUpdateFitbitLogDetails(FitbitLogDto fitbitLog, String type) {
	    LOGGER.info("INFO: UserDaoImpl - saveOrUpdateFitbitLogDetails() :: starts");
	    Transaction transaction = null;
	    FitbitLogDto updateFitbitLogData = null;
	    try (Session session = sessionFactory.openSession()) {
	      if (fitbitLog != null) {
	        transaction = session.beginTransaction();
	        switch (type) {
	          case AppConstants.DB_SAVE:
	            session.save(fitbitLog);
	            break;
	          case AppConstants.DB_UPDATE:
	            session.update(fitbitLog);
	            break;
	          default:
	            break;
	        }
	        transaction.commit();
	        updateFitbitLogData = fitbitLog;
	      }
	    } catch (Exception e) {
	      if (transaction != null) transaction.rollback();
	      LOGGER.error("ERROR: UserDaoImpl - saveOrUpdateFitbitLogDetails()", e);
	    }
	    LOGGER.info("INFO: UserDaoImpl - saveOrUpdateFitbitLogDetails() :: ends");
	    return updateFitbitLogData;
	  }
	  
		@SuppressWarnings("unchecked")
		@Override
		public List<UserDto> getALLUserInfo() throws CustomException {
			LOGGER.info("INFO: UserDaoImpl - getALLUserInfo() :: starts");
			List<UserDto> userList = new ArrayList<>();
			try (Session session = sessionFactory.openSession()) {
				
                 userList=  session.getNamedQuery("UserDto.getAllUsersInfo").list();
			} catch (Exception e) {
				LOGGER.error("ERROR: UserDaoImpl - getALLUserInfo()", e);
			}
			LOGGER.info("INFO: UserDaoImpl - getALLUserInfo() :: ends");
			return userList;
		}
		
		 @Override
		  public SleepDto saveOrUpdateFitbitSleepDetails(SleepDto sleepDto, String type) {
		    LOGGER.info("INFO: UserDaoImpl - saveOrUpdateFitbitSleepDetails() :: starts");
		    Transaction transaction = null;
		    SleepDto sleep = null;
		    try (Session session = sessionFactory.openSession()) {
		      if (sleepDto != null) {
		        transaction = session.beginTransaction();
		        switch (type) {
		          case AppConstants.DB_SAVE:
		            session.save(sleepDto);
		            break;
		          case AppConstants.DB_UPDATE:
		            session.update(sleepDto);
		            break;
		          default:
		            break;
		        }
		        transaction.commit();
		        sleep = sleepDto;
		      }
		    } catch (Exception e) {
		      if (transaction != null) transaction.rollback();
		      LOGGER.error("ERROR: UserDaoImpl - saveOrUpdateFitbitSleepDetails()", e);
		    }
		    LOGGER.info("INFO: UserDaoImpl - saveOrUpdateFitbitSleepDetails() :: ends");
		    return sleep;
		  }
		 
		 
		 @Override
		  public StepsDto saveOrUpdateFitbitStepsDetails(StepsDto stepsDto, String type) {
		    LOGGER.info("INFO: UserDaoImpl - saveOrUpdateFitbitStepsDetails() :: starts");
		    Transaction transaction = null;
		    StepsDto steps = null;
		    try (Session session = sessionFactory.openSession()) {
		      if (stepsDto != null) {
		        transaction = session.beginTransaction();
		        switch (type) {
		          case AppConstants.DB_SAVE:
		            session.save(stepsDto);
		            break;
		          case AppConstants.DB_UPDATE:
		            session.update(stepsDto);
		            break;
		          default:
		            break;
		        }
		        transaction.commit();
		        steps = stepsDto;
		      }
		    } catch (Exception e) {
		      if (transaction != null) transaction.rollback();
		      LOGGER.error("ERROR: UserDaoImpl - saveOrUpdateFitbitStepsDetails()", e);
		    }
		    LOGGER.info("INFO: UserDaoImpl - saveOrUpdateFitbitStepsDetails() :: ends");
		    return steps;
		  }
		 
		 @Override
		 public HeartRateDto saveOrUpdateFitbitHeartRateDetails(HeartRateDto heartRateDto, String type) {
		    LOGGER.info("INFO: UserDaoImpl - saveOrUpdateFitbitHeartRateDetails() :: starts");
		    Transaction transaction = null;
		    HeartRateDto heartRate = null;
		    try (Session session = sessionFactory.openSession()) {
		      if (heartRateDto != null) {
		        transaction = session.beginTransaction();
		        switch (type) {
		          case AppConstants.DB_SAVE:
		            session.save(heartRateDto);
		            break;
		          case AppConstants.DB_UPDATE:
		            session.update(heartRateDto);
		            break;
		          default:
		            break;
		        }
		        transaction.commit();
		        heartRate = heartRateDto;
		      }
		    } catch (Exception e) {
		      if (transaction != null) transaction.rollback();
		      LOGGER.error("ERROR: UserDaoImpl - saveOrUpdateFitbitHeartRateDetails()", e);
		    }
		    LOGGER.info("INFO: UserDaoImpl - saveOrUpdateFitbitHeartRateDetails() :: ends");
		    return heartRate;
		  }
		 
	@SuppressWarnings("unchecked")
	@Override
	public void deleteFitbitSleepData(List<String> days, int userId) throws CustomException {
		LOGGER.info("INFO: UserDaoImpl - deleteFitbitSleepData() :: starts");
		Transaction transaction = null;
		List<SleepDto> list = new ArrayList<SleepDto>();
		try (Session session = sessionFactory.openSession()) {
			transaction = session.beginTransaction();
			list = session.getNamedQuery("SleepDto.deleteSleep").setInteger("userId", userId).setParameterList("date", days).list();
			if(null != list) {
				for(SleepDto dto : list) {
					transaction = session.beginTransaction();
					session.delete(dto);
					transaction.commit();
				}
			}
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			LOGGER.error("ERROR: UserDaoImpl - deleteFitbitSleepData()", e);
		}
		LOGGER.info("INFO: UserDaoImpl - deleteFitbitSleepData() :: ends");
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void deleteFitbitStepsData(List<String> days, int userId) throws CustomException {
		LOGGER.info("INFO: UserDaoImpl - deleteFitbitStepsleepData() :: starts");
		Transaction transaction = null;
		List<StepsDto> list = new ArrayList<StepsDto>();
		try (Session session = sessionFactory.openSession()) {
			transaction = session.beginTransaction();
			list= session.getNamedQuery("StepsDto.deleteSteps").setInteger("userId", userId).setParameterList("date", days).list();
			if(null != list) {
				for(StepsDto dto : list) {
					transaction = session.beginTransaction();
					session.delete(dto);
					transaction.commit();
				}
			}
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			LOGGER.error("ERROR: UserDaoImpl - deleteFitbitStepsleepData()", e);
		}
		LOGGER.info("INFO: UserDaoImpl - deleteFitbitStepsleepData() :: ends");
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void deleteFitbitHeartRateData(List<String> days, int userId) throws CustomException {
		LOGGER.info("INFO: UserDaoImpl - deleteFitbitHeartRateData() :: starts");
		Transaction transaction = null;
		List<HeartRateDto> list = new ArrayList<HeartRateDto>();
		try (Session session = sessionFactory.openSession()) {
			transaction = session.beginTransaction();
			list = session.getNamedQuery("HeartRateDto.deleteHeartRate").setInteger("userId", userId).setParameterList("date", days).list();
			if(null != list) {
				for(HeartRateDto dto : list) {
					transaction = session.beginTransaction();
					session.delete(dto);
					transaction.commit();
				}
			}
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			LOGGER.error("ERROR: UserDaoImpl - deleteFitbitHeartRateData()", e);
		}
		LOGGER.info("INFO: UserDaoImpl - deleteFitbitHeartRateData() :: ends");
	}

}
