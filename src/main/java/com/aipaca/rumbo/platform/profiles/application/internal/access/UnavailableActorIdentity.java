package com.aipaca.rumbo.platform.profiles.application.internal.access;

import org.springframework.stereotype.Component;

/**
 * No IAM integration is installed in this project. This placeholder intentionally
 * rejects access instead of trusting arbitrary HTTP headers, parameters or IDs.
 * Replace this bean with a verified principal adapter when IAM is integrated.
 */
@Component
public final class UnavailableActorIdentity implements ActorIdentity {
    @Override
    public Long currentAccountId() {
        throw new ProfilesAccessException("Verified account identity is not configured", true);
    }

    @Override
    public boolean hasRole(String role) {
        return false;
    }
}
