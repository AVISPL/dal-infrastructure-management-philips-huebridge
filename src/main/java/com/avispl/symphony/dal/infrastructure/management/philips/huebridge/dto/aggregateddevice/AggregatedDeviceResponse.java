/**
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.aggregateddevice;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.romandzone.MetaData;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.system.ServicesResponse;

/**
 * AggregatedDeviceResponse class provides information of the aggregated device
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/2/2022
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AggregatedDeviceResponse {

	private String id;
	private ServicesResponse[] services;

	@JsonAlias("metadata")
	private MetaData metaData;

	@JsonAlias("product_data")
	private ProductData productData;

	/**
	 * Retrieves {@link #id}
	 *
	 * @return value of {@link #id}
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets {@link #id} value
	 *
	 * @param id new value of {@link #id}
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Retrieves {@link #services}
	 *
	 * @return value of {@link #services}
	 */
	public ServicesResponse[] getServices() {
		return services;
	}

	/**
	 * Sets {@link #services} value
	 *
	 * @param services new value of {@link #services}
	 */
	public void setServices(ServicesResponse[] services) {
		this.services = services;
	}

	/**
	 * Retrieves {@link #metaData}
	 *
	 * @return value of {@link #metaData}
	 */
	public MetaData getMetaData() {
		return metaData;
	}

	/**
	 * Sets {@link #metaData} value
	 *
	 * @param metaData new value of {@link #metaData}
	 */
	public void setMetaData(MetaData metaData) {
		this.metaData = metaData;
	}

	/**
	 * Retrieves {@link #productData}
	 *
	 * @return value of {@link #productData}
	 */
	public ProductData getProductData() {
		return productData;
	}

	/**
	 * Sets {@link #productData} value
	 *
	 * @param productData new value of {@link #productData}
	 */
	public void setProductData(ProductData productData) {
		this.productData = productData;
	}
}