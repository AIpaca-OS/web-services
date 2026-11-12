package com.aipaca.rumbo.platform.profiles.domain.model.aggregates;

import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.ServiceEnrollmentStatus;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.RequestServiceEnrollmentCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.Instant;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ServiceEnrollmentTests {
    private static final Instant REQUESTED_AT = Instant.parse("2026-11-10T23:59:00Z");
    private static final Instant ACCEPTED_AT = Instant.parse("2026-11-11T00:01:00Z");
    private static final Instant ENDED_AT = Instant.parse("2026-11-12T12:00:00Z");

    private ServiceEnrollment pendingEnrollment() {
        return new ServiceEnrollment(new RequestServiceEnrollmentCommand(2L, 3L, 4L), REQUESTED_AT);
    }

    private static ServiceEnrollment restoredEnrollment(ServiceEnrollmentStatus status) {
        return ServiceEnrollment.reconstitute(1L, 2L, 3L, 4L, status, REQUESTED_AT,
                ServiceEnrollmentStatus.PENDING.equals(status) ? null : ACCEPTED_AT,
                ServiceEnrollmentStatus.ENDED.equals(status) ? ENDED_AT : null);
    }

    @Test
    void requestRecordsReferencesAndDateWithoutGrantingActiveEnrollment() {
        var enrollment = pendingEnrollment();
        assertNull(enrollment.getId());
        assertEquals(2L, enrollment.getStudentId());
        assertEquals(3L, enrollment.getDriverProfileId());
        assertEquals(4L, enrollment.getRequestedByProfileId());
        assertEquals(ServiceEnrollmentStatus.PENDING, enrollment.getStatus());
        assertEquals(REQUESTED_AT, enrollment.getRequestedAt());
        assertNull(enrollment.getAcceptedAt());
        assertNull(enrollment.getEndedAt());
        assertFalse(enrollment.isActive());
    }

    @Test
    void acceptanceActivatesEnrollmentAndEndingRemovesActiveStatus() {
        var enrollment = pendingEnrollment();
        enrollment.accept(ACCEPTED_AT);
        assertEquals(ServiceEnrollmentStatus.ACCEPTED, enrollment.getStatus());
        assertEquals(ACCEPTED_AT, enrollment.getAcceptedAt());
        assertNull(enrollment.getEndedAt());
        assertTrue(enrollment.isActive());

        enrollment.end(ENDED_AT);
        assertEquals(ServiceEnrollmentStatus.ENDED, enrollment.getStatus());
        assertEquals(ENDED_AT, enrollment.getEndedAt());
        assertEquals(REQUESTED_AT, enrollment.getRequestedAt());
        assertEquals(ACCEPTED_AT, enrollment.getAcceptedAt());
        assertEquals(2L, enrollment.getStudentId());
        assertEquals(3L, enrollment.getDriverProfileId());
        assertEquals(4L, enrollment.getRequestedByProfileId());
        assertFalse(enrollment.isActive());
    }

    @Test
    void transitionsAllowEqualInstants() {
        var enrollment = pendingEnrollment();
        enrollment.accept(REQUESTED_AT);
        enrollment.end(REQUESTED_AT);
        assertEquals(REQUESTED_AT, enrollment.getAcceptedAt());
        assertEquals(REQUESTED_AT, enrollment.getEndedAt());
    }

    @ParameterizedTest
    @EnumSource(value = ServiceEnrollmentStatus.class, names = {"PENDING", "ACCEPTED", "ENDED"})
    void reconstitutionPreservesIdentityReferencesAndState(ServiceEnrollmentStatus status) {
        var enrollment = restoredEnrollment(status);
        assertEquals(1L, enrollment.getId());
        assertEquals(2L, enrollment.getStudentId());
        assertEquals(3L, enrollment.getDriverProfileId());
        assertEquals(4L, enrollment.getRequestedByProfileId());
        assertEquals(status, enrollment.getStatus());
        assertEquals(REQUESTED_AT, enrollment.getRequestedAt());
        assertEquals(ServiceEnrollmentStatus.PENDING.equals(status) ? null : ACCEPTED_AT, enrollment.getAcceptedAt());
        assertEquals(ServiceEnrollmentStatus.ENDED.equals(status) ? ENDED_AT : null, enrollment.getEndedAt());
        assertEquals(ServiceEnrollmentStatus.ACCEPTED.equals(status), enrollment.isActive());
        assertTrue(enrollment.domainEvents().isEmpty());
    }

    @ParameterizedTest
    @EnumSource(value = ServiceEnrollmentStatus.class, names = {"ACCEPTED", "ENDED"})
    void onlyPendingEnrollmentsCanBeAccepted(ServiceEnrollmentStatus status) {
        var enrollment = restoredEnrollment(status);
        var previousEnd = enrollment.getEndedAt();
        assertThrows(IllegalStateException.class, () -> enrollment.accept(ENDED_AT.plusSeconds(1)));
        assertEquals(status, enrollment.getStatus());
        assertEquals(ACCEPTED_AT, enrollment.getAcceptedAt());
        assertEquals(previousEnd, enrollment.getEndedAt());
    }

    @ParameterizedTest
    @EnumSource(value = ServiceEnrollmentStatus.class, names = {"PENDING", "ENDED"})
    void onlyAcceptedEnrollmentsCanBeEnded(ServiceEnrollmentStatus status) {
        var enrollment = restoredEnrollment(status);
        var previousAcceptance = enrollment.getAcceptedAt();
        var previousEnd = enrollment.getEndedAt();
        assertThrows(IllegalStateException.class, () -> enrollment.end(ENDED_AT.plusSeconds(1)));
        assertEquals(status, enrollment.getStatus());
        assertEquals(previousAcceptance, enrollment.getAcceptedAt());
        assertEquals(previousEnd, enrollment.getEndedAt());
    }

    @Test
    void invalidAcceptanceDatePreservesPendingState() {
        var enrollment = pendingEnrollment();
        assertThrows(NullPointerException.class, () -> enrollment.accept(null));
        assertThrows(IllegalArgumentException.class, () -> enrollment.accept(REQUESTED_AT.minusSeconds(1)));
        assertEquals(ServiceEnrollmentStatus.PENDING, enrollment.getStatus());
        assertNull(enrollment.getAcceptedAt());
        assertNull(enrollment.getEndedAt());
        assertFalse(enrollment.isActive());
    }

    @Test
    void invalidEndDatePreservesActiveEnrollment() {
        var enrollment = restoredEnrollment(ServiceEnrollmentStatus.ACCEPTED);
        assertThrows(NullPointerException.class, () -> enrollment.end(null));
        assertThrows(IllegalArgumentException.class, () -> enrollment.end(ACCEPTED_AT.minusSeconds(1)));
        assertEquals(ServiceEnrollmentStatus.ACCEPTED, enrollment.getStatus());
        assertEquals(ACCEPTED_AT, enrollment.getAcceptedAt());
        assertNull(enrollment.getEndedAt());
        assertTrue(enrollment.isActive());
    }

    @ParameterizedTest
    @MethodSource("invalidStates")
    void reconstitutionRejectsInconsistentStateOrChronology(ServiceEnrollmentStatus status, Instant acceptedAt, Instant endedAt) {
        assertThrows(IllegalArgumentException.class, () -> ServiceEnrollment.reconstitute(
                1L, 2L, 3L, 4L, status, REQUESTED_AT, acceptedAt, endedAt));
    }

    private static Stream<Arguments> invalidStates() {
        return Stream.of(
                Arguments.of(ServiceEnrollmentStatus.PENDING, ACCEPTED_AT, null),
                Arguments.of(ServiceEnrollmentStatus.PENDING, null, ENDED_AT),
                Arguments.of(ServiceEnrollmentStatus.ACCEPTED, null, null),
                Arguments.of(ServiceEnrollmentStatus.ACCEPTED, ACCEPTED_AT, ENDED_AT),
                Arguments.of(ServiceEnrollmentStatus.ENDED, null, null),
                Arguments.of(ServiceEnrollmentStatus.ENDED, ACCEPTED_AT, null),
                Arguments.of(ServiceEnrollmentStatus.ENDED, null, ENDED_AT),
                Arguments.of(ServiceEnrollmentStatus.ACCEPTED, REQUESTED_AT.minusSeconds(1), null),
                Arguments.of(ServiceEnrollmentStatus.ENDED, REQUESTED_AT.minusSeconds(1), ENDED_AT),
                Arguments.of(ServiceEnrollmentStatus.ENDED, ACCEPTED_AT, ACCEPTED_AT.minusSeconds(1)));
    }

    @Test
    void requiredDatesAndStatusCannotBeMissing() {
        assertThrows(NullPointerException.class, () -> new ServiceEnrollment(
                new RequestServiceEnrollmentCommand(2L, 3L, 4L), null));
        assertThrows(NullPointerException.class, () -> ServiceEnrollment.reconstitute(
                1L, 2L, 3L, 4L, null, REQUESTED_AT, null, null));
        assertThrows(NullPointerException.class, () -> ServiceEnrollment.reconstitute(
                1L, 2L, 3L, 4L, ServiceEnrollmentStatus.PENDING, null, null, null));
    }

    @Test
    void reconstitutionRequiresPositiveIdentityAndReferences() {
        for (Long invalidId : new Long[]{null, 0L, -1L}) {
            assertThrows(IllegalArgumentException.class, () -> ServiceEnrollment.reconstitute(
                    invalidId, 2L, 3L, 4L, ServiceEnrollmentStatus.PENDING, REQUESTED_AT, null, null));
            assertThrows(IllegalArgumentException.class, () -> ServiceEnrollment.reconstitute(
                    1L, invalidId, 3L, 4L, ServiceEnrollmentStatus.PENDING, REQUESTED_AT, null, null));
            assertThrows(IllegalArgumentException.class, () -> ServiceEnrollment.reconstitute(
                    1L, 2L, invalidId, 4L, ServiceEnrollmentStatus.PENDING, REQUESTED_AT, null, null));
            assertThrows(IllegalArgumentException.class, () -> ServiceEnrollment.reconstitute(
                    1L, 2L, 3L, invalidId, ServiceEnrollmentStatus.PENDING, REQUESTED_AT, null, null));
        }
    }

    @Test void requesterCanCancelPendingEnrollmentWithoutAccepting() {
        var enrollment = new ServiceEnrollment(new RequestServiceEnrollmentCommand(2L, 3L, 4L), REQUESTED_AT);
        var when = REQUESTED_AT.plusSeconds(10);
        enrollment.cancel(4L, when);
        assertEquals(ServiceEnrollmentStatus.CANCELLED, enrollment.getStatus());
        assertEquals(when, enrollment.getCancelledAt());
        assertNull(enrollment.getAcceptedAt());
        assertFalse(enrollment.isActive());
        assertThrows(IllegalStateException.class, () -> enrollment.accept(when.plusSeconds(2)));
        assertThrows(IllegalStateException.class, () -> enrollment.cancel(4L, when.plusSeconds(3)));
        assertThrows(IllegalStateException.class, () -> enrollment.end(when.plusSeconds(4)));
    }

    @Test void onlyOriginalRequesterMayCancel() {
        var enrollment = new ServiceEnrollment(new RequestServiceEnrollmentCommand(2L, 3L, 4L), REQUESTED_AT);
        assertThrows(IllegalStateException.class, () -> enrollment.cancel(5L, REQUESTED_AT.plusSeconds(10)));
        assertEquals(ServiceEnrollmentStatus.PENDING, enrollment.getStatus());
        assertNull(enrollment.getCancelledAt());
    }

    @Test void cancelledEnrollmentRoundTripsWithItsCancellationTimestamp() {
        var cancelled = ServiceEnrollment.reconstitute(5L, 2L, 3L, 4L,
                ServiceEnrollmentStatus.CANCELLED, REQUESTED_AT, null, REQUESTED_AT.plusSeconds(10), null);
        assertEquals(ServiceEnrollmentStatus.CANCELLED, cancelled.getStatus());
        assertEquals(REQUESTED_AT.plusSeconds(10), cancelled.getCancelledAt());
        assertThrows(IllegalArgumentException.class, () -> ServiceEnrollment.reconstitute(5L, 2L, 3L, 4L,
                ServiceEnrollmentStatus.CANCELLED, REQUESTED_AT, null, null, null));
    }
}
