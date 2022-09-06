/**
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.bridge;

/**
 * BridgeListResponse class provides bridge information
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/2/2022
 * @since 1.0.0
 */
public class BridgeListResponse {

	private String id;
	private OwnerResponse owner;

	/**
	 * Retrieves {@code {@link #id}}
	 *
	 * @return value of {@link #id}
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets {@code id}
	 *
	 * @param id the {@code java.lang.String} field
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Retrieves {@code {@link #owner}}
	 *
	 * @return value of {@link #owner}
	 */
	public OwnerResponse getOwner() {
		return owner;
	}

	/**
	 * Sets {@code owner}
	 *
	 * @param owner the {@code com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.bridge.OwnerResponse} field
	 */
	public void setOwner(OwnerResponse owner) {
		this.owner = owner;
	}
}