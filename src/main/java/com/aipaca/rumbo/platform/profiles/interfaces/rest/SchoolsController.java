package com.aipaca.rumbo.platform.profiles.interfaces.rest;

import com.aipaca.rumbo.platform.profiles.application.queryservices.SchoolQueryService;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.GetAllSchoolsQuery;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.GetSchoolByIdQuery;
import com.aipaca.rumbo.platform.profiles.interfaces.rest.resources.SchoolResource;
import com.aipaca.rumbo.platform.profiles.interfaces.rest.transform.SchoolResourceFromEntityAssembler;
import com.aipaca.rumbo.platform.shared.application.result.ApplicationError;
import com.aipaca.rumbo.platform.shared.interfaces.rest.transform.ProblemDetailFromApplicationErrorAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller that exposes the read-only catalog of schools.
 */
@RestController
@CrossOrigin(origins = "${app.cors.allowed-origins:*}")
@RequestMapping(value = "/api/v1/schools", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Schools", description = "School catalog endpoints")
public class SchoolsController {
    private final SchoolQueryService schoolQueryService;

    /**
     * Constructor
     *
     * @param schoolQueryService The {@link SchoolQueryService} instance
     */
    public SchoolsController(SchoolQueryService schoolQueryService) {
        this.schoolQueryService = schoolQueryService;
    }

    /**
     * Get all schools
     *
     * @return A list of {@link SchoolResource} resources
     */
    @GetMapping
    @Operation(summary = "Get all schools", description = "Retrieves the catalog of schools.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Schools found",
                    content = @Content(schema = @Schema(implementation = SchoolResource.class)))
    })
    public ResponseEntity<List<SchoolResource>> getAllSchools() {
        var schools = schoolQueryService.handle(new GetAllSchoolsQuery());
        return ResponseEntity.ok(schools.stream().map(SchoolResourceFromEntityAssembler::toResourceFromEntity).toList());
    }

    /**
     * Get a school by ID
     *
     * @param schoolId The school ID
     * @return A {@link SchoolResource} resource for the school
     */
    @GetMapping("/{schoolId}")
    @Operation(summary = "Get school by ID", description = "Retrieves a school by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "School found",
                    content = @Content(schema = @Schema(implementation = SchoolResource.class))),
            @ApiResponse(responseCode = "404", description = "School not found")
    })
    public ResponseEntity<?> getSchoolById(
            @PathVariable @Parameter(description = "School unique identifier", example = "1", required = true)
            Long schoolId) {
        var school = schoolQueryService.handle(new GetSchoolByIdQuery(schoolId));
        if (school.isEmpty()) {
            var error = ApplicationError.notFound("School", schoolId.toString());
            return ProblemDetailFromApplicationErrorAssembler.toResponseFromApplicationError(error);
        }
        return ResponseEntity.ok(SchoolResourceFromEntityAssembler.toResourceFromEntity(school.get()));
    }
}
