/**
 * 
 */
package com.gba.ws.util;

/**
 * Provides Error codes details
 * 
 * @author Mohan
 * @createdOn Nov 10, 2017 3:08:17 PM
 */
public enum ErrorCode {

	EC_31(31, "Your account is temporarily deactivated"),

	EC_32(32, "Your account is currently suspended for misusing our services"),

	EC_33(33, "Please provide your valid account credentials"),

	EC_41(41, "Your password should have a length of 6 to 64 characters"
			+ " and must contain atleast one uppercase letter, one lowercase letter and one number"),

	EC_42(42, "Sorry, this email id is already in use, if you are the registered user,"
			+ " please sign in to the application"),

	EC_43(43, "Please enter all required fields"),

	EC_44(44, "Provided value is invalid"),

	EC_45(45, "Activity does not exist"),

	EC_51(51, "This access code has expired"),

	EC_52(52, "This access code is invalid"),

	EC_53(53, "This access code is invalid"),

	EC_61(61, "User does not exists"),

	EC_71(71, "Email ID does not exist"),

	EC_72(72, "Current password should not match with the new password"),

	EC_73(73, "Old password provided is invalid"),

	EC_91(91, "This enrollment ID has expired"),

	EC_92(92, "This enrollment ID is invalid"),

	EC_93(93, "You have not enrolled into the study"),

	EC_94(94, "You have not enrolled into the study"),

	EC_95(95, "Study ID does not exist"),

	EC_96(96, "You are already enrolled to the study"),

	EC_101(101, "Your session has expired"),

	EC_102(102, "Your session is expired"),

	EC_103(103, "Runs already completed"),

	EC_104(104, "You have already completed the participation target"),

	EC_105(105, "Please provide valid state"),

	EC_106(106, "Not able to get new Access Token"),

	EC_107(107, "Not able to get data from Fitbit"),

	EC_108(108, "Failed to receive consent document"),

	EC_109(109, "Invalid OS type"),

	EC_110(110, "Location is not attached to group"),
	
	EC_111(111, "No sensore data found."),
	
	EC_112(112, "Failed to the add data"),

	EC_200(200, "OK"),

	EC_400(400, "Bad Request was made"),

	EC_401(401, "You are not authorized to access this information"),

	EC_403(403, "You forbidden to access this information"),

	EC_404(404, "Information not found"),

	EC_406(406, "Information sent was not accepted"),

	EC_500(500, "Internal Server Error"),
	
	EC_502(502, "Failed to Update");

	private final int code;
	private final String errorMessage;

	/**
	 * @param code
	 *            the error code value
	 * @param errorMessage
	 *            the error message details
	 */
	private ErrorCode(int code, String errorMessage) {
		this.code = code;
		this.errorMessage = errorMessage;
	}

	public int code() {
		return this.code;
	}

	public String errorMessage() {
		return this.errorMessage;
	}

	/**
	 * @param code
	 *            the error code value
	 * @return the {@link ErrorCode} details
	 */
	public static ErrorCode fromCode(int code) {
		for (ErrorCode ec : ErrorCode.values()) {
			if (ec.code == code) {
				return ec;
			}
		}
		throw new IllegalArgumentException("No matching constant for [" + code + "]");
	}

	/**
	 * @author Mohan
	 * @param errorMessage
	 *            the error message details
	 * @return the {@link ErrorCode} details
	 */
	public static ErrorCode fromErrorMessage(String errorMessage) {
		for (ErrorCode ec : ErrorCode.values()) {
			if (ec.errorMessage == errorMessage) {
				return ec;
			}
		}
		throw new IllegalArgumentException("No matching constant for [" + errorMessage + "]");
	}
}
