/**
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import com.avispl.symphony.api.dal.control.Controller;
import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.api.dal.dto.monitor.Statistics;
import com.avispl.symphony.api.dal.dto.monitor.aggregator.AggregatedDevice;
import com.avispl.symphony.api.dal.monitor.Monitorable;
import com.avispl.symphony.api.dal.monitor.aggregator.Aggregator;
import com.avispl.symphony.dal.communicator.RestCommunicator;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.NetworkInfoEnum;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.PhilipsConstant;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.PhilipsURL;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.PhilipsUtil;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common.SystemInfoEnum;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.AggregatorWrapper;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.BridgeWrapper;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.NetworkInfoResponse;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.RoomAndZoneWrapper;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.SystemWrapper;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.ZigbeeConnectivityWrapper;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.aggregateddevice.AggregatorDeviceResponse;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.aggregateddevice.ProductData;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.bridge.BridgeListResponse;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.romandzone.RoomAndZoneResponse;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.system.ServicesResponse;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.system.SystemResponse;
import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.zigbeeconnectivity.ZigbeeConnectivity;
import com.avispl.symphony.dal.util.StringUtils;

/**
 * PhilipsHue Aggregator Communicator Adapter
 *
 * Supported features are:
 * Monitoring for System and Network ìnformation
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
 * <li> - Bightness </li>
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
		private volatile boolean inProgress;

		public PhilipsHueDeviceDataLoader() {
			inProgress = true;
		}

		@Override
		public void run() {
			mainloop:
			while (inProgress) {
				try {
					TimeUnit.MILLISECONDS.sleep(500);
				} catch (InterruptedException e) {
					// Ignore for now
				}

				if (!inProgress) {
					break mainloop;
				}

				// next line will determine whether QSys monitoring was paused
				updateAggregatorStatus();
				if (devicePaused) {
					continue mainloop;
				}
				retrieveZones();
				if (zoneList.isEmpty()) {
					retrieveRooms();
				} else {
					if (!StringUtils.isNullOrEmpty(zoneName)) {
						for (RoomAndZoneResponse response : zoneList) {
							if (response.equals(zoneName)) {
								zoneListFilter = Collections.singletonList(response);
								break;
							}
						}
					}
				}

				if (logger.isDebugEnabled()) {
					logger.debug("Fetching system information and aggregator device list");
				}
				long currentTimestamp = System.currentTimeMillis();
				retrieveInfo(currentTimestamp);
				for (RoomAndZoneResponse roomAndZoneResponse : zoneList) {

				}
				if (validDeviceMetaDataRetrievalPeriodTimestamp <= currentTimestamp) {
					validDeviceMetaDataRetrievalPeriodTimestamp = currentTimestamp + deviceMetaDataRetrievalTimeout;

					retrieveRooms();

					do {
						try {
							TimeUnit.MILLISECONDS.sleep(500);
						} catch (InterruptedException e) {
							if (!inProgress) {
								break;
							}
						}
						devicesExecutionPool.removeIf(Future::isDone);
					} while (!devicesExecutionPool.isEmpty());
				}
				if (!inProgress) {
					break mainloop;
				}

				int aggregatedDevicesCount = aggregatedDeviceList.size();
				if (aggregatedDevicesCount == 0) {
					continue mainloop;
				}

				nextDevicesCollectionIterationTimestamp = System.currentTimeMillis();
				while (nextDevicesCollectionIterationTimestamp > System.currentTimeMillis()) {
					try {
						TimeUnit.MILLISECONDS.sleep(1000);
					} catch (InterruptedException e) {
						//
					}
				}

				if (!aggregatedDeviceList.isEmpty()) {
					if (logger.isDebugEnabled()) {
						logger.debug("Applying filter options");
					}

//					if (StringUtils.isNullOrEmpty(filterSystemName) || !systemResponseFilterList.isEmpty()) {
//						getFilteredAggregatedDeviceList();
//					} else {
//						aggregatedDeviceList.clear();
//					}
//					if (logger.isDebugEnabled()) {
//						logger.debug("Aggregated devices after applying filter: " + aggregatedDeviceList);
//					}
				}

				// We don't want to fetch devices statuses too often, so by default it's currentTime + 30s
				// otherwise - the variable is reset by the retrieveMultipleStatistics() call, which
				// launches devices detailed statistics collection
				nextDevicesCollectionIterationTimestamp = System.currentTimeMillis() + 30000;

				if (logger.isDebugEnabled()) {
					logger.debug("Finished collecting devices statistics cycle at " + new Date());
				}
			}
			// Finished collecting
		}

		/**
		 * Triggers main loop to stop
		 */
		public void stop() {
			inProgress = false;
		}
	}

	//The properties adapter
	private String zoneName;
	private String roomNames;
	private String deviceTypes;
	private String deviceNames;
	private List<String> roomNamesList = new LinkedList<>();
	private List<String> deviceTypesList = new LinkedList<>();
	private List<String> deviceNamesList = new LinkedList<>();

	/**
	 * List of Zones Filter
	 */
	private List<RoomAndZoneResponse> zoneListFilter = Collections.synchronizedList(new ArrayList<>());

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
	 * Indicates whether a device is considered as paused.
	 * True by default so if the system is rebooted and the actual value is lost -> the device won't start stats
	 * collection unless the {@link PhilipsHueDeviceCommunicator#retrieveMultipleStatistics()} method is called which will change it
	 * to a correct value
	 */
	private volatile boolean devicePaused = true;

	/**
	 * This parameter holds timestamp of when we need to stop performing API calls
	 * It used when device stop retrieving statistic. Updated each time of called #retrieveMultipleStatistics
	 */
	private volatile long validRetrieveStatisticsTimestamp;

	/**
	 * Aggregator inactivity timeout. If the {@link PhilipsHueDeviceCommunicator#retrieveMultipleStatistics()}  method is not
	 * called during this period of time - device is considered to be paused, thus the Cloud API
	 * is not supposed to be called
	 */
	private static final long retrieveStatisticsTimeOut = 3 * 60 * 1000;

	/**
	 * Time period within which the device metadata (basic devices information) cannot be refreshed.
	 * Ignored if device list is not yet retrieved or the cached device list is empty {@link PhilipsHueDeviceCommunicator#aggregatedDeviceList}
	 */
	private volatile long validDeviceMetaDataRetrievalPeriodTimestamp;

	/**
	 * We don't want the statistics to be collected constantly, because if there's not a big list of devices -
	 * new devices' statistics loop will be launched before the next monitoring iteration. To avoid that -
	 * this variable stores a timestamp which validates it, so when the devices' statistics is done collecting, variable
	 * is set to currentTime + 30s, at the same time, calling {@link #retrieveMultipleStatistics()} and updating the
	 * {@link #aggregatedDeviceList} resets it to the currentTime timestamp, which will re-activate data collection.
	 */
	private long nextDevicesCollectionIterationTimestamp;

	/**
	 * Device metadata retrieval timeout. The general devices list is retrieved once during this time period.
	 */
	private long deviceMetaDataRetrievalTimeout = 60 * 1000 / 2;

	/**
	 * List of error message occur while fetching system information
	 */
	private Set<String> systemErrorMessagesList = Collections.synchronizedSet(new LinkedHashSet<>());

	/**
	 * List of ID bridge
	 */
	private Set<String> bridgeIdList = Collections.synchronizedSet(new LinkedHashSet<>());

	/**
	 * List of system information
	 */
	private Set<SystemResponse> systemInfoList = Collections.synchronizedSet(new LinkedHashSet<>());

	/**
	 * List of aggregated device
	 */
	private List<AggregatedDevice> aggregatedDeviceList = Collections.synchronizedList(new ArrayList<>());

	/**
	 * List of Room
	 */
	private List<RoomAndZoneResponse> roomList = Collections.synchronizedList(new ArrayList<>());

	/**
	 * List of Zones
	 */
	private List<RoomAndZoneResponse> zoneList = Collections.synchronizedList(new ArrayList<>());

	/**
	 * List of Name and ID device
	 */
	private Map<String, String> nameAndIdDevice = Collections.synchronizedMap(new HashMap<>());

	/**
	 * Philips Hue API Token
	 */
	private String apiToken;

	/**
	 * Executor that runs all the async operations, that {@link #deviceDataLoader} is posting and
	 * {@link #devicesExecutionPool} is keeping track of
	 */
	private static ExecutorService executorService;

	/**
	 * Pool for keeping all the async operations in, to track any operations in progress and cancel them if needed
	 */
	private List<Future> devicesExecutionPool = new ArrayList<>();

	/**
	 * Runner service responsible for collecting data
	 */
	private PhilipsHueDeviceDataLoader deviceDataLoader;

	/**
	 * Update the status of the device.
	 * The device is considered as paused if did not receive any retrieveMultipleStatistics()
	 * calls during {@link PhilipsHueDeviceCommunicator#validRetrieveStatisticsTimestamp}
	 */
	private synchronized void updateAggregatorStatus() {
		devicePaused = validRetrieveStatisticsTimestamp < System.currentTimeMillis();
	}

	private synchronized void updateValidRetrieveStatisticsTimestamp() {
		validRetrieveStatisticsTimestamp = System.currentTimeMillis() + retrieveStatisticsTimeOut;
		updateAggregatorStatus();
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
		if (checkValidApiToken()) {
			executorService = Executors.newFixedThreadPool(8);
			executorService.submit(deviceDataLoader = new PhilipsHueDeviceDataLoader());
			validDeviceMetaDataRetrievalPeriodTimestamp = System.currentTimeMillis();
		}
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

	@Override
	public List<Statistics> getMultipleStatistics() throws Exception {
		ExtendedStatistics extendedStatistics = new ExtendedStatistics();
		Map<String, String> stats = new HashMap<>();
		retrieveNetworkInfo(stats);
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("New fetched network information"));
		}
		retrieveListBridgeId();
		retrieveSystemInfoByBridgeIdList(stats);
		if (logger.isDebugEnabled()) {
			logger.debug("New fetched system information");
		}
		extendedStatistics.setStatistics(stats);

		return Collections.singletonList(extendedStatistics);
	}

	@Override
	public void controlProperty(ControllableProperty controllableProperty) {
//TODO
	}

	@Override
	public void controlProperties(List<ControllableProperty> list) {
//TODO
	}

	@Override
	public List<AggregatedDevice> retrieveMultipleStatistics() {
		if (checkValidApiToken()) {
			if (executorService == null) {
				// Due to the bug that after changing properties on fly - the adapter is destroyed but adapter is not initialized properly,
				// so executor service is not running. We need to make sure executorService exists
				executorService = Executors.newFixedThreadPool(8);
				executorService.submit(deviceDataLoader = new PhilipsHueDeviceDataLoader());
			}
			nextDevicesCollectionIterationTimestamp = System.currentTimeMillis();
			updateValidRetrieveStatisticsTimestamp();
		}

		if (aggregatedDeviceList.isEmpty()) {
			return aggregatedDeviceList;
		}
		synchronized (aggregatedDeviceList) {
			aggregatedDeviceList.stream();
		}
		return aggregatedDeviceList;
	}

	@Override
	public List<AggregatedDevice> retrieveMultipleStatistics(List<String> list) throws Exception {
		return retrieveMultipleStatistics().stream().filter(aggregatedDevice -> list.contains(aggregatedDevice.getDeviceId())).collect(Collectors.toList());
	}

	@Override
	protected void authenticate() throws Exception {

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
	 * Retrieve aggregated devices and system information data -
	 * and set next device/system collection iteration timestamp
	 */
	private void retrieveInfo(long currentTimestamp) {
		if (validDeviceMetaDataRetrievalPeriodTimestamp > currentTimestamp) {
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("Aggregated devices data and system information retrieval is in cool down. %s seconds left",
						(validDeviceMetaDataRetrievalPeriodTimestamp - currentTimestamp) / 1000));
				if (!aggregatedDeviceList.isEmpty()) {
					logger.debug(String.format("Old fetched devices list: %s", aggregatedDeviceList));
				}
			}
			return;
		}
		retrieveDevices();
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("New fetched devices list: %s", aggregatedDeviceList));
		}
	}

	/**
	 * Retrieve list zones
	 */
	private void retrieveZones() {
		try {
			RoomAndZoneWrapper zoneWrapper = this.doGet("clip/v2/resource/zone", RoomAndZoneWrapper.class);
			if (zoneWrapper != null && zoneWrapper.getData() != null) {
				Collections.addAll(zoneList, zoneWrapper.getData());
			}
		} catch (Exception e) {
			String errorMessage = String.format("List Zones Data Retrieval-Error: %s with cause: %s", e.getMessage(), e.getCause().getMessage());
			systemErrorMessagesList.add(errorMessage);
			logger.error(errorMessage, e);
		}
	}

	private synchronized void retrieveRooms() {
		try {
			RoomAndZoneWrapper roomWrapper = this.doGet("clip/v2/resource/room", RoomAndZoneWrapper.class);
			if (roomWrapper != null && roomWrapper.getData() != null) {
				Collections.addAll(roomList, roomWrapper.getData());
			} else {
				systemErrorMessagesList.add("Retrieve rooms list error");
			}
		} catch (Exception e) {
			String errorMessage = String.format("List Rooms Data Retrieval-Error: %s with cause: %s", e.getMessage(), e.getCause().getMessage());
			systemErrorMessagesList.add(errorMessage);
			logger.error(errorMessage, e);
		}
	}

	private void retrieveDevices() {
		try {
			AggregatorWrapper systemResponse = this.doGet("/clip/v2/resource/device", AggregatorWrapper.class);
			nameAndIdDevice.clear();
			for (AggregatorDeviceResponse aggregatorDeviceResponse : systemResponse.getData()) {
				if (!"bridge".equalsIgnoreCase(aggregatorDeviceResponse.getServices()[0].getType())) {
					nameAndIdDevice.put(aggregatorDeviceResponse.getMetaData().getName(), aggregatorDeviceResponse.getServices()[0].getId());
				}
			}
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
	 * @param stats the stats is list of stats
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
					SystemWrapper systemWrapper = this.doGet(PhilipsUtil.getMonitorURL(PhilipsURL.DEVICE) + id, SystemWrapper.class);
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
}