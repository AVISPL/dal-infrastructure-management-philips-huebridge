/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge;


import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.api.dal.dto.monitor.aggregator.AggregatedDevice;
import com.avispl.symphony.api.dal.error.ResourceNotReachableException;

/**
 * Unit test for {@link PhilipsHueDeviceCommunicator}.
 * Test monitoring data with all bridge and aggregator device
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/8/2022
 * @since 1.0.0
 */
@Tag("RealDevice")
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

	/**
	 * Test control aggregated device with Brightness property
	 *
	 * Expect control Brightness successfully
	 */
	@Test
	void testChangeBrightnessForLight() throws Exception {
		philipsHueDeviceCommunicator.setRoomNameFilter("Room 2,AVI");
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Thread.sleep(10000);
		System.out.println(philipsHueDeviceCommunicator.retrieveMultipleStatistics());
		String property = "brightness";
		String value = "100.0";
		String deviceID = "bd22ceb5-e7b4-4ec3-90f4-3e362de7c2b5";
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		controllableProperty.setDeviceId(deviceID);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		List<AggregatedDevice> deviceList = philipsHueDeviceCommunicator.retrieveMultipleStatistics();
		System.out.println(deviceList);
		for (AggregatedDevice aggregatedDevice : deviceList) {
			if (aggregatedDevice.getDeviceId().equals(deviceID)) {
				Assertions.assertEquals("100.0", aggregatedDevice.getProperties().get("brightness"));
			}
		}
	}

	/**
	 * Test control aggregated device with status for light
	 *
	 * Expect control status successfully
	 */
	@Test
	void testChangStatusForLight() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Thread.sleep(30000);
		philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		String property = "status";
		String value = "1";
		String deviceID = "bd22ceb5-e7b4-4ec3-90f4-3e362de7c2b5";
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		controllableProperty.setDeviceId(deviceID);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		List<AggregatedDevice> deviceList = philipsHueDeviceCommunicator.retrieveMultipleStatistics();
		for (AggregatedDevice aggregatedDevice : deviceList) {
			if (aggregatedDevice.getDeviceId().equals(deviceID)) {
				Assertions.assertEquals("1", aggregatedDevice.getProperties().get("status"));
			}
		}
	}

	/**
	 * Test control aggregated device with device is light change colorTemperature(K)
	 *
	 * Expect control colorTemperature(K) successfully
	 */
	@Test
	void testChangColorTemperatureForLight() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Thread.sleep(30000);
		philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		String property = "colorTemperature(K)";
		String value = "300";
		String deviceID = "bd22ceb5-e7b4-4ec3-90f4-3e362de7c2b5";
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		controllableProperty.setDeviceId(deviceID);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		List<AggregatedDevice> deviceList = philipsHueDeviceCommunicator.retrieveMultipleStatistics();
		for (AggregatedDevice aggregatedDevice : deviceList) {
			if (aggregatedDevice.getDeviceId().equals(deviceID)) {
				Assertions.assertEquals("300", aggregatedDevice.getProperties().get(property));
			}
		}
	}

	/**
	 * Test control color light with ColorControl is Cyan color
	 *
	 * Expect control successfully
	 */
	@Test
	void testColorLight() throws Exception {
		philipsHueDeviceCommunicator.getMultipleStatistics();
		Thread.sleep(10000);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propertyName = "ColorControl";
		String propertyValue = "Cyan";
		String deviceId = "bd22ceb5-e7b4-4ec3-90f4-3e362de7c2bb";
		controllableProperty.setProperty(propertyName);
		controllableProperty.setValue(propertyValue);
		controllableProperty.setDeviceId(deviceId);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		List<AggregatedDevice> deviceList = philipsHueDeviceCommunicator.retrieveMultipleStatistics();
		for (AggregatedDevice aggregatedDevice : deviceList) {
			if (aggregatedDevice.getDeviceId().equals(deviceId)) {
				Assertions.assertEquals("300", aggregatedDevice.getProperties().get(propertyName));
			}
		}
	}

	/**
	 * Test control color light with custom color
	 *
	 * Expect control custom color successfully
	 */
	@Test
	void testColorLightCustomColor() throws Exception {
		philipsHueDeviceCommunicator.getMultipleStatistics();
		Thread.sleep(10000);
		ControllableProperty controllableProperty = new ControllableProperty();
		String propertyName = "ColorControl";
		String propertyValue = "CustomColor";
		String deviceId = "bd22ceb5-e7b4-4ec3-90f4-3e362de7c2bb";
		controllableProperty.setProperty(propertyName);
		controllableProperty.setValue(propertyValue);
		controllableProperty.setDeviceId(deviceId);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		List<AggregatedDevice> deviceList = philipsHueDeviceCommunicator.retrieveMultipleStatistics();
		for (AggregatedDevice aggregatedDevice : deviceList) {
			if (aggregatedDevice.getDeviceId().equals(deviceId)) {
				Assertions.assertEquals("300", aggregatedDevice.getProperties().get(propertyName));
			}
		}
	}

	/**
	 * Test update dropdown value of Room when change remove the value
	 *
	 * Expect room create failed because the device has been added with a sufficient number of devices
	 */
	@Test
	void testCreateRoomFailedDropdownDeviceNone() throws Exception {
		philipsHueDeviceCommunicator.getMultipleStatistics();
		Thread.sleep(10000);
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "CreateRoom#Name";
		String Value = "New room";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(Value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		property = "CreateRoom#DeviceAdd";
		Value = "1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(Value);
		Assert.assertThrows("Expect error because user added enough devices and cannot add new devices", ResourceNotReachableException.class,
				() -> philipsHueDeviceCommunicator.controlProperty(controllableProperty));
	}
	/**
	 * Test update dropdown value of Room when change remove the value
	 *
	 * Expect create room with new device successfully
	 */
	@Test
	void testCreateRoomWittNewDeviceAfterUpdatingDropdownListDevice() throws Exception {
		philipsHueDeviceCommunicator.getMultipleStatistics();
		Thread.sleep(10000);
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "CreateZone#Device0";
		String Value = "Light1-new";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(Value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		property = "CreateRoom#Type";
		Value = "Home";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(Value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		property = "Room-new 5#Device0";
		Value = "None";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(Value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		property = "Room-new 5#ApplyChanges";
		Value = "1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(Value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		property = "CreateRoom#Device0";
		Value = "Light1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(Value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assertions.assertEquals("True", stats.get("CreateRoom#Edited"));
		property = "CreateRoom#Create";
		Value = "1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(Value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assertions.assertEquals("False", stats.get("CreateRoom#Edited"));
	}
}