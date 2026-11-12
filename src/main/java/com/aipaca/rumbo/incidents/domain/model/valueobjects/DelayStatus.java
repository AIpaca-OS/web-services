package com.aipaca.rumbo.incidents.domain.model.valueobjects;

/**
 * Represents the lifecycle states of a delay.
 */
public enum DelayStatus {
    /**
     * The delay is the current estimation.
     */
    ACTIVE,

    /**
     * A later update replaced this delay.
     */
    SUPERSEDED
}
