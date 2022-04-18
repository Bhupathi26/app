/**
 * 
 */
package com.gba.ws.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Provides rewards level information.
 * 
 * @author Mohan
 * @createdOn Dec 4, 2017 12:10:10 PM
 */
@Entity
@Table(name = "reward_levels")
@NamedQueries(value = {

		@NamedQuery(name = "RewardLevelsDto.findAll", query = "FROM RewardLevelsDto RLDTO"), })
public class RewardLevelsDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7561188731212717489L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "reward_level_id")
	private int rewardLevelId;

	@Column(name = "level")
	private int level;

	@Column(name = "points")
	private int points;

	public int getRewardLevelId() {
		return this.rewardLevelId;
	}

	public RewardLevelsDto setRewardLevelId(int rewardLevelId) {
		this.rewardLevelId = rewardLevelId;
		return this;
	}

	public int getLevel() {
		return this.level;
	}

	public RewardLevelsDto setLevel(int level) {
		this.level = level;
		return this;
	}

	public int getPoints() {
		return this.points;
	}

	public RewardLevelsDto setPoints(int points) {
		this.points = points;
		return this;
	}

	@Override
	public String toString() {
		return "RewardLevelsDto [rewardLevelId=" + rewardLevelId + ", level=" + level + ", points=" + points + "]";
	}

}
