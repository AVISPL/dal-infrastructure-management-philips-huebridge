/**
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.aggregateddevice.TemperatureDevice;

/**
 * TemperatureWrapper class provides info of motion temperature
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 9/14/2022
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TemperatureWrapper {

	private  String error;
	private TemperatureDevice[] data;

	/**
	 * Retrieves {@link #error}
	 *
	 * @return value of {@link #error}
	 */
	public String getError() {
		return error;
	}

	/**
	 * Sets {@link #error} value
	 *
	 * @param error new value of {@link #error}
	 */
	public void setError(String error) {
		this.error = error;
	}

	/**
	 * Retrieves {@link #data}
	 *
	 * @return value of {@link #data}
	 */
	public TemperatureDevice[] getData() {
		return data;
	}

	/**
	 * Sets {@link #data} value
	 *
	 * @param data new value of {@link #data}
	 */
	public void setData(TemperatureDevice[] data) {
		this.data = data;
	}
}