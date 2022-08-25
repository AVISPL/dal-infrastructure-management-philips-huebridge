package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common;

/**
 * StyleEnum  class defined the enum for monitoring and controlling process
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/23/2022
 * @since 1.0.0
 */
public enum StyleEnum {

	SUNRISE("Sunrise","sunrise"),
	FADE_TO_BRIGHT("Fade To Bright","fade_to_bright"),
	;

	/**
	 * StyleEnum instantiation
	 *
	 * @param name {@code {@link #name }}
	 * @param value {@code {@link #value }}
	 */
	StyleEnum(String name,String value) {
		this.name = name;
		this.value = value;
	}

	private final String name;
	private final String value;

	/**
	 * Retrieves {@code {@link #name}}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
	}

	/**
	 * Retrieves {@code {@link #value}}
	 *
	 * @return value of {@link #value}
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Get name of StyleEnum by value
	 *
	 * @param value the value is value of StyleEnum
	 * @return String is StyleEnum value
	 */
	public static String getNameOfEnumByValue(String value) {
		for (StyleEnum style : StyleEnum.values()) {
			if (style.getValue().equalsIgnoreCase(value)) {
				return style.getName();
			}
		}
		return value;
	}
}