package com.aipaca.rumbo.platform.vehicles.domain.model.valueobjects;

import java.util.Locale;

/**
 * Value object representing a vehicle license plate.
 *
 * <p>The plate is normalized without spaces and in upper case so that uniqueness
 * does not depend on how the user typed it.</p>
 *
 * @param plate The license plate. It cannot be null, blank or longer than 20 characters.
 */
public record LicensePlate(String plate) {
    public LicensePlate {
        if (plate == null || plate.isBlank()) {
            throw new IllegalArgumentException("License plate cannot be null or blank");
        }
        plate = plate.replace(" ", "").trim().toUpperCase(Locale.ROOT);
        if (plate.length() > 20) {
            throw new IllegalArgumentException("License plate cannot exceed 20 characters");
        }
    }
}
