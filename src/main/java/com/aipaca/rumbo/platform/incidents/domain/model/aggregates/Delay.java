package com.aipaca.rumbo.platform.incidents.domain.model.aggregates;

import com.aipaca.rumbo.platform.incidents.domain.model.commands.ReportDelayCommand;
import com.aipaca.rumbo.platform.incidents.domain.model.commands.UpdateDelayCommand;
import com.aipaca.rumbo.platform.incidents.domain.model.valueobjects.DelayStatus;
import com.aipaca.rumbo.platform.incidents.domain.model.valueobjects.ProfileId;
import com.aipaca.rumbo.platform.incidents.domain.model.valueobjects.TripId;
import com.aipaca.rumbo.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.time.Instant;

/**
 * Delay aggregate root.
 *
 * <p>
 * A delay reported by a driver that affects the planned progress of a trip. An update of the estimation
 * does not change the reported delay: it creates a new delay that references the previous one, which
 * becomes {@link DelayStatus#SUPERSEDED}, so the history of estimations is kept. Trips and profiles
 * belong to other bounded contexts, so a delay keeps only their identities through {@link TripId} and
 * {@link ProfileId}.
 * </p>
 */
@Getter
public class Delay extends AbstractDomainAggregateRoot<Delay> {
    /**
     * The unique identifier for the delay.
     */
    private Long id;

    /**
     * The delayed trip.
     */
    private TripId tripId;

    /**
     * The driver profile that reported the delay.
     */
    private ProfileId reportedByProfileId;

    /**
     * The delay cause.
     */
    private String cause;

    /**
     * The estimated delay in minutes.
     */
    private int estimatedMinutes;

    /**
     * The delay status.
     */
    private DelayStatus status;

    /**
     * The delay that this one updates, or null for the first report.
     */
    private Long previousDelayId;

    /**
     * The report date.
     */
    private Instant reportedAt;

    private Delay() {
    }

    /**
     * Constructor with a ReportDelayCommand. Creates the first, active estimation of a delay.
     *
     * @param command The {@link ReportDelayCommand} instance
     */
    public Delay(ReportDelayCommand command) {
        this.tripId = new TripId(command.tripId());
        this.reportedByProfileId = new ProfileId(command.reportedByProfileId());
        this.cause = requireText(command.cause(), "Cause", 240);
        this.estimatedMinutes = requireMinutes(command.estimatedMinutes());
        this.status = DelayStatus.ACTIVE;
        this.reportedAt = Instant.now();
    }

    /**
     * Constructor with an UpdateDelayCommand and the delay it updates. Creates the new active
     * estimation of the same trip; the previous delay must be superseded by the caller.
     *
     * @param command  The {@link UpdateDelayCommand} instance
     * @param previous The active delay that is updated.
     */
    public Delay(UpdateDelayCommand command, Delay previous) {
        if (previous == null || previous.getId() == null) {
            throw new IllegalArgumentException("Previous delay must be persisted");
        }
        if (!previous.isActive()) {
            throw new IllegalArgumentException("Only the active delay can be updated");
        }
        this.tripId = previous.getTripId();
        this.reportedByProfileId = new ProfileId(command.reportedByProfileId());
        this.cause = requireText(command.cause(), "Cause", 240);
        this.estimatedMinutes = requireMinutes(command.estimatedMinutes());
        this.status = DelayStatus.ACTIVE;
        this.previousDelayId = previous.getId();
        this.reportedAt = Instant.now();
    }

    /**
     * Reconstitutes an existing delay from persistence state.
     *
     * @return The reconstituted Delay instance.
     */
    public static Delay reconstitute(Long id, TripId tripId, ProfileId reportedByProfileId, String cause,
                                     int estimatedMinutes, DelayStatus status, Long previousDelayId,
                                     Instant reportedAt) {
        var delay = new Delay();
        delay.id = id;
        delay.tripId = tripId;
        delay.reportedByProfileId = reportedByProfileId;
        delay.cause = cause;
        delay.estimatedMinutes = estimatedMinutes;
        delay.status = status;
        delay.previousDelayId = previousDelayId;
        delay.reportedAt = reportedAt;
        return delay;
    }

    /**
     * Marks the delay as replaced by a later estimation.
     */
    public void supersede() {
        if (!isActive()) {
            throw new IllegalArgumentException("Only the active delay can be updated");
        }
        this.status = DelayStatus.SUPERSEDED;
    }

    public boolean isActive() {
        return status == DelayStatus.ACTIVE;
    }

    public Long getTripIdValue() {
        return tripId.tripId();
    }

    public Long getReportedByProfileIdValue() {
        return reportedByProfileId.profileId();
    }

    private static int requireMinutes(Integer minutes) {
        if (minutes == null || minutes < 1 || minutes > 600) {
            throw new IllegalArgumentException("Estimated minutes must be between 1 and 600");
        }
        return minutes;
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
