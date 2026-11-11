package com.aipaca.rumbo.platform.profiles.domain.model.aggregates;

import com.aipaca.rumbo.platform.profiles.domain.model.commands.RequestServiceEnrollmentCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.ServiceEnrollmentStatus;
import com.aipaca.rumbo.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.time.Instant;
import java.util.Objects;

/**
 * Service enrollment aggregate root.
 *
 * <p>
 * Enrollment of a student in a driver's service, independent of route assignments and trips.
 * The lifecycle is PENDING -> ACCEPTED -> ENDED, and a PENDING enrollment may also become
 * CANCELLED. A student-driver pair may have at most one PENDING or ACCEPTED enrollment;
 * that rule and the permissions of each actor are checked by the application services.
 * No JPA or persistence annotation is present here — those concerns live
 * exclusively in {@code ServiceEnrollmentPersistenceEntity}.
 * </p>
 */
@Getter
public class ServiceEnrollment extends AbstractDomainAggregateRoot<ServiceEnrollment> {
    /**
     * The unique identifier for the enrollment.
     */
    private Long id;

    /**
     * The enrolled student id.
     */
    private Long studentId;

    /**
     * The driver profile id.
     */
    private Long driverProfileId;

    /**
     * The profile id of the tutor that requested the enrollment.
     */
    private Long requestedByProfileId;

    /**
     * The enrollment status.
     */
    private ServiceEnrollmentStatus status;

    /**
     * The request date.
     */
    private Instant requestedAt;

    /**
     * The acceptance date, if accepted.
     */
    private Instant acceptedAt;

    /**
     * The cancellation date, if cancelled.
     */
    private Instant cancelledAt;

    /**
     * The end date, if ended.
     */
    private Instant endedAt;

    private ServiceEnrollment(Long id, Long studentId, Long driverProfileId, Long requestedByProfileId,
                              ServiceEnrollmentStatus status, Instant requestedAt, Instant acceptedAt,
                              Instant cancelledAt, Instant endedAt) {
        if (id != null) {
            validateId(id, "Enrollment");
        }
        validateId(studentId, "Student");
        validateId(driverProfileId, "Driver profile");
        validateId(requestedByProfileId, "Requester profile");
        this.id = id;
        this.studentId = studentId;
        this.driverProfileId = driverProfileId;
        this.requestedByProfileId = requestedByProfileId;
        this.status = Objects.requireNonNull(status, "Enrollment status is required");
        this.requestedAt = Objects.requireNonNull(requestedAt, "Request date is required");
        this.acceptedAt = acceptedAt;
        this.cancelledAt = cancelledAt;
        this.endedAt = endedAt;
        validateState();
    }

    /**
     * Constructor with a RequestServiceEnrollmentCommand.
     * Creates a pending enrollment requested now.
     *
     * @param command The {@link RequestServiceEnrollmentCommand} instance
     */
    public ServiceEnrollment(RequestServiceEnrollmentCommand command) {
        this(command, Instant.now());
    }

    /**
     * Constructor with a RequestServiceEnrollmentCommand and the request date.
     *
     * @param command     The {@link RequestServiceEnrollmentCommand} instance
     * @param requestedAt The request date.
     */
    public ServiceEnrollment(RequestServiceEnrollmentCommand command, Instant requestedAt) {
        this(null, command.studentId(), command.driverProfileId(), command.requestedByProfileId(),
                ServiceEnrollmentStatus.PENDING, requestedAt, null, null, null);
    }

    /**
     * Reconstitutes an existing enrollment from persistence state.
     *
     * @param id                   The unique identifier.
     * @param studentId            The enrolled student id.
     * @param driverProfileId      The driver profile id.
     * @param requestedByProfileId The requester profile id.
     * @param status               The enrollment status.
     * @param requestedAt          The request date.
     * @param acceptedAt           The acceptance date, or null.
     * @param cancelledAt          The cancellation date, or null.
     * @param endedAt              The end date, or null.
     * @return The reconstituted ServiceEnrollment instance.
     */
    public static ServiceEnrollment reconstitute(Long id, Long studentId, Long driverProfileId,
                                                 Long requestedByProfileId, ServiceEnrollmentStatus status,
                                                 Instant requestedAt, Instant acceptedAt, Instant cancelledAt,
                                                 Instant endedAt) {
        validateId(id, "Enrollment");
        return new ServiceEnrollment(id, studentId, driverProfileId, requestedByProfileId,
                status, requestedAt, acceptedAt, cancelledAt, endedAt);
    }

