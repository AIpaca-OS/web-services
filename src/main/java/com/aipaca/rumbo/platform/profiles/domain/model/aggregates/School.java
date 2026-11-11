package com.aipaca.rumbo.platform.profiles.domain.model.aggregates;

import com.aipaca.rumbo.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

/**
 * School aggregate root, referenced by students through its identity.
 * Persistence mapping belongs to infrastructure.
 */
@Getter
public class School extends AbstractDomainAggregateRoot<School> {
    private Long id;
    private String name;
    private String address;
    private String district;

    private School(Long id, String name, String address, String district) {
        if (id != null && id < 1) {
            throw new IllegalArgumentException("Invalid school id");
        }
        this.id = id;
        this.name = validateText(name, "School name", 150);
        this.address = validateText(address, "School address", 255);
        this.district = validateText(district, "School district", 100);
    }

    /** Creates a school whose identity will be assigned by persistence. */
    public School(String name, String address, String district) {
        this(null, name, address, district);
    }

    /** Restores an existing school with its persisted identity and values. */
    public static School reconstitute(Long id, String name, String address, String district) {
        if (id == null || id < 1) {
            throw new IllegalArgumentException("Invalid school id");
        }
        return new School(id, name, address, district);
    }

    private static String validateText(String value, String fieldName, int maxLength) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
        if (value.length() > maxLength) {
            throw new IllegalArgumentException(fieldName + " cannot exceed " + maxLength + " characters");
        }
        return value;
    }
}
