/**
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common;

/**
 * SystemInfoEnum enum provides system information for the monitoring and controlling process
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/2/2022
 * @since 1.0.0
 */
public enum SystemInfoEnum {

	ID("ID"),
	MANUFACTURER("Manufacturer"),
	MODEL("Model"),
	ARCHETYPE("Archetype"),
	NAME("Name"),
	VERSION("SoftwareVersion"),
	TYPE("Type"),
	;

	/**
	 * SystemInfoEnum instantiation
	 *
	 * @param name {@code {@link #name}}
	 */
	SystemInfoEnum(String name) {
		this.name = name;
	}
	private final String name;

	/**
	 * Retrieves {@code {@link #name}}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
	}
}