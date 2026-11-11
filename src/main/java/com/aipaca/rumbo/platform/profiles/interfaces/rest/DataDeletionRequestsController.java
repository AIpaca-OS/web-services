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

import com.aipaca.rumbo.platform.profiles.application.commandservices.DataDeletionRequestCommandService;
import com.aipaca.rumbo.platform.profiles.application.queryservices.DataDeletionRequestQueryService;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.DeletionRequestStatus;

@RestController
@RequestMapping(value="/api/v1/data-deletion-requests",produces=MediaType.APPLICATION_JSON_VALUE)
@Tag(name="Data Deletion Requests",description="Student personal-data deletion request lifecycle.")
public class DataDeletionRequestsController {
    private final DataDeletionRequestCommandService commands;
    private final DataDeletionRequestQueryService queries;
    private final ProfilesAuthorization auth;
    public DataDeletionRequestsController(DataDeletionRequestCommandService commands,
            DataDeletionRequestQueryService queries,ProfilesAuthorization auth) {
        this.commands=commands;this.queries=queries;this.auth=auth;
    }
    @PostMapping(consumes=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> submit(@Valid @RequestBody RequestDeletionResource resource) {
        var cmd=ProfilesCommandsFromResourceAssembler.requestDeletion(resource,auth.currentTutor().getId());
        return ResponseEntityAssembler.toResponseEntityFromResult(commands.handle(cmd),CreatedIdResource::new,HttpStatus.CREATED);
    }
    @GetMapping("/mine")
    public ResponseEntity<List<DataDeletionRequestResource>> mine() {
        var list=queries.handle(new GetAllDataDeletionRequestsByRequestedByProfileIdQuery(auth.currentTutor().getId()));
        return ResponseEntity.ok(list.stream()
                .map(DataDeletionRequestResourceFromEntityAssembler::toResourceFromEntity).toList());
    }
    @GetMapping("/{requestId}")
    public ResponseEntity<?> byId(@PathVariable Long requestId) {
        var opt=queries.handle(new GetDataDeletionRequestByIdQuery(requestId));
        if(opt.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(DataDeletionRequestResourceFromEntityAssembler.toResourceFromEntity(opt.get()));
    }
    @PostMapping(value="/{requestId}/reject",consumes=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> reject(@PathVariable Long requestId,@Valid @RequestBody ResolveDeletionResource resource) {
        auth.requireAdmin();
        var cmd=new ResolveDataDeletionRequestCommand(requestId,DeletionRequestStatus.REJECTED,resource.resolutionNote());
        return ResponseEntityAssembler.toResponseEntityFromResult(commands.handle(cmd),CreatedIdResource::new,HttpStatus.OK);
    }
    @PostMapping(value="/{requestId}/approve",consumes=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> approve(@PathVariable Long requestId,@Valid @RequestBody ResolveDeletionResource resource) {
        auth.requireAdmin();
        var cmd=new ResolveDataDeletionRequestCommand(requestId,DeletionRequestStatus.APPROVED,resource.resolutionNote());
        return ResponseEntityAssembler.toResponseEntityFromResult(commands.handle(cmd),CreatedIdResource::new,HttpStatus.OK);
    }
}
