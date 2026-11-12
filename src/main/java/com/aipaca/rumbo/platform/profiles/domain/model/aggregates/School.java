package com.aipaca.rumbo.platform.profiles.domain.model.aggregates;

import com.aipaca.rumbo.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

/**
 * School aggregate root.
 *
 * <p>
 * Read-only catalog entry referenced by students through its identity.
 * No JPA or persistence annotation is present here — those concerns live
 * exclusively in {@code SchoolPersistenceEntity}.
 * </p>
 */
@Getter
public class School extends AbstractDomainAggregateRoot<School> {
    /**
     * The unique identifier for the school.
     */
    private Long id;

    /**
     * The school name.
     */
    private String name;

    /**
     * The school address.
     */
    private String address;

    /**
     * The school district.
     */
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

    /**
     * Creates a school whose identity will be assigned by persistence.
     *
     * @param name     The school name.
     * @param address  The school address.
     * @param district The school district.
     */
    public School(String name, String address, String district) {
        this(null, name, address, district);
    }

    /**
     * Reconstitutes an existing school from persistence state.
     *
     * @param id       The unique identifier.
     * @param name     The school name.
     * @param address  The school address.
     * @param district The school district.
     * @return The reconstituted School instance.
     */
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
