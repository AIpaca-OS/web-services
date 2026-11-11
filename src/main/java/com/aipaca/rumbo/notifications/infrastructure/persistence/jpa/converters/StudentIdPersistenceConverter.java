package com.aipaca.rumbo.notifications.infrastructure.persistence.jpa.converters;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import com.aipaca.rumbo.notifications.domain.model.valueobjects.StudentId;

@Converter(autoApply = false)
public class StudentIdPersistenceConverter {

    @Override
    public Long convertToDatabaseColumn(StudentId attribute) {
        return attribute == null ? null : attribute.studentId();
    }

    @Override
    public StudentId convertToEntityAttribute(String dbData) {
        return dbData == null ? null : new StudentId(dbData);
    }

}
