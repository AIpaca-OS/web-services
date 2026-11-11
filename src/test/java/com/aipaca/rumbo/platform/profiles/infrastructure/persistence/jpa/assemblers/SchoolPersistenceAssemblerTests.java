package com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.assemblers;

import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.School;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class SchoolPersistenceAssemblerTests {
    private static final String NAME = "Colegio San Martin";
    private static final String ADDRESS = "Av. Los Olivos 123";
    private static final String DISTRICT = "San Miguel";

    @Test
    void newSchoolRetainsUnassignedIdentityUntilPersistenceReturnsOne() {
        var school = new School(NAME, ADDRESS, DISTRICT);
        var entity = SchoolPersistenceAssembler.toPersistenceFromDomain(school);
        assertNull(entity.getId());
        assertEquals(NAME, entity.getName());
        assertEquals(ADDRESS, entity.getAddress());
        assertEquals(DISTRICT, entity.getDistrict());
        entity.setId(7L);
        var restored = SchoolPersistenceAssembler.toDomainFromPersistence(entity);
        assertEquals(7L, restored.getId());
        assertNull(school.getId());
        assertTrue(restored.domainEvents().isEmpty());
    }

    @Test
    void reconstitutionPreservesPersistedIdentityAndAllInformation() {
        var school = School.reconstitute(7L, NAME, ADDRESS, DISTRICT);
        var restored = SchoolPersistenceAssembler.toDomainFromPersistence(
                SchoolPersistenceAssembler.toPersistenceFromDomain(school));
        assertEquals(school.getId(), restored.getId());
        assertEquals(school.getName(), restored.getName());
        assertEquals(school.getAddress(), restored.getAddress());
        assertEquals(school.getDistrict(), restored.getDistrict());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -1})
    void reconstitutionRejectsInvalidPersistedIdentity(Long id) {
        var entity = SchoolPersistenceAssembler.toPersistenceFromDomain(new School(NAME, ADDRESS, DISTRICT));
        entity.setId(id);
        assertThrows(IllegalArgumentException.class, () -> SchoolPersistenceAssembler.toDomainFromPersistence(entity));
    }

    @Test
    void storedInvalidInformationCannotBypassDomainValidation() {
        var entity = SchoolPersistenceAssembler.toPersistenceFromDomain(School.reconstitute(7L, NAME, ADDRESS, DISTRICT));
        entity.setDistrict("d".repeat(101));
        assertThrows(IllegalArgumentException.class, () -> SchoolPersistenceAssembler.toDomainFromPersistence(entity));
    }

    @Test
    void maximumSupportedLengthsArePreserved() {
        var school = School.reconstitute(7L, "n".repeat(150), "a".repeat(255), "d".repeat(100));
        var restored = SchoolPersistenceAssembler.toDomainFromPersistence(
                SchoolPersistenceAssembler.toPersistenceFromDomain(school));
        assertEquals(school.getName(), restored.getName());
        assertEquals(school.getAddress(), restored.getAddress());
        assertEquals(school.getDistrict(), restored.getDistrict());
    }
}
