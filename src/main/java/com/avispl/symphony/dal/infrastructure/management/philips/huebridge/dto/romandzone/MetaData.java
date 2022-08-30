/**
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.romandzone;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.EnumTypeHandler;
import com.avispl.symphony.dal.util.StringUtils;

/**
 * MetaData class provides metadata as name and type for room and zone
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/2/2022
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MetaData {

	private String name;
	private String archetype;

	/**
	 * Retrieves {@code {@link #name}}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets {@code name}
	 *
	 * @param name the {@code java.lang.String} field
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Retrieves {@code {@link #archetype}}
	 *
	 * @return value of {@link #archetype}
	 */
	public String getArchetype() {
		return archetype;
	}

	/**
	 * Sets {@code archetype}
	 *
	 * @param archetype the {@code java.lang.String} field
	 */
	public void setArchetype(String archetype) {
		this.archetype = archetype;
	}

	/**
	 * Get To String of Children
	 *
	 * @return String is full param of Children
	 */
	@Override
	public String toString() {
		String archetypeDataValue = EnumTypeHandler.getFormatNameByColonValue(archetype, "archetype", false);
		String nameValue = EnumTypeHandler.getFormatNameByColonValue(name, "name", false);
		if (StringUtils.isNullOrEmpty(archetypeDataValue)) {
			return String.format("{%s}", nameValue);
		}
		return String.format("{%s,%s}", nameValue, archetypeDataValue);
	}
}