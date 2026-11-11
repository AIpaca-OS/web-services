package com.aipaca.rumbo.platform.routes.domain.model.valueobjects;

import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * Value object representing the departure time of a route on a day of the week.
 *
 * @param dayOfWeek     The service day. It cannot be null.
 * @param departureTime The departure time. It cannot be null.
 */
public record RouteSchedule(DayOfWeek dayOfWeek, LocalTime departureTime) {
    /**
     * Compact constructor for RouteSchedule.
     *
     * @throws IllegalArgumentException if the day or the time is null.
     */
    public RouteSchedule {
        if (dayOfWeek == null) {
            throw new IllegalArgumentException("Schedule day of week cannot be null");
        }
        if (departureTime == null) {
            throw new IllegalArgumentException("Schedule departure time cannot be null");
        }
    }
}
