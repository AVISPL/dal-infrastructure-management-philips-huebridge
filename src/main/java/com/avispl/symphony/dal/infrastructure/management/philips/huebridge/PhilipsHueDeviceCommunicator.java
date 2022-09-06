/**
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge;

import static java.lang.Float.isNaN;

import java.awt.Color;
import java.math.RoundingMode;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.CollectionUtils;

import com.google.common.math.IntMath;

import com.avispl.symphony.api.dal.control.Controller;
import com.avispl.symphony.api.dal.dto.control.AdvancedControllableProperty;
import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.api.dal.dto.monitor.Statistics;
import com.avispl.symphony.api.dal.dto.monitor.aggregator.AggregatedDevice;
import com.avispl.symphony.api.dal.error.ResourceNotReachableException;
import com.avispl.symphony.api.dal.monitor.Monitorable;
import com.avispl.symphony.api.dal.monitor.aggregator.Aggregator;
import com.avispl.symphony.dal.communicator.RestCommunicator;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.AggregatedDeviceColorControllingMetric;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.AutomationEnum;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.ControllingMetric;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.CreateRoomEnum;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.EndStateEnum;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.EnumTypeHandler;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.LightControlEnum;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.NetworkInfoEnum;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.PhilipsConstant;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.PhilipsURL;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.PhilipsUtil;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.RepeatDayEnum;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.RoomTypeEnum;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.RoomsAndZonesControlEnum;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.StyleEnum;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.SystemInfoEnum;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.TimeHourEnum;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.TimeMinuteEnum;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.TimerHourEnum;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.TypeOfAutomation;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.TypeOfDeviceEnum;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.AggregatorWrapper;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.AutomationWrapper;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.BridgeWrapper;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.DevicePowerWrapper;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.GroupLightWrapper;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.LightWrapper;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.MotionSensorWrapper;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.NetworkInfoResponse;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.ResponseData;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.RoomAndZoneWrapper;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.ScriptAutomationWrapper;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.SystemWrapper;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.ZigbeeConnectivityWrapper;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.aggregateddevice.AggregatedDeviceResponse;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.aggregateddevice.MotionDevice;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.aggregateddevice.PowerDevice;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.aggregateddevice.ProductData;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.automation.AutoConfiguration;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.automation.AutomationResponse;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.automation.CurrentTime;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.automation.FadeDuration;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.automation.Group;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.automation.Location;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.automation.TimeAndRepeat;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.automation.TimePoint;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.bridge.BridgeListResponse;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.grouplight.BrightnessLight;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.grouplight.ColorPointGamut;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.grouplight.ColorTemperature;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.grouplight.GroupLightResponse;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.grouplight.StatusLight;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.light.LightResponse;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.responseData.ErrorsResponse;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.romandzone.Children;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.romandzone.MetaData;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.romandzone.RoomAndZoneResponse;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.scriptautomation.ScriptAutomationResponse;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.system.ServicesResponse;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.system.SystemResponse;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.zigbeeconnectivity.ZigbeeConnectivity;
import com.avispl.symphony.dal.util.StringUtils;

/**
 * PhilipsHue Aggregator Communicator Adapter
 *
 * Supported features are:
 * Monitoring for System and Network information
 *
 * Monitoring Aggregated Device:
 * <ul>
 * <li> - Online / Offline Status</li>
 * <li> - Firmware Version</li>
 * <li> - Device ID</li>
 * <li> - Device Model</li>
 * <li> - Device Name</li>
 * <li> - Serial Number</li>
 * </ul>
 *
 * Controlling Aggregated Device:
 * <ul>
 * <li> - Online / Offline Status</li>
 * <li> - Brightness </li>
 * <li> - Temperature Color</li>
 * </ul>
 *
 * Controlling Zones and Rooms
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 7/22/2022
 * @since 1.0.0
 */
public class PhilipsHueDeviceCommunicator extends RestCommunicator implements Aggregator, Monitorable, Controller {

	/**
	 * Process that is running constantly and triggers collecting data from Philips Hue API endpoints, based on the given timeouts and thresholds.
	 *
	 * @author Maksym.Rossiytsev, Harry
	 * @since 1.0.0
	 */
	class PhilipsHueDeviceDataLoader implements Runnable {
		private final int threadIndex;

		/**
		 * Parameters constructors
		 *
		 * @param threadIndex index of thread
		 */
		public PhilipsHueDeviceDataLoader(int threadIndex) {
			this.threadIndex = threadIndex;
		}

		@Override
		public void run() {
			if (logger.isDebugEnabled()) {
				logger.debug("Fetching Philips Hue device detail information" + threadIndex);
			}

			if (!aggregatedDeviceList.isEmpty()) {
				retrieveDeviceDetail(threadIndex);
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Finished collecting devices statistics cycle at " + new Date());
			}
		}
		// Finished collecting
	}

	/**
	 * Executor that runs all the async operations
	 */
	private static ExecutorService executorService;
	/**
	 * The current phase of monitoring cycle in polling interval
	 */
	private final AtomicInteger currentPhase = new AtomicInteger(0);

	/**
	 * Caching the list of failed monitoring devices
	 */
	private final Set<String> failedMonitoringDeviceIds = ConcurrentHashMap.newKeySet();

	/**
	 * Map of Repeat name for automation
	 */
	private final Map<String, String> repeatNameOfCreateAutomationMap = new HashMap<>();

	/**
	 * Map of Automation and Repeat name for automation
	 */
	private final Map<String, Map<String, String>> repeatNameOfAutomationMap = new HashMap<>();

	/**
	 * Map of Room name and ID
	 */
	private final Map<String, String> roomNameAndIdMap = new HashMap<>();

	/**
	 * Map of Zone name and ID
	 */
	private final Map<String, String> zoneNameAndIdMap = new HashMap<>();

	/**
	 * Map of automation type and map ID and value of it
	 */
	private final Map<String, Map<String, Map<String, String>>> automationAndTypeMapOfDeviceAndValue = new HashMap<>();
	/**
	 * List of ID bridge
	 */
	private final Set<String> bridgeIdList = new LinkedHashSet<>();
	/**
	 * List of Room
	 */
	private final List<RoomAndZoneResponse> roomList = new ArrayList<>();
	/**
	 * List of Automation
	 */
	private final List<AutomationResponse> automationList = new ArrayList<>();
	/**
	 * List of script automation
	 */
	private final List<ScriptAutomationResponse> scriptAutomationList = new ArrayList<>();
	/**
	 * Map of ID and Name script automation
	 */
	private final Map<String, String> idAndNameOfAutomationMap = new HashMap<>();
	/**
	 * List of Zones
	 */
	private final List<RoomAndZoneResponse> zoneList = new ArrayList<>();
	/**
	 * List of Room after applying filter
	 */
	private List<RoomAndZoneResponse> roomListAfterFilter = new ArrayList<>();
	/**
	 * List of Zones after applying filter
	 */
	private List<RoomAndZoneResponse> zoneListAfterFilter = new ArrayList<>();
	/**
	 * List of Group Light
	 */
	private final List<GroupLightResponse> groupLightList = new ArrayList<>();
	/**
	 * Map of name and grouped light
	 */
	private final Map<String, GroupLightResponse> groupLightMap = new HashMap<>();
	/**
	 * Map of device name and room
	 */
	private final Map<String, String> deviceRoomMap = new HashMap<>();
	/**
	 * Map of device name and Zone
	 */
	private final Map<String, String> deviceZoneMap = new HashMap<>();
	/**
	 * Map of device name and room edit
	 */
	private final Map<String, Map<String, String>> deviceRoomControlMap = new HashMap<>();
	/**
	 * Map of device name and Zone edit
	 */
	private final Map<String, Map<String, String>> zoneNameAndMapZoneDeviceControl = new HashMap<>();
	/**
	 * Map of room name and dropdown list device in room
	 */
	private final Map<String, String[]> roomAndDropdownListControlMap = new HashMap<>();
	/**
	 * Map of device name and map of deviceId and Room
	 */
	private final Map<String, Map<String, String>> deviceNameAndMapDeviceIdOfRoomMap = new HashMap<>();
	/**
	 * Map of device name and device ID of zone
	 */
	private final Map<String, String> deviceNameAndDeviceIdZoneMap = new HashMap<>();
	/**
	 * Map of device exits in room
	 */
	private final Map<String, String> deviceExitsInRoomMap = new HashMap<>();
	/**
	 * Map of all device ID and name of it
	 */
	private final Map<String, String> allDeviceIdAndNameMap = new HashMap<>();
	/**
	 * ReentrantLock to prevent null pointer exception to localExtendedStatistics when controlProperty method is called before GetMultipleStatistics method.
	 */
	private final ReentrantLock reentrantLock = new ReentrantLock();
	/**
	 * Polling interval which applied in adapter
	 */
	private volatile int localPollingInterval = PhilipsConstant.MIN_POLLING_INTERVAL;
	/**
	 * Number of threads in a thread pool reserved for the device statistics collection
	 */
	private volatile int deviceStatisticsCollectionThreads;
	// Adapter properties
	private String zoneName;
	private String roomNames;
	private String deviceTypes;
	private String deviceNames;
	private String configManagement;
	private String pollingInterval;

	private ExtendedStatistics localExtendedStatistics;
	private ExtendedStatistics localCreateZone;
	private ExtendedStatistics localCreateRoom;
	private ExtendedStatistics localCreateAutomation;
	private final Map<String, String> localCreateRoomStats = new HashMap<>();
	private final Map<String, String> localCreateZoneStats = new HashMap<>();
	private final Map<String, String> localCreateAutomationStats = new HashMap<>();
	private boolean isCreateAutomation;
	private boolean isCreateRoom;
	private boolean isCreateZone;
	private boolean isEmergencyDelivery;
	private boolean isConfigManagement;
	/**
	 * Caching the list of device Ids
	 */
	private Set<String> deviceIds = ConcurrentHashMap.newKeySet();
	private Map<String, Map<String, String>> typeAndMapOfDeviceAndValue = new HashMap<>();
	/**
	 * Map of aggregated device with key is deviceId, value is {@link AggregatedDevice}
	 */
	private ConcurrentHashMap<String, AggregatedDevice> aggregatedDeviceList = new ConcurrentHashMap<>();
	private ConcurrentHashMap<String, AggregatedDevice> cacheAggregatedDeviceList = new ConcurrentHashMap<>();
	/**
	 * Map of cached color light aggregated device with key is deviceId, value is HSV color of the light
	 */
	private ConcurrentHashMap<String, float[]> cachedColorLightAggregatedDevice = new ConcurrentHashMap<>();
	/**
	 * Map of metadata of device with key is deviceId, value is {@link AggregatedDeviceResponse}
	 */
	private ConcurrentHashMap<String, AggregatedDeviceResponse> listMetadataDevice = new ConcurrentHashMap<>();
	/**
	 * Philips Hue API Token
	 */
	private String apiToken;
	/**
	 * Runner service responsible for collecting data
	 */
	private PhilipsHueDeviceDataLoader deviceDataLoader;

	/**
	 * Retrieves {@code {@link #zoneName}}
	 *
	 * @return value of {@link #zoneName}
	 */
	public String getZoneName() {
		return zoneName;
	}

