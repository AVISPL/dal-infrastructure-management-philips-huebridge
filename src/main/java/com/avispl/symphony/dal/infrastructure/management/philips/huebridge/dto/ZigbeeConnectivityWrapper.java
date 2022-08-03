/**
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto;

import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.zigbeeconnectivity.ZigbeeConnectivity;

/**
 * ZigbeeConnectivityWrapper class provides during the monitoring and controlling process
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/2/2022
 * @since 1.0.0
 */
public class ZigbeeConnectivityWrapper {

	private ZigbeeConnectivity[] data;

	/**
	 * Retrieves {@code {@link #data}}
	 *
	 * @return value of {@link #data}
	 */
	public ZigbeeConnectivity[] getData() {
		return data;
	}

	/**
	 * Sets {@code data}
	 *
	 * @param data the {@code com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.zigbeeconnectivity.ZigbeeConnectivity[]} field
	 */
	public void setData(ZigbeeConnectivity[] data) {
		this.data = data;
	}
}