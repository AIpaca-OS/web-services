package com.aipaca.rumbo.platform.profiles.interfaces.rest;

import com.aipaca.rumbo.platform.profiles.application.commandservices.ProfileCommandService;
import com.aipaca.rumbo.platform.profiles.application.queryservices.ProfileQueryService;
import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.Profile;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.GetProfileByAccountIdQuery;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.GetProfileByIdQuery;
import com.aipaca.rumbo.platform.profiles.interfaces.rest.resources.CreateProfileResource;
import com.aipaca.rumbo.platform.profiles.interfaces.rest.resources.ProfileResource;
import com.aipaca.rumbo.platform.profiles.interfaces.rest.resources.UpdateProfileContactResource;
import com.aipaca.rumbo.platform.profiles.interfaces.rest.transform.CreateProfileCommandFromResourceAssembler;
import com.aipaca.rumbo.platform.profiles.interfaces.rest.transform.ProfileResourceFromEntityAssembler;
import com.aipaca.rumbo.platform.profiles.interfaces.rest.transform.UpdateProfileContactCommandFromResourceAssembler;
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

/**
 * REST controller that exposes profile resources and profile retrieval endpoints.
 */
@RestController
@CrossOrigin(origins = "${app.cors.allowed-origins:*}")
@RequestMapping(value = "/api/v1/profiles", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Profiles", description = "Profile management endpoints")
public class ProfilesController {
    private final ProfileCommandService profileCommandService;
    private final ProfileQueryService profileQueryService;

    /**
     * Constructor
     *
     * @param profileCommandService The {@link ProfileCommandService} instance
     * @param profileQueryService   The {@link ProfileQueryService} instance
     */
    public ProfilesController(ProfileCommandService profileCommandService, ProfileQueryService profileQueryService) {
        this.profileCommandService = profileCommandService;
        this.profileQueryService = profileQueryService;
    }

    /**
     * Create a new profile
     *
     * @param resource The {@link CreateProfileResource} instance
     * @return A {@link ProfileResource} resource for the created profile
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create a new profile", description = "Creates the tutor or driver profile of an account.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Profile created successfully",
                    content = @Content(schema = @Schema(implementation = ProfileResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Conflict - the account already has a profile")
    })
    public ResponseEntity<?> createProfile(@Valid @RequestBody CreateProfileResource resource) {
        var createProfileCommand = CreateProfileCommandFromResourceAssembler.toCommandFromResource(resource);
        return toProfileResponse(profileCommandService.handle(createProfileCommand), HttpStatus.CREATED);
    }

    /**
     * Get a profile by ID
     *
     * @param profileId The profile ID
     * @return A {@link ProfileResource} resource for the profile
     */
    @GetMapping("/{profileId}")
    @Operation(summary = "Get profile by ID", description = "Retrieves a profile by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile found",
                    content = @Content(schema = @Schema(implementation = ProfileResource.class))),
            @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    public ResponseEntity<?> getProfileById(
            @PathVariable @Parameter(description = "Profile unique identifier", example = "1", required = true)
            Long profileId) {
        var profile = profileQueryService.handle(new GetProfileByIdQuery(profileId));
        if (profile.isEmpty()) {
            var error = ApplicationError.notFound("Profile", profileId.toString());
            return ProblemDetailFromApplicationErrorAssembler.toResponseFromApplicationError(error);
        }
        return ResponseEntity.ok(ProfileResourceFromEntityAssembler.toResourceFromEntity(profile.get()));
    }

    /**
     * Get a profile by account ID
     *
     * @param accountId The account ID
     * @return A {@link ProfileResource} resource for the profile
     */
    @GetMapping("/by-account/{accountId}")
    @Operation(summary = "Get profile by account ID", description = "Retrieves the profile that belongs to an account.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile found",
                    content = @Content(schema = @Schema(implementation = ProfileResource.class))),
            @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    public ResponseEntity<?> getProfileByAccountId(
            @PathVariable @Parameter(description = "Account unique identifier", example = "1", required = true)
            Long accountId) {
        var profile = profileQueryService.handle(new GetProfileByAccountIdQuery(accountId));
        if (profile.isEmpty()) {
            var error = ApplicationError.notFound("Profile", "account " + accountId);
            return ProblemDetailFromApplicationErrorAssembler.toResponseFromApplicationError(error);
        }
        return ResponseEntity.ok(ProfileResourceFromEntityAssembler.toResourceFromEntity(profile.get()));
    }

    /**
     * Update the contact of a profile
     *
     * @param profileId The profile ID
     * @param resource  The {@link UpdateProfileContactResource} instance
     * @return A {@link ProfileResource} resource for the updated profile
     */
    @PatchMapping(value = "/{profileId}/contact", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update profile contact", description = "Updates the contact phone number of a profile.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile updated",
                    content = @Content(schema = @Schema(implementation = ProfileResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    public ResponseEntity<?> updateProfileContact(
            @PathVariable @Parameter(description = "Profile unique identifier", example = "1", required = true)
            Long profileId,
            @Valid @RequestBody UpdateProfileContactResource resource) {
        var command = UpdateProfileContactCommandFromResourceAssembler.toCommandFromResource(profileId, resource);
        return toProfileResponse(profileCommandService.handle(command), HttpStatus.OK);
    }

    private ResponseEntity<?> toProfileResponse(Result<Long, ApplicationError> commandResult, HttpStatus status) {
        var result = commandResult.flatMap(profileId -> profileQueryService.handle(new GetProfileByIdQuery(profileId))
                .<Result<Profile, ApplicationError>>map(Result::success)
                .orElseGet(() -> Result.failure(ApplicationError.notFound("Profile", profileId.toString()))));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, ProfileResourceFromEntityAssembler::toResourceFromEntity, status);
    }
}
