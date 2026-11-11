package com.aipaca.rumbo.platform.notifications.domain.model.valueobjects;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record StudentId(Long studentId) {

    public StudentId{

        if (studentId == null || studentId < 1) throw new IllegalArgumentException("Student cannot be null or less than 1");
    }
}
