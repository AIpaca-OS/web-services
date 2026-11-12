package com.aipaca.rumbo.platform.vehicles.application.internal.commandservices;

import com.aipaca.rumbo.platform.shared.application.result.ApplicationError;
import com.aipaca.rumbo.platform.shared.application.result.Result;
import com.aipaca.rumbo.platform.vehicles.domain.model.aggregates.Vehicle;
import com.aipaca.rumbo.platform.vehicles.domain.model.commands.CreateVehicleCommand;
import com.aipaca.rumbo.platform.vehicles.domain.model.commands.DeleteVehicleCommand;
import com.aipaca.rumbo.platform.vehicles.domain.model.valueobjects.LicensePlate;
import com.aipaca.rumbo.platform.vehicles.domain.repositories.VehicleRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class VehicleCommandServiceImplTests {
    private final VehicleRepository vehicles = mock(VehicleRepository.class);
    private final VehicleCommandServiceImpl service = new VehicleCommandServiceImpl(vehicles);

    private static String errorCode(Result<Long, ApplicationError> result) {
        var failure = assertInstanceOf(Result.Failure.class, result);
        return ((ApplicationError) failure.error()).code();
    }

    @Test
    void duplicatedPlateIsAConflictRegardlessOfFormat() {
        when(vehicles.existsByLicensePlate(new LicensePlate("ABC123"))).thenReturn(true);
        var result = service.handle(new CreateVehicleCommand(2L, "Toyota", "Hiace", "abc 123", 15, 2022));
        assertEquals("VEHICLE_CONFLICT", errorCode(result));
        verify(vehicles, never()).save(any());
    }

    @Test
    void invalidDataIsAValidationError() {
        var result = service.handle(new CreateVehicleCommand(2L, "Toyota", "Hiace", "ABC123", 100, 2022));
        assertEquals("VALIDATION_ERROR", errorCode(result));
    }

    @Test
    void validVehicleReturnsTheGeneratedId() {
        when(vehicles.save(any())).thenAnswer(invocation -> {
            Vehicle vehicle = invocation.getArgument(0);
            return Vehicle.reconstitute(7L, vehicle.getDriverProfileId(), vehicle.getBrand(), vehicle.getModel(), vehicle.getLicensePlate(),
                    vehicle.getCapacity(), vehicle.getYear(), vehicle.getStatus());
        });
        var result = service.handle(new CreateVehicleCommand(2L, "Toyota", "Hiace", "ABC123", 15, 2022));
        assertEquals(7L, assertInstanceOf(Result.Success.class, result).value());
    }

    @Test
    void deletingAMissingVehicleIsNotFound() {
        when(vehicles.existsById(9L)).thenReturn(false);
        assertEquals("VEHICLE_NOT_FOUND", errorCode(service.handle(new DeleteVehicleCommand(9L))));
        verify(vehicles, never()).deleteById(any());
    }
}
