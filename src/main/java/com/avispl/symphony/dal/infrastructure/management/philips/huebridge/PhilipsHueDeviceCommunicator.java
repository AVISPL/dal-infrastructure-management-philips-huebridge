/**
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.CollectionUtils;

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
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.AutomationEnum;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.CreateRoomEnum;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.EndState;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.EnumTypeHandler;
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
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.TypeOfAutomation;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.TypeOfDeviceEnum;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.AggregatorWrapper;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.AutomationWrapper;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.BridgeWrapper;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.GroupLightWrapper;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.NetworkInfoResponse;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.ResponseData;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.RoomAndZoneWrapper;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.ScriptAutomationWrapper;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.SystemWrapper;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.ZigbeeConnectivityWrapper;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.aggregateddevice.AggregatedDeviceResponse;
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
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.grouplight.GroupLightResponse;
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
				logger.debug("Fetching other than SmartThings device list" + threadIndex);
			}

			//TODO implement retrieve device details
			//			if (!cachedDevices.isEmpty()) {
			//				retrieveDeviceDetail(threadIndex);
			//			}
			if (logger.isDebugEnabled()) {
				logger.debug("Finished collecting devices statistics cycle at " + new Date());
			}
		}
		// Finished collecting
	}

	//The properties adapter
	private String zoneName;
	private String roomNames;
	private String deviceTypes;
	private String deviceNames;

	private ExtendedStatistics localExtendedStatistics;
	private ExtendedStatistics localCreateZone;
	private ExtendedStatistics localCreateRoom;
	private ExtendedStatistics localCreateAutomation;
	private boolean isCreateAutomation;
	private boolean isCreateRoom;
	private boolean isCreateZone;
	private boolean isEmergencyDelivery;
	private Map<String, String> localCreateRoomStats = new HashMap<>();
	private Map<String, String> localCreateZoneStats = new HashMap<>();
	private Map<String, String> localCreateAutomationStats = new HashMap<>();

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

	private final Map<String, String> repeatNameOfCreateAutomationMap = new HashMap<>();
	private final Map<String, Map<String, String>> repeatNameOfAutomationMap = new HashMap<>();
	private final Map<String, String> roomNameAndIdMap = new HashMap<>();
	private final Map<String, String> zoneNameAndIdMap = new HashMap<>();
	private Map<String, Map<String, String>> typeAndMapOfDeviceAndValue;
	private Map<String, Map<String, Map<String, String>>> automationAndTypeMapOfDeviceAndValue;


	/**
	 * List of ID bridge
	 */
	private final Set<String> bridgeIdList = new LinkedHashSet<>();

	/**
	 * List of aggregated device
	 */
	private final List<AggregatedDevice> aggregatedDeviceList = new ArrayList<>();

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
	 * List of Zones
	 */
	private final List<RoomAndZoneResponse> zoneList = new ArrayList<>();

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
	 * Philips Hue API Token
	 */
	private String apiToken;

	/**
	 * ReentrantLock to prevent null pointer exception to localExtendedStatistics when controlProperty method is called before GetMultipleStatistics method.
	 */
	private final ReentrantLock reentrantLock = new ReentrantLock();

	/**
	 * Runner service responsible for collecting data
	 */
	private PhilipsHueDeviceDataLoader deviceDataLoader;

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
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void controlProperty(ControllableProperty controllableProperty) {
		String property = controllableProperty.getProperty();
		String value = String.valueOf(controllableProperty.getValue());

		reentrantLock.lock();
		try {
			if (localExtendedStatistics == null || localCreateRoom == null || localCreateZone == null) {
				if (logger.isDebugEnabled()) {
					logger.debug(String.format("Error while controlling %s metric", property));
				}
				return;
			}
			String[] propertyList = property.split(PhilipsConstant.HASH);
			String propertyGroup = propertyList[0];
			Map<String, String> updateCreateRoom = localCreateRoom.getStatistics();
			List<AdvancedControllableProperty> updateCreateRoomControllableProperties = localCreateRoom.getControllableProperties();
			Map<String, String> updateCreateZone = localCreateZone.getStatistics();
			List<AdvancedControllableProperty> updateCreateZoneControllableProperties = localCreateZone.getControllableProperties();
			Map<String, String> updateCreateAutomation = localCreateAutomation.getStatistics();
			List<AdvancedControllableProperty> updateCreateAutomationControllableProperties = localCreateAutomation.getControllableProperties();

			Map<String, String> localStats = localExtendedStatistics.getStatistics();
			List<AdvancedControllableProperty> localControllableProperties = localExtendedStatistics.getControllableProperties();

			if (PhilipsConstant.CREATE_AUTOMATION.equals(propertyGroup)) {
				populateCreateAutomation(property, value, updateCreateAutomation, updateCreateAutomationControllableProperties);
				localCreateAutomationStats.putAll(updateCreateAutomation);
			} else if (PhilipsConstant.CREATE_ROOM.equals(propertyGroup)) {
				populateCreateRoom(property, value, updateCreateRoom, updateCreateRoomControllableProperties);
				localCreateRoomStats.putAll(updateCreateRoom);
			} else if (PhilipsConstant.CREATE_ZONE.equals(propertyGroup)) {
				populateCreateZone(property, value, updateCreateZone, updateCreateZoneControllableProperties);
				localCreateZoneStats.putAll(updateCreateZone);
			} else {
				if (PhilipsConstant.ROOM.equals(property.substring(0, PhilipsConstant.ROOM.length()))) {
					populateControlRoomAndZone(property, value, localStats, localControllableProperties, true);
				}
				if (PhilipsConstant.ZONE.equals(property.substring(0, PhilipsConstant.ZONE.length()))) {
					populateControlRoomAndZone(property, value, localStats, localControllableProperties, false);
				}
			}
		} finally {
			reentrantLock.unlock();
		}
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

			if (!isEmergencyDelivery) {
				clearBeforeFetchingData();
				retrieveNetworkInfo(stats);
				retrieveZones();
				retrieveRooms();
				retrieveGroupLight();
				retrieveDevices();
				retrieveAutomations();
				retrieveScriptAutomation();
				retrieveListBridgeId();
				retrieveSystemInfoByBridgeIdList(stats);
				populateControl(stats, advancedControllableProperties);

				extendedStatistics.setStatistics(stats);
				extendedStatistics.setControllableProperties(advancedControllableProperties);
				localExtendedStatistics = extendedStatistics;
			}

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
				if (typeAndMapOfDeviceAndValue == null) {
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

			Map<String, String> localStats = localExtendedStatistics.getStatistics();
			List<AdvancedControllableProperty> localAdvancedControl = localExtendedStatistics.getControllableProperties();

			updateLocalExtendedByValue(localStats, localAdvancedControl, localCreateRoom, localCreateRoomStats);
			updateLocalExtendedByValue(localStats, localAdvancedControl, localCreateZone, localCreateZoneStats);
			updateLocalExtendedByValue(localStats, localAdvancedControl, localCreateAutomation, localCreateAutomationStats);
		} finally {
			reentrantLock.unlock();
		}
		return Collections.singletonList(localExtendedStatistics);
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
			Map<String, String> localCreatStats) {
		// add all stats of create room/zone into local stats
		Map<String, String> localRoomStats = localCreateExtended.getStatistics();
		stats.keySet().removeIf(item -> localCreatStats.containsKey(item));
		stats.putAll(localRoomStats);

		List<AdvancedControllableProperty> localCreateRoomControl = localCreateExtended.getControllableProperties();
		advancedControllableProperties.removeIf(item -> localCreatStats.containsKey(item.getName()));
		advancedControllableProperties.addAll(localCreateRoomControl);
		localCreatStats.clear();
		localCreatStats.putAll(localRoomStats);
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
		if (logger.isWarnEnabled()) {
			logger.warn("Start call retrieveMultipleStatistic");
		}
		return aggregatedDeviceList.stream().collect(Collectors.toList());
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
				case FADE_DURATION:
					AdvancedControllableProperty nameControlProperty = controlTextOrNumeric(stats, property, String.valueOf(PhilipsConstant.DEFAULT_FADE_DURATION), true);
					addOrUpdateAdvanceControlProperties(createAutomationControllableProperties, nameControlProperty);
					break;
				case NAME:
					stats.put(property, PhilipsConstant.EMPTY_STRING);
					createAutomationControllableProperties.add(createText(property, PhilipsConstant.EMPTY_STRING));
					break;
				case REPEAT:
					stats.put(property, PhilipsConstant.EMPTY_STRING);
					createAutomationControllableProperties.add(controlSwitch(stats, property, String.valueOf(PhilipsConstant.ZERO), PhilipsConstant.DISABLE, PhilipsConstant.ENABLE));
					break;
				case STATUS:
					stats.put(property, PhilipsConstant.EMPTY_STRING);
					createAutomationControllableProperties.add(controlSwitch(stats, property, String.valueOf(PhilipsConstant.ZERO), PhilipsConstant.OFFLINE, PhilipsConstant.ONLINE));
					break;
				case TIME_CURRENT:
					stats.put(property, PhilipsConstant.EMPTY_STRING);
					createAutomationControllableProperties.add(controlSwitch(stats, property, String.valueOf(PhilipsConstant.ZERO), PhilipsConstant.TIME_AM, PhilipsConstant.TIME_PM));
					break;
				case TIME_HOUR:
					String[] hourDropdown = EnumTypeHandler.getEnumNames(TimeHourEnum.class);
					AdvancedControllableProperty hourControlProperty = controlDropdown(stats, hourDropdown, property, TimeHourEnum.TIME_01.getName());
					addOrUpdateAdvanceControlProperties(createAutomationControllableProperties, hourControlProperty);
					break;
				case TIME_MINUTE:
					String[] timeMinuteDropdown = EnumTypeHandler.getEnumNames(TimeMinuteEnum.class);
					AdvancedControllableProperty timeMinuteControlProperty = controlDropdown(stats, timeMinuteDropdown, property, TimeMinuteEnum.MINUTE_00.getName());
					addOrUpdateAdvanceControlProperties(createAutomationControllableProperties, timeMinuteControlProperty);
					break;
				case TYPE:
					String[] typeDropdown = EnumTypeHandler.getEnumNames(TypeOfDeviceEnum.class);
					AdvancedControllableProperty typeControlProperty = controlDropdown(stats, typeDropdown, property, TypeOfDeviceEnum.NONE.getName());
					addOrUpdateAdvanceControlProperties(createAutomationControllableProperties, typeControlProperty);
					break;
				case TYPE_OF_AUTOMATION:
					String[] typeOfAutomationDropdown = EnumTypeHandler.getEnumNames(TypeOfAutomation.class);
					AdvancedControllableProperty typeOfAutomationControlProperty = controlDropdown(stats, typeOfAutomationDropdown, property, TypeOfAutomation.TIMER.getName());
					addOrUpdateAdvanceControlProperties(createAutomationControllableProperties, typeOfAutomationControlProperty);
					break;
				case REPEAT_ADD:
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
		aggregatedDeviceList.clear();
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
	}

	/**
	 * Populate control
	 *
	 * @param stats the stats are list of statistics
	 * @param advancedControllableProperties advancedControllableProperties is the list that store all controllable properties
	 */
	private void populateControl(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {
		for (RoomAndZoneResponse roomItem : roomList) {
			String groupName = PhilipsConstant.ROOM + PhilipsConstant.DASH + roomItem.getMetaData().getName();
			populateDetailsRoomAndZone(stats, advancedControllableProperties, roomItem, groupName, true);
		}
		for (RoomAndZoneResponse zoneItem : zoneList) {
			String groupName = PhilipsConstant.ZONE + PhilipsConstant.DASH + zoneItem.getMetaData().getName();
			populateDetailsRoomAndZone(stats, advancedControllableProperties, zoneItem, groupName, false);
		}
		for (AutomationResponse auto : automationList) {
			populateDetailsAutomation(stats, advancedControllableProperties, auto);
		}
	}

	private void populateDetailsAutomation(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties, AutomationResponse auto) {
		String groupName = PhilipsConstant.AUTOMATION + PhilipsConstant.DASH + auto.getMetaData().getName();
		for (AutomationEnum automation : AutomationEnum.values()) {
			String value;
			String autoName = auto.getMetaData().getName();
			String property = groupName + PhilipsConstant.HASH + automation.getName();
			switch (automation) {
				case NAME:
					stats.put(property, autoName);
					advancedControllableProperties.add(createText(property, autoName));
					break;
				case FADE_DURATION:
					FadeDuration fadeDuration = auto.getConfigurations().getDuration();
					FadeDuration fadeInDuration = auto.getConfigurations().getFadeInDuration();
					FadeDuration fadeOutDuration = auto.getConfigurations().getFadeOutDuration();
					String fadeDurationValue = "";
					if (fadeDuration != null) {
						fadeDurationValue = fadeDuration.getSeconds();
					} else if (fadeInDuration != null) {
						fadeDurationValue = fadeInDuration.getSeconds();
					} else if (fadeOutDuration != null) {
						fadeDurationValue = fadeOutDuration.getSeconds();
					}
					if (StringUtils.isNullOrEmpty(fadeDurationValue)) {
						fadeDurationValue = String.valueOf(getValueByRange(PhilipsConstant.MIN_FADE_DURATION, PhilipsConstant.MAX_FADE_DURATION, fadeDurationValue));
						AdvancedControllableProperty nameControllableProperty = controlTextOrNumeric(stats, property, fadeDurationValue, true);
						addOrUpdateAdvanceControlProperties(advancedControllableProperties, nameControllableProperty);
					}
					break;
				case STATUS:
					int status = 0;
					value = auto.getEnabled();
					if (PhilipsConstant.TRUE.equalsIgnoreCase(value)) {
						status = 1;
					}
					stats.put(property, PhilipsConstant.EMPTY_STRING);
					advancedControllableProperties.add(controlSwitch(stats, property, String.valueOf(status), PhilipsConstant.OFFLINE, PhilipsConstant.ONLINE));
					break;
				case TIME_HOUR:
					String[] hourDropdown = EnumTypeHandler.getEnumNames(TimeHourEnum.class);
					if (auto.getConfigurations() != null && auto.getConfigurations().getTimeAndRepeats() != null && auto.getConfigurations().getTimeAndRepeats().getTimePoint().getTimes().getHour() != null) {
						value = auto.getConfigurations().getTimeAndRepeats().getTimePoint().getTimes().getHour();
						int minute = Integer.parseInt(value);
						if (minute > 12) {
							stats.put(property, PhilipsConstant.EMPTY_STRING);
							advancedControllableProperties.add(controlSwitch(stats, property, String.valueOf(PhilipsConstant.NUMBER_ONE), PhilipsConstant.TIME_AM, PhilipsConstant.TIME_PM));
						} else {
							stats.put(property, PhilipsConstant.EMPTY_STRING);
							advancedControllableProperties.add(controlSwitch(stats, property, String.valueOf(PhilipsConstant.ZERO), PhilipsConstant.TIME_AM, PhilipsConstant.TIME_PM));
						}
						value = convertTimeFormat(minute);
						AdvancedControllableProperty hourControlProperty = controlDropdown(stats, hourDropdown, property, value);
						addOrUpdateAdvanceControlProperties(advancedControllableProperties, hourControlProperty);
					}
					break;
				case TIME_MINUTE:
					String[] timeMinuteDropdown = EnumTypeHandler.getEnumNames(TimeMinuteEnum.class);
					if (auto.getConfigurations() != null && auto.getConfigurations().getTimeAndRepeats() != null
							&& auto.getConfigurations().getTimeAndRepeats().getTimePoint().getTimes().getMinute() != null) {
						value = auto.getConfigurations().getTimeAndRepeats().getTimePoint().getTimes().getMinute();
						int minute = Integer.parseInt(value);
						if (minute < 10) {
							value = PhilipsConstant.ZERO + value;
						}
						AdvancedControllableProperty timeMinuteControlProperty = controlDropdown(stats, timeMinuteDropdown, property, value);
						addOrUpdateAdvanceControlProperties(advancedControllableProperties, timeMinuteControlProperty);
					}
					break;
				case TYPE:
					automationAndTypeMapOfDeviceAndValue = new HashMap<>();
					Location[] locationArray = auto.getConfigurations().getLocation();
					for (Location location1 : locationArray) {
						Map<String, Map<String, String>> typeDevice = new HashMap<>();
						Map<String, String> device = new HashMap<>();
						typeDevice.put(PhilipsConstant.DEVICE, new HashMap<>());
						//Device
						if (location1.getGroup() != null && location1.getItems() != null) {
							int deviceIndex = 0;
							for (Group groupItem : location1.getItems()) {
								Optional<Map<String, String>> data = deviceNameAndMapDeviceIdOfRoomMap.entrySet().stream().map(Entry::getValue).filter(item -> item.values().contains(groupItem.getId()))
										.findFirst();
								if (data.isPresent()) {
									String values = data.toString();
									Optional<Entry<String, String>> nameDevice = deviceNameAndDeviceIdZoneMap.entrySet().stream().filter(item -> values.contains(item.getValue())).findFirst();
									if (nameDevice.isPresent()) {
										device.put(PhilipsConstant.DEVICE + deviceIndex, nameDevice.get().getKey());
										deviceIndex++;
									}
								}
							}
							typeDevice.put(PhilipsConstant.DEVICE, device);
							automationAndTypeMapOfDeviceAndValue.put(autoName, typeDevice);
							List<String> deviceOfMap = allDeviceIdAndNameMap.values().stream().collect(Collectors.toList());
							populateAddRepeatDayForCreateAutomation(property, stats, advancedControllableProperties, deviceOfMap, device, PhilipsConstant.DEVICE);
						}
					}
					break;
				case REPEAT:
					if (auto.getConfigurations() != null && auto.getConfigurations().getTimeAndRepeats() != null && auto.getConfigurations().getTimeAndRepeats().getDays().length > 0) {
						Map<String, String> dayMap = new HashMap<>();
						int i = 0;
						for (String day : auto.getConfigurations().getTimeAndRepeats().getDays()) {
							day = day.substring(0, 1).toUpperCase(Locale.ROOT) + day.substring(1);
							dayMap.put(PhilipsConstant.REPEAT + i, day);
							i++;
						}
						repeatNameOfAutomationMap.put(auto.getMetaData().getName(), dayMap);
						List<String> dayDropdown = Arrays.stream(RepeatDayEnum.values()).map(RepeatDayEnum::getName).collect(Collectors.toList());
						populateAddRepeatDayForCreateAutomation(property, stats, advancedControllableProperties, dayDropdown, dayMap, PhilipsConstant.REPEAT);
						stats.put(property, String.valueOf(PhilipsConstant.NUMBER_ONE));
						advancedControllableProperties.add(controlSwitch(stats, property, String.valueOf(PhilipsConstant.NUMBER_ONE), PhilipsConstant.DISABLE, PhilipsConstant.ENABLE));

						String repeatAdd = groupName + PhilipsConstant.HASH + AutomationEnum.REPEAT_ADD.getName();
						advancedControllableProperties.add(createButton(repeatAdd, PhilipsConstant.ADD, PhilipsConstant.ADDING, 0));

						stats.put(property, PhilipsConstant.EMPTY_STRING);
						advancedControllableProperties.add(controlSwitch(stats, property, String.valueOf(PhilipsConstant.NUMBER_ONE), PhilipsConstant.DISABLE, PhilipsConstant.ENABLE));
					} else {
						repeatNameOfAutomationMap.put(auto.getMetaData().getName(), new HashMap<>());
						stats.put(property, String.valueOf(PhilipsConstant.NUMBER_ONE));
						advancedControllableProperties.add(controlSwitch(stats, property, String.valueOf(PhilipsConstant.NUMBER_ONE), PhilipsConstant.DISABLE, PhilipsConstant.ENABLE));
					}
					break;
				case ACTION:
					stats.put(property, PhilipsConstant.EMPTY_STRING);
					advancedControllableProperties.add(createButton(property, PhilipsConstant.DELETE, PhilipsConstant.DELETING, 0));
					break;
				case CANCEL:
				case CREATE:
				case TIME_CURRENT:
				case STYLE:
				case END_BRIGHTNESS:
				case END_WITH:
				case DEVICE_ADD:
				case ROOM_ADD:
				case ZONE_ADD:
				case REPEAT_ADD:
					break;
				case TYPE_OF_AUTOMATION:
					fadeDuration = auto.getConfigurations().getDuration();
					fadeInDuration = auto.getConfigurations().getFadeInDuration();
					TypeOfAutomation type;
					if (fadeDuration != null) {
						type = TypeOfAutomation.TIMER;
					} else if (fadeInDuration != null) {
						type = TypeOfAutomation.WAKE_UP_WITH_LIGHT;
					} else {
						type = TypeOfAutomation.GO_TO_SLEEP;
					}
					String[] typeOfAutomationDropdown = EnumTypeHandler.getEnumNames(TypeOfAutomation.class);
					AdvancedControllableProperty typeOfAutomationControlProperty = controlDropdown(stats, typeOfAutomationDropdown, property, type.getName());
					addOrUpdateAdvanceControlProperties(advancedControllableProperties, typeOfAutomationControlProperty);
					switch (type) {
						case GO_TO_SLEEP:
							String[] endWithDropdown = EnumTypeHandler.getEnumNames(EndState.class);
							value = EndState.getNameOfEnumByValue(auto.getConfigurations().getEndWith());
							property = groupName + PhilipsConstant.HASH + AutomationEnum.END_WITH.getName();
							AdvancedControllableProperty roomTypeControlProperty = controlDropdown(stats, endWithDropdown, property, value);
							addOrUpdateAdvanceControlProperties(advancedControllableProperties, roomTypeControlProperty);
							break;
						case WAKE_UP_WITH_LIGHT:
							String[] styleDropdown = EnumTypeHandler.getEnumNames(StyleEnum.class);
							value = StyleEnum.getNameOfEnumByValue(auto.getConfigurations().getEndWith());
							property = groupName + PhilipsConstant.HASH + AutomationEnum.STYLE.getName();
							AdvancedControllableProperty styleControlProperty = controlDropdown(stats, styleDropdown, property, value);
							addOrUpdateAdvanceControlProperties(advancedControllableProperties, styleControlProperty);

							property = groupName + PhilipsConstant.HASH + AutomationEnum.END_BRIGHTNESS.getName();
							AdvancedControllableProperty sliderControlProperty = createControlSlider(property, String.valueOf(PhilipsConstant.NUMBER_ONE), stats,
									String.valueOf(PhilipsConstant.MIN_END_BRIGHTNESS), String.valueOf(PhilipsConstant.MAX_END_BRIGHTNESS));
							addOrUpdateAdvanceControlProperties(advancedControllableProperties, sliderControlProperty);
							break;
						case TIMER:
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
		}
	}

	/**
	 * Populate room and zones control details
	 *
	 * @param stats the stats are list of statistics
	 * @param advancedControllableProperties advancedControllableProperties is the list that store all controllable properties
	 * @param roomAndZoneResponse the roomAndZoneResponse is roomAndZoneResponse DTO instance
	 * @param groupName the groupName is String with format Zone{ZoneName} or Room{RoomName}
	 */
	private void populateDetailsRoomAndZone(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties, RoomAndZoneResponse roomAndZoneResponse, String
			groupName,
			boolean isRoom) {
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
					if (isRoom) {
						populateDeviceForRoom(groupName, roomAndZoneResponse, stats, advancedControllableProperties);
					} else {
						populateDeviceForZone(groupName, roomAndZoneResponse, stats, advancedControllableProperties);
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
	 * populate device for rooms and zones
	 *
	 * @param stats the stats are list of statistics
	 * @param advancedControllableProperties advancedControllableProperties is the list that store all controllable properties
	 */
	private void populateDeviceForRoom(String groupName, RoomAndZoneResponse roomAndZoneResponse, Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {
		Map<String, String> mapOfRoomAndDevice = new HashMap<>();
		initialDeviceDropdown(mapOfRoomAndDevice, PhilipsConstant.DEVICE, allDeviceIdAndNameMap.size());
		String[] deviceDropdown = deviceExitsInRoomMap.entrySet().stream().filter(item -> item.getValue().equals(PhilipsConstant.FALSE)).map(Map.Entry::getKey).collect(Collectors.toList())
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
	private void initialDeviceDropdown(Map<String, String> deviceIndexAndValue, String name, int deviceSize) {
		for (int i = 0; i < deviceSize; i++) {
			if (i == 0) {
				deviceIndexAndValue.put(name + i, PhilipsConstant.NONE);
				continue;
			}
			deviceIndexAndValue.put(name + i, null);
		}
	}

	/**
	 * populate device for rooms and zones
	 *
	 * @param stats the stats are list of statistics
	 * @param advancedControllableProperties advancedControllableProperties is the list that store all controllable properties
	 */
	private void populateDeviceForZone(String groupName, RoomAndZoneResponse roomAndZoneResponse, Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {
		Map<String, String> mapOfRoomAndDevice = new HashMap<>();

		//init map device of zone
		initialDeviceDropdown(mapOfRoomAndDevice, PhilipsConstant.DEVICE, allDeviceIdAndNameMap.size());
		String[] deviceDropdown = deviceNameAndDeviceIdZoneMap.keySet().toArray(new String[0]);
		List<Children> children = Arrays.stream(roomAndZoneResponse.getChildren()).collect(Collectors.toList());
		int noOfDevices = children.size();
		String propertyKey = groupName + PhilipsConstant.HASH + PhilipsConstant.DEVICE;
		Arrays.sort(deviceDropdown);
		if (noOfDevices == 0) {
			mapOfRoomAndDevice.put(PhilipsConstant.DEVICE_0, PhilipsConstant.NONE);
			advancedControllableProperties.add(controlDropdown(stats, deviceDropdown, propertyKey + PhilipsConstant.ZONE, PhilipsConstant.NONE));
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
	 * @param deviceProperty the deviceProperty is name of device
	 * @param deviceNameMap the deviceNameMap is map of device
	 */
	private void updateDeviceRoomDropdownList(String property, String value, Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties, String deviceProperty,
			List<String> nameList, Map<String, String> deviceNameMap, String name) {
		if (PhilipsConstant.NONE.equals(value) && !name.equals(deviceProperty)) {
			stats.remove(property);
			deviceNameMap.replace(deviceProperty, null);
		} else {
			String[] deviceDropdownList = nameList.toArray(new String[0]);
			Arrays.sort(deviceDropdownList);
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

		initialDeviceDropdown(deviceRoomMap, PhilipsConstant.DEVICE, allDeviceIdAndNameMap.size());
		String[] deviceDropdown = deviceExitsInRoomMap.entrySet().stream().filter(item -> item.getValue().equals(PhilipsConstant.FALSE)).map(Map.Entry::getKey).collect(Collectors.toList())
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

		initialDeviceDropdown(deviceZoneMap, PhilipsConstant.DEVICE, deviceNameAndDeviceIdZoneMap.size());
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
	 * Populate create room
	 *
	 * @param property the property is property name with format GroupName#KeyName
	 * @param value the value is value of property
	 * @param stats the stats are list of statistics
	 * @param advancedControllableProperties advancedControllableProperties is the list that store all controllable properties
	 */
	private void populateCreateRoom(String property, String value, Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {
		String[] propertyList = property.split(PhilipsConstant.HASH);
		String propertyGroup = propertyList[0];
		String key = propertyList[1];
		isCreateRoom = true;
		List<String> deviceDropdown = deviceExitsInRoomMap.entrySet().stream().filter(item -> item.getValue().equals(PhilipsConstant.FALSE)).map(Map.Entry::getKey).collect(Collectors.toList());
		if (key.contains(PhilipsConstant.DEVICE) && !CreateRoomEnum.DEVICE_ADD.getRoomName().equals(key)) {
			updateDeviceRoomDropdownList(property, value, stats, advancedControllableProperties, key, deviceDropdown, deviceRoomMap, PhilipsConstant.DEVICE_0);
		} else {
			RoomsAndZonesControlEnum room = EnumTypeHandler.getMetricOfEnumByName(RoomsAndZonesControlEnum.class, key);
			switch (room) {
				case DEVICE_STATUS:
				case NAME:
				case TYPE:
					updateValueForTheControllableProperty(property, value, stats, advancedControllableProperties);
					break;
				case CREATE:
					RoomAndZoneResponse roomValue = convertRoomByValue(stats, PhilipsConstant.CREATE_ROOM + PhilipsConstant.HASH, deviceRoomMap, deviceNameAndMapDeviceIdOfRoomMap, false);
					sendRequestToCreateOrEditRoomAndZone(roomValue, true, false);
					isCreateRoom = false;
					deviceRoomMap.clear();
					break;
				case DEVICE_ADD:
					addDeviceForRoomAndZone(propertyGroup, stats, advancedControllableProperties, deviceDropdown, deviceRoomMap);
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
	private void populateCreateAutomation(String property, String value, Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {
		String[] propertyList = property.split(PhilipsConstant.HASH);
		String propertyGroup = propertyList[0];
		String key = propertyList[1];
		isCreateAutomation = true;
		if (!PhilipsConstant.REPEAT.equalsIgnoreCase(key) && key.contains(PhilipsConstant.REPEAT) && !AutomationEnum.REPEAT_ADD.getName().equals(key)) {
			List<String> repeatDayDropdown = Arrays.stream(RepeatDayEnum.values()).map(RepeatDayEnum::getName).collect(Collectors.toList());
			updateDeviceRoomDropdownList(property, value, stats, advancedControllableProperties, key, repeatDayDropdown, repeatNameOfCreateAutomationMap, PhilipsConstant.REPEAT_0);
		} else if (!PhilipsConstant.DEVICE.equalsIgnoreCase(key) && key.contains(PhilipsConstant.DEVICE) && !AutomationEnum.DEVICE_ADD.getName().equals(key)) {
			List<String> deviceDropdown = deviceNameAndDeviceIdZoneMap.keySet().stream().collect(Collectors.toList());
			Map<String, String> mapOfDevice = typeAndMapOfDeviceAndValue.get(PhilipsConstant.DEVICE);
			updateDeviceRoomDropdownList(property, value, stats, advancedControllableProperties, key, deviceDropdown, mapOfDevice, PhilipsConstant.DEVICE_0);
		} else if (!PhilipsConstant.ROOM.equalsIgnoreCase(key) && key.contains(PhilipsConstant.ROOM) && !AutomationEnum.ROOM_ADD.getName().equals(key)) {
			List<String> deviceDropdown = roomNameAndIdMap.keySet().stream().collect(Collectors.toList());
			Map<String, String> mapOfDevice = typeAndMapOfDeviceAndValue.get(PhilipsConstant.ROOM);
			updateDeviceRoomDropdownList(property, value, stats, advancedControllableProperties, key, deviceDropdown, mapOfDevice, PhilipsConstant.ROOM_0);
		} else if (!PhilipsConstant.ZONE.equalsIgnoreCase(key) && key.contains(PhilipsConstant.ZONE) && !AutomationEnum.ZONE_ADD.getName().equals(key)) {
			List<String> deviceDropdown = zoneNameAndIdMap.keySet().stream().collect(Collectors.toList());
			Map<String, String> mapOfDevice = typeAndMapOfDeviceAndValue.get(PhilipsConstant.ZONE);
			updateDeviceRoomDropdownList(property, value, stats, advancedControllableProperties, key, deviceDropdown, mapOfDevice, PhilipsConstant.ZONE_0);
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
				case FADE_DURATION:
					updateValueForTheControllableProperty(property, value, stats, advancedControllableProperties);
					break;
				case REPEAT:
					updateValueForTheControllableProperty(property, value, stats, advancedControllableProperties);
					if (String.valueOf(PhilipsConstant.NUMBER_ONE).equals(value)) {
						stats.put(propertyGroup + PhilipsConstant.HASH + AutomationEnum.REPEAT_ADD.getName(), PhilipsConstant.EMPTY_STRING);
						advancedControllableProperties.add(createButton(propertyGroup + PhilipsConstant.HASH + AutomationEnum.REPEAT_ADD.getName(), PhilipsConstant.ADD, PhilipsConstant.ADDING, 0));
						List<String> dayDropdown = Arrays.stream(RepeatDayEnum.values()).map(RepeatDayEnum::getName).collect(Collectors.toList());
						populateAddRepeatDayForCreateAutomation(property, stats, advancedControllableProperties, dayDropdown, repeatNameOfCreateAutomationMap, PhilipsConstant.REPEAT);
					} else {
						String repeatAdd = propertyGroup + PhilipsConstant.HASH + AutomationEnum.REPEAT_ADD.getName();

						stats.remove(repeatAdd);
						advancedControllableProperties.removeIf(item -> item.getName().equals(repeatAdd));
						populateRemoveRepeatDayForCreateAutomation(property, stats, advancedControllableProperties, repeatNameOfCreateAutomationMap);
					}
					break;
				case REPEAT_ADD:
					addRepeatDayForAutomation(propertyGroup, stats, advancedControllableProperties);
					break;
				case DEVICE_ADD:
					List<String> deviceDropdown = deviceNameAndDeviceIdZoneMap.keySet().stream().collect(Collectors.toList());
					Map<String, String> mapOfDevice = typeAndMapOfDeviceAndValue.get(PhilipsConstant.DEVICE);
					if (mapOfDevice == null) {
						mapOfDevice = new HashMap<>();
						initialDeviceDropdown(mapOfDevice, PhilipsConstant.DEVICE, deviceNameAndDeviceIdZoneMap.size());
						typeAndMapOfDeviceAndValue.put(PhilipsConstant.DEVICE, mapOfDevice);
					}
					addDeviceForRoomAndZone(propertyGroup, stats, advancedControllableProperties, deviceDropdown, mapOfDevice);
					break;
				case ROOM_ADD:
					Map<String, String> mapOfRoom = typeAndMapOfDeviceAndValue.get(PhilipsConstant.ROOM);
					if (mapOfRoom == null) {
						mapOfRoom = new HashMap<>();
						initialDeviceDropdown(mapOfRoom, PhilipsConstant.ROOM, roomNameAndIdMap.size());
						typeAndMapOfDeviceAndValue.put(PhilipsConstant.ROOM, mapOfRoom);
					}
					addRoomAndZoneForAutomation(propertyGroup, stats, advancedControllableProperties, roomNameAndIdMap, mapOfRoom);
					break;
				case ZONE_ADD:
					Map<String, String> mapOfZone = typeAndMapOfDeviceAndValue.get(PhilipsConstant.ZONE);
					if (mapOfZone == null) {
						mapOfZone = new HashMap<>();
						initialDeviceDropdown(mapOfZone, PhilipsConstant.ZONE, zoneNameAndIdMap.size());
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
							populateUpdateDeviceTypeForCreateAutomation(property, stats, advancedControllableProperties, type);
							break;
						default:
							if (logger.isDebugEnabled()) {
								logger.debug(String.format("Creating automation with  device type %s is not supported.", automationEnum.getName()));
							}
					}
					break;
				case CANCEL:
					isCreateAutomation = false;
					break;
				case TYPE_OF_AUTOMATION:
					updateValueForTheControllableProperty(property, value, stats, advancedControllableProperties);
					TypeOfAutomation typeAutomation = EnumTypeHandler.getMetricOfEnumByName(TypeOfAutomation.class, value);
					String endWith = propertyGroup + PhilipsConstant.HASH + AutomationEnum.END_WITH.getName();
					String style = propertyGroup + PhilipsConstant.HASH + AutomationEnum.STYLE.getName();
					String endBrightness = propertyGroup + PhilipsConstant.HASH + AutomationEnum.END_BRIGHTNESS.getName();
					switch (typeAutomation) {
						case GO_TO_SLEEP:
							String[] endWithDropdown = EnumTypeHandler.getEnumNames(EndState.class);
							AdvancedControllableProperty roomTypeControlProperty = controlDropdown(stats, endWithDropdown, endWith, EndState.LIGHT_OFF.getName());
							addOrUpdateAdvanceControlProperties(advancedControllableProperties, roomTypeControlProperty);
							stats.remove(style);
							advancedControllableProperties.removeIf(item -> item.getName().equals(style));
							stats.remove(endBrightness);
							advancedControllableProperties.removeIf(item -> item.getName().equals(endBrightness));
							break;
						case WAKE_UP_WITH_LIGHT:
							stats.remove(endWith);
							advancedControllableProperties.removeIf(item -> item.getName().equals(endWith));

							String[] styleDropdown = EnumTypeHandler.getEnumNames(StyleEnum.class);
							AdvancedControllableProperty styleControlProperty = controlDropdown(stats, styleDropdown, style, StyleEnum.SUNRISE.getName());
							addOrUpdateAdvanceControlProperties(advancedControllableProperties, styleControlProperty);

							AdvancedControllableProperty sliderControlProperty = createControlSlider(endBrightness, String.valueOf(PhilipsConstant.NUMBER_ONE), stats,
									String.valueOf(PhilipsConstant.MIN_END_BRIGHTNESS), String.valueOf(PhilipsConstant.MAX_END_BRIGHTNESS));
							addOrUpdateAdvanceControlProperties(advancedControllableProperties, sliderControlProperty);
							break;
						case TIMER:
							stats.remove(endWith);
							advancedControllableProperties.removeIf(item -> item.getName().equals(endWith));
							stats.remove(style);
							advancedControllableProperties.removeIf(item -> item.getName().equals(style));
							stats.remove(endBrightness);
							advancedControllableProperties.removeIf(item -> item.getName().equals(endBrightness));
							break;

						default:
							if (logger.isDebugEnabled()) {
								logger.debug(String.format("Controlling create automation automation type %s is not supported.", automationEnum.getName()));
							}
							break;
					}
					break;
				case ACTION:
					convertAutomationByValue(propertyGroup, stats);
					isCreateAutomation = false;
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
	 * Update list device for automation
	 *
	 * @param property the property is property name with format {CreateAutomationBehaviorInstance#{key}}
	 * @param stats the stats are list of statistics
	 * @param advancedControllableProperties advancedControllableProperties is the list that store all controllable properties
	 * @param type the type is type of TypeOfDevice enum instance
	 */
	private void updateListDeviceForCreateAuto(String property, Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties, TypeOfDeviceEnum type) {
		String[] propertyList = property.split(PhilipsConstant.HASH);
		String propertyGroup = propertyList[0];
		Map<String, String> deviceOfRoom = typeAndMapOfDeviceAndValue.get(PhilipsConstant.ROOM);
		Map<String, String> deviceOfZone = typeAndMapOfDeviceAndValue.get(PhilipsConstant.ZONE);
		Map<String, String> deviceMap = typeAndMapOfDeviceAndValue.get(PhilipsConstant.DEVICE);
		String roomAdd = propertyGroup + PhilipsConstant.HASH + AutomationEnum.ROOM_ADD.getName();
		String zoneAdd = propertyGroup + PhilipsConstant.HASH + AutomationEnum.ZONE_ADD.getName();
		String deviceAdd = propertyGroup + PhilipsConstant.HASH + AutomationEnum.DEVICE_ADD.getName();
		switch (type) {
			case DEVICE:
				stats.put(deviceAdd, PhilipsConstant.EMPTY_STRING);
				advancedControllableProperties.add(createButton(deviceAdd, PhilipsConstant.ADD, PhilipsConstant.ADDING, 0));
				if (deviceOfRoom.size() > 0) {
					populateRemoveRepeatDayForCreateAutomation(property, stats, advancedControllableProperties, deviceOfRoom);
				}
				if (deviceOfZone.size() > 0) {
					populateRemoveRepeatDayForCreateAutomation(property, stats, advancedControllableProperties, deviceOfZone);
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
					populateRemoveRepeatDayForCreateAutomation(property, stats, advancedControllableProperties, deviceMap);
				}
				if (deviceOfZone.size() > 0) {
					populateRemoveRepeatDayForCreateAutomation(property, stats, advancedControllableProperties, deviceOfZone);
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
					populateRemoveRepeatDayForCreateAutomation(property, stats, advancedControllableProperties, deviceMap);
				}
				if (deviceOfRoom.size() > 0) {
					populateRemoveRepeatDayForCreateAutomation(property, stats, advancedControllableProperties, deviceOfRoom);
				}
				stats.remove(deviceAdd);
				advancedControllableProperties.removeIf(item -> item.getName().equals(deviceAdd));
				stats.remove(roomAdd);
				advancedControllableProperties.removeIf(item -> item.getName().equals(roomAdd));
				break;
			case NONE:
				updateListDeviceForCreateAuto(property, stats, advancedControllableProperties, TypeOfDeviceEnum.DEVICE);
				updateListDeviceForCreateAuto(property, stats, advancedControllableProperties, TypeOfDeviceEnum.ROOM);
				updateListDeviceForCreateAuto(property, stats, advancedControllableProperties, TypeOfDeviceEnum.ZONE);
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
	private void populateUpdateDeviceTypeForCreateAutomation(String property, Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties, TypeOfDeviceEnum type) {
		String[] propertyList = property.split(PhilipsConstant.HASH);
		String propertyGroup = propertyList[0];
		Map<String, String> deviceIndexAndValue = typeAndMapOfDeviceAndValue.get(type.getName());
		switch (type) {
			case DEVICE:
				if (deviceIndexAndValue.size() == 0) {
					initialDeviceDropdown(deviceIndexAndValue, PhilipsConstant.DEVICE, allDeviceIdAndNameMap.size());
					String[] deviceDropdown = deviceNameAndDeviceIdZoneMap.keySet().toArray(new String[0]);
					AdvancedControllableProperty initDeviceControlProperty = controlDropdown(stats, deviceDropdown, propertyGroup + PhilipsConstant.HASH + PhilipsConstant.DEVICE_0, PhilipsConstant.NONE);
					addOrUpdateAdvanceControlProperties(advancedControllableProperties, initDeviceControlProperty);
					deviceIndexAndValue.put(PhilipsConstant.DEVICE_0, PhilipsConstant.NONE);
				} else {
					List<String> deviceDropdown = deviceNameAndDeviceIdZoneMap.keySet().stream().collect(Collectors.toList());
					populateAddRepeatDayForCreateAutomation(property, stats, advancedControllableProperties, deviceDropdown, deviceIndexAndValue, PhilipsConstant.DEVICE);
				}
				updateListDeviceForCreateAuto(property, stats, advancedControllableProperties, type);
				break;
			case ROOM:
				if (deviceIndexAndValue.size() == 0) {
					initialDeviceDropdown(deviceIndexAndValue, PhilipsConstant.ROOM, roomNameAndIdMap.size());
					String[] deviceDropdown = roomNameAndIdMap.keySet().toArray(new String[0]);
					AdvancedControllableProperty initDeviceControlProperty = controlDropdown(stats, deviceDropdown, propertyGroup + PhilipsConstant.HASH + PhilipsConstant.ROOM_0, PhilipsConstant.NONE);
					addOrUpdateAdvanceControlProperties(advancedControllableProperties, initDeviceControlProperty);
					deviceIndexAndValue.put(PhilipsConstant.ROOM_0, PhilipsConstant.NONE);
				} else {
					List<String> deviceDropdown = roomNameAndIdMap.keySet().stream().collect(Collectors.toList());
					populateAddRepeatDayForCreateAutomation(property, stats, advancedControllableProperties, deviceDropdown, deviceIndexAndValue, PhilipsConstant.ROOM);
				}
				updateListDeviceForCreateAuto(property, stats, advancedControllableProperties, type);
				break;
			case ZONE:
				if (deviceIndexAndValue.size() == 0) {
					initialDeviceDropdown(deviceIndexAndValue, PhilipsConstant.ZONE, zoneNameAndIdMap.size());
					String[] deviceDropdown = zoneNameAndIdMap.keySet().toArray(new String[0]);
					AdvancedControllableProperty initDeviceControlProperty = controlDropdown(stats, deviceDropdown, propertyGroup + PhilipsConstant.HASH + PhilipsConstant.ZONE_0, PhilipsConstant.NONE);
					addOrUpdateAdvanceControlProperties(advancedControllableProperties, initDeviceControlProperty);
					deviceIndexAndValue.put(PhilipsConstant.ZONE_0, PhilipsConstant.NONE);
				} else {
					List<String> deviceDropdown = zoneNameAndIdMap.keySet().stream().collect(Collectors.toList());
					populateAddRepeatDayForCreateAutomation(property, stats, advancedControllableProperties, deviceDropdown, deviceIndexAndValue, PhilipsConstant.ZONE);
				}
				updateListDeviceForCreateAuto(property, stats, advancedControllableProperties, type);
				break;
			case NONE:
				updateListDeviceForCreateAuto(property, stats, advancedControllableProperties, type);
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
	private void populateRemoveRepeatDayForCreateAutomation(String property, Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties,
			Map<String, String> deviceMap) {
		String propertyGroup = property.split(PhilipsConstant.HASH)[0];
		for (Entry<String, String> repeatEntry : deviceMap.entrySet()) {
			String value = repeatEntry.getValue();
			String repeatName = propertyGroup + PhilipsConstant.HASH + repeatEntry.getKey();
			if (!StringUtils.isNullOrEmpty(value)) {
				stats.remove(repeatName);
				advancedControllableProperties.removeIf(item -> item.getName().equals(repeatName));
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
	private void populateAddRepeatDayForCreateAutomation(String
			property, Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties, List<String> dropdownList,
			Map<String, String> deviceMap,
			String name) {
		String[] propertyList = property.split(PhilipsConstant.HASH);
		String propertyGroup = propertyList[0];
		String[] repeatDaysDropdown = dropdownList.stream().collect(Collectors.toList()).toArray(new String[0]);
		if (deviceMap.size() < 1) {
			initialDeviceDropdown(deviceMap, name, RepeatDayEnum.values().length - 1);

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
	 * Add new repeat for automation
	 *
	 * @param propertyGroup the propertyGroup is group name of property
	 * @param stats the stats are list of statistics
	 * @param advancedControllableProperties advancedControllableProperties is the list that store all controllable properties
	 */
	private void addRoomAndZoneForAutomation(String propertyGroup, Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties, Map<String, String> dropdown,
			Map<String, String> deviceMap) {

		String[] roomAndZoneDropdown = dropdown.keySet().toArray(new String[0]);
		String prefixName = propertyGroup + PhilipsConstant.HASH;
		//Check list repeat day added
		int countDevice = 0;
		for (Map.Entry<String, String> entry : deviceMap.entrySet()) {
			if (entry.getValue() != null) {
				//count the number of day
				countDevice++;
			}
		}
		if (countDevice >= dropdown.size() - 1) {
			throw new ResourceNotReachableException(
					String.format("Total day is %s, you have added enough room/zone and cannot add new room/zone. Please remove the room/zone and try again", dropdown.size() - 1));
		}
		for (Map.Entry<String, String> roomAndZoneEntry : deviceMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors.toList())) {
			String defaultName = PhilipsConstant.NONE;
			for (String roomAndZoneValue : dropdown.keySet()) {
				if (!deviceMap.values().contains(roomAndZoneValue)) {
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
	 */
	private void addRepeatDayForAutomation(String propertyGroup, Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {

		String[] repeatDayDropdown = EnumTypeHandler.getEnumNames(RepeatDayEnum.class);
		String prefixName = propertyGroup + PhilipsConstant.HASH;
		//Check list repeat day added
		int countDevice = 0;
		for (Map.Entry<String, String> entry : repeatNameOfCreateAutomationMap.entrySet()) {
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
		for (Map.Entry<String, String> repeatDayEntry : repeatNameOfCreateAutomationMap.entrySet()) {
			String defaultName = PhilipsConstant.NONE;
			for (RepeatDayEnum repeatDayEnum : RepeatDayEnum.values()) {
				if (!repeatNameOfCreateAutomationMap.values().contains(repeatDayEnum.getName())) {
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
	}

	/**
	 * Send request to change status for room/zone
	 *
	 * @param id the id is id of room/zone
	 * @param status the status is status of room/zone
	 * @param isRoomType the isRoomType is boolean value
	 */
	private void sendRequestToChangeStatus(String id, String status, boolean isRoomType) {
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
	 * Populate Control room
	 *
	 * @param property the property is property name with format GroupName#KeyName
	 * @param value the value is value of property
	 * @param stats the stats are list of statistics
	 * @param advancedControllableProperties advancedControllableProperties is the list that store all controllable properties
	 * @param isEditRoom is a boolean if true is edit room otherwise edit zone
	 */
	private void populateControlRoomAndZone(String property, String value, Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties, boolean isEditRoom) {
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
			deviceList = deviceExitsInRoomMap.entrySet().stream().filter(item -> item.getValue().equals(PhilipsConstant.FALSE)).map(Map.Entry::getKey).collect(Collectors.toList());
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
			updateDeviceRoomDropdownList(property, value, stats, advancedControllableProperties, key, deviceList, mapOfDeviceDropdown, PhilipsConstant.DEVICE_0);
		} else {
			RoomsAndZonesControlEnum room = EnumTypeHandler.getMetricOfEnumByName(RoomsAndZonesControlEnum.class, key);
			switch (room) {
				case DEVICE_STATUS:
					String status = PhilipsConstant.TRUE;
					if (String.valueOf(PhilipsConstant.ZERO).equals(value)) {
						status = PhilipsConstant.FALSE;
					}
					if (roomAndZoneResponse.isPresent()) {
						sendRequestToChangeStatus(roomAndZoneResponse.get().getServices()[0].getId(), String.format(PhilipsConstant.PARAM_CHANGE_STATUS, status.toLowerCase(Locale.ROOT)), true);
					}
					updateValueForTheControllableProperty(property, value, stats, advancedControllableProperties);
					isEmergencyDelivery = isCurrentEmergencyDelivery;
					break;
				case NAME:
				case TYPE:
					updateValueForTheControllableProperty(property, value, stats, advancedControllableProperties);
					break;
				case ACTION:
					if (roomAndZoneResponse.isPresent()) {
						sendRequestToDeleteRoomAndZone(roomAndZoneResponse.get().getId(), isEditRoom);
					}
					isEmergencyDelivery = false;
					break;
				case DEVICE_ADD:
					addDeviceForRoomAndZone(propertyGroup, stats, advancedControllableProperties, deviceDropdown, mapOfDeviceDropdown);
					break;
				case CANCEL:
					isEmergencyDelivery = false;
					break;
				case APPLY_CHANGE:
					RoomAndZoneResponse roomValue;
					if (roomAndZoneResponse.isPresent()) {
						if (isEditRoom) {
							roomValue = convertRoomByValue(stats, propertyGroup + PhilipsConstant.HASH, mapOfDeviceDropdown, deviceNameAndMapDeviceIdOfRoomMap, true);
							roomValue.setId(roomAndZoneResponse.get().getId());
							sendRequestToCreateOrEditRoomAndZone(roomValue, true, true);
						} else {
							roomValue = convertZoneByValue(stats, propertyGroup + PhilipsConstant.HASH, mapOfDeviceDropdown, deviceNameAndDeviceIdZoneMap, true);
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
	 * @param mapOfNameDevice the map name and  id of device
	 */
	private void addDeviceForRoomAndZone(String groupName, Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties, List<String> devicesName,
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
				if (!mapOfNameDevice.values().contains(nameValue)) {
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
	 * Populate create room
	 *
	 * @param property the property is property name with format GroupName#KeyName
	 * @param value the value is value of property
	 * @param stats the stats are list of statistics
	 * @param advancedControllableProperties advancedControllableProperties is the list that store all controllable properties
	 */
	private void populateCreateZone(String property, String value, Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {
		String[] propertyList = property.split(PhilipsConstant.HASH);
		String propertyGroup = propertyList[0];
		String key = propertyList[1];
		isCreateZone = true;
		if (key.contains(PhilipsConstant.DEVICE) && !CreateRoomEnum.DEVICE_ADD.getRoomName().equals(key)) {
			List<String> deviceDropdown = deviceNameAndDeviceIdZoneMap.keySet().stream().collect(Collectors.toList());
			updateDeviceRoomDropdownList(property, value, stats, advancedControllableProperties, key, deviceDropdown, deviceZoneMap, PhilipsConstant.DEVICE_0);
		} else {
			RoomsAndZonesControlEnum zone = EnumTypeHandler.getMetricOfEnumByName(RoomsAndZonesControlEnum.class, key);
			switch (zone) {
				case DEVICE_STATUS:
				case NAME:
				case TYPE:
					updateValueForTheControllableProperty(property, value, stats, advancedControllableProperties);
					break;
				case CREATE:
					RoomAndZoneResponse roomConvertData = convertZoneByValue(stats, PhilipsConstant.CREATE_ZONE + PhilipsConstant.HASH, deviceZoneMap, deviceNameAndDeviceIdZoneMap, false);
					sendRequestToCreateOrEditRoomAndZone(roomConvertData, false, false);
					isCreateZone = false;
					break;
				case DEVICE_ADD:
					List<String> deviceDropdown = deviceNameAndDeviceIdZoneMap.keySet().stream().collect(Collectors.toList());
					addDeviceForRoomAndZone(propertyGroup, stats, advancedControllableProperties, deviceDropdown, deviceZoneMap);
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
			throw new ResourceNotReachableException("Can't delete room/zone: " + e.getMessage(), e);
		}
	}

	private AutomationResponse convertAutomationByValue(String property, Map<String, String> stats) {
		AutomationResponse auto = new AutomationResponse();
		String name = stats.get(property + PhilipsConstant.HASH + AutomationEnum.NAME.getName());
		String fadeDuration = stats.get(property + PhilipsConstant.HASH + AutomationEnum.FADE_DURATION.getName());
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

		if (StringUtils.isNullOrEmpty(name)) {
			throw new ResourceNotReachableException("Error while creating automation, Automation name can't empty");
		}
		MetaData metaData = new MetaData();
		metaData.setName(name);
		auto.setMetaData(metaData);

		AutoConfiguration config = new AutoConfiguration();
		FadeDuration fadeDuration1 = new FadeDuration();
		fadeDuration1.setSeconds(fadeDuration);
		config.setDuration(fadeDuration1);
		config.setEndBrightness(endBrightness);
		config.setStyle(style);
		config.setEndWith(endWith);
		TimeAndRepeat timeAndRepeat = new TimeAndRepeat();
		List<String> days = new LinkedList<>();
		if (String.valueOf(PhilipsConstant.NUMBER_ONE).equals(repeat)) {
			for (Entry<String, String> day : repeatNameOfCreateAutomationMap.entrySet()) {
				if (!StringUtils.isNullOrEmpty(day.getValue()) && !PhilipsConstant.NONE.equals(day.getValue())) {
					days.add(day.getValue().toLowerCase(Locale.ROOT));
				}
			}
		}
		auto.setEnabled(PhilipsConstant.FALSE);
		if (String.valueOf(PhilipsConstant.NUMBER_ONE).equals(status)) {
			auto.setEnabled(PhilipsConstant.TRUE);
		}
		String[] dayArrays = days.toArray(new String[0]);
		timeAndRepeat.setDays(dayArrays);

		TimePoint timePoint = new TimePoint();
		CurrentTime currentTime = new CurrentTime();
		if (String.valueOf(PhilipsConstant.NUMBER_ONE).equals(timeCurrent)) {
			int time = Integer.parseInt(timeHour) + 12;
			timeHour = String.valueOf(time);
		}
		currentTime.setHour(timeHour);
		currentTime.setMinute(timeMinute);
		timePoint.setTimes(currentTime);
		timeAndRepeat.setTimePoint(timePoint);

		config.setTimeAndRepeats(timeAndRepeat);

		Location location = new Location();
		List<Location> locationList = new LinkedList<>();
		if (TypeOfDeviceEnum.DEVICE.getName().equals(type)) {
			Map<String, String> mapOfDevice = typeAndMapOfDeviceAndValue.get(PhilipsConstant.DEVICE);
			if (mapOfDevice != null) {
				Map<String, Map<Group, List<Group>>> nameOfGroupMap = new HashMap<>();

				for (Entry<String, String> deviceEntry : mapOfDevice.entrySet()) {
					Group group = new Group();
					List<Group> items = new LinkedList<>();
					Map<Group, List<Group>> roomAndListChirldren = new HashMap<>();
					if (!StringUtils.isNullOrEmpty(deviceEntry.getValue()) && !PhilipsConstant.NONE.equals(deviceEntry.getValue())) {
						String idLightDevice = deviceNameAndDeviceIdZoneMap.get(deviceEntry.getValue());
						Optional<Entry<String, Map<String, String>>> idDeviceInRoom = deviceNameAndMapDeviceIdOfRoomMap.entrySet().stream().filter(item -> item.getValue().values().contains(idLightDevice))
								.findFirst();
						String key = PhilipsConstant.EMPTY_STRING;
						if (idDeviceInRoom.isPresent()) {
							key = String.valueOf(idDeviceInRoom.get().getValue().keySet().stream().toArray()[0]);
						}
						String finalKey = key;
						Optional<RoomAndZoneResponse> room = roomList.stream().filter(item -> Arrays.stream(item.getChildren()).map(Children::getRid).collect(Collectors.toList()).contains(finalKey)).findFirst();
						if (room.isPresent()) {
							if (nameOfGroupMap.size() == 0) {
								group.setId(room.get().getId());
								group.setType(PhilipsConstant.ROOM);
								Group item = new Group();
								item.setId(idLightDevice);
								item.setType(PhilipsConstant.LIGHT);
								items.add(item);
								roomAndListChirldren.put(group, items);
								nameOfGroupMap.put(room.get().getMetaData().getName(), roomAndListChirldren);
							} else {
								Map<Group, List<Group>> groupAndDevice = nameOfGroupMap.get(room.get().getMetaData().getName());
								if (groupAndDevice == null) {
									group.setId(room.get().getId());
									group.setType(PhilipsConstant.ROOM);
									Group item = new Group();
									item.setId(idLightDevice);
									item.setType(PhilipsConstant.LIGHT);
									items.add(item);
									roomAndListChirldren.put(group, items);
									nameOfGroupMap.put(room.get().getMetaData().getName(), roomAndListChirldren);
								} else {
									group.setId(room.get().getId());
									group.setType(PhilipsConstant.ROOM);
									Group item = new Group();
									item.setId(idLightDevice);
									item.setType(PhilipsConstant.LIGHT);
									items.add(item);
									roomAndListChirldren.put(group, items);
									groupAndDevice.put(group, items);
								}
							}
						}
					}
				}
				for (Entry<String, Map<Group, List<Group>>> deviceList : nameOfGroupMap.entrySet()) {
					for (Entry<Group, List<Group>> deviceDetail : deviceList.getValue().entrySet()) {
						location.setGroup(deviceDetail.getKey());
						location.setItems(deviceDetail.getValue().toArray(new Group[0]));
						locationList.add(location);
					}
				}
			}
			config.setLocation(locationList.toArray(new Location[0]));
		}
		if (TypeOfDeviceEnum.ROOM.getName().equals(type)) {
			Map<String, String> mapOfDevice = typeAndMapOfDeviceAndValue.get(PhilipsConstant.ROOM);
			if (mapOfDevice != null) {
				Map<String, Group> nameOfGroupMap = new HashMap<>();

				for (Entry<String, String> deviceEntry : mapOfDevice.entrySet()) {
					Group group = new Group();
					if (!StringUtils.isNullOrEmpty(deviceEntry.getValue()) && !PhilipsConstant.NONE.equals(deviceEntry.getValue())) {
						String idDevice = deviceNameAndDeviceIdZoneMap.get(deviceEntry.getKey());
						Optional<RoomAndZoneResponse> room = roomList.stream().filter(item -> Arrays.toString(item.getChildren()).contains(idDevice)).findFirst();
						if (room.isPresent()) {
							if (nameOfGroupMap.size() == 0) {
								group.setId(room.get().getId());
								group.setType(PhilipsConstant.ROOM);
								nameOfGroupMap.put(room.get().getMetaData().getName(), group);
							} else {
								Group groupAndDevice = nameOfGroupMap.get(room.get().getMetaData().getName());
								if (groupAndDevice == null) {
									group.setId(room.get().getId());
									group.setType(PhilipsConstant.ROOM);
									nameOfGroupMap.put(room.get().getMetaData().getName(), group);
								}
							}
						}
					}
				}
				for (Entry<String, Group> deviceList : nameOfGroupMap.entrySet()) {
					location.setGroup(deviceList.getValue());
					location.setItems(new Group[0]);
					locationList.add(location);
				}
				config.setLocation(locationList.toArray(new Location[0]));
			}
		}
		if (TypeOfDeviceEnum.ZONE.getName().equals(type)) {
			Map<String, String> mapOfDevice = typeAndMapOfDeviceAndValue.get(PhilipsConstant.ZONE);
			if (mapOfDevice != null) {
				Map<String, Group> nameOfGroupMap = new HashMap<>();

				for (Entry<String, String> deviceEntry : mapOfDevice.entrySet()) {
					Group group = new Group();
					if (!StringUtils.isNullOrEmpty(deviceEntry.getValue()) && !PhilipsConstant.NONE.equals(deviceEntry.getValue())) {
						String idDevice = deviceNameAndDeviceIdZoneMap.get(deviceEntry.getKey());
						Optional<RoomAndZoneResponse> room = zoneList.stream().filter(item -> Arrays.toString(item.getChildren()).contains(idDevice)).findFirst();
						if (room.isPresent()) {
							if (nameOfGroupMap.size() == 0) {
								group.setId(room.get().getId());
								group.setType(PhilipsConstant.ZONE);
								nameOfGroupMap.put(room.get().getMetaData().getName(), group);
							} else {
								Group groupAndDevice = nameOfGroupMap.get(room.get().getMetaData().getName());
								if (groupAndDevice == null) {
									group.setId(room.get().getId());
									group.setType(PhilipsConstant.ZONE);
									nameOfGroupMap.put(room.get().getMetaData().getName(), group);
								}
							}
						}
					}
				}
				for (Entry<String, Group> deviceList : nameOfGroupMap.entrySet()) {
					location.setGroup(deviceList.getValue());
					location.setItems(new Group[0]);
					locationList.add(location);
				}
				config.setLocation(locationList.toArray(new Location[0]));
			}
		}
		auto.setConfigurations(config);

		String script = PhilipsConstant.EMPTY_STRING;
		if (TypeOfAutomation.TIMER.getName().equals(typeOfAutomation)) {
			Optional<ScriptAutomationResponse> timer = scriptAutomationList.stream().filter(item -> item.getMetadata().getName().equalsIgnoreCase(PhilipsConstant.TIMERS)).findFirst();
			if (timer.isPresent()) {
				script = timer.get().getId();
			}
		}
		if (TypeOfAutomation.GO_TO_SLEEP.getName().equals(typeOfAutomation)) {
			Optional<ScriptAutomationResponse> timer = scriptAutomationList.stream().filter(item -> item.getMetadata().getName().equalsIgnoreCase(PhilipsConstant.GO_TO_SLEEPS)).findFirst();
			if (timer.isPresent()) {
				script = timer.get().getId();
			}
		}
		if (TypeOfAutomation.WAKE_UP_WITH_LIGHT.getName().equals(typeOfAutomation)) {
			Optional<ScriptAutomationResponse> timer = scriptAutomationList.stream().filter(item -> item.getMetadata().getName().equalsIgnoreCase(PhilipsConstant.WAKE_UP_WITH_LIGHT)).findFirst();
			if (timer.isPresent()) {
				script = timer.get().getId();
			}
		}
		auto.setScriptId(script);
		return auto;
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
			hour = hour - 12;
			return String.valueOf(PhilipsConstant.ZERO) + hour;
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
	private RoomAndZoneResponse convertRoomByValue(Map<String, String> stats, String property, Map<String, String> deviceDropdown, Map<String, Map<String, String>> mapOfNameAndId,
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
		if (StringUtils.isNullOrEmpty(name)) {
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
	 * Convert Zone by value
	 *
	 * @param stats the stats are list of statistics
	 * @param property the property is property name
	 * @param mapOfDevice is map of device name
	 * @param mapOfNameAndId is map of name and id device
	 * @param isEditZone is boolean value if isEditZone =true then edit the zone, isEditZone = false then create the zone
	 * @return RoomAndZoneResponse is instance in RoomAndZoneResponseDTO
	 */
	private RoomAndZoneResponse convertZoneByValue(Map<String, String> stats, String property, Map<String, String> mapOfDevice, Map<String, String> mapOfNameAndId, boolean isEditZone) {
		String name = stats.get(property + RoomsAndZonesControlEnum.NAME.getName());
		String type = stats.get(property + RoomsAndZonesControlEnum.TYPE.getName());
		List<String> id = new LinkedList<>();
		List<Children> deviceIdList = new LinkedList<>();
		for (String value : mapOfDevice.values()) {
			Children children = new Children();
			if (!PhilipsConstant.NONE.equals(value) && !StringUtils.isNullOrEmpty(value)) {
				for (Entry<String, String> idDevice : mapOfNameAndId.entrySet()) {
					if (!id.contains(idDevice.getValue())) {
						children.setRid(idDevice.getValue());
						children.setType(PhilipsConstant.LIGHT.toLowerCase(Locale.ROOT));
						deviceIdList.add(children);
						id.add(idDevice.getValue());
						break;
					}
				}
			}
		}
		if (StringUtils.isNullOrEmpty(name)) {
			throw new ResourceNotReachableException("Error while creating Zone, Zone name can't empty");
		}
		if (PhilipsConstant.NONE.equals(type)) {
			throw new ResourceNotReachableException("Error while creating Zone, Zone type can't empty");
		}
		if (isEditZone) {
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
						zoneNameAndIdMap.put(PhilipsConstant.ALL_DEVICE + PhilipsConstant.DASH + zone.getMetaData().getName(), zone.getId());
					}
				}
				zoneNameAndIdMap.put(PhilipsConstant.NONE, PhilipsConstant.NONE);
			} else {
				throw new ResourceNotReachableException("List zones data is empty");
			}
		} catch (Exception e) {
			String errorMessage = String.format("List Zones Data Retrieval-Error: %s with cause: %s", e.getMessage(), e.getCause().getMessage());
			logger.error(errorMessage, e);
		}
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
			String errorMessage = String.format("List group light Data Retrieval-Error: %s with cause: %s", e.getMessage(), e.getCause().getMessage());
			logger.error(errorMessage, e);
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
						roomNameAndIdMap.put(PhilipsConstant.ALL_DEVICE + PhilipsConstant.DASH + room.getMetaData().getName(), room.getId());
					}
				}
				roomNameAndIdMap.put(PhilipsConstant.NONE, PhilipsConstant.NONE);
			} else {
				throw new ResourceNotReachableException("List rooms data is empty");
			}
		} catch (Exception e) {
			String errorMessage = String.format("List Rooms Data Retrieval-Error: %s with cause: %s", e.getMessage(), e.getCause().getMessage());
			logger.error(errorMessage, e);
		}
	}

	/**
	 * Retrieve all automation
	 */
	private void retrieveAutomations() {
		try {
			AutomationWrapper automationWrapper = this.doGet(PhilipsUtil.getMonitorURL(PhilipsURL.AUTOMATION), AutomationWrapper.class);
			if (automationWrapper != null && automationWrapper.getData() != null) {
				Collections.addAll(automationList, automationWrapper.getData());
			} else {
				throw new ResourceNotReachableException("List automation data is empty");
			}
		} catch (Exception e) {
			String errorMessage = String.format("List automation Data Retrieval-Error: %s with cause: %s", e.getMessage(), e.getCause().getMessage());
			logger.error(errorMessage, e);
		}
	}

	/**
	 * Retrieve all script id
	 */
	private void retrieveScriptAutomation() {
		try {
			ScriptAutomationWrapper automationWrapper = this.doGet(PhilipsUtil.getMonitorURL(PhilipsURL.SCRIPT_ID), ScriptAutomationWrapper.class);
			if (automationWrapper != null && automationWrapper.getData() != null) {
				Collections.addAll(scriptAutomationList, automationWrapper.getData());
			} else {
				throw new ResourceNotReachableException("List automation data is empty");
			}
		} catch (Exception e) {
			String errorMessage = String.format("List automation Data Retrieval-Error: %s with cause: %s", e.getMessage(), e.getCause().getMessage());
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
				boolean isDeviceExitsInRoom = false;
				String serviceID = PhilipsConstant.NONE;
				String serviceType = PhilipsConstant.NONE;
				ServicesResponse servicesData = aggregatorDeviceResponse.getServices()[0];
				if (servicesData != null) {
					serviceID = servicesData.getId();
					serviceType = servicesData.getType();
				}
				String aggregatorDeviceID = aggregatorDeviceResponse.getId();
				String aggregatorDeviceName = aggregatorDeviceResponse.getMetaData().getName();
				for (RoomAndZoneResponse response : roomList) {
					List<String> deviceList = Arrays.stream(response.getChildren()).map(Children::getRid).collect(Collectors.toList());
					if (PhilipsConstant.LIGHT.equalsIgnoreCase(serviceType) && deviceList.contains(aggregatorDeviceID)) {
						deviceExitsInRoomMap.put(aggregatorDeviceName, PhilipsConstant.TRUE);
						deviceNameAndDeviceIdZoneMap.put(aggregatorDeviceName + PhilipsConstant.DASH + response.getMetaData().getName(), serviceID);
						isDeviceExitsInRoom = true;
						break;
					}
				}
				if (!isDeviceExitsInRoom && PhilipsConstant.LIGHT.equalsIgnoreCase(serviceType)) {
					deviceNameAndDeviceIdZoneMap.put(aggregatorDeviceName, serviceID);
					deviceExitsInRoomMap.put(aggregatorDeviceName, PhilipsConstant.FALSE);
				}
				AggregatedDevice aggregatedDevice = new AggregatedDevice();
				Map<String, String> properties = new HashMap<>();
				if (!PhilipsConstant.BRIDGE.equalsIgnoreCase(serviceType)) {
					if (PhilipsConstant.LIGHT.equalsIgnoreCase(serviceType)) {
						Map<String, String> idDeviceMap = new HashMap<>();
						idDeviceMap.put(aggregatorDeviceID, serviceID);
						deviceNameAndMapDeviceIdOfRoomMap.put(aggregatorDeviceName, idDeviceMap);
						allDeviceIdAndNameMap.put(aggregatorDeviceID, aggregatorDeviceName);
						if (roomList.isEmpty()) {
							deviceExitsInRoomMap.put(aggregatorDeviceName, PhilipsConstant.FALSE);
							deviceNameAndDeviceIdZoneMap.put(aggregatorDeviceName, serviceID);
						}
					}
					aggregatedDevice.setDeviceId(serviceID);
					aggregatedDevice.setDeviceModel(aggregatorDeviceResponse.getProductData().getModel());
					aggregatedDevice.setDeviceName(aggregatorDeviceResponse.getProductData().getName());
					aggregatedDevice.setProperties(properties);
					aggregatedDeviceList.add(aggregatedDevice);
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
			String errorMessage = String.format("List Bridge Id Data Retrieval-Error: %s with cause: %s", e.getMessage(), e.getCause().getMessage());
			logger.error(errorMessage, e);
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
			String errorMessage = String.format("Network Information Data Retrieval-Error: %s with cause: %s", e.getMessage(), e.getCause().getMessage());
			logger.error(errorMessage, e);
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
			String errorMessage = String.format("System Information Data Retrieval-Error: %s with cause: %s", e.getMessage(), e.getCause().getMessage());
			logger.error(errorMessage, e);
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
			String errorMessage = String.format("Status for the device Data Retrieval-Error: %s with cause: %s", e.getMessage(), e.getCause().getMessage());
			logger.error(errorMessage, e);
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
			//example value  1xxxxxxx, return max value
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