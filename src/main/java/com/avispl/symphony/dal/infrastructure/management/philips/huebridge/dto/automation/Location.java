/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.automation;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.EnumTypeHandler;

/**
 * Location class provides during the monitoring and controlling process
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/23/2022
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Location {

	@JsonAlias("group")
	private Group group;

	@JsonAlias("items")
	private Group[] items;

	/**
	 * Retrieves {@code {@link #group}}
	 *
	 * @return value of {@link #group}
	 */
	public Group getGroup() {
		return group;
	}

	/**
	 * Sets {@code group}
	 *
	 * @param group the {@code com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.automation.Group} field
	 */
	public void setGroup(Group group) {
		this.group = group;
	}

	/**
	 * Retrieves {@code {@link #items}}
	 *
	 * @return value of {@link #items}
	 */
	public Group[] getItems() {
		return items;
	}

	/**
	 * Sets {@code items}
	 *
	 * @param items the {@code com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.automation.Group[]} field
	 */
	public void setItems(Group[] items) {
		this.items = items;
	}

	@Override
	public String toString() {
		String groupValue = EnumTypeHandler.getFormatNameByColonValue(group.toString(), "group", true);
		String itemsValue = EnumTypeHandler.getFormatNameByColonValue(items.toString(), "time_point", true);
		return String.format("{%s,%s}", groupValue, itemsValue);
	}
}