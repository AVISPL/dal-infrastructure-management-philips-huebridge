/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto;

import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.light.LightResponse;

/**
 * LightWrapper class provides a wrapper for get all lights request
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 9/6/2022
 * @since 1.0.0
 */
public class LightWrapper {

	private LightResponse[] data;

	/**
	 * Retrieves {@link #data}
	 *
	 * @return value of {@link #data}
	 */
	public LightResponse[] getData() {
		return data;
	}

	/**
	 * Sets {@link #data} value
	 *
	 * @param data new value of {@link #data}
	 */
	public void setData(LightResponse[] data) {
		this.data = data;
	}
}
