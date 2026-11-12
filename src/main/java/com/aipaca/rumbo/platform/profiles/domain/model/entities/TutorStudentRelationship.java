package com.aipaca.rumbo.platform.profiles.domain.model.entities;

import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.AuthorizationStatus;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.RelationshipType;
import lombok.Getter;

import java.time.Instant;
import java.util.Objects;

/**
 * Tutor student relationship entity.
 *
 * <p>
 * Authorization of a tutor over a student. It is owned exclusively by the {@code Student}
 * aggregate and modified only through it. A revoked relationship is never reactivated;
 * a new authorization creates a new relationship.
 * </p>
 */
@Getter
public class TutorStudentRelationship {
    /**
     * The unique identifier for the relationship.
     */
    private Long id;

    /**
     * The authorized tutor profile id.
     */
    private Long tutorProfileId;

    /**
     * The profile id of the tutor that granted the authorization.
     */
    private Long authorizedByProfileId;

    /**
     * The relationship between the tutor and the student.
     */
    private RelationshipType relationshipType;

    /**
     * The authorization status.
     */
    private AuthorizationStatus authorizationStatus;

    /**
     * The authorization date.
     */
    private Instant authorizedAt;

    /**
     * The revocation date, if revoked.
     */
    private Instant revokedAt;

    /**
     * Creates a relationship with its complete state.
     */
    private TutorStudentRelationship(Long id, Long tutorProfileId, Long authorizedByProfileId,
                                     RelationshipType relationshipType, AuthorizationStatus authorizationStatus,
                                     Instant authorizedAt, Instant revokedAt) {
        if (id != null) {
            requireId(id, "Relationship");
        }
        requireId(tutorProfileId, "Tutor");
        requireId(authorizedByProfileId, "Authorizer");
        this.id = id;
        this.tutorProfileId = tutorProfileId;
        this.authorizedByProfileId = authorizedByProfileId;
        this.relationshipType = Objects.requireNonNull(relationshipType, "Relationship type is required");
        this.authorizationStatus = Objects.requireNonNull(authorizationStatus, "Authorization status is required");
        this.authorizedAt = Objects.requireNonNull(authorizedAt, "Authorization date is required");
        this.revokedAt = revokedAt;
        if (authorizationStatus == AuthorizationStatus.AUTHORIZED && revokedAt != null
                || authorizationStatus == AuthorizationStatus.REVOKED && revokedAt == null) {
            throw new IllegalArgumentException("Inconsistent authorization state and date");
        }
        if (revokedAt != null && revokedAt.isBefore(authorizedAt)) {
            throw new IllegalArgumentException("Revocation cannot precede authorization");
        }
    }

    /**
     * Creates a new authorization.
     *
     * @param tutorProfileId        The authorized tutor profile id.
     * @param authorizedByProfileId The profile id of the tutor that grants the authorization.
     * @param relationshipType      The relationship between the tutor and the student.
     * @param at                    The authorization date.
     * @return The new authorized relationship.
     */
    public static TutorStudentRelationship authorizeNew(Long tutorProfileId, Long authorizedByProfileId,
                                                        RelationshipType relationshipType, Instant at) {
        return new TutorStudentRelationship(null, tutorProfileId, authorizedByProfileId,
                relationshipType, AuthorizationStatus.AUTHORIZED, at, null);
    }

    /**
     * Reconstitutes an existing relationship from persistence state.
     *
     * @param id                    The unique identifier.
     * @param tutorProfileId        The authorized tutor profile id.
     * @param authorizedByProfileId The profile id of the tutor that granted the authorization.
     * @param relationshipType      The relationship between the tutor and the student.
     * @param status                The authorization status.
     * @param authorizedAt          The authorization date.
     * @param revokedAt             The revocation date, if revoked.
     * @return The reconstituted TutorStudentRelationship instance.
     */
    public static TutorStudentRelationship reconstitute(Long id, Long tutorProfileId, Long authorizedByProfileId,
                                                        RelationshipType relationshipType, AuthorizationStatus status,
                                                        Instant authorizedAt, Instant revokedAt) {
        requireId(id, "Relationship");
        return new TutorStudentRelationship(id, tutorProfileId, authorizedByProfileId,
                relationshipType, status, authorizedAt, revokedAt);
    }

    /**
     * Revokes the authorization.
     *
     * @param at The revocation date.
     */
    public void revoke(Instant at) {
        Objects.requireNonNull(at, "Revocation date is required");
        if (!isAuthorized()) {
            throw new IllegalStateException("Authorization is already revoked");
        }
        if (at.isBefore(authorizedAt)) {
            throw new IllegalArgumentException("Revocation cannot precede authorization");
        }
        this.authorizationStatus = AuthorizationStatus.REVOKED;
        this.revokedAt = at;
    }

    /**
     * Checks whether the tutor is currently authorized.
     *
     * @return {@code true} when the authorization is active.
     */
    public boolean isAuthorized() {
        return authorizationStatus == AuthorizationStatus.AUTHORIZED;
    }

    private static void requireId(Long value, String label) {
        if (value == null || value < 1) {
            throw new IllegalArgumentException("Invalid " + label + " id");
        }
    }
}
