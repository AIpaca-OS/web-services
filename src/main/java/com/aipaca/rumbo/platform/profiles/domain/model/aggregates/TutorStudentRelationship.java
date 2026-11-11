
package com.aipaca.rumbo.platform.profiles.domain.model.aggregates;

import com.aipaca.rumbo.platform.profiles.domain.model.commands.AuthorizeTutorStudentRelationshipCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.AuthorizationStatus;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.RelationshipType;
import com.aipaca.rumbo.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;

import lombok.Getter;

import java.time.Instant;
import java.util.Objects;

@Getter
public class TutorStudentRelationship
        extends AbstractDomainAggregateRoot<TutorStudentRelationship> {

    private Long id;
    private Long tutorProfileId;
    private Long studentId;
    private Long authorizedByProfileId;

    private RelationshipType relationshipType;
    private AuthorizationStatus authorizationStatus;

    private Instant authorizedAt;
    private Instant revokedAt;

    private TutorStudentRelationship(
            Long id,
            Long tutorProfileId,
            Long studentId,
            Long authorizedByProfileId,
            RelationshipType relationshipType,
            AuthorizationStatus authorizationStatus,
            Instant authorizedAt,
            Instant revokedAt
    ) {
        if (id != null) validateId(id, "Relationship");

        validateId(tutorProfileId, "Tutor");
        validateId(studentId, "Student");

        if (authorizedByProfileId != null) {
            validateId(authorizedByProfileId, "Authorizer");
        }

        this.id = id;
        this.tutorProfileId = tutorProfileId;
        this.studentId = studentId;
        this.authorizedByProfileId = authorizedByProfileId;

        this.relationshipType = Objects.requireNonNull(
                relationshipType, "Relationship type is required");

        this.authorizationStatus = Objects.requireNonNull(
                authorizationStatus, "Authorization status is required");

        this.authorizedAt = Objects.requireNonNull(
                authorizedAt, "Authorization date is required");

        this.revokedAt = revokedAt;

        if (authorizationStatus == AuthorizationStatus.AUTHORIZED
                && revokedAt != null) {
            throw new IllegalArgumentException(
                    "An authorized relationship cannot have a revocation date"
            );
        }

        if (authorizationStatus == AuthorizationStatus.REVOKED
                && revokedAt == null) {
            throw new IllegalArgumentException(
                    "A revoked relationship must have a revocation date"
            );
        }

        if (revokedAt != null && revokedAt.isBefore(authorizedAt)) {
            throw new IllegalArgumentException(
                    "Revocation cannot precede authorization"
            );
        }
    }

    public TutorStudentRelationship(
            AuthorizeTutorStudentRelationshipCommand command
    ) {
        this(command, Instant.now());
    }

    public TutorStudentRelationship(
            AuthorizeTutorStudentRelationshipCommand command,
            Instant authorizedAt
    ) {
        this(
                null,
                command.tutorProfileId(),
                command.studentId(),
                command.authorizedByProfileId(),
                command.relationshipType(),
                AuthorizationStatus.AUTHORIZED,
                authorizedAt,
                null
        );
    }

    public static TutorStudentRelationship reconstitute(
            Long id,
            Long tutorProfileId,
            Long studentId,
            Long authorizedByProfileId,
            RelationshipType relationshipType,
            AuthorizationStatus authorizationStatus,
            Instant authorizedAt,
            Instant revokedAt
    ) {
        validateId(id, "Relationship");

        return new TutorStudentRelationship(
                id,
                tutorProfileId,
                studentId,
                authorizedByProfileId,
                relationshipType,
                authorizationStatus,
                authorizedAt,
                revokedAt
        );
    }

    public void authorize(Instant at) {
        Objects.requireNonNull(at, "Authorization date is required");

        if (this.authorizationStatus == AuthorizationStatus.AUTHORIZED) {
            throw new IllegalStateException(
                    "Relationship is already authorized"
            );
        }

        if (this.revokedAt != null && at.isBefore(this.revokedAt)) {
            throw new IllegalArgumentException(
                    "Authorization cannot precede revocation"
            );
        }

        this.authorizationStatus = AuthorizationStatus.AUTHORIZED;
        this.authorizedAt = at;
        this.revokedAt = null;
    }

    public void revoke(Instant at) {
        Objects.requireNonNull(at, "Revocation date is required");

        if (this.authorizationStatus == AuthorizationStatus.REVOKED) {
            throw new IllegalStateException(
                    "Relationship is already revoked"
            );
        }

        if (at.isBefore(this.authorizedAt)) {
            throw new IllegalArgumentException(
                    "Revocation cannot precede authorization"
            );
        }

        this.authorizationStatus = AuthorizationStatus.REVOKED;
        this.revokedAt = at;
    }

    public boolean isAuthorized() {
        return this.authorizationStatus == AuthorizationStatus.AUTHORIZED;
    }

    private static void validateId(Long id, String entityName) {
        if (id == null || id < 1) {
            throw new IllegalArgumentException(
                    entityName + " id cannot be null or less than 1"
            );
        }
    }
}
