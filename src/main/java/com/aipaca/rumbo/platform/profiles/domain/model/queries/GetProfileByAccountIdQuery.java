package com.aipaca.rumbo.platform.profiles.domain.model.queries;

public record GetProfileByAccountIdQuery(Long accountId) {
    public GetProfileByAccountIdQuery {
        if (accountId == null || accountId < 1) {
            throw new IllegalArgumentException(
                    "Account id cannot be null or less than 1"
            );
        }
    }
}
