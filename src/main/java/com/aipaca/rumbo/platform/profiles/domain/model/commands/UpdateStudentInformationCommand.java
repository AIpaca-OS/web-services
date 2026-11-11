package com.aipaca.rumbo.platform.profiles.domain.model.commands;

import java.time.LocalDate;

public record UpdateStudentInformationCommand(
        Long studentId,
        String firstName,
        String lastName,
        LocalDate birthDate,
        Long schoolId
) {
    public UpdateStudentInformationCommand {
        if (studentId == null || studentId < 1)
            throw new IllegalArgumentException("Invalid student id");

        if (firstName == null || firstName.isBlank())
            throw new IllegalArgumentException("First name is required");

        if (lastName == null || lastName.isBlank())
            throw new IllegalArgumentException("Last name is required");

        if (birthDate == null)
            throw new IllegalArgumentException("Birth date is required");

        if (schoolId != null && schoolId < 1)
            throw new IllegalArgumentException("Invalid school id");
    }
}