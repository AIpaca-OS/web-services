package com.aipaca.rumbo.platform.profiles.domain.model.queries;

public record GetTutorStudentRelationshipByTutorIdAndStudentIdQuery(
        Long tutorId,
        Long studentId
) {}
