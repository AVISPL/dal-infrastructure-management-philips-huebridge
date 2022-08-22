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
	public static String EMPTY_STRING = "";
	public static String SPACE = " ";
	public static String HASH = "#";
	public static String DASH = "-";
	public static String SLASH = "/";
	public static String DEVICE_CONNECTED = "DeviceConnected";
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
	public static String DEVICE = "Device";
	public static String TYPE = "Type";
	public static String DEVICE_ADD = "DeviceAdd";
	public static String ZONE = "Zone";
	public static String ROOM = "Room";
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
	public static String ERROR_MESSAGE_STATUS = "device (grouped_light) is \"soft off\", command (.on) may not have effect";
	public static String DISABLE = "Disable";
	public static String ENABLE = "Enable";
	public static String TIME_AM = "AM";
	public static String TIME_PM = "PM";

	public static int NUMBER_ONE = 1;
	public static int ZERO = 0;
	public static int DEFAULT_FADE_DURATION = 900;
}