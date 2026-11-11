package com.aipaca.rumbo.iam.infrastructure.security;

import com.aipaca.rumbo.iam.domain.model.Account;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtTokenService {
    private final String encodedSecret;
    private final long expirationMinutes;
    public JwtTokenService(@Value("${authorization.jwt.secret:}") String encodedSecret,
                           @Value("${authorization.jwt.expiration.minutes:60}") long expirationMinutes) {
        this.encodedSecret = encodedSecret;
        this.expirationMinutes = expirationMinutes;
    }

    private SecretKey signingKey() {
        if (encodedSecret == null || encodedSecret.isBlank())
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "JWT_SECRET must be configured");
        try {
            return Keys.hmacShaKeyFor(Decoders.BASE64.decode(encodedSecret));
        } catch (RuntimeException exception) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "JWT_SECRET is invalid");
        }
    }
    public String issue(Account account) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(account.getId().toString())
                .claim("role", account.getRole().name())
                .claim("version", account.getTokenVersion())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expirationMinutes * 60)))
                .signWith(signingKey())
                .compact();
    }
    public Claims verify(String token) throws JwtException {
        return Jwts.parser().verifyWith(signingKey()).build()
                .parseSignedClaims(token).getPayload();
    }
    public long expirationSeconds() { return expirationMinutes * 60; }
}
