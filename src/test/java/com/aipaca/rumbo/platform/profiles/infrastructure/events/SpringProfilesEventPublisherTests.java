package com.aipaca.rumbo.platform.profiles.infrastructure.events;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SpringProfilesEventPublisherTests {
    @AfterEach void clean() {
        if (TransactionSynchronizationManager.isSynchronizationActive())
            TransactionSynchronizationManager.clearSynchronization();
        TransactionSynchronizationManager.setActualTransactionActive(false);
    }

    @Test void publishesImmediatelyOutsideTransaction() {
        var source=mock(ApplicationEventPublisher.class);
        var pub=new SpringProfilesEventPublisher(source);
        Object event=new Object();
        pub.publishAfterCommit(event);
        verify(source).publishEvent(event);
    }

    @Test void doesNotPublishBeforeSuccessfulCommit() {
        var source=mock(ApplicationEventPublisher.class);
        var pub=new SpringProfilesEventPublisher(source);
        TransactionSynchronizationManager.initSynchronization();
        TransactionSynchronizationManager.setActualTransactionActive(true);
        Object event=new Object();
        pub.publishAfterCommit(event);
        verifyNoInteractions(source);
        var synchronizations=TransactionSynchronizationManager.getSynchronizations();
        assertEquals(1,synchronizations.size());
        synchronizations.getFirst().afterCommit();
        verify(source).publishEvent(event);
    }
}
