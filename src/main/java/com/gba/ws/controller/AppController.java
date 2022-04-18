package com.gba.ws.controller;

import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.gba.ws.bean.BaseResponse;
import com.gba.ws.bean.ErrorBean;
import com.gba.ws.bean.ErrorResponse;
import com.gba.ws.bean.fitbit.AppVersionInfoBean;
import com.gba.ws.service.AppService;
import com.gba.ws.util.AppConstants;
import com.gba.ws.util.AppUtil;
import com.gba.ws.util.ErrorCode;
//helloworld

/**
 * 
 * @author Mohan
 * @createdOn Jan 9, 2018 12:37:34 PM
 */
@Controller
public class AppController implements ErrorController {

	private static final Logger LOGGER = Logger.getLogger(AppController.class);

	private static final String INVALID_URI = "/error";
	private static final String PING_URI = "/ping";
	private static final String VERSION_INFO = "/getAppVersionInfo";
    private static final String UPDATE_CONFIG = "/updateAppConfigProperties";
	
	
	@Autowired
	private AppService appService;
	
	@Override
	public String getErrorPath() {
		return "/error";
	}

	/**
	 * 
	 * @author Mohan
	 * @return
	 */
	@RequestMapping(value = INVALID_URI, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> invalidUrl() {
		LOGGER.info("INFO: AppController - invalidUrl() :: starts");
		ErrorResponse errorResponse = new ErrorResponse().setError(
				new ErrorBean().setCode(HttpStatus.NOT_FOUND.value()).setMessage(ErrorCode.EC_404.errorMessage()));
		LOGGER.info("INFO: AppController - invalidUrl() :: ends");
		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	}

	/**
	 * To check the application status
	 * 
	 * @author Mohan
	 * @return It works!
	 */
	@RequestMapping(value = PING_URI, method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<Object> checkApplication() {
		LOGGER.info("INFO: AppController - checkApplication() :: starts");
		LOGGER.info("INFO: AppController - checkApplication() :: ends");
		return new ResponseEntity<>("App Lab Webservice is working!", HttpStatus.OK);
	}
	@RequestMapping(value = VERSION_INFO, method = RequestMethod.GET, produces =MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> getAppVersionInfo() {
		AppVersionInfoBean AppVersionInfoBean=null;
		LOGGER.info("INFO: AppController - getAppVersionInfo() :: starts");
		LOGGER.info("INFO: AppController - getAppVersionInfo() :: ends");
		AppVersionInfoBean=appService.getAppVersionInfo();
		return new ResponseEntity<>(AppVersionInfoBean, HttpStatus.OK);
	}
	
	
	
	/**
	 * To get update config properties
	 * 
	 * @author Kavya
	 * @return It works!
	 */
	@RequestMapping(value = UPDATE_CONFIG, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> updateAppConfigProperties() {
		LOGGER.info("updateAppConfigProperties() : Starts");
		BaseResponse baseResponse = new BaseResponse();
		Map<String,String> appConfigProperties = new HashMap<>();
		Enumeration<String> keys = null;
		String key;
		String value;
		try {
			//FileInputStream fis = new FileInputStream("/var/lib/tomcat8/webapps/appConfig.properties");
			FileInputStream fis = new FileInputStream("/root/apache-tomcat-9.0.43/webapps/appConfig.properties");
			ResourceBundle rb = new PropertyResourceBundle(fis);
			keys = rb.getKeys();
			while (keys.hasMoreElements()) {
				key = keys.nextElement();
				value = rb.getString(key);
				appConfigProperties.put(key, value);
			System.out.println("key ==> " + key + "\t value ==> " + value);
			}
			AppUtil.setAppConfigProperties(appConfigProperties);
			baseResponse.setStatus(AppConstants.SUCCESS);
			return new ResponseEntity<>(baseResponse, HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error("updateAppConfigProperties() : ERROR", e);
			baseResponse.setError(new ErrorBean().setCode(ErrorCode.EC_502.code()).setMessage(ErrorCode.EC_502.errorMessage())) ;
			return new ResponseEntity<>(baseResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
}
