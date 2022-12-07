/**
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common;

/**
 * DayEnum class defined list days for monitoring and controlling process
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/23/2022
 * @since 1.0.0
 */
public enum DayEnum {

	MONDAY("Monday"),
	TUESDAY("Tuesday"),
	WEDNESDAY("Wednesday"),
	THURSDAY("Thursday"),
	FRIDAY("Friday"),
	SATURDAY("Saturday"),
	SUNDAY("Sunday"),
	NONE("None"),
	;

	/**
	 * RepeatDayEnum instantiation
	 *
	 * @param name {@link #name}
	 */
	DayEnum(String name) {
		this.name = name;
	}

	private final String name;

	/**
	 * Retrieves {@link #name}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
	}
}