package com.aipaca.rumbo.platform.profiles.application.commandservices;

import com.aipaca.rumbo.platform.profiles.application.internal.commandservices.DataDeletionRequestCommandServiceImpl;
import com.aipaca.rumbo.platform.profiles.application.security.ProfilesAuthorization;
import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.*;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.*;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.*;
import com.aipaca.rumbo.platform.profiles.domain.repositories.DataDeletionRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.Instant;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataDeletionRequestCommandServiceImplTests {
    @Mock DataDeletionRequestRepository requests;
    @Mock ProfilesAuthorization auth;
    DataDeletionRequestCommandServiceImpl service;
    static final Instant AT=Instant.parse("2026-11-10T10:00:00Z");
    @BeforeEach void setup() { service=new DataDeletionRequestCommandServiceImpl(requests,auth); }
    private Profile tutor() { return Profile.reconstitute(5L,new AccountId(6L),ProfileType.TUTOR,
            new PersonName("Tutor","Uno"),"999999999"); }

    @Test void submissionStoresPendingEvenWithoutTripVerification() {
        when(auth.currentTutor()).thenReturn(tutor());
        when(requests.save(any(DataDeletionRequest.class))).thenAnswer(call -> DataDeletionRequest.reconstitute(
                9L,7L,5L,DeletionRequestStatus.PENDING,AT,null,null));
        var result=service.handle(new RequestStudentDataDeletionCommand(7L,5L));
        assertTrue(result.isSuccess());
        verify(requests).save(argThat(r -> r.getStatus()==DeletionRequestStatus.PENDING));
    }

    @Test void approvalFailsClosedWithoutTripAndErasureIntegrations() {
        when(requests.findById(9L)).thenReturn(Optional.of(DataDeletionRequest.reconstitute(
                9L,7L,5L,DeletionRequestStatus.PENDING,AT,null,null)));
        var result=service.handle(new ResolveDataDeletionRequestCommand(9L,DeletionRequestStatus.APPROVED,"Approved"));
        assertTrue(result.isFailure());
        verify(requests,never()).save(any());
    }

    @Test void rejectionIsPersistedByAdministrator() {
        when(requests.findById(9L)).thenReturn(Optional.of(DataDeletionRequest.reconstitute(
                9L,7L,5L,DeletionRequestStatus.PENDING,AT,null,null)));
        when(requests.save(any(DataDeletionRequest.class))).thenAnswer(call -> call.getArgument(0));
        var result=service.handle(new ResolveDataDeletionRequestCommand(9L,DeletionRequestStatus.REJECTED,"Incorrect details"));
        assertTrue(result.isSuccess());
        verify(requests).save(argThat(r -> r.getStatus()==DeletionRequestStatus.REJECTED));
    }
}
