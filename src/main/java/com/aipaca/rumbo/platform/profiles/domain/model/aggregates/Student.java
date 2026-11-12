package com.aipaca.rumbo.platform.profiles.domain.model.aggregates;

import com.aipaca.rumbo.platform.profiles.domain.model.commands.RegisterStudentCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.entities.TutorStudentRelationship;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.PersonName;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.RelationshipType;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.StudentStatus;
import com.aipaca.rumbo.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Student aggregate root.
 *
 * <p>
 * Represents a student transported by the service. Tutor authorizations are
 * {@link TutorStudentRelationship} entities that are modified only through this aggregate:
 * a student always keeps at least one authorized tutor until it is anonymized.
 * No JPA or persistence annotation is present here — those concerns live
 * exclusively in {@code StudentPersistenceEntity}.
 * </p>
 */
@Getter
public class Student extends AbstractDomainAggregateRoot<Student> {
    /**
     * The unique identifier for the student.
     */
    private Long id;

    /**
     * The name of the student.
     */
    private PersonName name;

    /**
     * The birth date of the student.
     */
    private LocalDate birthDate;

    /**
     */
    /**
     * The student status.
     */
    private StudentStatus status;

    /**
     * The anonymization date, if the student was anonymized.
     */
    private Instant anonymizedAt;

    /**
     * Persistence revision for optimistic concurrency; null for a newly registered student.
     */
    private Long version;

    /**
     * Incremented on any tutor authorization change so that concurrent updates are detected.
     */
    private long authorizationRevision;

    /**
     * The tutor authorizations of the student, including revoked history.
     */
    private final List<TutorStudentRelationship> tutorRelationships;

    private Student(Long id, PersonName name, LocalDate birthDate,
                    StudentStatus status, Instant anonymizedAt, Long version,
                    long authorizationRevision, Collection<TutorStudentRelationship> relationships) {
        if (id != null && id < 1) {
            throw new IllegalArgumentException("Invalid student id");
        }

        if (version != null && version < 0) {
            throw new IllegalArgumentException("Invalid version");
        }
        if (authorizationRevision < 0) {
            throw new IllegalArgumentException("Invalid authorization revision");
        }
        this.id = id;
        this.name = Objects.requireNonNull(name, "Student name is required");
        this.birthDate = Objects.requireNonNull(birthDate, "Birth date is required");
        this.status = Objects.requireNonNull(status, "Student status is required");
        this.anonymizedAt = anonymizedAt;
        this.version = version;
        this.authorizationRevision = authorizationRevision;
        this.tutorRelationships = new ArrayList<>(Objects.requireNonNull(relationships));
        if (this.tutorRelationships.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Relationships cannot contain nulls");
        }
        if (anonymizedAt != null && status != StudentStatus.INACTIVE) {
            throw new IllegalArgumentException("Anonymized student must be inactive");
        }
        long activeCount = this.tutorRelationships.stream().filter(TutorStudentRelationship::isAuthorized).count();
        if (anonymizedAt == null && activeCount < 1) {
            throw new IllegalArgumentException("Student requires at least one authorized tutor");
        }
        if (anonymizedAt != null && activeCount != 0) {
            throw new IllegalArgumentException("Anonymized student cannot have authorized tutors");
        }
        var activeTutorIds = this.tutorRelationships.stream()
                .filter(TutorStudentRelationship::isAuthorized)
                .map(TutorStudentRelationship::getTutorProfileId)
                .toList();
        if (activeTutorIds.stream().distinct().count() != activeTutorIds.size()) {
            throw new IllegalArgumentException("Duplicate active tutor authorizations");
        }
    }

    /**
     * Constructor with a RegisterStudentCommand.
     * The registering tutor becomes the first authorized tutor.
     *
     * @param command The {@link RegisterStudentCommand} instance
     */
    public Student(RegisterStudentCommand command) {
        this(command, Instant.now());
    }

    /**
     * Constructor with a RegisterStudentCommand and the authorization date.
     *
     * @param command The {@link RegisterStudentCommand} instance
     * @param at      The authorization date of the registering tutor.
     */
    public Student(RegisterStudentCommand command, Instant at) {
        this(null, new PersonName(command.firstName(), command.lastName()), command.birthDate(),
                StudentStatus.ACTIVE, null, null, 0L,
                List.of(TutorStudentRelationship.authorizeNew(
                        command.tutorProfileId(), command.tutorProfileId(), command.relationshipType(), at)));
    }

    /**
     * Reconstitutes an existing student from persistence state, keeping the relationship
     * history and the optimistic-lock revision.
     *
     * @param id                    The unique identifier.
     * @param name                  The name of the student.
     * @param birthDate             The birth date.
     * @param status                The student status.
     * @param anonymizedAt          The anonymization date, or null.
     * @param version               The persistence revision.
     * @param authorizationRevision The tutor authorization revision.
     * @param relationships         The tutor authorizations.
     * @return The reconstituted Student instance.
     */
    public static Student reconstitute(Long id, PersonName name, LocalDate birthDate,
                                       StudentStatus status, Instant anonymizedAt, Long version,
                                       long authorizationRevision, Collection<TutorStudentRelationship> relationships) {
        requireId(id, "Student");
        Objects.requireNonNull(version, "Persisted student must have a version");
        return new Student(id, name, birthDate, status, anonymizedAt, version,
                authorizationRevision, relationships);
    }

