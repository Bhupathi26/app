/**
 * 
 */
package com.gba.ws.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides rewards information in response.
 * 
 * @author Mohan
 * @createdOn Dec 4, 2017 11:21:27 AM
 */
public class RewardsResponse {

	private ErrorBean error = new ErrorBean();
	private int currentLevel = 0;
	private long points = 0L;
	private List<RewardLevelBean> rewardLevels = new ArrayList<>();

	public ErrorBean getError() {
		return this.error;
	}

	public RewardsResponse setError(ErrorBean error) {
		this.error = error;
		return this;
	}

	public int getCurrentLevel() {
		return this.currentLevel;
	}

	public RewardsResponse setCurrentLevel(int currentLevel) {
		this.currentLevel = currentLevel;
		return this;
	}

	public long getPoints() {
		return this.points;
	}

	public RewardsResponse setPoints(long points) {
		this.points = points;
		return this;
	}

	public List<RewardLevelBean> getRewardLevels() {
		return this.rewardLevels;
	}

	public RewardsResponse setRewardLevels(List<RewardLevelBean> rewardLevels) {
		this.rewardLevels = rewardLevels;
		return this;
	}

	@Override
	public String toString() {
		return "RewardsResponse [error=" + error + ", currentLevel=" + currentLevel + ", points=" + points
				+ ", rewardLevels=" + rewardLevels + "]";
	}

}
