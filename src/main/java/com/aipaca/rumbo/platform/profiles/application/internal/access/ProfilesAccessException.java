package com.aipaca.rumbo.platform.profiles.application.internal.access;
/** Used when an authenticated principal lacks access to the requested student/profile. */
public class ProfilesAccessException extends RuntimeException {
    private final boolean authenticationRequired;
    public ProfilesAccessException(String message, boolean authenticationRequired) {
        super(message);
        this.authenticationRequired = authenticationRequired;
    }
    public boolean isAuthenticationRequired() { return authenticationRequired; }
}
