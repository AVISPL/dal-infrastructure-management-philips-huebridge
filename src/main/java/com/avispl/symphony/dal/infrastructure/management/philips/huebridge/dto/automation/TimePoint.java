/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.automation;

import com.fasterxml.jackson.annotation.JsonAlias;

import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.EnumTypeHandler;

/**
 * TimePoint class provides TimePoint as time and type for automation
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
	 * Retrieves {@link #times}
	 *
	 * @return value of {@link #times}
	 */
	public CurrentTime getTimes() {
		return times;
	}

	/**
	 * Sets {@link #times} value
	 *
	 * @param times new value of {@link #times}
	 */
	public void setTimes(CurrentTime times) {
		this.times = times;
	}

	/**
	 * Retrieves {@link #type}
	 *
	 * @return value of {@link #type}
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets {@link #type} value
	 *
	 * @param type new value of {@link #type}
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