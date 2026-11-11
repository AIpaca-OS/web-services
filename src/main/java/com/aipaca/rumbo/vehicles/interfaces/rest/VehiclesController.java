package com.aipaca.rumbo.vehicles.interfaces.rest;

import com.aipaca.rumbo.vehicles.application.VehicleService;
import com.aipaca.rumbo.vehicles.domain.model.Vehicle;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Local-only fleet prototype. Disabled by default until IAM is integrated.
 * Setting APP_DEMO_ENABLED=true exposes this controller without authentication:
 * NEVER enable it in a public production environment.
 */
@RestController
@RequestMapping(value = "/api/v1/vehicles", produces = MediaType.APPLICATION_JSON_VALUE)
@ConditionalOnProperty(prefix = "app.demo", name = "enabled", havingValue = "true")
@Tag(name = "Vehicles", description = "Local demonstration CRUD; verification pending")
public class VehiclesController {
    private final VehicleService vehicles;

    public VehiclesController(VehicleService vehicles) {
        this.vehicles = vehicles;
    }

    public record VehicleInput(
            Long id,
            @NotBlank String brand,
            @NotBlank String model,
            @NotBlank String licensePlate,
            @NotNull @Min(1) @Max(80) Integer capacity,
            @NotNull @Min(1980) @Max(2100) Integer year,
            String status
    ) {}

    public record VehicleResource(
            Long id, String brand, String model, String licensePlate,
            Integer capacity, Integer year, String status
    ) {}

    private static VehicleResource toResource(Vehicle vehicle) {
        return new VehicleResource(vehicle.getId(), vehicle.getBrand(), vehicle.getModel(),
                vehicle.getLicensePlate(), vehicle.getCapacity(), vehicle.getYear(), vehicle.getStatus());
    }

    @GetMapping
    @Operation(summary = "List registered vehicles (local demo)")
    public List<VehicleResource> list() {
        return vehicles.findAll().stream().map(VehiclesController::toResource).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a vehicle by ID (local demo)")
    public VehicleResource get(@PathVariable Long id) {
        return toResource(vehicles.findById(id));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Register a vehicle as pending verification (local demo)")
    public ResponseEntity<VehicleResource> create(@Valid @RequestBody VehicleInput input) {
        Vehicle vehicle = vehicles.create(input.brand(), input.model(), input.licensePlate(),
                input.capacity(), input.year());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResource(vehicle));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update declared vehicle details (local demo)")
    public VehicleResource update(@PathVariable Long id, @Valid @RequestBody VehicleInput input) {
        return toResource(vehicles.update(id, input.brand(), input.model(), input.licensePlate(),
                input.capacity(), input.year()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a vehicle not assigned to any route (local demo)")
    public void delete(@PathVariable Long id) {
        vehicles.delete(id);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> invalidRequest(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> duplicatePlate(DataIntegrityViolationException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "License plate already registered"));
    }
}
