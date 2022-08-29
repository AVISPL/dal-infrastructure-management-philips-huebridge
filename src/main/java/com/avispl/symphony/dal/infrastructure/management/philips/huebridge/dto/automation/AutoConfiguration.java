/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.automation;

import com.fasterxml.jackson.annotation.JsonAlias;

import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.EnumTypeHandler;
import com.avispl.symphony.dal.util.StringUtils;

/**
 * AutoConfiguration class provides during the monitoring and controlling process
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
	 * Retrieves {@code {@link #style}}
	 *
	 * @return value of {@link #style}
	 */
	public String getStyle() {
		return style;
	}

	/**
	 * Sets {@code style}
	 *
	 * @param style the {@code java.lang.String} field
	 */
	public void setStyle(String style) {
		this.style = style;
	}

	/**
	 * Retrieves {@code {@link #location}}
	 *
	 * @return value of {@link #location}
	 */
	public Location[] getLocation() {
		return location;
	}

	/**
	 * Sets {@code location}
	 *
	 * @param location the {@code com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.automation.Location[]} field
	 */
	public void setLocation(Location[] location) {
		this.location = location;
	}

	/**
	 * Retrieves {@code {@link #timeAndRepeats}}
	 *
	 * @return value of {@link #timeAndRepeats}
	 */
	public TimeAndRepeat getTimeAndRepeats() {
		return timeAndRepeats;
	}

	/**
	 * Sets {@code timeAndRepeats}
	 *
	 * @param timeAndRepeats the {@code com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.automation.TimeAndRepeat} field
	 */
	public void setTimeAndRepeats(TimeAndRepeat timeAndRepeats) {
		this.timeAndRepeats = timeAndRepeats;
	}

	/**
	 * Retrieves {@code {@link #fadeInDuration}}
	 *
	 * @return value of {@link #fadeInDuration}
	 */
	public FadeDuration getFadeInDuration() {
		return fadeInDuration;
	}

	/**
	 * Sets {@code fadeInDuration}
	 *
	 * @param fadeInDuration the {@code com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.automation.FadeDuration} field
	 */
	public void setFadeInDuration(FadeDuration fadeInDuration) {
		this.fadeInDuration = fadeInDuration;
	}

	/**
	 * Retrieves {@code {@link #fadeOutDuration}}
	 *
	 * @return value of {@link #fadeOutDuration}
	 */
	public FadeDuration getFadeOutDuration() {
		return fadeOutDuration;
	}

	/**
	 * Sets {@code fadeOutDuration}
	 *
	 * @param fadeOutDuration the {@code com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.automation.FadeDuration} field
	 */
	public void setFadeOutDuration(FadeDuration fadeOutDuration) {
		this.fadeOutDuration = fadeOutDuration;
	}

	/**
	 * Retrieves {@code {@link #duration}}
	 *
	 * @return value of {@link #duration}
	 */
	public FadeDuration getDuration() {
		return duration;
	}

	/**
	 * Sets {@code duration}
	 *
	 * @param duration the {@code com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.automation.FadeDuration} field
	 */
	public void setDuration(FadeDuration duration) {
		this.duration = duration;
	}

	/**
	 * Retrieves {@code {@link #endBrightness}}
	 *
	 * @return value of {@link #endBrightness}
	 */
	public String getEndBrightness() {
		return endBrightness;
	}

	/**
	 * Sets {@code endBrightness}
	 *
	 * @param endBrightness the {@code java.lang.String} field
	 */
	public void setEndBrightness(String endBrightness) {
		this.endBrightness = endBrightness;
	}

	/**
	 * Retrieves {@code {@link #endWith}}
	 *
	 * @return value of {@link #endWith}
	 */
	public String getEndWith() {
		return endWith;
	}

	/**
	 * Sets {@code endWith}
	 *
	 * @param endWith the {@code java.lang.String} field
	 */
	public void setEndWith(String endWith) {
		this.endWith = endWith;
	}

	@Override
	public String toString() {
		String durationValue = "";
		String endBrightnessValue = "";
		String endState = "";
		String styleValue = "";
		String whatTime = "";
		if (duration != null && !StringUtils.isNullOrEmpty(duration.getSeconds())) {
			durationValue = EnumTypeHandler.getFormatNameByColonValue(duration.toString(), "duration", true);
			StringBuilder deviceGroup = new StringBuilder();
			for (Location locationItem : location) {
				deviceGroup.append(String.format("%s}", EnumTypeHandler.getFormatNameByColonValue(locationItem.getGroup().toString(), "group", true)));
			}
			whatTime = EnumTypeHandler.getFormatNameByColonValue(String.format("[{\"blink\":{},%s]", deviceGroup), "what", true);
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
		for (Location locationItem : location) {
			stringBuilder.append(locationItem.toString());
		}
		String locationValue = EnumTypeHandler.getFormatNameByColonValue(String.format("[%s],", stringBuilder), "where", true);
		durationValue = StringUtils.isNullOrEmpty(durationValue) ? durationValue : String.format("%s,", durationValue);
		endBrightnessValue = StringUtils.isNullOrEmpty(endBrightnessValue) ? endBrightnessValue : String.format(",%s", endBrightnessValue);
		styleValue = StringUtils.isNullOrEmpty(styleValue) ? styleValue : String.format(",%s", styleValue);

		return String.format("{%s %s %s %s %s %s", durationValue, locationValue, whatTime, endBrightnessValue, endState, styleValue);
	}
}