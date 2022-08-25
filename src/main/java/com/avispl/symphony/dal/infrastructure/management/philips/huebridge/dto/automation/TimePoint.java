/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.automation;

import com.fasterxml.jackson.annotation.JsonAlias;

import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.EnumTypeHandler;

/**
 * TimePoint class provides during the monitoring and controlling process
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/23/2022
 * @since 1.0.0
 */
public class TimePoint {

	@JsonAlias("time")
	private CurrentTime times;

	private String type;

	/**
	 * Retrieves {@code {@link #times}}
	 *
	 * @return value of {@link #times}
	 */
	public CurrentTime getTimes() {
		return times;
	}

	/**
	 * Sets {@code times}
	 *
	 * @param times the {@code com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.automation.CurrentTime} field
	 */
	public void setTimes(CurrentTime times) {
		this.times = times;
	}

	/**
	 * Retrieves {@code {@link #type}}
	 *
	 * @return value of {@link #type}
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets {@code type}
	 *
	 * @param type the {@code java.lang.String} field
	 */
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		String timeValue = EnumTypeHandler.getFormatNameByColonValue(times.toString(), "time", true);
		String typeValue = EnumTypeHandler.getFormatNameByColonValue(type, "type", false);

		return String.format("{%s,%s}", timeValue, typeValue);
	}
}