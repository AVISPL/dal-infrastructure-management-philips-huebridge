/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.automation;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.EnumTypeHandler;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.PhilipsConstant;

/**
 * TimeAndRepeat class provides time, day, and repeat for automation
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/23/2022
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TimeAndRepeat {

	@JsonAlias("recurrence_days")
	private String[] days;

	@JsonAlias("time_point")
	private TimePoint timePoint;

	/**
	 * Retrieves {@link #days}
	 *
	 * @return value of {@link #days}
	 */
	public String[] getDays() {
		return days;
	}

	/**
	 * Sets {@link #days} value
	 *
	 * @param days new value of {@link #days}
	 */
	public void setDays(String[] days) {
		this.days = days;
	}

	/**
	 * Retrieves {@link #timePoint}
	 *
	 * @return value of {@link #timePoint}
	 */
	public TimePoint getTimePoint() {
		return timePoint;
	}

	/**
	 * Sets {@link #timePoint} value
	 *
	 * @param timePoint new value of {@link #timePoint}
	 */
	public void setTimePoint(TimePoint timePoint) {
		this.timePoint = timePoint;
	}

	@Override
	public String toString() {
		String timePointValue = EnumTypeHandler.getFormatNameByColonValue(timePoint.toString(), "time_point", true);
		StringBuilder stringBuilder = new StringBuilder();
		int dayIndex = 0;
		for (String day : days) {
			String dayValue = String.format(PhilipsConstant.FORMAT_PERCENT + ",", day);
			if (dayIndex == days.length - 1) {
				dayValue = String.format(PhilipsConstant.FORMAT_PERCENT, day);
			}
			stringBuilder.append(dayValue);
			dayIndex++;
		}
		String value = days == null ? PhilipsConstant.EMPTY_STRING : EnumTypeHandler.getFormatNameByColonValue(String.format("[%s]", stringBuilder), "recurrence_days", true);
		value = days == null || days.length == 0 ? PhilipsConstant.EMPTY_STRING : String.format(",%s", value);
		return String.format("{%s %s", timePointValue, value);
	}
}