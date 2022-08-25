/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.scriptautomation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * MetaDataAutomation class contains name and category
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/2/2022
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MetaDataAutomation {

	private String name;
	private String category;

	/**
	 * Retrieves {@code {@link #name}}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets {@code name}
	 *
	 * @param name the {@code java.lang.String} field
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Retrieves {@code {@link #category}}
	 *
	 * @return value of {@link #category}
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * Sets {@code category}
	 *
	 * @param category the {@code java.lang.String} field
	 */
	public void setCategory(String category) {
		this.category = category;
	}
}