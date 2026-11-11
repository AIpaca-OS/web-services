package com.aipaca.rumbo.iam.infrastructure.integrations;

import com.aipaca.rumbo.iam.domain.model.Account;
import com.aipaca.rumbo.iam.domain.repositories.AccountRepository;
import com.aipaca.rumbo.platform.billing.application.internal.outboundservices.acl.BillingDriverAccess;
import org.springframework.stereotype.Component;
import java.util.Optional;

/** Billing uses the authenticated IAM account ID as the driver identifier. */
@Component
public class IamBillingDriverAccess implements BillingDriverAccess {
    private final AccountRepository accounts;
    public IamBillingDriverAccess(AccountRepository accounts) { this.accounts=accounts; }

    @Override
    public Optional<Long> findActiveDriverIdByUsername(String email) {
        return accounts.findByEmailIgnoreCase(email)
                .filter(a -> a.isEmailVerified() && a.getRole() == Account.Role.DRIVER)
                .map(Account::getId);
    }
}
