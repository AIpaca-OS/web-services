package com.aipaca.rumbo.platform.notifications.application.internal.outboundservices.acl;

import com.aipaca.rumbo.platform.shared.application.acl.TripInformationPort;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Anti-corruption adapter: Notifications reads a minimal audience projection from Trips,
 * never its persistence entities or its repositories.
 */
@Component
public class TripAudienceAcl {
    private final TripInformationPort tripInformation;

    public TripAudienceAcl(TripInformationPort tripInformation) {
        this.tripInformation = tripInformation;
    }

    public Audience of(Long tripId) {
        var trip = tripInformation.findById(tripId)
                .orElseThrow(() -> new IllegalStateException("Trip not found when creating journey notifications: " + tripId));
        return new Audience(trip.routeId(), trip.studentIds());
    }

    public record Audience(Long routeId, List<Long> studentIds) { }
}
