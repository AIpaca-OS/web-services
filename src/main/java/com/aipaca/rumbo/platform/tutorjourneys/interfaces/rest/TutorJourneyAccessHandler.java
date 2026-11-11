package com.aipaca.rumbo.platform.tutorjourneys.interfaces.rest;

import com.aipaca.rumbo.platform.profiles.application.internal.access.ProfilesAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Access errors from Profiles must remain 401/403 when called through tutor journey APIs. */
@RestControllerAdvice(basePackageClasses = TutorJourneysController.class)
public class TutorJourneyAccessHandler {
    @ExceptionHandler(ProfilesAccessException.class)
    ResponseEntity<ProblemDetail> denied(ProfilesAccessException exception) {
        HttpStatus status = exception.isAuthenticationRequired()
                ? HttpStatus.UNAUTHORIZED : HttpStatus.FORBIDDEN;
        return ResponseEntity.status(status)
                .body(ProblemDetail.forStatusAndDetail(status, exception.getMessage()));
    }
}
