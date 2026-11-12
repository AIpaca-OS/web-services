package com.aipaca.rumbo.platform.incidents.domain.model.entities;

import com.aipaca.rumbo.platform.incidents.domain.model.valueobjects.ProfileId;
import lombok.Getter;

import java.time.Instant;
import java.util.Objects;

/**
 * Incident acknowledgement entity.
 *
 * <p>Confirmation, with identity inside the {@code Incident} aggregate, that a tutor took notice
 * of a reported incident. The identifier stays null until the acknowledgement is persisted.</p>
 */
@Getter
public class IncidentAcknowledgement {
    /**
     * The unique identifier for the acknowledgement.
     */
    private Long id;

    /**
     * The tutor profile that confirms the reception.
     */
    private ProfileId tutorProfileId;

    /**
     * The acknowledgement date.
     */
    private Instant acknowledgedAt;

    /**
     * Creates an acknowledgement.
     *
     * @param id             The acknowledgement id, or null for a new acknowledgement.
     * @param tutorProfileId The tutor profile that confirms the reception.
     * @param acknowledgedAt The acknowledgement date.
     */
    public IncidentAcknowledgement(Long id, ProfileId tutorProfileId, Instant acknowledgedAt) {
        this.id = id;
        this.tutorProfileId = Objects.requireNonNull(tutorProfileId, "Tutor profile id cannot be null");
        this.acknowledgedAt = Objects.requireNonNull(acknowledgedAt, "Acknowledgement date cannot be null");
    }

    /**
     * Gets the tutor profile id value.
     *
     * @return The tutor profile id.
     */
    public Long getTutorProfileIdValue() {
        return tutorProfileId.profileId();
    }
}
