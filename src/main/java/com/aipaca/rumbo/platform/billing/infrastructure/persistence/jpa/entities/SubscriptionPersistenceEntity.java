package com.aipaca.rumbo.platform.billing.infrastructure.persistence.jpa.entities;

import com.aipaca.rumbo.platform.billing.domain.model.valueobjects.SubscriptionStatus;
import com.aipaca.rumbo.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "subscriptions")
@Getter
@Setter
@NoArgsConstructor
public class SubscriptionPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "driver_id", nullable = false)
    private Long driverId;

    @Column(name = "plan_id", nullable = false)
    private Long planId;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionStatus status;

    @Column(name = "vehicle_limit")
    private Integer vehicleLimit;

    @Column(name = "activation_key", nullable = false, unique = true, length = 64)
    private String activationKey;
}