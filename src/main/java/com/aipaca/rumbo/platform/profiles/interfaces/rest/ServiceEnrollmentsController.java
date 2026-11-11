package com.aipaca.rumbo.platform.profiles.interfaces.rest;

import com.aipaca.rumbo.platform.profiles.application.commandservices.ServiceEnrollmentCommandService;
import com.aipaca.rumbo.platform.profiles.application.queryservices.ServiceEnrollmentQueryService;
import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.ServiceEnrollment;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.AcceptServiceEnrollmentCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.CancelServiceEnrollmentCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.EndServiceEnrollmentCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.GetAllServiceEnrollmentsByDriverIdQuery;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.GetAllServiceEnrollmentsByStudentIdQuery;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.GetAllStudentsByDriverIdQuery;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.GetServiceEnrollmentByIdQuery;
import com.aipaca.rumbo.platform.profiles.interfaces.rest.resources.DriverStudentResource;
import com.aipaca.rumbo.platform.profiles.interfaces.rest.resources.RequestEnrollmentResource;
import com.aipaca.rumbo.platform.profiles.interfaces.rest.resources.ServiceEnrollmentResource;
import com.aipaca.rumbo.platform.profiles.interfaces.rest.transform.DriverStudentResourceFromEntityAssembler;
import com.aipaca.rumbo.platform.profiles.interfaces.rest.transform.RequestServiceEnrollmentCommandFromResourceAssembler;
import com.aipaca.rumbo.platform.profiles.interfaces.rest.transform.ServiceEnrollmentResourceFromEntityAssembler;
import com.aipaca.rumbo.platform.shared.application.result.ApplicationError;
import com.aipaca.rumbo.platform.shared.application.result.Result;
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

import java.util.List;

/**
 * REST controller that exposes the enrollment of students in the service of a driver.
 */
