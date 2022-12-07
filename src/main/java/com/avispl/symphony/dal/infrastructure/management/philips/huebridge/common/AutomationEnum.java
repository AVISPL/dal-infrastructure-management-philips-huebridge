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
	FADE_DURATION("FadeDuration(s)"),
	NAME("Name"),
	REPEAT("Repeat"),
	REPEAT_MONDAY("RepeatMonday"),
	REPEAT_TUESDAY("RepeatTuesday"),
	REPEAT_THURSDAY("RepeatThursday"),
	REPEAT_WEDNESDAY("RepeatWednesday"),
	REPEAT_FRIDAY("RepeatFriday"),
	REPEAT_SATURDAY("RepeatSaturday"),
	REPEAT_SUNDAY("RepeatSunday"),

	REPEAT_ADD("RepeatAdd"),
	STATUS("Status"),
	TIME_CURRENT("TimeCurrent"),
	TIME_HOUR("TimeHour"),
	TIME_MINUTE("TimeMinute"),
	TYPE("Type"),
	TYPE_OF_AUTOMATION("TypeOfAutomation"),
	CANCEL("CancelChanges"),
	CREATE("Create"),
	DEVICE_ADD("DeviceAdd"),
	ROOM_ADD("RoomAdd"),
	ZONE_ADD("ZoneAdd"),
	END_WITH("EndWith"),
	STYLE("Style"),
	END_BRIGHTNESS("MaximumBrightness"),
	FADE_DURATION_HOUR("FadeDurationHour"),
	FADE_DURATION_MINUTE("FadeDurationMinute"),
	DELETE("Delete"),
	APPLY_CHANGE("ApplyChanges"),
	;

	/**
	 * AutomationEnum instantiation
	 *
	 * @param name  {@link #name}
	 */
	AutomationEnum(String name) {
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