/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.philips.huebridge;


import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.api.dal.dto.monitor.aggregator.AggregatedDevice;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.RepeatDayEnum;

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
		philipsHueDeviceCommunicator.setConfigManagement("true");
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

	@Tag("RealDevice")
	@Test
	void testCreateAutoWithTypeTimerDevice() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "CreateAutomationBehaviorInstance#Name";
		String value = "New Automation Timer Device";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		property = "CreateAutomationBehaviorInstance#Type";
		value = "Device";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);

		property = "CreateAutomationBehaviorInstance#TypeOfAutomation";
		value = "Timer";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		property = "CreateAutomationBehaviorInstance#FadeDurationMinute";
		value = "01";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		property = "CreateAutomationBehaviorInstance#Device0";
		value = "Hue ambiance lamp 2-Kids bedroom1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();

		property = "CreateAutomationBehaviorInstance#Action";
		value = "1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
	}

	@Tag("RealDevice")
	@Test
	void testCreateAutoWithTypeTimerRoom() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "CreateAutomationBehaviorInstance#Name";
		String value = "New Automation Timer Room";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		property = "CreateAutomationBehaviorInstance#Type";
		value = "Room";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);

		property = "CreateAutomationBehaviorInstance#TypeOfAutomation";
		value = "Timer";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		property = "CreateAutomationBehaviorInstance#FadeDurationMinute";
		value = "01";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		property = "CreateAutomationBehaviorInstance#Room0";
		value = "AllDeviceInRoom-Kids bedroom1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();

		property = "CreateAutomationBehaviorInstance#Action";
		value = "1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
	}

	@Tag("RealDevice")
	@Test
	void testCreateAutoWithTypeTimerZone() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "CreateAutomationBehaviorInstance#Name";
		String value = "AllDeviceInRoom-Downstairs";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		property = "CreateAutomationBehaviorInstance#Type";
		value = "Zone";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);

		property = "CreateAutomationBehaviorInstance#TypeOfAutomation";
		value = "Timer";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		property = "CreateAutomationBehaviorInstance#FadeDurationMinute";
		value = "01";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		property = "CreateAutomationBehaviorInstance#Zone0";
		value = "AllDeviceInRoom-Downstairs";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();

		property = "CreateAutomationBehaviorInstance#Action";
		value = "1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
	}


	@Tag("RealDevice")
	@Test
	void testCreateAutoWithTypeGoToSleepDevice() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "CreateAutomationBehaviorInstance#Name";
		String value = "New Automation GoToSleep Device1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		property = "CreateAutomationBehaviorInstance#Type";
		value = "Room";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
//
//		property = "CreateAutomationBehaviorInstance#TypeOfAutomation";
//		value = "GoToSleep";
//		controllableProperty.setProperty(property);
//		controllableProperty.setValue(value);
//		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
//		property = "CreateAutomationBehaviorInstance#Repeat";
//		value = "1";
//		controllableProperty.setProperty(property);
//		controllableProperty.setValue(value);
//		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
//
//		property = "CreateAutomationBehaviorInstance#Repeat0";
//		value = RepeatDayEnum.MONDAY.getName();
//		controllableProperty.setProperty(property);
//		controllableProperty.setValue(value);
//
//		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
//		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
//		stats = extendedStatistics.getStatistics();
//		property = "CreateAutomationBehaviorInstance#Device0";
//		value = "Light 1";
//		controllableProperty.setProperty(property);
//		controllableProperty.setValue(value);
//		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
//
//		stats = extendedStatistics.getStatistics();
//		property = "CreateAutomationBehaviorInstance#DeviceAdd";
//		value = "Hue ambiance lamp 2-Kids bedroom HCM";
//		controllableProperty.setProperty(property);
//		controllableProperty.setValue(value);
//		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
//
//		property = "CreateAutomationBehaviorInstance#Action";
//		value = "1";
//		controllableProperty.setProperty(property);
//		controllableProperty.setValue(value);
//		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
	}

	@Tag("RealDevice")
	@Test
	void testCreateAutoWithTypeGoToSleepDevices() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		 extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		List<AggregatedDevice> aggregatedDeviceList=philipsHueDeviceCommunicator.retrieveMultipleStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "Automation-auto#Name";
		String value = "auto21";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		property = "Automation-auto#ApplyChange";
		value = "1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
	}
	@Tag("RealDevice")
	@Test
	void testCreateAutoWithTypeGoToSleepRoom() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		 extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Thread.sleep(3000);
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "Automation-New Automation GoToSleep Zone#Status";
		String value ="1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		property = "Zone-TMA12#ApplyChange";
		value = "1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		property = "Zone-TMA12#Device0";
		value = "Light 1-Kids bedroom HCM";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		property = "Zone-TMA12#ApplyChange";
		value = "1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
