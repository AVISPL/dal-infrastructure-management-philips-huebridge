/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.scriptautomation;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * ScriptAutomationResponse class provides during the monitoring and controlling process
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/23/2022
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScriptAutomationResponse {

	private String id;

	@JsonAlias("metadata")
	private MetaDataAutomation metadata;

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
	 * Retrieves {@link #metadata}
	 *
	 * @return value of {@link #metadata}
	 */
	public MetaDataAutomation getMetadata() {
		return metadata;
	}

	/**
	 * Sets {@link #metadata} value
	 *
	 * @param metadata new value of {@link #metadata}
	 */
	public void setMetadata(MetaDataAutomation metadata) {
		this.metadata = metadata;
	}
}