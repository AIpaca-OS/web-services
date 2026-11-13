package com.aipaca.rumbo.platform.billing.interfaces.rest;

import com.aipaca.rumbo.platform.billing.application.commandservices.PlanCommandService;
import com.aipaca.rumbo.platform.billing.application.queryservices.PlanQueryService;
import com.aipaca.rumbo.platform.billing.domain.model.aggregates.Plan;
import com.aipaca.rumbo.platform.billing.domain.model.queries.GetAllPlansQuery;
import com.aipaca.rumbo.platform.billing.domain.model.queries.GetPlanByIdQuery;
import com.aipaca.rumbo.platform.billing.interfaces.rest.resources.CreatePlanResource;
import com.aipaca.rumbo.platform.billing.interfaces.rest.resources.PlanResource;
import com.aipaca.rumbo.platform.billing.interfaces.rest.transform.CreatePlanCommandFromResourceAssembler;
import com.aipaca.rumbo.platform.billing.interfaces.rest.transform.PlanResourceFromEntityAssembler;
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
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@CrossOrigin(origins = "${app.cors.allowed-origins:*}")
@RestController
@RequestMapping(value = "/api/v1/plans", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Plans", description = "Subscription plan catalog endpoints")
public class PlansController {

    private final PlanCommandService planCommandService;
    private final PlanQueryService planQueryService;

    public PlansController(PlanCommandService planCommandService, PlanQueryService planQueryService) {
        this.planCommandService = planCommandService;
        this.planQueryService = planQueryService;
    }

    @GetMapping
    @Operation(summary = "Get all plans")
    @ApiResponse(responseCode = "200", description = "Plan catalog retrieved")
    public ResponseEntity<List<PlanResource>> getAllPlans() {
        var plans = planQueryService.handle(new GetAllPlansQuery());
        var planResources = plans.stream()
                .map(PlanResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        return ResponseEntity.ok(planResources);
    }

    @GetMapping("/{planId}")
    @Operation(summary = "Get a plan by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plan found"),
            @ApiResponse(responseCode = "400", description = "Invalid plan ID"),
            @ApiResponse(responseCode = "404", description = "Plan not found")
    })
    public ResponseEntity<PlanResource> getPlanById(
            @Parameter(description = "Unique plan identifier", example = "1")
            @PathVariable("planId") Long planId
    ) {
        var query = new GetPlanByIdQuery(planId);
        var plan = planQueryService.handle(query);

        if (plan.isEmpty()) return ResponseEntity.notFound().build();

        var resource = PlanResourceFromEntityAssembler.toResourceFromEntity(plan.get());
        return ResponseEntity.ok(resource);
    }

    @PostMapping
    @Operation(summary = "Create a plan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Plan created",
                    content = @Content(schema = @Schema(implementation = PlanResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid plan data"),
            @ApiResponse(responseCode = "409", description = "Plan name already exists")
    })
    public ResponseEntity<?> createPlan(@Valid @RequestBody CreatePlanResource resource) {
        var command = CreatePlanCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = planCommandService.handle(command)
                .flatMap(planId -> planQueryService.handle(new GetPlanByIdQuery(planId))
                        .<Result<Plan, ApplicationError>>map(Result::success)
                        .orElseGet(() -> Result.failure(
                                ApplicationError.notFound("Plan", planId.toString()))));

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                PlanResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.CREATED
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleInvalidPlanBody() {
        var error = ApplicationError.validationError(
                "request-body", "A valid plan request body is required");

        return ProblemDetailFromApplicationErrorAssembler.toResponseFromApplicationError(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> handleInvalidPlanId() {
        var error = ApplicationError.validationError(
                "planId", "Plan id must be a positive integer");

        return ProblemDetailFromApplicationErrorAssembler.toResponseFromApplicationError(error);
    }
}