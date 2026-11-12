package com.aipaca.rumbo.platform.profiles.interfaces.rest.transform;

import com.aipaca.rumbo.platform.profiles.domain.model.commands.RequestServiceEnrollmentCommand;
import com.aipaca.rumbo.platform.profiles.interfaces.rest.resources.RequestEnrollmentResource;

/**
 * Assembler to convert a RequestEnrollmentResource to a RequestServiceEnrollmentCommand.
 */
public class RequestServiceEnrollmentCommandFromResourceAssembler {
    /**
     * Converts a RequestEnrollmentResource to a RequestServiceEnrollmentCommand.
     *
     * @param resource The {@link RequestEnrollmentResource} resource to convert.
     * @return The {@link RequestServiceEnrollmentCommand} command.
     */
    public static RequestServiceEnrollmentCommand toCommandFromResource(RequestEnrollmentResource resource) {
        return new RequestServiceEnrollmentCommand(
                resource.studentId(),
                resource.driverProfileId(),
                resource.requestedByProfileId());
    }
}
