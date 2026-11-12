package com.aipaca.rumbo.platform.profiles.domain.model.queries;

public record GetAllServiceEnrollmentsByDriverIdQuery(
        Long driverId
) {
    public GetAllServiceEnrollmentsByDriverIdQuery {
        if (driverId == null || driverId < 1) {
            throw new IllegalArgumentException(
                    "Driver id cannot be null or less than 1"
            );
        }
    }
}
