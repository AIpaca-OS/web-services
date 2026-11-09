package com.aipaca.rumbo.platform.billing.infrastructure.persistence.jpa.entities;

import com.aipaca.rumbo.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * JPA persistence entity for subscription plans.
 */
@Entity
@Table(name = "plans")
@Getter
@Setter
@NoArgsConstructor
public class PlanPersistenceEntity
        extends AuditableAbstractPersistenceEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(name = "reference_price", nullable = false)
    private BigDecimal referencePrice;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(name = "billing_period_months")
    private Integer billingPeriodMonths;

    @Column(name = "vehicle_limit")
    private Integer vehicleLimit;

    @Column(nullable = false)
    private boolean active;
}