/**
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.grouplight;

import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.EnumTypeHandler;

/**
 * ColorPointGamut class provides Gamut of the color
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/30/2022
 * @since 1.0.0
 */
public class ColorPointGamut {

	private float valueA;
	private float valueB;
	/**
	 * ColorPointGamut instantiation
	 *
	 * @param valueA {@code {@link #valueA}}
	 * @param valueB {@code {@link #valueB}}
	 */
	public ColorPointGamut(float valueA, float valueB) {
		this.valueA = valueA;
		this.valueB = valueB;
	}

	/**
	 * Retrieves {@code {@link #valueA}}
	 *
	 * @return value of {@link #valueA}
	 */
	public float getValueA() {
		return valueA;
	}

	/**
	 * Sets {@code valueA}
	 *
	 * @param valueA the {@code float} field
	 */
	public void setValueA(float valueA) {
		this.valueA = valueA;
	}

	/**
	 * Retrieves {@code {@link #valueB}}
	 *
	 * @return value of {@link #valueB}
	 */
	public float getValueB() {
		return valueB;
	}

	/**
	 * Sets {@code valueB}
	 *
	 * @param valueB the {@code float} field
	 */
	public void setValueB(float valueB) {
		this.valueB = valueB;
	}

	@Override
	public String toString() {
		String xValue= EnumTypeHandler.getFormatNameByColonValue(String.valueOf(valueA), "x", true);
		String yValue = EnumTypeHandler.getFormatNameByColonValue(String.valueOf(valueB), "y", true);
		return String.format("{%s}", EnumTypeHandler.getFormatNameByColonValue(String.format("{%s,%s}", xValue,yValue), "color", true));
	}
}