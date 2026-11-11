package com.aipaca.rumbo.platform.profiles.domain.model.aggregates;

import com.aipaca.rumbo.platform.profiles.domain.model.commands.RequestStudentDataDeletionCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.DeletionRequestStatus;
import com.aipaca.rumbo.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.time.Instant;
import java.util.Objects;

/**
 * Student data deletion request aggregate root.
 *
 * <p>Tracks the request and its resolution. Authorization, unfinished trips and
 * the actual deletion of personal data must be coordinated by application services.</p>
 */
@Getter
public class DataDeletionRequest extends AbstractDomainAggregateRoot<DataDeletionRequest> {
    private Long id;
    private Long studentId;
    private Long requestedByProfileId;
    private DeletionRequestStatus status;
    private Instant requestedAt;
    private Instant resolvedAt;
    private String resolutionNote;

    private DataDeletionRequest(
            Long id,
            Long studentId,
            Long requestedByProfileId,
            DeletionRequestStatus status,
            Instant requestedAt,
            Instant resolvedAt,
            String resolutionNote
    ) {
        if (id != null) validateId(id, "Deletion request");
        validateId(studentId, "Student");
        validateId(requestedByProfileId, "Requester profile");
        Objects.requireNonNull(status, "Deletion request status is required");
        Objects.requireNonNull(requestedAt, "Request date is required");

        if (status == DeletionRequestStatus.PENDING) {
            if (resolvedAt != null || resolutionNote != null) {
                throw new IllegalArgumentException(
                        "Pending requests cannot have resolution data"
                );
            }
        } else {
            validateResolution(status, resolutionNote, requestedAt, resolvedAt);
        }

        this.id = id;
        this.studentId = studentId;
        this.requestedByProfileId = requestedByProfileId;
        this.status = status;
        this.requestedAt = requestedAt;
        this.resolvedAt = resolvedAt;
        this.resolutionNote = resolutionNote;
    }

    /** Creates a pending request using the current instant. */
    public DataDeletionRequest(RequestStudentDataDeletionCommand command) {
        this(command, Instant.now());
    }

    /** Creates a pending request with a supplied request date. */
    public DataDeletionRequest(RequestStudentDataDeletionCommand command, Instant requestedAt) {
        this(null, command.studentId(), command.requestedByProfileId(),
                DeletionRequestStatus.PENDING, requestedAt, null, null);
    }

    /** Restores persisted identity and state without creating a new request. */
    public static DataDeletionRequest reconstitute(
            Long id,
            Long studentId,
            Long requestedByProfileId,
            DeletionRequestStatus status,
            Instant requestedAt,
            Instant resolvedAt,
            String resolutionNote
    ) {
        validateId(id, "Deletion request");
        return new DataDeletionRequest(id, studentId, requestedByProfileId,
                status, requestedAt, resolvedAt, resolutionNote);
    }

    /**
     * Records the outcome of a pending request after application-level checks.
     * The supplied instant is the resolution date, not a persistence audit date.
     */
    public void resolve(DeletionRequestStatus status, String note, Instant at) {
        if (this.status != DeletionRequestStatus.PENDING) {
            throw new IllegalStateException("Only pending requests can be resolved");
        }
        validateResolution(status, note, this.requestedAt, at);

        this.status = status;
        this.resolutionNote = note;
        this.resolvedAt = at;
    }

    private static void validateResolution(
            DeletionRequestStatus status, String note, Instant requestedAt, Instant resolvedAt
    ) {
        if (status != DeletionRequestStatus.APPROVED && status != DeletionRequestStatus.REJECTED) {
            throw new IllegalArgumentException("Resolution status must be APPROVED or REJECTED");
        }
        if (note == null || note.isBlank()) {
            throw new IllegalArgumentException("Resolution note is required");
        }
        if (note.length() > 500) {
            throw new IllegalArgumentException("Resolution note cannot exceed 500 characters");
        }
        Objects.requireNonNull(resolvedAt, "Resolution date is required");
        if (resolvedAt.isBefore(requestedAt)) {
            throw new IllegalArgumentException("Resolution cannot precede request");
        }
    }

    private static void validateId(Long id, String entityName) {
        if (id == null || id < 1) {
            throw new IllegalArgumentException(entityName + " id cannot be null or less than 1");
        }
    }
}
