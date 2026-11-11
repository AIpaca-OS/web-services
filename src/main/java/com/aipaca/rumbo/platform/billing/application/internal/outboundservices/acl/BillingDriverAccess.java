package com.aipaca.rumbo.platform.billing.application.internal.outboundservices.acl;

import java.util.Optional;

public interface BillingDriverAccess {

    Optional<Long> findActiveDriverIdByUsername(String username);
}