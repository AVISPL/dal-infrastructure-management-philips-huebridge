/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.avispl.symphony.api.dal.error.ResourceNotReachableException;
import com.avispl.symphony.dal.util.StringUtils;

/**
 * EnumTypeHandler class provides during the monitoring and controlling process
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/15/2022
 * @since 1.0.0
 */
public class EnumTypeHandler {

	/**
	 * Get an array of all enum names
	 *
	 * @param enumType the enumtype is enum class
	 */
	public static <T extends Enum<T>> String[] getEnumNames(Class<T> enumType) {
		List<String> names = new ArrayList<>();
		for (T c : enumType.getEnumConstants()) {
			try {
				Method method = c.getClass().getMethod("getName");
				String name = (String) method.invoke(c); // getName executed
				names.add(name);
			} catch (Exception e) {
				throw new ResourceNotReachableException("Error to convert enum " + enumType.getSimpleName() + " to names", e);
			}
		}
		return names.toArray(new String[names.size()]);
	}


	/**
	 * Get metric name of enum by name
	 *
	 * @param enumType the enumtype is enum class
	 * @param name is String
	 * @return T is metric instance
	 */
	public static <T extends Enum<T>> T getMetricOfEnumByName(Class<T> enumType, String name) {
		try {
			for (T metric : enumType.getEnumConstants()) {
				Method methodName = metric.getClass().getMethod("getName");
				String nameMetric = (String) methodName.invoke(metric); // getName executed
				if (name.equals(nameMetric)) {
					return metric;
				}
			}
			throw new ResourceNotReachableException("Fail to get enum " + enumType.getSimpleName() + " with name is " + name);
		} catch (Exception e) {
			throw new ResourceNotReachableException(e.getMessage(), e);
		}
	}

	/**
	 * Get toString value by Array
	 *
	 * @param array is Array Object instance
	 * @return String is all value of array
	 */
	public static String getValueByStringArray(String[] array) {
		StringBuilder result = new StringBuilder();
		if (array != null) {
			for (String value : array) {
				result.append(value);
			}
		}
		return result.toString();
	}

	/**
	 * Get format name if value different null or empty
	 *
	 * @param value is value of property
	 * @param name is name of param request to send command
	 * @param isObjectFormat is boolean type value
	 * @return String is format name or empty string
	 */
	public static String getFormatNameByColonValue(String value, String name, boolean isObjectFormat) {
		String format = PhilipsConstant.FORMAT_PERCENT;
		if (isObjectFormat) {
			format = PhilipsConstant.FORMAT_PERCENT_OBJECT;
		}
		return StringUtils.isNullOrEmpty(value) ? PhilipsConstant.EMPTY_STRING : String.format(PhilipsConstant.FORMAT_PERCENT + PhilipsConstant.COLON + format, name, value);
	}
}