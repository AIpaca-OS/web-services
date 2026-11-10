package com.aipaca.rumbo.routes.infrastructure.integrations;

import com.aipaca.rumbo.routes.application.ports.StudentAuthorizationPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

/**
 * Routes ACL: delegates the driver/student decision to Profiles.
 * Profiles must check the bearer token, driver ownership, ACTIVE student,
 * authorized tutor relationship and active service enrollment.
 */
@Component
@ConditionalOnProperty(prefix="profiles.authorization", name="enabled", havingValue="true")
public class ProfilesStudentAuthorization implements StudentAuthorizationPort {
    private final RestClient client;
    private final boolean configured;

    public ProfilesStudentAuthorization(
            @Value("${profiles.authorization.base-url:}") String baseUrl) {
        String url = baseUrl == null ? "" : baseUrl.trim().replaceAll("/+$", "");
        configured = url.startsWith("https://") ||
                url.startsWith("http://localhost:") || url.equals("http://localhost");
        client = configured ? RestClient.builder().baseUrl(url).build() : null;
    }

    private record EligibilityResponse(boolean authorized) {}

    @Override
    public boolean isAuthorized(String studentId, String vehicleId, String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.matches("(?i)^Bearer \\S+$"))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Driver authentication is required");
        if (vehicleId == null || vehicleId.isBlank())
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "A vehicle must be assigned");
        if (studentId == null || studentId.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student ID is required");
        if (!configured)
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Profiles authorization URL is not configured");

        try {
            EligibilityResponse response = client.get()
                    .uri(builder -> builder.path("/api/v1/profiles/driver/eligible-students/{studentId}")
                            .queryParam("vehicleId", vehicleId)
                            .build(studentId))
                    .header("Authorization", authorizationHeader)
                    .retrieve()
                    .body(EligibilityResponse.class);
            return response != null && response.authorized();
        } catch (RestClientResponseException exception) {
            int status = exception.getStatusCode().value();
            if (status == 401)
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Driver session is not valid");
            if (status == 403 || status == 404) return false;
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Profiles authorization could not be verified");
        } catch (RestClientException exception) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Profiles authorization service is unavailable");
        }
    }
}
