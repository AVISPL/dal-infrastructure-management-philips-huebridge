/**
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.aggregateddevice;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.PhilipsConstant;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.SystemInfoEnum;

/**
 * ProductData class provides basic information for the aggregated device
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/2/2022
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductData {

	@JsonAlias("manufacturer_name")
	private String manufacturer;

	@JsonAlias("model_id")
	private String model;

	@JsonAlias("product_archetype")
	private String archetype;

	@JsonAlias("software_version")
	private String version;

	@JsonAlias("product_name")
	private String name;

	/**
	 * Retrieves {@link #manufacturer}
	 *
	 * @return value of {@link #manufacturer}
	 */
	public String getManufacturer() {
		return manufacturer;
	}

	/**
	 * Sets {@link #manufacturer} value
	 *
	 * @param manufacturer new value of {@link #manufacturer}
	 */
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
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
	 * Retrieves {@link #archetype}
	 *
	 * @return value of {@link #archetype}
	 */
	public String getArchetype() {
		return archetype;
	}

	/**
	 * Sets {@link #archetype} value
	 *
	 * @param archetype new value of {@link #archetype}
	 */
	public void setArchetype(String archetype) {
		this.archetype = archetype;
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
	 * Retrieves {@link #name}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets {@link #name} value
	 *
	 * @param name new value of {@link #name}
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the value by the metric monitoring
	 *
	 * @param systemInfoEnum the systemInfoEnum is metric monitoring
	 * @return String value of systemInfoEnum monitoring properties by metric
	 */
	public String getValueByMetric(SystemInfoEnum systemInfoEnum) {
		switch (systemInfoEnum) {
			case NAME:
				return getName();
			case VERSION:
				return getVersion();
			case MODEL:
				return getModel();
			case MANUFACTURER:
				return getManufacturer();
			case ARCHETYPE:
				return getArchetype();
			case TYPE:
			case ID:
			default:
				return PhilipsConstant.NONE;
		}
	}
}