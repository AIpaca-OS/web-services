package com.aipaca.rumbo.vehicles.application;

import com.aipaca.rumbo.routes.domain.repositories.RouteRepository;
import com.aipaca.rumbo.vehicles.domain.model.Vehicle;
import com.aipaca.rumbo.vehicles.domain.repositories.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class VehicleServiceTests {
    private VehicleRepository vehicleRepository;
    private RouteRepository routes;
    private VehicleService service;

    @BeforeEach void setUp() {
        vehicleRepository = mock(VehicleRepository.class);
        routes = mock(RouteRepository.class);
        service = new VehicleService(vehicleRepository, routes);
    }

    @Test void duplicatePlateReturnsConflict() {
        when(vehicleRepository.existsByLicensePlateIgnoreCase("XYZ-789")).thenReturn(true);
        var exception = assertThrows(ResponseStatusException.class,
                () -> service.create("Toyota", "Hiace", "xyz-789", 15, 2023));
        assertEquals(HttpStatus.CONFLICT.value(), exception.getStatusCode().value());
        verify(vehicleRepository, never()).save(any());
    }

    @Test void createVehiclePersistsPendingStatus() {
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(call -> call.getArgument(0));
        var vehicle = service.create("Toyota", "Hiace", "xyz-789", 15, 2023);
        assertEquals("PENDING_VERIFICATION", vehicle.getStatus());
        assertEquals("XYZ-789", vehicle.getLicensePlate());
        verify(vehicleRepository).save(any(Vehicle.class));
    }

    @Test void assignedVehicleCannotBeDeleted() {
        Vehicle vehicle = mock(Vehicle.class);
        when(vehicleRepository.findById(3L)).thenReturn(Optional.of(vehicle));
        when(routes.existsByVehicleId("3")).thenReturn(true);
        var exception = assertThrows(ResponseStatusException.class, () -> service.delete(3L));
        assertEquals(HttpStatus.CONFLICT.value(), exception.getStatusCode().value());
        verify(vehicleRepository, never()).delete(any(Vehicle.class));
    }

    @Test void unusedVehicleCanBeDeleted() {
        Vehicle vehicle = mock(Vehicle.class);
        when(vehicleRepository.findById(3L)).thenReturn(Optional.of(vehicle));
        service.delete(3L);
        verify(vehicleRepository).delete(vehicle);
    }

    @Test void missingVehicleReturnsNotFound() {
        var exception = assertThrows(ResponseStatusException.class, () -> service.findById(999L));
        assertEquals(HttpStatus.NOT_FOUND.value(), exception.getStatusCode().value());
    }
}
