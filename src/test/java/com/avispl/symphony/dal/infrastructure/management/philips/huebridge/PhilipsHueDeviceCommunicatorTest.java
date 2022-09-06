/**
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import java.util.List;
import java.util.Map;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.api.dal.dto.monitor.aggregator.AggregatedDevice;
import com.avispl.symphony.api.dal.error.ResourceNotReachableException;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.NetworkInfoEnum;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.PhilipsURL;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.PhilipsUtil;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.RepeatDayEnum;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.SystemInfoEnum;

/**
 * Unit test for {@link PhilipsHueDeviceCommunicator}.
 * Test monitoring data with all bridge and aggregator device
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/3/2022
 * @since 1.0.0
 */
@Tag("Mock")
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
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.GROUP_LIGHT)).thenReturn(PhilipsURL.GROUP_LIGHT.getUrl());
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.CONFIG)).thenReturn(PhilipsURL.CONFIG.getUrl());
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.API)).thenReturn(PhilipsURL.API.getUrl());
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.BRIDGE)).thenReturn(PhilipsURL.BRIDGE.getUrl());
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.ZIGBEE_CONNECTIVITY)).thenReturn(PhilipsURL.ZIGBEE_CONNECTIVITY.getUrl());
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.DEVICE)).thenReturn(PhilipsURL.DEVICE.getUrl());
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.ROOMS)).thenReturn(PhilipsURL.ROOMS.getUrl());
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.ZONES)).thenReturn(PhilipsURL.ZONES.getUrl());
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.AUTOMATION)).thenReturn(PhilipsURL.AUTOMATION.getUrl());
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.SCRIPT_ID)).thenReturn(PhilipsURL.SCRIPT_ID.getUrl());
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.LIGHT)).thenReturn(PhilipsURL.LIGHT.getUrl());
		philipsHueDeviceCommunicator = new PhilipsHueDeviceCommunicator();
		philipsHueDeviceCommunicator.setTrustAllCertificates(false);
		philipsHueDeviceCommunicator.setProtocol(PROTOCOL);
		philipsHueDeviceCommunicator.setPort(wireMockRule.port());
		philipsHueDeviceCommunicator.setHost(HOST_NAME);
		philipsHueDeviceCommunicator.setContentType("application/json");
		philipsHueDeviceCommunicator.setPassword("admin");
		philipsHueDeviceCommunicator.setConfigManagement("True");
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

	//Create Automation------------------------------------------------------------------------------------------------------------

	/**
	 * Test create automation with property is Name
	 *
	 * Expect create automation with property is Name successfully
	 */
	@Test
	void testCreateAutomationWithName() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertEquals("False", stats.get("CreateAutomationBehaviorInstance#Edited"));
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "CreateAutomationBehaviorInstance#Name";
		String value = "New Auto";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);

		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals(value, stats.get(property));
		Assert.assertEquals("True", stats.get("CreateAutomationBehaviorInstance#Edited"));
	}

	/**
	 * Test create automation with property is Repeat Enable add new repeat
	 *
	 * Expect create automation with property is Repeat Enable add new repeat successfully
	 */
	@Test
	void testCreateAutomationWithRepeatEnableAddNewRepeat() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertEquals("False", stats.get("CreateAutomationBehaviorInstance#Edited"));
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "CreateAutomationBehaviorInstance#Repeat";
		String value = "1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		property = "CreateAutomationBehaviorInstance#RepeatAdd";
		value = "1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);

		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("Monday", stats.get("CreateAutomationBehaviorInstance#Repeat1"));
		Assert.assertEquals("True", stats.get("CreateAutomationBehaviorInstance#Edited"));
	}

	/**
	 * Test create automation with property Type is Device
	 *
	 * Expect create automation with property Type is Device successfully
	 */
	@Test
	void testCreateAutomationWithTypeIsDevice() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertEquals("False", stats.get("CreateAutomationBehaviorInstance#Edited"));
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "CreateAutomationBehaviorInstance#Type";
		String value = "Device";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);

		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals(value, stats.get(property));
		Assert.assertEquals("True", stats.get("CreateAutomationBehaviorInstance#Edited"));
	}

	/**
	 * Test create automation with property Type is Device add new device
	 *
	 * Expect create automation with property Type is Device add new device successfully
	 */
	@Test
	void testCreateAutomationWithTypeIsDeviceAddNewDevice() throws Exception {
		testCreateAutomationWithTypeIsDevice();
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "CreateAutomationBehaviorInstance#DeviceAdd";
		String value = "1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);

		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertNotNull(stats.get("CreateAutomationBehaviorInstance#Device1"));
		Assert.assertEquals("True", stats.get("CreateAutomationBehaviorInstance#Edited"));
	}


	/**
	 * Test create automation with property Type is Device add new device error
	 *
	 * Expect create automation with property Type is Device add new device throw exception
	 */
	@Test
	void testCreateAutomationWithTypeIsDeviceAddNewDeviceThrowException() throws Exception {
		testCreateAutomationWithTypeIsDevice();
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "CreateAutomationBehaviorInstance#DeviceAdd";
		String value = "1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);

		Assert.assertThrows("Expect error because user added enough devices and cannot add new devices", ResourceNotReachableException.class,
				() -> philipsHueDeviceCommunicator.controlProperty(controllableProperty));
	}

	/**
	 * Test create automation with property Type is Room
	 *
	 * Expect create automation with property Type is Room successfully
	 */
	@Test
	void testCreateAutomationWithTypeIsRoom() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertEquals("False", stats.get("CreateAutomationBehaviorInstance#Edited"));
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "CreateAutomationBehaviorInstance#Type";
		String value = "Room";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);

		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals(value, stats.get(property));
		Assert.assertEquals("True", stats.get("CreateAutomationBehaviorInstance#Edited"));
	}

	/**
	 * Test create automation with property Type is Room add new Room
	 *
	 * Expect create automation with property Type is Room add new Room successfully
	 */
	@Test
	void testCreateAutomationWithTypeIsRoomAddNewRoom() throws Exception {
		testCreateAutomationWithTypeIsDevice();
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "CreateAutomationBehaviorInstance#Type";
		String value = "Room";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		property = "CreateAutomationBehaviorInstance#RoomAdd";
		value = "1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);

		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertNotNull(stats.get("CreateAutomationBehaviorInstance#Room1"));
		Assert.assertEquals("True", stats.get("CreateAutomationBehaviorInstance#Edited"));
	}


	/**
	 * Test create automation with property Type is Room add new device error
	 *
	 * Expect create automation with property Type is Room add new Room throw exception
	 */
	@Test
	void testCreateAutomationWithTypeIsRoomAddNewRoomThrowException() throws Exception {
		testCreateAutomationWithTypeIsDevice();
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "CreateAutomationBehaviorInstance#Type";
		String value = "Room";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		property = "CreateAutomationBehaviorInstance#RoomAdd";
		value = "1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		Assert.assertThrows("Expect error because user added enough room and cannot add new room", ResourceNotReachableException.class,
				() -> philipsHueDeviceCommunicator.controlProperty(controllableProperty));
	}

	/**
	 * Test create automation with property Type is Zone
	 *
	 * Expect create automation with property Zone is Room successfully
	 */
	@Test
	void testCreateAutomationWithTypeIsZone() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertEquals("False", stats.get("CreateAutomationBehaviorInstance#Edited"));
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "CreateAutomationBehaviorInstance#Type";
		String value = "Zone";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);

		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals(value, stats.get(property));
		Assert.assertEquals("True", stats.get("CreateAutomationBehaviorInstance#Edited"));
	}


	/**
	 * Test create automation with property Type is Zone add new Zone
	 *
	 * Expect create automation with property Type is Zone add new Zone successfully
	 */
	@Test
	void testCreateAutomationWithTypeIsZoneAddNewZone() throws Exception {
		testCreateAutomationWithTypeIsDevice();
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.ZONES)).thenReturn(PhilipsURL.ZONES.getUrl() + "-automation");
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "CreateAutomationBehaviorInstance#Type";
		String value = "Zone";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		property = "CreateAutomationBehaviorInstance#ZoneAdd";
		value = "1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);

		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertNotNull(stats.get("CreateAutomationBehaviorInstance#Zone1"));
		Assert.assertEquals("True", stats.get("CreateAutomationBehaviorInstance#Edited"));
	}


	/**
	 * Test create automation with property Type is Zone add new Zone error
	 *
	 * Expect create automation with property Type is Zone add new Zone throw exception
	 */
	@Test
	void testCreateAutomationWithTypeIsRoomAddNewDeviceThrowException() throws Exception {
		testCreateAutomationWithTypeIsDevice();
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.ZONES)).thenReturn(PhilipsURL.ZONES.getUrl() + "-automation");
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "CreateAutomationBehaviorInstance#Type";
		String value = "Zone";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		property = "CreateAutomationBehaviorInstance#ZoneAdd";
		value = "1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		Assert.assertThrows("Expect error because user added enough Zone and cannot add new Zone", ResourceNotReachableException.class,
				() -> philipsHueDeviceCommunicator.controlProperty(controllableProperty));
	}

	/**
	 * Test create automation with type timer and device type
	 *
	 * Expect create automation with type timer and device type successfully
	 */
	@Test
	void testCreateAutoWithApplyChangeTypeTimerDevice() throws Exception {
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.DEVICE)).thenReturn(PhilipsURL.DEVICE.getUrl() + "-automation");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.ROOMS)).thenReturn(PhilipsURL.ROOMS.getUrl() + "-automation");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.ZONES)).thenReturn(PhilipsURL.ZONES.getUrl() + "-automation");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.AUTOMATION)).thenReturn(PhilipsURL.AUTOMATION.getUrl());
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.SCRIPT_ID)).thenReturn(PhilipsURL.SCRIPT_ID.getUrl() + "-automation");
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
		value = "Light 1-New 334";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("True", stats.get("CreateAutomationBehaviorInstance#Edited"));
		property = "CreateAutomationBehaviorInstance#Action";
		value = "1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("False", stats.get("CreateAutomationBehaviorInstance#Edited"));
	}

	/**
	 * Test create automation with type go to sleeps and room type
	 *
	 * Expect create automation with type go to sleeps and room type successfully
	 */
	@Test
	void testCreateAutoWithApplyChangeTypeGoToSleepRoom() throws Exception {
		mock.reset();
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.DEVICE)).thenReturn(PhilipsURL.DEVICE.getUrl() + "-automation");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.ROOMS)).thenReturn(PhilipsURL.ROOMS.getUrl() + "-automation");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.ZONES)).thenReturn(PhilipsURL.ZONES.getUrl() + "-automation");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.AUTOMATION)).thenReturn(PhilipsURL.AUTOMATION.getUrl());
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.SCRIPT_ID)).thenReturn(PhilipsURL.SCRIPT_ID.getUrl() + "-automation");
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "CreateAutomationBehaviorInstance#Name";
		String value = "New Automation Timer Device";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		property = "CreateAutomationBehaviorInstance#Type";
		value = "Room";
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
		philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		property = "CreateAutomationBehaviorInstance#Room0";
		value = "AllDeviceInRoom-New 334";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("True", stats.get("CreateAutomationBehaviorInstance#Edited"));
		property = "CreateAutomationBehaviorInstance#Action";
		value = "1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("False", stats.get("CreateAutomationBehaviorInstance#Edited"));
	}

	/**
	 * Test create automation with type wake up with light and zone type
	 *
	 * Expect create automation with type wake up with light and zone type successfully
	 */
	@Test
	void testCreateAutoWithApplyChangeTypeWakeUpWithLightZone() throws Exception {
		mock.reset();
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.DEVICE)).thenReturn(PhilipsURL.DEVICE.getUrl() + "-automation");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.ROOMS)).thenReturn(PhilipsURL.ROOMS.getUrl() + "-automation");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.ZONES)).thenReturn(PhilipsURL.ZONES.getUrl() + "-automation");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.AUTOMATION)).thenReturn(PhilipsURL.AUTOMATION.getUrl());
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.SCRIPT_ID)).thenReturn(PhilipsURL.SCRIPT_ID.getUrl() + "-automation");
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "CreateAutomationBehaviorInstance#Name";
		String value = "New Automation Timer Device";
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
		philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		property = "CreateAutomationBehaviorInstance#Zone0";
		value = "AllDeviceInZone-NEw 23";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("True", stats.get("CreateAutomationBehaviorInstance#Edited"));
		property = "CreateAutomationBehaviorInstance#Action";
		value = "1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("False", stats.get("CreateAutomationBehaviorInstance#Edited"));
	}

	//Edit automation------------------------------------------------------------------------

	/**
	 * Test edit automation with property is Name
	 *
	 * Expect edit automation with property is Name successfully
	 */
	@Test
	void testEditAutomationWithPropertyName() throws Exception {
		mock.reset();
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.DEVICE)).thenReturn(PhilipsURL.DEVICE.getUrl() + "-automation");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.ROOMS)).thenReturn(PhilipsURL.ROOMS.getUrl() + "-automation");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.ZONES)).thenReturn(PhilipsURL.ZONES.getUrl() + "-automation");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.AUTOMATION)).thenReturn(PhilipsURL.AUTOMATION.getUrl() + "-edit");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.SCRIPT_ID)).thenReturn(PhilipsURL.SCRIPT_ID.getUrl() + "-automation");
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "AutomationGoToSleep-Go to sleep1#Name";
		String value = "New auto";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);

		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("True", stats.get("AutomationGoToSleep-Go to sleep1#Edited"));
		Assert.assertEquals(value, stats.get(property));
	}

	/**
	 * Test edit automation with FadeDuration
	 *
	 * Expect edit automation with FadeDuration with min value is 10 successfully
	 */
	@Test
	void testEditAutomationWithFadeDurationeMinValue() throws Exception {
		mock.reset();
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.DEVICE)).thenReturn(PhilipsURL.DEVICE.getUrl() + "-automation");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.ROOMS)).thenReturn(PhilipsURL.ROOMS.getUrl() + "-automation");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.ZONES)).thenReturn(PhilipsURL.ZONES.getUrl() + "-automation");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.AUTOMATION)).thenReturn(PhilipsURL.AUTOMATION.getUrl() + "-edit");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.SCRIPT_ID)).thenReturn(PhilipsURL.SCRIPT_ID.getUrl() + "-automation");
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "AutomationGoToSleep-Go to sleep1#FadeDuration";
		String value = "0";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);

		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("True", stats.get("AutomationGoToSleep-Go to sleep1#Edited"));
		Assert.assertEquals("10", stats.get(property));
	}

	/**
	 * Test edit automation with FadeDuration
	 *
	 * Expect edit automation with FadeDuratione with max value is 5400 successfully
	 */
	@Test
	void testEditAutomationWithFadeDurationeMaxValue() throws Exception {
		mock.reset();
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.DEVICE)).thenReturn(PhilipsURL.DEVICE.getUrl() + "-automation");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.ROOMS)).thenReturn(PhilipsURL.ROOMS.getUrl() + "-automation");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.ZONES)).thenReturn(PhilipsURL.ZONES.getUrl() + "-automation");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.AUTOMATION)).thenReturn(PhilipsURL.AUTOMATION.getUrl() + "-edit");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.SCRIPT_ID)).thenReturn(PhilipsURL.SCRIPT_ID.getUrl() + "-automation");
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "AutomationGoToSleep-Go to sleep1#FadeDuration";
		String value = "5401";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);

		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("True", stats.get("AutomationGoToSleep-Go to sleep1#Edited"));
		Assert.assertEquals("5400", stats.get(property));
	}

	/**
	 * Test edit automation with TimeCurrent
	 *
	 * Expect edit automation with TimeCurrent successfully
	 */
	@Test
	void testEditAutomationWithTimeCurrent() throws Exception {
		mock.reset();
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.DEVICE)).thenReturn(PhilipsURL.DEVICE.getUrl() + "-automation");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.ROOMS)).thenReturn(PhilipsURL.ROOMS.getUrl() + "-automation");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.ZONES)).thenReturn(PhilipsURL.ZONES.getUrl() + "-automation");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.AUTOMATION)).thenReturn(PhilipsURL.AUTOMATION.getUrl() + "-edit");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.SCRIPT_ID)).thenReturn(PhilipsURL.SCRIPT_ID.getUrl() + "-automation");
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "AutomationGoToSleep-Go to sleep1#TimeCurrent";
		String value = "AM";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("True", stats.get("AutomationGoToSleep-Go to sleep1#Edited"));
		Assert.assertEquals(value, stats.get(property));
	}

	/**
	 * Test edit automation with TimeHour
	 *
	 * Expect edit automation with TimeHour successfully
	 */
	@Test
	void testEditAutomationWithTimeHour() throws Exception {
		mock.reset();
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.DEVICE)).thenReturn(PhilipsURL.DEVICE.getUrl() + "-automation");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.ROOMS)).thenReturn(PhilipsURL.ROOMS.getUrl() + "-automation");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.ZONES)).thenReturn(PhilipsURL.ZONES.getUrl() + "-automation");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.AUTOMATION)).thenReturn(PhilipsURL.AUTOMATION.getUrl() + "-edit");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.SCRIPT_ID)).thenReturn(PhilipsURL.SCRIPT_ID.getUrl() + "-automation");
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "AutomationGoToSleep-Go to sleep1#TimeHour";
		String value = "08";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("True", stats.get("AutomationGoToSleep-Go to sleep1#Edited"));
		Assert.assertEquals(value, stats.get(property));
	}

	/**
	 * Test edit automation with TimeMinute
	 *
	 * Expect edit automation with TimeMinute successfully
	 */
	@Test
	void testEditAutomationWithTimeMinute() throws Exception {
		mock.reset();
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.DEVICE)).thenReturn(PhilipsURL.DEVICE.getUrl() + "-automation");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.ROOMS)).thenReturn(PhilipsURL.ROOMS.getUrl() + "-automation");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.ZONES)).thenReturn(PhilipsURL.ZONES.getUrl() + "-automation");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.AUTOMATION)).thenReturn(PhilipsURL.AUTOMATION.getUrl() + "-edit");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.SCRIPT_ID)).thenReturn(PhilipsURL.SCRIPT_ID.getUrl() + "-automation");
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "AutomationGoToSleep-Go to sleep1#TimeMinute";
		String value = "08";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("True", stats.get("AutomationGoToSleep-Go to sleep1#Edited"));
		Assert.assertEquals(value, stats.get(property));
	}

	/**
	 * Test edit automation with type is room
	 *
	 * Expect edit automation with type is room successfully
	 */
	@Test
	void testEditAutomationWithIsRoom() throws Exception {
		mock.reset();
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.DEVICE)).thenReturn(PhilipsURL.DEVICE.getUrl() + "-automation");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.ROOMS)).thenReturn(PhilipsURL.ROOMS.getUrl() + "-automation");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.ZONES)).thenReturn(PhilipsURL.ZONES.getUrl() + "-automation");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.AUTOMATION)).thenReturn(PhilipsURL.AUTOMATION.getUrl() + "-edit");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.SCRIPT_ID)).thenReturn(PhilipsURL.SCRIPT_ID.getUrl() + "-automation");
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "AutomationGoToSleep-Go to sleep1#Type";
		String value = "Room";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("True", stats.get("AutomationGoToSleep-Go to sleep1#Edited"));
		Assert.assertEquals(value, stats.get(property));
	}

	/**
	 * Test edit automation with Repeat Disable
	 *
	 * Expect edit automation with Repeat Disable successfully
	 */
	@Test
	void testEditAutomationWithRepeatDisable() throws Exception {
		mock.reset();
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.DEVICE)).thenReturn(PhilipsURL.DEVICE.getUrl() + "-automation");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.ROOMS)).thenReturn(PhilipsURL.ROOMS.getUrl() + "-automation");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.ZONES)).thenReturn(PhilipsURL.ZONES.getUrl() + "-automation");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.AUTOMATION)).thenReturn(PhilipsURL.AUTOMATION.getUrl() + "-edit");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.SCRIPT_ID)).thenReturn(PhilipsURL.SCRIPT_ID.getUrl() + "-automation");
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "AutomationGoToSleep-Go to sleep1#Repeat";
		String value = "0";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("True", stats.get("AutomationGoToSleep-Go to sleep1#Edited"));
		Assert.assertEquals(value, stats.get(property));
	}


	/**
	 * Test edit automation with Repeat Enable
	 *
	 * Expect edit automation with Repeat Disable successfully
	 */
	@Test
	void testEditAutomationWithRepeatEnable() throws Exception {
		testEditAutomationWithRepeatDisable();
		mock.reset();
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.DEVICE)).thenReturn(PhilipsURL.DEVICE.getUrl() + "-automation");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.ROOMS)).thenReturn(PhilipsURL.ROOMS.getUrl() + "-automation");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.ZONES)).thenReturn(PhilipsURL.ZONES.getUrl() + "-automation");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.AUTOMATION)).thenReturn(PhilipsURL.AUTOMATION.getUrl() + "-edit");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.SCRIPT_ID)).thenReturn(PhilipsURL.SCRIPT_ID.getUrl() + "-automation");
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "AutomationGoToSleep-Go to sleep1#Repeat";
		String value = "1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("True", stats.get("AutomationGoToSleep-Go to sleep1#Edited"));
		Assert.assertEquals(value, stats.get(property));
	}

	/**
	 * Test edit automation with Repeat add
	 *
	 * Expect edit automation with Repeat add successfully
	 */
	@Test
	void testEditAutomationWithRepeatAdd() throws Exception {
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.DEVICE)).thenReturn(PhilipsURL.DEVICE.getUrl() + "-automation");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.ROOMS)).thenReturn(PhilipsURL.ROOMS.getUrl() + "-automation");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.ZONES)).thenReturn(PhilipsURL.ZONES.getUrl() + "-automation");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.AUTOMATION)).thenReturn(PhilipsURL.AUTOMATION.getUrl() + "-edit");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.SCRIPT_ID)).thenReturn(PhilipsURL.SCRIPT_ID.getUrl() + "-automation");
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "AutomationGoToSleep-Go to sleep1#Repeat6";
		String value = "None";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		property = "AutomationGoToSleep-Go to sleep1#RepeatAdd";
		value = "1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("True", stats.get("AutomationGoToSleep-Go to sleep1#Edited"));
		Assert.assertNotNull(stats.get("AutomationGoToSleep-Go to sleep1#Repeat6"));
	}

	/**
	 * Test edit automation with Repeat Enable
	 *
	 * Expect edit automation with Repeat Disable successfully
	 */
	@Test
	void testEditAutomationWithZoneAdd() throws Exception {
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.DEVICE)).thenReturn(PhilipsURL.DEVICE.getUrl() + "-automation");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.ROOMS)).thenReturn(PhilipsURL.ROOMS.getUrl() + "-automation");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.ZONES)).thenReturn(PhilipsURL.ZONES.getUrl() + "-automation");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.AUTOMATION)).thenReturn(PhilipsURL.AUTOMATION.getUrl() + "-edit");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.SCRIPT_ID)).thenReturn(PhilipsURL.SCRIPT_ID.getUrl() + "-automation");
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "AutomationGoToSleep-Go to sleep1#ZoneAdd";
		String value = "1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("True", stats.get("AutomationGoToSleep-Go to sleep1#Edited"));
		Assert.assertNotNull(stats.get("AutomationGoToSleep-Go to sleep1#Zone1"));
	}

	/**
	 * Test edit automation with ApplyChange
	 *
	 * Expect edit automation with ApplyChange successfully
	 */
	@Test
	void testEditAutomationWithApplyChange() throws Exception {
		testEditAutomationWithZoneAdd();
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		String property = "AutomationGoToSleep-Go to sleep1#Name";
		String value = "Go to sleep11";
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		property = "AutomationGoToSleep-Go to sleep1#ApplyChange";
		value = "1";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertEquals("False", stats.get("AutomationGoToSleep-Go to sleep1#Edited"));
	}

	//Filter----------------------------------------------------------------------------------------------------------------

	/**
	 * Test filter device by zone name
	 *
	 * Expect filter successfully with first index of zoneList
	 */
	@Test
	void testFilterDeviceByZoneCaseSuccessFilter() throws Exception {
		// Test filter when zone is specified but its value is invalid. In that case we will take first index of zoneList
		philipsHueDeviceCommunicator.destroy();
		philipsHueDeviceCommunicator.setZoneName("Zone 01");
		// With roomNames, deviceTypes, deviceNames are empty
		philipsHueDeviceCommunicator.init();
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Assert.assertTrue(extendedStatistics.getStatistics().containsKey("Zone-Zone 01#Name"));
	}

	/**
	 * Test filter by zone is specified but its value is empty
	 *
	 * Expect filter successfully have device in zone
	 */
	@Test
	void testFilterDeviceByZoneCaseNotSpecifyFilter() throws Exception {
		// Test filter when zone is specified but its value is empty. In that case we will take first index of zoneList
		philipsHueDeviceCommunicator.destroy();
		// With roomNames, deviceTypes, deviceNames are empty
		philipsHueDeviceCommunicator.init();
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Assert.assertTrue(extendedStatistics.getStatistics().containsKey("Zone-Zone 01#Name"));
	}

	/**
	 * Test filter by zone name is empty
	 *
	 * Expect filter successfully have device in zone
	 */
	@Test
	void testFilterDeviceByZoneCaseEmptyZoneName() throws Exception {
		// Test filter when zone is specified but its value is empty. In that case we will take first index of zoneList
		philipsHueDeviceCommunicator.destroy();
		philipsHueDeviceCommunicator.setZoneName("");
		// With roomNames, deviceTypes, deviceNames are empty
		philipsHueDeviceCommunicator.init();
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Assert.assertTrue(extendedStatistics.getStatistics().containsKey("Zone-Zone 01#Name"));
	}

	/**
	 * Test filter device by zone name invalid
	 *
	 * Expect filter successfully with device exits in room
	 */
	@Test
	void testFilterDeviceByZoneCaseInvalidZoneName() throws Exception {
		// Test filter when zone is specified but its value is invalid. In that case we will take first index of zoneList
		philipsHueDeviceCommunicator.destroy();
		philipsHueDeviceCommunicator.setZoneName("$$###");
		// With roomNames, deviceTypes, deviceNames are empty
		philipsHueDeviceCommunicator.init();
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Assert.assertTrue(extendedStatistics.getStatistics().containsKey("Zone-Zone 01#Name"));
	}

	/**
	 * Test filter device by room name valid
	 *
	 * Expect filter successfully with device exits in room
	 */
	@Test
	void testFilterDeviceByRoomNameCaseSuccessFilter() throws Exception {
		// Test filter when zone is specified but its value is invalid. In that case we will take first index of zoneList
		philipsHueDeviceCommunicator.destroy();
		philipsHueDeviceCommunicator.setRoomNames("Living Room 01");
		// With roomNames, deviceTypes, deviceNames are empty
		philipsHueDeviceCommunicator.init();
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertTrue(!stats.containsKey("Room-New room 3#Type") && stats.containsKey("Room-Living Room 01#Type"));
	}

	/**
	 * Test filter device by room name valid
	 *
	 * Expect filter successfully with device exits in room
	 */
	@Test
	void testFilterDeviceByRoomNameCaseSuccessFilterTwo() throws Exception {
		philipsHueDeviceCommunicator.destroy();
		philipsHueDeviceCommunicator.setRoomNames("New room 3");
		philipsHueDeviceCommunicator.init();
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertTrue(stats.containsKey("Room-New room 3#Type") && !stats.containsKey("Room-Living Room 01#Type"));
	}

	/**
	 * Test filter device by list room name
	 *
	 * Expect filter successfully list device exits in room
	 */
	@Test
	void testFilterDeviceByRoomNameCaseSuccessFilterThree() throws Exception {
		philipsHueDeviceCommunicator.destroy();
		philipsHueDeviceCommunicator.setRoomNames("New room 3, Living Room 01");
		philipsHueDeviceCommunicator.init();
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertTrue(stats.containsKey("Room-New room 3#Type") && stats.containsKey("Room-Living Room 01#Type"));
	}

	/**
	 * Test filter device by list room name
	 *
	 * Expect filter successfully list device exits in room
	 */
	@Test
	void testFilterDeviceByRoomNameCaseSuccessFilterFour() throws Exception {
		philipsHueDeviceCommunicator.destroy();
		philipsHueDeviceCommunicator.setRoomNames("New room 3, NotExistRoom, Not Exist Room 123@@@");
		philipsHueDeviceCommunicator.init();
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertTrue(stats.containsKey("Room-New room 3#Type") && !stats.containsKey("Room-NotExistRoom#Type") && !stats.containsKey("Room-Not Exist Room 123@@@#Type"));
	}

	/**
	 * Test filter device by room name not exits
	 *
	 * Expect filter successfully list device in room not exit
	 */
	@Test
	void testFilterDeviceByRoomNameCaseInvalidRoomName() throws Exception {
		philipsHueDeviceCommunicator.destroy();
		philipsHueDeviceCommunicator.setRoomNames("NotExistRoom");
		philipsHueDeviceCommunicator.init();
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertTrue(!stats.containsKey("Room-New room 3#Type") && !stats.containsKey("Room-Living Room 01#Type"));
	}

	/**
	 * Test filter device by room name invalid format name
	 *
	 * Expect filter successfully list device in room not exit
	 */
	@Test
	void testFilterDeviceByRoomNameCaseInvalidFormat() throws Exception {
		philipsHueDeviceCommunicator.destroy();
		philipsHueDeviceCommunicator.setRoomNames("#@@#@#!@#!,");
		philipsHueDeviceCommunicator.init();
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertTrue(!stats.containsKey("Room-New room 3#Type") && !stats.containsKey("Room-Living Room 01#Type"));
	}

	/**
	 * Test filter with device type is light
	 *
	 * Expect filter successfully with device have type light
	 */
	@Test
	void testFilterDeviceByDeviceTypeCaseSuccessFilter() throws Exception {
		// Test filter when zone is specified but its value is invalid. In that case we will take first index of zoneList
		philipsHueDeviceCommunicator.destroy();
		philipsHueDeviceCommunicator.setDeviceTypes("light");
		// With roomNames, deviceTypes, deviceNames are empty
		philipsHueDeviceCommunicator.init();
		philipsHueDeviceCommunicator.getMultipleStatistics();
		List<AggregatedDevice> retrieveMultipleStatistics = philipsHueDeviceCommunicator.retrieveMultipleStatistics();
		for (AggregatedDevice device :
				retrieveMultipleStatistics) {
			Assertions.assertTrue(device.getDeviceType().equals("light"));
		}
	}

	/**
	 * Test filter device with list type (light, button)
	 *
	 * Expect filter successfully with device
	 */
	@Test
	void testFilterDeviceByDeviceTypeCaseSuccessFilterTwo() throws Exception {
		// Test filter when zone is specified but its value is invalid. In that case we will take first index of zoneList
		philipsHueDeviceCommunicator.destroy();
		philipsHueDeviceCommunicator.setDeviceTypes("light, button");
		// With roomNames, deviceTypes, deviceNames are empty
		philipsHueDeviceCommunicator.init();
		philipsHueDeviceCommunicator.getMultipleStatistics();
		List<AggregatedDevice> retrieveMultipleStatistics = philipsHueDeviceCommunicator.retrieveMultipleStatistics();
		for (AggregatedDevice device :
				retrieveMultipleStatistics) {
			Assertions.assertTrue(device.getDeviceType().equals("light") || device.getDeviceType().equals("button"));
		}
	}

	/**
	 * Test filter with device type is button
	 *
	 * Expect filter successfully with device have type button
	 */
	@Test
	void testFilterDeviceByDeviceTypeCaseSuccessFilterThree() throws Exception {
		// Test filter when zone is specified but its value is invalid. In that case we will take first index of zoneList
		philipsHueDeviceCommunicator.destroy();
		philipsHueDeviceCommunicator.setDeviceTypes("button");
		// With roomNames, deviceTypes, deviceNames are empty
		philipsHueDeviceCommunicator.init();
		philipsHueDeviceCommunicator.getMultipleStatistics();
		List<AggregatedDevice> retrieveMultipleStatistics = philipsHueDeviceCommunicator.retrieveMultipleStatistics();
		for (AggregatedDevice device :
				retrieveMultipleStatistics) {
			Assertions.assertTrue(device.getDeviceType().equals("button"));
		}
	}

	/**
	 * Test filter with device type not exits
	 *
	 * Expect filter successfully with list device empty
	 */
	@Test
	void testFilterDeviceByDeviceTypeFailCaseTypeNotExists() throws Exception {
		// Test filter when zone is specified but its value is invalid. In that case we will take first index of zoneList
		philipsHueDeviceCommunicator.destroy();
		philipsHueDeviceCommunicator.setDeviceTypes("NotExistType");
		// With roomNames, deviceTypes, deviceNames are empty
		philipsHueDeviceCommunicator.init();
		philipsHueDeviceCommunicator.getMultipleStatistics();
		List<AggregatedDevice> retrieveMultipleStatistics = philipsHueDeviceCommunicator.retrieveMultipleStatistics();
		Assert.assertTrue(retrieveMultipleStatistics.isEmpty());
	}

	/**
	 * Test filter with device name
	 *
	 * Expect filter successfully
	 */
	@Test
	void testFilterDeviceByDeviceNameCaseSuccessFilter() throws Exception {
		// Test filter when zone is specified but its value is invalid. In that case we will take first index of zoneList
		philipsHueDeviceCommunicator.destroy();
		philipsHueDeviceCommunicator.setDeviceNames("Hue ambiance lamp");
		// With roomNames, deviceTypes, deviceNames are empty
		philipsHueDeviceCommunicator.init();
		philipsHueDeviceCommunicator.getMultipleStatistics();
		List<AggregatedDevice> retrieveMultipleStatistics = philipsHueDeviceCommunicator.retrieveMultipleStatistics();
		for (AggregatedDevice device :
				retrieveMultipleStatistics) {
			Assertions.assertTrue(device.getDeviceName().equals("Hue ambiance lamp"));
		}
	}

	/**
	 * Test filter with list device name
	 *
	 * Expect filter successfully with list device
	 */
	@Test
	void testFilterDeviceByDeviceNameCaseSuccessFilterTwo() throws Exception {
		// Test filter when zone is specified but its value is invalid. In that case we will take first index of zoneList
		philipsHueDeviceCommunicator.destroy();
		philipsHueDeviceCommunicator.setDeviceNames("Hue dimmer switch, Hue ambiance lamp");
		// With roomNames, deviceTypes, deviceNames are empty
		philipsHueDeviceCommunicator.init();
		philipsHueDeviceCommunicator.getMultipleStatistics();
		List<AggregatedDevice> retrieveMultipleStatistics = philipsHueDeviceCommunicator.retrieveMultipleStatistics();
		for (AggregatedDevice device :
				retrieveMultipleStatistics) {
			Assertions.assertTrue(device.getDeviceName().equals("Hue dimmer switch") || device.getDeviceName().equals("Hue ambiance lamp"));
		}
	}

	/**
	 * Test filter with device name
	 *
	 * Expect filter successfully with device
	 */
	@Test
	void testFilterDeviceByDeviceNameCaseSuccessFilterThree() throws Exception {
		// Test filter when zone is specified but its value is invalid. In that case we will take first index of zoneList
		philipsHueDeviceCommunicator.destroy();
		philipsHueDeviceCommunicator.setDeviceNames("Hue dimmer switch");
		// With roomNames, deviceTypes, deviceNames are empty
		philipsHueDeviceCommunicator.init();
		philipsHueDeviceCommunicator.getMultipleStatistics();
		List<AggregatedDevice> retrieveMultipleStatistics = philipsHueDeviceCommunicator.retrieveMultipleStatistics();
		for (AggregatedDevice device :
				retrieveMultipleStatistics) {
			Assertions.assertTrue(device.getDeviceName().equals("Hue dimmer switch"));
		}
	}

	/**
	 * Test filter device with name not exits
	 *
	 * Expect list device empty
	 */
	@Test
	void testFilterDeviceByDeviceNameFailCaseNameNotExists() throws Exception {
		// Test filter when zone is specified but its value is invalid. In that case we will take first index of zoneList
		philipsHueDeviceCommunicator.destroy();
		philipsHueDeviceCommunicator.setDeviceNames("NotExistName");
		// With roomNames, deviceTypes, deviceNames are empty
		philipsHueDeviceCommunicator.init();
		philipsHueDeviceCommunicator.getMultipleStatistics();
		List<AggregatedDevice> retrieveMultipleStatistics = philipsHueDeviceCommunicator.retrieveMultipleStatistics();
		Assert.assertTrue(retrieveMultipleStatistics.isEmpty());
	}

	/**
	 * Test filter by device name
	 *
	 * Expect filter successfully with list device
	 */
	@Test
	void testFilterDeviceOverall() throws Exception {
		// Test filter when zone is specified but its value is invalid. In that case we will take first index of zoneList
		philipsHueDeviceCommunicator.destroy();
		philipsHueDeviceCommunicator.setZoneName("Zone 01");
		philipsHueDeviceCommunicator.setRoomNames("Living Room 01");
		philipsHueDeviceCommunicator.setDeviceTypes("light");
		philipsHueDeviceCommunicator.setDeviceNames("Hue ambiance lamp");
		// With roomNames, deviceTypes, deviceNames are empty
		philipsHueDeviceCommunicator.init();
		philipsHueDeviceCommunicator.getMultipleStatistics();
		List<AggregatedDevice> retrieveMultipleStatistics = philipsHueDeviceCommunicator.retrieveMultipleStatistics();
		for (AggregatedDevice device :
				retrieveMultipleStatistics) {
			Assertions.assertTrue(device.getDeviceName().equals("Hue ambiance lamp"));
		}
	}

	//Aggregated device------------------------------------------------------------------------------------------------------

	/**
	 * Test control aggregated device with device is light
	 *
	 * Expect control successfully
	 */
	@Test
	void testChangeBrightnessForLight() throws Exception {
		mock.reset();
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.DEVICE)).thenReturn(PhilipsURL.DEVICE.getUrl() + "-color-light");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.ZIGBEE_CONNECTIVITY)).thenReturn(PhilipsURL.ZIGBEE_CONNECTIVITY.getUrl() + "-color-light");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.LIGHT)).thenReturn(PhilipsURL.LIGHT.getUrl());
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.ROOMS)).thenReturn(PhilipsURL.ROOMS.getUrl() + "-color-light");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.ZONES)).thenReturn(PhilipsURL.ZONES.getUrl() + "-color-light");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.AUTOMATION)).thenReturn(PhilipsURL.AUTOMATION.getUrl());
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.SCRIPT_ID)).thenReturn(PhilipsURL.SCRIPT_ID.getUrl() + "-automation");
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Thread.sleep(30000);
		philipsHueDeviceCommunicator.retrieveMultipleStatistics();
		String property = "brightness";
		String value = "100.0";
		String deviceID = "677ecc8d-b277-4849-b5ab-2b6e30d8b531";
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		controllableProperty.setDeviceId(deviceID);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Thread.sleep(30000);
		List<AggregatedDevice> deviceList = philipsHueDeviceCommunicator.retrieveMultipleStatistics();
		for (AggregatedDevice aggregatedDevice : deviceList) {
			if (aggregatedDevice.getDeviceId().equals(deviceID)) {
				Assertions.assertEquals("100.0", aggregatedDevice.getProperties().get("brightness"));
			}
		}
	}

	/**
	 * Test control aggregated device with device is light status
	 *
	 * Expect control status successfully
	 */
	@Test
	void testChangStatusForLight() throws Exception {
		mock.reset();
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.DEVICE)).thenReturn(PhilipsURL.DEVICE.getUrl() + "-color-light");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.ZIGBEE_CONNECTIVITY)).thenReturn(PhilipsURL.ZIGBEE_CONNECTIVITY.getUrl() + "-color-light");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.LIGHT)).thenReturn(PhilipsURL.LIGHT.getUrl());
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.ROOMS)).thenReturn(PhilipsURL.ROOMS.getUrl() + "-color-light");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.ZONES)).thenReturn(PhilipsURL.ZONES.getUrl() + "-color-light");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.AUTOMATION)).thenReturn(PhilipsURL.AUTOMATION.getUrl());
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.SCRIPT_ID)).thenReturn(PhilipsURL.SCRIPT_ID.getUrl() + "-automation");
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Thread.sleep(30000);
		philipsHueDeviceCommunicator.retrieveMultipleStatistics();
		String property = "status";
		String value = "0";
		String deviceID = "677ecc8d-b277-4849-b5ab-2b6e30d8b531";
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		controllableProperty.setDeviceId(deviceID);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		Thread.sleep(30000);
		List<AggregatedDevice> deviceList = philipsHueDeviceCommunicator.retrieveMultipleStatistics();
		for (AggregatedDevice aggregatedDevice : deviceList) {
			if (aggregatedDevice.getDeviceId().equals(deviceID)) {
				Assertions.assertEquals("0", aggregatedDevice.getProperties().get("status"));
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
		mock.reset();
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.DEVICE)).thenReturn(PhilipsURL.DEVICE.getUrl() + "-color-light");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.ZIGBEE_CONNECTIVITY)).thenReturn(PhilipsURL.ZIGBEE_CONNECTIVITY.getUrl() + "-color-light");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.LIGHT)).thenReturn(PhilipsURL.LIGHT.getUrl());
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.ROOMS)).thenReturn(PhilipsURL.ROOMS.getUrl() + "-color-light");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.ZONES)).thenReturn(PhilipsURL.ZONES.getUrl() + "-color-light");
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.AUTOMATION)).thenReturn(PhilipsURL.AUTOMATION.getUrl());
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.SCRIPT_ID)).thenReturn(PhilipsURL.SCRIPT_ID.getUrl() + "-automation");
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Thread.sleep(30000);
		philipsHueDeviceCommunicator.retrieveMultipleStatistics();
		String property = "colorTemperature(K)";
		String value = "366";
		String deviceID = "677ecc8d-b277-4849-b5ab-2b6e30d8b531";
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		controllableProperty.setDeviceId(deviceID);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
		philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Thread.sleep(30000);
		List<AggregatedDevice> deviceList = philipsHueDeviceCommunicator.retrieveMultipleStatistics();
		for (AggregatedDevice aggregatedDevice : deviceList) {
			if (aggregatedDevice.getDeviceId().equals(deviceID)) {
				Assertions.assertEquals("366", aggregatedDevice.getProperties().get("colorTemperature(K)"));
			}
		}
	}
}