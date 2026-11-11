package com.aipaca.rumbo.platform.profiles.domain.model.valueobjects;

/**
 * Represents the possible relationship types
 * between a tutor and a student.
 */
public enum RelationshipType {
    /**
     * The tutor is the student's mother.
     */
    MOTHER,

    /**
     * The tutor is the student's father.
     */
    FATHER,

    /**
     * The tutor is the student's legal guardian.
     */
    LEGAL_GUARDIAN,

    /**
     * The tutor has another type of relationship
     * with the student.
     */
    OTHER
}
