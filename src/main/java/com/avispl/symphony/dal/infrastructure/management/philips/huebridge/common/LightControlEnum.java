/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common;

/**
 * LightControlEnum class provides properties light for controlling process
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/26/2022
 * @since 1.0.0
 */
public enum LightControlEnum {

	BRIGHTNESS("brightness"),
	COLOR_TEMPERATURE("colorTemperature(K)"),
	STATUS("status"),
	COLOR_CONTROL("ColorControl"),
	HUE_CONTROL("ColorControlHue"),
	SATURATION_CONTROL("ColorControlSaturation"),
	;

	/**
	 * LightControlEnum instantiation
	 *
	 * @param name {@code {@link #name}}
	 */
	LightControlEnum(String name) {
		this.name = name;
	}

	private final String name;

	/**
	 * Retrieves {@code {@link #name}}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
	}

}