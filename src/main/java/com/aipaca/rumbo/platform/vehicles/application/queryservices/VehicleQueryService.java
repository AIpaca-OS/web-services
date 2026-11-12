package com.aipaca.rumbo.platform.vehicles.application.queryservices;

import com.aipaca.rumbo.platform.vehicles.domain.model.aggregates.Vehicle;
import com.aipaca.rumbo.platform.vehicles.domain.model.queries.GetAllVehiclesByDriverProfileIdQuery;
import com.aipaca.rumbo.platform.vehicles.domain.model.queries.GetAllVehiclesQuery;
import com.aipaca.rumbo.platform.vehicles.domain.model.queries.GetVehicleByIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Vehicle Query Service
 */
public interface VehicleQueryService {
    Optional<Vehicle> handle(GetVehicleByIdQuery query);

    List<Vehicle> handle(GetAllVehiclesQuery query);

    List<Vehicle> handle(GetAllVehiclesByDriverProfileIdQuery query);
}
