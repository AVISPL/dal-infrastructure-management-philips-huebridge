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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.CreateRoomEum;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.EnumTypeHandler;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.NetworkInfoEnum;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.PhilipsConstant;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.PhilipsURL;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.PhilipsUtil;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.RoomTypeEnum;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.RoomsAndZonesControlEnum;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.SystemInfoEnum;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.AggregatorWrapper;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.BridgeWrapper;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.GroupLightWrapper;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.NetworkInfoResponse;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.ResponseData;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.RoomAndZoneWrapper;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.SystemWrapper;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.ZigbeeConnectivityWrapper;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.aggregateddevice.AggregatorDeviceResponse;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.aggregateddevice.ProductData;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.bridge.BridgeListResponse;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.grouplight.GroupLightResponse;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.responseData.ErrorsResponse;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.romandzone.Children;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.romandzone.MetaData;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.romandzone.RoomAndZoneResponse;
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
	 * Process that is running constantly and triggers collecting data from Q-Sys API endpoints, based on the given timeouts and thresholds.
	 *
	 * @author Maksym.Rossiytsev, Harry
	 * @since 1.0.0
	 */
	class PhilipsHueDeviceDataLoader implements Runnable {
		private final int threadIndex;
		private final List<String> deviceIds;

		public PhilipsHueDeviceDataLoader(int threadIndex, List<String> deviceIds) {
			this.threadIndex = threadIndex;
			this.deviceIds = deviceIds;
		}

		@Override
		public void run() {
			if (logger.isDebugEnabled()) {
				logger.debug("Fetching other than Philips device list" + threadIndex);
			}
			Long currentTime = System.currentTimeMillis();

			if (logger.isDebugEnabled()) {
				logger.debug("finished for" + threadIndex + "   " + (System.currentTimeMillis() - currentTime));
			}

			if (!aggregatedDeviceList.isEmpty()) {
				if (logger.isDebugEnabled()) {
					logger.debug("Applying filter options" + (System.currentTimeMillis() - currentTime));
				}
				if (logger.isDebugEnabled()) {
					logger.debug("Aggregated devices after applying filter: " + aggregatedDeviceList);
				}
			}

			if (logger.isDebugEnabled()) {
				logger.debug("Finished collecting devices statistics cycle at " + new Date());
			}
			// Finished collecting
		}
	}

	private ExtendedStatistics localExtendedStatistics;
	private ExtendedStatistics localCreateZone;
	private ExtendedStatistics localCreateRoom;

	private boolean isCreateRoom;
	private boolean isCreateZone;
	private boolean isEmergencyDelivery;

	//The properties adapter
	private String zoneName;
	private String roomNames;
	private String deviceTypes;
	private String deviceNames;

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
	 * List of error message occur while fetching system information
	 */
	private final Set<String> systemErrorMessagesList = Collections.synchronizedSet(new LinkedHashSet<>());

	/**
	 * List of ID bridge
	 */
	private final Set<String> bridgeIdList = Collections.synchronizedSet(new LinkedHashSet<>());

	/**
	 * List of aggregated device
	 */
	private final List<AggregatedDevice> aggregatedDeviceList = Collections.synchronizedList(new ArrayList<>());

	/**
	 * List of Room
	 */
	private final List<RoomAndZoneResponse> roomList = Collections.synchronizedList(new ArrayList<>());

	/**
	 * List of Zones
	 */
	private final List<RoomAndZoneResponse> zoneList = Collections.synchronizedList(new ArrayList<>());

	/**
	 * List of Group Light
	 */
	private final List<GroupLightResponse> groupLightList = Collections.synchronizedList(new ArrayList<>());

	/**
	 * Map of name and grouped light
	 */
	private final Map<String, GroupLightResponse> groupLightMap = new HashMap<>();

	private final Map<String, String> deviceRoomMap = new HashMap<>();
	private final Map<String, String> deviceZoneMap = new HashMap<>();
	private final Map<String, Map<String, String>> editDeviceRoomMap = new HashMap<>();
	private final Map<String, Map<String, String>> editDeviceZoneMap = new HashMap<>();
	private final Map<String, String[]> editMapOfRoomAndDropdownList = new HashMap<>();
	private final Map<String, Map<String, String>> deviceNameAndMapIdDeviceOfRoomMap = new HashMap<>();
	private final Map<String, String> nameAndDeviceOfZoneMap = new HashMap<>();
	private final Map<String, String> deviceExitsInRoomMap = new HashMap<>();
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
	 * Executor that runs all the async operations, that {@link #deviceDataLoader} is posting and
	 */
	private static ExecutorService executorService;

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

			Map<String, String> localStats = localExtendedStatistics.getStatistics();
			List<AdvancedControllableProperty> localControllableProperties = localExtendedStatistics.getControllableProperties();
			if (PhilipsConstant.CREATE_ROOM.equals(propertyGroup)) {
				populateCreateRoom(property, value, updateCreateRoom, updateCreateRoomControllableProperties);
			} else if (PhilipsConstant.CREATE_ZONE.equals(propertyGroup)) {
				populateCreateZone(property, value, updateCreateZone, updateCreateZoneControllableProperties);
			} else {
				if (PhilipsConstant.ROOM.equals(property.substring(0, PhilipsConstant.ROOM.length()))) {
					populateControlRoom(property, value, localStats, localControllableProperties, true);
				}
				if (PhilipsConstant.ZONE.equals(property.substring(0, PhilipsConstant.ZONE.length()))) {
					populateControlRoom(property, value, localStats, localControllableProperties, false);
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

			Map<String, String> localStats = localExtendedStatistics.getStatistics();
			Map<String, String> localRoomStats = localCreateRoom.getStatistics();
			Map<String, String> localZoneStats = localCreateZone.getStatistics();
			localStats.putAll(localRoomStats);
			localStats.putAll(localZoneStats);

			List<AdvancedControllableProperty> localAdvancedControl = localExtendedStatistics.getControllableProperties();
			List<AdvancedControllableProperty> localCreateRoomControl = localCreateRoom.getControllableProperties();
			List<AdvancedControllableProperty> localCreateZoneControl = localCreateZone.getControllableProperties();

			List<String> nameRoomList = localCreateRoomControl.stream().map(AdvancedControllableProperty::getName).collect(Collectors.toList());
			List<String> nameZoneList = localCreateZoneControl.stream().map(AdvancedControllableProperty::getName).collect(Collectors.toList());

			localAdvancedControl.removeIf(item -> nameRoomList.contains(item.getName()));
			localAdvancedControl.removeIf(item -> nameZoneList.contains(item.getName()));

			localAdvancedControl.addAll(localCreateRoomControl);
			localAdvancedControl.addAll(localCreateZoneControl);

			if (executorService == null) {
				executorService = Executors.newFixedThreadPool(8);
			}
			if (checkValidApiToken()) {
				for (int threadNumber = 0; threadNumber < 8; threadNumber++) {
					executorService.submit(deviceDataLoader = new PhilipsHueDeviceDataLoader(threadNumber, new LinkedList<>()));
				}
			}
		} finally {
			reentrantLock.unlock();
		}
		return Collections.singletonList(localExtendedStatistics);
	}

	/**
	 * Clear data before fetching data
	 */
	private void clearBeforeFetchingData() {
		bridgeIdList.clear();
		aggregatedDeviceList.clear();
		systemErrorMessagesList.clear();
		roomList.clear();
		editDeviceRoomMap.clear();
		zoneList.clear();
		roomList.clear();
		groupLightList.clear();
		groupLightMap.clear();
		editDeviceRoomMap.clear();
		deviceNameAndMapIdDeviceOfRoomMap.clear();
		nameAndDeviceOfZoneMap.clear();
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
		populateRoomAndZones(stats, advancedControllableProperties, true);
		populateRoomAndZones(stats, advancedControllableProperties, false);
	}

	/**
	 * Populate rooms and zones control
	 *
	 * @param stats the stats are list of statistics
	 * @param advancedControllableProperties advancedControllableProperties is the list that store all controllable properties
	 */
	private void populateRoomAndZones(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties, boolean isRoomType) {
		if (isRoomType) {
			for (RoomAndZoneResponse roomItem : roomList) {
				String groupName = PhilipsConstant.ROOM + PhilipsConstant.DASH + roomItem.getMetaData().getName();
				populateDetailsRoomAndZone(stats, advancedControllableProperties, roomItem, groupName, true);
			}
		} else {
			for (RoomAndZoneResponse zoneItem : zoneList) {
				String groupName = PhilipsConstant.ZONE + PhilipsConstant.DASH + zoneItem.getMetaData().getName();
				populateDetailsRoomAndZone(stats, advancedControllableProperties, zoneItem, groupName, false);
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
	private void populateDetailsRoomAndZone(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties, RoomAndZoneResponse roomAndZoneResponse, String groupName,
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
		Map<String, String> device = new HashMap<>();
		for (int i = 0; i < allDeviceIdAndNameMap.size() - 1; i++) {
			device.put(PhilipsConstant.DEVICE + i, null);
		}
		String[] deviceDropdown = deviceExitsInRoomMap.entrySet().stream().filter(item -> item.getValue().equals(PhilipsConstant.FALSE)).map(Map.Entry::getKey).collect(Collectors.toList())
				.toArray(new String[0]);
		List<Children> children = Arrays.stream(roomAndZoneResponse.getChildren()).collect(Collectors.toList());
		int index = children.size();

		Map<String, String> mapOfRoomAndDevice = new HashMap<>();
		String[] deviceDropdown1 = Arrays.copyOf(deviceDropdown, deviceDropdown.length + index);
		for (int i = 0; i < index; i++) {
			Children children1 = children.get(i);
			String deviceName = allDeviceIdAndNameMap.get(children1.getRid());
			if (!StringUtils.isNullOrEmpty(deviceName)) {
				deviceDropdown1[deviceDropdown.length + i] = deviceName;
			}
		}
		for (int i = 0; i < index + 1; i++) {
			if (i < index) {
				Children children1 = children.get(i);
				String deviceName = allDeviceIdAndNameMap.get(children1.getRid());
				mapOfRoomAndDevice.put(PhilipsConstant.DEVICE + i, deviceName);
				advancedControllableProperties.add(controlDropdown(stats, deviceDropdown1, groupName + PhilipsConstant.HASH + PhilipsConstant.DEVICE + i, deviceName));
			} else {
				if (i == 0 && index == 0) {
					mapOfRoomAndDevice.put(PhilipsConstant.DEVICE + i, PhilipsConstant.NONE);
					advancedControllableProperties.add(controlDropdown(stats, deviceDropdown1, groupName + PhilipsConstant.HASH + PhilipsConstant.DEVICE + i, PhilipsConstant.NONE));
					break;
				} else {
					mapOfRoomAndDevice.put(PhilipsConstant.DEVICE + i, null);
				}
			}
		}
		editDeviceRoomMap.put(roomAndZoneResponse.getMetaData().getName(), mapOfRoomAndDevice);
		editMapOfRoomAndDropdownList.put(roomAndZoneResponse.getMetaData().getName(), deviceDropdown1);
	}

	/**
	 * populate device for rooms and zones
	 *
	 * @param stats the stats are list of statistics
	 * @param advancedControllableProperties advancedControllableProperties is the list that store all controllable properties
	 */
	private void populateDeviceForZone(String groupName, RoomAndZoneResponse roomAndZoneResponse, Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {
		Map<String, String> device = new HashMap<>();
		for (int i = 0; i < allDeviceIdAndNameMap.size() - 1; i++) {
			device.put(PhilipsConstant.DEVICE + i, null);
		}
		String[] deviceDropdown = nameAndDeviceOfZoneMap.keySet().toArray(new String[0]);
		List<Children> children = Arrays.stream(roomAndZoneResponse.getChildren()).collect(Collectors.toList());
		int index = children.size();
		Map<String, String> mapOfRoomAndDevice = new HashMap<>();
		for (int i = 0; i < index + 1; i++) {
			if (i < index) {
				Children children1 = children.get(i);
				Optional<Entry<String, String>> deviveNameOption = nameAndDeviceOfZoneMap.entrySet().stream().filter(item -> item.getValue().equals(children1.getRid())).findFirst();
				String deviceName = PhilipsConstant.EMPTY_STRING;
				if (deviveNameOption.isPresent()) {
					deviceName = deviveNameOption.get().getKey();
				}
				mapOfRoomAndDevice.put(PhilipsConstant.DEVICE + i, deviceName);
				advancedControllableProperties.add(controlDropdown(stats, deviceDropdown, groupName + PhilipsConstant.HASH + PhilipsConstant.DEVICE + i, deviceName));
			} else {
				if (i == 0 && index == 0) {
					mapOfRoomAndDevice.put(PhilipsConstant.DEVICE + i, PhilipsConstant.NONE);
					advancedControllableProperties.add(controlDropdown(stats, deviceDropdown, groupName + PhilipsConstant.HASH + PhilipsConstant.DEVICE + i, PhilipsConstant.NONE));
					break;
				} else {
					mapOfRoomAndDevice.put(PhilipsConstant.DEVICE + i, null);
				}
			}
		}
		editDeviceZoneMap.put(roomAndZoneResponse.getMetaData().getName(), mapOfRoomAndDevice);
	}

	/**
	 * Update value and dropdown list of Device
	 *
	 * @param property the property is the filed name of controlling metric
	 * @param stats list of statistics
	 * @param advancedControllableProperties the advancedControllableProperties is advancedControllableProperties instance
	 * @param value the value is value of metric
	 * @param deviceProperty the deviceProperty is name of device
	 */
	private void updateDeviceRoomDropdownList(String property, String value, Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties, String deviceProperty,
			List<String> nameList, Map<String, String> mapOfNameDevice) {
		if (PhilipsConstant.NONE.equals(value) && !PhilipsConstant.DEVICE_0.equals(deviceProperty)) {
			stats.remove(property);
			mapOfNameDevice.replace(deviceProperty, null);
		} else {
			String[] deviceDropdownList = nameList.toArray(new String[0]);
			Arrays.sort(deviceDropdownList);
			AdvancedControllableProperty deviceControlProperty = controlDropdown(stats, deviceDropdownList, property, value);
			addOrUpdateAdvanceControlProperties(advancedControllableProperties, deviceControlProperty);
			mapOfNameDevice.put(deviceProperty, value);
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

		for (int i = 0; i < allDeviceIdAndNameMap.size() - 1; i++) {
			deviceRoomMap.put(PhilipsConstant.DEVICE + i, null);
		}
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

		for (int i = 0; i < nameAndDeviceOfZoneMap.size() - 1; i++) {
			deviceZoneMap.put(PhilipsConstant.DEVICE + i, null);
		}
		String[] deviceDropdown = nameAndDeviceOfZoneMap.keySet().toArray(new String[0]);
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
		if (key.contains(PhilipsConstant.DEVICE) && !CreateRoomEum.DEVICE_ADD.getName().equals(key)) {
			updateDeviceRoomDropdownList(property, value, stats, advancedControllableProperties, key, deviceDropdown, deviceRoomMap);
		} else {
			RoomsAndZonesControlEnum room = EnumTypeHandler.getMetricOfEnumByName(RoomsAndZonesControlEnum.class, key);
			switch (room) {
				case DEVICE_STATUS:
				case NAME:
				case TYPE:
					updateValueForTheControllableProperty(property, value, stats, advancedControllableProperties);
					break;
				case CREATE:
					RoomAndZoneResponse roomValue = convertRoomByValue(stats, PhilipsConstant.CREATE_ROOM + PhilipsConstant.HASH, deviceRoomMap, deviceNameAndMapIdDeviceOfRoomMap, false);
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
						logger.debug(String.format("Controlling create Room and Zone by name %s is not supported.", room.getName()));
					}
			}
		}
		populateCancelChangeButton(stats, advancedControllableProperties, propertyGroup, isCreateRoom);
	}

	/**
	 * Send request to change status for room/zone
	 *
	 * @param id the id is id of room/zone
	 * @param status the staatus is status of room/zone
	 * @param isRoomType the isRoomType is boolean value
	 */
	private void sendRequestToChangeStatus(String id, String status, boolean isRoomType) {
		try {
			ResponseData responseData = doPut(PhilipsUtil.getMonitorURL(PhilipsURL.GROUP_LIGHT) + PhilipsConstant.SLASH + id, status, ResponseData.class);
			if (responseData.getErrors().length > 0) {
				String errorMessage;
				if (PhilipsConstant.ERROR_MESSAGE_STATUS.equals(responseData.getErrors()[0].getDescription())) {
					errorMessage = String.format("Error while changing status for %s: the status is offline", isRoomType ? PhilipsConstant.ROOM : PhilipsConstant.ZONE);
				} else {
					errorMessage = String.format("Error while changing status for %s: %s", isRoomType ? PhilipsConstant.ROOM : PhilipsConstant.ZONE, responseData.getErrors()[0].getDescription());
				}
				throw new ResourceNotReachableException(errorMessage);
			}
		} catch (Exception e) {
			throw new ResourceNotReachableException(String.format("Can't change status for %s: ", isRoomType ? PhilipsConstant.ROOM : PhilipsConstant.ZONE) + e.getMessage(), e);
		}
	}

	/**
	 * Populate Control room
	 *
	 * @param property the property is property name with format GroupName#KeyName
	 * @param value the value is value of property
	 * @param stats the stats are list of statistics
	 * @param advancedControllableProperties advancedControllableProperties is the list that store all controllable properties
	 */
	private void populateControlRoom(String property, String value, Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties, boolean isEditRoom) {
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
			mapOfDeviceDropdown = editDeviceRoomMap.get(group);
			deviceDropdown = Arrays.stream(editMapOfRoomAndDropdownList.get(group)).collect(Collectors.toList());
		} else {
			group = propertyGroup.substring(PhilipsConstant.ZONE.length() + 1);
			roomAndZoneResponse = zoneList.stream().filter(item -> item.getMetaData().getName().equals(group)).findFirst();
			deviceList = nameAndDeviceOfZoneMap.keySet().stream().collect(Collectors.toList());
			mapOfDeviceDropdown = editDeviceZoneMap.get(group);
			deviceDropdown = nameAndDeviceOfZoneMap.keySet().stream().collect(Collectors.toList());
		}
		boolean isCurrentEmergencyDelivery = isEmergencyDelivery;
		isEmergencyDelivery = true;
		if (key.contains(PhilipsConstant.DEVICE) && !CreateRoomEum.DEVICE_ADD.getName().equals(key) && !RoomsAndZonesControlEnum.DEVICE_STATUS.getName().equals(key)) {
			updateDeviceRoomDropdownList(property, value, stats, advancedControllableProperties, key, deviceList, mapOfDeviceDropdown);
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
						if (isEditRoom) {
							sendRequestToDeleteRoomAndZone(roomAndZoneResponse.get().getId(), true);
						} else {
							sendRequestToDeleteRoomAndZone(roomAndZoneResponse.get().getId(), false);
						}
					}
					isEmergencyDelivery = false;
					localExtendedStatistics = new ExtendedStatistics();
					break;
				case DEVICE_ADD:
					addDeviceForRoomAndZone(propertyGroup, stats, advancedControllableProperties, deviceDropdown, mapOfDeviceDropdown);
					break;
				case CANCEL:
					isEmergencyDelivery = false;
					break;
				case APPLY_CHANGE:
					RoomAndZoneResponse roomValue;
					if (roomAndZoneResponse.isPresent() && isEditRoom) {
						roomValue = convertRoomByValue(stats, propertyGroup + PhilipsConstant.HASH, mapOfDeviceDropdown, deviceNameAndMapIdDeviceOfRoomMap, true);
						roomValue.setId(roomAndZoneResponse.get().getId());
						sendRequestToCreateOrEditRoomAndZone(roomValue, true, true);
					} else {
						roomValue = convertZoneByValue(stats, propertyGroup + PhilipsConstant.HASH, mapOfDeviceDropdown, nameAndDeviceOfZoneMap);
						roomValue.setId(roomAndZoneResponse.get().getId());
						sendRequestToCreateOrEditRoomAndZone(roomValue, false, true);
					}
					isEmergencyDelivery = false;
					break;
				default:
					if (logger.isDebugEnabled()) {
						logger.debug(String.format("Controlling create Room and Zone by name %s is not supported.", room.getName()));
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
	 * @param nameList the nameList is list name device
	 * @param mapOfNameDevice the map name and  id of device
	 */
	private void addDeviceForRoomAndZone(String groupName, Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties, List<String> nameList,
			Map<String, String> mapOfNameDevice) {
		String[] deviceNameArray = nameList.toArray(new String[0]);
		Arrays.sort(deviceNameArray);
		String prefixName = groupName + PhilipsConstant.HASH;
		//Check list device in room
		int countDevice = 0;
		for (Map.Entry<String, String> key : mapOfNameDevice.entrySet()) {
			if (key.getValue() != null) {
				//count the number of device
				countDevice++;
			}
		}
		if (countDevice >= nameList.size() - 1) {
			throw new ResourceNotReachableException(
					String.format("Total device is %s, you have added enough devices and cannot add new devices. Please remove the device and try again", nameList.size() - 1));
		}
		for (Map.Entry<String, String> deviceEntry : mapOfNameDevice.entrySet()) {
			String defaultName = PhilipsConstant.NONE;
			for (String nameValue : nameList) {
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
		if (key.contains(PhilipsConstant.DEVICE) && !CreateRoomEum.DEVICE_ADD.getName().equals(key)) {
			updateDeviceRoomDropdownList(property, value, stats, advancedControllableProperties, key, nameAndDeviceOfZoneMap.keySet().stream().collect(Collectors.toList()), deviceZoneMap);
		} else {
			RoomsAndZonesControlEnum zone = EnumTypeHandler.getMetricOfEnumByName(RoomsAndZonesControlEnum.class, key);
			switch (zone) {
				case DEVICE_STATUS:
				case NAME:
				case TYPE:
					updateValueForTheControllableProperty(property, value, stats, advancedControllableProperties);
					break;
				case CREATE:
					RoomAndZoneResponse roomConvertData = convertZoneByValue(stats, PhilipsConstant.CREATE_ZONE + PhilipsConstant.HASH, deviceZoneMap, nameAndDeviceOfZoneMap);
					sendRequestToCreateOrEditRoomAndZone(roomConvertData, false, false);
					isCreateZone = false;
					break;
				case DEVICE_ADD:
					List<String> deviceDropdown = nameAndDeviceOfZoneMap.keySet().stream().collect(Collectors.toList());
					addDeviceForRoomAndZone(propertyGroup, stats, advancedControllableProperties, deviceDropdown, deviceZoneMap);
					break;
				case CANCEL:
					isCreateZone = false;
					break;
				default:
					if (logger.isDebugEnabled()) {
						logger.debug(String.format("Controlling create Room and Zone by name %s is not supported.", zone.getName()));
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
		String request = isRoomType ? PhilipsUtil.getMonitorURL(PhilipsURL.ROOMS) : PhilipsUtil.getMonitorURL(PhilipsURL.ZONES);
		try {
			ResponseData responseData;
			if (isEditedRoomOrZone) {
				responseData = doPut(request + PhilipsConstant.SLASH + roomResponseData.getId(), roomResponseData.toString(), ResponseData.class);
			} else {
				responseData = doPost(request, roomResponseData.toString(), ResponseData.class);
			}
			if (responseData.getData() == null) {
				throw new ResourceNotReachableException(
						String.format("Error while creating/Editing %s: ", isRoomType ? PhilipsConstant.ROOM : PhilipsConstant.ZONE) + Arrays.stream(responseData.getErrors()).map(ErrorsResponse::getDescription));
			}
		} catch (Exception e) {
			throw new ResourceNotReachableException(String.format("Can't create/edit %s: ", isRoomType ? PhilipsConstant.ROOM : PhilipsConstant.ZONE) + e.getMessage(), e);
		}
	}

	/**
	 * Send request to delete  room
	 *
	 * @param id the id is id of room
	 * @param isDeleteRoom the boolean type
	 */
	private void sendRequestToDeleteRoomAndZone(String id, boolean isDeleteRoom) {
		String request = PhilipsUtil.getMonitorURL(PhilipsURL.ZONES);
		if (isDeleteRoom) {
			request = PhilipsUtil.getMonitorURL(PhilipsURL.ROOMS);
		}
		try {
			doDelete(request + PhilipsConstant.SLASH + id);
		} catch (Exception e) {
			throw new ResourceNotReachableException("Can't create room: " + e.getMessage(), e);
		}
	}

	/**
	 * Convert Room and Zone by value
	 *
	 * @param stats the stats are list of statistics
	 * @param property the property is property name
	 * @return RoomAndZoneResponse is instance in RoomAndZoneResponseDTO
	 */
	private RoomAndZoneResponse convertRoomByValue(Map<String, String> stats, String property, Map<String, String> deviceDropdown, Map<String, Map<String, String>> mapOfNameAndId, boolean isEdited) {
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
		checkNameOfRoomAndZoneIsPercent(name, isEdited);
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
	 * @param isEdited the is edited is boolean vallue with isEdited = true then edited room and zone
	 */
	private void checkNameOfRoomAndZoneIsPercent(String name, boolean isEdited) {
		if (!isEdited) {
			try {
				RoomAndZoneWrapper zoneWrapper = this.doGet(PhilipsUtil.getMonitorURL(PhilipsURL.ROOMS), RoomAndZoneWrapper.class);
				if (zoneWrapper != null && zoneWrapper.getData() != null && Arrays.stream(zoneWrapper.getData()).filter(item -> item.getMetaData().getName().equals(name)).findFirst().isPresent()) {
					throw new ResourceNotReachableException(String.format("The name: %s already exists", name));
				}
			} catch (Exception e) {
				throw new ResourceNotReachableException(String.format("Error when create Room/Zone: ", e.getMessage(), e));
			}
		}
	}

	/**
	 * Convert Room and Zone by value
	 *
	 * @param stats the stats are list of statistics
	 * @param property the property is property name
	 * @return RoomAndZoneResponse is instance in RoomAndZoneResponseDTO
	 */
	private RoomAndZoneResponse convertZoneByValue(Map<String, String> stats, String property, Map<String, String> deviceDropdown, Map<String, String> mapOfNameAndId) {
		String name = stats.get(property + RoomsAndZonesControlEnum.NAME.getName());
		String type = stats.get(property + RoomsAndZonesControlEnum.TYPE.getName());
		List<String> id = new LinkedList<>();
		List<Children> newListChildren = new LinkedList<>();
		for (String value : deviceDropdown.values()) {
			Children children = new Children();
			if (!PhilipsConstant.NONE.equals(value) && !StringUtils.isNullOrEmpty(value)) {
				for (Entry<String, String> idDevice : mapOfNameAndId.entrySet()) {
					if (!id.contains(idDevice.getValue())) {
						children.setRid(idDevice.getValue());
						children.setType(PhilipsConstant.LIGHT.toLowerCase(Locale.ROOT));
						newListChildren.add(children);
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

		type = RoomTypeEnum.getValueOfRoomTypeEnumByName(type);
		MetaData metaData = new MetaData();
		metaData.setArchetype(type);
		metaData.setName(name);
		RoomAndZoneResponse response = new RoomAndZoneResponse();
		response.setChildren(newListChildren.toArray(new Children[0]));
		response.setType(PhilipsConstant.ZONE.toLowerCase(Locale.ROOT));
		response.setMetaData(metaData);
		return response;
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
	 * Check API token validation
	 *
	 * @return boolean
	 */
	private boolean checkValidApiToken() {
		return !StringUtils.isNullOrEmpty(apiToken);
	}

	/**
	 * Retrieve list zones
	 */
	private void retrieveZones() {
		try {
			RoomAndZoneWrapper zoneWrapper = this.doGet(PhilipsUtil.getMonitorURL(PhilipsURL.ZONES), RoomAndZoneWrapper.class);
			if (zoneWrapper != null && zoneWrapper.getData() != null) {
				Collections.addAll(zoneList, zoneWrapper.getData());
			}
		} catch (Exception e) {
			String errorMessage = String.format("List Zones Data Retrieval-Error: %s with cause: %s", e.getMessage(), e.getCause().getMessage());
			systemErrorMessagesList.add(errorMessage);
			logger.error(errorMessage, e);
		}
	}

	/**
	 * Retrieve all rooms
	 */
	private synchronized void retrieveGroupLight() {
		try {
			GroupLightWrapper groupLightWrapper = this.doGet(PhilipsUtil.getMonitorURL(PhilipsURL.GROUP_LIGHT), GroupLightWrapper.class);
			if (groupLightWrapper != null && groupLightWrapper.getData() != null) {
				Collections.addAll(groupLightList, groupLightWrapper.getData());
				for (GroupLightResponse groupLightResponse : groupLightWrapper.getData()) {
					groupLightMap.put(groupLightResponse.getOwner().getRid(), groupLightResponse);
				}
			} else {
				systemErrorMessagesList.add("Retrieve rooms list error");
			}
		} catch (Exception e) {
			String errorMessage = String.format("List Rooms Data Retrieval-Error: %s with cause: %s", e.getMessage(), e.getCause().getMessage());
			systemErrorMessagesList.add(errorMessage);
			logger.error(errorMessage, e);
		}
	}

	/**
	 * Retrieve all group_light
	 */
	private synchronized void retrieveRooms() {
		try {
			RoomAndZoneWrapper roomAndZoneWrapper = this.doGet(PhilipsUtil.getMonitorURL(PhilipsURL.ROOMS), RoomAndZoneWrapper.class);
			if (roomAndZoneWrapper != null && roomAndZoneWrapper.getData() != null) {
				Collections.addAll(roomList, roomAndZoneWrapper.getData());
			} else {
				systemErrorMessagesList.add("Retrieve rooms list error");
			}
		} catch (Exception e) {
			String errorMessage = String.format("List Rooms Data Retrieval-Error: %s with cause: %s", e.getMessage(), e.getCause().getMessage());
			systemErrorMessagesList.add(errorMessage);
			logger.error(errorMessage, e);
		}
	}

	/**
	 * Retrieve all Aggregated device
	 */
	private void retrieveDevices() {
		try {
			AggregatorWrapper systemResponse = this.doGet(PhilipsUtil.getMonitorURL(PhilipsURL.DEVICE), AggregatorWrapper.class);
			for (AggregatorDeviceResponse aggregatorDeviceResponse : systemResponse.getData()) {
				boolean isDeviceExitsInRoom = false;
				for (RoomAndZoneResponse response : roomList) {
					if (PhilipsConstant.LIGHT.equalsIgnoreCase(aggregatorDeviceResponse.getServices()[0].getType()) && Arrays.stream(response.getChildren()).map(Children::getRid).collect(Collectors.toList())
							.contains(aggregatorDeviceResponse.getId())) {
						deviceExitsInRoomMap.put(aggregatorDeviceResponse.getMetaData().getName(), PhilipsConstant.TRUE);
						nameAndDeviceOfZoneMap.put(aggregatorDeviceResponse.getMetaData().getName() + PhilipsConstant.DASH + response.getMetaData().getName(), aggregatorDeviceResponse.getServices()[0].getId());
						isDeviceExitsInRoom = true;
						break;
					}
				}
				if (!isDeviceExitsInRoom && PhilipsConstant.LIGHT.equalsIgnoreCase(aggregatorDeviceResponse.getServices()[0].getType())) {
					nameAndDeviceOfZoneMap.put(aggregatorDeviceResponse.getMetaData().getName(), aggregatorDeviceResponse.getServices()[0].getId());
					deviceExitsInRoomMap.put(aggregatorDeviceResponse.getMetaData().getName(), PhilipsConstant.FALSE);
				}
				AggregatedDevice aggregatedDevice = new AggregatedDevice();
				Map<String, String> properties = new HashMap<>();
				if (!PhilipsConstant.BRIDGE.equalsIgnoreCase(aggregatorDeviceResponse.getServices()[0].getType())) {
					if (PhilipsConstant.LIGHT.equalsIgnoreCase(aggregatorDeviceResponse.getServices()[0].getType())) {
						Map<String, String> idDeviceMap = new HashMap<>();
						idDeviceMap.put(aggregatorDeviceResponse.getId(), aggregatorDeviceResponse.getServices()[0].getId());
						deviceNameAndMapIdDeviceOfRoomMap.put(aggregatorDeviceResponse.getMetaData().getName(), idDeviceMap);
						allDeviceIdAndNameMap.put(aggregatorDeviceResponse.getId(), aggregatorDeviceResponse.getMetaData().getName());
						if (roomList.isEmpty()) {
							deviceExitsInRoomMap.put(aggregatorDeviceResponse.getMetaData().getName(), PhilipsConstant.FALSE);
							nameAndDeviceOfZoneMap.put(aggregatorDeviceResponse.getMetaData().getName(), aggregatorDeviceResponse.getServices()[0].getId());
						}
					}
					aggregatedDevice.setDeviceId(aggregatorDeviceResponse.getServices()[0].getId());
					aggregatedDevice.setDeviceModel(aggregatorDeviceResponse.getProductData().getModel());
					aggregatedDevice.setDeviceName(aggregatorDeviceResponse.getProductData().getName());
					aggregatedDevice.setProperties(properties);
					aggregatedDeviceList.add(aggregatedDevice);
				}
			}
			deviceNameAndMapIdDeviceOfRoomMap.put(PhilipsConstant.NONE, new HashMap<>());
			nameAndDeviceOfZoneMap.put(PhilipsConstant.NONE, PhilipsConstant.NONE);
			allDeviceIdAndNameMap.put(PhilipsConstant.NONE, PhilipsConstant.NONE);
			deviceExitsInRoomMap.put(PhilipsConstant.NONE, PhilipsConstant.FALSE);
		} catch (Exception e) {
			String errorMessage = String.format("System Information Data Retrieval-Error: %s with cause: %s", e.getMessage(), e.getCause().getMessage());
			systemErrorMessagesList.add(errorMessage);
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
			}
		} catch (Exception e) {
			String errorMessage = String.format("List Bridge Id Data Retrieval-Error: %s with cause: %s", e.getMessage(), e.getCause().getMessage());
			systemErrorMessagesList.add(errorMessage);
			logger.error(errorMessage, e);
		}
	}

	/**
	 * Get network information every 30 seconds
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
					stats.put(networkInfoEnum.getName(), networkInfoResponse.getValueByMetric(networkInfoEnum));
				}
			} else {
				contributeNoneValueByNetworkInfo(stats);
			}
		} catch (Exception e) {
			contributeNoneValueByNetworkInfo(stats);
			String errorMessage = String.format("Network Information Data Retrieval-Error: %s with cause: %s", e.getMessage(), e.getCause().getMessage());
			systemErrorMessagesList.add(errorMessage);
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
								if (SystemInfoEnum.ID.getName().equals(systemInfoEnum.getName())) {
									stats.put(groupName + systemInfoEnum.getName(), systemResponse.getId());
									continue;
								}
								if (SystemInfoEnum.TYPE.getName().equals(systemInfoEnum.getName())) {
									ServicesResponse[] services = systemResponse.getServices();
									for (ServicesResponse servicesResponse : services) {
										String type = servicesResponse.getType();
										if (PhilipsConstant.BRIDGE.equals(type)) {
											stats.put(groupName + systemInfoEnum.getName(), PhilipsConstant.BRIDGE);
										} else if (PhilipsConstant.ZIGBEE_CONNECTIVITY.equals(type)) {
											retrieveZigbeeConnectivity(groupName, servicesResponse.getId(), stats);
										}
									}
								} else {
									ProductData productData = systemResponse.getProductData();
									if (productData != null) {
										stats.put(groupName + systemInfoEnum.getName(), productData.getValueByMetric(systemInfoEnum));
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
			systemErrorMessagesList.add(errorMessage);
			logger.error(errorMessage, e);
		}
	}

	/**
	 * Value of list statistics property of network info is none
	 *
	 * @param stats list statistics
	 */
	private void contributeNoneValueByNetworkInfo(Map<String, String> stats) {
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
			}
		} catch (Exception e) {
			String errorMessage = String.format("Network Information Data Retrieval-Error: %s with cause: %s", e.getMessage(), e.getCause().getMessage());
			systemErrorMessagesList.add(errorMessage);
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
		if (StringUtils.isNullOrEmpty(value)) {
			return PhilipsConstant.NONE;
		}
		return value;
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
}