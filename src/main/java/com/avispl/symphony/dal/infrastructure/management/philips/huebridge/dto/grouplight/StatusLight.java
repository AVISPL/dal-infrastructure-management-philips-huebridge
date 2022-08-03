/**
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.grouplight;

/**
 * StatusLight class provides during the monitoring and controlling process
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/3/2022
 * @since 1.0.0
 */
public class StatusLight {

	private boolean on;

	/**
	 * Retrieves {@code {@link #on}}
	 *
	 * @return value of {@link #on}
	 */
	public boolean isOn() {
		return on;
	}

	/**
	 * Sets {@code on}
	 *
	 * @param on the {@code boolean} field
	 */
	public void setOn(boolean on) {
		this.on = on;
	}
}