/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto;

import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.aggregateddevice.PowerDevice;

/**
 * DevicePowerWrapper class provides during the monitoring and controlling process
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/25/2022
 * @since 1.0.0
 */
public class DevicePowerWrapper {

	private PowerDevice[] data;

	/**
	 * Retrieves {@code {@link #data}}
	 *
	 * @return value of {@link #data}
	 */
	public PowerDevice[] getData() {
		return data;
	}

	/**
	 * Sets {@code data}
	 *
	 * @param data the {@code com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.aggregateddevice.PowerDevice[]} field
	 */
	public void setData(PowerDevice[] data) {
		this.data = data;
	}
}