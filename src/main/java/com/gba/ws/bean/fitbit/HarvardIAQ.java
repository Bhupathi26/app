
package com.gba.ws.bean.fitbit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Mohan
 * @createdOn Jan 22, 2018 4:15:36 PM
 */
public class HarvardIAQ {

	@SerializedName("gps_num")
	@Expose
	private int gpsNum = 0;

	private String app = "";

	@SerializedName("s_l0")
	@Expose
	private double light = 0D; // Light

	private String date = "";

	@SerializedName("s_d2")
	@Expose
	private float sD2 = 0F;

	@SerializedName("s_g8e")
	@Expose
	private float sG8e = 0F;

	@SerializedName("s_d0")
	@Expose
	private float pm25 = 0F; // PM2.5

	@SerializedName("s_d1")
	@Expose
	private float sD1 = 0F;

	@SerializedName("s_h0")
	@Expose
	private float relativeHumidity = 0F; // Relative Humidity

	@SerializedName("CFPM2.5")
	@Expose
	private String cfpm25 = "";

	@SerializedName("gps_fix")
	@Expose
	private float gpsFix = 0F;

	@SerializedName("CFPM1.0")
	@Expose
	private String cfpm1 = "";

	@SerializedName("ver_app")
	@Expose
	private String verApp = "";

	@SerializedName("device")
	@Expose
	private String device = "";

	@SerializedName("gps_lat")
	@Expose
	private double gpsLat = 0D;

	@SerializedName("s_t0")
	@Expose
	private float temperature = 0F; // Temperature

	private String timestamp = "";

	@SerializedName("s_lr")
	@Expose
	private float sLr = 0F;

	@SerializedName("gps_lon")
	@Expose
	private double gpsLon = 0D;

	@SerializedName("fmt_opt")
	@Expose
	private String fmtOpt = "";

	@SerializedName("s_lg")
	@Expose
	private float sLg = 0F;

	private double tick = 0D;

	@SerializedName("s_lb")
	@Expose
	private double sLb = 0D;

	@SerializedName("s_lc")
	@Expose
	private double sLc = 0D;

	@SerializedName("device_id")
	@Expose
	private String deviceId = "";

	@SerializedName("s_g8")
	@Expose
	private float co2 = 0F; // CO2

	@SerializedName("ver_format")
	@Expose
	private String verFormat = "";

	@SerializedName("CFPM10")
	@Expose
	private String cfpm10 = "";

	@SerializedName("FAKE_GPS")
	@Expose
	private String fakeGps = "";

	private String time = "";
	
	private String s_n0 = "";

	public int getGpsNum() {
		return this.gpsNum;
	}

	public HarvardIAQ setGpsNum(int gpsNum) {
		this.gpsNum = gpsNum;
		return this;
	}

	public String getApp() {
		return this.app;
	}

	public HarvardIAQ setApp(String app) {
		this.app = app;
		return this;
	}

	public double getLight() {
		return this.light;
	}

	public HarvardIAQ setLight(double light) {
		this.light = light;
		return this;
	}

	public String getDate() {
		return this.date;
	}

	public HarvardIAQ setDate(String date) {
		this.date = date;
		return this;
	}

	public float getsD2() {
		return this.sD2;
	}

	public HarvardIAQ setsD2(float sD2) {
		this.sD2 = sD2;
		return this;
	}

	public float getsG8e() {
		return this.sG8e;
	}

	public HarvardIAQ setsG8e(float sG8e) {
		this.sG8e = sG8e;
		return this;
	}

	public float getPm25() {
		return this.pm25;
	}

	public HarvardIAQ setPm25(float pm25) {
		this.pm25 = pm25;
		return this;
	}

	public float getsD1() {
		return this.sD1;
	}

	public HarvardIAQ setsD1(float sD1) {
		this.sD1 = sD1;
		return this;
	}

	public float getRelativeHumidity() {
		return this.relativeHumidity;
	}

	public HarvardIAQ setRelativeHumidity(float relativeHumidity) {
		this.relativeHumidity = relativeHumidity;
		return this;
	}

	public String getCfpm25() {
		return this.cfpm25;
	}

	public HarvardIAQ setCfpm25(String cfpm25) {
		this.cfpm25 = cfpm25;
		return this;
	}

	public float getGpsFix() {
		return this.gpsFix;
	}

	public HarvardIAQ setGpsFix(float gpsFix) {
		this.gpsFix = gpsFix;
		return this;
	}

	public String getCfpm1() {
		return this.cfpm1;
	}

	public HarvardIAQ setCfpm1(String cfpm1) {
		this.cfpm1 = cfpm1;
		return this;
	}

	public String getVerApp() {
		return this.verApp;
	}

	public HarvardIAQ setVerApp(String verApp) {
		this.verApp = verApp;
		return this;
	}

	public String getDevice() {
		return this.device;
	}

	public HarvardIAQ setDevice(String device) {
		this.device = device;
		return this;
	}

