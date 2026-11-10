package com.aipaca.rumbo.platform.profiles.domain.model.queries;

public record GetProfileByAccountIdQuery(Long accountId) {
    public GetProfileByAccountIdQuery {
        if (accountId == null) {
            throw new IllegalArgumentException(
                    "Account id cannot be null"
            );
        }
    }
}