	/**
	 * Sets {@code zoneName}
	 *
	 * @param zoneName the {@code java.lang.String} field
	 */
	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}

	/**
	 * Retrieves {@code {@link #roomNames}}
	 *
	 * @return value of {@link #roomNames}
	 */
	public String getRoomNames() {
		return roomNames;
	}

	/**
	 * Sets {@code roomNames}
	 *
	 * @param roomNames the {@code java.lang.String} field
	 */
	public void setRoomNames(String roomNames) {
		this.roomNames = roomNames;
	}

	/**
	 * Retrieves {@code {@link #deviceTypes}}
	 *
	 * @return value of {@link #deviceTypes}
	 */
	public String getDeviceTypes() {
		return deviceTypes;
	}

	/**
	 * Sets {@code deviceTypes}
	 *
	 * @param deviceTypes the {@code java.lang.String} field
	 */
	public void setDeviceTypes(String deviceTypes) {
		this.deviceTypes = deviceTypes;
	}

	/**
	 * Retrieves {@code {@link #deviceNames}}
	 *
	 * @return value of {@link #deviceNames}
	 */
	public String getDeviceNames() {
		return deviceNames;
	}

	/**
	 * Sets {@code deviceNames}
	 *
	 * @param deviceNames the {@code java.lang.String} field
	 */
	public void setDeviceNames(String deviceNames) {
		this.deviceNames = deviceNames;
	}

	/**
	 * Retrieves {@code {@link #configManagement}}
	 *
	 * @return value of {@link #configManagement}
	 */
	public String getConfigManagement() {
		return configManagement;
	}

	/**
	 * Sets {@code configManagement}
	 *
	 * @param configManagement the {@code java.lang.String} field
	 */
	public void setConfigManagement(String configManagement) {
		this.configManagement = configManagement;
	}

	/**
	 * Retrieves {@code {@link #pollingInterval }}
	 *
	 * @return value of {@link #pollingInterval}
	 */
	public String getPollingInterval() {
		return pollingInterval;
	}

	/**
	 * Sets {@code pollingInterval}
	 *
	 * @param pollingInterval the {@code java.lang.String} field
	 */
	public void setPollingInterval(String pollingInterval) {
		this.pollingInterval = pollingInterval;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Statistics> getMultipleStatistics() {

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Getting statistics from Philips Hue at host %s with port %s", this.host, this.getPort()));
		}
		reentrantLock.lock();
		try {
			ExtendedStatistics extendedStatistics = new ExtendedStatistics();
			List<AdvancedControllableProperty> advancedControllableProperties = new LinkedList<>();
			List<AdvancedControllableProperty> createRoomControllableProperties = new LinkedList<>();
			List<AdvancedControllableProperty> createZoneControllableProperties = new LinkedList<>();
			List<AdvancedControllableProperty> createAutomationControllableProperties = new LinkedList<>();
			Map<String, String> createAutomationStats = new HashMap<>();
			Map<String, String> stats = new HashMap<>();
			Map<String, String> createRoomStats = new HashMap<>();
			Map<String, String> createZoneStats = new HashMap<>();

			isValidConfigManagement();
			if (!isEmergencyDelivery) {
				clearBeforeFetchingData();
				retrieveNetworkInfo(stats);
				retrieveZones();
				retrieveRooms();
				retrieveGroupLight();
				retrieveDeviceDropdownList();
				// retrieve device and filter devices in first monitoring cycle of polling interval
				if (currentPhase.get() == localPollingInterval || currentPhase.get() == 0) {
					cacheAggregatedDeviceList.clear();
					retrieveDevices();
					//Add filter device IDs
					filterDeviceIds();
				}
				retrieveAutomations();
				retrieveScriptIdForAutomation();
				retrieveListBridgeId();
				retrieveSystemInfoByBridgeIdList(stats);
				populateControlForAggregator(stats, advancedControllableProperties);

				extendedStatistics.setStatistics(stats);
				extendedStatistics.setControllableProperties(advancedControllableProperties);
				localExtendedStatistics = extendedStatistics;
			}
			if (isConfigManagement) {
				if (!isCreateRoom) {
					createRoom(createRoomStats, createRoomControllableProperties);
					localCreateRoom = new ExtendedStatistics();
					localCreateRoom.setStatistics(createRoomStats);
					localCreateRoom.setControllableProperties(createRoomControllableProperties);
				}
				if (!isCreateZone) {
					createZone(createZoneStats, createZoneControllableProperties);
					localCreateZone = new ExtendedStatistics();
					localCreateZone.setStatistics(createZoneStats);
					localCreateZone.setControllableProperties(createZoneControllableProperties);
				}
				if (!isCreateAutomation) {
					if (typeAndMapOfDeviceAndValue == null || typeAndMapOfDeviceAndValue.size() == 0) {
						typeAndMapOfDeviceAndValue = new HashMap<>();
						typeAndMapOfDeviceAndValue.put(PhilipsConstant.DEVICE, new HashMap<>());
						typeAndMapOfDeviceAndValue.put(PhilipsConstant.ROOM, new HashMap<>());
						typeAndMapOfDeviceAndValue.put(PhilipsConstant.ZONE, new HashMap<>());
					}
					createAutomation(createAutomationStats, createAutomationControllableProperties);
					localCreateAutomation = new ExtendedStatistics();
					localCreateAutomation.setStatistics(createAutomationStats);
					localCreateAutomation.setControllableProperties(createAutomationControllableProperties);
				}
			}

			Map<String, String> localStats = localExtendedStatistics.getStatistics();
			List<AdvancedControllableProperty> localAdvancedControl = localExtendedStatistics.getControllableProperties();

			updateLocalExtendedByValue(localStats, localAdvancedControl, localCreateRoom, localCreateRoomStats, isCreateRoom);
			updateLocalExtendedByValue(localStats, localAdvancedControl, localCreateZone, localCreateZoneStats, isCreateZone);
			updateLocalExtendedByValue(localStats, localAdvancedControl, localCreateAutomation, localCreateAutomationStats, isCreateAutomation);

			if (!aggregatedDeviceList.isEmpty()) {
				populatePollingInterval(stats);

				// calculating polling interval and threads quantity
				if (currentPhase.get() == PhilipsConstant.FIRST_MONITORING_CYCLE_OF_POLLING_INTERVAL) {
					localPollingInterval = calculatingLocalPollingInterval();
					deviceStatisticsCollectionThreads = calculatingThreadQuantity();
					pushFailedMonitoringDevicesIDToPriority();
				}

				if (currentPhase.get() == localPollingInterval) {
					currentPhase.set(0);
				}
				currentPhase.incrementAndGet();

				if (executorService == null) {
					executorService = Executors.newFixedThreadPool(deviceStatisticsCollectionThreads);
				}
				for (int threadNumber = 0; threadNumber < deviceStatisticsCollectionThreads; threadNumber++) {
					executorService.submit(new PhilipsHueDeviceDataLoader(threadNumber));
				}
			}
		} finally {
			reentrantLock.unlock();
		}
		return Collections.singletonList(localExtendedStatistics);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void internalInit() throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("Internal init is called.");
		}
		apiToken = this.getPassword();
		super.internalInit();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected HttpHeaders putExtraRequestHeaders(HttpMethod httpMethod, String uri, HttpHeaders headers) {
		headers.set("hue-application-key", apiToken);
		return headers;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void internalDestroy() {
		if (logger.isDebugEnabled()) {
			logger.debug("Internal destroy is called.");
		}

		if (deviceDataLoader != null) {
			deviceDataLoader = null;
		}

		if (executorService != null) {
			executorService.shutdownNow();
			executorService = null;
		}
		aggregatedDeviceList.clear();
		listMetadataDevice.clear();
		if (localExtendedStatistics != null && localExtendedStatistics.getStatistics() != null && localExtendedStatistics.getControllableProperties() != null) {
			localExtendedStatistics.getStatistics().clear();
			localExtendedStatistics.getControllableProperties().clear();
		}
		if (localCreateAutomation != null && localCreateAutomation.getStatistics() != null && localCreateAutomation.getControllableProperties() != null) {
			localCreateAutomation.getStatistics().clear();
			localCreateAutomation.getControllableProperties().clear();
		}
		if (localCreateZone != null && localCreateZone.getStatistics() != null && localCreateZone.getControllableProperties() != null) {
			localCreateZone.getStatistics().clear();
			localCreateZone.getControllableProperties().clear();
		}
		if (localCreateRoom != null && localCreateRoom.getStatistics() != null && localCreateRoom.getControllableProperties() != null) {
			localCreateRoom.getStatistics().clear();
			localCreateRoom.getControllableProperties().clear();
		}
		super.internalDestroy();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void controlProperties(List<ControllableProperty> list) {
		if (CollectionUtils.isEmpty(list)) {
			throw new IllegalArgumentException("Controllable properties cannot be null or empty");
		}
		for (ControllableProperty controllableProperty : list) {
			controlProperty(controllableProperty);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<AggregatedDevice> retrieveMultipleStatistics() {
		if (logger.isDebugEnabled()) {
			logger.debug("Start call retrieveMultipleStatistic");
		}

		if (!cachedColorLightAggregatedDevice.isEmpty()) {
			for (Entry<String, float[]> cachedDevice : cachedColorLightAggregatedDevice.entrySet()
			) {
				AggregatedDevice newDevice = cacheAggregatedDeviceList.get(cachedDevice.getKey());
				String dropdownValueInNewData = newDevice.getProperties().get(PhilipsConstant.COLOR_CONTROL);
				if (!dropdownValueInNewData.equals(AggregatedDeviceColorControllingMetric.CUSTOM_COLOR)) {
					float[] hsv = cachedDevice.getValue();
					Map<String, String> stats = newDevice.getProperties();
					List<AdvancedControllableProperty> advancedControllableProperties = newDevice.getControllableProperties();
					String currentColor = AggregatedDeviceColorControllingMetric.CUSTOM_COLOR;
					populateControlPropertiesForColorLight(stats, advancedControllableProperties, hsv, currentColor);
					cacheAggregatedDeviceList.get(cachedDevice.getKey()).setProperties(stats);
					cacheAggregatedDeviceList.get(cachedDevice.getKey()).setControllableProperties(advancedControllableProperties);
				}
			}
		}
		return cacheAggregatedDeviceList.values().stream().collect(Collectors.toList());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<AggregatedDevice> retrieveMultipleStatistics(List<String> list) throws Exception {
		return retrieveMultipleStatistics().stream().filter(aggregatedDevice -> list.contains(aggregatedDevice.getDeviceId())).collect(Collectors.toList());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void authenticate() throws Exception {
		// The aggregator have its own authorization method
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void controlProperty(ControllableProperty controllableProperty) {
		String property = controllableProperty.getProperty();
		String value = String.valueOf(controllableProperty.getValue());
		String deviceId = controllableProperty.getDeviceId();
		if (!StringUtils.isNullOrEmpty(value) && StringUtils.isNullOrEmpty(value.trim())) {
			throw new ResourceNotReachableException("Value format is invalid: The value ' ' is invalid");
		}
		reentrantLock.lock();
		try {
			if (localExtendedStatistics == null) {
				if (logger.isDebugEnabled()) {
					logger.debug(String.format("Error while controlling %s metric", property));
				}
				return;
			} else if (isConfigManagement && (localCreateRoom == null || localCreateZone == null)) {
				if (logger.isDebugEnabled()) {
					logger.debug(String.format("Error while controlling %s metric", property));
				}
				return;
			}
			if (!StringUtils.isNullOrEmpty(deviceId)) {
				controlAggregatedDevice(controllableProperty);
			} else {
				String[] propertyList = property.split(PhilipsConstant.HASH);
				String propertyGroup = propertyList[0];
				Map<String, String> localStats = localExtendedStatistics.getStatistics();
				List<AdvancedControllableProperty> localControllableProperties = localExtendedStatistics.getControllableProperties();
				ControllingMetric metricName = ControllingMetric.getMetricByName(propertyGroup);
				if (metricName != null) {
					switch (metricName) {
						case AUTOMATION:
							populateControlForAutomation(property, value, localStats, localControllableProperties);
							break;
						case CREATE_AUTOMATION:
							Map<String, String> updateCreateAutomation = localCreateAutomation.getStatistics();
							List<AdvancedControllableProperty> updateCreateAutomationControllableProperties = localCreateAutomation.getControllableProperties();
							populatePropertiesForCreateAutomation(property, value, updateCreateAutomation, updateCreateAutomationControllableProperties);
							localCreateAutomationStats.putAll(updateCreateAutomation);
							break;
						case CREATE_ROOM:
							Map<String, String> updateCreateRoom = localCreateRoom.getStatistics();
							List<AdvancedControllableProperty> updateCreateRoomControllableProperties = localCreateRoom.getControllableProperties();
							populatePropertiesForCreateRoom(property, value, updateCreateRoom, updateCreateRoomControllableProperties);
							localCreateRoomStats.putAll(updateCreateRoom);
							break;
						case CREATE_ZONE:
							Map<String, String> updateCreateZone = localCreateZone.getStatistics();
							List<AdvancedControllableProperty> updateCreateZoneControllableProperties = localCreateZone.getControllableProperties();
							populatePropertiesForCreateZone(property, value, updateCreateZone, updateCreateZoneControllableProperties);
							localCreateZoneStats.putAll(updateCreateZone);
							break;
						case ROOM:
							populateControlPropertiesForRoomAndZone(property, value, localStats, localControllableProperties, true);
							break;
						case ZONE:
							populateControlPropertiesForRoomAndZone(property, value, localStats, localControllableProperties, false);
							break;
						default:
							throw new ResourceNotReachableException(String.format("The property name not exits: %s", metricName));
					}
				}
			}
		} finally {
			reentrantLock.unlock();
		}
	}

	/**
	 * Populate control properties for controlling color light.
	 *
	 * @param stats Map of aggregated device statistics
	 * @param advancedControllableProperties List of AdvancedControlProperties from aggregated device.
	 * @param hsv HSV value of the aggregated device where type is color light
	 * @param currentColor Current color of the light
	 */
	private void populateControlPropertiesForColorLight(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties, float[] hsv, String currentColor) {
		String hueControlLabel = PhilipsConstant.COLOR_CONTROL_HUE;
		String currentHueControlLabel = PhilipsConstant.COLOR_CONTROL_HUE_CURRENT_VALUE;
		String saturationLabel = PhilipsConstant.COLOR_CONTROL_SATURATION;
		String currentSaturationControlLabel = PhilipsConstant.COLOR_CONTROL_SATURATION_CURRENT_VALUE;
		if (AggregatedDeviceColorControllingMetric.CUSTOM_COLOR.equals(currentColor)) {
			String hueLabelStart = String.valueOf(AggregatedDeviceColorControllingMetric.MIN_HUE);
			String hueLabelEnd = String.valueOf(AggregatedDeviceColorControllingMetric.MAX_HUE);
			String saturationLabelStart = String.valueOf(AggregatedDeviceColorControllingMetric.MIN_SATURATION);
			String saturationLabelEnd = String.valueOf(AggregatedDeviceColorControllingMetric.MAX_SATURATION);
			String colorName = getColorNameByHueAndSaturation(hsv[0], hsv[1]);

			AdvancedControllableProperty slider1Property = createControlSlider(hueControlLabel, String.valueOf(convertHueToRadianValue(hsv[0])), stats,
					hueLabelStart, hueLabelEnd);
			addOrUpdateAdvanceControlProperties(advancedControllableProperties, slider1Property);

			AdvancedControllableProperty slider2Property = createControlSlider(saturationLabel, String.valueOf(hsv[1]), stats,
					saturationLabelStart, saturationLabelEnd);
			addOrUpdateAdvanceControlProperties(advancedControllableProperties, slider2Property);
			stats.put(currentHueControlLabel, String.valueOf(hsv[0]));
			stats.put(currentSaturationControlLabel, String.valueOf(hsv[1]));
			stats.put(PhilipsConstant.COLOR_CONTROL_CURRENT_COLOR, colorName);
			stats.put(PhilipsConstant.COLOR_CONTROL_VALUE
					, String.valueOf(AggregatedDeviceColorControllingMetric.DEFAULT_BRIGHTNESS * AggregatedDeviceColorControllingMetric.ONE_HUNDRED_PERCENT));
		} else {
			Set<String> unusedKeys = new HashSet<>();
			unusedKeys.add(hueControlLabel);
			unusedKeys.add(saturationLabel);
			unusedKeys.add(currentHueControlLabel);
			unusedKeys.add(currentSaturationControlLabel);
			unusedKeys.add(PhilipsConstant.COLOR_CONTROL_CURRENT_COLOR);
			unusedKeys.add(PhilipsConstant.COLOR_CONTROL_VALUE);
			removeUnusedStatsAndControls(stats, advancedControllableProperties, unusedKeys);
		}
	}

	/**
	 * @param stats Map of statistics that contains statistics to be removed
	 * @param controls Set of controls that contains AdvancedControllableProperty to be removed
	 * @param listKeys list key of statistics to be removed
	 */
	private void removeUnusedStatsAndControls(Map<String, String> stats, List<AdvancedControllableProperty> controls, Set<String> listKeys) {
		for (String key : listKeys) {
			stats.remove(key);
			controls.removeIf(advancedControllableProperty -> advancedControllableProperty.getName().equals(key));
		}
	}

	/**
	 * This method is used for get color name by Hue and Saturation:
	 *
	 * @param hue color hue value
	 * @param saturation color saturation value
	 */
	private String getColorNameByHueAndSaturation(float hue, float saturation) {
		Color color = Color.getHSBColor(convertHueToPercentValue(hue), convertSaturationToPercentValue(saturation), AggregatedDeviceColorControllingMetric.DEFAULT_BRIGHTNESS);
		String colorName =
				PhilipsConstant.LEFT_PARENTHESES + color.getRed() + PhilipsConstant.COMMA + color.getGreen() + PhilipsConstant.COMMA + color.getBlue() + PhilipsConstant.RIGHT_PARENTHESES;
		hue = convertHueToRadianValue(hue);
		if (hue >= AggregatedDeviceColorControllingMetric.HUE_COORDINATE && hue < AggregatedDeviceColorControllingMetric.REDS_RANGE) {
			return AggregatedDeviceColorControllingMetric.REDS + colorName;
		}
		if (hue >= AggregatedDeviceColorControllingMetric.REDS_RANGE && hue < AggregatedDeviceColorControllingMetric.ORANGES_RANGE) {
			return AggregatedDeviceColorControllingMetric.ORANGES + colorName;
		}
		if (hue >= AggregatedDeviceColorControllingMetric.ORANGES_RANGE && hue < AggregatedDeviceColorControllingMetric.YELLOWS_RANGE) {
			return AggregatedDeviceColorControllingMetric.YELLOWS + colorName;
		}
		if (hue >= AggregatedDeviceColorControllingMetric.YELLOWS_RANGE && hue < AggregatedDeviceColorControllingMetric.YELLOW_GREENS_RANGE) {
			return AggregatedDeviceColorControllingMetric.YELLOW_GREENS + colorName;
		}
		if (hue >= AggregatedDeviceColorControllingMetric.YELLOW_GREENS_RANGE && hue < AggregatedDeviceColorControllingMetric.GREENS_RANGE) {
			return AggregatedDeviceColorControllingMetric.GREENS + colorName;
		}
		if (hue >= AggregatedDeviceColorControllingMetric.GREENS_RANGE && hue < AggregatedDeviceColorControllingMetric.BLUE_GREENS_RANGE) {
			return AggregatedDeviceColorControllingMetric.BLUE_GREENS + colorName;
		}
		if (hue >= AggregatedDeviceColorControllingMetric.BLUE_GREENS_RANGE && hue < AggregatedDeviceColorControllingMetric.BLUES_RANGE) {
			return AggregatedDeviceColorControllingMetric.BLUES + colorName;
		}
		if (hue >= AggregatedDeviceColorControllingMetric.BLUES_RANGE && hue < AggregatedDeviceColorControllingMetric.BLUE_VIOLETS_RANGE) {
			return AggregatedDeviceColorControllingMetric.BLUE_VIOLETS + colorName;
		}
		if (hue >= AggregatedDeviceColorControllingMetric.BLUE_VIOLETS_RANGE && hue < AggregatedDeviceColorControllingMetric.VIOLETS_RANGE) {
			return AggregatedDeviceColorControllingMetric.VIOLETS + colorName;
		}
		if (hue >= AggregatedDeviceColorControllingMetric.VIOLETS_RANGE && hue < AggregatedDeviceColorControllingMetric.MAUVES_RANGE) {
			return AggregatedDeviceColorControllingMetric.MAUVES + colorName;
		}
		if (hue >= AggregatedDeviceColorControllingMetric.MAUVES_RANGE && hue < AggregatedDeviceColorControllingMetric.MAUVE_PINKS_RANGE) {
			return AggregatedDeviceColorControllingMetric.MAUVE_PINKS + colorName;
		}
		if (hue >= AggregatedDeviceColorControllingMetric.MAUVES_RANGE && hue < AggregatedDeviceColorControllingMetric.PINKS_RANGE) {
			return AggregatedDeviceColorControllingMetric.PINKS + colorName;
		}
		return colorName;
	}

	/**
	 * This method is used to convert hue from smartThings value to percent
	 *
	 * @param hue color hue value
	 * @return Float saturation value
	 */
	private Float convertHueToPercentValue(float hue) {
		return hue / AggregatedDeviceColorControllingMetric.ONE_HUNDRED_PERCENT;
	}

	/**
	 * This method is used to convert hue from smartThings value to radian value
	 *
	 * @param hue color hue value
	 * @return Float hue value
	 */
	private Float convertHueToRadianValue(float hue) {
		return hue * AggregatedDeviceColorControllingMetric.MAX_HUE / AggregatedDeviceColorControllingMetric.ONE_HUNDRED_PERCENT;
	}

	/**
	 * This method is used to convert hue from smartThings value to percent
	 *
	 * @param saturation color saturation value
	 * @return Float saturation value
	 */
	private Float convertSaturationToPercentValue(float saturation) {
		return saturation / AggregatedDeviceColorControllingMetric.ONE_HUNDRED_PERCENT;
	}

	/**
	 * Calculate RGB value base on XYZ value returned from the API
	 *
	 * @param xValue X value
	 * @param yValue Y value
	 * @param zValue Z value or brightness of the light
	 * @return Map of R,G,B respectively values.
	 */
	public Map<String, Float> calculateRGB(float xValue, float yValue, float zValue) {
		Map<String, Float> rgbMap = new HashMap<>();
		float z = (float) (1.0 - xValue - yValue);
		float Y = (float) (zValue / 255.0); // Brightness of lamp
		float X = (Y / yValue) * xValue;
		float Z = (Y / yValue) * z;
		float r = (float) (X * 1.612 - Y * 0.203 - Z * 0.302);
		float g = (float) (-X * 0.509 + Y * 1.412 + Z * 0.066);
		float b = (float) (X * 0.026 - Y * 0.072 + Z * 0.962);
		r = (float) (r <= 0.0031308 ? 12.92 * r : (1.0 + 0.055) * Math.pow(r, 1.0 / 2.4) - 0.055);
		g = (float) (g <= 0.0031308 ? 12.92 * g : (1.0 + 0.055) * Math.pow(g, 1.0 / 2.4) - 0.055);
		b = (float) (b <= 0.0031308 ? 12.92 * b : (1.0 + 0.055) * Math.pow(b, 1.0 / 2.4) - 0.055);
		float maxValue = Math.max(r, g);
		maxValue = Math.max(maxValue, b);
		r /= maxValue;
		g /= maxValue;
		r = r * 255;
		if (r < 0) {
			r = 255;
		}
		g = g * 255;
		if (g < 0) {
			g = 255;
		}
		b = b * 255;
		if (b < 0) {
			b = 255;
		}
		rgbMap.put("R", r);
		rgbMap.put("G", g);
		rgbMap.put("B", b);
		return rgbMap;
	}

	/**
	 * This method is used to validate input config management from user
	 *
	 * @return boolean is configManagement
	 */
	public void isValidConfigManagement() {
		isConfigManagement = StringUtils.isNotNullOrEmpty(this.configManagement) && this.configManagement.equalsIgnoreCase("true");
	}

	/**
	 * Control aggregated device by id
	 *
	 * @param controllableProperty is ControllableProperty instance
	 */
	private void controlAggregatedDevice(ControllableProperty controllableProperty) {
		Map<String, String> localStats = cacheAggregatedDeviceList.get(controllableProperty.getDeviceId()).getProperties();
		List<AdvancedControllableProperty> advancedControllableProperties = cacheAggregatedDeviceList.get(controllableProperty.getDeviceId()).getControllableProperties();
		String type = localStats.get(PhilipsConstant.DEVICE_TYPE);
		String key = controllableProperty.getProperty();
		String deviceId = controllableProperty.getDeviceId();
		String value = String.valueOf(controllableProperty.getValue());
		String deviceModel = cacheAggregatedDeviceList.get(deviceId).getDeviceModel();
		deviceId = listMetadataDevice.get(deviceId).getServices()[0].getId();
		if (PhilipsConstant.LIGHT.equalsIgnoreCase(type)) {
			LightControlEnum lightProperty = EnumTypeHandler.getMetricOfEnumByName(LightControlEnum.class, key);
			switch (lightProperty) {
				case COLOR_TEMPERATURE:
					ColorTemperature colorTemperature = new ColorTemperature();
					colorTemperature.setMirek(String.valueOf((int) Float.parseFloat(value)));
					sendRequestToControlAggregatedDevice(PhilipsUtil.getMonitorURL(PhilipsURL.LIGHT), deviceId, colorTemperature.toString());
					break;
				case STATUS:
					StatusLight statusLight = new StatusLight();
					statusLight.setOn(!String.valueOf(PhilipsConstant.ZERO).equals(value));
					sendRequestToControlAggregatedDevice(PhilipsUtil.getMonitorURL(PhilipsURL.LIGHT), deviceId, statusLight.toString());
					break;
				case BRIGHTNESS:
					BrightnessLight brightnessLight = new BrightnessLight();
					brightnessLight.setBrightness(String.valueOf((int) Float.parseFloat(value)));
					sendRequestToControlAggregatedDevice(PhilipsUtil.getMonitorURL(PhilipsURL.LIGHT), deviceId, brightnessLight.toString());
					break;
				case COLOR_CONTROL:
					aggregatedDeviceColorDropdownControl(localStats, advancedControllableProperties, key, value, deviceId, deviceModel);
					break;
				case HUE_CONTROL:
					Float hue = convertHueToValue(Float.parseFloat(value));
					Float saturationValue = Float.valueOf(localStats.get(PhilipsConstant.COLOR_CONTROL_SATURATION));
					int rgbValHueControl = Color.HSBtoRGB(hue, saturationValue, 100.0F);
					float redHueControl = (rgbValHueControl >> 16) & 0xFF;
					float greenHueControl = (rgbValHueControl >> 8) & 0xFF;
					float blueHueControl = rgbValHueControl & 0xFF;
					ColorPointGamut hueControl = calculateXY(redHueControl, greenHueControl, blueHueControl, deviceModel);
					sendRequestToControlAggregatedDevice(PhilipsUtil.getMonitorURL(PhilipsURL.LIGHT), deviceId, hueControl.toString());
					break;
				case SATURATION_CONTROL:
					Float saturation = Float.parseFloat(value);
					Float hueValue = Float.valueOf(localStats.get(PhilipsConstant.COLOR_CONTROL_HUE));
					int rgbValSaturationControl = Color.HSBtoRGB(hueValue, saturation, 100.0F);
					float redSaturationControl = (rgbValSaturationControl >> 16) & 0xFF;
					float greenSaturationControl = (rgbValSaturationControl >> 8) & 0xFF;
					float blueSaturationControl = rgbValSaturationControl & 0xFF;
					ColorPointGamut saturationControl = calculateXY(redSaturationControl, greenSaturationControl, blueSaturationControl, deviceModel);
					sendRequestToControlAggregatedDevice(PhilipsUtil.getMonitorURL(PhilipsURL.LIGHT), deviceId, saturationControl.toString());
					break;
				default:
					if (logger.isDebugEnabled()) {
						logger.debug(String.format("Controlling Light by name %s is not supported.", lightProperty.getName()));
					}
					break;
			}
			updateValueForTheControllableProperty(key, value, localStats, advancedControllableProperties);
			cacheAggregatedDeviceList.get(controllableProperty.getDeviceId()).setControllableProperties(advancedControllableProperties);
		}
		if (PhilipsConstant.MOTION_SENSOR.equalsIgnoreCase(type)) {
			MotionDevice motionDevice = new MotionDevice();
			motionDevice.setStatus(!String.valueOf(PhilipsConstant.ZERO).equals(value));
			sendRequestToControlAggregatedDevice(PhilipsUtil.getMonitorURL(PhilipsURL.MOTION_SENSOR), deviceId, motionDevice.toString());
			updateValueForTheControllableProperty(key, value, localStats, advancedControllableProperties);
			cacheAggregatedDeviceList.get(controllableProperty.getDeviceId()).setControllableProperties(advancedControllableProperties);
		}

	}

	/**
	 * This method is used to convert hue from radian value to smartThings value
	 *
	 * @param hue color hue value
	 * @return Float hue value
	 */
	private Float convertHueToValue(float hue) {
		return hue * AggregatedDeviceColorControllingMetric.ONE_HUNDRED_PERCENT / AggregatedDeviceColorControllingMetric.MAX_HUE;
	}

	/**
	 * This method is used for calling color dropdown control for aggregated device:
	 *
	 * @param stats is the map that store all statistics
	 * @param advancedControllableProperties is the list that store all controllable properties
	 * @param controllableProperty name of controllable property
	 * @param value value of controllable property
	 * @throws ResourceNotReachableException when fail to control
	 */
	private void aggregatedDeviceColorDropdownControl(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties, String controllableProperty, String value,
			String deviceId, String deviceModel) {
		Color color = initializeCommonColors().get(value);
		float[] hsv = new float[3];
		String currentColor = PhilipsConstant.EMPTY_STRING;
		if (color != null) {
			ColorPointGamut colorPointGamut = calculateXY(color.getRed(), color.getGreen(), color.getBlue(), deviceModel);
			sendRequestToControlAggregatedDevice(PhilipsUtil.getMonitorURL(PhilipsURL.LIGHT), deviceId, colorPointGamut.toString());
			Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsv);
			currentColor = getDefaultColorNameByHueAndSaturation(hsv[0], hsv[1]);
			isEmergencyDelivery = true;
			populateControlPropertiesForColorLight(stats, advancedControllableProperties, hsv, currentColor);
			updateValueForTheControllableProperty(controllableProperty, value, stats, advancedControllableProperties);
			if (cachedColorLightAggregatedDevice.get(deviceId) != null) {
				cachedColorLightAggregatedDevice.remove(deviceId);
			}
		} else {
			String currentHueControlLabel = PhilipsConstant.COLOR_CONTROL_HUE_CURRENT_VALUE;
			String saturationLabel = PhilipsConstant.COLOR_CONTROL_SATURATION;
			String hValue = stats.get(currentHueControlLabel);
			String sValue = stats.get(saturationLabel);
			String vValue = String.valueOf(PhilipsConstant.DEFAULT_V_VALUE);
			hsv[0] = Float.parseFloat(hValue);
			hsv[1] = Float.parseFloat(sValue);
			hsv[2] = Float.parseFloat(vValue);
			currentColor = AggregatedDeviceColorControllingMetric.CUSTOM_COLOR;
			populateControlPropertiesForColorLight(stats, advancedControllableProperties, hsv, currentColor);
			updateValueForTheControllableProperty(controllableProperty, value, stats, advancedControllableProperties);
			cachedColorLightAggregatedDevice.put(deviceId, hsv);
		}
	}

	/**
	 * This method is used for get color default name by Hue and Saturation:
	 *
	 * @param hue color hue value
	 * @param saturation color saturation value
	 */
	private String getDefaultColorNameByHueAndSaturation(float hue, float saturation) {
		Color color = Color.getHSBColor(convertHueToPercentValue(hue), convertSaturationToPercentValue(saturation), AggregatedDeviceColorControllingMetric.DEFAULT_BRIGHTNESS);
		if (color.equals(Color.RED)) {
			return AggregatedDeviceColorControllingMetric.RED;
		}
		if (color.equals(Color.CYAN)) {
			return AggregatedDeviceColorControllingMetric.CYAN;
		}
		if (color.equals(Color.GREEN)) {
			return AggregatedDeviceColorControllingMetric.GREEN;
		}
		if (color.equals(Color.ORANGE)) {
			return AggregatedDeviceColorControllingMetric.ORANGE;
		}
		if (color.equals(Color.PINK)) {
			return AggregatedDeviceColorControllingMetric.PINK;
		}
		if (color.equals(Color.BLUE)) {
			return AggregatedDeviceColorControllingMetric.BLUE;
		}
		if (color.equals(Color.WHITE)) {
			return AggregatedDeviceColorControllingMetric.WHITE;
		}
		if (color.equals(Color.YELLOW)) {
			return AggregatedDeviceColorControllingMetric.YELLOW;
		}
		return AggregatedDeviceColorControllingMetric.CUSTOM_COLOR;
	}

	/**
	 * This method is used to init Map<colorName, colorCode> of common color
	 */
	private Map<String, Color> initializeCommonColors() {
		Map<String, Color> commonColors = new HashMap<>();
		commonColors.put(AggregatedDeviceColorControllingMetric.BLUE, Color.BLUE);
		commonColors.put(AggregatedDeviceColorControllingMetric.CYAN, Color.CYAN);
		commonColors.put(AggregatedDeviceColorControllingMetric.GREEN, Color.GREEN);
		commonColors.put(AggregatedDeviceColorControllingMetric.ORANGE, Color.ORANGE);
		commonColors.put(AggregatedDeviceColorControllingMetric.PINK, Color.PINK);
		commonColors.put(AggregatedDeviceColorControllingMetric.RED, Color.RED);
		commonColors.put(AggregatedDeviceColorControllingMetric.WHITE, Color.WHITE);
		commonColors.put(AggregatedDeviceColorControllingMetric.YELLOW, Color.YELLOW);
		return commonColors;
	}

	/**
	 * Calculate Hue XY based on rgb value.
	 *
	 * @param red Red color
	 * @param green Green color
	 * @param blue Blue color
	 * @param model Model of Hue color light
	 * @return ColorPointGamut or X,Y values.
	 */
	public ColorPointGamut calculateXY(float red, float green, float blue, String model) {
		red = red / 255;
		green = green / 255;
		blue = blue / 255;
		float r = (float) (red > 0.04045 ? Math.pow((red + 0.055) / 1.055, 2.4000000953674316) : red / 12.92);
		float g = (float) (green > 0.04045 ? Math.pow((green + 0.055) / 1.055, 2.4000000953674316) : green / 12.92);
		float b = (float) (blue > 0.04045 ? Math.pow((blue + 0.055) / 1.055, 2.4000000953674316) : blue / 12.92);
		float x = (float) (r * 0.664511 + g * 0.154324 + b * 0.162028);
		float y = (float) (r * 0.283881 + g * 0.668433 + b * 0.047685);
		float z = (float) (r * 8.8E-5 + g * 0.07231 + b * 0.986039);
		float[] xyRaw = new float[] { x / (x + y + z), y / (x + y + z) };
		if (isNaN(xyRaw[0])) {
			xyRaw[0] = 0.0F;
		}

		if (isNaN(xyRaw[1])) {
			xyRaw[1] = 0.0F;
		}
		ColorPointGamut xy = new ColorPointGamut(xyRaw[0], xyRaw[1]);
		// 3 color (rgb)
		ColorPointGamut[] colorPoints = colorPointsForModel(model);
		boolean inReachOfLamps = checkPointInLampsReach(xy, colorPoints);
		if (!inReachOfLamps) {
			ColorPointGamut pAB = getClosestPointToPoints(colorPoints[0], colorPoints[1], xy);
			ColorPointGamut pAC = getClosestPointToPoints(colorPoints[2], colorPoints[0], xy);
			ColorPointGamut pBC = getClosestPointToPoints(colorPoints[1], colorPoints[2], xy);
			float dAB = getDistanceBetweenTwoPoints(xy, pAB);
			float dAC = getDistanceBetweenTwoPoints(xy, pAC);
			float dBC = getDistanceBetweenTwoPoints(xy, pBC);
			float lowest = dAB;
			ColorPointGamut closestPoint = pAB;
			if (dAC < dAB) {
				lowest = dAC;
				closestPoint = pAC;
			}

			if (dBC < lowest) {
				closestPoint = pBC;
			}

			xy.setValueA(closestPoint.getValueA());
			xy.setValueB(closestPoint.getValueB());
		}

		xy.setValueA(precision(xy.getValueA()));
		xy.setValueB(precision(xy.getValueB()));
		return xy;
	}

	/**
	 * Round the value
	 *
	 * @param d Value to be rounded
	 * @return Preciesed value.
	 */
	float precision(float d) {
		return (float) (Math.round(10000.0 * d) / 10000.0);
	}

	/**
	 * Calculate the distance between two points
	 *
	 * @param pointA point A
	 * @param pointB point B
	 * @return distance of two points
	 */
	float getDistanceBetweenTwoPoints(ColorPointGamut pointA, ColorPointGamut pointB) {
		float dx = pointA.getValueA() - pointB.getValueA();
		float dy = pointA.getValueB() - pointB.getValueB();
		return (float) Math.sqrt(dx * dx + dy * dy);
	}

	/**
	 * Check if xy point reach it limitation
	 *
	 * @param point to be checked
	 * @param colorPoints Maximum coordinate that a point can reach
	 * @return true/false depends on if the point is out of range.
	 */
	private boolean checkPointInLampsReach(ColorPointGamut point, ColorPointGamut[] colorPoints) {
		if (point != null && colorPoints != null) {
			ColorPointGamut red = colorPoints[0];
			ColorPointGamut green = colorPoints[1];
			ColorPointGamut blue = colorPoints[2];
			ColorPointGamut v1 = new ColorPointGamut(green.getValueA() - red.getValueA(), green.getValueB() - red.getValueB());
			ColorPointGamut v2 = new ColorPointGamut(blue.getValueA() - red.getValueA(), blue.getValueB() - red.getValueB());
			ColorPointGamut q = new ColorPointGamut(point.getValueA() - red.getValueA(), point.getValueB() - red.getValueB());
			float s = crossProduct(q, v2) / crossProduct(v1, v2);
			float t = crossProduct(v1, q) / crossProduct(v1, v2);
			return s >= 0.0 && t >= 0.0 && s + t <= 1.0;
		} else {
			return false;
		}
	}


	/**
	 * Calculate closest of two points
	 *
	 * @param pointA point A
	 * @param pointB point B
	 * @return ColorPointGamut DTO
	 */
	private ColorPointGamut getClosestPointToPoints(ColorPointGamut pointA, ColorPointGamut pointB, ColorPointGamut pointP) {
		if (pointA != null && pointB != null && pointP != null) {
			ColorPointGamut pointAP = new ColorPointGamut(pointP.getValueA() - pointA.getValueA(), pointP.getValueB() - pointA.getValueB());
			ColorPointGamut pointAB = new ColorPointGamut(pointB.getValueA() - pointA.getValueA(), pointB.getValueB() - pointA.getValueB());
			float ab2 = pointAB.getValueA() * pointAB.getValueA() + pointAB.getValueB() * pointAB.getValueB();
			float apAb = pointAP.getValueA() * pointAB.getValueA() + pointAP.getValueB() * pointAB.getValueB();
			float t = apAb / ab2;
			if (t < 0.0) {
				t = 0.0F;
			} else if (t > 1.0) {
				t = 1.0F;
			}
			return new ColorPointGamut(pointA.getValueA() + pointAB.getValueA() * t, pointA.getValueB() + pointAB.getValueB() * t);
		} else {
			return null;
		}
	}

	/**
	 * Calculate cross of two points
	 *
	 * @param point1 First point
	 * @param point2 Second point
	 * @return Result after the cross.
	 */
	float crossProduct(ColorPointGamut point1, ColorPointGamut point2) {
		return point1.getValueA() * point2.getValueB() - point1.getValueB() * point2.getValueA();
	}

	/**
	 * Get the maximum point of XY base on light model
	 *
	 * @param model Model to be checked.
	 * @return Array of ColorPointGamut.
	 */
	private ColorPointGamut[] colorPointsForModel(String model) {
		if (model == null) {
			model = PhilipsConstant.SPACE;
		}

		if (!PhilipsConstant.GAMUT_B_BULBS_LIST.contains(model) && !PhilipsConstant.MULTI_SOURCE_LUMINAIRES.contains(model)) {
			if (PhilipsConstant.GAMUT_A_BULBS_LIST.contains(model)) {
				return PhilipsConstant.colorPointsGamut_A;
			} else if (PhilipsConstant.GAMUT_C_BULBS_LIST.contains(model)) {
				return PhilipsConstant.colorPointsGamut_C;
			} else {
				return PhilipsConstant.colorPointsDefault;
			}
		} else {
			return PhilipsConstant.colorPointsGamut_B;
		}
	}


	/**
	 * Send request to control aggregated device
	 *
	 * @param request the request is url to control device
	 * @param deviceId the deviceId is id of the device
	 * @param requestBody the requestBody is json value to control device
	 */
	private void sendRequestToControlAggregatedDevice(String request, String deviceId, String requestBody) {
		try {
			ResponseData responseData = doPut(request + PhilipsConstant.SLASH + deviceId, requestBody, ResponseData.class);
			if (responseData.getData() == null) {
				throw new ResourceNotReachableException("Error while control:" + Arrays.stream(responseData.getErrors()).map(ErrorsResponse::getDescription));
			}
		} catch (Exception e) {
			throw new ResourceNotReachableException("Can't control the device: " + e.getMessage(), e);
		}
	}

	/**
	 * Retrieve device detail by id
	 *
	 * @param currentThread current thread index
	 *
	 * Submit thread to get device detail info
	 */
	private void retrieveDeviceDetail(int currentThread) {
		int currentPhaseIndex = currentPhase.get() - PhilipsConstant.CONVERT_POSITION_TO_INDEX;
		int devicesPerPollingIntervalQuantity = IntMath.divide(aggregatedDeviceList.size(), localPollingInterval, RoundingMode.CEILING);

		List<String> deviceIdsInThread;
		if (currentThread == deviceStatisticsCollectionThreads - PhilipsConstant.CONVERT_POSITION_TO_INDEX) {
			// add the rest of the devices for a monitoring interval to the last thread
			deviceIdsInThread = this.deviceIds.stream()
					.skip(currentPhaseIndex * devicesPerPollingIntervalQuantity + currentThread * PhilipsConstant.MAX_DEVICE_QUANTITY_PER_THREAD)
					.limit(devicesPerPollingIntervalQuantity - PhilipsConstant.MAX_DEVICE_QUANTITY_PER_THREAD * currentThread)
					.collect(Collectors.toList());
		} else {
			deviceIdsInThread = this.deviceIds.stream()
					.skip(currentPhaseIndex * devicesPerPollingIntervalQuantity + currentThread * PhilipsConstant.MAX_DEVICE_QUANTITY_PER_THREAD)
					.limit(PhilipsConstant.MAX_DEVICE_QUANTITY_PER_THREAD)
					.collect(Collectors.toList());
		}
		try {
			for (String deviceId : deviceIdsInThread) {
				Long startTime = System.currentTimeMillis();
				retrieveDeviceStatus(deviceId);
				if (logger.isDebugEnabled()) {
					logger.debug(String.format("Finished fetch %s details info in worker thread: %s", aggregatedDeviceList.get(deviceId).getDeviceName(), startTime));
				}
				String type = listMetadataDevice.get(deviceId).getServices()[0].getType();
				if (PhilipsConstant.LIGHT.equalsIgnoreCase(type)) {
					retrieveDeviceWhereTypeIsLight(listMetadataDevice.get(deviceId));
				} else if (PhilipsConstant.MOTION_SENSOR.equalsIgnoreCase(type)) {
					retrieveDeviceWhereTypeIsMotionSensor(listMetadataDevice.get(deviceId));
				} else {
					for (ServicesResponse serviceType : listMetadataDevice.get(deviceId).getServices()) {
						if (PhilipsConstant.BUTTON.equalsIgnoreCase(serviceType.getType())) {
							retrieveDeviceWhereTypeIsButton(listMetadataDevice.get(deviceId));
						}
					}
				}
				cacheAggregatedDeviceList.put(deviceId, aggregatedDeviceList.get(deviceId));
				if (logger.isDebugEnabled()) {
					Long time = System.currentTimeMillis() - startTime;
					logger.debug(String.format("Finished fetch %s details info in worker thread: %s", aggregatedDeviceList.get(deviceId).getDeviceName(), time));
				}
			}
		} catch (Exception e) {
			logger.error(String.format("Exception during retrieve '%s' data processing", e.getMessage()), e);
		}
	}

	/**
	 * Retrieve device with type is button
	 *
	 * @param aggregatedDeviceResponse instance in AggregatedDeviceResponse DTO
	 */
	private void retrieveDeviceWhereTypeIsButton(AggregatedDeviceResponse aggregatedDeviceResponse) {
		String deviceID = aggregatedDeviceResponse.getId();
		String buttonID = PhilipsConstant.EMPTY_STRING;
		Optional<ServicesResponse> servicesResponseStream = Arrays.stream(aggregatedDeviceResponse.getServices()).filter(item -> item.getType().equalsIgnoreCase(PhilipsConstant.DEVICE_POWER)).findFirst();
		if (servicesResponseStream.isPresent()) {
			buttonID = servicesResponseStream.get().getId();
		}
		String request = PhilipsURL.BUTTON_POWER.getUrl().concat(buttonID);
		try {
			DevicePowerWrapper powerWrapper = doGet(request, DevicePowerWrapper.class);
			if (powerWrapper != null) {
				PowerDevice powerDevice = powerWrapper.getData()[0];
				Map<String, String> oldProperties = aggregatedDeviceList.get(deviceID).getProperties();
				oldProperties.put(PhilipsConstant.BATTERY_LEVEL, powerDevice.getPowerState().getBatteryLevel());
			} else {
				logger.error(String.format("%s button device type is empty", aggregatedDeviceList.get(deviceID).getDeviceName()));
			}
		} catch (Exception e) {
			logger.error(String.format("Error while retrieve %s device detail info: %s ", aggregatedDeviceList.get(deviceID).getDeviceName(), e.getMessage()), e);
		}
	}

	/**
	 * Retrieve device with type is motion sensor
	 *
	 * @param aggregatedDeviceResponse instance in AggregatedDeviceResponse DTO
	 */
	private void retrieveDeviceWhereTypeIsMotionSensor(AggregatedDeviceResponse aggregatedDeviceResponse) {
		String deviceID = aggregatedDeviceResponse.getId();
		String motionID = PhilipsConstant.EMPTY_STRING;
		Optional<ServicesResponse> servicesResponseStream = Arrays.stream(aggregatedDeviceResponse.getServices()).filter(item -> item.getType().equalsIgnoreCase(PhilipsConstant.MOTION_SENSOR))
				.findFirst();
		if (servicesResponseStream.isPresent()) {
			motionID = servicesResponseStream.get().getId();
		}
		String request = PhilipsURL.MOTION_SENSOR.getUrl().concat(motionID);
		try {
			MotionSensorWrapper motionSensorWrapper = doGet(request, MotionSensorWrapper.class);
			if (motionSensorWrapper != null) {
				MotionDevice motionDevice = motionSensorWrapper.getData()[0];
				Map<String, String> oldProperties = aggregatedDeviceList.get(deviceID).getProperties();
				oldProperties.put(PhilipsConstant.MOTION_DETECTED, String.valueOf(motionDevice.getMotion().isMotionDetected()));
				List<AdvancedControllableProperty> advancedControllableProperties = aggregatedDeviceList.get(deviceID).getControllableProperties();
				if (advancedControllableProperties == null) {
					advancedControllableProperties = new LinkedList<>();
				}
				String status = motionDevice.getMotion().isMotion() ? PhilipsConstant.ONLINE : PhilipsConstant.OFFLINE;
				AdvancedControllableProperty statusControl = controlSwitch(oldProperties, PhilipsConstant.STATUS, status, PhilipsConstant.OFFLINE, PhilipsConstant.ONLINE);
				advancedControllableProperties.add(statusControl);
				aggregatedDeviceList.get(deviceID).setControllableProperties(advancedControllableProperties);
			} else {
				logger.error(String.format("%s button device type is empty", aggregatedDeviceList.get(deviceID).getDeviceName()));
			}
		} catch (Exception e) {
			logger.error(String.format("Error while retrieve %s device detail info: %s ", aggregatedDeviceList.get(deviceID).getDeviceName(), e.getMessage()), e);
		}
	}

	/**
	 * This method is used to retrieve info of device with type is light
	 *
	 * @param aggregatedDeviceResponse is AggregatedDeviceResponse DTO instance
	 */
	private void retrieveDeviceWhereTypeIsLight(AggregatedDeviceResponse aggregatedDeviceResponse) {
		String deviceID = aggregatedDeviceResponse.getId();
		String lightID = aggregatedDeviceResponse.getServices()[0].getId();
		String request = PhilipsURL.LIGHT.getUrl().concat(PhilipsConstant.SLASH)
				.concat(lightID);
		try {
			GroupLightWrapper groupLightWrapper = doGet(request, GroupLightWrapper.class);
			if (groupLightWrapper != null) {
				GroupLightResponse groupLightResponse = groupLightWrapper.getData()[0];
				Map<String, String> oldProperties = aggregatedDeviceList.get(deviceID).getProperties();
				List<AdvancedControllableProperty> advancedControllableProperties = aggregatedDeviceList.get(deviceID).getControllableProperties();
				if (advancedControllableProperties == null) {
					advancedControllableProperties = new LinkedList<>();
				}
				AggregatedDevice aggregatedDevice = aggregatedDeviceList.get(deviceID);
				if (groupLightWrapper.getData()[0].getColor() != null) {
					populateControlForColorLightDevices(groupLightResponse, oldProperties, advancedControllableProperties);
					aggregatedDevice.setControllableProperties(advancedControllableProperties);
				} else {
					populateControlForLightDevices(groupLightResponse, oldProperties, advancedControllableProperties);
					aggregatedDevice.setControllableProperties(advancedControllableProperties);
				}
			} else {
				logger.error(String.format("%s light device is empty", aggregatedDeviceList.get(deviceID).getDeviceName()));
			}

		} catch (Exception e) {
			logger.error(String.format("Error while retrieve %s device detail info: %s ", aggregatedDeviceList.get(deviceID).getDeviceName(), e.getMessage()), e);
		}
	}

	/**
	 * Populate aggregated devices for color light
	 *
	 * @param groupLightResponse is Group Light instance
	 * @param stats the stats are list of statistics
	 * @param advancedControllableProperties advancedControllableProperties is the list that store all controllable properties
	 */
	private void populateControlForColorLightDevices(GroupLightResponse groupLightResponse, Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {
		int status = PhilipsConstant.ZERO;
		if (groupLightResponse.getStatusLight().isOn()) {
			status = PhilipsConstant.NUMBER_ONE;
		}
		AdvancedControllableProperty statusControl = controlSwitch(stats, PhilipsConstant.STATUS, String.valueOf(status), PhilipsConstant.OFFLINE, PhilipsConstant.ONLINE);
		addOrUpdateAdvanceControlProperties(advancedControllableProperties, statusControl);

		String brightness = String.valueOf(PhilipsConstant.DEFAULT_V_VALUE);
		AdvancedControllableProperty sliderControlProperty = createControlSlider(PhilipsConstant.BRIGHTNESS, brightness, stats,
				String.valueOf(PhilipsConstant.MIN_END_BRIGHTNESS), String.valueOf(PhilipsConstant.MAX_END_BRIGHTNESS));
		addOrUpdateAdvanceControlProperties(advancedControllableProperties, sliderControlProperty);

		String colorTemperature = groupLightResponse.getTemperature().getMirek();
		AdvancedControllableProperty colorTemperatureControlProperty = createControlSlider(PhilipsConstant.COLOR_TEMPERATURE, colorTemperature, stats,
				String.valueOf(PhilipsConstant.MIN_COLOR_TEMPERATURE), String.valueOf(PhilipsConstant.MAX_COLOR_TEMPERATURE));
		addOrUpdateAdvanceControlProperties(advancedControllableProperties, colorTemperatureControlProperty);
		String xValue = groupLightResponse.getColor().getLocationLight().getAxisX();
		float xValueFloat = Float.parseFloat(xValue);
		String yValue = groupLightResponse.getColor().getLocationLight().getAxisY();
		float yValueFloat = Float.parseFloat(yValue);
		String zValue = String.valueOf(PhilipsConstant.DEFAULT_V_VALUE);
		float zValueFloat = Float.parseFloat(zValue);
		Map<String, Float> rgbMap = calculateRGB(xValueFloat, yValueFloat, zValueFloat);
		// Convert RGB to HSV:
		float[] hsv = new float[3];
		Color.RGBtoHSB((int) (rgbMap.get("R") / 255), (int) (rgbMap.get("G") / 255), (int) (rgbMap.get("B") / 255), hsv);
		String currentColor = getDefaultColorNameByHueAndSaturation(hsv[0], hsv[1]);
		// Color dropdown
		List<String> colorModes = new ArrayList<>(initializeCommonColors().keySet());
		colorModes.add(AggregatedDeviceColorControllingMetric.CUSTOM_COLOR);
		String[] colorDropdown = colorModes.toArray(new String[colorModes.size()]);
		AdvancedControllableProperty dropdownProperty = controlDropdown(stats, colorDropdown, PhilipsConstant.COLOR_CONTROL, currentColor);
		addOrUpdateAdvanceControlProperties(advancedControllableProperties, dropdownProperty);
		// populate custom HSV color control
		populateControlPropertiesForColorLight(stats, advancedControllableProperties, hsv, currentColor);
	}


	/**
	 * Populate device is light
	 *
	 * @param groupLightResponse is Group Light instance
	 * @param stats the stats are list of statistics
	 * @param advancedControllableProperties advancedControllableProperties is the list that store all controllable properties
	 */
	private void populateControlForLightDevices(GroupLightResponse groupLightResponse, Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {

		int status = PhilipsConstant.ZERO;
		if (groupLightResponse.getStatusLight().isOn()) {
			status = PhilipsConstant.NUMBER_ONE;
		}
		AdvancedControllableProperty statusControl = controlSwitch(stats, PhilipsConstant.STATUS, String.valueOf(status), PhilipsConstant.OFFLINE, PhilipsConstant.ONLINE);
		addOrUpdateAdvanceControlProperties(advancedControllableProperties, statusControl);

		String brightness = groupLightResponse.getDimming().getBrightness();
		AdvancedControllableProperty sliderControlProperty = createControlSlider(PhilipsConstant.BRIGHTNESS, brightness, stats,
				String.valueOf(PhilipsConstant.MIN_END_BRIGHTNESS), String.valueOf(PhilipsConstant.MAX_END_BRIGHTNESS));
		addOrUpdateAdvanceControlProperties(advancedControllableProperties, sliderControlProperty);

		String colorTemperature = groupLightResponse.getTemperature().getMirek();
		AdvancedControllableProperty colorTemperatureControlProperty = createControlSlider(PhilipsConstant.COLOR_TEMPERATURE, colorTemperature, stats,
				String.valueOf(PhilipsConstant.MIN_COLOR_TEMPERATURE), String.valueOf(PhilipsConstant.MAX_COLOR_TEMPERATURE));
		addOrUpdateAdvanceControlProperties(advancedControllableProperties, colorTemperatureControlProperty);
	}

	/**
	 * This method is used to retrieve list of device statuses by sending GET request to https://{hue-bridge-ip}/clip/v2/resource/zigbee_connectivity/{zigbee_service_id}
	 *
	 * @param deviceID the deviceID is id of device
	 */
	private void retrieveDeviceStatus(String deviceID) {

		Objects.requireNonNull(deviceID);
		AggregatedDeviceResponse aggregatedDeviceResponse = listMetadataDevice.get(deviceID);
		try {
			String zigbeeId = PhilipsConstant.EMPTY_STRING;
			for (ServicesResponse service : aggregatedDeviceResponse.getServices()) {
				if (service.getType().equalsIgnoreCase(PhilipsConstant.ZIGBEE_CONNECTIVITY)) {
					zigbeeId = service.getId();
				}
			}
			String request = PhilipsUtil.getMonitorURL(PhilipsURL.ZIGBEE_CONNECTIVITY).concat(zigbeeId);
			ZigbeeConnectivityWrapper zigbeeConnectivity = doGet(request, ZigbeeConnectivityWrapper.class);
			if (zigbeeConnectivity != null) {
				boolean status = zigbeeConnectivity.getData()[0].getStatus().equalsIgnoreCase(PhilipsConstant.CONNECTED);
				String mac = zigbeeConnectivity.getData()[0].getMacAddress();
				aggregatedDeviceList.get(deviceID).setDeviceOnline(status);
				Map<String, String> newProperties = new HashMap<>();
				Map<String, String> oldProperties = aggregatedDeviceList.get(deviceID).getProperties();
				newProperties.putAll(oldProperties);
				newProperties.put(PhilipsConstant.MAC_ADDRESS, mac);
				aggregatedDeviceList.get(deviceID).setProperties(newProperties);
			} else {
				logger.error(String.format("%s zigbee connectivity is empty", aggregatedDeviceList.get(deviceID).getDeviceName()));
			}
		} catch (Exception e) {
			failedMonitoringDeviceIds.add(deviceID);
			logger.error(String.format("Error while retrieve %s zigbee connectivity info: %s ", aggregatedDeviceList.get(deviceID).getDeviceName(), e.getMessage()), e);
		}
	}

	/**
	 * push failed monitoring Device ID to priority in next pollingInterval
	 */
	private void pushFailedMonitoringDevicesIDToPriority() {
		if (!failedMonitoringDeviceIds.isEmpty()) {
			deviceIds = deviceIds.stream().filter(id -> !failedMonitoringDeviceIds.contains(id)).collect(Collectors.toSet());
			deviceIds.addAll(failedMonitoringDeviceIds);
			failedMonitoringDeviceIds.clear();
		}
	}

	/**
	 * calculating minimum of polling interval
	 */
	private int calculatingThreadQuantity() {
		if (aggregatedDeviceList.isEmpty()) {
			return PhilipsConstant.MIN_THREAD_QUANTITY;
		}
		if (aggregatedDeviceList.size() / localPollingInterval < PhilipsConstant.MAX_THREAD_QUANTITY * PhilipsConstant.MAX_DEVICE_QUANTITY_PER_THREAD) {
			return IntMath.divide(aggregatedDeviceList.size(), localPollingInterval * PhilipsConstant.MAX_DEVICE_QUANTITY_PER_THREAD, RoundingMode.CEILING);
		}
		return PhilipsConstant.MAX_THREAD_QUANTITY;
	}

	/**
	 * Calculating minimum value of polling interval
	 *
	 * @throws ResourceNotReachableException when get limit rate exceed error
	 */
	private int calculatingLocalPollingInterval() {

		try {
			int pollingIntervalValue = PhilipsConstant.MIN_POLLING_INTERVAL;
			if (StringUtils.isNotNullOrEmpty(pollingInterval)) {
				pollingIntervalValue = Integer.parseInt(pollingInterval);
			}

			int minPollingInterval = calculatingMinPollingInterval();
			if (pollingIntervalValue < minPollingInterval) {
				logger.error(String.format("invalid pollingInterval value, pollingInterval must greater than: %s", minPollingInterval));
				return minPollingInterval;
			}
			return pollingIntervalValue;
		} catch (Exception e) {
			throw new ResourceNotReachableException(String.format("Unexpected pollingInterval value: %s", pollingInterval));
		}
	}

	/**
	 * This method is used to populate polling interval
	 *
	 * @param stats store all statistics
	 */
	public void populatePollingInterval(Map<String, String> stats) {
		Integer minPollingInterval = calculatingMinPollingInterval();

		Long nextPollingInterval = System.currentTimeMillis() + localPollingInterval * 1000;
		Date date = new Date(nextPollingInterval);
		Format format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

		stats.put(PhilipsConstant.MIN_POLLING_INTERVAL_KEY, minPollingInterval.toString());
		stats.put(PhilipsConstant.MAX_POLLING_INTERVAL_KEY, format.format(date));
	}

	/**
	 * calculating minimum of polling interval
	 *
	 * @throws ResourceNotReachableException when get limit rate exceed error
	 */
	private int calculatingMinPollingInterval() {
		if (!aggregatedDeviceList.isEmpty()) {
			return IntMath.divide(aggregatedDeviceList.size()
					, PhilipsConstant.MAX_THREAD_QUANTITY * PhilipsConstant.MAX_DEVICE_QUANTITY_PER_THREAD
					, RoundingMode.CEILING);
		}
		return PhilipsConstant.MIN_POLLING_INTERVAL;
	}

	/**
	 * Handle cases are Add new Device, Repeat, Room, Zone
	 *
	 * @param property the property is property name
	 * @param value the value is value of property
	 * @param typeAndMapOfDevice is type instance in TypeOfDeviceEnum
	 * @param stats the stats are list of statistics
	 * @param advancedControllableProperties advancedControllableProperties is the list that store all controllable properties
	 * @return boolean type
	 */
	private boolean handleControlForAddingNewDevice(String property, String value, Map<String, Map<String, String>> typeAndMapOfDevice, Map<String, String> stats,
			List<AdvancedControllableProperty> advancedControllableProperties) {
		String[] propertyList = property.split(PhilipsConstant.HASH);
		String propertyGroup = propertyList[0];
		String key = propertyList[1];
		boolean isControlAddNew = false;
		if (!PhilipsConstant.REPEAT.equalsIgnoreCase(key) && key.contains(PhilipsConstant.REPEAT) && !AutomationEnum.REPEAT_ADD.getName().equals(key)) {
			List<String> repeatDayDropdown = Arrays.stream(RepeatDayEnum.values()).map(RepeatDayEnum::getName).collect(Collectors.toList());
			Map<String, String> repeatDay = repeatNameOfAutomationMap.get(propertyGroup);
			if (repeatDay == null) {
				repeatDay = new HashMap<>();
			}
			updateDeviceDropdownList(property, value, stats, advancedControllableProperties, repeatDayDropdown, repeatDay, PhilipsConstant.REPEAT_0);
			repeatNameOfAutomationMap.put(propertyGroup, repeatDay);
			isControlAddNew = true;
		}
		if (!PhilipsConstant.DEVICE.equalsIgnoreCase(key) && key.contains(PhilipsConstant.DEVICE) && !AutomationEnum.DEVICE_ADD.getName().equals(key)) {
			List<String> deviceDropdown = deviceNameAndDeviceIdZoneMap.keySet().stream().collect(Collectors.toList());
			Map<String, String> mapOfDevice = typeAndMapOfDevice.get(PhilipsConstant.DEVICE);
			updateDeviceDropdownList(property, value, stats, advancedControllableProperties, deviceDropdown, mapOfDevice, PhilipsConstant.DEVICE_0);
			isControlAddNew = true;
		}
		if (!PhilipsConstant.ROOM.equalsIgnoreCase(key) && key.contains(PhilipsConstant.ROOM) && !AutomationEnum.ROOM_ADD.getName().equals(key)) {
			List<String> deviceDropdown = roomNameAndIdMap.keySet().stream().collect(Collectors.toList());
			Map<String, String> mapOfDevice = typeAndMapOfDevice.get(PhilipsConstant.ROOM);
			updateDeviceDropdownList(property, value, stats, advancedControllableProperties, deviceDropdown, mapOfDevice, PhilipsConstant.ROOM_0);
			isControlAddNew = true;
		}
		if (!PhilipsConstant.ZONE.equalsIgnoreCase(key) && key.contains(PhilipsConstant.ZONE) && !AutomationEnum.ZONE_ADD.getName().equals(key)) {
			List<String> deviceDropdown = zoneNameAndIdMap.keySet().stream().collect(Collectors.toList());
			Map<String, String> mapOfDevice = typeAndMapOfDevice.get(PhilipsConstant.ZONE);
			updateDeviceDropdownList(property, value, stats, advancedControllableProperties, deviceDropdown, mapOfDevice, PhilipsConstant.ZONE_0);
			isControlAddNew = true;
		}
		return isControlAddNew;
	}

	/**
	 * Populate Control for automation
	 *
	 * @param property the property is property names
	 * @param value the value is value of property
	 * @param stats the stats are list of statistics
	 * @param advancedControllableProperties advancedControllableProperties is the list that store all controllable properties
	 */
	private void populateControlForAutomation(String property, String value, Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {
		String[] propertyList = property.split(PhilipsConstant.HASH);
		String propertyGroup = propertyList[0];
		String key = propertyList[1];
		boolean isCurrentEmergencyDelivery = false;
		if (isEmergencyDelivery) {
			isCurrentEmergencyDelivery = true;
		}
		isEmergencyDelivery = true;
		Map<String, Map<String, String>> typeAndMapOfDevice = automationAndTypeMapOfDeviceAndValue.get(propertyGroup);
		boolean isAddNewValue = handleControlForAddingNewDevice(property, value, typeAndMapOfDevice, stats, advancedControllableProperties);
		if (!isAddNewValue) {
			AutomationEnum automationEnum = EnumTypeHandler.getMetricOfEnumByName(AutomationEnum.class, key);
			Map<String, String> repeatDay = repeatNameOfAutomationMap.get(propertyGroup);
			if (repeatDay == null) {
				repeatDay = new HashMap<>();
			}
			switch (automationEnum) {
				case NAME:
				case TIME_HOUR:
				case TIME_CURRENT:
				case TIME_MINUTE:
				case END_BRIGHTNESS:
				case END_WITH:
				case STYLE:
				case FADE_DURATION_HOUR:
				case FADE_DURATION_MINUTE:
					updateValueForTheControllableProperty(property, value, stats, advancedControllableProperties);
					break;
				case REPEAT:
					updateValueForTheControllableProperty(property, value, stats, advancedControllableProperties);
					if (String.valueOf(PhilipsConstant.NUMBER_ONE).equals(value)) {
						stats.put(propertyGroup + PhilipsConstant.HASH + AutomationEnum.REPEAT_ADD.getName(), PhilipsConstant.EMPTY_STRING);
						advancedControllableProperties.add(createButton(propertyGroup + PhilipsConstant.HASH + AutomationEnum.REPEAT_ADD.getName(), PhilipsConstant.ADD, PhilipsConstant.ADDING, 0));
						List<String> dayDropdown = Arrays.stream(RepeatDayEnum.values()).map(RepeatDayEnum::getName).collect(Collectors.toList());
						addRepeatDayForCreateAutomation(property, stats, advancedControllableProperties, dayDropdown, repeatDay, PhilipsConstant.REPEAT);
					} else {
						String repeatAdd = propertyGroup + PhilipsConstant.HASH + AutomationEnum.REPEAT_ADD.getName();
						stats.remove(repeatAdd);
						advancedControllableProperties.removeIf(item -> item.getName().equals(repeatAdd));
						removeRepeatDayForCreateAutomation(property, stats, advancedControllableProperties, repeatDay);
					}
					break;
				case REPEAT_ADD:
					if (repeatDay == null) {
						repeatDay = new HashMap<>();
						initializeDeviceDropdown(repeatDay, PhilipsConstant.REPEAT, PhilipsConstant.MAXIMUM_REPEAT_DAY);
						repeatDay.put(PhilipsConstant.REPEAT_0, null);
					}
					addNewRepeatForAutomation(propertyGroup, stats, advancedControllableProperties, repeatDay);
					repeatNameOfAutomationMap.put(propertyGroup, repeatDay);
					break;
				case FADE_DURATION:
					value = String.valueOf(getValueByRange(PhilipsConstant.MIN_FADE_DURATION, PhilipsConstant.MAX_FADE_DURATION, value));
					updateValueForTheControllableProperty(property, value, stats, advancedControllableProperties);
					break;
				case ACTION:
					String name = propertyGroup.substring(propertyGroup.indexOf(PhilipsConstant.DASH) + 1);
					Optional<AutomationResponse> automationResponse = automationList.stream().filter(item -> item.getMetaData().getName().equals(name)).findFirst();
					automationResponse.ifPresent(response -> sendRequestToDeleteAutomation(response.getId()));
					isEmergencyDelivery = false;
					break;
				case CANCEL:
					isEmergencyDelivery = false;
					break;
				case DEVICE_ADD:
					typeAndMapOfDevice = automationAndTypeMapOfDeviceAndValue.get(propertyGroup);
					List<String> deviceDropdown = deviceNameAndDeviceIdZoneMap.keySet().stream().collect(Collectors.toList());
					Map<String, String> mapOfDevice = typeAndMapOfDevice.get(PhilipsConstant.DEVICE);
					if (mapOfDevice == null) {
						mapOfDevice = new HashMap<>();
						initializeDeviceDropdown(mapOfDevice, PhilipsConstant.DEVICE, deviceNameAndDeviceIdZoneMap.size());
						typeAndMapOfDevice.put(PhilipsConstant.DEVICE, mapOfDevice);
					}
					addNewDeviceForRoomAndZone(propertyGroup, stats, advancedControllableProperties, deviceDropdown, mapOfDevice);
					break;
				case ROOM_ADD:
					typeAndMapOfDevice = automationAndTypeMapOfDeviceAndValue.get(propertyGroup);
					Map<String, String> mapOfRoom = typeAndMapOfDevice.get(PhilipsConstant.ROOM);
					if (mapOfRoom == null) {
						mapOfRoom = new HashMap<>();
						initializeDeviceDropdown(mapOfRoom, PhilipsConstant.ROOM, roomNameAndIdMap.size());
						typeAndMapOfDevice.put(PhilipsConstant.ROOM, mapOfRoom);
					}
					addRoomAndZoneForAutomation(propertyGroup, stats, advancedControllableProperties, roomNameAndIdMap, mapOfRoom);
					break;
				case ZONE_ADD:
					typeAndMapOfDevice = automationAndTypeMapOfDeviceAndValue.get(propertyGroup);
					Map<String, String> mapOfZone = typeAndMapOfDevice.get(PhilipsConstant.ZONE);
					if (mapOfZone == null) {
						mapOfZone = new HashMap<>();
						initializeDeviceDropdown(mapOfZone, PhilipsConstant.ZONE, zoneNameAndIdMap.size());
						typeAndMapOfDeviceAndValue.put(PhilipsConstant.ZONE, mapOfZone);
					}
					addRoomAndZoneForAutomation(propertyGroup, stats, advancedControllableProperties, zoneNameAndIdMap, mapOfZone);
					break;
				case TYPE:
					updateValueForTheControllableProperty(property, value, stats, advancedControllableProperties);
					TypeOfDeviceEnum type = EnumTypeHandler.getMetricOfEnumByName(TypeOfDeviceEnum.class, value);
					switch (type) {
						case DEVICE:
						case ROOM:
						case ZONE:
							updateDeviceTypeForCreateAutomationByType(property, stats, advancedControllableProperties, type, typeAndMapOfDevice);
							break;
						default:
							if (logger.isDebugEnabled()) {
								logger.debug(String.format("Creating automation with device type %s is not supported.", automationEnum.getName()));
							}
					}
					break;
				case DELETE:
				case CREATE:
					if (logger.isDebugEnabled()) {
						logger.debug(String.format("Controlling automation by name %s is not supported.", automationEnum.getName()));
					}
					break;
				case APPLY_CHANGE:
					AutomationResponse automationRequest = convertAutomationByValues(propertyGroup, stats, typeAndMapOfDevice, repeatDay);
					sendRequestToCreateAutomation(automationRequest, true);
					isEmergencyDelivery = false;
					break;
				case STATUS:
					String status = PhilipsConstant.TRUE;
					if (String.valueOf(PhilipsConstant.ZERO).equals(value)) {
						status = PhilipsConstant.FALSE;
					}
					String group = propertyGroup.substring(propertyGroup.indexOf(PhilipsConstant.DASH) + 1);
					Optional<AutomationResponse> automation = automationList.stream().filter(item -> item.getMetaData().getName().equals(group)).findFirst();
					if (automation.isPresent()) {
						sendRequestToChangeStatusForAutomation(automation.get().getId(), String.format(PhilipsConstant.PARAM_CHANGE_STATUS_AUTOMATION, status.toLowerCase(Locale.ROOT)));
					}
					updateValueForTheControllableProperty(property, value, stats, advancedControllableProperties);
					isEmergencyDelivery = isCurrentEmergencyDelivery;
					break;
				default:
					if (logger.isDebugEnabled()) {
						logger.debug(String.format("Controlling automation by name %s is not supported.", automationEnum.getName()));
					}
					break;
			}
		}
		if (isEmergencyDelivery) {
			stats.put(propertyGroup + PhilipsConstant.HASH + RoomsAndZonesControlEnum.APPLY_CHANGE.getName(), PhilipsConstant.EMPTY_STRING);
			advancedControllableProperties.add(createButton(propertyGroup + PhilipsConstant.HASH + RoomsAndZonesControlEnum.APPLY_CHANGE.getName(), PhilipsConstant.APPLY, PhilipsConstant.APPLYING, 0));
		}
		populateCancelChangeButton(stats, advancedControllableProperties, propertyGroup, isEmergencyDelivery);
	}

	/**
	 * Send request to delete automation
	 *
	 * @param id the id is id of automation
	 */
	private void sendRequestToDeleteAutomation(String id) {
		try {
			doDelete(PhilipsUtil.getMonitorURL(PhilipsURL.AUTOMATION) + PhilipsConstant.SLASH + id);
		} catch (Exception e) {
			throw new ResourceNotReachableException("Can't delete automation error during delete: " + e.getMessage(), e);
		}
	}

	/**
	 * Send request to change status for automation
	 *
	 * @param id the id is id of automation
	 * @param requestBody the requestBody is value of status
	 */
	private void sendRequestToChangeStatusForAutomation(String id, String requestBody) {
		try {
			ResponseData responseData = doPut(PhilipsUtil.getMonitorURL(PhilipsURL.AUTOMATION) + PhilipsConstant.SLASH + id, requestBody, ResponseData.class);
			if (responseData.getData() == null) {
				throw new ResourceNotReachableException("Error while control:" + Arrays.stream(responseData.getErrors()).map(ErrorsResponse::getDescription));
			}
		} catch (Exception e) {
			throw new ResourceNotReachableException("Can't change status for automation: " + e.getMessage(), e);
		}
	}

	/**
	 * Update statistics and advancedControllableProperties by value
	 *
	 * @param stats the stats are list of statistics
	 * @param advancedControllableProperties advancedControllableProperties is the list that store all controllable properties
	 * @param localCreateExtended is instance in ExtendedStatistics
	 * @param localCreatStats is instance in ExtendedStatistics
	 */
	private void updateLocalExtendedByValue(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties, ExtendedStatistics localCreateExtended,
			Map<String, String> localCreatStats, boolean isActionCreate) {

		if (isConfigManagement) {
			// add all stats of create room/zone into local stats
			Map<String, String> localRoomStats = localCreateExtended.getStatistics();
			stats.keySet().removeIf(localCreatStats::containsKey);
			stats.putAll(localRoomStats);

			List<AdvancedControllableProperty> localCreateRoomControl = localCreateExtended.getControllableProperties();
			advancedControllableProperties.removeIf(item -> localCreatStats.containsKey(item.getName()));
			advancedControllableProperties.addAll(localCreateRoomControl);
			if (!isActionCreate) {
				localCreatStats.clear();
			}
		}
	}

	/**
	 * Create default a automation
	 *
	 * @param stats the stats are list of statistics
	 * @param createAutomationControllableProperties advancedControllableProperties is the list that store all controllable properties
	 */
	private void createAutomation(Map<String, String> stats, List<AdvancedControllableProperty> createAutomationControllableProperties) {
		String group = PhilipsConstant.CREATE_AUTOMATION + PhilipsConstant.HASH;
		for (AutomationEnum automationEnum : AutomationEnum.values()) {
			String property = group + automationEnum.getName();
			switch (automationEnum) {
				case ACTION:
					stats.put(property, PhilipsConstant.EMPTY_STRING);
					createAutomationControllableProperties.add(createButton(property, PhilipsConstant.CREATE, PhilipsConstant.CREATING, 0L));
					break;
				case NAME:
					stats.put(property, PhilipsConstant.EMPTY_STRING);
					createAutomationControllableProperties.add(createText(property, PhilipsConstant.EMPTY_STRING));
					break;
				case STATUS:
					stats.put(property, PhilipsConstant.EMPTY_STRING);
					createAutomationControllableProperties.add(controlSwitch(stats, property, String.valueOf(PhilipsConstant.NUMBER_ONE), PhilipsConstant.OFFLINE, PhilipsConstant.ONLINE));
					break;
				case TYPE:
					String[] typeDropdown = EnumTypeHandler.getEnumNames(TypeOfDeviceEnum.class);
					AdvancedControllableProperty typeControlProperty = controlDropdown(stats, typeDropdown, property, TypeOfDeviceEnum.DEVICE.getName());
					addOrUpdateAdvanceControlProperties(createAutomationControllableProperties, typeControlProperty);
					updateDeviceTypeForCreateAutomationByType(property, stats, createAutomationControllableProperties, TypeOfDeviceEnum.DEVICE, typeAndMapOfDeviceAndValue);
					break;
				case TYPE_OF_AUTOMATION:
					String[] typeOfAutomationDropdown = EnumTypeHandler.getEnumNames(TypeOfAutomation.class);
					AdvancedControllableProperty typeOfAutomationControlProperty = controlDropdown(stats, typeOfAutomationDropdown, property, TypeOfAutomation.TIMER.getName());
					addOrUpdateAdvanceControlProperties(createAutomationControllableProperties, typeOfAutomationControlProperty);
					break;
				case FADE_DURATION_HOUR:
					String[] hourDropdown = EnumTypeHandler.getEnumNames(TimerHourEnum.class);
					AdvancedControllableProperty hourControlProperty = controlDropdown(stats, hourDropdown, property, TimerHourEnum.TIME_00.getName());
					addOrUpdateAdvanceControlProperties(createAutomationControllableProperties, hourControlProperty);
					break;
				case FADE_DURATION_MINUTE:
					String[] timeMinuteDropdown = EnumTypeHandler.getEnumNames(TimeMinuteEnum.class);
					AdvancedControllableProperty timeMinuteControlProperty = controlDropdown(stats, timeMinuteDropdown, property, TimeMinuteEnum.MINUTE_00.getName());
					addOrUpdateAdvanceControlProperties(createAutomationControllableProperties, timeMinuteControlProperty);
					break;
				case FADE_DURATION:
				case TIME_CURRENT:
				case REPEAT:
				case REPEAT_ADD:
				case TIME_HOUR:
				case TIME_MINUTE:
					if (logger.isDebugEnabled()) {
						logger.debug(String.format("Controlling create automation by name %s is not supported.", automationEnum.getName()));
					}
					break;
				default:
					if (logger.isDebugEnabled()) {
						logger.debug(String.format("Controlling create automation by name %s is not supported.", automationEnum.getName()));
					}
			}
		}
		stats.put(group + PhilipsConstant.EDITED, PhilipsConstant.FALSE);
	}

	/**
	 * Clear data before fetching data
	 */
	private void clearBeforeFetchingData() {
		bridgeIdList.clear();
		roomList.clear();
		deviceRoomControlMap.clear();
		zoneList.clear();
		roomList.clear();
		groupLightList.clear();
		groupLightMap.clear();
		deviceRoomControlMap.clear();
		deviceNameAndMapDeviceIdOfRoomMap.clear();
		deviceNameAndDeviceIdZoneMap.clear();
		allDeviceIdAndNameMap.clear();
		deviceExitsInRoomMap.clear();
		automationList.clear();
		if (automationAndTypeMapOfDeviceAndValue != null) {
			automationAndTypeMapOfDeviceAndValue.clear();
		}
		roomNameAndIdMap.clear();
		zoneNameAndIdMap.clear();
		repeatNameOfAutomationMap.clear();
	}

	/**
	 * Populate control for room, zone, and automation
	 *
	 * @param stats the stats are list of statistics
	 * @param advancedControllableProperties advancedControllableProperties is the list that store all controllable properties
	 */
	private void populateControlForAggregator(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {
		if (isConfigManagement) {
			List<RoomAndZoneResponse> newListRoom = roomList;
			if (StringUtils.isNotNullOrEmpty(roomNames)) {
				newListRoom = roomListAfterFilter;
			}
			List<RoomAndZoneResponse> newListZone = zoneList;
			if (StringUtils.isNotNullOrEmpty(zoneName)) {
				newListZone = zoneListAfterFilter;
			}
			for (RoomAndZoneResponse roomItem : newListRoom) {
				String groupName = PhilipsConstant.ROOM + PhilipsConstant.DASH + roomItem.getMetaData().getName();
				populatePropertiesRoomAndZone(stats, advancedControllableProperties, roomItem, groupName, true);
			}
			for (RoomAndZoneResponse zoneItem : newListZone) {
				String groupName = PhilipsConstant.ZONE + PhilipsConstant.DASH + zoneItem.getMetaData().getName();
				populatePropertiesRoomAndZone(stats, advancedControllableProperties, zoneItem, groupName, false);
			}
		}

		for (AutomationResponse automationResponse : automationList) {
			String name = idAndNameOfAutomationMap.get(automationResponse.getScriptId());
			//Support three type are Timer, Go To Sleeps, and Wake up with light
			TypeOfAutomation type = TypeOfAutomation.TIMER;
			if (PhilipsConstant.TIMERS.equals(name) || PhilipsConstant.WAKE_UP_WITH_LIGHT.equals(name) || PhilipsConstant.GO_TO_SLEEPS.equals(name)) {
				if (PhilipsConstant.WAKE_UP_WITH_LIGHT.equals(name)) {
					type = TypeOfAutomation.WAKE_UP_WITH_LIGHT;
				}
				if (PhilipsConstant.GO_TO_SLEEPS.equals(name)) {
					type = TypeOfAutomation.GO_TO_SLEEP;
				}
				if (isConfigManagement) {
					populatePropertiesForAutomation(stats, advancedControllableProperties, automationResponse, type);
				} else {
					int status = PhilipsConstant.ZERO;
					String value = automationResponse.getEnabled();
					if (PhilipsConstant.TRUE.equalsIgnoreCase(value)) {
						status = PhilipsConstant.NUMBER_ONE;
					}
					String property = PhilipsConstant.AUTOMATION + type.getName() + PhilipsConstant.DASH + automationResponse.getMetaData().getName() + PhilipsConstant.HASH + AutomationEnum.STATUS.getName();
					stats.put(property, String.valueOf(status));
					advancedControllableProperties.add(controlSwitch(stats, property, String.valueOf(status), PhilipsConstant.OFFLINE, PhilipsConstant.ONLINE));
				}
			}
		}
	}

	/**
	 * Populate properties for automation
	 *
	 * @param stats the stats are list of statistics
	 * @param advancedControllableProperties advancedControllableProperties is the list that store all controllable properties
	 * @param automationData the automationData is Automation Response instance
	 * @param type the type is type of the device instance in TypeOfDeviceEnum
	 */
	private void populatePropertiesForAutomation(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties, AutomationResponse automationData, TypeOfAutomation type) {
		String groupName = PhilipsConstant.AUTOMATION + type.getName() + PhilipsConstant.DASH + automationData.getMetaData().getName();
		for (AutomationEnum automation : AutomationEnum.values()) {
			String value;
			String autoName = automationData.getMetaData().getName();
			String property = groupName + PhilipsConstant.HASH + automation.getName();
			switch (automation) {
				case CANCEL:
				case CREATE:
				case TIME_CURRENT:
				case STYLE:
				case END_BRIGHTNESS:
				case DEVICE_ADD:
				case ROOM_ADD:
				case ZONE_ADD:
				case REPEAT_ADD:
				case FADE_DURATION_HOUR:
				case FADE_DURATION_MINUTE:
				case DELETE:
				case TIME_HOUR:
				case TIME_MINUTE:
				case END_WITH:
				case APPLY_CHANGE:
				case FADE_DURATION:
					break;
				case NAME:
					stats.put(property, autoName);
					advancedControllableProperties.add(createText(property, autoName));
					break;
				case STATUS:
					int status = PhilipsConstant.ZERO;
					value = automationData.getEnabled();
					if (PhilipsConstant.TRUE.equalsIgnoreCase(value)) {
						status = PhilipsConstant.NUMBER_ONE;
					}
					stats.put(property, PhilipsConstant.EMPTY_STRING);
					advancedControllableProperties.add(controlSwitch(stats, property, String.valueOf(status), PhilipsConstant.OFFLINE, PhilipsConstant.ONLINE));
					break;
				case REPEAT:
					if ((TypeOfAutomation.GO_TO_SLEEP.getName().equalsIgnoreCase(type.getName()) || TypeOfAutomation.WAKE_UP_WITH_LIGHT.getName().equalsIgnoreCase(type.getName()))
							&& automationData.getConfigurations().getTimeAndRepeats() != null) {
						String[] days = new String[0];
						if (automationData.getConfigurations().getTimeAndRepeats().getDays() != null) {
							Map<String, String> dayMap = new HashMap<>();
							initializeDeviceDropdown(dayMap, PhilipsConstant.REPEAT, RepeatDayEnum.values().length);
							int dayIndex = 0;
							days = automationData.getConfigurations().getTimeAndRepeats().getDays();
							for (String day : days) {
								day = day.substring(0, 1).toUpperCase(Locale.ROOT) + day.substring(1);
								dayMap.put(PhilipsConstant.REPEAT + dayIndex, day);
								dayIndex++;
							}
							repeatNameOfAutomationMap.put(groupName, dayMap);
							List<String> dayDropdown = Arrays.stream(RepeatDayEnum.values()).map(RepeatDayEnum::getName).collect(Collectors.toList());
							addRepeatDayForCreateAutomation(property, stats, advancedControllableProperties, dayDropdown, dayMap, PhilipsConstant.REPEAT);

							String repeatAdd = groupName + PhilipsConstant.HASH + AutomationEnum.REPEAT_ADD.getName();
							stats.put(repeatAdd, PhilipsConstant.EMPTY_STRING);
							advancedControllableProperties.add(createButton(repeatAdd, PhilipsConstant.ADD, PhilipsConstant.ADDING, 0));
						}

						int repeatValue = PhilipsConstant.ZERO;
						if (days.length > 0) {
							repeatValue = PhilipsConstant.NUMBER_ONE;
						}
						stats.put(property, String.valueOf(repeatValue));
						advancedControllableProperties.add(controlSwitch(stats, property, String.valueOf(repeatValue), PhilipsConstant.DISABLE, PhilipsConstant.ENABLE));
					}
					break;
				case ACTION:
					stats.put(property, PhilipsConstant.EMPTY_STRING);
					advancedControllableProperties.add(createButton(property, PhilipsConstant.DELETE, PhilipsConstant.DELETING, 0));
					break;
				case TYPE:
					Location[] locationArray = automationData.getConfigurations().getLocation();
					int zoneIndex = 0;
					populateDeviceForAutomationByType(stats, advancedControllableProperties, groupName, property, locationArray, zoneIndex);
					break;
				case TYPE_OF_AUTOMATION:
					stats.put(property, type.getName());
					switch (type) {
						case GO_TO_SLEEP:
							String fadeDuration = groupName + PhilipsConstant.HASH + AutomationEnum.FADE_DURATION.getName();
							String fadeOutDuration = automationData.getConfigurations().getFadeOutDuration().getSeconds();
							AdvancedControllableProperty nameControllableProperty = controlTextOrNumeric(stats, fadeDuration, fadeOutDuration, true);
							addOrUpdateAdvanceControlProperties(advancedControllableProperties, nameControllableProperty);

							String[] endWithDropdown = EnumTypeHandler.getEnumNames(EndStateEnum.class);
							value = EndStateEnum.getNameOfEnumByValue(automationData.getConfigurations().getEndWith());
							String endWith = groupName + PhilipsConstant.HASH + AutomationEnum.END_WITH.getName();
							AdvancedControllableProperty roomTypeControlProperty = controlDropdown(stats, endWithDropdown, endWith, value);
							addOrUpdateAdvanceControlProperties(advancedControllableProperties, roomTypeControlProperty);

							String[] hourDropdown = EnumTypeHandler.getEnumNames(TimeHourEnum.class);
							String hourValue = automationData.getConfigurations().getTimeAndRepeats().getTimePoint().getTimes().getHour();
							String hourKey = groupName + PhilipsConstant.HASH + AutomationEnum.TIME_HOUR.getName();
							AdvancedControllableProperty hourControlProperty = controlDropdown(stats, hourDropdown, hourKey, convertTimeFormat(Integer.parseInt(hourValue)));
							addOrUpdateAdvanceControlProperties(advancedControllableProperties, hourControlProperty);

							String[] minuteDropdown = EnumTypeHandler.getEnumNames(TimeMinuteEnum.class);
							value = automationData.getConfigurations().getTimeAndRepeats().getTimePoint().getTimes().getMinute();
							if (Integer.parseInt(value) < 10) {
								value = PhilipsConstant.ZERO + value;
							}
							String minuteKey = groupName + PhilipsConstant.HASH + AutomationEnum.TIME_MINUTE.getName();
							AdvancedControllableProperty minuteControlProperty = controlDropdown(stats, minuteDropdown, minuteKey, value);
							addOrUpdateAdvanceControlProperties(advancedControllableProperties, minuteControlProperty);
							int currentTime = PhilipsConstant.ZERO;
							if (Integer.parseInt(hourValue) >= 12) {
								currentTime = PhilipsConstant.NUMBER_ONE;
							}
							String timeCurrent = groupName + PhilipsConstant.HASH + AutomationEnum.TIME_CURRENT.getName();
							stats.put(timeCurrent, String.valueOf(currentTime));
							advancedControllableProperties.add(controlSwitch(stats, timeCurrent, String.valueOf(currentTime), PhilipsConstant.TIME_AM, PhilipsConstant.TIME_PM));
							break;
						case WAKE_UP_WITH_LIGHT:
							String fadeInDurationKey = groupName + PhilipsConstant.HASH + AutomationEnum.FADE_DURATION.getName();
							String fadeInDurationValue = automationData.getConfigurations().getFadeInDuration().getSeconds();
							nameControllableProperty = controlTextOrNumeric(stats, fadeInDurationKey, fadeInDurationValue, true);
							addOrUpdateAdvanceControlProperties(advancedControllableProperties, nameControllableProperty);

							String[] styleDropdown = EnumTypeHandler.getEnumNames(StyleEnum.class);
							value = StyleEnum.getNameOfEnumByValue(automationData.getConfigurations().getStyle());
							String style = groupName + PhilipsConstant.HASH + AutomationEnum.STYLE.getName();
							AdvancedControllableProperty styleControlProperty = controlDropdown(stats, styleDropdown, style, value);
							addOrUpdateAdvanceControlProperties(advancedControllableProperties, styleControlProperty);

							String endBrightness = groupName + PhilipsConstant.HASH + AutomationEnum.END_BRIGHTNESS.getName();
							value = automationData.getConfigurations().getEndBrightness();
							AdvancedControllableProperty sliderControlProperty = createControlSlider(endBrightness, value, stats,
									String.valueOf(PhilipsConstant.MIN_END_BRIGHTNESS), String.valueOf(PhilipsConstant.MAX_END_BRIGHTNESS));
							addOrUpdateAdvanceControlProperties(advancedControllableProperties, sliderControlProperty);

							hourDropdown = EnumTypeHandler.getEnumNames(TimerHourEnum.class);
							hourValue = automationData.getConfigurations().getTimeAndRepeats().getTimePoint().getTimes().getHour();
							hourKey = groupName + PhilipsConstant.HASH + AutomationEnum.TIME_HOUR.getName();
							hourControlProperty = controlDropdown(stats, hourDropdown, hourKey, convertTimeFormat(Integer.parseInt(hourValue)));
							addOrUpdateAdvanceControlProperties(advancedControllableProperties, hourControlProperty);

							minuteDropdown = EnumTypeHandler.getEnumNames(TimeMinuteEnum.class);
							value = automationData.getConfigurations().getTimeAndRepeats().getTimePoint().getTimes().getMinute();
							minuteKey = groupName + PhilipsConstant.HASH + AutomationEnum.TIME_MINUTE.getName();
							minuteControlProperty = controlDropdown(stats, minuteDropdown, minuteKey, convertTimeFormat(Integer.parseInt(value)));
							addOrUpdateAdvanceControlProperties(advancedControllableProperties, minuteControlProperty);
							currentTime = PhilipsConstant.ZERO;
							if (Integer.parseInt(hourValue) >= 12) {
								currentTime = PhilipsConstant.NUMBER_ONE;
							}
							timeCurrent = groupName + PhilipsConstant.HASH + AutomationEnum.TIME_CURRENT.getName();
							stats.put(timeCurrent, String.valueOf(currentTime));
							advancedControllableProperties.add(controlSwitch(stats, timeCurrent, String.valueOf(currentTime), PhilipsConstant.TIME_AM, PhilipsConstant.TIME_PM));
							break;
						case TIMER:
							fadeDuration = automationData.getConfigurations().getDuration().getSeconds();
							int fadeDurationHour = Integer.parseInt(fadeDuration);
							int hour = fadeDurationHour / 3600;
							int minute = fadeDurationHour % 60;
							hourDropdown = EnumTypeHandler.getEnumNames(TimerHourEnum.class);
							String[] timeMinuteDropdown = EnumTypeHandler.getEnumNames(TimeMinuteEnum.class);
							String fadeDurationHourKey = groupName + PhilipsConstant.HASH + AutomationEnum.FADE_DURATION_HOUR.getName();
							String fadeDurationMinuteKey = groupName + PhilipsConstant.HASH + AutomationEnum.FADE_DURATION_MINUTE.getName();

							hourControlProperty = controlDropdown(stats, hourDropdown, fadeDurationHourKey, convertTimeFormat(hour));
							addOrUpdateAdvanceControlProperties(advancedControllableProperties, hourControlProperty);

							AdvancedControllableProperty timeMinuteControlProperty = controlDropdown(stats, timeMinuteDropdown, fadeDurationMinuteKey, convertTimeFormat(minute));
							addOrUpdateAdvanceControlProperties(advancedControllableProperties, timeMinuteControlProperty);
							break;
						default:
							if (logger.isDebugEnabled()) {
								logger.debug(String.format("Controlling create automation automation type %s is not supported.", automation.getName()));
							}
							break;
					}
					break;
				default:
					if (logger.isDebugEnabled()) {
						logger.debug(String.format("Controlling automation behavior instance with property %s is not supported.", automation.getName()));
					}
					break;
			}
			stats.put(groupName + PhilipsConstant.HASH + PhilipsConstant.EDITED, PhilipsConstant.FALSE);
		}
	}

	/**
	 * Populate add list device for automation by type in TypeOfDeviceEnum
	 *
	 * @param stats the stats are list of statistics
	 * @param advancedControllableProperties advancedControllableProperties is the list that store all controllable properties
	 * @param groupName the property name with format Automation-{AutomationName}
	 * @param property the property is property name
	 * @param locationArray is Array list with Location DTO instance
	 * @param zoneIndex the zoneIndex is index of zone zone0, zone1,... etc
	 */
	private void populateDeviceForAutomationByType(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties, String groupName, String property,
			Location[] locationArray, int zoneIndex) {
		int deviceIndex = 0;
		Map<String, Map<String, String>> typeDevice = new HashMap<>();
		typeDevice.put(PhilipsConstant.DEVICE, new HashMap<>());
		typeDevice.put(PhilipsConstant.ROOM, new HashMap<>());
		typeDevice.put(PhilipsConstant.ZONE, new HashMap<>());
		for (Location locationItem : locationArray) {
			Map<String, String> device = new HashMap<>();
			//handle case value is device
			List<String> deviceOfRoomMapCopy = new LinkedList<>();
			deviceOfRoomMapCopy.addAll(roomNameAndIdMap.keySet().stream().collect(Collectors.toList()));
			List<String> deviceOfZoneMapCopy = zoneNameAndIdMap.keySet().stream().collect(Collectors.toList());
			if (locationItem.getGroup() != null && locationItem.getItems() != null) {
				for (Group groupItem : locationItem.getItems()) {
					Map<String, String> data = deviceNameAndMapDeviceIdOfRoomMap.values().stream().filter(item -> item.containsValue(groupItem.getId())).findFirst().orElse(new HashMap<>());
					if (deviceIndex == 0) {
						initializeDeviceDropdown(device, PhilipsConstant.DEVICE, deviceNameAndDeviceIdZoneMap.values().size());
					}
					if (!data.isEmpty()) {
						String values = data.toString();
						Entry<String, String> nameDevice = deviceNameAndDeviceIdZoneMap.entrySet().stream().filter(item -> values.contains(item.getValue())).findFirst().orElse(null);
						if (nameDevice != null) {
							device.put(PhilipsConstant.DEVICE + deviceIndex, nameDevice.getKey());
							deviceIndex++;
						}
					}
				}
				Map<String, String> deviceMapList = typeDevice.get(PhilipsConstant.DEVICE);
				if (!deviceMapList.isEmpty()) {
					deviceMapList.putAll(device);
					typeDevice.put(PhilipsConstant.DEVICE, deviceMapList);
				} else {
					typeDevice.put(PhilipsConstant.DEVICE, device);
				}
				automationAndTypeMapOfDeviceAndValue.put(groupName, typeDevice);
				List<String> deviceOfMap = deviceNameAndDeviceIdZoneMap.keySet().stream().collect(Collectors.toList());
				addRepeatDayForCreateAutomation(property, stats, advancedControllableProperties, deviceOfMap, device, PhilipsConstant.DEVICE);

				String deviceAdd = groupName + PhilipsConstant.HASH + AutomationEnum.DEVICE_ADD.getName();
				stats.put(deviceAdd, PhilipsConstant.EMPTY_STRING);
				advancedControllableProperties.add(createButton(deviceAdd, PhilipsConstant.ADD, PhilipsConstant.ADDING, 0));

				String[] typeDropdown = EnumTypeHandler.getEnumNames(TypeOfDeviceEnum.class);
				AdvancedControllableProperty typeControlProperty = controlDropdown(stats, typeDropdown, property, PhilipsConstant.DEVICE);
				addOrUpdateAdvanceControlProperties(advancedControllableProperties, typeControlProperty);
			} else if (locationItem.getGroup() != null && locationItem.getItems() == null) {

				//Handle case value is bridge home
				if (PhilipsConstant.BRIDGE_HOME.equalsIgnoreCase(locationItem.getGroup().getType())) {
					deviceIndex = 0;
					for (String value : deviceNameAndDeviceIdZoneMap.keySet()) {
						if (PhilipsConstant.NONE.equals(value)) {
							continue;
						}
						device.put(PhilipsConstant.DEVICE + deviceIndex, value);
						deviceIndex++;
					}
					typeDevice.put(PhilipsConstant.DEVICE, device);
					automationAndTypeMapOfDeviceAndValue.put(groupName, typeDevice);
					String deviceAdd = groupName + PhilipsConstant.HASH + AutomationEnum.DEVICE_ADD.getName();
					stats.put(deviceAdd, PhilipsConstant.EMPTY_STRING);
					advancedControllableProperties.add(createButton(deviceAdd, PhilipsConstant.ADD, PhilipsConstant.ADDING, 0));
					List<String> deviceOfMap = deviceNameAndDeviceIdZoneMap.keySet().stream().collect(Collectors.toList());
					addRepeatDayForCreateAutomation(property, stats, advancedControllableProperties, deviceOfMap, device, PhilipsConstant.DEVICE);

					String[] typeDropdown = EnumTypeHandler.getEnumNames(TypeOfDeviceEnum.class);
					AdvancedControllableProperty typeControlProperty = controlDropdown(stats, typeDropdown, property, PhilipsConstant.DEVICE);
					addOrUpdateAdvanceControlProperties(advancedControllableProperties, typeControlProperty);
				}
				//handle case value is room
				if (PhilipsConstant.ROOM.equalsIgnoreCase(locationItem.getGroup().getType())) {
					String roomId = locationItem.getGroup().getId();
					Entry<String, String> deviceNameInRoom = roomNameAndIdMap.entrySet().stream().filter(item -> item.getValue().equals(roomId)).findFirst().orElse(null);
					if (zoneIndex == 0) {
						initializeDeviceDropdown(device, PhilipsConstant.ROOM, roomNameAndIdMap.values().size());
					}
					if (deviceNameInRoom != null) {
						device.put(PhilipsConstant.ROOM + zoneIndex, deviceNameInRoom.getKey());
						zoneIndex++;
					} else {
						RoomAndZoneResponse deviceNameNotExitsInRoom = roomList.stream().filter(item -> item.getId().equals(roomId)).findFirst().orElse(null);
						if (deviceNameNotExitsInRoom != null) {
							String roomNameFormat = PhilipsConstant.ROOM_NO_ASSIGNED_DEVICE + PhilipsConstant.DASH + deviceNameNotExitsInRoom.getMetaData().getName();
							device.put(PhilipsConstant.ROOM + zoneIndex, roomNameFormat);
							deviceOfRoomMapCopy.add(roomNameFormat);
							zoneIndex++;
						} else {
							deviceNameNotExitsInRoom = zoneList.stream().filter(item -> item.getId().equals(roomId)).findFirst().orElse(null);
							if (deviceNameNotExitsInRoom != null) {
								String roomNameFormat = PhilipsConstant.ZONE_NO_ASSIGNED_DEVICE + PhilipsConstant.DASH + deviceNameNotExitsInRoom.getMetaData().getName();
								device.put(PhilipsConstant.ROOM + zoneIndex, roomNameFormat);
								deviceOfRoomMapCopy.add(roomNameFormat);
								zoneIndex++;
							}
						}
					}
					typeDevice.put(PhilipsConstant.ROOM, device);
					automationAndTypeMapOfDeviceAndValue.put(groupName, typeDevice);
					addRepeatDayForCreateAutomation(property, stats, advancedControllableProperties, deviceOfRoomMapCopy, device, PhilipsConstant.ROOM);

					String roomAdd = groupName + PhilipsConstant.HASH + AutomationEnum.ROOM_ADD.getName();
					stats.put(roomAdd, PhilipsConstant.EMPTY_STRING);
					advancedControllableProperties.add(createButton(roomAdd, PhilipsConstant.ADD, PhilipsConstant.ADDING, 0));
					String[] typeDropdown = EnumTypeHandler.getEnumNames(TypeOfDeviceEnum.class);
					AdvancedControllableProperty typeControlProperty = controlDropdown(stats, typeDropdown, property, PhilipsConstant.ROOM);
					addOrUpdateAdvanceControlProperties(advancedControllableProperties, typeControlProperty);
				}

				//handle case value is device
				if (PhilipsConstant.ZONE.equalsIgnoreCase(locationItem.getGroup().getType())) {
					String zoneId = locationItem.getGroup().getId();
					Optional<Entry<String, String>> deviceNameInZone = zoneNameAndIdMap.entrySet().stream().filter(item -> item.getValue().equals(zoneId)).findFirst();
					if (zoneIndex == 0) {
						initializeDeviceDropdown(device, PhilipsConstant.ZONE, zoneNameAndIdMap.values().size());
					}
					if (deviceNameInZone.isPresent()) {
						device.put(PhilipsConstant.ZONE + zoneIndex, deviceNameInZone.get().getKey());
						zoneIndex++;
					} else {
						Optional<RoomAndZoneResponse> deviceNameNotExitsInZone = zoneList.stream().filter(item -> item.getId().equals(zoneId)).findFirst();
						if (deviceNameNotExitsInZone.isPresent()) {
							String roomNameFormat = PhilipsConstant.ZONE_NO_ASSIGNED_DEVICE + PhilipsConstant.DASH + deviceNameNotExitsInZone.get().getMetaData().getName();
							device.put(PhilipsConstant.ZONE + zoneIndex, roomNameFormat);
							deviceOfZoneMapCopy.add(roomNameFormat);
							zoneIndex++;
						}
					}
					typeDevice.put(PhilipsConstant.ZONE, device);
					automationAndTypeMapOfDeviceAndValue.put(groupName, typeDevice);
					addRepeatDayForCreateAutomation(property, stats, advancedControllableProperties, deviceOfZoneMapCopy, device, PhilipsConstant.ZONE);

					String zoneAdd = groupName + PhilipsConstant.HASH + AutomationEnum.ZONE_ADD.getName();
					stats.put(zoneAdd, PhilipsConstant.EMPTY_STRING);
					advancedControllableProperties.add(createButton(zoneAdd, PhilipsConstant.ADD, PhilipsConstant.ADDING, 0));

					String[] typeDropdown = EnumTypeHandler.getEnumNames(TypeOfDeviceEnum.class);
					AdvancedControllableProperty typeControlProperty = controlDropdown(stats, typeDropdown, property, PhilipsConstant.ZONE);
					addOrUpdateAdvanceControlProperties(advancedControllableProperties, typeControlProperty);
				}
			}
		}
	}

	/**
	 * Populate room and zones control details
	 *
	 * @param stats the stats are list of statistics
	 * @param advancedControllableProperties advancedControllableProperties is the list that store all controllable properties
	 * @param roomAndZoneResponse the roomAndZoneResponse is roomAndZoneResponse DTO instance
	 * @param groupName the groupName is String with format Zone{ZoneName} or Room{RoomName}
	 * @param isRoomType is boolean value type is room if isRoomType = true and vice versa type is zone if isRoomType = false
	 */
	private void populatePropertiesRoomAndZone(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties, RoomAndZoneResponse roomAndZoneResponse, String groupName,
			boolean isRoomType) {
		for (RoomsAndZonesControlEnum zonesControlEnum : RoomsAndZonesControlEnum.values()) {
			String property = groupName + PhilipsConstant.HASH + zonesControlEnum.getName();
			String value;
			String roomName = roomAndZoneResponse.getMetaData().getName();
			switch (zonesControlEnum) {
				case NAME:
					value = roomName;
					AdvancedControllableProperty nameControllableProperty = controlTextOrNumeric(stats, property, value, false);
					addOrUpdateAdvanceControlProperties(advancedControllableProperties, nameControllableProperty);
					break;
				case ACTION:
					stats.put(property, PhilipsConstant.EMPTY_STRING);
					advancedControllableProperties.add(createButton(property, PhilipsConstant.DELETE, PhilipsConstant.DELETING, 0));
					break;
				case DEVICE_ADD:
					stats.put(property, PhilipsConstant.EMPTY_STRING);
					advancedControllableProperties.add(createButton(property, PhilipsConstant.ADD, PhilipsConstant.ADDING, 0));
					break;
				case TYPE:
					value = RoomTypeEnum.getNameOfRoomTypeEnumByValue(roomAndZoneResponse.getMetaData().getArchetype());
					AdvancedControllableProperty typeControllableProperty = controlDropdown(stats, EnumTypeHandler.getEnumNames(RoomTypeEnum.class), property, value);
					addOrUpdateAdvanceControlProperties(advancedControllableProperties, typeControllableProperty);
					break;
				case DEVICE_STATUS:
					if (groupLightMap.get(roomAndZoneResponse.getId()) != null) {
						value = groupLightMap.get(roomAndZoneResponse.getId()).getStatusLight().isOn() ? String.valueOf(PhilipsConstant.NUMBER_ONE) : String.valueOf(PhilipsConstant.ZERO);
						AdvancedControllableProperty statusControllableProperty = controlSwitch(stats, property, value, PhilipsConstant.OFFLINE, PhilipsConstant.ONLINE);
						addOrUpdateAdvanceControlProperties(advancedControllableProperties, statusControllableProperty);
					} else {
						stats.put(property, PhilipsConstant.NONE);
					}
					break;
				case DEVICE:
					if (isRoomType) {
						populateDeviceByRoom(groupName, roomAndZoneResponse, stats, advancedControllableProperties);
					} else {
						populateDeviceByZone(groupName, roomAndZoneResponse, stats, advancedControllableProperties);
					}
					break;
				default:
					if (logger.isDebugEnabled()) {
						logger.debug(String.format("Controlling zones with property %s is not supported.", zonesControlEnum.getName()));
					}
					break;
			}
			stats.put(groupName + PhilipsConstant.HASH + PhilipsConstant.EDITED, PhilipsConstant.FALSE);
		}
	}

	/**
	 * populate device by rooms and zones details
	 *
	 * @param stats the stats are list of statistics
	 * @param advancedControllableProperties advancedControllableProperties is the list that store all controllable properties
	 */
	private void populateDeviceByRoom(String groupName, RoomAndZoneResponse roomAndZoneResponse, Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {
		Map<String, String> mapOfRoomAndDevice = new HashMap<>();
		initializeDeviceDropdown(mapOfRoomAndDevice, PhilipsConstant.DEVICE, allDeviceIdAndNameMap.size());
		String[] deviceDropdown = deviceExitsInRoomMap.entrySet().stream().filter(item -> item.getValue().equals(PhilipsConstant.FALSE)).map(Entry::getKey).collect(Collectors.toList())
				.toArray(new String[0]);
		List<Children> children = Arrays.stream(roomAndZoneResponse.getChildren()).collect(Collectors.toList());
		int noOfDevices = children.size();
		String[] deviceDropdownCopy = Arrays.copyOf(deviceDropdown, deviceDropdown.length + noOfDevices);

		//Init dropdown list for room
		for (int childrenIndex = 0; childrenIndex < noOfDevices; childrenIndex++) {
			Children childrenItem = children.get(childrenIndex);
			String deviceName = allDeviceIdAndNameMap.get(childrenItem.getRid());
			if (!StringUtils.isNullOrEmpty(deviceName)) {
				deviceDropdownCopy[deviceDropdown.length + childrenIndex] = deviceName;
			}
		}
		String propertyKey = groupName + PhilipsConstant.HASH + PhilipsConstant.DEVICE;
		Arrays.sort(deviceDropdownCopy);
		//Handle case room has no device
		if (noOfDevices == 0) {
			mapOfRoomAndDevice.put(PhilipsConstant.DEVICE_0, PhilipsConstant.NONE);
			advancedControllableProperties.add(controlDropdown(stats, deviceDropdownCopy, propertyKey + PhilipsConstant.ZERO, PhilipsConstant.NONE));
		}
		//The room be assigned device
		for (int deviceIndex = 0; deviceIndex < noOfDevices; deviceIndex++) {
			Children childrenDetails = children.get(deviceIndex);
			String deviceName = allDeviceIdAndNameMap.get(childrenDetails.getRid());
			mapOfRoomAndDevice.put(PhilipsConstant.DEVICE + deviceIndex, deviceName);
			advancedControllableProperties.add(controlDropdown(stats, deviceDropdownCopy, propertyKey + deviceIndex, deviceName));
		}
		deviceRoomControlMap.put(roomAndZoneResponse.getMetaData().getName(), mapOfRoomAndDevice);
		roomAndDropdownListControlMap.put(roomAndZoneResponse.getMetaData().getName(), deviceDropdownCopy);
	}

	/**
	 * initial map of device value
	 *
	 * @param deviceIndexAndValue the deviceIndexAndValue is map of device index and value of it
	 * @param deviceSize the deviceSize is size of list device
	 */
	private void initializeDeviceDropdown(Map<String, String> deviceIndexAndValue, String name, int deviceSize) {
		for (int indexOfDevice = 0; indexOfDevice < deviceSize; indexOfDevice++) {
			if (indexOfDevice == 0) {
				deviceIndexAndValue.put(name + indexOfDevice, PhilipsConstant.NONE);
				continue;
			}
			deviceIndexAndValue.put(name + indexOfDevice, null);
		}
	}

	/**
	 * populate device by zones
	 *
	 * @param stats the stats are list of statistics
	 * @param advancedControllableProperties advancedControllableProperties is the list that store all controllable properties
	 */
	private void populateDeviceByZone(String groupName, RoomAndZoneResponse roomAndZoneResponse, Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {
		Map<String, String> mapOfRoomAndDevice = new HashMap<>();

		//init map device of zone
		initializeDeviceDropdown(mapOfRoomAndDevice, PhilipsConstant.DEVICE, allDeviceIdAndNameMap.size());
		String[] deviceDropdown = deviceNameAndDeviceIdZoneMap.keySet().toArray(new String[0]);
		List<Children> children = Arrays.stream(roomAndZoneResponse.getChildren()).collect(Collectors.toList());
		int noOfDevices = children.size();
		String propertyKey = groupName + PhilipsConstant.HASH + PhilipsConstant.DEVICE;
		Arrays.sort(deviceDropdown);
		if (noOfDevices == 0) {
			mapOfRoomAndDevice.put(PhilipsConstant.DEVICE_0, PhilipsConstant.NONE);
			advancedControllableProperties.add(controlDropdown(stats, deviceDropdown, propertyKey + PhilipsConstant.ZERO, PhilipsConstant.NONE));
		}
		for (int deviceIndex = 0; deviceIndex < noOfDevices; deviceIndex++) {
			Children childrenDetails = children.get(deviceIndex);
			Optional<Entry<String, String>> deviceNameOption = deviceNameAndDeviceIdZoneMap.entrySet().stream().filter(item -> item.getValue().equals(childrenDetails.getRid())).findFirst();
			String deviceName = PhilipsConstant.EMPTY_STRING;
			if (deviceNameOption.isPresent()) {
				deviceName = deviceNameOption.get().getKey();
			}
			mapOfRoomAndDevice.put(PhilipsConstant.DEVICE + deviceIndex, deviceName);
			advancedControllableProperties.add(controlDropdown(stats, deviceDropdown, propertyKey + deviceIndex, deviceName));
		}
		zoneNameAndMapZoneDeviceControl.put(roomAndZoneResponse.getMetaData().getName(), mapOfRoomAndDevice);
	}

	/**
	 * Update value and dropdown list of Device
	 *
	 * @param property the property is the filed name of controlling metric
	 * @param stats list of statistics
	 * @param advancedControllableProperties the advancedControllableProperties is advancedControllableProperties instance
	 * @param value the value is value of metric
	 * @param deviceNameMap the deviceNameMap is map of device
	 */
	private void updateDeviceDropdownList(String property, String value, Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties, List<String> nameList,
			Map<String, String> deviceNameMap, String name) {
		String[] propertyList = property.split(PhilipsConstant.HASH);
		String deviceProperty = propertyList[1];
		if (deviceNameMap == null) {
			deviceNameMap = new HashMap<>();
		}
		if (PhilipsConstant.NONE.equals(value) && !name.equals(deviceProperty)) {
			stats.remove(property);
			deviceNameMap.replace(deviceProperty, null);
		} else {
			String[] deviceDropdownList = nameList.toArray(new String[0]);
			AdvancedControllableProperty deviceControlProperty = controlDropdown(stats, deviceDropdownList, property, value);
			addOrUpdateAdvanceControlProperties(advancedControllableProperties, deviceControlProperty);
			deviceNameMap.put(deviceProperty, value);
		}
	}

	/**
	 * Create default room
	 *
	 * @param stats the stats are list of statistics
	 * @param advancedControllableProperties advancedControllableProperties is the list that store all controllable properties
	 */
	private void createRoom(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {

		stats.put(PhilipsConstant.CREATE_ROOM + PhilipsConstant.HASH + PhilipsConstant.CREATE, PhilipsConstant.EMPTY_STRING);
		advancedControllableProperties.add(createButton(PhilipsConstant.CREATE_ROOM + PhilipsConstant.HASH + PhilipsConstant.CREATE, PhilipsConstant.CREATE, PhilipsConstant.CREATING, 0));

		initializeDeviceDropdown(deviceRoomMap, PhilipsConstant.DEVICE, allDeviceIdAndNameMap.size());
		String[] deviceDropdown = deviceExitsInRoomMap.entrySet().stream().filter(item -> item.getValue().equals(PhilipsConstant.FALSE)).map(Entry::getKey).collect(Collectors.toList())
				.toArray(new String[0]);
		AdvancedControllableProperty deviceControlProperty = controlDropdown(stats, deviceDropdown, PhilipsConstant.CREATE_ROOM + PhilipsConstant.HASH + PhilipsConstant.DEVICE_0, PhilipsConstant.NONE);
		addOrUpdateAdvanceControlProperties(advancedControllableProperties, deviceControlProperty);
		deviceRoomMap.put(PhilipsConstant.DEVICE_0, PhilipsConstant.NONE);

		stats.put(PhilipsConstant.CREATE_ROOM + PhilipsConstant.HASH + PhilipsConstant.DEVICE_ADD, PhilipsConstant.EMPTY_STRING);
		advancedControllableProperties.add(createButton(PhilipsConstant.CREATE_ROOM + PhilipsConstant.HASH + PhilipsConstant.DEVICE_ADD, PhilipsConstant.ADD, PhilipsConstant.ADDING, 0));

		stats.put(PhilipsConstant.CREATE_ROOM + PhilipsConstant.HASH + PhilipsConstant.EDITED, PhilipsConstant.FALSE);

		stats.put(PhilipsConstant.CREATE_ROOM + PhilipsConstant.HASH + PhilipsConstant.NAME, PhilipsConstant.EMPTY_STRING);
		advancedControllableProperties.add(createText(PhilipsConstant.CREATE_ROOM + PhilipsConstant.HASH + PhilipsConstant.NAME, PhilipsConstant.EMPTY_STRING));

		String[] roomTypeDropdown = EnumTypeHandler.getEnumNames(RoomTypeEnum.class);
		AdvancedControllableProperty roomTypeControlProperty = controlDropdown(stats, roomTypeDropdown, PhilipsConstant.CREATE_ROOM + PhilipsConstant.HASH + PhilipsConstant.TYPE, PhilipsConstant.NONE);
		addOrUpdateAdvanceControlProperties(advancedControllableProperties, roomTypeControlProperty);
	}

	/**
	 * Create default a zone
	 *
	 * @param stats the stats are list of statistics
	 * @param advancedControllableProperties advancedControllableProperties is the list that store all controllable properties
	 */
	private void createZone(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {

		stats.put(PhilipsConstant.CREATE_ZONE + PhilipsConstant.HASH + PhilipsConstant.CREATE, PhilipsConstant.EMPTY_STRING);
		advancedControllableProperties.add(createButton(PhilipsConstant.CREATE_ZONE + PhilipsConstant.HASH + PhilipsConstant.CREATE, PhilipsConstant.CREATE, PhilipsConstant.CREATING, 0));

		initializeDeviceDropdown(deviceZoneMap, PhilipsConstant.DEVICE, deviceNameAndDeviceIdZoneMap.size());
		String[] deviceDropdown = deviceNameAndDeviceIdZoneMap.keySet().toArray(new String[0]);
		AdvancedControllableProperty deviceControlProperty = controlDropdown(stats, deviceDropdown, PhilipsConstant.CREATE_ZONE + PhilipsConstant.HASH + PhilipsConstant.DEVICE_0, PhilipsConstant.NONE);
		addOrUpdateAdvanceControlProperties(advancedControllableProperties, deviceControlProperty);
		deviceZoneMap.put(PhilipsConstant.DEVICE_0, PhilipsConstant.NONE);

		stats.put(PhilipsConstant.CREATE_ZONE + PhilipsConstant.HASH + PhilipsConstant.DEVICE_ADD, PhilipsConstant.EMPTY_STRING);
		advancedControllableProperties.add(createButton(PhilipsConstant.CREATE_ZONE + PhilipsConstant.HASH + PhilipsConstant.DEVICE_ADD, PhilipsConstant.ADD, PhilipsConstant.ADDING, 0));

		stats.put(PhilipsConstant.CREATE_ZONE + PhilipsConstant.HASH + PhilipsConstant.EDITED, PhilipsConstant.FALSE);

		stats.put(PhilipsConstant.CREATE_ZONE + PhilipsConstant.HASH + PhilipsConstant.NAME, PhilipsConstant.EMPTY_STRING);
		advancedControllableProperties.add(createText(PhilipsConstant.CREATE_ZONE + PhilipsConstant.HASH + PhilipsConstant.NAME, PhilipsConstant.EMPTY_STRING));

		String[] roomTypeDropdown = EnumTypeHandler.getEnumNames(RoomTypeEnum.class);
		AdvancedControllableProperty roomTypeControlProperty = controlDropdown(stats, roomTypeDropdown, PhilipsConstant.CREATE_ZONE + PhilipsConstant.HASH + PhilipsConstant.TYPE, PhilipsConstant.NONE);
		addOrUpdateAdvanceControlProperties(advancedControllableProperties, roomTypeControlProperty);
	}

	/**
	 * Populate create room details
	 *
	 * @param property the property is property name with format GroupName#KeyName
	 * @param value the value is value of property
	 * @param stats the stats are list of statistics
	 * @param advancedControllableProperties advancedControllableProperties is the list that store all controllable properties
	 */
	private void populatePropertiesForCreateRoom(String property, String value, Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {
		String[] propertyList = property.split(PhilipsConstant.HASH);
		String propertyGroup = propertyList[0];
		String key = propertyList[1];
		isCreateRoom = true;
		List<String> deviceDropdown = deviceExitsInRoomMap.entrySet().stream().filter(item -> item.getValue().equals(PhilipsConstant.FALSE)).map(Map.Entry::getKey).collect(Collectors.toList());
		if (key.contains(PhilipsConstant.DEVICE) && !CreateRoomEnum.DEVICE_ADD.getRoomName().equals(key)) {
			updateDeviceDropdownList(property, value, stats, advancedControllableProperties, deviceDropdown, deviceRoomMap, PhilipsConstant.DEVICE_0);
		} else {
			RoomsAndZonesControlEnum room = EnumTypeHandler.getMetricOfEnumByName(RoomsAndZonesControlEnum.class, key);
			switch (room) {
				case DEVICE_STATUS:
				case NAME:
				case TYPE:
					updateValueForTheControllableProperty(property, value, stats, advancedControllableProperties);
					break;
				case CREATE:
					RoomAndZoneResponse roomValue = convertRoomByValues(stats, PhilipsConstant.CREATE_ROOM + PhilipsConstant.HASH, deviceRoomMap, deviceNameAndMapDeviceIdOfRoomMap, false);
					sendRequestToCreateOrEditRoomAndZone(roomValue, true, false);
					isCreateRoom = false;
					deviceRoomMap.clear();
					break;
				case DEVICE_ADD:
					addNewDeviceForRoomAndZone(propertyGroup, stats, advancedControllableProperties, deviceDropdown, deviceRoomMap);
					break;
				case CANCEL:
					isCreateRoom = false;
					break;
				case DEVICE:
				case ACTION:
					break;
				default:
					if (logger.isDebugEnabled()) {
						logger.debug(String.format("Controlling create Room by name %s is not supported.", room.getName()));
					}
			}
		}
		populateCancelChangeButton(stats, advancedControllableProperties, propertyGroup, isCreateRoom);
	}

	/**
	 * Populate create automation
	 *
	 * @param property the property is property name with format GroupName#KeyName
	 * @param value the value is value of property
	 * @param stats the stats are list of statistics
	 * @param advancedControllableProperties advancedControllableProperties is the list that store all controllable properties
	 */
	private void populatePropertiesForCreateAutomation(String property, String value, Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {
		String[] propertyList = property.split(PhilipsConstant.HASH);
		String propertyGroup = propertyList[0];
		String key = propertyList[1];
		isCreateAutomation = true;

		if (!PhilipsConstant.REPEAT.equalsIgnoreCase(key) && key.contains(PhilipsConstant.REPEAT) && !AutomationEnum.REPEAT_ADD.getName().equals(key)) {
			List<String> repeatDayDropdown = Arrays.stream(RepeatDayEnum.values()).map(RepeatDayEnum::getName).collect(Collectors.toList());
			updateDeviceDropdownList(property, value, stats, advancedControllableProperties, repeatDayDropdown, repeatNameOfCreateAutomationMap, PhilipsConstant.REPEAT_0);
		} else if (!PhilipsConstant.DEVICE.equalsIgnoreCase(key) && key.contains(PhilipsConstant.DEVICE) && !AutomationEnum.DEVICE_ADD.getName().equals(key)) {
			List<String> deviceDropdown = deviceNameAndDeviceIdZoneMap.keySet().stream().collect(Collectors.toList());
			Map<String, String> mapOfDevice = typeAndMapOfDeviceAndValue.get(PhilipsConstant.DEVICE);
			updateDeviceDropdownList(property, value, stats, advancedControllableProperties, deviceDropdown, mapOfDevice, PhilipsConstant.DEVICE_0);
		} else if (!PhilipsConstant.ROOM.equalsIgnoreCase(key) && key.contains(PhilipsConstant.ROOM) && !AutomationEnum.ROOM_ADD.getName().equals(key)) {
			List<String> deviceDropdown = roomNameAndIdMap.keySet().stream().collect(Collectors.toList());
			Map<String, String> mapOfDevice = typeAndMapOfDeviceAndValue.get(PhilipsConstant.ROOM);
			updateDeviceDropdownList(property, value, stats, advancedControllableProperties, deviceDropdown, mapOfDevice, PhilipsConstant.ROOM_0);
		} else if (!PhilipsConstant.ZONE.equalsIgnoreCase(key) && key.contains(PhilipsConstant.ZONE) && !AutomationEnum.ZONE_ADD.getName().equals(key)) {
			List<String> deviceDropdown = zoneNameAndIdMap.keySet().stream().collect(Collectors.toList());
			Map<String, String> mapOfDevice = typeAndMapOfDeviceAndValue.get(PhilipsConstant.ZONE);
			updateDeviceDropdownList(property, value, stats, advancedControllableProperties, deviceDropdown, mapOfDevice, PhilipsConstant.ZONE_0);
		} else {
			AutomationEnum automationEnum = EnumTypeHandler.getMetricOfEnumByName(AutomationEnum.class, key);
			switch (automationEnum) {
				case NAME:
				case STATUS:
				case TIME_HOUR:
				case TIME_CURRENT:
				case TIME_MINUTE:
				case END_BRIGHTNESS:
				case END_WITH:
				case STYLE:
				case FADE_DURATION_HOUR:
				case FADE_DURATION_MINUTE:
					updateValueForTheControllableProperty(property, value, stats, advancedControllableProperties);
					break;
				case FADE_DURATION:
					value = String.valueOf(getValueByRange(PhilipsConstant.MIN_FADE_DURATION, PhilipsConstant.MAX_FADE_DURATION, value));
					updateValueForTheControllableProperty(property, value, stats, advancedControllableProperties);
					break;
				case REPEAT:
					updateValueForTheControllableProperty(property, value, stats, advancedControllableProperties);
					if (String.valueOf(PhilipsConstant.NUMBER_ONE).equals(value)) {
						stats.put(propertyGroup + PhilipsConstant.HASH + AutomationEnum.REPEAT_ADD.getName(), PhilipsConstant.EMPTY_STRING);
						advancedControllableProperties.add(createButton(propertyGroup + PhilipsConstant.HASH + AutomationEnum.REPEAT_ADD.getName(), PhilipsConstant.ADD, PhilipsConstant.ADDING, 0));
						List<String> dayDropdown = Arrays.stream(RepeatDayEnum.values()).map(RepeatDayEnum::getName).collect(Collectors.toList());
						addRepeatDayForCreateAutomation(property, stats, advancedControllableProperties, dayDropdown, repeatNameOfCreateAutomationMap, PhilipsConstant.REPEAT);
					} else {
						String repeatAdd = propertyGroup + PhilipsConstant.HASH + AutomationEnum.REPEAT_ADD.getName();

						stats.remove(repeatAdd);
						advancedControllableProperties.removeIf(item -> item.getName().equals(repeatAdd));
						removeRepeatDayForCreateAutomation(property, stats, advancedControllableProperties, repeatNameOfCreateAutomationMap);
					}
					break;
				case REPEAT_ADD:
					addNewRepeatForAutomation(propertyGroup, stats, advancedControllableProperties, repeatNameOfCreateAutomationMap);
					break;
				case DEVICE_ADD:
					List<String> deviceDropdown = deviceNameAndDeviceIdZoneMap.keySet().stream().collect(Collectors.toList());
					Map<String, String> mapOfDevice = typeAndMapOfDeviceAndValue.get(PhilipsConstant.DEVICE);
					if (mapOfDevice == null) {
						mapOfDevice = new HashMap<>();
						initializeDeviceDropdown(mapOfDevice, PhilipsConstant.DEVICE, deviceNameAndDeviceIdZoneMap.size());
						typeAndMapOfDeviceAndValue.put(PhilipsConstant.DEVICE, mapOfDevice);
					}
					addNewDeviceForRoomAndZone(propertyGroup, stats, advancedControllableProperties, deviceDropdown, mapOfDevice);
					break;
				case ROOM_ADD:
					Map<String, String> mapOfRoom = typeAndMapOfDeviceAndValue.get(PhilipsConstant.ROOM);
					if (mapOfRoom == null) {
						mapOfRoom = new HashMap<>();
						initializeDeviceDropdown(mapOfRoom, PhilipsConstant.ROOM, roomNameAndIdMap.size());
						typeAndMapOfDeviceAndValue.put(PhilipsConstant.ROOM, mapOfRoom);
					}
					addRoomAndZoneForAutomation(propertyGroup, stats, advancedControllableProperties, roomNameAndIdMap, mapOfRoom);
					break;
				case ZONE_ADD:
					Map<String, String> mapOfZone = typeAndMapOfDeviceAndValue.get(PhilipsConstant.ZONE);
					if (mapOfZone == null) {
						mapOfZone = new HashMap<>();
						initializeDeviceDropdown(mapOfZone, PhilipsConstant.ZONE, zoneNameAndIdMap.size());
						typeAndMapOfDeviceAndValue.put(PhilipsConstant.ZONE, mapOfZone);
					}
					addRoomAndZoneForAutomation(propertyGroup, stats, advancedControllableProperties, zoneNameAndIdMap, mapOfZone);
					break;
				case TYPE:
					updateValueForTheControllableProperty(property, value, stats, advancedControllableProperties);
					TypeOfDeviceEnum type = EnumTypeHandler.getMetricOfEnumByName(TypeOfDeviceEnum.class, value);
					switch (type) {
						case DEVICE:
						case ROOM:
						case ZONE:
							updateDeviceTypeForCreateAutomationByType(property, stats, advancedControllableProperties, type, typeAndMapOfDeviceAndValue);
							break;
						default:
							if (logger.isDebugEnabled()) {
								logger.debug(String.format("Creating automation with device type %s is not supported.", automationEnum.getName()));
							}
					}
					break;
				case CANCEL:
					isCreateAutomation = false;
					repeatNameOfCreateAutomationMap.clear();
					typeAndMapOfDeviceAndValue.clear();
					break;
				case TYPE_OF_AUTOMATION:
					updateValueForTheControllableProperty(property, value, stats, advancedControllableProperties);

					TypeOfAutomation typeAutomation = EnumTypeHandler.getMetricOfEnumByName(TypeOfAutomation.class, value);
					String endWith = propertyGroup + PhilipsConstant.HASH + AutomationEnum.END_WITH.getName();
					String endWithValue = localCreateAutomationStats.get(endWith);
					if (StringUtils.isNullOrEmpty(endWithValue)) {
						endWithValue = EndStateEnum.LIGHT_OFF.getName();
					}
					String style = propertyGroup + PhilipsConstant.HASH + AutomationEnum.STYLE.getName();
					String styleValue = localCreateAutomationStats.get(style);
					if (StringUtils.isNullOrEmpty(styleValue)) {
						styleValue = StyleEnum.SUNRISE.getName();
					}
					String endBrightness = propertyGroup + PhilipsConstant.HASH + AutomationEnum.END_BRIGHTNESS.getName();
					String endBrightnessValue = localCreateAutomationStats.get(endBrightness);
					if (StringUtils.isNullOrEmpty(endBrightnessValue)) {
						endBrightnessValue = String.valueOf(PhilipsConstant.NUMBER_ONE);
					}
					String durationHourName = propertyGroup + PhilipsConstant.HASH + AutomationEnum.FADE_DURATION_HOUR.getName();
					String durationHourNameValue = localCreateAutomationStats.get(durationHourName);
					if (StringUtils.isNullOrEmpty(durationHourNameValue)) {
						durationHourNameValue = TimerHourEnum.TIME_00.getName();
					}
					String durationMinuteName = propertyGroup + PhilipsConstant.HASH + AutomationEnum.FADE_DURATION_MINUTE.getName();
					String durationMinuteNameValue = localCreateAutomationStats.get(durationMinuteName);
					if (StringUtils.isNullOrEmpty(durationMinuteNameValue)) {
						durationMinuteNameValue = TimeMinuteEnum.MINUTE_01.getName();
					}
					String fadeDuration = propertyGroup + PhilipsConstant.HASH + AutomationEnum.FADE_DURATION.getName();
					String fadeDurationValue = localCreateAutomationStats.get(fadeDuration);
					if (StringUtils.isNullOrEmpty(fadeDurationValue)) {
						fadeDurationValue = String.valueOf(PhilipsConstant.DEFAULT_FADE_DURATION);
					}
					String repeat = propertyGroup + PhilipsConstant.HASH + AutomationEnum.REPEAT.getName();
					String repeatValue = localCreateAutomationStats.get(repeat);
					if (StringUtils.isNullOrEmpty(repeatValue)) {
						repeatValue = String.valueOf(PhilipsConstant.ZERO);
					}
					String timeCurrent = propertyGroup + PhilipsConstant.HASH + AutomationEnum.TIME_CURRENT.getName();
					String timeCurrentValue = localCreateAutomationStats.get(timeCurrent);
					if (StringUtils.isNullOrEmpty(timeCurrentValue)) {
						timeCurrentValue = String.valueOf(PhilipsConstant.ZERO);
					}
					String timeHour = propertyGroup + PhilipsConstant.HASH + AutomationEnum.TIME_HOUR.getName();
					String timeHourValue = localCreateAutomationStats.get(timeHour);
					if (StringUtils.isNullOrEmpty(timeHourValue)) {
						timeHourValue = TimeHourEnum.TIME_01.getName();
					}
					String timeMinute = propertyGroup + PhilipsConstant.HASH + AutomationEnum.TIME_MINUTE.getName();
					String timeMinuteValue = localCreateAutomationStats.get(timeMinute);
					if (StringUtils.isNullOrEmpty(timeMinuteValue)) {
						timeMinuteValue = TimeMinuteEnum.MINUTE_00.getName();
					}
					switch (typeAutomation) {
						case GO_TO_SLEEP:
							stats.put(repeat, repeatValue);
							advancedControllableProperties.add(controlSwitch(stats, repeat, repeatValue, PhilipsConstant.DISABLE, PhilipsConstant.ENABLE));
							stats.put(timeCurrent, timeCurrentValue);
							advancedControllableProperties.add(controlSwitch(stats, timeCurrent, timeCurrentValue, PhilipsConstant.TIME_AM, PhilipsConstant.TIME_PM));

							String[] hourDropdown = EnumTypeHandler.getEnumNames(TimeHourEnum.class);
							AdvancedControllableProperty hourControlProperty = controlDropdown(stats, hourDropdown, timeHour, timeHourValue);
							addOrUpdateAdvanceControlProperties(advancedControllableProperties, hourControlProperty);

							String[] timeMinuteDropdown = EnumTypeHandler.getEnumNames(TimeMinuteEnum.class);
							AdvancedControllableProperty timeMinuteControlProperty = controlDropdown(stats, timeMinuteDropdown, timeMinute, timeMinuteValue);
							addOrUpdateAdvanceControlProperties(advancedControllableProperties, timeMinuteControlProperty);

							AdvancedControllableProperty nameControllableProperty = controlTextOrNumeric(stats, fadeDuration, fadeDurationValue, true);
							addOrUpdateAdvanceControlProperties(advancedControllableProperties, nameControllableProperty);

							String[] endWithDropdown = EnumTypeHandler.getEnumNames(EndStateEnum.class);
							AdvancedControllableProperty roomTypeControlProperty = controlDropdown(stats, endWithDropdown, endWith, endWithValue);
							addOrUpdateAdvanceControlProperties(advancedControllableProperties, roomTypeControlProperty);

							stats.remove(style);
							advancedControllableProperties.removeIf(item -> item.getName().equals(style));
							stats.remove(endBrightness);
							advancedControllableProperties.removeIf(item -> item.getName().equals(endBrightness));
							stats.remove(durationHourName);
							advancedControllableProperties.removeIf(item -> item.getName().equals(durationHourName));
							stats.remove(durationMinuteName);
							advancedControllableProperties.removeIf(item -> item.getName().equals(durationMinuteName));
							break;
						case WAKE_UP_WITH_LIGHT:
							stats.put(repeat, repeatValue);
							advancedControllableProperties.add(controlSwitch(stats, repeat, repeatValue, PhilipsConstant.DISABLE, PhilipsConstant.ENABLE));

							stats.put(timeCurrent, timeCurrentValue);
							advancedControllableProperties.add(controlSwitch(stats, timeCurrent, timeCurrentValue, PhilipsConstant.TIME_AM, PhilipsConstant.TIME_PM));

							hourDropdown = EnumTypeHandler.getEnumNames(TimeHourEnum.class);
							hourControlProperty = controlDropdown(stats, hourDropdown, timeHour, timeHourValue);
							addOrUpdateAdvanceControlProperties(advancedControllableProperties, hourControlProperty);

							timeMinuteDropdown = EnumTypeHandler.getEnumNames(TimeMinuteEnum.class);
							timeMinuteControlProperty = controlDropdown(stats, timeMinuteDropdown, timeMinute, timeMinuteValue);
							addOrUpdateAdvanceControlProperties(advancedControllableProperties, timeMinuteControlProperty);

							nameControllableProperty = controlTextOrNumeric(stats, fadeDuration, fadeDurationValue, true);
							addOrUpdateAdvanceControlProperties(advancedControllableProperties, nameControllableProperty);

							stats.remove(endWith);
							advancedControllableProperties.removeIf(item -> item.getName().equals(endWith));

							String[] styleDropdown = EnumTypeHandler.getEnumNames(StyleEnum.class);
							AdvancedControllableProperty styleControlProperty = controlDropdown(stats, styleDropdown, style, styleValue);
							addOrUpdateAdvanceControlProperties(advancedControllableProperties, styleControlProperty);

							AdvancedControllableProperty sliderControlProperty = createControlSlider(endBrightness, endBrightnessValue, stats,
									String.valueOf(PhilipsConstant.MIN_END_BRIGHTNESS), String.valueOf(PhilipsConstant.MAX_END_BRIGHTNESS));
							addOrUpdateAdvanceControlProperties(advancedControllableProperties, sliderControlProperty);
							stats.remove(durationHourName);
							advancedControllableProperties.removeIf(item -> item.getName().equals(durationHourName));
							stats.remove(durationMinuteName);
							advancedControllableProperties.removeIf(item -> item.getName().equals(durationMinuteName));
							break;
						case TIMER:
							hourDropdown = EnumTypeHandler.getEnumNames(TimeHourEnum.class);
							hourControlProperty = controlDropdown(stats, hourDropdown, durationHourName, durationHourNameValue);
							addOrUpdateAdvanceControlProperties(advancedControllableProperties, hourControlProperty);
							timeMinuteDropdown = EnumTypeHandler.getEnumNames(TimeMinuteEnum.class);
							timeMinuteControlProperty = controlDropdown(stats, timeMinuteDropdown, durationMinuteName, durationMinuteNameValue);
							addOrUpdateAdvanceControlProperties(advancedControllableProperties, timeMinuteControlProperty);
							stats.remove(endWith);
							advancedControllableProperties.removeIf(item -> item.getName().equals(endWith));
							stats.remove(style);
							advancedControllableProperties.removeIf(item -> item.getName().equals(style));
							stats.remove(endBrightness);
							advancedControllableProperties.removeIf(item -> item.getName().equals(endBrightness));
							stats.remove(timeCurrent);
							advancedControllableProperties.removeIf(item -> item.getName().equals(timeCurrent));
							stats.remove(timeHour);
							advancedControllableProperties.removeIf(item -> item.getName().equals(timeHour));
							stats.remove(timeMinute);
							advancedControllableProperties.removeIf(item -> item.getName().equals(timeMinute));
							stats.remove(fadeDuration);
							advancedControllableProperties.removeIf(item -> item.getName().equals(fadeDuration));
							break;
						default:
							if (logger.isDebugEnabled()) {
								logger.debug(String.format("Controlling create automation automation type %s is not supported.", automationEnum.getName()));
							}
							break;
					}
					break;
				case ACTION:
					AutomationResponse automation = convertAutomationByValues(propertyGroup, stats, typeAndMapOfDeviceAndValue, repeatNameOfCreateAutomationMap);
					sendRequestToCreateAutomation(automation, false);
					isCreateAutomation = false;
					repeatNameOfCreateAutomationMap.clear();
					typeAndMapOfDeviceAndValue.clear();
					break;
				default:
					if (logger.isDebugEnabled()) {
						logger.debug(String.format("Controlling create automation by name %s is not supported.", automationEnum.getName()));
					}
			}
		}
		populateCancelChangeButton(stats, advancedControllableProperties, propertyGroup, isCreateAutomation);
	}

	/**
	 * Send request to create automation
	 *
	 * @param automationResponse the automationResponse is instance in AutomationResponse DTO
	 * @param isEditedAutomation is boolean type if edit automation then isEditedAutomation = true, and create automation if isEditedAutomation = false
	 */
	private void sendRequestToCreateAutomation(AutomationResponse automationResponse, boolean isEditedAutomation) {
		try {
			ResponseData autoResponseData;
			String request = PhilipsUtil.getMonitorURL(PhilipsURL.AUTOMATION);
			if (isEditedAutomation) {
				autoResponseData = doPut(request + PhilipsConstant.SLASH + automationResponse.getId(), automationResponse.toString(), ResponseData.class);
			} else {
				autoResponseData = doPost(request, automationResponse.toString(), ResponseData.class);
			}
			if (autoResponseData.getData() == null) {
				throw new ResourceNotReachableException("Error while creating/editing automation:" + Arrays.stream(autoResponseData.getErrors()).map(ErrorsResponse::getDescription));
			}
		} catch (Exception e) {
			throw new ResourceNotReachableException("Can't creat/edit automation: " + e.getMessage(), e);
		}
	}

	/**
	 * Update list device for automation
	 *
	 * @param property the property is property name with format {CreateAutomationBehaviorInstance#{key}}
	 * @param stats the stats are list of statistics
	 * @param advancedControllableProperties advancedControllableProperties is the list that store all controllable properties
	 * @param type the type is type of TypeOfDevice enum instance
	 */
	private void updateListDeviceForCreateAutomation(String property, Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties, TypeOfDeviceEnum type,
			Map<String, Map<String, String>> typeMApOfDevice) {
		String[] propertyList = property.split(PhilipsConstant.HASH);
		String propertyGroup = propertyList[0];
		Map<String, String> deviceOfRoom = typeMApOfDevice.get(PhilipsConstant.ROOM);
		Map<String, String> deviceOfZone = typeMApOfDevice.get(PhilipsConstant.ZONE);
		Map<String, String> deviceMap = typeMApOfDevice.get(PhilipsConstant.DEVICE);
		String roomAdd = propertyGroup + PhilipsConstant.HASH + AutomationEnum.ROOM_ADD.getName();
		String zoneAdd = propertyGroup + PhilipsConstant.HASH + AutomationEnum.ZONE_ADD.getName();
		String deviceAdd = propertyGroup + PhilipsConstant.HASH + AutomationEnum.DEVICE_ADD.getName();
		switch (type) {
			case DEVICE:
				stats.put(deviceAdd, PhilipsConstant.EMPTY_STRING);
				advancedControllableProperties.add(createButton(deviceAdd, PhilipsConstant.ADD, PhilipsConstant.ADDING, 0));
				if (deviceOfRoom.size() > 0) {
					removeRepeatDayForCreateAutomation(property, stats, advancedControllableProperties, deviceOfRoom);
				}
				if (deviceOfZone.size() > 0) {
					removeRepeatDayForCreateAutomation(property, stats, advancedControllableProperties, deviceOfZone);
				}
				stats.remove(roomAdd);
				advancedControllableProperties.removeIf(item -> item.getName().equals(roomAdd));
				stats.remove(zoneAdd);
				advancedControllableProperties.removeIf(item -> item.getName().equals(zoneAdd));
				break;
			case ROOM:
				stats.put(roomAdd, PhilipsConstant.EMPTY_STRING);
				advancedControllableProperties.add(createButton(roomAdd, PhilipsConstant.ADD, PhilipsConstant.ADDING, 0));
				if (deviceMap.size() > 0) {
					removeRepeatDayForCreateAutomation(property, stats, advancedControllableProperties, deviceMap);
				}
				if (deviceOfZone.size() > 0) {
					removeRepeatDayForCreateAutomation(property, stats, advancedControllableProperties, deviceOfZone);
				}
				stats.remove(deviceAdd);
				advancedControllableProperties.removeIf(item -> item.getName().equals(deviceAdd));
				stats.remove(zoneAdd);
				advancedControllableProperties.removeIf(item -> item.getName().equals(zoneAdd));
				break;
			case ZONE:
				stats.put(zoneAdd, PhilipsConstant.EMPTY_STRING);
				advancedControllableProperties.add(createButton(zoneAdd, PhilipsConstant.ADD, PhilipsConstant.ADDING, 0));

				if (deviceMap.size() > 0) {
					removeRepeatDayForCreateAutomation(property, stats, advancedControllableProperties, deviceMap);
				}
				if (deviceOfRoom.size() > 0) {
					removeRepeatDayForCreateAutomation(property, stats, advancedControllableProperties, deviceOfRoom);
				}
				stats.remove(deviceAdd);
				advancedControllableProperties.removeIf(item -> item.getName().equals(deviceAdd));
				stats.remove(roomAdd);
				advancedControllableProperties.removeIf(item -> item.getName().equals(roomAdd));
				break;
			default:
				if (logger.isDebugEnabled()) {
					logger.debug(String.format("Controlling create automation device type %s is not supported.", type.getName()));
				}
		}
	}

	/**
	 * Populate update repeat day for create automation
	 *
	 * @param property the property is property name with format {CreateAutomationBehaviorInstance#{key}}
	 * @param stats the stats are list of statistics
	 * @param advancedControllableProperties advancedControllableProperties is the list that store all controllable properties
	 * @param type the type is type of TypeOfDevice enum instance
	 */
	private void updateDeviceTypeForCreateAutomationByType(String property, Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties, TypeOfDeviceEnum type,
			Map<String, Map<String, String>> typeNameOfRepeat) {
		String[] propertyList = property.split(PhilipsConstant.HASH);
		String propertyGroup = propertyList[0];
		Map<String, String> deviceIndexAndValue = typeNameOfRepeat.get(type.getName());
		switch (type) {
			case DEVICE:
				if (deviceIndexAndValue.size() == 0) {
					initializeDeviceDropdown(deviceIndexAndValue, PhilipsConstant.DEVICE, allDeviceIdAndNameMap.size());
					String[] deviceDropdown = deviceNameAndDeviceIdZoneMap.keySet().toArray(new String[0]);
					AdvancedControllableProperty initDeviceControlProperty = controlDropdown(stats, deviceDropdown, propertyGroup + PhilipsConstant.HASH + PhilipsConstant.DEVICE_0, PhilipsConstant.NONE);
					addOrUpdateAdvanceControlProperties(advancedControllableProperties, initDeviceControlProperty);
					deviceIndexAndValue.put(PhilipsConstant.DEVICE_0, PhilipsConstant.NONE);
				} else {
					List<String> deviceDropdown = deviceNameAndDeviceIdZoneMap.keySet().stream().collect(Collectors.toList());
					addRepeatDayForCreateAutomation(property, stats, advancedControllableProperties, deviceDropdown, deviceIndexAndValue, PhilipsConstant.DEVICE);
				}
				updateListDeviceForCreateAutomation(property, stats, advancedControllableProperties, type, typeNameOfRepeat);
				break;
			case ROOM:
				if (deviceIndexAndValue.size() == 0) {
					initializeDeviceDropdown(deviceIndexAndValue, PhilipsConstant.ROOM, roomNameAndIdMap.size());
					String[] deviceDropdown = roomNameAndIdMap.keySet().toArray(new String[0]);
					AdvancedControllableProperty initDeviceControlProperty = controlDropdown(stats, deviceDropdown, propertyGroup + PhilipsConstant.HASH + PhilipsConstant.ROOM_0, PhilipsConstant.NONE);
					addOrUpdateAdvanceControlProperties(advancedControllableProperties, initDeviceControlProperty);
					deviceIndexAndValue.put(PhilipsConstant.ROOM_0, PhilipsConstant.NONE);
				} else {
					List<String> deviceDropdown = roomNameAndIdMap.keySet().stream().collect(Collectors.toList());
					addRepeatDayForCreateAutomation(property, stats, advancedControllableProperties, deviceDropdown, deviceIndexAndValue, PhilipsConstant.ROOM);
				}
				updateListDeviceForCreateAutomation(property, stats, advancedControllableProperties, type, typeNameOfRepeat);
				break;
			case ZONE:
				if (deviceIndexAndValue.size() == 0) {
					initializeDeviceDropdown(deviceIndexAndValue, PhilipsConstant.ZONE, zoneNameAndIdMap.size());
					String[] deviceDropdown = zoneNameAndIdMap.keySet().toArray(new String[0]);
					AdvancedControllableProperty initDeviceControlProperty = controlDropdown(stats, deviceDropdown, propertyGroup + PhilipsConstant.HASH + PhilipsConstant.ZONE_0, PhilipsConstant.NONE);
					addOrUpdateAdvanceControlProperties(advancedControllableProperties, initDeviceControlProperty);
					deviceIndexAndValue.put(PhilipsConstant.ZONE_0, PhilipsConstant.NONE);
				} else {
					List<String> deviceDropdown = zoneNameAndIdMap.keySet().stream().collect(Collectors.toList());
					addRepeatDayForCreateAutomation(property, stats, advancedControllableProperties, deviceDropdown, deviceIndexAndValue, PhilipsConstant.ZONE);
				}
				updateListDeviceForCreateAutomation(property, stats, advancedControllableProperties, type, typeNameOfRepeat);
				break;
			default:
				if (logger.isDebugEnabled()) {
					logger.debug(String.format("Controlling create automation device type %s is not supported.", type.getName()));
				}
		}
	}

	/**
	 * Populate remove repeat day for create automation
	 *
	 * @param property the property is property name with format {CreateAutomationBehaviorInstance#{key}}
	 * @param stats the stats are list of statistics
	 * @param advancedControllableProperties advancedControllableProperties is the list that store all controllable properties
	 */
	private void removeRepeatDayForCreateAutomation(String property, Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties, Map<String, String> deviceMap) {
		String propertyGroup = property.split(PhilipsConstant.HASH)[0];
		if (deviceMap != null) {
			for (Entry<String, String> repeatEntry : deviceMap.entrySet()) {
				String value = repeatEntry.getValue();
				String repeatName = propertyGroup + PhilipsConstant.HASH + repeatEntry.getKey();
				if (!StringUtils.isNullOrEmpty(value)) {
					stats.remove(repeatName);
					advancedControllableProperties.removeIf(item -> item.getName().equals(repeatName));
				}
			}
		}
	}

	/**
	 * Add new repeat for automation
	 *
	 * @param property the property is property name with format {CreateAutomationBehaviorInstance#{key}}
	 * @param stats the stats are list of statistics
	 * @param advancedControllableProperties advancedControllableProperties is the list that store all controllable properties
	 */
	private void addRepeatDayForCreateAutomation(String property, Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties, List<String> dropdownList,
			Map<String, String> deviceMap, String name) {
		String[] propertyList = property.split(PhilipsConstant.HASH);
		String propertyGroup = propertyList[0];
		String[] repeatDaysDropdown = dropdownList.toArray(new String[0]);
		if (deviceMap == null) {
			deviceMap = new HashMap<>();
		}
		if (deviceMap.size() < 1) {
			initializeDeviceDropdown(deviceMap, name, RepeatDayEnum.values().length - 1);

			AdvancedControllableProperty repeatDaysControlProperty = controlDropdown(stats, repeatDaysDropdown, propertyGroup + PhilipsConstant.HASH + name + PhilipsConstant.ZERO, PhilipsConstant.NONE);
			addOrUpdateAdvanceControlProperties(advancedControllableProperties, repeatDaysControlProperty);
			deviceMap.putIfAbsent(name + PhilipsConstant.ZERO, PhilipsConstant.NONE);
		} else {
			for (Entry<String, String> repeatEntry : deviceMap.entrySet()) {
				String value = repeatEntry.getValue();
				if (!StringUtils.isNullOrEmpty(value)) {
					AdvancedControllableProperty repeatDaysControlProperty = controlDropdown(stats, repeatDaysDropdown, propertyGroup + PhilipsConstant.HASH + repeatEntry.getKey(), value);
					addOrUpdateAdvanceControlProperties(advancedControllableProperties, repeatDaysControlProperty);
				}
			}
		}
	}

	/**
	 * Add room and zone for automation
	 *
	 * @param propertyGroup the propertyGroup is group name of property
	 * @param stats the stats are list of statistics
	 * @param advancedControllableProperties advancedControllableProperties is the list that store all controllable properties
	 * @param dayMaps are map of list Day
	 * @param deviceMap are map of device
	 */
	private void addRoomAndZoneForAutomation(String propertyGroup, Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties, Map<String, String> dayMaps,
			Map<String, String> deviceMap) {

		String[] roomAndZoneDropdown = dayMaps.keySet().toArray(new String[0]);
		String prefixName = propertyGroup + PhilipsConstant.HASH;
		//Check list room and zone
		int countDevice = 0;
		for (Map.Entry<String, String> entry : deviceMap.entrySet()) {
			if (entry.getValue() != null) {
				//count the number of room and zone
				countDevice++;
			}
		}
		if (countDevice >= dayMaps.size() - 1) {
			throw new ResourceNotReachableException(
					String.format("Total day is %s, you have added enough room/zone and cannot add new room/zone. Please remove the room/zone and try again", dayMaps.size() - 1));
		}
		for (Map.Entry<String, String> roomAndZoneEntry : deviceMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors.toList())) {
			String defaultName = PhilipsConstant.NONE;
			for (String roomAndZoneValue : dayMaps.keySet()) {
				if (!deviceMap.containsValue(roomAndZoneValue)) {
					defaultName = roomAndZoneValue;
					break;
				}
			}
			if (roomAndZoneEntry.getValue() == null) {
				advancedControllableProperties.add(controlDropdown(stats, roomAndZoneDropdown, prefixName + roomAndZoneEntry.getKey(), defaultName));
				roomAndZoneEntry.setValue(defaultName);
				break;
			}
		}
	}

	/**
	 * Add new repeat for automation
	 *
	 * @param propertyGroup the propertyGroup is group name of property
	 * @param stats the stats are list of statistics
	 * @param advancedControllableProperties advancedControllableProperties is the list that store all controllable properties
	 * @param repeatMap are map of repeat
	 */
	private void addNewRepeatForAutomation(String propertyGroup, Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties, Map<String, String> repeatMap) {

		String[] repeatDayDropdown = EnumTypeHandler.getEnumNames(RepeatDayEnum.class);
		String prefixName = propertyGroup + PhilipsConstant.HASH;
		//Check list repeat day added
		int countDevice = 0;
		for (Map.Entry<String, String> entry : repeatMap.entrySet()) {
			if (entry.getValue() != null) {
				//count the number of day
				countDevice++;
			}
		}

		//Maximum for repeat day is 7. with index RepeatDay0-RepeatDay6
		// if countSource >= 7 throw exception
		if (countDevice >= PhilipsConstant.MAXIMUM_REPEAT_DAY) {
			throw new ResourceNotReachableException("Total day is 7, you have added enough day and cannot add new day. Please remove the repeat and try again");
		}
		Map<String, String> mapCopy = new TreeMap<>();
		mapCopy.putAll(repeatMap);

		for (Map.Entry<String, String> repeatDayEntry : mapCopy.entrySet()) {
			String defaultName = PhilipsConstant.NONE;
			for (RepeatDayEnum repeatDayEnum : RepeatDayEnum.values()) {
				if (!repeatMap.containsValue(repeatDayEnum.getName())) {
					defaultName = repeatDayEnum.getName();
					break;
				}
			}
			if (repeatDayEntry.getValue() == null) {
				advancedControllableProperties.add(controlDropdown(stats, repeatDayDropdown, prefixName + repeatDayEntry.getKey(), defaultName));
				repeatDayEntry.setValue(defaultName);
				break;
			}
		}
		repeatMap.putAll(mapCopy);
	}

	/**
	 * Send request to change status for room/zone
	 *
	 * @param id the id is id of room/zone
	 * @param status the status is status of room/zone
	 * @param isRoomType the isRoomType is boolean value
	 */
	private void sendRequestToChangeStatusForTheDevice(String id, String status, boolean isRoomType) {
		try {
			ResponseData responseData = doPut(PhilipsUtil.getMonitorURL(PhilipsURL.GROUP_LIGHT) + PhilipsConstant.SLASH + id, status, ResponseData.class);
			if (responseData.getErrors().length > 0) {
				String name = PhilipsConstant.ZONE;
				if (isRoomType) {
					name = PhilipsConstant.ROOM;
				}
				String errorMessage;
				if (PhilipsConstant.ERROR_MESSAGE_STATUS.equals(responseData.getErrors()[0].getDescription())) {
					errorMessage = String.format("Error while changing status for %s: the status is offline", name);
				} else {
					errorMessage = String.format("Error while changing status for %s: %s", name, responseData.getErrors()[0].getDescription());
				}
				throw new ResourceNotReachableException(errorMessage);
			}
		} catch (Exception e) {
			throw new ResourceNotReachableException(String.format("Can't change status: %s", e.getMessage()), e);
		}
	}

	/**
	 * Populate Control for room and zone
	 *
	 * @param property the property is property name with format GroupName#KeyName
	 * @param value the value is value of property
	 * @param stats the stats are list of statistics
	 * @param advancedControllableProperties advancedControllableProperties is the list that store all controllable properties
	 * @param isEditRoom is a boolean if true is edit room otherwise edit zone
	 */
	private void populateControlPropertiesForRoomAndZone(String property, String value, Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties,
			boolean isEditRoom) {
		String[] propertyList = property.split(PhilipsConstant.HASH);
		String propertyGroup = propertyList[0];
		String key = propertyList[1];
		String group;
		List<String> deviceList;
		List<String> deviceDropdown;
		Map<String, String> mapOfDeviceDropdown;
		Optional<RoomAndZoneResponse> roomAndZoneResponse;
		if (isEditRoom) {
			group = propertyGroup.substring(PhilipsConstant.ROOM.length() + 1);
			roomAndZoneResponse = roomList.stream().filter(item -> item.getMetaData().getName().equals(group)).findFirst();
			deviceList = Arrays.stream(roomAndDropdownListControlMap.get(group)).collect(Collectors.toList());
			mapOfDeviceDropdown = deviceRoomControlMap.get(group);
			deviceDropdown = Arrays.stream(roomAndDropdownListControlMap.get(group)).collect(Collectors.toList());
		} else {
			group = propertyGroup.substring(PhilipsConstant.ZONE.length() + 1);
			roomAndZoneResponse = zoneList.stream().filter(item -> item.getMetaData().getName().equals(group)).findFirst();
			deviceList = deviceNameAndDeviceIdZoneMap.keySet().stream().collect(Collectors.toList());
			mapOfDeviceDropdown = zoneNameAndMapZoneDeviceControl.get(group);
			deviceDropdown = deviceNameAndDeviceIdZoneMap.keySet().stream().collect(Collectors.toList());
		}
		boolean isCurrentEmergencyDelivery = isEmergencyDelivery;
		isEmergencyDelivery = true;
		if (key.contains(PhilipsConstant.DEVICE) && !CreateRoomEnum.DEVICE_ADD.getRoomName().equals(key) && !RoomsAndZonesControlEnum.DEVICE_STATUS.getName().equals(key)) {
			updateDeviceDropdownList(property, value, stats, advancedControllableProperties, deviceList, mapOfDeviceDropdown, PhilipsConstant.DEVICE_0);
		} else {
			RoomsAndZonesControlEnum room = EnumTypeHandler.getMetricOfEnumByName(RoomsAndZonesControlEnum.class, key);
			switch (room) {
				case DEVICE_STATUS:
					String status = PhilipsConstant.TRUE;
					if (String.valueOf(PhilipsConstant.ZERO).equals(value)) {
						status = PhilipsConstant.FALSE;
					}
					if (roomAndZoneResponse.isPresent()) {
						sendRequestToChangeStatusForTheDevice(roomAndZoneResponse.get().getServices()[0].getId(), String.format(PhilipsConstant.PARAM_CHANGE_STATUS, status.toLowerCase(Locale.ROOT)), true);
					}
					updateValueForTheControllableProperty(property, value, stats, advancedControllableProperties);
					isEmergencyDelivery = isCurrentEmergencyDelivery;
					break;
				case NAME:
				case TYPE:
					updateValueForTheControllableProperty(property, value, stats, advancedControllableProperties);
					break;
				case ACTION:
					roomAndZoneResponse.ifPresent(andZoneResponse -> sendRequestToDeleteRoomAndZone(andZoneResponse.getId(), isEditRoom));
					isEmergencyDelivery = false;
					break;
				case DEVICE_ADD:
					addNewDeviceForRoomAndZone(propertyGroup, stats, advancedControllableProperties, deviceDropdown, mapOfDeviceDropdown);
					break;
				case CANCEL:
					isEmergencyDelivery = false;
					break;
				case APPLY_CHANGE:
					RoomAndZoneResponse roomValue;
					if (roomAndZoneResponse.isPresent()) {
						if (isEditRoom) {
							roomValue = convertRoomByValues(stats, propertyGroup + PhilipsConstant.HASH, mapOfDeviceDropdown, deviceNameAndMapDeviceIdOfRoomMap, true);
							roomValue.setId(roomAndZoneResponse.get().getId());
							sendRequestToCreateOrEditRoomAndZone(roomValue, true, true);
						} else {
							roomValue = convertZoneByValues(stats, propertyGroup + PhilipsConstant.HASH, mapOfDeviceDropdown, deviceNameAndDeviceIdZoneMap, true);
							roomValue.setId(roomAndZoneResponse.get().getId());
							sendRequestToCreateOrEditRoomAndZone(roomValue, false, true);
						}
					}
					isEmergencyDelivery = false;
					break;
				default:
					if (logger.isDebugEnabled()) {
						logger.debug(String.format("Controlling edit Room and Zone by name %s is not supported.", room.getName()));
					}
			}
		}
		if (isEmergencyDelivery) {
			stats.put(propertyGroup + PhilipsConstant.HASH + RoomsAndZonesControlEnum.APPLY_CHANGE.getName(), PhilipsConstant.EMPTY_STRING);
			advancedControllableProperties.add(createButton(propertyGroup + PhilipsConstant.HASH + RoomsAndZonesControlEnum.APPLY_CHANGE.getName(), PhilipsConstant.APPLY, PhilipsConstant.APPLYING, 0));
		}
		populateCancelChangeButton(stats, advancedControllableProperties, propertyGroup, isEmergencyDelivery);
	}

	/**
	 * Add new device for Room and Zone
	 *
	 * @param stats the stats are list of statistics
	 * @param advancedControllableProperties advancedControllableProperties is the list that store all controllable properties
	 * @param devicesName the nameList is list name device
	 * @param mapOfNameDevice the map name and id of device
	 */
	private void addNewDeviceForRoomAndZone(String groupName, Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties, List<String> devicesName,
			Map<String, String> mapOfNameDevice) {
		String[] deviceNameArray = devicesName.toArray(new String[0]);
		Arrays.sort(deviceNameArray);
		String prefixName = groupName + PhilipsConstant.HASH;
		//Check list device in room
		int countDevice = 0;
		for (Map.Entry<String, String> entry : mapOfNameDevice.entrySet()) {
			if (entry.getValue() != null) {
				//count the number of device
				countDevice++;
			}
		}
		if (countDevice >= devicesName.size() - 1) {
			throw new ResourceNotReachableException(
					String.format("Total device is %s, you have added enough devices and cannot add new devices. Please remove the device and try again", devicesName.size() - 1));
		}
		for (Map.Entry<String, String> deviceEntry : mapOfNameDevice.entrySet()) {
			String defaultName = PhilipsConstant.NONE;
			for (String nameValue : devicesName) {
				if (!mapOfNameDevice.containsValue(nameValue)) {
					defaultName = nameValue;
					break;
				}
			}
			if (deviceEntry.getValue() == null) {
				advancedControllableProperties.add(controlDropdown(stats, deviceNameArray, prefixName + deviceEntry.getKey(), defaultName));
				deviceEntry.setValue(defaultName);
				break;
			}
		}
	}

	/**
	 * Populate properties for create room
	 *
	 * @param property the property is property name with format GroupName#KeyName
	 * @param value the value is value of property
	 * @param stats the stats are list of statistics
	 * @param advancedControllableProperties advancedControllableProperties is the list that store all controllable properties
	 */
	private void populatePropertiesForCreateZone(String property, String value, Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {
		String[] propertyList = property.split(PhilipsConstant.HASH);
		String propertyGroup = propertyList[0];
		String key = propertyList[1];
		isCreateZone = true;
		if (key.contains(PhilipsConstant.DEVICE) && !CreateRoomEnum.DEVICE_ADD.getRoomName().equals(key)) {
			List<String> deviceDropdown = deviceNameAndDeviceIdZoneMap.keySet().stream().collect(Collectors.toList());
			updateDeviceDropdownList(property, value, stats, advancedControllableProperties, deviceDropdown, deviceZoneMap, PhilipsConstant.DEVICE_0);
		} else {
			RoomsAndZonesControlEnum zone = EnumTypeHandler.getMetricOfEnumByName(RoomsAndZonesControlEnum.class, key);
			switch (zone) {
				case DEVICE_STATUS:
				case NAME:
				case TYPE:
					updateValueForTheControllableProperty(property, value, stats, advancedControllableProperties);
					break;
				case CREATE:
					RoomAndZoneResponse roomConvertData = convertZoneByValues(stats, PhilipsConstant.CREATE_ZONE + PhilipsConstant.HASH, deviceZoneMap, deviceNameAndDeviceIdZoneMap, false);
					sendRequestToCreateOrEditRoomAndZone(roomConvertData, false, false);
					isCreateZone = false;
					break;
				case DEVICE_ADD:
					List<String> deviceDropdown = deviceNameAndDeviceIdZoneMap.keySet().stream().collect(Collectors.toList());
					addNewDeviceForRoomAndZone(propertyGroup, stats, advancedControllableProperties, deviceDropdown, deviceZoneMap);
					break;
				case CANCEL:
					isCreateZone = false;
					break;
				default:
					if (logger.isDebugEnabled()) {
						logger.debug(String.format("Controlling create Zone by name %s is not supported.", zone.getName()));
					}
			}
		}
		populateCancelChangeButton(stats, advancedControllableProperties, propertyGroup, isCreateZone);
	}

	/**
	 * Populate cancel change button and edit is true
	 *
	 * @param stats the stats are list of statistics
	 * @param advancedControllableProperties advancedControllableProperties is the list that store all controllable properties
	 * @param propertyGroup the propertyGroup is name of group
	 */
	private void populateCancelChangeButton(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties, String propertyGroup, boolean isCancelChangeButton) {
		if (isCancelChangeButton) {
			stats.put(propertyGroup + PhilipsConstant.HASH + PhilipsConstant.EDITED, PhilipsConstant.TRUE);
			stats.put(propertyGroup + PhilipsConstant.HASH + PhilipsConstant.CANCEL_CHANGE, PhilipsConstant.EMPTY_STRING);
			advancedControllableProperties.add(createButton(propertyGroup + PhilipsConstant.HASH + PhilipsConstant.CANCEL_CHANGE, PhilipsConstant.CANCEL, PhilipsConstant.CANCELING, 0));
		}
	}

	/**
	 * Send request to create/edit room and zone
	 *
	 * @param roomResponseData the roomResponseData is instance in RoomAndZoneResponse DTO
	 * @param isRoomType the isRoomType is boolean value
	 * @param isEditedRoomOrZone the isEditedRoomOrZone is boolean type if edit RoomOrZone then isEditedRoomOrZone = true and vice versa isEditedRoomOrZone = false
	 */
	private void sendRequestToCreateOrEditRoomAndZone(RoomAndZoneResponse roomResponseData, boolean isRoomType, boolean isEditedRoomOrZone) {
		String request = PhilipsUtil.getMonitorURL(PhilipsURL.ZONES);
		String name = PhilipsConstant.ZONE;
		if (isRoomType) {
			request = PhilipsUtil.getMonitorURL(PhilipsURL.ROOMS);
			name = PhilipsConstant.ROOM;
		}
		try {
			ResponseData responseData;
			if (isEditedRoomOrZone) {
				responseData = doPut(request + PhilipsConstant.SLASH + roomResponseData.getId(), roomResponseData.toString(), ResponseData.class);
			} else {
				responseData = doPost(request, roomResponseData.toString(), ResponseData.class);
			}
			if (responseData.getData() == null) {
				throw new ResourceNotReachableException(String.format("Error while creating/editing %s: ", name) + Arrays.stream(responseData.getErrors()).map(ErrorsResponse::getDescription));
			}
		} catch (Exception e) {
			throw new ResourceNotReachableException(String.format("Can't create/edit %s: ", name) + e.getMessage(), e);
		}
	}

	/**
	 * Send request to delete room and zone
	 *
	 * @param id the id is id of room and zone
	 * @param isDeleteRoom the boolean type if isDeleteRoom = true is delete the room vice versa isDeleteRoom = false is to delete zone
	 */
	private void sendRequestToDeleteRoomAndZone(String id, boolean isDeleteRoom) {
		String request = PhilipsUtil.getMonitorURL(PhilipsURL.ZONES);
		if (isDeleteRoom) {
			request = PhilipsUtil.getMonitorURL(PhilipsURL.ROOMS);
		}
		try {
			doDelete(request + PhilipsConstant.SLASH + id);
		} catch (Exception e) {
			throw new ResourceNotReachableException(String.format("Can't delete room/zone with ID: %s, %s", id, e.getMessage()), e);
		}
	}

	/**
	 * Convert Automation by value
	 *
	 * @param property is property name
	 * @param stats are list of statistics
	 * @param typeAndMapOfDeviceAndValues are map with key is type and value is map of device and value
	 * @return AutomationResponse DTO
	 */
	private AutomationResponse convertAutomationByValues(String property, Map<String, String> stats, Map<String, Map<String, String>> typeAndMapOfDeviceAndValues,
			Map<String, String> repeatNameOfAutomationMap) {
		AutomationResponse automationRequest = new AutomationResponse();
		String name = stats.get(property + PhilipsConstant.HASH + AutomationEnum.NAME.getName());
		String fadeDuration = stats.get(property + PhilipsConstant.HASH + AutomationEnum.FADE_DURATION.getName());
		String fadeDurationHour = stats.get(property + PhilipsConstant.HASH + AutomationEnum.FADE_DURATION_HOUR.getName());
		String fadeDurationMinute = stats.get(property + PhilipsConstant.HASH + AutomationEnum.FADE_DURATION_MINUTE.getName());
		String status = stats.get(property + PhilipsConstant.HASH + AutomationEnum.STATUS.getName());
		String timeCurrent = stats.get(property + PhilipsConstant.HASH + AutomationEnum.TIME_CURRENT.getName());
		String timeHour = stats.get(property + PhilipsConstant.HASH + AutomationEnum.TIME_HOUR.getName());
		String timeMinute = stats.get(property + PhilipsConstant.HASH + AutomationEnum.TIME_MINUTE.getName());
		String type = stats.get(property + PhilipsConstant.HASH + AutomationEnum.TYPE.getName());
		String typeOfAutomation = stats.get(property + PhilipsConstant.HASH + AutomationEnum.TYPE_OF_AUTOMATION.getName());
		String endBrightness = stats.get(property + PhilipsConstant.HASH + AutomationEnum.END_BRIGHTNESS.getName());
		String style = stats.get(property + PhilipsConstant.HASH + AutomationEnum.STYLE.getName());
		String endWith = stats.get(property + PhilipsConstant.HASH + AutomationEnum.END_WITH.getName());
		String repeat = stats.get(property + PhilipsConstant.HASH + AutomationEnum.REPEAT.getName());

		if (StringUtils.isNullOrEmpty(name) || StringUtils.isNullOrEmpty(name.trim())) {
			throw new ResourceNotReachableException("Error while creating/editing automation, Automation name can't empty or space");
		}
		MetaData metaData = new MetaData();
		metaData.setName(name);
		automationRequest.setMetaData(metaData);
		AutoConfiguration config = new AutoConfiguration();
		FadeDuration fadeDurationDTO = new FadeDuration();
		config.setEndBrightness(endBrightness);
		if (!StringUtils.isNullOrEmpty(style)) {
			config.setStyle(style.toLowerCase(Locale.ROOT));
		}
		TimeAndRepeat timeAndRepeat = new TimeAndRepeat();
		List<String> days = new LinkedList<>();
		if (String.valueOf(PhilipsConstant.NUMBER_ONE).equals(repeat)) {
			for (Entry<String, String> day : repeatNameOfAutomationMap.entrySet()) {
				String dayValue = day.getValue();
				if (!StringUtils.isNullOrEmpty(dayValue) && !PhilipsConstant.NONE.equals(dayValue) && !days.contains(dayValue.toLowerCase(Locale.ROOT))) {
					days.add(dayValue.toLowerCase(Locale.ROOT));
				}
			}
		}
		automationRequest.setEnabled(PhilipsConstant.TRUE);
		if (String.valueOf(PhilipsConstant.ZERO).equals(status)) {
			automationRequest.setEnabled(PhilipsConstant.FALSE);
		}
		convertDeviceForRoomAndZone(typeAndMapOfDeviceAndValues, type, config);
		automationRequest.setConfigurations(config);

		if (TypeOfAutomation.TIMER.getName().equals(typeOfAutomation)) {
			int durationHour = Integer.parseInt(fadeDurationHour);
			int durationMinute = Integer.parseInt(fadeDurationMinute);
			if (durationHour == 0 && durationMinute == 0) {
				throw new ResourceNotReachableException("Error while creating automation, When FadeDurationHour = 0 then the FadeDurationMinute cannot be 0");
			}
			//Parsing fade duration hurationHour * 3600 + durationMinute * 60
			int duration = durationHour * 3600 + durationMinute * 60;
			fadeDurationDTO.setSeconds(String.valueOf(duration));
			config.setDuration(fadeDurationDTO);
		}
		String scriptName = PhilipsConstant.TIMERS;
		if (TypeOfAutomation.GO_TO_SLEEP.getName().equals(typeOfAutomation)) {
			scriptName = PhilipsConstant.GO_TO_SLEEPS;
			fadeDurationDTO.setSeconds(fadeDuration);
			config.setFadeOutDuration(fadeDurationDTO);

			String[] dayArrays = days.toArray(new String[0]);
			timeAndRepeat.setDays(dayArrays);
			TimePoint timePoint = new TimePoint();
			CurrentTime currentTime = new CurrentTime();
			if (String.valueOf(PhilipsConstant.NUMBER_ONE).equals(timeCurrent)) {
				int time = Integer.parseInt(timeHour);
				timeHour = String.valueOf(time);
				if (time != 12) {
					timeHour = String.valueOf(time + 12);
				}
			} else {
				int time = Integer.parseInt(timeHour);
				if (time == 12) {
					timeHour = String.valueOf(0);
				}
			}
			currentTime.setHour(String.valueOf(Integer.valueOf(timeHour)));
			currentTime.setMinute(String.valueOf(Integer.valueOf(timeMinute)));
			timePoint.setTimes(currentTime);
			timePoint.setType(PhilipsConstant.TIME.toLowerCase(Locale.ROOT));
			timeAndRepeat.setTimePoint(timePoint);
			config.setTimeAndRepeats(timeAndRepeat);
			config.setEndWith(EndStateEnum.getValueOfEnumByName(endWith));
		}
		if (TypeOfAutomation.WAKE_UP_WITH_LIGHT.getName().equals(typeOfAutomation)) {
			scriptName = PhilipsConstant.WAKE_UP_WITH_LIGHT;
			fadeDurationDTO.setSeconds(fadeDuration);
			config.setFadeInDuration(fadeDurationDTO);
			String[] dayArrays = days.toArray(new String[0]);
			timeAndRepeat.setDays(dayArrays);
			TimePoint timePoint = new TimePoint();
			CurrentTime currentTime = new CurrentTime();
			if (String.valueOf(PhilipsConstant.NUMBER_ONE).equals(timeCurrent)) {
				int time = Integer.parseInt(timeHour) + 12;
				timeHour = String.valueOf(time);
			}
			currentTime.setHour(String.valueOf(Integer.valueOf(timeHour)));
			currentTime.setMinute(String.valueOf(Integer.valueOf(timeMinute)));
			timePoint.setTimes(currentTime);
			timePoint.setType(PhilipsConstant.TIME.toLowerCase(Locale.ROOT));
			timeAndRepeat.setTimePoint(timePoint);
			config.setTimeAndRepeats(timeAndRepeat);
		}

		// check name automation exits
		if (!PhilipsConstant.CREATE_AUTOMATION.equals(property)) {
			String nameValue = property.substring(property.indexOf(PhilipsConstant.DASH) + 1);
			Optional<AutomationResponse> automationResponse = automationList.stream().filter(item -> item.getMetaData().getName().equals(nameValue)).findFirst();
			automationResponse.ifPresent(response -> automationRequest.setId(response.getId()));
			if (!name.equals(nameValue)) {
				isAutomationNameExisting(name);
			}
		} else {
			String finalScriptName = scriptName;
			Optional<ScriptAutomationResponse> timer = scriptAutomationList.stream().filter(item -> item.getMetadata().getName().equalsIgnoreCase(finalScriptName)).findFirst();
			timer.ifPresent(scriptAutomationResponse -> automationRequest.setScriptId(scriptAutomationResponse.getId()));
			isAutomationNameExisting(name);
		}
		return automationRequest;
	}

	/**
	 * Convert device list for room and zone by value
	 *
	 * @param typeAndMapOfDeviceAndValues is map of type and device maps
	 * @param type the type is type of TypeOfDevice instance
	 * @param autoConfiguration is AutoConfiguration DTO instance
	 */
	private void convertDeviceForRoomAndZone(Map<String, Map<String, String>> typeAndMapOfDeviceAndValues, String type, AutoConfiguration autoConfiguration) {
		List<Location> locationList = new LinkedList<>();
		//Extract device list by type is device
		if (TypeOfDeviceEnum.DEVICE.getName().equals(type)) {
			Map<String, String> mapOfDevice = typeAndMapOfDeviceAndValues.get(PhilipsConstant.DEVICE);
			if (mapOfDevice != null) {
				Map<String, Map<Group, List<Group>>> nameOfGroupMap = new HashMap<>();
				List<Group> newItems = new LinkedList<>();
				List<Group> oldItems = new LinkedList<>();
				for (Entry<String, String> deviceEntry : mapOfDevice.entrySet()) {
					Group group = new Group();
					Map<Group, List<Group>> roomAndListChildren = new HashMap<>();
					String deviceEntryValue = deviceEntry.getValue();
					if (!StringUtils.isNullOrEmpty(deviceEntryValue) && !PhilipsConstant.NONE.equals(deviceEntryValue)) {
						String idLightDevice = deviceNameAndDeviceIdZoneMap.get(deviceEntry.getValue());
						Entry<String, Map<String, String>> idDeviceInRoom = deviceNameAndMapDeviceIdOfRoomMap.entrySet().stream().filter(item -> item.getValue().containsValue(idLightDevice))
								.findFirst().orElse(null);
						String key = PhilipsConstant.EMPTY_STRING;
						if (idDeviceInRoom != null) {
							key = String.valueOf(idDeviceInRoom.getValue().keySet().toArray()[0]);
						}
						String finalKey = key;
						Optional<RoomAndZoneResponse> room = roomList.stream().filter(item -> Arrays.stream(item.getChildren()).map(Children::getRid).collect(Collectors.toList()).contains(finalKey)).findFirst();
						if (room.isPresent()) {
							RoomAndZoneResponse roomAndZoneResponse = room.get();
							group.setId(roomAndZoneResponse.getId());
							group.setType(PhilipsConstant.ROOM.toLowerCase(Locale.ROOT));
							Group item = new Group();
							item.setId(idLightDevice);
							item.setType(PhilipsConstant.LIGHT);
							if (!oldItems.stream().map(Group::getId).collect(Collectors.toList()).contains(item.getId())) {
								oldItems.add(item);
								roomAndListChildren.put(group, oldItems);
								String name = roomAndZoneResponse.getMetaData().getName();
								if (nameOfGroupMap.size() == 0) {
									nameOfGroupMap.put(name, roomAndListChildren);
								} else {
									Map<Group, List<Group>> groupAndDevice = nameOfGroupMap.get(name);
									if (groupAndDevice == null) {
										nameOfGroupMap.put(name, roomAndListChildren);
									} else {
										if (groupAndDevice.entrySet().stream().map(groupListEntry -> groupListEntry.getKey().getId()).collect(Collectors.toList()).contains(group.getId())) {
											for (Entry<Group, List<Group>> groupListEntry : groupAndDevice.entrySet()) {
												if (groupListEntry.getKey().getId().equals(group.getId())) {
													groupAndDevice.putIfAbsent(groupListEntry.getKey(), oldItems);
													break;
												}
											}
										} else {
											groupAndDevice.put(group, oldItems);
										}
									}
								}
							}
						} else {
							//device can't be assigned in room with type bridge hơm
							GroupLightResponse bridgeId = groupLightList.stream().filter(item -> item.getOwner().getType().equals(PhilipsConstant.BRIDGE_HOME)).findFirst().orElse(null);

							//bridge Id always exists, so no need to check for null
							String id = bridgeId.getOwner().getRid();
							if (nameOfGroupMap.size() == 0) {
								group.setId(id);
								group.setType(PhilipsConstant.BRIDGE_HOME);
								Group item = new Group();
								item.setId(idLightDevice);
								item.setType(PhilipsConstant.LIGHT);
								if (oldItems.stream().map(Group::getId).collect(Collectors.toList()).contains(item.getId())) {
									continue;
								}
								oldItems.add(item);
								roomAndListChildren.put(group, oldItems);
								nameOfGroupMap.put(id, roomAndListChildren);
							} else {
								Map<Group, List<Group>> groupAndDevice = nameOfGroupMap.get(id);
								group.setId(id);
								group.setType(PhilipsConstant.BRIDGE_HOME);
								Group item = new Group();
								item.setId(idLightDevice);
								item.setType(PhilipsConstant.LIGHT);
								if (oldItems.stream().map(Group::getId).collect(Collectors.toList()).contains(item.getId())) {
									continue;
								}
								//Copy add device in room to bridge hone type
								//Handle case device can't be assigned in room
								if (groupAndDevice == null) {
									if (nameOfGroupMap.size() > 0) {
										for (Entry<String, Map<Group, List<Group>>> maps : nameOfGroupMap.entrySet()) {
											for (List<Group> groupList : maps.getValue().values()) {
												for (Group groupItem : groupList) {
													//copy all device in room tp bridge
													newItems.add(groupItem);
												}
											}
											//remove all group with room type
											nameOfGroupMap.remove(maps.getKey());
										}
										oldItems = newItems;
									}
									oldItems.add(item);
									roomAndListChildren.put(group, oldItems);
									nameOfGroupMap.put(id, roomAndListChildren);
								} else {
									if (groupAndDevice.entrySet().stream().map(groupListEntry -> groupListEntry.getKey().getId()).collect(Collectors.toList()).contains(group.getId())) {
										for (Entry<Group, List<Group>> groupListEntry : groupAndDevice.entrySet()) {
											if (groupListEntry.getKey().getId().equals(group.getId())) {
												oldItems.add(item);
												roomAndListChildren.put(group, oldItems);
												groupAndDevice.putIfAbsent(groupListEntry.getKey(), oldItems);
												break;
											}
										}
									} else {
										groupAndDevice.put(group, oldItems);
									}
								}
							}
						}
					}
				}
				for (Entry<String, Map<Group, List<Group>>> deviceList : nameOfGroupMap.entrySet()) {
					for (Entry<Group, List<Group>> deviceDetail : deviceList.getValue().entrySet()) {
						Location location = new Location();
						location.setGroup(deviceDetail.getKey());
						location.setItems(deviceDetail.getValue().toArray(new Group[0]));
						locationList.add(location);
					}
				}
			}
			if (locationList.isEmpty()) {
				throw new ResourceNotReachableException("Error creating device automation cannot be empty. Please select the device to create automation");
			}
			autoConfiguration.setLocation(locationList.toArray(new Location[0]));
		}

		//Extract device list by type is room
		if (TypeOfDeviceEnum.ROOM.getName().equals(type)) {
			Map<String, String> mapOfDevice = typeAndMapOfDeviceAndValues.get(PhilipsConstant.ROOM);
			if (mapOfDevice != null) {
				Map<String, Group> nameOfGroupMap = new HashMap<>();
				for (Entry<String, String> deviceEntry : mapOfDevice.entrySet()) {
					Group group = new Group();
					if (!StringUtils.isNullOrEmpty(deviceEntry.getValue()) && !PhilipsConstant.NONE.equals(deviceEntry.getValue())) {
						String idDevice = roomNameAndIdMap.get(deviceEntry.getValue());
						RoomAndZoneResponse room;
						if (deviceEntry.getValue().contains(PhilipsConstant.ROOM_NO_ASSIGNED_DEVICE)) {
							String name = deviceEntry.getValue().substring(deviceEntry.getValue().indexOf(PhilipsConstant.DASH) + 1);
							room = roomList.stream().filter(item -> item.getMetaData().getName().equalsIgnoreCase(name)).findFirst().orElse(null);
						} else {
							room = roomList.stream().filter(item -> item.getId().contains(idDevice)).findFirst().orElse(null);
						}
						if (room != null) {
							String roomName = room.getMetaData().getName();
							if (nameOfGroupMap.size() == 0) {
								group.setId(room.getId());
								group.setType(PhilipsConstant.ROOM.toLowerCase(Locale.ROOT));
								nameOfGroupMap.put(roomName, group);
							} else {
								Group groupAndDevice = nameOfGroupMap.get(roomName);
								if (groupAndDevice == null) {
									group.setId(room.getId());
									group.setType(PhilipsConstant.ROOM.toLowerCase(Locale.ROOT));
									nameOfGroupMap.put(roomName, group);
								}
							}
						}
					}
				}
				for (Entry<String, Group> deviceList : nameOfGroupMap.entrySet()) {
					Location locationItem = new Location();
					locationItem.setGroup(deviceList.getValue());
					locationList.add(locationItem);
				}
				if (locationList.isEmpty()) {
					throw new ResourceNotReachableException("Error creating room for automation cannot be empty. Please select the room to create automation");
				}
				autoConfiguration.setLocation(locationList.toArray(new Location[0]));
			}
		}

		//Extract device list by type is Zone
		if (TypeOfDeviceEnum.ZONE.getName().equals(type)) {
			Map<String, String> mapOfDevice = typeAndMapOfDeviceAndValues.get(PhilipsConstant.ZONE);
			if (mapOfDevice != null) {
				Map<String, Group> nameOfGroupMap = new HashMap<>();

				for (Entry<String, String> deviceEntry : mapOfDevice.entrySet()) {
					Group group = new Group();
					if (!StringUtils.isNullOrEmpty(deviceEntry.getValue()) && !PhilipsConstant.NONE.equals(deviceEntry.getValue())) {
						String idDevice = zoneNameAndIdMap.get(deviceEntry.getValue());
						RoomAndZoneResponse zone;
						if (deviceEntry.getValue().contains(PhilipsConstant.ZONE_NO_ASSIGNED_DEVICE)) {
							String name = deviceEntry.getValue().substring(deviceEntry.getValue().indexOf(PhilipsConstant.DASH) + 1);
							zone = zoneList.stream().filter(item -> item.getMetaData().getName().equalsIgnoreCase(name)).findFirst().orElse(null);
						} else {
							zone = zoneList.stream().filter(item -> item.getId().contains(idDevice)).findFirst().orElse(null);
						}
						if (zone != null) {
							if (nameOfGroupMap.size() == 0) {
								group.setId(zone.getId());
								group.setType(PhilipsConstant.ZONE.toLowerCase(Locale.ROOT));
								nameOfGroupMap.put(zone.getMetaData().getName(), group);
							} else {
								Group groupAndDevice = nameOfGroupMap.get(zone.getMetaData().getName());
								if (groupAndDevice == null) {
									group.setId(zone.getId());
									group.setType(PhilipsConstant.ZONE.toLowerCase(Locale.ROOT));
									nameOfGroupMap.put(zone.getMetaData().getName(), group);
								}
							}
						}
					}
				}
				for (Entry<String, Group> deviceList : nameOfGroupMap.entrySet()) {
					Location location = new Location();
					location.setGroup(deviceList.getValue());
					locationList.add(location);
				}
				if (locationList.isEmpty()) {
					throw new ResourceNotReachableException("Error creating zone for automation cannot be empty. Please select the zone to create automation");
				}
				autoConfiguration.setLocation(locationList.toArray(new Location[0]));
			}
		}
	}

	/**
	 * Convert time value with AM and PM value
	 * Example: time = 16h => time = 16-12 = 04h(PM)
	 * if time < 10 time = 0 + time
	 *
	 * @param hour the time is time value with index to 1-24
	 * @return time converted or initial time
	 */
	private String convertTimeFormat(int hour) {
		try {
			if (hour == 0) {
				return String.valueOf(12);
			}
			if (hour > 12) {
				hour = hour - 12;
			}
			return hour < 10 ? String.valueOf(PhilipsConstant.ZERO) + hour : String.valueOf(hour);
		} catch (Exception e) {
			return String.valueOf(hour);
		}
	}

	/**
	 * Convert Room by value
	 *
	 * @param stats the stats are list of statistics
	 * @param property the property is property name
	 * @param deviceDropdown is map of device name
	 * @param mapOfNameAndId is map of name and id device
	 * @param isEditRoom is boolean value if isEditRoom =true then edit the room, isEditRoom = false then create the room
	 * @return RoomAndZoneResponse is instance in RoomAndZoneResponseDTO
	 */
	private RoomAndZoneResponse convertRoomByValues(Map<String, String> stats, String property, Map<String, String> deviceDropdown, Map<String, Map<String, String>> mapOfNameAndId,
			boolean isEditRoom) {
		String name = stats.get(property + RoomsAndZonesControlEnum.NAME.getName());
		String type = stats.get(property + RoomsAndZonesControlEnum.TYPE.getName());

		List<String> id = new LinkedList<>();
		List<Children> newListChildren = new LinkedList<>();
		for (String value : deviceDropdown.values()) {
			Children children = new Children();
			if (!PhilipsConstant.NONE.equals(value) && !StringUtils.isNullOrEmpty(value)) {
				for (Entry<String, String> idDevice : mapOfNameAndId.get(value).entrySet()) {
					if (!id.contains(idDevice.getKey())) {
						children.setRid(idDevice.getKey());
						children.setType(PhilipsConstant.DEVICE.toLowerCase(Locale.ROOT));
						newListChildren.add(children);
						id.add(idDevice.getKey());
					}
				}
			}
		}
		if (StringUtils.isNullOrEmpty(name) || StringUtils.isNullOrEmpty(name.trim())) {
			throw new ResourceNotReachableException("Error while creating Room, Room name can't empty");
		}
		if (PhilipsConstant.NONE.equals(type)) {
			throw new ResourceNotReachableException("Error while creating Room, Room type can't empty");
		}
		if (isEditRoom) {
			String nameValue = property.substring(PhilipsConstant.ROOM.length() + 1, property.indexOf(PhilipsConstant.HASH));
			if (!PhilipsConstant.CREATE_ZONE.equals(name) && !name.equals(nameValue)) {
				isRoomAndZoneNameExisting(name, true);
			} else if (!PhilipsConstant.CREATE_ROOM.equals(nameValue) && !name.equals(nameValue)) {
				isRoomAndZoneNameExisting(name, true);
			}
		} else {
			isRoomAndZoneNameExisting(name, true);
		}
		type = RoomTypeEnum.getValueOfRoomTypeEnumByName(type);
		MetaData metaData = new MetaData();
		metaData.setArchetype(type);
		metaData.setName(name);
		RoomAndZoneResponse response = new RoomAndZoneResponse();
		response.setChildren(newListChildren.toArray(new Children[0]));
		response.setType(PhilipsConstant.ROOM.toLowerCase(Locale.ROOT));
		response.setMetaData(metaData);
		return response;
	}

	/**
	 * Check Name exits for room and zone
	 *
	 * @param name the name is name of room/zone
	 * @param isRoomType the is isRoomType is boolean value
	 */
	private void isRoomAndZoneNameExisting(String name, boolean isRoomType) {
		String request = PhilipsUtil.getMonitorURL(PhilipsURL.ROOMS);
		if (!isRoomType) {
			request = PhilipsUtil.getMonitorURL(PhilipsURL.ZONES);
		}
		try {
			RoomAndZoneWrapper zoneWrapper = this.doGet(request, RoomAndZoneWrapper.class);
			if (zoneWrapper != null && zoneWrapper.getData() != null && Arrays.stream(zoneWrapper.getData()).anyMatch(item -> item.getMetaData().getName().equals(name))) {
				throw new ResourceNotReachableException(String.format("The name: %s already exists", name));
			}
		} catch (Exception e) {
			throw new ResourceNotReachableException(String.format("Error when create Room/Zone: %s", e.getMessage()), e);
		}
	}

	/**
	 * Check Name exits for room and zone
	 *
	 * @param name the name is name of room/zone
	 */
	private void isAutomationNameExisting(String name) {
		try {
			AutomationWrapper automationWrapper = this.doGet(PhilipsUtil.getMonitorURL(PhilipsURL.AUTOMATION), AutomationWrapper.class);
			if (automationWrapper != null && automationWrapper.getData() != null && Arrays.stream(automationWrapper.getData()).anyMatch(item -> item.getMetaData().getName().equals(name))) {
				throw new ResourceNotReachableException(String.format("The name: %s already exists", name));
			}
		} catch (Exception e) {
			throw new ResourceNotReachableException(String.format("Error when create isAutomationNameExisting: %s", e.getMessage()), e);
		}
	}

	/**
	 * Convert Zone by value
	 *
	 * @param stats the stats are list of statistics
	 * @param property the property is property name
	 * @param mapOfDevice is map of device name
	 * @param mapOfNameAndId is map of name and id device
	 * @param isEditZone is boolean value if isEditZone =true then edit the zone, isEditZone = false then create the zone
	 * @return RoomAndZoneResponse is instance in RoomAndZoneResponseDTO
	 */
	private RoomAndZoneResponse convertZoneByValues(Map<String, String> stats, String property, Map<String, String> mapOfDevice, Map<String, String> mapOfNameAndId, boolean isEditZone) {
		String name = stats.get(property + RoomsAndZonesControlEnum.NAME.getName());
		String type = stats.get(property + RoomsAndZonesControlEnum.TYPE.getName());
		List<String> id = new LinkedList<>();
		List<Children> deviceIdList = new LinkedList<>();
		for (String value : mapOfDevice.values()) {
			Children children = new Children();
			if (!PhilipsConstant.NONE.equals(value) && !StringUtils.isNullOrEmpty(value)) {
				for (Entry<String, String> idDevice : mapOfNameAndId.entrySet()) {
					if (!id.contains(idDevice.getValue()) && value.equals(idDevice.getKey())) {
						children.setRid(idDevice.getValue());
						children.setType(PhilipsConstant.LIGHT.toLowerCase(Locale.ROOT));
						deviceIdList.add(children);
						id.add(idDevice.getValue());
						break;
					}
				}
			}
		}
		if (StringUtils.isNullOrEmpty(name) || StringUtils.isNullOrEmpty(name.trim())) {
			throw new ResourceNotReachableException("Error while creating Zone, Zone name can't empty or space");
		}
		if (PhilipsConstant.NONE.equals(type)) {
			throw new ResourceNotReachableException("Error while creating Zone, Zone type can't empty");
		}
		if (isEditZone) {
			String nameValue = property.substring(PhilipsConstant.ZONE.length() + 1, property.indexOf(PhilipsConstant.HASH));
			if (!PhilipsConstant.CREATE_ZONE.equals(name) && !name.equals(nameValue)) {
				isRoomAndZoneNameExisting(name, false);
			} else if (!PhilipsConstant.CREATE_ROOM.equals(nameValue) && !name.equals(nameValue)) {
				isRoomAndZoneNameExisting(name, false);
			}
		} else {
			isRoomAndZoneNameExisting(name, false);
		}
		type = RoomTypeEnum.getValueOfRoomTypeEnumByName(type);
		MetaData metaData = new MetaData();
		metaData.setArchetype(type);
		metaData.setName(name);
		RoomAndZoneResponse response = new RoomAndZoneResponse();
		response.setChildren(deviceIdList.toArray(new Children[0]));
		response.setType(PhilipsConstant.ZONE.toLowerCase(Locale.ROOT));
		response.setMetaData(metaData);
		return response;
	}

	/**
	 * Retrieve list zones
	 */
	private void retrieveZones() {
		try {
			RoomAndZoneWrapper zoneWrapper = this.doGet(PhilipsUtil.getMonitorURL(PhilipsURL.ZONES), RoomAndZoneWrapper.class);
			if (zoneWrapper != null && zoneWrapper.getData() != null) {
				Collections.addAll(zoneList, zoneWrapper.getData());
				for (RoomAndZoneResponse zone : zoneList) {
					if (zone.getChildren().length > 0) {
						zoneNameAndIdMap.put(PhilipsConstant.ALL_DEVICE_IN_ZONE + PhilipsConstant.DASH + zone.getMetaData().getName(), zone.getId());
					}
				}
				zoneNameAndIdMap.put(PhilipsConstant.NONE, PhilipsConstant.NONE);
			} else {
				throw new ResourceNotReachableException("List zones data is empty");
			}
		} catch (Exception e) {
			logger.error(String.format("Error while retrieving list zones %s", e.getMessage()), e);
		}
	}

	/**
	 * Retrieve list of lights
	 *
	 * @return Map of service id and device id
	 */
	private Map<String, String> retrieveLights() {
		List<LightResponse> lightResponseList = new ArrayList<>();
		Map<String, String> serviceIdAndDeviceId = new HashMap<>();
		try {
			LightWrapper lightWrapper = this.doGet(PhilipsUtil.getMonitorURL(PhilipsURL.LIGHT), LightWrapper.class);
			if (lightWrapper != null && lightWrapper.getData() != null) {
				Collections.addAll(lightResponseList, lightWrapper.getData());
			} else {
				throw new ResourceNotReachableException("List lights data is empty");
			}
		} catch (Exception e) {
			logger.error(String.format("Error while retrieving list lights %s", e.getMessage()), e);
		}
		for (LightResponse lightResponse : lightResponseList) {
			serviceIdAndDeviceId.put(lightResponse.getServiceId(), lightResponse.getOwner().getRid());
		}
		return serviceIdAndDeviceId;
	}

	/**
	 * Retrieve all rooms
	 */
	private void retrieveGroupLight() {
		try {
			GroupLightWrapper groupLightWrapper = this.doGet(PhilipsUtil.getMonitorURL(PhilipsURL.GROUP_LIGHT), GroupLightWrapper.class);
			if (groupLightWrapper != null && groupLightWrapper.getData() != null) {
				Collections.addAll(groupLightList, groupLightWrapper.getData());
				for (GroupLightResponse groupLightResponse : groupLightWrapper.getData()) {
					groupLightMap.put(groupLightResponse.getOwner().getRid(), groupLightResponse);
				}
			} else {
				throw new ResourceNotReachableException("List group light data is empty");
			}
		} catch (Exception e) {
			logger.error(String.format("Error while retrieving group light %s", e.getMessage()), e);
		}
	}

	/**
	 * Retrieve all group_light
	 */
	private void retrieveRooms() {
		try {
			RoomAndZoneWrapper roomAndZoneWrapper = this.doGet(PhilipsUtil.getMonitorURL(PhilipsURL.ROOMS), RoomAndZoneWrapper.class);
			if (roomAndZoneWrapper != null && roomAndZoneWrapper.getData() != null) {
				Collections.addAll(roomList, roomAndZoneWrapper.getData());
				for (RoomAndZoneResponse room : roomList) {
					if (room.getChildren().length > 0) {
						roomNameAndIdMap.put(PhilipsConstant.ALL_DEVICE_IN_ROOM + PhilipsConstant.DASH + room.getMetaData().getName(), room.getId());
					}
				}
				roomNameAndIdMap.put(PhilipsConstant.NONE, PhilipsConstant.NONE);
			} else {
				throw new ResourceNotReachableException("List rooms data is empty");
			}
		} catch (Exception e) {
			logger.error(String.format("Error while retrieving list rooms %s", e.getMessage()), e);
		}
	}

	/**
	 * Retrieve all automation
	 */
	private void retrieveAutomations() {
		try {
			AutomationWrapper automationWrapper = this.doGet(PhilipsUtil.getMonitorURL(PhilipsURL.AUTOMATION), AutomationWrapper.class);
			automationList.clear();
			if (automationWrapper != null && automationWrapper.getData() != null) {
				Collections.addAll(automationList, automationWrapper.getData());
			} else {
				throw new ResourceNotReachableException("List automation data is empty");
			}
		} catch (Exception e) {
			logger.error(String.format("Error while retrieving list automation %s", e.getMessage()), e);
		}
	}

	/**
	 * Retrieve all script id
	 */
	private void retrieveScriptIdForAutomation() {
		try {
			ScriptAutomationWrapper automationWrapper = this.doGet(PhilipsUtil.getMonitorURL(PhilipsURL.SCRIPT_ID), ScriptAutomationWrapper.class);
			if (automationWrapper != null && automationWrapper.getData() != null) {
				Collections.addAll(scriptAutomationList, automationWrapper.getData());
				for (ScriptAutomationResponse scriptAutomationResponse : automationWrapper.getData()) {
					idAndNameOfAutomationMap.put(scriptAutomationResponse.getId(), scriptAutomationResponse.getMetadata().getName());
				}
			} else {
				throw new ResourceNotReachableException("List script id of automation data is empty");
			}
		} catch (Exception e) {
			logger.error(String.format("Error while retrieving list script id for automation %s", e.getMessage()), e);
		}
	}

	/**
	 * Retrieve list maps of device: map device in room, map device in zone
	 */
	private void retrieveDeviceDropdownList() {
		try {
			AggregatorWrapper systemResponse = this.doGet(PhilipsUtil.getMonitorURL(PhilipsURL.DEVICE), AggregatorWrapper.class);
			if (systemResponse == null || systemResponse.getData() == null) {
				throw new ResourceNotReachableException("List aggregated device is empty");
			}
			for (AggregatedDeviceResponse aggregatorDeviceResponse : systemResponse.getData()) {
				boolean isDeviceExitsInRoom = false;
				ServicesResponse servicesData = aggregatorDeviceResponse.getServices()[0];
				String serviceDeviceID = servicesData.getId();
				String serviceType = servicesData.getType();
				String aggregatorDeviceID = aggregatorDeviceResponse.getId();
				String aggregatorDeviceName = aggregatorDeviceResponse.getMetaData().getName();
				for (RoomAndZoneResponse response : roomList) {
					List<String> deviceList = Arrays.stream(response.getChildren()).map(Children::getRid).collect(Collectors.toList());
					if (PhilipsConstant.LIGHT.equalsIgnoreCase(serviceType) && deviceList.contains(aggregatorDeviceID)) {
						deviceExitsInRoomMap.put(aggregatorDeviceName, PhilipsConstant.TRUE);
						deviceNameAndDeviceIdZoneMap.put(aggregatorDeviceName + PhilipsConstant.DASH + response.getMetaData().getName(), serviceDeviceID);
						isDeviceExitsInRoom = true;
						break;
					}
				}
				if (!isDeviceExitsInRoom && PhilipsConstant.LIGHT.equalsIgnoreCase(serviceType)) {
					deviceNameAndDeviceIdZoneMap.put(aggregatorDeviceName, serviceDeviceID);
					deviceExitsInRoomMap.put(aggregatorDeviceName, PhilipsConstant.FALSE);
				}
				if (!PhilipsConstant.BRIDGE.equalsIgnoreCase(serviceType) && PhilipsConstant.LIGHT.equalsIgnoreCase(serviceType)) {
					Map<String, String> idDeviceMap = new HashMap<>();
					idDeviceMap.put(aggregatorDeviceID, serviceDeviceID);
					deviceNameAndMapDeviceIdOfRoomMap.put(aggregatorDeviceName, idDeviceMap);
					allDeviceIdAndNameMap.put(aggregatorDeviceID, aggregatorDeviceName);
					if (roomList.isEmpty()) {
						deviceExitsInRoomMap.put(aggregatorDeviceName, PhilipsConstant.FALSE);
						deviceNameAndDeviceIdZoneMap.put(aggregatorDeviceName, serviceDeviceID);
					}
				}
			}
			deviceNameAndMapDeviceIdOfRoomMap.put(PhilipsConstant.NONE, new HashMap<>());
			deviceNameAndDeviceIdZoneMap.put(PhilipsConstant.NONE, PhilipsConstant.NONE);
			allDeviceIdAndNameMap.put(PhilipsConstant.NONE, PhilipsConstant.NONE);
			deviceExitsInRoomMap.put(PhilipsConstant.NONE, PhilipsConstant.FALSE);
		} catch (Exception e) {
			String errorMessage = String.format("Aggregated device Retrieval-Error: %s with cause: %s", e.getMessage(), e.getCause().getMessage());
			logger.error(errorMessage, e);
		}
	}

	/**
	 * Retrieve all Aggregated device
	 */
	private void retrieveDevices() {
		try {
			AggregatorWrapper systemResponse = this.doGet(PhilipsUtil.getMonitorURL(PhilipsURL.DEVICE), AggregatorWrapper.class);
			if (systemResponse == null || systemResponse.getData() == null) {
				throw new ResourceNotReachableException("List aggregated device is empty");
			}
			for (AggregatedDeviceResponse aggregatorDeviceResponse : systemResponse.getData()) {
				listMetadataDevice.put(aggregatorDeviceResponse.getId(), aggregatorDeviceResponse);
				AggregatedDevice aggregatedDevice = new AggregatedDevice();
				Map<String, String> properties = new HashMap<>();
				if (!PhilipsConstant.BRIDGE.equalsIgnoreCase(aggregatorDeviceResponse.getServices()[0].getType())) {
					properties.put(PhilipsConstant.SOFTWARE_VERSION, aggregatorDeviceResponse.getProductData().getVersion());
					properties.put(PhilipsConstant.DEVICE_TYPE, aggregatorDeviceResponse.getServices()[0].getType());
					properties.put(PhilipsConstant.PRODUCT_NAME, aggregatorDeviceResponse.getProductData().getName());
					aggregatedDevice.setDeviceId(aggregatorDeviceResponse.getId());
					aggregatedDevice.setDeviceModel(aggregatorDeviceResponse.getProductData().getModel());
					aggregatedDevice.setDeviceType(aggregatorDeviceResponse.getServices()[0].getType());
					aggregatedDevice.setDeviceName(aggregatorDeviceResponse.getMetaData().getName());
					aggregatedDevice.setProperties(properties);
					aggregatedDeviceList.put(aggregatedDevice.getDeviceId(), aggregatedDevice);
				}
			}
		} catch (Exception e) {
			String errorMessage = String.format("Aggregated device Retrieval-Error: %s with cause: %s", e.getMessage(), e.getCause().getMessage());
			logger.error(errorMessage, e);
		}
	}

	/**
	 * Filter By Ids device
	 */
	private void filterDeviceIds() {
		ConcurrentHashMap<String, AggregatedDevice> listOfDeviceAfterFiltered = new ConcurrentHashMap<>();
		Children[] listOfDeviceInsideZone = listServiceIdInZone();

		if (listOfDeviceInsideZone == null || listOfDeviceInsideZone.length == 0) {
			aggregatedDeviceList = new ConcurrentHashMap<>();
			listMetadataDevice = new ConcurrentHashMap<>();
			zoneListAfterFilter = new ArrayList<>();
			roomListAfterFilter = new ArrayList<>();
			return;
		}
		List<String> listRidInZone = new ArrayList<>();
		for (Children device : listOfDeviceInsideZone) {
			String serviceId = device.getRid();
			listRidInZone.add(serviceId);
		}
		// Filter by Zone Case roomNames is specified:
		if (StringUtils.isNotNullOrEmpty(roomNames)) {
			listOfDeviceAfterFiltered = filterByZone(listRidInZone);
			if (listOfDeviceAfterFiltered.isEmpty()) {
				aggregatedDeviceList = new ConcurrentHashMap<>();
				return;
			}
		} else {
			Map<String, String> serviceAndDeviceId = retrieveLights();
			for (String s : listRidInZone) {
				String deviceId = serviceAndDeviceId.get(s);
				if (deviceId != null) {
					listOfDeviceAfterFiltered.put(deviceId, aggregatedDeviceList.get(deviceId));
				}
			}
		}
		// Filter by Room
		try {
			if (StringUtils.isNotNullOrEmpty(roomNames)) {
				String[] handledRoomName = roomNames.split(PhilipsConstant.COMMA);
				List<String> listOfChildrenNodeInsideRoom = new ArrayList<>();
				for (String roomNameItem : handledRoomName) {
					for (RoomAndZoneResponse roomAndZoneResponse : roomList) {
						String roomName = roomAndZoneResponse.getMetaData().getName();
						if (roomNameItem.trim().equals(roomName)) {
							roomListAfterFilter.add(roomAndZoneResponse);
							Children[] tempList = roomAndZoneResponse.getChildren();
							for (Children children : tempList) {
								listOfChildrenNodeInsideRoom.add(children.getRid());
							}
						}
					}
				}
				if (!listOfChildrenNodeInsideRoom.isEmpty()) {
					listOfDeviceAfterFiltered.clear();
					for (String childrenId : listOfChildrenNodeInsideRoom) {
						AggregatedDeviceResponse aggregatedDeviceResponse = listMetadataDevice.get(childrenId);
						for (ServicesResponse service : aggregatedDeviceResponse.getServices()) {
							if (listRidInZone.contains(service.getId())) {
								// If deviceTypes and deviceNames are not specified then we return all devices in room
								String deviceId = aggregatedDeviceResponse.getId();
								listOfDeviceAfterFiltered.put(deviceId, aggregatedDeviceList.get(deviceId));
							}
						}
					}
				} else {
					// If there's no room that's match then it's empty list.
					listOfDeviceAfterFiltered.clear();
				}
			}
			// Ignore else condition, all device will be returned if filter by room name isn't specified.
		} catch (Exception e) {
			logger.error("Fail to filter device by room name", e);
			aggregatedDeviceList = new ConcurrentHashMap<>();
			listMetadataDevice = new ConcurrentHashMap<>();
			zoneListAfterFilter = new ArrayList<>();
			roomListAfterFilter = new ArrayList<>();
		}
		// Populate devices where type isn't light to the listOfDeviceAfterFiltered. Because other device --
		// types (motion, button,etc.) aren't belongs to any zones/rooms, so we only filter by types, names for these types.
		for (Entry<String, AggregatedDevice> device : aggregatedDeviceList.entrySet()) {
			if (!PhilipsConstant.LIGHT.equals(device.getValue().getDeviceType())) {
				listOfDeviceAfterFiltered.put(device.getKey(), device.getValue());
			}
		}
		// filter by type
		if (StringUtils.isNotNullOrEmpty(deviceTypes)) {
			listOfDeviceAfterFiltered = filterByDeviceType(listOfDeviceAfterFiltered);
		}
		if (StringUtils.isNotNullOrEmpty(deviceNames)) {
			// filter by name
			listOfDeviceAfterFiltered = filterByDeviceName(listOfDeviceAfterFiltered);
		}
		aggregatedDeviceList = listOfDeviceAfterFiltered;
		for (AggregatedDevice device : aggregatedDeviceList.values()) {
			deviceIds.add(device.getDeviceId());
		}
	}

	/**
	 * Filter device by zone
	 *
	 * @return Filtered list of device by zone.
	 */
	private ConcurrentHashMap<String, AggregatedDevice> filterByZone(List<String> listRidInZone) {
		ConcurrentHashMap<String, AggregatedDevice> listOfDeviceAfterFiltered = new ConcurrentHashMap<>();
		List<String> listOfChildrenNodeInsideRoom = new ArrayList<>();
		for (RoomAndZoneResponse roomAndZoneResponse : roomList) {
			Children[] tempList = roomAndZoneResponse.getChildren();
			for (Children children : tempList) {
				listOfChildrenNodeInsideRoom.add(children.getRid());
			}
		}
		for (String childrenId : listOfChildrenNodeInsideRoom) {
			AggregatedDeviceResponse aggregatedDeviceResponse = listMetadataDevice.get(childrenId);
			for (ServicesResponse service : aggregatedDeviceResponse.getServices()) {
				if (listRidInZone.contains(service.getId())) {
					String deviceId = aggregatedDeviceResponse.getId();
					listOfDeviceAfterFiltered.put(deviceId, aggregatedDeviceList.get(deviceId));
				}
			}
		}
		return listOfDeviceAfterFiltered;
	}

	/**
	 * Get list of service ids inside zone
	 */
	private Children[] listServiceIdInZone() {
		int indexOfZoneToBeUse = -1;
		try {
			if (zoneList.isEmpty()) {
				return null;
			}
			for (int indexZone = 0; indexZone < zoneList.size(); indexZone++) {
				if (zoneList.get(indexZone).getMetaData().getName().equals(zoneName)) {
					indexOfZoneToBeUse = indexZone;
					break;
				} else {
					indexOfZoneToBeUse = 0;
				}
			}
		} catch (Exception e) {
			indexOfZoneToBeUse = 0;
			logger.error(String.format("Error while handling zoneName adapter property with value: %s. Using first index of zoneList instead.", zoneName), e);
		}
		// return services of zone list
		zoneListAfterFilter.add(zoneList.get(indexOfZoneToBeUse));
		return zoneList.get(indexOfZoneToBeUse).getChildren();
	}

	/**
	 * Filter By device name
	 *
	 * @param listOfDeviceAfterFiltered are map with key is deivce name and value is Aggregated device
	 */
	private ConcurrentHashMap<String, AggregatedDevice> filterByDeviceName(ConcurrentHashMap<String, AggregatedDevice> listOfDeviceAfterFiltered) {
		ConcurrentHashMap<String, AggregatedDevice> listOfDeviceAfterFilteredName = new ConcurrentHashMap<>();
		try {
			String[] names = deviceNames.split(PhilipsConstant.COMMA);
			for (String name : names) {
				for (Entry<String, AggregatedDevice> device : listOfDeviceAfterFiltered.entrySet()
				) {
					String deviceKey = device.getKey();
					AggregatedDevice deviceValue = device.getValue();
					String deviceName = deviceValue.getDeviceName();
					if (deviceName.equals(name.trim())) {
						listOfDeviceAfterFilteredName.put(deviceKey, deviceValue);
					}
				}
			}
			return listOfDeviceAfterFilteredName;
		} catch (Exception e) {
			logger.error("Error while filtering by device name", e);
			return new ConcurrentHashMap<>();
		}
	}

	/**
	 * Filter by device type
	 *
	 * @param listOfDeviceAfterFiltered are map with key is deivce name and value is Aggregated device
	 */
	private ConcurrentHashMap<String, AggregatedDevice> filterByDeviceType(ConcurrentHashMap<String, AggregatedDevice> listOfDeviceAfterFiltered) {
		ConcurrentHashMap<String, AggregatedDevice> listOfDeviceAfterFilteredType = new ConcurrentHashMap<>();
		try {
			String[] types = deviceTypes.split(PhilipsConstant.COMMA);
			for (String type : types) {
				for (Entry<String, AggregatedDevice> device : listOfDeviceAfterFiltered.entrySet()
				) {
					String deviceKey = device.getKey();
					AggregatedDevice deviceValue = device.getValue();
					String deviceType = deviceValue.getDeviceType();
					if (!deviceType.equalsIgnoreCase(PhilipsConstant.BRIDGE) && deviceType.equals(type.trim())) {
						listOfDeviceAfterFilteredType.put(deviceKey, deviceValue);
					}
				}
			}
			return listOfDeviceAfterFilteredType;
		} catch (Exception e) {
			logger.error("Error while filtering by device type", e);
			return new ConcurrentHashMap<>();
		}
	}

	/**
	 * Get list bridge id
	 *
	 * API Endpoint: /clip/v2/resource/bridge
	 * Success: return list bridge id
	 */
	private void retrieveListBridgeId() {
		try {
			BridgeWrapper bridgeWrapper = this.doGet(PhilipsUtil.getMonitorURL(PhilipsURL.BRIDGE), BridgeWrapper.class);
			if (bridgeWrapper != null && bridgeWrapper.getData() != null) {
				for (BridgeListResponse id : bridgeWrapper.getData()) {
					if (id.getOwner() != null) {
						bridgeIdList.add(id.getOwner().getRid());
					}
				}
			} else {
				throw new ResourceNotReachableException("List bridge id is empty");
			}
		} catch (Exception e) {
			logger.error(String.format("Error while retrieving list bridge id for automation %s", e.getMessage()), e);
		}
	}

	/**
	 * Get network information
	 * API Endpoint: /username/config
	 * Success: return network information
	 *
	 * @param stats the stats are list of stats
	 */
	private void retrieveNetworkInfo(Map<String, String> stats) {
		try {
			NetworkInfoResponse networkInfoResponse = this.doGet(PhilipsUtil.getMonitorURL(PhilipsURL.API) + this.getPassword() + PhilipsUtil.getMonitorURL(PhilipsURL.CONFIG), NetworkInfoResponse.class);
			if (networkInfoResponse != null) {
				for (NetworkInfoEnum networkInfoEnum : NetworkInfoEnum.values()) {
					stats.put(networkInfoEnum.getName(), checkForNullData(networkInfoResponse.getValueByMetric(networkInfoEnum)));
				}
			} else {
				setNoneValueForNetworkInfo(stats);
				throw new ResourceNotReachableException("Network Information is empty");
			}
		} catch (Exception e) {
			setNoneValueForNetworkInfo(stats);
			logger.error(String.format("Error while retrieving network information: %s", e.getMessage()), e);
		}
	}

	/**
	 * Set value for list statistics property of network info is none
	 *
	 * @param stats list of statistics
	 */
	private void setNoneValueForNetworkInfo(Map<String, String> stats) {
		for (NetworkInfoEnum networkInfoEnum : NetworkInfoEnum.values()) {
			stats.put(networkInfoEnum.getName(), PhilipsConstant.NONE);
		}
	}

	/**
	 * Get system information
	 *
	 * API Endpoint: /clip/v2/resource/device/{id_device}
	 * Success: return system information
	 */
	private void retrieveSystemInfoByBridgeIdList(Map<String, String> stats) {
		try {
			if (bridgeIdList.isEmpty()) {
				contributeNoneValueBySystemInfo(stats);
			} else {
				for (String id : bridgeIdList) {
					SystemWrapper systemWrapper = this.doGet(PhilipsUtil.getMonitorURL(PhilipsURL.DEVICE) + PhilipsConstant.SLASH + id, SystemWrapper.class);
					if (systemWrapper != null && systemWrapper.getData() != null) {
						for (SystemResponse systemResponse : systemWrapper.getData()) {
							String groupName = PhilipsConstant.EMPTY_STRING;
							if (systemWrapper.getData().length > 1) {
								groupName = systemResponse.getProductData().getName() + PhilipsConstant.HASH;
							}
							for (SystemInfoEnum systemInfoEnum : SystemInfoEnum.values()) {
								String name = systemInfoEnum.getName();
								if (SystemInfoEnum.ID.getName().equals(name)) {
									stats.put(groupName + name, systemResponse.getId());
									continue;
								}
								if (SystemInfoEnum.TYPE.getName().equals(name)) {
									ServicesResponse[] services = systemResponse.getServices();
									for (ServicesResponse servicesResponse : services) {
										String type = servicesResponse.getType();
										if (PhilipsConstant.BRIDGE.equals(type)) {
											stats.put(groupName + name, PhilipsConstant.BRIDGE);
										} else if (PhilipsConstant.ZIGBEE_CONNECTIVITY.equals(type)) {
											retrieveZigbeeConnectivity(groupName, servicesResponse.getId(), stats);
										}
									}
								} else {
									ProductData productData = systemResponse.getProductData();
									if (productData != null) {
										stats.put(groupName + name, checkForNullData(productData.getValueByMetric(systemInfoEnum)));
									}
								}
							}
						}
					} else {
						contributeNoneValueBySystemInfo(stats);
					}
				}
			}
		} catch (Exception e) {
			logger.error(String.format("Error while retrieving system information %s", e.getMessage()), e);
		}
	}

	/**
	 * Value of list statistics property of System info is none
	 *
	 * @param stats list statistics
	 */
	private void contributeNoneValueBySystemInfo(Map<String, String> stats) {
		for (SystemInfoEnum systemInfoEnum : SystemInfoEnum.values()) {
			stats.put(systemInfoEnum.getName(), PhilipsConstant.NONE);
		}
	}

	/**
	 * Retrieve Status for the device
	 *
	 * @param groupName the groupName is name of group with format {bridgeName}#
	 * @param id the id is id of bridge
	 * @param stats the stats are list of statistics
	 */
	private void retrieveZigbeeConnectivity(String groupName, String id, Map<String, String> stats) {
		try {
			ZigbeeConnectivityWrapper zigbeeConnectivityWrapper = this.doGet(PhilipsUtil.getMonitorURL(PhilipsURL.ZIGBEE_CONNECTIVITY) + id, ZigbeeConnectivityWrapper.class);
			if (zigbeeConnectivityWrapper != null && zigbeeConnectivityWrapper.getData() != null) {
				ZigbeeConnectivity zigbeeConnectivity = zigbeeConnectivityWrapper.getData()[0];
				if (zigbeeConnectivity != null) {
					stats.put(groupName + PhilipsConstant.DEVICE_CONNECTED, checkForNullData(zigbeeConnectivity.getStatus()));
				} else {
					stats.put(groupName + PhilipsConstant.DEVICE_CONNECTED, PhilipsConstant.NONE);
				}
			} else {
				throw new ResourceNotReachableException("Status for the device is empty");
			}
		} catch (Exception e) {
			logger.error(String.format("Error while retrieving Status for the device %s", e.getMessage()), e);
		}
	}

	/**
	 * check for null data
	 *
	 * @param value the value of monitoring properties
	 * @return String (none/value)
	 */
	private String checkForNullData(String value) {
		return StringUtils.isNullOrEmpty(value) ? PhilipsConstant.NONE : value;
	}

	/**
	 * Get value by range if the value out of range return the initial value
	 *
	 * @param min is the minimum value
	 * @param max is the maximum value
	 * @param value is the value to compare between min and max value
	 * @return int is value or initial value
	 */
	private int getValueByRange(int min, int max, String value) {
		int initial = min;
		try {
			int valueCompare = Integer.parseInt(value);
			if (min <= valueCompare && valueCompare <= max) {
				return valueCompare;
			}
			if (valueCompare > max) {
				initial = max;
			}
			return initial;
		} catch (Exception e) {
			//example value 1xxxxxxx, return max value
			//example value -1xxxxxxx, return min value
			if (!value.contains(PhilipsConstant.DASH)) {
				initial = max;
			}
			return initial;
		}
	}
	// control ------------------------------------------------------------------------------------------------------------

	/**
	 * Add dropdown is control property for metric
	 *
	 * @param stats list statistic
	 * @param options list select
	 * @param name String name of metric
	 * @return AdvancedControllableProperty dropdown instance if add dropdown success else will is null
	 */
	private AdvancedControllableProperty controlDropdown(Map<String, String> stats, String[] options, String name, String value) {
		stats.put(name, value);
		return createDropdown(name, options, value);
	}

	/***
	 * Create dropdown advanced controllable property
	 *
	 * @param name the name of the control
	 * @param initialValue initial value of the control
	 * @return AdvancedControllableProperty dropdown instance
	 */
	private AdvancedControllableProperty createDropdown(String name, String[] values, String initialValue) {
		AdvancedControllableProperty.DropDown dropDown = new AdvancedControllableProperty.DropDown();
		dropDown.setOptions(values);
		dropDown.setLabels(values);

		return new AdvancedControllableProperty(name, new Date(), dropDown, initialValue);
	}

	/**
	 * Add/Update advancedControllableProperties if  advancedControllableProperties different empty
	 *
	 * @param advancedControllableProperties advancedControllableProperties is the list that store all controllable properties
	 * @param property the property is item advancedControllableProperties
	 */
	private void addOrUpdateAdvanceControlProperties(List<AdvancedControllableProperty> advancedControllableProperties, AdvancedControllableProperty property) {
		if (property != null) {
			advancedControllableProperties.removeIf(item -> item.getName().equals(property.getName()));
			advancedControllableProperties.add(property);
		}
	}

	/**
	 * Add text or numeric is control property for metric
	 *
	 * @param stats list statistic
	 * @param name String name of metric
	 * @return AdvancedControllableProperty text and numeric instance
	 */
	private AdvancedControllableProperty controlTextOrNumeric(Map<String, String> stats, String name, String value, boolean isNumeric) {
		stats.put(name, value);

		return isNumeric ? createNumeric(name, value) : createText(name, value);
	}

	/**
	 * Create Numeric is control property for metric
	 *
	 * @param name the name of the property
	 * @param initialValue the initialValue is number
	 * @return AdvancedControllableProperty Numeric instance
	 */
	private AdvancedControllableProperty createNumeric(String name, String initialValue) {
		AdvancedControllableProperty.Numeric numeric = new AdvancedControllableProperty.Numeric();

		return new AdvancedControllableProperty(name, new Date(), numeric, initialValue);
	}

	/**
	 * Create text is control property for metric
	 *
	 * @param name the name of the property
	 * @param stringValue character string
	 * @return AdvancedControllableProperty Text instance
	 */
	private AdvancedControllableProperty createText(String name, String stringValue) {
		AdvancedControllableProperty.Text text = new AdvancedControllableProperty.Text();

		return new AdvancedControllableProperty(name, new Date(), text, stringValue);
	}

	/**
	 * Add switch is control property for metric
	 *
	 * @param stats list statistic
	 * @param name String name of metric
	 * @return AdvancedControllableProperty switch instance
	 */
	private AdvancedControllableProperty controlSwitch(Map<String, String> stats, String name, String value, String labelOff, String labelOn) {
		if (StringUtils.isNullOrEmpty(value)) {
			value = PhilipsConstant.NONE;
		}
		stats.put(name, value);
		if (!PhilipsConstant.NONE.equals(value) && !StringUtils.isNullOrEmpty(value)) {
			return createSwitch(name, Integer.parseInt(value), labelOff, labelOn);
		}
		// if response data is null or none. Only display monitoring data not display controlling data
		return null;
	}

	/**
	 * Create switch is control property for metric
	 *
	 * @param name the name of property
	 * @param status initial status (0|1)
	 * @return AdvancedControllableProperty switch instance
	 */
	private AdvancedControllableProperty createSwitch(String name, int status, String labelOff, String labelOn) {
		AdvancedControllableProperty.Switch toggle = new AdvancedControllableProperty.Switch();
		toggle.setLabelOff(labelOff);
		toggle.setLabelOn(labelOn);

		AdvancedControllableProperty advancedControllableProperty = new AdvancedControllableProperty();
		advancedControllableProperty.setName(name);
		advancedControllableProperty.setValue(status);
		advancedControllableProperty.setType(toggle);
		advancedControllableProperty.setTimestamp(new Date());

		return advancedControllableProperty;
	}

	/**
	 * Create a button.
	 *
	 * @param name name of the button
	 * @param label label of the button
	 * @param labelPressed label of the button after pressing it
	 * @param gracePeriod grace period of button
	 * @return This returns the instance of {@link AdvancedControllableProperty} type Button.
	 */
	private AdvancedControllableProperty createButton(String name, String label, String labelPressed, long gracePeriod) {
		AdvancedControllableProperty.Button button = new AdvancedControllableProperty.Button();
		button.setLabel(label);
		button.setLabelPressed(labelPressed);
		button.setGracePeriod(gracePeriod);
		return new AdvancedControllableProperty(name, new Date(), button, PhilipsConstant.EMPTY_STRING);
	}

	/**
	 * Update the value for the control metric
	 *
	 * @param property is name of the metric
	 * @param value the value is value of properties
	 * @param extendedStatistics list statistics property
	 * @param advancedControllableProperties the advancedControllableProperties is list AdvancedControllableProperties
	 */
	private void updateValueForTheControllableProperty(String property, String value, Map<String, String> extendedStatistics, List<AdvancedControllableProperty> advancedControllableProperties) {
		for (AdvancedControllableProperty advancedControllableProperty : advancedControllableProperties) {
			if (advancedControllableProperty.getName().equals(property)) {
				extendedStatistics.put(property, value);
				advancedControllableProperty.setValue(value);
				break;
			}
		}
	}

	/**
	 * Create control slider is control property for the metric
	 *
	 * @param name the name of the metric
	 * @param value the value of the metric
	 * @param rangeStart is the starting number of the range
	 * @param rangeEnd is the end number of the range
	 * @return AdvancedControllableProperty slider instance
	 */
	private AdvancedControllableProperty createSlider(String name, Float value, String rangeStart, String rangeEnd) {
		AdvancedControllableProperty.Slider slider = new AdvancedControllableProperty.Slider();
		slider.setLabelEnd(String.valueOf(rangeEnd));
		slider.setLabelStart(String.valueOf(rangeStart));
		slider.setRangeEnd(Float.valueOf(rangeEnd));
		slider.setRangeStart(Float.valueOf(rangeStart));

		return new AdvancedControllableProperty(name, new Date(), slider, value);
	}

	/**
	 * Create control slider is control property for the metric
	 *
	 * @param name name of the slider
	 * @param stats list of statistics
	 * @param rangeStart is the starting number of the range
	 * @param rangeEnd is the end number of the range
	 * @return AdvancedControllableProperty slider instance if add slider success else will is null
	 */
	private AdvancedControllableProperty createControlSlider(String name, String value, Map<String, String> stats, String rangeStart, String rangeEnd) {
		stats.put(name, value);
		return createSlider(name, Float.valueOf(value), rangeStart, rangeEnd);
	}
}