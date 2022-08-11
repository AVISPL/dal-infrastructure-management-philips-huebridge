/**
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.grouplight;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * GroupLightResponse class provides during the monitoring and controlling process
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/3/2022
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupLightResponse {

	private String id;
	private BrightnessLight dimming;

	@JsonAlias("on")
	private StatusLight statusLight;

	@JsonAlias("color_temperature")
	private ColorTemperature temperature;

	/**
	 * Retrieves {@code {@link #id}}
	 *
	 * @return value of {@link #id}
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets {@code id}
	 *
	 * @param id the {@code java.lang.String} field
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Retrieves {@code {@link #dimming}}
	 *
	 * @return value of {@link #dimming}
	 */
	public BrightnessLight getDimming() {
		return dimming;
	}

	/**
	 * Sets {@code dimming}
	 *
	 * @param dimming the {@code com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.grouplight.BrightnessLight} field
	 */
	public void setDimming(BrightnessLight dimming) {
		this.dimming = dimming;
	}

	/**
	 * Retrieves {@code {@link #statusLight }}
	 *
	 * @return value of {@link #statusLight}
	 */
	public StatusLight getStatusLight() {
		return statusLight;
	}

	/**
	 * Sets {@code on}
	 *
	 * @param statusLight the {@code com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.grouplight.StatusLight} field
	 */
	public void setStatusLight(StatusLight statusLight) {
		this.statusLight = statusLight;
	}

	/**
	 * Retrieves {@code {@link #temperature}}
	 *
	 * @return value of {@link #temperature}
	 */
	public ColorTemperature getTemperature() {
		return temperature;
	}

	/**
	 * Sets {@code temperature}
	 *
	 * @param temperature the {@code com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.grouplight.ColorTemperature} field
	 */
	public void setTemperature(ColorTemperature temperature) {
		this.temperature = temperature;
	}
}