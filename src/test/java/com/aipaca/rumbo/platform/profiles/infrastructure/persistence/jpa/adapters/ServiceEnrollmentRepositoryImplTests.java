package com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.adapters;

import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.ServiceEnrollment;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.RequestServiceEnrollmentCommand;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.assemblers.ServiceEnrollmentPersistenceAssembler;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.entities.ServiceEnrollmentPersistenceEntity;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.repositories.ServiceEnrollmentPersistenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceEnrollmentRepositoryImplTests {
    private static final Instant REQUESTED = Instant.parse("2026-11-10T12:00:00Z");
    private static final Instant ACCEPTED = REQUESTED.plusSeconds(60);
    private static final Instant ENDED = ACCEPTED.plusSeconds(60);

    @Mock
    private ServiceEnrollmentPersistenceRepository persistenceRepository;
    private ServiceEnrollmentRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new ServiceEnrollmentRepositoryImpl(persistenceRepository);
    }

    private ServiceEnrollment stored(Long id, Long student, Long driver, String status) {
        return ServiceEnrollment.reconstitute(id, student, driver, 9L, status, REQUESTED,
                ServiceEnrollment.PENDING.equals(status) ? null : ACCEPTED,
                ServiceEnrollment.ENDED.equals(status) ? ENDED : null);
    }

    private ServiceEnrollmentPersistenceEntity asEntity(ServiceEnrollment enrollment) {
        return ServiceEnrollmentPersistenceAssembler.toPersistenceFromDomain(enrollment);
    }

    @Test
    void findByIdReconstitutesStateAndIdentity() {
        when(persistenceRepository.findById(25L)).thenReturn(Optional.of(asEntity(
                stored(25L, 7L, 8L, ServiceEnrollment.ACCEPTED))));
        var enrollment = repository.findById(25L).orElseThrow();
        assertEquals(25L, enrollment.getId());
        assertEquals(8L, enrollment.getDriverProfileId());
        assertEquals(ServiceEnrollment.ACCEPTED, enrollment.getStatus());
        assertTrue(enrollment.isActive());
    }

    @Test
    void missingIdIsNotReplacedWithAnotherRecord() {
        when(persistenceRepository.findById(25L)).thenReturn(Optional.empty());
        assertTrue(repository.findById(25L).isEmpty());
        verify(persistenceRepository, never()).findAll();
    }

    @Test
    void driverLookupIncludesPendingAcceptedAndEndedHistory() {
        when(persistenceRepository.findAllByDriverProfileId(8L)).thenReturn(List.of(
                asEntity(stored(25L, 7L, 8L, ServiceEnrollment.PENDING)),
                asEntity(stored(26L, 10L, 8L, ServiceEnrollment.ACCEPTED)),
                asEntity(stored(27L, 7L, 8L, ServiceEnrollment.ENDED))));
        var enrollments = repository.findAllByDriverProfileId(8L);
        assertEquals(List.of(ServiceEnrollment.PENDING, ServiceEnrollment.ACCEPTED, ServiceEnrollment.ENDED),
                enrollments.stream().map(ServiceEnrollment::getStatus).toList());
        assertTrue(enrollments.stream().allMatch(e -> e.getDriverProfileId().equals(8L)));
    }

    @Test
    void studentLookupPreservesMultipleDriversAndEndedHistory() {
        when(persistenceRepository.findAllByStudentId(7L)).thenReturn(List.of(
                asEntity(stored(25L, 7L, 8L, ServiceEnrollment.ENDED)),
                asEntity(stored(26L, 7L, 10L, ServiceEnrollment.ACCEPTED))));
        var enrollments = repository.findAllByStudentId(7L);
        assertEquals(List.of(8L, 10L), enrollments.stream().map(ServiceEnrollment::getDriverProfileId).toList());
        assertEquals(List.of(false, true), enrollments.stream().map(ServiceEnrollment::isActive).toList());
    }

    @Test
    void noMatchesReturnEmptyListsWithoutUnrestrictedQueries() {
        when(persistenceRepository.findAllByDriverProfileId(8L)).thenReturn(List.of());
        when(persistenceRepository.findAllByStudentId(7L)).thenReturn(List.of());
        assertTrue(repository.findAllByDriverProfileId(8L).isEmpty());
        assertTrue(repository.findAllByStudentId(7L).isEmpty());
        verify(persistenceRepository, never()).findAll();
    }

    @Test
    void duplicateCheckUsesExactlyPendingAndAcceptedForTheSpecifiedPair() {
        when(persistenceRepository.existsByStudentIdAndDriverProfileIdAndStatusIn(
                eq(7L), eq(8L), any())).thenAnswer(invocation -> {
            Long studentId = invocation.getArgument(0);
            Long driverId = invocation.getArgument(1);
            Collection<?> states = invocation.getArgument(2);
            assertEquals(7L, studentId);
            assertEquals(8L, driverId);
            assertEquals(Set.of(ServiceEnrollment.PENDING, ServiceEnrollment.ACCEPTED), Set.copyOf(states));
            assertFalse(states.contains(ServiceEnrollment.ENDED));
            return true;
        });
        assertTrue(repository.existsPendingOrAcceptedByStudentIdAndDriverProfileId(7L, 8L));
    }

    @Test
    void endedRowsMustNotBlockReEnrollmentInTheSamePair() {
        when(persistenceRepository.existsByStudentIdAndDriverProfileIdAndStatusIn(
                eq(7L), eq(8L), any())).thenAnswer(invocation -> {
            Collection<?> statuses = invocation.getArgument(2);
            // The saved history contains only ENDED rows, which are not part of the lookup.
            assertFalse(statuses.contains(ServiceEnrollment.ENDED));
            return false;
        });
        assertFalse(repository.existsPendingOrAcceptedByStudentIdAndDriverProfileId(7L, 8L));
    }

    @Test
    void duplicateCheckDoesNotAssumeExclusivityAcrossDifferentDrivers() {
        when(persistenceRepository.existsByStudentIdAndDriverProfileIdAndStatusIn(
                eq(7L), eq(10L), any())).thenReturn(false);
        assertFalse(repository.existsPendingOrAcceptedByStudentIdAndDriverProfileId(7L, 10L));
        verify(persistenceRepository).existsByStudentIdAndDriverProfileIdAndStatusIn(
                eq(7L), eq(10L), any());
    }

    @Test
    void saveNewEnrollmentUsesGeneratedIdentityReturnedByJpa() {
        var enrollment = new ServiceEnrollment(new RequestServiceEnrollmentCommand(7L, 8L, 9L), REQUESTED);
        var saved = asEntity(stored(25L, 7L, 8L, ServiceEnrollment.PENDING));
        when(persistenceRepository.save(any(ServiceEnrollmentPersistenceEntity.class))).thenAnswer(invocation -> {
            ServiceEnrollmentPersistenceEntity input = invocation.getArgument(0);
            assertNull(input.getId());
            assertEquals(9L, input.getRequestedByProfileId());
            assertEquals(ServiceEnrollment.PENDING, input.getStatus());
            assertEquals(REQUESTED, input.getRequestedAt());
            return saved;
        });
        assertEquals(25L, repository.save(enrollment).getId());
        assertNull(enrollment.getId());
    }

    @Test
    void acceptedAndEndedUpdatesKeepTheSameIdentity() {
        var enrollment = stored(25L, 7L, 8L, ServiceEnrollment.PENDING);
        when(persistenceRepository.save(any(ServiceEnrollmentPersistenceEntity.class))).thenAnswer(invocation -> {
            ServiceEnrollmentPersistenceEntity input = invocation.getArgument(0);
            assertEquals(25L, input.getId());
            return input;
        });
        enrollment.accept(ACCEPTED);
        var accepted = repository.save(enrollment);
        assertTrue(accepted.isActive());
        accepted.end(ENDED);
        var ended = repository.save(accepted);
        assertEquals(25L, ended.getId());
        assertEquals(ServiceEnrollment.ENDED, ended.getStatus());
        assertEquals(REQUESTED, ended.getRequestedAt());
        assertEquals(ACCEPTED, ended.getAcceptedAt());
        assertEquals(ENDED, ended.getEndedAt());
        verify(persistenceRepository, times(2)).save(any(ServiceEnrollmentPersistenceEntity.class));
    }

    @Test
    void persistenceIntegrityFailurePropagates() {
        var failure = new DataIntegrityViolationException("uk_service_enrollments_active_pair");
        when(persistenceRepository.save(any(ServiceEnrollmentPersistenceEntity.class))).thenThrow(failure);
        assertSame(failure, assertThrows(DataIntegrityViolationException.class,
                () -> repository.save(stored(25L, 7L, 8L, ServiceEnrollment.ACCEPTED))));
    }
}
