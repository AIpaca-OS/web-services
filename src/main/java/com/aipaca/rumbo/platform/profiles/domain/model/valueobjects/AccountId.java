package com.aipaca.rumbo.platform.profiles.domain.model.valueobjects;

public record AccountId(Long accountId) {
    public AccountId {
        if (accountId == null || accountId < 1) {
            throw new IllegalArgumentException("Account id cannot be null or less than 1");
        }
    }
}
