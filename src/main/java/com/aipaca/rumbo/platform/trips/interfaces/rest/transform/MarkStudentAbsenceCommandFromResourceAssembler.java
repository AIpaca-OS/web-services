package com.aipaca.rumbo.platform.trips.interfaces.rest.transform;

import com.aipaca.rumbo.platform.trips.domain.model.commands.MarkStudentAbsenceCommand;
import com.aipaca.rumbo.platform.trips.interfaces.rest.resources.MarkStudentAbsenceResource;

/**
 * Assembler to convert a MarkStudentAbsenceResource to a MarkStudentAbsenceCommand.
 */
public class MarkStudentAbsenceCommandFromResourceAssembler {
    /**
     * Converts a MarkStudentAbsenceResource to a MarkStudentAbsenceCommand.
     *
     * @param tripId   The trip id taken from the path.
     * @param resource The {@link MarkStudentAbsenceResource} resource to convert.
     * @return The {@link MarkStudentAbsenceCommand} command.
     */
    public static MarkStudentAbsenceCommand toCommandFromResource(Long tripId, MarkStudentAbsenceResource resource) {
        return new MarkStudentAbsenceCommand(tripId, resource.studentId());
    }
}
