/**
 * 
 */
package com.gba.ws.service;

import java.util.List;
import java.util.Map;

import org.json.JSONException;

import com.gba.ws.bean.FitbitLass4uApiResponseBean;
import com.gba.ws.bean.PushNotificationBean;
import com.gba.ws.bean.fitbit.AppVersionInfoBean;

/**
 * Provides business logic for the application service layer.
 * 
 * @author Mohan
 * @createdOn Jan 11, 2018 11:58:37 AM
 */
public interface AppService {

	/**
	 * Send activity new run available push notifications from Monday to Friday if
	 * the LASS4U and Fitbit sensor conditions meet the activity threshold
	 * conditions for all active users.
	 * <p>
	 * Cron expression for every
	 * <ol>
	 * <li>1 minute (0 0/1 * * * ?)
	 * <li>15 minute (0 0/15 * * * ?)
	 * 
	 * @author Mohan
	 */
	public void sendPushNotifications();

	/**
	 * Send push notification to the users.
	 * 
	 * @author Mohan
	 * @param pushNotificationMap
	 *            user activity push notification details map
	 * @throws JSONException
	 */
	public void sendTriggerredActivityRunsPushNotification(
			Map<Integer, List<PushNotificationBean>> pushNotificationMap);

	/**
	 * Send fitbit and lass4u sensor api failed push notification to users
	 * 
	 * @author Mohan
	 * @param fitbitLass4uApiResponseList
	 *            the fitbit and lass4u sensor api status details list
	 * @throws JSONException
	 */
	public void sendFitbitAndLass4uSensorFailedNotification(
			List<FitbitLass4uApiResponseBean> fitbitLass4uApiResponseList);
	
	/**
	 * Send expiry push notifications from Monday to Friday
	 * <p>
	 * Cron expression for every
	 * <ol>
	 * <li>1 minute (0 0/1 * * * ?)
	 * <li>15 minute (0 0/15 * * * ?)
	 * 
	 * @author Pradyumn
	 */
	public void sendExpiryPushNotifications();

	public AppVersionInfoBean getAppVersionInfo();
}
