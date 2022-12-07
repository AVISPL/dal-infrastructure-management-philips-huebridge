/**
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.light;

import com.avispl.symphony.dal.infrastructure.management.philips.huebridge.dto.bridge.OwnerResponse;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * LightResponse class provides info about the lights
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 9/6/2022
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LightResponse {

    @JsonAlias("id")
    private String serviceId;

    private OwnerResponse owner;

    /**
     * Retrieves {@link #serviceId}
     *
     * @return value of {@link #serviceId}
     */
    public String getServiceId() {
        return serviceId;
    }

    /**
     * Sets {@link #serviceId} value
     *
     * @param serviceId new value of {@link #serviceId}
     */
    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    /**
     * Retrieves {@link #owner}
     *
     * @return value of {@link #owner}
     */
    public OwnerResponse getOwner() {
        return owner;
    }

    /**
     * Sets {@link #owner} value
     *
     * @param owner new value of {@link #owner}
     */
    public void setOwner(OwnerResponse owner) {
        this.owner = owner;
    }
}
