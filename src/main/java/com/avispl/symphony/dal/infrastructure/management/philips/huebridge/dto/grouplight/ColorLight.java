/**
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.grouplight;

import com.fasterxml.jackson.annotation.JsonAlias;

/**
 * ColorLight class provides information of the color light
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/30/2022
 * @since 1.0.0
 */
public class ColorLight {

	private GamutColor gamut;

	@JsonAlias("gamut_type")
	private String gamutType;

	@JsonAlias("xy")
	private LocationLight locationLight;

	/**
	 * Retrieves {@code {@link #gamut}}
	 *
	 * @return value of {@link #gamut}
	 */
	public GamutColor getGamut() {
		return gamut;
	}

	/**
	 * Sets {@code gamut}
	 *
	 * @param gamut the {@code com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.grouplight.GamutColor} field
	 */
	public void setGamut(GamutColor gamut) {
		this.gamut = gamut;
	}

	/**
	 * Retrieves {@code {@link #gamutType}}
	 *
	 * @return value of {@link #gamutType}
	 */
	public String getGamutType() {
		return gamutType;
	}

	/**
	 * Sets {@code gamutType}
	 *
	 * @param gamutType the {@code java.lang.String} field
	 */
	public void setGamutType(String gamutType) {
		this.gamutType = gamutType;
	}

	/**
	 * Retrieves {@code {@link #locationLight}}
	 *
	 * @return value of {@link #locationLight}
	 */
	public LocationLight getLocationLight() {
		return locationLight;
	}

	/**
	 * Sets {@code locationLight}
	 *
	 * @param locationLight the {@code com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.grouplight.LocationLight} field
	 */
	public void setLocationLight(LocationLight locationLight) {
		this.locationLight = locationLight;
	}
}