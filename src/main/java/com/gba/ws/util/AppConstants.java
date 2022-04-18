package com.gba.ws.util;

import java.text.SimpleDateFormat;


/**
 * Provides application constants.
 * 
 * @author Mohan
 * @createdOn Jan 8, 2018 4:11:29 PM
 */
public interface AppConstants {

	String SUCCESS = "SUCCESS";
	String FAILURE = "FAILURE";

	String NOT_EXIST = "NOT_EXIST";
	String SESSION_OUT = "SESSION_OUT";
	String USER_DEACTIVATE = "USER_DEACTIVATE";

	SimpleDateFormat SDF_DATE_TIME = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat SDF_DATE_TIME_12 = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
	SimpleDateFormat SDF_DATE = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat SDF_TIME_24 = new SimpleDateFormat("HH:mm:ss");
	SimpleDateFormat SDF_TIME_12 = new SimpleDateFormat("hh:mm a");

	SimpleDateFormat SDF_MMDDYYYY = new SimpleDateFormat("MM/dd/yyyy");
	SimpleDateFormat DISPLAY_DATE = new SimpleDateFormat("EEE, MMM dd, yyyy");
	SimpleDateFormat DISPLAY_DATE_TIME = new SimpleDateFormat("EEE, MMM dd, yyyy 'at' hh:mm a");
	SimpleDateFormat DISPLAY_DATE_TIME_MIN = new SimpleDateFormat("dd MMM yyyy  HH:mm:ss");
	SimpleDateFormat SDF_DATE_TIME_TIMEZONE = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	SimpleDateFormat SDF_DATE_TIME_TIMEZONE_MILLISECONDS = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

	String SDF_DAY = "EEEE";

	String SDF_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	String SDF_DATE_TIME_MILLISECONDS_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
	String SDF_DATE_TIME_12_FORMAT = "MM/dd/yyyy hh:mm a";
	String SDF_DATE_FORMAT = "yyyy-MM-dd";
	String SDF_TIME_24_FORMAT = "HH:mm:ss";
	String SDF_TIME_12_FORMAT = "hh:mm a";

	String SDF_MMDDYYYY_FORMAT = "MM/dd/yyyy";
	String DISPLAY_DATE_FORMAT = "EEE, MMM dd, yyyy";
	String DISPLAY_DATE_TIME_FORMAT = "EEE, MMM dd, yyyy 'at' hh:mm a";
	String DISPLAY_DATE_TIME_MIN_FORMAT = "dd MMM yyyy  HH:mm:ss";
	String SDF_DATE_TIME_TIMEZONE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
	String SDF_DATE_TIME_TIMEZONE_MILLISECONDS_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

	String PSWD_SALT = "BTCSoft";
	String REFRESH_SESSION_AUTHKEY_PATH = "refreshToken";
	String SERVER_TIMEZONE = "America/New_York";

	int AUTH_KEY_LENGTH = 10;
	int VERIFICATION_KEY_LENGTH = 4;
	int MAX_RUN_COUNT = 20;

	String START_TIME = "00:00:00";
	String END_TIME = "23:59:59";
	String USER_SESSION_TIMEOUT = "user.session.timeout";

	String USER_AUTHKEY_UPD = "user.authkey.upd";
	String USER_REGISTRATION_SUCCESS = "user.registration.success";
	String USER_LOGIN_SUCCESS = "user.login.success";
	String USER_EMAIL_VERIFIED = "user.email.verified";
	String USER_RESEND_TOKEN = "user.resend.token";
	String USER_FORGOT_PSWD = "user.forgot.pswd";
	String USER_FETCH_UPD = "user.fetch.upd";
	String USER_UPDATE_UPD = "user.update.upd";
	String USER_UPDATE_PSWD = "user.update.pswd";
	String USER_LOGOUT_SUCCESS = "user.logout.success";
	String UPDATE_DEVICE_TOKEN_SUCCESS = "update.devicetoken.success";
	String FETCH_GROUP_LOCATION_SUCCESS = "fetch.group.location.success";
	String FETCH_BUILDING_LOCATION_SUCCESS = "fetch.building.location.success";

	String STUDY_ELGBTY_VERIFIED = "study.elgbty.verified";
	String STUDY_ENROLL = "study.enroll";
	String STUDY_SIGNED_CONSENT = "study.signed.consent";
	String STUDY_STORE_CONSENT = "study.store.consent";
	
	String RESPONSE_STORE_ACTIVITY = "response.store.activity";
	String RESPONSE_SIGNED_ACTIVITY = "response.signed.activity";

	String ACTIVITY_LIST_SUCCESS = "activity.list.success";
	String UPDATE_ACTIVITY_STATE = "update.activity.state";
	String ACTIVITY_META_DATA = "activity.meta.data";
	String UPDATE_THRESHOLD_ACTIVITY_STATE = "update.threshold.activity.state";

