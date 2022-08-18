package com.avispl.symphony.dal.infrastructure.management.philips.huebridge;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import java.util.List;
import java.util.Map;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.primitives.Doubles;
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
		List<Double> a  = getRGBtoXY(255f, 0f, 0f);
		System.out.println(a);
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

	public static List<Double> getRGBtoXY(float r, float g, float b) {
		// For the hue bulb the corners of the triangle are:
		// -Red: 0.675, 0.322
		// -Green: 0.4091, 0.518
		// -Blue: 0.167, 0.04
		double[] normalizedToOne = new double[3];
		float cred, cgreen, cblue;
		cred = r;
		cgreen = g;
		cblue = b;
		normalizedToOne[0] = (cred / 255);
		normalizedToOne[1] = (cgreen / 255);
		normalizedToOne[2] = (cblue / 255);
		float red, green, blue;

		// Make red more vivid
		if (normalizedToOne[0] > 0.04045) {
			red = (float) Math.pow(
					(normalizedToOne[0] + 0.055) / (1.0 + 0.055), 2.4);
		} else {
			red = (float) (normalizedToOne[0] / 12.92);
		}

		// Make green more vivid
		if (normalizedToOne[1] > 0.04045) {
			green = (float) Math.pow((normalizedToOne[1] + 0.055)
					/ (1.0 + 0.055), 2.4);
		} else {
			green = (float) (normalizedToOne[1] / 12.92);
		}

		// Make blue more vivid
		if (normalizedToOne[2] > 0.04045) {
			blue = (float) Math.pow((normalizedToOne[2] + 0.055)
					/ (1.0 + 0.055), 2.4);
		} else {
			blue = (float) (normalizedToOne[2] / 12.92);
		}

		float X = (float) (red * 0.649926 + green * 0.103455 + blue * 0.197109);
		float Y = (float) (red * 0.234327 + green * 0.743075 + blue * 0.022598);
		float Z = (float) (red * 0.0000000 + green * 0.053077 + blue * 1.035763);

		float x = X / (X + Y + Z);
		float y = Y / (X + Y + Z);

		double[] xy = new double[2];
		xy[0] = x;
		xy[1] = y;
		List<Double> xyAsList = Doubles.asList(xy);
		return xyAsList;
	}
}