    /**
     * Gets a defensive, unmodifiable snapshot of the tutor authorizations.
     *
     * @return The tutor authorizations.
     */
    public List<TutorStudentRelationship> getTutorRelationships() {
        return List.copyOf(tutorRelationships);
    }

    /**
     * Checks whether a tutor is currently authorized for the student.
     *
     * @param tutorProfileId The tutor profile id.
     * @return {@code true} when the tutor holds an active authorization.
     */
    public boolean hasAuthorizedTutor(Long tutorProfileId) {
        return tutorRelationships.stream()
                .anyMatch(relationship -> relationship.getTutorProfileId().equals(tutorProfileId)
                        && relationship.isAuthorized());
    }

    /**
     * Authorizes a new tutor. Only an already authorized tutor can grant the authorization.
     *
     * @param tutorProfileId The tutor profile id to authorize.
     * @param type           The relationship between the tutor and the student.
     * @param authorizedBy   The profile id of the authorized tutor that grants it.
     * @param at             The authorization date.
     */
    public void authorizeTutor(Long tutorProfileId, RelationshipType type, Long authorizedBy, Instant at) {
        requireActive();
        requireId(tutorProfileId, "Tutor");
        requireId(authorizedBy, "Authorizer");
        Objects.requireNonNull(type, "Relationship type is required");
        Objects.requireNonNull(at, "Authorization date is required");
        if (!hasAuthorizedTutor(authorizedBy)) {
            throw new IllegalStateException("Authorizer is not authorized");
        }
        if (hasAuthorizedTutor(tutorProfileId)) {
            throw new IllegalStateException("Tutor is already authorized");
        }
        tutorRelationships.add(TutorStudentRelationship.authorizeNew(tutorProfileId, authorizedBy, type, at));
        authorizationRevision++;
    }

    /**
     * Revokes the authorization of a tutor. The revoker must be authorized and
     * the last authorized tutor cannot be revoked.
     *
     * @param tutorProfileId The tutor profile id to revoke.
     * @param by             The profile id of the authorized tutor that revokes it.
     * @param at             The revocation date.
     */
    public void revokeTutor(Long tutorProfileId, Long by, Instant at) {
        requireActive();
        requireId(tutorProfileId, "Tutor");
        requireId(by, "Revoker");
        Objects.requireNonNull(at, "Revocation date is required");
        if (!hasAuthorizedTutor(by)) {
            throw new IllegalStateException("Revoker is not authorized");
        }
        var target = tutorRelationships.stream()
                .filter(relationship -> relationship.getTutorProfileId().equals(tutorProfileId)
                        && relationship.isAuthorized())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Tutor has no active authorization"));
        if (tutorRelationships.stream().filter(TutorStudentRelationship::isAuthorized).count() <= 1) {
            throw new IllegalStateException("Cannot revoke last authorized tutor");
        }
        target.revoke(at);
        authorizationRevision++;
    }

    /**
     * Updates the personal information of an active student.
     *
     * @param firstName The first name.
     * @param lastName  The last name.
     * @param birthDate The birth date.
     */
    public void updateInformation(String firstName, String lastName, LocalDate birthDate) {
        requireActive();

        var newName = new PersonName(firstName, lastName);
        Objects.requireNonNull(birthDate, "Birth date is required");
        this.name = newName;
        this.birthDate = birthDate;
    }

    /**
     * Activates the student.
     */
    public void activate() {
        requireNotAnonymized();
        this.status = StudentStatus.ACTIVE;
    }

    /**
     * Deactivates the student.
     */
    public void deactivate() {
        requireNotAnonymized();
        this.status = StudentStatus.INACTIVE;
    }

    /**
     * Marks the student as anonymized and revokes every tutor authorization.
     * This does NOT physically remove the name and birth date; actual erasure of
     * personal data requires a separately approved data policy.
     *
     * @param at The anonymization date.
     */
    public void anonymize(Instant at) {
        requireNotAnonymized();
        Objects.requireNonNull(at, "Anonymization date is required");
        // Validate the whole collection before mutating anything.
        if (tutorRelationships.stream()
                .filter(TutorStudentRelationship::isAuthorized)
                .anyMatch(relationship -> at.isBefore(relationship.getAuthorizedAt()))) {
            throw new IllegalArgumentException("Anonymization cannot precede authorization");
        }
        for (var relationship : tutorRelationships) {
            if (relationship.isAuthorized()) {
                relationship.revoke(at);
            }
        }
        authorizationRevision++;
        status = StudentStatus.INACTIVE;
        anonymizedAt = at;
    }

    /**
     * Full name getter.
     *
     * @return Full name
     */
    public String getFullName() {
        return name.getFullName();
    }

    private void requireActive() {
        requireNotAnonymized();
        if (status != StudentStatus.ACTIVE) {
            throw new IllegalStateException("Student is inactive");
        }
    }

    private void requireNotAnonymized() {
        if (anonymizedAt != null) {
            throw new IllegalStateException("Student is anonymized");
        }
    }

    private static void requireId(Long value, String label) {
        if (value == null || value < 1) {
            throw new IllegalArgumentException("Invalid " + label + " id");
        }
    }
}
