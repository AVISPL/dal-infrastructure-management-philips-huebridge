/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.responseData;

/**
 * ErrorsResponse class provides during the monitoring and controlling process
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/17/2022
 * @since 1.0.0
 */
public class ErrorsResponse {

	private String description;

	/**
	 * Retrieves {@code {@link #description}}
	 *
	 * @return value of {@link #description}
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets {@code description}
	 *
	 * @param description the {@code java.lang.String} field
	 */
	public void setDescription(String description) {
		this.description = description;
	}
}