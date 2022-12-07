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
	 * Retrieves {@link #gamut}
	 *
	 * @return value of {@link #gamut}
	 */
	public GamutColor getGamut() {
		return gamut;
	}

	/**
	 * Sets {@link #gamut} value
	 *
	 * @param gamut new value of {@link #gamut}
	 */
	public void setGamut(GamutColor gamut) {
		this.gamut = gamut;
	}

	/**
	 * Retrieves {@link #gamutType}
	 *
	 * @return value of {@link #gamutType}
	 */
	public String getGamutType() {
		return gamutType;
	}

	/**
	 * Sets {@link #gamutType} value
	 *
	 * @param gamutType new value of {@link #gamutType}
	 */
	public void setGamutType(String gamutType) {
		this.gamutType = gamutType;
	}

	/**
	 * Retrieves {@link #locationLight}
	 *
	 * @return value of {@link #locationLight}
	 */
	public LocationLight getLocationLight() {
		return locationLight;
	}

	/**
	 * Sets {@link #locationLight} value
	 *
	 * @param locationLight new value of {@link #locationLight}
	 */
	public void setLocationLight(LocationLight locationLight) {
		this.locationLight = locationLight;
	}
}