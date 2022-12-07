/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.automation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.EnumTypeHandler;

/**
 * CurrentTime class provides current time as hour and minute for automation
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
	 * Retrieves {@link #hour}
	 *
	 * @return value of {@link #hour}
	 */
	public String getHour() {
		return hour;
	}

	/**
	 * Sets {@link #hour} value
	 *
	 * @param hour new value of {@link #hour}
	 */
	public void setHour(String hour) {
		this.hour = hour;
	}

	/**
	 * Retrieves {@link #minute}
	 *
	 * @return value of {@link #minute}
	 */
	public String getMinute() {
		return minute;
	}

	/**
	 * Sets {@link #minute} value
	 *
	 * @param minute new value of {@link #minute}
	 */
	public void setMinute(String minute) {
		this.minute = minute;
	}

	@Override
	public String toString() {
		String hourValue = EnumTypeHandler.getFormatNameByColonValue(hour, "hour", true);
		String minuteValue = EnumTypeHandler.getFormatNameByColonValue(minute, "minute", true);

		return String.format("{%s,%s}", hourValue, minuteValue);
	}
}