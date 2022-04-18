/**
 * 
 */
package com.gba.ws.bean;

/**
 * Provides rewards level information.
 * 
 * @author Mohan
 * @createdOn Dec 4, 2017 11:23:02 AM
 */
public class RewardLevelBean {

	private int level = 0;
	private int points = 0;

	public int getLevel() {
		return this.level;
	}

	public RewardLevelBean setLevel(int level) {
		this.level = level;
		return this;
	}

	public int getPoints() {
		return this.points;
	}

	public RewardLevelBean setPoints(int points) {
		this.points = points;
		return this;
	}

	@Override
	public String toString() {
		return "RewardLevelBean [level=" + level + ", points=" + points + "]";
	}

}
