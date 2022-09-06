/**
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common;

/**
 * NetworkInfoEnum class provides property name for NetworkInformation
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/2/2022
 * @since 1.0.0
 */
public enum NetworkInfoEnum {

	MAC_ADDRESS("MACAddress"),
	NETMASK("SubNetmask"),
	LOCATION_TIME("LocationTime"),
	TIMEZONE("Timezone"),
	UTC("UTC"),
	ADDRESS("IPAddress");

	/**
	 * NetworkInfoEnum instantiation
	 *
	 * @param name {@code {@link #name}}
	 */
	NetworkInfoEnum(String name) {
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