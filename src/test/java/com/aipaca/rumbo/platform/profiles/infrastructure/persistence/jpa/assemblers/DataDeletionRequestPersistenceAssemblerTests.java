package com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.assemblers;

import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.DataDeletionRequest;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.RequestStudentDataDeletionCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.DeletionRequestStatus;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.*;

class DataDeletionRequestPersistenceAssemblerTests {
    private static final Instant AT = Instant.parse("2026-11-10T12:00:00Z");
    private DataDeletionRequest request() {
        return new DataDeletionRequest(new RequestStudentDataDeletionCommand(7L, 1L), AT);
    }
    @Test void newRequestRemainsPendingAndUnassigned() {
        var entity = DataDeletionRequestPersistenceAssembler.toPersistenceFromDomain(request());
        assertNull(entity.getId());
        assertEquals(DeletionRequestStatus.PENDING, entity.getStatus());
        assertNull(entity.getResolvedAt());
        assertNull(entity.getResolutionNote());
    }
    @Test void canReconstituteApprovedAndRejectedWithInstantAndReason() {
        for (var state : new DeletionRequestStatus[]{DeletionRequestStatus.APPROVED, DeletionRequestStatus.REJECTED}) {
            var original = request();
            original.resolve(state, "Valid resolution", AT.plusSeconds(10));
            var entity = DataDeletionRequestPersistenceAssembler.toPersistenceFromDomain(original);
            entity.setId(10L);
            var restored = DataDeletionRequestPersistenceAssembler.toDomainFromPersistence(entity);
            assertEquals(10L, restored.getId());
            assertEquals(state, restored.getStatus());
            assertEquals("Valid resolution", restored.getResolutionNote());
            assertEquals(AT.plusSeconds(10), restored.getResolvedAt());
        }
    }
    @Test void corruptStoredStateIsRejected() {
        var entity = DataDeletionRequestPersistenceAssembler.toPersistenceFromDomain(request());
        entity.setId(10L);
        entity.setStatus(DeletionRequestStatus.APPROVED);
        assertThrows(IllegalArgumentException.class,
                () -> DataDeletionRequestPersistenceAssembler.toDomainFromPersistence(entity));
    }
}
