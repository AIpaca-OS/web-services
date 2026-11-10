package com.aipaca.rumbo.platform.shared.infrastructure.documentation.openapi.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfiguration {
    // Properties

    // Rumbo Web Services API
    @Value("${spring.application.name}")
    String applicationName;

    // REST API for school transportation management
    @Value("${documentation.application.description}")
    String applicationDescription;

    // v1.0.0
    @Value("${documentation.application.version}")
    String applicationVersion;

    /**
     * Builds the OpenAPI document used by Swagger UI and client generation tools.
     * @return configured OpenAPI descriptor
     */
    @Bean
    public OpenAPI rumboOpenApi() {

        // General configuration
        var openApi = new OpenAPI();
        openApi
                .info(new Info()
                        .title(this.applicationName)
                        .description(this.applicationDescription)
                        .version(this.applicationVersion)
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .externalDocs(new ExternalDocumentation()
                        .description("Rumbo wiki Documentation")
                        .url("https://rumbo.wiki.github.io/docs"));

        // Add server configurations
        // IMPORTANT: Change when importing new server ports.
        openApi.servers(List.of(
                new Server()
                        .url("http://localhost:8092")
                        .description("Local Development Environment"),
                new Server()
                        .url("https://staging-api.acme-learning.com")
                        .description("Staging Environment"),
                new Server()
                        .url("https://api.acme-learning.com")
                        .description("Production Environment")
        ));

        // Add a security scheme
        final String securitySchemeName = "bearerAuth";

        openApi.addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT Bearer token for API authentication")));

        return openApi;
    }
}