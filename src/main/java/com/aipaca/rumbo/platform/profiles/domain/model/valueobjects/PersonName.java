package com.aipaca.rumbo.platform.profiles.domain.model.valueobjects;

/**
 * PersonName Value Object.
 *
 * @param firstName First name. It cannot be null or blank.
 * @param lastName  Last name. It cannot be null or blank.
 */
public record PersonName(String firstName, String lastName) {

    /**
     * Full name getter
     *
     * @return Full name
     */
    public String getFullName() {
        return "%s %s".formatted(firstName, lastName);
    }

    /**
     * Constructor with validation
     *
     * @param firstName First name
     * @param lastName  Last name
     */
    public PersonName {
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name must not be null nor blank");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Last name must not be null nor blank");
        }
    }
}
