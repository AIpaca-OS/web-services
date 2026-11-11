package com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.entities;

import com.aipaca.rumbo.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/** JPA representation of a school catalog entry. */
@Getter
@Setter
@Entity
@Table(name = "schools")
public class SchoolPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "address", nullable = false, length = 255)
    private String address;

    @Column(name = "district", nullable = false, length = 100)
    private String district;

    public SchoolPersistenceEntity() {
    }
}
