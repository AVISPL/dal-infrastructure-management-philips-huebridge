/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common;

/**
 * CreateRoomEnum class provides property name for room
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/4/2022
 * @since 1.0.0
 */
public enum CreateRoomEnum {

	NAME("Name"),
	DEVICE_0("Device0"),
	TYPE("Type"),
	EDITED("Edited"),
	DEVICE_ADD("DeviceAdd");

	/**
	 * CreateRoomEum instantiation
	 *
	 * @param name {@code {@link #roomName }}
	 */
	CreateRoomEnum(String name) {
		this.roomName = name;
	}

	private final String roomName;

	/**
	 * Retrieves {@link #roomName}
	 *
	 * @return value of {@link #roomName}
	 */
	public String getRoomName() {
		return roomName;
	}
}