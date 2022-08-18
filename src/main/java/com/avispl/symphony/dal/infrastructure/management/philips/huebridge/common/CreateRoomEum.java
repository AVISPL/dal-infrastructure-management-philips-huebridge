/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common;

/**
 * CreateRoomEum  class defined the enum for monitoring and controlling process
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/4/2022
 * @since 1.0.0
 */
public enum CreateRoomEum {

	NAME("Name"),
	DEVICE_0("Device0"),
	TYPE("Type"),
	EDITED("Edited"),
	DEVICE_ADD("DeviceAdd");

	CreateRoomEum(String name) {
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