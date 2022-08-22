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
 * RoomResponse class provides during the monitoring and controlling process
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
	 * Retrieves {@code {@link #services}}
	 *
	 * @return value of {@link #services}
	 */
	public ServicesResponse[] getServices() {
		return services;
	}

	/**
	 * Sets {@code services}
	 *
	 * @param services the {@code com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.system.ServicesResponse[]} field
	 */
	public void setServices(ServicesResponse[] services) {
		this.services = services;
	}

	/**
	 * Retrieves {@code {@link #metaData}}
	 *
	 * @return value of {@link #metaData}
	 */
	public MetaData getMetaData() {
		return metaData;
	}

	/**
	 * Sets {@code metaData}
	 *
	 * @param metaData the {@code com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.romandzone.MetaData} field
	 */
	public void setMetaData(MetaData metaData) {
		this.metaData = metaData;
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
	 * Retrieves {@code {@link #children}}
	 *
	 * @return value of {@link #children}
	 */
	public Children[] getChildren() {
		return children;
	}

	/**
	 * Sets {@code children}
	 *
	 * @param children the {@code com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.romandzone.Children[]} field
	 */
	public void setChildren(Children[] children) {
		this.children = children;
	}

	/**
	 * /**
	 * Get To String of stream configs
	 *
	 * @return String is full param of stream config
	 */
	@Override
	public String toString() {
		String metaDataValue = EnumTypeHandler.getFormatNameByColonValue(metaData.toString(), "metadata",true);
		String typeValue = EnumTypeHandler.getFormatNameByColonValue(type, "type", false);
		String childrenValue;
		String childrenItemValue = PhilipsConstant.EMPTY_STRING;
		for (Children children : children) {
			childrenItemValue = StringUtils.isNullOrEmpty(childrenItemValue) ? childrenItemValue : childrenItemValue + PhilipsConstant.COMMA;
			childrenItemValue = childrenItemValue + children.toString();
		}
		childrenItemValue = String.format("[%s]", childrenItemValue);
		childrenValue = EnumTypeHandler.getFormatNameByColonValue(childrenItemValue, "children",true);

		return String.format("{%s,%s,%s}", metaDataValue, childrenValue, typeValue);
	}
}