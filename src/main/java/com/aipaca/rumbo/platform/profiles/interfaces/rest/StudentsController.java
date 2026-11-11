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

import com.aipaca.rumbo.platform.profiles.application.commandservices.StudentCommandService;
import com.aipaca.rumbo.platform.profiles.application.queryservices.StudentQueryService;

@RestController
@RequestMapping(value="/api/v1/students",produces=MediaType.APPLICATION_JSON_VALUE)
@Tag(name="Students",description="Students accessible to an authorized tutor (US06, US07)")
public class StudentsController {
    private final StudentCommandService commands;
    private final StudentQueryService queries;
    private final ProfilesAuthorization auth;
    public StudentsController(StudentCommandService commands,StudentQueryService queries,ProfilesAuthorization auth) {
        this.commands=commands;this.queries=queries;this.auth=auth;
    }
    @PostMapping(consumes=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> register(@Valid @RequestBody CreateStudentResource resource) {
        var cmd=ProfilesCommandsFromResourceAssembler.registerStudent(resource,auth.currentTutor().getId());
        return ResponseEntityAssembler.toResponseEntityFromResult(commands.handle(cmd),CreatedIdResource::new,HttpStatus.CREATED);
    }
    @GetMapping
    public ResponseEntity<List<StudentResource>> mine() {
        var list=queries.handle(new GetAllStudentsByTutorIdQuery(auth.currentTutor().getId()));
        return ResponseEntity.ok(list.stream().map(StudentResourceFromEntityAssembler::toResourceFromEntity).toList());
    }
    @GetMapping("/{studentId}")
    public ResponseEntity<?> getById(@PathVariable Long studentId) {
        var opt=queries.handle(new GetStudentByIdQuery(studentId));
        if(opt.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(StudentResourceFromEntityAssembler.toResourceFromEntity(opt.get()));
    }
    @PutMapping(value="/{studentId}",consumes=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@PathVariable Long studentId,@Valid @RequestBody UpdateStudentResource resource) {
        var cmd=ProfilesCommandsFromResourceAssembler.updateStudent(studentId,resource);
        return ResponseEntityAssembler.toResponseEntityFromResult(commands.handle(cmd),CreatedIdResource::new,HttpStatus.OK);
    }
    @GetMapping("/{studentId}/tutors")
    public ResponseEntity<List<TutorStudentRelationshipResource>> tutors(@PathVariable Long studentId) {
        var list=queries.handle(new GetAllTutorStudentRelationshipsByStudentIdQuery(studentId));
        return ResponseEntity.ok(list.stream()
                .map(TutorStudentRelationshipResourceFromEntityAssembler::toResourceFromEntity).toList());
    }
    @PostMapping(value="/{studentId}/tutors",consumes=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> authorizeTutor(@PathVariable Long studentId,@Valid @RequestBody AuthorizeTutorResource resource) {
        var cmd=ProfilesCommandsFromResourceAssembler.authorizeTutor(studentId,resource,auth.currentTutor().getId());
        return ResponseEntityAssembler.toResponseEntityFromResult(commands.handle(cmd),CreatedIdResource::new,HttpStatus.CREATED);
    }
    @PostMapping("/{studentId}/tutors/{tutorId}/revoke")
    public ResponseEntity<?> revokeTutor(@PathVariable Long studentId,@PathVariable Long tutorId) {
        var cmd=new RevokeTutorStudentRelationshipCommand(studentId,tutorId,auth.currentTutor().getId());
        return ResponseEntityAssembler.toResponseEntityFromResult(commands.handle(cmd),CreatedIdResource::new,HttpStatus.OK);
    }
}
