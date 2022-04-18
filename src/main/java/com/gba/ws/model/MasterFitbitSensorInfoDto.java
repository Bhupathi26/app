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
import org.hibernate.annotations.Type;

@Entity
@Table(name = "master_fitbit_sensor")
@NamedQueries(value = { 
		@NamedQuery(name = "MasterFitbitSensorInfoDto.getMasterSensorFitbitData", query = "from MasterFitbitSensorInfoDto MIO"),
})
public class MasterFitbitSensorInfoDto implements Serializable{

	private static final long serialVersionUID = 193851443027423864L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "master_id")
	private Integer masterId;
	
	@Column(name = "fitbit_info" , length= 1)
	@Type(type = "yes_no")
	private boolean fitbitinfo = true;
	
	@Column(name = "health_sensor_info" , length= 1)
	@Type(type = "yes_no")
	private boolean healthSensorInfo = true;

	public Integer getMasterId() {
		return masterId;
	}

	public void setMasterId(Integer masterId) {
		this.masterId = masterId;
	}


	public boolean isFitbitinfo() {
		return fitbitinfo;
	}

	public void setFitbitinfo(boolean fitbitinfo) {
		this.fitbitinfo = fitbitinfo;
	}

	public boolean isHealthSensorInfo() {
		return healthSensorInfo;
	}

	public void setHealthSensorInfo(boolean healthSensorInfo) {
		this.healthSensorInfo = healthSensorInfo;
	}

	@Override
	public String toString() {
		return "MasterFitbitSensorInfoDto [masterId=" + masterId + ", fitbitinfo=" + fitbitinfo + ", healthSensorInfo="
				+ healthSensorInfo + "]";
	}
	
	
	
	
	
}