	String ACTIVITY_RUNS = "activity.runs";
	int USER_DEACTIVE = 0;
	int USER_ACTIVE = 1;
	int USER_PENDING = 2;

	String USER_LANGUAGE_ENGLISH = "en";
	String USER_LANGUAGE_SPANISH = "es";
	String USER_LANGUAGE_CHINESE = "zh-CN";
	String USER_LANGUAGE_CHINESE_QUALTRICS = "CN";

	String TEMPERATURE_CELCIUS = "celcius";
	String TEMPERATURE_FARENHEIT = "farenheit";

	int HTTP_STATUS_CODE_200 = 200;
	String HTTP_STATUS_500 = "http.status.code.500";

	String CHARSET_ENCODING_UTF_8 = "UTF-8";

	String PLATFORM_TYPE_IOS = "iOS";
	String PLATFORM_TYPE_ANDROID = "Android";
	String PLATFORM_IOS = "ios";
	String PLATFORM_ANDROID = "android";

	String AUTH_TYPE_PLATFORM = "platform";
	String AUTH_TYPE_OS = "os";
	String AUTH_TYPE_BUNDLE_ID = "bundleId";

	String DEFAULT_APP_VERSION = "1.0";

	String FIND_BY_TYPE_USERID = "userId";
	String FIND_BY_TYPE_EMAIL = "emailId";
	String FIND_BY_TYPE_SESSION_AUTH_KEY = "sessionAuthKey";
	String FIND_BY_TYPE_ENROLLMENTID = "enrollmentId";
	String FIND_BY_TYPE_USERID_STUDYID = "userIdAndStudyId";
	
	String FIND_BY_TYPE_USERID_ENROLLMENTID = "userIdAndEnrollmentId";
	
	String FIND_BY_TYPE_STUDYID = "studyId";
	String FIND_BY_TYPE_ACTIVITY_IDS = "activityIds";
	String FIND_BY_TYPE_ACTIVITY_CONDITIONID = "activityConditionId";
	String FIND_BY_TYPE_ACTIVITY_ID = "activityId";
	String FIND_BY_TYPE_USERID_CONDITIONID = "userIdAndConditionId";
	String FIND_BY_TYPE_USERID_CONDITIONIDS = "userIdAndConditionIds";
	String FIND_BY_TYPE_CONDITIONID = "conditionId";
	String FIND_BY_TYPE_GROUPID = "groupId";
	String FIND_BY_TYPE_GROUPID_LIST = "groupIdList";
	String FIND_BY_TYPE_CONDITIONIDS = "conditionIds";
	String FIND_BY_TYPE_ACTIVITY_SUB_TYPE = "activitySubType";
	String FIND_BY_TYPE_GROUP_ID_NAME = "groupIdsName";
	String FIND_BY_TYPE_USERID_STUDYID_ACTIVITY_SUB_TYPE = "userIdAndStudyIdAndActivitySubtype";

	String FIND_BY_TYPE_USERID_ACTIVITYID_RUNID = "userIdNActivityIdNAunId";
	String FIND_BY_TYPE_QUESTION_ID = "questionId";
	String FIND_BY_TYPE_CHOICE_ID = "choicesId";
	String FIND_BY_TYPE_USERID_ACTIVITYID_RUNID_THRESHOLD = "userIdNActivityIdNAunIdNThreshold";

	String DB_SAVE = "save";
	String DB_UPDATE = "update";
	String DB_SAVE_OR_UPDATE = "saveOrUpdate";

	String MAIL_SUBJECT = "subject";
	String MAIL_BODY = "body";

	String MAIL_EN_APP_NAME = "mail.en.app.name";
	String MAIL_EN_EMAIL_VERFICATION = "mail.en.email.verification";
	String MAIL_EN_REGISTRATION = "mail.en.registration";
	String MAIL_EN_FORGOT_PSWD = "mail.en.forgot.pswd";
	String MAIL_EN_CONSENT_OVERVIEW = "mail.en.consent.overview";
	String MAIL_EN_REWARDS_NEW_LEVEL = "mail.en.rewards.level";
	String MAIL_EN_LEAVE_STUDY = "mail.en.leave.study";

	String MAIL_ES_APP_NAME = "mail.es.app.name";
	String MAIL_ES_EMAIL_VERFICATION = "mail.es.email.verification";
	String MAIL_ES_REGISTRATION = "mail.es.registration";
	String MAIL_ES_FORGOT_PSWD = "mail.es.forgot.pswd";
	String MAIL_ES_CONSENT_OVERVIEW = "mail.es.consent.overview";
	String MAIL_ES_REWARDS_NEW_LEVEL = "mail.es.rewards.level";
	String MAIL_ES_LEAVE_STUDY = "mail.es.leave.study";

