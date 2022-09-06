/**
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.system;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Services class provides id and type for service device
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/2/2022
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServicesResponse {

	@JsonAlias("rid")
	private String id;

	@JsonAlias("rtype")
	private String type;

	/**
	 * Retrieves {@link #id}
	 *
	 * @return value of {@link #id}
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets {@link #id} value
	 *
	 * @param id new value of {@link #id}
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Retrieves {@link #type}
	 *
	 * @return value of {@link #type}
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets {@link #type} value
	 *
	 * @param type new value of {@link #type}
	 */
	public void setType(String type) {
		this.type = type;
	}
}