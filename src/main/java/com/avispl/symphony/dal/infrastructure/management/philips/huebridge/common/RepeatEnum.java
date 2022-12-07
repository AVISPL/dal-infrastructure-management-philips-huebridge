/**
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common;

/**
 * RepeatDayEnum class defined list days for monitoring and controlling process
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/23/2022
 * @since 1.0.0
 */
public enum RepeatEnum {

	REPEAT_MONDAY("RepeatMonday"),
	REPEAT_TUESDAY("RepeatTuesday"),
	REPEAT_THURSDAY("RepeatThursday"),
	REPEAT_WEDNESDAY("RepeatWednesday"),
	REPEAT_FRIDAY("RepeatFriday"),
	REPEAT_SATURDAY("RepeatSaturday"),
	REPEAT_SUNDAY("RepeatSunday"),
	;

	/**
	 * RepeatEnum instantiation
	 *
	 * @param name {@link #name}
	 */
	RepeatEnum(String name) {
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