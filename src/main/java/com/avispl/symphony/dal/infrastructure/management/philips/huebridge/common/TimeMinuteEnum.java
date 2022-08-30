/**
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common;

/**
 * NUTEMinuteEnum enum provides time minute for automation during the monitoring and controlling process
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/22/2022
 * @since 1.0.0
 */
public enum TimeMinuteEnum {

	MINUTE_00("00"), MINUTE_01("01"),
	MINUTE_02("02"), MINUTE_03("03"),
	MINUTE_04("04"), MINUTE_05("05"),
	MINUTE_06("06"), MINUTE_07("07"),
	MINUTE_08("08"), MINUTE_09("09"),
	MINUTE_10("10"), MINUTE_11("11"),
	MINUTE_12("12"), MINUTE_13("13"),
	MINUTE_14("14"), MINUTE_15("15"),
	MINUTE_16("16"), MINUTE_17("17"),
	MINUTE_18("18"), MINUTE_19("19"),
	MINUTE_20("20"), MINUTE_21("21"),
	MINUTE_22("22"), MINUTE_23("23"),
	MINUTE_24("24"), MINUTE_25("25"),
	MINUTE_26("26"), MINUTE_27("27"),
	MINUTE_28("28"), MINUTE_29("29"),
	MINUTE_30("30"), MINUTE_31("31"),
	MINUTE_32("32"), MINUTE_33("33"),
	MINUTE_34("34"), MINUTE_35("35"),
	MINUTE_36("36"), MINUTE_37("37"),
	MINUTE_38("38"), MINUTE_39("39"),
	MINUTE_40("40"), MINUTE_41("41"),
	MINUTE_42("42"), MINUTE_43("43"),
	MINUTE_44("44"), MINUTE_45("45"),
	MINUTE_46("46"), MINUTE_47("47"),
	MINUTE_48("48"), MINUTE_49("49"),
	MINUTE_50("50"), MINUTE_51("51"),
	MINUTE_52("52"), MINUTE_53("53"),
	MINUTE_54("54"), MINUTE_55("55"),
	MINUTE_56("56"), MINUTE_57("57"),
	MINUTE_58("58"), MINUTE_59("59"),
	;

	/**
	 * TimeMinuteEnum instantiation
	 *
	 * @param name {@code {@link #name}}
	 */
	TimeMinuteEnum(String name) {
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