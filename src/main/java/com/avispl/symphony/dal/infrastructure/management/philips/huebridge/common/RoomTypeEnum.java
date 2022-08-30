/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.common;

/**
 * RoomTypeEnum enum provides type information for room and zone
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
	OFFICE("Office", "office"),
	GUEST_ROOM("Guest Room", "guest_room"),
	TOILET("Toilet", "toilet"),
	STAIRCASE("Staircase", "staircase"),
	HALLWAY("Hallway", "hallway"),
	LAUNDRY_ROOM("Laundry Room", "laundry_room"),
	STORAGE("Storage", "storage"),
	CLOSET("Closet", "closet"),
	GARAGE("Garage", "garage"),
	OTHER("Other", "other"),
	GYM("Gym", "gym"),
	LOUNGE("Lounge", "lounge"),
	TV("TV", "tv"),
	COMPUTER("Computer", "computer"),
	RECREATION("Recreation", "recreation"),
	GAMING_ROOM("Gaming Room", "man_cave"),
	MUSIC("Music", "music"),
	LIBRARY("Library", "reading"),
	STUDIO("Studio", "studio"),
	BACKYARD("Backyard", "garden"),
	TERRACE("Terrace", "terrace"),
	BALCONY("Balcony", "balcony"),
	DRIVEWAY("Driveway", "driveway"),
	CARPORT("Carport", "carport"),
	FRONT_DOOR("Front Door", "front_door"),
	PORCH("Porch", "porch"),
	BARBECUE("Barbecue", "barbecue"),
	POOL("Pool", "pool"),
	DOWNSTAIRS("Downstairs", "downstairs"),
	UPSTAIRS("Upstairs", "upstairs"),
	TOP_FLOOR("Top Floor", "top_floor"),
	ATTIC("Attic", "attic"),
	HOME("Home", "home"),
	NONE("None", "None"),
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
	 * Get value of room type by name
	 *
	 * @param name the name is name of ProtocolEnum
	 * @return String is protocol value
	 */
	public static String getValueOfRoomTypeEnumByName(String name) {
		for (RoomTypeEnum protocolEnum : RoomTypeEnum.values()) {
			if (protocolEnum.getName().equalsIgnoreCase(name)) {
				return protocolEnum.getValue();
			}
		}
		return name;
	}

	/**
	 * Get name of RoomTypeEnum by value
	 *
	 * @param value the value is value of RoomTypeEnum
	 * @return String is RoomTypeEnum value
	 */
	public static String getNameOfRoomTypeEnumByValue(String value) {
		for (RoomTypeEnum protocolEnum : RoomTypeEnum.values()) {
			if (protocolEnum.getValue().equalsIgnoreCase(value)) {
				return protocolEnum.getName();
			}
		}
		return value;
	}
}