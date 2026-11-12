package com.aipaca.rumbo.platform.profiles.application.internal.access;
/** Port implemented by the eventual IAM adapter, never trusted request-body identifiers. */
public interface ActorIdentity {
    Long currentAccountId();
    boolean hasRole(String role);
}
