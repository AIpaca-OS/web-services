package com.aipaca.rumbo.platform.billing.interfaces.rest;

import com.aipaca.rumbo.platform.billing.application.commandservices.SubscriptionCommandService;
import com.aipaca.rumbo.platform.billing.application.queryservices.SubscriptionQueryService;
import com.aipaca.rumbo.platform.billing.domain.model.queries.GetDriverSubscriptionsQuery;
import com.aipaca.rumbo.platform.billing.interfaces.rest.resources.ActivateSubscriptionResource;
import com.aipaca.rumbo.platform.billing.interfaces.rest.resources.SubscriptionResource;
import com.aipaca.rumbo.platform.billing.interfaces.rest.transform.ActivateSubscriptionCommandFromResourceAssembler;
import com.aipaca.rumbo.platform.billing.interfaces.rest.transform.SubscriptionResourceFromEntityAssembler;
import com.aipaca.rumbo.platform.shared.application.result.ApplicationError;
import com.aipaca.rumbo.platform.shared.interfaces.rest.transform.ProblemDetailFromApplicationErrorAssembler;
import com.aipaca.rumbo.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@CrossOrigin(origins = "${app.cors.allowed-origins:*}")
@RequestMapping(value = "/api/v1/subscriptions",
        produces = APPLICATION_JSON_VALUE)
@Tag(name = "Subscriptions", description = "Driver subscription endpoints")
public class SubscriptionsController {

    private final SubscriptionCommandService commandService;
    private final SubscriptionQueryService queryService;

    public SubscriptionsController(
            SubscriptionCommandService commandService,
            SubscriptionQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @PostMapping
    @Operation(summary = "Activate a driver subscription")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Subscription activated",
                    content = @Content(schema =
                    @Schema(implementation = SubscriptionResource.class))),
            @ApiResponse(responseCode = "200",
                    description = "Existing active subscription returned",
                    content = @Content(schema =
                    @Schema(implementation = SubscriptionResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Plan not found"),
            @ApiResponse(responseCode = "409", description = "Plan cannot be activated")
    })
    public ResponseEntity<?> activate(
            @RequestBody ActivateSubscriptionResource resource) {

        var command = ActivateSubscriptionCommandFromResourceAssembler
                .toCommandFromResource(resource.driverId(), resource);

        var result = commandService.handle(command);

        var status = result.toOptional()
                .filter(SubscriptionCommandService.Activation::created)
                .isPresent() ? HttpStatus.CREATED : HttpStatus.OK;

        var today = LocalDate.now(ZoneId.of("America/Lima"));

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                activation -> SubscriptionResourceFromEntityAssembler
                        .toResourceFromEntity(activation.subscription(), today),
                status);
    }

    @GetMapping
    @Operation(summary = "Get subscriptions by driver ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Driver subscriptions retrieved"),
            @ApiResponse(responseCode = "400",
                    description = "A positive driverId is required")
    })
    public ResponseEntity<List<SubscriptionResource>> getSubscriptions(
            @Parameter(example = "1")
            @RequestParam("driverId") Long driverId) {

        var today = LocalDate.now(ZoneId.of("America/Lima"));

        var resources = queryService
                .handle(new GetDriverSubscriptionsQuery(driverId))
                .stream()
                .map(subscription -> SubscriptionResourceFromEntityAssembler
                        .toResourceFromEntity(subscription, today))
                .toList();

        return ResponseEntity.ok(resources);
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<ProblemDetail> handleInvalidRequest() {
        var error = ApplicationError.validationError(
                "request", "Positive driverId and planId are required");

        return ProblemDetailFromApplicationErrorAssembler
                .toResponseFromApplicationError(error);
    }
}