	String MAIL_ZH_CN_APP_NAME = "mail.zhCN.app.name";
	String MAIL_ZH_CN_EMAIL_VERFICATION = "mail.zhCN.email.verification";
	String MAIL_ZH_CN_REGISTRATION = "mail.zhCN.registration";
	String MAIL_ZH_CN_FORGOT_PSWD = "mail.zhCN.forgot.pswd";
	String MAIL_ZH_CN_CONSENT_OVERVIEW = "mail.zhCN.consent.overview";
	String MAIL_ZH_CN_REWARDS_NEW_LEVEL = "mail.zhCN.rewards.level";
	String MAIL_ZH_CN_LEAVE_STUDY = "mail.zhCN.leave.study";

	String MAIL_ONBOARDING_NEW_PARTICIPANT = "mail.onboarding.new.participant";
	String MAIL_PARTICIPANT_REACHES_NEW_LEVEL = "mail.participant.reaches.new.level";
	String MAIL_PARTICIPANT_LEAVE_STUDY = "mail.participant.leave.study";

				/*Added By Kavya*/
	String ACTIVITY_TYPE_AUT_TEST_TASK = "autTestTask";
	String ACTIVITY_TYPE_STROOP_TEST_TASK = "stroopTestTask";
	String ACTIVITY_TYPE_ARITHMETIC_TEST_TASK = "arithmeticTestTask";
	String ACTIVITY_TYPE_SURVEY = "questionnaire";
	        /*Added By Kavya*/
	String ACTIVITY_TYPE_AUT_WEB = "AUT";
	
	String ACTIVITY_TYPE_STROOP_WEB = "Stroop";
	String ACTIVITY_TYPE_ARITHMETIC_WEB = "Arithmetic";
	String ACTIVITY_TYPE_SURVEY_WEB = "Survey";

	String ACTIVITY_SUB_TYPE_TRIGGERED = "triggered";
	String ACTIVITY_SUB_TYPE_SCHEDULED = "scheduled";

	String ACTIVITY_FREQUENCY_DAILY = "daily";
	String ACTIVITY_FREQUENCY_WEEKLY = "weekly";

	String RUN_STATE_START = "start";
	String RUN_STATE_RESUME = "resume";
	String RUN_STATE_COMPLETED = "completed";
	String RUN_STATE_INCOMPLETE = "incomplete";
	String RUN_STATES = "start,resume,completed,incomplete";

	String STEP_INTSRUCTION = "instruction";
	String STEP_QUESTION = "question";
	String STEP_TASK = "task";

	String QUALTRICS_QT_TYPE_TASK = "task";
	String QUALTRICS_QT_TYPE_QUESTIONNAIRE = "instruction,question";

	String RESPONSE_TYPE_INTSRUCTION = "instruction";
	String RESPONSE_TYPE_VALUE_PICKER = "valuePicker";
	String RESPONSE_TYPE_TEXT_CHOICE = "textChoice";
	String RESPONSE_TYPE_BOOLEAN = "boolean";
	String RESPONSE_TYPE_NUMERIC = "numeric";
	String RESPONSE_TYPE_TIME_OF_DAY = "timeOfDay";
	String RESPONSE_TYPE_DATE = "date";
	String RESPONSE_TYPE_TEXT = "text";
	String RESPONSE_TYPE_EMAIL = "email";
	String RESPONSE_TYPE_TIME_INTERVAL = "timeInterval";

	String RESPONSE_TYPE_SENSOR_ID = "sensorId";
	String RESPONSE_TYPE_ENROLLMENT_ID = "enrollementId";
	String RESPONSE_TYPE_ACTIVITY_ID = "activityId ";
			/*Added By Kavya*/
	String RESPONSE_TYPE_AUT_TEST = "autTest";
	String RESPONSE_TYPE_STROOP_TEST = "stroopTest";
	String RESPONSE_TYPE_ARITHMETIC_TEST = "arithmeticTest";

	String DATE_FORMAT_TYPE_DATE = "Date";
	String DATE_FORMAT_TYPE_DATE_TIME = "Date-Time";

	String NUMERIC_FORMAT_INTEGER = "Integer";
	String NUMERIC_FORMAT_DECIMAL = "Decimal";

	String TEXT_CHOICE_FORMAT_SINGLE = "Single";
	String TEXT_CHOICE_FORMAT_MULTIPLE = "Multiple";

	String THRESHOLD_RANGE_GT = "GT";
	String THRESHOLD_RANGE_LT = "LT";
	String THRESHOLD_RANGE_BTW = "BTW";

	String YES = "Yes";
	String NO = "No";

