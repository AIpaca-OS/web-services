package com.aipaca.rumbo.platform.profiles.interfaces.rest;

import com.aipaca.rumbo.platform.profiles.application.commandservices.StudentCommandService;
import com.aipaca.rumbo.platform.profiles.application.queryservices.StudentQueryService;
import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.Student;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.RevokeTutorStudentRelationshipCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.entities.TutorStudentRelationship;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.GetActiveTutorStudentRelationshipByTutorIdAndStudentIdQuery;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.GetAllStudentsByTutorIdQuery;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.GetAllTutorStudentRelationshipsByStudentIdQuery;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.GetStudentByIdQuery;
import com.aipaca.rumbo.platform.profiles.interfaces.rest.resources.*;
import com.aipaca.rumbo.platform.profiles.interfaces.rest.transform.*;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller that exposes student registration and tutor authorization endpoints.
 */
@RestController
@CrossOrigin(origins = "${app.cors.allowed-origins:*}")
@RequestMapping(value = "/api/v1/students", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Students", description = "Student management endpoints")
public class StudentsController {
    private final StudentCommandService studentCommandService;
    private final StudentQueryService studentQueryService;

    /**
     * Constructor
     *
     * @param studentCommandService The {@link StudentCommandService} instance
     * @param studentQueryService   The {@link StudentQueryService} instance
     */
    public StudentsController(StudentCommandService studentCommandService, StudentQueryService studentQueryService) {
        this.studentCommandService = studentCommandService;
        this.studentQueryService = studentQueryService;
    }

    /**
     * Register a new student
     *
     * @param resource The {@link CreateStudentResource} instance
     * @return A {@link StudentResource} resource for the registered student
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Register a new student", description = "Registers a student; the registering tutor becomes its first authorized tutor.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Student registered",
                    content = @Content(schema = @Schema(implementation = StudentResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data or tutor profile"),
            @ApiResponse(responseCode = "404", description = "School not found")
    })
    public ResponseEntity<?> registerStudent(@Valid @RequestBody CreateStudentResource resource) {
        var registerStudentCommand = RegisterStudentCommandFromResourceAssembler.toCommandFromResource(resource);
        return toStudentResponse(studentCommandService.handle(registerStudentCommand), HttpStatus.CREATED);
    }

    /**
     * Get the students of a tutor
     *
     * @param tutorProfileId The tutor profile ID
     * @return A list of {@link StudentResource} resources
     */
    @GetMapping
    @Operation(summary = "Get students by tutor", description = "Retrieves the students the tutor is currently authorized for.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Students found",
                    content = @Content(schema = @Schema(implementation = StudentResource.class)))
    })
    public ResponseEntity<List<StudentResource>> getStudentsByTutorProfileId(
            @RequestParam @Parameter(description = "Tutor profile unique identifier", example = "1", required = true)
            Long tutorProfileId) {
        var students = studentQueryService.handle(new GetAllStudentsByTutorIdQuery(tutorProfileId));
        return ResponseEntity.ok(students.stream().map(StudentResourceFromEntityAssembler::toResourceFromEntity).toList());
    }

    /**
     * Get a student by ID
     *
     * @param studentId The student ID
     * @return A {@link StudentResource} resource for the student
     */
    @GetMapping("/{studentId}")
    @Operation(summary = "Get student by ID", description = "Retrieves a student by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Student found",
                    content = @Content(schema = @Schema(implementation = StudentResource.class))),
            @ApiResponse(responseCode = "404", description = "Student not found")
    })
    public ResponseEntity<?> getStudentById(
            @PathVariable @Parameter(description = "Student unique identifier", example = "1", required = true)
            Long studentId) {
        var student = studentQueryService.handle(new GetStudentByIdQuery(studentId));
        if (student.isEmpty()) {
            var error = ApplicationError.notFound("Student", studentId.toString());
            return ProblemDetailFromApplicationErrorAssembler.toResponseFromApplicationError(error);
        }
        return ResponseEntity.ok(StudentResourceFromEntityAssembler.toResourceFromEntity(student.get()));
    }

    /**
     * Update the information of a student
     *
     * @param studentId The student ID
     * @param resource  The {@link UpdateStudentResource} instance
     * @return A {@link StudentResource} resource for the updated student
     */
    @PutMapping(value = "/{studentId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update student", description = "Updates the personal information of an active student.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Student updated",
                    content = @Content(schema = @Schema(implementation = StudentResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Student or school not found"),
            @ApiResponse(responseCode = "422", description = "The student is inactive or anonymized")
    })
    public ResponseEntity<?> updateStudent(
            @PathVariable @Parameter(description = "Student unique identifier", example = "1", required = true)
            Long studentId,
            @Valid @RequestBody UpdateStudentResource resource) {
        var command = UpdateStudentInformationCommandFromResourceAssembler.toCommandFromResource(studentId, resource);
        return toStudentResponse(studentCommandService.handle(command), HttpStatus.OK);
    }

    /**
     * Get the tutor authorizations of a student
     *
     * @param studentId The student ID
     * @return A list of {@link TutorStudentRelationshipResource} resources, including revoked history
     */
    @GetMapping("/{studentId}/tutors")
    @Operation(summary = "Get student tutors", description = "Retrieves the tutor authorizations of a student, including revoked history.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tutor authorizations found",
                    content = @Content(schema = @Schema(implementation = TutorStudentRelationshipResource.class)))
    })
    public ResponseEntity<List<TutorStudentRelationshipResource>> getStudentTutors(
            @PathVariable @Parameter(description = "Student unique identifier", example = "1", required = true)
            Long studentId) {
        var relationships = studentQueryService.handle(new GetAllTutorStudentRelationshipsByStudentIdQuery(studentId));
        return ResponseEntity.ok(relationships.stream()
                .map(TutorStudentRelationshipResourceFromEntityAssembler::toResourceFromEntity).toList());
    }

    /**
     * Authorize a tutor of a student
     *
     * @param studentId The student ID
     * @param resource  The {@link AuthorizeTutorResource} instance
     * @return A {@link TutorStudentRelationshipResource} resource for the new authorization
     */
    @PostMapping(value = "/{studentId}/tutors", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Authorize tutor", description = "An authorized tutor authorizes another tutor of the student.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tutor authorized",
                    content = @Content(schema = @Schema(implementation = TutorStudentRelationshipResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data or tutor profiles"),
            @ApiResponse(responseCode = "404", description = "Student not found"),
            @ApiResponse(responseCode = "422", description = "The authorizer is not authorized or the tutor already is")
    })
    public ResponseEntity<?> authorizeTutor(
            @PathVariable @Parameter(description = "Student unique identifier", example = "1", required = true)
            Long studentId,
            @Valid @RequestBody AuthorizeTutorResource resource) {
        var command = AuthorizeTutorStudentRelationshipCommandFromResourceAssembler.toCommandFromResource(studentId, resource);
        var result = studentCommandService.handle(command)
                .flatMap(id -> studentQueryService.handle(new GetActiveTutorStudentRelationshipByTutorIdAndStudentIdQuery(
                                command.tutorProfileId(), id))
                        .<Result<TutorStudentRelationship, ApplicationError>>map(Result::success)
                        .orElseGet(() -> Result.failure(ApplicationError.notFound("Relationship", id.toString()))));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, TutorStudentRelationshipResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }

    /**
     * Revoke a tutor of a student
     *
     * @param studentId            The student ID
     * @param tutorId              The tutor profile ID to revoke
     * @param requestedByProfileId The profile ID of the authorized tutor that revokes it
     * @return A {@link StudentResource} resource for the student
     */
    @PostMapping("/{studentId}/tutors/{tutorId}/revoke")
    @Operation(summary = "Revoke tutor", description = "An authorized tutor revokes another tutor; the last tutor cannot be revoked.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tutor revoked",
                    content = @Content(schema = @Schema(implementation = StudentResource.class))),
            @ApiResponse(responseCode = "404", description = "Student not found"),
            @ApiResponse(responseCode = "422", description = "The revocation is not allowed")
    })
    public ResponseEntity<?> revokeTutor(
            @PathVariable @Parameter(description = "Student unique identifier", example = "1", required = true)
            Long studentId,
            @PathVariable @Parameter(description = "Tutor profile unique identifier", example = "2", required = true)
            Long tutorId,
            @RequestParam @Parameter(description = "Profile id of the authorized tutor that revokes it", example = "1", required = true)
            Long requestedByProfileId) {
        var command = new RevokeTutorStudentRelationshipCommand(studentId, tutorId, requestedByProfileId);
        return toStudentResponse(studentCommandService.handle(command), HttpStatus.OK);
    }

    private ResponseEntity<?> toStudentResponse(Result<Long, ApplicationError> commandResult, HttpStatus status) {
        var result = commandResult.flatMap(studentId -> studentQueryService.handle(new GetStudentByIdQuery(studentId))
                .<Result<Student, ApplicationError>>map(Result::success)
                .orElseGet(() -> Result.failure(ApplicationError.notFound("Student", studentId.toString()))));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, StudentResourceFromEntityAssembler::toResourceFromEntity, status);
    }
}
