package com.aipaca.rumbo.platform.profiles.domain.model.queries;

public record GetSchoolByIdQuery(Long schoolId) {
    public GetSchoolByIdQuery {
        if (schoolId == null || schoolId < 1) {
            throw new IllegalArgumentException(
                    "School id cannot be null or less than 1"
            );
        }
    }
}
