package com.aipaca.rumbo.platform.profiles.domain.model.commands;

import java.time.LocalDate;

/**
 * Update Student Information Command
 *
 * @param studentId student id
 * @param firstName first name
 * @param lastName  last name
 * @param birthDate birth date
 */
public record UpdateStudentInformationCommand(
        Long studentId,
        String firstName,
        String lastName,
        LocalDate birthDate) {
    /**
     * Compact constructor for UpdateStudentInformationCommand.
     *
     * @throws IllegalArgumentException if any value is invalid.
     */
    public UpdateStudentInformationCommand {
        if (studentId == null || studentId < 1) {
            throw new IllegalArgumentException("Invalid student id");
        }
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Last name is required");
        }
        if (birthDate == null) {
            throw new IllegalArgumentException("Birth date is required");
        }

    }
}
