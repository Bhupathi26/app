package com.gba.ws.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

@Entity
@Table(name = "cratwords_user_map")
@NamedQueries(value = {

		@NamedQuery(name = "CratWordsUserMapDto.findWordsId", query = "FROM CratWordsUserMapDto CWUMD"
				+ " WHERE CWUMD.userId =:userId")
})
public class CratWordsUserMapDto implements Serializable{
	
	private static final long serialVersionUID = 193851443027423864L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "cratwords_user_map_id")
	private Integer cratWordsUserMapId;
	
	@Column(name = "crat_words_id")
	private Integer cratWordsId;
	
	@Column(name = "user_id")
	private Integer userId;

	public Integer getCratWordsUserMapId() {
		return cratWordsUserMapId;
	}

	public CratWordsUserMapDto setCratWordsUserMapId(Integer cratWordsUserMapId) {
		this.cratWordsUserMapId = cratWordsUserMapId;
		return this;
		
	}

	public Integer getCratWordsId() {
		return cratWordsId;
	}

	public CratWordsUserMapDto setCratWordsId(Integer cratWordsId) {
		this.cratWordsId = cratWordsId;
		return this;
	}
	
	public Integer getUserId() {
		return userId;
	}

	public CratWordsUserMapDto setUserId(Integer userId) {
		this.userId = userId;
		return this;
	}

	@Override
	public String toString() {
		return "CratWordsUserMapDto [cratWordsUserMapId=" + cratWordsUserMapId + ", cratWordsId=" + cratWordsId
				+ ", userId=" + userId + "]";
	}
	
	

}
