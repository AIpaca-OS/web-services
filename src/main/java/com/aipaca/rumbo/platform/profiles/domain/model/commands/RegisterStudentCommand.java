package com.aipaca.rumbo.platform.profiles.domain.model.commands;

import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.RelationshipType;

import java.time.LocalDate;

public record RegisterStudentCommand(
        Long tutorProfileId,
        String firstName,
        String lastName,
        LocalDate birthDate,
        Long schoolId,
        RelationshipType relationshipType
) {
    public RegisterStudentCommand {
        if (tutorProfileId == null || tutorProfileId < 1)
            throw new IllegalArgumentException("Invalid tutor profile id");

        if (firstName == null || firstName.isBlank())
            throw new IllegalArgumentException("First name is required");

        if (lastName == null || lastName.isBlank())
            throw new IllegalArgumentException("Last name is required");

        if (birthDate == null)
            throw new IllegalArgumentException("Birth date is required");

        if (schoolId != null && schoolId < 1)
            throw new IllegalArgumentException("Invalid school id");

        if (relationshipType == null)
            throw new IllegalArgumentException("Relationship type is required");
    }
}
