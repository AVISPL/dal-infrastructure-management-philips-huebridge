/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.automation;

import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.EnumTypeHandler;

/**
 * FadeDuration class provides fade duration for automation
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/24/2022
 * @since 1.0.0
 */
public class FadeDuration {

	private String seconds;

	/**
	 * Retrieves {@link #seconds}
	 *
	 * @return value of {@link #seconds}
	 */
	public String getSeconds() {
		return seconds;
	}

	/**
	 * Sets {@link #seconds} value
	 *
	 * @param seconds new value of {@link #seconds}
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