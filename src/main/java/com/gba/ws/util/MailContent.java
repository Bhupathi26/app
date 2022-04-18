package com.gba.ws.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * Provides mail content details.
 * 
 * @author Mohan
 * @createdOn Jan 8, 2018 5:22:48 PM
 */
public class MailContent {

	private static final Logger LOGGER = Logger.getLogger(MailContent.class);

	private static final String ENGLISH_SUBJECT_EMAIL_VERIFICATION = "mail.en.subject.email.verification";
	private static final String ENGLISH_CONTENT_EMAIL_VERIFICATION = "mail.en.content.email.verification";

	private static final String ENGLISH_SUBJECT_FORGOT_PSWD = "mail.en.subject.forgot.pswd";
	private static final String ENGLISH_CONTENT_FORGOT_PSWD = "mail.en.content.forgot.pswd";

	private static final String ENGLISH_SUBJECT_CONSENT_OVERVIEW = "mail.en.subject.consent.overview";
	private static final String ENGLISH_CONTENT_CONSENT_OVERVIEW = "mail.en.content.consent.overview";

	private static final String ENGLISH_SUBJECT_REWARDS_LEVEL = "mail.en.subject.rewards.level";
	private static final String ENGLISH_CONTENT_REWARDS_LEVEL = "mail.en.content.rewards.level";
	private static final String ENGLISH_CONTENT_REWARDS_LEVEL_FINISHED = "mail.en.content.rewards.level.finished";

	private static final String ENGLISH_SUBJECT_LEAVE_STUDY = "mail.en.subject.leave.study";
	private static final String ENGLISH_CONTENT_LEAVE_STUDY = "mail.en.content.leave.study";

	private static final String SPANISH_SUBJECT_EMAIL_VERIFICATION = "mail.es.subject.email.verification";
	private static final String SPANISH_CONTENT_EMAIL_VERIFICATION = "mail.es.content.email.verification";

	private static final String SPANISH_SUBJECT_FORGOT_PSWD = "mail.es.subject.forgot.pswd";
	private static final String SPANISH_CONTENT_FORGOT_PSWD = "mail.es.content.forgot.pswd";

	private static final String SPANISH_SUBJECT_CONSENT_OVERVIEW = "mail.es.subject.consent.overview";
	private static final String SPANISH_CONTENT_CONSENT_OVERVIEW = "mail.es.content.consent.overview";

	private static final String SPANISH_SUBJECT_REWARDS_LEVEL = "mail.es.subject.rewards.level";
	private static final String SPANISH_CONTENT_REWARDS_LEVEL = "mail.es.content.rewards.level";
	private static final String SPANISH_CONTENT_REWARDS_LEVEL_FINISHED = "mail.es.content.rewards.level.finished";

	private static final String SPANISH_SUBJECT_LEAVE_STUDY = "mail.es.subject.leave.study";
	private static final String SPANISH_CONTENT_LEAVE_STUDY = "mail.es.content.leave.study";

	private static final String CHINESE_SUBJECT_EMAIL_VERIFICATION = "mail.zhCN.subject.email.verification";
	private static final String CHINESE_CONTENT_EMAIL_VERIFICATION = "mail.zhCN.content.email.verification";

	private static final String CHINESE_SUBJECT_FORGOT_PSWD = "mail.zhCN.subject.forgot.pswd";
	private static final String CHINESE_CONTENT_FORGOT_PSWD = "mail.zhCN.content.forgot.pswd";

	private static final String CHINESE_SUBJECT_CONSENT_OVERVIEW = "mail.zhCN.subject.consent.overview";
	private static final String CHINESE_CONTENT_CONSENT_OVERVIEW = "mail.zhCN.content.consent.overview";

	private static final String CHINESE_SUBJECT_REWARDS_LEVEL = "mail.zhCN.subject.rewards.level";
	private static final String CHINESE_CONTENT_REWARDS_LEVEL = "mail.zhCN.content.rewards.level";
	private static final String CHINESE_CONTENT_REWARDS_LEVEL_FINISHED = "mail.zhCN.content.rewards.level.finished";

