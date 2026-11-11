package com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.assemblers;

import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.Student;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.RegisterStudentCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.PersonName;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.RelationshipType;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.StudentStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class StudentPersistenceAssemblerTests {
    private static final LocalDate BIRTH_DATE = LocalDate.of(2016, 3, 10);
    private static final Instant ANONYMIZED_AT = Instant.parse("2026-11-10T12:00:00Z");

    @Test
    void newStudentKeepsUnassignedIdentityUntilPersistenceReturnsOne() {
        var student = new Student(new RegisterStudentCommand(
                1L, "Ana", "Perez", BIRTH_DATE, null, RelationshipType.MOTHER));
        var entity = StudentPersistenceAssembler.toPersistenceFromDomain(student);
        assertNull(entity.getId());
        assertEquals(StudentStatus.ACTIVE, entity.getStatus());
        assertNull(entity.getSchoolId());
        assertNull(entity.getAnonymizedAt());
        entity.setId(7L);

        var restored = StudentPersistenceAssembler.toDomainFromPersistence(entity);
        assertEquals(7L, restored.getId());
        assertNull(student.getId());
        assertTrue(restored.domainEvents().isEmpty());
    }

    @ParameterizedTest
    @MethodSource("storedStates")
    void reconstitutionPreservesCompleteStateWithoutAnonymizingData(
            StudentStatus status, Long schoolId, Instant anonymizedAt
    ) {
        var student = Student.reconstitute(7L, new PersonName("Ana", "Perez"),
                BIRTH_DATE, schoolId, status, anonymizedAt);
        var entity = StudentPersistenceAssembler.toPersistenceFromDomain(student);
        var restored = StudentPersistenceAssembler.toDomainFromPersistence(entity);
        assertEquals(student.getId(), restored.getId());
        assertEquals(student.getName(), restored.getName());
        assertEquals(BIRTH_DATE, restored.getBirthDate());
        assertEquals(schoolId, restored.getSchoolId());
        assertEquals(status, restored.getStatus());
        assertEquals(anonymizedAt, restored.getAnonymizedAt());
    }

    private static Stream<Arguments> storedStates() {
        return Stream.of(
                Arguments.of(StudentStatus.ACTIVE, null, null),
                Arguments.of(StudentStatus.ACTIVE, 2L, null),
                Arguments.of(StudentStatus.INACTIVE, null, ANONYMIZED_AT),
                Arguments.of(StudentStatus.INACTIVE, 2L, null));
    }

    @Test
    void invalidStoredInformationCannotBypassDomainValidation() {
        var student = Student.reconstitute(7L, new PersonName("Ana", "Perez"),
                BIRTH_DATE, 2L, StudentStatus.ACTIVE, null);
        var entity = StudentPersistenceAssembler.toPersistenceFromDomain(student);
        entity.setSchoolId(0L);
        assertThrows(IllegalArgumentException.class,
                () -> StudentPersistenceAssembler.toDomainFromPersistence(entity));
        entity.setSchoolId(null);
        entity.setBirthDate(null);
        assertThrows(NullPointerException.class,
                () -> StudentPersistenceAssembler.toDomainFromPersistence(entity));
    }
}
