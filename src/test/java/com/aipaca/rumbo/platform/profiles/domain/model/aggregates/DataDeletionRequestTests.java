package com.aipaca.rumbo.platform.profiles.domain.model.aggregates;

import com.aipaca.rumbo.platform.profiles.domain.model.commands.RequestStudentDataDeletionCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.ResolveDataDeletionRequestCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.DeletionRequestStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class DataDeletionRequestTests {
    private static final Instant REQUESTED_AT = Instant.parse("2026-11-10T23:59:00Z");
    private static final Instant RESOLVED_AT = Instant.parse("2026-11-11T00:01:00Z");

    private DataDeletionRequest pendingRequest() {
        return new DataDeletionRequest(new RequestStudentDataDeletionCommand(2L, 3L), REQUESTED_AT);
    }

    @Test
    void creationRecordsRequesterAndDateWithoutResolvingOrAssigningIdentity() {
        var request = pendingRequest();
        assertNull(request.getId());
        assertEquals(2L, request.getStudentId());
        assertEquals(3L, request.getRequestedByProfileId());
        assertEquals(DeletionRequestStatus.PENDING, request.getStatus());
        assertEquals(REQUESTED_AT, request.getRequestedAt());
        assertNull(request.getResolvedAt());
        assertNull(request.getResolutionNote());
        assertTrue(request.domainEvents().isEmpty());
    }

    @ParameterizedTest
    @EnumSource(value = DeletionRequestStatus.class, names = {"APPROVED", "REJECTED"})
    void pendingRequestCanBeResolvedOnceUsingCommandValues(DeletionRequestStatus outcome) {
        var request = pendingRequest();
        var command = new ResolveDataDeletionRequestCommand(1L, outcome, "Reviewed request");
        request.resolve(command.status(), command.resolutionNote(), RESOLVED_AT);

        assertEquals(outcome, request.getStatus());
        assertEquals(command.resolutionNote(), request.getResolutionNote());
        assertEquals(RESOLVED_AT, request.getResolvedAt());
        assertEquals(REQUESTED_AT, request.getRequestedAt());
        assertThrows(IllegalStateException.class,
                () -> request.resolve(outcome, "Another outcome", RESOLVED_AT.plusSeconds(1)));
        assertEquals(command.resolutionNote(), request.getResolutionNote());
    }

    @Test
    void invalidResolutionPreservesPendingState() {
        var request = pendingRequest();
        assertThrows(IllegalArgumentException.class,
                () -> request.resolve(DeletionRequestStatus.PENDING, "Note", RESOLVED_AT));
        assertThrows(IllegalArgumentException.class,
                () -> request.resolve(null, "Note", RESOLVED_AT));
        for (String note : new String[]{null, "", "  ", "x".repeat(501)}) {
            assertThrows(IllegalArgumentException.class,
                    () -> request.resolve(DeletionRequestStatus.APPROVED, note, RESOLVED_AT));
        }
        assertThrows(NullPointerException.class,
                () -> request.resolve(DeletionRequestStatus.APPROVED, "Note", null));
        assertThrows(IllegalArgumentException.class,
                () -> request.resolve(DeletionRequestStatus.APPROVED, "Note", REQUESTED_AT.minusSeconds(1)));
        assertEquals(DeletionRequestStatus.PENDING, request.getStatus());
        assertNull(request.getResolvedAt());
        assertNull(request.getResolutionNote());
    }

    @Test
    void resolutionAllowsSameInstantAndMaximumNoteLength() {
        var request = pendingRequest();
        request.resolve(DeletionRequestStatus.REJECTED, "x".repeat(500), REQUESTED_AT);
        assertEquals(REQUESTED_AT, request.getResolvedAt());
        assertEquals(500, request.getResolutionNote().length());
    }

    @ParameterizedTest
    @EnumSource(DeletionRequestStatus.class)
    void reconstitutionPreservesIdentityAndStoredState(DeletionRequestStatus status) {
        boolean pending = status == DeletionRequestStatus.PENDING;
        var request = DataDeletionRequest.reconstitute(1L, 2L, 3L, status, REQUESTED_AT,
                pending ? null : RESOLVED_AT, pending ? null : "Reviewed request");
        assertEquals(1L, request.getId());
        assertEquals(2L, request.getStudentId());
        assertEquals(3L, request.getRequestedByProfileId());
        assertEquals(status, request.getStatus());
        assertEquals(REQUESTED_AT, request.getRequestedAt());
        assertEquals(pending ? null : RESOLVED_AT, request.getResolvedAt());
        assertEquals(pending ? null : "Reviewed request", request.getResolutionNote());
        assertTrue(request.domainEvents().isEmpty());
    }

    @Test
    void reconstitutionRejectsInvalidIdentityAndReferences() {
        for (Long invalidId : new Long[]{null, 0L, -1L}) {
            assertThrows(IllegalArgumentException.class, () -> DataDeletionRequest.reconstitute(
                    invalidId, 2L, 3L, DeletionRequestStatus.PENDING, REQUESTED_AT, null, null));
            assertThrows(IllegalArgumentException.class, () -> DataDeletionRequest.reconstitute(
                    1L, invalidId, 3L, DeletionRequestStatus.PENDING, REQUESTED_AT, null, null));
            assertThrows(IllegalArgumentException.class, () -> DataDeletionRequest.reconstitute(
                    1L, 2L, invalidId, DeletionRequestStatus.PENDING, REQUESTED_AT, null, null));
        }
    }

    @Test
    void reconstitutionRejectsInconsistentResolutionData() {
        assertThrows(IllegalArgumentException.class, () -> DataDeletionRequest.reconstitute(
                1L, 2L, 3L, DeletionRequestStatus.PENDING, REQUESTED_AT, RESOLVED_AT, null));
        assertThrows(IllegalArgumentException.class, () -> DataDeletionRequest.reconstitute(
                1L, 2L, 3L, DeletionRequestStatus.PENDING, REQUESTED_AT, null, "Note"));
        assertThrows(NullPointerException.class, () -> DataDeletionRequest.reconstitute(
                1L, 2L, 3L, DeletionRequestStatus.APPROVED, REQUESTED_AT, null, "Note"));
        assertThrows(IllegalArgumentException.class, () -> DataDeletionRequest.reconstitute(
                1L, 2L, 3L, DeletionRequestStatus.REJECTED, REQUESTED_AT, RESOLVED_AT, " "));
        assertThrows(IllegalArgumentException.class, () -> DataDeletionRequest.reconstitute(
                1L, 2L, 3L, DeletionRequestStatus.APPROVED, REQUESTED_AT,
                REQUESTED_AT.minusSeconds(1), "Note"));
    }

    @Test
    void requestRequiresStatusAndRequestDate() {
        var command = new RequestStudentDataDeletionCommand(2L, 3L);
        assertThrows(NullPointerException.class, () -> new DataDeletionRequest(command, null));
        assertThrows(NullPointerException.class, () -> DataDeletionRequest.reconstitute(
                1L, 2L, 3L, null, REQUESTED_AT, null, null));
    }

    @Test
    void explicitApproveAndRejectDelegateToValidatedResolution() {
        var approved = pendingRequest();
        approved.approve("Administrator approved", RESOLVED_AT);
        assertEquals(DeletionRequestStatus.APPROVED, approved.getStatus());
        assertEquals("Administrator approved", approved.getResolutionNote());
        assertEquals(RESOLVED_AT, approved.getResolvedAt());

        var rejected = pendingRequest();
        rejected.reject("Administrator rejected", RESOLVED_AT);
        assertEquals(DeletionRequestStatus.REJECTED, rejected.getStatus());
        assertThrows(IllegalStateException.class,
                () -> rejected.approve("Second decision", RESOLVED_AT.plusSeconds(1)));
    }
}
