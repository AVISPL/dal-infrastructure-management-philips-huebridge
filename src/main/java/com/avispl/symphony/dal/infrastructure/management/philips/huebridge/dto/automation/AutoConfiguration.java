/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.automation;

import com.fasterxml.jackson.annotation.JsonAlias;

import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.EnumTypeHandler;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.PhilipsConstant;
import com.avispl.symphony.dal.util.StringUtils;

/**
 * AutoConfiguration class provides configuration information such as location time type, ... etc
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/24/2022
 * @since 1.0.0
 */
public class AutoConfiguration {

	private String style;
	@JsonAlias("where")
	private Location[] location;

	@JsonAlias("when")
	private TimeAndRepeat timeAndRepeats;

	@JsonAlias("fade_in_duration")
	private FadeDuration fadeInDuration;

	@JsonAlias("fade_out_duration")
	private FadeDuration fadeOutDuration;

	@JsonAlias("duration")
	private FadeDuration duration;

	@JsonAlias("end_brightness")
	private String endBrightness;

	@JsonAlias("end_state")
	private String endWith;

	/**
	 * Retrieves {@link #style}
	 *
	 * @return value of {@link #style}
	 */
	public String getStyle() {
		return style;
	}

	/**
	 * Sets {@link #style} value
	 *
	 * @param style new value of {@link #style}
	 */
	public void setStyle(String style) {
		this.style = style;
	}

	/**
	 * Retrieves {@link #location}
	 *
	 * @return value of {@link #location}
	 */
	public Location[] getLocation() {
		return location;
	}

	/**
	 * Sets {@link #location} value
	 *
	 * @param location new value of {@link #location}
	 */
	public void setLocation(Location[] location) {
		this.location = location;
	}

	/**
	 * Retrieves {@link #timeAndRepeats}
	 *
	 * @return value of {@link #timeAndRepeats}
	 */
	public TimeAndRepeat getTimeAndRepeats() {
		return timeAndRepeats;
	}

	/**
	 * Sets {@link #timeAndRepeats} value
	 *
	 * @param timeAndRepeats new value of {@link #timeAndRepeats}
	 */
	public void setTimeAndRepeats(TimeAndRepeat timeAndRepeats) {
		this.timeAndRepeats = timeAndRepeats;
	}

	/**
	 * Retrieves {@link #fadeInDuration}
	 *
	 * @return value of {@link #fadeInDuration}
	 */
	public FadeDuration getFadeInDuration() {
		return fadeInDuration;
	}

	/**
	 * Sets {@link #fadeInDuration} value
	 *
	 * @param fadeInDuration new value of {@link #fadeInDuration}
	 */
	public void setFadeInDuration(FadeDuration fadeInDuration) {
		this.fadeInDuration = fadeInDuration;
	}

	/**
	 * Retrieves {@link #fadeOutDuration}
	 *
	 * @return value of {@link #fadeOutDuration}
	 */
	public FadeDuration getFadeOutDuration() {
		return fadeOutDuration;
	}

	/**
	 * Sets {@link #fadeOutDuration} value
	 *
	 * @param fadeOutDuration new value of {@link #fadeOutDuration}
	 */
	public void setFadeOutDuration(FadeDuration fadeOutDuration) {
		this.fadeOutDuration = fadeOutDuration;
	}

	/**
	 * Retrieves {@link #duration}
	 *
	 * @return value of {@link #duration}
	 */
	public FadeDuration getDuration() {
		return duration;
	}

	/**
	 * Sets {@link #duration} value
	 *
	 * @param duration new value of {@link #duration}
	 */
	public void setDuration(FadeDuration duration) {
		this.duration = duration;
	}

	/**
	 * Retrieves {@link #endBrightness}
	 *
	 * @return value of {@link #endBrightness}
	 */
	public String getEndBrightness() {
		return endBrightness;
	}

	/**
	 * Sets {@link #endBrightness} value
	 *
	 * @param endBrightness new value of {@link #endBrightness}
	 */
	public void setEndBrightness(String endBrightness) {
		this.endBrightness = endBrightness;
	}

	/**
	 * Retrieves {@link #endWith}
	 *
	 * @return value of {@link #endWith}
	 */
	public String getEndWith() {
		return endWith;
	}

	/**
	 * Sets {@link #endWith} value
	 *
	 * @param endWith new value of {@link #endWith}
	 */
	public void setEndWith(String endWith) {
		this.endWith = endWith;
	}

	@Override
	public String toString() {
		String durationValue = PhilipsConstant.EMPTY_STRING;
		String endBrightnessValue = PhilipsConstant.EMPTY_STRING;
		String endState = PhilipsConstant.EMPTY_STRING;
		String styleValue = PhilipsConstant.EMPTY_STRING;
		String whatTime;
		if (duration != null && !StringUtils.isNullOrEmpty(duration.getSeconds())) {
			durationValue = EnumTypeHandler.getFormatNameByColonValue(duration.toString(), "duration", true);
			StringBuilder deviceGroup = new StringBuilder();
			int locationIndex = 0;
			for (Location locationItem : location) {
				String values = EnumTypeHandler.getFormatNameByColonValue(locationItem.getGroup().toString(), "group", true);
				if (location.length - 1 != locationIndex) {
					whatTime = String.format("{\"blink\":{},%s},", values);
				} else {
					whatTime = String.format("{\"blink\":{},%s}", values);
				}
				deviceGroup.append(whatTime);
				locationIndex++;
			}
			whatTime = EnumTypeHandler.getFormatNameByColonValue(String.format("[%s]", deviceGroup), "what", true);
		} else {
			whatTime = EnumTypeHandler.getFormatNameByColonValue(String.format("%s}", timeAndRepeats.toString()), "when", true);
		}
		if (fadeInDuration != null && !StringUtils.isNullOrEmpty(fadeInDuration.getSeconds())) {
			durationValue = EnumTypeHandler.getFormatNameByColonValue(fadeInDuration.toString(), "fade_in_duration", true);
			endBrightnessValue = EnumTypeHandler.getFormatNameByColonValue(endBrightness, "end_brightness", true);
			styleValue = EnumTypeHandler.getFormatNameByColonValue(style, "style", false);
		}
		if (fadeOutDuration != null && !StringUtils.isNullOrEmpty(fadeOutDuration.getSeconds())) {
			durationValue = EnumTypeHandler.getFormatNameByColonValue(fadeOutDuration.toString(), "fade_out_duration", true);
			endState = String.format(",%s", EnumTypeHandler.getFormatNameByColonValue(endWith, "end_state", false));
		}
		StringBuilder stringBuilder = new StringBuilder();
		int locationIndex = 0;
		for (Location locationItem : location) {
			String values = locationItem.toString();
			if (location.length - 1 != locationIndex) {
				values = String.format("%s,", values);
			}
			stringBuilder.append(values);
			locationIndex++;
		}
		String locationValue = EnumTypeHandler.getFormatNameByColonValue(String.format("[%s],", stringBuilder), "where", true);
		durationValue = StringUtils.isNullOrEmpty(durationValue) ? durationValue : String.format("%s,", durationValue);
		endBrightnessValue = StringUtils.isNullOrEmpty(endBrightnessValue) ? endBrightnessValue : String.format(",%s", endBrightnessValue);
		styleValue = StringUtils.isNullOrEmpty(styleValue) ? styleValue : String.format(",%s", styleValue);

		return String.format("{%s %s %s %s %s %s", durationValue, locationValue, whatTime, endBrightnessValue, endState, styleValue);
	}
}