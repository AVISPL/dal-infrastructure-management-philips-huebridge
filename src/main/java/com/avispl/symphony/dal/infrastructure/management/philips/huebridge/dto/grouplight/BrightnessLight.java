/**
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.grouplight;

import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.EnumTypeHandler;

/**
 * BrightnessLight class provides info about the brightness of the light device
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/3/2022
 * @since 1.0.0
 */
public class BrightnessLight {

	private String brightness;

	/**
	 * Retrieves {@link #brightness}
	 *
	 * @return value of {@link #brightness}
	 */
	public String getBrightness() {
		return brightness;
	}

	/**
	 * Sets {@link #brightness} value
	 *
	 * @param brightness new value of {@link #brightness}
	 */
	public void setBrightness(String brightness) {
		this.brightness = brightness;
	}

	@Override
	public String toString() {
		String result = String.format("{%s}", EnumTypeHandler.getFormatNameByColonValue(brightness, "brightness", true));
		return String.format("{%s}", EnumTypeHandler.getFormatNameByColonValue(result, "dimming", true));
	}
}