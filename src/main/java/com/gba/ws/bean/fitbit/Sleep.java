/**
 * 
 */
package com.gba.ws.bean.fitbit;

/**
 * @author Mohan
 * @createdOn Jan 12, 2018 11:47:24 AM
 */
public class Sleep {

	private boolean isMainSleep = false;
	private String logId = "";
	private String minutesAfterWakeup = "";
	private String dateOfSleep = "";
	private String infoCode = "";
	private String minutesToFallAsleep = "";
	private String type = "";
	private String endTime = "";
	private String startTime = "";
	private String minutesAwake = "";
	private String timeInBed = "";
	private String duration = "";
	private Levels levels = new Levels();
	private String efficiency = "";
	private int minutesAsleep = 0;

	public boolean isMainSleep() {
		return this.isMainSleep;
	}

	public Sleep setMainSleep(boolean isMainSleep) {
		this.isMainSleep = isMainSleep;
		return this;
	}

	public String getLogId() {
		return this.logId;
	}

	public Sleep setLogId(String logId) {
		this.logId = logId;
		return this;
	}

	public String getMinutesAfterWakeup() {
		return this.minutesAfterWakeup;
	}

	public Sleep setMinutesAfterWakeup(String minutesAfterWakeup) {
		this.minutesAfterWakeup = minutesAfterWakeup;
		return this;
	}

	public String getDateOfSleep() {
		return this.dateOfSleep;
	}

	public Sleep setDateOfSleep(String dateOfSleep) {
		this.dateOfSleep = dateOfSleep;
		return this;
	}

	public String getInfoCode() {
		return this.infoCode;
	}

	public Sleep setInfoCode(String infoCode) {
		this.infoCode = infoCode;
		return this;
	}

	public String getMinutesToFallAsleep() {
		return this.minutesToFallAsleep;
	}

	public Sleep setMinutesToFallAsleep(String minutesToFallAsleep) {
		this.minutesToFallAsleep = minutesToFallAsleep;
		return this;
	}

	public String getType() {
		return this.type;
	}

	public Sleep setType(String type) {
		this.type = type;
		return this;
	}

	public String getEndTime() {
		return this.endTime;
	}

	public Sleep setEndTime(String endTime) {
		this.endTime = endTime;
		return this;
	}

	public String getStartTime() {
		return this.startTime;
	}

	public Sleep setStartTime(String startTime) {
		this.startTime = startTime;
		return this;
	}

	public String getMinutesAwake() {
		return this.minutesAwake;
	}

	public Sleep setMinutesAwake(String minutesAwake) {
		this.minutesAwake = minutesAwake;
		return this;
	}

	public String getTimeInBed() {
		return this.timeInBed;
	}

	public Sleep setTimeInBed(String timeInBed) {
		this.timeInBed = timeInBed;
		return this;
	}

	public String getDuration() {
		return this.duration;
	}

	public Sleep setDuration(String duration) {
		this.duration = duration;
		return this;
	}

	public Levels getLevels() {
		return this.levels;
	}

	public Sleep setLevels(Levels levels) {
		this.levels = levels;
		return this;
	}

	public String getEfficiency() {
		return this.efficiency;
	}

	public Sleep setEfficiency(String efficiency) {
		this.efficiency = efficiency;
		return this;
	}

	public int getMinutesAsleep() {
		return this.minutesAsleep;
	}

	public Sleep setMinutesAsleep(int minutesAsleep) {
		this.minutesAsleep = minutesAsleep;
		return this;
	}

}
