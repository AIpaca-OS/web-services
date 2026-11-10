package com.aipaca.rumbo.notifications.domain.model.valueobjects;

public record StudentId(Long studentId) {

    public StudentId{

        if (studentId == null || studentId < 1) throw new IllegalArgumentException("Student cannot be null or less than 1");
    }
}
