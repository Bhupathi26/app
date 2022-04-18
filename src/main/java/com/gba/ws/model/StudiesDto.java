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
 * Provides Studies details.
 * 
 * @author Mohan
 * @createdOn Jan 8, 2018 5:54:21 PM
 */
@Entity
@Table(name = "studies")
@NamedQueries(value = {

		@NamedQuery(name = "StudiesDto.fetchByStudyId", query = "FROM StudiesDto SDTO"
				+ " WHERE SDTO.studyId =:studyId") })
public class StudiesDto implements Serializable {

	private static final long serialVersionUID = 3015495872921536293L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "study_id")
	private int studyId;

	@Column(name = "study_name")
	private String studyName;

	public int getStudyId() {
		return studyId;
	}

	public StudiesDto setStudyId(int studyId) {
		this.studyId = studyId;
		return this;
	}

	public String getStudyName() {
		return studyName;
	}

	public StudiesDto setStudyName(String studyName) {
		this.studyName = studyName;
		return this;
	}

	@Override
	public String toString() {
		return "StudiesDto [studyId=" + studyId + ", studyName=" + studyName + "]";
	}

}
