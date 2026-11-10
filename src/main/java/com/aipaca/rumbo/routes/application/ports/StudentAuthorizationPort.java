package com.aipaca.rumbo.routes.application.ports;

/**
 * Anti-corruption layer: Profiles owns the decision about student eligibility.
 * The driver bearer token is verified by Profiles, never by trusting frontend IDs.
 */
public interface StudentAuthorizationPort {
    boolean isAuthorized(String studentId, String vehicleId, String authorizationHeader);
    boolean canManageVehicle(String vehicleId, String authorizationHeader);
}
