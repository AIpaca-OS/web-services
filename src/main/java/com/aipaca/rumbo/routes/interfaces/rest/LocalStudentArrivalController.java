package com.aipaca.rumbo.routes.interfaces.rest;

import com.aipaca.rumbo.platform.profiles.application.internal.access.ProfilesAuthorization;
import com.aipaca.rumbo.routes.application.commandservices.TripCommandService;
import com.aipaca.rumbo.routes.domain.model.commands.ConfirmStudentArrivalCommand;
import com.aipaca.rumbo.routes.interfaces.rest.resources.RouteResources.TripView;
import com.aipaca.rumbo.routes.interfaces.rest.transform.RouteResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/** Protected by the localhost-only actor until the real IAM driver identity exists. */
@RestController
@RequestMapping("/api/v1/trips")
@org.springframework.context.annotation.Profile("local")
@ConditionalOnProperty(prefix="app.profiles.demo",name="enabled",havingValue="true")
@Tag(name="Trip arrivals", description="Explicit driver-confirmed student arrival")
public class LocalStudentArrivalController {
    private final TripCommandService trips;
    private final ProfilesAuthorization authorization;

    public LocalStudentArrivalController(TripCommandService trips, ProfilesAuthorization authorization) {
        this.trips = trips;
        this.authorization = authorization;
    }
    @PatchMapping("/{id}/passengers/{studentId}/arrived")
    @Operation(summary="Driver confirms a student's arrival after the assigned stop is served")
    public TripView confirm(@PathVariable UUID id, @PathVariable String studentId) {
        authorization.currentDriver();
        return RouteResourceFromEntityAssembler.view(
                trips.handle(new ConfirmStudentArrivalCommand(id,studentId)));
    }
}
