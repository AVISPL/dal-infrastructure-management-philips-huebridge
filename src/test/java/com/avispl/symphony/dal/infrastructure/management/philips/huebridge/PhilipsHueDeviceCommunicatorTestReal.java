/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.philips.huebridge;


import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;

class PhilipsHueDeviceCommunicatorTestReal {

	static PhilipsHueDeviceCommunicator philipsHueDeviceCommunicator;
	private static final int HTTP_PORT = 80;
	private static final int HTTPS_PORT = 443;
	private static final String HOST_NAME = "192.168.159.52";
	private static final String PROTOCOL = "https";


	@BeforeEach
	public void init() throws Exception {
		philipsHueDeviceCommunicator = new PhilipsHueDeviceCommunicator();
		philipsHueDeviceCommunicator.setTrustAllCertificates(true);
		philipsHueDeviceCommunicator.setProtocol(PROTOCOL);
		philipsHueDeviceCommunicator.setPort(443);
		philipsHueDeviceCommunicator.setHost(HOST_NAME);
		philipsHueDeviceCommunicator.setContentType("application/json");
		philipsHueDeviceCommunicator.setPassword("QKRlUW-Zty5sfoagCf6BOqi9RXdS0qWcJDESnaiM");
		philipsHueDeviceCommunicator.init();
		philipsHueDeviceCommunicator.authenticate();
	}

	@AfterEach
	void stopWireMockRule() {
		philipsHueDeviceCommunicator.destroy();
	}

	/**
	 * Test getMultipleStatistics get all current system
	 * Expect getMultipleStatistics successfully with three systems
	 */
	@Tag("Mock")
	@Test
	void testGetMultipleStatistics() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "CreateAutomationBehaviorInstance#Name";
		String value = "New room 02";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		property = "CreateAutomationBehaviorInstance#Repeat";
		value = "1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		property = "CreateAutomationBehaviorInstance#Repeat0";
		value = "Monday";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		property = "CreateAutomationBehaviorInstance#Type";
		value = "Device";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		property = "CreateAutomationBehaviorInstance#Device0";
		value = "Light 2-TMA room3";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		property = "CreateAutomationBehaviorInstance#Action";
		value = "1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);

		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
	}
}