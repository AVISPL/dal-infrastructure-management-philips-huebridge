/**
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common;

/**
 * EndState class defined the enum for monitoring and controlling process
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/23/2022
 * @since 1.0.0
 */
public enum EndState {

	LIGHT_OFF("Light Off", "turn_off"),
	WARM_NIGHT_LIGHT("Warm Night Light", "nightlight"),
	DIMMED_LIGHT("Dimmed Light", "dimmed_light"),
	;

	/**
	 * EndState instantiation
	 *
	 * @param name {@code {@link #name }}
	 * @param value {@code {@link #value }}
	 */
	EndState(String name, String value) {
		this.name = name;
		this.value = value;
	}

	private final String name;
	private final String value;

	/**
	 * Retrieves {@code {@link #name}}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
	}

	/**
	 * Retrieves {@code {@link #value}}
	 *
	 * @return value of {@link #value}
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Get name of EndState by value
	 *
	 * @param value the value is value of EndState
	 * @return String is protocol value
	 */
	public static String getNameOfEnumByValue(String value) {
		for (EndState endState : EndState.values()) {
			if (endState.getValue().equalsIgnoreCase(value)) {
				return endState.getName();
			}
		}
		return value;
	}
}