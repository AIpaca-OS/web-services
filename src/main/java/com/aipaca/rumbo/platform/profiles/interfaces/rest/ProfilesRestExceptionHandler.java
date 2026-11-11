package com.aipaca.rumbo.platform.profiles.interfaces.rest;

import com.aipaca.rumbo.platform.profiles.application.internal.access.ProfilesAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Security and persistence conflicts in the Profiles HTTP boundary. */
@RestControllerAdvice(basePackages="com.aipaca.rumbo.platform.profiles.interfaces.rest")
public class ProfilesRestExceptionHandler {
    @ExceptionHandler(ProfilesAccessException.class)
    ResponseEntity<ProblemDetail> access(ProfilesAccessException ex) {
        var status=ex.isAuthenticationRequired()?HttpStatus.UNAUTHORIZED:HttpStatus.FORBIDDEN;
        return problem(status,ex.getMessage());
    }
    @ExceptionHandler({ObjectOptimisticLockingFailureException.class,DataIntegrityViolationException.class})
    ResponseEntity<ProblemDetail> integrity(RuntimeException ex) {
        return problem(HttpStatus.CONFLICT,"Concurrent modification or database uniqueness conflict");
    }
    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ProblemDetail> invalid(IllegalArgumentException ex) {
        return problem(HttpStatus.BAD_REQUEST,ex.getMessage());
    }
    @ExceptionHandler(IllegalStateException.class)
    ResponseEntity<ProblemDetail> conflict(IllegalStateException ex) {
        return problem(HttpStatus.CONFLICT,ex.getMessage());
    }
    private ResponseEntity<ProblemDetail> problem(HttpStatus status,String message) {
        return ResponseEntity.status(status).body(ProblemDetail.forStatusAndDetail(status,message));
    }
}
