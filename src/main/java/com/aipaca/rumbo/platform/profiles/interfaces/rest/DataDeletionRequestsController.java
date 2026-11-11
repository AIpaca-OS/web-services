package com.aipaca.rumbo.platform.profiles.interfaces.rest;

import com.aipaca.rumbo.platform.profiles.application.commandservices.DataDeletionRequestCommandService;
import com.aipaca.rumbo.platform.profiles.application.queryservices.DataDeletionRequestQueryService;
import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.DataDeletionRequest;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.GetAllDataDeletionRequestsByRequestedByProfileIdQuery;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.GetDataDeletionRequestByIdQuery;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.DeletionRequestStatus;
import com.aipaca.rumbo.platform.profiles.interfaces.rest.resources.DataDeletionRequestResource;
import com.aipaca.rumbo.platform.profiles.interfaces.rest.resources.RequestDeletionResource;
import com.aipaca.rumbo.platform.profiles.interfaces.rest.resources.ResolveDeletionResource;
import com.aipaca.rumbo.platform.profiles.interfaces.rest.transform.DataDeletionRequestResourceFromEntityAssembler;
import com.aipaca.rumbo.platform.profiles.interfaces.rest.transform.RequestStudentDataDeletionCommandFromResourceAssembler;
import com.aipaca.rumbo.platform.profiles.interfaces.rest.transform.ResolveDataDeletionRequestCommandFromResourceAssembler;
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
 * REST controller that exposes the requests to delete the personal data of a student.
 */
