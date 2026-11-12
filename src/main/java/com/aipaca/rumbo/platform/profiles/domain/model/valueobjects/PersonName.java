package com.aipaca.rumbo.platform.profiles.domain.model.valueobjects;

public record PersonName(String firstName, String lastName) {
    public String getFullName() {
        return "%s %s".formatted(firstName, lastName);
    }

    public PersonName {
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name must not be null nor blank");
        }

        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Last name must not be null nor blank");
        }
    }
}
