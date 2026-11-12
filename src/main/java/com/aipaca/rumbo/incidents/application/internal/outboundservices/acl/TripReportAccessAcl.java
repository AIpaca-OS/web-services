package com.aipaca.rumbo.incidents.application.internal.outboundservices.acl;

import com.aipaca.rumbo.platform.shared.application.acl.TripInformationPort;
import org.springframework.stereotype.Component;

/**
 * Validates that an incident/delay is reported for a trip actually operated by
 * the indicated driver. This is not user authentication; IAM remains required.
 */
@Component
public class TripReportAccessAcl {
    private final TripInformationPort trips;

    public TripReportAccessAcl(TripInformationPort trips) {
        this.trips = trips;
    }

    public boolean canReport(Long tripId, Long driverId, Long studentId) {
        return trips.findById(tripId)
                .filter(trip -> trip.inProgress())
                .filter(trip -> trip.driverProfileId().equals(driverId))
                .filter(trip -> studentId == null || trip.studentIds().contains(studentId))
                .isPresent();
    }
}
