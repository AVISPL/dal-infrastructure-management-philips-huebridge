/**
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common;

/**
 * TypeOfAutomation enum provides type automation during the monitoring and controlling process
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/22/2022
 * @since 1.0.0
 */
public enum TypeOfAutomation {

	TIMER("Timer"),
	WAKE_UP_WITH_LIGHT("WakeUpWithLight"),
	GO_TO_SLEEP("GoToSleep"),
	;

	/**
	 * TypeOfAutomation instantiation
	 *
	 * @param name {@code {@link #name}}
	 */
	TypeOfAutomation(String name) {
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