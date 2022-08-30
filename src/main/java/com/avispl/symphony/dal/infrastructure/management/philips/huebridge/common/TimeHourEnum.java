/**
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common;

/**
 * TimeHourEnum enum contains time of device
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/22/2022
 * @since 1.0.0
 */
public enum TimeHourEnum {

	TIME_01("01"),
	TIME_02("02"),
	TIME_03("03"),
	TIME_04("04"),
	TIME_05("05"),
	TIME_06("06"),
	TIME_07("07"),
	TIME_08("08"),
	TIME_09("09"),
	TIME_10("10"),
	TIME_11("11"),
	TIME_12("12"),
	;

	/**
	 * TimeHourEnum instantiation
	 *
	 * @param name {@code {@link #name}}
	 */
	TimeHourEnum(String name) {
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