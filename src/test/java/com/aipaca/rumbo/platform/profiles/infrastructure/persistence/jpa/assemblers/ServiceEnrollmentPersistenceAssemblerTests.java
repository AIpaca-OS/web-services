package com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.assemblers;

import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.ServiceEnrollmentStatus;
import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.ServiceEnrollment;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.RequestServiceEnrollmentCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ServiceEnrollmentPersistenceAssemblerTests {
    private static final Instant REQUESTED = Instant.parse("2026-11-10T12:00:00Z");
    private static final Instant ACCEPTED = REQUESTED.plusSeconds(60);
    private static final Instant ENDED = ACCEPTED.plusSeconds(60);

    @Test
    void newEnrollmentHasNoIdentityAndKeepsOriginalRequesterAndRequestTime() {
        var enrollment = new ServiceEnrollment(new RequestServiceEnrollmentCommand(7L, 8L, 9L), REQUESTED);
        var entity = ServiceEnrollmentPersistenceAssembler.toPersistenceFromDomain(enrollment);
        assertNull(entity.getId());
        assertEquals(7L, entity.getStudentId());
        assertEquals(8L, entity.getDriverProfileId());
        assertEquals(9L, entity.getRequestedByProfileId());
        assertEquals(ServiceEnrollmentStatus.PENDING, entity.getStatus());
        assertEquals(REQUESTED, entity.getRequestedAt());
        assertNull(entity.getAcceptedAt());
        assertNull(entity.getEndedAt());
        entity.setId(25L);
        var restored = ServiceEnrollmentPersistenceAssembler.toDomainFromPersistence(entity);
        assertEquals(25L, restored.getId());
        assertNull(enrollment.getId());
        assertFalse(restored.isActive());
    }

    @ParameterizedTest
    @MethodSource("allValidStates")
    void roundTripPreservesAllValidStatesAndFullTimestamps(ServiceEnrollmentStatus status, Instant acceptedAt, Instant endedAt) {
        var enrollment = ServiceEnrollment.reconstitute(25L, 7L, 8L, 9L, status, REQUESTED, acceptedAt, endedAt);
        var restored = ServiceEnrollmentPersistenceAssembler.toDomainFromPersistence(
                ServiceEnrollmentPersistenceAssembler.toPersistenceFromDomain(enrollment));
        assertEquals(enrollment.getId(), restored.getId());
        assertEquals(enrollment.getStudentId(), restored.getStudentId());
        assertEquals(enrollment.getDriverProfileId(), restored.getDriverProfileId());
        assertEquals(enrollment.getRequestedByProfileId(), restored.getRequestedByProfileId());
        assertEquals(enrollment.getStatus(), restored.getStatus());
        assertEquals(enrollment.getRequestedAt(), restored.getRequestedAt());
        assertEquals(enrollment.getAcceptedAt(), restored.getAcceptedAt());
        assertEquals(enrollment.getEndedAt(), restored.getEndedAt());
        assertEquals(ServiceEnrollmentStatus.ACCEPTED.equals(status), restored.isActive());
        assertTrue(restored.domainEvents().isEmpty());
    }

    private static Stream<Arguments> allValidStates() {
        return Stream.of(
                Arguments.of(ServiceEnrollmentStatus.PENDING, null, null),
                Arguments.of(ServiceEnrollmentStatus.ACCEPTED, ACCEPTED, null),
                Arguments.of(ServiceEnrollmentStatus.ENDED, ACCEPTED, ENDED));
    }

    @Test
    void endedEnrollmentCanBeStoredWithoutChangingItsHistoricalIdentity() {
        var enrollment = new ServiceEnrollment(new RequestServiceEnrollmentCommand(7L, 8L, 9L), REQUESTED);
        enrollment.accept(ACCEPTED);
        enrollment.end(ENDED);
        var entity = ServiceEnrollmentPersistenceAssembler.toPersistenceFromDomain(enrollment);
        entity.setId(25L);
        var restored = ServiceEnrollmentPersistenceAssembler.toDomainFromPersistence(entity);
        assertEquals(ServiceEnrollmentStatus.ENDED, restored.getStatus());
        assertEquals(REQUESTED, restored.getRequestedAt());
        assertEquals(ACCEPTED, restored.getAcceptedAt());
        assertEquals(ENDED, restored.getEndedAt());
    }

    @Test
    void nullEntityYieldsNull() {
        assertNull(ServiceEnrollmentPersistenceAssembler.toDomainFromPersistence(null));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -1})
    void storedEnrollmentRequiresValidPersistedIdentity(Long id) {
        var entity = ServiceEnrollmentPersistenceAssembler.toPersistenceFromDomain(
                ServiceEnrollment.reconstitute(25L, 7L, 8L, 9L, ServiceEnrollmentStatus.PENDING, REQUESTED, null, null));
        entity.setId(id);
        assertThrows(IllegalArgumentException.class,
                () -> ServiceEnrollmentPersistenceAssembler.toDomainFromPersistence(entity));
    }

    @Test
    void corruptedStoredStateIsRejectedByDomainInvariants() {
        var entity = ServiceEnrollmentPersistenceAssembler.toPersistenceFromDomain(
                ServiceEnrollment.reconstitute(25L, 7L, 8L, 9L, ServiceEnrollmentStatus.PENDING, REQUESTED, null, null));
        entity.setStatus(ServiceEnrollmentStatus.ACCEPTED);
        assertThrows(IllegalArgumentException.class,
                () -> ServiceEnrollmentPersistenceAssembler.toDomainFromPersistence(entity));
        entity.setStatus(ServiceEnrollmentStatus.ENDED);
        entity.setAcceptedAt(ACCEPTED);
        entity.setEndedAt(REQUESTED);
        assertThrows(IllegalArgumentException.class,
                () -> ServiceEnrollmentPersistenceAssembler.toDomainFromPersistence(entity));
        entity.setStatus(ServiceEnrollmentStatus.CANCELLED);
        entity.setAcceptedAt(null);
        entity.setEndedAt(null);
        assertThrows(IllegalArgumentException.class,
                () -> ServiceEnrollmentPersistenceAssembler.toDomainFromPersistence(entity));
    }

    @Test void cancelledEnrollmentPersistsItsTimestampAndRemainsTerminal() {
        var enrollment = new ServiceEnrollment(new RequestServiceEnrollmentCommand(7L, 8L, 9L), REQUESTED);
        enrollment.cancel(9L, REQUESTED.plusSeconds(5));
        var entity = ServiceEnrollmentPersistenceAssembler.toPersistenceFromDomain(enrollment);
        entity.setId(25L);
        var restored = ServiceEnrollmentPersistenceAssembler.toDomainFromPersistence(entity);
        assertEquals(ServiceEnrollmentStatus.CANCELLED, restored.getStatus());
        assertEquals(REQUESTED.plusSeconds(5), restored.getCancelledAt());
        assertNull(restored.getEndedAt());
    }
}
