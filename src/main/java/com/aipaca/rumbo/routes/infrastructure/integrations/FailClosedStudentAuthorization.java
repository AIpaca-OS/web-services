package com.aipaca.rumbo.routes.infrastructure.integrations;
import com.aipaca.rumbo.routes.application.ports.StudentAuthorizationPort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
/** Security default: never claim a student is authorized until Students BC is integrated. */
@Component
public class FailClosedStudentAuthorization implements StudentAuthorizationPort {
    @Override public boolean isAuthorized(String studentId,String vehicleId){
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"Student authorization integration is not configured");
    }
}
