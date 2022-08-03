/**
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.NetworkInfoEnum;

/**
 * NetworkInfoResponse class provides during the monitoring and controlling process
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
	 * Retrieves {@code {@link #mac}}
	 *
	 * @return value of {@link #mac}
	 */
	public String getMac() {
		return mac;
	}

	/**
	 * Sets {@code mac}
	 *
	 * @param mac the {@code java.lang.String} field
	 */
	public void setMac(String mac) {
		this.mac = mac;
	}

	/**
	 * Retrieves {@code {@link #netmask}}
	 *
	 * @return value of {@link #netmask}
	 */
	public String getNetmask() {
		return netmask;
	}

	/**
	 * Sets {@code netmask}
	 *
	 * @param netmask the {@code java.lang.String} field
	 */
	public void setNetmask(String netmask) {
		this.netmask = netmask;
	}

	/**
	 * Retrieves {@code {@link #localtime}}
	 *
	 * @return value of {@link #localtime}
	 */
	public String getLocaltime() {
		return localtime;
	}

	/**
	 * Sets {@code localtime}
	 *
	 * @param localtime the {@code java.lang.String} field
	 */
	public void setLocaltime(String localtime) {
		this.localtime = localtime;
	}

	/**
	 * Retrieves {@code {@link #timezone}}
	 *
	 * @return value of {@link #timezone}
	 */
	public String getTimezone() {
		return timezone;
	}

	/**
	 * Sets {@code timezone}
	 *
	 * @param timezone the {@code java.lang.String} field
	 */
	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	/**
	 * Retrieves {@code {@link #utc}}
	 *
	 * @return value of {@link #utc}
	 */
	public String getUtc() {
		return utc;
	}

	/**
	 * Sets {@code utc}
	 *
	 * @param utc the {@code java.lang.String} field
	 */
	public void setUtc(String utc) {
		this.utc = utc;
	}

	/**
	 * Retrieves {@code {@link #address}}
	 *
	 * @return value of {@link #address}
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * Sets {@code address}
	 *
	 * @param address the {@code java.lang.String} field
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * Retrieves {@code {@link #model}}
	 *
	 * @return value of {@link #model}
	 */
	public String getModel() {
		return model;
	}

	/**
	 * Sets {@code model}
	 *
	 * @param model the {@code java.lang.String} field
	 */
	public void setModel(String model) {
		this.model = model;
	}

	/**
	 * Retrieves {@code {@link #version}}
	 *
	 * @return value of {@link #version}
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Sets {@code version}
	 *
	 * @param version the {@code java.lang.String} field
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
				return "None";
		}
	}
}