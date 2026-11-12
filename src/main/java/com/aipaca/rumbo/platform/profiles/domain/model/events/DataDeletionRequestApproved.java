package com.aipaca.rumbo.platform.profiles.domain.model.events;

import java.time.Instant;

/** Domain fact originating in Profiles; consumers belong to other bounded contexts. */
public record DataDeletionRequestApproved(Long requestId, Long studentId, Instant occurredAt) { }
