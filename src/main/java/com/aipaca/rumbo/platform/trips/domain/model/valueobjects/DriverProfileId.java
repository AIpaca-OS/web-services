package com.aipaca.rumbo.platform.trips.domain.model.valueobjects;

/**
 * Value object representing the driver profile id.
 *
 * <p>Identifies the driver that runs a trip, a profile of the Profiles bounded context.</p>
 *
 * @param driverProfileId The driver profile id. It cannot be null or less than 1.
 */
public record DriverProfileId(Long driverProfileId) {
    /**
     * Compact constructor for DriverProfileId.
     *
     * @throws IllegalArgumentException if the driver profile id is null or less than 1.
     */
    public DriverProfileId {
        if (driverProfileId == null || driverProfileId < 1) {
            throw new IllegalArgumentException("Driver profile id cannot be null or less than 1");
        }
    }
}
