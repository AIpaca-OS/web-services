package com.aipaca.rumbo.platform.vehicles.application.internal.commandservices;

import com.aipaca.rumbo.platform.vehicles.application.commandservices.VehicleCommandService;
import com.aipaca.rumbo.platform.vehicles.domain.model.aggregates.Vehicle;
import com.aipaca.rumbo.platform.vehicles.domain.model.commands.CreateVehicleCommand;
import com.aipaca.rumbo.platform.vehicles.domain.model.commands.DeleteVehicleCommand;
import com.aipaca.rumbo.platform.vehicles.domain.model.commands.UpdateVehicleCommand;
import com.aipaca.rumbo.platform.vehicles.domain.model.valueobjects.LicensePlate;
import com.aipaca.rumbo.platform.vehicles.domain.repositories.VehicleRepository;
import com.aipaca.rumbo.platform.shared.application.result.ApplicationError;
import com.aipaca.rumbo.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Vehicle Command Service Implementation
 */
@Service
@Transactional
public class VehicleCommandServiceImpl implements VehicleCommandService {
    private final VehicleRepository vehicleRepository;

    /**
     * Constructor
     *
     * @param vehicleRepository The {@link VehicleRepository} instance
     */
    public VehicleCommandServiceImpl(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    // inherited javadoc
    @Override
    public Result<Long, ApplicationError> handle(CreateVehicleCommand command) {
        try {
            var licensePlate = new LicensePlate(command.licensePlate());
            if (vehicleRepository.existsByLicensePlate(licensePlate)) {
                return Result.failure(ApplicationError.conflict("Vehicle",
                        "License plate '%s' is already registered".formatted(licensePlate.plate())));
            }
            var savedVehicle = vehicleRepository.save(new Vehicle(command));
            return Result.success(savedVehicle.getId());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("Vehicle", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("create-vehicle", e.getMessage()));
        }
    }

    // inherited javadoc
    @Override
    public Result<Long, ApplicationError> handle(UpdateVehicleCommand command) {
        var result = vehicleRepository.findById(command.vehicleId());
        if (result.isEmpty())
            return Result.failure(ApplicationError.notFound("Vehicle", command.vehicleId().toString()));
        try {
            var licensePlate = new LicensePlate(command.licensePlate());
            if (vehicleRepository.existsByLicensePlateAndIdIsNot(licensePlate, command.vehicleId())) {
                return Result.failure(ApplicationError.conflict("Vehicle",
                        "License plate '%s' is already registered".formatted(licensePlate.plate())));
            }
            var vehicle = result.get().updateInformation(command.brand(), command.model(), command.licensePlate(),
                    command.capacity(), command.year());
            return Result.success(vehicleRepository.save(vehicle).getId());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("Vehicle", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("update-vehicle", e.getMessage()));
        }
    }

    // inherited javadoc
    @Override
    public Result<Long, ApplicationError> handle(DeleteVehicleCommand command) {
        if (!vehicleRepository.existsById(command.vehicleId()))
            return Result.failure(ApplicationError.notFound("Vehicle", command.vehicleId().toString()));
        try {
            vehicleRepository.deleteById(command.vehicleId());
            return Result.success(command.vehicleId());
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("delete-vehicle", e.getMessage()));
        }
    }
}
