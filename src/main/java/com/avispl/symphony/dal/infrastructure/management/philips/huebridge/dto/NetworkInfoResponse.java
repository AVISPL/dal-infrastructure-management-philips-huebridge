/**
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.NetworkInfoEnum;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.PhilipsConstant;

/**
 * NetworkInfoResponse class provides information of Network
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/2/2022
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class NetworkInfoResponse {

	private String mac;
	private String netmask;
	private String localtime;
	private String timezone;

	@JsonAlias("UTC")
	private String utc;

	@JsonAlias("ipaddress")
	private String address;

	@JsonAlias("modelid")
	private String model;

	@JsonAlias("swversion")
	private String version;

	/**
	 * Retrieves {@link #mac}
	 *
	 * @return value of {@link #mac}
	 */
	public String getMac() {
		return mac;
	}

	/**
	 * Sets {@link #mac} value
	 *
	 * @param mac new value of {@link #mac}
	 */
	public void setMac(String mac) {
		this.mac = mac;
	}

	/**
	 * Retrieves {@link #netmask}
	 *
	 * @return value of {@link #netmask}
	 */
	public String getNetmask() {
		return netmask;
	}

	/**
	 * Sets {@link #netmask} value
	 *
	 * @param netmask new value of {@link #netmask}
	 */
	public void setNetmask(String netmask) {
		this.netmask = netmask;
	}

	/**
	 * Retrieves {@link #localtime}
	 *
	 * @return value of {@link #localtime}
	 */
	public String getLocaltime() {
		return localtime;
	}

	/**
	 * Sets {@link #localtime} value
	 *
	 * @param localtime new value of {@link #localtime}
	 */
	public void setLocaltime(String localtime) {
		this.localtime = localtime;
	}

	/**
	 * Retrieves {@link #timezone}
	 *
	 * @return value of {@link #timezone}
	 */
	public String getTimezone() {
		return timezone;
	}

	/**
	 * Sets {@link #timezone} value
	 *
	 * @param timezone new value of {@link #timezone}
	 */
	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	/**
	 * Retrieves {@link #utc}
	 *
	 * @return value of {@link #utc}
	 */
	public String getUtc() {
		return utc;
	}

	/**
	 * Sets {@link #utc} value
	 *
	 * @param utc new value of {@link #utc}
	 */
	public void setUtc(String utc) {
		this.utc = utc;
	}

	/**
	 * Retrieves {@link #address}
	 *
	 * @return value of {@link #address}
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * Sets {@link #address} value
	 *
	 * @param address new value of {@link #address}
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * Retrieves {@link #model}
	 *
	 * @return value of {@link #model}
	 */
	public String getModel() {
		return model;
	}

	/**
	 * Sets {@link #model} value
	 *
	 * @param model new value of {@link #model}
	 */
	public void setModel(String model) {
		this.model = model;
	}

	/**
	 * Retrieves {@link #version}
	 *
	 * @return value of {@link #version}
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Sets {@link #version} value
	 *
	 * @param version new value of {@link #version}
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * Get the value by the metric monitoring
	 *
	 * @param networkInfoEnum the networkInfoEnum is metric monitoring
	 * @return String value of networkInfoEnum monitoring properties by metric
	 */
	public String getValueByMetric(NetworkInfoEnum networkInfoEnum) {
		switch (networkInfoEnum) {
			case TIMEZONE:
				return getTimezone();
			case ADDRESS:
				return getAddress();
			case LOCATION_TIME:
				return getLocaltime();
			case MAC_ADDRESS:
				return getMac();
			case NETMASK:
				return getNetmask();
			case UTC:
				return getUtc();
			default:
				return PhilipsConstant.NONE;
		}
	}
}