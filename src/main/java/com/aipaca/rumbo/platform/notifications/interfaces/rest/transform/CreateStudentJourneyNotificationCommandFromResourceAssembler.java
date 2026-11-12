package com.aipaca.rumbo.platform.notifications.interfaces.rest.transform;

import com.aipaca.rumbo.platform.notifications.domain.model.commands.CreateStudentJourneyNotificationCommand;
import com.aipaca.rumbo.platform.notifications.interfaces.rest.resources.CreateStudentJourneyNotificationResource;

/**
 * Assembler to convert a CreateStudentJourneyNotificationResource to a CreateStudentJourneyNotificationCommand.
 */
public class CreateStudentJourneyNotificationCommandFromResourceAssembler {
    /**
     * Converts a CreateStudentJourneyNotificationResource to a CreateStudentJourneyNotificationCommand.
     *
     * @param resource The {@link CreateStudentJourneyNotificationResource} resource to convert.
     * @return The {@link CreateStudentJourneyNotificationCommand} command.
     */
    public static CreateStudentJourneyNotificationCommand toCommandFromResource(CreateStudentJourneyNotificationResource resource) {
        return new CreateStudentJourneyNotificationCommand(
                resource.studentId(),
                resource.routeId(),
                resource.tripId(),
                resource.journeyEvent(),
                resource.title(),
                resource.message(),
                resource.recipientProfileId());
    }
}
