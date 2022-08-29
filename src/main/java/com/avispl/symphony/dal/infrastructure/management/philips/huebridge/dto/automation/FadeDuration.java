/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.automation;

import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.EnumTypeHandler;

/**
 * FadeDuration class provides during the monitoring and controlling process
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/24/2022
 * @since 1.0.0
 */
public class FadeDuration {

	private String seconds;

	/**
	 * Retrieves {@code {@link #seconds}}
	 *
	 * @return value of {@link #seconds}
	 */
	public String getSeconds() {
		return seconds;
	}

	/**
	 * Sets {@code seconds}
	 *
	 * @param seconds the {@code java.lang.String} field
	 */
	public void setSeconds(String seconds) {
		this.seconds = seconds;
	}

	@Override
	public String
	toString() {
		return String.format("{%s}", EnumTypeHandler.getFormatNameByColonValue(seconds, "seconds", true));
	}
}