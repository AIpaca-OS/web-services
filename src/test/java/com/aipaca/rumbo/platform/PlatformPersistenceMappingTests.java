package com.aipaca.rumbo.platform;

import com.aipaca.rumbo.platform.shared.infrastructure.persistence.jpa.configuration.strategy.SnakeCaseWithPluralizedTablePhysicalNamingStrategy;
import jakarta.persistence.Entity;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.mapping.Table;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import org.junit.jupiter.api.TestInstance;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.List;

import static io.github.encryptorcode.pluralize.Pluralize.pluralize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Builds the Hibernate metadata of every persistence entity of the platform without a database
 * and checks the naming convention and the foreign keys inside each bounded context.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PlatformPersistenceMappingTests {
    private MetadataImplementor metadata;
    private StandardServiceRegistry registry;

    @BeforeAll
    void buildOfflineMapping() throws Exception {
        var connections = mock(ConnectionProvider.class);
        when(connections.getConnection()).thenThrow(new AssertionError("JDBC connections are forbidden in this test"));
        registry = new StandardServiceRegistryBuilder()
                .addService(ConnectionProvider.class, connections)
                .applySetting("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
                .applySetting("hibernate.boot.allow_jdbc_metadata_access", false)
                .applySetting("hibernate.hbm2ddl.auto", "none")
                .applySetting("hibernate.physical_naming_strategy",
                        SnakeCaseWithPluralizedTablePhysicalNamingStrategy.class.getName())
                .build();
        var sources = new MetadataSources(registry);
        var scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Entity.class));
        for (var candidate : scanner.findCandidateComponents("com.aipaca.rumbo.platform")) {
            sources.addAnnotatedClass(Class.forName(candidate.getBeanClassName()));
        }
        metadata = (MetadataImplementor) sources.buildMetadata();
        metadata.validate();
    }

    @AfterAll
    void close() {
        if (registry != null) StandardServiceRegistryBuilder.destroy(registry);
    }

    private Table table(String name) {
        return metadata.collectTableMappings().stream()
                .filter(table -> table.getName().equals(name))
                .findFirst().orElseThrow(() -> new AssertionError("Missing table " + name));
    }

    private boolean hasTable(String name) {
        return metadata.collectTableMappings().stream().anyMatch(table -> table.getName().equals(name));
    }

    private void assertForeignKey(String table, String column, String referencedTable) {
        var found = table(table).getForeignKeyCollection().stream()
                .anyMatch(fk -> fk.getColumns().size() == 1
                        && fk.getColumns().getFirst().getName().equals(column)
                        && fk.getReferencedTable().getName().equals(referencedTable));
        assertTrue(found, "%s.%s must reference %s".formatted(table, column, referencedTable));
    }

    @Test
    void everyTableNameIsPluralSnakeCase() {
        var names = metadata.collectTableMappings().stream().map(Table::getName).toList();
        // Only migrated contexts are required; future bounded contexts are checked when merged.
        assertTrue(names.containsAll(List.of("vehicles", "profiles", "students", "plans", "subscriptions")),
                names.toString());
        for (var name : names) {
            assertTrue(name.matches("[a-z][a-z0-9_]*"), name);
            assertEquals(pluralize(name), name, name + " must be plural");
        }
    }

    @Test
    void routeChildrenAreForeignKeys() {
        assumeTrue(hasTable("routes") && hasTable("route_stops")
                        && hasTable("route_assignments") && hasTable("route_schedules")
                        && hasTable("trips") && hasTable("trip_passengers"),
                "Routes and Trips bounded contexts are not merged yet");
        assertForeignKey("route_stops", "route_id", "routes");
        assertForeignKey("route_assignments", "route_id", "routes");
        assertForeignKey("route_assignments", "stop_id", "route_stops");
        assertForeignKey("route_schedules", "route_id", "routes");
        assertForeignKey("trip_passengers", "trip_id", "trips");
    }

    @Test
    void incidentChildrenAndDelayUpdatesAreForeignKeys() {
        assumeTrue(hasTable("incident_acknowledgements") && hasTable("incidents") && hasTable("delays"),
                "Incidents and Delays bounded contexts are not merged yet");
        assertForeignKey("incident_acknowledgements", "incident_id", "incidents");
        assertForeignKey("delays", "previous_delay_id", "delays");
    }

    @Test
    void referencesToOtherBoundedContextsAreConvertedIdentitiesNotForeignKeys() {
        for (var name : List.of("routes", "notifications", "trips", "vehicles",
                "incidents", "subscriptions")) {
            if (hasTable(name)) {
                assertTrue(table(name).getForeignKeyCollection().isEmpty(),
                        name + " must reference other bounded contexts by ID, not by FK");
            }
        }

    }
}