	String REPEAT_FREQUENCY_DAYS = "monday,tuesday,wednesday,thursday,friday";

	String APP_DEFAULT_VERSION = "1.0";

	String GBA_CURRENT_PATH = "gba.current.path";
	String GBA_DOCS_CONSENT_PATH = "gba.docs.consent.path";
	String FITBIT_REDIRECTION_URL = "fitbit.redirection.url";
	String DEFAULT_STUDY_ID = "1";

	int STROOP_TEST_COMPLETION_MIN_TIME = 100000; // in milli seconds 100 * 1000 = 100000
	int ARITHMETIC_TEST_COMPLETION_MIN_TIME = 250000; // in milli seconds 250 * 1000 = 250000
			/*Added By Kavya*/
	int AUT_TEST_COMPLETION_MIN_TIME = 180000; // in milli seconds 180 * 1000 = 180000
	
	String FITBIT_RT_URL = "fitbit.rt.url";
	String FITBIT_RT_HEADER_AUTHORIZATION = "fitbit.rt.header.authorization";
	String FITBIT_RT_HEADER_CONTENT_TYPE = "fitbit.rt.header.content_type";
	String FITBIT_RT_HEADER_GRANT_TYPE = "fitbit.rt.header.grant_type";

	String FITBIT_API_SLEEP = "fitbit.api.sleep";
	String FITBIT_API_HEARTRATE_RESTING = "fitbit.api.heartrate.resting";
	String FITBIT_API_HEARTRATE_INTRADAY = "fitbit.api.heartrate.intraday";
	String FITBIT_API_ACTIVITY_STEPS = "fitbit.api.activitysteps";

	String LASS4U_API_URL = "lass4u.api.url";
	String LASS4U_API_USERNAME = "lass4u.api.username";
	String LASS4U_API_PSSWORD = "lass4u.api.pssword";

	String FIREBASE_SERVER_KEY = "fcm.server.key";
	String FIREBASE_API_URL = "fcm.api.url";

	String PUSH_NOTIFICATION_MESSAGE_ENGLISH = "push.notification.message.en";
	String PUSH_NOTIFICATION_MESSAGE_SPANISH = "push.notification.message.es";
	String PUSH_NOTIFICATION_MESSAGE_CHINESE = "push.notification.message.zhCN";
	
	String PUSH_EXPIRY_NOTIFICATION_MESSAGE_ENGLISH = "push.notification.expiry.message.en";
	String PUSH_EXPIRY_NOTIFICATION_MESSAGE_SPANISH = "push.notification.expiry.message.es";
	String PUSH_EXPIRY_NOTIFICATION_MESSAGE_CHINESE = "push.notification.expiry.message.zhCN";

	String PUSH_NOTIFICATION_LASS4U_DATA_FAILED_ENGLISH = "push.notification.lass4u.data.failed.en";
	String PUSH_NOTIFICATION_LASS4U_DATA_FAILED_SPANISH = "push.notification.lass4u.data.failed.es";
	String PUSH_NOTIFICATION_LASS4U_DATA_FAILED_CHINESE = "push.notification.lass4u.data.failed.zhCN";

	String PUSH_NOTIFICATION_FITBIT_DATA_FAILED_ENGLISH = "push.notification.fitbit.data.failed.en";
	String PUSH_NOTIFICATION_FITBIT_DATA_FAILED_SPANISH = "push.notification.fitbit.data.failed.es";
	String PUSH_NOTIFICATION_FITBIT_DATA_FAILED_CHINESE = "push.notification.fitbit.data.failed.zhCN";

	String ANROID_BUNDLEID_KEY = "android.bundleid";
	String IOS_BUNDLEID_KEY = "ios.bundleid";

	String HEADER_VALUE_AUTH_TYPE = "Bearer ";
	String HEADER_KEY_AUTHORIZATION = "Authorization";
	String HEADER_KEY_CONTENT_TYPE = "Content-Type";
	String HEADER_KEY_GRANT_TYPE = "grant_type";
	String HEADER_KEY_REFRESH_TOKEN = "refresh_token";

	long MAX_BONUS_POINTS = 500L;
	String BONUS_LEVEL = "Bonus";
	
    public static final String INDIVIDUAL_GROUP= "Individual";
    public static final String BUILDING_GROUP = "Building";
    public static final String GENERAL_GROUP = "General";
    
    //Added by Fathima for cRAT activities
    public static final String RESPONSE_TYPE_CRAT_TEST = "cratTest ";
    String ACTIVITY_TYPE_CRAT_WEB = "CRAT";
    String ACTIVITY_TYPE_CRAT_TEST_TASK = "cratTestTask";
    int CRAT_TEST_COMPLETION_MIN_TIME = 120000; // in milli seconds 120 * 1000 = 180000
}