	public double getGpsLat() {
		return this.gpsLat;
	}

	public HarvardIAQ setGpsLat(double gpsLat) {
		this.gpsLat = gpsLat;
		return this;
	}

	public float getTemperature() {
		return this.temperature;
	}

	public HarvardIAQ setTemperature(float temperature) {
		this.temperature = temperature;
		return this;
	}

	public String getTimestamp() {
		return this.timestamp;
	}

	public HarvardIAQ setTimestamp(String timestamp) {
		this.timestamp = timestamp;
		return this;
	}

	public float getsLr() {
		return this.sLr;
	}

	public HarvardIAQ setsLr(float sLr) {
		this.sLr = sLr;
		return this;
	}

	public double getGpsLon() {
		return this.gpsLon;
	}

	public HarvardIAQ setGpsLon(double gpsLon) {
		this.gpsLon = gpsLon;
		return this;
	}

	public String getFmtOpt() {
		return this.fmtOpt;
	}

	public HarvardIAQ setFmtOpt(String fmtOpt) {
		this.fmtOpt = fmtOpt;
		return this;
	}

	public float getsLg() {
		return this.sLg;
	}

	public HarvardIAQ setsLg(float sLg) {
		this.sLg = sLg;
		return this;
	}

	public double getTick() {
		return this.tick;
	}

	public HarvardIAQ setTick(double tick) {
		this.tick = tick;
		return this;
	}

	public double getsLb() {
		return this.sLb;
	}

	public HarvardIAQ setsLb(double sLb) {
		this.sLb = sLb;
		return this;
	}

	public double getsLc() {
		return this.sLc;
	}

	public HarvardIAQ setsLc(double sLc) {
		this.sLc = sLc;
		return this;
	}

	public String getDeviceId() {
		return this.deviceId;
	}

	public HarvardIAQ setDeviceId(String deviceId) {
		this.deviceId = deviceId;
		return this;
	}

	public float getCo2() {
		return this.co2;
	}

	public HarvardIAQ setCo2(float co2) {
		this.co2 = co2;
		return this;
	}

	public String getVerFormat() {
		return this.verFormat;
	}

	public HarvardIAQ setVerFormat(String verFormat) {
		this.verFormat = verFormat;
		return this;
	}

	public String getCfpm10() {
		return this.cfpm10;
	}

	public HarvardIAQ setCfpm10(String cfpm10) {
		this.cfpm10 = cfpm10;
		return this;
	}

	public String getFakeGps() {
		return this.fakeGps;
	}

	public HarvardIAQ setFakeGps(String fakeGps) {
		this.fakeGps = fakeGps;
		return this;
	}

	public String getTime() {
		return this.time;
	}

	public HarvardIAQ setTime(String time) {
		this.time = time;
		return this;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("HarvardIAQ [gpsNum=");
		builder.append(this.gpsNum);
		builder.append(", app=");
		builder.append(this.app);
		builder.append(", light=");
		builder.append(this.light);
		builder.append(", date=");
		builder.append(this.date);
		builder.append(", sD2=");
		builder.append(this.sD2);
		builder.append(", sG8e=");
		builder.append(this.sG8e);
		builder.append(", pm25=");
		builder.append(this.pm25);
		builder.append(", sD1=");
		builder.append(this.sD1);
		builder.append(", relativeHumidity=");
		builder.append(this.relativeHumidity);
		builder.append(", cfpm25=");
		builder.append(this.cfpm25);
		builder.append(", gpsFix=");
		builder.append(this.gpsFix);
		builder.append(", cfpm1=");
		builder.append(this.cfpm1);
		builder.append(", verApp=");
		builder.append(this.verApp);
		builder.append(", device=");
		builder.append(this.device);
		builder.append(", gpsLat=");
		builder.append(this.gpsLat);
		builder.append(", temperature=");
		builder.append(this.temperature);
		builder.append(", timestamp=");
		builder.append(this.timestamp);
		builder.append(", sLr=");
		builder.append(this.sLr);
		builder.append(", gpsLon=");
		builder.append(this.gpsLon);
		builder.append(", fmtOpt=");
		builder.append(this.fmtOpt);
		builder.append(", sLg=");
		builder.append(this.sLg);
		builder.append(", tick=");
		builder.append(this.tick);
		builder.append(", sLb=");
		builder.append(this.sLb);
		builder.append(", sLc=");
		builder.append(this.sLc);
		builder.append(", deviceId=");
		builder.append(this.deviceId);
		builder.append(", co2=");
		builder.append(this.co2);
		builder.append(", verFormat=");
		builder.append(this.verFormat);
		builder.append(", cfpm10=");
		builder.append(this.cfpm10);
		builder.append(", fakeGps=");
		builder.append(this.fakeGps);
		builder.append(", time=");
		builder.append(this.time);
		builder.append("]");
		return builder.toString();
	}

	public String getS_n0() {
		return s_n0;
	}

	public void setS_n0(String s_n0) {
		this.s_n0 = s_n0;
	}

}
