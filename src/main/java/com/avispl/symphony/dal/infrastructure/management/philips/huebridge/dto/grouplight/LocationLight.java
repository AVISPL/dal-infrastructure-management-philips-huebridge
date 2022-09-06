/**
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.grouplight;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * LocationLight class provides location with coordinates x,y
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/30/2022
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocationLight {

	@JsonAlias("x")
	private String axisX;

	@JsonAlias("y")
	private String axisY;

	/**
	 * Retrieves {@link #axisX}
	 *
	 * @return value of {@link #axisX}
	 */
	public String getAxisX() {
		return axisX;
	}

	/**
	 * Sets {@link #axisX} value
	 *
	 * @param axisX new value of {@link #axisX}
	 */
	public void setAxisX(String axisX) {
		this.axisX = axisX;
	}

	/**
	 * Retrieves {@link #axisY}
	 *
	 * @return value of {@link #axisY}
	 */
	public String getAxisY() {
		return axisY;
	}

	/**
	 * Sets {@link #axisY} value
	 *
	 * @param axisY new value of {@link #axisY}
	 */
	public void setAxisY(String axisY) {
		this.axisY = axisY;
	}
}