package com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa;

import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.entities.ProfilePersistenceEntity;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.entities.DataDeletionRequestPersistenceEntity;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.entities.ServiceEnrollmentPersistenceEntity;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.entities.StudentPersistenceEntity;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.entities.TutorStudentRelationshipPersistenceEntity;
import com.aipaca.rumbo.platform.shared.infrastructure.persistence.jpa.configuration.strategy.SnakeCaseWithPluralizedTablePhysicalNamingStrategy;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

import static org.mockito.Mockito.*;

/** Shared offline mapping setup; it never loads application datasource settings. */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class OfflineProfilesMappingTestSupport {
    protected MetadataImplementor metadata;
    protected SessionFactory sessionFactory;
    private StandardServiceRegistry registry;
    private ConnectionProvider connections;

    @BeforeAll
    void buildOfflineMapping() throws Exception {
        connections = mock(ConnectionProvider.class);
        when(connections.getConnection()).thenThrow(new AssertionError("JDBC connections are forbidden in this test"));
        registry = new StandardServiceRegistryBuilder()
                .addService(ConnectionProvider.class, connections)
                .applySetting("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
                .applySetting("hibernate.boot.allow_jdbc_metadata_access", false)
                .applySetting("hibernate.hbm2ddl.auto", "none")
                .applySetting("jakarta.persistence.schema-generation.database.action", "none")
                .applySetting("hibernate.physical_naming_strategy",
                        SnakeCaseWithPluralizedTablePhysicalNamingStrategy.class.getName())
                .build();
        metadata = (MetadataImplementor) new MetadataSources(registry)
                .addAnnotatedClass(ProfilePersistenceEntity.class)
                .addAnnotatedClass(TutorStudentRelationshipPersistenceEntity.class)
                .addAnnotatedClass(StudentPersistenceEntity.class)
                .addAnnotatedClass(ServiceEnrollmentPersistenceEntity.class)
                .addAnnotatedClass(DataDeletionRequestPersistenceEntity.class).buildMetadata();
        metadata.validate();
        sessionFactory = metadata.buildSessionFactory();
    }

    @AfterAll
    void closeOfflineMapping() throws Exception {
        try {
            if (sessionFactory != null) sessionFactory.close();
        } finally {
            if (registry != null) StandardServiceRegistryBuilder.destroy(registry);
        }
        if (connections != null) verify(connections, never()).getConnection();
    }
}
