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

import com.aipaca.rumbo.platform.profiles.application.queryservices.SchoolQueryService;

@RestController
@RequestMapping(value="/api/v1/schools",produces=MediaType.APPLICATION_JSON_VALUE)
@Tag(name="Schools",description="Read-only catalog of schools")
public class SchoolsController {
    private final SchoolQueryService queries;
    public SchoolsController(SchoolQueryService queries) {this.queries=queries;}
    @GetMapping
    public ResponseEntity<List<SchoolResource>> all() {
        return ResponseEntity.ok(queries.handle(new GetAllSchoolsQuery()).stream()
                .map(SchoolResourceFromEntityAssembler::toResourceFromEntity).toList());
    }
    @GetMapping("/{schoolId}")
    public ResponseEntity<?> byId(@PathVariable Long schoolId) {
        var found=queries.handle(new GetSchoolByIdQuery(schoolId));
        if(found.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(SchoolResourceFromEntityAssembler.toResourceFromEntity(found.get()));
    }
}
