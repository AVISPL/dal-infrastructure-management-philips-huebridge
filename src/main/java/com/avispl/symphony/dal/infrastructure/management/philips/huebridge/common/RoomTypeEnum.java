/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common;

/**
 * RoomTypeEnum  class defined the enum for monitoring and controlling process
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/15/2022
 * @since 1.0.0
 */
public enum RoomTypeEnum {

	LIVING_ROOM("Living Room", "living_room"),
	KITCHEN("Kitchen", "kitchen"),
	DINING("Dining", "dining"),
	BEDROOM("Bedroom", "bedroom"),
	KIDS_BEDROOM("Kids Bedroom", "kids_bedroom"),
	BATHROOM("Bathroom", "bathroom"),
	NURSERY("Nursery", "nursery"),
	RECREATION("Recreation", "recreation"),
	OFFICE("Office", "office"),
	GYM("Gym", "gym"),
	HALLWAY("Hallway", "hallway"),
	TOILET("Toilet", "toilet"),
	FRONT_DOOR("Front Door", "front_door"),
	GARAGE("Garage", "garage"),
	TERRACE("Terrace", "terrace"),
	GARDEN("Garden", "garden"),
	DRIVEWAY("Driveway", "driveway"),
	CARPORT("Carport", "carport"),
	HOME("Home", "home"),
	DOWNSTAIRS("Downstairs", "downstairs"),
	UPSTAIRS("Upstairs", "upstairs"),
	TOP_FLOOR("Top Floor", "top_floor"),
	ATTIC("Attic", "attic"),
	GUEST_ROOM("Guest_room", "guest_room"),
	STAIRCASE("Staircase", "staircase"),
	LOUNGE("Lounge", "lounge"),
	MAN_CAVE("Man Cave", "man_cave"),
	COMPUTER("Computer", "computer"),
	STUDIO("Studio", "studio"),
	MUSIC("Music", "music"),
	TV("TV", "tv"),
	CLOSET("Closet", "closet"),
	STORAGE("Storage", "storage"),
	LAUNDRY_ROOM("Laundry Room", "laundry_room"),
	BALCONY("Balcony", "balcony"),
	PORCH("Porch", "porch"),
	BARBECUE("Barbecue", "barbecue"),
	POOL("Pool", "pool"),
	OTHER("Other", "other"),
	READING("Reading", "reading"),
	;

	private final String name;
	private final String value;

	/**
	 * RoomTypeEnum instantiation
	 *
	 * @param name {@code {@link #name}}
	 * @param value {@code {@link #value }}
	 */
	RoomTypeEnum(String name, String value) {
		this.name = name;
		this.value = value;
	}

	/**
	 * Retrieves {@code {@link #name}}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
	}

	/**
	 * Retrieves {@code {@link #value}}
	 *
	 * @return value of {@link #value}
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Get name of Protocol by value
	 *
	 * @param value the value is value of ProtocolEnum
	 * @return String is protocol value
	 */
	public static String getNameOfRoomTypeEnumByValue(String value) {
		String defaultValue = value;
		for (RoomTypeEnum protocolEnum : RoomTypeEnum.values()) {
			if (protocolEnum.getName().equalsIgnoreCase(value)) {
				defaultValue = protocolEnum.getValue();
				break;
			}
		}
		return defaultValue;
	}
}