	private static final String CHINESE_SUBJECT_LEAVE_STUDY = "mail.zhCN.subject.leave.study";
	private static final String CHINESE_CONTENT_LEAVE_STUDY = "mail.zhCN.content.leave.study";

	private static final String ENGLISH_SUBJECT_ONBOARDING_NEW_PARTICIPANT = "mail.subject.onboarding.new.participant";
	private static final String ENGLISH_CONTENT_ONBOARDING_NEW_PARTICIPANT = "mail.content.onboarding.new.participant";

	private static final String ENGLISH_SUBJECT_PARTICIPANT_REACHES_NEW_LEVEL = "mail.subject.participant.reaches.new.level";
	private static final String ENGLISH_CONTENT_PARTICIPANT_REACHES_NEW_LEVEL = "mail.content.participant.reaches.new.level";

	private static final String ENGLISH_SUBJECT_PARTICIPANT_LEAVE_STUDY = "mail.subject.participant.leave.study";
	private static final String ENGLISH_CONTENT_PARTICIPANT_LEAVE_STUDY = "mail.content.participant.leave.study";

	private static final String ENGLISH_REWARDS_LEVEL_BONUS = "mail.en.rewards.level.bonus";
	private static final String SPANISH_REWARDS_LEVEL_BONUS = "mail.es.rewards.level.bonus";
	private static final String CHINESE_REWARDS_LEVEL_BONUS = "mail.zhCN.rewards.level.bonus";

	// private constructor to hide the implicit public one.
	private MailContent() {
		super();
	}

	/**
	 * Get the mail content for the provided keyvalues
	 * 
	 * @author Mohan
	 * @param mailContent
	 *            the dynamic content of the mail
	 * @param keyValue
	 *            the keyvalues details
	 * @return the dynamic mail content
	 */
	public static String generateMailContent(String mailContent, Map<String, String> keyValue) {
		LOGGER.info("INFO: MailContent - generateMailContent() :: starts");
		String dynamicMailContent = mailContent;
		if (StringUtils.isNotEmpty(dynamicMailContent)) {
			for (Map.Entry<String, String> entry : keyValue.entrySet()) {
				dynamicMailContent = dynamicMailContent.replace(entry.getKey(),
						StringUtils.isBlank(entry.getValue()) ? "" : entry.getValue());
			}
		}
		LOGGER.info("INFO: MailContent - generateMailContent() :: ends");
		return dynamicMailContent;
	}

	/**
	 * Participant enters an email during sign up and submits it for verification
	 * 
	 * @author Mohan
	 * @param firstName
	 *            the first name
	 * @param accessCode
	 *            the access code value
	 * @param studyName
	 *            the study name
	 * @param language
	 *            the user language
	 * @return the mail dynamic content
	 */
	public static Map<String, String> emailVerificationContent(String firstName, String accessCode, String studyName,
			String language) {
		LOGGER.info("INFO: MailContent - emailVerificationContent() :: starts");
		Map<String, String> mailContentMap = new HashMap<>();
		Map<String, String> keyValuesMap = new HashMap<>();
		String mailContent = "";
		String subject = "";
		try {

			keyValuesMap.put(AppEnums.MKV_FIRST_NAME.value(), firstName);
			keyValuesMap.put(AppEnums.MKV_STUDY_NAME.value(), studyName);
			keyValuesMap.put(AppEnums.MKV_ACCESS_CODE.value(), accessCode);

			switch (language) {
				case AppConstants.USER_LANGUAGE_SPANISH:
					subject = AppUtil.getAppProperties().get(SPANISH_SUBJECT_EMAIL_VERIFICATION);
					mailContent = generateMailContent(AppUtil.getAppProperties().get(SPANISH_CONTENT_EMAIL_VERIFICATION),
							keyValuesMap);
					break;
				case AppConstants.USER_LANGUAGE_CHINESE:
					subject = AppUtil.getAppProperties().get(CHINESE_SUBJECT_EMAIL_VERIFICATION);
					mailContent = generateMailContent(AppUtil.getAppProperties().get(CHINESE_CONTENT_EMAIL_VERIFICATION),
							keyValuesMap);
					break;
				default:
					subject = AppUtil.getAppProperties().get(ENGLISH_SUBJECT_EMAIL_VERIFICATION);
					mailContent = generateMailContent(AppUtil.getAppProperties().get(ENGLISH_CONTENT_EMAIL_VERIFICATION),
							keyValuesMap);
					break;
			}

			mailContentMap.put(AppConstants.MAIL_SUBJECT, subject);
			mailContentMap.put(AppConstants.MAIL_BODY, mailContent);
		} catch (Exception e) {
			LOGGER.error("ERROR: MailContent - emailVerificationContent()", e);
		}
		LOGGER.info("INFO: MailContent - emailVerificationContent() :: ends");
		return mailContentMap;
	}

