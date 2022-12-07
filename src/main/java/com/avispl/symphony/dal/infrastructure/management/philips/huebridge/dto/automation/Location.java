/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.automation;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.EnumTypeHandler;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.PhilipsConstant;

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
	 * Retrieves {@link #group}
	 *
	 * @return value of {@link #group}
	 */
	public Group getGroup() {
		return group;
	}

	/**
	 * Sets {@link #group} value
	 *
	 * @param group new value of {@link #group}
	 */
	public void setGroup(Group group) {
		this.group = group;
	}

	/**
	 * Retrieves {@link #items}
	 *
	 * @return value of {@link #items}
	 */
	public Group[] getItems() {
		return items;
	}

	/**
	 * Sets {@link #items} value
	 *
	 * @param items new value of {@link #items}
	 */
	public void setItems(Group[] items) {
		this.items = items;
	}

	@Override
	public String toString() {
		String groupValue = EnumTypeHandler.getFormatNameByColonValue(group.toString(), "group", true);
		String itemsValue = PhilipsConstant.EMPTY_STRING;
		if (items != null) {
			StringBuilder stringBuilder = new StringBuilder();
			int itemIndex = 0;
			for (Group item : items) {
				String values = item.toString();
				if (items.length - 1 != itemIndex) {
					values = String.format("%s,", item);
				}
				stringBuilder.append(values);
				itemIndex++;
			}
			itemsValue = String.format(",%s", EnumTypeHandler.getFormatNameByColonValue(String.format("[%s]", stringBuilder), "items", true));
		}
		return String.format("{%s %s}", groupValue, itemsValue);
	}
}