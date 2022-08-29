/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common;

/**
 * AutomationEnum  class defined the enum for monitoring and controlling process
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/22/2022
 * @since 1.0.0
 */
public enum AutomationEnum {

	ACTION("Action"),
	FADE_DURATION("FadeDuration"),
	NAME("Name"),
	REPEAT("Repeat"),
	REPEAT_ADD("RepeatAdd"),
	STATUS("Status"),
	TIME_CURRENT("TimeCurrent"),
	TIME_HOUR("TimeHour"),
	TIME_MINUTE("TimeMinute"),
	TYPE("Type"),
	TYPE_OF_AUTOMATION("TypeOfAutomation"),
	CANCEL("CancelChange"),
	CREATE("Create"),
	DEVICE_ADD("DeviceAdd"),
	ROOM_ADD("RoomAdd"),
	ZONE_ADD("ZoneAdd"),
	END_WITH("EndWith"),
	STYLE("Style"),
	END_BRIGHTNESS("EndBrightness"),
	FADE_DURATION_HOUR("FadeDurationTime"),
	FADE_DURATION_MINUTE("FadeDurationMinute"),
	DELETE("Delete"),
	APPLY_CHANGE("ApplyChange"),
	;

	/**
	 * AutomationEnum instantiation
	 *
	 * @param name {@code {@link #name}}
	 */
	AutomationEnum(String name) {
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