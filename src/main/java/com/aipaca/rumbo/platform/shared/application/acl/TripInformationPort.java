package com.aipaca.rumbo.platform.shared.application.acl;

import java.util.List;
import java.util.Optional;

/** Read-only contract shared between bounded contexts. */
public interface TripInformationPort {
    Optional<TripSummary> findById(Long tripId);

    record TripSummary(Long routeId, Long driverProfileId,
                       boolean inProgress, List<Long> studentIds) {
        public TripSummary {
            studentIds = List.copyOf(studentIds);
        }
    }
}
