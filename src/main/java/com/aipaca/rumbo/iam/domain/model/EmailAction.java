package com.aipaca.rumbo.iam.domain.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "iam_email_actions", indexes = @Index(name = "ix_iam_email_actions_token", columnList = "token_hash", unique = true))
public class EmailAction {
    public enum Purpose { VERIFY_EMAIL, RESET_PASSWORD }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
    @Column(name = "token_hash", nullable = false, length = 64, unique = true)
    private String tokenHash;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private Purpose purpose;
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;
    @Column(name = "used_at")
    private Instant usedAt;

    protected EmailAction() { }
    public EmailAction(Account account, String tokenHash, Purpose purpose, Instant expiresAt) {
        this.account = account;
        this.tokenHash = tokenHash;
        this.purpose = purpose;
        this.expiresAt = expiresAt;
    }
    public Account getAccount() { return account; }
    public boolean isUsable() { return usedAt == null && Instant.now().isBefore(expiresAt); }
    public void consume() { usedAt = Instant.now(); }
}
