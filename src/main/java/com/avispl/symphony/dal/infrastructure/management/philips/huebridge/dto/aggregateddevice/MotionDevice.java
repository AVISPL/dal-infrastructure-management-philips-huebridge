/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.aggregateddevice;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.EnumTypeHandler;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.PhilipsConstant;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.bridge.OwnerResponse;

/**
 * MotionDevice class provides information of the motion sensor device
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/26/2022
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MotionDevice {

	private String id;
	private String type;
	private OwnerResponse owner;
	private MotionDeviceDetails motion;

	@JsonAlias("enabled")
	private boolean status;

	/**
	 * Retrieves {@code {@link #id}}
	 *
	 * @return value of {@link #id}
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets {@code id}
	 *
	 * @param id the {@code java.lang.String} field
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Retrieves {@code {@link #type}}
	 *
	 * @return value of {@link #type}
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets {@code type}
	 *
	 * @param type the {@code java.lang.String} field
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Retrieves {@code {@link #owner}}
	 *
	 * @return value of {@link #owner}
	 */
	public OwnerResponse getOwner() {
		return owner;
	}

	/**
	 * Sets {@code owner}
	 *
	 * @param owner the {@code com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.bridge.OwnerResponse} field
	 */
	public void setOwner(OwnerResponse owner) {
		this.owner = owner;
	}

	/**
	 * Retrieves {@code {@link #motion}}
	 *
	 * @return value of {@link #motion}
	 */
	public MotionDeviceDetails getMotion() {
		return motion;
	}

	/**
	 * Sets {@code motion}
	 *
	 * @param motion the {@code com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.aggregateddevice.MotionDeviceDetails} field
	 */
	public void setMotion(MotionDeviceDetails motion) {
		this.motion = motion;
	}

	/**
	 * Retrieves {@code {@link #status}}
	 *
	 * @return value of {@link #status}
	 */
	public boolean isStatus() {
		return status;
	}

	/**
	 * Sets {@code status}
	 *
	 * @param status the {@code boolean} field
	 */
	public void setStatus(boolean status) {
		this.status = status;
	}

	@Override
	public String toString() {
		String value = status ? PhilipsConstant.TRUE.toLowerCase(Locale.ROOT) : PhilipsConstant.FALSE.toLowerCase(Locale.ROOT);
		return String.format("{%s}", EnumTypeHandler.getFormatNameByColonValue(value, "enabled", true));

	}
}