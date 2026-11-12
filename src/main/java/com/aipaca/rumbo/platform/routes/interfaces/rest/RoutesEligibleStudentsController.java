
package com.aipaca.rumbo.platform.routes.interfaces.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("platformRoutesEligibleStudentsController")
@CrossOrigin(origins = "${app.cors.allowed-origins:*}")
@RequestMapping(
        value = "/api/v1/routes",
        produces = MediaType.APPLICATION_JSON_VALUE
)
@Tag(
        name = "Routes",
        description = "School route planning endpoints"
)
public class RoutesEligibleStudentsController {

    @GetMapping("/eligible-students")
    @Operation(
            summary = "Get eligible students",
            description = "Requires authenticated Profiles and IAM integration."
    )
    @ApiResponse(
            responseCode = "503",
            description = "Student authorization service not available"
    )
    public ResponseEntity<ProblemDetail> getEligibleStudents() {

        var problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.SERVICE_UNAVAILABLE,
                "El catalogo de estudiantes requiere " +
                        "la integracion de Profiles e IAM/JWT."
        );

        problem.setTitle(
                "Catalogo de estudiantes no disponible"
        );

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(problem);
    }
}
