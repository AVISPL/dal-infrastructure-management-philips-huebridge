/**
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import java.util.Map;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.api.dal.error.ResourceNotReachableException;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.NetworkInfoEnum;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.PhilipsURL;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.PhilipsUtil;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.SystemInfoEnum;

/**
 * Unit test for {@link PhilipsHueDeviceCommunicator}.
 * Test monitoring data with all bridge and aggregator device
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/3/2022
 * @since 1.0.0
 */
public class PhilipsHueDeviceCommunicatorTest {
	static PhilipsHueDeviceCommunicator philipsHueDeviceCommunicator;
	private static final int HTTP_PORT = 8088;
	private static final int HTTPS_PORT = 8443;
	private static final String HOST_NAME = "127.0.0.1";
	private static final String PROTOCOL = "http";

	@Rule
	WireMockRule wireMockRule = new WireMockRule(options().port(HTTP_PORT).httpsPort(HTTPS_PORT)
			.bindAddress(HOST_NAME));
	MockedStatic<PhilipsUtil> mock = Mockito.mockStatic(PhilipsUtil.class);

	@BeforeEach
	public void init() throws Exception {
		wireMockRule.start();
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.DEVICE)).thenReturn(PhilipsURL.DEVICE.getUrl());
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.GROUP_LIGHT)).thenReturn(PhilipsURL.GROUP_LIGHT.getUrl());
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.CONFIG)).thenReturn(PhilipsURL.CONFIG.getUrl());
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.API)).thenReturn(PhilipsURL.API.getUrl());
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.BRIDGE)).thenReturn(PhilipsURL.BRIDGE.getUrl());
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.ZIGBEE_CONNECTIVITY)).thenReturn(PhilipsURL.ZIGBEE_CONNECTIVITY.getUrl());
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.ROOMS)).thenReturn(PhilipsURL.ROOMS.getUrl());
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.ZONES)).thenReturn(PhilipsURL.ZONES.getUrl());
		philipsHueDeviceCommunicator = new PhilipsHueDeviceCommunicator();
		philipsHueDeviceCommunicator.setTrustAllCertificates(false);
		philipsHueDeviceCommunicator.setProtocol(PROTOCOL);
		philipsHueDeviceCommunicator.setPort(wireMockRule.port());
		philipsHueDeviceCommunicator.setHost(HOST_NAME);
		philipsHueDeviceCommunicator.setContentType("application/json");
		philipsHueDeviceCommunicator.setPassword("admin");
		philipsHueDeviceCommunicator.init();
		philipsHueDeviceCommunicator.authenticate();
	}

	@AfterEach
	void stopWireMockRule() {
		philipsHueDeviceCommunicator.destroy();
		wireMockRule.stop();
		mock.close();
	}

	/**
	 * Test system monitoring
	 *
	 * Expect get monitoring data successfully
	 */
	@Tag("Mock")
	@Test
	void testSystemInfoMonitoring() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertEquals("541c699c-4876-494e-ae94-2e57b8f9bc81", stats.get(SystemInfoEnum.ID.getName()));
		Assert.assertEquals("Signify Netherlands B.V.", stats.get(SystemInfoEnum.MANUFACTURER.getName()));
		Assert.assertEquals("BSB002", stats.get(SystemInfoEnum.MODEL.getName()));
		Assert.assertEquals("bridge_v2", stats.get(SystemInfoEnum.ARCHETYPE.getName()));
		Assert.assertEquals("Philips hue", stats.get(SystemInfoEnum.NAME.getName()));
		Assert.assertEquals("1.52.1952154030", stats.get(SystemInfoEnum.VERSION.getName()));
		Assert.assertEquals("bridge", stats.get(SystemInfoEnum.TYPE.getName()));
	}

	/**
	 * Test Network info monitoring
	 *
	 * Expect get Network info monitoring data successfully
	 */
	@Tag("Mock")
	@Test
	void testNetworkInfoMonitoring() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();

		Assert.assertEquals("192.168.159.125", stats.get(NetworkInfoEnum.ADDRESS.getName()));
		Assert.assertEquals("255.255.255.0", stats.get(NetworkInfoEnum.NETMASK.getName()));
		Assert.assertEquals("2022-08-03T11:32:23", stats.get(NetworkInfoEnum.LOCATION_TIME.getName()));
		Assert.assertEquals("Asia/Ho_Chi_Minh", stats.get(NetworkInfoEnum.TIMEZONE.getName()));
		Assert.assertEquals("2022-08-03T04:32:23", stats.get(NetworkInfoEnum.UTC.getName()));
		Assert.assertEquals("ec:b5:fa:32:c7:cd", stats.get(NetworkInfoEnum.MAC_ADDRESS.getName()));
	}

	/**
	 * Test Network info monitoring
	 *
	 * Expect get Network info monitoring data successfully
	 */
	@Tag("Mock")
	@Test
	void testSystemMonitoringNoneValue() throws Exception {
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.BRIDGE)).thenReturn("/bridge-none");
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();

		Assert.assertEquals("None", stats.get(SystemInfoEnum.ID.getName()));
		Assert.assertEquals("None", stats.get(SystemInfoEnum.MANUFACTURER.getName()));
		Assert.assertEquals("None", stats.get(SystemInfoEnum.MODEL.getName()));
		Assert.assertEquals("None", stats.get(SystemInfoEnum.ARCHETYPE.getName()));
		Assert.assertEquals("None", stats.get(SystemInfoEnum.NAME.getName()));
		Assert.assertEquals("None", stats.get(SystemInfoEnum.VERSION.getName()));
		Assert.assertEquals("None", stats.get(SystemInfoEnum.TYPE.getName()));
	}

	/**
	 * Test Network info monitoring
	 *
	 * Expect get Network info monitoring data successfully
	 */
	@Tag("Mock")
	@Test
	void testNetworkInfoMonitoringNoneValue() throws Exception {
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.CONFIG)).thenReturn("/config-none");
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();

		Assert.assertEquals("None", stats.get(NetworkInfoEnum.ADDRESS.getName()));
		Assert.assertEquals("None", stats.get(NetworkInfoEnum.NETMASK.getName()));
		Assert.assertEquals("None", stats.get(NetworkInfoEnum.LOCATION_TIME.getName()));
		Assert.assertEquals("None", stats.get(NetworkInfoEnum.TIMEZONE.getName()));
		Assert.assertEquals("None", stats.get(NetworkInfoEnum.UTC.getName()));
		Assert.assertEquals("None", stats.get(NetworkInfoEnum.MAC_ADDRESS.getName()));
	}

	//Create room-------------------------------------------------------------------------------------------

	/**
	 * Test create room name
	 *
	 * Expect create room name successfully
	 */
	@Tag("Mock")
	@Test
	void testCreateRoomName() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertEquals("False", stats.get("CreateRoom#Edited"));
		Assert.assertEquals("", stats.get("CreateRoom#Name"));

		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "CreateRoom#Name";
		String value = "New room 01";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);

		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("True", stats.get("CreateRoom#Edited"));
		Assert.assertEquals("New room 01", stats.get("CreateRoom#Name"));
	}

	/**
	 * Test create room type
	 *
	 * Expect create room type successfully
	 */
	@Tag("Mock")
	@Test
	void testCreateRoomType() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertEquals("False", stats.get("CreateRoom#Edited"));
		Assert.assertEquals("None", stats.get("CreateRoom#Type"));

		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "CreateRoom#Type";
		String value = "Living Room";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);

		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("True", stats.get("CreateRoom#Edited"));
		Assert.assertEquals("Living Room", stats.get("CreateRoom#Type"));
	}

	/**
	 * Test create room with device0
	 *
	 * Expect create room with device0 successfully
	 */
	@Tag("Mock")
	@Test
	void testCreateRoomWithDevice0() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertEquals("False", stats.get("CreateRoom#Edited"));
		Assert.assertEquals("None", stats.get("CreateRoom#Device0"));

		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "CreateRoom#Device0";
		String value = "Light 2";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);

		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("True", stats.get("CreateRoom#Edited"));
		Assert.assertEquals("Light 2", stats.get("CreateRoom#Device0"));
	}

	/**
	 * Test create room add new device
	 *
	 * Expect create room add new successfully
	 */
	@Tag("Mock")
	@Test
	void testCreateRoomAddNewDevice() throws Exception {
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.ROOMS)).thenReturn("/clip/v2/resource/room-add-device");
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertEquals("False", stats.get("CreateRoom#Edited"));
		Assert.assertEquals("None", stats.get("CreateRoom#Device0"));

		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "CreateRoom#Device0";
		String value = "Light 2";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);

		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("True", stats.get("CreateRoom#Edited"));
		Assert.assertEquals("Light 2", stats.get("CreateRoom#Device0"));
	}

	/**
	 * Test create room add new device
	 *
	 * Expect create room add new throw exception
	 */
	@Tag("Mock")
	@Test
	void testCreateRoomAddNewDeviceError() throws Exception {
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.ROOMS)).thenReturn(PhilipsURL.ROOMS.getUrl());
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertEquals("False", stats.get("CreateRoom#Edited"));

		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "CreateRoom#DeviceAdd";
		String value = "1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		Assert.assertThrows("Expect error because user added enough devices and cannot add new devices", ResourceNotReachableException.class,
				() -> philipsHueDeviceCommunicator.controlProperty(controllableProperty));
	}

	/**
	 * Test create room with CancelChange property
	 *
	 * Expect create room CancelChange successfully
	 */
	@Tag("Mock")
	@Test
	void testCreateCancelChange() throws Exception {
		testCreateRoomName();
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertEquals("True", stats.get("CreateRoom#Edited"));
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "CreateRoom#CancelChange";
		String value = "New room";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);

		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("False", stats.get("CreateRoom#Edited"));
	}

	/**
	 * Test create room new room
	 *
	 * Expect create room successfully
	 */
	@Tag("Mock")
	@Test
	void testCreateRoom() throws Exception {
		testCreateRoomName();
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "CreateRoom#Type";
		String value = "Living Room";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);

		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("True", stats.get("CreateRoom#Edited"));
		Assert.assertEquals("Living Room", stats.get("CreateRoom#Type"));

		controllableProperty = new ControllableProperty();
		property = "CreateRoom#Device0";
		value = "Light 2";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);

		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("True", stats.get("CreateRoom#Edited"));
		Assert.assertEquals("Light 2", stats.get("CreateRoom#Device0"));
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("True", stats.get("CreateRoom#Edited"));
		controllableProperty = new ControllableProperty();
		property = "CreateRoom#Create";
		value = "1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("False", stats.get("CreateRoom#Edited"));
	}

	//Edit room-------------------------------------------------------------------------------------------

	/**
	 * Test control room name
	 *
	 * Expect control room name successfully
	 */
	@Tag("Mock")
	@Test
	void testControlRoomName() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertEquals("False", stats.get("Room-New room 3#Edited"));
		Assert.assertEquals("New room 3", stats.get("Room-New room 3#Name"));

		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "Room-New room 3#Name";
		String value = "New room";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);

		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		Assert.assertEquals("True", stats.get("Room-New room 3#Edited"));
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("New room", stats.get("Room-New room 3#Name"));
	}

	/**
	 * Test control room type
	 *
	 * Expect control room type successfully
	 */
	@Tag("Mock")
	@Test
	void testControlRoomType() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertEquals("False", stats.get("Room-New room 3#Edited"));
		Assert.assertEquals("Upstairs", stats.get("Room-New room 3#Type"));

		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "Room-New room 3#Type";
		String value = "Living Room";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);

		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		Assert.assertEquals("True", stats.get("Room-New room 3#Edited"));
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("Living Room", stats.get("Room-New room 3#Type"));
	}

	/**
	 * Test control room status
	 *
	 * Expect control room status successfully
	 */
	@Tag("Mock")
	@Test
	void testControlRoomStatus() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertEquals("False", stats.get("Room-New room 3#Edited"));
		Assert.assertEquals("0", stats.get("Room-New room 3#DeviceStatus"));

		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "Room-New room 3#DeviceStatus";
		String value = "Online";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);

		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		Assert.assertEquals("False", stats.get("Room-New room 3#Edited"));
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.GROUP_LIGHT)).thenReturn("/clip/v2/resource/grouped_light-status-online");
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("1", stats.get("Room-New room 3#DeviceStatus"));
	}

	/**
	 * Test control room with device0
	 *
	 * Expect control room with device0 successfully
	 */
	@Tag("Mock")
	@Test
	void testControlRoomWithDevice0() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertEquals("False", stats.get("Room-New room 3#Edited"));
		Assert.assertEquals("Light 1", stats.get("Room-New room 3#Device0"));

		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "Room-New room 3#Device0";
		String value = "Light 2";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);

		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		Assert.assertEquals("True", stats.get("Room-New room 3#Edited"));
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("Light 2", stats.get("Room-New room 3#Device0"));
	}

	/**
	 * Test control room add new device
	 *
	 * Expect control room add new successfully
	 */
	@Tag("Mock")
	@Test
	void testControlRoomAddNewDevice() throws Exception {
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.ROOMS)).thenReturn("/clip/v2/resource/room-add-device");
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertEquals("False", stats.get("Room-New room 3#Edited"));
		Assert.assertEquals("Light 1", stats.get("Room-New room 3#Device0"));

		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "Room-New room 3#DeviceAdd";
		String value = "1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);

		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		Assert.assertEquals("True", stats.get("Room-New room 3#Edited"));
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("Light 2", stats.get("Room-New room 3#Device1"));
	}

	/**
	 * Test control room add new device
	 *
	 * Expect control room add new throw exception
	 */
	@Tag("Mock")
	@Test
	void testControlRoomAddNewDeviceError() throws Exception {
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.ROOMS)).thenReturn(PhilipsURL.ROOMS.getUrl());
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertEquals("False", stats.get("Room-New room 3#Edited"));

		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "Room-New room 3#DeviceAdd";
		String value = "1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		Assert.assertThrows("Expect error because user added enough devices and cannot add new devices", ResourceNotReachableException.class,
				() -> philipsHueDeviceCommunicator.controlProperty(controllableProperty));
	}

	/**
	 * Test control delete room
	 *
	 * Expect control delete room successfully
	 */
	@Tag("Mock")
	@Test
	void testControlDeleteRoom() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertEquals("False", stats.get("Room-New room 3#Edited"));

		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "Room-New room 3#Action";
		String value = "1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);

		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.ROOMS)).thenReturn("/clip/v2/resource/room-delete-room");
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals(null, stats.get("Room-New room 3#Edited"));
		Assert.assertEquals(null, stats.get("Room-New room 3#DeviceStatus"));
	}

	/**
	 * Test control room with CancelChange property
	 *
	 * Expect control room CancelChange successfully
	 */
	@Tag("Mock")
	@Test
	void testControlCancelChange() throws Exception {
		testControlRoomName();
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertEquals("True", stats.get("Room-New room 3#Edited"));
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "Room-New room 3#CancelChange";
		String value = "New room";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);

		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("False", stats.get("Room-New room 3#Edited"));
	}

	/**
	 * Test control room edit room
	 *
	 * Expect control room ApplyChange edd=it room successfully
	 */
	@Tag("Mock")
	@Test
	void testControlApplyChange() throws Exception {
		testControlRoomName();
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "Room-New room 3#Type";
		String value = "Living Room";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);

		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		Assert.assertEquals("True", stats.get("Room-New room 3#Edited"));
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("Living Room", stats.get("Room-New room 3#Type"));

		controllableProperty = new ControllableProperty();
		property = "Room-New room 3#Device0";
		value = "Light 2";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);

		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		Assert.assertEquals("True", stats.get("Room-New room 3#Edited"));
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("Light 2", stats.get("Room-New room 3#Device0"));
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("True", stats.get("Room-New room 3#Edited"));
		controllableProperty = new ControllableProperty();
		property = "Room-New room 3#ApplyChange";
		value = "1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);

		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("False", stats.get("Room-New room 3#Edited"));
	}

	//Create Zone---------------------------------------------------------------------------------------------------------------

	/**
	 * Test create Zone name
	 *
	 * Expect create Zone name successfully
	 */
	@Tag("Mock")
	@Test
	void testCreateZoneName() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertEquals("False", stats.get("CreateZone#Edited"));
		Assert.assertEquals("", stats.get("CreateZone#Name"));

		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "CreateZone#Name";
		String value = "New Zone 01";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);

		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("True", stats.get("CreateZone#Edited"));
		Assert.assertEquals("New Zone 01", stats.get("CreateZone#Name"));
	}

	/**
	 * Test create Zone type
	 *
	 * Expect create Zone type successfully
	 */
	@Tag("Mock")
	@Test
	void testCreateZoneType() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertEquals("False", stats.get("CreateZone#Edited"));
		Assert.assertEquals("None", stats.get("CreateZone#Type"));

		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "CreateZone#Type";
		String value = "Living Zone";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);

		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("True", stats.get("CreateZone#Edited"));
		Assert.assertEquals("Living Zone", stats.get("CreateZone#Type"));
	}

	/**
	 * Test create Zone with device0
	 *
	 * Expect create Zone with device0 successfully
	 */
	@Tag("Mock")
	@Test
	void testCreateZoneWithDevice0() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertEquals("False", stats.get("CreateZone#Edited"));
		Assert.assertEquals("None", stats.get("CreateZone#Device0"));

		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "CreateZone#Device0";
		String value = "Light 2";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);

		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("True", stats.get("CreateZone#Edited"));
		Assert.assertEquals("Light 2", stats.get("CreateZone#Device0"));
	}

	/**
	 * Test create Zone add new device
	 *
	 * Expect create Zone add new successfully
	 */
	@Tag("Mock")
	@Test
	void testCreateZoneAddNewDevice() throws Exception {
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.ZONES)).thenReturn("/clip/v2/resource/Zone-add-device");
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertEquals("False", stats.get("CreateZone#Edited"));
		Assert.assertEquals("None", stats.get("CreateZone#Device0"));

		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "CreateZone#Device0";
		String value = "Light 2-Living Room 01";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);

		property = "CreateZone#DeviceAdd";
		value = "1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("True", stats.get("CreateZone#Edited"));
		Assert.assertEquals("Light 1-New room 3", stats.get("CreateZone#Device1"));
	}

	/**
	 * Test create Zone add new device
	 *
	 * Expect create Zone add new throw exception
	 */
	@Tag("Mock")
	@Test
	void testCreateZoneAddNewDeviceError() throws Exception {
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.ZONES)).thenReturn(PhilipsURL.ZONES.getUrl());
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertEquals("False", stats.get("CreateZone#Edited"));

		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "CreateZone#DeviceAdd";
		String value = "1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);

		Assert.assertThrows("Expect error because user added enough devices and cannot add new devices", ResourceNotReachableException.class,
				() -> philipsHueDeviceCommunicator.controlProperty(controllableProperty));
	}

	/**
	 * Test create Zone with CancelChange property
	 *
	 * Expect create Zone CancelChange successfully
	 */
	@Tag("Mock")
	@Test
	void testCreateZoneCancelChange() throws Exception {
		testCreateZoneName();
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertEquals("True", stats.get("CreateZone#Edited"));
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "CreateZone#CancelChange";
		String value = "New Zone";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);

		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("False", stats.get("CreateZone#Edited"));
	}

	/**
	 * Test create Zone new Zone
	 *
	 * Expect create Zone successfully
	 */
	@Tag("Mock")
	@Test
	void testCreateZone() throws Exception {
		testCreateZoneName();
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "CreateZone#Type";
		String value = "Living Zone";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);

		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("True", stats.get("CreateZone#Edited"));
		Assert.assertEquals("Living Zone", stats.get("CreateZone#Type"));

		controllableProperty = new ControllableProperty();
		property = "CreateZone#Device0";
		value = "Light 2";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);

		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("True", stats.get("CreateZone#Edited"));
		Assert.assertEquals("Light 2", stats.get("CreateZone#Device0"));
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("True", stats.get("CreateZone#Edited"));
		controllableProperty = new ControllableProperty();
		property = "CreateZone#Create";
		value = "1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("False", stats.get("CreateZone#Edited"));
	}

	//Edit zone-------------------------------------------------------------------------------------------

	/**
	 * Test control Zone name
	 *
	 * Expect control Zone name successfully
	 */
	@Tag("Mock")
	@Test
	void testControlZoneName() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertEquals("False", stats.get("Zone-Zone 01#Edited"));
		Assert.assertEquals("Zone 01", stats.get("Zone-Zone 01#Name"));

		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "Zone-Zone 01#Name";
		String value = "New Zone";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);

		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		Assert.assertEquals("True", stats.get("Zone-Zone 01#Edited"));
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("New Zone", stats.get("Zone-Zone 01#Name"));
	}

	/**
	 * Test control Zone type
	 *
	 * Expect control Zone type successfully
	 */
	@Tag("Mock")
	@Test
	void testControlZoneType() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertEquals("False", stats.get("Zone-Zone 01#Edited"));
		Assert.assertEquals("Living Room", stats.get("Zone-Zone 01#Type"));

		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "Zone-Zone 01#Type";
		String value = "Upstairs";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);

		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		Assert.assertEquals("True", stats.get("Zone-Zone 01#Edited"));
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("Upstairs", stats.get("Zone-Zone 01#Type"));
	}

	/**
	 * Test control Zone status
	 *
	 * Expect control Zone status successfully
	 */
	@Tag("Mock")
	@Test
	void testControlZoneStatus() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertEquals("False", stats.get("Zone-Zone 01#Edited"));
		Assert.assertEquals("0", stats.get("Zone-Zone 01#DeviceStatus"));

		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "Zone-Zone 01#DeviceStatus";
		String value = "Online";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);

		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		Assert.assertEquals("False", stats.get("Zone-Zone 01#Edited"));
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.GROUP_LIGHT)).thenReturn("/clip/v2/resource/grouped_light-status-online-zone");
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("1", stats.get("Zone-Zone 01#DeviceStatus"));
	}

	/**
	 * Test control Zone with device0
	 *
	 * Expect control Zone with device0 successfully
	 */
	@Tag("Mock")
	@Test
	void testControlZoneWithDevice0() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertEquals("False", stats.get("Zone-Zone 01#Edited"));
		Assert.assertEquals("Light 2-Living Room 01", stats.get("Zone-Zone 01#Device0"));

		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "Zone-Zone 01#Device0";
		String value = "Light 1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);

		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		Assert.assertEquals("True", stats.get("Zone-Zone 01#Edited"));
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("Light 1", stats.get("Zone-Zone 01#Device0"));
	}

	/**
	 * Test control Zone add new device
	 *
	 * Expect control Zone add new successfully
	 */
	@Tag("Mock")
	@Test
	void testControlZoneAddNewDevice() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertEquals("False", stats.get("Zone-Zone 01#Edited"));
		Assert.assertEquals("Light 2-Living Room 01", stats.get("Zone-Zone 01#Device0"));
		Assert.assertEquals(null, stats.get("Zone-Zone 01#Device1"));

		ControllableProperty controllableProperty = new ControllableProperty();

		String property = "Zone-Zone 01#Device0";
		String value = "Light 1-New room 3";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);

		property = "Zone-Zone 01#DeviceAdd";
		value = "1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);

		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		Assert.assertEquals("True", stats.get("Zone-Zone 01#Edited"));
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("Light 2-Living Room 01", stats.get("Zone-Zone 01#Device1"));
	}

	/**
	 * Test control Zone add new device
	 *
	 * Expect control Zone add new throw exception
	 */
	@Tag("Mock")
	@Test
	void testControlZoneAddNewDeviceError() throws Exception {
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.ZONES)).thenReturn(PhilipsURL.ZONES.getUrl());
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertEquals("False", stats.get("Zone-Zone 01#Edited"));

		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "Zone-Zone 01#DeviceAdd";
		String value = "1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);

		Assert.assertThrows("Expect error because user added enough devices and cannot add new devices", ResourceNotReachableException.class,
				() -> philipsHueDeviceCommunicator.controlProperty(controllableProperty));
	}

	/**
	 * Test control delete Zone
	 *
	 * Expect control delete Zone successfully
	 */
	@Tag("Mock")
	@Test
	void testControlDeleteZone() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertEquals("False", stats.get("Zone-Zone 01#Edited"));

		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "Zone-Zone 01#Action";
		String value = "1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);

		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.ZONES)).thenReturn("/clip/v2/resource/Zone-delete-Zone");
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals(null, stats.get("Zone-Zone 01#Edited"));
		Assert.assertEquals(null, stats.get("Zone-Zone 01#DeviceStatus"));
	}

	/**
	 * Test control Zone with CancelChange property
	 *
	 * Expect control Zone CancelChange successfully
	 */
	@Tag("Mock")
	@Test
	void testControlZoneCancelChange() throws Exception {
		testControlZoneName();
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertEquals("True", stats.get("Zone-Zone 01#Edited"));
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "Zone-Zone 01#CancelChange";
		String value = "New Zone";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);

		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("False", stats.get("Zone-Zone 01#Edited"));
	}

	/**
	 * Test control Zone edit Zone
	 *
	 * Expect control Zone ApplyChange edd=it Zone successfully
	 */
	@Tag("Mock")
	@Test
	void testControlZoneApplyChange() throws Exception {
		testControlZoneName();
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "Zone-Zone 01#Type";
		String value = "Living Zone";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);

		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		Assert.assertEquals("True", stats.get("Zone-Zone 01#Edited"));
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("Living Zone", stats.get("Zone-Zone 01#Type"));

		controllableProperty = new ControllableProperty();
		property = "Zone-Zone 01#Device0";
		value = "Light 2";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);

		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		Assert.assertEquals("True", stats.get("Zone-Zone 01#Edited"));
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("Light 2", stats.get("Zone-Zone 01#Device0"));
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("True", stats.get("Zone-Zone 01#Edited"));
		controllableProperty = new ControllableProperty();
		property = "Zone-Zone 01#ApplyChange";
		value = "1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);

		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("False", stats.get("Zone-Zone 01#Edited"));
	}
}