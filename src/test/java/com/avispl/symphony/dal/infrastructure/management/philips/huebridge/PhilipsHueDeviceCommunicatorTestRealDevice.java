package com.avispl.symphony.dal.infrastructure.management.philips.huebridge;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import java.util.Map;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.NetworkInfoEnum;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.PhilipsURL;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.PhilipsUtil;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.SystemInfoEnum;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.NetworkInfoResponse;

/**
 * Unit test for {@link PhilipsHueDeviceCommunicator}.
 * Test monitoring data with all bridge and aggregator device
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/3/2022
 * @since 1.0.0
 */
public class PhilipsHueDeviceCommunicatorTestRealDevice {
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
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.CONFIG)).thenReturn(PhilipsURL.CONFIG.getUrl());
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.API)).thenReturn(PhilipsURL.API.getUrl());
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.BRIDGE)).thenReturn(PhilipsURL.BRIDGE.getUrl());
		mock.when(() -> PhilipsUtil.getMonitorURL(PhilipsURL.ZIGBEE_CONNECTIVITY)).thenReturn(PhilipsURL.ZIGBEE_CONNECTIVITY.getUrl());
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
}