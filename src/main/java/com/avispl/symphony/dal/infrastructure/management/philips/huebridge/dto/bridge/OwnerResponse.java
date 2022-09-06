/**
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.bridge;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * OwnerResponse class provides Owner as id and type for the device
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/2/2022
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OwnerResponse {

	private String rid;

	@JsonAlias("rtype")
	private String type;

	/**
	 * Retrieves {@code {@link #rid}}
	 *
	 * @return value of {@link #rid}
	 */
	public String getRid() {
		return rid;
	}

	/**
	 * Sets {@code rid}
	 *
	 * @param rid the {@code java.lang.String} field
	 */
	public void setRid(String rid) {
		this.rid = rid;
	}

	/**
	 * Retrieves {@code {@link #type}}
	 *
	 * @return value of {@link #type}
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets {@code type}
	 *
	 * @param type the {@code java.lang.String} field
	 */
	public void setType(String type) {
		this.type = type;
	}
}