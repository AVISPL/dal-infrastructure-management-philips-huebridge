/**
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.romandzone;

/**
 * Children class provides during the monitoring and controlling process
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 8/3/2022
 * @since 1.0.0
 */
public class Children {

	private String rid;

	/**
	 * Retrieves {@code {@link #rid}}
	 *
	 * @return value of {@link #rid}
	 */
	public String getRid() {
		return rid;
	}

	/**
	 * Sets {@code rid}
	 *
	 * @param rid the {@code java.lang.String} field
	 */
	public void setRid(String rid) {
		this.rid = rid;
	}
}