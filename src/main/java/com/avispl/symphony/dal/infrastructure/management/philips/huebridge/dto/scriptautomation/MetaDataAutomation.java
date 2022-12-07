/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.scriptautomation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * MetaDataAutomation class provides metadata as name and category for automation
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
	 * Retrieves {@link #name}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets {@link #name} value
	 *
	 * @param name new value of {@link #name}
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Retrieves {@link #category}
	 *
	 * @return value of {@link #category}
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * Sets {@link #category} value
	 *
	 * @param category new value of {@link #category}
	 */
	public void setCategory(String category) {
		this.category = category;
	}
}