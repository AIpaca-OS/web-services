package com.aipaca.rumbo.platform.profiles.interfaces.rest.transform;

import com.aipaca.rumbo.platform.profiles.domain.model.commands.AuthorizeTutorStudentRelationshipCommand;
import com.aipaca.rumbo.platform.profiles.interfaces.rest.resources.AuthorizeTutorResource;

/**
 * Assembler to convert an AuthorizeTutorResource to an AuthorizeTutorStudentRelationshipCommand.
 */
public class AuthorizeTutorStudentRelationshipCommandFromResourceAssembler {
    /**
     * Converts an AuthorizeTutorResource to an AuthorizeTutorStudentRelationshipCommand.
     *
     * @param studentId The student id taken from the path.
     * @param resource The {@link AuthorizeTutorResource} resource to convert.
     * @return The {@link AuthorizeTutorStudentRelationshipCommand} command.
     */
    public static AuthorizeTutorStudentRelationshipCommand toCommandFromResource(Long studentId, AuthorizeTutorResource resource) {
        return new AuthorizeTutorStudentRelationshipCommand(
                studentId,
                resource.tutorProfileId(),
                resource.authorizedByProfileId(),
                resource.relationshipType());
    }
}