@RestController
@RequestMapping(value = "/api/v1/data-deletion-requests", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Data Deletion Requests", description = "Student personal-data deletion request endpoints")
public class DataDeletionRequestsController {
    private final DataDeletionRequestCommandService dataDeletionRequestCommandService;
    private final DataDeletionRequestQueryService dataDeletionRequestQueryService;

    /**
     * Constructor
     *
     * @param dataDeletionRequestCommandService The {@link DataDeletionRequestCommandService} instance
     * @param dataDeletionRequestQueryService   The {@link DataDeletionRequestQueryService} instance
     */
    public DataDeletionRequestsController(DataDeletionRequestCommandService dataDeletionRequestCommandService,
                                          DataDeletionRequestQueryService dataDeletionRequestQueryService) {
        this.dataDeletionRequestCommandService = dataDeletionRequestCommandService;
        this.dataDeletionRequestQueryService = dataDeletionRequestQueryService;
    }

    /**
     * Request the deletion of the data of a student
     *
     * @param resource The {@link RequestDeletionResource} instance
     * @return A {@link DataDeletionRequestResource} resource for the pending request
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Request data deletion", description = "An authorized tutor requests the deletion of the personal data of a student.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Request created",
                    content = @Content(schema = @Schema(implementation = DataDeletionRequestResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data or requester profile"),
            @ApiResponse(responseCode = "404", description = "Student not found"),
            @ApiResponse(responseCode = "422", description = "The requester is not an authorized tutor")
    })
    public ResponseEntity<?> requestDataDeletion(@Valid @RequestBody RequestDeletionResource resource) {
        var command = RequestStudentDataDeletionCommandFromResourceAssembler.toCommandFromResource(resource);
        return toRequestResponse(dataDeletionRequestCommandService.handle(command), HttpStatus.CREATED);
    }

    /**
     * Get the requests made by a profile
     *
     * @param requestedByProfileId The requester profile ID
     * @return A list of {@link DataDeletionRequestResource} resources
     */
    @GetMapping("/by-requester/{requestedByProfileId}")
    @Operation(summary = "Get requests by requester", description = "Retrieves the data deletion requests made by a profile.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Requests found",
                    content = @Content(schema = @Schema(implementation = DataDeletionRequestResource.class)))
    })
    public ResponseEntity<List<DataDeletionRequestResource>> getDataDeletionRequestsByRequester(
            @PathVariable @Parameter(description = "Requester profile unique identifier", example = "1", required = true)
            Long requestedByProfileId) {
        var requests = dataDeletionRequestQueryService.handle(
                new GetAllDataDeletionRequestsByRequestedByProfileIdQuery(requestedByProfileId));
        return ResponseEntity.ok(requests.stream()
                .map(DataDeletionRequestResourceFromEntityAssembler::toResourceFromEntity).toList());
    }

    /**
     * Get a request by ID
     *
     * @param requestId The request ID
     * @return A {@link DataDeletionRequestResource} resource for the request
     */
    @GetMapping("/{requestId}")
    @Operation(summary = "Get request by ID", description = "Retrieves a data deletion request by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request found",
                    content = @Content(schema = @Schema(implementation = DataDeletionRequestResource.class))),
            @ApiResponse(responseCode = "404", description = "Request not found")
    })
    public ResponseEntity<?> getDataDeletionRequestById(
            @PathVariable @Parameter(description = "Request unique identifier", example = "1", required = true)
            Long requestId) {
        var request = dataDeletionRequestQueryService.handle(new GetDataDeletionRequestByIdQuery(requestId));
        if (request.isEmpty()) {
            var error = ApplicationError.notFound("Deletion_Request", requestId.toString());
            return ProblemDetailFromApplicationErrorAssembler.toResponseFromApplicationError(error);
        }
        return ResponseEntity.ok(DataDeletionRequestResourceFromEntityAssembler.toResourceFromEntity(request.get()));
    }

    /**
     * Reject a pending request
     *
     * @param requestId The request ID
     * @param resource  The {@link ResolveDeletionResource} instance
     * @return A {@link DataDeletionRequestResource} resource for the rejected request
     */
    @PostMapping(value = "/{requestId}/reject", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Reject request", description = "Rejects a pending data deletion request.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request rejected",
                    content = @Content(schema = @Schema(implementation = DataDeletionRequestResource.class))),
            @ApiResponse(responseCode = "404", description = "Request not found"),
            @ApiResponse(responseCode = "422", description = "The request is not pending")
    })
    public ResponseEntity<?> rejectDataDeletionRequest(
            @PathVariable @Parameter(description = "Request unique identifier", example = "1", required = true)
            Long requestId,
            @Valid @RequestBody ResolveDeletionResource resource) {
        var command = ResolveDataDeletionRequestCommandFromResourceAssembler.toCommandFromResource(
                requestId, DeletionRequestStatus.REJECTED, resource);
        return toRequestResponse(dataDeletionRequestCommandService.handle(command), HttpStatus.OK);
    }

    /**
     * Approve a pending request
     *
     * @param requestId The request ID
     * @param resource  The {@link ResolveDeletionResource} instance
     * @return A {@link DataDeletionRequestResource} resource for the approved request
     */
    @PostMapping(value = "/{requestId}/approve", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Approve request", description = "Approval is declined until trip verification and personal-data erasure exist.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request approved",
                    content = @Content(schema = @Schema(implementation = DataDeletionRequestResource.class))),
            @ApiResponse(responseCode = "404", description = "Request not found"),
            @ApiResponse(responseCode = "409", description = "Approval is not available yet")
    })
    public ResponseEntity<?> approveDataDeletionRequest(
            @PathVariable @Parameter(description = "Request unique identifier", example = "1", required = true)
            Long requestId,
            @Valid @RequestBody ResolveDeletionResource resource) {
        var command = ResolveDataDeletionRequestCommandFromResourceAssembler.toCommandFromResource(
                requestId, DeletionRequestStatus.APPROVED, resource);
        return toRequestResponse(dataDeletionRequestCommandService.handle(command), HttpStatus.OK);
    }

    private ResponseEntity<?> toRequestResponse(Result<Long, ApplicationError> commandResult, HttpStatus status) {
        var result = commandResult.flatMap(id -> dataDeletionRequestQueryService.handle(new GetDataDeletionRequestByIdQuery(id))
                .<Result<DataDeletionRequest, ApplicationError>>map(Result::success)
                .orElseGet(() -> Result.failure(ApplicationError.notFound("Deletion_Request", id.toString()))));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, DataDeletionRequestResourceFromEntityAssembler::toResourceFromEntity, status);
    }
}
