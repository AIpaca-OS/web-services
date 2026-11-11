package com.aipaca.rumbo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class RumboWebServicesApplication {

	public static void main(String[] args) {
		SpringApplication.run(RumboWebServicesApplication.class, args);
	}

}
