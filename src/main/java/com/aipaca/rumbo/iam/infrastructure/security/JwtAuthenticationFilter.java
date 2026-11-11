package com.aipaca.rumbo.iam.infrastructure.security;

import com.aipaca.rumbo.iam.domain.model.Account;
import com.aipaca.rumbo.iam.domain.repositories.AccountRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenService jwt;
    private final AccountRepository accounts;
    public JwtAuthenticationFilter(JwtTokenService jwt, AccountRepository accounts) {
        this.jwt = jwt;
        this.accounts = accounts;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ") && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                Claims claims = jwt.verify(header.substring(7));
                long id = Long.parseLong(claims.getSubject());
                Account account = accounts.findById(id).orElse(null);
                Number version = claims.get("version", Number.class);
                if (account != null && account.isEmailVerified()
                        && version != null && version.longValue() == account.getTokenVersion()
                        && account.getRole().name().equals(claims.get("role", String.class))) {
                    var authentication = new UsernamePasswordAuthenticationToken(
                            account.getEmail(), null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + account.getRole().name())));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (JwtException | IllegalArgumentException invalid) {
                SecurityContextHolder.clearContext();
            }
        }
        chain.doFilter(request, response);
    }
}
