/**
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.romandzone;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.EnumTypeHandler;

/**
 * Children class provides during the monitoring and controlling process
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/3/2022
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Children {

	private String rid;

	@JsonAlias("rtype")
	private String type;

	/**
	 * Retrieves {@code {@link #rid}}
	 *
	 * @return value of {@link #rid}
	 */
	public String getRid() {
		return rid;
	}

	/**
	 * Sets {@code rid}
	 *
	 * @param rid the {@code java.lang.String} field
	 */
	public void setRid(String rid) {
		this.rid = rid;
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
	 * Get To String of Children
	 *
	 * @return String is full param of Children
	 */
	@Override
	public String toString() {
		String ridValue = EnumTypeHandler.getFormatNameByColonValue(rid, "rid",false);
		String typeValue = EnumTypeHandler.getFormatNameByColonValue(type, "rtype",false);
		return String.format("{%s,%s}", ridValue,  typeValue);
	}
}