package com.aipaca.rumbo.platform.profiles.domain.model.aggregates;

import com.aipaca.rumbo.platform.profiles.domain.model.commands.RequestServiceEnrollmentCommand;
import com.aipaca.rumbo.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;

import lombok.Getter;

import java.time.Instant;
import java.util.Objects;

/**
 * Student enrollment in a driver's service, independent of route assignments and trips.
 *
 * <p>Protects local state and date consistency. Actor authorization and conflicts
 * with other enrollments must be checked by application services.</p>
 * <p>The lifecycle is PENDING -> ACCEPTED -> ENDED. A student-driver pair may have
 * at most one PENDING or ACCEPTED enrollment. Application services must authorize
 * ending an accepted enrollment for either a currently authorized tutor of the
 * student or the driver referenced by this enrollment.</p>
 */
@Getter
public class ServiceEnrollment extends AbstractDomainAggregateRoot<ServiceEnrollment> {
    public static final String PENDING = "PENDING";
    public static final String ACCEPTED = "ACCEPTED";
    public static final String ENDED = "ENDED";

    private Long id;
    private Long studentId;
    private Long driverProfileId;
    private Long requestedByProfileId;

    private String status;

    private Instant requestedAt;
    private Instant acceptedAt;
    private Instant endedAt;

    // Constructor principal
    private ServiceEnrollment(
            Long id,
            Long studentId,
            Long driverProfileId,
            Long requestedByProfileId,
            String status,
            Instant requestedAt,
            Instant acceptedAt,
            Instant endedAt
    ) {
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

        this.status = Objects.requireNonNull(
                status, "Enrollment status is required"
        );

        this.requestedAt = Objects.requireNonNull(
                requestedAt, "Request date is required"
        );

        this.acceptedAt = acceptedAt;
        this.endedAt = endedAt;

        validateState();
    }

    // Crear una nueva afiliación
    public ServiceEnrollment(
            RequestServiceEnrollmentCommand command
    ) {
        this(command, Instant.now());
    }

    // Crear una afiliación con fecha proporcionada
    public ServiceEnrollment(
            RequestServiceEnrollmentCommand command,
            Instant requestedAt
    ) {
        this(
                null,
                command.studentId(),
                command.driverProfileId(),
                command.requestedByProfileId(),
                PENDING,
                requestedAt,
                null,
                null
        );
    }

    // Reconstruir una afiliación existente
    public static ServiceEnrollment reconstitute(
            Long id,
            Long studentId,
            Long driverProfileId,
            Long requestedByProfileId,
            String status,
            Instant requestedAt,
            Instant acceptedAt,
            Instant endedAt
    ) {
        validateId(id, "Enrollment");

        return new ServiceEnrollment(
                id,
                studentId,
                driverProfileId,
                requestedByProfileId,
                status,
                requestedAt,
                acceptedAt,
                endedAt
        );
    }

    // Aceptar una afiliación pendiente
    public void accept(Instant at) {
        Objects.requireNonNull(
                at, "Acceptance date is required"
        );

        if (!PENDING.equals(this.status)) {
            throw new IllegalStateException(
                    "Only pending enrollments can be accepted"
            );
        }

        if (at.isBefore(this.requestedAt)) {
            throw new IllegalArgumentException(
                    "Acceptance cannot precede request"
            );
        }

        this.status = ACCEPTED;
        this.acceptedAt = at;
    }

    // Finalizar una afiliación aceptada
    public void end(Instant at) {
        Objects.requireNonNull(
                at, "End date is required"
        );

        if (!ACCEPTED.equals(this.status)) {
            throw new IllegalStateException(
                    "Only accepted enrollments can be ended"
            );
        }

        if (at.isBefore(this.acceptedAt)) {
            throw new IllegalArgumentException(
                    "End date cannot precede acceptance"
            );
        }

        this.status = ENDED;
        this.endedAt = at;
    }

    // Consultar si la afiliación está vigente
    public boolean isActive() {
        return ACCEPTED.equals(this.status);
    }

    // Validar la consistencia del estado
    private void validateState() {

        switch (this.status) {

            case PENDING -> {
                if (acceptedAt != null || endedAt != null) {
                    throw new IllegalArgumentException(
                            "Pending enrollment cannot have acceptance or end dates"
                    );
                }
            }

            case ACCEPTED -> {
                if (acceptedAt == null || endedAt != null) {
                    throw new IllegalArgumentException(
                            "Accepted enrollment requires acceptance date and no end date"
                    );
                }
            }

            case ENDED -> {
                if (acceptedAt == null || endedAt == null) {
                    throw new IllegalArgumentException(
                            "Ended enrollment requires acceptance and end dates"
                    );
                }
            }

            default -> throw new IllegalArgumentException(
                    "Invalid enrollment status"
            );
        }

        if (acceptedAt != null &&
                acceptedAt.isBefore(requestedAt)) {
            throw new IllegalArgumentException(
                    "Acceptance cannot precede request"
            );
        }

        if (endedAt != null &&
                endedAt.isBefore(acceptedAt)) {
            throw new IllegalArgumentException(
                    "End date cannot precede acceptance"
            );
        }
    }

    private static void validateId(Long id, String entityName) {
        if (id == null || id < 1) {
            throw new IllegalArgumentException(
                    entityName + " id cannot be null or less than 1"
            );
        }
    }
}
