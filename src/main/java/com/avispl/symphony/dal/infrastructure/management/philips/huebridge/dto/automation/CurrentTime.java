/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.automation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.EnumTypeHandler;

/**
 * CurrentTime class provides during the monitoring and controlling process
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/24/2022
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrentTime {

	private String hour;
	private String minute;

	/**
	 * Retrieves {@code {@link #hour}}
	 *
	 * @return value of {@link #hour}
	 */
	public String getHour() {
		return hour;
	}

	/**
	 * Sets {@code hour}
	 *
	 * @param hour the {@code java.lang.String} field
	 */
	public void setHour(String hour) {
		this.hour = hour;
	}

	/**
	 * Retrieves {@code {@link #minute}}
	 *
	 * @return value of {@link #minute}
	 */
	public String getMinute() {
		return minute;
	}

	/**
	 * Sets {@code minute}
	 *
	 * @param minute the {@code java.lang.String} field
	 */
	public void setMinute(String minute) {
		this.minute = minute;
	}

	@Override
	public String toString() {
		String hourValue = EnumTypeHandler.getFormatNameByColonValue(hour, "hour", false);
		String minuteValue = EnumTypeHandler.getFormatNameByColonValue(minute, "minute", false);

		return String.format("{%s,%s}", hourValue, minuteValue);
	}
}