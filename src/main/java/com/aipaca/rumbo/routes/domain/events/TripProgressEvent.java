package com.aipaca.rumbo.routes.domain.events;

import java.util.List;
import java.util.UUID;

/** A trip lifecycle fact, emitted only after a valid domain transition. */
public record TripProgressEvent(UUID routeId, UUID tripId,
                                String kind, List<String> studentIds) {
    public TripProgressEvent {
        studentIds = List.copyOf(studentIds);
    }
}
