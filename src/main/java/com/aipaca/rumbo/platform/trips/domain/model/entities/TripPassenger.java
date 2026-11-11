package com.aipaca.rumbo.platform.trips.domain.model.entities;

import com.aipaca.rumbo.platform.trips.domain.model.valueobjects.SchoolId;
import com.aipaca.rumbo.platform.trips.domain.model.valueobjects.StudentId;
import lombok.Getter;

import java.time.Instant;
import java.util.Objects;

/**
 * Trip passenger entity.
 *
 * <p>Snapshot of a route assignment inside the {@code Trip} aggregate. The stop data is
 * copied when the trip is planned, so later route changes do not alter a planned trip. The school
 * served at the stop, if any, is kept through its {@link SchoolId}.</p>
 */
@Getter
public class TripPassenger {
    private Long id;
    private StudentId studentId;
    private Long stopId;
    private int stopPosition;
    private String stopAddress;
    private SchoolId schoolId;
    private boolean absent;
    private boolean stopServed;
    private Instant arrivedAt;

    /**
     * Creates a passenger snapshot for a new trip.
     */
    public TripPassenger(StudentId studentId, Long stopId, int stopPosition, String stopAddress, SchoolId schoolId) {
        this(null, studentId, stopId, stopPosition, stopAddress, schoolId, false, false, null);
    }

    /**
     * Creates a passenger with its complete state.
     */
    public TripPassenger(Long id, StudentId studentId, Long stopId, int stopPosition, String stopAddress,
                         SchoolId schoolId, boolean absent, boolean stopServed, Instant arrivedAt) {
        if (stopId == null || stopId < 1) {
            throw new IllegalArgumentException("Stop id cannot be null or less than 1");
        }
        this.id = id;
        this.studentId = Objects.requireNonNull(studentId, "Student id cannot be null");
        this.stopId = stopId;
        this.stopPosition = stopPosition;
        this.stopAddress = stopAddress;
        this.schoolId = schoolId;
        this.absent = absent;
        this.stopServed = stopServed;
        this.arrivedAt = arrivedAt;
    }

    /**
     * Records the driver's explicit confirmation, distinct from a served stop or a completed trip.
     */
    public void confirmArrival() {
        if (absent) throw new IllegalArgumentException("An absent student cannot be marked arrived");
        if (!stopServed) throw new IllegalArgumentException("Serve the assigned stop before confirming arrival");
        if (arrivedAt != null) throw new IllegalArgumentException("Arrival already confirmed");
        arrivedAt = Instant.now();
    }

    public void markAbsent() {
        if (stopServed) throw new IllegalArgumentException("Stop has already been served");
        absent = true;
    }

    public void markStopServed() {
        stopServed = true;
    }

    public boolean isArrived() {
        return arrivedAt != null;
    }

    public Long getStudentIdValue() {
        return studentId.studentId();
    }

    public Long getSchoolIdValue() {
        return schoolId == null ? null : schoolId.schoolId();
    }
}
