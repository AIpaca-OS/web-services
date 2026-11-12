package com.aipaca.rumbo.platform.profiles.application.commandservices;

import com.aipaca.rumbo.platform.profiles.application.internal.commandservices.DataDeletionRequestCommandServiceImpl;
import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.*;
import com.aipaca.rumbo.platform.profiles.domain.model.entities.TutorStudentRelationship;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.*;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.*;
import com.aipaca.rumbo.platform.profiles.domain.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataDeletionRequestCommandServiceImplTests {
    @Mock DataDeletionRequestRepository requests;
    @Mock ProfileRepository profiles;
    @Mock StudentRepository students;
    DataDeletionRequestCommandServiceImpl service;
    static final Instant AT = Instant.parse("2026-11-10T10:00:00Z");

    @BeforeEach void setup() {
        service = new DataDeletionRequestCommandServiceImpl(requests, profiles, students);
    }

    private Profile tutor() {
        return Profile.reconstitute(5L,new AccountId(6L),ProfileType.TUTOR,
                new PersonName("Tutor","Uno"),"999999999");
    }

    private Student student() {
        var link=TutorStudentRelationship.reconstitute(31L,5L,5L,
                RelationshipType.MOTHER,AuthorizationStatus.AUTHORIZED,AT,null);
        return Student.reconstitute(7L,new PersonName("Estudiante","Uno"),
                LocalDate.of(2018,5,1),StudentStatus.ACTIVE,null,0L,0L,List.of(link));
    }

    @Test void submissionRegistersPendingRequest() {
        when(profiles.findById(5L)).thenReturn(Optional.of(tutor()));
        when(students.findById(7L)).thenReturn(Optional.of(student()));
        when(requests.save(any(DataDeletionRequest.class))).thenAnswer(call -> DataDeletionRequest.reconstitute(
                9L,7L,5L,DeletionRequestStatus.PENDING,AT,null,null));
        var result=service.handle(new RequestStudentDataDeletionCommand(7L,5L));
        assertTrue(result.isSuccess());
        verify(requests).save(argThat(r -> r.getStatus()==DeletionRequestStatus.PENDING));
    }

    @Test void approvalFailsWithoutTripAndErasureIntegrations() {
        when(requests.findById(9L)).thenReturn(Optional.of(DataDeletionRequest.reconstitute(
                9L,7L,5L,DeletionRequestStatus.PENDING,AT,null,null)));
        var result=service.handle(new ResolveDataDeletionRequestCommand(9L,DeletionRequestStatus.APPROVED,"Approved"));
        assertTrue(result.isFailure());
        verify(requests,never()).save(any());
    }

    @Test void rejectionIsSaved() {
        when(requests.findById(9L)).thenReturn(Optional.of(DataDeletionRequest.reconstitute(
                9L,7L,5L,DeletionRequestStatus.PENDING,AT,null,null)));
        when(requests.save(any(DataDeletionRequest.class))).thenAnswer(call -> call.getArgument(0));
        var result=service.handle(new ResolveDataDeletionRequestCommand(9L,DeletionRequestStatus.REJECTED,"Incorrect details"));
        assertTrue(result.isSuccess());
        verify(requests).save(argThat(r -> r.getStatus()==DeletionRequestStatus.REJECTED));
    }
}
