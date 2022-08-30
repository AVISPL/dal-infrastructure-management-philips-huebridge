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
	 * Retrieves {@code {@link #axisX}}
	 *
	 * @return value of {@link #axisX}
	 */
	public String getAxisX() {
		return axisX;
	}

	/**
	 * Sets {@code axisX}
	 *
	 * @param axisX the {@code java.lang.String} field
	 */
	public void setAxisX(String axisX) {
		this.axisX = axisX;
	}

	/**
	 * Retrieves {@code {@link #axisY}}
	 *
	 * @return value of {@link #axisY}
	 */
	public String getAxisY() {
		return axisY;
	}

	/**
	 * Sets {@code axisY}
	 *
	 * @param axisY the {@code java.lang.String} field
	 */
	public void setAxisY(String axisY) {
		this.axisY = axisY;
	}
}