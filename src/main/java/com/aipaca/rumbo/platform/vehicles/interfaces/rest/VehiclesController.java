package com.aipaca.rumbo.platform.vehicles.interfaces.rest;

import com.aipaca.rumbo.platform.vehicles.application.commandservices.VehicleCommandService;
import com.aipaca.rumbo.platform.vehicles.application.queryservices.VehicleQueryService;
import com.aipaca.rumbo.platform.vehicles.domain.model.aggregates.Vehicle;
import com.aipaca.rumbo.platform.vehicles.domain.model.commands.DeleteVehicleCommand;
import com.aipaca.rumbo.platform.vehicles.domain.model.queries.GetAllVehiclesByDriverProfileIdQuery;
import com.aipaca.rumbo.platform.vehicles.domain.model.queries.GetAllVehiclesQuery;
import com.aipaca.rumbo.platform.vehicles.domain.model.queries.GetVehicleByIdQuery;
import com.aipaca.rumbo.platform.vehicles.interfaces.rest.resources.CreateVehicleResource;
import com.aipaca.rumbo.platform.vehicles.interfaces.rest.resources.UpdateVehicleResource;
import com.aipaca.rumbo.platform.vehicles.interfaces.rest.resources.VehicleResource;
import com.aipaca.rumbo.platform.vehicles.interfaces.rest.transform.CreateVehicleCommandFromResourceAssembler;
import com.aipaca.rumbo.platform.vehicles.interfaces.rest.transform.UpdateVehicleCommandFromResourceAssembler;
import com.aipaca.rumbo.platform.vehicles.interfaces.rest.transform.VehicleResourceFromEntityAssembler;
import com.aipaca.rumbo.platform.shared.application.result.ApplicationError;
import com.aipaca.rumbo.platform.shared.application.result.Result;
import com.aipaca.rumbo.platform.shared.interfaces.rest.resources.MessageResource;
import com.aipaca.rumbo.platform.shared.interfaces.rest.transform.ProblemDetailFromApplicationErrorAssembler;
import com.aipaca.rumbo.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.util.List;

/**
 * REST controller that exposes vehicle registration endpoints.
 */
@RestController
@ConditionalOnProperty(prefix = "app.platform", name = "vehicles-enabled", havingValue = "true")
@CrossOrigin(origins = "${app.cors.allowed-origins:*}")
@RequestMapping(value = "/api/v1/vehicles", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Vehicles", description = "Vehicle management endpoints")
public class VehiclesController {
    private final VehicleCommandService vehicleCommandService;
    private final VehicleQueryService vehicleQueryService;

    /**
     * Constructor
     *
     * @param vehicleCommandService The {@link VehicleCommandService} instance
     * @param vehicleQueryService   The {@link VehicleQueryService} instance
     */
    public VehiclesController(VehicleCommandService vehicleCommandService, VehicleQueryService vehicleQueryService) {
        this.vehicleCommandService = vehicleCommandService;
        this.vehicleQueryService = vehicleQueryService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Register a vehicle", description = "Registers a vehicle as pending verification.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Vehicle registered",
                    content = @Content(schema = @Schema(implementation = VehicleResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "License plate already registered")
    })
    public ResponseEntity<?> createVehicle(@Valid @RequestBody CreateVehicleResource resource) {
        var command = CreateVehicleCommandFromResourceAssembler.toCommandFromResource(resource);
        return toVehicleResponse(vehicleCommandService.handle(command), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get vehicles", description = "Retrieves every registered vehicle, or only those of a driver.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehicles found",
                    content = @Content(schema = @Schema(implementation = VehicleResource.class)))
    })
    public ResponseEntity<List<VehicleResource>> getAllVehicles(
            @RequestParam(required = false)
            @Parameter(description = "Driver profile unique identifier; when present, only the vehicles of that driver", example = "2")
            Long driverProfileId) {
        var vehicles = driverProfileId == null
                ? vehicleQueryService.handle(new GetAllVehiclesQuery())
                : vehicleQueryService.handle(new GetAllVehiclesByDriverProfileIdQuery(driverProfileId));
        return ResponseEntity.ok(vehicles.stream().map(VehicleResourceFromEntityAssembler::toResourceFromEntity).toList());
    }

    @GetMapping("/{vehicleId}")
    @Operation(summary = "Get vehicle by ID", description = "Retrieves a vehicle by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehicle found",
                    content = @Content(schema = @Schema(implementation = VehicleResource.class))),
            @ApiResponse(responseCode = "404", description = "Vehicle not found")
    })
    public ResponseEntity<?> getVehicleById(
            @PathVariable @Parameter(description = "Vehicle unique identifier", example = "1", required = true) Long vehicleId) {
        var vehicle = vehicleQueryService.handle(new GetVehicleByIdQuery(vehicleId));
        if (vehicle.isEmpty()) {
            var error = ApplicationError.notFound("Vehicle", vehicleId.toString());
            return ProblemDetailFromApplicationErrorAssembler.toResponseFromApplicationError(error);
        }
        return ResponseEntity.ok(VehicleResourceFromEntityAssembler.toResourceFromEntity(vehicle.get()));
    }

    @PutMapping(value = "/{vehicleId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update vehicle", description = "Updates the declared information of a vehicle.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehicle updated",
                    content = @Content(schema = @Schema(implementation = VehicleResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Vehicle not found"),
            @ApiResponse(responseCode = "409", description = "License plate already registered")
    })
    public ResponseEntity<?> updateVehicle(
            @PathVariable @Parameter(description = "Vehicle unique identifier", example = "1", required = true) Long vehicleId,
            @Valid @RequestBody UpdateVehicleResource resource) {
        var command = UpdateVehicleCommandFromResourceAssembler.toCommandFromResource(vehicleId, resource);
        return toVehicleResponse(vehicleCommandService.handle(command), HttpStatus.OK);
    }

    @DeleteMapping("/{vehicleId}")
    @Operation(summary = "Delete vehicle", description = "Deletes a vehicle.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehicle deleted"),
            @ApiResponse(responseCode = "404", description = "Vehicle not found")
    })
    public ResponseEntity<?> deleteVehicle(
            @PathVariable @Parameter(description = "Vehicle unique identifier", example = "1", required = true) Long vehicleId) {
        var result = vehicleCommandService.handle(new DeleteVehicleCommand(vehicleId))
                .map(id -> new MessageResource("Vehicle with given id successfully deleted"));
        return ResponseEntityAssembler.toResponseEntityFromResult(result, message -> message, HttpStatus.OK);
    }

    private ResponseEntity<?> toVehicleResponse(Result<Long, ApplicationError> commandResult, HttpStatus successStatus) {
        var result = commandResult.flatMap(vehicleId -> vehicleQueryService.handle(new GetVehicleByIdQuery(vehicleId))
                .<Result<Vehicle, ApplicationError>>map(Result::success)
                .orElseGet(() -> Result.failure(ApplicationError.notFound("Vehicle", vehicleId.toString()))));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, VehicleResourceFromEntityAssembler::toResourceFromEntity, successStatus);
    }
}
