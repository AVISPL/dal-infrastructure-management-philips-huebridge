/**
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.aggregateddevice;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Temperature class provides the device temperature information
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 9/14/2022
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Temperature {

	private String temperature;
	@JsonAlias("temperature_valid")
	private boolean temperatureValue;

	/**
	 * Retrieves {@link #temperature}
	 *
	 * @return value of {@link #temperature}
	 */
	public String getTemperature() {
		return temperature;
	}

	/**
	 * Sets {@link #temperature} value
	 *
	 * @param temperature new value of {@link #temperature}
	 */
	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}

	/**
	 * Retrieves {@link #temperatureValue}
	 *
	 * @return value of {@link #temperatureValue}
	 */
	public boolean isTemperatureValue() {
		return temperatureValue;
	}

	/**
	 * Sets {@link #temperatureValue} value
	 *
	 * @param temperatureValue new value of {@link #temperatureValue}
	 */
	public void setTemperatureValue(boolean temperatureValue) {
		this.temperatureValue = temperatureValue;
	}
}