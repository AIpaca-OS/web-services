package com.aipaca.rumbo.platform.routes.interfaces.rest;
import org.springframework.http.HttpStatus;
import com.aipaca.rumbo.platform.profiles.application.internal.access.ProfilesAccessException;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestControllerAdvice(basePackages="com.aipaca.rumbo.routes")
public class RouteExceptionHandler {
    @ExceptionHandler(ProfilesAccessException.class)
    public ResponseEntity<ProblemDetail> access(ProfilesAccessException e) {
        HttpStatus status = e.isAuthenticationRequired()
                ? HttpStatus.UNAUTHORIZED : HttpStatus.FORBIDDEN;
        return ResponseEntity.status(status).body(
                ProblemDetail.forStatusAndDetail(status, e.getMessage()));
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> invalid(IllegalArgumentException e){
        return ResponseEntity.unprocessableEntity().body(
            ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY,e.getMessage()));
    }
}
