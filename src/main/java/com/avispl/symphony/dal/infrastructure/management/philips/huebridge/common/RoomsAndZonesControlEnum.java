package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common;

/**
 * RoomsAndZonesControlEnum class defined the enum for monitoring and controlling process
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/11/2022
 * @since 1.0.0
 */
public enum RoomsAndZonesControlEnum {

	NAME("Name"),
	TYPE("Type"),
	DEVICE_STATUS("DeviceStatus"),
	ACTION("Action"),
	CREATE("Create"),
	CANCEL("CancelChange"),
	DEVICE("Device"),
	DELETE("Delete"),
	APPLY_CHANGE("ApplyChange"),
	DEVICE_ADD("DeviceAdd");

	RoomsAndZonesControlEnum(String name) {
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