package com.aipaca.rumbo.platform.incidents.domain.model.aggregates;

import com.aipaca.rumbo.platform.incidents.domain.model.commands.ReportIncidentCommand;
import com.aipaca.rumbo.platform.incidents.domain.model.entities.IncidentAcknowledgement;
import com.aipaca.rumbo.platform.incidents.domain.model.valueobjects.*;
import com.aipaca.rumbo.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Incident aggregate root.
 *
 * <p>
 * An unexpected situation reported by a driver during a trip. It keeps its category, description,
 * status and resolution, and the {@link IncidentAcknowledgement} entities through which tutors confirm
 * that they took notice of it. Trips, students and profiles belong to other bounded contexts, so an
 * incident keeps only their identities through {@link TripId}, {@link StudentId} and {@link ProfileId}.
 * </p>
 */
@Getter
public class Incident extends AbstractDomainAggregateRoot<Incident> {
    /**
     * The unique identifier for the incident.
     */
    private Long id;

    /**
     * The trip where the incident happened.
     */
    private TripId tripId;

    /**
     * The driver profile that reported the incident.
     */
    private ProfileId reportedByProfileId;

    /**
     * The affected student, or null when the incident affects the whole trip.
     */
    private StudentId studentId;

    /**
     * The incident category.
     */
    private IncidentCategory category;

    /**
     * The incident description.
     */
    private String description;

    /**
     * The incident status.
     */
    private IncidentStatus status;

    /**
     * The resolution, if resolved.
     */
    private String resolution;

    /**
     * The report date.
     */
    private Instant reportedAt;

    /**
     * The resolution date, if resolved.
     */
    private Instant resolvedAt;

    /**
     * The tutor acknowledgements of the incident.
     */
    private final List<IncidentAcknowledgement> acknowledgements;

    private Incident() {
        this.acknowledgements = new ArrayList<>();
    }

    /**
     * Constructor with a ReportIncidentCommand. Creates an open incident reported now.
     *
     * @param command The {@link ReportIncidentCommand} instance
     */
    public Incident(ReportIncidentCommand command) {
        this();
        this.tripId = new TripId(command.tripId());
        this.reportedByProfileId = new ProfileId(command.reportedByProfileId());
        this.studentId = command.studentId() == null ? null : new StudentId(command.studentId());
        this.category = command.category();
        this.description = requireText(command.description(), "Description", 500);
        this.status = IncidentStatus.OPEN;
        this.reportedAt = Instant.now();
    }

    /**
     * Reconstitutes an existing incident from persistence state.
     *
     * @return The reconstituted Incident instance.
     */
    public static Incident reconstitute(Long id, TripId tripId, ProfileId reportedByProfileId, StudentId studentId,
                                        IncidentCategory category, String description, IncidentStatus status,
                                        String resolution, Instant reportedAt, Instant resolvedAt,
                                        List<IncidentAcknowledgement> acknowledgements) {
        var incident = new Incident();
        incident.id = id;
        incident.tripId = tripId;
        incident.reportedByProfileId = reportedByProfileId;
        incident.studentId = studentId;
        incident.category = category;
        incident.description = description;
        incident.status = status;
        incident.resolution = resolution;
        incident.reportedAt = reportedAt;
        incident.resolvedAt = resolvedAt;
        incident.acknowledgements.addAll(acknowledgements);
        return incident;
    }

    /**
     * Updates the category and the description of an open incident.
     *
     * @param category    The incident category.
     * @param description The incident description.
     */
    public void update(IncidentCategory category, String description) {
        requireOpen();
        if (category == null) {
            throw new IllegalArgumentException("Incident category cannot be null");
        }
        this.category = category;
        this.description = requireText(description, "Description", 500);
    }

    /**
     * Resolves an open incident. A resolved incident keeps its original resolution.
     *
     * @param resolution The outcome of the incident.
     */
    public void resolve(String resolution) {
        requireOpen();
        this.resolution = requireText(resolution, "Resolution", 500);
        this.status = IncidentStatus.RESOLVED;
        this.resolvedAt = Instant.now();
    }

    /**
     * Records that a tutor took notice of the incident. Each tutor acknowledges it only once.
     *
     * @param tutorProfileId The tutor profile that confirms the reception.
     */
    public void acknowledge(ProfileId tutorProfileId) {
        if (tutorProfileId == null) {
            throw new IllegalArgumentException("Tutor profile id cannot be null");
        }
        if (isAcknowledgedBy(tutorProfileId)) {
            throw new IllegalArgumentException("The tutor already acknowledged this incident");
        }
        acknowledgements.add(new IncidentAcknowledgement(null, tutorProfileId, Instant.now()));
    }

    /**
     * Checks whether a tutor already acknowledged the incident.
     *
     * @param tutorProfileId The tutor profile id.
     * @return {@code true} when the tutor has an acknowledgement.
     */
    public boolean isAcknowledgedBy(ProfileId tutorProfileId) {
        return acknowledgements.stream()
                .anyMatch(acknowledgement -> acknowledgement.getTutorProfileId().equals(tutorProfileId));
    }

    /**
     * Gets an unmodifiable snapshot of the acknowledgements.
     *
     * @return The acknowledgements.
     */
    public List<IncidentAcknowledgement> getAcknowledgements() {
        return List.copyOf(acknowledgements);
    }

    public boolean isResolved() {
        return status == IncidentStatus.RESOLVED;
    }

    public Long getTripIdValue() {
        return tripId.tripId();
    }

    public Long getReportedByProfileIdValue() {
        return reportedByProfileId.profileId();
    }

    public Long getStudentIdValue() {
        return studentId == null ? null : studentId.studentId();
    }

    private void requireOpen() {
        if (status != IncidentStatus.OPEN) {
            throw new IllegalArgumentException("The incident is already resolved");
        }
    }

    private static String requireText(String value, String field, int maxLength) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " cannot be null or blank");
        }
        if (value.trim().length() > maxLength) {
            throw new IllegalArgumentException(field + " cannot exceed " + maxLength + " characters");
        }
        return value.trim();
    }
}
