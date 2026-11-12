package com.aipaca.rumbo.platform.vehicles.domain.model.aggregates;

import com.aipaca.rumbo.platform.vehicles.domain.model.commands.CreateVehicleCommand;
import com.aipaca.rumbo.platform.vehicles.domain.model.valueobjects.DriverProfileId;
import com.aipaca.rumbo.platform.vehicles.domain.model.valueobjects.LicensePlate;
import com.aipaca.rumbo.platform.vehicles.domain.model.valueobjects.VehicleStatus;
import com.aipaca.rumbo.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.util.Objects;

/**
 * Vehicle aggregate root.
 *
 * <p>A vehicle registered by its owner driver. The driver is a profile of the Profiles bounded
 * context, so the vehicle keeps only its identity through {@link DriverProfileId}.
 * No JPA or persistence annotation is present here — those concerns live
 * exclusively in {@code VehiclePersistenceEntity}.</p>
 */
@Getter
public class Vehicle extends AbstractDomainAggregateRoot<Vehicle> {
    private Long id;
    private DriverProfileId driverProfileId;
    private String brand;
    private String model;
    private LicensePlate licensePlate;
    private Integer capacity;
    private Integer year;
    private VehicleStatus status;

    private Vehicle() {
    }

    /**
     * Registers a new vehicle as pending verification.
     *
     * @param command The {@link CreateVehicleCommand} instance
     */
    public Vehicle(CreateVehicleCommand command) {
        this.driverProfileId = new DriverProfileId(command.driverProfileId());
        updateInformation(command.brand(), command.model(), command.licensePlate(), command.capacity(), command.year());
        this.status = VehicleStatus.PENDING_VERIFICATION;
    }

    /**
     * Reconstitutes an existing vehicle from persistence state.
     *
     * @return The reconstituted Vehicle instance.
     */
    public static Vehicle reconstitute(Long id, DriverProfileId driverProfileId, String brand, String model, LicensePlate licensePlate,
                                       Integer capacity, Integer year, VehicleStatus status) {
        var vehicle = new Vehicle();
        vehicle.id = id;
        vehicle.driverProfileId = driverProfileId;
        vehicle.brand = brand;
        vehicle.model = model;
        vehicle.licensePlate = licensePlate;
        vehicle.capacity = capacity;
        vehicle.year = year;
        vehicle.status = status;
        return vehicle;
    }

    /**
     * Updates the declared information of the vehicle.
     */
    public Vehicle updateInformation(String brand, String model, String licensePlate, Integer capacity, Integer year) {
        this.brand = requireText(brand, "Brand", 80);
        this.model = requireText(model, "Model", 80);
        this.licensePlate = new LicensePlate(licensePlate);
        if (capacity == null || capacity < 1 || capacity > 80)
            throw new IllegalArgumentException("Capacity must be between 1 and 80");
        if (year == null || year < 1980 || year > 2100)
            throw new IllegalArgumentException("Year must be between 1980 and 2100");
        this.capacity = capacity;
        this.year = year;
        return this;
    }

    public Long getDriverProfileIdValue() {
        return driverProfileId.driverProfileId();
    }

    public String getLicensePlateValue() {
        return licensePlate.plate();
    }

    private static String requireText(String value, String field, int maxLength) {
        if (value == null || value.isBlank() || value.trim().length() > maxLength)
            throw new IllegalArgumentException(field + " is required and cannot exceed " + maxLength + " characters");
        return Objects.requireNonNull(value).trim();
    }
}
