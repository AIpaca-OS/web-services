package com.aipaca.rumbo.routes.application.ports;
/** ACL contract owned by Routes; Students BC must provide a verified implementation. */
public interface StudentAuthorizationPort {
    boolean isAuthorized(String studentId, String vehicleId);
}
