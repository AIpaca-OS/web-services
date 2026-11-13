package com.aipaca.rumbo.platform.billing.interfaces.rest;

import com.aipaca.rumbo.platform.billing.application.commandservices.SubscriptionCommandService;
import com.aipaca.rumbo.platform.billing.application.queryservices.SubscriptionQueryService;
import com.aipaca.rumbo.platform.billing.domain.model.commands.ActivateSubscriptionCommand;
import com.aipaca.rumbo.platform.billing.domain.model.commands.CancelSubscriptionCommand;
import com.aipaca.rumbo.platform.billing.domain.model.commands.ChangeSubscriptionPlanCommand;
import com.aipaca.rumbo.platform.billing.domain.model.commands.ChangeSubscriptionStatusCommand;
import com.aipaca.rumbo.platform.billing.domain.model.queries.GetDriverSubscriptionsQuery;
import com.aipaca.rumbo.platform.billing.domain.model.valueobjects.SubscriptionStatus;
import com.aipaca.rumbo.platform.billing.infrastructure.security.BillingDemoSessionService;
import com.aipaca.rumbo.platform.billing.interfaces.rest.transform.SubscriptionResourceFromEntityAssembler;
import com.aipaca.rumbo.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Opt-in public demonstration only: fictitious accounts, no payments or verified IAM.
 * A client can access only its randomly assigned demo driver through a signed bearer token.
 * Private subscription endpoints remain protected by DeploymentPreviewAccessFilter.
 */
@CrossOrigin(origins = "${app.cors.allowed-origins:*}")
@RestController
@RequestMapping("/api/v1/billing-demo")
@ConditionalOnProperty(name = "BILLING_PUBLIC_DEMO_ENABLED", havingValue = "true")
public class BillingDemoController {
    private final BillingDemoSessionService sessions;
    private final SubscriptionCommandService commands;
    private final SubscriptionQueryService queries;

    public BillingDemoController(
            BillingDemoSessionService sessions,
            SubscriptionCommandService commands,
            SubscriptionQueryService queries) {
        this.sessions = sessions;
        this.commands = commands;
        this.queries = queries;
    }

    public record SessionResource(String token) { }
    public record PlanSelection(@NotNull @Positive Long planId) { }
    public record StatusSelection(@NotNull SubscriptionStatus status) { }

    @PostMapping("/session")
    public SessionResource createDemoSession() {
        return new SessionResource(sessions.issueToken());
    }

    @GetMapping("/subscriptions")
    public ResponseEntity<?> list(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
                                  String authorization) {
        long driverId = sessions.requireDriverId(authorization);
        var today = LocalDate.now(ZoneId.of("America/Lima"));
        return ResponseEntity.ok(queries.handle(new GetDriverSubscriptionsQuery(driverId))
                .stream()
                .map(value -> SubscriptionResourceFromEntityAssembler
                        .toResourceFromEntity(value, today))
                .toList());
    }

    @PostMapping("/subscriptions")
    public ResponseEntity<?> activate(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
            String authorization,
            @Valid @RequestBody PlanSelection selection) {
        long driverId = sessions.requireDriverId(authorization);
        var result = commands.handle(new ActivateSubscriptionCommand(driverId, selection.planId()));
        var status = result.toOptional()
                .filter(SubscriptionCommandService.Activation::created)
                .isPresent() ? HttpStatus.CREATED : HttpStatus.OK;
        var today = LocalDate.now(ZoneId.of("America/Lima"));
        return ResponseEntityAssembler.toResponseEntityFromResult(result,
                value -> SubscriptionResourceFromEntityAssembler
                        .toResourceFromEntity(value.subscription(), today), status);
    }

    @PatchMapping("/subscriptions/{id}/plan")
    public ResponseEntity<?> changePlan(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
            String authorization,
            @PathVariable Long id,
            @Valid @RequestBody PlanSelection selection) {
        long driverId = sessions.requireDriverId(authorization);
        var today = LocalDate.now(ZoneId.of("America/Lima"));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                commands.handle(new ChangeSubscriptionPlanCommand(id, driverId, selection.planId())),
                value -> SubscriptionResourceFromEntityAssembler.toResourceFromEntity(value, today),
                HttpStatus.OK);
    }

    @PatchMapping("/subscriptions/{id}/status")
    public ResponseEntity<?> changeStatus(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
            String authorization,
            @PathVariable Long id,
            @Valid @RequestBody StatusSelection selection) {
        long driverId = sessions.requireDriverId(authorization);
        if (selection.status() != SubscriptionStatus.ACTIVE
                && selection.status() != SubscriptionStatus.SUSPENDED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid demo status");
        }
        var today = LocalDate.now(ZoneId.of("America/Lima"));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                commands.handle(new ChangeSubscriptionStatusCommand(id, driverId, selection.status())),
                value -> SubscriptionResourceFromEntityAssembler.toResourceFromEntity(value, today),
                HttpStatus.OK);
    }

    @PatchMapping("/subscriptions/{id}/cancel")
    public ResponseEntity<?> cancel(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
            String authorization,
            @PathVariable Long id) {
        long driverId = sessions.requireDriverId(authorization);
        var today = LocalDate.now(ZoneId.of("America/Lima"));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                commands.handle(new CancelSubscriptionCommand(id, driverId)),
                value -> SubscriptionResourceFromEntityAssembler.toResourceFromEntity(value, today),
                HttpStatus.OK);
    }
}
