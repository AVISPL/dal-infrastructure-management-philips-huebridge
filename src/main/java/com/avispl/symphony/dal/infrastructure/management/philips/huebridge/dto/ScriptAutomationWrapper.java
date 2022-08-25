/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto;

import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.scriptautomation.ScriptAutomationResponse;

/**
 * ScriptAutomation class provides during the monitoring and controlling process
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/23/2022
 * @since 1.0.0
 */
public class ScriptAutomationWrapper {

	private ScriptAutomationResponse[] data;

	/**
	 * Retrieves {@code {@link #data}}
	 *
	 * @return value of {@link #data}
	 */
	public ScriptAutomationResponse[] getData() {
		return data;
	}

	/**
	 * Sets {@code data}
	 *
	 * @param data the {@code com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.scriptautomation.ScriptAutomationResponse[]} field
	 */
	public void setData(ScriptAutomationResponse[] data) {
		this.data = data;
	}
}