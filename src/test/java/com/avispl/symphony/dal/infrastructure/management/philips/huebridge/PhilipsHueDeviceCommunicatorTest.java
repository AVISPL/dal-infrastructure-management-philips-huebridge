package com.avispl.symphony.dal.infrastructure.management.philips.huebridge;


import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.PhilipsConstant;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.RoomsAndZonesControlEnum;

class PhilipsHueDeviceCommunicatorTest {

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
	 * Test getMultipleStatistics Control room
	 */
	@Tag("Mock")
	@Test
	void testControlRoom() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = PhilipsConstant.CREATE_ROOM + PhilipsConstant.HASH + PhilipsConstant.DEVICE_ADD;
		String propValue = "1";
//		controllableProperty.setValue(propValue);
//		controllableProperty.setProperty(propName);
//		philipsHueDeviceCommunicator.controlProperty(controllableProperty);

//		propName = PhilipsConstant.CREATE_ROOM + PhilipsConstant.HASH + PhilipsConstant.DEVICE + "0";
//		propValue = "Light 2";
//		controllableProperty.setValue(propValue);
//		controllableProperty.setProperty(propName);
//		philipsHueDeviceCommunicator.controlProperty(controllableProperty);

		propName = PhilipsConstant.CREATE_ROOM + PhilipsConstant.HASH + "Name";
		propValue = "Living Room 01";
		controllableProperty.setValue(propValue);
		controllableProperty.setProperty(propName);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);

		propName = PhilipsConstant.CREATE_ROOM + PhilipsConstant.HASH + "Type";
		propValue = "Living Room";
		controllableProperty.setValue(propValue);
		controllableProperty.setProperty(propName);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);

		propName = PhilipsConstant.CREATE_ROOM + PhilipsConstant.HASH + RoomsAndZonesControlEnum.ACTION.getName();
		propValue = "1";
		controllableProperty.setValue(propValue);
		controllableProperty.setProperty(propName);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
	}

	/**
	 * Test getMultipleStatistics Control Zone
	 */
	@Tag("Mock")
	@Test
	void testControlZone() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) philipsHueDeviceCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String propName = PhilipsConstant.CREATE_ZONE + PhilipsConstant.HASH + PhilipsConstant.DEVICE_ADD;
		String propValue = "1";
		controllableProperty.setValue(propValue);
		controllableProperty.setProperty(propName);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);

//		propName = PhilipsConstant.CREATE_ROOM + PhilipsConstant.HASH + RoomsAndZonesControlEnum.CANCEL.getName();
//		propValue = "1";
//		controllableProperty.setValue(propValue);
//		controllableProperty.setProperty(propName);
//		philipsHueDeviceCommunicator.controlProperty(controllableProperty);

		propName = PhilipsConstant.CREATE_ZONE + PhilipsConstant.HASH + PhilipsConstant.DEVICE + "0";
		propValue = "Light 1";
		controllableProperty.setValue(propValue);
		controllableProperty.setProperty(propName);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);

		propName = PhilipsConstant.CREATE_ZONE + PhilipsConstant.HASH + PhilipsConstant.DEVICE + "1";
		propValue = "None";
		controllableProperty.setValue(propValue);
		controllableProperty.setProperty(propName);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);

		propName = PhilipsConstant.CREATE_ZONE + PhilipsConstant.HASH + PhilipsConstant.DEVICE_ADD;
		propValue = "1";
		controllableProperty.setValue(propValue);
		controllableProperty.setProperty(propName);
		philipsHueDeviceCommunicator.controlProperty(controllableProperty);
	}
}