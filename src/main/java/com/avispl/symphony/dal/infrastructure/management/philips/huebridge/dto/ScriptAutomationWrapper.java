/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto;

import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.scriptautomation.ScriptAutomationResponse;

/**
 * ScriptAutomationWrapper class provides a wrapper for ScriptAutomationResponse
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/23/2022
 * @since 1.0.0
 */
public class ScriptAutomationWrapper {

	private ScriptAutomationResponse[] data;

	/**
	 * Retrieves {@link #data}
	 *
	 * @return value of {@link #data}
	 */
	public ScriptAutomationResponse[] getData() {
		return data;
	}

	/**
	 * Sets {@link #data} value
	 *
	 * @param data new value of {@link #data}
	 */
	public void setData(ScriptAutomationResponse[] data) {
		this.data = data;
	}
}