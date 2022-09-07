package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common;

import com.avispl.symphony.dal.util.StringUtils;

/**
 * StyleEnum enum provides style information for the automation process
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/23/2022
 * @since 1.0.0
 */
public enum StyleEnum {

	SUNRISE("Sunrise", "sunrise"),
	FADE_TO_BRIGHT("Fade To Bright", "basic"),
	;

	/**
	 * StyleEnum instantiation
	 *
	 * @param name {@code {@link #name }}
	 * @param value {@code {@link #value }}
	 */
	StyleEnum(String name, String value) {
		this.name = name;
		this.value = value;
	}

	private final String name;
	private final String value;

	/**
	 * Retrieves {@link #name}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
	}

	/**
	 * Retrieves {@link #value}
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
		if (StringUtils.isNullOrEmpty(value)) {
			return PhilipsConstant.NONE;
		}
		for (StyleEnum style : StyleEnum.values()) {
			if (style.getValue().equalsIgnoreCase(value)) {
				return style.getName();
			}
		}
		return value;
	}

	/**
	 * Get value of StyleEnum by name
	 *
	 * @param name the name is name of StyleEnum
	 * @return String is StyleEnum name
	 */
	public static String getValueOfEnumByName(String name) {
		if (StringUtils.isNullOrEmpty(name)) {
			return PhilipsConstant.NONE;
		}
		for (StyleEnum style : StyleEnum.values()) {
			if (style.getName().equalsIgnoreCase(name)) {
				return style.getValue();
			}
		}
		return name;
	}
}