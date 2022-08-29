/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common;

/**
 * ControllingMetric class provides during the monitoring and controlling process
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/26/2022
 * @since 1.0.0
 */
public enum ControllingMetric {
	AUTOMATION("Automation"),
	ROOM("Room"),
	ZONE("Zone"),
	CREATE_ROOM("CreateRoom"),
	CREATE_ZONE("CreateZone"),
	CREATE_AUTOMATION("CreateAutomationBehaviorInstance"),
	;

	/**
	 * ControllingMetric instantiation
	 *
	 * @param name {@code {@link #name}}
	 */
	ControllingMetric(String name) {
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

	/**
	 * Get metric name by name
	 *
	 * @param name the name is name of controlling metric
	 * @return ControllingMetric instance
	 */
	public static ControllingMetric getMetricByName(String name) {
		for (ControllingMetric metric : ControllingMetric.values()) {
			if (metric.getName().equalsIgnoreCase(name)) {
				return metric;
			}
			if (name.contains(PhilipsConstant.DASH)) {
				String currentName = name.substring(0, name.indexOf(PhilipsConstant.DASH));
				if (metric.getName().equalsIgnoreCase(currentName)) {
					return metric;
				}
			}
		}
		//This null case has been handled
		return null;
	}
}