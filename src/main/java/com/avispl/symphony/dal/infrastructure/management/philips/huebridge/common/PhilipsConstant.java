/**
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common;

import java.util.ArrayList;
import java.util.Arrays;

import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.grouplight.ColorPointGamut;

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
	public static String TEMPERATURE_TYPE = "temperature";
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
	public static String CANCEL_CHANGE = "CancelChanges";
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
	public static String PRODUCT_NAME = "productName";
	public static String SOFTWARE_VERSION = "softwareVersion";
	public static String BATTERY_LEVEL = "batteryLevel(%)";
	public static String MOTION_DETECTED = "motionDetected";
	public static String TEMPERATURE = "temperature";
	public static String TEMPERATURE_VALID = "temperatureValid";
	public static String MIN_POLLING_INTERVAL_KEY = "MinPollingInterval";
	public static String MAX_POLLING_INTERVAL_KEY = "NextPollingInterval";
	public static String COLOUR_SATURATION = "ColourSaturation(%)";
	public static String COLOUR_SATURATION_CURRENT_VALUE = "ColourSaturationCurrentValue(%)";
	public static String COLOUR_HUE_CURRENT_VALUE = "ColourHueCurrentValue";
	public static String COLOUR_HUE = "ColourHue";
	public static String COLOUR_CURRENT_COLOR = "ColourCurrentColor";
	public static String COLOUR_CONTROL = "ColourControl";
	public static String CURRENT_ZONE_FILTER = "CurrentZoneFilter";
	public static String MESSAGE_UNAUTHORIZED = "hue personal wireless lighting";
	public static String MESSAGE_CONNECTION_TIMEOUT = "Cannot reach resource";
	public static String ZONE_LIST = "ZoneList#Zone";
	public static String ROOM_LIST = "RoomList#Room";
	public static int NUMBER_METRIC_MONITORING = 8;
	public static int MAXIMUM_REPEAT_DAY = 7;
	public static int NUMBER_ONE = 1;
	public static int ZERO = 0;
	public static int DEFAULT_FADE_DURATION = 900;
	public static int DEFAULT_FADE_DURATION_GO_TO_SLEEP = 1800;
	public static int MIN_FADE_DURATION = 10;
	public static int MAX_FADE_DURATION = 5400;
	public static int MIN_END_BRIGHTNESS = 1;
	public static int MAX_END_BRIGHTNESS = 100;
	public static int MIN_COLOR_TEMPERATURE = 153;
	public static int MAX_COLOR_TEMPERATURE = 500;
	public static int DEFAULT_V_VALUE = 100;

	// Data loader constants
	public static int MIN_POLLING_INTERVAL = 1;
	public static int MAX_THREAD_QUANTITY = 8;
	public static final int MIN_THREAD_QUANTITY = 1;
	public static int MAX_DEVICE_QUANTITY_PER_THREAD = 8;
	public static final int FIRST_MONITORING_CYCLE_OF_POLLING_INTERVAL = 0;
	public static final int CONVERT_POSITION_TO_INDEX = 1;
	public static final String RIGHT_PARENTHESES = ")";
	public static final String LEFT_PARENTHESES = "(";
	public static final ArrayList<String> GAMUT_A_BULBS_LIST = new ArrayList<>(
			Arrays.asList("LLC001", "LLC005", "LLC006", "LLC007", "LLC010", "LLC011", "LLC012", "LLC014", "LLC013", "LST001")
	);
	public static final ArrayList<String> GAMUT_B_BULBS_LIST = new ArrayList<>(
			Arrays.asList("LCT001", "LCT002", "LCT003", "LCT004", "LLM001", "LCT005", "LCT006", "LCT007")
	);
	public static final ArrayList<String> GAMUT_C_BULBS_LIST = new ArrayList<>(
			Arrays.asList("LCT010", "LCT011", "LCT012", "LCT014", "LCT015", "LCT016", "LLC020", "LST002")
	);
	public static final ArrayList<String> MULTI_SOURCE_LUMINAIRES = new ArrayList<>(
			Arrays.asList("HBL001", "HBL002", "HBL003", "HIL001", "HIL002", "HEL001", "HEL002")
	);
	public static final ColorPointGamut[] colorPointsGamut_A = new ColorPointGamut[]
			{ new ColorPointGamut((float) 0.703, (float) 0.296), new ColorPointGamut((float) 0.214, (float) 0.709), new ColorPointGamut((float) 0.139, (float) 0.081) };
	public static final ColorPointGamut[] colorPointsGamut_B = new ColorPointGamut[]
			{ new ColorPointGamut((float) 0.674, (float) 0.322), new ColorPointGamut((float) 0.408, (float) 0.517), new ColorPointGamut((float) 0.168, (float) 0.041) };
	public static final ColorPointGamut[] colorPointsGamut_C = new ColorPointGamut[]
			{ new ColorPointGamut((float) 0.692, (float) 0.308), new ColorPointGamut((float) 0.17, (float) 0.7), new ColorPointGamut((float) 0.153, (float) 0.048) };
	public static final ColorPointGamut[] colorPointsDefault = new ColorPointGamut[]
			{ new ColorPointGamut((float) 1.0, (float) 0.0), new ColorPointGamut((float) 0.0, (float) 1.0), new ColorPointGamut((float) 0.0, (float) 0.0) };
}