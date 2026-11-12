package com.aipaca.rumbo.platform.profiles.domain.model.valueobjects;

/**
 * Represents the possible states of a student enrollment
 * in a driver's service.
 */
public enum ServiceEnrollmentStatus {
    /**
     * The enrollment has been requested by a tutor and
     * waits for the driver.
     */
    PENDING,

    /**
     * The driver accepted the enrollment; the service is active.
     */
    ACCEPTED,

    /**
     * The requester cancelled the pending enrollment.
     */
    CANCELLED,

    /**
     * The accepted enrollment has ended.
     */
    ENDED
}