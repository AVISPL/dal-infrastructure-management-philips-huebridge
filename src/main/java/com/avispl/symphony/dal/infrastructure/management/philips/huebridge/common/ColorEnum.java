/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common;

/**
 * ColorEnum class defined the enum for monitoring and controlling process
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/17/2022
 * @since 1.0.0
 */
public enum ColorEnum {

	RED("Red", "255,0,0"),
	YELLOW("Yellow", "255,255,0"),
	GREEN("Green", "0,128,0"),
	BLUE("Blue", "0,0,255"),
	PURPLE("Purple", "128,0,128"),
	BLACK("Black", "0,0,0"),
	WHITE("White", "255,255,255"),
	ORANGE("Orange", "255,127,0"),
	BROWN("Brown", "165,42,42"),
	;

	private final String name;
	private final String rgbValue;

	/**
	 * ColorEnum instantiation
	 *
	 * @param name {@code {@link #name}}
	 * @param rgbValue {@code {@link #rgbValue}}
	 */
	ColorEnum(String name, String rgbValue) {
		this.name = name;
		this.rgbValue = rgbValue;
	}

	/**
	 * Retrieves {@code {@link #name}}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
	}

	/**
	 * Retrieves {@code {@link #rgbValue}}
	 *
	 * @return value of {@link #rgbValue}
	 */
	public String getRgbValue() {
		return rgbValue;
	}
}