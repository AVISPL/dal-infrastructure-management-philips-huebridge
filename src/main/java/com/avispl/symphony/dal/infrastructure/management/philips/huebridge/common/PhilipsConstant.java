/**
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common;

/**
 * PhilipsConstant class provides the constant during the monitoring and controlling process
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/2/2022
 * @since 1.0.0
 */
public class PhilipsConstant {

	public static String ZIGBEE_CONNECTIVITY = "zigbee_connectivity";
	public static String BRIDGE = "bridge";
	public static String LIGHT = "light";
	public static String BUTTON = "Button";
	public static String MOTION_SENSOR = "motion";
	public static String DEVICE_POWER = "device_power";
	public static String STATUS = "status";
	public static String BRIGHTNESS = "brightness";
	public static String CONNECTED = "connected";
	public static String COLOR_TEMPERATURE = "colorTemperature(K)";
	public static String EMPTY_STRING = "";
	public static String SPACE = " ";
	public static String HASH = "#";
	public static String DASH = "-";
	public static String SLASH = "/";
	public static String DEVICE_CONNECTED = "DeviceConnected";
	public static String BRIDGE_HOME = "bridge_home";
	public static String NONE = "None";
	public static String COMMA = ",";
	public static String CREATE_ROOM = "CreateRoom";
	public static String CREATE_ZONE = "CreateZone";
	public static String CREATE_AUTOMATION = "CreateAutomationBehaviorInstance";
	public static String DELETE = "Delete";
	public static String DELETING = "Deleting";
	public static String CREATE = "Create";
	public static String CREATING = "Creating";
	public static String ADD = "Add";
	public static String ADDING = "Adding";
	public static String EDITED = "Edited";
	public static String FALSE = "False";
	public static String TRUE = "True";
	public static String NAME = "Name";
	public static String DEVICE_0 = "Device0";
	public static String ROOM_0 = "Room0";
	public static String ZONE_0 = "Zone0";
	public static String REPEAT_0 = "Repeat0";
	public static String REPEAT = "Repeat";
	public static String DEVICE = "Device";
	public static String TYPE = "Type";
	public static String DEVICE_ADD = "DeviceAdd";
	public static String ZONE = "Zone";
	public static String ROOM = "Room";
	public static String AUTOMATION = "Automation";
	public static String OFFLINE = "Offline";
	public static String ONLINE = "Online";
	public static String CANCEL = "Cancel";
	public static String CANCELING = "Canceling";
	public static String CANCEL_CHANGE = "CancelChange";
	public static String FORMAT_PERCENT = "\"%s\"";
	public static String FORMAT_PERCENT_OBJECT = "%s";
	public static String COLON = ":";
	public static String APPLYING = "Applying";
	public static String APPLY = "Apply";
	public static String PARAM_CHANGE_STATUS = "{\"on\": {\"on\": %s}}";
	public static String PARAM_CHANGE_STATUS_AUTOMATION = "{\"enabled\": %s}";
	public static String ERROR_MESSAGE_STATUS = "device (grouped_light) is \"soft off\", command (.on) may not have effect";
	public static String DISABLE = "Disable";
	public static String ENABLE = "Enable";
	public static String TIME_AM = "AM";
	public static String TIME_PM = "PM";
	public static String ALL_DEVICE_IN_ROOM = "AllDeviceInRoom";
	public static String ALL_DEVICE_IN_ZONE = "AllDeviceInZone";
	public static String ROOM_NO_ASSIGNED_DEVICE = "RoomNoAssignedDevice";
	public static String ZONE_NO_ASSIGNED_DEVICE = "ZoneNoAssignedDevice";
	public static String GO_TO_SLEEPS = "Basic go to sleep routine";
	public static String WAKE_UP_WITH_LIGHT = "Basic wake up routine";
	public static String MAC_ADDRESS = "macAddress";
	public static String TIMERS = "Timers";
	public static String TIME = "Time";
	public static String DEVICE_TYPE = "deviceType";
	public static String SOFTWARE_VERSION = "softwareVersion";
	public static String BATTERY_LEVEL = "batteryLevel(%)";
	public static String MOTION_DETECTED = "motionDetected";
	public static String MIN_POLLING_INTERVAL_KEY = "MinPollingInterval";
	public static String MAX_POLLING_INTERVAL_KEY = "MaxPollingInterval";
	public static int MAXIMUM_REPEAT_DAY = 7;
	public static int NUMBER_ONE = 1;
	public static int ZERO = 0;
	public static int DEFAULT_FADE_DURATION = 900;
	public static int MIN_FADE_DURATION = 10;
	public static int MAX_FADE_DURATION = 5400;
	public static int MIN_END_BRIGHTNESS = 1;
	public static int MAX_END_BRIGHTNESS = 100;
	public static int MIN_COLOR_TEMPERATURE = 153;
	public static int MAX_COLOR_TEMPERATURE = 500;

	// Data loader constants
	public static int MIN_POLLING_INTERVAL = 1;
	public static int MAX_THREAD_QUANTITY = 8;
	public static final int MIN_THREAD_QUANTITY = 1;
	public static int MAX_DEVICE_QUANTITY_PER_THREAD = 8;
	public static final int FIRST_MONITORING_CYCLE_OF_POLLING_INTERVAL = 0;
	public static final int CONVERT_POSITION_TO_INDEX = 1;

}