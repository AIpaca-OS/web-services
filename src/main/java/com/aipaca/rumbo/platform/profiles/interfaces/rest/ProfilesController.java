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

import com.aipaca.rumbo.platform.profiles.application.commandservices.ProfileCommandService;
import com.aipaca.rumbo.platform.profiles.application.queryservices.ProfileQueryService;

@RestController
@RequestMapping(value="/api/v1/profiles",produces=MediaType.APPLICATION_JSON_VALUE)
@Tag(name="Profiles",description="Authenticated self-profile operations (IAM identity required)")
public class ProfilesController {
    private final ProfileCommandService commands;
    private final ProfileQueryService queries;
    private final ProfilesAuthorization auth;
    public ProfilesController(ProfileCommandService commands,ProfileQueryService queries,ProfilesAuthorization auth) {
        this.commands=commands;this.queries=queries;this.auth=auth;
    }
    @PostMapping(consumes=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(@Valid @RequestBody CreateProfileResource resource) {
        var command=ProfilesCommandsFromResourceAssembler.createProfile(resource,auth.currentAccountId());
        return ResponseEntityAssembler.toResponseEntityFromResult(commands.handle(command),CreatedIdResource::new,HttpStatus.CREATED);
    }
    @GetMapping("/me")
    public ResponseEntity<?> getMine() {
        var p=queries.handle(new GetProfileByAccountIdQuery(auth.currentAccountId()));
        if(p.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(ProfileResourceFromEntityAssembler.toResourceFromEntity(p.get()));
    }
    @GetMapping("/{profileId}")
    public ResponseEntity<?> getById(@PathVariable Long profileId) {
        var p=queries.handle(new GetProfileByIdQuery(profileId));
        if(p.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(ProfileResourceFromEntityAssembler.toResourceFromEntity(p.get()));
    }
    @PatchMapping(value="/me/contact",consumes=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateContact(@Valid @RequestBody UpdateProfileContactResource resource) {
        Long profileId=auth.currentProfile().getId();
        return ResponseEntityAssembler.toResponseEntityFromResult(
                commands.handle(new UpdateProfileContactCommand(profileId,resource.phoneNumber())),
                CreatedIdResource::new,HttpStatus.OK);
    }
}
