/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.responseData.ErrorsResponse;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.romandzone.Children;

/**
 * ResponseData class provides information for the response data request
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/17/2022
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseData {

	private Children[] data;
	private ErrorsResponse[] errors;

	/**
	 * Retrieves {@link #data}
	 *
	 * @return value of {@link #data}
	 */
	public Children[] getData() {
		return data;
	}

	/**
	 * Sets {@link #data} value
	 *
	 * @param data new value of {@link #data}
	 */
	public void setData(Children[] data) {
		this.data = data;
	}

	/**
	 * Retrieves {@link #errors}
	 *
	 * @return value of {@link #errors}
	 */
	public ErrorsResponse[] getErrors() {
		return errors;
	}

	/**
	 * Sets {@link #errors} value
	 *
	 * @param errors new value of {@link #errors}
	 */
	public void setErrors(ErrorsResponse[] errors) {
		this.errors = errors;
	}
}