package com.aipaca.rumbo.platform.profiles.domain.model.aggregates;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SchoolTests {
    private static final String NAME = "Colegio San Martin";
    private static final String ADDRESS = "Av. Los Olivos 123";
    private static final String DISTRICT = "San Miguel";

    @Test
    void creationPreservesSchoolInformationWithoutAssigningIdentity() {
        var school = new School(NAME, ADDRESS, DISTRICT);

        assertNull(school.getId());
        assertEquals(NAME, school.getName());
        assertEquals(ADDRESS, school.getAddress());
        assertEquals(DISTRICT, school.getDistrict());
    }

    @Test
    void reconstitutionPreservesPersistedIdentityAndInformation() {
        var school = School.reconstitute(7L, NAME, ADDRESS, DISTRICT);

        assertEquals(7L, school.getId());
        assertEquals(NAME, school.getName());
        assertEquals(ADDRESS, school.getAddress());
        assertEquals(DISTRICT, school.getDistrict());
        assertTrue(school.domainEvents().isEmpty());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -1})
    void reconstitutionRequiresPositiveIdentity(Long id) {
        assertThrows(IllegalArgumentException.class,
                () -> School.reconstitute(id, NAME, ADDRESS, DISTRICT));
    }

    @ParameterizedTest
    @MethodSource("invalidSchoolInformation")
    void creationAndReconstitutionRejectInvalidInformation(String name, String address, String district) {
        assertThrows(IllegalArgumentException.class, () -> new School(name, address, district));
        assertThrows(IllegalArgumentException.class,
                () -> School.reconstitute(7L, name, address, district));
    }

    private static Stream<Arguments> invalidSchoolInformation() {
        var missingValues = Stream.of(null, "", "   ", "\t\n").flatMap(value -> Stream.of(
                Arguments.of(value, ADDRESS, DISTRICT),
                Arguments.of(NAME, value, DISTRICT),
                Arguments.of(NAME, ADDRESS, value)));
        var oversizedValues = Stream.of(
                Arguments.of("n".repeat(151), ADDRESS, DISTRICT),
                Arguments.of(NAME, "a".repeat(256), DISTRICT),
                Arguments.of(NAME, ADDRESS, "d".repeat(101)));
        return Stream.concat(missingValues, oversizedValues);
    }

    @Test
    void creationAndReconstitutionAcceptMaximumFieldLengths() {
        var name = "n".repeat(150);
        var address = "a".repeat(255);
        var district = "d".repeat(100);

        var school = new School(name, address, district);
        var restored = School.reconstitute(7L, name, address, district);

        assertEquals(name, school.getName());
        assertEquals(address, school.getAddress());
        assertEquals(district, school.getDistrict());
        assertEquals(name, restored.getName());
        assertEquals(address, restored.getAddress());
        assertEquals(district, restored.getDistrict());
    }
}
