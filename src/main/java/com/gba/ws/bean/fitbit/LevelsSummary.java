/**
 * 
 */
package com.gba.ws.bean.fitbit;

/**
 * @author Mohan
 * @createdOn Jan 12, 2018 11:44:54 AM
 */
public class LevelsSummary {

	private Asleep asleep = new Asleep();
	private Restless restless = new Restless();
	private Awake awake = new Awake();

	public Asleep getAsleep() {
		return this.asleep;
	}

	public LevelsSummary setAsleep(Asleep asleep) {
		this.asleep = asleep;
		return this;
	}

	public Restless getRestless() {
		return this.restless;
	}

	public LevelsSummary setRestless(Restless restless) {
		this.restless = restless;
		return this;
	}

	public Awake getAwake() {
		return this.awake;
	}

	public LevelsSummary setAwake(Awake awake) {
		this.awake = awake;
		return this;
	}

}
