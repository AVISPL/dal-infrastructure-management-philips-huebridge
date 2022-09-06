/**
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.grouplight;

import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.EnumTypeHandler;

/**
 * ColorTemperature class provides info about the color temperature of the light device
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/3/2022
 * @since 1.0.0
 */
public class ColorTemperature {

	private String mirek;

	/**
	 * Retrieves {@link #mirek}
	 *
	 * @return value of {@link #mirek}
	 */
	public String getMirek() {
		return mirek;
	}

	/**
	 * Sets {@link #mirek} value
	 *
	 * @param mirek new value of {@link #mirek}
	 */
	public void setMirek(String mirek) {
		this.mirek = mirek;
	}

	@Override
	public String toString() {
		String result = String.format("{%s}", EnumTypeHandler.getFormatNameByColonValue(mirek, "mirek", true));
		return String.format("{%s}", EnumTypeHandler.getFormatNameByColonValue(result, "color_temperature", true));
	}
}