	/**
	 * Participant Clicks on forgot password and enters valid email id
	 * 
	 * @author Mohan
	 * @param firstName
	 *            the first name
	 * @param tempPassword
	 *            the temporary password
	 * @param language
	 *            the user language
	 * @return the mail dynamic content
	 */
	public static Map<String, String> forgotPasswordContent(String firstName, String tempPassword, String language) {
		LOGGER.info("INFO: MailContent - forgotPasswordContent() :: starts");
		Map<String, String> mailContentMap = new HashMap<>();
		Map<String, String> keyValuesMap = new HashMap<>();
		String mailContent = "";
		String subject = "";
		try {

			keyValuesMap.put(AppEnums.MKV_FIRST_NAME.value(), firstName);
			keyValuesMap.put(AppEnums.MKV_TEMP_PASSWPRD.value(), tempPassword);

			switch (language) {
				case AppConstants.USER_LANGUAGE_SPANISH:
					subject = AppUtil.getAppProperties().get(SPANISH_SUBJECT_FORGOT_PSWD);
					mailContent = generateMailContent(AppUtil.getAppProperties().get(SPANISH_CONTENT_FORGOT_PSWD),
							keyValuesMap);
					break;
				case AppConstants.USER_LANGUAGE_CHINESE:
					subject = AppUtil.getAppProperties().get(CHINESE_SUBJECT_FORGOT_PSWD);
					mailContent = generateMailContent(AppUtil.getAppProperties().get(CHINESE_CONTENT_FORGOT_PSWD),
							keyValuesMap);
					break;
				default:
					subject = AppUtil.getAppProperties().get(ENGLISH_SUBJECT_FORGOT_PSWD);
					mailContent = generateMailContent(AppUtil.getAppProperties().get(ENGLISH_CONTENT_FORGOT_PSWD),
							keyValuesMap);
					break;
			}

			mailContentMap.put(AppConstants.MAIL_SUBJECT, subject);
			mailContentMap.put(AppConstants.MAIL_BODY, mailContent);
		} catch (Exception e) {
			LOGGER.error("ERROR: MailContent - forgotPasswordContent()", e);
		}
		LOGGER.info("INFO: MailContent - forgotPasswordContent() :: ends");
		return mailContentMap;
	}

