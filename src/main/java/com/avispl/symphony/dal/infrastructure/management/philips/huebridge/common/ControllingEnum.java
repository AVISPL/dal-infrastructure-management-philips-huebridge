/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common;

/**
 * ControllingEnum  class defined the enum for monitoring and controlling process
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/14/2022
 * @since 1.0.0
 */
public enum ControllingEnum {

	DEVICE("Device");

	private final String name;

	/**
	 * ControllingEnum instantiation
	 *
	 * @param name {@code {@link #name}}
	 */
	ControllingEnum(String name) {
		this.name = name;
	}

	/**
	 * Retrieves {@code {@link #name}}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
	}

	public static ControllingEnum getMetricByValue(String value) {
		if (DEVICE.getName().equals(value)) {
			return DEVICE;
		}
		return DEVICE;
	}
}