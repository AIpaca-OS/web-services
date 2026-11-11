package com.aipaca.rumbo.platform.profiles.domain.model.queries;
/** Lists students in ACCEPTED enrollments of the authenticated driver. */
public record GetAllStudentsByDriverIdQuery(Long driverId) {
    public GetAllStudentsByDriverIdQuery {
        if (driverId == null || driverId < 1) throw new IllegalArgumentException("Invalid driver id");
    }
}
