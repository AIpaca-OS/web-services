package com.aipaca.rumbo.iam.interfaces.rest;

import com.aipaca.rumbo.iam.application.IdentityService;
import com.aipaca.rumbo.iam.application.IdentityService.AccountView;
import com.aipaca.rumbo.iam.application.IdentityService.LoginResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/authentication")
@Tag(name = "Identity and Access Management", description = "Verified accounts, JWT and password recovery")
public class AuthenticationController {
    public record SignUpRequest(@NotBlank @Email String email,
                                @NotBlank @Size(min=8, max=72) String password,
                                @NotBlank String role) {}
    public record SignInRequest(@NotBlank @Email String email, @NotBlank String password) {}
    public record TokenRequest(@NotBlank String token) {}
    public record PasswordResetRequest(@NotBlank @Email String email) {}
    public record NewPasswordRequest(@NotBlank String token, @NotBlank @Size(min=8,max=72) String newPassword) {}
    public record ChangePasswordRequest(@NotBlank String currentPassword,
                                        @NotBlank @Size(min=8,max=72) String newPassword) {}

    private final IdentityService identities;
    public AuthenticationController(IdentityService identities) { this.identities = identities; }

    @PostMapping("/sign-up")
    @Operation(summary = "Register a driver or tutor and request email verification")
    public ResponseEntity<AccountView> signUp(@Valid @RequestBody SignUpRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(identities.signUp(request.email(), request.password(), request.role()));
    }
    @PostMapping("/verify-email")
    @Operation(summary = "Confirm email ownership with one-time verification token")
    public Map<String,String> verifyEmail(@Valid @RequestBody TokenRequest request) {
        identities.verifyEmail(request.token());
        return Map.of("message", "Email verified");
    }
    @PostMapping("/sign-in")
    @Operation(summary = "Authenticate account and issue a signed JWT")
    public LoginResult signIn(@Valid @RequestBody SignInRequest request) {
        return identities.signIn(request.email(), request.password());
    }
    @GetMapping("/me")
    @Operation(summary = "Get authenticated account information")
    public AccountView current(Principal principal) { return identities.current(principal.getName()); }

    @PostMapping("/request-password-reset")
    @Operation(summary = "Request a password reset email without revealing account existence")
    public ResponseEntity<Void> requestPasswordReset(@Valid @RequestBody PasswordResetRequest request) {
        identities.requestPasswordReset(request.email());
        return ResponseEntity.accepted().build();
    }
    @PostMapping("/reset-password")
    @Operation(summary = "Set password using valid one-time reset token")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody NewPasswordRequest request) {
        identities.resetPassword(request.token(),request.newPassword());
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/me/password")
    @Operation(summary = "Change password and revoke earlier JWTs")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request, Principal principal) {
        identities.changePassword(principal.getName(), request.currentPassword(), request.newPassword());
        return ResponseEntity.noContent().build();
    }
}
