package com.aipaca.rumbo.notifications.domain.model.valueobjects;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record RouteId(Long courseId) {

    public RouteId {
        if (courseId == null || courseId < 1) throw new IllegalArgumentException("Course cannot be null or less than 1");
    }
}
