package com.aipaca.rumbo.platform.trips.domain.model.commands;

/**
 * Mark Student Absence Command
 *
 * @param tripId    trip id
 * @param studentId student id
 */
public record MarkStudentAbsenceCommand(Long tripId, Long studentId) {
    public MarkStudentAbsenceCommand {
        if (tripId == null || tripId < 1) {
            throw new IllegalArgumentException("Trip id cannot be null or less than 1");
        }
        if (studentId == null || studentId < 1) {
            throw new IllegalArgumentException("Student id cannot be null or less than 1");
        }
    }
}
