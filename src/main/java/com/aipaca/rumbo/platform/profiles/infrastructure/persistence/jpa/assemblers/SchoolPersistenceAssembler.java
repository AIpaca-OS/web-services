package com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.assemblers;

import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.School;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.entities.SchoolPersistenceEntity;

/** Translates school domain and persistence representations without database access. */
public final class SchoolPersistenceAssembler {
    private SchoolPersistenceAssembler() {
    }

    public static School toDomainFromPersistence(SchoolPersistenceEntity entity) {
        if (entity == null) return null;
        return School.reconstitute(entity.getId(), entity.getName(), entity.getAddress(), entity.getDistrict());
    }

    public static SchoolPersistenceEntity toPersistenceFromDomain(School school) {
        var entity = new SchoolPersistenceEntity();
        entity.setId(school.getId());
        entity.setName(school.getName());
        entity.setAddress(school.getAddress());
        entity.setDistrict(school.getDistrict());
        return entity;
    }
}
