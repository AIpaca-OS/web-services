package com.aipaca.rumbo.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Bootstrap class for the Learning Center Platform application.
 * <p>Initializes Spring Boot autoconfiguration and JPA auditing infrastructure.</p>
 */
@EnableJpaAuditing
@SpringBootApplication
public class RumboWebServicesApplication {

	public static void main(String[] args) {
		SpringApplication.run(RumboWebServicesApplication.class, args);
	}

}
