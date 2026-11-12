package com.aipaca.rumbo.platform.routes.domain.model.entities;

import com.aipaca.rumbo.platform.routes.domain.model.valueobjects.StudentId;
import lombok.Getter;

import java.util.Objects;

/**
 * Route assignment entity.
 *
 * <p>Assigns a student to one of the stops of the {@code Route} aggregate.
 * The identifier stays null until the assignment is persisted.</p>
 */
@Getter
public class RouteAssignment {
    private Long id;
    private Long stopId;
    private StudentId studentId;

    /**
     * Creates an assignment.
     *
     * @param id        The assignment id, or null for a new assignment.
     * @param stopId    The id of a stop of the same route. It cannot be null.
     * @param studentId The assigned student. It cannot be null.
     */
    public RouteAssignment(Long id, Long stopId, StudentId studentId) {
        if (stopId == null || stopId < 1) {
            throw new IllegalArgumentException("Stop id cannot be null or less than 1");
        }
        this.id = id;
        this.stopId = stopId;
        this.studentId = Objects.requireNonNull(studentId, "Student id cannot be null");
    }

    /**
     * Creates a new assignment.
     *
     * @param stopId    The id of a stop of the same route.
     * @param studentId The assigned student.
     */
    public RouteAssignment(Long stopId, StudentId studentId) {
        this(null, stopId, studentId);
    }

    public Long getStudentIdValue() {
        return studentId.studentId();
    }
}
