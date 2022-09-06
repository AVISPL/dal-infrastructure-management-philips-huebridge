/**
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common;

/**
 * PhilipsUtils class defined the URL of the Philips Hue device
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/3/2022
 * @since 1.0.0
 */
public enum PhilipsURL {

	DEVICE("clip/v2/resource/device"),
	BRIDGE("clip/v2/resource/bridge"),
	API("api/"),
	CONFIG("/config"),
	ZIGBEE_CONNECTIVITY("clip/v2/resource/zigbee_connectivity/"),
	ROOMS("clip/v2/resource/room"),
	ZONES("clip/v2/resource/zone"),
	GROUP_LIGHT("clip/v2/resource/grouped_light"),
	AUTOMATION("clip/v2/resource/behavior_instance"),
	SCRIPT_ID("clip/v2/resource/behavior_script"),
	LIGHT("clip/v2/resource/light"),
	BUTTON_POWER("/clip/v2/resource/device_power/"),
	MOTION_SENSOR("/clip/v2/resource/motion/"),
	;
	private String url;

	/**
	 * PhilipsURL instantiation
	 *
	 * @param url {@code {@link #url}}
	 */
	PhilipsURL(String url) {
		this.url = url;
	}

	/**
	 * Retrieves {@link #url}
	 *
	 * @return value of {@link #url}
	 */
	public String getUrl() {
		return url;
	}
}