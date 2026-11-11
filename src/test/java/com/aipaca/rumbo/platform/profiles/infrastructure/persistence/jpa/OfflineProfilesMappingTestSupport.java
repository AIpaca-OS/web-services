package com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa;

import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.entities.*;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

/** Builds real JPA metadata for mapping tests using the local/CI PostgreSQL datasource. */
abstract class OfflineProfilesMappingTestSupport {
    protected static Metadata metadata;
    protected static SessionFactory sessionFactory;
    private static StandardServiceRegistry registry;

    @BeforeAll
    static void initializeMetadata() {
        registry = new StandardServiceRegistryBuilder()
                .applySetting("hibernate.connection.driver_class", "org.postgresql.Driver")
                .applySetting("hibernate.connection.url",
                        System.getenv().getOrDefault("SPRING_DATASOURCE_URL",
                                "jdbc:postgresql://localhost:5432/rumbo"))
                .applySetting("hibernate.connection.username",
                        System.getenv().getOrDefault("SPRING_DATASOURCE_USERNAME", "rumbo"))
                .applySetting("hibernate.connection.password",
                        System.getenv().getOrDefault("SPRING_DATASOURCE_PASSWORD", ""))
                .applySetting("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
                .applySetting("hibernate.physical_naming_strategy",
                        "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy")
                .applySetting("hibernate.hbm2ddl.auto", "none")
                .build();
        metadata = new MetadataSources(registry)
                .addAnnotatedClass(ProfilePersistenceEntity.class)
                .addAnnotatedClass(SchoolPersistenceEntity.class)
                .addAnnotatedClass(StudentPersistenceEntity.class)
                .addAnnotatedClass(TutorStudentRelationshipPersistenceEntity.class)
                .buildMetadata();
        sessionFactory = metadata.buildSessionFactory();
    }

    @AfterAll
    static void closeMetadata() {
        if (sessionFactory != null) sessionFactory.close();
        if (registry != null) StandardServiceRegistryBuilder.destroy(registry);
    }
}
