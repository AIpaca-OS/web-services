package com.aipaca.rumbo.iam.application;

import com.aipaca.rumbo.iam.domain.model.Account;
import com.aipaca.rumbo.iam.domain.model.EmailAction;
import com.aipaca.rumbo.iam.domain.repositories.AccountRepository;
import com.aipaca.rumbo.iam.domain.repositories.EmailActionRepository;
import com.aipaca.rumbo.iam.infrastructure.security.JwtTokenService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;

@Service
@Transactional
public class IdentityService {
    public record AccountView(Long id, String email, String role, boolean verified) {}
    public record LoginResult(String token, String tokenType, long expiresIn, AccountView account) {}

    private final AccountRepository accounts;
    private final EmailActionRepository actions;
    private final PasswordEncoder passwords;
    private final JwtTokenService jwt;
    private final ObjectProvider<JavaMailSender> mailProvider;
    private final String frontendUrl;
    private final String mailFrom;
    private final SecureRandom random = new SecureRandom();

    public IdentityService(AccountRepository accounts, EmailActionRepository actions,
                           PasswordEncoder passwords, JwtTokenService jwt,
                           ObjectProvider<JavaMailSender> mailProvider,
                           @Value("${app.frontend.public-url:http://localhost:4200}") String frontendUrl,
                           @Value("${app.mail.from:no-reply@rumbo.invalid}") String mailFrom) {
        this.accounts = accounts; this.actions = actions; this.passwords = passwords;
        this.jwt = jwt; this.mailProvider = mailProvider;
        this.frontendUrl = frontendUrl.replaceAll("/+$", ""); this.mailFrom = mailFrom;
    }
    private String canonicalEmail(String email) { return email.strip().toLowerCase(Locale.ROOT); }
    private AccountView view(Account a) { return new AccountView(a.getId(), a.getEmail(), a.getRole().name(), a.isEmailVerified()); }

    public AccountView signUp(String email, String plainPassword, String roleName) {
        String normalized = canonicalEmail(email);
        Account.Role role;
        try { role = Account.Role.valueOf(roleName.toUpperCase(Locale.ROOT)); }
        catch (RuntimeException e) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role"); }
        if (role == Account.Role.ASSISTANT)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Assistants require a driver's invitation");
        if (accounts.existsByEmailIgnoreCase(normalized))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        requireMailService();
        Account saved = accounts.save(new Account(normalized, passwords.encode(plainPassword), role));
        String token = createAction(saved, EmailAction.Purpose.VERIFY_EMAIL, 1);
        send(normalized, "Verify your Rumbo account",
                "Open the following link to verify your account:\n" + frontendUrl + "/login?verify=" + token);
        return view(saved);
    }

    public void verifyEmail(String token) {
        EmailAction action = usableAction(token, EmailAction.Purpose.VERIFY_EMAIL);
        action.getAccount().verifyEmail();
        action.consume();
    }

    @Transactional(readOnly = true)
    public LoginResult signIn(String email, String plainPassword) {
        Account account = accounts.findByEmailIgnoreCase(canonicalEmail(email)).orElse(null);
        if (account == null || !passwords.matches(plainPassword, account.getPasswordHash()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        if (!account.isEmailVerified())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Verify your email before signing in");
        return new LoginResult(jwt.issue(account), "Bearer", jwt.expirationSeconds(), view(account));
    }

    @Transactional(readOnly = true)
    public AccountView current(String email) {
        Account account = accounts.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        return view(account);
    }

    public void requestPasswordReset(String email) {
        requireMailService();
        accounts.findByEmailIgnoreCase(canonicalEmail(email)).filter(Account::isEmailVerified).ifPresent(a -> {
            String token = createAction(a, EmailAction.Purpose.RESET_PASSWORD, 1);
            try {
                send(a.getEmail(), "Reset your Rumbo password",
                        "Open this one-time link to reset your password:\n" +
                                frontendUrl + "/login?reset=" + token);
            } catch (ResponseStatusException ignored) {
                // Uniform response to avoid account enumeration; delivery failures need server monitoring.
            }
        });
    }
    public void resetPassword(String token, String newPassword) {
        EmailAction action = usableAction(token, EmailAction.Purpose.RESET_PASSWORD);
        action.getAccount().changePassword(passwords.encode(newPassword));
        action.consume();
    }
    public void changePassword(String email, String currentPassword, String newPassword) {
        Account account = accounts.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        if (!passwords.matches(currentPassword, account.getPasswordHash()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Current password is invalid");
        account.changePassword(passwords.encode(newPassword));
    }
    private String createAction(Account account, EmailAction.Purpose purpose, long days) {
        byte[] value = new byte[32]; random.nextBytes(value);
        String raw = Base64.getUrlEncoder().withoutPadding().encodeToString(value);
        actions.save(new EmailAction(account, hash(raw), purpose,
                Instant.now().plus(days, ChronoUnit.DAYS)));
        return raw;
    }
    private EmailAction usableAction(String raw, EmailAction.Purpose purpose) {
        return actions.findByTokenHashAndPurpose(hash(raw), purpose)
                .filter(EmailAction::isUsable)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired token"));
    }
    private String hash(String token) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8)));
        } catch (java.security.NoSuchAlgorithmException e) { throw new IllegalStateException(e); }
    }
    private JavaMailSender requireMailService() {
        JavaMailSender sender = mailProvider.getIfAvailable();
        if (sender == null)
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "SMTP is not configured");
        return sender;
    }
    private void send(String recipient, String subject, String text) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(mailFrom); msg.setTo(recipient); msg.setSubject(subject); msg.setText(text);
        try { requireMailService().send(msg); }
        catch (MailException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Email delivery is unavailable");
        }
    }
}
