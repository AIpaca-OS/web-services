package com.aipaca.rumbo.platform.routes.infrastructure.integrations;

import com.aipaca.rumbo.platform.routes.application.ports.StudentAuthorizationPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/** Default until the Profiles authorization endpoint is available. */
@Component
@ConditionalOnProperty(prefix="profiles.authorization",name="enabled",havingValue="false",matchIfMissing=true)
public class FailClosedStudentAuthorization implements StudentAuthorizationPort {
    @Override
    public boolean isAuthorized(String studentId, String vehicleId, String authorizationHeader) {
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                "Profiles authorization service is not configured");
    }

    @Override
    public boolean canManageVehicle(String vehicleId, String authorizationHeader) {
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                "Profiles authorization service is not configured");
    }
    @Override
    public java.util.List<EligibleStudent> getEligibleStudents(String authorizationHeader) {
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                "Profiles authorization service is not configured");
    }

}
