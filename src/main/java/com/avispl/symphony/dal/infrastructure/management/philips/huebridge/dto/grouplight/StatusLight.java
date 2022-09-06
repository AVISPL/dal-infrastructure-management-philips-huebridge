/**
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.grouplight;

import java.util.Locale;

import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.EnumTypeHandler;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.PhilipsConstant;

/**
 * StatusLight class provides info about the status of the light device
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/3/2022
 * @since 1.0.0
 */
public class StatusLight {

	private boolean on;

	/**
	 * Retrieves {@link #on}
	 *
	 * @return value of {@link #on}
	 */
	public boolean isOn() {
		return on;
	}

	/**
	 * Sets {@link #on} value
	 *
	 * @param on new value of {@link #on}
	 */
	public void setOn(boolean on) {
		this.on = on;
	}

	@Override
	public String toString() {
		String value = on ? PhilipsConstant.TRUE.toLowerCase(Locale.ROOT) : PhilipsConstant.FALSE.toLowerCase(Locale.ROOT);
		String result = String.format("{%s}", EnumTypeHandler.getFormatNameByColonValue(value, "on", true));
		return String.format("{%s}", EnumTypeHandler.getFormatNameByColonValue(result, "on", true));
	}
}