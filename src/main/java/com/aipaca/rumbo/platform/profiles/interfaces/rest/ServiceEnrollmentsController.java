package com.aipaca.rumbo.platform.profiles.interfaces.rest;
import com.aipaca.rumbo.platform.profiles.interfaces.rest.resources.*;
import com.aipaca.rumbo.platform.profiles.interfaces.rest.transform.*;
import com.aipaca.rumbo.platform.profiles.application.internal.access.ProfilesAuthorization;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.*;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.*;
import com.aipaca.rumbo.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import com.aipaca.rumbo.platform.profiles.application.commandservices.ServiceEnrollmentCommandService;
import com.aipaca.rumbo.platform.profiles.application.queryservices.ServiceEnrollmentQueryService;

@RestController
@RequestMapping(value="/api/v1/service-enrollments",produces=MediaType.APPLICATION_JSON_VALUE)
@Tag(name="Service Enrollments",description="Student affiliation to drivers, independent of trips")
public class ServiceEnrollmentsController {
    private final ServiceEnrollmentCommandService commands;
    private final ServiceEnrollmentQueryService queries;
    private final ProfilesAuthorization auth;
    public ServiceEnrollmentsController(ServiceEnrollmentCommandService commands,
            ServiceEnrollmentQueryService queries, ProfilesAuthorization auth) {
        this.commands=commands;this.queries=queries;this.auth=auth;
    }
    @PostMapping(consumes=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> request(@Valid @RequestBody RequestEnrollmentResource resource) {
        var cmd=ProfilesCommandsFromResourceAssembler.requestEnrollment(resource,auth.currentTutor().getId());
        return ResponseEntityAssembler.toResponseEntityFromResult(commands.handle(cmd),CreatedIdResource::new,HttpStatus.CREATED);
    }
    @GetMapping("/drivers/me")
    public ResponseEntity<List<ServiceEnrollmentResource>> byDriver() {
        var list=queries.handle(new GetAllServiceEnrollmentsByDriverIdQuery(auth.currentDriver().getId()));
        return ResponseEntity.ok(list.stream()
                .map(ServiceEnrollmentResourceFromEntityAssembler::toResourceFromEntity).toList());
    }
    @GetMapping("/drivers/me/students")
    public ResponseEntity<List<DriverStudentResource>> driverStudents() {
        var list=queries.handle(new GetAllStudentsByDriverIdQuery(auth.currentDriver().getId()));
        return ResponseEntity.ok(list.stream().map(s -> new DriverStudentResource(
                s.getId(), s.getName().firstName(), s.getName().lastName(),
                s.getSchoolId(), s.getStatus())).toList());
    }
    @GetMapping("/students/{studentId}")
    public ResponseEntity<List<ServiceEnrollmentResource>> byStudent(@PathVariable Long studentId) {
        var list=queries.handle(new GetAllServiceEnrollmentsByStudentIdQuery(studentId));
        return ResponseEntity.ok(list.stream()
                .map(ServiceEnrollmentResourceFromEntityAssembler::toResourceFromEntity).toList());
    }
    @PostMapping("/{enrollmentId}/accept")
    public ResponseEntity<?> accept(@PathVariable Long enrollmentId) {
        var cmd=new AcceptServiceEnrollmentCommand(enrollmentId,auth.currentDriver().getId());
        return ResponseEntityAssembler.toResponseEntityFromResult(commands.handle(cmd),CreatedIdResource::new,HttpStatus.OK);
    }
    @PostMapping("/{enrollmentId}/cancel")
    public ResponseEntity<?> cancel(@PathVariable Long enrollmentId) {
        var cmd=new CancelServiceEnrollmentCommand(enrollmentId,auth.currentTutor().getId());
        return ResponseEntityAssembler.toResponseEntityFromResult(commands.handle(cmd),CreatedIdResource::new,HttpStatus.OK);
    }
    @PostMapping("/{enrollmentId}/end")
    public ResponseEntity<?> end(@PathVariable Long enrollmentId) {
        var cmd=new EndServiceEnrollmentCommand(enrollmentId,auth.currentProfile().getId());
        return ResponseEntityAssembler.toResponseEntityFromResult(commands.handle(cmd),CreatedIdResource::new,HttpStatus.OK);
    }
}
