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
	 * @param valueA {@link #valueA}
	 * @param valueB {@link #valueB}
	 */
	public ColorPointGamut(float valueA, float valueB) {
		this.valueA = valueA;
		this.valueB = valueB;
	}

	/**
	 * Retrieves {@link #valueA}
	 *
	 * @return value of {@link #valueA}
	 */
	public float getValueA() {
		return valueA;
	}

	/**
	 * Sets {@link #valueA} value
	 *
	 * @param valueA new value of {@link #valueA}
	 */
	public void setValueA(float valueA) {
		this.valueA = valueA;
	}

	/**
	 * Retrieves {@link #valueB}
	 *
	 * @return value of {@link #valueB}
	 */
	public float getValueB() {
		return valueB;
	}

	/**
	 * Sets {@link #valueB} value
	 *
	 * @param valueB new value of {@link #valueB}
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