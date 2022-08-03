/**
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common;

/**
 * NetworkInfoEnum define an Enum for monitoring process
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/2/2022
 * @since 1.0.0
 */
public enum NetworkInfoEnum {

	MAC_ADDRESS("MacAddress"),
	NETMASK("Netmask"),
	LOCATION_TIME("LocationTime"),
	TIMEZONE("Timezone"),
	UTC("UTC"),
	ADDRESS("IpAddress");

	NetworkInfoEnum(String name) {
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