/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.aggregateddevice;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * MotionDeviceDetails class provides information of the motion sensor device
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/26/2022
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MotionDeviceDetails {

	private boolean motion;

	@JsonAlias("motion_valid")
	private boolean motionDetected;

	/**
	 * Retrieves {@link #motion}
	 *
	 * @return value of {@link #motion}
	 */
	public boolean isMotion() {
		return motion;
	}

	/**
	 * Sets {@link #motion} value
	 *
	 * @param motion new value of {@link #motion}
	 */
	public void setMotion(boolean motion) {
		this.motion = motion;
	}

	/**
	 * Retrieves {@link #motionDetected}
	 *
	 * @return value of {@link #motionDetected}
	 */
	public boolean isMotionDetected() {
		return motionDetected;
	}

	/**
	 * Sets {@link #motionDetected} value
	 *
	 * @param motionDetected new value of {@link #motionDetected}
	 */
	public void setMotionDetected(boolean motionDetected) {
		this.motionDetected = motionDetected;
	}
}