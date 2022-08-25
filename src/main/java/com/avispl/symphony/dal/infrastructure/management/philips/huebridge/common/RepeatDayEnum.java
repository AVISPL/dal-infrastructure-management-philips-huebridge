package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common;

/**
 * RepeatDayEnum class defined list days for monitoring and controlling process
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/23/2022
 * @since 1.0.0
 */
public enum RepeatDayEnum {

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
	 * @param name {@code {@link #name }}
	 */
	RepeatDayEnum(String name) {
		this.name = name;
	}

	private final String name;

	/**
	 * Retrieves {@code {@link #name }}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
	}
}