package com.aipaca.rumbo.iam.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AccountTests {
    @Test void passwordChangeRevokesEarlierJwtVersion() {
        Account account = new Account("driver@example.com", "hash1", Account.Role.DRIVER);
        assertFalse(account.isEmailVerified());
        assertEquals(0, account.getTokenVersion());
        account.verifyEmail();
        account.changePassword("hash2");
        assertTrue(account.isEmailVerified());
        assertEquals("hash2", account.getPasswordHash());
        assertEquals(1, account.getTokenVersion());
    }
}
