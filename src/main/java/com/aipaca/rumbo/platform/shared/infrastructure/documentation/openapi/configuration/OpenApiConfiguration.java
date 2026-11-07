package com.aipaca.rumbo.platform.shared.infrastructure.documentation.openapi.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {
    @Bean
    public OpenAPI rumboOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Rumbo Web Services API")
                        .description("REST API for school transportation management")
                        .version("v1"));
    }
}