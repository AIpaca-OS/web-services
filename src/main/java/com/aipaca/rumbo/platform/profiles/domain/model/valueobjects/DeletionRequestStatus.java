package com.aipaca.rumbo.platform.profiles.domain.model.valueobjects;

/**
 * Represents the possible states of a student
 * data deletion request.
 */
public enum DeletionRequestStatus {
    /**
     * The request has been created but has not
     * yet been resolved.
     */
    PENDING,

    /**
     * The request has been approved.
     */
    APPROVED,

    /**
     * The request has been rejected.
     */
    REJECTED
}