package com.aipaca.rumbo.incidents.infrastructure.persistence.jpa.converters;

import com.aipaca.rumbo.platform.incidents.domain.model.valueobjects.StudentId;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts {@link StudentId} values between the domain model and persistence column values.
 */
@Converter(autoApply = false)
public class StudentIdPersistenceConverter implements AttributeConverter<StudentId, Long> {

    @Override
    public Long convertToDatabaseColumn(StudentId attribute) {
        return attribute == null ? null : attribute.studentId();
    }

    @Override
    public StudentId convertToEntityAttribute(Long dbData) {
        return dbData == null ? null : new StudentId(dbData);
    }
}
