package com.aipaca.rumbo.platform.profiles.application.internal;

import com.aipaca.rumbo.platform.shared.application.result.ApplicationError;
import com.aipaca.rumbo.platform.shared.application.result.Result;

/** Result helpers for expected business failures. Do not swallow persistence exceptions. */
public final class ProfilesResults {
    private ProfilesResults() { }
    public static <T> Result<T, ApplicationError> notFound(String entity, Object id) {
        return Result.failure(ApplicationError.notFound(entity, String.valueOf(id)));
    }
    public static <T> Result<T, ApplicationError> conflict(String entity, String reason) {
        return Result.failure(ApplicationError.conflict(entity, reason));
    }
    public static <T> Result<T, ApplicationError> invalid(String concept, String reason) {
        return Result.failure(ApplicationError.validationError(concept, reason));
    }
    public static <T> Result<T, ApplicationError> domainFailure(RuntimeException ex) {
        return Result.failure(ApplicationError.businessRuleViolation("Domain rule", ex.getMessage()));
    }
}