	/**
	 * Participant Joins a Study
	 * 
	 * @author Mohan
	 * @param firstName
	 *            the first name
	 * @param studyName
	 *            the study name
	 * @param language
	 *            the user language
	 * @return the mail dynamic content
	 */
	public static Map<String, String> consentOverviewCompletionContent(String firstName, String studyName,
			String language) {
		LOGGER.info("INFO: MailContent - consentOverviewCompletionContent() :: starts");
		Map<String, String> mailContentMap = new HashMap<>();
		Map<String, String> keyValuesMap = new HashMap<>();
		String mailContent = "";
		String subject = "";
		try {

			keyValuesMap.put(AppEnums.MKV_FIRST_NAME.value(), firstName);
			keyValuesMap.put(AppEnums.MKV_STUDY_NAME.value(), studyName);

			switch (language) {
				case AppConstants.USER_LANGUAGE_SPANISH:
					subject = AppUtil.getAppProperties().get(SPANISH_SUBJECT_CONSENT_OVERVIEW);
					mailContent = generateMailContent(AppUtil.getAppProperties().get(SPANISH_CONTENT_CONSENT_OVERVIEW),
							keyValuesMap);
					break;
				case AppConstants.USER_LANGUAGE_CHINESE:
					subject = AppUtil.getAppProperties().get(CHINESE_SUBJECT_CONSENT_OVERVIEW);
					mailContent = generateMailContent(AppUtil.getAppProperties().get(CHINESE_CONTENT_CONSENT_OVERVIEW),
							keyValuesMap);
					break;
				default:
					subject = AppUtil.getAppProperties().get(ENGLISH_SUBJECT_CONSENT_OVERVIEW);
					mailContent = generateMailContent(AppUtil.getAppProperties().get(ENGLISH_CONTENT_CONSENT_OVERVIEW),
							keyValuesMap);
					break;
			}

			mailContentMap.put(AppConstants.MAIL_SUBJECT, subject);
			mailContentMap.put(AppConstants.MAIL_BODY, mailContent);
		} catch (Exception e) {
			LOGGER.error("ERROR: MailContent - consentOverviewCompletionContent()", e);
		}
		LOGGER.info("INFO: MailContent - consentOverviewCompletionContent() :: ends");
		return mailContentMap;
	}

	/**
	 * Participant Joins a Study and Mail Admin
	 * 
	 * @author Mohan
	 * @param enrollmentId
	 *            the enrollment identifier
	 * @param studyName
	 *            the study name
	 * @return the mail dynamic content
	 */
	public static Map<String, String> newParticipantOnBoarding(String enrollmentId, String studyName) {
		LOGGER.info("INFO: MailContent - newParticipantOnBoarding() :: starts");
		Map<String, String> mailContentMap = new HashMap<>();
		Map<String, String> keyValuesMap = new HashMap<>();
		String mailContent = "";
		String subject = "";
		try {

			keyValuesMap.put(AppEnums.MKV_ENROLLMENT_ID.value(), enrollmentId);
			keyValuesMap.put(AppEnums.MKV_STUDY_NAME.value(), studyName);

			subject = AppUtil.getAppProperties().get(ENGLISH_SUBJECT_ONBOARDING_NEW_PARTICIPANT);
			mailContent = generateMailContent(
					AppUtil.getAppProperties().get(ENGLISH_CONTENT_ONBOARDING_NEW_PARTICIPANT), keyValuesMap);

			mailContentMap.put(AppConstants.MAIL_SUBJECT, subject);
			mailContentMap.put(AppConstants.MAIL_BODY, mailContent);
		} catch (Exception e) {
			LOGGER.error("ERROR: MailContent - newParticipantOnBoarding()", e);
		}
		LOGGER.info("INFO: MailContent - newParticipantOnBoarding() :: ends");
		return mailContentMap;
	}

