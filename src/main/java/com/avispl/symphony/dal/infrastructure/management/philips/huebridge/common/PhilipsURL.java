/**
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common;

/**
 * PhilipsUtils class defined the URL of the Philips Hue deivce
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/3/2022
 * @since 1.0.0
 */
public enum PhilipsURL {

	DEVICE("Device", true, "clip/v2/resource/device"),
	BRIDGE("bridge", true, "clip/v2/resource/bridge"),
	API("API", false, "api/"),
	CONFIG("Config", true, "/config"),
	ZIGBEE_CONNECTIVITY("ZigbeeConnectivity", true, "clip/v2/resource/zigbee_connectivity/"),
	ROOMS("Rooms", true, "clip/v2/resource/room"),
	ZONES("Zones", true, "clip/v2/resource/zone"),
	GROUP_LIGHT("GroupLight", true, "clip/v2/resource/grouped_light"),
	;

	private final String name;
	private boolean isMonitor;
	private String url;

	/**
	 * PhilipsURL instantiation
	 *
	 * @param name {@code {@link #name}}
	 */
	PhilipsURL(String name, boolean isMonitor, String url) {
		this.name = name;
		this.isMonitor = isMonitor;
		this.url = url;
	}

	/**
	 * Retrieves {@code {@link #name}}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
	}

	/**
	 * Retrieves {@code {@link #isMonitor}}
	 *
	 * @return value of {@link #isMonitor}
	 */
	public boolean isMonitor() {
		return isMonitor;
	}

	/**
	 * Retrieves {@code {@link #url}}
	 *
	 * @return value of {@link #url}
	 */
	public String getUrl() {
		return url;
	}
}