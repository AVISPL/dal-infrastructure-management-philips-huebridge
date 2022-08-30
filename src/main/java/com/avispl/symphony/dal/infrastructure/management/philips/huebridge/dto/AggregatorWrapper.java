/**
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto;

import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.aggregateddevice.AggregatedDeviceResponse;

/**
 * AggregatorWrapper class provides a wrapper for AggregatorResponse
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/2/2022
 * @since 1.0.0
 */
public class AggregatorWrapper {
	
	private AggregatedDeviceResponse[] data;

	/**
	 * Retrieves {@code {@link #data}}
	 *
	 * @return value of {@link #data}
	 */
	public AggregatedDeviceResponse[] getData() {
		return data;
	}

	/**
	 * Sets {@code data}
	 *
	 * @param data the {@code com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.aggregateddevice.AggregatorDeviceResponse[]} field
	 */
	public void setData(AggregatedDeviceResponse[] data) {
		this.data = data;
	}
}