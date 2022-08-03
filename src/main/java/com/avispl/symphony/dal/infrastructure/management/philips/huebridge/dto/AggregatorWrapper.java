/**
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto;

import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.aggregateddevice.AggregatorDeviceResponse;

/**
 * AggregatorWrapper class provides during the monitoring and controlling process
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/2/2022
 * @since 1.0.0
 */
public class AggregatorWrapper {
	
	private AggregatorDeviceResponse[] data;

	/**
	 * Retrieves {@code {@link #data}}
	 *
	 * @return value of {@link #data}
	 */
	public AggregatorDeviceResponse[] getData() {
		return data;
	}

	/**
	 * Sets {@code data}
	 *
	 * @param data the {@code com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.aggregateddevice.AggregatorDeviceResponse[]} field
	 */
	public void setData(AggregatorDeviceResponse[] data) {
		this.data = data;
	}
}