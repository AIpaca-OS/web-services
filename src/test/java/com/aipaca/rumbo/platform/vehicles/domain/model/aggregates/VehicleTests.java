package com.aipaca.rumbo.platform.vehicles.domain.model.aggregates;

import com.aipaca.rumbo.platform.vehicles.domain.model.commands.CreateVehicleCommand;
import com.aipaca.rumbo.platform.vehicles.domain.model.valueobjects.LicensePlate;
import com.aipaca.rumbo.platform.vehicles.domain.model.valueobjects.VehicleStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VehicleTests {

    @Test
    void newVehicleIsPendingVerificationWithNormalizedPlate() {
        var vehicle = new Vehicle(new CreateVehicleCommand(2L, " Toyota ", "Hiace", " abc 123 ", 15, 2022));
        assertEquals("Toyota", vehicle.getBrand());
        assertEquals("ABC123", vehicle.getLicensePlateValue());
        assertEquals(VehicleStatus.PENDING_VERIFICATION, vehicle.getStatus());
        assertEquals(2L, vehicle.getDriverProfileIdValue());
        assertNull(vehicle.getId());
        assertThrows(IllegalArgumentException.class,
                () -> new CreateVehicleCommand(null, "Toyota", "Hiace", "ABC123", 15, 2022));
    }

    @Test
    void capacityYearAndTextAreValidated() {
        assertThrows(IllegalArgumentException.class,
                () -> new Vehicle(new CreateVehicleCommand(2L, "Toyota", "Hiace", "ABC123", 0, 2022)));
        assertThrows(IllegalArgumentException.class,
                () -> new Vehicle(new CreateVehicleCommand(2L, "Toyota", "Hiace", "ABC123", 15, 1979)));
        assertThrows(IllegalArgumentException.class,
                () -> new Vehicle(new CreateVehicleCommand(2L, " ", "Hiace", "ABC123", 15, 2022)));
        assertThrows(IllegalArgumentException.class, () -> new LicensePlate("X".repeat(21)));
    }
}
