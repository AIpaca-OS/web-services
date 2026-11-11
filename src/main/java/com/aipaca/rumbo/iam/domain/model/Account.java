package com.aipaca.rumbo.iam.domain.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "iam_accounts", uniqueConstraints = @UniqueConstraint(name = "uk_iam_accounts_email", columnNames = "email"))
public class Account {
    public enum Role { DRIVER, TUTOR, ASSISTANT }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 254)
    private String email;
    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;
    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = false;
    @Column(name = "token_version", nullable = false)
    private long tokenVersion = 0;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    protected Account() { }
    public Account(String email, String passwordHash, Role role) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public Role getRole() { return role; }
    public boolean isEmailVerified() { return emailVerified; }
    public long getTokenVersion() { return tokenVersion; }
    public void verifyEmail() { emailVerified = true; }
    public void changePassword(String encodedPassword) {
        passwordHash = encodedPassword;
        tokenVersion++;
    }
}
