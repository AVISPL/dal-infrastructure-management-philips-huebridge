/**
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common;

import java.util.Objects;

/**
 * PhilipsUtil class support getting the URL by metric
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/3/2022
 * @since 1.0.0
 */
public class PhilipsUtil {

	/**
	 * Retrieves the URL for monitoring process
	 *
	 * @param url is instance of PhilipsURL
	 * @return URL is instance of PhilipsURL
	 * @throws Exception if the name is not supported
	 */
	public static String getMonitorURL(PhilipsURL url) {
		Objects.requireNonNull(url);
		switch (url) {
			case DEVICE:
				return PhilipsURL.DEVICE.getUrl();
			case BRIDGE:
				return PhilipsURL.BRIDGE.getUrl();
			case API:
				return PhilipsURL.API.getUrl();
			case CONFIG:
				return PhilipsURL.CONFIG.getUrl();
			case ZIGBEE_CONNECTIVITY:
				return PhilipsURL.ZIGBEE_CONNECTIVITY.getUrl();
			case ROOMS:
				return PhilipsURL.ROOMS.getUrl();
			case ZONES:
				return PhilipsURL.ZONES.getUrl();
			case GROUP_LIGHT:
				return PhilipsURL.GROUP_LIGHT.getUrl();
			default:
				throw new IllegalArgumentException("Do not support Philips Hue metric: " + url.name());
		}
	}
}
