package com.aipaca.rumbo.platform.profiles.domain.model.aggregates;

import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.AuthorizationStatus;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.RelationshipType;
import lombok.Getter;
import java.time.Instant;
import java.util.Objects;

/** Child domain entity owned exclusively by Student; revoked rows are never reactivated. */
@Getter
public final class TutorStudentRelationship {
    private final Long id;
    private final Long tutorProfileId;
    private final Long authorizedByProfileId;
    private final RelationshipType relationshipType;
    private AuthorizationStatus authorizationStatus;
    private final Instant authorizedAt;
    private Instant revokedAt;

    private TutorStudentRelationship(Long id, Long tutorProfileId, Long authorizedByProfileId,
                                     RelationshipType relationshipType, AuthorizationStatus authorizationStatus,
                                     Instant authorizedAt, Instant revokedAt) {
        if (id != null) requireId(id, "Relationship");
        requireId(tutorProfileId, "Tutor");
        requireId(authorizedByProfileId, "Authorizer");
        this.id = id;
        this.tutorProfileId = tutorProfileId;
        this.authorizedByProfileId = authorizedByProfileId;
        this.relationshipType = Objects.requireNonNull(relationshipType, "Relationship type is required");
        this.authorizationStatus = Objects.requireNonNull(authorizationStatus, "Authorization status is required");
        this.authorizedAt = Objects.requireNonNull(authorizedAt, "Authorization date is required");
        this.revokedAt = revokedAt;
        if (authorizationStatus == AuthorizationStatus.AUTHORIZED && revokedAt != null ||
            authorizationStatus == AuthorizationStatus.REVOKED && revokedAt == null) {
            throw new IllegalArgumentException("Inconsistent authorization state and date");
        }
        if (revokedAt != null && revokedAt.isBefore(authorizedAt)) {
            throw new IllegalArgumentException("Revocation cannot precede authorization");
        }
    }

    static TutorStudentRelationship authorizeNew(Long tutorProfileId, Long authorizedByProfileId,
                                                  RelationshipType relationshipType, Instant at) {
        return new TutorStudentRelationship(null, tutorProfileId, authorizedByProfileId,
                relationshipType, AuthorizationStatus.AUTHORIZED, at, null);
    }

    /** Persistence reconstitution, not a public business creation API. */
    public static TutorStudentRelationship reconstitute(Long id, Long tutorProfileId, Long authorizedByProfileId,
            RelationshipType relationshipType, AuthorizationStatus status, Instant authorizedAt, Instant revokedAt) {
        requireId(id, "Relationship");
        return new TutorStudentRelationship(id, tutorProfileId, authorizedByProfileId,
                relationshipType, status, authorizedAt, revokedAt);
    }

    void revoke(Instant at) {
        Objects.requireNonNull(at, "Revocation date is required");
        if (!isAuthorized()) throw new IllegalStateException("Authorization is already revoked");
        if (at.isBefore(authorizedAt)) throw new IllegalArgumentException("Revocation cannot precede authorization");
        authorizationStatus = AuthorizationStatus.REVOKED;
        revokedAt = at;
    }

    public boolean isAuthorized() { return authorizationStatus == AuthorizationStatus.AUTHORIZED; }

    private static void requireId(Long value, String label) {
        if (value == null || value < 1) throw new IllegalArgumentException("Invalid " + label + " id");
    }
}
