package com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.assemblers;

import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.Student;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.PersonName;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.embeddables.PersonNamePersistenceEmbeddable;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.entities.StudentPersistenceEntity;

/** Translates the complete student state between domain and persistence representations. */
public final class StudentPersistenceAssembler {
    private StudentPersistenceAssembler() {
    }

    public static Student toDomainFromPersistence(StudentPersistenceEntity entity) {
        if (entity == null) return null;
        return Student.reconstitute(
                entity.getId(),
                new PersonName(entity.getName().getFirstName(), entity.getName().getLastName()),
                entity.getBirthDate(), entity.getSchoolId(), entity.getStatus(), entity.getAnonymizedAt());
    }

    public static StudentPersistenceEntity toPersistenceFromDomain(Student student) {
        var entity = new StudentPersistenceEntity();
        entity.setId(student.getId());
        entity.setName(new PersonNamePersistenceEmbeddable(
                student.getName().firstName(), student.getName().lastName()));
        entity.setBirthDate(student.getBirthDate());
        entity.setSchoolId(student.getSchoolId());
        entity.setStatus(student.getStatus());
        entity.setAnonymizedAt(student.getAnonymizedAt());
        return entity;
    }
}
