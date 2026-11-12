
package com.aipaca.rumbo.platform.billing.infrastructure.persistence.jpa.adapters;

import com.aipaca.rumbo.platform.billing.domain.model.aggregates.DriverSubscription;
import com.aipaca.rumbo.platform.billing.domain.repositories.SubscriptionRepository;
import com.aipaca.rumbo.platform.billing.infrastructure.persistence.jpa.assemblers.SubscriptionPersistenceAssembler;
import com.aipaca.rumbo.platform.billing.infrastructure.persistence.jpa.repositories.SubscriptionPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SubscriptionRepositoryImpl
        implements SubscriptionRepository {

    private final SubscriptionPersistenceRepository persistenceRepository;

    public SubscriptionRepositoryImpl(
            SubscriptionPersistenceRepository persistenceRepository) {
        this.persistenceRepository = persistenceRepository;
    }

    @Override
    public Optional<DriverSubscription> findLatest(
            Long driverId,
            Long planId) {

        return persistenceRepository
                .findFirstByDriverIdAndPlanIdOrderByIdDesc(
                        driverId, planId)
                .map(SubscriptionPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<DriverSubscription> findAllByDriverId(
            Long driverId) {

        return persistenceRepository
                .findAllByDriverIdOrderByIdDesc(driverId)
                .stream()
                .map(SubscriptionPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public Optional<DriverSubscription> findById(
            Long subscriptionId) {

        return persistenceRepository.findById(subscriptionId)
                .map(SubscriptionPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public DriverSubscription save(
            DriverSubscription subscription,
            Long previousSubscriptionId) {

        var entity = SubscriptionPersistenceAssembler
                .toPersistenceFromDomain(
                        subscription, previousSubscriptionId);

        return SubscriptionPersistenceAssembler.toDomainFromPersistence(
                persistenceRepository.save(entity));
    }

    @Override
    public DriverSubscription update(
            DriverSubscription subscription) {

        var entity = persistenceRepository
                .findById(subscription.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Subscription not found"));

        entity.setStatus(subscription.getStatus());

        return SubscriptionPersistenceAssembler.toDomainFromPersistence(
                persistenceRepository.save(entity));
    }
}
