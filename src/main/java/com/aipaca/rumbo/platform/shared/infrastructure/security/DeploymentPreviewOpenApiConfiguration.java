package com.aipaca.rumbo.platform.shared.infrastructure.security;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Render preview Swagger authorization for temporary shared test key.
 * This is NOT a substitute for verified JWT roles and relationships.
 */
@Configuration
@Profile("prod")
@SecurityScheme(
        name = "RumboPreviewKey",
        type = SecuritySchemeType.APIKEY,
        in = SecuritySchemeIn.HEADER,
        paramName = "X-Rumbo-Preview-Key",
        description = "Private course preview key from Render; never store in Angular source")
@OpenAPIDefinition(security = @SecurityRequirement(name = "RumboPreviewKey"))
public class DeploymentPreviewOpenApiConfiguration {
}
