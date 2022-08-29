/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.aggregateddevice;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * PowerState class provides during the monitoring and controlling process
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
	 * Retrieves {@code {@link #batteryLevel}}
	 *
	 * @return value of {@link #batteryLevel}
	 */
	public String getBatteryLevel() {
		return batteryLevel;
	}

	/**
	 * Sets {@code batteryLevel}
	 *
	 * @param batteryLevel the {@code java.lang.String} field
	 */
	public void setBatteryLevel(String batteryLevel) {
		this.batteryLevel = batteryLevel;
	}
}