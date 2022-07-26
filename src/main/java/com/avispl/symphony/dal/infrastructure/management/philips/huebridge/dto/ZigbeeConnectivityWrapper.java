/**
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto;

import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.zigbeeconnectivity.ZigbeeConnectivity;

/**
 * ZigbeeConnectivityWrapper class provides a wrapper for ZigbeeConnectivity
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/2/2022
 * @since 1.0.0
 */
public class ZigbeeConnectivityWrapper {

	private ZigbeeConnectivity[] data;

	/**
	 * Retrieves {@link #data}
	 *
	 * @return value of {@link #data}
	 */
	public ZigbeeConnectivity[] getData() {
		return data;
	}

	/**
	 * Sets {@link #data} value
	 *
	 * @param data new value of {@link #data}
	 */
	public void setData(ZigbeeConnectivity[] data) {
		this.data = data;
	}
}