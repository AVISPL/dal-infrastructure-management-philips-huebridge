/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.automation;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.EnumTypeHandler;

/**
 * TimeAndRepeat class provides during the monitoring and controlling process
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
	 * Retrieves {@code {@link #days}}
	 *
	 * @return value of {@link #days}
	 */
	public String[] getDays() {
		return days;
	}

	/**
	 * Sets {@code days}
	 *
	 * @param days the {@code java.lang.String[]} field
	 */
	public void setDays(String[] days) {
		this.days = days;
	}

	/**
	 * Retrieves {@code {@link #timePoint}}
	 *
	 * @return value of {@link #timePoint}
	 */
	public TimePoint getTimePoint() {
		return timePoint;
	}

	/**
	 * Sets {@code timePoint}
	 *
	 * @param timePoint the {@code com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.automation.TimePoint} field
	 */
	public void setTimePoint(TimePoint timePoint) {
		this.timePoint = timePoint;
	}

	@Override
	public String toString() {
		String timePointValue = EnumTypeHandler.getFormatNameByColonValue(timePoint.toString(), "time_point", true);
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("[");
		for (String day : days) {
			if (days[days.length - 1].equals(day)) {
				stringBuilder.append("\"" + day + "\"");
			} else {
				stringBuilder.append("\"" + day + "\"" + ",");
			}
		}
		stringBuilder.append("]");
		return String.format("{%s,%s}", stringBuilder, timePointValue);
	}
}