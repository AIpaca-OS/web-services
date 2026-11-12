package com.aipaca.rumbo.platform.profiles.domain.model.aggregates;

import com.aipaca.rumbo.platform.profiles.domain.model.commands.RequestStudentDataDeletionCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.DeletionRequestStatus;
import com.aipaca.rumbo.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.time.Instant;
import java.util.Objects;

/**
 * Data deletion request aggregate root.
 *
 * <p>
 * Tracks a request to delete the personal data of a student and its resolution.
 * Saving a request does not delete any data: authorization, unfinished trips and the
 * actual erasure must be coordinated by application services.
 * No JPA or persistence annotation is present here — those concerns live
 * exclusively in {@code DataDeletionRequestPersistenceEntity}.
 * </p>
 */
@Getter
public class DataDeletionRequest extends AbstractDomainAggregateRoot<DataDeletionRequest> {
    /**
     * The unique identifier for the request.
     */
    private Long id;

    /**
     * The student whose data must be deleted.
     */
    private Long studentId;

    /**
     * The profile id of the tutor that requested the deletion.
     */
    private Long requestedByProfileId;

    /**
     * The request status.
     */
    private DeletionRequestStatus status;

    /**
     * The request date.
     */
    private Instant requestedAt;

    /**
     * The resolution date, if resolved.
     */
    private Instant resolvedAt;

    /**
     * The resolution note, if resolved.
     */
    private String resolutionNote;

    private DataDeletionRequest(Long id, Long studentId, Long requestedByProfileId, DeletionRequestStatus status,
                                Instant requestedAt, Instant resolvedAt, String resolutionNote) {
        if (id != null) {
            validateId(id, "Deletion request");
        }
        validateId(studentId, "Student");
        validateId(requestedByProfileId, "Requester profile");
        Objects.requireNonNull(status, "Deletion request status is required");
        Objects.requireNonNull(requestedAt, "Request date is required");
        if (status == DeletionRequestStatus.PENDING) {
            if (resolvedAt != null || resolutionNote != null) {
                throw new IllegalArgumentException("Pending requests cannot have resolution data");
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

    /**
     * Constructor with a RequestStudentDataDeletionCommand.
     * Creates a pending request using the current instant.
     *
     * @param command The {@link RequestStudentDataDeletionCommand} instance
     */
    public DataDeletionRequest(RequestStudentDataDeletionCommand command) {
        this(command, Instant.now());
    }

    /**
     * Constructor with a RequestStudentDataDeletionCommand and the request date.
     *
     * @param command     The {@link RequestStudentDataDeletionCommand} instance
     * @param requestedAt The request date.
     */
    public DataDeletionRequest(RequestStudentDataDeletionCommand command, Instant requestedAt) {
        this(null, command.studentId(), command.requestedByProfileId(),
                DeletionRequestStatus.PENDING, requestedAt, null, null);
    }

    /**
     * Reconstitutes an existing request from persistence state.
     *
     * @param id                   The unique identifier.
     * @param studentId            The student id.
     * @param requestedByProfileId The requester profile id.
     * @param status               The request status.
     * @param requestedAt          The request date.
     * @param resolvedAt           The resolution date, or null.
     * @param resolutionNote       The resolution note, or null.
     * @return The reconstituted DataDeletionRequest instance.
     */
    public static DataDeletionRequest reconstitute(Long id, Long studentId, Long requestedByProfileId,
                                                   DeletionRequestStatus status, Instant requestedAt,
                                                   Instant resolvedAt, String resolutionNote) {
        validateId(id, "Deletion request");
        return new DataDeletionRequest(id, studentId, requestedByProfileId,
                status, requestedAt, resolvedAt, resolutionNote);
    }

    /**
     * Records the outcome of a pending request after the application-level checks.
     *
     * @param status The resolution status, APPROVED or REJECTED.
     * @param note   The resolution note.
     * @param at     The resolution date.
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

    /**
     * Approves a pending request.
     *
     * @param note The resolution note.
     * @param at   The resolution date.
     */
    public void approve(String note, Instant at) {
        resolve(DeletionRequestStatus.APPROVED, note, at);
    }

    /**
     * Rejects a pending request.
     *
     * @param note The resolution note.
     * @param at   The resolution date.
     */
    public void reject(String note, Instant at) {
        resolve(DeletionRequestStatus.REJECTED, note, at);
    }

    private static void validateResolution(DeletionRequestStatus status, String note,
                                           Instant requestedAt, Instant resolvedAt) {
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
