package com.aipaca.rumbo.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Bootstrap class for the Rumbo Web Services platform.
 *
 * <p>Initializes Spring Boot auto-configuration and JPA auditing infrastructure.
 * Every bounded context lives under {@code com.aipaca.rumbo.platform}.</p>
 */
@EnableJpaAuditing
@SpringBootApplication
public class RumboWebServicesApplication {

    public static void main(String[] args) {
        SpringApplication.run(RumboWebServicesApplication.class, args);
    }

}
