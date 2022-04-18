package com.gba.ws.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gba.ws.bean.ErrorBean;
import com.gba.ws.bean.ErrorResponse;
import com.gba.ws.model.EnrollmentTokensDto;
import com.google.gson.Gson;

/**
 * Provides application util methods.
 * 
 * @author Mohan
 * @createdOn Jan 8, 2018 4:15:08 PM
 */
public class AppUtil {

	private static final Logger LOGGER = Logger.getLogger(AppUtil.class);

	// private constructor to hide the implicit public one.
	private AppUtil() {
		super();
	}
	private static Map<String, String> appConfigProperties;
	/**
	 * To fetch the message resource properties
	 * 
	 * @author Mohan
	 * @return key values for message resource propeties file
	 */
	public static Map<String, String> getAppProperties() {
		LOGGER.info("INFO: AppUtil - getAppProperties() :: starts");
		Map<String, String> appHM = new HashMap<>();
		try {
			ResourceBundle appRB = ResourceBundle.getBundle("messageResource");
			Enumeration<String> appKeys = appRB.getKeys();
			while (appKeys.hasMoreElements()) {
				String key = appKeys.nextElement();
				String value = appRB.getString(key);
				appHM.put(key, value);
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: AppUtil - getAppProperties()", e);
		}
		LOGGER.info("INFO: AppUtil - getAppProperties() :: ends");
		return appHM;
	}

	/**
	 * To fetch the authorization resource properties
	 * 
	 * @author Mohan
	 * @return key values for authorization resource propeties file
	 */
	public static Map<String, String> getAuthorizationProperties() {
		LOGGER.info("INFO: AppUtil - getAuthorizationProperties() :: starts");
		Map<String, String> authHM = new HashMap<>();
		try {
			ResourceBundle authRB = ResourceBundle.getBundle("authorizationResource");
			Enumeration<String> authKeys = authRB.getKeys();
			while (authKeys.hasMoreElements()) {
				String key = authKeys.nextElement();
				String value = authRB.getString(key);
				authHM.put(key, value);
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: AppUtil - getAuthorizationProperties()", e);
		}
		LOGGER.info("INFO: AppUtil - getAuthorizationProperties() :: ends");
		return authHM;
	}

	/**
	 * Provides failure response in response header
	 * 
	 * @author Mohan
	 * @param status
	 *            the status of the header
	 * @param title
	 *            the title of the header
	 * @param detail
	 *            the details of the header
	 * @param response
	 *            the response
	 */
	public static void getFailureResponse(String status, String title, String detail, HttpServletResponse response) {
		LOGGER.info("INFO: AppUtil - getFailureResponse() :: starts");
		try {
			response.setHeader("status", status);
			response.setHeader("title", title);
			response.setHeader("StatusMessage", detail);
		} catch (Exception e) {
			LOGGER.error("ERROR: AppUtil - getFailureResponse()", e);
		}
		LOGGER.info("INFO: AppUtil - getFailureResponse() :: ends");
	}

	/**
	 * Provides error response in the response body
	 * 
	 * @author Mohan
	 * @param errorResponse
	 *            the {@link ErrorResponse} details
	 * @param response
	 *            the {@link HttpServletResponse} response
	 */
	public static void setErrorResponse(ErrorResponse errorResponse, HttpServletResponse response) {
		LOGGER.info("INFO: AppUtil - setErrorResponse() :: starts");
		try {
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
			response.getWriter().flush();
			response.getWriter().close();
		} catch (Exception e) {
			LOGGER.error("ERROR: AppUtil - setErrorResponse()", e);
		}
		LOGGER.info("INFO: AppUtil - setErrorResponse() :: ends");
	}

	/**
	 * Provides Encrypted password
	 * 
	 * @author Mohan
	 * @param password
	 *            the password to be encrypted
	 * @return the encrypted password
	 */
	public static String getEncryptedPassword(String password) {
		LOGGER.info("INFO: AppUtil - getEncryptedPassword() :: starts");
		String hashedPassword = null;
		try {
			if (!StringUtils.isEmpty(password)) {
				BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
				hashedPassword = passwordEncoder.encode(password);
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: AppUtil - getEncryptedPassword()", e);
		}
		LOGGER.info("INFO: AppUtil - getEncryptedPassword() :: ends");
		return hashedPassword;
	}

	/**
	 * Compares the encrypted password and raw password are same or not
	 * 
	 * @author Mohan
	 * @param encodedPassword
	 *            the encrypted password
	 * @param rawPassword
	 *            the non-encrypted password
	 * @return true or false
	 */
	public static boolean compareEncryptedPassword(String encodedPassword, String rawPassword) {
		LOGGER.info("INFO: AppUtil - compareEncryptedPassword() :: starts");
		boolean isMatch = false;
		try {
			if (StringUtils.isNotEmpty(encodedPassword)) {
				BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
				isMatch = passwordEncoder.matches(rawPassword, encodedPassword);
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: AppUtil - compareEncryptedPassword()", e);
		}
		LOGGER.info("INFO: AppUtil - compareEncryptedPassword() :: ends");
		return isMatch;
	}

	/**
	 * To append the milliseconds to file path for auto refresh of file changes
	 * 
	 * @author Mohan
	 * @return the new file path
	 */
	public static String getMilliSecondsForImagePath() {
		LOGGER.info("INFO: AppUtil - getMilliSecondsForImagePath() :: starts");
		StringBuilder milliSeconds = new StringBuilder().append("?v=").append(Calendar.getInstance().getTimeInMillis());
		LOGGER.info("INFO: AppUtil - getMilliSecondsForImagePath() :: ends");
		return milliSeconds.toString();
	}

	/**
	 * Get the current date
	 * 
	 * @author Mohan
	 * @return the current date of Server
	 */
	public static String getCurrentDate() {
		LOGGER.info("INFO: AppUtil - getCurrentDate() :: starts");
		String currentDate = "";
		try {
			LocalDate now = LocalDate.now();
			DateTimeFormatter datePattern = DateTimeFormatter.ofPattern(AppConstants.SDF_DATE_FORMAT); 
			currentDate =  now.format(datePattern);
		} catch (Exception e) {
			LOGGER.error("ERROR: AppUtil - getCurrentDate()", e);
		}
		LOGGER.info("INFO: AppUtil - getCurrentDate() :: ends");
		return currentDate;
	}
	
	/**
	 * Get the current date with timeZone param
	 * 
	 * @author Kavya
	 * @return the current date of User
	 */
	
	public static String getCurrentUserDate(String timeZone) {
		LOGGER.info("INFO: AppUtil - getCurrentUserDate() :: starts");
		String currentDate = "";
		try {
			DateTimeFormatter datePattern = DateTimeFormatter.ofPattern(AppConstants.SDF_DATE_FORMAT); 
			LocalDate userDate = LocalDate.now(ZoneId.of(timeZone));
			currentDate = userDate.format(datePattern);
		} catch (Exception e) {
			LOGGER.error("ERROR: AppUtil - getCurrentUserDate()", e);
		}
		LOGGER.info("INFO: AppUtil - getCurrentUserDate() :: ends");
		return currentDate;
	}

	/**
	 * Get the current date time
	 * 
	 * @author Mohan
	 * @return the current date time
	 */
	public static String getCurrentDateTime() {
		LOGGER.info("INFO: AppUtil - getCurrentDateTime() :: starts");
		String currentDateTime = "";
		try {
			LocalDateTime now = LocalDateTime.now(ZoneId.of(AppConstants.SERVER_TIMEZONE));
			DateTimeFormatter datePattern = DateTimeFormatter.ofPattern(AppConstants.SDF_DATE_TIME_FORMAT); 
			currentDateTime =  now.format(datePattern);
		} catch (Exception e) {
			LOGGER.error("ERROR: AppUtil - getCurrentDateTime()", e);
		}
		LOGGER.info("INFO: AppUtil - getCurrentDateTime() :: ends");
		return currentDateTime;
	}

	/**
	 * Get the current date time of timeZone
	 * 
	 * @author Kavya
	 * @return the current date time for timeZone
	 */
	public static String getCurrentDateTime(String timeZone) {
		LOGGER.info("INFO: AppUtil - getCurrentDateTime() :: starts");
		String currentDateTime = "";
		try {
			LocalDateTime now = LocalDateTime.now(ZoneId.of(timeZone));
			DateTimeFormatter datePattern = DateTimeFormatter.ofPattern(AppConstants.SDF_DATE_TIME_FORMAT); 
			currentDateTime =  now.format(datePattern);
		} catch (Exception e) {
			LOGGER.error("ERROR: AppUtil - getCurrentDateTime()", e);
		}
		LOGGER.info("INFO: AppUtil - getCurrentDateTime() :: ends");
		return currentDateTime;
	}
	
	
	
	/**
	 * Get converted base64 value of an image
	 * 
	 * @author Mohan
	 * @param image
	 *            the image to be converted to base64
	 * @return the base64 value
	 */
	public static String getBase64Image(String image) {
		LOGGER.info("INFO: AppUtil - getBase64Image() :: starts");
		String base64Image = "";
		try {
			byte[] imageBytes = IOUtils.toByteArray(new URL(image));
			base64Image = Base64.getEncoder().encodeToString(imageBytes);
		} catch (Exception e) {
			LOGGER.error("ERROR: AppUtil - getBase64Image()", e);
		}
		LOGGER.info("INFO: AppUtil - getBase64Image() :: ends");
		return base64Image;
	}

	/**
	 * Format the input date or date time for the provided output format
	 * 
	 * @author Mohan
	 * @param inputDate
	 *            the input date time
	 * @param inputFormat
	 *            the input date time format
	 * @param outputFormat
	 *            the output date time format
	 * @return the formatted date time
	 */
	public static String getFormattedDate(String inputDate, String inputFormat, String outputFormat) {
		LOGGER.info("INFO: AppUtil - getFormattedDate() :: starts");
		String finalDateTime = "";
		LocalDateTime formattedDate;
		if (StringUtils.isNotEmpty(inputDate) && !"null".equalsIgnoreCase(inputDate)){
			try {
				DateTimeFormatter oldPattern = DateTimeFormatter.ofPattern(inputFormat);
				DateTimeFormatter newPattern = DateTimeFormatter.ofPattern(outputFormat);
				formattedDate = LocalDateTime.parse(inputDate, oldPattern);
				finalDateTime = formattedDate.format(newPattern);
			} catch (Exception e){
				LOGGER.error("ERROR: AppUtil - getFormattedDate()", e);
			}
		}
		LOGGER.info("INFO: AppUtil - getFormattedDate() :: ends");
		return finalDateTime;
	}
	
	
	/**
	 * Format the input date or date time for the provided output format
	 * 
	 * @author Kavya
	 * @param inputDate
	 *            the input date time
	 * @param inputFormat
	 *            the input date time format
	 * @param outputFormat
	 *            the output date time format
	 * @return the formatted date time
	 */
	public static String getFormattedDateTimeESTFormat(String inputDate, String inputFormat, String outputFormat) {
		LOGGER.info("INFO: AppUtil - getFormattedDate() :: starts");
		String finalDateTime = "";
		LocalDateTime formattedDate;
		ZoneId etZoneId = ZoneId.of("America/New_York");
		if (StringUtils.isNotEmpty(inputDate) && !"null".equalsIgnoreCase(inputDate)){
			try {
				DateTimeFormatter oldPattern = DateTimeFormatter.ofPattern(inputFormat);
				DateTimeFormatter newPattern = DateTimeFormatter.ofPattern(outputFormat);
				
				formattedDate = LocalDateTime.parse(inputDate, oldPattern);
				ZonedDateTime currentISTime = formattedDate.atZone(etZoneId);
				ZonedDateTime currentETime =  currentISTime.withZoneSameInstant(etZoneId); 
				finalDateTime = newPattern.format(currentETime);
			} catch (Exception e){
				LOGGER.error("ERROR: AppUtil - getFormattedDate()", e);
			}
		}
		LOGGER.info("INFO: AppUtil - getFormattedDate() :: ends");
		return finalDateTime;
	}


	

	/**
	 * Get the platform type for the provided Authorization credencials
	 * 
	 * @author Mohan
	 * @param authCredentials
	 *            the Authorization key details
	 * @return the platform type
	 */
	public static String platformType(String authCredentials) {
		LOGGER.info("INFO: AppUtil - platformType() :: starts");
		String platform = "";
		try {
			if (StringUtils.isNotEmpty(authCredentials) && authCredentials.contains("Basic")) {
				final String encodedAuthorization = authCredentials.replaceFirst("Basic" + " ", "");
				byte[] decodedBytes = Base64.getMimeDecoder().decode(encodedAuthorization);
				String bundleIdAndAppToken = new String(decodedBytes, AppConstants.CHARSET_ENCODING_UTF_8);
				if (bundleIdAndAppToken.contains(":")) {
					final StringTokenizer tokenizer = new StringTokenizer(bundleIdAndAppToken, ":");
					final String bundleId = tokenizer.nextToken();
					final String appToken = tokenizer.nextToken();
					if ((Arrays
							.asList(AppUtil.getAuthorizationProperties().get(AppConstants.ANROID_BUNDLEID_KEY).trim()
									.split(","))
							.contains(bundleId)
							|| Arrays.asList(AppUtil.getAuthorizationProperties().get(AppConstants.IOS_BUNDLEID_KEY)
									.trim().split(",")).contains(bundleId))
							&& AppUtil.getAuthorizationProperties().containsValue(appToken)) {
						final String device = new StringTokenizer(appToken, ".").nextToken();
						if (device.equals(AppConstants.PLATFORM_ANDROID)) {
							platform = AppConstants.PLATFORM_TYPE_ANDROID;
						} else {
							platform = AppConstants.PLATFORM_TYPE_IOS;
						}
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: AppUtil - platformType()", e);
		}
		LOGGER.info("INFO: AppUtil - platformType() :: ends");
		return platform;
	}

	/**
	 * To add the input minutes to input date time
	 * 
	 * @author Mohan
	 * @param dateTime
	 *            the input date time
	 * @param minutes
	 *            the input minutes
	 * @return the new date time with added minutes
	 */
	public static String addMinutes(String dateTime, int minutes) {
		LOGGER.info("INFO: AppUtil - addMinutes() :: starts");
		String newDateTime = "";
		try {
			if(StringUtils.isNotEmpty(dateTime)) {
			  DateTimeFormatter datePattern = DateTimeFormatter.ofPattern(AppConstants.SDF_DATE_TIME_FORMAT); 
				LocalDateTime localDateTime = LocalDateTime.parse(dateTime, datePattern);
				localDateTime = localDateTime.plusMinutes(minutes);
				newDateTime = localDateTime.format(datePattern);
			}
	
		} catch (Exception e) {
			LOGGER.error("ERROR: AppUtil - addMinutes()", e);
		}
		LOGGER.info("INFO: AppUtil - addMinutes() :: ends");
		return newDateTime;
	}

	/**
	 * To add the input days to input date time and format date time
	 * 
	 * @author Mohan
	 * @param inputDateTime
	 *            the input date time
	 * @param days
	 *            the input days
	 * @param format
	 *            the input format
	 * @return the new date time with added days
	 */
	public static String addDays(String inputDateTime,String inputformat , int days, String format) {
		LOGGER.info("INFO: AppUtil - addDays() :: starts");
		String updatedDateTime = "";
		try {
			LOGGER.info("INFO: AppUtil - addDays() inputDateTime {} ,String inputformat {} , int days {}, String format {}"+" "+inputDateTime+" "+inputformat+" "+days+" "+format);
			if(StringUtils.isNotEmpty(inputDateTime)) {
				if(inputDateTime.length()<18)
					inputDateTime=inputDateTime+" 00:00:00";
				  DateTimeFormatter datePattern = DateTimeFormatter.ofPattern(inputformat); 
				  DateTimeFormatter datePattern2 = DateTimeFormatter.ofPattern(format); 
					LocalDateTime localDateTime = LocalDateTime.parse(inputDateTime, datePattern);
					localDateTime = localDateTime.plusDays(days);
					updatedDateTime = localDateTime.format(datePattern2);
					
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: AppUtil - addDays()", e);
		}
		LOGGER.info("INFO: AppUtil - addDays() :: ends");
		return updatedDateTime;
	}
	
	public static String addDaysDate(String inputDateTime,String inputformat , int days, String format) {
		LOGGER.info("INFO: AppUtil - addDays() :: starts");
		String updatedDateTime = "";
		try {
			if(StringUtils.isNotEmpty(inputDateTime)) {
				  DateTimeFormatter datePattern = DateTimeFormatter.ofPattern(inputformat); 
				  DateTimeFormatter datePattern2 = DateTimeFormatter.ofPattern(format); 
					LocalDate localDate = LocalDate.parse(inputDateTime, datePattern);
					localDate = localDate.plusDays(days);
					updatedDateTime = localDate.format(datePattern2);
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: AppUtil - addDays()", e);
		}
		LOGGER.info("INFO: AppUtil - addDays() :: ends");
		return updatedDateTime;
	}

	/**
	 * Add weeks to the input date time
	 * 
	 * @author Mohan
	 * @param dateTime
	 *            the input date time
	 * @param weeks
	 *            the input weeks
	 * @return the new date time with added weeks
	 */
	public static String addWeeks(String dateTime, int weeks) {
		LOGGER.info("INFO: AppUtil - addWeeks() :: starts");
		String newDateTime = "";
		try {
			SimpleDateFormat date = AppConstants.SDF_DATE_TIME;
			Date dt = date.parse(dateTime);
			Calendar cal = Calendar.getInstance();
			cal.setTime(dt);
			cal.add(Calendar.WEEK_OF_MONTH, weeks);
			Date newDate = cal.getTime();
			newDateTime = date.format(newDate);
		} catch (Exception e) {
			LOGGER.error("ERROR: AppUtil - addWeeks()", e);
		}
		LOGGER.info("INFO: AppUtil - addWeeks() :: ends");
		return newDateTime;
	}

	/**
	 * Get the newly generated UUID
	 * 
	 * @author Mohan
	 * @return the newly generated UUID value
	 */
	public static String uniqueUUID() {
		LOGGER.info("INFO: AppUtil - uniqueUUID() :: starts");
		String uniqueiD = "";
		try {
			uniqueiD = UUID.randomUUID().toString();
		} catch (Exception e) {
			LOGGER.error("ERROR: AppUtil - uniqueUUID()", e);
		}
		LOGGER.info("INFO: AppUtil - uniqueUUID() :: ends");
		return uniqueiD;
	}

	/**
	 * Save the signed consent document in the path specified
	 * 
	 * @author Mohan
	 * @param content
	 *            the consent document content
	 * @param userId
	 *            the user identifier
	 * @param studyId
	 *            the study identifier
	 * @return the saved consent document name
	 */
	public static String saveSignedConsentDocument(String content, String userId, String studyId) {
		LOGGER.info("INFO: AppUtil - saveSignedConsentDocument() :: starts");
		File serverFile;
		String consentFileName = null;
		try {
			if (StringUtils.isNotEmpty(content)) {
				byte[] bytes = Base64.getDecoder().decode(content.replaceAll("\n", ""));
				String currentPath =  System.getProperty(AppUtil.getAppProperties().get("gba.current.path"));
				System.out.println("Current Path"+currentPath);
				String rootPath = currentPath.replace('\\', '/')
						+ AppUtil.getAppProperties().get("gba.docs.upload.path");
				File directory = new File(rootPath + File.separator);
				if (!directory.exists()) {
					directory.mkdirs();
				}
				consentFileName = AppUtil.getStandardFileName(userId, studyId);
				LOGGER.warn("WARN: AppUtil - saveSignedConsentDocument() :: CONSENT FILE NAME ==> " + consentFileName);
				serverFile = new File(directory.getAbsolutePath() + File.separator + consentFileName);
				saveFileInPath(serverFile, bytes);
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: AppUtil - saveSignedConsentDocument()", e);
		}
		LOGGER.info("INFO: AppUtil - saveSignedConsentDocument() :: ends");
		return consentFileName;
	}

	/**
	 * Save the file in path
	 * 
	 * @author Mohan
	 * @param serverFile
	 *            the connsent document file
	 * @param bytes
	 *            the byte array of file
	 */
	public static void saveFileInPath(File serverFile, byte[] bytes) {
		LOGGER.info("INFO: AppUtil - saveFileInPath() :: starts");
		try (FileOutputStream fileOutputStream = new FileOutputStream(serverFile);
				BufferedOutputStream stream = new BufferedOutputStream(fileOutputStream);) {
			stream.write(bytes);
			LOGGER.warn("WARN: AppUtil - saveFileInPath() :: CONSENT FILE PATH ==> " + serverFile.getAbsolutePath());
		} catch (Exception e) {
			LOGGER.error("ERROR: AppUtil - saveFileInPath()", e);
		}
		LOGGER.info("INFO: AppUtil - saveFileInPath() :: ends");
	}

	/**
	 * Get the standard file name for the provided user and study identifier
	 * 
	 * @author Mohan
	 * @param userId
	 *            the user identifier
	 * @param studyId
	 *            the study identifier
	 * @return the standard file name
	 */
	public static String getStandardFileName(String userId, String studyId) {
		LOGGER.info("INFO: AppUtil - getStandardFileName() :: starts");
		String fileName = null;
		try {
			fileName = new StringBuilder().append("ForHealthApp_")
					.append(new SimpleDateFormat("MMddyyyyHHmmss").format(new Date())).append(studyId).append(userId)
					.append(".pdf").toString();
		} catch (Exception e) {
			LOGGER.error("ERROR: AppUtil - getStandardFileName()", e);
		}
		LOGGER.info("INFO: AppUtil - getStandardFileName() :: ends");
		return fileName;
	}

	/**
	 * Get the randomly generated alphanumeric value
	 * 
	 * @author Mohan
	 * @return the random alphanumeric value
	 */
	public static String randomAlphanumeric() {
		LOGGER.info("INFO: AppUtil - randomAlphanumeric() :: starts");
		final String matchExp = "(([a-zA-Z]+[0-9]+)+|(([0-9]+[a-zA-Z]+)+))[0-9a-zA-Z]*";
		String alphaNumeric = "";
		try {
			do {
				alphaNumeric = RandomStringUtils.randomAlphanumeric(AppConstants.AUTH_KEY_LENGTH);
			} while (!alphaNumeric.matches(matchExp));
		} catch (Exception e) {
			LOGGER.error("ERROR: AppUtil - randomAlphanumeric()", e);
		}
		LOGGER.info("INFO: AppUtil - randomAlphanumeric() :: ends");
		return alphaNumeric;
	}

	/**
	 * Remove the duplicate consent document for the enrollment identifier
	 * 
	 * @param fileName
	 *            the consent document file name
	 * @return true or false
	 */
	public static boolean deleteOldConsentPDF(String fileName) {
		LOGGER.info("INFO: AppUtil - deleteOldConsentPDF() :: starts");
		boolean isDeleted = false;
		try {
			if (StringUtils.isNotEmpty(fileName)) {
				String currentPath = System.getProperty(AppUtil.getAppProperties().get("gba.current.path"));
				String rootPath = currentPath.replace('\\', '/')
						+ AppUtil.getAppProperties().get("gba.docs.consent.path");
				String filePath = rootPath + fileName; // get the file download path
				LOGGER.warn("WARN: AppUtil - deleteOldConsentPDF() :: CONSENT FILE DOWNLOAD PATH ==> " + filePath);

				isDeleted = Files.deleteIfExists(Paths.get(filePath));
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: AppUtil - deleteOldConsentPDF()", e);
		}
		LOGGER.info("INFO: AppUtil - deleteOldConsentPDF() :: ends");
		return isDeleted;
	}

	/**
	 * Get the formatted date time for the provided timezone
	 * 
	 * @author Mohan
	 * @param inputDate
	 *            the input date time
	 * @param inputFormat
	 *            the input format
	 * @param outputFormat
	 *            the output format
	 * @param timeZone
	 *            the input timezone
	 * @return the formatted date time
	 */
	public static String getFormattedDateByTimeZone(String inputDate, String inputFormat, String outputFormat,
			String timeZone) {
		LOGGER.info("INFO: AppUtil - getFormattedDateByTimeZone() :: starts");
		String finalDate = "";
		try {
			if (StringUtils.isNotEmpty(inputDate)) {
				SimpleDateFormat sdf = new SimpleDateFormat(inputFormat);
				sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
				Date formattedDate = new Date(sdf.parse(inputDate).getTime());

				sdf = new SimpleDateFormat(outputFormat);
				sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
				finalDate = sdf.format(formattedDate);
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: AppUtil - getFormattedDateByTimeZone()", e);
		}
		LOGGER.info("INFO: AppUtil - getFormattedDateByTimeZone() :: ends");
		return finalDate;
	}

	/**
	 * Get the name of the day for the provided date or date time
	 * 
	 * @author Mohan
	 * @param input
	 *            the input date or date time
	 * @return the name of the day
	 */
	public static String getDayByDate(String input) {
		LOGGER.info("INFO: AppUtil - getDayByDate() :: starts");
		String day = "";
		try {
			if (StringUtils.isNotEmpty(input)) {
				SimpleDateFormat newDateFormat = new SimpleDateFormat(AppConstants.SDF_DATE_FORMAT);
				Date date = newDateFormat.parse(input);
				newDateFormat.applyPattern(AppConstants.SDF_DAY);
				day = newDateFormat.format(date).toLowerCase();
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: AppUtil - getDayByDate()", e);
		}
		LOGGER.info("INFO: AppUtil - getDayByDate() :: ends");
		return day;
	}

	/**
	 * Get the number of days between two input date's
	 * 
	 * @author Mohan
	 * @param startDate
	 *            the from date
	 * @param endDate
	 *            the to date
	 * @return the number of days
	 */
	public static int noOfDaysBetweenTwoDates(String startDate, String endDate) {
		LOGGER.info("INFO: AppUtil - getDayByDate() :: starts");
		int daysDiff = 0;
		try {
			long diff = AppConstants.SDF_DATE.parse(endDate).getTime()
					- AppConstants.SDF_DATE.parse(startDate).getTime();
			long diffDays = diff / (24 * 60 * 60 * 1000) + 1;
			daysDiff = (int) diffDays;
		} catch (Exception e) {
			LOGGER.error("ERROR: AppUtil - getDayByDate()", e);
		}
		LOGGER.info("INFO: AppUtil - getDayByDate() :: ends");
		return daysDiff;
	}

	/**
	 * Get the format pattern for the provided date time format
	 * 
	 * @author Mohan
	 * @param date
	 *            the input date
	 * @param time
	 *            the input time
	 * @param inputTimeZoneFormat
	 *            the input date time format
	 * @return the timezone formatted date time
	 */
	public static String getUserTimeZoneRunFormat(String date, String time, String inputTimeZoneFormat) {
		LOGGER.info("INFO: AppUtil - getUserTimezoneRuns() :: starts");
		String userTimeZoneFormat = "";
		try {
			userTimeZoneFormat = new StringBuilder().append(date).append(inputTimeZoneFormat.substring(10, 11))
					.append(time).append(inputTimeZoneFormat.substring(19, inputTimeZoneFormat.length())).toString();
		} catch (Exception e) {
			LOGGER.error("ERROR: AppUtil - getUserTimezoneRuns()", e);
		}
		LOGGER.info("INFO: AppUtil - getUserTimezoneRuns() :: ends");
		return userTimeZoneFormat;
	}

	
	/**
	 * Get the response details when Internal server error occured
	 * 
	 * @author Mohan
	 * @return the response entitiy details for the cause
	 */
	public static ResponseEntity<Object> httpResponseForInternalServerError() {
		LOGGER.info("INFO: AppUtil - httpResponseForInternalServerError() :: starts");
		ErrorResponse errorResponse = new ErrorResponse();
		try {
			errorResponse.setError(
					new ErrorBean().setCode(ErrorCode.EC_500.code()).setMessage(ErrorCode.EC_500.errorMessage()));
		} catch (Exception e) {
			LOGGER.error("ERROR: AppUtil - httpResponseForInternalServerError()", e);
		}
		LOGGER.info("INFO: AppUtil - httpResponseForInternalServerError() :: ends");
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Get the response details when request is bad
	 * 
	 * @author Mohan
	 * @return the response entitiy details for the cause
	 */
	public static ResponseEntity<Object> httpResponseForBadRequest(int errorCode) {
		LOGGER.info("INFO: AppUtil - httpResponseForBadRequest() :: starts");
		ErrorResponse errorResponse = new ErrorResponse();
		int code = 0;
		String errorMessage = "";
		try {
			switch (errorCode) {
				case 400:
					code = ErrorCode.EC_400.code();
					errorMessage = ErrorCode.EC_400.errorMessage();
					break;
				case 43:
					code = ErrorCode.EC_43.code();
					errorMessage = ErrorCode.EC_43.errorMessage();
					break;
				default:
					break;
			}

			errorResponse.setError(new ErrorBean().setCode(code).setMessage(errorMessage));
		} catch (Exception e) {
			LOGGER.error("ERROR: AppUtil - httpResponseForBadRequest()", e);
		}
		LOGGER.info("INFO: AppUtil - httpResponseForBadRequest() :: ends");
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Get the response details for not acceptable
	 * 
	 * @author Mohan
	 * @return the response entitiy details for the cause
	 */
	public static ResponseEntity<Object> httpResponseForNotAcceptable(int errorCode) {
		LOGGER.info("INFO: AppUtil - httpResponseForNotAcceptable() :: starts");
		ErrorResponse errorResponse = new ErrorResponse();
		int code = 0;
		String errorMessage = "";
		try {
			switch (errorCode) {
				case 406:
					code = ErrorCode.EC_406.code();
					errorMessage = ErrorCode.EC_406.errorMessage();
					break;
				case 44:
					code = ErrorCode.EC_44.code();
					errorMessage = ErrorCode.EC_44.errorMessage();
					break;
				default:
					break;
			}

			errorResponse.setError(new ErrorBean().setCode(code).setMessage(errorMessage));
		} catch (Exception e) {
			LOGGER.error("ERROR: AppUtil - httpResponseForNotAcceptable()", e);
		}
		LOGGER.info("INFO: AppUtil - httpResponseForNotAcceptable() :: ends");
		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_ACCEPTABLE);
	}

	/**
	 * Get the response details for not found
	 * 
	 * @author Mohan
	 * @return the response entitiy details for the cause
	 */
	public static ResponseEntity<Object> httpResponseForNotFound(int errorCode) {
		LOGGER.info("INFO: AppUtil - httpResponseForNotFound() :: starts");
		ErrorResponse errorResponse = new ErrorResponse();
		int code = 0;
		String errorMessage = "";
		try {
			switch (errorCode) {
				case 404:
					code = ErrorCode.EC_404.code();
					errorMessage = ErrorCode.EC_404.errorMessage();
					break;
				case 45:
					code = ErrorCode.EC_45.code();
					errorMessage = ErrorCode.EC_45.errorMessage();
					break;
				case 95:
					code = ErrorCode.EC_95.code();
					errorMessage = ErrorCode.EC_95.errorMessage();
					break;
				case 71:
					code = ErrorCode.EC_71.code();
					errorMessage = ErrorCode.EC_71.errorMessage();
					break;
				case 111:
					code = ErrorCode.EC_111.code();
					errorMessage = ErrorCode.EC_111.errorMessage();
					break;
				case 112:
					code = ErrorCode.EC_112.code();
					errorMessage = ErrorCode.EC_112.errorMessage();
					break;
				default:
					break;
			}

			errorResponse.setError(new ErrorBean().setCode(code).setMessage(errorMessage));
		} catch (Exception e) {
			LOGGER.error("ERROR: AppUtil - httpResponseForNotFound()", e);
		}
		LOGGER.info("INFO: AppUtil - httpResponseForNotFound() :: ends");
		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	}
	
	public static ResponseEntity<Object> httpResponseNotFound(int errorCode) {
		LOGGER.info("INFO: AppUtil - httpResponseForNotFound() :: starts");
		ErrorResponse errorResponse = new ErrorResponse();
		int code = 0;
		String errorMessage = "";
		try {
			switch (errorCode) {
				case 43:
					code = ErrorCode.EC_43.code();
					errorMessage = ErrorCode.EC_43.errorMessage();
					break;
				case 111:
					code = ErrorCode.EC_111.code();
					errorMessage = ErrorCode.EC_111.errorMessage();
					break;
				default:
					break;
			}

			errorResponse.setError(new ErrorBean().setCode(code).setMessage(errorMessage));
		} catch (Exception e) {
			LOGGER.error("ERROR: AppUtil - httpResponseForNotFound()", e);
		}
		LOGGER.info("INFO: AppUtil - httpResponseForNotFound() :: ends");
		return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
	}

	/**
	 * Get the response details when un-authorized
	 * 
	 * @author Mohan
	 * @return the response entitiy details for the cause
	 */
	public static ResponseEntity<Object> httpResponseForUnAuthorized(int errorCode) {
		LOGGER.info("INFO: AppUtil - httpResponseForUnAuthorized() :: starts");
		ErrorResponse errorResponse = new ErrorResponse();
		int code = 0;
		String errorMessage = "";
		try {
			switch (errorCode) {
				case 401:
					code = ErrorCode.EC_401.code();
					errorMessage = ErrorCode.EC_401.errorMessage();
					break;
				case 94:
					code = ErrorCode.EC_94.code();
					errorMessage = ErrorCode.EC_94.errorMessage();
					break;
				default:
					break;
			}

			errorResponse.setError(new ErrorBean().setCode(code).setMessage(errorMessage));
		} catch (Exception e) {
			LOGGER.error("ERROR: AppUtil - httpResponseForUnAuthorized()", e);
		}
		LOGGER.info("INFO: AppUtil - httpResponseForUnAuthorized() :: ends");
		return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
	}

	/**
	 * Read the enrollment id's from the XLSX file
	 * 
	 * @author Mohan
	 * @param fileName
	 *            the file path
	 * @return the {@link EnrollmentTokensDto} details
	 */
	public static List<EnrollmentTokensDto> readXLSXFile(String fileName) {
		LOGGER.info("INFO: AppUtil - readXLSXFile() :: starts");
		List<EnrollmentTokensDto> enrollmentTokensList = new ArrayList<>();
		InputStream xlsxFileToRead = null;
		XSSFWorkbook workbook = null;
		try {
			xlsxFileToRead = new FileInputStream(fileName);

			// Getting the workbook instance for xlsx file
			workbook = new XSSFWorkbook(xlsxFileToRead);

			// getting the first sheet from the workbook using sheet name.
			// We can also pass the index of the sheet which starts from '0'.
			XSSFSheet sheet = workbook.getSheetAt(0);
			XSSFRow row;
			XSSFCell cell;

			// Iterating all the rows in the sheet
			Iterator<Row> rows = sheet.rowIterator();

			while (rows.hasNext()) {
				EnrollmentTokensDto enrollmentToken = new EnrollmentTokensDto();
				enrollmentToken.setIsActive(true);
				row = (XSSFRow) rows.next();

				// Iterating all the cells of the current row
				Iterator<Cell> cells = row.cellIterator();
				while (cells.hasNext()) {
					// read the cell content
					// XSSFCell.CELL_TYPE_STRING
					// XSSFCell.CELL_TYPE_NUMERIC
					// XSSFCell.CELL_TYPE_BOOLEAN
					// XSSFCell.CELL_TYPE_BLANK
					// XSSFCell.CELL_TYPE_FORMULA
					// XSSFCell.CELL_TYPE_ERROR
					cell = (XSSFCell) cells.next();
					switch (cell.getColumnIndex()) {
						case 1:
							enrollmentToken.setCountry(cell.getStringCellValue());
							break;
						case 2:
							enrollmentToken.setGroupId(Long.toString(Math.round(cell.getNumericCellValue())));
							break;
						case 3:
							enrollmentToken.setEnrollmentId(Long.toString(Math.round(cell.getNumericCellValue())));
							break;
						default:
							break;
					}
				}
				enrollmentTokensList.add(enrollmentToken);
			}
			xlsxFileToRead.close();
		} catch (IOException e) {
			LOGGER.error("ERROR: AppUtil - readXLSXFile()", e);
		}
		LOGGER.info("INFO: AppUtil - readXLSXFile() :: ends");
		return enrollmentTokensList;
	}

	/**
	 * Covert the date time provided to timezone provided
	 * 
	 * @author Mohan
	 * @param inputDate
	 *            the input date time
	 * @param inputFormat
	 *            the input format
	 * @param outputFormat
	 *            the ouput format
	 * @param timeZone
	 *            the user timezone
	 * @return the coverted date time
	 */
	public static String convertDateTimeByTimeZone(String inputDate, String inputFormat, String outputFormat,
			String timeZone) {
		LOGGER.info("INFO: AppUtil - convertDateTimeByTimeZone() :: starts");
		String finalDate = "";
		try {
			if (StringUtils.isNotEmpty(inputDate)) {
				SimpleDateFormat sdf = new SimpleDateFormat(inputFormat);
				sdf.setTimeZone(TimeZone.getTimeZone(AppConstants.SERVER_TIMEZONE));
				Date formattedDate = new Date(sdf.parse(inputDate).getTime());

				sdf = new SimpleDateFormat(outputFormat);
				if (StringUtils.isNotEmpty(timeZone)) {
					sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
				}
				finalDate = sdf.format(formattedDate);
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: AppUtil - convertDateTimeByTimeZone()", e);
		}
		LOGGER.info("INFO: AppUtil - convertDateTimeByTimeZone() :: ends");
		return finalDate;
	}

	/**
	 * Get the current date time in milliseconds
	 * 
	 * @author Mohan
	 * @return the current date time in milliseconds
	 */
	public static String getCurrentDateTimeInMilliSeconds() {
		LOGGER.info("INFO: AppUtil - getCurrentDateTimeInMilliSeconds() :: starts");
		String milliSeconds = Long.toString(Calendar.getInstance().getTimeInMillis());
		LOGGER.info("INFO: AppUtil - getCurrentDateTimeInMilliSeconds() :: ends");
		return milliSeconds;
	}

	/**
	 * Get the current date time for the provided user time zone
	 * 
	 * @author Mohan
	 * @param outputFormat
	 *            the required output format
	 * @param userTimeZone
	 *            the user time zone
	 * @return the user time zone current date time
	 */
	public static String getCurrentDateTimeForUser(String outputFormat, String userTimeZone) {
		LOGGER.info("INFO: AppUtil - getCurrentDateTimeForUser() :: starts");
		String userTimeZoneCurrentDateTime = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(AppConstants.SDF_DATE_TIME_FORMAT);
			sdf.setTimeZone(TimeZone.getTimeZone(AppConstants.SERVER_TIMEZONE));
			Date formattedDate = new Date(sdf.parse(AppUtil.getCurrentDateTime()).getTime());

			sdf = new SimpleDateFormat(outputFormat);
			sdf.setTimeZone(TimeZone.getTimeZone(userTimeZone));
			userTimeZoneCurrentDateTime = sdf.format(formattedDate);
		} catch (Exception e) {
			LOGGER.error("ERROR: AppUtil - getCurrentDateTimeForUser()", e);
		}
		LOGGER.info("INFO: AppUtil - getCurrentDateTimeForUser() :: ends");
		return userTimeZoneCurrentDateTime;
	}

	/**
	 * Convert date time from one timeZone to another timeZone
	 * 
	 * @author Mohan
	 * @param inputDate
	 *            the input date time
	 * @param inputFormat
	 *            the input format
	 * @param outputFormat
	 *            the output format
	 * @param fromTimeZone
	 *            the from time zone
	 * @param toTimeZone
	 *            the to time zone
	 * @return the converted date time
	 */
	public static String convertDateTimeFromOneTimeZoneToAnotherTimeZone(String inputDate, String inputFormat,
			String outputFormat, String fromTimeZone, String toTimeZone) {
		LOGGER.info("INFO: AppUtil - convertDateTimeFromOneTimeZoneToAnotherTimeZone() :: starts");
		String finalDate = "";
		try {
			if (StringUtils.isNotEmpty(inputDate)) {
				SimpleDateFormat sdf = new SimpleDateFormat(inputFormat);
				sdf.setTimeZone(TimeZone.getTimeZone(fromTimeZone));
				Date formattedDate = new Date(sdf.parse(inputDate).getTime());

				sdf = new SimpleDateFormat(outputFormat);
				sdf.setTimeZone(TimeZone.getTimeZone(toTimeZone));
				finalDate = sdf.format(formattedDate);
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: AppUtil - convertDateTimeFromOneTimeZoneToAnotherTimeZone()", e);
		}
		LOGGER.info("INFO: AppUtil - convertDateTimeFromOneTimeZoneToAnotherTimeZone() :: ends");
		return finalDate;
	}

	/**
	 * To get the response from the provided url, method type
	 * 
	 * @author Mohan
	 * @param url
	 *            the request uri
	 * @param method
	 *            the request method type (e.g GET, POST etc)
	 * @param headers
	 *            the headers params
	 * @param headers
	 *            the body params
	 * @return the {@link Responsemodel} details
	 */
	public static Responsemodel exchangeData(String url, String method, Map<String, String> headers,
			Map<String, String> params) {
		LOGGER.info("INFO: AppUtil - exchangeData() :: starts");
		Responsemodel responseModel = new Responsemodel();
		String responseBody = null;
		int statusCode = 0;
		try {

			URL obj = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
			connection.setRequestMethod(method);
			connection.setReadTimeout((int) TimeUnit.SECONDS.toMillis(180));// 3 min timeout
			connection.setRequestProperty(AppConstants.HEADER_KEY_AUTHORIZATION,
					AppUtil.getAppProperties().get(AppConstants.FITBIT_RT_HEADER_AUTHORIZATION));
			connection.setRequestProperty(AppConstants.HEADER_KEY_CONTENT_TYPE,
					AppUtil.getAppProperties().get(AppConstants.FITBIT_RT_HEADER_CONTENT_TYPE));

			if (headers != null) {
				for (Map.Entry<String, String> entry : headers.entrySet()) {
					connection.setRequestProperty(entry.getKey(), entry.getValue());
				}
			}

			// add doInput for POST method
			if (HttpMethod.POST.toString().equals(method)) {
				connection.setDoInput(true);
				connection.setDoOutput(true);

				if (params != null) {
					OutputStream os = connection.getOutputStream();
					BufferedWriter writer = new BufferedWriter(
							new OutputStreamWriter(os, AppConstants.CHARSET_ENCODING_UTF_8));
					writer.write(new Gson().toJson(params));

					writer.flush();
					writer.close();
					os.close();
				}
			}

			try {
				// Will throw IOException if server responds with 401.
				statusCode = connection.getResponseCode();
			} catch (IOException e) {
				// Will return 401, because now connection has the correct internal state.
				statusCode = connection.getResponseCode();
			}

			BufferedReader bufferedReader;
			if (statusCode == HttpURLConnection.HTTP_OK) {
				bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			} else {
				bufferedReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
			}

			String inputLine;
			StringBuilder response = new StringBuilder();
			while ((inputLine = bufferedReader.readLine()) != null) {
				response.append(inputLine);
			}
			bufferedReader.close();
			connection.disconnect();
			responseBody = response.toString();
		} catch (Exception e) {
			LOGGER.error("ERROR: AppUtil - exchangeData()", e);
		}
		responseModel.setStatusCode(statusCode);
		responseModel.setBody(responseBody);
		LOGGER.info("INFO: AppUtil - exchangeData() :: ends");
		LOGGER.warn("WARN: AppUtil - Response Model --> " + responseModel.toString());
		return responseModel;
	}
	
	
	public static Map<String, String> getAppConfigProperties() {
		return appConfigProperties;
	}

	public static void setAppConfigProperties(Map<String, String> appConfigProperties) {
		AppUtil.appConfigProperties = appConfigProperties;
	}
	

	/**
	 * Save the responses activity  document in the path specified
	 * 
	 * @author Kavya
	 * @param content
	 *            the jsonFile document content
	 * @param userId
	 *            the user identifier
	 * @param enrollmentId
	 *            the enrollment identifier
	 * @return the saved responses activity document name
	 */
	public static String saveResponsesActivityDocument(String content, String userId, String enrollmentId) {
		LOGGER.info("INFO: AppUtil - saveResponsesActivityDocument() :: starts");
		File serverFile;
		String consentFileName = null;
		try {
			if (StringUtils.isNotEmpty(content)) {
				byte[] bytes = Base64.getDecoder().decode(content.replaceAll("\n", ""));
				String currentPath =  System.getProperty(AppUtil.getAppProperties().get("gba.current.path"));
				System.out.println("Current Path"+currentPath);
				String rootPath = currentPath.replace('\\', '/')
						+ AppUtil.getAppProperties().get("gba.docs.responses.path");
				File directory = new File(rootPath + File.separator);
				if (!directory.exists()) {
					directory.mkdirs();
				}
				consentFileName = AppUtil.getStandardFileNameForResponses(userId, enrollmentId);
				LOGGER.warn("WARN: AppUtil - saveResponsesActivityDocument() :: CONSENT FILE NAME ==> " + consentFileName);
				serverFile = new File(directory.getAbsolutePath() + File.separator + consentFileName);
				
				
				serverFile.getAbsolutePath();
				
				saveFileInPath(serverFile, bytes);
			}
		} catch (Exception e) {
			LOGGER.error("ERROR: AppUtil - saveResponsesActivityDocument()", e);
		}
		LOGGER.info("INFO: AppUtil - saveResponsesActivityDocument() :: ends");
		return consentFileName;
	}
	
	/**
	 * Get the standard file name for the provided user and enrollment identifier
	 * 
	 * @author kavya
	 * @param userId
	 *            the user identifier
	 * @param enrollmentId
	 *            the enrollment identifier
	 * @return the standard file name
	 */
	public static String getStandardFileNameForResponses(String userId, String enrollmentId) {
		LOGGER.info("INFO: AppUtil - getStandardFileNameForResponses() :: starts");
		String fileName = null;
		try {
			fileName = new StringBuilder().append("ForHealthApp_")
					.append(new SimpleDateFormat("MMddyyyyHHmmss").format(new Date())).append("_").append(enrollmentId).append("_").append(userId)
					.append(".json").toString();
		} catch (Exception e) {
			LOGGER.error("ERROR: AppUtil - getStandardFileNameForResponses()", e);
		}
		LOGGER.info("INFO: AppUtil - getStandardFileNameForResponses() :: ends");
		return fileName;
	}
	
	
	public static String getFilePathOFResponseDoucument(String consentFileName) {
		String getJsonPath = null;
		File serverFile;
		LOGGER.info("INFO: AppUtil - getFilePathOFResponseDoucument() :: starts");
		try {
			String currentPath =  System.getProperty(AppUtil.getAppProperties().get("gba.current.path"));
		
			System.out.println("Current Path"+currentPath);
			String rootPath = currentPath.replace('\\', '/')
					+ AppUtil.getAppProperties().get("gba.docs.responses.path");
			File directory = new File(rootPath + File.separator);
			serverFile = new File(directory.getAbsolutePath() + File.separator + consentFileName);
			getJsonPath  = serverFile.getAbsolutePath();
		} catch (Exception e) {
			LOGGER.error("ERROR: AppUtil - getFilePathOFResponseDoucument()", e);
		}
		LOGGER.info("INFO: AppUtil - getFilePathOFResponseDoucument() :: ends");
		return getJsonPath;
		
	}
	

}
