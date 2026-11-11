package com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.entities;

import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.AccountId;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.ProfileType;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.converters.AccountIdPersistenceConverter;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.embeddables.PersonNamePersistenceEmbeddable;
import com.aipaca.rumbo.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

/** JPA mapping for profiles, including one profile per IAM account. */
@Getter
@Setter
@Entity
@Table(name = "profiles", uniqueConstraints = @UniqueConstraint(
        name = "uk_profiles_account_id", columnNames = "account_id"))
public class ProfilePersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Convert(converter = AccountIdPersistenceConverter.class)
    @Column(name = "account_id", nullable = false)
    private AccountId accountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "profile_type", nullable = false, length = 10)
    private ProfileType profileType;

    @Embedded
    private PersonNamePersistenceEmbeddable name;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    public ProfilePersistenceEntity() {
    }
}
