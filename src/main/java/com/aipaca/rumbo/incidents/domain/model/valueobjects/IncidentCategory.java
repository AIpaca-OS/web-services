package com.aipaca.rumbo.incidents.domain.model.valueobjects;

/**
 * Represents the categories of an incident reported during a trip.
 */
public enum IncidentCategory {
    /**
     * The vehicle has a mechanical failure.
     */
    MECHANICAL_FAILURE,

    /**
     * The vehicle was involved in a traffic accident.
     */
    TRAFFIC_ACCIDENT,

    /**
     * The road is blocked or closed.
     */
    ROAD_BLOCKED,

    /**
     * A student needs health assistance.
     */
    STUDENT_HEALTH,

    /**
     * Any other incident.
     */
    OTHER
}
