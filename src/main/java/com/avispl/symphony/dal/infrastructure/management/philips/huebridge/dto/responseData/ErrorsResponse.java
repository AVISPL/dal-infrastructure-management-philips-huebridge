/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.responseData;

/**
 * ErrorsResponse class provides error message for response data
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/17/2022
 * @since 1.0.0
 */
public class ErrorsResponse {

	private String description;

	/**
	 * Retrieves {@link #description}
	 *
	 * @return value of {@link #description}
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets {@link #description} value
	 *
	 * @param description new value of {@link #description}
	 */
	public void setDescription(String description) {
		this.description = description;
	}
}