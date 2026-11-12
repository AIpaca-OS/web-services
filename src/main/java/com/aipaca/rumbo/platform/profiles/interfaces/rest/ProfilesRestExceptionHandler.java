package com.aipaca.rumbo.platform.profiles.interfaces.rest;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Exception handler for the REST controllers of the Profiles bounded context.
 *
 * <p>Translates concurrency and uniqueness conflicts, invalid arguments and rejected
 * state transitions that escape the application services into ProblemDetail responses.</p>
 */
@RestControllerAdvice(basePackages = "com.aipaca.rumbo.platform.profiles.interfaces.rest")
public class ProfilesRestExceptionHandler {

    /**
     * Handles optimistic locking failures and database uniqueness violations.
     *
     * @param ex the persistence exception
     * @return error response with CONFLICT status
     */
    @ExceptionHandler({ObjectOptimisticLockingFailureException.class, DataIntegrityViolationException.class})
    public ResponseEntity<ProblemDetail> handleIntegrityConflict(RuntimeException ex) {
        return toProblem(HttpStatus.CONFLICT, "Concurrent update or database uniqueness conflict");
    }

    /**
     * Handles invalid arguments.
     *
     * @param ex the illegal argument exception
     * @return error response with BAD_REQUEST status
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgumentException(IllegalArgumentException ex) {
        return toProblem(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Handles state transitions rejected by an aggregate.
     *
     * @param ex the illegal state exception
     * @return error response with CONFLICT status
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ProblemDetail> handleIllegalStateException(IllegalStateException ex) {
        return toProblem(HttpStatus.CONFLICT, ex.getMessage());
    }

    private ResponseEntity<ProblemDetail> toProblem(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(ProblemDetail.forStatusAndDetail(status, message));
    }
}
