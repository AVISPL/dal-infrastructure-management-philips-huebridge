/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto;

import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.aggregateddevice.PowerDevice;

/**
 * DevicePowerWrapper class provides a wrapper for DevicePower
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/25/2022
 * @since 1.0.0
 */
public class DevicePowerWrapper {

	private PowerDevice[] data;

	/**
	 * Retrieves {@link #data}
	 *
	 * @return value of {@link #data}
	 */
	public PowerDevice[] getData() {
		return data;
	}

	/**
	 * Sets {@link #data} value
	 *
	 * @param data new value of {@link #data}
	 */
	public void setData(PowerDevice[] data) {
		this.data = data;
	}
}