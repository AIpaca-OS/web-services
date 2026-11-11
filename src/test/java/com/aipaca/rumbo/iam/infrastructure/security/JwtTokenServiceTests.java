package com.aipaca.rumbo.iam.infrastructure.security;

import com.aipaca.rumbo.iam.domain.model.Account;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

import java.util.Base64;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtTokenServiceTests {
    private JwtTokenService service() {
        byte[] bytes = new byte[32];
        java.util.Arrays.fill(bytes, (byte) 4);
        return new JwtTokenService(Base64.getEncoder().encodeToString(bytes), 60);
    }
    @Test void signedTokenCarriesRoleAndVersion() {
        var account = mock(Account.class);
        when(account.getId()).thenReturn(12L);
        when(account.getRole()).thenReturn(Account.Role.DRIVER);
        when(account.getTokenVersion()).thenReturn(3L);
        var token = service().issue(account);
        var claims = service().verify(token);
        assertEquals("12", claims.getSubject());
        assertEquals("DRIVER", claims.get("role", String.class));
        assertEquals(3L, claims.get("version", Number.class).longValue());
    }
    @Test void tokenWithChangedSignatureIsRejected() {
        var account = mock(Account.class);
        when(account.getId()).thenReturn(1L);
        when(account.getRole()).thenReturn(Account.Role.TUTOR);
        var token = service().issue(account);
        String bad = token.substring(0, token.length()-2) + "xx";
        assertThrows(JwtException.class, () -> service().verify(bad));
    }
}