	/**
	 * When participant reaches a new level and receives a reward
	 * 
	 * @author Mohan
	 * @param firstName
	 *            the first name
	 * @param newLevel
	 *            the new level value
	 * @param oldLevel
	 *            the old level value
	 * @param studyName
	 *            the study name
	 * @param isFinalLevel
	 *            is user reached final level or not
	 * @param language
	 *            the user language
	 * @return the mail dynamic content
	 */
	public static Map<String, String> participantReachesANewLevelAndRecievesRewards(String firstName, String newLevel,
			String oldLevel, String studyName, boolean isFinalLevel, boolean isBonusLevel, String language) {
		LOGGER.info("INFO: MailContent - participantReachesANewLevelAndRecievesRewards() :: starts");
		Map<String, String> mailContentMap = new HashMap<>();
		Map<String, String> keyValuesMap = new HashMap<>();
		String mailContent = "";
		String subject = "";
		String newLevelReached = "";
		try {

			if (isBonusLevel) {
				switch (language) {
					case AppConstants.USER_LANGUAGE_SPANISH:
						newLevelReached = AppUtil.getAppProperties().get(SPANISH_REWARDS_LEVEL_BONUS);
						break;
					case AppConstants.USER_LANGUAGE_CHINESE:
						newLevelReached = AppUtil.getAppProperties().get(CHINESE_REWARDS_LEVEL_BONUS);
						break;
					default:
						newLevelReached = AppUtil.getAppProperties().get(ENGLISH_REWARDS_LEVEL_BONUS);
						break;
				}
			} else {
				newLevelReached = newLevel;
			}

			keyValuesMap.put(AppEnums.MKV_FIRST_NAME.value(), firstName);
			keyValuesMap.put(AppEnums.MKV_NEW_LEVEL.value(), newLevelReached);
			keyValuesMap.put(AppEnums.MKV_OLD_LEVEL.value(), oldLevel);
			keyValuesMap.put(AppEnums.MKV_STUDY_NAME.value(), studyName);

			switch (language) {
				case AppConstants.USER_LANGUAGE_SPANISH:
					subject = AppUtil.getAppProperties().get(SPANISH_SUBJECT_REWARDS_LEVEL);
					mailContent = generateMailContent(
							(isFinalLevel) ? AppUtil.getAppProperties().get(SPANISH_CONTENT_REWARDS_LEVEL_FINISHED)
									: AppUtil.getAppProperties().get(SPANISH_CONTENT_REWARDS_LEVEL),
							keyValuesMap);
					break;
				case AppConstants.USER_LANGUAGE_CHINESE:
					subject = AppUtil.getAppProperties().get(CHINESE_SUBJECT_REWARDS_LEVEL);
					mailContent = generateMailContent(
							(isFinalLevel) ? AppUtil.getAppProperties().get(CHINESE_CONTENT_REWARDS_LEVEL_FINISHED)
									: AppUtil.getAppProperties().get(CHINESE_CONTENT_REWARDS_LEVEL),
							keyValuesMap);
					break;
				default:
					subject = AppUtil.getAppProperties().get(ENGLISH_SUBJECT_REWARDS_LEVEL);
					mailContent = generateMailContent(
							(isFinalLevel) ? AppUtil.getAppProperties().get(ENGLISH_CONTENT_REWARDS_LEVEL_FINISHED)
									: AppUtil.getAppProperties().get(ENGLISH_CONTENT_REWARDS_LEVEL),
							keyValuesMap);
					break;
			}

			mailContentMap.put(AppConstants.MAIL_SUBJECT, subject);
			mailContentMap.put(AppConstants.MAIL_BODY, mailContent);
		} catch (Exception e) {
			LOGGER.error("ERROR: MailContent - participantReachesANewLevelAndRecievesRewards()", e);
		}
		LOGGER.info("INFO: MailContent - participantReachesANewLevelAndRecievesRewards() :: ends");
		return mailContentMap;
	}

	/**
	 * Notify admins about Participant reaches a new Level
	 * 
	 * @author Mohan
	 * @param enrollmentId
	 *            the enrollment identifier
	 * @param level
	 *            the level value
	 * @return the mail dynamic content
	 */
	public static Map<String, String> participantReachesNewLevel(String enrollmentId, String level) {
		LOGGER.info("INFO: MailContent - participantReachesNewLevel() :: starts");
		Map<String, String> mailContentMap = new HashMap<>();
		Map<String, String> keyValuesMap = new HashMap<>();
		String mailContent = "";
		String subject = "";
		try {

			keyValuesMap.put(AppEnums.MKV_ENROLLMENT_ID.value(), enrollmentId);
			keyValuesMap.put(AppEnums.MKV_LEVEL.value(), level);

			subject = AppUtil.getAppProperties().get(ENGLISH_SUBJECT_PARTICIPANT_REACHES_NEW_LEVEL);
			mailContent = generateMailContent(
					AppUtil.getAppProperties().get(ENGLISH_CONTENT_PARTICIPANT_REACHES_NEW_LEVEL), keyValuesMap);

			mailContentMap.put(AppConstants.MAIL_SUBJECT, subject);
			mailContentMap.put(AppConstants.MAIL_BODY, mailContent);
		} catch (Exception e) {
			LOGGER.error("ERROR: MailContent - participantReachesNewLevel()", e);
		}
		LOGGER.info("INFO: MailContent - participantReachesNewLevel() :: ends");
		return mailContentMap;
	}

