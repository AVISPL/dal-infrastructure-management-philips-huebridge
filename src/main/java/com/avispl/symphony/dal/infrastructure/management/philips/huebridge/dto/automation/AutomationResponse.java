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
	 * Retrieves {@code {@link #enabled}}
	 *
	 * @return value of {@link #enabled}
	 */
	public String getEnabled() {
		return enabled;
	}

	/**
	 * Sets {@code enabled}
	 *
	 * @param enabled the {@code java.lang.String} field
	 */
	public void setEnabled(String enabled) {
		this.enabled = enabled;
	}

	/**
	 * Retrieves {@code {@link #status}}
	 *
	 * @return value of {@link #status}
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Sets {@code status}
	 *
	 * @param status the {@code java.lang.String} field
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * Retrieves {@code {@link #scriptId }}
	 *
	 * @return value of {@link #scriptId}
	 */
	public String getScriptId() {
		return scriptId;
	}

	/**
	 * Sets {@code script_id}
	 *
	 * @param scriptId the {@code java.lang.String} field
	 */
	public void setScriptId(String scriptId) {
		this.scriptId = scriptId;
	}

	/**
	 * Retrieves {@code {@link #configurations}}
	 *
	 * @return value of {@link #configurations}
	 */
	public AutoConfiguration getConfigurations() {
		return configurations;
	}

	/**
	 * Sets {@code configurations}
	 *
	 * @param configurations the {@code com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.automation.AutoConfiguration} field
	 */
	public void setConfigurations(AutoConfiguration configurations) {
		this.configurations = configurations;
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