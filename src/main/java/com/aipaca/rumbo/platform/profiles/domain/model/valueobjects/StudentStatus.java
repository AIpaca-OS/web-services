package com.aipaca.rumbo.platform.profiles.domain.model.valueobjects;

/**
 * Represents the possible statuses of a student
 * within the Profiles & Relationship Management bounded context.
 */
public enum StudentStatus {
    /**
     * Indicates that the student is currently active
     * and can participate in the service.
     */
    ACTIVE,

    /**
     * Indicates that the student is currently inactive.
     */
    INACTIVE
}