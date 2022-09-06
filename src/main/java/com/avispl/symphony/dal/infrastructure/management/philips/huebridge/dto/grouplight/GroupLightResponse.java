/**
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.grouplight;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.bridge.OwnerResponse;

/**
 * GroupLightResponse class provides info about the group light
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

	private OwnerResponse owner;

	private ColorLight color;

	/**
	 * Retrieves {@link #id}
	 *
	 * @return value of {@link #id}
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets {@link #id} value
	 *
	 * @param id new value of {@link #id}
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Retrieves {@link #dimming}
	 *
	 * @return value of {@link #dimming}
	 */
	public BrightnessLight getDimming() {
		return dimming;
	}

	/**
	 * Sets {@link #dimming} value
	 *
	 * @param dimming new value of {@link #dimming}
	 */
	public void setDimming(BrightnessLight dimming) {
		this.dimming = dimming;
	}

	/**
	 * Retrieves {@link #statusLight}
	 *
	 * @return value of {@link #statusLight}
	 */
	public StatusLight getStatusLight() {
		return statusLight;
	}

	/**
	 * Sets {@link #statusLight} value
	 *
	 * @param statusLight new value of {@link #statusLight}
	 */
	public void setStatusLight(StatusLight statusLight) {
		this.statusLight = statusLight;
	}

	/**
	 * Retrieves {@link #temperature}
	 *
	 * @return value of {@link #temperature}
	 */
	public ColorTemperature getTemperature() {
		return temperature;
	}

	/**
	 * Sets {@link #temperature} value
	 *
	 * @param temperature new value of {@link #temperature}
	 */
	public void setTemperature(ColorTemperature temperature) {
		this.temperature = temperature;
	}

	/**
	 * Retrieves {@link #owner}
	 *
	 * @return value of {@link #owner}
	 */
	public OwnerResponse getOwner() {
		return owner;
	}

	/**
	 * Sets {@link #owner} value
	 *
	 * @param owner new value of {@link #owner}
	 */
	public void setOwner(OwnerResponse owner) {
		this.owner = owner;
	}

	/**
	 * Retrieves {@link #color}
	 *
	 * @return value of {@link #color}
	 */
	public ColorLight getColor() {
		return color;
	}

	/**
	 * Sets {@link #color} value
	 *
	 * @param color new value of {@link #color}
	 */
	public void setColor(ColorLight color) {
		this.color = color;
	}
}