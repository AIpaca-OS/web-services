package com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.assemblers;

import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.Student;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.RegisterStudentCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.RelationshipType;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.StudentStatus;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class StudentPersistenceAssemblerTests {
    private static final Instant AT = Instant.parse("2026-11-10T12:00:00Z");
    private static final LocalDate BIRTH = LocalDate.of(2016, 3, 10);

    @Test void studentRegistrationIncludesInitialTutorAndUnassignedChildIdentity() {
        var student = new Student(new RegisterStudentCommand(1L, "Ana", "Perez", BIRTH,
                RelationshipType.MOTHER), AT);
        var entity = StudentPersistenceAssembler.toPersistenceFromDomain(student);
        assertNull(entity.getId());
        assertNull(entity.getVersion());
        assertEquals(0L, entity.getAuthorizationRevision());
        assertEquals(1, entity.getTutorRelationships().size());
        assertSame(entity, entity.getTutorRelationships().getFirst().getStudent());
        assertEquals(1L, entity.getTutorRelationships().getFirst().getTutorProfileId());
    }

    @Test void persistedStudentRoundTripPreservesVersionAndCompleteHistory() {
        var student = new Student(new RegisterStudentCommand(1L, "Ana", "Perez", BIRTH,
                RelationshipType.MOTHER), AT);
        student.authorizeTutor(2L, RelationshipType.FATHER, 1L, AT.plusSeconds(1));
        student.revokeTutor(2L, 1L, AT.plusSeconds(2));
        student.authorizeTutor(2L, RelationshipType.FATHER, 1L, AT.plusSeconds(3));
        var entity = StudentPersistenceAssembler.toPersistenceFromDomain(student);
        entity.setId(7L);
        entity.setVersion(2L);
        assertEquals(3L, entity.getAuthorizationRevision());
        long id = 11L;
        for (var child : entity.getTutorRelationships()) child.setId(id++);
        var restored = StudentPersistenceAssembler.toDomainFromPersistence(entity);
        assertEquals(7L, restored.getId());
        assertEquals(2L, restored.getVersion());
        assertEquals(3L, restored.getAuthorizationRevision());
        assertEquals(3, restored.getTutorRelationships().size());
        assertEquals(2, restored.getTutorRelationships().stream().filter(r -> r.isAuthorized()).count());
        assertEquals(1, restored.getTutorRelationships().stream().filter(r -> !r.isAuthorized()).count());
        assertEquals(StudentStatus.ACTIVE, restored.getStatus());
    }

    @Test void corruptedStudentWithoutAuthorizedTutorFailsToReconstitute() {
        var student = new Student(new RegisterStudentCommand(1L, "Ana", "Perez", BIRTH,
                RelationshipType.MOTHER), AT);
        var entity = StudentPersistenceAssembler.toPersistenceFromDomain(student);
        entity.setId(7L);
        entity.setVersion(0L);
        entity.getTutorRelationships().getFirst().setId(9L);
        entity.getTutorRelationships().getFirst().setAuthorizationStatus(
                com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.AuthorizationStatus.REVOKED);
        entity.getTutorRelationships().getFirst().setRevokedAt(AT.plusSeconds(1));
        assertThrows(IllegalArgumentException.class, () -> StudentPersistenceAssembler.toDomainFromPersistence(entity));
    }
}