//		property = "CreateAutomationBehaviorInstance#Type";
//		value = "Room";
//		controllableProperty.setProperty(property);
//		controllableProperty.setValue(value);
//		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
//
//		property = "CreateAutomationBehaviorInstance#TypeOfAutomation";
//		value = "GoToSleep";
//		controllableProperty.setProperty(property);
//		controllableProperty.setValue(value);
//		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
//		property = "CreateAutomationBehaviorInstance#Repeat";
//		value = "1";
//		controllableProperty.setProperty(property);
//		controllableProperty.setValue(value);
//		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
//
//		property = "CreateAutomationBehaviorInstance#Repeat0";
//		value = RepeatDayEnum.MONDAY.getName();
//		controllableProperty.setProperty(property);
//		controllableProperty.setValue(value);
//
//		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
//		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
//		stats = extendedStatistics.getStatistics();
//		property = "CreateAutomationBehaviorInstance#Room0";
//		value = "AllDeviceInRoom-Kids bedroom1";
//		controllableProperty.setProperty(property);
//		controllableProperty.setValue(value);
//		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
//
//		property = "CreateAutomationBehaviorInstance#Action";
//		value = "1";
//		controllableProperty.setProperty(property);
//		controllableProperty.setValue(value);
//		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
	}

	@Tag("RealDevice")
	@Test
	void testCreateAutoWithTypeGoToSleepZone() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Thread.sleep(10000);
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "AutomationWakeUpWithLight-WU 2 light#TimeCurrent";
		String value = "1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		property = "AutomationWakeUpWithLight-WU 2 light#ApplyChange";
		value = "1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);

		property = "CreateAutomationBehaviorInstance#TypeOfAutomation";
		value = "GoToSleep";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		property = "CreateAutomationBehaviorInstance#Repeat";
		value = "1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);

		property = "CreateAutomationBehaviorInstance#Repeat0";
		value = RepeatDayEnum.MONDAY.getName();
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);

		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		property = "CreateAutomationBehaviorInstance#Zone0";
		value = "AllDeviceInRoom-Downstairs";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);

		property = "CreateAutomationBehaviorInstance#Action";
		value = "1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
	}


	@Tag("RealDevice")
	@Test
	void testCreateAutoWithTypeWakeUpWithLightDevice() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "CreateAutomationBehaviorInstance#Name";
		String value = "Automation WakeUp device";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		property = "CreateAutomationBehaviorInstance#Type";
		value = "Device";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);

		property = "CreateAutomationBehaviorInstance#TypeOfAutomation";
		value = "WakeUpWithLight";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		property = "CreateAutomationBehaviorInstance#Repeat";
		value = "1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);

		property = "CreateAutomationBehaviorInstance#Repeat0";
		value = RepeatDayEnum.MONDAY.getName();
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);

		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		property = "CreateAutomationBehaviorInstance#Device0";
		value = "Light 1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);

		property = "CreateAutomationBehaviorInstance#Action";
		value = "1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
	}


	@Tag("RealDevice")
	@Test
	void testCreateAutoWithTypeWakeUpWithLightRoom() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "CreateAutomationBehaviorInstance#Name";
		String value = "Automation WakeUp room";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		property = "CreateAutomationBehaviorInstance#Type";
		value = "Room";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);

		property = "CreateAutomationBehaviorInstance#TypeOfAutomation";
		value = "WakeUpWithLight";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		property = "CreateAutomationBehaviorInstance#Repeat";
		value = "1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);

		property = "CreateAutomationBehaviorInstance#Repeat0";
		value = RepeatDayEnum.MONDAY.getName();
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);

		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		property = "CreateAutomationBehaviorInstance#Room0";
		value = "AllDeviceInRoom-Kids bedroom1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);

		property = "CreateAutomationBehaviorInstance#Action";
		value = "1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
	}


	@Tag("RealDevice")
	@Test
	void testCreateAutoWithTypeWakeUpWithLightZone() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "CreateAutomationBehaviorInstance#Name";
		String value = "Automation WakeUp zone";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		property = "CreateAutomationBehaviorInstance#Type";
		value = "Zone";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);

		property = "CreateAutomationBehaviorInstance#TypeOfAutomation";
		value = "WakeUpWithLight";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		property = "CreateAutomationBehaviorInstance#Repeat";
		value = "1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);

		property = "CreateAutomationBehaviorInstance#Repeat0";
		value = RepeatDayEnum.MONDAY.getName();
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);

		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		property = "CreateAutomationBehaviorInstance#Zone0";
		value = "AllDeviceInRoom-Downstairs";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);

		property = "CreateAutomationBehaviorInstance#Action";
		value = "1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
	}

	@Tag("RealDevice")
	@Test
	void testCreateDelete() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "Automation-Go to sleep#DeviceAdd";
		String value = "1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
	}

	//Edit AutoCreate****************************************************************************

	@Tag("RealDevice")
	@Test
	void testCreateAutomationWithName() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "Automation-New Automation WakeUpWithLight#Name";
		String value = "ABC";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals(value, stats.get(property));
	}

	@Tag("RealDevice")
	@Test
	void testCreateAutomationWithFadeDurationMin() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "Automation-New Automation WakeUpWithLight#FadeDuration";
		String value = "9";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("10", stats.get(property));
	}

	@Tag("RealDevice")
	@Test
	void testCreateAutomationWithFadeDurationMax() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "Automation-New Automation WakeUpWithLight#FadeDuration";
		String value = "5401";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("5400", stats.get(property));
	}

	@Tag("RealDevice")
	@Test
	void testCreateAutomationWithRepeat() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "Automation-New Automation WakeUpWithLight#Repeat";
		String value = "0";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("0", stats.get(property));
	}


	@Tag("RealDevice")
	@Test
	void testCreateAutomationWithRepeatAdd() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "Automation-New Automation WakeUpWithLight#Repeat";
		String value = "1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		String repeat1 = "Automation-New Automation WakeUpWithLight#Repeat1";;
		Assert.assertNull( stats.get(repeat1));
		property = "Automation-New Automation WakeUpWithLight#RepeatAdd";
		value = "1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		 repeat1 = "Automation-New Automation WakeUpWithLight#Repeat1";;
		Assert.assertNotNull( stats.get(repeat1));
	}


	@Tag("RealDevice")
	@Test
	void testCreateAutomationWithStyle() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "Automation-New Automation WakeUpWithLight#Style";
		String value = "Sunrise";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals(value, stats.get(property));
	}


	@Tag("RealDevice")
	@Test
	void testCreateAutomationWithTimeCurrent() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "Automation-New Automation WakeUpWithLight#TimeCurrent";
		String value = "1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals(value, stats.get(property));
	}
	@Tag("RealDevice")
	@Test
	void testCreateAutomationWithType() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "Automation-New Automation WakeUpWithLight#Type";
		String value = "Room";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals(value, stats.get(property));
	}

	@Tag("RealDevice")
	@Test
	void testCreateAutomationWithDeviceAdd() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "Automation-New Automation WakeUpWithLight#Type";
		String value = "Device";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		property = "Automation-New Automation WakeUpWithLight#DeviceAdd";
		value = "1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
	}

}