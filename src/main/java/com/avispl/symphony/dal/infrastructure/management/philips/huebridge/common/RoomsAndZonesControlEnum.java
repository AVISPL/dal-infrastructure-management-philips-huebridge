package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common;

/**
 * RoomsAndZonesControlEnum enum provides metric name for room and zone
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

	/**
	 * RoomsAndZonesControlEnum instantiation
	 *
	 * @param name {@link #name}
	 */
	RoomsAndZonesControlEnum(String name) {
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