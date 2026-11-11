package com.aipaca.rumbo.platform.notifications.domain.model.valueobjects;

/**
 * Represents the facts of the journey of a student that its tutors are notified about.
 */
public enum JourneyEvent {
    /**
     * The route of the student was published.
     */
    ROUTE_PUBLISHED,

    /**
     * A trip was planned for the route of the student.
     */
    TRIP_PLANNED,

    /**
     * The driver started the trip.
     */
    TRIP_STARTED,

    /**
     * The driver served the stop assigned to the student.
     */
    STOP_SERVED,

    /**
     * The student was reported absent.
     */
    ABSENT,

    /**
     * The driver confirmed the arrival of the student.
     */
    STUDENT_ARRIVED,

    /**
     * The driver cancelled the trip.
     */
    TRIP_CANCELLED,

    /**
     * The driver completed the trip.
     */
    TRIP_COMPLETED
}
