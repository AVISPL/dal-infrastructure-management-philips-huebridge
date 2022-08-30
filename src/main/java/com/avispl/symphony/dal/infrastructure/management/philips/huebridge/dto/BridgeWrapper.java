/**
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto;

import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.bridge.BridgeListResponse;

/**
 * BridgeWrapper class provides a wrapper for BridgeResponse
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/2/2022
 * @since 1.0.0
 */
public class BridgeWrapper {

	private BridgeListResponse[] data;

	/**
	 * Retrieves {@code {@link #data}}
	 *
	 * @return value of {@link #data}
	 */
	public BridgeListResponse[] getData() {
		return data;
	}

	/**
	 * Sets {@code data}
	 *
	 * @param data the {@code com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.bridge.BridgeListResponse[]} field
	 */
	public void setData(BridgeListResponse[] data) {
		this.data = data;
	}
}