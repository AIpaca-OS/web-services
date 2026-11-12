package com.aipaca.rumbo.platform.profiles.domain.model.commands;

import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.RelationshipType;
import java.time.LocalDate;

/**
 * Register Student Command
 *
 * @param tutorProfileId   id of the tutor profile that registers the student
 * @param firstName        first name
 * @param lastName         last name
 * @param birthDate        birth date
 * @param relationshipType relationship between tutor and student
 */
public record RegisterStudentCommand(
        Long tutorProfileId,
        String firstName,
        String lastName,
        LocalDate birthDate,
        RelationshipType relationshipType
) {
    /**
     * Compact constructor for RegisterStudentCommand.
     *
     * @throws IllegalArgumentException if any value is invalid.
     */
    public RegisterStudentCommand {
        if (tutorProfileId == null || tutorProfileId < 1) {
            throw new IllegalArgumentException("Invalid tutor profile id");
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

        if (relationshipType == null) {
            throw new IllegalArgumentException("Relationship type is required");
        }
    }
}
