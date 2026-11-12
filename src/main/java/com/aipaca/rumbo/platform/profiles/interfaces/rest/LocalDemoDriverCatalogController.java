package com.aipaca.rumbo.platform.profiles.interfaces.rest;

import com.aipaca.rumbo.platform.profiles.application.internal.access.ActorIdentity;
import com.aipaca.rumbo.platform.profiles.application.internal.access.LocalDemoActorIdentity;
import com.aipaca.rumbo.platform.profiles.application.internal.access.ProfilesAccessException;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.AccountId;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.ProfileType;
import com.aipaca.rumbo.platform.profiles.domain.repositories.ProfileRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * Presents only a seeded fictional driver to help tutors request service enrollment
 * in the local Sprint 3 demonstration. Never registered on public/production profiles.
 * ActorIdentity also enforces loopback-only requests.
 */
@RestController
@org.springframework.context.annotation.Profile("local")
@ConditionalOnProperty(prefix = "app.profiles.demo", name = "enabled", havingValue = "true")
@RequestMapping("/api/v1/local-demo/drivers")
@Tag(name = "Local Demo Profiles", description = "Fictitious driver catalog; disabled outside local demo")
public class LocalDemoDriverCatalogController {
    public record DemoDriver(Long id, String displayName) {}

    private final ProfileRepository profiles;
    private final ActorIdentity actor;

    public LocalDemoDriverCatalogController(ProfileRepository profiles, ActorIdentity actor) {
        this.profiles = profiles;
        this.actor = actor;
    }

    @GetMapping
    @Operation(summary = "Show the seeded fictional driver available for local tutor enrollment")
    public List<DemoDriver> drivers() {
        if (!actor.hasRole("TUTOR")) {
            throw new ProfilesAccessException("Tutor demo role is required", false);
        }
        return profiles.findByAccountId(new AccountId(LocalDemoActorIdentity.DRIVER_ACCOUNT_ID))
                .filter(p -> p.getProfileType() == ProfileType.DRIVER)
                .map(p -> List.of(new DemoDriver(p.getId(), "Conductor escolar")))
                .orElseGet(List::of);
    }
}
