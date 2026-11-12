package com.aipaca.rumbo.vehicles.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VehicleTests {
    @Test void plateIsNormalizedAndStatusIsPending() {
        var vehicle = new Vehicle("Toyota", "Hiace", "xyz-789", 15, 2023);
        assertEquals("XYZ-789", vehicle.getLicensePlate());
        assertEquals("PENDING_VERIFICATION", vehicle.getStatus());
    }

    @Test void invalidCapacityAndPlateAreRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Vehicle("Toyota", "Hiace", "XYZ-789", 0, 2023));
        assertThrows(IllegalArgumentException.class,
                () -> new Vehicle("Toyota", "Hiace", "   ", 15, 2023));
    }

    @Test void editingVehicleNeverMarksItAsVerified() {
        var vehicle = new Vehicle("Toyota", "Hiace", "XYZ-789", 15, 2023);
        vehicle.update("Toyota", "Coaster", "xyz-789", 20, 2024);
        assertEquals("PENDING_VERIFICATION", vehicle.getStatus());
        assertEquals("Coaster", vehicle.getModel());
    }
}
