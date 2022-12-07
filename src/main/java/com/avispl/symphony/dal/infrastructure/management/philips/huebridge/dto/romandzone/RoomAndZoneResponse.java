/**
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.romandzone;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.EnumTypeHandler;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.PhilipsConstant;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.system.ServicesResponse;
import com.avispl.symphony.dal.util.StringUtils;

/**
 * RoomResponse class provides information for room and zone
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/3/2022
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoomAndZoneResponse {

	private String id;
	private String type;
	private ServicesResponse[] services;
	private Children[] children;

	@JsonAlias("metadata")
	private MetaData metaData;

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
	 * Retrieves {@link #type}
	 *
	 * @return value of {@link #type}
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets {@link #type} value
	 *
	 * @param type new value of {@link #type}
	 */
	public void setType(String type) {
		this.type = type;
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
	 * Retrieves {@link #children}
	 *
	 * @return value of {@link #children}
	 */
	public Children[] getChildren() {
		return children;
	}

	/**
	 * Sets {@link #children} value
	 *
	 * @param children new value of {@link #children}
	 */
	public void setChildren(Children[] children) {
		this.children = children;
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
	 * /**
	 * Get To String of stream configs
	 *
	 * @return String is full param of stream config
	 */
	@Override
	public String toString() {
		String metaDataValue = EnumTypeHandler.getFormatNameByColonValue(metaData.toString(), "metadata", true);
		String typeValue = EnumTypeHandler.getFormatNameByColonValue(type, "type", false);
		String childrenValue;
		String childrenItemValue = PhilipsConstant.EMPTY_STRING;
		for (Children children : children) {
			childrenItemValue = StringUtils.isNullOrEmpty(childrenItemValue) ? childrenItemValue : childrenItemValue + PhilipsConstant.COMMA;
			childrenItemValue = childrenItemValue + children.toString();
		}
		childrenItemValue = String.format("[%s]", childrenItemValue);
		childrenValue = EnumTypeHandler.getFormatNameByColonValue(childrenItemValue, "children", true);

		return String.format("{%s,%s,%s}", metaDataValue, childrenValue, typeValue);
	}
}