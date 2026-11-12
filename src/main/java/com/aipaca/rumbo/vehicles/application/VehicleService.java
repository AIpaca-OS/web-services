package com.aipaca.rumbo.vehicles.application;

import com.aipaca.rumbo.routes.domain.repositories.RouteRepository;
import com.aipaca.rumbo.vehicles.domain.model.Vehicle;
import com.aipaca.rumbo.vehicles.domain.repositories.VehicleRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class VehicleService {
    private final VehicleRepository vehicles;
    private final RouteRepository routes;

    public VehicleService(VehicleRepository vehicles, RouteRepository routes) {
        this.vehicles = vehicles;
        this.routes = routes;
    }

    @Transactional(readOnly = true)
    public List<Vehicle> findAll() {
        return vehicles.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    @Transactional(readOnly = true)
    public Vehicle findById(Long id) {
        return vehicles.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found"));
    }

    @Transactional
    public Vehicle create(String brand, String model, String plate, Integer capacity, Integer year) {
        String normalized = Vehicle.normalizePlate(plate);
        if (vehicles.existsByLicensePlateIgnoreCase(normalized))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "License plate already registered");
        return vehicles.save(new Vehicle(brand, model, normalized, capacity, year));
    }

    @Transactional
    public Vehicle update(Long id, String brand, String model, String plate, Integer capacity, Integer year) {
        Vehicle vehicle = findById(id);
        String normalized = Vehicle.normalizePlate(plate);
        if (vehicles.existsByLicensePlateIgnoreCaseAndIdNot(normalized, id))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "License plate already registered");
        vehicle.update(brand, model, normalized, capacity, year);
        return vehicles.save(vehicle);
    }

    @Transactional
    public void delete(Long id) {
        Vehicle vehicle = findById(id);
        if (routes.existsByVehicleId(String.valueOf(id)))
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Vehicle is assigned to a route and cannot be deleted");
        vehicles.delete(vehicle);
    }
}
