/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.aggregateddevice;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.bridge.OwnerResponse;

/**
 * PowerDevice class provides the power information for the device
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/25/2022
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PowerDevice {

	private String id;
	private OwnerResponse owner;
	@JsonAlias("power_state")
	private PowerState powerState;
	private String type;

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
	 * Retrieves {@code {@link #owner}}
	 *
	 * @return value of {@link #owner}
	 */
	public OwnerResponse getOwner() {
		return owner;
	}

	/**
	 * Sets {@code owner}
	 *
	 * @param owner the {@code com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.bridge.OwnerResponse} field
	 */
	public void setOwner(OwnerResponse owner) {
		this.owner = owner;
	}

	/**
	 * Retrieves {@code {@link #powerState }}
	 *
	 * @return value of {@link #powerState}
	 */
	public PowerState getPowerState() {
		return powerState;
	}

	/**
	 * Sets {@code power_state}
	 *
	 * @param powerState the {@code com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.aggregateddevice.PowerState} field
	 */
	public void setPowerState(PowerState powerState) {
		this.powerState = powerState;
	}

	/**
	 * Retrieves {@code {@link #type}}
	 *
	 * @return value of {@link #type}
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets {@code type}
	 *
	 * @param type the {@code java.lang.String} field
	 */
	public void setType(String type) {
		this.type = type;
	}
}