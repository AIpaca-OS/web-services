package com.aipaca.rumbo.platform.vehicles.application.internal.queryservices;

import com.aipaca.rumbo.platform.vehicles.application.queryservices.VehicleQueryService;
import com.aipaca.rumbo.platform.vehicles.domain.model.aggregates.Vehicle;
import com.aipaca.rumbo.platform.vehicles.domain.model.queries.GetAllVehiclesByDriverProfileIdQuery;
import com.aipaca.rumbo.platform.vehicles.domain.model.queries.GetAllVehiclesQuery;
import com.aipaca.rumbo.platform.vehicles.domain.model.queries.GetVehicleByIdQuery;
import com.aipaca.rumbo.platform.vehicles.domain.model.valueobjects.DriverProfileId;
import com.aipaca.rumbo.platform.vehicles.domain.repositories.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Vehicle Query Service Implementation
 */
@Service
@Transactional(readOnly = true)
public class VehicleQueryServiceImpl implements VehicleQueryService {
    private final VehicleRepository vehicleRepository;

    public VehicleQueryServiceImpl(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    // inherited javadoc
    @Override
    public Optional<Vehicle> handle(GetVehicleByIdQuery query) {
        return vehicleRepository.findById(query.vehicleId());
    }

    // inherited javadoc
    @Override
    public List<Vehicle> handle(GetAllVehiclesQuery query) {
        return vehicleRepository.findAll();
    }

    // inherited javadoc
    @Override
    public List<Vehicle> handle(GetAllVehiclesByDriverProfileIdQuery query) {
        return vehicleRepository.findAllByDriverProfileId(new DriverProfileId(query.driverProfileId()));
    }
}
