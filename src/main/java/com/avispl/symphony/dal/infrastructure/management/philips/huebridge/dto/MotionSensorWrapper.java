/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto;

import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.aggregateddevice.MotionDevice;

/**
 * MotionSensorWrapper class provides a wrapper for MotionDevice
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/26/2022
 * @since 1.0.0
 */
public class MotionSensorWrapper {

	private MotionDevice[] data;

	/**
	 * Retrieves {@link #data}
	 *
	 * @return value of {@link #data}
	 */
	public MotionDevice[] getData() {
		return data;
	}

	/**
	 * Sets {@link #data} value
	 *
	 * @param data new value of {@link #data}
	 */
	public void setData(MotionDevice[] data) {
		this.data = data;
	}
}