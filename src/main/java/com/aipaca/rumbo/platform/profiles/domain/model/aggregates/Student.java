package com.aipaca.rumbo.platform.profiles.domain.model.aggregates;

import com.aipaca.rumbo.platform.profiles.domain.model.commands.RegisterStudentCommand;
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

/** Aggregate root: tutor authorizations are modified only through this student. */
@Getter
public class Student extends AbstractDomainAggregateRoot<Student> {
    private final Long id;
    private PersonName name;
    private LocalDate birthDate;
    private Long schoolId;
    private StudentStatus status;
    private Instant anonymizedAt;
    /** Persistence revision for optimistic concurrency; null for a newly registered student. */
    private final Long version;
    /** Incremented on any tutor authorization change so @Version detects conflicting updates. */
    private long authorizationRevision;
    private final List<TutorStudentRelationship> tutorRelationships;

    private Student(Long id, PersonName name, LocalDate birthDate, Long schoolId,
                    StudentStatus status, Instant anonymizedAt, Long version,
                    long authorizationRevision, Collection<TutorStudentRelationship> relationships) {
        if (id != null && id < 1) throw new IllegalArgumentException("Invalid student id");
        if (schoolId != null && schoolId < 1) throw new IllegalArgumentException("Invalid school id");
        if (version != null && version < 0) throw new IllegalArgumentException("Invalid version");
        if (authorizationRevision < 0) throw new IllegalArgumentException("Invalid authorization revision");
        this.id = id;
        this.name = Objects.requireNonNull(name, "Student name is required");
        this.birthDate = Objects.requireNonNull(birthDate, "Birth date is required");
        this.schoolId = schoolId;
        this.status = Objects.requireNonNull(status, "Student status is required");
        this.anonymizedAt = anonymizedAt;
        this.version = version;
        this.authorizationRevision = authorizationRevision;
        this.tutorRelationships = new ArrayList<>(Objects.requireNonNull(relationships));
        if (this.tutorRelationships.stream().anyMatch(Objects::isNull))
            throw new IllegalArgumentException("Relationships cannot contain nulls");
        if (anonymizedAt != null && status != StudentStatus.INACTIVE)
            throw new IllegalArgumentException("Anonymized student must be inactive");
        long activeCount = this.tutorRelationships.stream().filter(TutorStudentRelationship::isAuthorized).count();
        if (anonymizedAt == null && activeCount < 1)
            throw new IllegalArgumentException("Student requires at least one authorized tutor");
        if (anonymizedAt != null && activeCount != 0)
            throw new IllegalArgumentException("Anonymized student cannot have authorized tutors");
        var activeTutorIds = this.tutorRelationships.stream().filter(TutorStudentRelationship::isAuthorized)
                .map(TutorStudentRelationship::getTutorProfileId).toList();
        if (activeTutorIds.stream().distinct().count() != activeTutorIds.size())
            throw new IllegalArgumentException("Duplicate active tutor authorizations");
    }

    public Student(RegisterStudentCommand command) { this(command, Instant.now()); }

    public Student(RegisterStudentCommand command, Instant at) {
        this(null, new PersonName(command.firstName(), command.lastName()), command.birthDate(),
                command.schoolId(), StudentStatus.ACTIVE, null, null, 0L,
                List.of(TutorStudentRelationship.authorizeNew(
                        command.tutorProfileId(), command.tutorProfileId(), command.relationshipType(), at)));
    }

    /** Reconstitution keeps relationship history and optimistic-lock revision. */
    public static Student reconstitute(Long id, PersonName name, LocalDate birthDate, Long schoolId,
                                       StudentStatus status, Instant anonymizedAt, Long version,
                                       long authorizationRevision, Collection<TutorStudentRelationship> relationships) {
        requireId(id, "Student");
        Objects.requireNonNull(version, "Persisted student must have a version");
        return new Student(id, name, birthDate, schoolId, status, anonymizedAt, version,
                authorizationRevision, relationships);
    }

