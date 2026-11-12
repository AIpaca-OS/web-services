package com.aipaca.rumbo.platform.routes.application.internal.commandservices;

import com.aipaca.rumbo.platform.routes.application.commandservices.RouteCommandService;
import com.aipaca.rumbo.platform.routes.domain.model.aggregates.Route;
import com.aipaca.rumbo.platform.routes.domain.model.commands.*;
import com.aipaca.rumbo.platform.routes.domain.model.valueobjects.StudentId;
import com.aipaca.rumbo.platform.routes.domain.repositories.RouteRepository;
import com.aipaca.rumbo.platform.shared.application.result.ApplicationError;
import com.aipaca.rumbo.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Route Command Service Implementation
 */
@Service
@Transactional
public class RouteCommandServiceImpl implements RouteCommandService {
    private final RouteRepository routeRepository;

    /**
     * Constructor
     *
     * @param routeRepository The {@link RouteRepository} instance
     */
    public RouteCommandServiceImpl(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    // inherited javadoc
    @Override
    public Result<Long, ApplicationError> handle(CreateRouteCommand command) {
        try {
            var route = new Route(command);
            var savedRoute = routeRepository.save(route);
            return Result.success(savedRoute.getId());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("Route", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("create-route", e.getMessage()));
        }
    }

    // inherited javadoc
    @Override
    public Result<Long, ApplicationError> handle(UpdateRouteCommand command) {
        var result = routeRepository.findById(command.routeId());
        if (result.isEmpty())
            return Result.failure(ApplicationError.notFound("Route", command.routeId().toString()));
        var route = result.get();
        try {
            route.updateInformation(command.name(), command.shift(), command.vehicleId());
            route.replaceStops(command.stops());
            route.updateSchedule(command.schedule(), command.scheduleConfirmed());
            return Result.success(routeRepository.save(route).getId());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.businessRuleViolation("Route update", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("update-route", e.getMessage()));
        }
    }

    // inherited javadoc
    @Override
    public Result<Long, ApplicationError> handle(UpdateRouteScheduleCommand command) {
        var result = routeRepository.findById(command.routeId());
        if (result.isEmpty())
            return Result.failure(ApplicationError.notFound("Route", command.routeId().toString()));
        var route = result.get();
        try {
            route.updateSchedule(command.schedule(), command.confirm());
            return Result.success(routeRepository.save(route).getId());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.businessRuleViolation("Route schedule", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("update-route-schedule", e.getMessage()));
        }
    }

    // inherited javadoc
    @Override
    public Result<Long, ApplicationError> handle(AssignStudentToRouteCommand command) {
        var result = routeRepository.findById(command.routeId());
        if (result.isEmpty())
            return Result.failure(ApplicationError.notFound("Route", command.routeId().toString()));
        var route = result.get();
        var studentId = new StudentId(command.studentId());
        if (route.isStudentAssigned(studentId))
            return Result.failure(ApplicationError.conflict("Route",
                    "Student %d is already assigned to this route".formatted(command.studentId())));
        try {
            route.assignStudent(command.stopId(), studentId);
            var savedRoute = routeRepository.save(route);
            return savedRoute.findAssignment(studentId)
                    .<Result<Long, ApplicationError>>map(assignment -> Result.success(assignment.getId()))
                    .orElseGet(() -> Result.failure(ApplicationError.unexpected(
                            "assign-student", "Assignment not found after saving the route")));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.businessRuleViolation("Route assignment", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("assign-student", e.getMessage()));
        }
    }

    // inherited javadoc
    @Override
    public Result<Long, ApplicationError> handle(ReplaceRouteAssignmentsCommand command) {
        var result = routeRepository.findById(command.routeId());
        if (result.isEmpty())
            return Result.failure(ApplicationError.notFound("Route", command.routeId().toString()));
        var route = result.get();
        try {
            route.replaceAssignments(command.assignments());
            return Result.success(routeRepository.save(route).getId());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.businessRuleViolation("Route assignments", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("replace-route-assignments", e.getMessage()));
        }
    }

    // inherited javadoc
    @Override
    public Result<Long, ApplicationError> handle(PublishRouteCommand command) {
        var result = routeRepository.findById(command.routeId());
        if (result.isEmpty())
            return Result.failure(ApplicationError.notFound("Route", command.routeId().toString()));
        var route = result.get();
        if (route.isPublished())
            return Result.failure(ApplicationError.conflict("Route", "Route has already been published"));
        try {
            route.publish();
            return Result.success(routeRepository.save(route).getId());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.businessRuleViolation("Route publication", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("publish-route", e.getMessage()));
        }
    }

    // inherited javadoc
    @Override
    public Result<Long, ApplicationError> handle(DeleteRouteCommand command) {
        if (!routeRepository.existsById(command.routeId()))
            return Result.failure(ApplicationError.notFound("Route", command.routeId().toString()));
        try {
            routeRepository.deleteById(command.routeId());
            return Result.success(command.routeId());
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("delete-route", e.getMessage()));
        }
    }
}
