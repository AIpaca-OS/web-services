package com.aipaca.rumbo.platform.shared.application.events;

import java.util.List;

/**
 * Integration fact shared by bounded contexts. Only IDs and non-sensitive event text
 * cross context boundaries; domain entities and JPA classes remain private.
 *
 * @param routeId published route identifier, null for incidents resolved using the trip
 * @param tripId trip identifier, if the fact belongs to a trip
 * @param studentIds affected students, or empty to resolve the audience using the trip
 * @param kind journey fact (or INCIDENT / DELAY)
 * @param title notification title
 * @param message notification text without personal student information
 */
public record StudentJourneyEvent(Long routeId, Long tripId, List<Long> studentIds,
                                 String kind, String title, String message) {
    public StudentJourneyEvent {
        studentIds = studentIds == null ? List.of() : List.copyOf(studentIds);
        if (routeId == null && tripId == null)
            throw new IllegalArgumentException("A route or trip is required for a school journey event");
        if (kind == null || kind.isBlank())
            throw new IllegalArgumentException("Journey event kind is required");
    }
}
