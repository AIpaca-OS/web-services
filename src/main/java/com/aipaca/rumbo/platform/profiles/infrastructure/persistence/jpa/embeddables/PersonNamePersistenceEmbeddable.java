package com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.embeddables;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

/** Persistence representation of the components of PersonName. */
@Getter
@Setter
@Embeddable
public class PersonNamePersistenceEmbeddable {
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    public PersonNamePersistenceEmbeddable() {
    }

    public PersonNamePersistenceEmbeddable(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