@RestController
@RequestMapping(value = "/api/v1/service-enrollments", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Service Enrollments", description = "Student-driver service enrollment endpoints")
public class ServiceEnrollmentsController {
    private final ServiceEnrollmentCommandService serviceEnrollmentCommandService;
    private final ServiceEnrollmentQueryService serviceEnrollmentQueryService;

    /**
     * Constructor
     *
     * @param serviceEnrollmentCommandService The {@link ServiceEnrollmentCommandService} instance
     * @param serviceEnrollmentQueryService   The {@link ServiceEnrollmentQueryService} instance
     */
    public ServiceEnrollmentsController(ServiceEnrollmentCommandService serviceEnrollmentCommandService,
                                        ServiceEnrollmentQueryService serviceEnrollmentQueryService) {
        this.serviceEnrollmentCommandService = serviceEnrollmentCommandService;
        this.serviceEnrollmentQueryService = serviceEnrollmentQueryService;
    }

    /**
     * Request a service enrollment
     *
     * @param resource The {@link RequestEnrollmentResource} instance
     * @return A {@link ServiceEnrollmentResource} resource for the requested enrollment
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Request an enrollment", description = "An authorized tutor requests the enrollment of an active student with a driver.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Enrollment requested",
                    content = @Content(schema = @Schema(implementation = ServiceEnrollmentResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data or profiles"),
            @ApiResponse(responseCode = "404", description = "Student or driver not found"),
            @ApiResponse(responseCode = "409", description = "A pending or accepted enrollment already exists"),
            @ApiResponse(responseCode = "422", description = "The requester is not an authorized tutor of an active student")
    })
    public ResponseEntity<?> requestServiceEnrollment(@Valid @RequestBody RequestEnrollmentResource resource) {
        var command = RequestServiceEnrollmentCommandFromResourceAssembler.toCommandFromResource(resource);
        return toEnrollmentResponse(serviceEnrollmentCommandService.handle(command), HttpStatus.CREATED);
    }

    /**
     * Get a service enrollment by ID
     *
     * @param enrollmentId The enrollment ID
     * @return A {@link ServiceEnrollmentResource} resource for the enrollment
     */
    @GetMapping("/{enrollmentId}")
    @Operation(summary = "Get enrollment by ID", description = "Retrieves a service enrollment by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Enrollment found",
                    content = @Content(schema = @Schema(implementation = ServiceEnrollmentResource.class))),
            @ApiResponse(responseCode = "404", description = "Enrollment not found")
    })
    public ResponseEntity<?> getServiceEnrollmentById(
            @PathVariable @Parameter(description = "Enrollment unique identifier", example = "1", required = true)
            Long enrollmentId) {
        var enrollment = serviceEnrollmentQueryService.handle(new GetServiceEnrollmentByIdQuery(enrollmentId));
        if (enrollment.isEmpty()) {
            var error = ApplicationError.notFound("Enrollment", enrollmentId.toString());
            return ProblemDetailFromApplicationErrorAssembler.toResponseFromApplicationError(error);
        }
        return ResponseEntity.ok(ServiceEnrollmentResourceFromEntityAssembler.toResourceFromEntity(enrollment.get()));
    }

    /**
     * Get the enrollments of a driver
     *
     * @param driverProfileId The driver profile ID
     * @return A list of {@link ServiceEnrollmentResource} resources
     */
    @GetMapping("/drivers/{driverProfileId}")
    @Operation(summary = "Get enrollments by driver", description = "Retrieves every enrollment of a driver.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Enrollments found",
                    content = @Content(schema = @Schema(implementation = ServiceEnrollmentResource.class)))
    })
    public ResponseEntity<List<ServiceEnrollmentResource>> getServiceEnrollmentsByDriverProfileId(
            @PathVariable @Parameter(description = "Driver profile unique identifier", example = "3", required = true)
            Long driverProfileId) {
        var enrollments = serviceEnrollmentQueryService.handle(new GetAllServiceEnrollmentsByDriverIdQuery(driverProfileId));
        return ResponseEntity.ok(enrollments.stream()
                .map(ServiceEnrollmentResourceFromEntityAssembler::toResourceFromEntity).toList());
    }

    /**
     * Get the students of a driver
     *
     * @param driverProfileId The driver profile ID
     * @return A list of {@link DriverStudentResource} resources
     */
    @GetMapping("/drivers/{driverProfileId}/students")
    @Operation(summary = "Get students by driver", description = "Retrieves the active students with a pending or accepted enrollment with the driver.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Students found",
                    content = @Content(schema = @Schema(implementation = DriverStudentResource.class)))
    })
    public ResponseEntity<List<DriverStudentResource>> getStudentsByDriverProfileId(
            @PathVariable @Parameter(description = "Driver profile unique identifier", example = "3", required = true)
            Long driverProfileId) {
        var students = serviceEnrollmentQueryService.handle(new GetAllStudentsByDriverIdQuery(driverProfileId));
        return ResponseEntity.ok(students.stream()
                .map(DriverStudentResourceFromEntityAssembler::toResourceFromEntity).toList());
    }

    /**
     * Get the enrollments of a student
     *
     * @param studentId The student ID
     * @return A list of {@link ServiceEnrollmentResource} resources
     */
    @GetMapping("/students/{studentId}")
    @Operation(summary = "Get enrollments by student", description = "Retrieves every enrollment of a student.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Enrollments found",
                    content = @Content(schema = @Schema(implementation = ServiceEnrollmentResource.class)))
    })
    public ResponseEntity<List<ServiceEnrollmentResource>> getServiceEnrollmentsByStudentId(
            @PathVariable @Parameter(description = "Student unique identifier", example = "1", required = true)
            Long studentId) {
        var enrollments = serviceEnrollmentQueryService.handle(new GetAllServiceEnrollmentsByStudentIdQuery(studentId));
        return ResponseEntity.ok(enrollments.stream()
                .map(ServiceEnrollmentResourceFromEntityAssembler::toResourceFromEntity).toList());
    }

    /**
     * Accept a pending enrollment
     *
     * @param enrollmentId    The enrollment ID
     * @param driverProfileId The driver profile ID of the enrollment
     * @return A {@link ServiceEnrollmentResource} resource for the accepted enrollment
     */
    @PostMapping("/{enrollmentId}/accept")
    @Operation(summary = "Accept enrollment", description = "The driver of the enrollment accepts a pending enrollment.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Enrollment accepted",
                    content = @Content(schema = @Schema(implementation = ServiceEnrollmentResource.class))),
            @ApiResponse(responseCode = "404", description = "Enrollment not found"),
            @ApiResponse(responseCode = "409", description = "The driver does not match the enrollment"),
            @ApiResponse(responseCode = "422", description = "The enrollment is not pending")
    })
    public ResponseEntity<?> acceptServiceEnrollment(
            @PathVariable @Parameter(description = "Enrollment unique identifier", example = "1", required = true)
            Long enrollmentId,
            @RequestParam @Parameter(description = "Driver profile id of the enrollment", example = "3", required = true)
            Long driverProfileId) {
        var command = new AcceptServiceEnrollmentCommand(enrollmentId, driverProfileId);
        return toEnrollmentResponse(serviceEnrollmentCommandService.handle(command), HttpStatus.OK);
    }

    /**
     * Cancel a pending enrollment
     *
     * @param enrollmentId         The enrollment ID
     * @param requestedByProfileId The profile ID that requested the enrollment
     * @return A {@link ServiceEnrollmentResource} resource for the cancelled enrollment
     */
    @PostMapping("/{enrollmentId}/cancel")
    @Operation(summary = "Cancel enrollment", description = "The requester cancels a pending enrollment.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Enrollment cancelled",
                    content = @Content(schema = @Schema(implementation = ServiceEnrollmentResource.class))),
            @ApiResponse(responseCode = "404", description = "Enrollment not found"),
            @ApiResponse(responseCode = "422", description = "The cancellation is not allowed")
    })
    public ResponseEntity<?> cancelServiceEnrollment(
            @PathVariable @Parameter(description = "Enrollment unique identifier", example = "1", required = true)
            Long enrollmentId,
            @RequestParam @Parameter(description = "Profile id that requested the enrollment", example = "1", required = true)
            Long requestedByProfileId) {
        var command = new CancelServiceEnrollmentCommand(enrollmentId, requestedByProfileId);
        return toEnrollmentResponse(serviceEnrollmentCommandService.handle(command), HttpStatus.OK);
    }

    /**
     * End an accepted enrollment
     *
     * @param enrollmentId         The enrollment ID
     * @param requestedByProfileId The profile ID of the driver or an authorized tutor
     * @return A {@link ServiceEnrollmentResource} resource for the ended enrollment
     */
    @PostMapping("/{enrollmentId}/end")
    @Operation(summary = "End enrollment", description = "The driver or an authorized tutor ends an accepted enrollment.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Enrollment ended",
                    content = @Content(schema = @Schema(implementation = ServiceEnrollmentResource.class))),
            @ApiResponse(responseCode = "404", description = "Enrollment or profile not found"),
            @ApiResponse(responseCode = "422", description = "The termination is not allowed")
    })
    public ResponseEntity<?> endServiceEnrollment(
            @PathVariable @Parameter(description = "Enrollment unique identifier", example = "1", required = true)
            Long enrollmentId,
            @RequestParam @Parameter(description = "Profile id of the driver or an authorized tutor", example = "3", required = true)
            Long requestedByProfileId) {
        var command = new EndServiceEnrollmentCommand(enrollmentId, requestedByProfileId);
        return toEnrollmentResponse(serviceEnrollmentCommandService.handle(command), HttpStatus.OK);
    }

    private ResponseEntity<?> toEnrollmentResponse(Result<Long, ApplicationError> commandResult, HttpStatus status) {
        var result = commandResult.flatMap(id -> serviceEnrollmentQueryService.handle(new GetServiceEnrollmentByIdQuery(id))
                .<Result<ServiceEnrollment, ApplicationError>>map(Result::success)
                .orElseGet(() -> Result.failure(ApplicationError.notFound("Enrollment", id.toString()))));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, ServiceEnrollmentResourceFromEntityAssembler::toResourceFromEntity, status);
    }
}
