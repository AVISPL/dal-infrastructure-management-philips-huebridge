/**
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.automation;

import com.fasterxml.jackson.annotation.JsonAlias;

import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.EnumTypeHandler;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.romandzone.MetaData;
import com.avispl.symphony.dal.util.StringUtils;

/**
 * AutomationResponse class provides information about automation
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/23/2022
 * @since 1.0.0
 */
public class AutomationResponse {

	private String id;
	private String enabled;
	private String status;

	@JsonAlias("script_id")
	private String scriptId;

	@JsonAlias("configuration")
	private AutoConfiguration configurations;

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
	 * Retrieves {@link #enabled}
	 *
	 * @return value of {@link #enabled}
	 */
	public String getEnabled() {
		return enabled;
	}

	/**
	 * Sets {@link #enabled} value
	 *
	 * @param enabled new value of {@link #enabled}
	 */
	public void setEnabled(String enabled) {
		this.enabled = enabled;
	}

	/**
	 * Retrieves {@link #status}
	 *
	 * @return value of {@link #status}
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Sets {@link #status} value
	 *
	 * @param status new value of {@link #status}
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * Retrieves {@link #scriptId}
	 *
	 * @return value of {@link #scriptId}
	 */
	public String getScriptId() {
		return scriptId;
	}

	/**
	 * Sets {@link #scriptId} value
	 *
	 * @param scriptId new value of {@link #scriptId}
	 */
	public void setScriptId(String scriptId) {
		this.scriptId = scriptId;
	}

	/**
	 * Retrieves {@link #configurations}
	 *
	 * @return value of {@link #configurations}
	 */
	public AutoConfiguration getConfigurations() {
		return configurations;
	}

	/**
	 * Sets {@link #configurations} value
	 *
	 * @param configurations new value of {@link #configurations}
	 */
	public void setConfigurations(AutoConfiguration configurations) {
		this.configurations = configurations;
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

	@Override
	public String toString() {
		String metaDataValue = EnumTypeHandler.getFormatNameByColonValue(metaData.toString(), "metadata", true);
		String script = StringUtils.isNullOrEmpty(scriptId) ? "" : String.format(",%s", EnumTypeHandler.getFormatNameByColonValue(scriptId, "script_id", false));
		String enabledValue = EnumTypeHandler.getFormatNameByColonValue(enabled, "enabled", false);
		enabledValue = enabledValue.replace("\"False\"", "false").replace("\"True\"", "true");
		String configurationValue = EnumTypeHandler.getFormatNameByColonValue(configurations.toString(), "configuration", true);

		return String.format("{%s}, %s, %s %s}", configurationValue, metaDataValue, enabledValue, script);
	}
}