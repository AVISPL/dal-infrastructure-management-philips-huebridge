/**
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common;

/**
 * TypeOfDeviceEnum enum provides type of device for monitoring and controlling process
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/22/2022
 * @since 1.0.0
 */
public enum TypeOfDeviceEnum {

	DEVICE("Device"),
	ROOM("Room"),
	ZONE("Zone"),
	;

	/**
	 * TypeOfDeviceEnum instantiation
	 *
	 * @param name {@code {@link #name}}
	 */
	TypeOfDeviceEnum(String name) {
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