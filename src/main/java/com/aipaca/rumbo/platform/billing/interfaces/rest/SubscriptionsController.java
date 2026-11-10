package com.aipaca.rumbo.platform.billing.interfaces.rest;

import com.aipaca.rumbo.platform.billing.application.commandservices.SubscriptionCommandService;
import com.aipaca.rumbo.platform.billing.application.internal.outboundservices.acl.BillingDriverAccess;
import com.aipaca.rumbo.platform.billing.application.queryservices.SubscriptionQueryService;
import com.aipaca.rumbo.platform.billing.domain.model.queries.GetDriverSubscriptionsQuery;
import com.aipaca.rumbo.platform.billing.interfaces.rest.resources.ActivateSubscriptionResource;
import com.aipaca.rumbo.platform.billing.interfaces.rest.transform.ActivateSubscriptionCommandFromResourceAssembler;
import com.aipaca.rumbo.platform.billing.interfaces.rest.transform.SubscriptionResourceFromEntityAssembler;
import com.aipaca.rumbo.platform.shared.application.result.ApplicationError;
import com.aipaca.rumbo.platform.shared.interfaces.rest.transform.ProblemDetailFromApplicationErrorAssembler;
import com.aipaca.rumbo.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;
import java.util.function.Function;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/subscriptions", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Subscriptions", description = "Driver subscription endpoints")
public class SubscriptionsController {

    private final SubscriptionCommandService commandService;
    private final SubscriptionQueryService queryService;
    private final Optional<BillingDriverAccess> driverAccess;

    public SubscriptionsController(SubscriptionCommandService commandService,
                                   SubscriptionQueryService queryService,
                                   Optional<BillingDriverAccess> driverAccess) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.driverAccess = driverAccess;
    }

    @PostMapping
    @Operation(summary = "Activate a subscription for the authenticated driver")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Subscription activated"),
            @ApiResponse(responseCode = "200", description = "Existing active subscription returned"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Active driver account required"),
            @ApiResponse(responseCode = "404", description = "Plan not found"),
            @ApiResponse(responseCode = "409", description = "Plan cannot be activated"),
            @ApiResponse(responseCode = "503", description = "Driver identity integration unavailable")
    })
    public ResponseEntity<?> activate(@RequestBody ActivateSubscriptionResource resource,
                                      Principal principal) {
        return withDriver(principal, driverId -> {
            var command = ActivateSubscriptionCommandFromResourceAssembler
                    .toCommandFromResource(driverId, resource);
            var result = commandService.handle(command);
            var status = result.toOptional()
                    .filter(SubscriptionCommandService.Activation::created)
                    .isPresent() ? HttpStatus.CREATED : HttpStatus.OK;
            var today = LocalDate.now(ZoneId.of("America/Lima"));

            return ResponseEntityAssembler.toResponseEntityFromResult(result,
                    activation -> SubscriptionResourceFromEntityAssembler
                            .toResourceFromEntity(activation.subscription(), today),
                    status);
        });
    }

    @GetMapping
    @Operation(summary = "Get subscriptions for the authenticated driver")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Driver subscriptions retrieved"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Active driver account required"),
            @ApiResponse(responseCode = "503", description = "Driver identity integration unavailable")
    })
    public ResponseEntity<?> getSubscriptions(Principal principal) {
        return withDriver(principal, driverId -> {
            var today = LocalDate.now(ZoneId.of("America/Lima"));
            var resources = queryService.handle(new GetDriverSubscriptionsQuery(driverId)).stream()
                    .map(subscription -> SubscriptionResourceFromEntityAssembler
                            .toResourceFromEntity(subscription, today))
                    .toList();
            return ResponseEntity.ok(resources);
        });
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleInvalidRequest() {
        var error = ApplicationError.validationError("planId", "A positive plan id is required");
        return ProblemDetailFromApplicationErrorAssembler.toResponseFromApplicationError(error);
    }

    private ResponseEntity<?> withDriver(Principal principal, Function<Long, ResponseEntity<?>> action) {
        if (principal == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        if (driverAccess.isEmpty())
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();

        var driverId = driverAccess.get().findActiveDriverIdByUsername(principal.getName());
        if (driverId.isEmpty())
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        return action.apply(driverId.get());
    }
}