    /** Returns a defensive, unmodifiable snapshot. */
    public List<TutorStudentRelationship> getTutorRelationships() { return List.copyOf(tutorRelationships); }

    public boolean hasAuthorizedTutor(Long tutorProfileId) {
        return tutorRelationships.stream().anyMatch(r -> r.getTutorProfileId().equals(tutorProfileId) && r.isAuthorized());
    }

    public void authorizeTutor(Long tutorProfileId, RelationshipType type, Long authorizedBy, Instant at) {
        requireActive();
        requireId(tutorProfileId, "Tutor");
        requireId(authorizedBy, "Authorizer");
        Objects.requireNonNull(type, "Relationship type is required");
        Objects.requireNonNull(at, "Authorization date is required");
        if (!hasAuthorizedTutor(authorizedBy)) throw new IllegalStateException("Authorizer is not authorized");
        if (hasAuthorizedTutor(tutorProfileId)) throw new IllegalStateException("Tutor is already authorized");
        tutorRelationships.add(TutorStudentRelationship.authorizeNew(tutorProfileId, authorizedBy, type, at));
        authorizationRevision++;
    }

    public void revokeTutor(Long tutorProfileId, Long by, Instant at) {
        requireActive();
        requireId(tutorProfileId, "Tutor");
        requireId(by, "Revoker");
        Objects.requireNonNull(at, "Revocation date is required");
        if (!hasAuthorizedTutor(by)) throw new IllegalStateException("Revoker is not authorized");
        var target = tutorRelationships.stream()
                .filter(r -> r.getTutorProfileId().equals(tutorProfileId) && r.isAuthorized())
                .findFirst().orElseThrow(() -> new IllegalStateException("Tutor has no active authorization"));
        if (tutorRelationships.stream().filter(TutorStudentRelationship::isAuthorized).count() <= 1)
            throw new IllegalStateException("Cannot revoke last authorized tutor");
        target.revoke(at);
        authorizationRevision++;
    }

    public void updateInformation(String firstName, String lastName, LocalDate birthDate, Long schoolId) {
        requireActive();
        if (schoolId != null && schoolId < 1) throw new IllegalArgumentException("Invalid school id");
        var newName = new PersonName(firstName, lastName);
        Objects.requireNonNull(birthDate, "Birth date is required");
        this.name = newName;
        this.birthDate = birthDate;
        this.schoolId = schoolId;
    }

    public void activate() { requireNotAnonymized(); this.status = StudentStatus.ACTIVE; }
    public void deactivate() { requireNotAnonymized(); this.status = StudentStatus.INACTIVE; }

    /**
     * Marks deletion lifecycle and revokes access. IMPORTANT: this does NOT physically remove
     * name and date of birth; actual PII erasure requires a separately approved data policy.
     */
    public void anonymize(Instant at) {
        requireNotAnonymized();
        Objects.requireNonNull(at, "Anonymization date is required");
        // Validate the whole collection BEFORE mutating anything.
        if (tutorRelationships.stream().filter(TutorStudentRelationship::isAuthorized)
                .anyMatch(r -> at.isBefore(r.getAuthorizedAt())))
            throw new IllegalArgumentException("Anonymization cannot precede authorization");
        for (var relationship : tutorRelationships) if (relationship.isAuthorized()) relationship.revoke(at);
        authorizationRevision++;
        status = StudentStatus.INACTIVE;
        anonymizedAt = at;
    }

    public String getFullName() { return name.getFullName(); }

    private void requireActive() {
        requireNotAnonymized();
        if (status != StudentStatus.ACTIVE) throw new IllegalStateException("Student is inactive");
    }
    private void requireNotAnonymized() {
        if (anonymizedAt != null) throw new IllegalStateException("Student is anonymized");
    }
    private static void requireId(Long value, String label) {
        if (value == null || value < 1) throw new IllegalArgumentException("Invalid " + label + " id");
    }
}
