package com.aipaca.rumbo.platform.profiles.domain.model.valueobjects;

/**
 * Represents the possible authorization states
 * of a tutor-student relationship.
 */
public enum AuthorizationStatus {
    /**
     * The tutor is currently authorized to access
     * the student's information.
     */
    AUTHORIZED,

    /**
     * The tutor's authorization has been revoked.
     */
    REVOKED,
}
