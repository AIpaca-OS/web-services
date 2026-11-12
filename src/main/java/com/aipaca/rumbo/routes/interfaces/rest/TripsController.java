package com.aipaca.rumbo.routes.interfaces.rest;
import com.aipaca.rumbo.routes.application.commandservices.TripCommandService;
import com.aipaca.rumbo.routes.application.queryservices.TripQueryService;
import com.aipaca.rumbo.routes.domain.model.commands.*;
import com.aipaca.rumbo.routes.domain.model.queries.*;
import com.aipaca.rumbo.routes.interfaces.rest.transform.RouteResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.media.*;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.UUID;
import static com.aipaca.rumbo.routes.interfaces.rest.resources.RouteResources.*;
@RestController
@RequestMapping(value="/api/v1/trips",produces=MediaType.APPLICATION_JSON_VALUE)
@Tag(name="Trips",description="Trip execution, absences and cancellation")
public class TripsController {
    private final TripCommandService commands;
    private final TripQueryService queries;
    public TripsController(TripCommandService commands,TripQueryService queries){this.commands=commands;this.queries=queries;}
    @GetMapping("/{id}")
    @Operation(summary="Get a trip by ID")
    @ApiResponses({@ApiResponse(responseCode="200",description="Trip found"),@ApiResponse(responseCode="404",description="Not found")})
    public TripView get(@PathVariable UUID id){return RouteResourceFromEntityAssembler.view(queries.handle(new GetTripByIdQuery(id))
        .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Trip not found")));}
    @PostMapping(value="/{id}/absences",consumes=MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary="Report absence",description="Requires tutor authentication and permission integration before production use.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content=@Content(examples=@ExampleObject(value="{\"studentId\":\"student-1\"}")))
    @ApiResponse(responseCode="200",description="Absence recorded")
    public TripView absent(@PathVariable UUID id,@Valid @RequestBody AbsenceInput r){return RouteResourceFromEntityAssembler.view(commands.handle(new MarkAbsenceCommand(id,r.studentId())));}
    @PatchMapping("/{id}/start") @Operation(summary="Start scheduled trip")
    @ApiResponse(responseCode="200",description="Trip started")
    public TripView start(@PathVariable UUID id){return RouteResourceFromEntityAssembler.view(commands.handle(new StartTripCommand(id)));}
    @PatchMapping("/{id}/stops/{stopId}/served") @Operation(summary="Mark stop served")
    @ApiResponse(responseCode="200",description="Stop completed")
    public TripView serve(@PathVariable UUID id,@PathVariable UUID stopId){return RouteResourceFromEntityAssembler.view(commands.handle(new ServeStopCommand(id,stopId)));}
    @PatchMapping(value="/{id}/cancel",consumes=MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary="Cancel trip",description="Preserves previously recorded events; notifications integration pending.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content=@Content(examples=@ExampleObject(value="{\"reason\":\"Mechanical issue\"}")))
    @ApiResponse(responseCode="200",description="Trip cancelled")
    public TripView cancel(@PathVariable UUID id,@Valid @RequestBody CancelInput r){return RouteResourceFromEntityAssembler.view(commands.handle(new CancelTripCommand(id,r.reason())));}
    @PatchMapping("/{id}/complete") @Operation(summary="Complete trip")
    @ApiResponse(responseCode="200",description="Trip completed")
    public TripView complete(@PathVariable UUID id){return RouteResourceFromEntityAssembler.view(commands.handle(new CompleteTripCommand(id)));}
}
