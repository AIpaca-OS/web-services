package com.aipaca.rumbo.platform.profiles.interfaces.rest.transform;

import com.aipaca.rumbo.platform.profiles.domain.model.commands.ResolveDataDeletionRequestCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.DeletionRequestStatus;
import com.aipaca.rumbo.platform.profiles.interfaces.rest.resources.ResolveDeletionResource;

/**
 * Assembler to convert a ResolveDeletionResource to a ResolveDataDeletionRequestCommand.
 */
public class ResolveDataDeletionRequestCommandFromResourceAssembler {
    /**
     * Converts a ResolveDeletionResource to a ResolveDataDeletionRequestCommand.
     *
     * @param requestId The deletion request id taken from the path.
     * @param status The resolution status, APPROVED or REJECTED.
     * @param resource The {@link ResolveDeletionResource} resource to convert.
     * @return The {@link ResolveDataDeletionRequestCommand} command.
     */
    public static ResolveDataDeletionRequestCommand toCommandFromResource(Long requestId, DeletionRequestStatus status, ResolveDeletionResource resource) {
        return new ResolveDataDeletionRequestCommand(requestId, status, resource.resolutionNote());
    }
}
