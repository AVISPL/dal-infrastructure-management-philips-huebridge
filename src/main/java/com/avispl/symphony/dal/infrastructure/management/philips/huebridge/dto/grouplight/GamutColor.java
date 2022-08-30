/**
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.grouplight;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * GamutColor class provides gamut color is blue, green, red for color light device
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/30/2022
 * @since 1.0.0
 */@JsonIgnoreProperties(ignoreUnknown = true)
public class GamutColor {

	private LocationLight blue;
	private LocationLight green;
	private LocationLight red;

	/**
	 * Retrieves {@code {@link #blue}}
	 *
	 * @return value of {@link #blue}
	 */
	public LocationLight getBlue() {
		return blue;
	}

	/**
	 * Sets {@code blue}
	 *
	 * @param blue the {@code com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.grouplight.LocationLight} field
	 */
	public void setBlue(LocationLight blue) {
		this.blue = blue;
	}

	/**
	 * Retrieves {@code {@link #green}}
	 *
	 * @return value of {@link #green}
	 */
	public LocationLight getGreen() {
		return green;
	}

	/**
	 * Sets {@code green}
	 *
	 * @param green the {@code com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.grouplight.LocationLight} field
	 */
	public void setGreen(LocationLight green) {
		this.green = green;
	}

	/**
	 * Retrieves {@code {@link #red}}
	 *
	 * @return value of {@link #red}
	 */
	public LocationLight getRed() {
		return red;
	}

	/**
	 * Sets {@code red}
	 *
	 * @param red the {@code com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.grouplight.LocationLight} field
	 */
	public void setRed(LocationLight red) {
		this.red = red;
	}
}