/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.aggregateddevice;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * PowerState class provides the battery for the device
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/25/2022
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PowerState {

	@JsonAlias("battery_level")
	private String batteryLevel;

	/**
	 * Retrieves {@link #batteryLevel}
	 *
	 * @return value of {@link #batteryLevel}
	 */
	public String getBatteryLevel() {
		return batteryLevel;
	}

	/**
	 * Sets {@link #batteryLevel} value
	 *
	 * @param batteryLevel new value of {@link #batteryLevel}
	 */
	public void setBatteryLevel(String batteryLevel) {
		this.batteryLevel = batteryLevel;
	}
}