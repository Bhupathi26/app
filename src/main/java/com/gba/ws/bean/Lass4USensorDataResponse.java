package com.gba.ws.bean;

import com.gba.ws.bean.fitbit.Lass4UBeanSensorData;

public class Lass4USensorDataResponse {

	private ErrorBean errorBean = new ErrorBean();

	private Lass4UBeanSensorData lass4UBean = new Lass4UBeanSensorData();

	public ErrorBean getErrorBean() {
		return errorBean;
	}

	public void setErrorBean(ErrorBean errorBean) {
		this.errorBean = errorBean;
	}

	public Lass4UBeanSensorData getLass4UBean() {
		return lass4UBean;
	}

	public void setLass4UBean(Lass4UBeanSensorData lass4uBean) {
		lass4UBean = lass4uBean;
	}

	@Override
	public String toString() {
		return "Lass4USensorDataResponse [errorBean=" + errorBean + ", lass4UBean=" + lass4UBean + "]";
	}

}
