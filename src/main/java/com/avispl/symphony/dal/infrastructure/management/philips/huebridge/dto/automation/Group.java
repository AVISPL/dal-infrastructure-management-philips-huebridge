/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.automation;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.EnumTypeHandler;

/**
 * Group class provides group id and type for automation
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/23/2022
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Group {

	@JsonAlias("rid")
	private String id;

	@JsonAlias("rtype")
	private String type;

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

	@Override
	public String toString() {
		String rid = EnumTypeHandler.getFormatNameByColonValue(id, "rid", false);
		String rType = EnumTypeHandler.getFormatNameByColonValue(type, "rtype", false);

		return String.format("{%s,%s}", rid, rType);
	}
}