    /**
     * Reconstitutes an existing enrollment that has no cancellation date.
     *
     * @param id                   The unique identifier.
     * @param studentId            The enrolled student id.
     * @param driverProfileId      The driver profile id.
     * @param requestedByProfileId The requester profile id.
     * @param status               The enrollment status.
     * @param requestedAt          The request date.
     * @param acceptedAt           The acceptance date, or null.
     * @param endedAt              The end date, or null.
     * @return The reconstituted ServiceEnrollment instance.
     */
    public static ServiceEnrollment reconstitute(Long id, Long studentId, Long driverProfileId,
                                                 Long requestedByProfileId, ServiceEnrollmentStatus status,
                                                 Instant requestedAt, Instant acceptedAt, Instant endedAt) {
        return reconstitute(id, studentId, driverProfileId, requestedByProfileId,
                status, requestedAt, acceptedAt, null, endedAt);
    }

    /**
     * Cancels a pending enrollment. Only the profile that requested it can cancel it.
     *
     * @param by The profile id that cancels the enrollment.
     * @param at The cancellation date.
     */
    public void cancel(Long by, Instant at) {
        validateId(by, "Cancelling actor");
        Objects.requireNonNull(at, "Cancellation date is required");
        if (status != ServiceEnrollmentStatus.PENDING) {
            throw new IllegalStateException("Only pending enrollments can be cancelled");
        }
        if (!requestedByProfileId.equals(by)) {
            throw new IllegalStateException("Only requester can cancel enrollment");
        }
        if (at.isBefore(requestedAt)) {
            throw new IllegalArgumentException("Cancellation cannot precede request");
        }
        this.status = ServiceEnrollmentStatus.CANCELLED;
        this.cancelledAt = at;
    }

    /**
     * Accepts a pending enrollment.
     *
     * @param at The acceptance date.
     */
    public void accept(Instant at) {
        Objects.requireNonNull(at, "Acceptance date is required");
        if (status != ServiceEnrollmentStatus.PENDING) {
            throw new IllegalStateException("Only pending enrollments can be accepted");
        }
        if (at.isBefore(requestedAt)) {
            throw new IllegalArgumentException("Acceptance cannot precede request");
        }
        this.status = ServiceEnrollmentStatus.ACCEPTED;
        this.acceptedAt = at;
    }

    /**
     * Ends an accepted enrollment.
     *
     * @param at The end date.
     */
    public void end(Instant at) {
        Objects.requireNonNull(at, "End date is required");
        if (status != ServiceEnrollmentStatus.ACCEPTED) {
            throw new IllegalStateException("Only accepted enrollments can be ended");
        }
        if (at.isBefore(acceptedAt)) {
            throw new IllegalArgumentException("End date cannot precede acceptance");
        }
        this.status = ServiceEnrollmentStatus.ENDED;
        this.endedAt = at;
    }

    /**
     * Checks whether the service is currently active.
     *
     * @return {@code true} when the enrollment is accepted.
     */
    public boolean isActive() {
        return status == ServiceEnrollmentStatus.ACCEPTED;
    }

    /**
     * Checks whether the enrollment is waiting for the driver.
     *
     * @return {@code true} when the enrollment is pending.
     */
    public boolean isPending() {
        return status == ServiceEnrollmentStatus.PENDING;
    }

    private void validateState() {
        switch (status) {
            case PENDING -> {
                if (acceptedAt != null || cancelledAt != null || endedAt != null) {
                    throw new IllegalArgumentException("Pending enrollment cannot have acceptance or end dates");
                }
            }
            case ACCEPTED -> {
                if (acceptedAt == null || cancelledAt != null || endedAt != null) {
                    throw new IllegalArgumentException("Accepted enrollment requires acceptance date and no end date");
                }
            }
            case ENDED -> {
                if (acceptedAt == null || cancelledAt != null || endedAt == null) {
                    throw new IllegalArgumentException("Ended enrollment requires acceptance and end dates");
                }
            }
            case CANCELLED -> {
                if (acceptedAt != null || cancelledAt == null || endedAt != null) {
                    throw new IllegalArgumentException("Cancelled enrollment needs cancellation date only");
                }
            }
        }
        if (cancelledAt != null && cancelledAt.isBefore(requestedAt)) {
            throw new IllegalArgumentException("Cancellation cannot precede request");
        }
        if (acceptedAt != null && acceptedAt.isBefore(requestedAt)) {
            throw new IllegalArgumentException("Acceptance cannot precede request");
        }
        if (endedAt != null && endedAt.isBefore(acceptedAt)) {
            throw new IllegalArgumentException("End date cannot precede acceptance");
        }
    }

    private static void validateId(Long id, String entityName) {
        if (id == null || id < 1) {
            throw new IllegalArgumentException(entityName + " id cannot be null or less than 1");
        }
    }
}