	/**
	 * Notify participant about leave a study
	 * 
	 * @author Mohan
	 * @param firstName
	 *            the first name
	 * @param studyName
	 *            the study name
	 * @param language
	 *            the user language
	 * @return the mail dynamic content
	 */
	public static Map<String, String> participantLeavesStudy(String firstName, String studyName, String language) {
		LOGGER.info("INFO: MailContent - participantLeavesStudy() :: starts");
		Map<String, String> mailContentMap = new HashMap<>();
		Map<String, String> keyValuesMap = new HashMap<>();
		String mailContent = "";
		String subject = "";
		try {

			keyValuesMap.put(AppEnums.MKV_FIRST_NAME.value(), firstName);
			keyValuesMap.put(AppEnums.MKV_STUDY_NAME.value(), studyName);

			switch (language) {
				case AppConstants.USER_LANGUAGE_SPANISH:
					subject = AppUtil.getAppProperties().get(SPANISH_SUBJECT_LEAVE_STUDY);
					mailContent = generateMailContent(AppUtil.getAppProperties().get(SPANISH_CONTENT_LEAVE_STUDY),
							keyValuesMap);
					break;
				case AppConstants.USER_LANGUAGE_CHINESE:
					subject = AppUtil.getAppProperties().get(CHINESE_SUBJECT_LEAVE_STUDY);
					mailContent = generateMailContent(AppUtil.getAppProperties().get(CHINESE_CONTENT_LEAVE_STUDY),
							keyValuesMap);
					break;
				default:
					subject = AppUtil.getAppProperties().get(ENGLISH_SUBJECT_LEAVE_STUDY);
					mailContent = generateMailContent(AppUtil.getAppProperties().get(ENGLISH_CONTENT_LEAVE_STUDY),
							keyValuesMap);
					break;
			}

			mailContentMap.put(AppConstants.MAIL_SUBJECT, subject);
			mailContentMap.put(AppConstants.MAIL_BODY, mailContent);
		} catch (Exception e) {
			LOGGER.error("ERROR: MailContent - participantLeavesStudy()", e);
		}
		LOGGER.info("INFO: MailContent - participantLeavesStudy() :: ends");
		return mailContentMap;
	}

	/**
	 * Notify admin about participant leaving the study
	 * 
	 * @author Mohan
	 * @param enrollmentId
	 *            the enrollment identifier
	 * @param studyName
	 *            the study name
	 * @return the mail dynamic content
	 */
	public static Map<String, String> notifyLeaveStudyToAdmin(String enrollmentId, String studyName) {
		LOGGER.info("INFO: MailContent - notifyLeaveStudyToAdmin() :: starts");
		Map<String, String> mailContentMap = new HashMap<>();
		Map<String, String> keyValuesMap = new HashMap<>();
		String mailContent = "";
		String subject = "";
		try {

			keyValuesMap.put(AppEnums.MKV_ENROLLMENT_ID.value(), enrollmentId);
			keyValuesMap.put(AppEnums.MKV_STUDY_NAME.value(), studyName);

			subject = AppUtil.getAppProperties().get(ENGLISH_SUBJECT_PARTICIPANT_LEAVE_STUDY);
			mailContent = generateMailContent(AppUtil.getAppProperties().get(ENGLISH_CONTENT_PARTICIPANT_LEAVE_STUDY),
					keyValuesMap);

			mailContentMap.put(AppConstants.MAIL_SUBJECT, subject);
			mailContentMap.put(AppConstants.MAIL_BODY, mailContent);
		} catch (Exception e) {
			LOGGER.error("ERROR: MailContent - notifyLeaveStudyToAdmin()", e);
		}
		LOGGER.info("INFO: MailContent - notifyLeaveStudyToAdmin() :: ends");
		return mailContentMap;
	}
}
