/**
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.grouplight;

/**
 * BrightnessLight class provides during the monitoring and controlling process
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/3/2022
 * @since 1.0.0
 */
public class BrightnessLight {

	private String brightness;

	/**
	 * Retrieves {@code {@link #brightness}}
	 *
	 * @return value of {@link #brightness}
	 */
	public String getBrightness() {
		return brightness;
	}

	/**
	 * Sets {@code brightness}
	 *
	 * @param brightness the {@code java.lang.String} field
	 */
	public void setBrightness(String brightness) {
		this.brightness = brightness;
	}
}