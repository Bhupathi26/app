/**
 * 
 */
package com.gba.ws.util;

/**
 * Provides application enumerations
 * 
 * @author Mohan
 * @createdOn Nov 10, 2017 12:41:53 PM
 */
public enum AppEnums {

	// request params
	RP_REFRESH_TOKEN("refreshToken"),

	RP_EMAIL("email"),

	RP_PASSWORD("password"),

	RP_FIRST_NAME("firstName"),

	RP_LAST_NAME("lastName"),

	RP_LANGUAGE("language"),

	RP_AGREED_TNC("agreedTNC"),

	RP_TIMEZONE("timeZone"),

	RP_TOKEN("token"),

	RP_REMAINDERS("reminders"),

	RP_LASS4U("lass4u"),

	RP_FITBIT("fitbit"),

	RP_TEMPERATURE("temperature"),

	RP_OLD_PASSWORD("oldPassword"),

	RP_NEW_PASSWORD("newPassword"),

	RP_USER_ID("userId"),

	RP_STUDY_ID("studyId"),

	RP_CONSENT("consent"),

	RP_LAST_COMPLETED_DATE("lastCompletedDate"),

	RP_RUN_ID("runId"),

	RP_RUN_STATE("runState"),

	RP_CURRENT_DATE("currentDate"),

	RP_DURATION("duration"),

	RP_DEVICE_TOKEN("deviceToken"),

	RP_OS_TYPE("osType"),

	// question format keys
	QF_STYLE("style"),

	QF_PLACEHOLDER("placeholder"),

	QF_UNIT("unit"),

	QF_MIN_VALUE("minValue"),

	QF_MAX_VALUE("maxValue"),

	QF_MAX_LENGTH("maxLength"),

	QF_VALIDATION_REGEX("validationRegex"),

	QF_INVALID_MESSAGE("invalidMessage"),

	QF_MULTIPLE_LINES("multipleLines"),

	QF_TEXT_CHOICES("textChoices"),

	QF_SELECTION_STYLE("selectionStyle"),

	QF_DEFAULT("default"),

	QF_STEP("step"),

	QF_VALUE_PICKER_CHOICES("valuePickerChoices"),

	// threshold condition keys
	TC_TEMPERATURE("temperature"),

	TC_CO2("CO2"),

	TC_HUMIDITY("relativeHumidity"),

	TC_NOISE("noise"),

	TC_PM2_5("pm2.5"),

	TC_LIGHT("light"),

	TC_STEPS("steps"),

	TC_SLEEP("sleep"),

	TC_HEART_RATE("heartRate"),

	TC_RESTING_HEART_RATE("restingHeartRate"),

	TC_INTRADAY_HEART_RATE("intraDayHeartRate"),

	// lass4u and fitbit api failed count
	TC_LASS4U_FAILED_COUNT("lass4UCount"),

	TC_FITBIT_FAILED_COUNT("fitbitCount"),

	TC_FITBIT_USER_INFO("fitbitUserInfo"),

	// e-mail content keys
	MKV_FIRST_NAME("$firstName"),

	MKV_STUDY_NAME("$studyName"),

	MKV_ACCESS_CODE("$accessCode"),

	MKV_TEMP_PASSWPRD("$tempPassword"),

	MKV_ENROLLMENT_ID("$enrollmentId"),

	MKV_NEW_LEVEL("$newLevel"),

	MKV_OLD_LEVEL("$oldLevel"),

	MKV_LEVEL("$level"),

	MKV_ACTIVITY_NAME("$activityName"),

	// fitbit intra day criteria
	FT_TIME_FROM("$from"),

	FT_TIME_TO("$to"),
	
	//FT_DAY("$day"),
	FT_DAY("$day"),

	// push notification params
	PN_PRIORITY("priority"),

	PN_HIGH("high"),

	PN_MESSAGE("message"),

	PN_TITLE("title"),

	PN_ACTIVITY_ID("activityId"),

	PN_RUN_ID("runId"),

	PN_N_TYPE("nType"),

	PN_REGISTRATION_IDS("registration_ids"),

	PN_TO("to"),

	PN_NOTIFICATION("notification"),

	PN_DATA("data"),

	PN_CONTENT_AVAILABLE("content-available"),

	PN_BODY("body"),

	PN_TRIGGER("trigger"),

	// Query key names
	QK_USER_IDENTIFIER("userId"),

	QK_EMAIL("email"),

	QK_SESSION_AUTHORIZATION_KEY("sessionAuthKey"),

	QK_CREATED_ON("createdOn"),

	QK_ENROLLMENT_IDENTIFIER("enrollmentId"),

	QK_STUDY_IDENTIFIER("studyId"),

	QK_ACTIVITY_CONDITION_IDENTIFIER("activityConditionId"),

	QK_ACTIVITY_IDENTIFIER("activityId"),

	QK_USER_STUDIES_IDENTIFIER("userStudiesId"),

	QK_ACTIVITY_IDENTIFIER_LIST("activityIdList"),

	QK_CONDITION_IDENTIFIER_LIST("conditionIdList"),

	QK_CONDITION_IDENTIFIER("conditionId"),

	QK_ACTIVITY_RUN_IDENTIFIER("activityRunId"),

	QK_QUESTION_IDENTIFIER("questionId"),

	QK_CHOICES_IDENTIFIER("choicesId"),

	QK_AUTHORIZATION_INFO_IDENTIFIER("authInfoId"),

	QK_RUN_STATE("runState"),

	QK_GROUP_IDENTIFIER("groupId"),
	
	QK_GROUP_IDENTIFIER_LIST("groupIdList"),

	QK_CONSENT_IDENTIFIER("consentId"),

	QK_SESSION_EXPIRED_DATE("sessionExpiredDate"),

	QK_USER_IDENTIFIER_LIST("userIdsList"),

	QK_ACTIVITY_SUB_TYPE("activitySubType"),

	QK_END_DATE("endDate"),
	
	MKV_RUN_END_TIME("$expiryMinutes"),
	
	
	RESP_ST_TYPE("jsonFile");

	private final String value;

	/**
	 * @param value
	 */
	private AppEnums(String value) {
		this.value = value;
	}

	public String value() {
		return this.value;
	}

	/**
	 * @author Mohan
	 * @param value
	 * @return {@link AppEnums}
	 */
	public static AppEnums fromValue(String value) {
		for (AppEnums ae : AppEnums.values()) {
			if (ae.value == value) {
				return ae;
			}
		}
		throw new IllegalArgumentException("No matching constant for [" + value + "]");
	}
}
