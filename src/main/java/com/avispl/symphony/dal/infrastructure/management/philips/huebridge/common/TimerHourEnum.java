/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common;

/**
 * TimerHourEnum class provides time for monitoring and controlling process
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/24/2022
 * @since 1.0.0
 */
public enum TimerHourEnum {

	TIME_00("00"),
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
	TIME_13("13"),
	TIME_14("14"),
	TIME_15("15"),
	TIME_16("16"),
	TIME_17("17"),
	TIME_18("18"),
	TIME_19("19"),
	TIME_20("20"),
	TIME_21("21"),
	TIME_22("22"),
	TIME_23("23"),
	;

	/**
	 * TimerHourEnum instantiation
	 *
	 * @param name {@link #name}
	 */
	TimerHourEnum(String name) {
		this.name = name;
	}

	private final String name;

	/**
	 * Retrieves {@link #name}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
	}
}