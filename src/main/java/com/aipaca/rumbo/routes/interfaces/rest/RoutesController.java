package com.aipaca.rumbo.routes.interfaces.rest;
import com.aipaca.rumbo.routes.application.commandservices.*;
import com.aipaca.rumbo.routes.application.queryservices.*;
import com.aipaca.rumbo.routes.domain.model.RouteStatus;
import com.aipaca.rumbo.routes.domain.model.commands.*;
import com.aipaca.rumbo.routes.domain.model.queries.*;
import com.aipaca.rumbo.routes.interfaces.rest.transform.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.media.*;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
import static com.aipaca.rumbo.routes.interfaces.rest.resources.RouteResources.*;
@RestController
@RequestMapping(value="/api/v1/routes",produces=MediaType.APPLICATION_JSON_VALUE)
@Tag(name="Routes",description="School route planning, stops, schedules and assignments")
public class RoutesController {
    private final RouteCommandService commands;
    private final RouteQueryService queries;
    private final TripCommandService tripCommands;
    private final TripQueryService tripQueries;
    public RoutesController(RouteCommandService commands, RouteQueryService queries,
            TripCommandService tripCommands,TripQueryService tripQueries){
        this.commands=commands;this.queries=queries;this.tripCommands=tripCommands;this.tripQueries=tripQueries;
    }
    @GetMapping
    @Operation(summary="List routes",description="Lists routes available to the current application context. Authentication and driver filtering are pending.")
    @ApiResponse(responseCode="200",description="Routes returned")
    public List<RouteView> all(){return queries.handle(new GetAllRoutesQuery()).stream().map(RouteResourceFromEntityAssembler::view).toList();}
    @GetMapping("/{id}")
    @Operation(summary="Get route by ID")
    @ApiResponses({@ApiResponse(responseCode="200",description="Route found"),@ApiResponse(responseCode="404",description="Route not found")})
    public RouteView get(@PathVariable UUID id){return RouteResourceFromEntityAssembler.view(queries.handle(new GetRouteByIdQuery(id))
            .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Route not found")));}
    @PostMapping(consumes=MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary="Create configurable route",description="Creates a new route in CONFIGURABLE status, with ordered stops and optional schedule.")
    @RequestBody(content=@Content(examples=@ExampleObject(name="Create morning route",value="{\"name\":\"Ruta Surco\",\"shift\":\"MORNING\",\"vehicleId\":\"vehicle-1\",\"stops\":[{\"address\":\"Av. Primavera 123\"}],\"schedule\":[{\"dayOfWeek\":\"MONDAY\",\"departureTime\":\"07:00\"}],\"scheduleConfirmed\":true}")))
    @ApiResponses({@ApiResponse(responseCode="201",description="Route created"),@ApiResponse(responseCode="422",description="Invalid domain data")})
    public RouteView create(@Valid @org.springframework.web.bind.annotation.RequestBody RouteInput resource){
        return RouteResourceFromEntityAssembler.view(commands.handle(RouteCommandFromResourceAssembler.toCommand(resource)));
    }
    @PutMapping(value="/{id}",consumes=MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary="Update route",description="Replaces route configuration. Existing stop IDs must be preserved when assignments exist.")
    @RequestBody(content=@Content(examples=@ExampleObject(value="{\"name\":\"Ruta Surco actualizada\",\"shift\":\"MORNING\",\"vehicleId\":\"vehicle-1\",\"stops\":[{\"address\":\"Av. Primavera 123\"}],\"schedule\":[],\"scheduleConfirmed\":false}")))
    @ApiResponses({@ApiResponse(responseCode="200",description="Route updated"),@ApiResponse(responseCode="404",description="Route not found")})
    public RouteView update(@PathVariable UUID id,@Valid @org.springframework.web.bind.annotation.RequestBody RouteInput resource){
        return RouteResourceFromEntityAssembler.view(commands.handle(RouteCommandFromResourceAssembler.toCommand(id,resource)));
    }
    @PutMapping(value="/{id}/schedule",consumes=MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary="Configure weekly schedule",description="Each service day must be unique; time format HH:mm.")
    @RequestBody(content=@Content(examples=@ExampleObject(value="{\"schedule\":[{\"dayOfWeek\":\"MONDAY\",\"departureTime\":\"07:00\"}],\"confirm\":true}")))
    @ApiResponse(responseCode="200",description="Schedule updated")
    public RouteView schedule(@PathVariable UUID id,@Valid @org.springframework.web.bind.annotation.RequestBody ScheduleUpdate resource){
        return RouteResourceFromEntityAssembler.view(commands.handle(RouteCommandFromResourceAssembler.toCommand(id,resource)));
    }
    @GetMapping("/{id}/assignments")
    @Operation(summary="List student assignments to stops")
    @ApiResponse(responseCode="200",description="Assignments returned")
    public List<AssignmentView> assignments(@PathVariable UUID id){return queries.handle(new GetRouteAssignmentsQuery(id)).stream().map(RouteResourceFromEntityAssembler::view).toList();}
    @PostMapping(value="/{id}/assignments",consumes=MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary="Assign authorized student",description="Profiles verifies bearer token, driver, vehicle, active student and approved relationship.")
    @RequestBody(content=@Content(examples=@ExampleObject(value="{\"studentId\":\"student-1\",\"stopId\":\"11111111-1111-1111-1111-111111111111\"}")))
    @ApiResponses({@ApiResponse(responseCode="201",description="Assignment created"),
        @ApiResponse(responseCode="401",description="Driver credentials required"),
        @ApiResponse(responseCode="403",description="Student not eligible for driver"),
        @ApiResponse(responseCode="409",description="Student already assigned"),
        @ApiResponse(responseCode="503",description="Profiles authorization unavailable")})
    public AssignmentView assign(@PathVariable UUID id,
            @Valid @org.springframework.web.bind.annotation.RequestBody AssignmentInput r,
            @RequestHeader(value="Authorization",required=false) String authorizationHeader){
        return RouteResourceFromEntityAssembler.view(
            commands.handle(new AssignStudentCommand(id,r.stopId(),r.studentId(),authorizationHeader)));
    }
    @PatchMapping(value="/{id}/status",consumes=MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary="Publish configured route",description="Requires stops, confirmed schedule, vehicle and assigned students.")
    @RequestBody(content=@Content(examples=@ExampleObject(value="{\"status\":\"PUBLISHED\"}")))
    @ApiResponses({@ApiResponse(responseCode="200",description="Route published"),@ApiResponse(responseCode="422",description="Route incomplete")})
    public RouteView status(@PathVariable UUID id,
            @Valid @org.springframework.web.bind.annotation.RequestBody StatusUpdate r,
            @RequestHeader(value="Authorization",required=false) String authorizationHeader){
        if(r.status()!=RouteStatus.PUBLISHED)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Only publication supported");
        return RouteResourceFromEntityAssembler.view(
            commands.handle(new PublishRouteCommand(id,authorizationHeader)));
    }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary="Delete route without trips")
    @ApiResponses({@ApiResponse(responseCode="204",description="Deleted"),@ApiResponse(responseCode="409",description="Route already has trips")})
    public void delete(@PathVariable UUID id){commands.handle(new DeleteRouteCommand(id));}
    @PostMapping(value="/{id}/trips",consumes=MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary="Plan one trip from a published route",description="Snapshots route assignments and stop order for the selected service date.")
    @RequestBody(content=@Content(examples=@ExampleObject(value="{\"date\":\"2026-11-02\"}")))
    @ApiResponses({@ApiResponse(responseCode="201",description="Trip planned"),@ApiResponse(responseCode="409",description="Trip already scheduled")})
    public TripView trip(@PathVariable UUID id,@Valid @org.springframework.web.bind.annotation.RequestBody TripInput r){
        return RouteResourceFromEntityAssembler.view(tripCommands.handle(new PlanTripCommand(id,r.date())));
    }
    @GetMapping("/{id}/trips")
    @Operation(summary="List trips for a route")
    @ApiResponse(responseCode="200",description="Trips returned")
    public List<TripView> trips(@PathVariable UUID id){return tripQueries.handle(new GetTripsByRouteQuery(id)).stream().map(RouteResourceFromEntityAssembler::view).toList();}
}
