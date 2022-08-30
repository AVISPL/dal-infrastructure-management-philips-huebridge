/**
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.zigbeeconnectivity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * ZigbeeConnectivity class provides info of MAC address and Status for the device
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/2/2022
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ZigbeeConnectivity {

	@JsonAlias("mac_address")
	private String macAddress;

	private String status;

	/**
	 * Retrieves {@code {@link #macAddress}}
	 *
	 * @return value of {@link #macAddress}
	 */
	public String getMacAddress() {
		return macAddress;
	}

	/**
	 * Sets {@code macAddress}
	 *
	 * @param macAddress the {@code java.lang.String} field
	 */
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	/**
	 * Retrieves {@code {@link #status}}
	 *
	 * @return value of {@link #status}
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Sets {@code status}
	 *
	 * @param status the {@code java.lang.String} field
	 */
	public void setStatus(String status) {
		this.status = status;
	}
}